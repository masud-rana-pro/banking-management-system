package com.sbms.depositscheme.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.depositscheme.dto.request.DepositSchemeEnrollmentRequest;
import com.sbms.depositscheme.dto.request.DepositSchemeRequest;
import com.sbms.depositscheme.dto.response.DepositSchemeDashboardSummaryResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeEnrollmentResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeProfitDistributionResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeScheduleResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IDepositSchemeController {

    ApiResponse<DepositSchemeResponse> create(DepositSchemeRequest request);

    ApiResponse<List<DepositSchemeResponse>> list();

    ApiResponse<DepositSchemeResponse> getById(Long id);

    ApiResponse<DepositSchemeResponse> update(Long id, DepositSchemeRequest request);

    ApiResponse<DepositSchemeResponse> archive(Long id);

    ApiResponse<DepositSchemeResponse> restore(Long id);

    ApiResponse<DepositSchemeEnrollmentResponse> createEnrollment(DepositSchemeEnrollmentRequest request);

    ApiResponse<List<DepositSchemeEnrollmentResponse>> listEnrollments(Long schemeId, Long customerId, Long accountId);

    ApiResponse<DepositSchemeEnrollmentResponse> getEnrollmentById(Long id);

    ResponseEntity<byte[]> previewEnrollmentCertificate(Long id);

    ResponseEntity<byte[]> downloadEnrollmentCertificate(Long id);

    ApiResponse<List<DepositSchemeScheduleResponse>> getEnrollmentSchedule(Long id);

    ApiResponse<List<DepositSchemeProfitDistributionResponse>> getEnrollmentProfitDistribution(Long id);

    ApiResponse<DepositSchemeDashboardSummaryResponse> getDashboardSummary();
}
