package com.sbms.account.service;

import com.sbms.account.dto.request.AccountTypeRequest;
import com.sbms.account.dto.response.AccountTypeDropdownResponse;
import com.sbms.account.dto.response.AccountTypeResponse;

import java.util.List;

public interface IAccountTypeService {
    AccountTypeResponse create(AccountTypeRequest request);
    List<AccountTypeResponse> list();
    AccountTypeResponse getById(Long id);
    AccountTypeResponse update(Long id, AccountTypeRequest request);
    AccountTypeResponse archive(Long id);
    AccountTypeResponse restore(Long id);
    List<AccountTypeDropdownResponse> dropdown();
}
