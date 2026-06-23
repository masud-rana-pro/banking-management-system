package com.sbms.card.dto.response;

import com.sbms.card.enums.CardStatus;
import com.sbms.card.enums.CardType;
import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CardResponse(
        Long id,
        String cardRefNo,
        Long customerId,
        String customerCode,
        String customerName,
        Long accountId,
        String accountNumber,
        String accountTypeCode,
        String accountTypeName,
        Long branchId,
        BigDecimal currentBalance,
        CardType cardType,
        String maskedCardNo,
        LocalDate issueDate,
        LocalDate expiryDate,
        CardStatus cardStatus,
        String blockReason,
        RecordStatus status,
        boolean expiringSoon,
        long eventCount,
        long pinEventCount,
        long usageAlertCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
