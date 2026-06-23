package com.sbms.profit.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProfitRatioRequest {

    private String ratioCode;
    private Long accountTypeId;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal ratioPercent;

    public String getRatioCode() {
        return ratioCode;
    }

    public void setRatioCode(String ratioCode) {
        this.ratioCode = ratioCode;
    }

    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Long accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public BigDecimal getRatioPercent() {
        return ratioPercent;
    }

    public void setRatioPercent(BigDecimal ratioPercent) {
        this.ratioPercent = ratioPercent;
    }
}
