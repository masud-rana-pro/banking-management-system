package com.sbms.account.service;

import com.sbms.account.dto.request.AccountOpeningRequestDto;
import com.sbms.account.dto.request.AccountWorkflowActionRequest;
import com.sbms.account.dto.response.AccountOpeningRequestResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAccountOpeningRequestService {
    AccountOpeningRequestResponse create(AccountOpeningRequestDto request, String username);
    List<AccountOpeningRequestResponse> list();
    AccountOpeningRequestResponse getById(Long id);
    AccountOpeningRequestResponse update(Long id, AccountOpeningRequestDto request, String username);
    AccountOpeningRequestResponse submit(Long id, String username);
    AccountOpeningRequestResponse verify(Long id, String username);
    AccountOpeningRequestResponse approve(Long id, String username);
    AccountOpeningRequestResponse reject(Long id, AccountWorkflowActionRequest request, String username);
    AccountOpeningRequestResponse returnForCorrection(Long id, AccountWorkflowActionRequest request, String username);
    ResponseEntity<byte[]> previewOpeningForm(Long id);
    ResponseEntity<byte[]> downloadOpeningForm(Long id);
}
