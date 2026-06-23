package com.sbms.zakat.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.zakat.controller.IZakatController;
import com.sbms.zakat.dto.request.CharityBeneficiaryRequest;
import com.sbms.zakat.dto.request.CharityPayoutRequest;
import com.sbms.zakat.dto.request.ZakatCalculationRequest;
import com.sbms.zakat.dto.request.ZakatProfileRequest;
import com.sbms.zakat.dto.response.*;
import com.sbms.zakat.service.IZakatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zakat")
@RequiresPermission("ZAKAT_CHARITY_ACCESS")
public class ZakatController implements IZakatController {

    @Autowired
    private IZakatService zakatService;

    @Override
    @RequiresPermission("ZAKAT_PROFILE_CREATE")
    @PostMapping("/profile/create")
    public ApiResponse<ZakatProfileResponse> createProfile(@RequestBody ZakatProfileRequest request) {
        return ResponseBuilder.success("Zakat profile created successfully", zakatService.createProfile(request));
    }

    @Override
    @RequiresPermission("ZAKAT_PROFILE_EDIT")
    @PutMapping("/profile/{id}")
    public ApiResponse<ZakatProfileResponse> updateProfile(@PathVariable Long id, @RequestBody ZakatProfileRequest request) {
        return ResponseBuilder.success("Zakat profile updated successfully", zakatService.updateProfile(id, request));
    }

    @Override
    @GetMapping("/profile/list")
    public ApiResponse<List<ZakatProfileResponse>> listProfiles(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer zakatYear,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Zakat profile list fetched successfully", zakatService.listProfiles(customerId, zakatYear, keyword));
    }

    @Override
    @GetMapping("/profile/{id}")
    public ApiResponse<ZakatProfileResponse> getProfileById(@PathVariable Long id) {
        return ResponseBuilder.success("Zakat profile fetched successfully", zakatService.getProfileById(id));
    }

    @Override
    @GetMapping("/profile/{id}/sheet/preview")
    public ResponseEntity<byte[]> previewProfileSheet(@PathVariable Long id) {
        return zakatService.previewProfileSheet(id);
    }

    @Override
    @GetMapping("/profile/{id}/sheet/download")
    public ResponseEntity<byte[]> downloadProfileSheet(@PathVariable Long id) {
        return zakatService.downloadProfileSheet(id);
    }

    @RequiresPermission("ZAKAT_CALCULATE")
    @Override
    @PostMapping("/calculate")
    public ApiResponse<ZakatProfileResponse> calculate(@RequestBody ZakatCalculationRequest request,
                                                       HttpServletRequest servletRequest) {
        ZakatProfileResponse response = zakatService.calculate(request);
        return ResponseBuilder.success("Zakat calculation completed successfully", response);
    }

    @Override
    @GetMapping("/charity-fund")
    public ApiResponse<List<CharityFundResponse>> getCharityFund() {
        return ResponseBuilder.success("Charity fund ledger fetched successfully", zakatService.getCharityFund());
    }

    @Override
    @GetMapping("/beneficiaries/list")
    public ApiResponse<List<CharityBeneficiaryResponse>> listBeneficiaries(@RequestParam(required = false) String keyword) {
        return ResponseBuilder.success("Charity beneficiary list fetched successfully", zakatService.listBeneficiaries(keyword));
    }

    @Override
    @RequiresPermission("CHARITY_BENEFICIARY_CREATE")
    @PostMapping("/beneficiaries/create")
    public ApiResponse<CharityBeneficiaryResponse> createBeneficiary(@RequestBody CharityBeneficiaryRequest request) {
        return ResponseBuilder.success("Charity beneficiary created successfully", zakatService.createBeneficiary(request));
    }

    @Override
    @RequiresPermission("CHARITY_BENEFICIARY_EDIT")
    @PutMapping("/beneficiaries/{id}")
    public ApiResponse<CharityBeneficiaryResponse> updateBeneficiary(@PathVariable Long id, @RequestBody CharityBeneficiaryRequest request) {
        return ResponseBuilder.success("Charity beneficiary updated successfully", zakatService.updateBeneficiary(id, request));
    }

    @Override
    @RequiresPermission("CHARITY_BENEFICIARY_ARCHIVE")
    @DeleteMapping("/beneficiaries/{id}")
    public ApiResponse<CharityBeneficiaryResponse> archiveBeneficiary(@PathVariable Long id) {
        return ResponseBuilder.success("Charity beneficiary archived successfully", zakatService.archiveBeneficiary(id));
    }

    @Override
    @RequiresPermission("CHARITY_BENEFICIARY_RESTORE")
    @PutMapping("/beneficiaries/{id}/restore")
    public ApiResponse<CharityBeneficiaryResponse> restoreBeneficiary(@PathVariable Long id) {
        return ResponseBuilder.success("Charity beneficiary restored successfully", zakatService.restoreBeneficiary(id));
    }

    @Override
    @GetMapping("/payouts/list")
    public ApiResponse<List<CharityPayoutResponse>> listPayouts() {
        return ResponseBuilder.success("Charity payout list fetched successfully", zakatService.listPayouts());
    }

    @RequiresPermission("CHARITY_PAYOUT_CREATE")
    @Override
    @PostMapping("/payouts/create")
    public ApiResponse<CharityPayoutResponse> createPayout(@RequestBody CharityPayoutRequest request,
                                                           HttpServletRequest servletRequest) {
        CharityPayoutResponse response = zakatService.createPayout(request);
        return ResponseBuilder.success("Charity payout created successfully", response);
    }

    @Override
    @GetMapping("/payouts/{id}/receipt/preview")
    public ResponseEntity<byte[]> previewPayoutReceipt(@PathVariable Long id) {
        return zakatService.previewPayoutReceipt(id);
    }

    @Override
    @GetMapping("/payouts/{id}/receipt/download")
    public ResponseEntity<byte[]> downloadPayoutReceipt(@PathVariable Long id) {
        return zakatService.downloadPayoutReceipt(id);
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<ZakatDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Zakat dashboard summary fetched successfully", zakatService.dashboardSummary());
    }
}
