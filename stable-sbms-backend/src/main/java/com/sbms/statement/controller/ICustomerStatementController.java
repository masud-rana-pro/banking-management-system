package com.sbms.statement.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.statement.dto.request.CustomerStatementRequestDto;
import com.sbms.statement.dto.response.CustomerStatementRequestResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICustomerStatementController {
    ApiResponse<CustomerStatementRequestResponse> request(CustomerStatementRequestDto request);
    ApiResponse<List<CustomerStatementRequestResponse>> list();
    ApiResponse<CustomerStatementRequestResponse> getById(Long id);
    ResponseEntity<byte[]> preview(Long id);
    ResponseEntity<byte[]> download(Long id);
    ResponseEntity<byte[]> export(String exportType, String search, String status);
}
