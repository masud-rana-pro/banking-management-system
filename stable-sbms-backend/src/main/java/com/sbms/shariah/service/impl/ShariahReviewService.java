package com.sbms.shariah.service.impl;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.shariah.dto.request.ShariahChecklistSaveRequest;
import com.sbms.shariah.dto.request.ShariahChecklistSelectionRequest;
import com.sbms.shariah.dto.request.ShariahDecisionRequest;
import com.sbms.shariah.dto.request.ShariahReviewCaseRequest;
import com.sbms.shariah.dto.response.*;
import com.sbms.shariah.entity.ShariahChecklistItem;
import com.sbms.shariah.entity.ShariahReviewCase;
import com.sbms.shariah.entity.ShariahReviewChecklist;
import com.sbms.shariah.entity.ShariahReviewDecision;
import com.sbms.shariah.enums.ShariahCaseStatus;
import com.sbms.shariah.enums.ShariahDecisionType;
import com.sbms.shariah.repository.ShariahChecklistItemRepository;
import com.sbms.shariah.repository.ShariahReviewCaseRepository;
import com.sbms.shariah.repository.ShariahReviewChecklistRepository;
import com.sbms.shariah.repository.ShariahReviewDecisionRepository;
import com.sbms.shariah.service.IShariahReviewService;
import com.sbms.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ShariahReviewService implements IShariahReviewService {

    @Autowired
    private ShariahReviewCaseRepository caseRepository;

    @Autowired
    private ShariahChecklistItemRepository checklistItemRepository;

    @Autowired
    private ShariahReviewChecklistRepository checklistRepository;

    @Autowired
    private ShariahReviewDecisionRepository decisionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AutomatedMailService automatedMailService;

    @Override
    public ShariahReviewCaseResponse create(ShariahReviewCaseRequest request) {
        if (request == null) throw new BadRequestException("Shariah review request is required");
        String referenceModule = upper(request.getReferenceModule());
        if (referenceModule == null) throw new BadRequestException("Reference module is required");
        if (request.getReferenceId() == null) throw new BadRequestException("Reference id is required");
        String submittedBy = trim(request.getSubmittedBy());
        if (submittedBy == null) throw new BadRequestException("Submitted by is required");

        caseRepository.findOpenByReference(referenceModule, request.getReferenceId())
                .ifPresent(existing -> {
                    throw new BadRequestException("An open Shariah review case already exists for this source reference");
                });

        ShariahReviewCase entity = new ShariahReviewCase();
        entity.setCaseNo(generateCaseNo());
        entity.setReferenceModule(referenceModule);
        entity.setReferenceId(request.getReferenceId());
        entity.setSubmittedBy(submittedBy);
        entity.setRemarks(trim(request.getRemarks()));
        entity.setCaseStatus(ShariahCaseStatus.PENDING_REVIEW);
        entity.setStatus(RecordStatus.ACTIVE);

        ShariahReviewCase saved = caseRepository.save(entity);
        createDecision(saved, ShariahDecisionType.SUBMITTED, submittedBy, trim(request.getRemarks()));
        return map(saved);
    }

    @Override
    public List<ShariahReviewCaseResponse> list(String referenceModule, String caseStatus, String keyword) {
        return caseRepository.findAll(upper(referenceModule), resolveCaseStatus(caseStatus), trim(keyword))
                .stream().map(this::map).toList();
    }

    @Override
    public ShariahReviewCaseResponse getById(Long id) {
        return map(getCase(id));
    }

    @Override
    public List<ShariahChecklistItemResponse> getChecklistItems() {
        return checklistItemRepository.findActive().stream()
                .map(item -> new ShariahChecklistItemResponse(
                        item.getId(),
                        item.getItemCode(),
                        item.getItemName(),
                        item.getDescription(),
                        item.getStatus(),
                        Boolean.FALSE,
                        null,
                        item.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public ShariahReviewCaseResponse saveChecklist(Long id, ShariahChecklistSaveRequest request) {
        ShariahReviewCase entity = getCase(id);
        syncChecklist(entity, request == null ? null : request.getChecklistItems());
        String reviewer = request == null ? null : trim(request.getReviewedBy());
        String remarks = request == null ? null : trim(request.getRemarks());
        if (reviewer != null || remarks != null) {
            createDecision(entity, ShariahDecisionType.CHECKLIST_UPDATED, fallback(reviewer, "BOARD_REVIEWER"), remarks);
        }
        if (remarks != null) {
            entity.setRemarks(remarks);
            caseRepository.update(entity);
        }
        return map(entity);
    }

    @Override
    public ShariahReviewCaseResponse approve(Long id, ShariahDecisionRequest request) {
        return applyDecision(id, request, ShariahCaseStatus.APPROVED, ShariahDecisionType.APPROVED, false);
    }

    @Override
    public ShariahReviewCaseResponse reject(Long id, ShariahDecisionRequest request) {
        if (request == null || trim(request.getRemarks()) == null) {
            throw new BadRequestException("Reject remarks are required for traceability");
        }
        return applyDecision(id, request, ShariahCaseStatus.REJECTED, ShariahDecisionType.REJECTED, false);
    }

    @Override
    public ShariahReviewCaseResponse returnCase(Long id, ShariahDecisionRequest request) {
        if (request == null || trim(request.getRemarks()) == null) {
            throw new BadRequestException("Correction remarks are required when returning a case");
        }
        return applyDecision(id, request, ShariahCaseStatus.RETURNED, ShariahDecisionType.RETURNED, true);
    }

    @Override
    public List<ShariahReviewDecisionResponse> getHistory(Long id) {
        getCase(id);
        return decisionRepository.findByCaseId(id).stream().map(this::mapDecision).toList();
    }

    @Override
    public ShariahDashboardSummaryResponse dashboardSummary() {
        return new ShariahDashboardSummaryResponse(
                caseRepository.countByStatus(ShariahCaseStatus.PENDING_REVIEW),
                caseRepository.countByStatus(ShariahCaseStatus.APPROVED),
                caseRepository.countByStatus(ShariahCaseStatus.REJECTED),
                caseRepository.countByStatus(ShariahCaseStatus.RETURNED),
                caseRepository.countUpcomingReviews(),
                caseRepository.findLatest(5).stream().map(this::map).toList(),
                decisionRepository.findLatest(8).stream().map(this::mapDecision).toList(),
                caseRepository.moduleBreakdown()
        );
    }

    private ShariahReviewCaseResponse applyDecision(Long id, ShariahDecisionRequest request, ShariahCaseStatus caseStatus,
                                                    ShariahDecisionType decisionType, boolean keepOpen) {
        ShariahReviewCase entity = getCase(id);
        if (entity.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived Shariah review case cannot be changed");
        }
        if (request != null) {
            syncChecklist(entity, request.getChecklistItems());
        }
        String decisionBy = request == null ? null : trim(request.getDecisionBy());
        if (decisionBy == null) {
            throw new BadRequestException("Decision by is required");
        }

        entity.setCaseStatus(caseStatus);
        entity.setRemarks(request == null ? null : trim(request.getRemarks()));
        if (!keepOpen && entity.getStatus() == RecordStatus.PENDING) {
            entity.setStatus(RecordStatus.ACTIVE);
        }
        caseRepository.update(entity);
        createDecision(entity, decisionType, decisionBy, trim(request == null ? null : request.getRemarks()));
        sendDecisionMail(entity, decisionType, trim(request == null ? null : request.getRemarks()));
        return map(entity);
    }

    private void syncChecklist(ShariahReviewCase entity, List<ShariahChecklistSelectionRequest> requestItems) {
        checklistRepository.deleteByCaseId(entity.getId());
        if (requestItems == null || requestItems.isEmpty()) {
            return;
        }

        Map<Long, ShariahChecklistSelectionRequest> selected = new LinkedHashMap<>();
        for (ShariahChecklistSelectionRequest item : requestItems) {
            if (item == null || item.getItemId() == null || !Boolean.TRUE.equals(item.getSelected())) {
                continue;
            }
            selected.put(item.getItemId(), item);
        }
        if (selected.isEmpty()) {
            return;
        }

        List<ShariahChecklistItem> items = checklistItemRepository.findAllByIds(new ArrayList<>(selected.keySet()));
        if (items.size() != selected.size()) {
            throw new BadRequestException("One or more checklist items are invalid or archived");
        }

        for (ShariahChecklistItem item : items) {
            ShariahReviewChecklist row = new ShariahReviewChecklist();
            row.setReviewCase(entity);
            row.setChecklistItem(item);
            row.setSelectedFlag(Boolean.TRUE);
            row.setNote(trim(selected.get(item.getId()).getNote()));
            row.setStatus(RecordStatus.ACTIVE);
            checklistRepository.save(row);
        }
    }

    private void createDecision(ShariahReviewCase entity, ShariahDecisionType decisionType, String decisionBy, String remarks) {
        ShariahReviewDecision decision = new ShariahReviewDecision();
        decision.setReviewCase(entity);
        decision.setDecision(decisionType);
        decision.setDecisionBy(fallback(decisionBy, "SYSTEM_USER"));
        decision.setRemarks(remarks);
        decision.setStatus(RecordStatus.ACTIVE);
        decisionRepository.save(decision);
    }

    private ShariahReviewCase getCase(Long id) {
        if (id == null) throw new BadRequestException("Shariah review case id is required");
        return caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shariah review case not found"));
    }

    private String generateCaseNo() {
        String last = caseRepository.findLastCaseNo();
        int next = 1;
        if (last != null && last.matches("SHR-\\d+")) {
            next = Integer.parseInt(last.substring(4)) + 1;
        }
        return String.format("SHR-%05d", next);
    }

    private ShariahReviewCaseResponse map(ShariahReviewCase entity) {
        List<ShariahReviewChecklist> selectedRows = checklistRepository.findByCaseId(entity.getId());
        Map<Long, ShariahReviewChecklist> selectedMap = new HashMap<>();
        for (ShariahReviewChecklist row : selectedRows) {
            selectedMap.put(row.getChecklistItem().getId(), row);
        }

        List<ShariahChecklistItemResponse> checklistItems = checklistItemRepository.findActive().stream()
                .map(item -> {
                    ShariahReviewChecklist selected = selectedMap.get(item.getId());
                    return new ShariahChecklistItemResponse(
                            item.getId(),
                            item.getItemCode(),
                            item.getItemName(),
                            item.getDescription(),
                            item.getStatus(),
                            selected != null && Boolean.TRUE.equals(selected.getSelectedFlag()),
                            selected == null ? null : selected.getNote(),
                            item.getCreatedAt()
                    );
                })
                .toList();

        List<ShariahReviewDecisionResponse> history = decisionRepository.findByCaseId(entity.getId()).stream()
                .map(this::mapDecision)
                .toList();

        return new ShariahReviewCaseResponse(
                entity.getId(),
                entity.getCaseNo(),
                entity.getReferenceModule(),
                entity.getReferenceId(),
                entity.getSubmittedBy(),
                entity.getSubmittedAt(),
                entity.getCaseStatus().name(),
                entity.getRemarks(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                checklistItems,
                history
        );
    }

    private ShariahReviewDecisionResponse mapDecision(ShariahReviewDecision entity) {
        return new ShariahReviewDecisionResponse(
                entity.getId(),
                entity.getReviewCase().getId(),
                entity.getReviewCase().getCaseNo(),
                entity.getDecision().name(),
                entity.getDecisionBy(),
                entity.getDecisionAt(),
                entity.getRemarks(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private ShariahCaseStatus resolveCaseStatus(String value) {
        String normalized = upper(value);
        if (normalized == null) {
            return null;
        }
        try {
            return ShariahCaseStatus.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid case status supplied");
        }
    }

    private String upper(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim().toUpperCase();
    }

    private String trim(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String fallback(String value, String fallback) {
        return trim(value) == null ? fallback : trim(value);
    }

    private void sendDecisionMail(ShariahReviewCase entity, ShariahDecisionType decisionType, String remarks) {
        if (entity == null || trim(entity.getSubmittedBy()) == null || decisionType == null) {
            return;
        }
        userRepository.findByUsername(entity.getSubmittedBy()).ifPresent(user -> {
            if (trim(user.getEmail()) == null) {
                return;
            }
            automatedMailService.sendApprovalDecisionEmail(
                    user.getEmail(),
                    "Shariah Review Case",
                    entity.getCaseNo(),
                    prettyDecision(decisionType),
                    remarks,
                    "/shariah/reviews/" + entity.getId(),
                    "Open Shariah Review"
            );
        });
    }

    private String prettyDecision(ShariahDecisionType value) {
        return value == null ? "Updated" : value.name().replace('_', ' ');
    }
}
