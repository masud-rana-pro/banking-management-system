package com.sbms.profit.service.impl;

import com.sbms.account.entity.Account;
import com.sbms.account.enums.AccountStatus;
import com.sbms.account.repository.AccountRepository;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.profit.dto.request.ProfitScheduleRequest;
import com.sbms.profit.dto.response.ProfitScheduleResponse;
import com.sbms.profit.entity.ProfitRatio;
import com.sbms.profit.entity.ProfitSchedule;
import com.sbms.profit.repository.ProfitRatioRepository;
import com.sbms.profit.repository.ProfitScheduleRepository;
import com.sbms.profit.service.IProfitScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProfitScheduleService implements IProfitScheduleService {

    @Autowired
    private ProfitScheduleRepository profitScheduleRepository;

    @Autowired
    private ProfitRatioRepository profitRatioRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public ProfitScheduleResponse create(ProfitScheduleRequest request) {
        Account account = validateRequest(request);
        if (profitScheduleRepository.findByAccountId(account.getId()).isPresent()) {
            throw new BadRequestException("Profit schedule already exists for the selected account");
        }

        ProfitSchedule entity = new ProfitSchedule();
        entity.setAccount(account);
        entity.setProfitFrequency(request.getProfitFrequency());
        entity.setNextPostingDate(request.getNextPostingDate());
        entity.setStatus(RecordStatus.ACTIVE);

        ProfitSchedule saved = profitScheduleRepository.save(entity);
        return mapResponse(saved);
    }

    @Override
    public List<ProfitScheduleResponse> list() {
        return profitScheduleRepository.findAll().stream().map(this::mapResponse).toList();
    }

    @Override
    public ProfitScheduleResponse getById(Long id) {
        return mapResponse(getEntity(id));
    }

    @Override
    public ProfitScheduleResponse archive(Long id) {
        ProfitSchedule entity = getEntity(id);
        entity.setStatus(RecordStatus.ARCHIVED);
        return mapResponse(profitScheduleRepository.update(entity));
    }

    @Override
    public ProfitScheduleResponse restore(Long id) {
        ProfitSchedule entity = getEntity(id);
        ProfitSchedule existing = profitScheduleRepository.findByAccountId(entity.getAccount().getId()).orElse(null);
        if (existing != null && !existing.getId().equals(entity.getId())) {
            throw new BadRequestException("Another active schedule already exists for this account");
        }
        validateAccountEligibility(entity.getAccount(), entity.getNextPostingDate());
        entity.setStatus(RecordStatus.ACTIVE);
        return mapResponse(profitScheduleRepository.update(entity));
    }

    private Account validateRequest(ProfitScheduleRequest request) {
        if (request == null) {
            throw new BadRequestException("Profit schedule request is required");
        }
        if (request.getAccountId() == null) {
            throw new BadRequestException("Account is required");
        }
        if (request.getProfitFrequency() == null) {
            throw new BadRequestException("Profit frequency is required");
        }
        if (request.getNextPostingDate() == null) {
            throw new BadRequestException("Next posting date is required");
        }
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        validateAccountEligibility(account, request.getNextPostingDate());
        return account;
    }

    private void validateAccountEligibility(Account account, LocalDate nextPostingDate) {
        if (account.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived account cannot be scheduled for profit posting");
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Only active accounts can be scheduled for profit posting");
        }
        if (!Boolean.TRUE.equals(account.getAccountType().getProfitApplicable())) {
            throw new BadRequestException("Selected account type is not profit applicable");
        }
        ProfitRatio ratio = profitRatioRepository.findActiveRatio(account.getAccountType().getId(), nextPostingDate)
                .orElseThrow(() -> new BadRequestException("No active profit ratio found for the selected account type and posting date"));
        account.setProfitRatioId(ratio.getId());
        accountRepository.update(account);
    }

    private ProfitSchedule getEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Profit schedule id is required");
        }
        return profitScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profit schedule not found"));
    }

    private ProfitScheduleResponse mapResponse(ProfitSchedule entity) {
        Account account = entity.getAccount();
        return new ProfitScheduleResponse(
                entity.getId(),
                account.getId(),
                account.getAccountNumber(),
                account.getCustomer().getId(),
                account.getCustomer().getCustomerCode(),
                account.getCustomer().getFullName(),
                account.getAccountType().getId(),
                account.getAccountType().getTypeCode(),
                account.getAccountType().getTypeName(),
                account.getBranchId(),
                account.getCurrentBalance(),
                entity.getProfitFrequency().name(),
                entity.getNextPostingDate(),
                entity.getLastPostingDate(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
