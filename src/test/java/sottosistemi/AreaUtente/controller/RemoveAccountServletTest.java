package sottosistemi.AreaUtente.controller;

import jakarta.servlet.ServletConfig;
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
import sottosistemi.AreaUtente.service.AccountService;

import java.io.PrintWriter;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@DisplayName("Test Cases for RemoveAccountServlet")
class RemoveAccountServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private AccountService accountService;

    @Mock
    private PrintWriter writer;

    @Mock
    private ServletConfig servletConfig;

    @InjectMocks
    private RemoveAccountServlet removeAccountServlet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(writer);
        removeAccountServlet.init(servletConfig);
    }

    @Test
    @DisplayName("TC_1.1: Utente non autenticato")
    void testUserNotAuthenticated() throws Exception {
        when(session.getAttribute("utente")).thenReturn(null);

        removeAccountServlet.doPost(request, response);

        verify(writer).write(contains("Utente non autenticato"));
    }

    @Test
    @DisplayName("TC_2.1: Utente con prenotazioni attive")
    void testUserWithActiveBookings() throws Exception {
        Utente utente = new Utente();
        utente.setIdUtente(1);
        when(session.getAttribute("utente")).thenReturn(utente);
        when(accountService.hasActiveBookingsForUser(1)).thenReturn(true);

        removeAccountServlet.doPost(request, response);

        verify(writer).write(contains("Non è possibile eliminare l'account"));
    }

    @Test
    @DisplayName("TC_3.1: Eliminazione riuscita")
    void testSuccessfulDeletion() throws Exception {
        Utente utente = new Utente();
        utente.setIdUtente(1);
        when(session.getAttribute("utente")).thenReturn(utente);
        when(accountService.hasActiveBookingsForUser(1)).thenReturn(false);

        removeAccountServlet.doPost(request, response);

        verify(accountService).deleteUser(1);
        verify(session).invalidate();
        verify(writer).write(contains("\"success\":true"));
    }


    @Test
    @DisplayName("TC_4.1: Errore SQL durante verifica prenotazioni")
    void testSQLExceptionDuringBookingCheck() throws Exception {
        Utente utente = new Utente();
        utente.setIdUtente(1);
        when(session.getAttribute("utente")).thenReturn(utente);
        when(accountService.hasActiveBookingsForUser(1)).thenThrow(new SQLException("Errore DB"));

        removeAccountServlet.doPost(request, response);

        verify(writer).write(contains("Errore nel database"));
    }

    @Test
    @DisplayName("TC_4.2: Errore SQL durante eliminazione utente")
    void testSQLExceptionDuringUserDeletion() throws Exception {
        Utente utente = new Utente();
        utente.setIdUtente(1);
        when(session.getAttribute("utente")).thenReturn(utente);
        when(accountService.hasActiveBookingsForUser(1)).thenReturn(false);
        doThrow(new SQLException("Errore DB")).when(accountService).deleteUser(1);

        removeAccountServlet.doPost(request, response);

        verify(writer).write(contains("Errore nel database"));
    }
}
