package com.sbms.atm.controller.impl;

import com.sbms.atm.controller.IReplenishmentController;
import com.sbms.atm.dto.request.ReplenishmentRequest;
import com.sbms.atm.dto.response.ReplenishmentResponse;
import com.sbms.atm.service.IReplenishmentService;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atm-terminals/replenishment")
@CrossOrigin(origins = "http://localhost:4200")
@RequiresPermission("ATM_CDM_ACCESS")
public class ReplenishmentController implements IReplenishmentController {

    @Autowired
    private IReplenishmentService service;

    @Override
    @RequiresPermission("ATM_REPLENISHMENT_CREATE")
    @PostMapping("/create")
    public ApiResponse<ReplenishmentResponse> create(@RequestBody ReplenishmentRequest request) {
        return ResponseBuilder.success("Replenishment completed successfully", service.create(request));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<ReplenishmentResponse>> list() {
        return ResponseBuilder.success("Replenishment list loaded successfully", service.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<ReplenishmentResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Replenishment loaded successfully", service.getById(id));
    }

    @Override
    @GetMapping("/terminal/{terminalId}")
    public ApiResponse<List<ReplenishmentResponse>> byTerminal(@PathVariable Long terminalId) {
        return ResponseBuilder.success("Terminal replenishments loaded successfully", service.getByTerminal(terminalId));
    }

    @Override
    @GetMapping("/{id}/report/preview")
    public ResponseEntity<byte[]> previewReport(@PathVariable Long id) {
        return service.previewReport(id);
    }

    @Override
    @GetMapping("/{id}/report/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) {
        return service.downloadReport(id);
    }
}
