package com.sbms.closing.service;

import com.sbms.closing.dto.request.MonthlyClosingDecisionRequest;
import com.sbms.closing.dto.request.MonthlyClosingRunRequest;
import com.sbms.closing.dto.response.MonthlyClosingDashboardSummaryResponse;
import com.sbms.closing.dto.response.MonthlyClosingRunResponse;

import java.time.LocalDate;
import java.util.List;

public interface IMonthlyClosingService {
    MonthlyClosingRunResponse createOrRefresh(MonthlyClosingRunRequest request, String username);
    List<MonthlyClosingRunResponse> list(Long branchId, String status, LocalDate closingMonth);
    MonthlyClosingRunResponse getById(Long id);
    MonthlyClosingRunResponse submit(Long id, String username);
    MonthlyClosingRunResponse approve(Long id, MonthlyClosingDecisionRequest request, String username);
    MonthlyClosingRunResponse reject(Long id, MonthlyClosingDecisionRequest request, String username);
    MonthlyClosingRunResponse reopen(Long id, MonthlyClosingDecisionRequest request, String username);
    MonthlyClosingDashboardSummaryResponse dashboardSummary();
}
