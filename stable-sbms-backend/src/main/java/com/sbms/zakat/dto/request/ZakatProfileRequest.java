package com.sbms.zakat.dto.request;

import java.math.BigDecimal;

public class ZakatProfileRequest {
    private Long customerId;
    private Integer zakatYear;
    private BigDecimal nisabAmount;
    private BigDecimal eligibleAssetAmount;
    private String remarks;
    private String proofDocumentName;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Integer getZakatYear() { return zakatYear; }
    public void setZakatYear(Integer zakatYear) { this.zakatYear = zakatYear; }
    public BigDecimal getNisabAmount() { return nisabAmount; }
    public void setNisabAmount(BigDecimal nisabAmount) { this.nisabAmount = nisabAmount; }
    public BigDecimal getEligibleAssetAmount() { return eligibleAssetAmount; }
    public void setEligibleAssetAmount(BigDecimal eligibleAssetAmount) { this.eligibleAssetAmount = eligibleAssetAmount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getProofDocumentName() { return proofDocumentName; }
    public void setProofDocumentName(String proofDocumentName) { this.proofDocumentName = proofDocumentName; }
}
