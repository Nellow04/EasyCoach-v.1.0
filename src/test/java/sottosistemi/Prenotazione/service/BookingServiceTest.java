package sottosistemi.Prenotazione.service;

import model.beans.Prenotazione;
import model.dao.PrenotazioneDAO;
import model.dao.TimeslotDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for BookingService")
class BookingServiceTest {

    @Mock
    private PrenotazioneDAO prenotazioneDAO;

    @Mock
    private TimeslotDAO timeslotDAO;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingService(prenotazioneDAO, timeslotDAO);
    }

    @Test
    @DisplayName("TC_3.1: Parametri mancanti - checkAvailability")
    void testCheckAvailability_ParametersMissing() throws SQLException {
        Map<String, String> params = new HashMap<>(); // Nessun parametro fornito

        Map<String, Object> result = bookingService.checkAvailability(params);

        assertTrue(result.containsKey("error"));
        assertEquals("Parametri mancanti per checkAvailability", result.get("error"));
    }

    @Test
    @DisplayName("TC_3.2: Parametri con formato errato - checkAvailability")
    void testCheckAvailability_InvalidParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("timeslotId", "notAnInteger");
        params.put("date", "notADate");

        assertThrows(NumberFormatException.class, () -> bookingService.checkAvailability(params));
    }

    @Test
    @DisplayName("TC_3.3: Disponibilità positiva - checkAvailability")
    void testCheckAvailability_Available() throws SQLException {
        Map<String, String> params = new HashMap<>();
        params.put("timeslotId", "1");
        params.put("date", LocalDate.now().toString());

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("disponibile", true);
        when(prenotazioneDAO.checkTimeslotStatus(anyInt(), any(LocalDateTime.class))).thenReturn(mockResult);

        Map<String, Object> result = bookingService.checkAvailability(params);

        assertTrue((Boolean) result.get("disponibile"));
    }

    @Test
    @DisplayName("TC_3.4: Disponibilità negativa - checkAvailability")
    void testCheckAvailability_NotAvailable() throws SQLException {
        Map<String, String> params = new HashMap<>();
        params.put("timeslotId", "1");
        params.put("date", LocalDate.now().toString());

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("disponibile", false);
        when(prenotazioneDAO.checkTimeslotStatus(anyInt(), any(LocalDateTime.class))).thenReturn(mockResult);

        Map<String, Object> result = bookingService.checkAvailability(params);

        assertFalse((Boolean) result.get("disponibile"));
    }


    @Test
    @DisplayName("TC_4.1: Parametri mancanti - createBooking")
    void testCreateBooking_ParametersMissing() {
        Map<String, String> params = new HashMap<>();

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(params));
    }

    @Test
    @DisplayName("TC_4.2: Parametri con formato errato - createBooking")
    void testCreateBooking_InvalidParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("timeslotId", "notAnInteger");
        params.put("dataPrenotazione", "notADate");
        params.put("idSessione", "1");
        params.put("idUtente", "1");

        assertThrows(NumberFormatException.class, () -> bookingService.createBooking(params));
    }

    @Test
    @DisplayName("TC_4.3: Timeslot non disponibile - createBooking")
    void testCreateBooking_TimeslotNotAvailable() throws SQLException {
        Map<String, String> params = new HashMap<>();
        params.put("timeslotId", "1");
        params.put("dataPrenotazione", LocalDate.now().toString());
        params.put("idSessione", "1");
        params.put("idUtente", "1");

        when(prenotazioneDAO.isDisponibile(anyInt(), any(LocalDateTime.class))).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(params));
    }

    @Test
    @DisplayName("TC_4.4: Creazione riuscita - createBooking")
    void testCreateBooking_Success() throws SQLException {
        Map<String, String> params = new HashMap<>();
        params.put("timeslotId", "1");
        params.put("dataPrenotazione", LocalDate.now().toString());
        params.put("idSessione", "1");
        params.put("idUtente", "1");

        when(prenotazioneDAO.isDisponibile(anyInt(), any(LocalDateTime.class))).thenReturn(true);

        bookingService.createBooking(params);

        verify(prenotazioneDAO).doSave(any(Prenotazione.class));
    }


    @Test
    @DisplayName("TC_6.1: ID prenotazione mancante - confirmBooking")
    void testConfirmBooking_MissingId() {
        Map<String, String> params = new HashMap<>();

        assertThrows(IllegalArgumentException.class, () -> bookingService.confirmBooking(params));
    }

    @Test
    @DisplayName("TC_6.2: ID prenotazione con formato errato - confirmBooking")
    void testConfirmBooking_InvalidId() {
        Map<String, String> params = new HashMap<>();
        params.put("idPrenotazione", "notAnInteger");

        assertThrows(NumberFormatException.class, () -> bookingService.confirmBooking(params));
    }

    @Test
    @DisplayName("TC_6.3: Prenotazione non trovata - confirmBooking")
    void testConfirmBooking_NotFound() throws SQLException {
        Map<String, String> params = new HashMap<>();
        params.put("idPrenotazione", "1");

        when(prenotazioneDAO.doFindById(1)).thenReturn(null);

        assertNull(bookingService.confirmBooking(params));
    }

    @Test
    @DisplayName("TC_6.4: Conferma riuscita - confirmBooking")
    void testConfirmBooking_Success() throws SQLException {
        Map<String, String> params = new HashMap<>();
        params.put("idPrenotazione", "1");

        Prenotazione mockPrenotazione = new Prenotazione();
        when(prenotazioneDAO.doFindById(1)).thenReturn(mockPrenotazione);

        bookingService.confirmBooking(params);

        verify(prenotazioneDAO).doUpdate(mockPrenotazione);
        assertEquals("ATTIVA", mockPrenotazione.getStatusPrenotazione());
    }


    // Test per la branch coverage
    @Test
    @DisplayName("TC_4.1: Costruttore senza parametri - Verifica inizializzazione")
    void testDefaultConstructor() {
        BookingService service = new BookingService(); // Usa il costruttore senza parametri
        assertNotNull(service); // Verifica che il service non sia null
    }

    @Test
    @DisplayName("TC_3.5: Errore SQL - confirmBooking")
    void testConfirmBooking_SQLException() throws SQLException {
        Map<String, String> params = new HashMap<>();
        params.put("idPrenotazione", "1");

        when(prenotazioneDAO.doFindById(1)).thenThrow(new SQLException("Errore DB"));

        assertThrows(SQLException.class, () -> bookingService.confirmBooking(params));
    }

    @Test
    @DisplayName("TC_1.5: Errore SQL - checkAvailability")
    void testCheckAvailability_SQLException() throws SQLException {
        Map<String, String> params = new HashMap<>();
        params.put("timeslotId", "1");
        params.put("date", LocalDate.now().toString());

        when(prenotazioneDAO.checkTimeslotStatus(anyInt(), any(LocalDateTime.class))).thenThrow(new SQLException("Errore DB"));

        assertThrows(SQLException.class, () -> bookingService.checkAvailability(params));
    }

    @Test
    @DisplayName("TC_2.5: Errore SQL - createBooking")
    void testCreateBooking_SQLException() throws SQLException {
        Map<String, String> params = new HashMap<>();
        params.put("timeslotId", "1");
        params.put("dataPrenotazione", LocalDate.now().toString());
        params.put("idSessione", "1");
        params.put("idUtente", "1");

        when(prenotazioneDAO.isDisponibile(anyInt(), any(LocalDateTime.class))).thenThrow(new SQLException("Errore DB"));

        assertThrows(SQLException.class, () -> bookingService.createBooking(params));
    }

}