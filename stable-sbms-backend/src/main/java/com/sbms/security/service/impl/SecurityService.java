package com.sbms.security.service.impl;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.security.dto.request.InvestigationCaseActionRequest;
import com.sbms.security.dto.response.AuditLogResponse;
import com.sbms.security.dto.response.InvestigationCaseResponse;
import com.sbms.security.dto.response.SecurityDashboardSummaryResponse;
import com.sbms.security.dto.response.SecurityEventResponse;
import com.sbms.security.entity.AuditLog;
import com.sbms.security.entity.InvestigationCase;
import com.sbms.security.entity.SecurityEventLog;
import com.sbms.security.enums.InvestigationCaseStatus;
import com.sbms.security.repository.AuditLogRepository;
import com.sbms.security.repository.InvestigationCaseRepository;
import com.sbms.security.repository.SecurityEventLogRepository;
import com.sbms.security.service.ISecurityService;
import com.sbms.user.entity.User;
import com.sbms.user.enums.UserStatus;
import com.sbms.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class SecurityService implements ISecurityService {

    private static final String INVESTIGATION_MODULE = "SECURITY_INVESTIGATION";
    private static final String SYSTEM_USER = "SYSTEM";

    @Autowired
    private SecurityEventLogRepository securityEventLogRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private InvestigationCaseRepository investigationCaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AutomatedMailService automatedMailService;

    @Override
    @Transactional(readOnly = true)
    public List<SecurityEventResponse> getSecurityEvents(String severityLevel, String keyword) {
        return securityEventLogRepository.findAll(severityLevel, keyword).stream()
                .map(this::mapSecurityEvent)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SecurityEventResponse getSecurityEventById(Long id) {
        return mapSecurityEvent(getSecurityEventEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SecurityEventResponse> getSuspiciousActivities(String keyword) {
        return securityEventLogRepository.findSuspiciousActivities(keyword).stream()
                .map(this::mapSecurityEvent)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SecurityEventResponse getSuspiciousActivityById(Long id) {
        SecurityEventLog entity = getSecurityEventEntity(id);
        if (!isSuspicious(entity)) {
            throw new BadRequestException("Selected security event is not marked as suspicious");
        }
        return mapSecurityEvent(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogs(String moduleName, String keyword) {
        return auditLogRepository.findAll(moduleName, keyword).stream()
                .map(this::mapAuditLog)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse getAuditLogById(Long id) {
        if (id == null) {
            throw new BadRequestException("Audit log id is required");
        }
        return mapAuditLog(auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestigationCaseResponse> getInvestigationCases(String caseStatus, String caseType, String keyword) {
        return investigationCaseRepository.findAll(caseStatus, caseType, keyword).stream()
                .map(this::mapInvestigationCase)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvestigationCaseResponse getInvestigationCaseById(Long id) {
        return mapInvestigationCase(getInvestigationCaseEntity(id));
    }

    @Override
    public InvestigationCaseResponse assignInvestigationCase(Long id, InvestigationCaseActionRequest request) {
        InvestigationCase entity = getInvestigationCaseEntity(id);
        if (entity.getCaseStatus() == InvestigationCaseStatus.CLOSED) {
            throw new BadRequestException("Closed investigation case cannot be assigned");
        }
        if (request == null || request.getAssignedTo() == null) {
            throw new BadRequestException("Assigned user is required");
        }
        if (!userRepository.existsById(request.getAssignedTo())) {
            throw new BadRequestException("Assigned user not found");
        }
        if (request.getRemarks() == null || request.getRemarks().trim().length() < 5) {
            throw new BadRequestException("Assignment remarks must be at least 5 characters");
        }

        String oldValue = "{\"caseStatus\":\"" + entity.getCaseStatus() + "\",\"assignedTo\":" + entity.getAssignedTo() + "}";
        entity.setAssignedTo(request.getAssignedTo());
        entity.setCaseStatus(InvestigationCaseStatus.ASSIGNED);
        entity.setRemarks(request.getRemarks().trim());
        if (request.getEvidenceFileName() != null && !request.getEvidenceFileName().trim().isEmpty()) {
            entity.setEvidenceFileName(request.getEvidenceFileName().trim());
        }
        investigationCaseRepository.update(entity);
        String newValue = "{\"caseStatus\":\"" + entity.getCaseStatus() + "\",\"assignedTo\":" + entity.getAssignedTo()
                + ",\"evidenceFileName\":\"" + safe(entity.getEvidenceFileName()) + "\"}";
        createAuditLog(INVESTIGATION_MODULE, entity.getId(), "ASSIGN", oldValue, newValue, resolveActor(request.getPerformedBy()));
        sendInvestigationMailToAssignedUser(entity, "Assigned", entity.getRemarks());
        return mapInvestigationCase(entity);
    }

    @Override
    public InvestigationCaseResponse closeInvestigationCase(Long id, InvestigationCaseActionRequest request) {
        InvestigationCase entity = getInvestigationCaseEntity(id);
        if (entity.getCaseStatus() == InvestigationCaseStatus.CLOSED) {
            throw new BadRequestException("Investigation case is already closed");
        }
        if (request == null || request.getRemarks() == null || request.getRemarks().trim().length() < 8) {
            throw new BadRequestException("Closure remarks must be at least 8 characters");
        }

        String oldValue = "{\"caseStatus\":\"" + entity.getCaseStatus() + "\",\"remarks\":\"" + safe(entity.getRemarks()) + "\"}";
        entity.setCaseStatus(InvestigationCaseStatus.CLOSED);
        entity.setRemarks(request.getRemarks().trim());
        if (request.getEvidenceFileName() != null && !request.getEvidenceFileName().trim().isEmpty()) {
            entity.setEvidenceFileName(request.getEvidenceFileName().trim());
        }
        investigationCaseRepository.update(entity);
        String newValue = "{\"caseStatus\":\"" + entity.getCaseStatus() + "\",\"remarks\":\"" + safe(entity.getRemarks())
                + "\",\"evidenceFileName\":\"" + safe(entity.getEvidenceFileName()) + "\"}";
        createAuditLog(INVESTIGATION_MODULE, entity.getId(), "CLOSE", oldValue, newValue, resolveActor(request.getPerformedBy()));
        sendInvestigationClosureMail(entity);
        return mapInvestigationCase(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public SecurityDashboardSummaryResponse getDashboardSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime fromTime = today.atStartOfDay();
        LocalDateTime toTime = LocalDateTime.now();

        long lockedUsers = userRepository.findAll().stream()
                .filter(user -> user.getStatus() == UserStatus.LOCKED)
                .count();

        return new SecurityDashboardSummaryResponse(
                safeLong(securityEventLogRepository.countByEventCodeBetween("FAILED_LOGIN", fromTime, toTime)),
                lockedUsers,
                safeLong(securityEventLogRepository.countSuspiciousActivities()),
                safeLong(auditLogRepository.countBetween(fromTime, toTime)),
                safeLong(investigationCaseRepository.countOpenCases()),
                safeLong(securityEventLogRepository.countAmlFlagsBetween(fromTime, toTime)),
                securityEventLogRepository.findRecent(10).stream().map(this::mapSecurityEvent).toList(),
                investigationCaseRepository.findRecent(8).stream().map(this::mapInvestigationCase).toList(),
                auditLogRepository.findRecent(8).stream().map(this::mapAuditLog).toList()
        );
    }

    private SecurityEventLog getSecurityEventEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Security event id is required");
        }
        return securityEventLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Security event not found"));
    }

    private InvestigationCase getInvestigationCaseEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Investigation case id is required");
        }
        return investigationCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investigation case not found"));
    }

    private void createAuditLog(String moduleName, Long referenceId, String actionName, String oldValueJson, String newValueJson, String performedBy) {
        AuditLog log = new AuditLog();
        log.setModuleName(moduleName);
        log.setReferenceId(referenceId);
        log.setActionName(actionName);
        log.setOldValueJson(oldValueJson);
        log.setNewValueJson(newValueJson);
        log.setPerformedBy(performedBy);
        log.setStatus(RecordStatus.ACTIVE);
        auditLogRepository.save(log);
    }

    private SecurityEventResponse mapSecurityEvent(SecurityEventLog entity) {
        Optional<User> user = entity.getUserId() == null ? Optional.empty() : userRepository.findById(entity.getUserId());
        return new SecurityEventResponse(
                entity.getId(),
                entity.getEventCode(),
                entity.getEventName(),
                entity.getUserId(),
                user.map(User::getUsername).orElse(null),
                user.map(User::getFullName).orElse(null),
                entity.getIpAddress(),
                entity.getDeviceInfo(),
                entity.getReferenceModule(),
                entity.getReferenceId(),
                entity.getEventTime(),
                entity.getSeverityLevel(),
                entity.getRemarks(),
                entity.getStatus(),
                isSuspicious(entity)
        );
    }

    private AuditLogResponse mapAuditLog(AuditLog entity) {
        return new AuditLogResponse(
                entity.getId(),
                entity.getModuleName(),
                entity.getReferenceId(),
                entity.getActionName(),
                entity.getOldValueJson(),
                entity.getNewValueJson(),
                entity.getPerformedBy(),
                entity.getPerformedAt(),
                entity.getStatus()
        );
    }

    private InvestigationCaseResponse mapInvestigationCase(InvestigationCase entity) {
        Optional<User> assignedUser = entity.getAssignedTo() == null ? Optional.empty() : userRepository.findById(entity.getAssignedTo());
        List<AuditLogResponse> auditTrail = auditLogRepository.findRelated(INVESTIGATION_MODULE, entity.getId()).stream()
                .map(this::mapAuditLog)
                .toList();
        return new InvestigationCaseResponse(
                entity.getId(),
                entity.getCaseNo(),
                entity.getCaseType(),
                entity.getReferenceModule(),
                entity.getReferenceId(),
                entity.getOpenedBy(),
                entity.getOpenedAt(),
                entity.getAssignedTo(),
                assignedUser.map(User::getUsername).orElse(null),
                assignedUser.map(User::getFullName).orElse(null),
                entity.getCaseStatus(),
                entity.getRemarks(),
                entity.getEvidenceFileName(),
                entity.getStatus(),
                entity.getCreatedAt(),
                auditTrail
        );
    }

    private boolean isSuspicious(SecurityEventLog entity) {
        String code = safe(entity.getEventCode()).toUpperCase(Locale.ROOT);
        return entity.getSeverityLevel().ordinal() >= 2
                || List.of("FAILED_LOGIN", "SUSPICIOUS_TXN", "AML_FLAG", "SANCTION_HIT", "MULTIPLE_REVERSAL").contains(code);
    }

    private String resolveActor(String performedBy) {
        return performedBy == null || performedBy.trim().isEmpty() ? SYSTEM_USER : performedBy.trim();
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("\"", "'");
    }

    private Long safeLong(Long value) {
        return value == null ? 0L : value;
    }

    private void sendInvestigationMailToAssignedUser(InvestigationCase entity, String decision, String remarks) {
        if (entity == null || entity.getAssignedTo() == null) {
            return;
        }
        userRepository.findById(entity.getAssignedTo()).ifPresent(user -> {
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                return;
            }
            automatedMailService.sendApprovalDecisionEmail(
                    user.getEmail(),
                    "Investigation Case",
                    entity.getCaseNo(),
                    decision,
                    remarks,
                    "/security/investigations/" + entity.getId(),
                    "Open Investigation"
            );
        });
    }

    private void sendInvestigationClosureMail(InvestigationCase entity) {
        if (entity == null) {
            return;
        }
        if (entity.getAssignedTo() != null) {
            sendInvestigationMailToAssignedUser(entity, "Closed", entity.getRemarks());
        }
        if (entity.getOpenedBy() != null && !entity.getOpenedBy().trim().isEmpty()) {
            userRepository.findByUsername(entity.getOpenedBy()).ifPresent(user -> {
                if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                    return;
                }
                if (entity.getAssignedTo() != null && user.getId().equals(entity.getAssignedTo())) {
                    return;
                }
                automatedMailService.sendApprovalDecisionEmail(
                        user.getEmail(),
                        "Investigation Case",
                        entity.getCaseNo(),
                        "Closed",
                        entity.getRemarks(),
                        "/security/investigations/" + entity.getId(),
                        "Open Investigation"
                );
            });
        }
    }
}
