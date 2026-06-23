package com.sbms.account.service.impl;

import com.sbms.account.dto.request.AccountWorkflowActionRequest;
import com.sbms.account.dto.response.AccountDashboardSummaryResponse;
import com.sbms.account.dto.response.AccountResponse;
import com.sbms.account.entity.Account;
import com.sbms.account.enums.AccountOpeningRequestStatus;
import com.sbms.account.enums.AccountStatus;
import com.sbms.account.repository.AccountOpeningRequestRepository;
import com.sbms.account.repository.AccountRepository;
import com.sbms.account.service.IAccountService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AccountService implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountOpeningRequestRepository openingRequestRepository;

    @Autowired
    private AutomatedMailService automatedMailService;

    @Override
    public List<AccountResponse> list() {
        return accountRepository.findAll().stream().map(this::mapResponse).toList();
    }

    @Override
    public AccountResponse getById(Long id) {
        return mapResponse(getEntity(id));
    }

    @Override
    public List<AccountResponse> search(String keyword) {
        return accountRepository.search(keyword).stream().map(this::mapResponse).toList();
    }

    @Override
    public AccountResponse activate(Long id, AccountWorkflowActionRequest request, String username) {
        Account entity = getEntity(id);
        if (entity.getAccountStatus() == AccountStatus.CLOSED) {
            throw new BadRequestException("Closed account cannot be activated");
        }
        entity.setAccountStatus(AccountStatus.ACTIVE);
        entity.setRemarks(resolveRemarks(request, entity.getRemarks(), "Activated account"));
        AccountResponse response = mapResponse(accountRepository.update(entity));
        sendAccountStatusMail(entity, "Activated", entity.getRemarks());
        return response;
    }

    @Override
    public AccountResponse block(Long id, AccountWorkflowActionRequest request, String username) {
        Account entity = getEntity(id);
        if (entity.getAccountStatus() == AccountStatus.CLOSED) {
            throw new BadRequestException("Closed account cannot be blocked");
        }
        entity.setAccountStatus(AccountStatus.SUSPENDED);
        entity.setRemarks(resolveRemarks(request, entity.getRemarks(), "Blocked account"));
        AccountResponse response = mapResponse(accountRepository.update(entity));
        sendAccountStatusMail(entity, "Blocked", entity.getRemarks());
        return response;
    }

    @Override
    public AccountResponse freeze(Long id, AccountWorkflowActionRequest request, String username) {
        Account entity = getEntity(id);
        if (entity.getAccountStatus() == AccountStatus.CLOSED) {
            throw new BadRequestException("Closed account cannot be frozen");
        }
        entity.setAccountStatus(AccountStatus.SUSPENDED);
        entity.setRemarks(resolveRemarks(request, entity.getRemarks(), "Frozen account"));
        AccountResponse response = mapResponse(accountRepository.update(entity));
        sendAccountStatusMail(entity, "Frozen", entity.getRemarks());
        return response;
    }

    @Override
    public AccountResponse close(Long id, AccountWorkflowActionRequest request, String username) {
        Account entity = getEntity(id);
        entity.setAccountStatus(AccountStatus.CLOSED);
        entity.setClosedDate(LocalDate.now());
        entity.setRemarks(resolveRemarks(request, entity.getRemarks(), "Account closed"));
        AccountResponse response = mapResponse(accountRepository.update(entity));
        sendAccountStatusMail(entity, "Closed", entity.getRemarks());
        return response;
    }

    @Override
    public AccountDashboardSummaryResponse dashboardSummary() {
        return new AccountDashboardSummaryResponse(
                accountRepository.countAll(),
                openingRequestRepository.countByStatuses(List.of(
                        AccountOpeningRequestStatus.DRAFT,
                        AccountOpeningRequestStatus.SUBMITTED,
                        AccountOpeningRequestStatus.SENT_BACK
                )),
                accountRepository.countByStatuses(List.of(AccountStatus.ACTIVE)),
                accountRepository.countByStatuses(List.of(AccountStatus.SUSPENDED)),
                0L,
                openingRequestRepository.countByStatuses(List.of(
                        AccountOpeningRequestStatus.SUBMITTED,
                        AccountOpeningRequestStatus.VERIFIED
                ))
        );
    }

    private Account getEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Account id is required");
        }
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    private AccountResponse mapResponse(Account entity) {
        return new AccountResponse(
                entity.getId(),
                entity.getAccountNumber(),
                entity.getCustomer().getId(),
                entity.getCustomer().getCustomerCode(),
                entity.getCustomer().getFullName(),
                entity.getAccountType().getId(),
                entity.getAccountType().getTypeCode(),
                entity.getAccountType().getTypeName(),
                entity.getBranchId(),
                entity.getOpeningRequest() == null ? null : entity.getOpeningRequest().getId(),
                entity.getOpeningRequest() == null ? null : entity.getOpeningRequest().getRequestNo(),
                entity.getOpenedAt() == null ? null : entity.getOpenedAt().toLocalDate(),
                entity.getCurrentBalance(),
                entity.getAvailableBalance(),
                entity.getProfitRatioId(),
                resolveDisplayStatus(entity),
                entity.getClosedDate(),
                entity.getRemarks(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String resolveDisplayStatus(Account entity) {
        if (entity.getAccountStatus() == AccountStatus.PENDING) {
            return "PENDING_ACTIVATION";
        }
        if (entity.getAccountStatus() == AccountStatus.SUSPENDED) {
            String remarks = entity.getRemarks() == null ? "" : entity.getRemarks().toLowerCase();
            return remarks.contains("frozen") || remarks.contains("freeze") ? "FROZEN" : "BLOCKED";
        }
        return entity.getAccountStatus().name();
    }

    private String resolveRemarks(AccountWorkflowActionRequest request, String current, String fallback) {
        String incoming = request == null ? null : trim(request.getRemarks());
        if (incoming != null) {
            return incoming;
        }
        if (trim(current) != null) {
            return current;
        }
        return fallback;
    }

    private String trim(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private void sendAccountStatusMail(Account entity, String decision, String remarks) {
        if (entity == null || entity.getCustomer() == null || trim(entity.getCustomer().getEmail()) == null) {
            return;
        }
        automatedMailService.sendApprovalDecisionEmail(
                entity.getCustomer().getEmail(),
                "Account",
                entity.getAccountNumber(),
                decision,
                remarks,
                "/accounts/" + entity.getId(),
                "Open Account"
        );
    }
}
