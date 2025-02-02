package sottosistemi.AreaUtente.controller;

import com.google.gson.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.beans.Sessione;
import model.dto.PrenotazioneDetailsDTO;
import sottosistemi.AreaUtente.service.MentorDashboardService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DashboardMentorServlet", urlPatterns = {"/DashboardMentorServlet"})
public class DashboardMentorServlet extends HttpServlet {

    private MentorDashboardService mentorDashboardService;
    private Gson gson;

    public DashboardMentorServlet() {
        super();
        this.mentorDashboardService = new MentorDashboardService();
        // Configura Gson per gestire LocalDateTime
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        });
        this.gson = gsonBuilder.create();
    }

    public DashboardMentorServlet (MentorDashboardService mentorDashboardService){
        this.mentorDashboardService = mentorDashboardService;
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Verifica che l'utente sia un mentor
        String ruolo = (String) request.getSession().getAttribute("ruolo");

        if (!"MENTOR".equals(ruolo)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Accesso negato");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(gson.toJson(error));
            return;
        }

        String action = request.getParameter("action");

        if ("getBookings".equals(action) || "getSessions".equals(action)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if ("getBookings".equals(action)) {
                handleGetBookings(request, response);
            } else {
                handleGetSessions(request, response);
            }
        } else {
            response.setContentType("text/html");
            handleDashboardDisplay(request, response);
        }
    }

    private void handleGetSessions(HttpServletRequest request, HttpServletResponse response) throws IOException {


        Integer idMentor = (Integer) request.getSession().getAttribute("idUtente");

        if (idMentor == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Utente non autorizzato");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(error));
            return;
        }

        try {
            // Recupera le sessioni dal Service
            List<Sessione> sessions = mentorDashboardService.findSessionsByMentorId(idMentor);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("sessions", sessions);

            response.getWriter().write(gson.toJson(responseData));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore nel caricamento delle sessioni: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(error));
        }
    }

    private void handleGetBookings(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Integer idMentor = (Integer) request.getSession().getAttribute("idUtente");

        if (idMentor == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Utente non autorizzato");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(error));
            return;
        }

        try {
            // Ottiene le prenotazioni attive dal Service
            List<PrenotazioneDetailsDTO> activeBookings = mentorDashboardService.findActiveBookingsForMentor(idMentor);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("activeBookings", activeBookings);

            response.getWriter().write(gson.toJson(responseData));

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore nel caricamento delle prenotazioni: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(error));
        }
    }

    private void handleDashboardDisplay(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getRequestDispatcher("/dashboardMentor.jsp").forward(request, response);
    }
}
