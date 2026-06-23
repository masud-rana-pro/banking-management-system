package com.sbms.verification.dto.request;

public record ResetPasswordRequest(
        Long requestId,
        String otpCode,
        String newPassword,
        String confirmPassword
) {
}
