package com.sbms.card.dto.response;

import java.util.List;

public record CardDashboardSummaryResponse(
        long totalCards,
        long activeCards,
        long blockedCards,
        long expiringSoon,
        long cardTxnCount,
        long pendingActivations,
        long cardUsageAlertsToday,
        List<CardResponse> expiringCards,
        List<CardResponse> pendingActivationCards,
        List<CardTransactionResponse> recentUsageAlerts
) {
}
