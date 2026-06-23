package com.sbms.statement.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.statement.enums.StatementRequestStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerStatementRequestResponse(
        Long id,
        String requestNo,
        Long customerId,
        String customerCode,
        String customerName,
        Long accountId,
        String accountNumber,
        Long branchId,
        LocalDate dateFrom,
        LocalDate dateTo,
        StatementRequestStatus requestStatus,
        Long generatedFileId,
        String requestedBy,
        LocalDateTime requestedAt,
        LocalDateTime generatedAt,
        RecordStatus status,
        FileReferenceResponse generatedFile,
        Long transactionCount,
        BigDecimal totalDebit,
        BigDecimal totalCredit,
        BigDecimal netMovement,
        BigDecimal profitPosted,
        BigDecimal currentBalance,
        List<StatementLineResponse> lines
) {
}
