package sottosistemi.Sessione.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.Sessione.service.SessionManagementService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("CheckTimeSlotServlet Tests")
class CheckTimeSlotServletTest {

    private CheckTimeSlotServlet servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private SessionManagementService sessionManagementService;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new CheckTimeSlotServlet(sessionManagementService); // Passa il mock al costruttore

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }


    @Test
    @DisplayName("TC_1.1: Utente non autenticato")
    void testDoGet_UtenteNonAutenticato() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("idUtente")).thenReturn(null);

        servlet.doGet(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("ID Mentor non trovato"));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("TC_1.2: Utente non autorizzato")
    void testDoGet_UtenteNonAutorizzato() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("STUDENT");

        servlet.doGet(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Utente non autorizzato"));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("TC_1.3: Nessun timeslot trovato")
    void testDoGet_NessunTimeslotTrovato() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(sessionManagementService.getTimeslotsByMentorIdAsMap(1)).thenReturn(List.of());

        servlet.doGet(request, response);

        String jsonResponse = responseWriter.toString();
        assertEquals("[]", jsonResponse); // Lista vuota in JSON
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("TC_1.4: Timeslots trovati")
    void testDoGet_TimeslotsTrovati() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");

        List<Map<String, Integer>> mockSlots = List.of(
                Map.of("giorno", 1, "orario", 10),
                Map.of("giorno", 2, "orario", 15)
        );
        when(sessionManagementService.getTimeslotsByMentorIdAsMap(1)).thenReturn(mockSlots);

        servlet.doGet(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("giorno"));
        assertTrue(jsonResponse.contains("orario"));
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("TC_1.5: Errore SQL durante il recupero dei timeslot")
    void testDoGet_ErroreSQL() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(sessionManagementService.getTimeslotsByMentorIdAsMap(1)).thenThrow(new SQLException("Errore SQL"));

        servlet.doGet(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Errore durante il recupero dei timeslot"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("TC_1.6: Errore generico durante l'esecuzione")
    void testDoGet_ErroreGenerico() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(sessionManagementService.getTimeslotsByMentorIdAsMap(1)).thenThrow(new RuntimeException("Errore generico"));

        servlet.doGet(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Errore imprevisto"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}