package com.sbms.branch.service;

import com.sbms.branch.dto.BranchRequestDto;
import com.sbms.branch.dto.BranchDashboardSummaryDto;
import com.sbms.branch.dto.BranchResponseDto;

import java.util.List;

public interface IBranchService {

    BranchResponseDto create(BranchRequestDto dto);

    BranchResponseDto update(Long id, BranchRequestDto dto);

    BranchResponseDto getById(Long id);

    List<BranchResponseDto> getAll(String search, String status);

    List<BranchResponseDto> dropdown();
    
    BranchDashboardSummaryDto getDashboardSummary();

    void softDelete(Long id);

    void restore(Long id);
}
