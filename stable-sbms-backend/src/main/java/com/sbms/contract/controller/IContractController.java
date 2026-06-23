package com.sbms.contract.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.contract.dto.request.ContractGenerateRequest;
import com.sbms.contract.dto.request.ContractSignRequest;
import com.sbms.contract.dto.request.ContractTemplateRequest;
import com.sbms.contract.dto.response.ContractDashboardSummaryResponse;
import com.sbms.contract.dto.response.ContractResponse;
import com.sbms.contract.dto.response.ContractTemplateResponse;
import com.sbms.contract.dto.response.ContractVersionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IContractController {
    ApiResponse<ContractTemplateResponse> createTemplate(ContractTemplateRequest request);
    ApiResponse<List<ContractTemplateResponse>> listTemplates();
    ApiResponse<ContractTemplateResponse> getTemplateById(Long id);
    ApiResponse<ContractTemplateResponse> updateTemplate(Long id, ContractTemplateRequest request);
    ApiResponse<ContractTemplateResponse> archiveTemplate(Long id);
    ApiResponse<ContractTemplateResponse> restoreTemplate(Long id);
    ApiResponse<ContractResponse> generate(ContractGenerateRequest request, HttpServletRequest servletRequest);
    ApiResponse<List<ContractResponse>> list(Long templateId, Long customerId, String referenceModule, String keyword);
    ApiResponse<ContractResponse> getById(Long id);
    ResponseEntity<byte[]> previewPrintCopy(Long id);
    ResponseEntity<byte[]> downloadPrintCopy(Long id);
    ApiResponse<ContractResponse> customerSign(Long id, ContractSignRequest request, HttpServletRequest servletRequest);
    ApiResponse<ContractResponse> shariahSign(Long id, ContractSignRequest request, HttpServletRequest servletRequest);
    ApiResponse<List<ContractVersionResponse>> getVersions(Long id);
    ApiResponse<ContractDashboardSummaryResponse> dashboardSummary();
}
