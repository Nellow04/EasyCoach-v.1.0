package sottosistemi.AreaUtente.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.beans.Sessione;
import model.beans.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.AreaUtente.service.HomeService;

import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.*;

@DisplayName("Test Cases for HomeServlet")
class HomeServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HomeService homeService;

    @Mock
    private ServletContext servletContext;
    @Mock
    private ServletConfig servletConfig;

    @InjectMocks
    private HomeServlet homeServlet;



    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(request.getRequestDispatcher("/index.jsp")).thenReturn(dispatcher);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        // Inizializza la servlet con il mock di ServletConfig
        homeServlet.init(servletConfig);
    }

    @Test
    @DisplayName("TC_1.1: Recupero dati con successo")
    void testHomeServletSuccess() throws Exception {
        // Simulazione del servizio che restituisce dati validi
        List<Sessione> sessioni = List.of(new Sessione());
        List<Utente> mentor = List.of(new Utente());
        when(homeService.getSessioniInEvidenza()).thenReturn(sessioni);
        when(homeService.getMentorCasuali()).thenReturn(mentor);

        homeServlet.doGet(request, response);

        // Verifica che i dati siano stati impostati correttamente
        verify(request).setAttribute("sessioniInEvidenza", sessioni);
        verify(request).setAttribute("mentorPiuAttivi", mentor);
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_2.1: Errore SQL durante il recupero dati")
    void testHomeServletSQLException() throws Exception {
        // Simula un errore nel servizio
        when(homeService.getSessioniInEvidenza()).thenThrow(new SQLException("Errore database"));

        homeServlet.doGet(request, response);

        // Verifica che venga loggato l'errore e fatto il redirect
        verify(servletContext).log(anyString(), any(SQLException.class));
        verify(response).sendRedirect("error.jsp");
    }
}
