package com.sbms.shariah.entity;

import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shariah_review_checklist")
public class ShariahReviewChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ShariahReviewCase reviewCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ShariahChecklistItem checklistItem;

    @Column(name = "selected_flag", nullable = false)
    private Boolean selectedFlag = Boolean.TRUE;

    @Column(name = "note", length = 1000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
        if (selectedFlag == null) {
            selectedFlag = Boolean.TRUE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public ShariahReviewCase getReviewCase() { return reviewCase; }
    public void setReviewCase(ShariahReviewCase reviewCase) { this.reviewCase = reviewCase; }
    public ShariahChecklistItem getChecklistItem() { return checklistItem; }
    public void setChecklistItem(ShariahChecklistItem checklistItem) { this.checklistItem = checklistItem; }
    public Boolean getSelectedFlag() { return selectedFlag; }
    public void setSelectedFlag(Boolean selectedFlag) { this.selectedFlag = selectedFlag; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
