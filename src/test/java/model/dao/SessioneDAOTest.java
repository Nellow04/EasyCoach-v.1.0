package model.dao;

import model.beans.Sessione;
import model.connection.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for SessioneDAO")
class SessioneDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private SessioneDAO sessioneDAO;
    private MockedStatic<DBConnection> mockedDBConnection;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        sessioneDAO = new SessioneDAO();

        mockedDBConnection = mockStatic(DBConnection.class);
        when(DBConnection.getConnection()).thenReturn(mockConnection);

        // Mock per prepareStatement con RETURN_GENERATED_KEYS
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);

        // Mock per prepareStatement senza RETURN_GENERATED_KEYS
        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockPreparedStatement);
    }


    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    @DisplayName("TC_1: Test salvataggio sessione")
    void testDoSave() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(10);

        Sessione sessione = new Sessione();
        sessione.setIdUtente(1);
        sessione.setTitolo("Test Title");
        sessione.setDescrizione("Descrizione");
        sessione.setPrezzo(100.0);
        sessione.setImmagine("img.png");
        sessione.setStatusSessione("ATTIVA");

        int id = sessioneDAO.doSave(sessione);
        assertEquals(10, id);
    }

    @Test
    @DisplayName("TC_2: Test ricerca sessione per ID")
    void testDoFindById() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("idSessione")).thenReturn(1);
        when(mockResultSet.getString("titolo")).thenReturn("Test Title");

        Sessione sessione = sessioneDAO.doFindById(1);
        assertNotNull(sessione);
        assertEquals(1, sessione.getIdSessione());
    }

    @Test
    @DisplayName("TC_3: Test aggiornamento sessione")
    void testDoUpdate() throws SQLException {
        Sessione sessione = new Sessione();
        sessione.setIdSessione(1);
        sessione.setTitolo("Updated Title");

        sessioneDAO.doUpdate(sessione);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("TC_4: Test recupero tutte le sessioni")
    void testDoFindAll() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);

        List<Sessione> sessioni = sessioneDAO.doFindAll();
        assertEquals(1, sessioni.size());
    }

    @Test
    @DisplayName("TC_5: Test ricerca sessione per utente")
    void testFindByUserId() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);

        List<Sessione> sessioni = sessioneDAO.findByUserId(1);
        assertEquals(1, sessioni.size());
    }

    @Test
    @DisplayName("TC_6: Test ricerca sessione per titolo")
    void testFindByTitleLike() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);

        List<Sessione> sessioni = sessioneDAO.findByTitleLike("Test");
        assertEquals(1, sessioni.size());
    }

    @Test
    @DisplayName("TC_7: Test archiviazione sessione")
    void testArchiveSession() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        sessioneDAO.archiveSession(1, true);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
    @Test
    @DisplayName("TC_1: Test salvataggio sessione con generazione ID fallita")
    void testDoSaveFailingKeyGeneration() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Sessione sessione = new Sessione();
        assertThrows(SQLException.class, () -> sessioneDAO.doSave(sessione));
    }

    @Test
    @DisplayName("TC_2: Test ricerca sessione per ID inesistente")
    void testDoFindByIdNotFound() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Sessione sessione = sessioneDAO.doFindById(99);
        assertNull(sessione);
    }

    @Test
    @DisplayName("TC_3: Test ricerca di tre sessioni casuali con DB vuoto")
    void testGetTreSessioniCasualiEmpty() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<Sessione> sessioni = sessioneDAO.getTreSessioniCasuali();
        assertTrue(sessioni.isEmpty());
    }

    @Test
    @DisplayName("TC_4: Test archiviazione sessione con utente non admin e prenotazioni attive")
    void testArchiveSessionWithBookings() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        assertThrows(SQLException.class, () -> sessioneDAO.archiveSession(1, false));
    }

    @Test
    @DisplayName("TC_5: Test archiviazione sessione inesistente")
    void testArchiveSessionNotFound() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertThrows(SQLException.class, () -> sessioneDAO.archiveSession(99, true));
    }
}
