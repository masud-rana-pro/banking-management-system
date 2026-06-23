package com.sbms.verification.dto.request;

public record StepUpChallengeRequest(
        String actionCode,
        String targetModule,
        Long targetId,
        String channelPreference,
        String remarks
) {
}
