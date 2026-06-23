package com.sbms.contract.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ContractResponse {
    private final Long id;
    private final String contractNo;
    private final String contractType;
    private final Long templateId;
    private final String templateCode;
    private final String templateName;
    private final Integer templateVersionNo;
    private final Long customerId;
    private final String customerCode;
    private final String customerName;
    private final String referenceModule;
    private final Long referenceId;
    private final String contractText;
    private final String supportingDocumentName;
    private final String signedByCustomer;
    private final String signedByShariah;
    private final LocalDateTime customerSignedAt;
    private final LocalDateTime shariahSignedAt;
    private final LocalDateTime signedDate;
    private final String contractStatus;
    private final String remarks;
    private final RecordStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<ContractVersionResponse> versions;

    public ContractResponse(Long id, String contractNo, String contractType, Long templateId, String templateCode,
                            String templateName, Integer templateVersionNo, Long customerId, String customerCode,
                            String customerName, String referenceModule, Long referenceId, String contractText,
                            String supportingDocumentName,
                            String signedByCustomer, String signedByShariah, LocalDateTime customerSignedAt,
                            LocalDateTime shariahSignedAt, LocalDateTime signedDate, String contractStatus,
                            String remarks, RecordStatus status, LocalDateTime createdAt, LocalDateTime updatedAt,
                            List<ContractVersionResponse> versions) {
        this.id = id;
        this.contractNo = contractNo;
        this.contractType = contractType;
        this.templateId = templateId;
        this.templateCode = templateCode;
        this.templateName = templateName;
        this.templateVersionNo = templateVersionNo;
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.referenceModule = referenceModule;
        this.referenceId = referenceId;
        this.contractText = contractText;
        this.supportingDocumentName = supportingDocumentName;
        this.signedByCustomer = signedByCustomer;
        this.signedByShariah = signedByShariah;
        this.customerSignedAt = customerSignedAt;
        this.shariahSignedAt = shariahSignedAt;
        this.signedDate = signedDate;
        this.contractStatus = contractStatus;
        this.remarks = remarks;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.versions = versions;
    }

    public Long getId() { return id; }
    public String getContractNo() { return contractNo; }
    public String getContractType() { return contractType; }
    public Long getTemplateId() { return templateId; }
    public String getTemplateCode() { return templateCode; }
    public String getTemplateName() { return templateName; }
    public Integer getTemplateVersionNo() { return templateVersionNo; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerCode() { return customerCode; }
    public String getCustomerName() { return customerName; }
    public String getReferenceModule() { return referenceModule; }
    public Long getReferenceId() { return referenceId; }
    public String getContractText() { return contractText; }
    public String getSupportingDocumentName() { return supportingDocumentName; }
    public String getSignedByCustomer() { return signedByCustomer; }
    public String getSignedByShariah() { return signedByShariah; }
    public LocalDateTime getCustomerSignedAt() { return customerSignedAt; }
    public LocalDateTime getShariahSignedAt() { return shariahSignedAt; }
    public LocalDateTime getSignedDate() { return signedDate; }
    public String getContractStatus() { return contractStatus; }
    public String getRemarks() { return remarks; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<ContractVersionResponse> getVersions() { return versions; }
}
