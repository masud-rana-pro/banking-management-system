package com.sbms.branch.controller;

import com.sbms.branch.dto.BranchUserAssignmentRequestDto;
import com.sbms.branch.dto.BranchUserAssignmentResponseDto;
import com.sbms.common.response.ApiResponse;

import java.util.List;

public interface IBranchUserAssignmentController {

    ApiResponse<BranchUserAssignmentResponseDto> create(BranchUserAssignmentRequestDto request);

    ApiResponse<BranchUserAssignmentResponseDto> update(Long id, BranchUserAssignmentRequestDto request);

    ApiResponse<BranchUserAssignmentResponseDto> getById(Long id);

    ApiResponse<List<BranchUserAssignmentResponseDto>> getAll(Long branchId, Long userId, String status);

    ApiResponse<Void> deactivate(Long id);
}