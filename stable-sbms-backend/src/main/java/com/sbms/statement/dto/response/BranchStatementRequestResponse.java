package com.sbms.statement.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.statement.enums.StatementRequestStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BranchStatementRequestResponse(
        Long id,
        String requestNo,
        Long branchId,
        String branchCode,
        String branchName,
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
        BigDecimal totalTransactionAmount,
        BigDecimal totalCashIn,
        BigDecimal totalCashOut,
        BigDecimal vaultClosingBalance,
        Long activeTerminalCount,
        List<StatementLineResponse> lines
) {
}
