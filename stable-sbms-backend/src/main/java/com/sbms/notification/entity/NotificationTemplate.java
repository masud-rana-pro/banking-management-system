package com.sbms.notification.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.notification.enums.NotificationChannelType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_template", uniqueConstraints = {
        @UniqueConstraint(name = "uk_notification_template_code", columnNames = "template_code")
})
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_code", nullable = false, length = 40)
    private String templateCode;

    @Column(name = "template_name", nullable = false, length = 160)
    private String templateName;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false, length = 20)
    private NotificationChannelType channelType;

    @Column(name = "subject_text", length = 255)
    private String subjectText;

    @Lob
    @Column(name = "body_text", nullable = false, columnDefinition = "LONGTEXT")
    private String bodyText;

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
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public NotificationChannelType getChannelType() { return channelType; }
    public void setChannelType(NotificationChannelType channelType) { this.channelType = channelType; }
    public String getSubjectText() { return subjectText; }
    public void setSubjectText(String subjectText) { this.subjectText = subjectText; }
    public String getBodyText() { return bodyText; }
    public void setBodyText(String bodyText) { this.bodyText = bodyText; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
