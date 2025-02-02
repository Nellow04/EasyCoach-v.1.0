package sottosistemi.AreaUtente.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.beans.Utente;
import sottosistemi.AreaUtente.service.AccountService;

import java.io.IOException;

@WebServlet("/UpdatePassword")
public class UpdatePasswordServlet extends HttpServlet {

    private AccountService accountService;

    public UpdatePasswordServlet() {
        super();
        this.accountService = new AccountService();
    }

    public UpdatePasswordServlet (AccountService accountService){
        this.accountService = accountService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Determina la pagina di ritorno in base al ruolo
        String returnPage = "MENTOR".equals(utente.getRuolo()) ? "dashboardMentor.jsp" : "dashboardMentee.jsp";

        String currentPassword = request.getParameter("currentPassword");
        System.out.println("CURRENT PASSWORD: " + currentPassword);
        String newPassword = request.getParameter("newPassword");
        System.out.println("\nNEW PASSWORD: " + newPassword);
        String confirmPassword = request.getParameter("confirmPassword");
        System.out.println("\nCONFIRM PASSWORD: " + confirmPassword);

        // Validazione base
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errore", "Le password non coincidono");
            request.getRequestDispatcher(returnPage).forward(request, response);
            return;
        }

        try {
            String doubleHashedCurrentPassword = accountService.hashPassword(currentPassword);
            String doubleHashedNewPassword = accountService.hashPassword(newPassword);

            // Verifica password attuale
            if (!doubleHashedCurrentPassword.equals(utente.getPassword())) {
                request.setAttribute("errore", "Password attuale non corretta");
                request.getRequestDispatcher(returnPage).forward(request, response);
                return;
            }

            // Aggiorna la password dell'utente
            utente.setPassword(doubleHashedNewPassword);
            accountService.updateUserPassword(utente);

            session.setAttribute("utente", utente);
            session.setAttribute("messaggio", "Password aggiornata con successo");
            response.sendRedirect(returnPage);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore durante l'aggiornamento della password: " + e.getMessage());
            request.getRequestDispatcher(returnPage).forward(request, response);
        }
    }
}
