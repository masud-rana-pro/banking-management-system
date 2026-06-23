package com.sbms.financing.dto.request;

import java.math.BigDecimal;

public class FinancingApplicationRequest {

    private Long customerId;
    private Long productId;
    private Long branchId;
    private BigDecimal requestedAmount;
    private String assetDescription;
    private String purpose;
    private String supportingDocumentName;
    private String remarks;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
    public String getAssetDescription() { return assetDescription; }
    public void setAssetDescription(String assetDescription) { this.assetDescription = assetDescription; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getSupportingDocumentName() { return supportingDocumentName; }
    public void setSupportingDocumentName(String supportingDocumentName) { this.supportingDocumentName = supportingDocumentName; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
