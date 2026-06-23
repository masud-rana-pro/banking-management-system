package com.sbms.accounting.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class GlJournalResponse {
    private Long id;
    private LocalDate journalDate;
    private String journalType;
    private String sourceType;
    private Long sourceReferenceId;
    private String sourceReferenceNo;
    private Long branchId;
    private String description;
    private RecordStatus status;
    private String createdBy;
    private LocalDateTime createdAt;
    private List<GlJournalLineResponse> lines;

    public GlJournalResponse(Long id, LocalDate journalDate, String journalType, String sourceType, Long sourceReferenceId,
                             String sourceReferenceNo, Long branchId, String description, RecordStatus status,
                             String createdBy, LocalDateTime createdAt, List<GlJournalLineResponse> lines) {
        this.id = id;
        this.journalDate = journalDate;
        this.journalType = journalType;
        this.sourceType = sourceType;
        this.sourceReferenceId = sourceReferenceId;
        this.sourceReferenceNo = sourceReferenceNo;
        this.branchId = branchId;
        this.description = description;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.lines = lines;
    }

    public Long getId() { return id; }
    public LocalDate getJournalDate() { return journalDate; }
    public String getJournalType() { return journalType; }
    public String getSourceType() { return sourceType; }
    public Long getSourceReferenceId() { return sourceReferenceId; }
    public String getSourceReferenceNo() { return sourceReferenceNo; }
    public Long getBranchId() { return branchId; }
    public String getDescription() { return description; }
    public RecordStatus getStatus() { return status; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<GlJournalLineResponse> getLines() { return lines; }
}
