package sottosistemi.Autenticazione.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

@DisplayName("Test Cases for LogoutServlet")
class LogoutServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private LogoutServlet logoutServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_3.1: Sessione esistente - Sessione invalidata e redirect")
    void testDoGet_SessioneEsistente() throws Exception {
        // Configura il mock per simulare una sessione esistente
        when(request.getSession(false)).thenReturn(session);

        // Esegui il metodo doGet
        logoutServlet.doGet(request, response);

        // Verifica che la sessione venga invalidata
        verify(session).invalidate();

        // Verifica che ci sia il redirect
        verify(response).sendRedirect("index.jsp");
    }

    @Test
    @DisplayName("TC_3.2: Nessuna sessione - Solo redirect")
    void testDoGet_NessunaSessione() throws Exception {
        // Configura il mock per simulare l'assenza di una sessione
        when(request.getSession(false)).thenReturn(null);

        // Esegui il metodo doGet
        logoutServlet.doGet(request, response);

        // Verifica che la sessione non venga invalidata
        verify(session, never()).invalidate();

        // Verifica che ci sia il redirect
        verify(response).sendRedirect("index.jsp");
    }

    @Test
    @DisplayName("TC_3.3: Richiesta POST - Comportamento identico a GET")
    void testDoPost_ComportamentoIdenticoAGET() throws Exception {
        // Configura il mock per simulare una sessione esistente
        when(request.getSession(false)).thenReturn(session);

        // Esegui il metodo doPost
        logoutServlet.doPost(request, response);

        // Verifica che la sessione venga invalidata
        verify(session).invalidate();

        // Verifica che ci sia il redirect
        verify(response).sendRedirect("index.jsp");
    }
}
