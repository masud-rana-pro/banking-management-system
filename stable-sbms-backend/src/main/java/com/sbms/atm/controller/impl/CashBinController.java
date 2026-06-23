package com.sbms.atm.controller.impl;

import com.sbms.atm.controller.ICashBinController;
import com.sbms.atm.dto.request.CashBinRequest;
import com.sbms.atm.dto.response.CashBinResponse;
import com.sbms.atm.service.ICashBinService;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atm-terminals/cash-bin")
@CrossOrigin(origins = "http://localhost:4200")
@RequiresPermission("ATM_CDM_ACCESS")
public class CashBinController implements ICashBinController {

    @Autowired
    private ICashBinService service;

    @Override
    @RequiresPermission("ATM_CASH_BIN_CREATE")
    @PostMapping("/create")
    public ApiResponse<CashBinResponse> create(@RequestBody CashBinRequest request) {
        return ResponseBuilder.success("Cash bin created successfully", service.create(request));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<CashBinResponse>> list() {
        return ResponseBuilder.success("Cash bin list loaded successfully", service.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<CashBinResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Cash bin loaded successfully", service.getById(id));
    }

    @Override
    @RequiresPermission("ATM_CASH_BIN_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<CashBinResponse> update(
            @PathVariable Long id,
            @RequestBody CashBinRequest request
    ) {
        return ResponseBuilder.success("Cash bin updated successfully", service.update(id, request));
    }

    @Override
    @RequiresPermission("ATM_CASH_BIN_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<CashBinResponse> archive(@PathVariable Long id) {
        return ResponseBuilder.success("Cash bin archived successfully", service.archive(id));
    }

    @Override
    @RequiresPermission("ATM_CASH_BIN_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<CashBinResponse> restore(@PathVariable Long id) {
        return ResponseBuilder.success("Cash bin restored successfully", service.restore(id));
    }

    @Override
    @GetMapping("/terminal/{terminalId}")
    public ApiResponse<List<CashBinResponse>> byTerminal(@PathVariable Long terminalId) {
        return ResponseBuilder.success("Terminal cash bins loaded successfully", service.getByTerminal(terminalId));
    }

    @Override
    @GetMapping("/{id}/profile/preview")
    public ResponseEntity<byte[]> previewProfile(@PathVariable Long id) {
        return service.previewProfile(id);
    }

    @Override
    @GetMapping("/{id}/profile/download")
    public ResponseEntity<byte[]> downloadProfile(@PathVariable Long id) {
        return service.downloadProfile(id);
    }
}
