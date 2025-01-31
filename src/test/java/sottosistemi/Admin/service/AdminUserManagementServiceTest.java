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

@DisplayName("AdminUserManagementService Tests")
class AdminUserManagementServiceTest {

    @Mock
    private UtenteDAO utenteDAO;

    @Mock
    private SessioneDAO sessioneDAO;

    private AdminUserManagementService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AdminUserManagementService(utenteDAO, sessioneDAO);
    }

    @Test
    @DisplayName("TC_1.1: Nessun utente nel database")
    void testGetAllUsersSimplified_NoUsers() throws Exception {
        when(utenteDAO.doFindAll()).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = service.getAllUsersSimplified();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "La lista deve essere vuota");
    }

    @Test
    @DisplayName("TC_1.2: Un solo utente con attributi completi")
    void testGetAllUsersSimplified_SingleUser() throws Exception {
        // Crea un utente usando il costruttore vuoto e i setter
        Utente user = new Utente();
        user.setIdUtente(1);
        user.setNome("Mario");
        user.setCognome("Rossi");
        user.setEmail("mario.rossi@example.com");
        user.setRuolo("ADMIN");

        List<Utente> users = List.of(user);
        when(utenteDAO.doFindAll()).thenReturn(users);

        List<Map<String, Object>> result = service.getAllUsersSimplified();

        assertNotNull(result);
        assertEquals(1, result.size(), "La lista deve contenere un utente");
        Map<String, Object> simplifiedUser = result.get(0);
        assertEquals(1, simplifiedUser.get("id"));
        assertEquals("Mario Rossi", simplifiedUser.get("nome"));
        assertEquals("mario.rossi@example.com", simplifiedUser.get("email"));
        assertEquals("ADMIN", simplifiedUser.get("ruolo"));
    }


    @Test
    @DisplayName("TC_1.3: Più utenti con attributi completi")
    void testGetAllUsersSimplified_MultipleUsers() throws Exception {
        // Crea utenti usando il costruttore vuoto e i setter
        Utente user1 = new Utente();
        user1.setIdUtente(1);
        user1.setNome("Mario");
        user1.setCognome("Rossi");
        user1.setEmail("mario.rossi@example.com");
        user1.setRuolo("ADMIN");

        Utente user2 = new Utente();
        user2.setIdUtente(2);
        user2.setNome("Luigi");
        user2.setCognome("Verdi");
        user2.setEmail("luigi.verdi@example.com");
        user2.setRuolo("MENTOR");

        List<Utente> users = List.of(user1, user2);
        when(utenteDAO.doFindAll()).thenReturn(users);

        List<Map<String, Object>> result = service.getAllUsersSimplified();

        assertNotNull(result);
        assertEquals(2, result.size(), "La lista deve contenere due utenti");
    }


    @Test
    @DisplayName("TC_1.4: Eccezione durante l'accesso al database")
    void testGetAllUsersSimplified_Exception() throws Exception {
        when(utenteDAO.doFindAll()).thenThrow(new RuntimeException("Errore di connessione"));

        Exception exception = assertThrows(Exception.class, service::getAllUsersSimplified);
        assertEquals("Errore di connessione", exception.getMessage());
    }

    @Test
    @DisplayName("TC_2.1: Eliminazione utente senza sessioni")
    void testDeleteUser_NoSessions() throws Exception {
        when(sessioneDAO.doFindAll()).thenReturn(new ArrayList<>());

        service.deleteUser(1);

        verify(sessioneDAO, never()).archiveSession(anyInt(), anyBoolean());
        verify(utenteDAO).doDelete(1);
    }

    @Test
    @DisplayName("TC_2.2: Eliminazione utente con sessioni")
    void testDeleteUser_WithSessions() throws Exception {
        // Crea sessioni usando il costruttore vuoto e i setter
        Sessione session1 = new Sessione();
        session1.setIdSessione(1);
        session1.setTitolo("Sessione 1");
        session1.setIdUtente(1);

        Sessione session2 = new Sessione();
        session2.setIdSessione(2);
        session2.setTitolo("Sessione 2");
        session2.setIdUtente(1);

        List<Sessione> sessions = List.of(session1, session2);
        when(sessioneDAO.doFindAll()).thenReturn(sessions);

        service.deleteUser(1);

        verify(sessioneDAO, times(2)).archiveSession(anyInt(), eq(true));
        verify(utenteDAO).doDelete(1);
    }


    @Test
    @DisplayName("TC_2.3: Eccezione durante l'eliminazione utente")
    void testDeleteUser_Exception() throws Exception {
        when(sessioneDAO.doFindAll()).thenThrow(new RuntimeException("Errore durante l'archiviazione"));

        Exception exception = assertThrows(Exception.class, () -> service.deleteUser(1));
        assertEquals("Errore durante l'archiviazione", exception.getMessage());
    }
}