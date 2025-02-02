package sottosistemi.AreaUtente.controller;

import jakarta.servlet.RequestDispatcher;
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

import static org.mockito.Mockito.*;

@DisplayName("Test Cases for UpdatePasswordServlet")
class UpdatePasswordServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private AccountService accountService;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private ServletConfig servletConfig;

    @InjectMocks
    private UpdatePasswordServlet updatePasswordServlet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        updatePasswordServlet.init(servletConfig);
    }

    @Test
    @DisplayName("TC_1.1: Utente non autenticato")
    void testUserNotAuthenticated() throws Exception {
        when(session.getAttribute("utente")).thenReturn(null);

        updatePasswordServlet.doPost(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    @Test
    @DisplayName("TC_2.1: Le password nuove non coincidono")
    void testPasswordsDoNotMatch() throws Exception {
        Utente utente = new Utente();
        when(session.getAttribute("utente")).thenReturn(utente);
        when(request.getParameter("newPassword")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password321");

        updatePasswordServlet.doPost(request, response);

        verify(request).setAttribute("errore", "Le password non coincidono");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_3.1: Password attuale errata")
    void testWrongCurrentPassword() throws Exception {
        Utente utente = new Utente();
        utente.setPassword("hashedPassword");

        when(session.getAttribute("utente")).thenReturn(utente);
        when(request.getParameter("currentPassword")).thenReturn("wrongPassword");
        when(request.getParameter("newPassword")).thenReturn("newPassword"); // Simuliamo newPassword
        when(request.getParameter("confirmPassword")).thenReturn("newPassword"); // Simuliamo confirmPassword
        when(accountService.hashPassword("wrongPassword")).thenReturn("wrongHashed");

        updatePasswordServlet.doPost(request, response);

        verify(request).setAttribute("errore", "Password attuale non corretta");
        verify(dispatcher).forward(request, response);
    }


    @Test
    @DisplayName("TC_4.1: Password aggiornata correttamente")
    void testSuccessfulPasswordUpdate() throws Exception {
        Utente utente = new Utente();
        utente.setPassword("hashedPassword");
        when(session.getAttribute("utente")).thenReturn(utente);
        when(request.getParameter("currentPassword")).thenReturn("oldPassword");
        when(request.getParameter("newPassword")).thenReturn("newPassword");
        when(request.getParameter("confirmPassword")).thenReturn("newPassword");
        when(accountService.hashPassword("oldPassword")).thenReturn("hashedPassword");
        when(accountService.hashPassword("newPassword")).thenReturn("newHashedPassword");

        updatePasswordServlet.doPost(request, response);

        verify(accountService).updateUserPassword(utente);
        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("TC_5.1: Errore durante l'hashing della password")
    void testHashingError() throws Exception {
        Utente utente = new Utente();
        utente.setPassword("hashedPassword");
        when(session.getAttribute("utente")).thenReturn(utente);
        when(request.getParameter("currentPassword")).thenReturn("oldPassword");
        when(request.getParameter("newPassword")).thenReturn("newPassword");
        when(request.getParameter("confirmPassword")).thenReturn("newPassword");
        when(accountService.hashPassword("oldPassword")).thenThrow(new RuntimeException("Hashing error"));

        updatePasswordServlet.doPost(request, response);

        verify(request).setAttribute(contains("errore"), contains("Errore durante l'aggiornamento"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_5.2: Errore nel database")
    void testDatabaseError() throws Exception {
        Utente utente = new Utente();
        utente.setPassword("hashedPassword");
        when(session.getAttribute("utente")).thenReturn(utente);
        when(request.getParameter("currentPassword")).thenReturn("oldPassword");
        when(request.getParameter("newPassword")).thenReturn("newPassword");
        when(request.getParameter("confirmPassword")).thenReturn("newPassword");
        when(accountService.hashPassword("oldPassword")).thenReturn("hashedPassword");
        when(accountService.hashPassword("newPassword")).thenReturn("newHashedPassword");
        doThrow(new RuntimeException("DB Error")).when(accountService).updateUserPassword(utente);

        updatePasswordServlet.doPost(request, response);

        verify(request).setAttribute(contains("errore"), contains("Errore durante l'aggiornamento"));
        verify(dispatcher).forward(request, response);
    }
}
