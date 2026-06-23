package com.sbms.branch.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "teller_limit")
public class BranchTellerLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="branch_id", nullable=false)
    private Long branchId;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="limit_date", nullable=false)
    private LocalDate limitDate;

    @Column(name="daily_deposit_limit", nullable=false, precision=18, scale=2)
    private BigDecimal dailyDepositLimit;

    @Column(name="daily_withdraw_limit", nullable=false, precision=18, scale=2)
    private BigDecimal dailyWithdrawLimit;

    @Column(name="single_txn_limit", nullable=false, precision=18, scale=2)
    private BigDecimal singleTxnLimit;

    @Column(name="approved_by")
    private Long approvedBy;

    @Column(name="approved_at")
    private LocalDateTime approvedAt;

    @Column(name="status", length=30)
    private String status = "ACTIVE";

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null || status.isBlank()) status = "ACTIVE";
        if (approvedAt == null && approvedBy != null) approvedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getLimitDate() { return limitDate; }
    public void setLimitDate(LocalDate limitDate) { this.limitDate = limitDate; }

    public BigDecimal getDailyDepositLimit() { return dailyDepositLimit; }
    public void setDailyDepositLimit(BigDecimal dailyDepositLimit) { this.dailyDepositLimit = dailyDepositLimit; }

    public BigDecimal getDailyWithdrawLimit() { return dailyWithdrawLimit; }
    public void setDailyWithdrawLimit(BigDecimal dailyWithdrawLimit) { this.dailyWithdrawLimit = dailyWithdrawLimit; }

    public BigDecimal getSingleTxnLimit() { return singleTxnLimit; }
    public void setSingleTxnLimit(BigDecimal singleTxnLimit) { this.singleTxnLimit = singleTxnLimit; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}