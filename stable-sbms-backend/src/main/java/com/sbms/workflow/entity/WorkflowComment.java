package com.sbms.workflow.entity;

import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_comment")
public class WorkflowComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_name", nullable = false, length = 80)
    private String moduleName;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "comment_text", nullable = false, length = 1000)
    private String commentText;

    @Column(name = "comment_by", nullable = false, length = 120)
    private String commentBy;

    @Column(name = "comment_at", nullable = false)
    private LocalDateTime commentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @PrePersist
    public void prePersist() {
        if (commentAt == null) {
            commentAt = LocalDateTime.now();
        }
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
    }

    public Long getId() { return id; }
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
    public String getCommentBy() { return commentBy; }
    public void setCommentBy(String commentBy) { this.commentBy = commentBy; }
    public LocalDateTime getCommentAt() { return commentAt; }
    public void setCommentAt(LocalDateTime commentAt) { this.commentAt = commentAt; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
}
