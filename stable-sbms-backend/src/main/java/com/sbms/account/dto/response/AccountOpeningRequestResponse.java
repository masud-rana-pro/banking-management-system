package com.sbms.account.dto.response;

import com.sbms.account.enums.AccountOpeningRequestStatus;
import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AccountOpeningRequestResponse(
        Long id,
        String requestNo,
        Long customerId,
        String customerCode,
        String customerName,
        Long accountTypeId,
        String accountTypeCode,
        String accountTypeName,
        Long branchId,
        LocalDate requestedDate,
        BigDecimal initialDepositAmount,
        AccountOpeningRequestStatus requestStatus,
        String verifiedBy,
        LocalDateTime verifiedAt,
        String approvedBy,
        LocalDateTime approvedAt,
        String remarks,
        String applicantImageName,
        RecordStatus status,
        LocalDateTime createdAt,
        Long accountId
) {
}
