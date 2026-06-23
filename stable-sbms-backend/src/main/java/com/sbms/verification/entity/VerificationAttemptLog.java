package com.sbms.verification.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_attempt_log")
public class VerificationAttemptLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private OtpVerificationRequest request;

    @Column(name = "attempt_type", nullable = false, length = 40)
    private String attemptType;

    @Column(name = "attempt_value_masked", length = 160)
    private String attemptValueMasked;

    @Column(name = "attempt_status", nullable = false, length = 40)
    private String attemptStatus;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "ip_address", length = 80)
    private String ipAddress;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "created_by", length = 120)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public OtpVerificationRequest getRequest() { return request; }
    public void setRequest(OtpVerificationRequest request) { this.request = request; }
    public String getAttemptType() { return attemptType; }
    public void setAttemptType(String attemptType) { this.attemptType = attemptType; }
    public String getAttemptValueMasked() { return attemptValueMasked; }
    public void setAttemptValueMasked(String attemptValueMasked) { this.attemptValueMasked = attemptValueMasked; }
    public String getAttemptStatus() { return attemptStatus; }
    public void setAttemptStatus(String attemptStatus) { this.attemptStatus = attemptStatus; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
