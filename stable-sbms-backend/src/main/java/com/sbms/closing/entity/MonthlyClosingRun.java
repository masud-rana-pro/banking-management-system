package com.sbms.closing.entity;

import com.sbms.closing.enums.MonthlyClosingStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "monthly_closing_run",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_monthly_closing_branch_month", columnNames = {"branch_id", "closing_month"})
        }
)
public class MonthlyClosingRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "closing_ref", nullable = false, unique = true, length = 40)
    private String closingRef;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "branch_code", nullable = false, length = 40)
    private String branchCode;

    @Column(name = "branch_name", nullable = false, length = 160)
    private String branchName;

    @Column(name = "closing_month", nullable = false)
    private LocalDate closingMonth;

    @Column(name = "period_from", nullable = false)
    private LocalDate periodFrom;

    @Column(name = "period_to", nullable = false)
    private LocalDate periodTo;

    @Column(name = "transaction_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal transactionAmount = BigDecimal.ZERO;

    @Column(name = "reversed_count", nullable = false)
    private Long reversedCount = 0L;

    @Column(name = "vault_closing_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal vaultClosingBalance = BigDecimal.ZERO;

    @Column(name = "profit_posted", nullable = false, precision = 18, scale = 2)
    private BigDecimal profitPosted = BigDecimal.ZERO;

    @Column(name = "vault_closed_confirmed", nullable = false)
    private Boolean vaultClosedConfirmed = Boolean.FALSE;

    @Column(name = "profit_posted_confirmed", nullable = false)
    private Boolean profitPostedConfirmed = Boolean.FALSE;

    @Column(name = "reversals_reviewed", nullable = false)
    private Boolean reversalsReviewed = Boolean.FALSE;

    @Column(name = "statements_generated", nullable = false)
    private Boolean statementsGenerated = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private MonthlyClosingStatus status = MonthlyClosingStatus.DRAFT;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "created_by", nullable = false, length = 120)
    private String createdBy;

    @Column(name = "submitted_by", length = 120)
    private String submittedBy;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_by", length = 120)
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_by", length = 120)
    private String rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "reopened_by", length = 120)
    private String reopenedBy;

    @Column(name = "reopened_at")
    private LocalDateTime reopenedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (status == null) {
            status = MonthlyClosingStatus.DRAFT;
        }
        if (transactionAmount == null) {
            transactionAmount = BigDecimal.ZERO;
        }
        if (vaultClosingBalance == null) {
            vaultClosingBalance = BigDecimal.ZERO;
        }
        if (profitPosted == null) {
            profitPosted = BigDecimal.ZERO;
        }
        if (reversedCount == null) {
            reversedCount = 0L;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
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
    public MonthlyClosingStatus getStatus() { return status; }
    public void setStatus(MonthlyClosingStatus status) { this.status = status; }
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
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
