package model.connection;

import jakarta.servlet.ServletContextEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

@DisplayName("Test Cases for ConnectionPoolContextListener")
class ConnectionPoolContextListenerTest {

    private ConnectionPoolContextListener listener;
    private ServletContextEvent mockEvent;

    @BeforeEach
    void setUp() {
        listener = new ConnectionPoolContextListener();
        mockEvent = mock(ServletContextEvent.class);
    }

    @Test
    @DisplayName("TC_1: Test chiusura del connection pool")
    void testContextDestroyed() {
        assertDoesNotThrow(() -> listener.contextDestroyed(mockEvent));
    }
}
