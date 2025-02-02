package model.dao;

import model.beans.Utente;
import model.connection.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for UtenteDAO")
class UtenteDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private UtenteDAO utenteDAO;
    private MockedStatic<DBConnection> mockedDBConnection;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        utenteDAO = new UtenteDAO();

        // Reset del mock statico prima di ogni test
        mockedDBConnection = mockStatic(DBConnection.class);
        when(DBConnection.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    @AfterEach
    void tearDown() {
        // Chiusura del mock statico dopo ogni test
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    @DisplayName("TC_1: Test salvataggio utente")
    void testDoSave() throws SQLException {
        Utente utente = new Utente();
        utente.setIdUtente(1);
        utente.setEmail("test@mail.com");
        utente.setNome("Mario");
        utente.setCognome("Rossi");
        utente.setPassword("password");
        utente.setRuolo("user");

        utenteDAO.doSave(utente);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("TC_2: Test ricerca utente per ID")
    void testDoFindById() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("idUtente")).thenReturn(1);
        when(mockResultSet.getString("email")).thenReturn("test@mail.com");
        when(mockResultSet.getString("nome")).thenReturn("Mario");
        when(mockResultSet.getString("cognome")).thenReturn("Rossi");
        when(mockResultSet.getString("password")).thenReturn("password");
        when(mockResultSet.getString("ruolo")).thenReturn("user");

        Utente utente = utenteDAO.doFindById(1);
        assertNotNull(utente);
        assertEquals(1, utente.getIdUtente());
        assertEquals("test@mail.com", utente.getEmail());
    }

    @Test
    @DisplayName("TC_3: Test aggiornamento utente")
    void testDoUpdate() throws SQLException {
        Utente utente = new Utente();
        utente.setIdUtente(1);
        utente.setEmail("test@mail.com");
        utente.setNome("Mario");
        utente.setCognome("Rossi");
        utente.setPassword("newpassword");
        utente.setRuolo("admin");

        utenteDAO.doUpdate(utente);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("TC_4: Test eliminazione utente")
    void testDoDelete() throws SQLException {
        utenteDAO.doDelete(1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("TC_5: Test recupero tutti gli utenti")
    void testDoFindAll() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("idUtente")).thenReturn(1);
        when(mockResultSet.getString("email")).thenReturn("test@mail.com");
        when(mockResultSet.getString("nome")).thenReturn("Mario");
        when(mockResultSet.getString("cognome")).thenReturn("Rossi");
        when(mockResultSet.getString("password")).thenReturn("password");
        when(mockResultSet.getString("ruolo")).thenReturn("user");

        List<Utente> utenti = utenteDAO.doFindAll();
        assertEquals(1, utenti.size());
    }

    @Test
    @DisplayName("TC_6: Test ricerca utente per email")
    void testDoRetrieveByEmail() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("idUtente")).thenReturn(1);
        when(mockResultSet.getString("email")).thenReturn("test@mail.com");
        when(mockResultSet.getString("nome")).thenReturn("Mario");
        when(mockResultSet.getString("cognome")).thenReturn("Rossi");
        when(mockResultSet.getString("password")).thenReturn("password");
        when(mockResultSet.getString("ruolo")).thenReturn("user");

        Utente utente = utenteDAO.doRetrieveByEmail("test@mail.com");
        assertNotNull(utente);
        assertEquals("test@mail.com", utente.getEmail());
    }

    @Test
    @DisplayName("TC_7: Test aggiornamento password utente")
    void testDoUpdatePassword() throws SQLException {
        Utente utente = new Utente();
        utente.setIdUtente(1);
        utente.setPassword("newpassword");

        // Simuliamo che la query per controllare l'esistenza dell'utente trovi un risultato
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Simula che l'utente esista

        // Simuliamo l'aggiornamento della password
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        utenteDAO.doUpdatePassword(utente);

        // Verifica che l'update sia stato eseguito
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }


    @Test
    @DisplayName("TC_8: Test recupero tutti i mentor")
    void testGetAllMentors() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("idUtente")).thenReturn(1);
        when(mockResultSet.getString("email")).thenReturn("mentor@mail.com");
        when(mockResultSet.getString("nome")).thenReturn("Luca");
        when(mockResultSet.getString("cognome")).thenReturn("Bianchi");
        when(mockResultSet.getString("password")).thenReturn("password");
        when(mockResultSet.getString("ruolo")).thenReturn("mentor");

        List<Utente> mentors = utenteDAO.getAllMentors();
        assertEquals(1, mentors.size());
        assertEquals("mentor@mail.com", mentors.get(0).getEmail());
    }

    @Test
    @DisplayName("TC_9: Test salvataggio utente con SQLException")
    void testDoSaveSQLException() throws SQLException {
        doThrow(new SQLException("DB error")).when(mockPreparedStatement).executeUpdate();
        Utente utente = new Utente();
        assertThrows(SQLException.class, () -> utenteDAO.doSave(utente));
    }

    @Test
    @DisplayName("TC_10: Test ricerca utente per ID inesistente")
    void testDoFindByIdNotFound() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        assertNull(utenteDAO.doFindById(99));
    }

    @Test
    @DisplayName("TC_11: Test aggiornamento utente con SQLException")
    void testDoUpdateSQLException() throws SQLException {
        doThrow(new SQLException("DB error")).when(mockPreparedStatement).executeUpdate();
        Utente utente = new Utente();
        assertThrows(SQLException.class, () -> utenteDAO.doUpdate(utente));
    }

    @Test
    @DisplayName("TC_12: Test eliminazione utente inesistente")
    void testDoDeleteNotExisting() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);
        utenteDAO.doDelete(99);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("TC_13: Test recupero utenti con database vuoto")
    void testDoFindAllEmpty() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        List<Utente> utenti = utenteDAO.doFindAll();
        assertTrue(utenti.isEmpty());
    }

    @Test
    @DisplayName("TC_14: Test ricerca utente per email inesistente")
    void testDoRetrieveByEmailNotFound() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        assertNull(utenteDAO.doRetrieveByEmail("notfound@mail.com"));
    }

    @Test
    @DisplayName("TC_15: Test aggiornamento password con utente inesistente")
    void testDoUpdatePasswordUserNotFound() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        Utente utente = new Utente();
        utente.setIdUtente(99);
        assertThrows(SQLException.class, () -> utenteDAO.doUpdatePassword(utente));
    }
}
