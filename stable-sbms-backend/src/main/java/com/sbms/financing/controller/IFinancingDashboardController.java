package com.sbms.financing.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.financing.dto.response.FinancingDashboardSummaryResponse;

public interface IFinancingDashboardController {
    ApiResponse<FinancingDashboardSummaryResponse> dashboardSummary();
}
