package com.sbms.atm.controller;

import com.sbms.atm.dto.request.ReconciliationRequest;
import com.sbms.atm.dto.response.ReconciliationResponse;
import com.sbms.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IReconciliationController {

    ApiResponse<ReconciliationResponse> create(ReconciliationRequest request);

    ApiResponse<List<ReconciliationResponse>> list();

    ApiResponse<ReconciliationResponse> getById(Long id);

    ApiResponse<List<ReconciliationResponse>> byTerminal(Long terminalId);
    ResponseEntity<byte[]> previewReport(Long id);
    ResponseEntity<byte[]> downloadReport(Long id);
}
