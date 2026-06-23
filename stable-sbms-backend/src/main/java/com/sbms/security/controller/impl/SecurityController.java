package com.sbms.security.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.security.controller.IAuditLogController;
import com.sbms.security.controller.IInvestigationController;
import com.sbms.security.controller.ISecurityController;
import com.sbms.security.dto.request.InvestigationCaseActionRequest;
import com.sbms.security.dto.response.AuditLogResponse;
import com.sbms.security.dto.response.InvestigationCaseResponse;
import com.sbms.security.dto.response.SecurityDashboardSummaryResponse;
import com.sbms.security.dto.response.SecurityEventResponse;
import com.sbms.security.service.ISecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("SECURITY_AUDIT_ACCESS")
public class SecurityController implements ISecurityController, IAuditLogController, IInvestigationController {

    @Autowired
    private ISecurityService securityService;

    @Override
    @GetMapping("/events/list")
    public ApiResponse<List<SecurityEventResponse>> listSecurityEvents(
            @RequestParam(required = false) String severityLevel,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Security events fetched successfully",
                securityService.getSecurityEvents(severityLevel, keyword));
    }

    @Override
    @GetMapping("/events/{id}")
    public ApiResponse<SecurityEventResponse> getSecurityEventById(@PathVariable Long id) {
        return ResponseBuilder.success("Security event fetched successfully",
                securityService.getSecurityEventById(id));
    }

    @Override
    @GetMapping("/suspicious-activities/list")
    public ApiResponse<List<SecurityEventResponse>> listSuspiciousActivities(@RequestParam(required = false) String keyword) {
        return ResponseBuilder.success("Suspicious activities fetched successfully",
                securityService.getSuspiciousActivities(keyword));
    }

    @Override
    @GetMapping("/suspicious-activities/{id}")
    public ApiResponse<SecurityEventResponse> getSuspiciousActivityById(@PathVariable Long id) {
        return ResponseBuilder.success("Suspicious activity fetched successfully",
                securityService.getSuspiciousActivityById(id));
    }

    @Override
    @GetMapping("/audit-logs/list")
    public ApiResponse<List<AuditLogResponse>> listAuditLogs(
            @RequestParam(required = false) String moduleName,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Audit logs fetched successfully",
                securityService.getAuditLogs(moduleName, keyword));
    }

    @Override
    @GetMapping("/audit-logs/{id}")
    public ApiResponse<AuditLogResponse> getAuditLogById(@PathVariable Long id) {
        return ResponseBuilder.success("Audit log fetched successfully",
                securityService.getAuditLogById(id));
    }

    @Override
    @GetMapping("/investigation-cases/list")
    public ApiResponse<List<InvestigationCaseResponse>> listInvestigationCases(
            @RequestParam(required = false) String caseStatus,
            @RequestParam(required = false) String caseType,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Investigation cases fetched successfully",
                securityService.getInvestigationCases(caseStatus, caseType, keyword));
    }

    @Override
    @GetMapping("/investigation-cases/{id}")
    public ApiResponse<InvestigationCaseResponse> getInvestigationCaseById(@PathVariable Long id) {
        return ResponseBuilder.success("Investigation case fetched successfully",
                securityService.getInvestigationCaseById(id));
    }

    @Override
    @RequiresPermission("SECURITY_INVESTIGATION_ASSIGN")
    @PostMapping("/investigation-cases/{id}/assign")
    public ApiResponse<InvestigationCaseResponse> assignInvestigationCase(
            @PathVariable Long id,
            @RequestBody InvestigationCaseActionRequest request
    ) {
        return ResponseBuilder.success("Investigation case assigned successfully",
                securityService.assignInvestigationCase(id, request));
    }

    @Override
    @RequiresPermission("SECURITY_INVESTIGATION_CLOSE")
    @PostMapping("/investigation-cases/{id}/close")
    public ApiResponse<InvestigationCaseResponse> closeInvestigationCase(
            @PathVariable Long id,
            @RequestBody InvestigationCaseActionRequest request
    ) {
        return ResponseBuilder.success("Investigation case closed successfully",
                securityService.closeInvestigationCase(id, request));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<SecurityDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Security dashboard summary fetched successfully",
                securityService.getDashboardSummary());
    }
}
