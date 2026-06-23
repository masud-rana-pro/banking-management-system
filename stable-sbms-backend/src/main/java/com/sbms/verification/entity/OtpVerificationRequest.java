package com.sbms.verification.entity;

import com.sbms.customer.entity.Customer;
import com.sbms.user.entity.User;
import com.sbms.verification.enums.ChannelType;
import com.sbms.verification.enums.TokenType;
import com.sbms.verification.enums.VerificationPurpose;
import com.sbms.verification.enums.VerificationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verification_request")
public class OtpVerificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "reference_module", length = 60)
    private String referenceModule;

    @Column(name = "reference_id")
    private Long referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 40)
    private VerificationPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false, length = 20)
    private ChannelType channelType;

    @Column(name = "contact_value", nullable = false, length = 160)
    private String contactValue;

    @Column(name = "token_code_hash", nullable = false, length = 255)
    private String tokenCodeHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 30)
    private TokenType tokenType;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    @Column(name = "max_attempt_count", nullable = false)
    private Integer maxAttemptCount = 5;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false, length = 20)
    private VerificationStatus requestStatus = VerificationStatus.PENDING;

    @Column(name = "provider_response", length = 2000)
    private String providerResponse;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (attemptCount == null) {
            attemptCount = 0;
        }
        if (maxAttemptCount == null || maxAttemptCount < 1) {
            maxAttemptCount = 5;
        }
        if (requestStatus == null) {
            requestStatus = VerificationStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getReferenceModule() { return referenceModule; }
    public void setReferenceModule(String referenceModule) { this.referenceModule = referenceModule; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public VerificationPurpose getPurpose() { return purpose; }
    public void setPurpose(VerificationPurpose purpose) { this.purpose = purpose; }
    public ChannelType getChannelType() { return channelType; }
    public void setChannelType(ChannelType channelType) { this.channelType = channelType; }
    public String getContactValue() { return contactValue; }
    public void setContactValue(String contactValue) { this.contactValue = contactValue; }
    public String getTokenCodeHash() { return tokenCodeHash; }
    public void setTokenCodeHash(String tokenCodeHash) { this.tokenCodeHash = tokenCodeHash; }
    public TokenType getTokenType() { return tokenType; }
    public void setTokenType(TokenType tokenType) { this.tokenType = tokenType; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
    public Integer getAttemptCount() { return attemptCount; }
    public void setAttemptCount(Integer attemptCount) { this.attemptCount = attemptCount; }
    public Integer getMaxAttemptCount() { return maxAttemptCount; }
    public void setMaxAttemptCount(Integer maxAttemptCount) { this.maxAttemptCount = maxAttemptCount; }
    public VerificationStatus getRequestStatus() { return requestStatus; }
    public void setRequestStatus(VerificationStatus requestStatus) { this.requestStatus = requestStatus; }
    public String getProviderResponse() { return providerResponse; }
    public void setProviderResponse(String providerResponse) { this.providerResponse = providerResponse; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
