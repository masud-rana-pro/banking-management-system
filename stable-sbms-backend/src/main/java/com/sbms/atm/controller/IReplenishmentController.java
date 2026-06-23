package com.sbms.atm.controller;

import com.sbms.atm.dto.request.ReplenishmentRequest;
import com.sbms.atm.dto.response.ReplenishmentResponse;
import com.sbms.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IReplenishmentController {

    ApiResponse<ReplenishmentResponse> create(ReplenishmentRequest request);

    ApiResponse<List<ReplenishmentResponse>> list();

    ApiResponse<ReplenishmentResponse> getById(Long id);

    ApiResponse<List<ReplenishmentResponse>> byTerminal(Long terminalId);
    ResponseEntity<byte[]> previewReport(Long id);
    ResponseEntity<byte[]> downloadReport(Long id);
}
