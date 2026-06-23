package com.sbms.branch.controller;

import com.sbms.branch.dto.BranchRequestDto;
import com.sbms.branch.dto.BranchResponseDto;
import com.sbms.common.response.ApiResponse;
import com.sbms.branch.dto.BranchDashboardSummaryDto;

import java.util.List;

public interface IBranchController {

    ApiResponse<BranchResponseDto> create(BranchRequestDto request);

    ApiResponse<BranchResponseDto> update(Long id, BranchRequestDto request);

    ApiResponse<BranchResponseDto> getById(Long id);

    ApiResponse<List<BranchResponseDto>> getAll(String search, String status);

    ApiResponse<List<BranchResponseDto>> dropdown();
    
    ApiResponse<BranchDashboardSummaryDto> getDashboardSummary();

    ApiResponse<Void> softDelete(Long id);

    ApiResponse<Void> restore(Long id);
}
