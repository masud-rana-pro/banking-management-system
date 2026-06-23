package com.sbms.verification.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record VerificationLogResponse(
        Long id,
        String purpose,
        String channelType,
        String tokenType,
        String contactValue,
        String contactValueMasked,
        Long userId,
        String username,
        Long customerId,
        String customerCode,
        String customerName,
        String referenceModule,
        Long referenceId,
        Integer attemptCount,
        Integer maxAttemptCount,
        String requestStatus,
        String providerResponse,
        LocalDateTime sentAt,
        LocalDateTime expiresAt,
        LocalDateTime usedAt,
        Boolean contactVerified,
        String lastDispatchStatus,
        List<VerificationAttemptResponse> attempts
) {
}
