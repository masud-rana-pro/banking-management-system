package com.sbms.zakat.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CharityFundResponse {
    private final Long id;
    private final LocalDate fundDate;
    private final String sourceType;
    private final Long referenceId;
    private final BigDecimal creditAmount;
    private final BigDecimal debitAmount;
    private final BigDecimal balanceAfter;
    private final String remarks;
    private final LocalDateTime createdAt;

    public CharityFundResponse(Long id, LocalDate fundDate, String sourceType, Long referenceId, BigDecimal creditAmount,
                               BigDecimal debitAmount, BigDecimal balanceAfter, String remarks, LocalDateTime createdAt) {
        this.id = id;
        this.fundDate = fundDate;
        this.sourceType = sourceType;
        this.referenceId = referenceId;
        this.creditAmount = creditAmount;
        this.debitAmount = debitAmount;
        this.balanceAfter = balanceAfter;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public LocalDate getFundDate() { return fundDate; }
    public String getSourceType() { return sourceType; }
    public Long getReferenceId() { return referenceId; }
    public BigDecimal getCreditAmount() { return creditAmount; }
    public BigDecimal getDebitAmount() { return debitAmount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getRemarks() { return remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
