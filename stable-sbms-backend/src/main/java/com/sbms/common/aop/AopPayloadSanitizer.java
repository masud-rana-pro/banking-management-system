package com.sbms.common.aop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Component
public class AopPayloadSanitizer {

    private static final int MAX_LENGTH = 1800;
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password",
            "newpassword",
            "currentpassword",
            "confirmpassword",
            "otp",
            "otpcode",
            "token",
            "jwttoken",
            "authorization",
            "stepuptoken",
            "pin",
            "secret",
            "apppassword",
            "authheadervalue"
    );

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public String sanitizeArguments(Object[] args) {
        ArrayNode array = objectMapper.createArrayNode();
        if (args == null) {
            return "[]";
        }
        for (Object arg : args) {
            if (shouldIgnore(arg)) {
                continue;
            }
            array.add(sanitizeNode(toNode(arg)));
        }
        return trimToLimit(write(array));
    }

    public String sanitizeSingle(Object value) {
        return trimToLimit(write(sanitizeNode(toNode(value))));
    }

    private JsonNode toNode(Object value) {
        if (value == null) {
            return NullNode.getInstance();
        }
        try {
            return objectMapper.valueToTree(value);
        } catch (IllegalArgumentException ex) {
            return objectMapper.getNodeFactory().textNode(String.valueOf(value));
        }
    }

    private JsonNode sanitizeNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return NullNode.getInstance();
        }
        if (node.isObject()) {
            ObjectNode sanitized = objectMapper.createObjectNode();
            node.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                if (isSensitive(key)) {
                    sanitized.put(key, "***masked***");
                } else {
                    sanitized.set(key, sanitizeNode(entry.getValue()));
                }
            });
            return sanitized;
        }
        if (node.isArray()) {
            ArrayNode sanitized = objectMapper.createArrayNode();
            node.forEach(item -> sanitized.add(sanitizeNode(item)));
            return sanitized;
        }
        if (node.isTextual() && node.asText().length() > 300) {
            return objectMapper.getNodeFactory().textNode(node.asText().substring(0, 297) + "...");
        }
        return node;
    }

    private boolean shouldIgnore(Object arg) {
        return arg == null
                || arg instanceof ServletRequest
                || arg instanceof ServletResponse
                || arg instanceof BindingResult
                || arg instanceof MultipartFile;
    }

    private boolean isSensitive(String key) {
        return key != null && SENSITIVE_KEYS.contains(key.trim().toLowerCase(Locale.ROOT));
    }

    private String write(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (Exception ex) {
            return "\"serialization-failed\"";
        }
    }

    private String trimToLimit(String value) {
        if (value == null) {
            return null;
        }
        return value.length() <= MAX_LENGTH ? value : value.substring(0, MAX_LENGTH - 3) + "...";
    }
}
