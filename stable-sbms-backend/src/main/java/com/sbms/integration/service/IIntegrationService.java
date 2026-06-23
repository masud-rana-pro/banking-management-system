package com.sbms.integration.service;

import com.sbms.integration.dto.request.IntegrationProviderRequest;
import com.sbms.integration.dto.request.IntegrationProviderTestRequest;
import com.sbms.integration.dto.response.IntegrationDashboardSummaryResponse;
import com.sbms.integration.dto.response.IntegrationExecutionLogResponse;
import com.sbms.integration.dto.response.IntegrationProviderResponse;

import java.util.List;

public interface IIntegrationService {

    IntegrationProviderResponse createProvider(IntegrationProviderRequest request);
    List<IntegrationProviderResponse> listProviders(String providerType, String status, String keyword);
    IntegrationProviderResponse getProviderById(Long id);
    IntegrationProviderResponse updateProvider(Long id, IntegrationProviderRequest request);
    IntegrationProviderResponse archiveProvider(Long id);
    IntegrationProviderResponse restoreProvider(Long id);
    IntegrationExecutionLogResponse testProvider(Long id, IntegrationProviderTestRequest request);

    List<IntegrationExecutionLogResponse> listLogs(Long providerId, String executionStatus, String keyword);
    IntegrationExecutionLogResponse getLogById(Long id);
    IntegrationExecutionLogResponse retryLog(Long id);

    IntegrationDashboardSummaryResponse getDashboardSummary();
}
