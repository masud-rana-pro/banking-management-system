package com.sbms.calculation.dto.response;

import java.util.List;

public record CalculationDashboardSummaryResponse(
        long recentCalculations,
        long failedCalculations,
        long financingSimulationCount,
        long depositSimulationCount,
        long profitSimulationCount,
        List<CalculationMetricResponse> productWiseSimulationCounts,
        List<CalculationRecentItemResponse> recentItems
) {
}
