package com.sbms.kyc.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.kyc.enums.KycDocumentType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class KycDocumentResponse {

    private Long id;
    private Long customerId;
    private String customerCode;
    private String customerName;
    private KycDocumentType documentType;
    private String fileReferenceId;
    private String documentNo;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Boolean verifiedFlag;
    private RecordStatus status;
    private LocalDateTime createdAt;

    public KycDocumentResponse() {
    }

    public KycDocumentResponse(
            Long id,
            Long customerId,
            String customerCode,
            String customerName,
            KycDocumentType documentType,
            String fileReferenceId,
            String documentNo,
            LocalDate issueDate,
            LocalDate expiryDate,
            Boolean verifiedFlag,
            RecordStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.documentType = documentType;
        this.fileReferenceId = fileReferenceId;
        this.documentNo = documentNo;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.verifiedFlag = verifiedFlag;
        this.status = status;
        this.createdAt = createdAt;
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

    public KycDocumentType getDocumentType() {
        return documentType;
    }

    public String getFileReferenceId() {
        return fileReferenceId;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public Boolean getVerifiedFlag() {
        return verifiedFlag;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
