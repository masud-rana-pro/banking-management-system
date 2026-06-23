package com.sbms.financing.dto.request;

import com.sbms.financing.enums.FinancingType;

import java.math.BigDecimal;

public class FinancingProductRequest {

    private String productCode;
    private String productName;
    private FinancingType financingType;
    private BigDecimal minimumAmount;
    private BigDecimal maximumAmount;
    private Integer tenureMonths;
    private String profitRule;

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public FinancingType getFinancingType() { return financingType; }
    public void setFinancingType(FinancingType financingType) { this.financingType = financingType; }
    public BigDecimal getMinimumAmount() { return minimumAmount; }
    public void setMinimumAmount(BigDecimal minimumAmount) { this.minimumAmount = minimumAmount; }
    public BigDecimal getMaximumAmount() { return maximumAmount; }
    public void setMaximumAmount(BigDecimal maximumAmount) { this.maximumAmount = maximumAmount; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
    public String getProfitRule() { return profitRule; }
    public void setProfitRule(String profitRule) { this.profitRule = profitRule; }
}
