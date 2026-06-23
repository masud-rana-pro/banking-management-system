package com.sbms.dashboard.dto;

import java.math.BigDecimal;

public record DashboardKpiResponse(
        String code,
        String label,
        BigDecimal value,
        String displayValue,
        String helper,
        String icon,
        String tone,
        BigDecimal changePercent
) {
}

