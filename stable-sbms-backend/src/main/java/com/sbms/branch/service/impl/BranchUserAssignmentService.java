package com.sbms.branch.service.impl;

import com.sbms.branch.dto.BranchUserAssignmentRequestDto;
import com.sbms.branch.dto.BranchUserAssignmentResponseDto;
import com.sbms.branch.entity.BranchUserAssignment;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.branch.repository.BranchUserAssignmentRepository;
import com.sbms.branch.service.IBranchUserAssignmentService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BranchUserAssignmentService implements IBranchUserAssignmentService {

    @Autowired
    private BranchUserAssignmentRepository assignmentRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Override
    public BranchUserAssignmentResponseDto create(BranchUserAssignmentRequestDto request) {
        validate(request, null);

        BranchUserAssignment assignment = new BranchUserAssignment();
        apply(request, assignment);

        return toDto(assignmentRepository.save(assignment));
    }

    @Override
    public BranchUserAssignmentResponseDto update(Long id, BranchUserAssignmentRequestDto request) {
        BranchUserAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch user assignment not found"));

        validate(request, id);
        apply(request, assignment);

        return toDto(assignmentRepository.save(assignment));
    }

    @Override
    public BranchUserAssignmentResponseDto getById(Long id) {
        BranchUserAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch user assignment not found"));

        return toDto(assignment);
    }

    @Override
    public List<BranchUserAssignmentResponseDto> getAll(Long branchId, Long userId, String status) {
        return assignmentRepository.findAll(branchId, userId, status)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deactivate(Long id) {
        getById(id);
        assignmentRepository.deactivate(id);
    }

    private void validate(BranchUserAssignmentRequestDto request, Long id) {
        if (request.getBranchId() == null || request.getBranchId() <= 0) {
            throw new BadRequestException("Valid branch is required");
        }

        branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new BadRequestException("Valid user is required");
        }

        if (request.getAssignmentRole() == null || request.getAssignmentRole().isBlank()) {
            throw new BadRequestException("Assignment role is required");
        }

        if (request.getFromDate() == null) {
            throw new BadRequestException("From date is required");
        }

        if (request.getToDate() != null && request.getFromDate().isAfter(request.getToDate())) {
            throw new BadRequestException("From date cannot be after to date");
        }

        if (assignmentRepository.existsActiveAssignment(request.getBranchId(), request.getUserId(), id)) {
            throw new BadRequestException("Active assignment already exists for this user and branch");
        }

        if (Boolean.TRUE.equals(request.getIsPrimary())
                && assignmentRepository.existsActivePrimary(request.getBranchId(), request.getAssignmentRole(), id)) {
            throw new BadRequestException("Primary active assignment already exists for this branch and role");
        }
    }

    private void apply(BranchUserAssignmentRequestDto request, BranchUserAssignment assignment) {
        assignment.setBranchId(request.getBranchId());
        assignment.setUserId(request.getUserId());
        assignment.setAssignmentRole(request.getAssignmentRole());
        assignment.setFromDate(request.getFromDate());
        assignment.setToDate(request.getToDate());
        assignment.setIsPrimary(Boolean.TRUE.equals(request.getIsPrimary()));
        assignment.setStatus(
                request.getStatus() == null || request.getStatus().isBlank()
                        ? "ACTIVE"
                        : request.getStatus()
        );
    }

    private BranchUserAssignmentResponseDto toDto(BranchUserAssignment assignment) {
        BranchUserAssignmentResponseDto dto = new BranchUserAssignmentResponseDto();

        dto.setId(assignment.getId());
        dto.setBranchId(assignment.getBranchId());
        dto.setUserId(assignment.getUserId());
        dto.setAssignmentRole(assignment.getAssignmentRole());
        dto.setFromDate(assignment.getFromDate());
        dto.setToDate(assignment.getToDate());
        dto.setIsPrimary(assignment.getIsPrimary());
        dto.setStatus(assignment.getStatus());
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());

        return dto;
    }
}