package com.sbms.atm.controller;

import com.sbms.atm.dto.request.CashBinRequest;
import com.sbms.atm.dto.response.CashBinResponse;
import com.sbms.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICashBinController {

    ApiResponse<CashBinResponse> create(CashBinRequest request);

    ApiResponse<List<CashBinResponse>> list();

    ApiResponse<CashBinResponse> getById(Long id);

    ApiResponse<CashBinResponse> update(Long id, CashBinRequest request);

    ApiResponse<CashBinResponse> archive(Long id);

    ApiResponse<CashBinResponse> restore(Long id);

    ApiResponse<List<CashBinResponse>> byTerminal(Long terminalId);
    ResponseEntity<byte[]> previewProfile(Long id);
    ResponseEntity<byte[]> downloadProfile(Long id);
}
