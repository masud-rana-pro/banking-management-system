package com.sbms.security.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.security.enums.SecuritySeverityLevel;

import java.time.LocalDateTime;

public class SecurityEventResponse {

    private Long id;
    private String eventCode;
    private String eventName;
    private Long userId;
    private String username;
    private String fullName;
    private String ipAddress;
    private String deviceInfo;
    private String referenceModule;
    private Long referenceId;
    private LocalDateTime eventTime;
    private SecuritySeverityLevel severityLevel;
    private String remarks;
    private RecordStatus status;
    private boolean suspicious;

    public SecurityEventResponse(Long id, String eventCode, String eventName, Long userId, String username, String fullName,
                                 String ipAddress, String deviceInfo, String referenceModule, Long referenceId,
                                 LocalDateTime eventTime, SecuritySeverityLevel severityLevel, String remarks,
                                 RecordStatus status, boolean suspicious) {
        this.id = id;
        this.eventCode = eventCode;
        this.eventName = eventName;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
        this.referenceModule = referenceModule;
        this.referenceId = referenceId;
        this.eventTime = eventTime;
        this.severityLevel = severityLevel;
        this.remarks = remarks;
        this.status = status;
        this.suspicious = suspicious;
    }

    public Long getId() { return id; }
    public String getEventCode() { return eventCode; }
    public String getEventName() { return eventName; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getIpAddress() { return ipAddress; }
    public String getDeviceInfo() { return deviceInfo; }
    public String getReferenceModule() { return referenceModule; }
    public Long getReferenceId() { return referenceId; }
    public LocalDateTime getEventTime() { return eventTime; }
    public SecuritySeverityLevel getSeverityLevel() { return severityLevel; }
    public String getRemarks() { return remarks; }
    public RecordStatus getStatus() { return status; }
    public boolean isSuspicious() { return suspicious; }
}
