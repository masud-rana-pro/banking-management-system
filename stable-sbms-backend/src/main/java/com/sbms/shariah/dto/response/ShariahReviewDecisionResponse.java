package com.sbms.shariah.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public class ShariahReviewDecisionResponse {
    private final Long id;
    private final Long caseId;
    private final String caseNo;
    private final String decision;
    private final String decisionBy;
    private final LocalDateTime decisionAt;
    private final String remarks;
    private final RecordStatus status;
    private final LocalDateTime createdAt;

    public ShariahReviewDecisionResponse(Long id, Long caseId, String caseNo, String decision, String decisionBy,
                                         LocalDateTime decisionAt, String remarks, RecordStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.caseId = caseId;
        this.caseNo = caseNo;
        this.decision = decision;
        this.decisionBy = decisionBy;
        this.decisionAt = decisionAt;
        this.remarks = remarks;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getCaseId() { return caseId; }
    public String getCaseNo() { return caseNo; }
    public String getDecision() { return decision; }
    public String getDecisionBy() { return decisionBy; }
    public LocalDateTime getDecisionAt() { return decisionAt; }
    public String getRemarks() { return remarks; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
