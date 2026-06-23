package com.sbms.branch.service;

import com.sbms.branch.dto.BranchUserAssignmentRequestDto;
import com.sbms.branch.dto.BranchUserAssignmentResponseDto;

import java.util.List;

public interface IBranchUserAssignmentService {

    BranchUserAssignmentResponseDto create(BranchUserAssignmentRequestDto request);

    BranchUserAssignmentResponseDto update(Long id, BranchUserAssignmentRequestDto request);

    BranchUserAssignmentResponseDto getById(Long id);

    List<BranchUserAssignmentResponseDto> getAll(Long branchId, Long userId, String status);

    void deactivate(Long id);
}