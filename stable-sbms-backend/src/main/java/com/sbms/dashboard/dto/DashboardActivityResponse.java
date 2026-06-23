package com.sbms.dashboard.dto;

import java.math.BigDecimal;

public record DashboardActivityResponse(
        String title,
        String subtitle,
        String badge,
        BigDecimal amount,
        String route,
        String icon,
        String tone
) {
}

