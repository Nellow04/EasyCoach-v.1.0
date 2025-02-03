package sottosistemi.Sessione.service;

import model.beans.Sessione;
import model.beans.Timeslot;
import model.dao.PrenotazioneDAO;
import model.dao.SessioneDAO;
import model.dao.TimeslotDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SessionManagementService Tests")
class SessionManagementServiceTest {

    private SessionManagementService service;

    @Mock
    private SessioneDAO sessioneDAO;

    @Mock
    private TimeslotDAO timeslotDAO;

    @Mock
    private PrenotazioneDAO prenotazioneDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SessionManagementService(sessioneDAO, timeslotDAO, prenotazioneDAO);
    }

    @Test
    @DisplayName("TC_7.1: Recupera timeslots con risultati")
    void testGetTimeslotsByMentorIdAsMap_WithResults() throws SQLException {
        Timeslot timeslot = new Timeslot();
        timeslot.setGiorno(1);
        timeslot.setOrario(10);
        when(timeslotDAO.findByMentorId(1)).thenReturn(List.of(timeslot));

        List<Map<String, Integer>> result = service.getTimeslotsByMentorIdAsMap(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).get("giorno"));
        assertEquals(10, result.get(0).get("orario"));
    }

    @Test
    @DisplayName("TC_7.2: Recupera timeslots senza risultati")
    void testGetTimeslotsByMentorIdAsMap_NoResults() throws SQLException {
        when(timeslotDAO.findByMentorId(1)).thenReturn(List.of());

        List<Map<String, Integer>> result = service.getTimeslotsByMentorIdAsMap(1);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC_8.1: Recupera timeslots tramite session ID con risultati")
    void testFindTimeslotsBySessionId_WithResults() throws SQLException {
        Timeslot timeslot = new Timeslot();
        timeslot.setIdSessione(1);
        timeslot.setGiorno(1);
        timeslot.setOrario(10);
        when(timeslotDAO.findBySessionId(1)).thenReturn(List.of(timeslot));

        List<Timeslot> result = service.findTimeslotsBySessionId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getGiorno());
        assertEquals(10, result.get(0).getOrario());
    }

    @Test
    @DisplayName("TC_8.2: Recupera timeslots tramite session ID senza risultati")
    void testFindTimeslotsBySessionId_NoResults() throws SQLException {
        when(timeslotDAO.findBySessionId(1)).thenReturn(List.of());

        List<Timeslot> result = service.findTimeslotsBySessionId(1);

        assertTrue(result.isEmpty());
    }



    @Test
    @DisplayName("TC_9.1: Recupera sessione tramite ID con successo")
    void testFindSessionById_Success() throws SQLException {
        Sessione session = new Sessione();
        session.setIdSessione(1);
        when(sessioneDAO.doFindById(1)).thenReturn(session);

        Sessione result = service.findSessionById(1);

        assertNotNull(result);
        assertEquals(1, result.getIdSessione());
    }

    @Test
    @DisplayName("TC_9.2: Recupera sessione tramite ID senza risultati")
    void testFindSessionById_NoResults() throws SQLException {
        when(sessioneDAO.doFindById(1)).thenReturn(null);

        Sessione result = service.findSessionById(1);

        assertNull(result);
    }

    @Test
    @DisplayName("TC_10.1: Verifica prenotazioni attive (true)")
    void testHasActiveBookings_True() throws SQLException {
        when(prenotazioneDAO.hasActiveBookings(1)).thenReturn(true);

        assertTrue(service.hasActiveBookings(1));
        verify(prenotazioneDAO).hasActiveBookings(1);
    }

    @Test
    @DisplayName("TC_10.2: Verifica prenotazioni attive (false)")
    void testHasActiveBookings_False() throws SQLException {
        when(prenotazioneDAO.hasActiveBookings(1)).thenReturn(false);

        assertFalse(service.hasActiveBookings(1));
        verify(prenotazioneDAO).hasActiveBookings(1);
    }

    @Test
    @DisplayName("TC_11.1: Archivia sessione con successo")
    void testArchiveSession_Success() throws SQLException {
        Sessione session = new Sessione();
        session.setIdSessione(1);
        session.setStatusSessione("ATTIVA");

        service.archiveSession(session);

        assertEquals("ARCHIVIATA", session.getStatusSessione());
        verify(sessioneDAO).doUpdate(session);
        verify(timeslotDAO).doDeleteBySessione(1);
    }


    // Test per ridondanti nel TCS ma utili per garantire la branch coverage
    @Test
    @DisplayName("TC_7.3: Crea sessione con days e hour corretti")
    void testCreateSession() throws SQLException {
        Sessione session = new Sessione();
        session.setIdUtente(1);
        session.setTitolo("Sessione Test");
        when(sessioneDAO.doSave(session)).thenReturn(1);

        String[] days = {"1", "2"};
        String[] hours = {"10", "15"};

        int sessionId = service.createSession(session, days, hours);

        assertEquals(1, sessionId);
        verify(sessioneDAO).doSave(session);
        verify(timeslotDAO, times(2)).doSave(any(Timeslot.class));
    }

    @Test
    @DisplayName("TC_7.4: Crea sessione con days e hours null")
    void testCreateSession_WithNullDaysAndHours() throws SQLException {
        Sessione session = new Sessione();
        session.setIdUtente(1);
        session.setTitolo("Sessione Test");
        when(sessioneDAO.doSave(session)).thenReturn(1);

        int sessionId = service.createSession(session, null, null);

        assertEquals(1, sessionId);
        verify(sessioneDAO).doSave(session);
        verifyNoInteractions(timeslotDAO);
    }

    @Test
    @DisplayName("TC_7.9: Aggiorna sessione con nuovi days e hours")
    void testUpdateSession_WithValidDaysAndHours() throws SQLException {
        Sessione session = new Sessione();
        session.setIdSessione(1);
        session.setTitolo("Sessione Aggiornata");

        String[] days = {"1", "2"};
        String[] hours = {"10", "15"};

        service.updateSession(session, days, hours);

        verify(sessioneDAO).doUpdate(session);
        verify(timeslotDAO).doDeleteBySessione(1);
        verify(timeslotDAO, times(2)).doSave(any(Timeslot.class));
    }

    @Test
    @DisplayName("TC_7.10: Aggiorna sessione con days e hours null")
    void testUpdateSession_WithNullDaysAndHours() throws SQLException {
        Sessione session = new Sessione();
        session.setIdSessione(1);
        session.setTitolo("Sessione Aggiornata");

        service.updateSession(session, null, null);

        verify(sessioneDAO).doUpdate(session);
        verify(timeslotDAO).doDeleteBySessione(1);
        verifyNoMoreInteractions(timeslotDAO);
    }

}