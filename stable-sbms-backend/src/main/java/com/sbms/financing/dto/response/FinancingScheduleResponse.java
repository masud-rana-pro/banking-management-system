package com.sbms.financing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FinancingScheduleResponse {

    private final Long id;
    private final Long applicationId;
    private final String applicationNo;
    private final Integer installmentNo;
    private final LocalDate dueDate;
    private final BigDecimal principalAmount;
    private final BigDecimal profitAmount;
    private final BigDecimal charityAmount;
    private final BigDecimal paidAmount;
    private final LocalDate paidDate;
    private final String scheduleStatus;
    private final LocalDateTime createdAt;

    public FinancingScheduleResponse(Long id, Long applicationId, String applicationNo, Integer installmentNo,
                                     LocalDate dueDate, BigDecimal principalAmount, BigDecimal profitAmount,
                                     BigDecimal charityAmount, BigDecimal paidAmount, LocalDate paidDate,
                                     String scheduleStatus, LocalDateTime createdAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.applicationNo = applicationNo;
        this.installmentNo = installmentNo;
        this.dueDate = dueDate;
        this.principalAmount = principalAmount;
        this.profitAmount = profitAmount;
        this.charityAmount = charityAmount;
        this.paidAmount = paidAmount;
        this.paidDate = paidDate;
        this.scheduleStatus = scheduleStatus;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getApplicationId() { return applicationId; }
    public String getApplicationNo() { return applicationNo; }
    public Integer getInstallmentNo() { return installmentNo; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public BigDecimal getProfitAmount() { return profitAmount; }
    public BigDecimal getCharityAmount() { return charityAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public LocalDate getPaidDate() { return paidDate; }
    public String getScheduleStatus() { return scheduleStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
