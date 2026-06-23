package com.sbms.depositscheme.service;

import com.sbms.depositscheme.dto.request.DepositSchemeEnrollmentRequest;
import com.sbms.depositscheme.dto.request.DepositSchemeRequest;
import com.sbms.depositscheme.dto.response.DepositSchemeDashboardSummaryResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeEnrollmentResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeProfitDistributionResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeScheduleResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IDepositSchemeService {

    DepositSchemeResponse create(DepositSchemeRequest request);

    List<DepositSchemeResponse> list();

    DepositSchemeResponse getById(Long id);

    DepositSchemeResponse update(Long id, DepositSchemeRequest request);

    DepositSchemeResponse archive(Long id);

    DepositSchemeResponse restore(Long id);

    DepositSchemeEnrollmentResponse createEnrollment(DepositSchemeEnrollmentRequest request);

    List<DepositSchemeEnrollmentResponse> listEnrollments(Long schemeId, Long customerId, Long accountId);

    DepositSchemeEnrollmentResponse getEnrollmentById(Long id);

    ResponseEntity<byte[]> previewEnrollmentCertificate(Long id);

    ResponseEntity<byte[]> downloadEnrollmentCertificate(Long id);

    List<DepositSchemeScheduleResponse> getEnrollmentSchedule(Long enrollmentId);

    List<DepositSchemeProfitDistributionResponse> getEnrollmentProfitDistribution(Long enrollmentId);

    DepositSchemeDashboardSummaryResponse getDashboardSummary();
}
