package com.sbms.profit.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProfitRatioResponse(
        Long id,
        String ratioCode,
        Long accountTypeId,
        String accountTypeCode,
        String accountTypeName,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        BigDecimal ratioPercent,
        RecordStatus status,
        Boolean activeNow,
        Long linkedScheduleCount,
        Long linkedPostingCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
