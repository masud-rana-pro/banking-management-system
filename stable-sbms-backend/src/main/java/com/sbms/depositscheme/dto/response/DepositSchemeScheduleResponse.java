package com.sbms.depositscheme.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DepositSchemeScheduleResponse {

    private final Long id;
    private final Long enrollmentId;
    private final String enrollmentNo;
    private final Integer installmentNo;
    private final LocalDate dueDate;
    private final BigDecimal installmentAmount;
    private final BigDecimal profitAmount;
    private final BigDecimal totalDueAmount;
    private final String paymentStatus;
    private final LocalDateTime paidAt;
    private final RecordStatus status;
    private final LocalDateTime createdAt;

    public DepositSchemeScheduleResponse(Long id, Long enrollmentId, String enrollmentNo, Integer installmentNo,
                                         LocalDate dueDate, BigDecimal installmentAmount, BigDecimal profitAmount,
                                         BigDecimal totalDueAmount, String paymentStatus, LocalDateTime paidAt,
                                         RecordStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.enrollmentNo = enrollmentNo;
        this.installmentNo = installmentNo;
        this.dueDate = dueDate;
        this.installmentAmount = installmentAmount;
        this.profitAmount = profitAmount;
        this.totalDueAmount = totalDueAmount;
        this.paymentStatus = paymentStatus;
        this.paidAt = paidAt;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getEnrollmentId() { return enrollmentId; }
    public String getEnrollmentNo() { return enrollmentNo; }
    public Integer getInstallmentNo() { return installmentNo; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getInstallmentAmount() { return installmentAmount; }
    public BigDecimal getProfitAmount() { return profitAmount; }
    public BigDecimal getTotalDueAmount() { return totalDueAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
