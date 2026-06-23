package com.sbms.branch.service.impl;

import com.sbms.branch.dto.BranchTellerLimitRequestDto;
import com.sbms.branch.dto.BranchTellerLimitResponseDto;
import com.sbms.branch.entity.BranchTellerLimit;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.branch.repository.BranchTellerLimitRepository;
import com.sbms.branch.repository.BranchUserAssignmentRepository;
import com.sbms.branch.service.IBranchTellerLimitService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BranchTellerLimitService implements IBranchTellerLimitService {

    @Autowired
    private BranchTellerLimitRepository tellerLimitRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BranchUserAssignmentRepository assignmentRepository;

    @Override
    public BranchTellerLimitResponseDto create(BranchTellerLimitRequestDto request) {
        validate(request, null);

        BranchTellerLimit entity = new BranchTellerLimit();
        apply(request, entity);

        return toDto(tellerLimitRepository.save(entity));
    }

    @Override
    public BranchTellerLimitResponseDto update(Long id, BranchTellerLimitRequestDto request) {
        BranchTellerLimit entity = tellerLimitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teller limit not found"));

        validate(request, id);
        apply(request, entity);

        return toDto(tellerLimitRepository.save(entity));
    }

    @Override
    public BranchTellerLimitResponseDto getById(Long id) {
        BranchTellerLimit entity = tellerLimitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teller limit not found"));

        return toDto(entity);
    }

    @Override
    public List<BranchTellerLimitResponseDto> getAll(Long branchId, Long userId, String status) {
        return tellerLimitRepository.findAll(branchId, userId, status)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deactivate(Long id) {
        getById(id);
        tellerLimitRepository.deactivate(id);
    }

    private void validate(BranchTellerLimitRequestDto request, Long excludeId) {
        if (request.getBranchId() == null || request.getBranchId() <= 0) {
            throw new BadRequestException("Valid branch is required");
        }

        branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new BadRequestException("Valid teller user is required");
        }

        if (!assignmentRepository.existsActiveTellerAssignment(request.getBranchId(), request.getUserId())) {
            throw new BadRequestException("Teller user must be actively assigned to the selected branch");
        }

        if (request.getLimitDate() == null) {
            throw new BadRequestException("Limit date is required");
        }

        if (isInvalidAmount(request.getDailyDepositLimit())) {
            throw new BadRequestException("Daily deposit limit must be greater than zero");
        }

        if (isInvalidAmount(request.getDailyWithdrawLimit())) {
            throw new BadRequestException("Daily withdraw limit must be greater than zero");
        }

        if (isInvalidAmount(request.getSingleTxnLimit())) {
            throw new BadRequestException("Single transaction limit must be greater than zero");
        }

        if (request.getSingleTxnLimit().compareTo(request.getDailyDepositLimit()) > 0) {
            throw new BadRequestException("Single transaction limit cannot be greater than daily deposit limit");
        }

        if (request.getSingleTxnLimit().compareTo(request.getDailyWithdrawLimit()) > 0) {
            throw new BadRequestException("Single transaction limit cannot be greater than daily withdraw limit");
        }

        if (tellerLimitRepository.existsByBranchUserDate(
                request.getBranchId(),
                request.getUserId(),
                request.getLimitDate(),
                excludeId
        )) {
            throw new BadRequestException("Teller limit already exists for this branch, user and date");
        }
    }

    private boolean isInvalidAmount(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) <= 0;
    }

    private void apply(BranchTellerLimitRequestDto request, BranchTellerLimit entity) {
        entity.setBranchId(request.getBranchId());
        entity.setUserId(request.getUserId());
        entity.setLimitDate(request.getLimitDate());
        entity.setDailyDepositLimit(request.getDailyDepositLimit());
        entity.setDailyWithdrawLimit(request.getDailyWithdrawLimit());
        entity.setSingleTxnLimit(request.getSingleTxnLimit());

        // Handbook অনুযায়ী Approved By form field না।
        // Auth integration হলে এখানে logged-in user id বসবে।
        entity.setApprovedBy(1L);
        entity.setApprovedAt(LocalDateTime.now());

        entity.setStatus(
                request.getStatus() == null || request.getStatus().isBlank()
                        ? "ACTIVE"
                        : request.getStatus()
        );
    }

    private BranchTellerLimitResponseDto toDto(BranchTellerLimit entity) {
        BranchTellerLimitResponseDto dto = new BranchTellerLimitResponseDto();

        dto.setId(entity.getId());
        dto.setBranchId(entity.getBranchId());
        dto.setUserId(entity.getUserId());
        dto.setLimitDate(entity.getLimitDate());
        dto.setDailyDepositLimit(entity.getDailyDepositLimit());
        dto.setDailyWithdrawLimit(entity.getDailyWithdrawLimit());
        dto.setSingleTxnLimit(entity.getSingleTxnLimit());
        dto.setApprovedBy(entity.getApprovedBy());
        dto.setApprovedAt(entity.getApprovedAt());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}