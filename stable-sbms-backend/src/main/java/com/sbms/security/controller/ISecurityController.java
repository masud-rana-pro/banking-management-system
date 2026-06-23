package com.sbms.security.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.security.dto.response.SecurityDashboardSummaryResponse;
import com.sbms.security.dto.response.SecurityEventResponse;

import java.util.List;

public interface ISecurityController {

    ApiResponse<List<SecurityEventResponse>> listSecurityEvents(String severityLevel, String keyword);

    ApiResponse<SecurityEventResponse> getSecurityEventById(Long id);

    ApiResponse<List<SecurityEventResponse>> listSuspiciousActivities(String keyword);

    ApiResponse<SecurityEventResponse> getSuspiciousActivityById(Long id);

    ApiResponse<SecurityDashboardSummaryResponse> dashboardSummary();
}
