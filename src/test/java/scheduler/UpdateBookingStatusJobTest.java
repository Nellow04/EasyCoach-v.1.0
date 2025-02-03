package scheduler;

import model.dao.PrenotazioneDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for UpdateBookingStatusJob")
class UpdateBookingStatusJobTest {

    @Mock
    private PrenotazioneDAO mockPrenotazioneDAO;

    @Mock
    private JobExecutionContext mockJobExecutionContext;

    @InjectMocks
    private UpdateBookingStatusJob updateBookingStatusJob;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        updateBookingStatusJob = new UpdateBookingStatusJob(mockPrenotazioneDAO);
    }

    @Test
    @DisplayName("TC_1: Esecuzione corretta del job")
    void testExecute_Success() throws SQLException {
        assertDoesNotThrow(() -> updateBookingStatusJob.execute(mockJobExecutionContext));

        verify(mockPrenotazioneDAO, times(1)).updateExpiredBookings();
    }

    @Test
    @DisplayName("TC_2: Eccezione SQL durante l'aggiornamento delle prenotazioni")
    void testExecute_SQLException() throws SQLException {
        doThrow(new SQLException("Errore di test")).when(mockPrenotazioneDAO).updateExpiredBookings();

        JobExecutionException exception = assertThrows(JobExecutionException.class, () -> {
            updateBookingStatusJob.execute(mockJobExecutionContext);
        });

        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof SQLException);
    }
}