package com.sbms.contract.dto.request;

public class ContractSignRequest {
    private String signedBy;
    private String remarks;

    public String getSignedBy() { return signedBy; }
    public void setSignedBy(String signedBy) { this.signedBy = signedBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
