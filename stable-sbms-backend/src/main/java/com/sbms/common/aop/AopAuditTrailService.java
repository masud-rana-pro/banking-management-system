package com.sbms.common.aop;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.security.entity.AuditLog;
import com.sbms.security.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AopAuditTrailService {

    private final AuditLogRepository auditLogRepository;

    public AopAuditTrailService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String moduleName,
                       Long referenceId,
                       String actionName,
                       String description,
                       String payloadJson,
                       String outcome,
                       Long userId,
                       String username,
                       String ipAddress) {
        AuditLog log = new AuditLog();
        log.setModuleName(defaultValue(moduleName, "SYSTEM"));
        log.setReferenceId(referenceId);
        log.setEntityId(referenceId);
        log.setActionName(defaultValue(actionName, "UNKNOWN_ACTION"));
        log.setAction(defaultValue(actionName, "UNKNOWN_ACTION"));
        log.setDescription(defaultValue(description, actionName));
        log.setNewValueJson(payloadJson);
        log.setOldValueJson(outcome == null ? null : "{\"outcome\":\"" + outcome + "\"}");
        log.setUserId(userId);
        log.setUsername(defaultValue(username, "SYSTEM"));
        log.setPerformedBy(defaultValue(username, "SYSTEM"));
        log.setIpAddress(ipAddress);
        log.setEntityType(defaultValue(moduleName, "SYSTEM"));
        log.setStatus(RecordStatus.ACTIVE);
        auditLogRepository.save(log);
    }

    private String defaultValue(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }
}
