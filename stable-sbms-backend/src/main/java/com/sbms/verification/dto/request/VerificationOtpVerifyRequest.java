package com.sbms.verification.dto.request;

public record VerificationOtpVerifyRequest(
        Long requestId,
        String otpCode,
        String ipAddress,
        String deviceInfo,
        String createdBy
) {
}
