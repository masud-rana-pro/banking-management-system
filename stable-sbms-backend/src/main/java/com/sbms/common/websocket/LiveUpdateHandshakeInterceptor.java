package com.sbms.common.websocket;

import com.sbms.auth.AuthService;
import com.sbms.user.entity.UserSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
public class LiveUpdateHandshakeInterceptor implements HandshakeInterceptor {

    private final AuthService authService;

    public LiveUpdateHandshakeInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        try {
            String token = extractToken(request);
            UserSession session = authService.resolveActiveSession(token);
            attributes.put(AuthService.REQUEST_USER_ID, session.getUser().getId());
            attributes.put(AuthService.REQUEST_USERNAME, session.getUser().getUsername());
            attributes.put(AuthService.REQUEST_ROLE_CODE, session.getUser().getRole() == null ? null : session.getUser().getRole().getCode());
            attributes.put(AuthService.REQUEST_PERMISSIONS, authService.getGrantedPermissions(session.getUser()));
            return true;
        } catch (Exception ex) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // no-op
    }

    private String extractToken(ServerHttpRequest request) {
        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");
        if (token != null && !token.trim().isEmpty()) {
            return token.trim();
        }
        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders != null) {
            for (String header : authHeaders) {
                if (header != null && header.startsWith("Bearer ")) {
                    return header.substring(7).trim();
                }
            }
        }
        return null;
    }
}
