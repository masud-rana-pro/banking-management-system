package com.sbms.workflow.service.impl;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.workflow.dto.response.WorkflowCommentResponse;
import com.sbms.workflow.dto.response.WorkflowDashboardSummaryResponse;
import com.sbms.workflow.dto.response.WorkflowHistoryResponse;
import com.sbms.workflow.entity.WorkflowComment;
import com.sbms.workflow.entity.WorkflowHistory;
import com.sbms.workflow.repository.WorkflowCommentRepository;
import com.sbms.workflow.repository.WorkflowHistoryRepository;
import com.sbms.workflow.service.IWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkflowService implements IWorkflowService {

    private static final String DEFAULT_ACTOR = "SYSTEM";

    @Autowired
    private WorkflowHistoryRepository workflowHistoryRepository;

    @Autowired
    private WorkflowCommentRepository workflowCommentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowHistoryResponse> getWorkflowHistory(String moduleName, String keyword) {
        return workflowHistoryRepository.findAll(moduleName, keyword).stream().map(this::mapHistory).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowHistoryResponse getWorkflowHistoryById(Long id) {
        if (id == null) {
            throw new BadRequestException("Workflow history id is required");
        }
        return mapHistory(workflowHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow history not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowHistoryResponse> getPendingApprovals(String keyword) {
        return workflowHistoryRepository.findPending(keyword).stream().map(this::mapHistory).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowHistoryResponse> getMySubmissions(String actionBy) {
        String actor = actionBy == null || actionBy.trim().isEmpty() ? DEFAULT_ACTOR : actionBy.trim();
        return workflowHistoryRepository.findByActionBy(actor).stream().map(this::mapHistory).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowDashboardSummaryResponse getDashboardSummary(String actor) {
        String resolvedActor = actor == null || actor.trim().isEmpty() ? DEFAULT_ACTOR : actor.trim();
        return new WorkflowDashboardSummaryResponse(
                safeLong(workflowHistoryRepository.countPending()),
                (long) workflowHistoryRepository.findByActionBy(resolvedActor).stream()
                        .filter(item -> item.getToStatus() != null
                                && List.of("PENDING", "SUBMITTED", "PENDING_REVIEW", "UNDER_REVIEW", "RETURNED", "ASSIGNED")
                                .contains(item.getToStatus().toUpperCase()))
                        .count(),
                safeLong(workflowHistoryRepository.countCompleted()),
                safeLong(workflowHistoryRepository.countBottlenecks()),
                workflowHistoryRepository.findRecent(10).stream().map(this::mapHistory).toList(),
                workflowHistoryRepository.findPending(null).stream().limit(10).map(this::mapHistory).toList()
        );
    }

    private WorkflowHistoryResponse mapHistory(WorkflowHistory entity) {
        List<WorkflowCommentResponse> comments = workflowCommentRepository
                .findByModuleReference(entity.getModuleName(), entity.getReferenceId())
                .stream()
                .map(this::mapComment)
                .toList();
        return new WorkflowHistoryResponse(
                entity.getId(),
                entity.getModuleName(),
                entity.getReferenceId(),
                entity.getActionName(),
                entity.getFromStatus(),
                entity.getToStatus(),
                entity.getActionBy(),
                entity.getActionAt(),
                entity.getRemarks(),
                entity.getStatus(),
                comments
        );
    }

    private WorkflowCommentResponse mapComment(WorkflowComment entity) {
        return new WorkflowCommentResponse(
                entity.getId(),
                entity.getModuleName(),
                entity.getReferenceId(),
                entity.getCommentText(),
                entity.getCommentBy(),
                entity.getCommentAt(),
                entity.getStatus()
        );
    }

    private Long safeLong(Long value) {
        return value == null ? 0L : value;
    }
}
