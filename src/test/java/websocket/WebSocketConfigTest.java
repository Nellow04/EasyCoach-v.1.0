package websocket;

import jakarta.websocket.server.ServerEndpointConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Test Cases for WebSocketConfig")
class WebSocketConfigTest {

    private WebSocketConfig webSocketConfig;

    @BeforeEach
    void setUp() {
        webSocketConfig = new WebSocketConfig();
    }

    @Test
    @DisplayName("TC_1: Verifica che getEndpointConfigs restituisca un set vuoto")
    void testGetEndpointConfigs() {
        Set<ServerEndpointConfig> result = webSocketConfig.getEndpointConfigs(Set.of());
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Il set di endpoint config dovrebbe essere vuoto");
    }

    @Test
    @DisplayName("TC_2: Verifica che getAnnotatedEndpointClasses contenga NotificationWebSocket")
    void testGetAnnotatedEndpointClasses() {
        Set<Class<?>> result = webSocketConfig.getAnnotatedEndpointClasses(Set.of());
        assertNotNull(result);
        assertTrue(result.contains(NotificationWebSocket.class), "Dovrebbe contenere NotificationWebSocket");
    }
}