package sottosistemi.Autenticazione.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sottosistemi.Autenticazione.service.AutenticazioneService;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    private AutenticazioneService autenticazioneService = new AutenticazioneService();
    public RegisterServlet (AutenticazioneService autenticazioneService){
        this.autenticazioneService = autenticazioneService;
    }

    public RegisterServlet(){
        super();
        this.autenticazioneService = new AutenticazioneService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) Recupero parametri dal form
        String email = request.getParameter("email");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String hashedPassword = request.getParameter("hashedPassword");
        String ruolo = request.getParameter("ruolo");

        // 3) Verifica se l'email è già registrata
        try {
            if (autenticazioneService.isEmailRegistrata(email)) {
                request.setAttribute("errore", "Email già registrata");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



        try {

            // 2) Validazione server-side
            if (!autenticazioneService.validaInputRegistrazione(email, nome, cognome, hashedPassword, ruolo)) {
                request.setAttribute("errore", "Dati inseriti non validi");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // 4) Esegue la seconda crittografia della password
            String doubleHashedPassword = autenticazioneService.hashPassword(hashedPassword);

            // 5) Salva il nuovo utente nel database
            autenticazioneService.registraNuovoUtente(email, nome, cognome, doubleHashedPassword, ruolo);

            // 6) Redirect alla pagina di login
            response.sendRedirect("login.jsp");

        } catch (Exception e) {
            // 7) Gestione errori
            request.setAttribute("errore", "Errore durante la registrazione");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}