package model.dao;

import model.beans.Prenotazione;
import model.connection.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for PrenotazioneDAO")
class PrenotazioneDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private PrenotazioneDAO prenotazioneDAO;
    private MockedStatic<DBConnection> mockedDBConnection;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        prenotazioneDAO = new PrenotazioneDAO();

        mockedDBConnection = mockStatic(DBConnection.class);
        when(DBConnection.getConnection()).thenReturn(mockConnection);

        // ✅ Mock per prepareStatement con RETURN_GENERATED_KEYS
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);

        // ✅ Mock per prepareStatement normale
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
    @DisplayName("TC_1: Test salvataggio prenotazione")
    void testDoSave() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(10);

        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setIdUtente(1);
        prenotazione.setIdTimeslot(2);
        prenotazione.setIdSessione(3);
        prenotazione.setDataPrenotazione(LocalDateTime.now());
        prenotazione.setStatusPrenotazione("ATTIVA");

        prenotazioneDAO.doSave(prenotazione);
        assertEquals(10, prenotazione.getIdPrenotazione());
    }

    @Test
    @DisplayName("TC_2: Test ricerca prenotazione per ID")
    void testDoFindById() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("idPrenotazione")).thenReturn(1);
        when(mockResultSet.getInt("idUtente")).thenReturn(2);
        when(mockResultSet.getInt("idTimeslot")).thenReturn(3);
        when(mockResultSet.getInt("idSessione")).thenReturn(4);
        when(mockResultSet.getTimestamp("dataPrenotazione")).thenReturn(Timestamp.valueOf(LocalDateTime.now())); // ✅ Corretto valore
        when(mockResultSet.getString("statusPrenotazione")).thenReturn("ATTIVA");

        Prenotazione prenotazione = prenotazioneDAO.doFindById(1);

        assertNotNull(prenotazione);
        assertEquals(1, prenotazione.getIdPrenotazione());
        assertEquals("ATTIVA", prenotazione.getStatusPrenotazione());
    }


    @Test
    @DisplayName("TC_3: Test aggiornamento prenotazione")
    void testDoUpdate() throws SQLException {
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setIdPrenotazione(1);
        prenotazione.setIdUtente(2);
        prenotazione.setIdTimeslot(3);
        prenotazione.setIdSessione(4);
        prenotazione.setDataPrenotazione(LocalDateTime.now()); // ✅ Assicuriamoci che non sia null
        prenotazione.setStatusPrenotazione("CONCLUSA");
        prenotazione.setLinkVideoconferenza("http://meeting.com");

        // ✅ Mock per evitare RuntimeException
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        prenotazioneDAO.doUpdate(prenotazione);

        verify(mockPreparedStatement, times(1)).setInt(1, prenotazione.getIdUtente());
        verify(mockPreparedStatement, times(1)).setInt(2, prenotazione.getIdTimeslot());
        verify(mockPreparedStatement, times(1)).setInt(3, prenotazione.getIdSessione());
        verify(mockPreparedStatement, times(1)).setTimestamp(4, Timestamp.valueOf(prenotazione.getDataPrenotazione())); // ✅ Corretta conversione
        verify(mockPreparedStatement, times(1)).setString(5, prenotazione.getStatusPrenotazione());
        verify(mockPreparedStatement, times(1)).setString(6, prenotazione.getLinkVideoconferenza());
        verify(mockPreparedStatement, times(1)).setInt(7, prenotazione.getIdPrenotazione());

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }



    @Test
    @DisplayName("TC_4: Test verifica disponibilità timeslot")
    void testIsDisponibile() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(0);

        assertTrue(prenotazioneDAO.isDisponibile(1, LocalDateTime.now()));
    }

    @Test
    @DisplayName("TC_5: Test recupero tutte le prenotazioni")
    void testDoFindAll() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("idPrenotazione")).thenReturn(1);
        when(mockResultSet.getInt("idUtente")).thenReturn(2);
        when(mockResultSet.getInt("idTimeslot")).thenReturn(3);
        when(mockResultSet.getInt("idSessione")).thenReturn(4);
        when(mockResultSet.getTimestamp("dataPrenotazione")).thenReturn(Timestamp.valueOf(LocalDateTime.now())); // ✅ Prevenzione null
        when(mockResultSet.getString("statusPrenotazione")).thenReturn("ATTIVA");

        List<Prenotazione> prenotazioni = prenotazioneDAO.doFindAll();

        assertEquals(1, prenotazioni.size());
        assertEquals("ATTIVA", prenotazioni.get(0).getStatusPrenotazione());
    }


    @Test
    @DisplayName("TC_6: Test verifica prenotazioni attive per utente")
    void testHasActiveBookingsForUser() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        assertTrue(prenotazioneDAO.hasActiveBookingsForUser(1));
    }

    @Test
    @DisplayName("TC_7: Test salvataggio prenotazione con SQLException")
    void testDoSaveSQLException() throws SQLException {
        doThrow(new SQLException("DB error")).when(mockPreparedStatement).executeUpdate();
        Prenotazione prenotazione = new Prenotazione();
        assertThrows(NullPointerException.class, () -> prenotazioneDAO.doSave(prenotazione));
    }

    @Test
    @DisplayName("TC_8: Test ricerca prenotazione per ID inesistente")
    void testDoFindByIdNotFound() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        Prenotazione prenotazione = prenotazioneDAO.doFindById(99);
        assertNull(prenotazione);
    }

    @Test
    @DisplayName("TC_9: Test verifica disponibilità timeslot con SQLException")
    void testIsDisponibileSQLException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("DB error"));
        assertThrows(SQLException.class, () -> prenotazioneDAO.isDisponibile(1, LocalDateTime.now()));
    }

    @Test
    @DisplayName("TC_10: Test recupero tutte le prenotazioni con database vuoto")
    void testDoFindAllEmpty() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        List<Prenotazione> prenotazioni = prenotazioneDAO.doFindAll();
        assertTrue(prenotazioni.isEmpty());
    }

    @Test
    @DisplayName("TC_11: Test aggiornamento prenotazioni scadute con SQLException")
    void testUpdateExpiredBookingsSQLException() throws SQLException {
        doThrow(new SQLException("DB error")).when(mockPreparedStatement).executeUpdate();
        assertThrows(SQLException.class, () -> prenotazioneDAO.updateExpiredBookings());
    }

    @Test
    @DisplayName("TC_12: Test verifica prenotazioni attive per sessione con database vuoto")
    void testHasActiveBookingsEmpty() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        assertFalse(prenotazioneDAO.hasActiveBookings(1));
    }

    @Test
    @DisplayName("TC_13: Test verifica stato timeslot con ResultSet vuoto")
    void testCheckTimeslotStatusEmpty() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        assertTrue((Boolean) prenotazioneDAO.checkTimeslotStatus(1, LocalDateTime.now()).get("disponibile"));
    }
}
