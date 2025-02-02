package model.dao;

import model.beans.Pagamento;
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

@DisplayName("Test Cases for PagamentoDAO")
class PagamentoDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private PagamentoDAO pagamentoDAO;
    private MockedStatic<DBConnection> mockedDBConnection;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        pagamentoDAO = new PagamentoDAO();

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
    @DisplayName("TC_1: Test salvataggio pagamento con SQLException")
    void testDoSaveSQLException() throws SQLException {
        doThrow(new SQLException("DB error")).when(mockPreparedStatement).executeUpdate();
        Pagamento pagamento = new Pagamento();
        assertThrows(SQLException.class, () -> pagamentoDAO.doSave(pagamento));
    }

    @Test
    @DisplayName("TC_2: Test ricerca pagamento per ID inesistente")
    void testDoFindByIdNotFound() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        Pagamento pagamento = pagamentoDAO.doFindById(99);
        assertNull(pagamento);
    }

    @Test
    @DisplayName("TC_3: Test aggiornamento pagamento con SQLException")
    void testDoUpdateSQLException() throws SQLException {
        doThrow(new SQLException("DB error")).when(mockPreparedStatement).executeUpdate();
        Pagamento pagamento = new Pagamento();
        assertThrows(SQLException.class, () -> pagamentoDAO.doUpdate(pagamento));
    }

    @Test
    @DisplayName("TC_4: Test recupero tutti i pagamenti con database vuoto")
    void testDoFindAllEmpty() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        List<Pagamento> pagamenti = pagamentoDAO.doFindAll();
        assertTrue(pagamenti.isEmpty());
    }

    @Test
    @DisplayName("TC_8: Test ricerca pagamento con SQLException")
    void testDoFindByIdSQLException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("DB error"));
        assertThrows(SQLException.class, () -> pagamentoDAO.doFindById(1));
    }
}
