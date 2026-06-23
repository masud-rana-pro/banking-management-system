package com.sbms.zakat.entity;

import com.sbms.zakat.enums.CharityFundSourceType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "charity_fund")
public class CharityFund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fund_date", nullable = false)
    private LocalDate fundDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    private CharityFundSourceType sourceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "credit_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO;

    @Column(name = "debit_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO;

    @Column(name = "balance_after", nullable = false, precision = 18, scale = 2)
    private BigDecimal balanceAfter = BigDecimal.ZERO;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (fundDate == null) {
            fundDate = LocalDate.now();
        }
        if (creditAmount == null) {
            creditAmount = BigDecimal.ZERO;
        }
        if (debitAmount == null) {
            debitAmount = BigDecimal.ZERO;
        }
        if (balanceAfter == null) {
            balanceAfter = BigDecimal.ZERO;
        }
    }

    public Long getId() { return id; }
    public LocalDate getFundDate() { return fundDate; }
    public void setFundDate(LocalDate fundDate) { this.fundDate = fundDate; }
    public CharityFundSourceType getSourceType() { return sourceType; }
    public void setSourceType(CharityFundSourceType sourceType) { this.sourceType = sourceType; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public BigDecimal getCreditAmount() { return creditAmount; }
    public void setCreditAmount(BigDecimal creditAmount) { this.creditAmount = creditAmount; }
    public BigDecimal getDebitAmount() { return debitAmount; }
    public void setDebitAmount(BigDecimal debitAmount) { this.debitAmount = debitAmount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
