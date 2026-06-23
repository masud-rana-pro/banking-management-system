package com.sbms.account.dto.response;

public record AccountDashboardSummaryResponse(
        Long totalAccounts,
        Long pendingOpeningRequests,
        Long activeAccounts,
        Long blockedAccounts,
        Long frozenAccounts,
        Long awaitingVerificationAccounts
) {
}
