package com.sbms.verification.dto.request;

public record StepUpChallengeVerifyRequest(
        Long requestId,
        String actionCode,
        String targetModule,
        Long targetId,
        String otpCode
) {
}
