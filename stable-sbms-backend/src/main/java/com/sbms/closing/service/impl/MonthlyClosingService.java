package com.sbms.closing.service.impl;

import com.sbms.branch.entity.Branch;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.closing.dto.request.MonthlyClosingDecisionRequest;
import com.sbms.closing.dto.request.MonthlyClosingRunRequest;
import com.sbms.closing.dto.response.MonthlyClosingDashboardSummaryResponse;
import com.sbms.closing.dto.response.MonthlyClosingRunResponse;
import com.sbms.closing.entity.MonthlyClosingRun;
import com.sbms.closing.enums.MonthlyClosingStatus;
import com.sbms.closing.repository.MonthlyClosingRunRepository;
import com.sbms.closing.service.IMonthlyClosingService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.common.aop.AopRequestContext;
import com.sbms.report.repository.ReportDataRepository;
import com.sbms.user.entity.User;
import com.sbms.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class MonthlyClosingService implements IMonthlyClosingService {

    private static final String SYSTEM_CLOSING_USER = "SYSTEM_MONTH_END";
    private static final int CHECKLIST_TOTAL_COUNT = 4;
    private static final Set<String> GLOBAL_OVERSIGHT_ROLES = Set.of(
            "SYSTEM_ADMIN",
            "MIS_OFFICER",
            "COMPLIANCE_OFFICER",
            "INTERNAL_AUDITOR",
            "TREASURY_FINANCE_OFFICER"
    );

    private final MonthlyClosingRunRepository monthlyClosingRunRepository;
    private final BranchRepository branchRepository;
    private final ReportDataRepository reportDataRepository;
    private final UserRepository userRepository;
    private final AutomatedMailService automatedMailService;

    public MonthlyClosingService(MonthlyClosingRunRepository monthlyClosingRunRepository,
                                 BranchRepository branchRepository,
                                 ReportDataRepository reportDataRepository,
                                 UserRepository userRepository,
                                 AutomatedMailService automatedMailService) {
        this.monthlyClosingRunRepository = monthlyClosingRunRepository;
        this.branchRepository = branchRepository;
        this.reportDataRepository = reportDataRepository;
        this.userRepository = userRepository;
        this.automatedMailService = automatedMailService;
    }

    @Override
    public MonthlyClosingRunResponse createOrRefresh(MonthlyClosingRunRequest request, String username) {
        validateRequest(request);
        LocalDate closingMonth = normalizeClosingMonth(request.getClosingMonth());
        Long effectiveBranchId = resolveEffectiveBranchId(request.getBranchId(), username, true);
        Branch branch = branchRepository.findById(effectiveBranchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        MonthlyClosingRun entity = monthlyClosingRunRepository.findByBranchIdAndClosingMonth(branch.getId(), closingMonth)
                .orElseGet(MonthlyClosingRun::new);

        if (entity.getId() != null && entity.getStatus() == MonthlyClosingStatus.APPROVED) {
            throw new BadRequestException("Approved monthly closing run cannot be refreshed directly");
        }

        ClosingSnapshot snapshot = loadSnapshot(branch, closingMonth);

        if (entity.getId() == null) {
            entity.setClosingRef(nextClosingRef(branch.getBranchCode(), closingMonth));
            entity.setCreatedBy(resolveUser(username));
            entity.setStatus(MonthlyClosingStatus.DRAFT);
        }

        entity.setBranchId(branch.getId());
        entity.setBranchCode(branch.getBranchCode());
        entity.setBranchName(branch.getBranchName());
        entity.setClosingMonth(closingMonth);
        entity.setPeriodFrom(closingMonth.withDayOfMonth(1));
        entity.setPeriodTo(closingMonth.withDayOfMonth(closingMonth.lengthOfMonth()));
        entity.setTransactionAmount(snapshot.transactionAmount());
        entity.setReversedCount(snapshot.reversedCount());
        entity.setVaultClosingBalance(snapshot.vaultClosingBalance());
        entity.setProfitPosted(snapshot.profitPosted());
        entity.setVaultClosedConfirmed(Boolean.TRUE.equals(request.getVaultClosedConfirmed()));
        entity.setProfitPostedConfirmed(Boolean.TRUE.equals(request.getProfitPostedConfirmed()));
        entity.setReversalsReviewed(Boolean.TRUE.equals(request.getReversalsReviewed()));
        entity.setStatementsGenerated(Boolean.TRUE.equals(request.getStatementsGenerated()));
        entity.setRemarks(blankToNull(request.getRemarks()));

        if (entity.getStatus() == MonthlyClosingStatus.REJECTED || entity.getStatus() == MonthlyClosingStatus.REOPENED) {
            entity.setStatus(MonthlyClosingStatus.DRAFT);
        }

        return toResponse(monthlyClosingRunRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyClosingRunResponse> list(Long branchId, String status, LocalDate closingMonth) {
        LocalDate normalizedMonth = closingMonth == null ? null : normalizeClosingMonth(closingMonth);
        Long effectiveBranchId = resolveEffectiveBranchId(branchId, AopRequestContext.currentUsername(), false);
        return monthlyClosingRunRepository.findAll(effectiveBranchId, status, normalizedMonth)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyClosingRunResponse getById(Long id) {
        MonthlyClosingRun entity = getEntity(id);
        ensureCanAccess(entity, AopRequestContext.currentUsername());
        return toResponse(entity);
    }

    @Override
    public MonthlyClosingRunResponse submit(Long id, String username) {
        MonthlyClosingRun entity = getEntity(id);
        ensureCanAccess(entity, username);
        if (entity.getStatus() == MonthlyClosingStatus.APPROVED) {
            throw new BadRequestException("Approved monthly closing run cannot be submitted again");
        }
        List<String> outstandingItems = outstandingChecklistItems(entity);
        if (!outstandingItems.isEmpty()) {
            throw new BadRequestException("Month-end checklist is incomplete: " + String.join(", ", outstandingItems));
        }

        entity.setStatus(MonthlyClosingStatus.SUBMITTED);
        entity.setSubmittedBy(resolveUser(username));
        entity.setSubmittedAt(LocalDateTime.now());

        MonthlyClosingRun saved = monthlyClosingRunRepository.save(entity);
        sendDecisionMail(saved, "Submitted", "Month-end closing run submitted for review", "/reports/monthly-closing", "Open Monthly Closing");
        return toResponse(saved);
    }

    @Override
    public MonthlyClosingRunResponse approve(Long id, MonthlyClosingDecisionRequest request, String username) {
        MonthlyClosingRun entity = getEntity(id);
        ensureCanAccess(entity, username);
        ensureSubmitted(entity);
        entity.setStatus(MonthlyClosingStatus.APPROVED);
        entity.setApprovedBy(resolveUser(username));
        entity.setApprovedAt(LocalDateTime.now());
        entity.setRemarks(resolveDecisionRemarks(entity.getRemarks(), request));
        MonthlyClosingRun saved = monthlyClosingRunRepository.save(entity);
        sendDecisionMail(saved, "Approved", entity.getRemarks(), "/reports/monthly-closing", "Open Monthly Closing");
        return toResponse(saved);
    }

    @Override
    public MonthlyClosingRunResponse reject(Long id, MonthlyClosingDecisionRequest request, String username) {
        MonthlyClosingRun entity = getEntity(id);
        ensureCanAccess(entity, username);
        ensureSubmitted(entity);
        String remarks = requiredDecisionRemarks(request, "Rejection remarks are required");
        entity.setStatus(MonthlyClosingStatus.REJECTED);
        entity.setRejectedBy(resolveUser(username));
        entity.setRejectedAt(LocalDateTime.now());
        entity.setRemarks(remarks);
        MonthlyClosingRun saved = monthlyClosingRunRepository.save(entity);
        sendDecisionMail(saved, "Rejected", remarks, "/reports/monthly-closing", "Review Monthly Closing");
        return toResponse(saved);
    }

    @Override
    public MonthlyClosingRunResponse reopen(Long id, MonthlyClosingDecisionRequest request, String username) {
        MonthlyClosingRun entity = getEntity(id);
        ensureCanAccess(entity, username);
        if (entity.getStatus() != MonthlyClosingStatus.APPROVED && entity.getStatus() != MonthlyClosingStatus.REJECTED) {
            throw new BadRequestException("Only approved or rejected closing runs can be reopened");
        }
        String remarks = requiredDecisionRemarks(request, "Reopen remarks are required");
        entity.setStatus(MonthlyClosingStatus.REOPENED);
        entity.setReopenedBy(resolveUser(username));
        entity.setReopenedAt(LocalDateTime.now());
        entity.setRemarks(remarks);
        MonthlyClosingRun saved = monthlyClosingRunRepository.save(entity);
        sendDecisionMail(saved, "Reopened", remarks, "/reports/monthly-closing", "Update Monthly Closing");
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyClosingDashboardSummaryResponse dashboardSummary() {
        LocalDate closingMonth = LocalDate.now().withDayOfMonth(1);
        Long branchId = resolveEffectiveBranchId(null, AopRequestContext.currentUsername(), false);
        return new MonthlyClosingDashboardSummaryResponse(
                monthlyClosingRunRepository.countByStatusAndMonth(MonthlyClosingStatus.DRAFT, closingMonth, branchId),
                monthlyClosingRunRepository.countByStatusAndMonth(MonthlyClosingStatus.SUBMITTED, closingMonth, branchId),
                monthlyClosingRunRepository.countByStatusAndMonth(MonthlyClosingStatus.APPROVED, closingMonth, branchId),
                monthlyClosingRunRepository.countByStatusAndMonth(MonthlyClosingStatus.REJECTED, closingMonth, branchId),
                monthlyClosingRunRepository.countByStatusAndMonth(MonthlyClosingStatus.REOPENED, closingMonth, branchId)
        );
    }

    private MonthlyClosingRun getEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Monthly closing run id is required");
        }
        return monthlyClosingRunRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Monthly closing run not found"));
    }

    private void validateRequest(MonthlyClosingRunRequest request) {
        if (request == null) {
            throw new BadRequestException("Monthly closing request is required");
        }
        if (request.getBranchId() == null || request.getBranchId() <= 0) {
            throw new BadRequestException("Valid branch is required");
        }
        if (request.getClosingMonth() == null) {
            throw new BadRequestException("Closing month is required");
        }
        if (normalizeClosingMonth(request.getClosingMonth()).isAfter(LocalDate.now().withDayOfMonth(1))) {
            throw new BadRequestException("Future month cannot be used for monthly closing");
        }
    }

    private LocalDate normalizeClosingMonth(LocalDate closingMonth) {
        return closingMonth.withDayOfMonth(1);
    }

    private ClosingSnapshot loadSnapshot(Branch branch, LocalDate closingMonth) {
        LocalDate dateFrom = closingMonth.withDayOfMonth(1);
        LocalDate dateTo = closingMonth.withDayOfMonth(closingMonth.lengthOfMonth());
        Object[] row = reportDataRepository.findMonthlyClosingSnapshot(dateFrom, dateTo, branch.getId()).orElse(null);
        if (row == null) {
            return new ClosingSnapshot(BigDecimal.ZERO, 0L, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return new ClosingSnapshot(
                toBigDecimal(row[2]),
                toLong(row[3]),
                toBigDecimal(row[4]),
                toBigDecimal(row[5])
        );
    }

    private void ensureSubmitted(MonthlyClosingRun entity) {
        if (entity.getStatus() != MonthlyClosingStatus.SUBMITTED) {
            throw new BadRequestException("Monthly closing run must be submitted before this action");
        }
    }

    private void ensureCanAccess(MonthlyClosingRun entity, String username) {
        Long scopedBranchId = resolveEffectiveBranchId(null, username, false);
        if (scopedBranchId != null && !scopedBranchId.equals(entity.getBranchId())) {
            throw new BadRequestException("You do not have access to this branch monthly closing run");
        }
    }

    private Long resolveEffectiveBranchId(Long requestedBranchId, String username, boolean strictBranchMatch) {
        User user = resolveCurrentUser(username);
        if (user == null || user.getBranchId() == null || canViewAllBranches(user)) {
            return requestedBranchId;
        }
        if (requestedBranchId == null) {
            return user.getBranchId();
        }
        if (strictBranchMatch && !user.getBranchId().equals(requestedBranchId)) {
            throw new BadRequestException("You can only operate monthly closing for your assigned branch");
        }
        return user.getBranchId();
    }

    private User resolveCurrentUser(String username) {
        String resolvedUsername = resolveUser(username);
        if (SYSTEM_CLOSING_USER.equalsIgnoreCase(resolvedUsername)) {
            return null;
        }
        return userRepository.findByUsername(resolvedUsername).orElse(null);
    }

    private boolean canViewAllBranches(User user) {
        String roleCode = user.getRole() == null || user.getRole().getCode() == null
                ? AopRequestContext.currentRoleCode()
                : user.getRole().getCode();
        return roleCode != null && GLOBAL_OVERSIGHT_ROLES.contains(roleCode.trim().toUpperCase(Locale.ROOT));
    }

    private String resolveUser(String username) {
        return username == null || username.trim().isEmpty() ? SYSTEM_CLOSING_USER : username.trim();
    }

    private String nextClosingRef(String branchCode, LocalDate closingMonth) {
        return "MCL-" + safeCode(branchCode) + "-" + closingMonth.getYear() + String.format("%02d", closingMonth.getMonthValue());
    }

    private String safeCode(String branchCode) {
        if (branchCode == null || branchCode.trim().isEmpty()) {
            return "GEN";
        }
        return branchCode.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
    }

    private String requiredDecisionRemarks(MonthlyClosingDecisionRequest request, String message) {
        if (request == null || request.getRemarks() == null || request.getRemarks().trim().isEmpty()) {
            throw new BadRequestException(message);
        }
        return request.getRemarks().trim();
    }

    private String resolveDecisionRemarks(String existingRemarks, MonthlyClosingDecisionRequest request) {
        if (request == null || request.getRemarks() == null || request.getRemarks().trim().isEmpty()) {
            return blankToNull(existingRemarks);
        }
        return request.getRemarks().trim();
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        return new BigDecimal(String.valueOf(value));
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private void sendDecisionMail(MonthlyClosingRun entity, String decision, String remarks, String routePath, String ctaLabel) {
        if (entity.getSubmittedBy() == null || entity.getSubmittedBy().isBlank()) {
            return;
        }
        userRepository.findByUsername(entity.getSubmittedBy()).map(User::getEmail).ifPresent(email ->
                automatedMailService.sendApprovalDecisionEmail(
                        email,
                        "Monthly Closing",
                        entity.getClosingRef(),
                        decision,
                        remarks,
                        routePath,
                        ctaLabel
                ));
    }

    private MonthlyClosingRunResponse toResponse(MonthlyClosingRun entity) {
        List<String> outstandingItems = outstandingChecklistItems(entity);
        int completedCount = CHECKLIST_TOTAL_COUNT - outstandingItems.size();
        MonthlyClosingRunResponse response = new MonthlyClosingRunResponse();
        response.setId(entity.getId());
        response.setClosingRef(entity.getClosingRef());
        response.setBranchId(entity.getBranchId());
        response.setBranchCode(entity.getBranchCode());
        response.setBranchName(entity.getBranchName());
        response.setClosingMonth(entity.getClosingMonth());
        response.setPeriodFrom(entity.getPeriodFrom());
        response.setPeriodTo(entity.getPeriodTo());
        response.setTransactionAmount(entity.getTransactionAmount());
        response.setReversedCount(entity.getReversedCount());
        response.setVaultClosingBalance(entity.getVaultClosingBalance());
        response.setProfitPosted(entity.getProfitPosted());
        response.setVaultClosedConfirmed(entity.getVaultClosedConfirmed());
        response.setProfitPostedConfirmed(entity.getProfitPostedConfirmed());
        response.setReversalsReviewed(entity.getReversalsReviewed());
        response.setStatementsGenerated(entity.getStatementsGenerated());
        response.setChecklistCompletedCount(completedCount);
        response.setChecklistTotalCount(CHECKLIST_TOTAL_COUNT);
        response.setChecklistProgressPercent((int) Math.round((completedCount * 100.0d) / CHECKLIST_TOTAL_COUNT));
        response.setReadyForSubmit(outstandingItems.isEmpty());
        response.setOutstandingChecklistItems(outstandingItems);
        response.setStatus(entity.getStatus().name());
        response.setRemarks(entity.getRemarks());
        response.setCreatedBy(entity.getCreatedBy());
        response.setSubmittedBy(entity.getSubmittedBy());
        response.setSubmittedAt(entity.getSubmittedAt());
        response.setApprovedBy(entity.getApprovedBy());
        response.setApprovedAt(entity.getApprovedAt());
        response.setRejectedBy(entity.getRejectedBy());
        response.setRejectedAt(entity.getRejectedAt());
        response.setReopenedBy(entity.getReopenedBy());
        response.setReopenedAt(entity.getReopenedAt());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    private List<String> outstandingChecklistItems(MonthlyClosingRun entity) {
        List<String> items = new ArrayList<>();
        if (!Boolean.TRUE.equals(entity.getVaultClosedConfirmed())) {
            items.add("Vault close confirmation pending");
        }
        if (!Boolean.TRUE.equals(entity.getProfitPostedConfirmed())) {
            items.add("Profit posting confirmation pending");
        }
        if (!Boolean.TRUE.equals(entity.getReversalsReviewed())) {
            items.add("Reversal review pending");
        }
        if (!Boolean.TRUE.equals(entity.getStatementsGenerated())) {
            items.add("Statement generation confirmation pending");
        }
        return items;
    }

    private record ClosingSnapshot(
            BigDecimal transactionAmount,
            Long reversedCount,
            BigDecimal vaultClosingBalance,
            BigDecimal profitPosted
    ) {
    }
}
