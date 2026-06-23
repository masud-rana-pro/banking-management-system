package com.sbms.workflow.dto.response;

import java.util.List;

public class WorkflowDashboardSummaryResponse {

    private Long pendingApprovals;
    private Long myPendingTasks;
    private Long recentCompletedTasks;
    private Long workflowBottlenecks;
    private List<WorkflowHistoryResponse> recentHistory;
    private List<WorkflowHistoryResponse> pendingQueue;

    public WorkflowDashboardSummaryResponse(Long pendingApprovals, Long myPendingTasks, Long recentCompletedTasks,
                                            Long workflowBottlenecks, List<WorkflowHistoryResponse> recentHistory,
                                            List<WorkflowHistoryResponse> pendingQueue) {
        this.pendingApprovals = pendingApprovals;
        this.myPendingTasks = myPendingTasks;
        this.recentCompletedTasks = recentCompletedTasks;
        this.workflowBottlenecks = workflowBottlenecks;
        this.recentHistory = recentHistory;
        this.pendingQueue = pendingQueue;
    }

    public Long getPendingApprovals() { return pendingApprovals; }
    public Long getMyPendingTasks() { return myPendingTasks; }
    public Long getRecentCompletedTasks() { return recentCompletedTasks; }
    public Long getWorkflowBottlenecks() { return workflowBottlenecks; }
    public List<WorkflowHistoryResponse> getRecentHistory() { return recentHistory; }
    public List<WorkflowHistoryResponse> getPendingQueue() { return pendingQueue; }
}
