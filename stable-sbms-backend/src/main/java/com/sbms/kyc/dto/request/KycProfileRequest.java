package com.sbms.kyc.dto.request;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.kyc.enums.KycReviewStatus;
import com.sbms.kyc.enums.RiskLevel;

public class KycProfileRequest {

    private Long customerId;
    private RiskLevel riskLevel;
    private String sourceOfFundsNote;
    private Boolean pepFlag;
    private Boolean sanctionFlag;
    private Boolean amlFlag;
    private KycReviewStatus reviewStatus;
    private String remarks;
    private RecordStatus status;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getSourceOfFundsNote() {
        return sourceOfFundsNote;
    }

    public void setSourceOfFundsNote(String sourceOfFundsNote) {
        this.sourceOfFundsNote = sourceOfFundsNote;
    }

    public Boolean getPepFlag() {
        return pepFlag;
    }

    public void setPepFlag(Boolean pepFlag) {
        this.pepFlag = pepFlag;
    }

    public Boolean getSanctionFlag() {
        return sanctionFlag;
    }

    public void setSanctionFlag(Boolean sanctionFlag) {
        this.sanctionFlag = sanctionFlag;
    }

    public Boolean getAmlFlag() {
        return amlFlag;
    }

    public void setAmlFlag(Boolean amlFlag) {
        this.amlFlag = amlFlag;
    }

    public KycReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(KycReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }
}
