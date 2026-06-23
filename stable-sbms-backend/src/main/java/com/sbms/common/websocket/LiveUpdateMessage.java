package com.sbms.common.websocket;

import java.time.LocalDateTime;

public record LiveUpdateMessage(
        String id,
        LocalDateTime timestamp,
        String category,
        String title,
        String message,
        String severity,
        String route,
        String targetUsername,
        String targetRoleCode,
        String requiredPermission
) {
}
