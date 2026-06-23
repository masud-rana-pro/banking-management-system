package com.sbms.atm.service;

import com.sbms.atm.dto.request.ReplenishmentRequest;
import com.sbms.atm.dto.response.ReplenishmentResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IReplenishmentService {

    ReplenishmentResponse create(ReplenishmentRequest request);

    List<ReplenishmentResponse> getAll();

    ReplenishmentResponse getById(Long id);

    List<ReplenishmentResponse> getByTerminal(Long terminalId);
    ResponseEntity<byte[]> previewReport(Long id);
    ResponseEntity<byte[]> downloadReport(Long id);
}
