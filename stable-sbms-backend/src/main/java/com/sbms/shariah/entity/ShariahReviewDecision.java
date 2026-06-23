package com.sbms.shariah.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.shariah.enums.ShariahDecisionType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shariah_review_decision")
public class ShariahReviewDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ShariahReviewCase reviewCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 40)
    private ShariahDecisionType decision;

    @Column(name = "decision_by", nullable = false, length = 160)
    private String decisionBy;

    @Column(name = "decision_at", nullable = false)
    private LocalDateTime decisionAt;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (decisionAt == null) {
            decisionAt = now;
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
    }

    public Long getId() { return id; }
    public ShariahReviewCase getReviewCase() { return reviewCase; }
    public void setReviewCase(ShariahReviewCase reviewCase) { this.reviewCase = reviewCase; }
    public ShariahDecisionType getDecision() { return decision; }
    public void setDecision(ShariahDecisionType decision) { this.decision = decision; }
    public String getDecisionBy() { return decisionBy; }
    public void setDecisionBy(String decisionBy) { this.decisionBy = decisionBy; }
    public LocalDateTime getDecisionAt() { return decisionAt; }
    public void setDecisionAt(LocalDateTime decisionAt) { this.decisionAt = decisionAt; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
