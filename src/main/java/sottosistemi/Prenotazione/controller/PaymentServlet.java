package sottosistemi.Prenotazione.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.beans.Pagamento;
import sottosistemi.Prenotazione.service.PaymentService;
import sottosistemi.Prenotazione.service.PaymentService.ValidationException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet che gestisce il pagamento di una prenotazione (carta, paypal, googlepay, ecc.).
 */
@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private PaymentService paymentService = new PaymentService();

    public PaymentServlet (){
        super();
        this.paymentService = new PaymentService();
    }

    public PaymentServlet(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Pattern e validazioni spostati in PaymentService (validateCardPayment)

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        Map<String, Object> result = new HashMap<>();

        // Verifica se l'utente è un MENTOR
        String ruolo = (String) request.getSession().getAttribute("ruolo");
        if ("MENTOR".equals(ruolo)) {
            result.put("success", false);
            result.put("message", "I mentor non possono effettuare prenotazioni");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        try {
            // Recupero parametri
            String metodoPagamento = request.getParameter("metodoPagamento");
            if (metodoPagamento == null || metodoPagamento.trim().isEmpty()) {
                throw new ValidationException("Metodo di pagamento non specificato");
            }

            // Se pagamento con CARTA, validazione
            if ("CARTA".equals(metodoPagamento)) {
                paymentService.validateCardPayment(
                        request.getParameter("numeroCarta"),
                        request.getParameter("scadenzaGGMM"),
                        request.getParameter("scadenzaAnno"),
                        request.getParameter("cardHolder"),
                        request.getParameter("cvv")
                );
            }

            // Parametri rimanenti
            int idPrenotazione = Integer.parseInt(request.getParameter("idPrenotazione"));
            double totalePagato = Double.parseDouble(request.getParameter("totalePagato"));

            // Esegui pagamento
            Pagamento pagamento = paymentService.processPayment(
                    idPrenotazione,
                    metodoPagamento,
                    totalePagato,
                    String.valueOf(request.getSession().getAttribute("idUtente"))
            );

            if (pagamento != null) {
                result.put("success", true);
                result.put("message", "Pagamento completato con successo");
            }

        } catch (ValidationException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            result.put("success", false);
            result.put("message", "Errore durante il salvataggio del pagamento: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Si è verificato un errore imprevisto");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        response.getWriter().write(gson.toJson(result));
    }
}
