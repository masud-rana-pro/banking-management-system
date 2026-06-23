package com.sbms.financing.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FinancingProductResponse {

    private final Long id;
    private final String productCode;
    private final String productName;
    private final String financingType;
    private final BigDecimal minimumAmount;
    private final BigDecimal maximumAmount;
    private final Integer tenureMonths;
    private final String profitRule;
    private final RecordStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long applicationCount;

    public FinancingProductResponse(Long id, String productCode, String productName, String financingType,
                                    BigDecimal minimumAmount, BigDecimal maximumAmount, Integer tenureMonths,
                                    String profitRule, RecordStatus status, LocalDateTime createdAt,
                                    LocalDateTime updatedAt, Long applicationCount) {
        this.id = id;
        this.productCode = productCode;
        this.productName = productName;
        this.financingType = financingType;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.tenureMonths = tenureMonths;
        this.profitRule = profitRule;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.applicationCount = applicationCount;
    }

    public Long getId() { return id; }
    public String getProductCode() { return productCode; }
    public String getProductName() { return productName; }
    public String getFinancingType() { return financingType; }
    public BigDecimal getMinimumAmount() { return minimumAmount; }
    public BigDecimal getMaximumAmount() { return maximumAmount; }
    public Integer getTenureMonths() { return tenureMonths; }
    public String getProfitRule() { return profitRule; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getApplicationCount() { return applicationCount; }
}
