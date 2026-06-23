package com.sbms.common.websocket;

import com.sbms.auth.AuthService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class LiveUpdateWebSocketHandler extends TextWebSocketHandler {

    private final LiveUpdateSessionRegistry sessionRegistry;
    private final LiveUpdateGateway liveUpdateGateway;

    public LiveUpdateWebSocketHandler(LiveUpdateSessionRegistry sessionRegistry,
                                      LiveUpdateGateway liveUpdateGateway) {
        this.sessionRegistry = sessionRegistry;
        this.liveUpdateGateway = liveUpdateGateway;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = session == null ? null : String.valueOf(session.getAttributes().getOrDefault(AuthService.REQUEST_USERNAME, ""));
        boolean wasOnline = sessionRegistry.isUserOnline(username);
        sessionRegistry.register(session);
        if (username != null && !username.isBlank() && !wasOnline) {
            publishPresence("ONLINE", username);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if (message != null && "PING".equalsIgnoreCase(message.getPayload())) {
            session.sendMessage(new TextMessage("PONG"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = session == null ? null : String.valueOf(session.getAttributes().getOrDefault(AuthService.REQUEST_USERNAME, ""));
        sessionRegistry.unregister(session);
        if (username != null && !username.isBlank() && !sessionRegistry.isUserOnline(username)) {
            publishPresence("OFFLINE", username);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String username = session == null ? null : String.valueOf(session.getAttributes().getOrDefault(AuthService.REQUEST_USERNAME, ""));
        sessionRegistry.unregister(session);
        if (username != null && !username.isBlank() && !sessionRegistry.isUserOnline(username)) {
            publishPresence("OFFLINE", username);
        }
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    private void publishPresence(String state, String username) {
        liveUpdateGateway.publish(
                "PRESENCE",
                state,
                username,
                "INFO",
                null,
                null,
                null,
                null
        );
    }
}
