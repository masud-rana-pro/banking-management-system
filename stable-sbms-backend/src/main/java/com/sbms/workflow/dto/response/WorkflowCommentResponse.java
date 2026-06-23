package com.sbms.workflow.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public class WorkflowCommentResponse {

    private Long id;
    private String moduleName;
    private Long referenceId;
    private String commentText;
    private String commentBy;
    private LocalDateTime commentAt;
    private RecordStatus status;

    public WorkflowCommentResponse(Long id, String moduleName, Long referenceId, String commentText, String commentBy,
                                   LocalDateTime commentAt, RecordStatus status) {
        this.id = id;
        this.moduleName = moduleName;
        this.referenceId = referenceId;
        this.commentText = commentText;
        this.commentBy = commentBy;
        this.commentAt = commentAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getModuleName() { return moduleName; }
    public Long getReferenceId() { return referenceId; }
    public String getCommentText() { return commentText; }
    public String getCommentBy() { return commentBy; }
    public LocalDateTime getCommentAt() { return commentAt; }
    public RecordStatus getStatus() { return status; }
}
