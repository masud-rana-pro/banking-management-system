package com.sbms.common.aop;

import com.sbms.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collections;
import java.util.Map;

public final class AopRequestContext {

    private AopRequestContext() {
    }

    public static HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes = currentAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    public static HttpServletResponse currentResponse() {
        ServletRequestAttributes attributes = currentAttributes();
        return attributes == null ? null : attributes.getResponse();
    }

    public static Long currentUserId() {
        HttpServletRequest request = currentRequest();
        Object value = request == null ? null : request.getAttribute(AuthService.REQUEST_USER_ID);
        return value instanceof Long longValue ? longValue : null;
    }

    public static String currentUsername() {
        HttpServletRequest request = currentRequest();
        Object value = request == null ? null : request.getAttribute(AuthService.REQUEST_USERNAME);
        return value == null ? null : String.valueOf(value);
    }

    public static String currentRoleCode() {
        HttpServletRequest request = currentRequest();
        Object value = request == null ? null : request.getAttribute(AuthService.REQUEST_ROLE_CODE);
        return value == null ? null : String.valueOf(value);
    }

    public static String requestUri() {
        HttpServletRequest request = currentRequest();
        return request == null ? null : request.getRequestURI();
    }

    public static String requestMethod() {
        HttpServletRequest request = currentRequest();
        return request == null ? null : request.getMethod();
    }

    public static String clientIp() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return null;
        }
        String forwarded = trim(request.getHeader("X-Forwarded-For"));
        if (forwarded != null) {
            int commaIndex = forwarded.indexOf(',');
            return commaIndex >= 0 ? forwarded.substring(0, commaIndex).trim() : forwarded;
        }
        return trim(request.getRemoteAddr());
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> pathVariables() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return Collections.emptyMap();
        }
        Object value = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (value instanceof Map<?, ?> mapValue) {
            return (Map<String, String>) mapValue;
        }
        return Collections.emptyMap();
    }

    private static ServletRequestAttributes currentAttributes() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        return attributes;
    }

    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
