package com.sbms.security.service;

import com.sbms.security.dto.request.InvestigationCaseActionRequest;
import com.sbms.security.dto.response.AuditLogResponse;
import com.sbms.security.dto.response.InvestigationCaseResponse;
import com.sbms.security.dto.response.SecurityDashboardSummaryResponse;
import com.sbms.security.dto.response.SecurityEventResponse;

import java.util.List;

public interface ISecurityService {

    List<SecurityEventResponse> getSecurityEvents(String severityLevel, String keyword);

    SecurityEventResponse getSecurityEventById(Long id);

    List<SecurityEventResponse> getSuspiciousActivities(String keyword);

    SecurityEventResponse getSuspiciousActivityById(Long id);

    List<AuditLogResponse> getAuditLogs(String moduleName, String keyword);

    AuditLogResponse getAuditLogById(Long id);

    List<InvestigationCaseResponse> getInvestigationCases(String caseStatus, String caseType, String keyword);

    InvestigationCaseResponse getInvestigationCaseById(Long id);

    InvestigationCaseResponse assignInvestigationCase(Long id, InvestigationCaseActionRequest request);

    InvestigationCaseResponse closeInvestigationCase(Long id, InvestigationCaseActionRequest request);

    SecurityDashboardSummaryResponse getDashboardSummary();
}
