package sottosistemi.Prenotazione.controller;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.beans.Prenotazione;
import sottosistemi.Prenotazione.service.BookingService;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servlet per la gestione delle operazioni di Prenotazione
 * (checkAvailability, booking, create, confirm)
 */
@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private BookingService bookingService = new BookingService();

    public BookingServlet (){
        super();
        this.bookingService = new BookingService();
    }

    public BookingServlet (BookingService bookingService){
        this.bookingService = bookingService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        // Controllo del ruolo
        String ruolo = (String) request.getSession().getAttribute("ruolo");
        if ("MENTOR".equals(ruolo)) {
            result.addProperty("error", "I mentor non possono effettuare prenotazioni");
            out.print(result.toString());
            return;
        }

        try {
            // Recupero i parametri da request
            Map<String, String> params;
            Object paramsAttr = request.getAttribute("params");

            if (paramsAttr != null && paramsAttr instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> castedParams = (Map<String, String>) paramsAttr;
                params = castedParams;
            } else {
                // parseRequestBody: prende il body x-www-form-urlencoded e lo mette in una mappa
                params = parseRequestBody(request.getReader().lines().collect(Collectors.joining()));
            }

            // Azione richiesta
            String action = params.get("action");
            if (action == null) {
                result.addProperty("error", "Azione non specificata");
                out.print(result.toString());
                return;
            }

            switch (action) {
                case "checkAvailability": {
                    // Spostiamo la logica nel Service:
                    Map<String, Object> availability = bookingService.checkAvailability(params);
                    if (availability.containsKey("error")) {
                        result.addProperty("error", (String) availability.get("error"));
                    } else {
                        // availability di solito contiene "disponibile" (Boolean) e "status" (String)
                        Boolean disponibile = (Boolean) availability.get("disponibile");
                        result.addProperty("disponibile", disponibile);

                        if (availability.containsKey("status")) {
                            result.addProperty("status", (String) availability.get("status"));
                        }
                    }
                    break;
                }
                case "booking": {
                    // Nell'originale, facevi un ulteriore switch su create/confirm.
                    // Puoi gestirlo qui o leggere un sotto-action se vuoi.
                    String subAction = params.get("subAction"); // ad es. "create" o "confirm"
                    if ("create".equals(subAction)) {
                        try {
                            Prenotazione pren = bookingService.createBooking(params);
                            result.addProperty("success", true);
                            result.addProperty("message", "Prenotazione creata con successo");
                            result.addProperty("idPrenotazione", pren.getIdPrenotazione());
                        } catch (Exception e) {
                            result.addProperty("success", false);
                            result.addProperty("error", e.getMessage());
                        }
                    } else if ("confirm".equals(subAction)) {
                        try {
                            Prenotazione updated = bookingService.confirmBooking(params);
                            if (updated == null) {
                                result.addProperty("success", false);
                                result.addProperty("error", "Prenotazione non trovata");
                            } else {
                                result.addProperty("success", true);
                                result.addProperty("message", "Prenotazione confermata con successo");
                            }
                        } catch (Exception e) {
                            result.addProperty("success", false);
                            result.addProperty("error", e.getMessage());
                        }
                    } else {
                        result.addProperty("success", false);
                        result.addProperty("error", "Sotto-azione booking non valida. Usa create/confirm");
                    }
                    break;
                }
                case "create": {
                    // Se preferisci tenerlo come azione diretta "create"
                    // invece di passare da "booking", lo gestisci così
                    try {
                        Prenotazione pren = bookingService.createBooking(params);
                        result.addProperty("success", true);
                        result.addProperty("message", "Prenotazione creata con successo");
                        result.addProperty("idPrenotazione", pren.getIdPrenotazione());
                    } catch (Exception e) {
                        result.addProperty("success", false);
                        result.addProperty("error", e.getMessage());
                    }
                    break;
                }
                case "confirm": {
                    // Azione diretta "confirm"
                    try {
                        Prenotazione updated = bookingService.confirmBooking(params);
                        if (updated == null) {
                            result.addProperty("success", false);
                            result.addProperty("error", "Prenotazione non trovata");
                        } else {
                            result.addProperty("success", true);
                            result.addProperty("message", "Prenotazione confermata con successo");
                        }
                    } catch (Exception e) {
                        result.addProperty("success", false);
                        result.addProperty("error", e.getMessage());
                    }
                    break;
                }
                default: {
                    result.addProperty("error", "Azione non valida");
                }
            }

        } catch (Exception e) {
            result.addProperty("error", "Errore del server: " + e.getMessage());
        }

        out.print(result.toString());
    }

    /**
     * Esempio di parsing del corpo x-www-form-urlencoded in parametri chiave=valore.
     */
    private Map<String, String> parseRequestBody(String body) {
        Map<String, String> params = new HashMap<>();
        if (body != null && !body.trim().isEmpty()) {
            for (String pair : body.split("&")) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(
                            keyValue[0],
                            java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    );
                }
            }
        }
        return params;
    }
}
