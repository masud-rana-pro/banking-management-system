package com.sbms.profit.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProfitPostingResponse(
        Long id,
        String postingRef,
        Long accountId,
        String accountNumber,
        Long customerId,
        String customerCode,
        String customerName,
        Long branchId,
        Long scheduleId,
        String profitFrequency,
        String ratioCode,
        LocalDate postingDate,
        BigDecimal profitAmount,
        LocalDate periodFrom,
        LocalDate periodTo,
        BigDecimal snapshotBalance,
        BigDecimal snapshotAverageBalance,
        String postedBy,
        String status,
        String failureReason,
        LocalDateTime createdAt
) {
}
