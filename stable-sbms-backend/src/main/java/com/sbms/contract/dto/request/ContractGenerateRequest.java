package com.sbms.contract.dto.request;

public class ContractGenerateRequest {
    private Long templateId;
    private Long customerId;
    private String referenceModule;
    private Long referenceId;
    private String contractText;
    private String supportingDocumentName;
    private String remarks;
    private String generatedBy;

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getReferenceModule() { return referenceModule; }
    public void setReferenceModule(String referenceModule) { this.referenceModule = referenceModule; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public String getContractText() { return contractText; }
    public void setContractText(String contractText) { this.contractText = contractText; }
    public String getSupportingDocumentName() { return supportingDocumentName; }
    public void setSupportingDocumentName(String supportingDocumentName) { this.supportingDocumentName = supportingDocumentName; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
}
