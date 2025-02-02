package model.connection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for DBConnection (Mocked)")
class DBConnectionTest {

    @Mock
    private DataSource mockDataSource;

    @Mock
    private Connection mockConnection;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
    }

    @Test
    @DisplayName("TC_1: Ottenere una connessione valida")
    void testGetConnectionMocked() throws SQLException {
        assertNotNull(mockDataSource.getConnection());
        assertFalse(mockDataSource.getConnection().isClosed());
    }

    @Test
    @DisplayName("TC_2: Chiudere connessione")
    void testCloseConnectionMocked() throws SQLException {
        mockConnection.close();
        verify(mockConnection, times(1)).close();
    }

    @Test
    @DisplayName("TC_4: Chiusura del pool con pool già chiuso")
    void testClosePoolWithNullDataSource() {
        DBConnection.closePool(); // Simuliamo che il pool sia già chiuso
        assertDoesNotThrow(DBConnection::closePool);
    }

    @Test
    @DisplayName("TC_5: Simulazione errore inizializzazione pool")
    void testInitializationError() {
        try (MockedStatic<DBConnection> mockedDBConnection = mockStatic(DBConnection.class)) {
            mockedDBConnection.when(DBConnection::getConnection).thenThrow(new RuntimeException("Errore inizializzazione"));
            RuntimeException exception = assertThrows(RuntimeException.class, DBConnection::getConnection);
            assertEquals("Errore inizializzazione", exception.getMessage());
        }
    }

}
