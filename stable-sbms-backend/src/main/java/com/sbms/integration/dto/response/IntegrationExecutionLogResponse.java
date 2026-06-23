package com.sbms.integration.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.integration.enums.IntegrationExecutionStatus;
import com.sbms.integration.enums.IntegrationProviderType;

import java.time.LocalDateTime;

public record IntegrationExecutionLogResponse(
        Long id,
        Long providerId,
        String providerCode,
        String providerName,
        IntegrationProviderType providerType,
        String referenceModule,
        Long referenceId,
        String requestPayload,
        String responsePayload,
        Integer httpStatus,
        IntegrationExecutionStatus executionStatus,
        LocalDateTime executedAt,
        Integer retryCount,
        RecordStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
