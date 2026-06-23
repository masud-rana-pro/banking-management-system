package com.sbms.verification.dto.response;

import java.time.LocalDateTime;

public record VerificationContactStatusResponse(
        Long id,
        String referenceModule,
        Long referenceId,
        String contactType,
        String contactValue,
        Boolean isPrimary,
        Boolean isVerified,
        LocalDateTime verifiedAt,
        String verifiedBy,
        String verificationMethod,
        Long lastVerificationRequestId,
        String status,
        LocalDateTime updatedAt
) {
}
