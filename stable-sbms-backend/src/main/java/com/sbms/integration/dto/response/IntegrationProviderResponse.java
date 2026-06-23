package com.sbms.integration.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.integration.enums.IntegrationAuthType;
import com.sbms.integration.enums.IntegrationExecutionStatus;
import com.sbms.integration.enums.IntegrationProviderType;

import java.time.LocalDateTime;

public record IntegrationProviderResponse(
        Long id,
        String providerCode,
        String providerName,
        IntegrationProviderType providerType,
        String baseUrl,
        IntegrationAuthType authType,
        String maskedApiKey,
        String username,
        String maskedPassword,
        Integer timeoutSec,
        RecordStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        IntegrationExecutionStatus lastExecutionStatus,
        LocalDateTime lastExecutedAt,
        long executionCount,
        long failureCount
) {}
