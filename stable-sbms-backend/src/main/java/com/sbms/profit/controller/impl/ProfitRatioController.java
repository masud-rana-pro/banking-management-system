package com.sbms.profit.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.profit.controller.IProfitRatioController;
import com.sbms.profit.dto.request.ProfitRatioRequest;
import com.sbms.profit.dto.response.ProfitRatioDropdownResponse;
import com.sbms.profit.dto.response.ProfitRatioResponse;
import com.sbms.profit.service.IProfitRatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profit-ratios")
@RequiresPermission("PROFIT_MANAGEMENT_ACCESS")
public class ProfitRatioController implements IProfitRatioController {

    @Autowired
    private IProfitRatioService profitRatioService;

    @Override
    @RequiresPermission("PROFIT_RATIO_CREATE")
    @PostMapping("/create")
    public ApiResponse<ProfitRatioResponse> create(@RequestBody ProfitRatioRequest request) {
        return ResponseBuilder.success("Profit ratio created successfully", profitRatioService.create(request));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<ProfitRatioResponse>> list() {
        return ResponseBuilder.success("Profit ratio list fetched successfully", profitRatioService.list());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<ProfitRatioResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Profit ratio fetched successfully", profitRatioService.getById(id));
    }

    @Override
    @RequiresPermission("PROFIT_RATIO_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<ProfitRatioResponse> update(@PathVariable Long id, @RequestBody ProfitRatioRequest request) {
        return ResponseBuilder.success("Profit ratio updated successfully", profitRatioService.update(id, request));
    }

    @Override
    @RequiresPermission("PROFIT_RATIO_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<ProfitRatioResponse> archive(@PathVariable Long id) {
        return ResponseBuilder.success("Profit ratio archived successfully", profitRatioService.archive(id));
    }

    @Override
    @RequiresPermission("PROFIT_RATIO_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<ProfitRatioResponse> restore(@PathVariable Long id) {
        return ResponseBuilder.success("Profit ratio restored successfully", profitRatioService.restore(id));
    }

    @Override
    @GetMapping("/dropdown")
    public ApiResponse<List<ProfitRatioDropdownResponse>> dropdown() {
        return ResponseBuilder.success("Profit ratio dropdown fetched successfully", profitRatioService.dropdown());
    }
}
