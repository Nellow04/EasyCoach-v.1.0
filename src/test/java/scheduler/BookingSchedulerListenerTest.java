package scheduler;

import jakarta.servlet.ServletContextEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for BookingSchedulerListener")
class BookingSchedulerListenerTest {

    @Mock
    private Scheduler mockScheduler;

    @Mock
    private ServletContextEvent mockServletContextEvent;

    @InjectMocks
    private BookingSchedulerListener bookingSchedulerListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingSchedulerListener = new BookingSchedulerListener(mockScheduler);
    }

    @Test
    @DisplayName("TC_1: Scheduler avviato correttamente all'inizializzazione")
    void testContextInitialized_SchedulerStart() throws Exception {
        doNothing().when(mockScheduler).start();
        when(mockScheduler.scheduleJob(any(), any())).thenReturn(null);


        bookingSchedulerListener.contextInitialized(mockServletContextEvent);

        verify(mockScheduler, times(1)).start();
        verify(mockScheduler, times(1)).scheduleJob(any(), any());
    }

    @Test
    @DisplayName("TC_2: Scheduler shutdown correttamente alla distruzione del contesto")
    void testContextDestroyed_SchedulerShutdown() throws Exception {
        doNothing().when(mockScheduler).shutdown();

        bookingSchedulerListener.contextDestroyed(mockServletContextEvent);

        verify(mockScheduler, times(1)).shutdown();
    }

    @Test
    @DisplayName("TC_3: Eccezione durante lo shutdown viene gestita correttamente")
    void testContextDestroyed_SchedulerException() throws Exception {
        doThrow(new SchedulerException("Errore simulato"))
                .when(mockScheduler).shutdown();

        assertDoesNotThrow(() -> bookingSchedulerListener.contextDestroyed(mockServletContextEvent));
    }

    @Test
    @DisplayName("TC_4: Eccezione durante l'inizializzazione del contesto - Gestione dell'errore")
    void testContextInitialized_SchedulerException() throws Exception {
        doThrow(new SchedulerException("Errore di test")).when(mockScheduler).start();

        assertDoesNotThrow(() -> bookingSchedulerListener.contextInitialized(mockServletContextEvent));

        verify(mockScheduler, times(1)).start();
        verify(mockScheduler, never()).scheduleJob(any(), any()); // Non deve essere chiamato se lo start fallisce
    }

    @Test
    @DisplayName("TC_5: Chiamata a contextDestroyed con scheduler null - Nessuna azione")
    void testContextDestroyed_NullScheduler() throws SchedulerException {
        BookingSchedulerListener listener = new BookingSchedulerListener(null); // Simula l'assenza dello scheduler

        assertDoesNotThrow(() -> listener.contextDestroyed(mockServletContextEvent));

        verify(mockScheduler, never()).shutdown(); // Lo shutdown non deve mai essere chiamato
    }

    @Test
    @DisplayName("TC_6: Eccezione durante lo shutdown del scheduler - Gestione dell'errore")
    void testContextDestroyed_SchedulerShutdownException() throws Exception {
        doThrow(new SchedulerException("Errore di test")).when(mockScheduler).shutdown();

        assertDoesNotThrow(() -> bookingSchedulerListener.contextDestroyed(mockServletContextEvent));

        verify(mockScheduler, times(1)).shutdown(); // Deve comunque provare a chiamarlo
    }

}
