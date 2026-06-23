package com.sbms.security.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.security.enums.SecuritySeverityLevel;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_event_log")
public class SecurityEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_code", nullable = false, length = 50)
    private String eventCode;

    @Column(name = "event_name", nullable = false, length = 160)
    private String eventName;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "ip_address", length = 80)
    private String ipAddress;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "reference_module", length = 80)
    private String referenceModule;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity_level", nullable = false, length = 20)
    private SecuritySeverityLevel severityLevel = SecuritySeverityLevel.LOW;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @PrePersist
    public void prePersist() {
        if (eventTime == null) {
            eventTime = LocalDateTime.now();
        }
        if (severityLevel == null) {
            severityLevel = SecuritySeverityLevel.LOW;
        }
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
    }

    public Long getId() { return id; }
    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    public String getReferenceModule() { return referenceModule; }
    public void setReferenceModule(String referenceModule) { this.referenceModule = referenceModule; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
    public SecuritySeverityLevel getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(SecuritySeverityLevel severityLevel) { this.severityLevel = severityLevel; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
}
