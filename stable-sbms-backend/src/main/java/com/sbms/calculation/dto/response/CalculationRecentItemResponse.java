package com.sbms.calculation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CalculationRecentItemResponse(
        String sourceModule,
        String referenceNo,
        String productType,
        String customerOrAccount,
        BigDecimal amount,
        String status,
        LocalDate eventDate,
        String routeHint
) {
}
