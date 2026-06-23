package com.sbms.branch.service.impl;

import com.sbms.branch.dto.BranchRequestDto;
import com.sbms.branch.dto.BranchResponseDto;
import com.sbms.branch.entity.Branch;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.branch.service.IBranchService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sbms.branch.dto.BranchDashboardSummaryDto;
import com.sbms.branch.repository.BranchTellerLimitRepository;
import com.sbms.branch.repository.BranchUserAssignmentRepository;
import com.sbms.branch.repository.VaultBalanceRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BranchService implements IBranchService {

    @Autowired
    private BranchRepository branchRepository;
    
    @Autowired
    private BranchUserAssignmentRepository assignmentRepository;

    @Autowired
    private BranchTellerLimitRepository tellerLimitRepository;

    @Autowired
    private VaultBalanceRepository vaultBalanceRepository;

    @Override
    public BranchResponseDto create(BranchRequestDto dto) {
        validate(dto, null);

        Branch branch = new Branch();
        apply(dto, branch);

        return toDto(branchRepository.save(branch));
    }

    @Override
    public BranchResponseDto update(Long id, BranchRequestDto dto) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        validate(dto, id);
        apply(dto, branch);

        return toDto(branchRepository.save(branch));
    }

    @Override
    public BranchResponseDto getById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        return toDto(branch);
    }

    @Override
    public List<BranchResponseDto> getAll(String search, String status) {
        return branchRepository.findAll(search, status)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BranchResponseDto> dropdown() {
        return branchRepository.findAll(null, "ACTIVE")
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void softDelete(Long id) {
        getById(id);
        branchRepository.softDelete(id, null, "Archived from UI");
    }

    @Override
    public void restore(Long id) {
        branchRepository.restore(id);
    }

    private void validate(BranchRequestDto dto, Long id) {
        if (dto.getBranchCode() != null
                && !dto.getBranchCode().isBlank()
                && branchRepository.existsByBranchCode(dto.getBranchCode(), id)) {
            throw new BadRequestException("Branch code already exists");
        }

        if (branchRepository.existsByRoutingNo(dto.getRoutingNo(), id)) {
            throw new BadRequestException("Routing no already exists");
        }
    }

    private void apply(BranchRequestDto dto, Branch branch) {
        branch.setBranchCode(
                dto.getBranchCode() == null || dto.getBranchCode().isBlank()
                        ? generateCode()
                        : dto.getBranchCode()
        );

        branch.setBranchName(dto.getBranchName());
        branch.setBranchShortName(dto.getBranchShortName());
        branch.setBranchType(dto.getBranchType());
        branch.setRoutingNo(dto.getRoutingNo());
        branch.setSwiftCode(dto.getSwiftCode());
        branch.setEmail(dto.getEmail());
        branch.setMobile(dto.getMobile());
        branch.setPhone(dto.getPhone());
        branch.setAddressLine1(dto.getAddressLine1());
        branch.setAddressLine2(dto.getAddressLine2());
        branch.setCountryId(dto.getCountryId());
        branch.setDivisionId(dto.getDivisionId());
        branch.setDistrictId(dto.getDistrictId());
        branch.setUpazilaId(dto.getUpazilaId());
        branch.setPostalCode(dto.getPostalCode());
        branch.setManagerUserId(dto.getManagerUserId());
        branch.setOpenedDate(dto.getOpenedDate());
        branch.setStatus(dto.getStatus() == null || dto.getStatus().isBlank() ? "ACTIVE" : dto.getStatus());
    }

    private String generateCode() {
        return "BR" + (System.currentTimeMillis() % 1000000);
    }

    private BranchResponseDto toDto(Branch branch) {
        BranchResponseDto dto = new BranchResponseDto();

        dto.setId(branch.getId());
        dto.setBranchCode(branch.getBranchCode());
        dto.setBranchName(branch.getBranchName());
        dto.setBranchShortName(branch.getBranchShortName());
        dto.setBranchType(branch.getBranchType());
        dto.setRoutingNo(branch.getRoutingNo());
        dto.setSwiftCode(branch.getSwiftCode());
        dto.setEmail(branch.getEmail());
        dto.setMobile(branch.getMobile());
        dto.setPhone(branch.getPhone());
        dto.setAddressLine1(branch.getAddressLine1());
        dto.setAddressLine2(branch.getAddressLine2());
        dto.setCountryId(branch.getCountryId());
        dto.setDivisionId(branch.getDivisionId());
        dto.setDistrictId(branch.getDistrictId());
        dto.setUpazilaId(branch.getUpazilaId());
        dto.setPostalCode(branch.getPostalCode());
        dto.setManagerUserId(branch.getManagerUserId());
        dto.setOpenedDate(branch.getOpenedDate());
        dto.setStatus(branch.getStatus());
        dto.setCreatedAt(branch.getCreatedAt());
        dto.setUpdatedAt(branch.getUpdatedAt());

        return dto;
    }
    
    @Override
    public BranchDashboardSummaryDto getDashboardSummary() {
        LocalDate today = LocalDate.now();

        BranchDashboardSummaryDto dto = new BranchDashboardSummaryDto();

        dto.setTotalBranches(branchRepository.countAllActiveOrInactive());
        dto.setActiveBranches(branchRepository.countByStatus("ACTIVE"));

        BigDecimal cashPosition = vaultBalanceRepository.getCurrentBranchCashPosition();
        dto.setBranchCashPosition(cashPosition == null ? BigDecimal.ZERO : cashPosition);

        dto.setPendingAssignments(assignmentRepository.countPendingAssignments());
        dto.setTellerLimitAlerts(tellerLimitRepository.countTellerLimitAlerts());

        Long opened = vaultBalanceRepository.countTodayVaultOpened(today);
        Long closed = vaultBalanceRepository.countTodayVaultClosed(today);
        Long pendingClose = vaultBalanceRepository.countTodayVaultPendingClose(today);

        dto.setTodayVaultOpened(opened);
        dto.setTodayVaultClosed(closed);
        dto.setTodayVaultPendingClose(pendingClose);

        return dto;
    }
}
