package com.sbms.transaction.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.transaction.enums.StandingInstructionStatus;
import com.sbms.transaction.enums.TransferMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StandingInstructionResponse(
        Long id,
        String instructionCode,
        Long fromAccountId,
        String fromAccountNumber,
        String fromCustomerName,
        Long toAccountId,
        String toAccountNumber,
        String toCustomerName,
        Long branchId,
        BigDecimal amount,
        TransferMode transferMode,
        LocalDate scheduleDate,
        String frequency,
        LocalDate nextExecutionDate,
        StandingInstructionStatus instructionStatus,
        String remarks,
        RecordStatus status,
        LocalDateTime createdAt
) {
}
