package com.sbms.verification.dto.response;

import java.time.LocalDateTime;

public record VerificationChannelResponse(
        Long id,
        String channelCode,
        String channelName,
        String providerName,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
