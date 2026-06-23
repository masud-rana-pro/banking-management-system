package com.sbms.calculation.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CalculationSimulateRequest(
        String sourceModule,
        String productType,
        BigDecimal principalAmount,
        BigDecimal ratePercent,
        Integer tenureMonths,
        String frequency,
        LocalDate startDate,
        String remarks
) {
}
