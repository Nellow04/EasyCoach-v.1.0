package sottosistemi.Autenticazione.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.beans.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.Autenticazione.service.AutenticazioneService;

import static org.mockito.Mockito.*;

@DisplayName("Test Cases for LoginServlet")
class LoginServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private AutenticazioneService autenticazioneService;

    @InjectMocks
    private LoginServlet loginServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_1.1: Parametri vuoti - Mostra errore")
    void testDoPost_ParametriVuoti() throws Exception {
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("password")).thenReturn("");
        when(request.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);

        loginServlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), eq("Email e password non possono essere vuoti"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_1.2: Credenziali non valide - Mostra errore")
    void testDoPost_CredenzialiNonValide() throws Exception {
        when(request.getParameter("email")).thenReturn("user@test.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(autenticazioneService.effettuaLogin("user@test.com", "wrongpassword")).thenReturn(null);
        when(request.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);

        loginServlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), eq("Credenziali non valide"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_1.3: Login riuscito - Redirect ADMIN")
    void testDoPost_LoginRiuscito_Admin() throws Exception {
        // Simula l'utente con ruolo ADMIN
        Utente utente = new Utente();
        utente.setRuolo("ADMIN");
        utente.setIdUtente(1);

        // Configura i mock per i parametri e la sessione
        when(request.getParameter("email")).thenReturn("admin@test.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(autenticazioneService.effettuaLogin("admin@test.com", "password123")).thenReturn(utente);
        when(request.getSession()).thenReturn(session);

        // Esegui il metodo
        loginServlet.doPost(request, response);

        // Verifica che i dati siano salvati correttamente nella sessione
        verify(session).setAttribute("utente", utente);
        verify(session).setAttribute("idUtente", utente.getIdUtente());
        verify(session).setAttribute("ruolo", utente.getRuolo().toUpperCase());

        // Verifica che il redirect avvenga verso la dashboard admin
        verify(response).sendRedirect("dashboardAdmin.jsp");
    }


    @Test
    @DisplayName("TC_1.4: Login riuscito - Redirect MENTOR")
    void testDoPost_LoginRiuscito_Mentor() throws Exception {
        Utente utente = new Utente();
        utente.setRuolo("MENTOR");
        utente.setIdUtente(2);

        when(request.getParameter("email")).thenReturn("mentor@test.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(autenticazioneService.effettuaLogin("mentor@test.com", "password123")).thenReturn(utente);
        when(request.getSession()).thenReturn(session);

        loginServlet.doPost(request, response);

        verify(session).setAttribute("utente", utente);
        verify(session).setAttribute("idUtente", utente.getIdUtente());
        verify(session).setAttribute("ruolo", utente.getRuolo().toUpperCase());

        verify(response).sendRedirect("index.jsp");
    }


    @Test
    @DisplayName("TC_1.5: Eccezione generica - Mostra errore generico")
    void testDoPost_EccezioneGenerica() throws Exception {
        // Configura mock per scatenare un'eccezione
        when(request.getParameter("email")).thenReturn("user@test.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(autenticazioneService.effettuaLogin("user@test.com", "password123")).thenThrow(new RuntimeException());
        when(request.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);

        loginServlet.doPost(request, response);

        // Verifica che l'errore venga impostato e il forward eseguito
        verify(request).setAttribute(eq("errore"), eq("Errore durante l'autenticazione"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_1.6: Email null - Mostra errore")
    void testDoPost_EmailNull() throws Exception {
        when(request.getParameter("email")).thenReturn(null);
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);

        loginServlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), eq("Email e password non possono essere vuoti"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_1.7: Email vuota - Mostra errore")
    void testDoPost_EmailVuota() throws Exception {
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);

        loginServlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), eq("Email e password non possono essere vuoti"));
        verify(dispatcher).forward(request, response);
    }


    @Test
    @DisplayName("TC_1.7: Password null - Mostra errore")
    void testDoPost_PasswordNull() throws Exception {
        when(request.getParameter("email")).thenReturn("test@email.com");
        when(request.getParameter("password")).thenReturn(null);
        when(request.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);

        loginServlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), eq("Email e password non possono essere vuoti"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_1.9: Password vuota - Mostra errore")
    void testDoPost_PasswordVuota() throws Exception {
        when(request.getParameter("email")).thenReturn("test@email.com");
        when(request.getParameter("password")).thenReturn("");
        when(request.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);

        loginServlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), eq("Email e password non possono essere vuoti"));
        verify(dispatcher).forward(request, response);
    }


    @Test
    @DisplayName("TC_1.8: Login riuscito - Redirect MENTEE")
    void testDoPost_LoginRiuscito_Mentee() throws Exception {
        Utente utente = new Utente();
        utente.setRuolo("MENTEE");
        utente.setIdUtente(3);

        when(request.getParameter("email")).thenReturn("mentee@test.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(autenticazioneService.effettuaLogin("mentee@test.com", "password123")).thenReturn(utente);
        when(request.getSession()).thenReturn(session);

        loginServlet.doPost(request, response);

        verify(session).setAttribute("utente", utente);
        verify(session).setAttribute("idUtente", utente.getIdUtente());
        verify(session).setAttribute("ruolo", utente.getRuolo().toUpperCase());

        verify(response).sendRedirect("index.jsp");
    }

    @Test
    @DisplayName("TC_1.9: Login riuscito - Redirect Default")
    void testDoPost_LoginRiuscito_Default() throws Exception {
        Utente utente = new Utente();
        utente.setRuolo("UNKNOWN"); // Ruolo non previsto
        utente.setIdUtente(4);

        when(request.getParameter("email")).thenReturn("unknown@test.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(autenticazioneService.effettuaLogin("unknown@test.com", "password123")).thenReturn(utente);
        when(request.getSession()).thenReturn(session);

        loginServlet.doPost(request, response);

        verify(session).setAttribute("utente", utente);
        verify(session).setAttribute("idUtente", utente.getIdUtente());
        verify(session).setAttribute("ruolo", utente.getRuolo().toUpperCase());

        verify(response).sendRedirect("index.jsp");
    }

}
