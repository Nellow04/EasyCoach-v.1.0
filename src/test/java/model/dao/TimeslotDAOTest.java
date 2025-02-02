package model.dao;

import model.beans.Timeslot;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for TimeslotDAO")
class TimeslotDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private TimeslotDAO timeslotDAO;
    private MockedStatic<DBConnection> mockedDBConnection;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        timeslotDAO = new TimeslotDAO();

        mockedDBConnection = mockStatic(DBConnection.class);
        when(DBConnection.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    @DisplayName("TC_1: Test salvataggio timeslot")
    void testDoSave() throws SQLException {
        Timeslot timeslot = new Timeslot();
        timeslot.setIdSessione(1);
        timeslot.setGiorno(3);
        timeslot.setOrario(10);

        timeslotDAO.doSave(timeslot);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("TC_2: Test ricerca timeslot per ID")
    void testDoFindById() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("idTimeslot")).thenReturn(1);
        when(mockResultSet.getInt("idSessione")).thenReturn(2);
        when(mockResultSet.getInt("giorno")).thenReturn(3);
        when(mockResultSet.getInt("orario")).thenReturn(10);

        Timeslot timeslot = timeslotDAO.doFindById(1);
        assertNotNull(timeslot);
        assertEquals(1, timeslot.getIdTimeslot());
    }

    @Test
    @DisplayName("TC_3: Test aggiornamento timeslot")
    void testDoUpdate() throws SQLException {
        Timeslot timeslot = new Timeslot();
        timeslot.setIdTimeslot(1);
        timeslot.setIdSessione(2);
        timeslot.setGiorno(4);
        timeslot.setOrario(12);

        timeslotDAO.doUpdate(timeslot);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("TC_4: Test recupero tutti i timeslot")
    void testDoFindAll() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("idTimeslot")).thenReturn(1);

        List<Timeslot> timeslots = timeslotDAO.doFindAll();
        assertEquals(1, timeslots.size());
    }

    @Test
    @DisplayName("TC_5: Test ricerca timeslot per mentor")
    void testFindByMentorId() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);

        List<Timeslot> timeslots = timeslotDAO.findByMentorId(1);
        assertEquals(1, timeslots.size());
    }

    @Test
    @DisplayName("TC_6: Test ricerca timeslot per sessione")
    void testFindBySessionId() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);

        List<Timeslot> timeslots = timeslotDAO.findBySessionId(1);
        assertEquals(1, timeslots.size());
    }

    @Test
    @DisplayName("TC_7: Test eliminazione timeslot per sessione")
    void testDoDeleteBySessione() throws SQLException {
        timeslotDAO.doDeleteBySessione(1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

}