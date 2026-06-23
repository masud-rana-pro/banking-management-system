package com.sbms.account.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String accountNumber,
        Long customerId,
        String customerCode,
        String customerName,
        Long accountTypeId,
        String accountTypeCode,
        String accountTypeName,
        Long branchId,
        Long openingRequestId,
        String requestNo,
        LocalDate openedDate,
        BigDecimal currentBalance,
        BigDecimal availableBalance,
        Long profitRatioId,
        String accountStatus,
        LocalDate closedDate,
        String remarks,
        RecordStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
