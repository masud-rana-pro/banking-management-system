package com.sbms.financing.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FinancingAssetVerificationResponse {

    private final Long id;
    private final BigDecimal assetValue;
    private final String verificationNote;
    private final String verifiedBy;
    private final LocalDateTime verifiedAt;
    private final RecordStatus status;
    private final LocalDateTime createdAt;

    public FinancingAssetVerificationResponse(Long id, BigDecimal assetValue, String verificationNote,
                                              String verifiedBy, LocalDateTime verifiedAt,
                                              RecordStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.assetValue = assetValue;
        this.verificationNote = verificationNote;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = verifiedAt;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public BigDecimal getAssetValue() { return assetValue; }
    public String getVerificationNote() { return verificationNote; }
    public String getVerifiedBy() { return verifiedBy; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
