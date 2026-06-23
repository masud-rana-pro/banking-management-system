package com.sbms.integration.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.integration.enums.IntegrationAuthType;
import com.sbms.integration.enums.IntegrationProviderType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "integration_provider", uniqueConstraints = {
        @UniqueConstraint(name = "uk_integration_provider_code", columnNames = "provider_code")
})
public class IntegrationProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_code", nullable = false, length = 40)
    private String providerCode;

    @Column(name = "provider_name", nullable = false, length = 160)
    private String providerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false, length = 30)
    private IntegrationProviderType providerType;

    @Column(name = "base_url", nullable = false, length = 255)
    private String baseUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false, length = 30)
    private IntegrationAuthType authType;

    @Column(name = "api_key", length = 255)
    private String apiKey;

    @Column(name = "username", length = 120)
    private String username;

    @Column(name = "password_enc", length = 255)
    private String passwordEnc;

    @Column(name = "timeout_sec", nullable = false)
    private Integer timeoutSec;

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
    public String getProviderCode() { return providerCode; }
    public void setProviderCode(String providerCode) { this.providerCode = providerCode; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public IntegrationProviderType getProviderType() { return providerType; }
    public void setProviderType(IntegrationProviderType providerType) { this.providerType = providerType; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public IntegrationAuthType getAuthType() { return authType; }
    public void setAuthType(IntegrationAuthType authType) { this.authType = authType; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordEnc() { return passwordEnc; }
    public void setPasswordEnc(String passwordEnc) { this.passwordEnc = passwordEnc; }
    public Integer getTimeoutSec() { return timeoutSec; }
    public void setTimeoutSec(Integer timeoutSec) { this.timeoutSec = timeoutSec; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
