package com.sbms.closing.controller.impl;

import com.sbms.closing.controller.IMonthlyClosingController;
import com.sbms.closing.dto.request.MonthlyClosingDecisionRequest;
import com.sbms.closing.dto.request.MonthlyClosingRunRequest;
import com.sbms.closing.dto.response.MonthlyClosingDashboardSummaryResponse;
import com.sbms.closing.dto.response.MonthlyClosingRunResponse;
import com.sbms.closing.service.IMonthlyClosingService;
import com.sbms.common.aop.AopRequestContext;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/monthly-closing-runs")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("REPORTING_REGULATORY_ACCESS")
public class MonthlyClosingController implements IMonthlyClosingController {

    private final IMonthlyClosingService monthlyClosingService;

    public MonthlyClosingController(IMonthlyClosingService monthlyClosingService) {
        this.monthlyClosingService = monthlyClosingService;
    }

    @Override
    @RequiresPermission("MONTHLY_CLOSING_CREATE")
    @PostMapping("/create")
    public ApiResponse<MonthlyClosingRunResponse> create(@RequestBody MonthlyClosingRunRequest request) {
        return ResponseBuilder.success("Monthly closing run saved successfully",
                monthlyClosingService.createOrRefresh(request, actor("SYSTEM_MONTH_END")));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<MonthlyClosingRunResponse>> list(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate closingMonth
    ) {
        return ResponseBuilder.success("Monthly closing run list fetched successfully",
                monthlyClosingService.list(branchId, status, closingMonth));
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<MonthlyClosingRunResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Monthly closing run fetched successfully",
                monthlyClosingService.getById(id));
    }

    @Override
    @RequiresPermission("MONTHLY_CLOSING_SUBMIT")
    @PostMapping("/{id}/submit")
    public ApiResponse<MonthlyClosingRunResponse> submit(@PathVariable Long id) {
        return ResponseBuilder.success("Monthly closing run submitted successfully",
                monthlyClosingService.submit(id, actor("SYSTEM_MONTH_END")));
    }

    @Override
    @RequiresPermission("MONTHLY_CLOSING_APPROVE")
    @PostMapping("/{id}/approve")
    public ApiResponse<MonthlyClosingRunResponse> approve(@PathVariable Long id,
                                                          @RequestBody(required = false) MonthlyClosingDecisionRequest request) {
        return ResponseBuilder.success("Monthly closing run approved successfully",
                monthlyClosingService.approve(id, request, actor("SYSTEM_MONTH_END")));
    }

    @Override
    @RequiresPermission("MONTHLY_CLOSING_REJECT")
    @PostMapping("/{id}/reject")
    public ApiResponse<MonthlyClosingRunResponse> reject(@PathVariable Long id,
                                                         @RequestBody(required = false) MonthlyClosingDecisionRequest request) {
        return ResponseBuilder.success("Monthly closing run rejected successfully",
                monthlyClosingService.reject(id, request, actor("SYSTEM_MONTH_END")));
    }

    @Override
    @RequiresPermission("MONTHLY_CLOSING_REOPEN")
    @PostMapping("/{id}/reopen")
    public ApiResponse<MonthlyClosingRunResponse> reopen(@PathVariable Long id,
                                                         @RequestBody(required = false) MonthlyClosingDecisionRequest request) {
        return ResponseBuilder.success("Monthly closing run reopened successfully",
                monthlyClosingService.reopen(id, request, actor("SYSTEM_MONTH_END")));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<MonthlyClosingDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Monthly closing dashboard summary fetched successfully",
                monthlyClosingService.dashboardSummary());
    }

    private String actor(String fallback) {
        String username = AopRequestContext.currentUsername();
        return username == null || username.trim().isEmpty() ? fallback : username.trim();
    }
}
