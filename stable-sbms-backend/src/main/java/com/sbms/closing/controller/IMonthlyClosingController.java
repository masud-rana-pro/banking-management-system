package com.sbms.closing.controller;

import com.sbms.closing.dto.request.MonthlyClosingDecisionRequest;
import com.sbms.closing.dto.request.MonthlyClosingRunRequest;
import com.sbms.closing.dto.response.MonthlyClosingDashboardSummaryResponse;
import com.sbms.closing.dto.response.MonthlyClosingRunResponse;
import com.sbms.common.response.ApiResponse;

import java.time.LocalDate;
import java.util.List;

public interface IMonthlyClosingController {
    ApiResponse<MonthlyClosingRunResponse> create(MonthlyClosingRunRequest request);
    ApiResponse<List<MonthlyClosingRunResponse>> list(Long branchId, String status, LocalDate closingMonth);
    ApiResponse<MonthlyClosingRunResponse> getById(Long id);
    ApiResponse<MonthlyClosingRunResponse> submit(Long id);
    ApiResponse<MonthlyClosingRunResponse> approve(Long id, MonthlyClosingDecisionRequest request);
    ApiResponse<MonthlyClosingRunResponse> reject(Long id, MonthlyClosingDecisionRequest request);
    ApiResponse<MonthlyClosingRunResponse> reopen(Long id, MonthlyClosingDecisionRequest request);
    ApiResponse<MonthlyClosingDashboardSummaryResponse> dashboardSummary();
}
