package com.sbms.atm.service;

import com.sbms.atm.dto.request.AtmTerminalRequest;
import com.sbms.atm.dto.response.AtmDashboardSummaryResponse;
import com.sbms.atm.dto.response.AtmTerminalDropdownResponse;
import com.sbms.atm.dto.response.AtmTerminalResponse;
import com.sbms.atm.dto.response.DeviceJournalResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAtmTerminalService {

    AtmTerminalResponse create(AtmTerminalRequest request);

    List<AtmTerminalResponse> getAll();

    AtmTerminalResponse getById(Long id);

    AtmTerminalResponse update(Long id, AtmTerminalRequest request);

    AtmTerminalResponse archive(Long id);

    AtmTerminalResponse restore(Long id);

    List<AtmTerminalDropdownResponse> dropdown();

    AtmDashboardSummaryResponse dashboardSummary();

    List<DeviceJournalResponse> deviceJournal(Long terminalId);
    ResponseEntity<byte[]> previewProfile(Long id);
    ResponseEntity<byte[]> downloadProfile(Long id);
}
