package com.sbms.security.dto.response;

import java.util.List;

public class SecurityDashboardSummaryResponse {

    private Long failedLoginsToday;
    private Long lockedUsers;
    private Long suspiciousTxnCount;
    private Long auditEventsToday;
    private Long openInvestigationCases;
    private Long amlFlagsToday;
    private List<SecurityEventResponse> recentSecurityEvents;
    private List<InvestigationCaseResponse> recentInvestigationCases;
    private List<AuditLogResponse> recentAuditLogs;

    public SecurityDashboardSummaryResponse(Long failedLoginsToday, Long lockedUsers, Long suspiciousTxnCount,
                                            Long auditEventsToday, Long openInvestigationCases, Long amlFlagsToday,
                                            List<SecurityEventResponse> recentSecurityEvents,
                                            List<InvestigationCaseResponse> recentInvestigationCases,
                                            List<AuditLogResponse> recentAuditLogs) {
        this.failedLoginsToday = failedLoginsToday;
        this.lockedUsers = lockedUsers;
        this.suspiciousTxnCount = suspiciousTxnCount;
        this.auditEventsToday = auditEventsToday;
        this.openInvestigationCases = openInvestigationCases;
        this.amlFlagsToday = amlFlagsToday;
        this.recentSecurityEvents = recentSecurityEvents;
        this.recentInvestigationCases = recentInvestigationCases;
        this.recentAuditLogs = recentAuditLogs;
    }

    public Long getFailedLoginsToday() { return failedLoginsToday; }
    public Long getLockedUsers() { return lockedUsers; }
    public Long getSuspiciousTxnCount() { return suspiciousTxnCount; }
    public Long getAuditEventsToday() { return auditEventsToday; }
    public Long getOpenInvestigationCases() { return openInvestigationCases; }
    public Long getAmlFlagsToday() { return amlFlagsToday; }
    public List<SecurityEventResponse> getRecentSecurityEvents() { return recentSecurityEvents; }
    public List<InvestigationCaseResponse> getRecentInvestigationCases() { return recentInvestigationCases; }
    public List<AuditLogResponse> getRecentAuditLogs() { return recentAuditLogs; }
}
