package com.sbms.workflow.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;
import java.util.List;

public class WorkflowHistoryResponse {

    private Long id;
    private String moduleName;
    private Long referenceId;
    private String actionName;
    private String fromStatus;
    private String toStatus;
    private String actionBy;
    private LocalDateTime actionAt;
    private String remarks;
    private RecordStatus status;
    private List<WorkflowCommentResponse> comments;

    public WorkflowHistoryResponse(Long id, String moduleName, Long referenceId, String actionName, String fromStatus,
                                   String toStatus, String actionBy, LocalDateTime actionAt, String remarks,
                                   RecordStatus status, List<WorkflowCommentResponse> comments) {
        this.id = id;
        this.moduleName = moduleName;
        this.referenceId = referenceId;
        this.actionName = actionName;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.actionBy = actionBy;
        this.actionAt = actionAt;
        this.remarks = remarks;
        this.status = status;
        this.comments = comments;
    }

    public Long getId() { return id; }
    public String getModuleName() { return moduleName; }
    public Long getReferenceId() { return referenceId; }
    public String getActionName() { return actionName; }
    public String getFromStatus() { return fromStatus; }
    public String getToStatus() { return toStatus; }
    public String getActionBy() { return actionBy; }
    public LocalDateTime getActionAt() { return actionAt; }
    public String getRemarks() { return remarks; }
    public RecordStatus getStatus() { return status; }
    public List<WorkflowCommentResponse> getComments() { return comments; }
}
