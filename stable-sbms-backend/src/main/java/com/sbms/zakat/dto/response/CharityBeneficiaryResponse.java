package com.sbms.zakat.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public class CharityBeneficiaryResponse {
    private final Long id;
    private final String beneficiaryCode;
    private final String beneficiaryName;
    private final String mobile;
    private final String address;
    private final String proofDocumentName;
    private final RecordStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long payoutCount;

    public CharityBeneficiaryResponse(Long id, String beneficiaryCode, String beneficiaryName, String mobile, String address, String proofDocumentName,
                                      RecordStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, Long payoutCount) {
        this.id = id;
        this.beneficiaryCode = beneficiaryCode;
        this.beneficiaryName = beneficiaryName;
        this.mobile = mobile;
        this.address = address;
        this.proofDocumentName = proofDocumentName;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.payoutCount = payoutCount;
    }

    public Long getId() { return id; }
    public String getBeneficiaryCode() { return beneficiaryCode; }
    public String getBeneficiaryName() { return beneficiaryName; }
    public String getMobile() { return mobile; }
    public String getAddress() { return address; }
    public String getProofDocumentName() { return proofDocumentName; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getPayoutCount() { return payoutCount; }
}
