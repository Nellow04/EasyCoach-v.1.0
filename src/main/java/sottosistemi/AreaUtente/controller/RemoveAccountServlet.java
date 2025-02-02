package sottosistemi.AreaUtente.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.beans.Utente;
import sottosistemi.AreaUtente.service.AccountService;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/RemoveAccountServlet")
public class RemoveAccountServlet extends HttpServlet {

    private AccountService accountService;

    public RemoveAccountServlet() {
        super();
        this.accountService = new AccountService();
    }

    public RemoveAccountServlet (AccountService accountService){
        this.accountService = accountService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Utente utente = (Utente) request.getSession().getAttribute("utente");

        if (utente == null) {
            response.getWriter().write("{\"success\":false, \"error\":\"Utente non autenticato.\"}");
            return;
        }

        try {
            // Verifica se l'utente ha prenotazioni attive (delegato al service)
            if (accountService.hasActiveBookingsForUser(utente.getIdUtente())) {
                response.getWriter().write("{\"success\":false, \"error\":\"Non è possibile eliminare l'account perché ci sono prenotazioni attive.\"}");
                return;
            }

            // Elimina l'utente
            accountService.deleteUser(utente.getIdUtente());

            // Invalida la sessione dopo l'eliminazione
            request.getSession().invalidate();
            response.getWriter().write("{\"success\":true}");
        } catch (SQLException e) {
            response.getWriter().write("{\"success\":false, \"error\":\"Errore nel database.\"}");
            e.printStackTrace();
        }
    }
}
