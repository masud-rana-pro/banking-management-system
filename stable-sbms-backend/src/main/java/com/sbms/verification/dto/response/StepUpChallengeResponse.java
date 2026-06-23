package com.sbms.verification.dto.response;

import java.time.LocalDateTime;

public record StepUpChallengeResponse(
        Long requestId,
        String actionCode,
        String targetModule,
        Long targetId,
        String otpChannelType,
        String otpDestinationMasked,
        LocalDateTime otpExpiresAt
) {
}
