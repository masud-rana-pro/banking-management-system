package com.sbms.profit.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.profit.controller.IProfitScheduleController;
import com.sbms.profit.dto.request.ProfitScheduleRequest;
import com.sbms.profit.dto.response.ProfitScheduleResponse;
import com.sbms.profit.service.IProfitScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profit-schedules")
@RequiresPermission("PROFIT_MANAGEMENT_ACCESS")
public class ProfitScheduleController implements IProfitScheduleController {

    @Autowired
    private IProfitScheduleService profitScheduleService;

    @Override
    @RequiresPermission("PROFIT_SCHEDULE_CREATE")
    @PostMapping("/create")
    public ApiResponse<ProfitScheduleResponse> create(@RequestBody ProfitScheduleRequest request) {
        return ResponseBuilder.success("Profit schedule created successfully", profitScheduleService.create(request));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<ProfitScheduleResponse>> list() {
        return ResponseBuilder.success("Profit schedule list fetched successfully", profitScheduleService.list());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<ProfitScheduleResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Profit schedule fetched successfully", profitScheduleService.getById(id));
    }

    @Override
    @RequiresPermission("PROFIT_SCHEDULE_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<ProfitScheduleResponse> archive(@PathVariable Long id) {
        return ResponseBuilder.success("Profit schedule archived successfully", profitScheduleService.archive(id));
    }

    @Override
    @RequiresPermission("PROFIT_SCHEDULE_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<ProfitScheduleResponse> restore(@PathVariable Long id) {
        return ResponseBuilder.success("Profit schedule restored successfully", profitScheduleService.restore(id));
    }
}
