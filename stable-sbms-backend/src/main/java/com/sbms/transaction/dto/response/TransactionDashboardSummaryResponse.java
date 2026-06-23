package com.sbms.transaction.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record TransactionDashboardSummaryResponse(
        BigDecimal todayDepositTotal,
        BigDecimal todayWithdrawalTotal,
        BigDecimal todayTransferTotal,
        Long pendingReversals,
        BigDecimal tellerLimitUsed,
        BigDecimal tellerLimit,
        BigDecimal tellerLimitUsagePercent,
        Long suspiciousTransactionCount,
        List<BranchTransactionSummaryResponse> topBranches
) {
}
