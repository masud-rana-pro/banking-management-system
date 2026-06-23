package com.sbms.security.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;
    private String moduleName;
    private Long referenceId;
    private String actionName;
    private String oldValueJson;
    private String newValueJson;
    private String performedBy;
    private LocalDateTime performedAt;
    private RecordStatus status;

    public AuditLogResponse(Long id, String moduleName, Long referenceId, String actionName, String oldValueJson,
                            String newValueJson, String performedBy, LocalDateTime performedAt, RecordStatus status) {
        this.id = id;
        this.moduleName = moduleName;
        this.referenceId = referenceId;
        this.actionName = actionName;
        this.oldValueJson = oldValueJson;
        this.newValueJson = newValueJson;
        this.performedBy = performedBy;
        this.performedAt = performedAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getModuleName() { return moduleName; }
    public Long getReferenceId() { return referenceId; }
    public String getActionName() { return actionName; }
    public String getOldValueJson() { return oldValueJson; }
    public String getNewValueJson() { return newValueJson; }
    public String getPerformedBy() { return performedBy; }
    public LocalDateTime getPerformedAt() { return performedAt; }
    public RecordStatus getStatus() { return status; }
}
