package com.sbms.depositscheme.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DepositSchemeEnrollmentResponse {

    private final Long id;
    private final String enrollmentNo;
    private final Long schemeId;
    private final String schemeCode;
    private final String schemeName;
    private final String schemeType;
    private final Long customerId;
    private final String customerCode;
    private final String customerName;
    private final Long linkedAccountId;
    private final String linkedAccountNumber;
    private final Long branchId;
    private final LocalDate startDate;
    private final BigDecimal installmentAmount;
    private final LocalDate maturityDate;
    private final String enrollmentStatus;
    private final BigDecimal maturityAmount;
    private final Boolean earlyWithdrawalRequested;
    private final LocalDateTime earlyWithdrawalRequestedAt;
    private final String remarks;
    private final RecordStatus status;
    private final Integer tenureMonths;
    private final BigDecimal schemeProfitRatio;
    private final String schemeProfitFrequency;
    private final Long totalScheduledInstallments;
    private final Long paidInstallments;
    private final Long remainingInstallments;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public DepositSchemeEnrollmentResponse(
            Long id,
            String enrollmentNo,
            Long schemeId,
            String schemeCode,
            String schemeName,
            String schemeType,
            Long customerId,
            String customerCode,
            String customerName,
            Long linkedAccountId,
            String linkedAccountNumber,
            Long branchId,
            LocalDate startDate,
            BigDecimal installmentAmount,
            LocalDate maturityDate,
            String enrollmentStatus,
            BigDecimal maturityAmount,
            Boolean earlyWithdrawalRequested,
            LocalDateTime earlyWithdrawalRequestedAt,
            String remarks,
            RecordStatus status,
            Integer tenureMonths,
            BigDecimal schemeProfitRatio,
            String schemeProfitFrequency,
            Long totalScheduledInstallments,
            Long paidInstallments,
            Long remainingInstallments,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.enrollmentNo = enrollmentNo;
        this.schemeId = schemeId;
        this.schemeCode = schemeCode;
        this.schemeName = schemeName;
        this.schemeType = schemeType;
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.linkedAccountId = linkedAccountId;
        this.linkedAccountNumber = linkedAccountNumber;
        this.branchId = branchId;
        this.startDate = startDate;
        this.installmentAmount = installmentAmount;
        this.maturityDate = maturityDate;
        this.enrollmentStatus = enrollmentStatus;
        this.maturityAmount = maturityAmount;
        this.earlyWithdrawalRequested = earlyWithdrawalRequested;
        this.earlyWithdrawalRequestedAt = earlyWithdrawalRequestedAt;
        this.remarks = remarks;
        this.status = status;
        this.tenureMonths = tenureMonths;
        this.schemeProfitRatio = schemeProfitRatio;
        this.schemeProfitFrequency = schemeProfitFrequency;
        this.totalScheduledInstallments = totalScheduledInstallments;
        this.paidInstallments = paidInstallments;
        this.remainingInstallments = remainingInstallments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public String getEnrollmentNo() { return enrollmentNo; }
    public Long getSchemeId() { return schemeId; }
    public String getSchemeCode() { return schemeCode; }
    public String getSchemeName() { return schemeName; }
    public String getSchemeType() { return schemeType; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerCode() { return customerCode; }
    public String getCustomerName() { return customerName; }
    public Long getLinkedAccountId() { return linkedAccountId; }
    public String getLinkedAccountNumber() { return linkedAccountNumber; }
    public Long getBranchId() { return branchId; }
    public LocalDate getStartDate() { return startDate; }
    public BigDecimal getInstallmentAmount() { return installmentAmount; }
    public LocalDate getMaturityDate() { return maturityDate; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public BigDecimal getMaturityAmount() { return maturityAmount; }
    public Boolean getEarlyWithdrawalRequested() { return earlyWithdrawalRequested; }
    public LocalDateTime getEarlyWithdrawalRequestedAt() { return earlyWithdrawalRequestedAt; }
    public String getRemarks() { return remarks; }
    public RecordStatus getStatus() { return status; }
    public Integer getTenureMonths() { return tenureMonths; }
    public BigDecimal getSchemeProfitRatio() { return schemeProfitRatio; }
    public String getSchemeProfitFrequency() { return schemeProfitFrequency; }
    public Long getTotalScheduledInstallments() { return totalScheduledInstallments; }
    public Long getPaidInstallments() { return paidInstallments; }
    public Long getRemainingInstallments() { return remainingInstallments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
