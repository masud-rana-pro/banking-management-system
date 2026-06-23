package com.sbms.integration.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.integration.controller.IIntegrationController;
import com.sbms.integration.dto.request.IntegrationProviderRequest;
import com.sbms.integration.dto.request.IntegrationProviderTestRequest;
import com.sbms.integration.dto.response.IntegrationDashboardSummaryResponse;
import com.sbms.integration.dto.response.IntegrationExecutionLogResponse;
import com.sbms.integration.dto.response.IntegrationProviderResponse;
import com.sbms.integration.service.IIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integrations")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("INTEGRATION_MANAGEMENT_ACCESS")
public class IntegrationController implements IIntegrationController {

    @Autowired
    private IIntegrationService integrationService;

    @Override
    @RequiresPermission("INTEGRATION_PROVIDER_CREATE")
    @PostMapping("/providers/create")
    public ApiResponse<IntegrationProviderResponse> createProvider(@RequestBody IntegrationProviderRequest request) {
        return ResponseBuilder.success("Integration provider created successfully", integrationService.createProvider(request));
    }

    @Override
    @GetMapping("/providers/list")
    public ApiResponse<List<IntegrationProviderResponse>> listProviders(
            @RequestParam(required = false) String providerType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Integration providers fetched successfully", integrationService.listProviders(providerType, status, keyword));
    }

    @Override
    @GetMapping("/providers/{id}")
    public ApiResponse<IntegrationProviderResponse> getProviderById(@PathVariable Long id) {
        return ResponseBuilder.success("Integration provider fetched successfully", integrationService.getProviderById(id));
    }

    @Override
    @RequiresPermission("INTEGRATION_PROVIDER_EDIT")
    @PutMapping("/providers/{id}")
    public ApiResponse<IntegrationProviderResponse> updateProvider(@PathVariable Long id, @RequestBody IntegrationProviderRequest request) {
        return ResponseBuilder.success("Integration provider updated successfully", integrationService.updateProvider(id, request));
    }

    @Override
    @RequiresPermission("INTEGRATION_PROVIDER_ARCHIVE")
    @DeleteMapping("/providers/{id}")
    public ApiResponse<IntegrationProviderResponse> archiveProvider(@PathVariable Long id) {
        return ResponseBuilder.success("Integration provider archived successfully", integrationService.archiveProvider(id));
    }

    @Override
    @RequiresPermission("INTEGRATION_PROVIDER_RESTORE")
    @PutMapping("/providers/{id}/restore")
    public ApiResponse<IntegrationProviderResponse> restoreProvider(@PathVariable Long id) {
        return ResponseBuilder.success("Integration provider restored successfully", integrationService.restoreProvider(id));
    }

    @Override
    @RequiresPermission("INTEGRATION_PROVIDER_TEST")
    @PostMapping("/providers/{id}/test")
    public ApiResponse<IntegrationExecutionLogResponse> testProvider(@PathVariable Long id, @RequestBody(required = false) IntegrationProviderTestRequest request) {
        return ResponseBuilder.success("Integration provider test executed successfully", integrationService.testProvider(id, request));
    }

    @Override
    @GetMapping("/logs/list")
    public ApiResponse<List<IntegrationExecutionLogResponse>> listLogs(
            @RequestParam(required = false) Long providerId,
            @RequestParam(required = false) String executionStatus,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Integration execution logs fetched successfully", integrationService.listLogs(providerId, executionStatus, keyword));
    }

    @Override
    @GetMapping("/logs/{id}")
    public ApiResponse<IntegrationExecutionLogResponse> getLogById(@PathVariable Long id) {
        return ResponseBuilder.success("Integration execution log fetched successfully", integrationService.getLogById(id));
    }

    @Override
    @RequiresPermission("INTEGRATION_LOG_RETRY")
    @PostMapping("/logs/{id}/retry")
    public ApiResponse<IntegrationExecutionLogResponse> retryLog(@PathVariable Long id) {
        return ResponseBuilder.success("Integration execution retry queued successfully", integrationService.retryLog(id));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<IntegrationDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Integration dashboard summary fetched successfully", integrationService.getDashboardSummary());
    }
}
