package sottosistemi.AreaUtente.controller;

import com.google.gson.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dto.PrenotazioneDetailsDTO;
import sottosistemi.AreaUtente.service.MenteeDashboardService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DashboardMenteeServlet", urlPatterns = {"/DashboardMenteeServlet"})
public class DashboardMenteeServlet extends HttpServlet {

    private MenteeDashboardService menteeDashboardService;
    private Gson gson;

    public DashboardMenteeServlet() {
        super();
        this.menteeDashboardService = new MenteeDashboardService();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        });
        this.gson = gsonBuilder.create();
    }
    public DashboardMenteeServlet (MenteeDashboardService menteeDashboardService){
        this.menteeDashboardService = menteeDashboardService;
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Verifica che l'utente sia un mentee
        String ruolo = (String) request.getSession().getAttribute("ruolo");

        if (!"MENTEE".equals(ruolo)) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String action = request.getParameter("action");

        if ("getBookings".equals(action)) {
            handleGetBookings(request, response);
        } else {
            handleDashboardDisplay(request, response);
        }
    }

    private void handleGetBookings(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Integer idMentee = (Integer) request.getSession().getAttribute("idUtente");

        if (idMentee == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // Ottiene i dettagli delle prenotazioni attive e concluse tramite il Service
            List<PrenotazioneDetailsDTO> activeBookings = menteeDashboardService.findActiveBookingsForMentee(idMentee);
            List<PrenotazioneDetailsDTO> completedBookings = menteeDashboardService.findCompletedBookingsForMentee(idMentee);

            // Crea un oggetto per la risposta JSON
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("activeBookings", activeBookings);
            responseData.put("completedBookings", completedBookings);

            // Invia la risposta JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(responseData));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore nel caricamento delle prenotazioni: " + e.getMessage());
            response.getWriter().write(gson.toJson(error));
        }
    }

    private void handleDashboardDisplay(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Forward alla JSP per il rendering iniziale
        request.getRequestDispatcher("/dashboardMentee.jsp").forward(request, response);
    }
}
