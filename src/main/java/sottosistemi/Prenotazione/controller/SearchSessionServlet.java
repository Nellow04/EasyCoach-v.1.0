package sottosistemi.Prenotazione.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.beans.Sessione;
import sottosistemi.Prenotazione.service.SessionRetrievalService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet per la ricerca di sessioni in base al titolo.
 */
@WebServlet("/SearchSessionServlet")
public class SearchSessionServlet extends HttpServlet {

    private SessionRetrievalService sessionRetrievalService = new SessionRetrievalService();
    private final Gson gson = new Gson();

    public SearchSessionServlet (SessionRetrievalService sessionRetrievalService){
        this.sessionRetrievalService = sessionRetrievalService;
    }

    public SearchSessionServlet() {
        super();
        this.sessionRetrievalService = new SessionRetrievalService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("q");
        if (query == null) query = "";

        List<Sessione> resultList;
        try {
            // Usa il service per effettuare la ricerca
            resultList = sessionRetrievalService.findSessionsByTitleLike(query);
        } catch (SQLException e) {
            throw new ServletException("DB error searching sessions", e);
        }

        // Convert to JSON
        String json = gson.toJson(resultList);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
