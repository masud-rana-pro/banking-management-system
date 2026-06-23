package com.sbms.branch.service;

import com.sbms.branch.dto.BranchTellerLimitRequestDto;
import com.sbms.branch.dto.BranchTellerLimitResponseDto;

import java.util.List;

public interface IBranchTellerLimitService {

    BranchTellerLimitResponseDto create(BranchTellerLimitRequestDto request);

    BranchTellerLimitResponseDto update(Long id, BranchTellerLimitRequestDto request);

    BranchTellerLimitResponseDto getById(Long id);

    List<BranchTellerLimitResponseDto> getAll(Long branchId, Long userId, String status);

    void deactivate(Long id);
}