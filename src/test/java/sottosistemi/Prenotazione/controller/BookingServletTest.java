package sottosistemi.Prenotazione.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.beans.Prenotazione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.Prenotazione.service.BookingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("BookingServlet Tests")
class BookingServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private BookingService bookingService;

    @Mock
    private PrintWriter writer;

    private BookingServlet bookingServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingServlet = new BookingServlet(bookingService);
    }

    @Test
    @DisplayName("TC_1.1: Blocco per ruolo MENTOR")
    void testDoPost_MentorRoleBlocked() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(response.getWriter()).thenReturn(writer);

        bookingServlet.doPost(request, response);

        String expectedJson = "{\"error\":\"I mentor non possono effettuare prenotazioni\"}";
        verify(writer).print(expectedJson);
    }

    @Test
    @DisplayName("TC_1.2: Parametro action assente")
    void testDoPost_MissingAction() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("UTENTE");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));
        when(response.getWriter()).thenReturn(writer);

        bookingServlet.doPost(request, response);

        String expectedJson = "{\"error\":\"Azione non specificata\"}";
        verify(writer).print(expectedJson);
    }

    @Test
    @DisplayName("TC_1.3: checkAvailability con disponibilità positiva")
    void testDoPost_CheckAvailabilityAvailable() throws Exception {
        Map<String, Object> availability = new HashMap<>();
        availability.put("disponibile", true);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("UTENTE");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("action=checkAvailability&timeslotId=1&date=2025-01-01")));
        when(response.getWriter()).thenReturn(writer);
        when(bookingService.checkAvailability(any())).thenReturn(availability);

        bookingServlet.doPost(request, response);

        String expectedJson = "{\"disponibile\":true}";
        verify(writer).print(expectedJson);
    }

    @Test
    @DisplayName("TC_1.4: booking con subAction create")
    void testDoPost_BookingCreate() throws Exception {
        Prenotazione mockPrenotazione = new Prenotazione();
        mockPrenotazione.setIdPrenotazione(1);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("UTENTE");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("action=booking&subAction=create&timeslotId=1&dataPrenotazione=2025-01-01&idSessione=1&idUtente=1")));
        when(response.getWriter()).thenReturn(writer);
        when(bookingService.createBooking(any())).thenReturn(mockPrenotazione);

        bookingServlet.doPost(request, response);

        String expectedJson = "{\"success\":true,\"message\":\"Prenotazione creata con successo\",\"idPrenotazione\":1}";
        verify(writer).print(expectedJson);
    }

    @Test
    @DisplayName("TC_1.5: Errore generico del server")
    void testDoPost_GenericServerError() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("UTENTE");
        when(request.getReader()).thenThrow(new IOException("Errore generico"));
        when(response.getWriter()).thenReturn(writer);

        bookingServlet.doPost(request, response);

        String expectedJson = "{\"error\":\"Errore del server: Errore generico\"}";
        verify(writer).print(expectedJson);
    }

    @Test
    @DisplayName("TC_1.6: paramsAttr non è un'istanza di Map")
    void testDoPost_ParamsAttrNotMap() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("UTENTE");
        when(request.getAttribute("params")).thenReturn("NonUnaMappa"); // Simula un valore non valido
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));
        when(response.getWriter()).thenReturn(writer);

        bookingServlet.doPost(request, response);

        // Dato che non viene fornita un'azione valida, ci aspettiamo un errore specifico
        String expectedJson = "{\"error\":\"Azione non specificata\"}";
        verify(writer).print(expectedJson);
    }


    @Test
    @DisplayName("TC_1.7: checkAvailability con errore")
    void testDoPost_CheckAvailabilityError() throws Exception {
        Map<String, Object> availability = new HashMap<>();
        availability.put("error", "Errore durante il controllo disponibilità");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("UTENTE");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("action=checkAvailability&timeslotId=1&date=2025-01-01")));
        when(response.getWriter()).thenReturn(writer);
        when(bookingService.checkAvailability(any())).thenReturn(availability);

        bookingServlet.doPost(request, response);

        String expectedJson = "{\"error\":\"Errore durante il controllo disponibilità\"}";
        verify(writer).print(expectedJson);
    }

    @Test
    @DisplayName("TC_1.8: availability senza status")
    void testDoPost_CheckAvailabilityWithoutStatus() throws Exception {
        Map<String, Object> availability = new HashMap<>();
        availability.put("disponibile", true);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("UTENTE");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("action=checkAvailability&timeslotId=1&date=2025-01-01")));
        when(response.getWriter()).thenReturn(writer);
        when(bookingService.checkAvailability(any())).thenReturn(availability);

        bookingServlet.doPost(request, response);

        String expectedJson = "{\"disponibile\":true}";
        verify(writer).print(expectedJson);
    }

    @Test
    @DisplayName("TC_1.9: subAction non valida in booking")
    void testDoPost_BookingInvalidSubAction() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("UTENTE");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("action=booking&subAction=invalid")));
        when(response.getWriter()).thenReturn(writer);

        bookingServlet.doPost(request, response);

        String expectedJson = "{\"success\":false,\"error\":\"Sotto-azione booking non valida. Usa create/confirm\"}";
        verify(writer).print(expectedJson);
    }

    @Test
    @DisplayName("TC_1.10: confirm con Prenotazione non trovata")
    void testDoPost_ConfirmBookingNotFound() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("UTENTE");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("action=confirm&idPrenotazione=1")));
        when(response.getWriter()).thenReturn(writer);
        when(bookingService.confirmBooking(any())).thenReturn(null);

        bookingServlet.doPost(request, response);

        String expectedJson = "{\"success\":false,\"error\":\"Prenotazione non trovata\"}";
        verify(writer).print(expectedJson);
    }

    @Test
    @DisplayName("TC_1.11: Eccezione durante confirm")
    void testDoPost_ConfirmBookingException() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("UTENTE");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("action=confirm&idPrenotazione=1")));
        when(response.getWriter()).thenReturn(writer);
        when(bookingService.confirmBooking(any())).thenThrow(new RuntimeException("Errore conferma"));

        bookingServlet.doPost(request, response);

        String expectedJson = "{\"success\":false,\"error\":\"Errore conferma\"}";
        verify(writer).print(expectedJson);
    }



}