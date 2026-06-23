package com.sbms.transaction.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        String transactionRef,
        String transactionType,
        String channelType,
        Long branchId,
        Long terminalId,
        Long debitAccountId,
        String debitAccountNumber,
        String debitCustomerCode,
        String debitCustomerName,
        Long creditAccountId,
        String creditAccountNumber,
        String creditCustomerCode,
        String creditCustomerName,
        BigDecimal amount,
        String narration,
        String postedBy,
        String approvedBy,
        Boolean reversalFlag,
        Long parentTransactionId,
        String parentTransactionRef,
        String transactionStatus,
        RecordStatus status,
        LocalDateTime transactionDate,
        LocalDateTime createdAt,
        String cashType,
        String cashDirection,
        Long tellerUserId,
        String cashRemarks,
        String transferMode,
        String transferRemarks,
        String chequeNo,
        String draweeBank,
        String chequeStatus,
        String chequeRemarks,
        Long standingInstructionId,
        String standingInstructionCode,
        String reversalRequestStatus,
        String reversalReason,
        String reversalRequestedBy,
        LocalDateTime reversalRequestedAt,
        Long reversalTransactionId
) {
}
