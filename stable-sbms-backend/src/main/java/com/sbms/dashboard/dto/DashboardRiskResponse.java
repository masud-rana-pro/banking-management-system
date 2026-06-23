package com.sbms.dashboard.dto;

public record DashboardRiskResponse(
        String label,
        long value,
        String helper,
        String route,
        String icon,
        String tone
) {
}

