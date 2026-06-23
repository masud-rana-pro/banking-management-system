package com.sbms.notification.entity;

import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_event", uniqueConstraints = {
        @UniqueConstraint(name = "uk_notification_event_code", columnNames = "event_code")
})
public class NotificationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_code", nullable = false, length = 60)
    private String eventCode;

    @Column(name = "event_name", nullable = false, length = 180)
    private String eventName;

    @Column(name = "reference_module", length = 80)
    private String referenceModule;

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
        if (createdAt == null) createdAt = now;
        updatedAt = now;
        if (status == null) status = RecordStatus.ACTIVE;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getReferenceModule() { return referenceModule; }
    public void setReferenceModule(String referenceModule) { this.referenceModule = referenceModule; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
