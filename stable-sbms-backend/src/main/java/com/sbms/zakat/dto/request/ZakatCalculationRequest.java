package com.sbms.zakat.dto.request;

import java.math.BigDecimal;

public class ZakatCalculationRequest {
    private Long profileId;
    private BigDecimal nisabAmount;
    private BigDecimal eligibleAssetAmount;
    private String remarks;

    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }
    public BigDecimal getNisabAmount() { return nisabAmount; }
    public void setNisabAmount(BigDecimal nisabAmount) { this.nisabAmount = nisabAmount; }
    public BigDecimal getEligibleAssetAmount() { return eligibleAssetAmount; }
    public void setEligibleAssetAmount(BigDecimal eligibleAssetAmount) { this.eligibleAssetAmount = eligibleAssetAmount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
