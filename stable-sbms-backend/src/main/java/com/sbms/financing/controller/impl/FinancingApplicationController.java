package com.sbms.financing.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.financing.controller.IFinancingApplicationController;
import com.sbms.financing.dto.request.FinancingApplicationRequest;
import com.sbms.financing.dto.request.FinancingDisbursementRequest;
import com.sbms.financing.dto.request.FinancingRepaymentCollectionRequest;
import com.sbms.financing.dto.request.FinancingVerifyRequest;
import com.sbms.financing.dto.request.FinancingWorkflowActionRequest;
import com.sbms.financing.dto.response.FinancingApplicationResponse;
import com.sbms.financing.dto.response.FinancingRepaymentCollectionResponse;
import com.sbms.financing.dto.response.FinancingScheduleResponse;
import com.sbms.financing.service.IFinancingApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financing-applications")
@RequiresPermission("FINANCING_ACCESS")
public class FinancingApplicationController implements IFinancingApplicationController {

    @Autowired
    private IFinancingApplicationService applicationService;

    @Override
    @RequiresPermission("FINANCING_APPLICATION_CREATE")
    @PostMapping("/create")
    public ApiResponse<FinancingApplicationResponse> create(@RequestBody FinancingApplicationRequest request) {
        return ResponseBuilder.success("Financing application created successfully", applicationService.create(request));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<FinancingApplicationResponse>> list(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Financing application list fetched successfully", applicationService.list(productId, customerId, branchId, keyword));
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<FinancingApplicationResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Financing application fetched successfully", applicationService.getById(id));
    }

    @Override
    @RequiresPermission("FINANCING_APPLICATION_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<FinancingApplicationResponse> update(@PathVariable Long id, @RequestBody FinancingApplicationRequest request) {
        return ResponseBuilder.success("Financing application updated successfully", applicationService.update(id, request));
    }

    @Override
    @RequiresPermission("FINANCING_APPLICATION_SUBMIT")
    @PostMapping("/{id}/submit")
    public ApiResponse<FinancingApplicationResponse> submit(@PathVariable Long id, @RequestBody(required = false) FinancingWorkflowActionRequest request) {
        return ResponseBuilder.success("Financing application submitted successfully", applicationService.submit(id, request));
    }

    @RequiresPermission("FINANCING_VERIFY")
    @Override
    @PostMapping("/{id}/verify")
    public ApiResponse<FinancingApplicationResponse> verify(@PathVariable Long id, @RequestBody FinancingVerifyRequest request) {
        return ResponseBuilder.success("Financing application verified successfully", applicationService.verify(id, request));
    }

    @RequiresPermission("FINANCING_REVIEW")
    @Override
    @PostMapping("/{id}/review")
    public ApiResponse<FinancingApplicationResponse> review(@PathVariable Long id, @RequestBody(required = false) FinancingWorkflowActionRequest request) {
        return ResponseBuilder.success("Financing application moved to shariah review successfully", applicationService.review(id, request));
    }

    @RequiresPermission("FINANCING_APPROVE")
    @Override
    @PostMapping("/{id}/approve")
    public ApiResponse<FinancingApplicationResponse> approve(@PathVariable Long id,
                                                             @RequestBody(required = false) FinancingWorkflowActionRequest request,
                                                             HttpServletRequest servletRequest) {
        FinancingApplicationResponse response = applicationService.approve(id, request);
        return ResponseBuilder.success("Financing application approved successfully", response);
    }

    @RequiresPermission("FINANCING_REJECT")
    @Override
    @PostMapping("/{id}/reject")
    public ApiResponse<FinancingApplicationResponse> reject(@PathVariable Long id,
                                                            @RequestBody FinancingWorkflowActionRequest request,
                                                            HttpServletRequest servletRequest) {
        FinancingApplicationResponse response = applicationService.reject(id, request);
        return ResponseBuilder.success("Financing application rejected successfully", response);
    }

    @RequiresPermission("FINANCING_RETURN")
    @Override
    @PostMapping("/{id}/return")
    public ApiResponse<FinancingApplicationResponse> returnApplication(@PathVariable Long id,
                                                                       @RequestBody FinancingWorkflowActionRequest request,
                                                                       HttpServletRequest servletRequest) {
        FinancingApplicationResponse response = applicationService.returnApplication(id, request);
        return ResponseBuilder.success("Financing application returned successfully", response);
    }

    @Override
    @RequiresPermission("FINANCING_APPLICATION_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<FinancingApplicationResponse> archive(@PathVariable Long id) {
        return ResponseBuilder.success("Financing application archived successfully", applicationService.archive(id));
    }

    @Override
    @RequiresPermission("FINANCING_APPLICATION_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<FinancingApplicationResponse> restore(@PathVariable Long id) {
        return ResponseBuilder.success("Financing application restored successfully", applicationService.restore(id));
    }

    @RequiresPermission("FINANCING_DISBURSE")
    @Override
    @PostMapping("/{id}/disburse")
    public ApiResponse<FinancingApplicationResponse> disburse(@PathVariable Long id,
                                                              @RequestBody FinancingDisbursementRequest request,
                                                              HttpServletRequest servletRequest) {
        FinancingApplicationResponse response = applicationService.disburse(id, request);
        return ResponseBuilder.success("Financing application disbursed successfully", response);
    }

    @Override
    @GetMapping("/{id}/schedule")
    public ApiResponse<List<FinancingScheduleResponse>> getSchedule(@PathVariable Long id) {
        return ResponseBuilder.success("Financing installment schedule fetched successfully", applicationService.getSchedule(id));
    }

    @RequiresPermission("FINANCING_COLLECT_PAYMENT")
    @Override
    @PostMapping("/{id}/collect-payment")
    public ApiResponse<FinancingRepaymentCollectionResponse> collectPayment(@PathVariable Long id,
                                                                            @RequestBody FinancingRepaymentCollectionRequest request,
                                                                            HttpServletRequest servletRequest) {
        FinancingRepaymentCollectionResponse response = applicationService.collectPayment(id, request);
        return ResponseBuilder.success("Financing repayment collected successfully", response);
    }

    @Override
    @GetMapping("/{id}/sanction-letter/preview")
    public ResponseEntity<byte[]> previewSanctionLetter(@PathVariable Long id) {
        return applicationService.previewSanctionLetter(id);
    }

    @Override
    @GetMapping("/{id}/sanction-letter/download")
    public ResponseEntity<byte[]> downloadSanctionLetter(@PathVariable Long id) {
        return applicationService.downloadSanctionLetter(id);
    }
}
