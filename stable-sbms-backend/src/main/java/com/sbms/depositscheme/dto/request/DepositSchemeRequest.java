package com.sbms.depositscheme.dto.request;

import com.sbms.depositscheme.enums.DepositSchemeType;
import com.sbms.profit.enums.ProfitFrequency;

import java.math.BigDecimal;

public class DepositSchemeRequest {

    private String schemeCode;
    private String schemeName;
    private DepositSchemeType schemeType;
    private Integer tenureMonths;
    private BigDecimal minimumInstallment;
    private BigDecimal profitRatio;
    private ProfitFrequency profitFrequency;

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public DepositSchemeType getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(DepositSchemeType schemeType) {
        this.schemeType = schemeType;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public BigDecimal getMinimumInstallment() {
        return minimumInstallment;
    }

    public void setMinimumInstallment(BigDecimal minimumInstallment) {
        this.minimumInstallment = minimumInstallment;
    }

    public BigDecimal getProfitRatio() {
        return profitRatio;
    }

    public void setProfitRatio(BigDecimal profitRatio) {
        this.profitRatio = profitRatio;
    }

    public ProfitFrequency getProfitFrequency() {
        return profitFrequency;
    }

    public void setProfitFrequency(ProfitFrequency profitFrequency) {
        this.profitFrequency = profitFrequency;
    }
}
