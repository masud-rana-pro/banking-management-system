package com.sbms.verification.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.verification.enums.ChannelType;
import com.sbms.verification.enums.VerificationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_dispatch_queue")
public class VerificationDispatchQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private OtpVerificationRequest request;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false, length = 20)
    private ChannelType channelType;

    @Column(name = "provider_name", length = 120)
    private String providerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "dispatch_status", nullable = false, length = 20)
    private VerificationStatus dispatchStatus = VerificationStatus.SENT;

    @Column(name = "provider_response", length = 2000)
    private String providerResponse;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
        if (dispatchStatus == null) {
            dispatchStatus = VerificationStatus.SENT;
        }
    }

    public Long getId() { return id; }
    public OtpVerificationRequest getRequest() { return request; }
    public void setRequest(OtpVerificationRequest request) { this.request = request; }
    public ChannelType getChannelType() { return channelType; }
    public void setChannelType(ChannelType channelType) { this.channelType = channelType; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public VerificationStatus getDispatchStatus() { return dispatchStatus; }
    public void setDispatchStatus(VerificationStatus dispatchStatus) { this.dispatchStatus = dispatchStatus; }
    public String getProviderResponse() { return providerResponse; }
    public void setProviderResponse(String providerResponse) { this.providerResponse = providerResponse; }
    public LocalDateTime getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(LocalDateTime dispatchedAt) { this.dispatchedAt = dispatchedAt; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
