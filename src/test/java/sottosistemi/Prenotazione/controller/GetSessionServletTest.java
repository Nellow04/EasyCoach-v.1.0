package sottosistemi.Prenotazione.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.beans.Sessione;
import model.beans.Timeslot;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@DisplayName("Test Cases for GetSessionServlet")
class GetSessionServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SessionRetrievalService sessionRetrievalService;

    @InjectMocks
    private GetSessionServlet getSessionServlet;

    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_1.1: Corpo della richiesta vuoto")
    void testDoPost_RequestBodyEmpty() throws Exception {
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new java.io.StringReader("")));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        getSessionServlet.doPost(request, response);

        JsonObject jsonResponse = gson.fromJson(stringWriter.toString(), JsonObject.class);
        assertTrue(jsonResponse.has("error"));
        assertEquals("ID sessione non valido (null o vuoto)", jsonResponse.get("error").getAsString());
    }

    @Test
    @DisplayName("TC_1.2: ID sessione non numerico")
    void testDoPost_SessioneIdNonNumerico() throws Exception {
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new java.io.StringReader("sessioneId=abc")));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        getSessionServlet.doPost(request, response);

        JsonObject jsonResponse = gson.fromJson(stringWriter.toString(), JsonObject.class);
        assertTrue(jsonResponse.has("error"));
        assertEquals("ID sessione non valido (non numerico)", jsonResponse.get("error").getAsString());
    }

    @Test
    @DisplayName("TC_1.3: Sessione non trovata")
    void testDoPost_SessioneNonTrovata() throws Exception {
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new java.io.StringReader("sessioneId=1")));
        when(sessionRetrievalService.findSessionById(1)).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        getSessionServlet.doPost(request, response);

        JsonObject jsonResponse = gson.fromJson(stringWriter.toString(), JsonObject.class);
        assertTrue(jsonResponse.has("error"));
        assertEquals("Sessione non trovata per ID: 1", jsonResponse.get("error").getAsString());
    }

    @Test
    @DisplayName("TC_1.4: Recupero dati con successo")
    void testDoPost_Success() throws Exception {
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new java.io.StringReader("sessioneId=1")));

        Sessione sessioneMock = new Sessione();
        sessioneMock.setIdSessione(1);
        sessioneMock.setTitolo("Mock Session");
        when(sessionRetrievalService.findSessionById(1)).thenReturn(sessioneMock);

        List<Sessione> sessioniCorrelate = new ArrayList<>();
        sessioniCorrelate.add(new Sessione());
        sessioniCorrelate.add(new Sessione());
        when(sessionRetrievalService.findCorrelatedSessions(1)).thenReturn(sessioniCorrelate);

        List<Timeslot> timeslots = new ArrayList<>();
        timeslots.add(new Timeslot());
        timeslots.add(new Timeslot());
        when(sessionRetrievalService.findTimeslotsBySessionId(1)).thenReturn(timeslots);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        getSessionServlet.doPost(request, response);

        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("Mock Session"));
        assertTrue(jsonResponse.contains("sessioniCorrelate"));
        assertTrue(jsonResponse.contains("timeslots"));
    }

    @Test
    @DisplayName("TC_1.5: Errore del database durante il recupero della sessione")
    void testDoPost_DatabaseError() throws Exception {
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new java.io.StringReader("sessioneId=1")));
        when(sessionRetrievalService.findSessionById(1)).thenThrow(new SQLException("Database error"));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        getSessionServlet.doPost(request, response);

        JsonObject jsonResponse = gson.fromJson(stringWriter.toString(), JsonObject.class);
        assertTrue(jsonResponse.has("error"));
        assertEquals("Errore del database: Database error", jsonResponse.get("error").getAsString());
    }

    @Test
    @DisplayName("TC_1.6: Errore interno del server")
    void testDoPost_InternalServerError() throws Exception {
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new java.io.StringReader("sessioneId=1")));
        when(sessionRetrievalService.findSessionById(1)).thenThrow(new RuntimeException("Unexpected error"));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        getSessionServlet.doPost(request, response);

        JsonObject jsonResponse = gson.fromJson(stringWriter.toString(), JsonObject.class);
        assertTrue(jsonResponse.has("error"));
        assertEquals("Errore interno del server: Unexpected error", jsonResponse.get("error").getAsString());
    }

}
