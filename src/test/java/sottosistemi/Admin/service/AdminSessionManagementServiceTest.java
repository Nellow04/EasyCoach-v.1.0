package sottosistemi.Admin.service;

import model.dao.SessioneDAO;
import model.dao.UtenteDAO;
import model.beans.Sessione;
import model.beans.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AdminSessionManagementService Tests")
class AdminSessionManagementServiceTest {

    @Mock
    private SessioneDAO sessioneDAO;

    @Mock
    private UtenteDAO utenteDAO;

    private AdminSessionManagementService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AdminSessionManagementService(sessioneDAO, utenteDAO);
    }

    @Test
    @DisplayName("TC_1.1: Nessuna sessione nel database")
    void testGetAllSessionsEnriched_NoSessions() throws Exception {
        when(sessioneDAO.doFindAll()).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = service.getAllSessionsEnriched();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "La lista deve essere vuota");
    }

    @Test
    @DisplayName("TC_1.2: Una sola sessione con mentor presente")
    void testGetAllSessionsEnriched_SingleSessionWithMentor() throws Exception {
        Sessione session = new Sessione();
        session.setIdSessione(1);
        session.setTitolo("Sessione Test");
        session.setIdUtente(1);

        Utente mentor = new Utente();
        mentor.setIdUtente(1);
        mentor.setNome("Mario");
        mentor.setCognome("Rossi");

        when(sessioneDAO.doFindAll()).thenReturn(List.of(session));
        when(utenteDAO.doFindById(1)).thenReturn(mentor);

        List<Map<String, Object>> result = service.getAllSessionsEnriched();

        assertNotNull(result);
        assertEquals(1, result.size(), "La lista deve contenere una sessione");
        Map<String, Object> enrichedSession = result.get(0);
        assertEquals("Mario Rossi", enrichedSession.get("mentorNome"));
    }

    @Test
    @DisplayName("TC_1.3: Una sola sessione con mentor assente")
    void testGetAllSessionsEnriched_SingleSessionWithoutMentor() throws Exception {
        Sessione session = new Sessione();
        session.setIdSessione(1);
        session.setTitolo("Sessione Test");
        session.setIdUtente(1);

        when(sessioneDAO.doFindAll()).thenReturn(List.of(session));
        when(utenteDAO.doFindById(1)).thenReturn(null);

        List<Map<String, Object>> result = service.getAllSessionsEnriched();

        assertNotNull(result);
        assertEquals(1, result.size(), "La lista deve contenere una sessione");
        Map<String, Object> enrichedSession = result.get(0);
        assertEquals("N/A", enrichedSession.get("mentorNome"));
    }

    @Test
    @DisplayName("TC_1.4: Eccezione durante il recupero del mentor")
    void testGetAllSessionsEnriched_ExceptionDuringMentorFetch() throws Exception {
        Sessione session = new Sessione();
        session.setIdSessione(1);
        session.setTitolo("Sessione Test");
        session.setIdUtente(1);

        when(sessioneDAO.doFindAll()).thenReturn(List.of(session));
        when(utenteDAO.doFindById(1)).thenThrow(new RuntimeException("Errore DB"));

        List<Map<String, Object>> result = service.getAllSessionsEnriched();

        assertNotNull(result);
        assertEquals(1, result.size(), "La lista deve contenere una sessione");
        Map<String, Object> enrichedSession = result.get(0);
        assertEquals("N/A", enrichedSession.get("mentorNome"));
    }

    @Test
    @DisplayName("TC_2.1: Archiviazione di una sessione esistente")
    void testDeleteSession_ValidSession() throws Exception {
        doNothing().when(sessioneDAO).archiveSession(1, true);

        service.deleteSession(1);

        verify(sessioneDAO).archiveSession(1, true);
    }

    @Test
    @DisplayName("TC_2.2: Eccezione durante l'archiviazione di una sessione")
    void testDeleteSession_ExceptionDuringArchive() throws Exception {
        doThrow(new RuntimeException("Errore DB"))
                .when(sessioneDAO).archiveSession(1, true);

        Exception exception = assertThrows(Exception.class, () -> service.deleteSession(1));
        assertEquals("Errore DB", exception.getMessage());
    }
}