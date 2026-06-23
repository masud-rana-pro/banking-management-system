package com.sbms.contract.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.contract.controller.IContractController;
import com.sbms.contract.dto.request.ContractGenerateRequest;
import com.sbms.contract.dto.request.ContractSignRequest;
import com.sbms.contract.dto.request.ContractTemplateRequest;
import com.sbms.contract.dto.response.ContractDashboardSummaryResponse;
import com.sbms.contract.dto.response.ContractResponse;
import com.sbms.contract.dto.response.ContractTemplateResponse;
import com.sbms.contract.dto.response.ContractVersionResponse;
import com.sbms.contract.service.IContractService;
import com.sbms.contract.service.IContractTemplateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiresPermission("CONTRACTS_ACCESS")
public class ContractController implements IContractController {

    @Autowired
    private IContractTemplateService templateService;

    @Autowired
    private IContractService contractService;

    @Override
    @RequiresPermission("CONTRACT_TEMPLATE_CREATE")
    @PostMapping("/templates/create")
    public ApiResponse<ContractTemplateResponse> createTemplate(@RequestBody ContractTemplateRequest request) {
        return ResponseBuilder.success("Contract template created successfully", templateService.create(request));
    }

    @Override
    @GetMapping("/templates/list")
    public ApiResponse<List<ContractTemplateResponse>> listTemplates() {
        return ResponseBuilder.success("Contract template list fetched successfully", templateService.list());
    }

    @Override
    @GetMapping("/templates/{id}")
    public ApiResponse<ContractTemplateResponse> getTemplateById(@PathVariable Long id) {
        return ResponseBuilder.success("Contract template fetched successfully", templateService.getById(id));
    }

    @Override
    @RequiresPermission("CONTRACT_TEMPLATE_EDIT")
    @PutMapping("/templates/{id}")
    public ApiResponse<ContractTemplateResponse> updateTemplate(@PathVariable Long id, @RequestBody ContractTemplateRequest request) {
        return ResponseBuilder.success("Contract template updated successfully", templateService.update(id, request));
    }

    @Override
    @RequiresPermission("CONTRACT_TEMPLATE_ARCHIVE")
    @DeleteMapping("/templates/{id}")
    public ApiResponse<ContractTemplateResponse> archiveTemplate(@PathVariable Long id) {
        return ResponseBuilder.success("Contract template archived successfully", templateService.archive(id));
    }

    @Override
    @RequiresPermission("CONTRACT_TEMPLATE_RESTORE")
    @PutMapping("/templates/{id}/restore")
    public ApiResponse<ContractTemplateResponse> restoreTemplate(@PathVariable Long id) {
        return ResponseBuilder.success("Contract template restored successfully", templateService.restore(id));
    }

    @RequiresPermission("CONTRACT_GENERATE")
    @Override
    @PostMapping("/generate")
    public ApiResponse<ContractResponse> generate(@RequestBody ContractGenerateRequest request,
                                                  HttpServletRequest servletRequest) {
        ContractResponse response = contractService.generate(request);
        return ResponseBuilder.success("Contract generated successfully", response);
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<ContractResponse>> list(
            @RequestParam(required = false) Long templateId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String referenceModule,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Contract list fetched successfully", contractService.list(templateId, customerId, referenceModule, keyword));
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<ContractResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Contract fetched successfully", contractService.getById(id));
    }

    @Override
    @GetMapping("/{id}/print-copy/preview")
    public ResponseEntity<byte[]> previewPrintCopy(@PathVariable Long id) {
        return contractService.previewPrintCopy(id);
    }

    @Override
    @GetMapping("/{id}/print-copy/download")
    public ResponseEntity<byte[]> downloadPrintCopy(@PathVariable Long id) {
        return contractService.downloadPrintCopy(id);
    }

    @RequiresPermission("CONTRACT_CUSTOMER_SIGN")
    @Override
    @PostMapping("/{id}/customer-sign")
    public ApiResponse<ContractResponse> customerSign(@PathVariable Long id,
                                                      @RequestBody(required = false) ContractSignRequest request,
                                                      HttpServletRequest servletRequest) {
        ContractResponse response = contractService.customerSign(id, request);
        return ResponseBuilder.success("Customer signature captured successfully", response);
    }

    @RequiresPermission("CONTRACT_SHARIAH_SIGN")
    @Override
    @PostMapping("/{id}/shariah-sign")
    public ApiResponse<ContractResponse> shariahSign(@PathVariable Long id,
                                                     @RequestBody(required = false) ContractSignRequest request,
                                                     HttpServletRequest servletRequest) {
        ContractResponse response = contractService.shariahSign(id, request);
        return ResponseBuilder.success("Shariah signature captured successfully", response);
    }

    @Override
    @GetMapping("/{id}/versions")
    public ApiResponse<List<ContractVersionResponse>> getVersions(@PathVariable Long id) {
        return ResponseBuilder.success("Contract version history fetched successfully", contractService.getVersions(id));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<ContractDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Contract dashboard summary fetched successfully", contractService.dashboardSummary());
    }
}
