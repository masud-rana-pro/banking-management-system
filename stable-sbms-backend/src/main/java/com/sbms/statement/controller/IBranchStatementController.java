package com.sbms.statement.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.statement.dto.request.BranchStatementRequestDto;
import com.sbms.statement.dto.response.BranchStatementRequestResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBranchStatementController {
    ApiResponse<BranchStatementRequestResponse> request(BranchStatementRequestDto request);
    ApiResponse<List<BranchStatementRequestResponse>> list();
    ApiResponse<BranchStatementRequestResponse> getById(Long id);
    ResponseEntity<byte[]> preview(Long id);
    ResponseEntity<byte[]> download(Long id);
    ResponseEntity<byte[]> export(String exportType, String search, String status);
}
