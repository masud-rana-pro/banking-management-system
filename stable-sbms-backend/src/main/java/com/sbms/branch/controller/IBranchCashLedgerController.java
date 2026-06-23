package com.sbms.branch.controller;

import com.sbms.branch.dto.BranchCashLedgerResponseDto;
import com.sbms.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBranchCashLedgerController {

    ApiResponse<List<BranchCashLedgerResponseDto>> getAll(Long branchId, String entryType, String sourceType);

    ResponseEntity<byte[]> previewReport(Long branchId, String entryType, String sourceType);

    ResponseEntity<byte[]> downloadReport(Long branchId, String entryType, String sourceType);
}
