package com.sbms.workflow.service;

import com.sbms.workflow.dto.response.WorkflowDashboardSummaryResponse;
import com.sbms.workflow.dto.response.WorkflowHistoryResponse;

import java.util.List;

public interface IWorkflowService {

    List<WorkflowHistoryResponse> getWorkflowHistory(String moduleName, String keyword);

    WorkflowHistoryResponse getWorkflowHistoryById(Long id);

    List<WorkflowHistoryResponse> getPendingApprovals(String keyword);

    List<WorkflowHistoryResponse> getMySubmissions(String actionBy);

    WorkflowDashboardSummaryResponse getDashboardSummary(String actor);
}
