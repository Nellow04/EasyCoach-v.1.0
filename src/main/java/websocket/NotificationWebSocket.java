package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint("/notifications")
public class NotificationWebSocket {
    private static final Map<String, Session> menteeConnections = new ConcurrentHashMap<>();
    private static final Map<String, Session> mentorConnections = new ConcurrentHashMap<>();
    // Code di messaggi in attesa per utenti non connessi
    private static final Map<String, Queue<NotificationMessage>> pendingMenteeNotifications = new ConcurrentHashMap<>();
    private static final Map<String, Queue<NotificationMessage>> pendingMentorNotifications = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();
    private static final Logger logger = Logger.getLogger(NotificationWebSocket.class.getName());

    @OnOpen
    public void onOpen(Session session) {
        try {
            String userType = session.getRequestParameterMap().get("userType").get(0);
            String userId = session.getRequestParameterMap().get("userId").get(0);
            
            logger.info("Nuova connessione WebSocket: " + userType + " - " + userId);
            
            if ("MENTEE".equals(userType)) {
                menteeConnections.put(userId, session);
                // Invia eventuali notifiche in attesa
                sendPendingNotifications(userId, true);
            } else if ("MENTOR".equals(userType)) {
                mentorConnections.put(userId, session);
                // Invia eventuali notifiche in attesa
                sendPendingNotifications(userId, false);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante l'apertura della connessione WebSocket", e);
        }
    }

    private void sendPendingNotifications(String userId, boolean isMentee) {
        try {
            Queue<NotificationMessage> pendingQueue = isMentee ? 
                pendingMenteeNotifications.get(userId) : 
                pendingMentorNotifications.get(userId);

            if (pendingQueue != null && !pendingQueue.isEmpty()) {
                Session session = isMentee ? menteeConnections.get(userId) : mentorConnections.get(userId);
                logger.info("Invio notifiche in attesa per " + (isMentee ? "MENTEE" : "MENTOR") + " " + userId);
                
                NotificationMessage notification;
                while ((notification = pendingQueue.poll()) != null && session != null && session.isOpen()) {
                    String json = gson.toJson(notification);
                    session.getBasicRemote().sendText(json);
                    logger.info("Inviata notifica in attesa: " + json);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante l'invio delle notifiche in attesa", e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            String userType = session.getRequestParameterMap().get("userType").get(0);
            String userId = session.getRequestParameterMap().get("userId").get(0);
            
            logger.info("Chiusura connessione WebSocket: " + userType + " - " + userId);
            
            if ("MENTEE".equals(userType)) {
                menteeConnections.remove(userId);
            } else if ("MENTOR".equals(userType)) {
                mentorConnections.remove(userId);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante la chiusura della connessione WebSocket", e);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.log(Level.SEVERE, "Errore WebSocket", throwable);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("Messaggio ricevuto: " + message);
    }

    public static void notifyMentee(String menteeId, String message) {
        Session session = menteeConnections.get(menteeId);
        logger.info("Tentativo di invio notifica a MENTEE " + menteeId + ". Sessione trovata: " + (session != null) + ", aperta: " + (session != null && session.isOpen()));
        
        NotificationMessage notification = new NotificationMessage("MENTEE", message);
        
        if (session != null && session.isOpen()) {
            try {
                String json = gson.toJson(notification);
                logger.info("Invio notifica al MENTEE " + menteeId + ": " + json);
                session.getBasicRemote().sendText(json);
                logger.info("Notifica inviata con successo al MENTEE " + menteeId);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Errore durante l'invio della notifica al MENTEE: " + menteeId, e);
                addPendingNotification(menteeId, notification, true);
            }
        } else {
            logger.warning("Impossibile inviare notifica al MENTEE " + menteeId + ": sessione non trovata o chiusa");
            addPendingNotification(menteeId, notification, true);
        }
    }

    public static void notifyMentor(String mentorId, String message) {
        Session session = mentorConnections.get(mentorId);
        logger.info("Tentativo di invio notifica a MENTOR " + mentorId + ". Sessione trovata: " + (session != null) + ", aperta: " + (session != null && session.isOpen()));
        
        NotificationMessage notification = new NotificationMessage("MENTOR", message);
        
        if (session != null && session.isOpen()) {
            try {
                String json = gson.toJson(notification);
                logger.info("Invio notifica al MENTOR " + mentorId + ": " + json);
                session.getBasicRemote().sendText(json);
                logger.info("Notifica inviata con successo al MENTOR " + mentorId);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Errore durante l'invio della notifica al MENTOR: " + mentorId, e);
                addPendingNotification(mentorId, notification, false);
            }
        } else {
            logger.warning("Impossibile inviare notifica al MENTOR " + mentorId + ": sessione non trovata o chiusa");
            addPendingNotification(mentorId, notification, false);
        }
    }

    private static void addPendingNotification(String userId, NotificationMessage notification, boolean isMentee) {
        Map<String, Queue<NotificationMessage>> pendingMap = isMentee ? pendingMenteeNotifications : pendingMentorNotifications;
        pendingMap.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>()).offer(notification);
        logger.info("Aggiunta notifica in attesa per " + (isMentee ? "MENTEE" : "MENTOR") + " " + userId);
    }

    // Resa pubblica e con getter per la serializzazione JSON
    public static class NotificationMessage {
        private final String type;
        private final String message;

        public NotificationMessage(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }
    }
}
