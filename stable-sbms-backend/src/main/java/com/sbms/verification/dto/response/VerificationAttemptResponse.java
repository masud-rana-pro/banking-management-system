package com.sbms.verification.dto.response;

import java.time.LocalDateTime;

public record VerificationAttemptResponse(
        Long id,
        String attemptType,
        String attemptValueMasked,
        String attemptStatus,
        String remarks,
        String ipAddress,
        String deviceInfo,
        String createdBy,
        LocalDateTime createdAt
) {
}
