package com.sbms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbms.auth.AuthService;
import com.sbms.common.response.ApiResponse;
import com.sbms.user.entity.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    private final PermissionAuthorizationService permissionAuthorizationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthInterceptor(AuthService authService,
        PermissionAuthorizationService permissionAuthorizationService) {
        this.authService = authService;
        this.permissionAuthorizationService = permissionAuthorizationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String header = request.getHeader("Authorization");
        String token = header != null && header.startsWith("Bearer ") ? header.substring(7) : request.getParameter("access_token");
        try {
            UserSession session = authService.resolveActiveSession(token);
            var permissions = authService.getGrantedPermissions(session.getUser());
            request.setAttribute(AuthService.REQUEST_USER_ID, session.getUser().getId());
            request.setAttribute(AuthService.REQUEST_USERNAME, session.getUser().getUsername());
            request.setAttribute(AuthService.REQUEST_ROLE_CODE, session.getUser().getRole() == null ? null : session.getUser().getRole().getCode());
            request.setAttribute(AuthService.REQUEST_PERMISSIONS, permissions);
            permissionAuthorizationService.authorize(request, (HandlerMethod) handler, permissions);
            return true;
        } catch (com.sbms.common.exception.ForbiddenException ex) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), ApiResponse.fail(ex.getMessage(), null));
            return false;
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), ApiResponse.fail(ex.getMessage(), null));
            return false;
        }
    }
}
