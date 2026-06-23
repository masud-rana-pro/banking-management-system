package com.sbms.account.service;

import com.sbms.account.dto.request.AccountWorkflowActionRequest;
import com.sbms.account.dto.response.AccountDashboardSummaryResponse;
import com.sbms.account.dto.response.AccountResponse;

import java.util.List;

public interface IAccountService {
    List<AccountResponse> list();
    AccountResponse getById(Long id);
    List<AccountResponse> search(String keyword);
    AccountResponse activate(Long id, AccountWorkflowActionRequest request, String username);
    AccountResponse block(Long id, AccountWorkflowActionRequest request, String username);
    AccountResponse freeze(Long id, AccountWorkflowActionRequest request, String username);
    AccountResponse close(Long id, AccountWorkflowActionRequest request, String username);
    AccountDashboardSummaryResponse dashboardSummary();
}
