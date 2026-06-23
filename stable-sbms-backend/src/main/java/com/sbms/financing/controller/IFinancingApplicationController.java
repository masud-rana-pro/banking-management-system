package com.sbms.financing.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.financing.dto.request.FinancingApplicationRequest;
import com.sbms.financing.dto.request.FinancingDisbursementRequest;
import com.sbms.financing.dto.request.FinancingRepaymentCollectionRequest;
import com.sbms.financing.dto.request.FinancingVerifyRequest;
import com.sbms.financing.dto.request.FinancingWorkflowActionRequest;
import com.sbms.financing.dto.response.FinancingApplicationResponse;
import com.sbms.financing.dto.response.FinancingRepaymentCollectionResponse;
import com.sbms.financing.dto.response.FinancingScheduleResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IFinancingApplicationController {
    ApiResponse<FinancingApplicationResponse> create(FinancingApplicationRequest request);
    ApiResponse<List<FinancingApplicationResponse>> list(Long productId, Long customerId, Long branchId, String keyword);
    ApiResponse<FinancingApplicationResponse> getById(Long id);
    ApiResponse<FinancingApplicationResponse> update(Long id, FinancingApplicationRequest request);
    ApiResponse<FinancingApplicationResponse> submit(Long id, FinancingWorkflowActionRequest request);
    ApiResponse<FinancingApplicationResponse> verify(Long id, FinancingVerifyRequest request);
    ApiResponse<FinancingApplicationResponse> review(Long id, FinancingWorkflowActionRequest request);
    ApiResponse<FinancingApplicationResponse> approve(Long id, FinancingWorkflowActionRequest request, HttpServletRequest servletRequest);
    ApiResponse<FinancingApplicationResponse> reject(Long id, FinancingWorkflowActionRequest request, HttpServletRequest servletRequest);
    ApiResponse<FinancingApplicationResponse> returnApplication(Long id, FinancingWorkflowActionRequest request, HttpServletRequest servletRequest);
    ApiResponse<FinancingApplicationResponse> archive(Long id);
    ApiResponse<FinancingApplicationResponse> restore(Long id);
    ApiResponse<FinancingApplicationResponse> disburse(Long id, FinancingDisbursementRequest request, HttpServletRequest servletRequest);
    ApiResponse<List<FinancingScheduleResponse>> getSchedule(Long id);
    ApiResponse<FinancingRepaymentCollectionResponse> collectPayment(Long id, FinancingRepaymentCollectionRequest request, HttpServletRequest servletRequest);
    ResponseEntity<byte[]> previewSanctionLetter(Long id);
    ResponseEntity<byte[]> downloadSanctionLetter(Long id);
}
