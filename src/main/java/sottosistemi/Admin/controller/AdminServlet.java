package sottosistemi.Admin.controller;

import com.google.gson.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sottosistemi.Admin.service.AdminSessionManagementService;
import sottosistemi.Admin.service.AdminUserManagementService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminServlet", urlPatterns = {"/AdminServlet"})
public class AdminServlet extends HttpServlet {
    private AdminUserManagementService adminUserManagementService;
    private AdminSessionManagementService adminSessionManagementService;
    private Gson gson;

    public AdminServlet (){
        super();
        this.adminUserManagementService = new AdminUserManagementService();
        this.adminSessionManagementService = new AdminSessionManagementService();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        });
        this.gson = gsonBuilder.create();
    }

    public AdminServlet (AdminUserManagementService adminUserManagementService, AdminSessionManagementService adminSessionManagementService){
        this.adminSessionManagementService = adminSessionManagementService;
        this.adminUserManagementService = adminUserManagementService;
        this.gson = new Gson();
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        // Verifica che l'utente sia un admin
        String ruolo = (String) request.getSession().getAttribute("ruolo");
        if (!"ADMIN".equals(ruolo)) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Accesso negato");
            return;
        }

        if (action == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Azione non valida");
            return;
        }



        try {
            switch (action) {
                case "getUsers":
                    handleGetUsers(request, response);
                    break;
                case "getSessions":
                    handleGetSessions(request, response);
                    break;
                case "deleteUser":
                    handleDeleteUser(request, response);
                    break;
                case "deleteSession":
                    handleDeleteSession(request, response);
                    break;
                default:
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Azione non valida");
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleError(response, e);
        }
    }

    private void handleGetUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Recupero la lista di utenti in forma semplificata tramite il Service
            List<Map<String, Object>> simplifiedUsers = adminUserManagementService.getAllUsersSimplified();

            String jsonResponse = gson.toJson(simplifiedUsers);
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            handleError(response, e);
        }
    }

    private void handleGetSessions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Recupero la lista di sessioni arricchite tramite il Service
            List<Map<String, Object>> enrichedSessions = adminSessionManagementService.getAllSessionsEnriched();

            String jsonResponse = gson.toJson(enrichedSessions);
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            handleError(response, e);
        }
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {

            int userId = Integer.parseInt(request.getParameter("userId"));

            try {
                // Deleghiamo la logica al Service
                adminUserManagementService.deleteUser(userId);

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                response.getWriter().write(gson.toJson(result));
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore nell'eliminazione dell'utente");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "ID utente non valido");
        } catch (Exception e) {
            e.printStackTrace();
            handleError(response, e);
        }
    }

    private void handleDeleteSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int sessionId = Integer.parseInt(request.getParameter("sessionId"));

            try {
                // Deleghiamo la logica al Service
                adminSessionManagementService.deleteSession(sessionId);
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                response.getWriter().write(gson.toJson(result));
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore nell'archiviazione della sessione");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "ID sessione non valido");
        } catch (Exception e) {
            e.printStackTrace();
            handleError(response, e);
        }
    }

    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        e.printStackTrace();
        Map<String, String> error = new HashMap<>();
        error.put("error", "Si è verificato un errore: " + e.getMessage());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write(gson.toJson(error));
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        response.getWriter().write(gson.toJson(error));
    }
}
