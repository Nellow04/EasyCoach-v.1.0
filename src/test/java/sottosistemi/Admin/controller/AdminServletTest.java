package sottosistemi.Admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.Admin.service.AdminSessionManagementService;
import sottosistemi.Admin.service.AdminUserManagementService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("AdminServlet Tests")
class AdminServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private AdminUserManagementService adminUserManagementService;

    @Mock
    private AdminSessionManagementService adminSessionManagementService;

    private AdminServlet adminServlet;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        adminServlet = new AdminServlet(adminUserManagementService, adminSessionManagementService);

        // Configurazione del writer per simulare la risposta
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }


    @Test
    @DisplayName("TC_1.1: Accesso negato per ruolo non ADMIN")
    void testDoPost_AccessDenied() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("getUsers");

        adminServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Accesso negato"));
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("TC_1.2: Azione non specificata")
    void testDoPost_ActionNotSpecified() throws Exception {
        // Mock della sessione e del ruolo
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("ADMIN");

        // Azione non specificata
        when(request.getParameter("action")).thenReturn(null);

        // Esegui il metodo della servlet
        adminServlet.doPost(request, response);

        // Verifica che la risposta contenga l'errore atteso
        String jsonResponse = responseWriter.toString();
        assertEquals("{\"success\":false,\"error\":\"Azione non valida\"}", jsonResponse, "Il messaggio di errore non corrisponde");

        // Verifica che lo stato della risposta sia 400 (Bad Request)
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }




    @Test
    @DisplayName("TC_2.1: Recupero utenti con successo")
    void testDoPost_GetUsers_Success() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("ADMIN");
        when(request.getParameter("action")).thenReturn("getUsers");

        List<Map<String, Object>> mockUsers = List.of(
                Map.of("id", 1, "nome", "Mario Rossi", "email", "mario@example.com", "ruolo", "ADMIN"),
                Map.of("id", 2, "nome", "Luigi Verdi", "email", "luigi@example.com", "ruolo", "MENTOR")
        );
        when(adminUserManagementService.getAllUsersSimplified()).thenReturn(mockUsers);

        adminServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Mario Rossi"));
        assertTrue(jsonResponse.contains("Luigi Verdi"));
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("TC_2.2: Recupero sessioni con successo")
    void testDoPost_GetSessions_Success() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("ADMIN");
        when(request.getParameter("action")).thenReturn("getSessions");

        List<Map<String, Object>> mockSessions = List.of(
                Map.of("id", 1, "titolo", "Sessione 1", "mentorNome", "Mario Rossi"),
                Map.of("id", 2, "titolo", "Sessione 2", "mentorNome", "Luigi Verdi")
        );
        when(adminSessionManagementService.getAllSessionsEnriched()).thenReturn(mockSessions);

        adminServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Sessione 1"));
        assertTrue(jsonResponse.contains("Sessione 2"));
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("TC_3.1: Eliminazione utente con successo")
    void testDoPost_DeleteUser_Success() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("ADMIN");
        when(request.getParameter("action")).thenReturn("deleteUser");
        when(request.getParameter("userId")).thenReturn("1");

        doNothing().when(adminUserManagementService).deleteUser(1);

        adminServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("\"success\":true"));
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(adminUserManagementService).deleteUser(1);
    }


    @Test
    @DisplayName("TC_3.2: Eliminazione sessione con successo")
    void testDoPost_DeleteSession_Success() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("ADMIN");
        when(request.getParameter("action")).thenReturn("deleteSession");
        when(request.getParameter("sessionId")).thenReturn("1");

        doNothing().when(adminSessionManagementService).deleteSession(1);

        adminServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("\"success\":true"));
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(adminSessionManagementService).deleteSession(1);
    }


    @Test
    @DisplayName("TC_3.3: ID utente non valido per eliminazione")
    void testDoPost_DeleteUser_InvalidId() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("ADMIN");
        when(request.getParameter("action")).thenReturn("deleteUser");
        when(request.getParameter("userId")).thenReturn("invalid");

        adminServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("ID utente non valido"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("TC_3.4: ID sessione non valido per eliminazione")
    void testDoPost_DeleteSession_InvalidId() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("ADMIN");
        when(request.getParameter("action")).thenReturn("deleteSession");
        when(request.getParameter("sessionId")).thenReturn("invalid");

        adminServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("ID sessione non valido"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
