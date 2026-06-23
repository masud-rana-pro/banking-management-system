package com.sbms.kyc.dto.request;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.kyc.enums.KycDocumentType;

import java.time.LocalDate;

public class KycDocumentRequest {

    private Long customerId;
    private KycDocumentType documentType;
    private String fileReferenceId;
    private String documentNo;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Boolean verifiedFlag;
    private RecordStatus status;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public KycDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(KycDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getFileReferenceId() {
        return fileReferenceId;
    }

    public void setFileReferenceId(String fileReferenceId) {
        this.fileReferenceId = fileReferenceId;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getVerifiedFlag() {
        return verifiedFlag;
    }

    public void setVerifiedFlag(Boolean verifiedFlag) {
        this.verifiedFlag = verifiedFlag;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }
}
