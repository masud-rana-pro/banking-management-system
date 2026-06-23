package com.sbms.shariah.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ShariahReviewCaseResponse {
    private final Long id;
    private final String caseNo;
    private final String referenceModule;
    private final Long referenceId;
    private final String submittedBy;
    private final LocalDateTime submittedAt;
    private final String caseStatus;
    private final String remarks;
    private final RecordStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<ShariahChecklistItemResponse> checklistItems;
    private final List<ShariahReviewDecisionResponse> history;

    public ShariahReviewCaseResponse(Long id, String caseNo, String referenceModule, Long referenceId,
                                     String submittedBy, LocalDateTime submittedAt, String caseStatus, String remarks,
                                     RecordStatus status, LocalDateTime createdAt, LocalDateTime updatedAt,
                                     List<ShariahChecklistItemResponse> checklistItems,
                                     List<ShariahReviewDecisionResponse> history) {
        this.id = id;
        this.caseNo = caseNo;
        this.referenceModule = referenceModule;
        this.referenceId = referenceId;
        this.submittedBy = submittedBy;
        this.submittedAt = submittedAt;
        this.caseStatus = caseStatus;
        this.remarks = remarks;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.checklistItems = checklistItems;
        this.history = history;
    }

    public Long getId() { return id; }
    public String getCaseNo() { return caseNo; }
    public String getReferenceModule() { return referenceModule; }
    public Long getReferenceId() { return referenceId; }
    public String getSubmittedBy() { return submittedBy; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public String getCaseStatus() { return caseStatus; }
    public String getRemarks() { return remarks; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<ShariahChecklistItemResponse> getChecklistItems() { return checklistItems; }
    public List<ShariahReviewDecisionResponse> getHistory() { return history; }
}
