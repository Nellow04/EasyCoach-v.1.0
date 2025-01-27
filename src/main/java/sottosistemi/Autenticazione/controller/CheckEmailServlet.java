package sottosistemi.Autenticazione.controller;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sottosistemi.Autenticazione.service.AutenticazioneService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/CheckEmailServlet")
public class CheckEmailServlet extends HttpServlet {
    private AutenticazioneService autenticazioneService;
    private final Gson gson = new Gson();

    // Costruttore predefinito (per ambiente reale)
    public CheckEmailServlet() {
        super();
        this.autenticazioneService = new AutenticazioneService();
    }

    // Costruttore per test (iniezione di dipendenza)
    public CheckEmailServlet(AutenticazioneService autenticazioneService) {
        this.autenticazioneService = autenticazioneService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");

        Map<String, Boolean> result = new HashMap<>();
        try {
            if (email == null || email.isEmpty()) { // Validazione input
                result.put("exists", false);
            } else {
                boolean exists = autenticazioneService.checkEmailExists(email);
                result.put("exists", exists);
            }
        } catch (Exception e) {
            result.put("exists", false);
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(result));
    }


}