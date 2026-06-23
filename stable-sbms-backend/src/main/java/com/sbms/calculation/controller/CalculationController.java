package com.sbms.calculation.controller;

import com.sbms.calculation.dto.request.CalculationSimulateRequest;
import com.sbms.calculation.dto.response.CalculationDashboardSummaryResponse;
import com.sbms.calculation.dto.response.CalculationSimulationResponse;
import com.sbms.calculation.service.CalculationEngineService;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculations")
@RequiresPermission("CALCULATION_ENGINE_ACCESS")
public class CalculationController {

    private final CalculationEngineService calculationEngineService;

    public CalculationController(CalculationEngineService calculationEngineService) {
        this.calculationEngineService = calculationEngineService;
    }

    @GetMapping("/dashboard-summary")
    public ApiResponse<CalculationDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Calculation dashboard summary fetched successfully", calculationEngineService.dashboardSummary());
    }

    @RequiresPermission("CALCULATION_SIMULATE")
    @PostMapping("/simulate")
    public ApiResponse<CalculationSimulationResponse> simulate(@RequestBody CalculationSimulateRequest request) {
        return ResponseBuilder.success("Calculation simulation completed successfully", calculationEngineService.simulate(request));
    }
}
