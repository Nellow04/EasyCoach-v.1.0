package sottosistemi.Prenotazione.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.beans.Sessione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.Prenotazione.service.SessionRetrievalService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Test Cases for SearchSessionServlet")
class SearchSessionServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SessionRetrievalService sessionRetrievalService;

    @InjectMocks
    private SearchSessionServlet searchSessionServlet;

    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_1.1: Parametro q nullo")
    void testDoGet_QueryNulla() throws Exception {
        when(request.getParameter("q")).thenReturn(null);
        when(sessionRetrievalService.findSessionsByTitleLike(""))
                .thenReturn(Collections.emptyList());

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        searchSessionServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        String jsonResponse = stringWriter.toString();
        assertEquals("[]", jsonResponse);
    }

    @Test
    @DisplayName("TC_1.2: Parametro q vuoto")
    void testDoGet_QueryVuota() throws Exception {
        when(request.getParameter("q")).thenReturn("");
        when(sessionRetrievalService.findSessionsByTitleLike(""))
                .thenReturn(Collections.emptyList());

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        searchSessionServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        String jsonResponse = stringWriter.toString();
        assertEquals("[]", jsonResponse);
    }

    @Test
    @DisplayName("TC_1.3: Parametro q valido")
    void testDoGet_QueryValida() throws Exception {
        when(request.getParameter("q")).thenReturn("test");
        Sessione session1 = new Sessione();
        session1.setIdSessione(1);
        session1.setTitolo("Test Session");
        session1.setStatusSessione("ATTIVA");

        Sessione session2 = new Sessione();
        session2.setIdSessione(2);
        session2.setTitolo("Another Test");
        session2.setStatusSessione("ATTIVA");

        List<Sessione> mockSessions = List.of(session1, session2);
        when(sessionRetrievalService.findSessionsByTitleLike("test"))
                .thenReturn(mockSessions);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        searchSessionServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        String jsonResponse = stringWriter.toString();

        String expectedJson = gson.toJson(mockSessions);
        assertEquals(expectedJson, jsonResponse);
    }

    @Test
    @DisplayName("TC_2.1: Lista di sessioni vuota")
    void testDoGet_ListaVuota() throws Exception {
        when(request.getParameter("q")).thenReturn("irrelevant");
        when(sessionRetrievalService.findSessionsByTitleLike("irrelevant"))
                .thenReturn(Collections.emptyList());

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        searchSessionServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        String jsonResponse = stringWriter.toString();
        assertEquals("[]", jsonResponse);
    }

    @Test
    @DisplayName("TC_3.1: Eccezione SQLException")
    void testDoGet_EccezioneSQL() throws Exception {
        when(request.getParameter("q")).thenReturn("test");
        when(sessionRetrievalService.findSessionsByTitleLike("test"))
                .thenThrow(new SQLException("Errore nel database"));

        Exception thrown = assertThrows(Exception.class, () -> {
            searchSessionServlet.doGet(request, response);
        });

        assertTrue(thrown.getMessage().contains("DB error searching sessions"));
    }
}
