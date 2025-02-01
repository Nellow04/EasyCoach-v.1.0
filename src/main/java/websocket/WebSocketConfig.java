package websocket;

import jakarta.websocket.Endpoint;
import jakarta.websocket.server.ServerApplicationConfig;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;

import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/notifications")
public class WebSocketConfig implements ServerApplicationConfig {
    
    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        return new HashSet<>();
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        Set<Class<?>> endpoints = new HashSet<>();
        endpoints.add(NotificationWebSocket.class);
        return endpoints;
    }
}
