package com.sbms.closing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MonthlyClosingRunResponse {

    private Long id;
    private String closingRef;
    private Long branchId;
    private String branchCode;
    private String branchName;
    private LocalDate closingMonth;
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private BigDecimal transactionAmount;
    private Long reversedCount;
    private BigDecimal vaultClosingBalance;
    private BigDecimal profitPosted;
    private Boolean vaultClosedConfirmed;
    private Boolean profitPostedConfirmed;
    private Boolean reversalsReviewed;
    private Boolean statementsGenerated;
    private Integer checklistCompletedCount;
    private Integer checklistTotalCount;
    private Integer checklistProgressPercent;
    private Boolean readyForSubmit;
    private List<String> outstandingChecklistItems;
    private String status;
    private String remarks;
    private String createdBy;
    private String submittedBy;
    private LocalDateTime submittedAt;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectedBy;
    private LocalDateTime rejectedAt;
    private String reopenedBy;
    private LocalDateTime reopenedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClosingRef() { return closingRef; }
    public void setClosingRef(String closingRef) { this.closingRef = closingRef; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public LocalDate getClosingMonth() { return closingMonth; }
    public void setClosingMonth(LocalDate closingMonth) { this.closingMonth = closingMonth; }
    public LocalDate getPeriodFrom() { return periodFrom; }
    public void setPeriodFrom(LocalDate periodFrom) { this.periodFrom = periodFrom; }
    public LocalDate getPeriodTo() { return periodTo; }
    public void setPeriodTo(LocalDate periodTo) { this.periodTo = periodTo; }
    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }
    public Long getReversedCount() { return reversedCount; }
    public void setReversedCount(Long reversedCount) { this.reversedCount = reversedCount; }
    public BigDecimal getVaultClosingBalance() { return vaultClosingBalance; }
    public void setVaultClosingBalance(BigDecimal vaultClosingBalance) { this.vaultClosingBalance = vaultClosingBalance; }
    public BigDecimal getProfitPosted() { return profitPosted; }
    public void setProfitPosted(BigDecimal profitPosted) { this.profitPosted = profitPosted; }
    public Boolean getVaultClosedConfirmed() { return vaultClosedConfirmed; }
    public void setVaultClosedConfirmed(Boolean vaultClosedConfirmed) { this.vaultClosedConfirmed = vaultClosedConfirmed; }
    public Boolean getProfitPostedConfirmed() { return profitPostedConfirmed; }
    public void setProfitPostedConfirmed(Boolean profitPostedConfirmed) { this.profitPostedConfirmed = profitPostedConfirmed; }
    public Boolean getReversalsReviewed() { return reversalsReviewed; }
    public void setReversalsReviewed(Boolean reversalsReviewed) { this.reversalsReviewed = reversalsReviewed; }
    public Boolean getStatementsGenerated() { return statementsGenerated; }
    public void setStatementsGenerated(Boolean statementsGenerated) { this.statementsGenerated = statementsGenerated; }
    public Integer getChecklistCompletedCount() { return checklistCompletedCount; }
    public void setChecklistCompletedCount(Integer checklistCompletedCount) { this.checklistCompletedCount = checklistCompletedCount; }
    public Integer getChecklistTotalCount() { return checklistTotalCount; }
    public void setChecklistTotalCount(Integer checklistTotalCount) { this.checklistTotalCount = checklistTotalCount; }
    public Integer getChecklistProgressPercent() { return checklistProgressPercent; }
    public void setChecklistProgressPercent(Integer checklistProgressPercent) { this.checklistProgressPercent = checklistProgressPercent; }
    public Boolean getReadyForSubmit() { return readyForSubmit; }
    public void setReadyForSubmit(Boolean readyForSubmit) { this.readyForSubmit = readyForSubmit; }
    public List<String> getOutstandingChecklistItems() { return outstandingChecklistItems; }
    public void setOutstandingChecklistItems(List<String> outstandingChecklistItems) { this.outstandingChecklistItems = outstandingChecklistItems; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public String getRejectedBy() { return rejectedBy; }
    public void setRejectedBy(String rejectedBy) { this.rejectedBy = rejectedBy; }
    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }
    public String getReopenedBy() { return reopenedBy; }
    public void setReopenedBy(String reopenedBy) { this.reopenedBy = reopenedBy; }
    public LocalDateTime getReopenedAt() { return reopenedAt; }
    public void setReopenedAt(LocalDateTime reopenedAt) { this.reopenedAt = reopenedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
