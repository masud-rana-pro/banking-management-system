package com.sbms.branch.service;

import com.sbms.branch.dto.VaultBalanceRequestDto;
import com.sbms.branch.dto.VaultBalanceResponseDto;
import com.sbms.branch.dto.VaultCloseRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IVaultBalanceService {

    VaultBalanceResponseDto open(VaultBalanceRequestDto request);

    VaultBalanceResponseDto close(Long id, VaultCloseRequestDto request);

    VaultBalanceResponseDto getById(Long id);

    ResponseEntity<byte[]> previewReport(Long id);

    ResponseEntity<byte[]> downloadReport(Long id);

    List<VaultBalanceResponseDto> getAll(Long branchId, String status, Boolean isClosed);
}
