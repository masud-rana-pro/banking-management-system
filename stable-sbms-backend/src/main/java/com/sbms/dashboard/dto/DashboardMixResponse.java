package com.sbms.dashboard.dto;

import java.math.BigDecimal;

public record DashboardMixResponse(
        String label,
        BigDecimal value,
        BigDecimal percentage,
        String tone
) {
}

