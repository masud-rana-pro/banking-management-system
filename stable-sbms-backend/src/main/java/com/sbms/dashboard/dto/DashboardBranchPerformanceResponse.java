package com.sbms.dashboard.dto;

import java.math.BigDecimal;

public record DashboardBranchPerformanceResponse(
        Long branchId,
        String branchCode,
        String branchName,
        BigDecimal deposits,
        BigDecimal financing,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal netProfit,
        BigDecimal transactionVolume
) {
}

