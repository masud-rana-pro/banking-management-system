package com.sbms.kyc.dto.response;

import com.sbms.customer.enums.CustomerStatus;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.kyc.enums.KycReviewStatus;
import com.sbms.kyc.enums.RiskLevel;

import java.time.LocalDateTime;

public class KycProfileResponse {

    private Long id;
    private Long customerId;
    private String customerCode;
    private String customerName;
    private Long branchId;
    private CustomerStatus customerStatus;
    private RiskLevel riskLevel;
    private String sourceOfFundsNote;
    private Boolean pepFlag;
    private Boolean sanctionFlag;
    private Boolean amlFlag;
    private KycReviewStatus reviewStatus;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String remarks;
    private RecordStatus status;
    private Long documentCount;
    private Long decisionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public KycProfileResponse() {
    }

    public KycProfileResponse(
            Long id,
            Long customerId,
            String customerCode,
            String customerName,
            Long branchId,
            CustomerStatus customerStatus,
            RiskLevel riskLevel,
            String sourceOfFundsNote,
            Boolean pepFlag,
            Boolean sanctionFlag,
            Boolean amlFlag,
            KycReviewStatus reviewStatus,
            String reviewedBy,
            LocalDateTime reviewedAt,
            String remarks,
            RecordStatus status,
            Long documentCount,
            Long decisionCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.branchId = branchId;
        this.customerStatus = customerStatus;
        this.riskLevel = riskLevel;
        this.sourceOfFundsNote = sourceOfFundsNote;
        this.pepFlag = pepFlag;
        this.sanctionFlag = sanctionFlag;
        this.amlFlag = amlFlag;
        this.reviewStatus = reviewStatus;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.remarks = remarks;
        this.status = status;
        this.documentCount = documentCount;
        this.decisionCount = decisionCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getBranchId() {
        return branchId;
    }

    public CustomerStatus getCustomerStatus() {
        return customerStatus;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getSourceOfFundsNote() {
        return sourceOfFundsNote;
    }

    public Boolean getPepFlag() {
        return pepFlag;
    }

    public Boolean getSanctionFlag() {
        return sanctionFlag;
    }

    public Boolean getAmlFlag() {
        return amlFlag;
    }

    public KycReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public Long getDocumentCount() {
        return documentCount;
    }

    public Long getDecisionCount() {
        return decisionCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
