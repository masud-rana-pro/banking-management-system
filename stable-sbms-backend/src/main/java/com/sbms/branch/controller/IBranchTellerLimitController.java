package com.sbms.branch.controller;

import com.sbms.branch.dto.BranchTellerLimitRequestDto;
import com.sbms.branch.dto.BranchTellerLimitResponseDto;
import com.sbms.common.response.ApiResponse;

import java.util.List;

public interface IBranchTellerLimitController {

    ApiResponse<BranchTellerLimitResponseDto> create(BranchTellerLimitRequestDto request);

    ApiResponse<BranchTellerLimitResponseDto> update(Long id, BranchTellerLimitRequestDto request);

    ApiResponse<BranchTellerLimitResponseDto> getById(Long id);

    ApiResponse<List<BranchTellerLimitResponseDto>> getAll(Long branchId, Long userId, String status);

    ApiResponse<Void> deactivate(Long id);
}