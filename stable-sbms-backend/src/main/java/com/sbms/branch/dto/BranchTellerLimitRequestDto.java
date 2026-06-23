package com.sbms.branch.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BranchTellerLimitRequestDto {

    private Long branchId;
    private Long userId;
    private LocalDate limitDate;
    private BigDecimal dailyDepositLimit;
    private BigDecimal dailyWithdrawLimit;
    private BigDecimal singleTxnLimit;
    private String status;

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}