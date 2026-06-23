package com.sbms.verification.dto.response;

import java.time.LocalDateTime;

public record StepUpVerifyResponse(
        String verificationToken,
        String actionCode,
        String targetModule,
        Long targetId,
        LocalDateTime verifiedAt,
        LocalDateTime tokenExpiresAt
) {
}
