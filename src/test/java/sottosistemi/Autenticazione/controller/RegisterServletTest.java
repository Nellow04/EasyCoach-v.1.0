package sottosistemi.Autenticazione.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.Autenticazione.service.AutenticazioneService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for RegisterServlet")
class RegisterServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private AutenticazioneService autenticazioneService;

    @InjectMocks
    private RegisterServlet registerServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerServlet = new RegisterServlet(autenticazioneService); // Iniettiamo il mock di AutenticazioneService
    }

    @Test
    @DisplayName("TC_2.1: Registrazione riuscita - Redirect a login.jsp")
    void testDoPost_RegistrazioneRiuscita() throws Exception {
        // Configura i parametri validi
        when(request.getParameter("email")).thenReturn("test@email.com");
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("hashedPassword")).thenReturn("hashedPassword123");
        when(request.getParameter("ruolo")).thenReturn("MENTOR");

        // Configura i metodi di AutenticazioneService
        when(autenticazioneService.validaInputRegistrazione(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(autenticazioneService.isEmailRegistrata("test@email.com")).thenReturn(false);
        when(autenticazioneService.hashPassword("hashedPassword123")).thenReturn("doubleHashedPassword");

        // Esegui il metodo
        registerServlet.doPost(request, response);

        // Verifica il redirect a login.jsp
        verify(response).sendRedirect("login.jsp");
    }

    @Test
    @DisplayName("TC_2.2: Parametri non validi - Errore a register.jsp")
    void testDoPost_ParametriNonValidi() throws Exception {
        when(request.getParameter("email")).thenReturn(""); // Parametri non validi
        when(autenticazioneService.validaInputRegistrazione(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(false);
        when(request.getRequestDispatcher("register.jsp")).thenReturn(dispatcher);

        registerServlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), eq("Dati inseriti non validi"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_2.3: Email già registrata - Errore a register.jsp")
    void testDoPost_EmailGiaRegistrata() throws Exception {
        when(request.getParameter("email")).thenReturn("test@email.com");
        when(autenticazioneService.validaInputRegistrazione(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(autenticazioneService.isEmailRegistrata("test@email.com")).thenReturn(true);
        when(request.getRequestDispatcher("register.jsp")).thenReturn(dispatcher);

        registerServlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), eq("Email già registrata"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_2.4: Eccezione durante la registrazione - Errore generico")
    void testDoPost_EccezioneGenerica() throws Exception {
        when(request.getParameter("email")).thenReturn("test@email.com");
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("hashedPassword")).thenReturn("hashedPassword123");
        when(request.getParameter("ruolo")).thenReturn("MENTOR");

        when(autenticazioneService.validaInputRegistrazione(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(autenticazioneService.isEmailRegistrata("test@email.com")).thenReturn(false);
        when(autenticazioneService.hashPassword(anyString())).thenThrow(new RuntimeException()); // Simula un'eccezione
        when(request.getRequestDispatcher("register.jsp")).thenReturn(dispatcher);

        registerServlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), eq("Errore durante la registrazione"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_2.5: Eccezione SQL durante il controllo email - RuntimeException")
    void testDoPost_EccezioneSQLDuranteControlloEmail() throws Exception {
        // Configura i parametri validi
        when(request.getParameter("email")).thenReturn("test@email.com");
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("hashedPassword")).thenReturn("hashedPassword123");
        when(request.getParameter("ruolo")).thenReturn("MENTOR");

        // Configura il comportamento di autenticazioneService per lanciare SQLException
        when(autenticazioneService.isEmailRegistrata("test@email.com")).thenThrow(new SQLException());

        // Verifica che venga lanciata una RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registerServlet.doPost(request, response);
        });

        // Verifica che l'eccezione originale sia stata incapsulata
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof SQLException);
    }

}
