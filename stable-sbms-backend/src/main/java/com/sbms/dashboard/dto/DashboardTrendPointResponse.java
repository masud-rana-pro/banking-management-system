package com.sbms.dashboard.dto;

import java.math.BigDecimal;

public record DashboardTrendPointResponse(
        String label,
        BigDecimal depositInflow,
        BigDecimal withdrawalOutflow,
        BigDecimal financingDisbursed,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal netProfit,
        BigDecimal transactionVolume
) {
}

