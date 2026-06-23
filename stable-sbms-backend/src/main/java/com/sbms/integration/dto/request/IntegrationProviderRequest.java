package com.sbms.integration.dto.request;

import com.sbms.integration.enums.IntegrationAuthType;
import com.sbms.integration.enums.IntegrationProviderType;

public class IntegrationProviderRequest {

    private String providerCode;
    private String providerName;
    private IntegrationProviderType providerType;
    private String baseUrl;
    private IntegrationAuthType authType;
    private String apiKey;
    private String username;
    private String password;
    private Integer timeoutSec;

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
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getTimeoutSec() { return timeoutSec; }
    public void setTimeoutSec(Integer timeoutSec) { this.timeoutSec = timeoutSec; }
}
