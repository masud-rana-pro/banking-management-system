package com.sbms.common.websocket;

import com.sbms.auth.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LiveUpdateSessionRegistry {

    private final Map<String, ClientSession> sessions = new ConcurrentHashMap<>();

    public void register(WebSocketSession session) {
        if (session == null || session.getId() == null) {
            return;
        }
        sessions.put(session.getId(), new ClientSession(
                session,
                asLong(session.getAttributes().get(AuthService.REQUEST_USER_ID)),
                asString(session.getAttributes().get(AuthService.REQUEST_USERNAME)),
                asString(session.getAttributes().get(AuthService.REQUEST_ROLE_CODE)),
                asPermissions(session.getAttributes().get(AuthService.REQUEST_PERMISSIONS))
        ));
    }

    public void unregister(WebSocketSession session) {
        if (session == null || session.getId() == null) {
            return;
        }
        sessions.remove(session.getId());
    }

    public Collection<ClientSession> activeSessions() {
        return sessions.values();
    }

    public Set<String> onlineUsernames() {
        return sessions.values().stream()
                .map(ClientSession::username)
                .filter(value -> value != null && !value.trim().isEmpty())
                .collect(Collectors.toSet());
    }

    public boolean isUserOnline(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return sessions.values().stream()
                .map(ClientSession::username)
                .filter(value -> value != null)
                .anyMatch(value -> value.equalsIgnoreCase(username.trim()));
    }

    private Long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Set<String> asPermissions(Object value) {
        if (!(value instanceof Collection<?> items)) {
            return Collections.emptySet();
        }
        return items.stream()
                .map(String::valueOf)
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
    }

    public record ClientSession(
            WebSocketSession session,
            Long userId,
            String username,
            String roleCode,
            Set<String> permissions
    ) {
    }
}
