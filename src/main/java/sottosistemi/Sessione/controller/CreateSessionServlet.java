package sottosistemi.Sessione.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.beans.Sessione;
import sottosistemi.Sessione.service.ImageService;
import sottosistemi.Sessione.service.SessionManagementService;
import sottosistemi.Sessione.service.SessionValidationService;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;


@WebServlet("/CreateSessionServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,  // 1 MB
        maxFileSize = 1024 * 1024 * 10,   // 10 MB
        maxRequestSize = 1024 * 1024 * 15  // 15 MB
)
public class CreateSessionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Cartelle e path come prima
    private static final String UPLOAD_DIRECTORY = "uploads";
    private static final String PERMANENT_UPLOAD_PATH = System.getProperty("user.home") + File.separator + "easycoach_uploads";

    // Service già esistente per la logica di sessione
    private SessionManagementService sessionManagementService = new SessionManagementService();
    // Nuovi service
    private SessionValidationService validationService = new SessionValidationService();
    private ImageService imageService = new ImageService();

    public CreateSessionServlet () throws ServletException {
        super();
        this.sessionManagementService = new SessionManagementService();
        this.validationService = new SessionValidationService();
        this.imageService = new ImageService();

        // Creazione directory
        try {
            Files.createDirectories(Paths.get(PERMANENT_UPLOAD_PATH));
        } catch (IOException e) {
            throw new ServletException("Impossibile creare la directory per le immagini", e);
        }
    }

    public CreateSessionServlet (SessionManagementService sessionManagementService, SessionValidationService sessionValidationService, ImageService imageService){
        this.sessionManagementService = sessionManagementService;
        this.validationService = sessionValidationService;
        this.imageService = imageService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verifica che l'utente sia un mentor
        String ruolo = (String) request.getSession().getAttribute("ruolo");
        if (!"MENTOR".equals(ruolo)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Solo i mentor possono creare sessioni");
            return;
        }

        Integer idUtente = (Integer) request.getSession().getAttribute("idUtente");
        if (idUtente == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utente non autenticato");
            return;
        }

        try {
            // Ottieni i parametri
            String titolo = request.getParameter("titolo");
            String descrizione = request.getParameter("descrizione");
            String prezzo = request.getParameter("prezzo");
            String[] timeslot_days = request.getParameterValues("timeslot_day[]");
            String[] timeslot_hours = request.getParameterValues("timeslot_hour[]");
            Part imagePart = request.getPart("immagine");  // potenzialmente obbligatoria

            // *** Validazione dati ***
            boolean imageIsProvided = (imagePart != null && imagePart.getSize() > 0);
            Map<String, String> errors = validationService.validateForm(
                    titolo,
                    descrizione,
                    prezzo,
                    timeslot_days,
                    timeslot_hours,
                    imageIsProvided // a CreateSession l'immagine è obbligatoria
            );

            // Se l'immagine è presente, verifichiamo anche estensione/dimensione via ImageService
            if (imageIsProvided && !imageService.validateImage(imagePart)) {
                errors.put("immagine", "L'immagine deve essere JPG/JPEG/PNG/GIF, max 10MB");
            }

            if (!errors.isEmpty()) {
                // Ritorna errori in JSON
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonArray errorArray = new JsonArray();
                errors.forEach((field, message) -> {
                    JsonObject errorObj = new JsonObject();
                    errorObj.addProperty("field", field);
                    errorObj.addProperty("message", message);
                    errorArray.add(errorObj);
                });
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.add("errors", errorArray);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(jsonResponse.toString());
                return;
            }

            // Se validato, gestiamo l'immagine
            String fileName = null;
            if (imageIsProvided) {
                fileName = imageService.processImageUpload(
                        imagePart,
                        PERMANENT_UPLOAD_PATH,
                        UPLOAD_DIRECTORY
                );
            }

            // Creazione bean
            Sessione sessione = new Sessione();
            sessione.setIdUtente(idUtente);
            sessione.setTitolo(titolo);
            sessione.setDescrizione(descrizione);
            sessione.setPrezzo(Double.parseDouble(prezzo.replace(',', '.')));
            sessione.setImmagine(fileName);
            sessione.setStatusSessione("attiva");

            // Salvataggio sessione
            sessionManagementService.createSession(sessione, timeslot_days, timeslot_hours);

            // Redirect
            response.sendRedirect("dashboardMentor.jsp?success=true");

        } catch (SQLException e) {
            // Gestione errori
            request.setAttribute("error", "Errore durante la creazione della sessione: " + e.getMessage());
            request.getRequestDispatcher("session.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Formato del prezzo non valido");
            request.getRequestDispatcher("session.jsp").forward(request, response);
        }
    }
}
