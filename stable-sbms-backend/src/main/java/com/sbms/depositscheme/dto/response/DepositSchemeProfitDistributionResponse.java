package com.sbms.depositscheme.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DepositSchemeProfitDistributionResponse {

    private final Long id;
    private final Long enrollmentId;
    private final String enrollmentNo;
    private final Integer distributionNo;
    private final LocalDate periodFrom;
    private final LocalDate periodTo;
    private final LocalDate distributionDate;
    private final BigDecimal profitAmount;
    private final String distributionStatus;
    private final Long creditedAccountId;
    private final String creditedAccountNumber;
    private final String remarks;
    private final RecordStatus status;
    private final LocalDateTime createdAt;

    public DepositSchemeProfitDistributionResponse(Long id, Long enrollmentId, String enrollmentNo, Integer distributionNo,
                                                   LocalDate periodFrom, LocalDate periodTo, LocalDate distributionDate,
                                                   BigDecimal profitAmount, String distributionStatus, Long creditedAccountId,
                                                   String creditedAccountNumber, String remarks, RecordStatus status,
                                                   LocalDateTime createdAt) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.enrollmentNo = enrollmentNo;
        this.distributionNo = distributionNo;
        this.periodFrom = periodFrom;
        this.periodTo = periodTo;
        this.distributionDate = distributionDate;
        this.profitAmount = profitAmount;
        this.distributionStatus = distributionStatus;
        this.creditedAccountId = creditedAccountId;
        this.creditedAccountNumber = creditedAccountNumber;
        this.remarks = remarks;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getEnrollmentId() { return enrollmentId; }
    public String getEnrollmentNo() { return enrollmentNo; }
    public Integer getDistributionNo() { return distributionNo; }
    public LocalDate getPeriodFrom() { return periodFrom; }
    public LocalDate getPeriodTo() { return periodTo; }
    public LocalDate getDistributionDate() { return distributionDate; }
    public BigDecimal getProfitAmount() { return profitAmount; }
    public String getDistributionStatus() { return distributionStatus; }
    public Long getCreditedAccountId() { return creditedAccountId; }
    public String getCreditedAccountNumber() { return creditedAccountNumber; }
    public String getRemarks() { return remarks; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
