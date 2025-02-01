package sottosistemi.Prenotazione.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.beans.Sessione;
import model.beans.Timeslot;
import sottosistemi.Prenotazione.service.SessionRetrievalService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servlet per ottenere i dettagli di una sessione (e sessioni correlate)
 * in formato JSON o tramite forward a JSP.
 */
@WebServlet("/GetSessionServlet")
public class GetSessionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private SessionRetrievalService sessionRetrievalService = new SessionRetrievalService();
    private final Gson gson = new Gson();

    public GetSessionServlet (){
        super();
        this.sessionRetrievalService = new SessionRetrievalService();
    }

    public GetSessionServlet (SessionRetrievalService sessionRetrievalService){
        this.sessionRetrievalService = sessionRetrievalService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // Leggi il corpo della richiesta
            String body = request.getReader().lines().collect(Collectors.joining());

            // estrae sessioneId dalla ricerca passata come dato
            String sessioneId = null;
            if (body != null && !body.trim().isEmpty()) {
                String[] params = body.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && keyValue[0].equals("sessioneId")) {
                        sessioneId = keyValue[1];
                        break;
                    }
                }
            }

            if (sessioneId == null || sessioneId.trim().isEmpty()) {
                result.addProperty("error", "ID sessione non valido (null o vuoto)");
                out.print(result.toString());
                return;
            }

            // Recupera la sessione
            Sessione sessione = sessionRetrievalService.findSessionById(Integer.parseInt(sessioneId));
            if (sessione == null) {
                result.addProperty("error", "Sessione non trovata per ID: " + sessioneId);
                out.print(result.toString());
                return;
            }

            // Ottieni le sessioni correlate (max 4)
            List<Sessione> sessioniCorrelate = sessionRetrievalService.findCorrelatedSessions(Integer.parseInt(sessioneId));

            // Recupera i timeslot
            List<Timeslot> timeslots = sessionRetrievalService.findTimeslotsBySessionId(sessione.getIdSessione());

            // Costruisci la risposta JSON
            Map<String, Object> sessioneMap = new HashMap<>();
            sessioneMap.put("sessione", sessione);
            sessioneMap.put("sessioniCorrelate", sessioniCorrelate);
            sessioneMap.put("timeslots", timeslots);

            out.print(gson.toJson(sessioneMap));

        } catch (SQLException e) {
            result.addProperty("error", "Errore del database: " + e.getMessage());
            out.print(result.toString());
        } catch (NumberFormatException e) {
            result.addProperty("error", "ID sessione non valido (non numerico)");
            out.print(result.toString());
        } catch (Exception e) {
            result.addProperty("error", "Errore interno del server: " + e.getMessage());
            out.print(result.toString());
        }
    }
}
