package com.sbms.profit.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.profit.dto.response.ProfitDashboardSummaryResponse;
import com.sbms.profit.service.IProfitPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profit")
public class ProfitDashboardController {

    @Autowired
    private IProfitPostingService profitPostingService;

    @GetMapping("/dashboard-summary")
    public ApiResponse<ProfitDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Profit dashboard summary fetched successfully", profitPostingService.dashboardSummary());
    }
}
