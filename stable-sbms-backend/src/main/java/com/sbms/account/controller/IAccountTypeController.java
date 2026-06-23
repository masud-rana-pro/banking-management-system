package com.sbms.account.controller;

import com.sbms.account.dto.request.AccountTypeRequest;
import com.sbms.account.dto.response.AccountTypeDropdownResponse;
import com.sbms.account.dto.response.AccountTypeResponse;
import com.sbms.common.response.ApiResponse;

import java.util.List;

public interface IAccountTypeController {
    ApiResponse<AccountTypeResponse> create(AccountTypeRequest request);
    ApiResponse<List<AccountTypeResponse>> list();
    ApiResponse<AccountTypeResponse> getById(Long id);
    ApiResponse<AccountTypeResponse> update(Long id, AccountTypeRequest request);
    ApiResponse<AccountTypeResponse> archive(Long id);
    ApiResponse<AccountTypeResponse> restore(Long id);
    ApiResponse<List<AccountTypeDropdownResponse>> dropdown();
}
