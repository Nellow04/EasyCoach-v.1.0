package sottosistemi.Prenotazione.service;

import model.beans.Sessione;
import model.beans.Timeslot;
import model.dao.SessioneDAO;
import model.dao.TimeslotDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("Test Cases for SessionRetrievalService")
class SessionRetrievalServiceTest {

    @Mock
    private SessioneDAO sessioneDAO;

    @Mock
    private TimeslotDAO timeslotDAO;

    @InjectMocks
    private SessionRetrievalService sessionRetrievalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_1.1: Trova sessione per ID valido")
    void testFindSessionById_Valido() throws SQLException {
        int sessioneId = 1;
        Sessione sessioneMock = new Sessione();
        sessioneMock.setIdSessione(sessioneId);

        when(sessioneDAO.doFindById(sessioneId)).thenReturn(sessioneMock);

        Sessione result = sessionRetrievalService.findSessionById(sessioneId);

        assertNotNull(result);
        assertEquals(sessioneId, result.getIdSessione());
    }

    @Test
    @DisplayName("TC_1.2: Sessione non trovata")
    void testFindSessionById_NonTrovata() throws SQLException {
        int sessioneId = 999;

        when(sessioneDAO.doFindById(sessioneId)).thenReturn(null);

        Sessione result = sessionRetrievalService.findSessionById(sessioneId);

        assertNull(result);
    }

    @Test
    @DisplayName("TC_1.3: ID sessione negativo")
    void testFindSessionById_IdNegativo() throws SQLException {
        int sessioneId = -1;

        Sessione result = sessionRetrievalService.findSessionById(sessioneId);

        assertNull(result);
    }

    @Test
    @DisplayName("TC_1.4: Eccezione SQL in findSessionById")
    void testFindSessionById_SQLException() throws SQLException {
        int sessioneId = 1;

        when(sessioneDAO.doFindById(sessioneId)).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> sessionRetrievalService.findSessionById(sessioneId));
    }

    @Test
    @DisplayName("TC_2.1: Trova tutte le sessioni")
    void testFindAllSessions_Presenti() throws SQLException {
        List<Sessione> sessioniMock = List.of(new Sessione(), new Sessione());

        when(sessioneDAO.doFindAll()).thenReturn(sessioniMock);

        List<Sessione> result = sessionRetrievalService.findAllSessions();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("TC_2.2: Nessuna sessione trovata")
    void testFindAllSessions_Nessuna() throws SQLException {
        when(sessioneDAO.doFindAll()).thenReturn(Collections.emptyList());

        List<Sessione> result = sessionRetrievalService.findAllSessions();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC_2.3: Eccezione SQL in findAllSessions")
    void testFindAllSessions_SQLException() throws SQLException {
        when(sessioneDAO.doFindAll()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> sessionRetrievalService.findAllSessions());
    }

    @Test
    @DisplayName("TC_3.1: Trova timeslot per sessione valida")
    void testFindTimeslotsBySessionId_Valida() throws SQLException {
        int sessioneId = 1;
        List<Timeslot> timeslotsMock = List.of(new Timeslot(), new Timeslot());

        when(timeslotDAO.findBySessionId(sessioneId)).thenReturn(timeslotsMock);

        List<Timeslot> result = sessionRetrievalService.findTimeslotsBySessionId(sessioneId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("TC_3.2: Nessun timeslot per sessione")
    void testFindTimeslotsBySessionId_Nessuno() throws SQLException {
        int sessioneId = 1;

        when(timeslotDAO.findBySessionId(sessioneId)).thenReturn(Collections.emptyList());

        List<Timeslot> result = sessionRetrievalService.findTimeslotsBySessionId(sessioneId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC_3.3: ID sessione inesistente")
    void testFindTimeslotsBySessionId_Inesistente() throws SQLException {
        int sessioneId = 999;

        when(timeslotDAO.findBySessionId(sessioneId)).thenReturn(Collections.emptyList());

        List<Timeslot> result = sessionRetrievalService.findTimeslotsBySessionId(sessioneId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC_3.4: ID sessione negativo")
    void testFindTimeslotsBySessionId_IdNegativo() throws SQLException {
        int sessioneId = -1;

        List<Timeslot> result = sessionRetrievalService.findTimeslotsBySessionId(sessioneId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC_3.5: Eccezione SQL in findTimeslotsBySessionId")
    void testFindTimeslotsBySessionId_SQLException() throws SQLException {
        int sessioneId = 1;

        when(timeslotDAO.findBySessionId(sessioneId)).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> sessionRetrievalService.findTimeslotsBySessionId(sessioneId));
    }

    @Test
    @DisplayName("TC_4.1: Sessioni correlate trovate")
    void testFindCorrelatedSessions_Trovate() throws SQLException {
        int currentSessionId = 1;
        List<Sessione> allSessions = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Sessione sessione = new Sessione();
            sessione.setIdSessione(i);
            sessione.setStatusSessione(i % 2 == 0 ? "ATTIVA" : "INATTIVA");
            allSessions.add(sessione);
        }

        when(sessioneDAO.doFindAll()).thenReturn(allSessions);

        List<Sessione> result = sessionRetrievalService.findCorrelatedSessions(currentSessionId);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(s -> s.getStatusSessione().equalsIgnoreCase("ATTIVA")));
    }

    @Test
    @DisplayName("TC_4.2: Nessuna sessione correlata")
    void testFindCorrelatedSessions_Nessuna() throws SQLException {
        int currentSessionId = 1;
        List<Sessione> allSessions = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Sessione sessione = new Sessione();
            sessione.setIdSessione(i);
            sessione.setStatusSessione("INATTIVA");
            allSessions.add(sessione);
        }

        when(sessioneDAO.doFindAll()).thenReturn(allSessions);

        List<Sessione> result = sessionRetrievalService.findCorrelatedSessions(currentSessionId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC_4.3: Eccezione SQL in findCorrelatedSessions")
    void testFindCorrelatedSessions_SQLException() throws SQLException {
        int currentSessionId = 1;

        when(sessioneDAO.doFindAll()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> sessionRetrievalService.findCorrelatedSessions(currentSessionId));
    }

    @Test
    @DisplayName("TC_5.1: Sessioni trovate con query valida")
    void testFindSessionsByTitleLike_Trovate() throws SQLException {
        String query = "test";
        List<Sessione> sessioniMock = List.of(new Sessione(), new Sessione());

        when(sessioneDAO.findByTitleLike(query)).thenReturn(sessioniMock);

        List<Sessione> result = sessionRetrievalService.findSessionsByTitleLike(query);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("TC_5.2: Nessuna sessione trovata con query valida")
    void testFindSessionsByTitleLike_Nessuna() throws SQLException {
        String query = "test";

        when(sessioneDAO.findByTitleLike(query)).thenReturn(Collections.emptyList());

        List<Sessione> result = sessionRetrievalService.findSessionsByTitleLike(query);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC_5.3: Query nulla o vuota")
    void testFindSessionsByTitleLike_QueryNulla() throws SQLException {
        String query = null;

        List<Sessione> result = sessionRetrievalService.findSessionsByTitleLike(query);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC_5.4: Eccezione SQL in findSessionsByTitleLike")
    void testFindSessionsByTitleLike_SQLException() throws SQLException {
        String query = "test";

        when(sessioneDAO.findByTitleLike(query)).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> sessionRetrievalService.findSessionsByTitleLike(query));
    }
}