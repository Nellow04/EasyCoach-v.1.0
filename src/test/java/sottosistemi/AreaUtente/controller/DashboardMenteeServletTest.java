package sottosistemi.AreaUtente.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.PrenotazioneDetailsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.AreaUtente.service.MenteeDashboardService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for DashboardMenteeServlet")
class DashboardMenteeServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private MenteeDashboardService menteeDashboardService;

    @Mock
    private PrintWriter writer;

    @InjectMocks
    private DashboardMenteeServlet servlet;

    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
        stringWriter = new StringWriter();

        // Configura il mock writer per scrivere su stringWriter
        doAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            stringWriter.write(arg);
            return null;
        }).when(writer).write(any(String.class));

        when(response.getWriter()).thenReturn(writer); // Usa il mock writer
        when(request.getRequestDispatcher("/dashboardMentee.jsp")).thenReturn(dispatcher);
    }

    // Categoria 1: Autorizzazione utente
    @Test
    @DisplayName("TC_1.1: Accesso non autorizzato (ruolo non MENTEE)")
    void testUnauthorizedAccess() throws ServletException, IOException {
        // Setup
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");

        // Test
        servlet.doGet(request, response);

        // Verify
        verify(response).sendRedirect(any());
    }

    @Test
    @DisplayName("TC_1.2: Accesso autorizzato (ruolo MENTEE)")
    void testAuthorizedAccess() throws ServletException, IOException {
        // Setup
        when(session.getAttribute("ruolo")).thenReturn("MENTEE");
        when(request.getParameter("action")).thenReturn(null);

        // Test
        servlet.doGet(request, response);

        // Verify
        verify(dispatcher).forward(request, response);
    }

    // Categoria 2: Gestione delle azioni
    @Test
    @DisplayName("TC_2.1: Action null - display dashboard")
    void testNullAction() throws ServletException, IOException {
        // Setup
        when(session.getAttribute("ruolo")).thenReturn("MENTEE");
        when(request.getParameter("action")).thenReturn(null);

        // Test
        servlet.doGet(request, response);

        // Verify
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_2.2: Action getBookings - success")
    void testGetBookingsSuccess() throws Exception {
        // Setup
        when(session.getAttribute("ruolo")).thenReturn("MENTEE");
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(request.getParameter("action")).thenReturn("getBookings");

        List<PrenotazioneDetailsDTO> activeBookings = new ArrayList<>();
        List<PrenotazioneDetailsDTO> completedBookings = new ArrayList<>();

        when(menteeDashboardService.findActiveBookingsForMentee(1)).thenReturn(activeBookings);
        when(menteeDashboardService.findCompletedBookingsForMentee(1)).thenReturn(completedBookings);

        // Test
        servlet.doGet(request, response);

        // Verify
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(writer).write(any(String.class)); // Ora writer è un mock

        // Verifica il contenuto JSON
        String result = stringWriter.toString();
        assertTrue(result.contains("activeBookings"));
        assertTrue(result.contains("completedBookings"));
    }

    // Categoria 3: Gestione errori
    @Test
    @DisplayName("TC_3.1: GetBookings - utente non autenticato")
    void testGetBookingsUnauthenticated() throws ServletException, IOException {
        // Setup
        when(session.getAttribute("ruolo")).thenReturn("MENTEE");
        when(session.getAttribute("idUtente")).thenReturn(null);
        when(request.getParameter("action")).thenReturn("getBookings");

        // Test
        servlet.doGet(request, response);

        // Verify
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("TC_3.2: GetBookings - errore del service")
    void testGetBookingsServiceError() throws Exception {
        // Setup
        when(session.getAttribute("ruolo")).thenReturn("MENTEE");
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(request.getParameter("action")).thenReturn("getBookings");

        when(menteeDashboardService.findActiveBookingsForMentee(1))
                .thenThrow(new RuntimeException("Database error"));

        // Test
        servlet.doGet(request, response);

        // Verify
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String result = stringWriter.toString();
        assertTrue(result.contains("error"));
        assertTrue(result.contains("Errore nel caricamento delle prenotazioni"));
    }

}