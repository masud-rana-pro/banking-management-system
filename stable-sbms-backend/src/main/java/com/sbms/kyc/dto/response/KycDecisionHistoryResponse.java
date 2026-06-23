package com.sbms.kyc.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.kyc.enums.KycDecision;

import java.time.LocalDateTime;

public class KycDecisionHistoryResponse {

    private Long id;
    private Long kycProfileId;
    private KycDecision decision;
    private String decisionBy;
    private LocalDateTime decisionAt;
    private String remarks;
    private RecordStatus status;

    public KycDecisionHistoryResponse() {
    }

    public KycDecisionHistoryResponse(
            Long id,
            Long kycProfileId,
            KycDecision decision,
            String decisionBy,
            LocalDateTime decisionAt,
            String remarks,
            RecordStatus status
    ) {
        this.id = id;
        this.kycProfileId = kycProfileId;
        this.decision = decision;
        this.decisionBy = decisionBy;
        this.decisionAt = decisionAt;
        this.remarks = remarks;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getKycProfileId() {
        return kycProfileId;
    }

    public KycDecision getDecision() {
        return decision;
    }

    public String getDecisionBy() {
        return decisionBy;
    }

    public LocalDateTime getDecisionAt() {
        return decisionAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public RecordStatus getStatus() {
        return status;
    }
}
