package com.sbms.atm.controller.impl;

import com.sbms.atm.controller.IAtmTerminalController;
import com.sbms.atm.dto.request.AtmTerminalRequest;
import com.sbms.atm.dto.response.AtmDashboardSummaryResponse;
import com.sbms.atm.dto.response.AtmTerminalDropdownResponse;
import com.sbms.atm.dto.response.AtmTerminalResponse;
import com.sbms.atm.dto.response.DeviceJournalResponse;
import com.sbms.atm.service.IAtmTerminalService;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atm-terminals")
@CrossOrigin(origins = "http://localhost:4200")
@RequiresPermission("ATM_CDM_ACCESS")
public class AtmTerminalController implements IAtmTerminalController {

    @Autowired
    private IAtmTerminalService service;

    @Override
    @RequiresPermission("ATM_TERMINAL_CREATE")
    @PostMapping("/create")
    public ApiResponse<AtmTerminalResponse> create(@RequestBody AtmTerminalRequest request) {
        return ResponseBuilder.success("ATM/CDM terminal created successfully", service.create(request));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<AtmTerminalResponse>> list() {
        return ResponseBuilder.success("ATM/CDM terminal list loaded successfully", service.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<AtmTerminalResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("ATM/CDM terminal loaded successfully", service.getById(id));
    }

    @Override
    @RequiresPermission("ATM_TERMINAL_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<AtmTerminalResponse> update(
            @PathVariable Long id,
            @RequestBody AtmTerminalRequest request
    ) {
        return ResponseBuilder.success("ATM/CDM terminal updated successfully", service.update(id, request));
    }

    @Override
    @RequiresPermission("ATM_TERMINAL_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<AtmTerminalResponse> archive(@PathVariable Long id) {
        return ResponseBuilder.success("ATM/CDM terminal archived successfully", service.archive(id));
    }

    @Override
    @RequiresPermission("ATM_TERMINAL_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<AtmTerminalResponse> restore(@PathVariable Long id) {
        return ResponseBuilder.success("ATM/CDM terminal restored successfully", service.restore(id));
    }

    @Override
    @GetMapping("/dropdown")
    public ApiResponse<List<AtmTerminalDropdownResponse>> dropdown() {
        return ResponseBuilder.success("ATM/CDM terminal dropdown loaded successfully", service.dropdown());
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<AtmDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("ATM/CDM dashboard summary loaded successfully", service.dashboardSummary());
    }

    @Override
    @GetMapping("/device-journal")
    public ApiResponse<List<DeviceJournalResponse>> deviceJournal(
            @RequestParam(required = false) Long terminalId
    ) {
        return ResponseBuilder.success("ATM/CDM device journal loaded successfully", service.deviceJournal(terminalId));
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
