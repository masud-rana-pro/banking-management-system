package com.sbms.verification.dto.request;

public record VerificationSendOtpRequest(
        Long userId,
        Long customerId,
        String referenceModule,
        Long referenceId,
        String contactValue,
        String remarks
) {
}
