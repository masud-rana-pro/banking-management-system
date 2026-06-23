package com.sbms.atm.service;

import com.sbms.atm.dto.request.ReconciliationRequest;
import com.sbms.atm.dto.response.ReconciliationResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IReconciliationService {

    ReconciliationResponse create(ReconciliationRequest request);

    List<ReconciliationResponse> getAll();

    ReconciliationResponse getById(Long id);

    List<ReconciliationResponse> getByTerminal(Long terminalId);
    ResponseEntity<byte[]> previewReport(Long id);
    ResponseEntity<byte[]> downloadReport(Long id);
}
