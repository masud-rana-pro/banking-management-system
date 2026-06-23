package com.sbms.verification.entity;

import com.sbms.verification.enums.ChannelType;
import com.sbms.verification.enums.VerificationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_request")
public class PasswordResetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private OtpVerificationRequest request;

    @Column(name = "identifier", nullable = false, length = 160)
    private String identifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false, length = 20)
    private ChannelType channelType;

    @Column(name = "reset_token_hash", length = 255)
    private String resetTokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "reset_status", nullable = false, length = 20)
    private VerificationStatus resetStatus = VerificationStatus.SENT;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (resetStatus == null) {
            resetStatus = VerificationStatus.SENT;
        }
    }

    public Long getId() { return id; }
    public OtpVerificationRequest getRequest() { return request; }
    public void setRequest(OtpVerificationRequest request) { this.request = request; }
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public ChannelType getChannelType() { return channelType; }
    public void setChannelType(ChannelType channelType) { this.channelType = channelType; }
    public String getResetTokenHash() { return resetTokenHash; }
    public void setResetTokenHash(String resetTokenHash) { this.resetTokenHash = resetTokenHash; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
    public VerificationStatus getResetStatus() { return resetStatus; }
    public void setResetStatus(VerificationStatus resetStatus) { this.resetStatus = resetStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
