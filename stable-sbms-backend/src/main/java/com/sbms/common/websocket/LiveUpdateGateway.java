package com.sbms.common.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
public class LiveUpdateGateway {

    private final ObjectMapper objectMapper;
    private final LiveUpdateSessionRegistry sessionRegistry;

    public LiveUpdateGateway(ObjectMapper objectMapper, LiveUpdateSessionRegistry sessionRegistry) {
        this.objectMapper = objectMapper;
        this.sessionRegistry = sessionRegistry;
    }

    public void publish(LiveUpdateMessage message) {
        if (message == null) {
            return;
        }
        String payload = toJson(message);
        sessionRegistry.activeSessions().stream()
                .filter(client -> isAuthorized(message, client))
                .forEach(client -> send(client.session(), payload));
    }

    public void publish(String category,
                        String title,
                        String message,
                        String severity,
                        String route,
                        String targetUsername,
                        String targetRoleCode,
                        String requiredPermission) {
        publish(new LiveUpdateMessage(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                normalize(category),
                title,
                message,
                normalize(severity),
                route,
                blankToNull(targetUsername),
                blankToNull(targetRoleCode),
                normalize(requiredPermission)
        ));
    }

    private boolean isAuthorized(LiveUpdateMessage message, LiveUpdateSessionRegistry.ClientSession client) {
        if (message.targetUsername() != null) {
            return message.targetUsername().equalsIgnoreCase(blankToNull(client.username()));
        }
        if (message.targetRoleCode() != null && !message.targetRoleCode().equalsIgnoreCase(blankToNull(client.roleCode()))) {
            return false;
        }
        if (message.requiredPermission() != null) {
            return client.permissions().contains(message.requiredPermission().toUpperCase(Locale.ROOT));
        }
        return true;
    }

    private void send(WebSocketSession session, String payload) {
        if (session == null || !session.isOpen()) {
            return;
        }
        synchronized (session) {
            try {
                session.sendMessage(new TextMessage(payload));
            } catch (IOException ignored) {
                // Ignore failed push; connection cleanup will happen on socket close.
            }
        }
    }

    private String toJson(LiveUpdateMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize live update message", ex);
        }
    }

    private String normalize(String value) {
        String trimmed = blankToNull(value);
        return trimmed == null ? null : trimmed.toUpperCase(Locale.ROOT);
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
