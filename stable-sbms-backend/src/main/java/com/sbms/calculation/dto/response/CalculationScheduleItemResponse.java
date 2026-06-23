package com.sbms.calculation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CalculationScheduleItemResponse(
        Integer periodNo,
        LocalDate dueDate,
        BigDecimal principalComponent,
        BigDecimal profitComponent,
        BigDecimal totalAmount,
        BigDecimal outstandingBalance,
        String note
) {
}
