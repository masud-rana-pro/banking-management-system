package com.sbms.zakat.dto.request;

public class CharityBeneficiaryRequest {
    private String beneficiaryCode;
    private String beneficiaryName;
    private String mobile;
    private String address;
    private String proofDocumentName;

    public String getBeneficiaryCode() { return beneficiaryCode; }
    public void setBeneficiaryCode(String beneficiaryCode) { this.beneficiaryCode = beneficiaryCode; }
    public String getBeneficiaryName() { return beneficiaryName; }
    public void setBeneficiaryName(String beneficiaryName) { this.beneficiaryName = beneficiaryName; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getProofDocumentName() { return proofDocumentName; }
    public void setProofDocumentName(String proofDocumentName) { this.proofDocumentName = proofDocumentName; }
}
