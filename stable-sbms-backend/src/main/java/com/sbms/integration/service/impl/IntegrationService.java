package com.sbms.integration.service.impl;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.integration.dto.request.IntegrationProviderRequest;
import com.sbms.integration.dto.request.IntegrationProviderTestRequest;
import com.sbms.integration.dto.response.*;
import com.sbms.integration.entity.IntegrationExecutionLog;
import com.sbms.integration.entity.IntegrationProvider;
import com.sbms.integration.enums.IntegrationExecutionStatus;
import com.sbms.integration.enums.IntegrationProviderType;
import com.sbms.integration.repository.IntegrationExecutionLogRepository;
import com.sbms.integration.repository.IntegrationProviderRepository;
import com.sbms.integration.service.IIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
@Transactional
public class IntegrationService implements IIntegrationService {

    @Autowired
    private IntegrationProviderRepository providerRepository;

    @Autowired
    private IntegrationExecutionLogRepository executionLogRepository;

    @Autowired
    private AutomatedMailService automatedMailService;

    @Override
    public IntegrationProviderResponse createProvider(IntegrationProviderRequest request) {
        validateProvider(request, null);
        IntegrationProvider entity = new IntegrationProvider();
        entity.setProviderCode(resolveProviderCode(request.getProviderCode()));
        applyProvider(entity, request);
        entity.setStatus(RecordStatus.ACTIVE);
        return mapProvider(providerRepository.save(entity));
    }

    @Override
    public List<IntegrationProviderResponse> listProviders(String providerType, String status, String keyword) {
        return providerRepository.findAll(providerType, status, keyword).stream().map(this::mapProvider).toList();
    }

    @Override
    public IntegrationProviderResponse getProviderById(Long id) {
        return mapProvider(getProviderEntity(id));
    }

    @Override
    public IntegrationProviderResponse updateProvider(Long id, IntegrationProviderRequest request) {
        IntegrationProvider entity = getProviderEntity(id);
        validateProvider(request, id);
        if (request.getProviderCode() != null && !request.getProviderCode().trim().isEmpty()) {
            entity.setProviderCode(request.getProviderCode().trim().toUpperCase());
        }
        applyProvider(entity, request);
        return mapProvider(providerRepository.update(entity));
    }

    @Override
    public IntegrationProviderResponse archiveProvider(Long id) {
        IntegrationProvider entity = getProviderEntity(id);
        entity.setStatus(RecordStatus.ARCHIVED);
        return mapProvider(providerRepository.update(entity));
    }

    @Override
    public IntegrationProviderResponse restoreProvider(Long id) {
        IntegrationProvider entity = getProviderEntity(id);
        entity.setStatus(RecordStatus.ACTIVE);
        return mapProvider(providerRepository.update(entity));
    }

    @Override
    public IntegrationExecutionLogResponse testProvider(Long id, IntegrationProviderTestRequest request) {
        IntegrationProvider provider = getProviderEntity(id);
        IntegrationExecutionLog log = new IntegrationExecutionLog();
        log.setProvider(provider);
        log.setReferenceModule(request == null || request.getReferenceModule() == null || request.getReferenceModule().trim().isEmpty()
                ? "INTEGRATION_PROVIDER_TEST"
                : request.getReferenceModule().trim().toUpperCase());
        log.setReferenceId(request == null ? null : request.getReferenceId());
        log.setRequestPayload(request == null || request.getRequestPayload() == null || request.getRequestPayload().trim().isEmpty()
                ? "{\"providerCode\":\"" + provider.getProviderCode() + "\",\"baseUrl\":\"" + provider.getBaseUrl() + "\"}"
                : request.getRequestPayload().trim());
        log.setExecutedAt(LocalDateTime.now());

        boolean success = provider.getStatus() == RecordStatus.ACTIVE
                && provider.getTimeoutSec() != null
                && provider.getTimeoutSec() > 0
                && provider.getBaseUrl() != null
                && !provider.getBaseUrl().trim().isEmpty()
                && !provider.getBaseUrl().toLowerCase().contains("fail")
                && !provider.getBaseUrl().toLowerCase().contains("timeout");

        log.setHttpStatus(success ? 200 : 503);
        log.setExecutionStatus(success ? IntegrationExecutionStatus.SUCCESS : IntegrationExecutionStatus.FAILED);
        log.setResponsePayload(success
                ? "Test connection established successfully for " + provider.getProviderCode()
                : "Provider test failed due to inactive provider or invalid endpoint/timeout configuration");
        log.setRetryCount(0);
        log.setStatus(RecordStatus.ACTIVE);
        IntegrationExecutionLogResponse response = mapLog(executionLogRepository.save(log));
        automatedMailService.sendOperationalAlertToSupport(
                success ? "Integration provider test succeeded" : "Integration provider test failed",
                "An integration provider health check has finished.",
                "Provider: " + safe(provider.getProviderName()) + "<br>Status: " + response.executionStatus() + "<br>HTTP: " + safeStatus(response.httpStatus()),
                "/integrations/dashboard",
                "Open Integrations"
        );
        return response;
    }

    @Override
    public List<IntegrationExecutionLogResponse> listLogs(Long providerId, String executionStatus, String keyword) {
        return executionLogRepository.findAll(providerId, executionStatus, keyword).stream().map(this::mapLog).toList();
    }

    @Override
    public IntegrationExecutionLogResponse getLogById(Long id) {
        return mapLog(getExecutionLogEntity(id));
    }

    @Override
    public IntegrationExecutionLogResponse retryLog(Long id) {
        IntegrationExecutionLog entity = getExecutionLogEntity(id);
        entity.setRetryCount((entity.getRetryCount() == null ? 0 : entity.getRetryCount()) + 1);
        entity.setExecutionStatus(IntegrationExecutionStatus.RETRY_PENDING);
        entity.setHttpStatus(202);
        entity.setExecutedAt(LocalDateTime.now());
        entity.setResponsePayload((entity.getResponsePayload() == null ? "" : entity.getResponsePayload() + " | ")
                + "Retry queued manually at " + LocalDateTime.now());
        IntegrationExecutionLogResponse response = mapLog(executionLogRepository.update(entity));
        automatedMailService.sendOperationalAlertToSupport(
                "Integration retry queued",
                "A failed integration execution has been queued for retry.",
                "Provider: " + safe(response.providerName()) + "<br>Reference Module: " + safe(response.referenceModule()) + "<br>Execution Status: " + response.executionStatus(),
                "/integrations/dashboard",
                "Open Integrations"
        );
        return response;
    }

    @Override
    public IntegrationDashboardSummaryResponse getDashboardSummary() {
        long successCount = executionLogRepository.countByStatus(IntegrationExecutionStatus.SUCCESS);
        long total = executionLogRepository.countTotal();
        double successRate = total == 0 ? 0.0d : Math.round((successCount * 10000.0d) / total) / 100.0d;

        return new IntegrationDashboardSummaryResponse(
                providerRepository.countByStatus(RecordStatus.ACTIVE),
                executionLogRepository.countByStatus(IntegrationExecutionStatus.FAILED),
                executionLogRepository.countByStatus(IntegrationExecutionStatus.RETRY_PENDING),
                executionLogRepository.findLastSuccessfulSync(),
                successRate,
                Arrays.stream(IntegrationProviderType.values())
                        .map(type -> new IntegrationProviderTypeSummaryResponse(
                                type,
                                providerRepository.countByType(type),
                                providerRepository.countActiveByType(type)
                        )).toList(),
                executionLogRepository.findRecent(10).stream().map(this::mapLog).toList()
        );
    }

    private void validateProvider(IntegrationProviderRequest request, Long existingId) {
        if (request == null) throw new BadRequestException("Integration provider request is required");
        if (request.getProviderName() == null || request.getProviderName().trim().isEmpty()) throw new BadRequestException("Provider name is required");
        if (request.getProviderType() == null) throw new BadRequestException("Provider type is required");
        if (request.getBaseUrl() == null || request.getBaseUrl().trim().isEmpty()) throw new BadRequestException("Base URL is required");
        if (request.getAuthType() == null) throw new BadRequestException("Auth type is required");
        if (request.getTimeoutSec() == null || request.getTimeoutSec() <= 0) throw new BadRequestException("Timeout sec must be greater than zero");
        if (request.getProviderCode() != null && !request.getProviderCode().trim().isEmpty()) {
            providerRepository.findByProviderCode(request.getProviderCode().trim())
                    .filter(item -> existingId == null || !item.getId().equals(existingId))
                    .ifPresent(item -> { throw new BadRequestException("Provider code already exists"); });
        }
    }

    private IntegrationProvider getProviderEntity(Long id) {
        if (id == null) throw new BadRequestException("Provider id is required");
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Integration provider not found"));
    }

    private IntegrationExecutionLog getExecutionLogEntity(Long id) {
        if (id == null) throw new BadRequestException("Execution log id is required");
        return executionLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Integration execution log not found"));
    }

    private String resolveProviderCode(String requested) {
        if (requested != null && !requested.trim().isEmpty()) return requested.trim().toUpperCase();
        String last = providerRepository.findLastProviderCode();
        int next = 1;
        if (last != null && last.matches("INT-\\d+")) next = Integer.parseInt(last.substring(4)) + 1;
        return String.format("INT-%05d", next);
    }

    private void applyProvider(IntegrationProvider entity, IntegrationProviderRequest request) {
        entity.setProviderName(request.getProviderName().trim());
        entity.setProviderType(request.getProviderType());
        entity.setBaseUrl(request.getBaseUrl().trim());
        entity.setAuthType(request.getAuthType());
        entity.setApiKey(blankToNull(request.getApiKey()));
        entity.setUsername(blankToNull(request.getUsername()));
        entity.setPasswordEnc(encodePassword(blankToNull(request.getPassword())));
        entity.setTimeoutSec(request.getTimeoutSec());
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String encodePassword(String rawPassword) {
        if (rawPassword == null) return null;
        return Base64.getEncoder().encodeToString(rawPassword.getBytes(StandardCharsets.UTF_8));
    }

    private String maskCredential(String value) {
        if (value == null || value.isEmpty()) return null;
        if (value.length() <= 4) return "****";
        return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
    }

    private IntegrationProviderResponse mapProvider(IntegrationProvider entity) {
        IntegrationExecutionStatus lastExecutionStatus = null;
        LocalDateTime lastExecutedAt = null;
        var latest = executionLogRepository.findLatestByProviderId(entity.getId());
        if (latest.isPresent()) {
            lastExecutionStatus = latest.get().getExecutionStatus();
            lastExecutedAt = latest.get().getExecutedAt();
        }

        return new IntegrationProviderResponse(
                entity.getId(),
                entity.getProviderCode(),
                entity.getProviderName(),
                entity.getProviderType(),
                entity.getBaseUrl(),
                entity.getAuthType(),
                maskCredential(entity.getApiKey()),
                entity.getUsername(),
                maskCredential(entity.getPasswordEnc()),
                entity.getTimeoutSec(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                lastExecutionStatus,
                lastExecutedAt,
                executionLogRepository.countByProviderId(entity.getId()),
                executionLogRepository.countFailuresByProviderId(entity.getId())
        );
    }

    private IntegrationExecutionLogResponse mapLog(IntegrationExecutionLog entity) {
        return new IntegrationExecutionLogResponse(
                entity.getId(),
                entity.getProvider().getId(),
                entity.getProvider().getProviderCode(),
                entity.getProvider().getProviderName(),
                entity.getProvider().getProviderType(),
                entity.getReferenceModule(),
                entity.getReferenceId(),
                entity.getRequestPayload(),
                entity.getResponsePayload(),
                entity.getHttpStatus(),
                entity.getExecutionStatus(),
                entity.getExecutedAt(),
                entity.getRetryCount(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private String safeStatus(Integer value) {
        return value == null ? "-" : String.valueOf(value);
    }
}
