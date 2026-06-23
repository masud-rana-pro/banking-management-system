package com.sbms.verification.dto.response;

import java.time.LocalDateTime;

public record VerificationTemplateResponse(
        Long id,
        String purpose,
        String channelType,
        String templateCode,
        String templateName,
        String subjectLine,
        String templateBody,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
