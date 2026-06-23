package com.sbms.branch.controller.impl;

import com.sbms.branch.controller.IVaultBalanceController;
import com.sbms.branch.dto.VaultBalanceRequestDto;
import com.sbms.branch.dto.VaultBalanceResponseDto;
import com.sbms.branch.dto.VaultCloseRequestDto;
import com.sbms.branch.service.IVaultBalanceService;
import com.sbms.common.response.ApiResponse;
import com.sbms.config.RequiresPermission;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches/vault")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("BRANCH_MANAGEMENT_ACCESS")
public class VaultBalanceController implements IVaultBalanceController {

    @Autowired
    private IVaultBalanceService vaultBalanceService;

    @Override
    @RequiresPermission("BRANCH_VAULT_MANAGE")
    @PostMapping("/open")
    public ApiResponse<VaultBalanceResponseDto> open(@Valid @RequestBody VaultBalanceRequestDto request) {
        return ApiResponse.success("Vault opened successfully", vaultBalanceService.open(request));
    }

    @Override
    @RequiresPermission("BRANCH_VAULT_MANAGE")
    @PostMapping({"/{id}/close", "/close/{id}"})
    public ApiResponse<VaultBalanceResponseDto> close(
            @PathVariable Long id,
            @Valid @RequestBody VaultCloseRequestDto request
    ) {
        return ApiResponse.success("Vault closed successfully", vaultBalanceService.close(id, request));
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<VaultBalanceResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success("Vault balance loaded successfully", vaultBalanceService.getById(id));
    }

    @Override
    @GetMapping("/{id}/report/preview")
    public ResponseEntity<byte[]> previewReport(@PathVariable Long id) {
        return vaultBalanceService.previewReport(id);
    }

    @Override
    @GetMapping("/{id}/report/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) {
        return vaultBalanceService.downloadReport(id);
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<VaultBalanceResponseDto>> getAll(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isClosed
    ) {
        return ApiResponse.success(
                "Vault balance list loaded successfully",
                vaultBalanceService.getAll(branchId, status, isClosed)
        );
    }
}
