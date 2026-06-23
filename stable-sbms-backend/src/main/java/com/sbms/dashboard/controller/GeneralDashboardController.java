package com.sbms.dashboard.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.config.RequiresPermission;
import com.sbms.dashboard.dto.GeneralDashboardResponse;
import com.sbms.dashboard.service.IGeneralDashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("ADMIN_DASHBOARD_ACCESS")
public class GeneralDashboardController {

    private final IGeneralDashboardService dashboardService;

    public GeneralDashboardController(IGeneralDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/general-overview")
    public ApiResponse<GeneralDashboardResponse> generalOverview(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String window
    ) {
        return ApiResponse.success(
                "General banking dashboard overview loaded successfully",
                dashboardService.getOverview(branchId, dateFrom, dateTo, window)
        );
    }
}

