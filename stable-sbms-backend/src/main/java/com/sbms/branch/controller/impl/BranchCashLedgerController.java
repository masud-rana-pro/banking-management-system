package com.sbms.branch.controller.impl;

import com.sbms.branch.controller.IBranchCashLedgerController;
import com.sbms.branch.dto.BranchCashLedgerResponseDto;
import com.sbms.branch.service.IBranchCashLedgerService;
import com.sbms.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches/cash-ledger")
@CrossOrigin(origins = "http://localhost:4200")
public class BranchCashLedgerController implements IBranchCashLedgerController {

    @Autowired
    private IBranchCashLedgerService ledgerService;

    @Override
    @GetMapping
    public ApiResponse<List<BranchCashLedgerResponseDto>> getAll(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String entryType,
            @RequestParam(required = false) String sourceType
    ) {
        return ApiResponse.success(
                "Branch cash ledger loaded successfully",
                ledgerService.getAll(branchId, entryType, sourceType)
        );
    }

    @Override
    @GetMapping("/report/preview")
    public ResponseEntity<byte[]> previewReport(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String entryType,
            @RequestParam(required = false) String sourceType
    ) {
        return ledgerService.previewReport(branchId, entryType, sourceType);
    }

    @Override
    @GetMapping("/report/download")
    public ResponseEntity<byte[]> downloadReport(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String entryType,
            @RequestParam(required = false) String sourceType
    ) {
        return ledgerService.downloadReport(branchId, entryType, sourceType);
    }
}
