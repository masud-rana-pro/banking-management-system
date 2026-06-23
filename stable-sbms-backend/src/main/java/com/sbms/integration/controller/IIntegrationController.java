package com.sbms.integration.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.integration.dto.request.IntegrationProviderRequest;
import com.sbms.integration.dto.request.IntegrationProviderTestRequest;
import com.sbms.integration.dto.response.IntegrationDashboardSummaryResponse;
import com.sbms.integration.dto.response.IntegrationExecutionLogResponse;
import com.sbms.integration.dto.response.IntegrationProviderResponse;

import java.util.List;

public interface IIntegrationController {

    ApiResponse<IntegrationProviderResponse> createProvider(IntegrationProviderRequest request);
    ApiResponse<List<IntegrationProviderResponse>> listProviders(String providerType, String status, String keyword);
    ApiResponse<IntegrationProviderResponse> getProviderById(Long id);
    ApiResponse<IntegrationProviderResponse> updateProvider(Long id, IntegrationProviderRequest request);
    ApiResponse<IntegrationProviderResponse> archiveProvider(Long id);
    ApiResponse<IntegrationProviderResponse> restoreProvider(Long id);
    ApiResponse<IntegrationExecutionLogResponse> testProvider(Long id, IntegrationProviderTestRequest request);

    ApiResponse<List<IntegrationExecutionLogResponse>> listLogs(Long providerId, String executionStatus, String keyword);
    ApiResponse<IntegrationExecutionLogResponse> getLogById(Long id);
    ApiResponse<IntegrationExecutionLogResponse> retryLog(Long id);

    ApiResponse<IntegrationDashboardSummaryResponse> dashboardSummary();
}
