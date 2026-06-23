package com.sbms.kyc.service.impl;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.CustomerStatus;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.customer.repository.CustomerRepository;
import com.sbms.kyc.dto.request.KycActionRequest;
import com.sbms.kyc.dto.request.KycDocumentRequest;
import com.sbms.kyc.dto.request.KycProfileRequest;
import com.sbms.kyc.dto.response.KycDashboardSummaryResponse;
import com.sbms.kyc.dto.response.KycDecisionHistoryResponse;
import com.sbms.kyc.dto.response.KycDocumentResponse;
import com.sbms.kyc.dto.response.KycProfileResponse;
import com.sbms.kyc.entity.CustomerDocument;
import com.sbms.kyc.entity.KycDecisionHistory;
import com.sbms.kyc.entity.KycProfile;
import com.sbms.kyc.enums.KycDecision;
import com.sbms.kyc.enums.KycReviewStatus;
import com.sbms.kyc.enums.RiskLevel;
import com.sbms.kyc.repository.CustomerDocumentRepository;
import com.sbms.kyc.repository.KycDecisionHistoryRepository;
import com.sbms.kyc.repository.KycProfileRepository;
import com.sbms.kyc.service.IKycService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class KycService implements IKycService {

    @Autowired
    private KycProfileRepository kycProfileRepository;

    @Autowired
    private CustomerDocumentRepository customerDocumentRepository;

    @Autowired
    private KycDecisionHistoryRepository kycDecisionHistoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AutomatedMailService automatedMailService;

    @Override
    public KycProfileResponse createProfile(KycProfileRequest request, String username) {
        validateProfileRequest(request);

        if (kycProfileRepository.existsByCustomerId(request.getCustomerId())) {
            throw new BadRequestException("KYC profile already exists for this customer");
        }

        Customer customer = getCustomer(request.getCustomerId());

        KycProfile profile = new KycProfile();
        profile.setCustomer(customer);
        mapProfileRequest(request, profile);

        if (profile.getReviewStatus() == null) {
            profile.setReviewStatus(KycReviewStatus.DRAFT);
        }

        return mapProfileResponse(kycProfileRepository.save(profile));
    }

    @Override
    public List<KycProfileResponse> getProfiles() {
        return kycProfileRepository.findAll()
                .stream()
                .map(this::mapProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    public KycProfileResponse getProfileByCustomerId(Long customerId) {
        if (customerId == null) {
            throw new BadRequestException("Customer id is required");
        }

        return mapProfileResponse(
                kycProfileRepository.findByCustomerId(customerId)
                        .orElseThrow(() -> new ResourceNotFoundException("KYC profile not found for this customer"))
        );
    }

    @Override
    public KycProfileResponse getProfileById(Long id) {
        return mapProfileResponse(getProfile(id));
    }

    @Override
    public KycProfileResponse updateProfile(Long id, KycProfileRequest request, String username) {
        validateProfileRequest(request);

        if (kycProfileRepository.existsByCustomerIdExceptId(request.getCustomerId(), id)) {
            throw new BadRequestException("Another KYC profile already exists for this customer");
        }

        KycProfile profile = getProfile(id);
        Customer customer = getCustomer(request.getCustomerId());

        profile.setCustomer(customer);
        mapProfileRequest(request, profile);

        return mapProfileResponse(kycProfileRepository.update(profile));
    }

    @Override
    public KycProfileResponse submitProfile(Long id, String username) {
        KycProfile profile = getProfile(id);
        ensureDocumentExists(profile);

        if (profile.getRiskLevel() == null) {
            throw new BadRequestException("Risk level is required before submitting KYC");
        }

        if (isBlank(profile.getSourceOfFundsNote())) {
            throw new BadRequestException("Source of funds note is required before submitting KYC");
        }

        profile.setReviewStatus(KycReviewStatus.SUBMITTED);
        updateCustomerStatus(profile.getCustomer(), CustomerStatus.PENDING_KYC);
        saveDecisionHistory(profile, KycDecision.SUBMIT, username, profile.getRemarks());

        return mapProfileResponse(kycProfileRepository.update(profile));
    }

    @Override
    public KycProfileResponse verifyProfile(Long id, String username) {
        KycProfile profile = getProfile(id);
        ensureDocumentExists(profile);

        if (profile.getRiskLevel() == null) {
            throw new BadRequestException("Risk level is required before verifying KYC");
        }

        profile.setReviewStatus(KycReviewStatus.VERIFIED);
        profile.setReviewedBy(username);
        profile.setReviewedAt(LocalDateTime.now());
        saveDecisionHistory(profile, KycDecision.VERIFY, username, profile.getRemarks());

        return mapProfileResponse(kycProfileRepository.update(profile));
    }

    @Override
    public KycProfileResponse approveProfile(Long id, String username) {
        KycProfile profile = getProfile(id);
        ensureDocumentExists(profile);

        if (profile.getReviewedBy() == null || profile.getReviewedBy().trim().isEmpty()) {
            throw new BadRequestException("Reviewer identity must be stored before approval");
        }

        if (profile.getReviewStatus() != KycReviewStatus.VERIFIED
                && profile.getReviewStatus() != KycReviewStatus.APPROVED) {
            throw new BadRequestException("Only verified KYC profile can be approved");
        }

        profile.setReviewStatus(KycReviewStatus.APPROVED);
        profile.setReviewedAt(LocalDateTime.now());
        updateCustomerStatus(profile.getCustomer(), CustomerStatus.ACTIVE);
        saveDecisionHistory(profile, KycDecision.APPROVE, username, profile.getRemarks());
        KycProfileResponse response = mapProfileResponse(kycProfileRepository.update(profile));
        sendKycDecisionMail(profile, "Approved", profile.getRemarks());
        return response;
    }

    @Override
    public KycProfileResponse rejectProfile(Long id, KycActionRequest request, String username) {
        KycProfile profile = getProfile(id);
        profile.setReviewStatus(KycReviewStatus.REJECTED);
        profile.setReviewedBy(username);
        profile.setReviewedAt(LocalDateTime.now());

        if (!isBlank(request != null ? request.getRemarks() : null)) {
            profile.setRemarks(request.getRemarks().trim());
        } else if (isBlank(profile.getRemarks())) {
            profile.setRemarks("Rejected from KYC review");
        }

        updateCustomerStatus(profile.getCustomer(), CustomerStatus.REJECTED);
        saveDecisionHistory(profile, KycDecision.REJECT, username, profile.getRemarks());
        KycProfileResponse response = mapProfileResponse(kycProfileRepository.update(profile));
        sendKycDecisionMail(profile, "Rejected", profile.getRemarks());
        return response;
    }

    @Override
    public KycProfileResponse returnProfile(Long id, KycActionRequest request, String username) {
        KycProfile profile = getProfile(id);
        String remarks = request == null ? null : request.getRemarks();

        if (isBlank(remarks)) {
            throw new BadRequestException("Correction remarks are required before returning KYC");
        }

        profile.setReviewStatus(KycReviewStatus.SENT_BACK);
        profile.setReviewedBy(username);
        profile.setReviewedAt(LocalDateTime.now());
        profile.setRemarks(remarks.trim());

        updateCustomerStatus(profile.getCustomer(), CustomerStatus.PENDING_KYC);
        saveDecisionHistory(profile, KycDecision.RETURN, username, profile.getRemarks());
        KycProfileResponse response = mapProfileResponse(kycProfileRepository.update(profile));
        sendKycDecisionMail(profile, "Returned for correction", profile.getRemarks());
        return response;
    }

    @Override
    public KycDocumentResponse uploadDocument(KycDocumentRequest request) {
        validateDocumentRequest(request);
        Customer customer = getCustomer(request.getCustomerId());

        CustomerDocument document = new CustomerDocument();
        document.setCustomer(customer);
        document.setDocumentType(request.getDocumentType());
        document.setFileReferenceId(trim(request.getFileReferenceId()));
        document.setDocumentNo(trim(request.getDocumentNo()));
        document.setIssueDate(request.getIssueDate());
        document.setExpiryDate(request.getExpiryDate());
        document.setVerifiedFlag(Boolean.TRUE.equals(request.getVerifiedFlag()));

        if (request.getStatus() != null) {
            document.setStatus(request.getStatus());
        }

        return mapDocumentResponse(customerDocumentRepository.save(document));
    }

    @Override
    public List<KycDocumentResponse> getDocumentsByProfile(Long profileId) {
        KycProfile profile = getProfile(profileId);
        return customerDocumentRepository.findByCustomerId(profile.getCustomer().getId())
                .stream()
                .map(this::mapDocumentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<KycDecisionHistoryResponse> getDecisionHistory(Long kycId) {
        getProfile(kycId);
        return kycDecisionHistoryRepository.findByKycProfileId(kycId)
                .stream()
                .map(this::mapHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public KycDashboardSummaryResponse dashboardSummary() {
        return new KycDashboardSummaryResponse(
                kycProfileRepository.countByReviewStatuses(List.of(
                        KycReviewStatus.DRAFT,
                        KycReviewStatus.SUBMITTED,
                        KycReviewStatus.UNDER_REVIEW
                )),
                kycProfileRepository.countByReviewStatuses(List.of(
                        KycReviewStatus.VERIFIED,
                        KycReviewStatus.APPROVED
                )),
                kycProfileRepository.countByReviewStatuses(List.of(KycReviewStatus.REJECTED)),
                kycProfileRepository.countByReviewStatuses(List.of(KycReviewStatus.SENT_BACK)),
                kycProfileRepository.countByRiskLevel(RiskLevel.HIGH),
                kycProfileRepository.countByRiskLevel(RiskLevel.LOW),
                kycProfileRepository.countByRiskLevel(RiskLevel.MEDIUM),
                kycProfileRepository.countByRiskLevel(RiskLevel.HIGH)
        );
    }

    private void validateProfileRequest(KycProfileRequest request) {
        if (request == null) {
            throw new BadRequestException("KYC profile request is required");
        }

        if (request.getCustomerId() == null) {
            throw new BadRequestException("Customer is required");
        }

        if (request.getRiskLevel() == null) {
            throw new BadRequestException("Risk level is required");
        }
    }

    private void validateDocumentRequest(KycDocumentRequest request) {
        if (request == null) {
            throw new BadRequestException("Document request is required");
        }

        if (request.getCustomerId() == null) {
            throw new BadRequestException("Customer is required");
        }

        if (request.getDocumentType() == null) {
            throw new BadRequestException("Document type is required");
        }

        if (request.getIssueDate() != null
                && request.getExpiryDate() != null
                && request.getExpiryDate().isBefore(request.getIssueDate())) {
            throw new BadRequestException("Expiry date cannot be before issue date");
        }
    }

    private void ensureDocumentExists(KycProfile profile) {
        Long documentCount = customerDocumentRepository.countActiveByCustomerId(profile.getCustomer().getId());

        if (documentCount == null || documentCount == 0) {
            throw new BadRequestException("KYC cannot proceed without required documents");
        }
    }

    private void updateCustomerStatus(Customer customer, CustomerStatus status) {
        customer.setCustomerStatus(status);
        customerRepository.update(customer);
    }

    private void saveDecisionHistory(KycProfile profile, KycDecision decision, String username, String remarks) {
        KycDecisionHistory history = new KycDecisionHistory();
        history.setKycProfile(profile);
        history.setDecision(decision);
        history.setDecisionBy(username);
        history.setRemarks(trim(remarks));
        kycDecisionHistoryRepository.save(history);
    }

    private KycProfile getProfile(Long id) {
        if (id == null) {
            throw new BadRequestException("KYC profile id is required");
        }

        return kycProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KYC profile not found"));
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private void mapProfileRequest(KycProfileRequest request, KycProfile profile) {
        profile.setRiskLevel(request.getRiskLevel());
        profile.setSourceOfFundsNote(trim(request.getSourceOfFundsNote()));
        profile.setPepFlag(Boolean.TRUE.equals(request.getPepFlag()));
        profile.setSanctionFlag(Boolean.TRUE.equals(request.getSanctionFlag()));
        profile.setAmlFlag(Boolean.TRUE.equals(request.getAmlFlag()));
        profile.setRemarks(trim(request.getRemarks()));

        if (request.getReviewStatus() != null) {
            profile.setReviewStatus(request.getReviewStatus());
        }

        if (request.getStatus() != null) {
            profile.setStatus(request.getStatus());
        }
    }

    private KycProfileResponse mapProfileResponse(KycProfile profile) {
        Long documentCount = customerDocumentRepository.countActiveByCustomerId(profile.getCustomer().getId());
        Long decisionCount = kycDecisionHistoryRepository.countActiveByKycProfileId(profile.getId());

        return new KycProfileResponse(
                profile.getId(),
                profile.getCustomer().getId(),
                profile.getCustomer().getCustomerCode(),
                profile.getCustomer().getFullName(),
                profile.getCustomer().getBranchId(),
                profile.getCustomer().getCustomerStatus(),
                profile.getRiskLevel(),
                profile.getSourceOfFundsNote(),
                profile.getPepFlag(),
                profile.getSanctionFlag(),
                profile.getAmlFlag(),
                profile.getReviewStatus(),
                profile.getReviewedBy(),
                profile.getReviewedAt(),
                profile.getRemarks(),
                profile.getStatus(),
                documentCount,
                decisionCount,
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    private KycDocumentResponse mapDocumentResponse(CustomerDocument document) {
        return new KycDocumentResponse(
                document.getId(),
                document.getCustomer().getId(),
                document.getCustomer().getCustomerCode(),
                document.getCustomer().getFullName(),
                document.getDocumentType(),
                document.getFileReferenceId(),
                document.getDocumentNo(),
                document.getIssueDate(),
                document.getExpiryDate(),
                document.getVerifiedFlag(),
                document.getStatus(),
                document.getCreatedAt()
        );
    }

    private KycDecisionHistoryResponse mapHistoryResponse(KycDecisionHistory history) {
        return new KycDecisionHistoryResponse(
                history.getId(),
                history.getKycProfile().getId(),
                history.getDecision(),
                history.getDecisionBy(),
                history.getDecisionAt(),
                history.getRemarks(),
                history.getStatus()
        );
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void sendKycDecisionMail(KycProfile profile, String decision, String remarks) {
        if (profile == null || profile.getCustomer() == null || isBlank(profile.getCustomer().getEmail())) {
            return;
        }
        automatedMailService.sendApprovalDecisionEmail(
                profile.getCustomer().getEmail(),
                "KYC Profile",
                profile.getCustomer().getCustomerCode(),
                decision,
                remarks,
                "/kyc/" + profile.getId(),
                "Open KYC"
        );
    }
}
