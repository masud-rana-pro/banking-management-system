package com.sbms.atm.service;

import com.sbms.atm.dto.request.CashBinRequest;
import com.sbms.atm.dto.response.CashBinResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICashBinService {

    CashBinResponse create(CashBinRequest request);

    List<CashBinResponse> getAll();

    CashBinResponse getById(Long id);

    CashBinResponse update(Long id, CashBinRequest request);

    CashBinResponse archive(Long id);

    CashBinResponse restore(Long id);

    List<CashBinResponse> getByTerminal(Long terminalId);
    ResponseEntity<byte[]> previewProfile(Long id);
    ResponseEntity<byte[]> downloadProfile(Long id);
}
