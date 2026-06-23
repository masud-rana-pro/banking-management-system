package com.sbms.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GeneralDashboardResponse(
        String scopeLabel,
        String window,
        LocalDate dateFrom,
        LocalDate dateTo,
        List<DashboardKpiResponse> kpis,
        List<DashboardTrendPointResponse> businessTrend,
        List<DashboardTrendPointResponse> profitabilityTrend,
        List<DashboardMixResponse> portfolioMix,
        List<DashboardBranchPerformanceResponse> branchPerformance,
        List<DashboardRiskResponse> riskSnapshot,
        List<DashboardActivityResponse> pendingApprovals,
        List<DashboardActivityResponse> recentTransactions,
        List<DashboardActivityResponse> alerts
) {
}

