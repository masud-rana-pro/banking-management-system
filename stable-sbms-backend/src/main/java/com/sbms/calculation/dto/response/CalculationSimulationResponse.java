package com.sbms.calculation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CalculationSimulationResponse(
        String sourceModule,
        String productType,
        String formulaName,
        BigDecimal principalAmount,
        BigDecimal ratePercent,
        Integer tenureMonths,
        String frequency,
        LocalDate startDate,
        BigDecimal totalPrincipal,
        BigDecimal totalProfit,
        BigDecimal totalPayable,
        BigDecimal periodicAmount,
        BigDecimal residualAmount,
        List<String> assumptions,
        List<CalculationScheduleItemResponse> schedule
) {
}
