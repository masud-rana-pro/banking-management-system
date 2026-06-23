package com.sbms.profit.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProfitScheduleResponse(
        Long id,
        Long accountId,
        String accountNumber,
        Long customerId,
        String customerCode,
        String customerName,
        Long accountTypeId,
        String accountTypeCode,
        String accountTypeName,
        Long branchId,
        BigDecimal currentBalance,
        String profitFrequency,
        LocalDate nextPostingDate,
        LocalDate lastPostingDate,
        RecordStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
