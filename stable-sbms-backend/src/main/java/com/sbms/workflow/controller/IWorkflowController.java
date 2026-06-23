package com.sbms.workflow.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.workflow.dto.response.WorkflowDashboardSummaryResponse;
import com.sbms.workflow.dto.response.WorkflowHistoryResponse;

import java.util.List;

public interface IWorkflowController {

    ApiResponse<List<WorkflowHistoryResponse>> listHistory(String moduleName, String keyword);

    ApiResponse<WorkflowHistoryResponse> getHistoryById(Long id);

    ApiResponse<List<WorkflowHistoryResponse>> getPending(String keyword);

    ApiResponse<List<WorkflowHistoryResponse>> getMySubmissions(String actionBy);

    ApiResponse<WorkflowDashboardSummaryResponse> getDashboardSummary(String actor);
}
