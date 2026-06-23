package com.sbms.financing.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.financing.controller.IFinancingDashboardController;
import com.sbms.financing.dto.response.FinancingDashboardSummaryResponse;
import com.sbms.financing.service.IFinancingApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/financing")
public class FinancingDashboardController implements IFinancingDashboardController {

    @Autowired
    private IFinancingApplicationService applicationService;

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<FinancingDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Financing dashboard summary fetched successfully", applicationService.getDashboardSummary());
    }
}
