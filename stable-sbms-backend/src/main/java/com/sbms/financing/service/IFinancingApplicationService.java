package com.sbms.financing.service;

import com.sbms.financing.dto.request.FinancingApplicationRequest;
import com.sbms.financing.dto.request.FinancingDisbursementRequest;
import com.sbms.financing.dto.request.FinancingRepaymentCollectionRequest;
import com.sbms.financing.dto.request.FinancingVerifyRequest;
import com.sbms.financing.dto.request.FinancingWorkflowActionRequest;
import com.sbms.financing.dto.response.FinancingApplicationResponse;
import com.sbms.financing.dto.response.FinancingDashboardSummaryResponse;
import com.sbms.financing.dto.response.FinancingRepaymentCollectionResponse;
import com.sbms.financing.dto.response.FinancingScheduleResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IFinancingApplicationService {
    FinancingApplicationResponse create(FinancingApplicationRequest request);
    List<FinancingApplicationResponse> list(Long productId, Long customerId, Long branchId, String keyword);
    FinancingApplicationResponse getById(Long id);
    FinancingApplicationResponse update(Long id, FinancingApplicationRequest request);
    FinancingApplicationResponse submit(Long id, FinancingWorkflowActionRequest request);
    FinancingApplicationResponse verify(Long id, FinancingVerifyRequest request);
    FinancingApplicationResponse review(Long id, FinancingWorkflowActionRequest request);
    FinancingApplicationResponse approve(Long id, FinancingWorkflowActionRequest request);
    FinancingApplicationResponse reject(Long id, FinancingWorkflowActionRequest request);
    FinancingApplicationResponse returnApplication(Long id, FinancingWorkflowActionRequest request);
    FinancingApplicationResponse archive(Long id);
    FinancingApplicationResponse restore(Long id);
    FinancingApplicationResponse disburse(Long id, FinancingDisbursementRequest request);
    List<FinancingScheduleResponse> getSchedule(Long id);
    FinancingRepaymentCollectionResponse collectPayment(Long id, FinancingRepaymentCollectionRequest request);
    FinancingDashboardSummaryResponse getDashboardSummary();
    ResponseEntity<byte[]> previewSanctionLetter(Long id);
    ResponseEntity<byte[]> downloadSanctionLetter(Long id);
}
