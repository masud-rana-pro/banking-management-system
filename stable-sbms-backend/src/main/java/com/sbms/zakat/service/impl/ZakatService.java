package com.sbms.zakat.service.impl;

import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.common.document.PdfDocumentService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.customer.repository.CustomerRepository;
import com.sbms.zakat.dto.request.CharityBeneficiaryRequest;
import com.sbms.zakat.dto.request.CharityPayoutRequest;
import com.sbms.zakat.dto.request.ZakatCalculationRequest;
import com.sbms.zakat.dto.request.ZakatProfileRequest;
import com.sbms.zakat.dto.response.*;
import com.sbms.zakat.entity.*;
import com.sbms.zakat.enums.CharityFundSourceType;
import com.sbms.zakat.enums.ZakatCalculationStatus;
import com.sbms.zakat.repository.*;
import com.sbms.zakat.service.IZakatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class ZakatService implements IZakatService {

    private static final BigDecimal ZAKAT_RATE = new BigDecimal("0.025");
    private static final DateTimeFormatter RECEIPT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    @Autowired
    private ZakatProfileRepository zakatProfileRepository;

    @Autowired
    private CharityFundRepository charityFundRepository;

    @Autowired
    private CharityBeneficiaryRepository beneficiaryRepository;

    @Autowired
    private CharityPayoutRepository payoutRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AutomatedMailService automatedMailService;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Override
    public ZakatProfileResponse createProfile(ZakatProfileRequest request) {
        validateProfileRequest(request);
        zakatProfileRepository.findByCustomerAndYear(request.getCustomerId(), request.getZakatYear())
                .ifPresent(existing -> {
                    throw new BadRequestException("Zakat profile already exists for this customer and year");
                });

        Customer customer = findCustomer(request.getCustomerId());
        ZakatProfile profile = new ZakatProfile();
        profile.setCustomer(customer);
        profile.setZakatYear(request.getZakatYear());
        profile.setNisabAmount(safeAmount(request.getNisabAmount()));
        profile.setEligibleAssetAmount(safeAmount(request.getEligibleAssetAmount()));
        profile.setRemarks(trim(request.getRemarks()));
        profile.setProofDocumentName(trim(request.getProofDocumentName()));
        profile.setCalculationStatus(ZakatCalculationStatus.PROFILED);
        return mapProfile(zakatProfileRepository.save(profile));
    }

    @Override
    public ZakatProfileResponse updateProfile(Long id, ZakatProfileRequest request) {
        validateProfileRequest(request);
        ZakatProfile profile = getProfile(id);
        zakatProfileRepository.findByCustomerAndYear(request.getCustomerId(), request.getZakatYear())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BadRequestException("Zakat profile already exists for this customer and year");
                    }
                });
        profile.setCustomer(findCustomer(request.getCustomerId()));
        profile.setZakatYear(request.getZakatYear());
        profile.setNisabAmount(safeAmount(request.getNisabAmount()));
        profile.setEligibleAssetAmount(safeAmount(request.getEligibleAssetAmount()));
        profile.setRemarks(trim(request.getRemarks()));
        profile.setProofDocumentName(trim(request.getProofDocumentName()));
        if (profile.getCalculationStatus() == ZakatCalculationStatus.DEDUCTED) {
            profile.setCalculationStatus(ZakatCalculationStatus.CALCULATED);
        }
        return mapProfile(zakatProfileRepository.update(profile));
    }

    @Override
    public List<ZakatProfileResponse> listProfiles(Long customerId, Integer zakatYear, String keyword) {
        return zakatProfileRepository.findAll(customerId, zakatYear, keyword).stream()
                .map(this::mapProfile)
                .toList();
    }

    @Override
    public ZakatProfileResponse getProfileById(Long id) {
        return mapProfile(getProfile(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewProfileSheet(Long id) {
        ZakatProfileResponse profile = getProfileById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildProfileSheetHtml(profile));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("zakat-profile-sheet-" + id + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadProfileSheet(Long id) {
        ZakatProfileResponse profile = getProfileById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildProfileSheetHtml(profile));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("zakat-profile-sheet-" + id + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    public ZakatProfileResponse calculate(ZakatCalculationRequest request) {
        if (request == null || request.getProfileId() == null) {
            throw new BadRequestException("Zakat profile is required for calculation");
        }

        ZakatProfile profile = getProfile(request.getProfileId());
        BigDecimal nisab = request.getNisabAmount() == null ? profile.getNisabAmount() : safeAmount(request.getNisabAmount());
        BigDecimal eligible = request.getEligibleAssetAmount() == null ? profile.getEligibleAssetAmount() : safeAmount(request.getEligibleAssetAmount());
        String remarks = trim(request.getRemarks());

        profile.setNisabAmount(nisab);
        profile.setEligibleAssetAmount(eligible);
        profile.setRemarks(remarks);

        if (eligible.compareTo(nisab) <= 0) {
            profile.setZakatAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            profile.setCalculationStatus(ZakatCalculationStatus.BELOW_NISAB);
            ZakatProfileResponse response = mapProfile(zakatProfileRepository.update(profile));
            sendZakatProfileMail(profile, "Below nisab", remarks);
            return response;
        }

        BigDecimal zakat = eligible.multiply(ZAKAT_RATE).setScale(2, RoundingMode.HALF_UP);
        profile.setZakatAmount(zakat);

        if (profile.getCalculationStatus() != ZakatCalculationStatus.DEDUCTED) {
            profile.setCalculationStatus(ZakatCalculationStatus.DEDUCTED);
            createFundMovement(
                    LocalDate.now(),
                    CharityFundSourceType.ZAKAT_DEDUCTION,
                    profile.getId(),
                    zakat,
                    BigDecimal.ZERO,
                    buildZakatFundRemark(profile)
            );
        } else {
            profile.setCalculationStatus(ZakatCalculationStatus.DEDUCTED);
        }

        ZakatProfileResponse response = mapProfile(zakatProfileRepository.update(profile));
        sendZakatProfileMail(profile, "Calculated and deducted", remarks);
        return response;
    }

    @Override
    public List<CharityFundResponse> getCharityFund() {
        return charityFundRepository.findAll().stream()
                .map(this::mapFund)
                .toList();
    }

    @Override
    public List<CharityBeneficiaryResponse> listBeneficiaries(String keyword) {
        return beneficiaryRepository.findAll(keyword).stream()
                .map(this::mapBeneficiary)
                .toList();
    }

    @Override
    public CharityBeneficiaryResponse createBeneficiary(CharityBeneficiaryRequest request) {
        validateBeneficiaryRequest(request, null);
        CharityBeneficiary entity = new CharityBeneficiary();
        entity.setBeneficiaryCode(resolveBeneficiaryCode(request.getBeneficiaryCode()));
        entity.setBeneficiaryName(trim(request.getBeneficiaryName()));
        entity.setMobile(trim(request.getMobile()));
        entity.setAddress(trim(request.getAddress()));
        entity.setProofDocumentName(trim(request.getProofDocumentName()));
        entity.setStatus(RecordStatus.ACTIVE);
        return mapBeneficiary(beneficiaryRepository.save(entity));
    }

    @Override
    public CharityBeneficiaryResponse updateBeneficiary(Long id, CharityBeneficiaryRequest request) {
        validateBeneficiaryRequest(request, id);
        CharityBeneficiary entity = getBeneficiary(id);
        entity.setBeneficiaryCode(resolveBeneficiaryCodeForUpdate(trim(request.getBeneficiaryCode()), id));
        entity.setBeneficiaryName(trim(request.getBeneficiaryName()));
        entity.setMobile(trim(request.getMobile()));
        entity.setAddress(trim(request.getAddress()));
        entity.setProofDocumentName(trim(request.getProofDocumentName()));
        return mapBeneficiary(beneficiaryRepository.update(entity));
    }

    @Override
    public CharityBeneficiaryResponse archiveBeneficiary(Long id) {
        CharityBeneficiary entity = getBeneficiary(id);
        entity.setStatus(RecordStatus.ARCHIVED);
        return mapBeneficiary(beneficiaryRepository.update(entity));
    }

    @Override
    public CharityBeneficiaryResponse restoreBeneficiary(Long id) {
        CharityBeneficiary entity = getBeneficiary(id);
        entity.setStatus(RecordStatus.ACTIVE);
        return mapBeneficiary(beneficiaryRepository.update(entity));
    }

    @Override
    public List<CharityPayoutResponse> listPayouts() {
        return payoutRepository.findAll().stream()
                .map(this::mapPayout)
                .toList();
    }

    @Override
    public CharityPayoutResponse createPayout(CharityPayoutRequest request) {
        validatePayoutRequest(request);
        CharityBeneficiary beneficiary = beneficiaryRepository.findActiveById(request.getBeneficiaryId())
                .orElseThrow(() -> new BadRequestException("Beneficiary must be active for payout"));
        BigDecimal amount = safeAmount(request.getAmount());
        BigDecimal currentBalance = charityFundRepository.currentBalance();
        if (amount.compareTo(currentBalance) > 0) {
            throw new BadRequestException("Insufficient charity fund balance for payout");
        }

        CharityPayout payout = new CharityPayout();
        payout.setBeneficiary(beneficiary);
        payout.setPayoutDate(request.getPayoutDate() == null ? LocalDate.now() : request.getPayoutDate());
        payout.setAmount(amount);
        payout.setApprovedBy(trim(request.getApprovedBy()));
        payout.setRemarks(trim(request.getRemarks()));
        payout.setStatus(RecordStatus.ACTIVE);
        CharityPayout saved = payoutRepository.save(payout);

        createFundMovement(
                saved.getPayoutDate(),
                CharityFundSourceType.PAYOUT,
                saved.getId(),
                BigDecimal.ZERO,
                amount,
                "Charity payout to " + beneficiary.getBeneficiaryCode() + " - " + beneficiary.getBeneficiaryName()
        );

        return mapPayout(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewPayoutReceipt(Long id) {
        CharityPayoutResponse payout = getPayoutResponse(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildPayoutReceiptHtml(payout));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("charity-payout-receipt-" + payout.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadPayoutReceipt(Long id) {
        CharityPayoutResponse payout = getPayoutResponse(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildPayoutReceiptHtml(payout));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("charity-payout-receipt-" + payout.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    public ZakatDashboardSummaryResponse dashboardSummary() {
        return new ZakatDashboardSummaryResponse(
                zakatProfileRepository.countDueAccounts(),
                zakatProfileRepository.sumCalculatedAmount(),
                charityFundRepository.currentBalance(),
                payoutRepository.sumPayoutAmount(),
                zakatProfileRepository.countUpcomingReminders(),
                zakatProfileRepository.findLatest(5).stream().map(this::mapProfile).toList(),
                charityFundRepository.findLatest(8).stream().map(this::mapFund).toList(),
                payoutRepository.findLatest(5).stream().map(this::mapPayout).toList()
        );
    }

    private void createFundMovement(LocalDate fundDate, CharityFundSourceType sourceType, Long referenceId,
                                    BigDecimal creditAmount, BigDecimal debitAmount, String remarks) {
        BigDecimal currentBalance = charityFundRepository.currentBalance();
        CharityFund fund = new CharityFund();
        fund.setFundDate(fundDate);
        fund.setSourceType(sourceType);
        fund.setReferenceId(referenceId);
        fund.setCreditAmount(creditAmount);
        fund.setDebitAmount(debitAmount);
        fund.setBalanceAfter(currentBalance.add(creditAmount).subtract(debitAmount).setScale(2, RoundingMode.HALF_UP));
        fund.setRemarks(remarks);
        charityFundRepository.save(fund);
    }

    private Customer findCustomer(Long id) {
        return customerRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private ZakatProfile getProfile(Long id) {
        return zakatProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zakat profile not found"));
    }

    private CharityBeneficiary getBeneficiary(Long id) {
        return beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charity beneficiary not found"));
    }

    private CharityPayout getPayout(Long id) {
        return payoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charity payout not found"));
    }

    private void validateProfileRequest(ZakatProfileRequest request) {
        if (request == null) throw new BadRequestException("Zakat profile request is required");
        if (request.getCustomerId() == null) throw new BadRequestException("Customer is required");
        if (request.getZakatYear() == null || request.getZakatYear() < 2000 || request.getZakatYear() > Year.now().getValue() + 2) {
            throw new BadRequestException("Valid zakat year is required");
        }
        if (safeAmount(request.getNisabAmount()).compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Nisab amount must be greater than zero");
        }
        if (safeAmount(request.getEligibleAssetAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Eligible asset amount cannot be negative");
        }
    }

    private void validateBeneficiaryRequest(CharityBeneficiaryRequest request, Long id) {
        if (request == null) throw new BadRequestException("Beneficiary request is required");
        String name = trim(request.getBeneficiaryName());
        if (name == null) throw new BadRequestException("Beneficiary name is required");
        String code = trim(request.getBeneficiaryCode());
        if (code != null) {
            if (id == null && beneficiaryRepository.existsByBeneficiaryCode(code)) {
                throw new BadRequestException("Beneficiary code already exists");
            }
            if (id != null && beneficiaryRepository.existsByBeneficiaryCodeExceptId(code, id)) {
                throw new BadRequestException("Beneficiary code already exists");
            }
        }
    }

    private void validatePayoutRequest(CharityPayoutRequest request) {
        if (request == null) throw new BadRequestException("Payout request is required");
        if (request.getBeneficiaryId() == null) throw new BadRequestException("Beneficiary is required");
        if (safeAmount(request.getAmount()).compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException("Payout amount must be greater than zero");
        if (trim(request.getApprovedBy()) == null) throw new BadRequestException("Approved by is required");
    }

    private String resolveBeneficiaryCode(String code) {
        String trimmed = trim(code);
        if (trimmed != null) {
            return trimmed;
        }
        String last = beneficiaryRepository.findLastBeneficiaryCode();
        int next = 1;
        if (last != null && last.matches("BEN-\\d+")) {
            next = Integer.parseInt(last.substring(4)) + 1;
        }
        return String.format("BEN-%05d", next);
    }

    private String resolveBeneficiaryCodeForUpdate(String code, Long id) {
        if (code != null) {
            return code;
        }
        return getBeneficiary(id).getBeneficiaryCode();
    }

    private String buildZakatFundRemark(ZakatProfile profile) {
        return "Zakat deduction for " + profile.getCustomer().getCustomerCode() + " year " + profile.getZakatYear();
    }

    private ZakatProfileResponse mapProfile(ZakatProfile entity) {
        return new ZakatProfileResponse(
                entity.getId(),
                entity.getCustomer().getId(),
                entity.getCustomer().getCustomerCode(),
                entity.getCustomer().getFullName(),
                entity.getZakatYear(),
                entity.getNisabAmount(),
                entity.getEligibleAssetAmount(),
                entity.getZakatAmount(),
                entity.getCalculationStatus().name(),
                entity.getRemarks(),
                entity.getProofDocumentName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private CharityFundResponse mapFund(CharityFund entity) {
        return new CharityFundResponse(
                entity.getId(),
                entity.getFundDate(),
                entity.getSourceType().name(),
                entity.getReferenceId(),
                entity.getCreditAmount(),
                entity.getDebitAmount(),
                entity.getBalanceAfter(),
                entity.getRemarks(),
                entity.getCreatedAt()
        );
    }

    private CharityBeneficiaryResponse mapBeneficiary(CharityBeneficiary entity) {
        return new CharityBeneficiaryResponse(
                entity.getId(),
                entity.getBeneficiaryCode(),
                entity.getBeneficiaryName(),
                entity.getMobile(),
                entity.getAddress(),
                entity.getProofDocumentName(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                payoutRepository.countByBeneficiaryId(entity.getId())
        );
    }

    private CharityPayoutResponse mapPayout(CharityPayout entity) {
        return new CharityPayoutResponse(
                entity.getId(),
                entity.getBeneficiary().getId(),
                entity.getBeneficiary().getBeneficiaryCode(),
                entity.getBeneficiary().getBeneficiaryName(),
                entity.getPayoutDate(),
                entity.getAmount(),
                entity.getApprovedBy(),
                entity.getRemarks(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private String trim(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private void sendZakatProfileMail(ZakatProfile profile, String decision, String remarks) {
        if (profile == null || profile.getCustomer() == null || trim(profile.getCustomer().getEmail()) == null) {
            return;
        }
        automatedMailService.sendApprovalDecisionEmail(
                profile.getCustomer().getEmail(),
                "Zakat Profile",
                profile.getCustomer().getCustomerCode() + " / " + profile.getZakatYear(),
                decision,
                remarks,
                "/zakat/profiles/" + profile.getId(),
                "Open Zakat Profile"
        );
    }

    private CharityPayoutResponse getPayoutResponse(Long id) {
        return mapPayout(getPayout(id));
    }

    private String buildPayoutReceiptHtml(CharityPayoutResponse payout) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("item", payout);
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(
                ".doc-accent{background:linear-gradient(90deg,#0d6045,#d4af37,#0b6b4d);}" +
                ".receipt-panel{padding:0 30px 10px;}" +
                ".receipt-note{font-size:13px;color:#5f6f67;line-height:1.7;}" +
                ".receipt-highlight{padding:18px 22px;border:1px solid #dbe7e1;border-radius:18px;background:linear-gradient(180deg,#fcfdfb,#f6faf7);}" +
                ".receipt-highlight strong{font-size:18px;color:#17452e;}"
        ));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        model.put("issuedDate", LocalDate.now().format(RECEIPT_DATE_FORMATTER));
        return documentTemplateService.render("zakat/charity-payout-receipt", model);
    }

    private String buildProfileSheetHtml(ZakatProfileResponse profile) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("item", profile);
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(
                ".doc-accent{background:linear-gradient(90deg,#0d6045,#d4af37,#0d6f50);}" +
                ".certificate-panel{margin:18px 30px 0;padding:22px 24px;border:1px solid #dbe7e1;border-radius:20px;background:linear-gradient(180deg,#fcfdfb,#f6faf7);}" +
                ".certificate-kicker{font-size:11px;font-weight:800;letter-spacing:.14em;text-transform:uppercase;color:#0b694d;margin-bottom:8px;}" +
                ".certificate-panel h1{margin:0;font-size:28px;line-height:1.12;color:#16281f;}" +
                ".certificate-panel p{margin:12px 0 0;font-size:14px;line-height:1.85;color:#42564b;}" +
                ".certificate-panel strong{color:#17452e;}"
        ));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        model.put("issuedDate", LocalDate.now().format(RECEIPT_DATE_FORMATTER));
        return documentTemplateService.render("zakat/zakat-profile-sheet", model);
    }
}
