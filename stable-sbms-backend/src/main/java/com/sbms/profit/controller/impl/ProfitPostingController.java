package com.sbms.profit.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.common.aop.AopRequestContext;
import com.sbms.config.RequiresPermission;
import com.sbms.profit.controller.IProfitPostingController;
import com.sbms.profit.dto.request.ProfitPostingRunRequest;
import com.sbms.profit.dto.response.ProfitPostingResponse;
import com.sbms.profit.dto.response.ProfitPostingRunResponse;
import com.sbms.profit.service.IProfitPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profit-postings")
@RequiresPermission("PROFIT_MANAGEMENT_ACCESS")
public class ProfitPostingController implements IProfitPostingController {

    @Autowired
    private IProfitPostingService profitPostingService;

    @Override
    @RequiresPermission("PROFIT_POSTING_RUN")
    @PostMapping("/run")
    public ApiResponse<ProfitPostingRunResponse> run(@RequestBody(required = false) ProfitPostingRunRequest request) {
        return ResponseBuilder.success("Profit posting run completed successfully", profitPostingService.run(request, actor("SYSTEM_PROFIT_ENGINE")));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<ProfitPostingResponse>> list() {
        return ResponseBuilder.success("Profit posting list fetched successfully", profitPostingService.list());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<ProfitPostingResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Profit posting fetched successfully", profitPostingService.getById(id));
    }

    @Override
    @GetMapping("/{id}/advice/preview")
    public ResponseEntity<byte[]> previewAdvice(@PathVariable Long id) {
        return profitPostingService.previewAdvice(id);
    }

    @Override
    @GetMapping("/{id}/advice/download")
    public ResponseEntity<byte[]> downloadAdvice(@PathVariable Long id) {
        return profitPostingService.downloadAdvice(id);
    }

    private String actor(String fallback) {
        String username = AopRequestContext.currentUsername();
        return username == null || username.trim().isEmpty() ? fallback : username.trim();
    }
}
