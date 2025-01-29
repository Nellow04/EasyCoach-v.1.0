package sottosistemi.Sessione.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sottosistemi.Sessione.service.SessionManagementService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/CheckTimeSlotServlet")
public class CheckTimeSlotServlet extends HttpServlet {

    private final Gson gson = new Gson();

    /**
     * Invece di restituire direttamente un DAO, istanziamo e usiamo il nostro Service.
     */
    private SessionManagementService sessionManagementService = new SessionManagementService();
    public CheckTimeSlotServlet(){
        super();
        this.sessionManagementService = new SessionManagementService();
    }
    public CheckTimeSlotServlet (SessionManagementService sessionManagementService){
        this.sessionManagementService = sessionManagementService;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Ottieni l'ID del mentor dalla sessione
            Integer mentorId = (Integer) request.getSession().getAttribute("idUtente");
            String ruolo = (String) request.getSession().getAttribute("ruolo");

            if (mentorId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson("ID Mentor non trovato"));
                return;
            }

            if (!"MENTOR".equals(ruolo)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson("Utente non autorizzato"));
                return;
            }

            // Ottieni tutti i timeslot del mentor dal Service
            List<Map<String, Integer>> slots = sessionManagementService.getTimeslotsByMentorIdAsMap(mentorId);

            // Serializza in JSON e invia la risposta
            String jsonResponse = gson.toJson(slots);
            response.getWriter().write(jsonResponse);

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Errore durante il recupero dei timeslot: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Errore imprevisto: " + e.getMessage()));
        }
    }
}
