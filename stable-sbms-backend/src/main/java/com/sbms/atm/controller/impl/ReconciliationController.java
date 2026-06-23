package com.sbms.atm.controller.impl;

import com.sbms.atm.controller.IReconciliationController;
import com.sbms.atm.dto.request.ReconciliationRequest;
import com.sbms.atm.dto.response.ReconciliationResponse;
import com.sbms.atm.service.IReconciliationService;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atm-terminals/reconciliation")
@CrossOrigin(origins = "http://localhost:4200")
@RequiresPermission("ATM_CDM_ACCESS")
public class ReconciliationController implements IReconciliationController {

    @Autowired
    private IReconciliationService service;

    @Override
    @RequiresPermission("ATM_RECONCILIATION_CREATE")
    @PostMapping("/create")
    public ApiResponse<ReconciliationResponse> create(@RequestBody ReconciliationRequest request) {
        return ResponseBuilder.success("Reconciliation completed successfully", service.create(request));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<ReconciliationResponse>> list() {
        return ResponseBuilder.success("Reconciliation list loaded successfully", service.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<ReconciliationResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Reconciliation loaded successfully", service.getById(id));
    }

    @Override
    @GetMapping("/terminal/{terminalId}")
    public ApiResponse<List<ReconciliationResponse>> byTerminal(@PathVariable Long terminalId) {
        return ResponseBuilder.success("Terminal reconciliation list loaded successfully", service.getByTerminal(terminalId));
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
