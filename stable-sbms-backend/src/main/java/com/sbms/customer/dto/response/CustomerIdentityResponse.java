package com.sbms.customer.dto.response;

import com.sbms.customer.enums.DocumentType;
import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerIdentityResponse {

    private Long id;
    private Long customerId;
    private String customerCode;
    private String customerName;
    private DocumentType documentType;
    private String documentNo;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String issueCountry;
    private String imageFileName;
    private Boolean verifiedFlag;
    private RecordStatus status;
    private LocalDateTime createdAt;

    public CustomerIdentityResponse() {
    }

    public CustomerIdentityResponse(Long id, Long customerId, String customerCode, String customerName,
                                    DocumentType documentType, String documentNo, LocalDate issueDate,
                                    LocalDate expiryDate, String issueCountry, String imageFileName, Boolean verifiedFlag,
                                    RecordStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.documentType = documentType;
        this.documentNo = documentNo;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.issueCountry = issueCountry;
        this.imageFileName = imageFileName;
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

    public DocumentType getDocumentType() {
        return documentType;
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

    public String getIssueCountry() {
        return issueCountry;
    }

    public String getImageFileName() {
        return imageFileName;
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
