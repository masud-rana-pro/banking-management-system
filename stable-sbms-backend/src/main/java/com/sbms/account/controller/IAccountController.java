package com.sbms.account.controller;

import com.sbms.account.dto.request.AccountWorkflowActionRequest;
import com.sbms.account.dto.response.AccountDashboardSummaryResponse;
import com.sbms.account.dto.response.AccountResponse;
import com.sbms.common.response.ApiResponse;

import java.util.List;

public interface IAccountController {
    ApiResponse<List<AccountResponse>> list();
    ApiResponse<AccountResponse> getById(Long id);
    ApiResponse<List<AccountResponse>> search(String keyword);
    ApiResponse<AccountResponse> activate(Long id, AccountWorkflowActionRequest request);
    ApiResponse<AccountResponse> block(Long id, AccountWorkflowActionRequest request);
    ApiResponse<AccountResponse> freeze(Long id, AccountWorkflowActionRequest request);
    ApiResponse<AccountResponse> close(Long id, AccountWorkflowActionRequest request);
    ApiResponse<AccountDashboardSummaryResponse> dashboardSummary();
}
