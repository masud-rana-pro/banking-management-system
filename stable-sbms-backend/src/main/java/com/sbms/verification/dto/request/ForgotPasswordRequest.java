package com.sbms.verification.dto.request;

public record ForgotPasswordRequest(
        String identifier,
        String channelType
) {
}
