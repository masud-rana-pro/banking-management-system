package com.sbms.account.controller;

import com.sbms.account.dto.request.AccountOpeningRequestDto;
import com.sbms.account.dto.request.AccountWorkflowActionRequest;
import com.sbms.account.dto.response.AccountOpeningRequestResponse;
import com.sbms.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAccountOpeningRequestController {
    ApiResponse<AccountOpeningRequestResponse> create(AccountOpeningRequestDto request);
    ApiResponse<List<AccountOpeningRequestResponse>> list();
    ApiResponse<AccountOpeningRequestResponse> getById(Long id);
    ApiResponse<AccountOpeningRequestResponse> update(Long id, AccountOpeningRequestDto request);
    ApiResponse<AccountOpeningRequestResponse> submit(Long id);
    ApiResponse<AccountOpeningRequestResponse> verify(Long id);
    ApiResponse<AccountOpeningRequestResponse> approve(Long id);
    ApiResponse<AccountOpeningRequestResponse> reject(Long id, AccountWorkflowActionRequest request);
    ApiResponse<AccountOpeningRequestResponse> returnForCorrection(Long id, AccountWorkflowActionRequest request);
    ResponseEntity<byte[]> previewOpeningForm(Long id);
    ResponseEntity<byte[]> downloadOpeningForm(Long id);
}
