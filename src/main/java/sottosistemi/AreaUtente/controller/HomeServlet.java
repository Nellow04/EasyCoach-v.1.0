package sottosistemi.AreaUtente.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.beans.Sessione;
import model.beans.Utente;
import sottosistemi.AreaUtente.service.HomeService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet per la gestione della homepage.
 * Recupera i dati delle sessioni in evidenza e dei mentor più attivi tramite HomeService.
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    private final HomeService homeService;

    public HomeServlet() {
        super();
        this.homeService = new HomeService();
    }

    public HomeServlet (HomeService homeService){
        this.homeService = homeService;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Recupera le sessioni in evidenza tramite il service
            List<Sessione> sessioniInEvidenza = homeService.getSessioniInEvidenza();
            request.setAttribute("sessioniInEvidenza", sessioniInEvidenza);

            // Recupera i mentor casuali tramite il service
            List<Utente> mentorCasuali = homeService.getMentorCasuali();
            request.setAttribute("mentorPiuAttivi", mentorCasuali);

            // Forward alla vista
            request.getRequestDispatcher("/index.jsp").forward(request, response);

        } catch (SQLException e) {
            // Log dell'errore
            getServletContext().log("Errore nel recupero dei dati per la homepage", e);

            // Redirect alla pagina di errore
            response.sendRedirect("error.jsp");
        }
    }
}
