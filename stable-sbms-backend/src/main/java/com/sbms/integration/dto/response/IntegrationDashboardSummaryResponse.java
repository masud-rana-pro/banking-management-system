package com.sbms.integration.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record IntegrationDashboardSummaryResponse(
        long activeProviders,
        long failedIntegrations,
        long retryPending,
        LocalDateTime lastSuccessfulSync,
        double successRate,
        List<IntegrationProviderTypeSummaryResponse> providerTypeSummary,
        List<IntegrationExecutionLogResponse> recentLogs
) {}
