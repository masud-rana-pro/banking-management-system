package com.sbms.financing.dto.response;

public class FinancingProductMetricResponse {

    private final String label;
    private final Long count;

    public FinancingProductMetricResponse(String label, Long count) {
        this.label = label;
        this.count = count;
    }

    public String getLabel() { return label; }
    public Long getCount() { return count; }
}
