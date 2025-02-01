package websocket;

import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for NotificationWebSocket")
class NotificationWebSocketTest {

    @Mock
    private Session mockSession;
    @Mock
    private RemoteEndpoint.Basic mockRemote;

    private NotificationWebSocket webSocket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webSocket = new NotificationWebSocket();
        when(mockSession.getBasicRemote()).thenReturn(mockRemote);
    }

    @Test
    @DisplayName("TC_1: Connessione WebSocket - MENTEE")
    void testOnOpen_Mentee() {
        when(mockSession.getRequestParameterMap()).thenReturn(Map.of("userType", List.of("MENTEE"), "userId", List.of("mentee1")));
        assertDoesNotThrow(() -> webSocket.onOpen(mockSession));
    }

    @Test
    @DisplayName("TC_2: Connessione WebSocket - MENTOR")
    void testOnOpen_Mentor() {
        when(mockSession.getRequestParameterMap()).thenReturn(Map.of("userType", List.of("MENTOR"), "userId",  List.of("mentor1")));
        assertDoesNotThrow(() -> webSocket.onOpen(mockSession));
    }

    @Test
    @DisplayName("TC_3: Ricezione Messaggio WebSocket")
    void testOnMessage() {
        assertDoesNotThrow(() -> webSocket.onMessage("Test message", mockSession));
    }

    @Test
    @DisplayName("TC_4: Chiusura Connessione")
    void testOnClose() {
        when(mockSession.getRequestParameterMap()).thenReturn(Map.of("userType", List.of("MENTEE"), "userId", List.of("mentee1")));
        assertDoesNotThrow(() -> webSocket.onClose(mockSession));
    }

    @Test
    @DisplayName("TC_5: Errore WebSocket")
    void testOnError() {
        assertDoesNotThrow(() -> webSocket.onError(mockSession, new Exception("Test Exception")));
    }

    @Test
    @DisplayName("TC_6: Invio Notifica a MENTEE con sessione attiva")
    void testNotifyMentee_Success() throws IOException {
        NotificationWebSocket webSocket = new NotificationWebSocket(); // Creiamo l'istanza reale
        when(mockSession.isOpen()).thenReturn(true);
        when(mockSession.getBasicRemote()).thenReturn(mockRemote);
        doNothing().when(mockRemote).sendText(anyString());

        // Simula l'apertura della connessione prima di inviare la notifica
        when(mockSession.getRequestParameterMap()).thenReturn(Map.of("userType", List.of("MENTEE"), "userId", List.of("mentee1")));
        webSocket.onOpen(mockSession);  // Qui registriamo la sessione attiva

        // Ora la sessione è attiva, quindi la notifica dovrebbe essere inviata
        NotificationWebSocket.notifyMentee("mentee1", "Test notification");

        verify(mockRemote, atLeastOnce()).sendText(anyString()); // Verifica che il messaggio venga inviato
    }

    @Test
    @DisplayName("TC_7: Invio Notifica a MENTOR con sessione attiva")
    void testNotifyMentor_Success() throws IOException {
        NotificationWebSocket webSocket = new NotificationWebSocket(); // Creiamo l'istanza reale
        when(mockSession.isOpen()).thenReturn(true);
        when(mockSession.getBasicRemote()).thenReturn(mockRemote);
        doNothing().when(mockRemote).sendText(anyString());

        // Simula l'apertura della connessione prima di inviare la notifica
        when(mockSession.getRequestParameterMap()).thenReturn(Map.of("userType", List.of("MENTOR"), "userId", List.of("mentor1")));
        webSocket.onOpen(mockSession);  // Qui registriamo la sessione attiva

        // Ora la sessione è attiva, quindi la notifica dovrebbe essere inviata
        NotificationWebSocket.notifyMentor("mentor1", "Test notification");

        verify(mockRemote, atLeastOnce()).sendText(anyString()); // Verifica che il messaggio venga inviato
    }


    @Test
    @DisplayName("TC_8: Invio Notifica a MENTEE senza sessione attiva")
    void testNotifyMentee_Failed() {
        NotificationWebSocket.notifyMentee("mentee1", "Test notification");
        // Non ci dovrebbe essere nessuna interazione con sessioni
    }

    @Test
    @DisplayName("TC_9: Invio Notifica a MENTOR senza sessione attiva")
    void testNotifyMentor_Failed() {
        NotificationWebSocket.notifyMentor("mentor1", "Test notification");
        // Non ci dovrebbe essere nessuna interazione con sessioni
    }
}
