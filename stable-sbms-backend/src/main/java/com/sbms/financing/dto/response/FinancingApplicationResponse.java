package com.sbms.financing.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FinancingApplicationResponse {

    private final Long id;
    private final String applicationNo;
    private final Long customerId;
    private final String customerCode;
    private final String customerName;
    private final Long productId;
    private final String productCode;
    private final String productName;
    private final String financingType;
    private final Long branchId;
    private final BigDecimal requestedAmount;
    private final String assetDescription;
    private final String purpose;
    private final String supportingDocumentName;
    private final String applicationStatus;
    private final LocalDateTime submittedAt;
    private final String approvedBy;
    private final LocalDateTime approvedAt;
    private final String remarks;
    private final RecordStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final FinancingAssetVerificationResponse assetVerification;
    private final FinancingDisbursementResponse disbursement;
    private final List<FinancingScheduleResponse> schedules;
    private final BigDecimal totalPaidAmount;
    private final BigDecimal totalOutstandingAmount;
    private final BigDecimal totalCharityAmount;

    public FinancingApplicationResponse(
            Long id,
            String applicationNo,
            Long customerId,
            String customerCode,
            String customerName,
            Long productId,
            String productCode,
            String productName,
            String financingType,
            Long branchId,
            BigDecimal requestedAmount,
            String assetDescription,
            String purpose,
            String supportingDocumentName,
            String applicationStatus,
            LocalDateTime submittedAt,
            String approvedBy,
            LocalDateTime approvedAt,
            String remarks,
            RecordStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            FinancingAssetVerificationResponse assetVerification,
            FinancingDisbursementResponse disbursement,
            List<FinancingScheduleResponse> schedules,
            BigDecimal totalPaidAmount,
            BigDecimal totalOutstandingAmount,
            BigDecimal totalCharityAmount
    ) {
        this.id = id;
        this.applicationNo = applicationNo;
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.financingType = financingType;
        this.branchId = branchId;
        this.requestedAmount = requestedAmount;
        this.assetDescription = assetDescription;
        this.purpose = purpose;
        this.supportingDocumentName = supportingDocumentName;
        this.applicationStatus = applicationStatus;
        this.submittedAt = submittedAt;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.remarks = remarks;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.assetVerification = assetVerification;
        this.disbursement = disbursement;
        this.schedules = schedules;
        this.totalPaidAmount = totalPaidAmount;
        this.totalOutstandingAmount = totalOutstandingAmount;
        this.totalCharityAmount = totalCharityAmount;
    }

    public Long getId() { return id; }
    public String getApplicationNo() { return applicationNo; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerCode() { return customerCode; }
    public String getCustomerName() { return customerName; }
    public Long getProductId() { return productId; }
    public String getProductCode() { return productCode; }
    public String getProductName() { return productName; }
    public String getFinancingType() { return financingType; }
    public Long getBranchId() { return branchId; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public String getAssetDescription() { return assetDescription; }
    public String getPurpose() { return purpose; }
    public String getSupportingDocumentName() { return supportingDocumentName; }
    public String getApplicationStatus() { return applicationStatus; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public String getApprovedBy() { return approvedBy; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getRemarks() { return remarks; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public FinancingAssetVerificationResponse getAssetVerification() { return assetVerification; }
    public FinancingDisbursementResponse getDisbursement() { return disbursement; }
    public List<FinancingScheduleResponse> getSchedules() { return schedules; }
    public BigDecimal getTotalPaidAmount() { return totalPaidAmount; }
    public BigDecimal getTotalOutstandingAmount() { return totalOutstandingAmount; }
    public BigDecimal getTotalCharityAmount() { return totalCharityAmount; }
}
