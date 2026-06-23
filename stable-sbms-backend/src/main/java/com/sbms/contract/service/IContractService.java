package com.sbms.contract.service;

import com.sbms.contract.dto.request.ContractGenerateRequest;
import com.sbms.contract.dto.request.ContractSignRequest;
import com.sbms.contract.dto.response.ContractDashboardSummaryResponse;
import com.sbms.contract.dto.response.ContractResponse;
import com.sbms.contract.dto.response.ContractVersionResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IContractService {
    ContractResponse generate(ContractGenerateRequest request);
    List<ContractResponse> list(Long templateId, Long customerId, String referenceModule, String keyword);
    ContractResponse getById(Long id);
    ResponseEntity<byte[]> previewPrintCopy(Long id);
    ResponseEntity<byte[]> downloadPrintCopy(Long id);
    ContractResponse customerSign(Long id, ContractSignRequest request);
    ContractResponse shariahSign(Long id, ContractSignRequest request);
    List<ContractVersionResponse> getVersions(Long id);
    ContractDashboardSummaryResponse dashboardSummary();
}
