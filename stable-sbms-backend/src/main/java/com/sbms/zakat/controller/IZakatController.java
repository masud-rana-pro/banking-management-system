package com.sbms.zakat.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.zakat.dto.request.CharityBeneficiaryRequest;
import com.sbms.zakat.dto.request.CharityPayoutRequest;
import com.sbms.zakat.dto.request.ZakatCalculationRequest;
import com.sbms.zakat.dto.request.ZakatProfileRequest;
import com.sbms.zakat.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IZakatController {
    ApiResponse<ZakatProfileResponse> createProfile(ZakatProfileRequest request);
    ApiResponse<ZakatProfileResponse> updateProfile(Long id, ZakatProfileRequest request);
    ApiResponse<List<ZakatProfileResponse>> listProfiles(Long customerId, Integer zakatYear, String keyword);
    ApiResponse<ZakatProfileResponse> getProfileById(Long id);
    ResponseEntity<byte[]> previewProfileSheet(Long id);
    ResponseEntity<byte[]> downloadProfileSheet(Long id);
    ApiResponse<ZakatProfileResponse> calculate(ZakatCalculationRequest request, HttpServletRequest servletRequest);
    ApiResponse<List<CharityFundResponse>> getCharityFund();
    ApiResponse<List<CharityBeneficiaryResponse>> listBeneficiaries(String keyword);
    ApiResponse<CharityBeneficiaryResponse> createBeneficiary(CharityBeneficiaryRequest request);
    ApiResponse<CharityBeneficiaryResponse> updateBeneficiary(Long id, CharityBeneficiaryRequest request);
    ApiResponse<CharityBeneficiaryResponse> archiveBeneficiary(Long id);
    ApiResponse<CharityBeneficiaryResponse> restoreBeneficiary(Long id);
    ApiResponse<List<CharityPayoutResponse>> listPayouts();
    ApiResponse<CharityPayoutResponse> createPayout(CharityPayoutRequest request, HttpServletRequest servletRequest);
    ResponseEntity<byte[]> previewPayoutReceipt(Long id);
    ResponseEntity<byte[]> downloadPayoutReceipt(Long id);
    ApiResponse<ZakatDashboardSummaryResponse> dashboardSummary();
}
