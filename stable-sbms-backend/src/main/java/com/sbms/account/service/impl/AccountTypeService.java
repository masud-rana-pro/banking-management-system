package com.sbms.account.service.impl;

import com.sbms.account.dto.request.AccountTypeRequest;
import com.sbms.account.dto.response.AccountTypeDropdownResponse;
import com.sbms.account.dto.response.AccountTypeResponse;
import com.sbms.account.entity.AccountType;
import com.sbms.account.repository.AccountTypeRepository;
import com.sbms.account.service.IAccountTypeService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.customer.enums.RecordStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class AccountTypeService implements IAccountTypeService {

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Override
    public AccountTypeResponse create(AccountTypeRequest request) {
        validate(request);

        String typeCode = trim(request.getTypeCode());
        if (typeCode == null || typeCode.isEmpty()) {
            typeCode = generateTypeCode();
        }
        if (accountTypeRepository.existsByTypeCode(typeCode)) {
            throw new BadRequestException("Account type code already exists");
        }

        AccountType entity = new AccountType();
        request.setTypeCode(typeCode);
        mapRequest(request, entity);
        return mapResponse(accountTypeRepository.save(entity));
    }

    @Override
    public List<AccountTypeResponse> list() {
        return accountTypeRepository.findAll().stream().map(this::mapResponse).toList();
    }

    @Override
    public AccountTypeResponse getById(Long id) {
        return mapResponse(getEntity(id));
    }

    @Override
    public AccountTypeResponse update(Long id, AccountTypeRequest request) {
        validate(request);

        AccountType entity = getEntity(id);
        String typeCode = trim(request.getTypeCode());
        if (typeCode == null || typeCode.isEmpty()) {
            typeCode = entity.getTypeCode();
        }
        if (accountTypeRepository.existsByTypeCodeExceptId(typeCode, id)) {
            throw new BadRequestException("Another account type already uses this code");
        }

        request.setTypeCode(typeCode);
        mapRequest(request, entity);
        return mapResponse(accountTypeRepository.update(entity));
    }

    @Override
    public AccountTypeResponse archive(Long id) {
        AccountType entity = getEntity(id);
        entity.setStatus(RecordStatus.ARCHIVED);
        return mapResponse(accountTypeRepository.update(entity));
    }

    @Override
    public AccountTypeResponse restore(Long id) {
        AccountType entity = getEntity(id);
        entity.setStatus(RecordStatus.ACTIVE);
        return mapResponse(accountTypeRepository.update(entity));
    }

    @Override
    public List<AccountTypeDropdownResponse> dropdown() {
        return accountTypeRepository.findDropdown()
                .stream()
                .map(item -> new AccountTypeDropdownResponse(
                        item.getId(),
                        item.getTypeCode(),
                        item.getTypeName(),
                        item.getTypeCode() + " - " + item.getTypeName()
                ))
                .toList();
    }

    private void validate(AccountTypeRequest request) {
        if (request == null) {
            throw new BadRequestException("Account type request is required");
        }
        if (trim(request.getTypeName()) == null) {
            throw new BadRequestException("Type name is required");
        }
        if (request.getShariahContractType() == null) {
            throw new BadRequestException("Shariah contract type is required");
        }
        if (trim(request.getCurrencyCode()) == null) {
            throw new BadRequestException("Currency code is required");
        }
        BigDecimal minimum = request.getMinimumOpeningBalance();
        if (minimum == null || minimum.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Minimum opening balance must be zero or positive");
        }
    }

    private void mapRequest(AccountTypeRequest request, AccountType entity) {
        String typeCode = trim(request.getTypeCode());
        String typeName = trim(request.getTypeName());
        entity.setTypeCode(typeCode);
        entity.setCode(typeCode);
        entity.setTypeName(typeName);
        entity.setName(typeName);
        entity.setShariahContractType(request.getShariahContractType());
        entity.setCurrencyCode(trim(request.getCurrencyCode()).toUpperCase());
        entity.setMinimumOpeningBalance(request.getMinimumOpeningBalance());
        entity.setMinimumBalance(request.getMinimumOpeningBalance());
        entity.setProfitApplicable(Boolean.TRUE.equals(request.getProfitApplicable()));
        entity.setPsrRequired(Boolean.TRUE.equals(request.getProfitApplicable()));
        entity.setWithdrawalAllowed(!Boolean.FALSE.equals(request.getWithdrawalAllowed()));
        entity.setAccountCategory(resolveCategory(typeName));
        entity.setAccountSubcategory(typeName);
        entity.setStatus(request.getStatus() == null ? RecordStatus.ACTIVE : request.getStatus());
    }

    private AccountType getEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Account type id is required");
        }
        return accountTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account type not found"));
    }

    private AccountTypeResponse mapResponse(AccountType entity) {
        return new AccountTypeResponse(
                entity.getId(),
                entity.getTypeCode(),
                entity.getTypeName(),
                entity.getShariahContractType(),
                entity.getCurrencyCode(),
                entity.getMinimumOpeningBalance(),
                entity.getProfitApplicable(),
                entity.getWithdrawalAllowed(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private String generateTypeCode() {
        String last = accountTypeRepository.findLastTypeCode();
        int next = 1;
        if (last != null && last.startsWith("ACT-")) {
            try {
                next = Integer.parseInt(last.substring(4)) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }
        return String.format("ACT-%03d", next);
    }

    private String trim(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String resolveCategory(String typeName) {
        String value = typeName == null ? "" : typeName.toLowerCase();
        if (value.contains("current")) {
            return "CURRENT";
        }
        if (value.contains("deposit")) {
            return "DEPOSIT";
        }
        return "SAVINGS";
    }
}
