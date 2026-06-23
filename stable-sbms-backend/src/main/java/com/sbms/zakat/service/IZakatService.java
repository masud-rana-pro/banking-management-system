package com.sbms.zakat.service;

import com.sbms.zakat.dto.request.CharityBeneficiaryRequest;
import com.sbms.zakat.dto.request.CharityPayoutRequest;
import com.sbms.zakat.dto.request.ZakatCalculationRequest;
import com.sbms.zakat.dto.request.ZakatProfileRequest;
import com.sbms.zakat.dto.response.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IZakatService {
    ZakatProfileResponse createProfile(ZakatProfileRequest request);
    ZakatProfileResponse updateProfile(Long id, ZakatProfileRequest request);
    List<ZakatProfileResponse> listProfiles(Long customerId, Integer zakatYear, String keyword);
    ZakatProfileResponse getProfileById(Long id);
    ResponseEntity<byte[]> previewProfileSheet(Long id);
    ResponseEntity<byte[]> downloadProfileSheet(Long id);
    ZakatProfileResponse calculate(ZakatCalculationRequest request);
    List<CharityFundResponse> getCharityFund();
    List<CharityBeneficiaryResponse> listBeneficiaries(String keyword);
    CharityBeneficiaryResponse createBeneficiary(CharityBeneficiaryRequest request);
    CharityBeneficiaryResponse updateBeneficiary(Long id, CharityBeneficiaryRequest request);
    CharityBeneficiaryResponse archiveBeneficiary(Long id);
    CharityBeneficiaryResponse restoreBeneficiary(Long id);
    List<CharityPayoutResponse> listPayouts();
    CharityPayoutResponse createPayout(CharityPayoutRequest request);
    ResponseEntity<byte[]> previewPayoutReceipt(Long id);
    ResponseEntity<byte[]> downloadPayoutReceipt(Long id);
    ZakatDashboardSummaryResponse dashboardSummary();
}
