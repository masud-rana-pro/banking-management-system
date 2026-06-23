package com.sbms.verification.dto.response;

import java.util.List;

public record VerificationDashboardSummaryResponse(
        long pendingRequests,
        long verifiedRequests,
        long failedRequests,
        long passwordResetRequests,
        long providerDispatchCount,
        long unverifiedEmailCount,
        long unverifiedMobileCount,
        List<VerificationLogResponse> recentRequests,
        List<VerificationAttemptResponse> recentAttempts,
        List<VerificationContactStatusResponse> recentContactStatuses
) {
}
