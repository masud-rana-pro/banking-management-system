package com.sbms.branch.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BranchTellerLimitResponseDto {

    private Long id;
    private Long branchId;
    private Long userId;
    private LocalDate limitDate;
    private BigDecimal dailyDepositLimit;
    private BigDecimal dailyWithdrawLimit;
    private BigDecimal singleTxnLimit;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}