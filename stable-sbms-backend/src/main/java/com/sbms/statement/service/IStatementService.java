package com.sbms.statement.service;

import com.sbms.statement.dto.request.BranchStatementRequestDto;
import com.sbms.statement.dto.request.CustomerStatementRequestDto;
import com.sbms.statement.dto.response.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IStatementService {
    CustomerStatementRequestResponse requestCustomerStatement(CustomerStatementRequestDto request, String username);
    List<CustomerStatementRequestResponse> listCustomerStatements();
    CustomerStatementRequestResponse getCustomerStatement(Long id);
    ResponseEntity<byte[]> previewCustomerStatement(Long id);
    ResponseEntity<byte[]> downloadCustomerStatement(Long id);
    ResponseEntity<byte[]> exportCustomerStatements(String exportType, String search, String status);

    BranchStatementRequestResponse requestBranchStatement(BranchStatementRequestDto request, String username);
    List<BranchStatementRequestResponse> listBranchStatements();
    BranchStatementRequestResponse getBranchStatement(Long id);
    ResponseEntity<byte[]> previewBranchStatement(Long id);
    ResponseEntity<byte[]> downloadBranchStatement(Long id);
    ResponseEntity<byte[]> exportBranchStatements(String exportType, String search, String status);

    List<FileReferenceResponse> listFiles();
    StatementDashboardSummaryResponse dashboardSummary();
}
