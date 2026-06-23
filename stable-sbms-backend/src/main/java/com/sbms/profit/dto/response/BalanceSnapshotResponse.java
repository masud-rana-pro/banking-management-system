package com.sbms.profit.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BalanceSnapshotResponse(
        Long id,
        Long accountId,
        String accountNumber,
        LocalDate snapshotDate,
        BigDecimal closingBalance,
        BigDecimal averageBalance,
        RecordStatus status,
        LocalDateTime createdAt
) {
}
