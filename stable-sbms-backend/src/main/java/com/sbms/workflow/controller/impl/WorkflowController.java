package com.sbms.workflow.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.workflow.controller.IWorkflowController;
import com.sbms.workflow.dto.response.WorkflowDashboardSummaryResponse;
import com.sbms.workflow.dto.response.WorkflowHistoryResponse;
import com.sbms.workflow.service.IWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflows")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
public class WorkflowController implements IWorkflowController {

    @Autowired
    private IWorkflowService workflowService;

    @Override
    @GetMapping("/history/list")
    public ApiResponse<List<WorkflowHistoryResponse>> listHistory(
            @RequestParam(required = false) String moduleName,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Workflow history fetched successfully",
                workflowService.getWorkflowHistory(moduleName, keyword));
    }

    @Override
    @GetMapping("/history/{id}")
    public ApiResponse<WorkflowHistoryResponse> getHistoryById(@PathVariable Long id) {
        return ResponseBuilder.success("Workflow history detail fetched successfully",
                workflowService.getWorkflowHistoryById(id));
    }

    @Override
    @GetMapping("/pending")
    public ApiResponse<List<WorkflowHistoryResponse>> getPending(@RequestParam(required = false) String keyword) {
        return ResponseBuilder.success("Pending workflow approvals fetched successfully",
                workflowService.getPendingApprovals(keyword));
    }

    @Override
    @GetMapping("/my-submissions")
    public ApiResponse<List<WorkflowHistoryResponse>> getMySubmissions(@RequestParam(required = false) String actionBy) {
        return ResponseBuilder.success("My workflow submissions fetched successfully",
                workflowService.getMySubmissions(actionBy));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<WorkflowDashboardSummaryResponse> getDashboardSummary(@RequestParam(required = false) String actor) {
        return ResponseBuilder.success("Workflow dashboard summary fetched successfully",
                workflowService.getDashboardSummary(actor));
    }
}
