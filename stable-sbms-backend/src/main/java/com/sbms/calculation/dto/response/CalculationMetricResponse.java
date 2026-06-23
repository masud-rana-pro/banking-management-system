package com.sbms.calculation.dto.response;

public record CalculationMetricResponse(
        String productType,
        Long usageCount
) {
}
