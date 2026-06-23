package com.sbms.branch.controller;

import com.sbms.branch.dto.VaultBalanceRequestDto;
import com.sbms.branch.dto.VaultBalanceResponseDto;
import com.sbms.branch.dto.VaultCloseRequestDto;
import com.sbms.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IVaultBalanceController {

    ApiResponse<VaultBalanceResponseDto> open(VaultBalanceRequestDto request);

    ApiResponse<VaultBalanceResponseDto> close(Long id, VaultCloseRequestDto request);

    ApiResponse<VaultBalanceResponseDto> getById(Long id);

    ResponseEntity<byte[]> previewReport(Long id);

    ResponseEntity<byte[]> downloadReport(Long id);

    ApiResponse<List<VaultBalanceResponseDto>> getAll(Long branchId, String status, Boolean isClosed);
}
