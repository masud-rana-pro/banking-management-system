package com.sbms.kyc.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.kyc.dto.request.KycActionRequest;
import com.sbms.kyc.dto.request.KycDocumentRequest;
import com.sbms.kyc.dto.request.KycProfileRequest;
import com.sbms.kyc.dto.response.KycDashboardSummaryResponse;
import com.sbms.kyc.dto.response.KycDecisionHistoryResponse;
import com.sbms.kyc.dto.response.KycDocumentResponse;
import com.sbms.kyc.dto.response.KycProfileResponse;

import java.util.List;

public interface IKycController {

    ApiResponse<KycProfileResponse> createProfile(KycProfileRequest request);

    ApiResponse<List<KycProfileResponse>> listProfiles();

    ApiResponse<KycProfileResponse> getProfileByCustomerId(Long customerId);

    ApiResponse<KycProfileResponse> getProfileById(Long id);

    ApiResponse<KycProfileResponse> updateProfile(Long id, KycProfileRequest request);

    ApiResponse<KycProfileResponse> submitProfile(Long id);

    ApiResponse<KycProfileResponse> verifyProfile(Long id);

    ApiResponse<KycProfileResponse> approveProfile(Long id);

    ApiResponse<KycProfileResponse> rejectProfile(Long id, KycActionRequest request);

    ApiResponse<KycProfileResponse> returnProfile(Long id, KycActionRequest request);

    ApiResponse<KycDocumentResponse> uploadDocument(KycDocumentRequest request);

    ApiResponse<List<KycDocumentResponse>> getDocumentsByProfile(Long id);

    ApiResponse<List<KycDecisionHistoryResponse>> getDecisionHistory(Long kycId);

    ApiResponse<KycDashboardSummaryResponse> dashboardSummary();
}
