package com.sbms.atm.controller;

import com.sbms.atm.dto.request.AtmTerminalRequest;
import com.sbms.atm.dto.response.AtmDashboardSummaryResponse;
import com.sbms.atm.dto.response.AtmTerminalDropdownResponse;
import com.sbms.atm.dto.response.AtmTerminalResponse;
import com.sbms.atm.dto.response.DeviceJournalResponse;
import com.sbms.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAtmTerminalController {

    ApiResponse<AtmTerminalResponse> create(AtmTerminalRequest request);

    ApiResponse<List<AtmTerminalResponse>> list();

    ApiResponse<AtmTerminalResponse> getById(Long id);

    ApiResponse<AtmTerminalResponse> update(Long id, AtmTerminalRequest request);

    ApiResponse<AtmTerminalResponse> archive(Long id);

    ApiResponse<AtmTerminalResponse> restore(Long id);

    ApiResponse<List<AtmTerminalDropdownResponse>> dropdown();

    ApiResponse<AtmDashboardSummaryResponse> dashboardSummary();

    ApiResponse<List<DeviceJournalResponse>> deviceJournal(Long terminalId);
    ResponseEntity<byte[]> previewProfile(Long id);
    ResponseEntity<byte[]> downloadProfile(Long id);
}
