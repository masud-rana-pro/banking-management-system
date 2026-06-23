package com.sbms.branch.service;

import com.sbms.branch.dto.BranchCashLedgerResponseDto;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface IBranchCashLedgerService {

    List<BranchCashLedgerResponseDto> getAll(Long branchId, String entryType, String sourceType);

    ResponseEntity<byte[]> previewReport(Long branchId, String entryType, String sourceType);

    ResponseEntity<byte[]> downloadReport(Long branchId, String entryType, String sourceType);
}
