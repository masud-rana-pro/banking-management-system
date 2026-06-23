package com.sbms.kyc.service;

import com.sbms.kyc.dto.request.KycActionRequest;
import com.sbms.kyc.dto.request.KycDocumentRequest;
import com.sbms.kyc.dto.request.KycProfileRequest;
import com.sbms.kyc.dto.response.KycDashboardSummaryResponse;
import com.sbms.kyc.dto.response.KycDecisionHistoryResponse;
import com.sbms.kyc.dto.response.KycDocumentResponse;
import com.sbms.kyc.dto.response.KycProfileResponse;

import java.util.List;

public interface IKycService {

    KycProfileResponse createProfile(KycProfileRequest request, String username);

    List<KycProfileResponse> getProfiles();

    KycProfileResponse getProfileByCustomerId(Long customerId);

    KycProfileResponse getProfileById(Long id);

    KycProfileResponse updateProfile(Long id, KycProfileRequest request, String username);

    KycProfileResponse submitProfile(Long id, String username);

    KycProfileResponse verifyProfile(Long id, String username);

    KycProfileResponse approveProfile(Long id, String username);

    KycProfileResponse rejectProfile(Long id, KycActionRequest request, String username);

    KycProfileResponse returnProfile(Long id, KycActionRequest request, String username);

    KycDocumentResponse uploadDocument(KycDocumentRequest request);

    List<KycDocumentResponse> getDocumentsByProfile(Long profileId);

    List<KycDecisionHistoryResponse> getDecisionHistory(Long kycId);

    KycDashboardSummaryResponse dashboardSummary();
}
