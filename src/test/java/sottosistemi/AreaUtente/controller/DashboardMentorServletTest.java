package sottosistemi.AreaUtente.controller;

import com.google.gson.Gson;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.beans.Sessione;
import model.dto.PrenotazioneDetailsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.AreaUtente.service.MentorDashboardService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for DashboardMentorServlet")
class DashboardMentorServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private MentorDashboardService mentorDashboardService;

    @Mock
    private PrintWriter writer;

    @InjectMocks
    private DashboardMentorServlet servlet;

    private StringWriter stringWriter;
    private Gson gson = new Gson();

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
        stringWriter = new StringWriter();

        doAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            stringWriter.write(arg);
            return null;
        }).when(writer).write(any(String.class));

        when(response.getWriter()).thenReturn(writer);
        when(request.getRequestDispatcher("/dashboardMentor.jsp")).thenReturn(dispatcher);
    }

    // CATEGORIA 1: AUTORIZZAZIONE
    @Test
    @DisplayName("TC_1.1: Accesso con ruolo non MENTOR")
    void testUnauthorizedRole() throws ServletException, IOException {
        when(session.getAttribute("ruolo")).thenReturn("STUDENT");

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(stringWriter.toString().contains("Accesso negato"));
    }

    @Test
    @DisplayName("TC_1.2: Accesso autorizzato come MENTOR")
    void testAuthorizedAccess() throws ServletException, IOException {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn(null);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    // CATEGORIA 2: GESTIONE AZIONI
    @Test
    @DisplayName("TC_2.1: Action getBookings - successo")
    void testGetBookingsSuccess() throws Exception {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(request.getParameter("action")).thenReturn("getBookings");

        List<PrenotazioneDetailsDTO> mockBookings = new ArrayList<>();
        when(mentorDashboardService.findActiveBookingsForMentor(1)).thenReturn(mockBookings);

        servlet.doGet(request, response);

        verify(response).setContentType("application/json");
        Map<String, Object> responseData = gson.fromJson(stringWriter.toString(), HashMap.class);
        assertTrue(responseData.containsKey("activeBookings"));
    }

    @Test
    @DisplayName("TC_3.4: GetSessions - idUtente mancante")
    void testMissingUserIdForSessions() throws ServletException, IOException {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(null);
        when(request.getParameter("action")).thenReturn("getSessions");

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(stringWriter.toString().contains("Utente non autorizzato"));
    }

    @Test
    @DisplayName("TC_2.2: Action getSessions - successo")
    void testGetSessionsSuccess() throws Exception {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(request.getParameter("action")).thenReturn("getSessions");

        Sessione mockSession = new Sessione();
        mockSession.setIdSessione(1);
        mockSession.setIdUtente(1);
        mockSession.setTitolo("Introduzione a Java");
        mockSession.setDescrizione("Una sessione introduttiva su Java.");
        mockSession.setPrezzo(50.0);
        mockSession.setImmagine("java.png");
        mockSession.setStatusSessione("ATTIVA");

        List<Sessione> mockSessions = List.of(mockSession);

        when(mentorDashboardService.findSessionsByMentorId(1)).thenReturn(mockSessions);

        servlet.doGet(request, response);

        verify(response).setContentType("application/json");
        Map<String, Object> responseData = gson.fromJson(stringWriter.toString(), HashMap.class);
        assertTrue(responseData.containsKey("sessions"));
        List<Map<String, Object>> sessions = (List<Map<String, Object>>) responseData.get("sessions");
        assertEquals(1, sessions.size());

        Map<String, Object> sessionData = sessions.get(0);
        assertEquals(1.0, sessionData.get("idSessione"));
        assertEquals("Introduzione a Java", sessionData.get("titolo"));
        assertEquals("ATTIVA", sessionData.get("statusSessione"));
    }

    @Test
    @DisplayName("TC_2.3: Action sconosciuta - mostra dashboard")
    void testUnknownAction() throws ServletException, IOException {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("invalidAction");

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    // CATEGORIA 3: GESTIONE ERRORI
    @Test
    @DisplayName("TC_3.1: GetBookings - idUtente mancante")
    void testMissingUserIdForBookings() throws ServletException, IOException {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(null);
        when(request.getParameter("action")).thenReturn("getBookings");

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(stringWriter.toString().contains("Utente non autorizzato"));
    }

    @Test
    @DisplayName("TC_3.2: GetSessions - eccezione del service")
    void testServiceExceptionForSessions() throws Exception {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(request.getParameter("action")).thenReturn("getSessions");

        when(mentorDashboardService.findSessionsByMentorId(1))
                .thenThrow(new RuntimeException("DB Error"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(stringWriter.toString().contains("Errore nel caricamento delle sessioni"));
    }

    @Test
    @DisplayName("TC_3.3: GetBookings - eccezione del service")
    void testServiceExceptionForBookings() throws Exception {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(request.getParameter("action")).thenReturn("getBookings");

        when(mentorDashboardService.findActiveBookingsForMentor(1))
                .thenThrow(new RuntimeException("DB Error"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(stringWriter.toString().contains("Errore nel caricamento delle prenotazioni"));
    }
}