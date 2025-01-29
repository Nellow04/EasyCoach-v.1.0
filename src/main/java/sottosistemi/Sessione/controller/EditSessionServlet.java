package sottosistemi.Sessione.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.beans.Sessione;
import model.beans.Timeslot;
import sottosistemi.Sessione.service.ImageService;
import sottosistemi.Sessione.service.SessionManagementService;
import sottosistemi.Sessione.service.SessionValidationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/EditSessionServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10,  // 10 MB
        maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class EditSessionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String PERMANENT_UPLOAD_PATH = System.getProperty("user.home") + File.separator + "easycoach_uploads";
    private static final String UPLOAD_DIRECTORY = "uploads";

    // Service principale (CRUD sessioni)
    private SessionManagementService sessionManagementService = new SessionManagementService();

    // I due nuovi service
    private SessionValidationService validationService = new SessionValidationService();
    private ImageService imageService = new ImageService();

    public EditSessionServlet() throws ServletException {
        super();
        this.sessionManagementService = new SessionManagementService();
        this.validationService = new SessionValidationService();
        this.imageService = new ImageService();

        try {
            Files.createDirectories(Paths.get(PERMANENT_UPLOAD_PATH));
        } catch (IOException e) {
            throw new ServletException("Impossibile creare la directory per le immagini", e);
        }
    }

    public EditSessionServlet (SessionManagementService sessionManagementService, SessionValidationService sessionValidationService, ImageService imageService){
        this.sessionManagementService = sessionManagementService;
        this.validationService = sessionValidationService;
        this.imageService = imageService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Autorizzazione
        Integer mentorId = (Integer) request.getSession().getAttribute("idUtente");
        String ruolo = (String) request.getSession().getAttribute("ruolo");

        if (mentorId == null || !"MENTOR".equals(ruolo)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Utente non autorizzato");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Azione non specificata");
            return;
        }

        try {
            switch (action) {
                case "load":
                    loadSession(request, response, mentorId);
                    break;
                case "save":
                    saveSession(request, response, mentorId);
                    break;
                case "delete":
                    deleteSession(request, response, mentorId);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Azione non valida");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Errore del server: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Errore imprevisto: " + e.getMessage());
        }
    }

    private void loadSession(HttpServletRequest request, HttpServletResponse response, int mentorId)
            throws SQLException, IOException {
        int idSessione = Integer.parseInt(request.getParameter("idSessione"));
        Sessione sessione = sessionManagementService.findSessionById(idSessione);

        if (sessione == null || sessione.getIdUtente() != mentorId) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Non autorizzato ad accedere a questa sessione");
            return;
        }
        List<Timeslot> timeslots = sessionManagementService.findTimeslotsBySessionId(idSessione);

        // JSON di risposta
        Map<String, Object> responseData = Map.of(
                "sessione", sessione,
                "timeslots", timeslots
        );
        response.getWriter().write(new Gson().toJson(responseData));
    }

    private void saveSession(HttpServletRequest request, HttpServletResponse response, int mentorId)
            throws ServletException, IOException, SQLException {
        // Controllo se c'è immagine
        Part filePart = request.getPart("immagine");

        // (Opzionale) se l'immagine è presente, controlliamo dimensione e formato
        if (filePart != null && filePart.getSize() > 0) {
            if (!imageService.validateImage(filePart)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Il formato del file non è valido o supera i 10MB.");
                return;
            }
        }

        int sessionId = Integer.parseInt(request.getParameter("idSessione"));
        Sessione sessione = sessionManagementService.findSessionById(sessionId);

        if (sessione == null || sessione.getIdUtente() != mentorId) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Non autorizzato a modificare questa sessione");
            return;
        }

        // Parametri dalla request
        String nome = request.getParameter("nome");
        String descrizione = request.getParameter("descrizione");
        String prezzoStr = request.getParameter("prezzo");
        String[] timeslotDays = request.getParameterValues("timeslot_day[]");
        String[] timeslotHours = request.getParameterValues("timeslot_hour[]");

        // Validazione dei dati usando il ValidationService
        Map<String, String> errors = validationService.validateFormEdit(nome, descrizione, prezzoStr, timeslotDays, timeslotHours);
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new Gson().toJson(Map.of("errors", errors)));
            return;
        }

        // Gestione immagine usando l'ImageService
        String immagineUrl = null;
        if (filePart != null && filePart.getSize() > 0) {
            immagineUrl = imageService.processImageUpload(filePart, PERMANENT_UPLOAD_PATH, UPLOAD_DIRECTORY);

            // Se c'è una nuova immagine, elimina quella vecchia
            if (sessione.getImmagine() != null && !sessione.getImmagine().isEmpty()) {
                imageService.deleteImage(sessione.getImmagine(), PERMANENT_UPLOAD_PATH);
            }
        } else {
            immagineUrl = sessione.getImmagine(); // Mantieni immagine esistente
        }

        // Aggiorna i dati della sessione
        sessione.setTitolo(nome);
        sessione.setDescrizione(descrizione);
        sessione.setPrezzo(Double.parseDouble(prezzoStr.replace(',', '.')));
        sessione.setImmagine(immagineUrl);

        // Chiamata al service per aggiornare la sessione e i timeslot
        sessionManagementService.updateSession(sessione, timeslotDays, timeslotHours);

        response.getWriter().write(new Gson().toJson(Map.of("success", true, "message", "Sessione aggiornata con successo")));
    }

    private void deleteSession(HttpServletRequest request, HttpServletResponse response, int mentorId)
            throws IOException, SQLException {
        int idSessione = Integer.parseInt(request.getParameter("idSessione"));
        Sessione sessione = sessionManagementService.findSessionById(idSessione);

        if (sessione == null || sessione.getIdUtente() != mentorId) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Non autorizzato a eliminare questa sessione");
            return;
        }

        // Verifica che non ci siano prenotazioni attive
        if (sessionManagementService.hasActiveBookings(idSessione)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(new Gson().toJson(Map.of("success", false, "message", "Ci sono prenotazioni attive per questa sessione")));
            return;
        }

        // Archivia la sessione e cancella i timeslot
        sessionManagementService.archiveSession(sessione);

        response.getWriter().write(new Gson().toJson(Map.of("success", true, "message", "Sessione archiviata con successo")));
    }
}


