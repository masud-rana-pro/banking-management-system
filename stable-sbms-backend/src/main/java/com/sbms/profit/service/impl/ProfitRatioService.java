package com.sbms.profit.service.impl;

import com.sbms.account.entity.Account;
import com.sbms.account.entity.AccountType;
import com.sbms.account.repository.AccountRepository;
import com.sbms.account.repository.AccountTypeRepository;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.profit.dto.request.ProfitRatioRequest;
import com.sbms.profit.dto.response.ProfitRatioDropdownResponse;
import com.sbms.profit.dto.response.ProfitRatioResponse;
import com.sbms.profit.entity.ProfitRatio;
import com.sbms.profit.repository.ProfitPostingRepository;
import com.sbms.profit.repository.ProfitRatioRepository;
import com.sbms.profit.repository.ProfitScheduleRepository;
import com.sbms.profit.service.IProfitRatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProfitRatioService implements IProfitRatioService {

    @Autowired
    private ProfitRatioRepository profitRatioRepository;

    @Autowired
    private ProfitScheduleRepository profitScheduleRepository;

    @Autowired
    private ProfitPostingRepository profitPostingRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public ProfitRatioResponse create(ProfitRatioRequest request) {
        AccountType accountType = validateRequest(request, null);

        ProfitRatio entity = new ProfitRatio();
        entity.setRatioCode(resolveRatioCode(trim(request.getRatioCode()), null));
        entity.setAccountType(accountType);
        entity.setEffectiveFrom(request.getEffectiveFrom());
        entity.setEffectiveTo(request.getEffectiveTo());
        entity.setRatioPercent(request.getRatioPercent().setScale(4, RoundingMode.HALF_UP));
        entity.setStatus(RecordStatus.ACTIVE);

        ProfitRatio saved = profitRatioRepository.save(entity);
        syncAccountRatioLink(accountType.getId());
        return mapResponse(saved);
    }

    @Override
    public List<ProfitRatioResponse> list() {
        return profitRatioRepository.findAll().stream().map(this::mapResponse).toList();
    }

    @Override
    public ProfitRatioResponse getById(Long id) {
        return mapResponse(getEntity(id));
    }

    @Override
    public ProfitRatioResponse update(Long id, ProfitRatioRequest request) {
        ProfitRatio entity = getEntity(id);
        AccountType accountType = validateRequest(request, id);

        entity.setRatioCode(resolveRatioCode(trim(request.getRatioCode()), id));
        entity.setAccountType(accountType);
        entity.setEffectiveFrom(request.getEffectiveFrom());
        entity.setEffectiveTo(request.getEffectiveTo());
        entity.setRatioPercent(request.getRatioPercent().setScale(4, RoundingMode.HALF_UP));

        ProfitRatio updated = profitRatioRepository.update(entity);
        syncAccountRatioLink(accountType.getId());
        return mapResponse(updated);
    }

    @Override
    public ProfitRatioResponse archive(Long id) {
        ProfitRatio entity = getEntity(id);
        entity.setStatus(RecordStatus.ARCHIVED);
        ProfitRatio updated = profitRatioRepository.update(entity);
        syncAccountRatioLink(entity.getAccountType().getId());
        return mapResponse(updated);
    }

    @Override
    public ProfitRatioResponse restore(Long id) {
        ProfitRatio entity = getEntity(id);
        if (profitRatioRepository.existsOverlap(
                entity.getAccountType().getId(),
                entity.getEffectiveFrom(),
                entity.getEffectiveTo(),
                entity.getId()
        )) {
            throw new BadRequestException("Another active profit ratio overlaps with this effective window");
        }
        entity.setStatus(RecordStatus.ACTIVE);
        ProfitRatio updated = profitRatioRepository.update(entity);
        syncAccountRatioLink(entity.getAccountType().getId());
        return mapResponse(updated);
    }

    @Override
    public List<ProfitRatioDropdownResponse> dropdown() {
        return profitRatioRepository.findDropdown().stream()
                .map(item -> new ProfitRatioDropdownResponse(
                        item.getId(),
                        item.getRatioCode(),
                        item.getAccountType().getId(),
                        item.getAccountType().getTypeName()
                ))
                .toList();
    }

    private AccountType validateRequest(ProfitRatioRequest request, Long currentId) {
        if (request == null) {
            throw new BadRequestException("Profit ratio request is required");
        }
        if (request.getAccountTypeId() == null) {
            throw new BadRequestException("Account type is required");
        }
        if (request.getEffectiveFrom() == null) {
            throw new BadRequestException("Effective from date is required");
        }
        if (request.getEffectiveTo() != null && request.getEffectiveTo().isBefore(request.getEffectiveFrom())) {
            throw new BadRequestException("Effective to date cannot be earlier than effective from date");
        }
        if (request.getRatioPercent() == null || request.getRatioPercent().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Ratio percent must be greater than zero");
        }

        AccountType accountType = accountTypeRepository.findById(request.getAccountTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Account type not found"));
        if (accountType.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived account type cannot be linked with profit ratio");
        }
        if (!Boolean.TRUE.equals(accountType.getProfitApplicable())) {
            throw new BadRequestException("Selected account type is not profit applicable");
        }

        if (profitRatioRepository.existsOverlap(
                accountType.getId(),
                request.getEffectiveFrom(),
                request.getEffectiveTo(),
                currentId
        )) {
            throw new BadRequestException("Effective window overlaps with another profit ratio for the same account type");
        }

        return accountType;
    }

    private ProfitRatio getEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Profit ratio id is required");
        }
        return profitRatioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profit ratio not found"));
    }

    private ProfitRatioResponse mapResponse(ProfitRatio entity) {
        LocalDate today = LocalDate.now();
        boolean activeNow = entity.getStatus() == RecordStatus.ACTIVE
                && !entity.getEffectiveFrom().isAfter(today)
                && (entity.getEffectiveTo() == null || !entity.getEffectiveTo().isBefore(today));
        return new ProfitRatioResponse(
                entity.getId(),
                entity.getRatioCode(),
                entity.getAccountType().getId(),
                entity.getAccountType().getTypeCode(),
                entity.getAccountType().getTypeName(),
                entity.getEffectiveFrom(),
                entity.getEffectiveTo(),
                entity.getRatioPercent(),
                entity.getStatus(),
                activeNow,
                profitScheduleRepository.countByAccountTypeId(entity.getAccountType().getId()),
                profitPostingRepository.countByAccountTypeId(entity.getAccountType().getId()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String resolveRatioCode(String ratioCode, Long currentId) {
        if (ratioCode != null) {
            ProfitRatio existing = profitRatioRepository.findByRatioCode(ratioCode).orElse(null);
            if (existing != null && (currentId == null || !existing.getId().equals(currentId))) {
                throw new BadRequestException("Profit ratio code already exists");
            }
            return ratioCode;
        }

        String lastCode = profitRatioRepository.findLastRatioCode();
        int nextNumber = 1;
        if (lastCode != null && lastCode.startsWith("PSR-")) {
            nextNumber = Integer.parseInt(lastCode.substring(4)) + 1;
        }
        return String.format("PSR-%05d", nextNumber);
    }

    private void syncAccountRatioLink(Long accountTypeId) {
        LocalDate today = LocalDate.now();
        Long ratioId = profitRatioRepository.findActiveRatio(accountTypeId, today)
                .map(ProfitRatio::getId)
                .orElse(null);
        List<Account> accounts = accountRepository.findByAccountTypeId(accountTypeId);
        for (Account account : accounts) {
            account.setProfitRatioId(ratioId);
            accountRepository.update(account);
        }
    }

    private String trim(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
