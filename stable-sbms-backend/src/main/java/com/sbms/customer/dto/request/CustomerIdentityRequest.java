package com.sbms.customer.dto.request;

import com.sbms.customer.enums.DocumentType;
import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDate;

public class CustomerIdentityRequest {

    private Long customerId;
    private DocumentType documentType;
    private String documentNo;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String issueCountry;
    private String imageFileName;
    private Boolean verifiedFlag;
    private RecordStatus status;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
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

    public String getIssueCountry() {
        return issueCountry;
    }

    public void setIssueCountry(String issueCountry) {
        this.issueCountry = issueCountry;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
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
