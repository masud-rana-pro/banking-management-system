package com.sbms.transaction.dto.response;

public record BranchTransactionSummaryResponse(
        Long branchId,
        Long transactionCount
) {
}
