package sottosistemi.Autenticazione.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.beans.Utente;
import sottosistemi.Autenticazione.service.AutenticazioneService;

import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private AutenticazioneService autenticazioneService = new AutenticazioneService();

    // Costruttore di default richiesto dal container servlet
    public LoginServlet() {
        super();
        this.autenticazioneService = new AutenticazioneService();
    }
    // Costruttore per iniettare dipendenze nei test
    public LoginServlet(AutenticazioneService autenticazioneService) {
        this.autenticazioneService = autenticazioneService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) Recupero parametri dal form
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // 2) Validazione server-side dei campi vuoti
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errore", "Email e password non possono essere vuoti");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        try {
            // 3) Verifica credenziali con il Service
            Utente utente = autenticazioneService.effettuaLogin(email, password);

            // 4) Se le credenziali non sono valide, invia errore
            if (utente == null) {
                request.setAttribute("errore", "Credenziali non valide");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            // 5) Login riuscito: salva informazioni dell'utente nella sessione
            HttpSession session = request.getSession();
            session.setAttribute("utente", utente);
            session.setAttribute("idUtente", utente.getIdUtente());
            session.setAttribute("ruolo", utente.getRuolo().toUpperCase());

            // 6) Redirect in base al ruolo
            switch (utente.getRuolo()) {
                case "ADMIN":
                    response.sendRedirect("dashboardAdmin.jsp");
                    break;
                case "MENTOR":
                case "MENTEE":
                default:
                    response.sendRedirect("index.jsp");
                    break;
            }

        } catch (Exception e) {
            // 7) Gestione errore generico
            request.setAttribute("errore", "Errore durante l'autenticazione");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}