package com.sbms.account.service.impl;

import com.sbms.account.dto.request.AccountOpeningRequestDto;
import com.sbms.account.dto.request.AccountWorkflowActionRequest;
import com.sbms.account.dto.response.AccountOpeningRequestResponse;
import com.sbms.account.entity.Account;
import com.sbms.account.entity.AccountOpeningRequest;
import com.sbms.account.entity.AccountType;
import com.sbms.account.enums.AccountOpeningRequestStatus;
import com.sbms.account.enums.AccountStatus;
import com.sbms.account.repository.AccountOpeningRequestRepository;
import com.sbms.account.repository.AccountRepository;
import com.sbms.account.repository.AccountTypeRepository;
import com.sbms.account.service.IAccountOpeningRequestService;
import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.common.document.PdfDocumentService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AccountOpeningRequestService implements IAccountOpeningRequestService {

    @Autowired
    private AccountOpeningRequestRepository openingRequestRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AutomatedMailService automatedMailService;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Override
    public AccountOpeningRequestResponse create(AccountOpeningRequestDto request, String username) {
        validateRequest(request);
        Customer customer = getCustomer(request.getCustomerId());
        AccountType accountType = getAccountType(request.getAccountTypeId());

        AccountOpeningRequest entity = new AccountOpeningRequest();
        entity.setRequestNo(generateRequestNo());
        entity.setCustomer(customer);
        entity.setAccountType(accountType);
        mapRequest(request, entity, customer, accountType);
        return mapResponse(openingRequestRepository.save(entity));
    }

    @Override
    public List<AccountOpeningRequestResponse> list() {
        return openingRequestRepository.findAll().stream().map(this::mapResponse).toList();
    }

    @Override
    public AccountOpeningRequestResponse getById(Long id) {
        return mapResponse(getEntity(id));
    }

    @Override
    public AccountOpeningRequestResponse update(Long id, AccountOpeningRequestDto request, String username) {
        validateRequest(request);
        AccountOpeningRequest entity = getEntity(id);
        Customer customer = getCustomer(request.getCustomerId());
        AccountType accountType = getAccountType(request.getAccountTypeId());
        entity.setCustomer(customer);
        entity.setAccountType(accountType);
        mapRequest(request, entity, customer, accountType);
        return mapResponse(openingRequestRepository.update(entity));
    }

    @Override
    public AccountOpeningRequestResponse submit(Long id, String username) {
        AccountOpeningRequest entity = getEntity(id);
        validateForSubmission(entity);
        entity.setRequestStatus(AccountOpeningRequestStatus.SUBMITTED);
        return mapResponse(openingRequestRepository.update(entity));
    }

    @Override
    public AccountOpeningRequestResponse verify(Long id, String username) {
        AccountOpeningRequest entity = getEntity(id);
        validateForSubmission(entity);
        if (entity.getRequestStatus() != AccountOpeningRequestStatus.SUBMITTED
                && entity.getRequestStatus() != AccountOpeningRequestStatus.VERIFIED) {
            throw new BadRequestException("Only submitted request can be verified");
        }
        entity.setRequestStatus(AccountOpeningRequestStatus.VERIFIED);
        entity.setVerifiedBy(username);
        entity.setVerifiedAt(LocalDateTime.now());
        return mapResponse(openingRequestRepository.update(entity));
    }

    @Override
    public AccountOpeningRequestResponse approve(Long id, String username) {
        AccountOpeningRequest entity = getEntity(id);
        validateForSubmission(entity);
        if (entity.getVerifiedBy() == null || entity.getVerifiedBy().trim().isEmpty()) {
            throw new BadRequestException("Verifier must be recorded before approval");
        }
        if (entity.getRequestStatus() != AccountOpeningRequestStatus.VERIFIED
                && entity.getRequestStatus() != AccountOpeningRequestStatus.APPROVED) {
            throw new BadRequestException("Only verified request can be approved");
        }

        entity.setRequestStatus(AccountOpeningRequestStatus.APPROVED);
        entity.setApprovedBy(username);
        entity.setApprovedAt(LocalDateTime.now());
        openingRequestRepository.update(entity);

        if (accountRepository.findByOpeningRequestId(entity.getId()).isEmpty()) {
            Account account = new Account();
            account.setAccountNumber(generateAccountNumber());
            account.setAccountName(entity.getCustomer().getFullName() + " - " + entity.getAccountType().getTypeName());
            account.setCustomer(entity.getCustomer());
            account.setAccountType(entity.getAccountType());
            account.setBranchId(entity.getBranchId());
            account.setOpeningRequest(entity);
            account.setOpenedAt(LocalDateTime.now());
            account.setCurrentBalance(entity.getInitialDepositAmount());
            account.setAvailableBalance(entity.getInitialDepositAmount());
            account.setCurrencyCode(entity.getAccountType().getCurrencyCode());
            account.setAccountStatus(AccountStatus.PENDING);
            account.setRemarks(trim(entity.getRemarks()));
            accountRepository.save(account);
        }

        AccountOpeningRequestResponse response = mapResponse(entity);
        sendDecisionMail(entity, "Approved", entity.getRemarks(), "/accounts/opening-requests/" + entity.getId(), "Open Request");
        return response;
    }

    @Override
    public AccountOpeningRequestResponse reject(Long id, AccountWorkflowActionRequest request, String username) {
        AccountOpeningRequest entity = getEntity(id);
        entity.setRequestStatus(AccountOpeningRequestStatus.REJECTED);
        entity.setApprovedBy(username);
        entity.setApprovedAt(LocalDateTime.now());
        entity.setRemarks(trim(request == null ? null : request.getRemarks()) != null
                ? trim(request.getRemarks())
                : "Account opening request rejected");
        AccountOpeningRequestResponse response = mapResponse(openingRequestRepository.update(entity));
        sendDecisionMail(entity, "Rejected", entity.getRemarks(), "/accounts/opening-requests/" + entity.getId(), "Open Request");
        return response;
    }

    @Override
    public AccountOpeningRequestResponse returnForCorrection(Long id, AccountWorkflowActionRequest request, String username) {
        AccountOpeningRequest entity = getEntity(id);
        String remarks = trim(request == null ? null : request.getRemarks());
        if (remarks == null) {
            throw new BadRequestException("Correction remarks are required before return");
        }
        entity.setRequestStatus(AccountOpeningRequestStatus.SENT_BACK);
        entity.setRemarks(remarks);
        AccountOpeningRequestResponse response = mapResponse(openingRequestRepository.update(entity));
        sendDecisionMail(entity, "Returned for correction", entity.getRemarks(), "/accounts/opening-requests/" + entity.getId(), "Open Request");
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewOpeningForm(Long id) {
        AccountOpeningRequestResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildOpeningFormHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(response.requestNo() + "-opening-form.pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadOpeningForm(Long id) {
        AccountOpeningRequestResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildOpeningFormHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(response.requestNo() + "-opening-form.pdf").build().toString())
                .body(pdf);
    }

    private void validateRequest(AccountOpeningRequestDto request) {
        if (request == null) {
            throw new BadRequestException("Account opening request data is required");
        }
        if (request.getCustomerId() == null) {
            throw new BadRequestException("Customer is required");
        }
        if (request.getAccountTypeId() == null) {
            throw new BadRequestException("Account type is required");
        }
        if (request.getBranchId() == null || request.getBranchId() <= 0) {
            throw new BadRequestException("Branch is required");
        }
        if (request.getInitialDepositAmount() == null || request.getInitialDepositAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Initial deposit must be zero or positive");
        }
    }

    private void validateForSubmission(AccountOpeningRequest entity) {
        if (entity.getInitialDepositAmount() == null
                || entity.getInitialDepositAmount().compareTo(entity.getAccountType().getMinimumOpeningBalance()) < 0) {
            throw new BadRequestException("Initial deposit is below minimum opening balance");
        }
        if (entity.getCustomer() == null || entity.getCustomer().getId() == null) {
            throw new BadRequestException("Customer is required");
        }
    }

    private void mapRequest(AccountOpeningRequestDto request, AccountOpeningRequest entity, Customer customer, AccountType accountType) {
        entity.setBranchId(request.getBranchId());
        entity.setRequestedDate(request.getRequestedDate() == null ? LocalDate.now() : request.getRequestedDate());
        entity.setInitialDepositAmount(request.getInitialDepositAmount());
        entity.setRemarks(trim(request.getRemarks()));
        entity.setApplicantImageName(trim(request.getApplicantImageName()));
        entity.setStatus(request.getStatus() == null ? RecordStatus.ACTIVE : request.getStatus());
        entity.setRequestStatus(request.getRequestStatus() == null ? AccountOpeningRequestStatus.DRAFT : request.getRequestStatus());

        if (entity.getInitialDepositAmount().compareTo(accountType.getMinimumOpeningBalance()) < 0
                && entity.getRequestStatus() != AccountOpeningRequestStatus.DRAFT
                && entity.getRequestStatus() != AccountOpeningRequestStatus.SENT_BACK) {
            throw new BadRequestException("Initial deposit cannot be below minimum opening balance");
        }

        if (customer.getBranchId() != null && customer.getBranchId() > 0 && !customer.getBranchId().equals(request.getBranchId())) {
            entity.setRemarks(buildMergedRemarks(entity.getRemarks(), "Customer branch and request branch differ"));
        }
    }

    private AccountOpeningRequest getEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Opening request id is required");
        }
        return openingRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account opening request not found"));
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findActiveById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private AccountType getAccountType(Long accountTypeId) {
        AccountType accountType = accountTypeRepository.findById(accountTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Account type not found"));
        if (accountType.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived account type cannot be used");
        }
        return accountType;
    }

    private AccountOpeningRequestResponse mapResponse(AccountOpeningRequest entity) {
        Long accountId = accountRepository.findByOpeningRequestId(entity.getId())
                .map(Account::getId)
                .orElse(null);

        return new AccountOpeningRequestResponse(
                entity.getId(),
                entity.getRequestNo(),
                entity.getCustomer().getId(),
                entity.getCustomer().getCustomerCode(),
                entity.getCustomer().getFullName(),
                entity.getAccountType().getId(),
                entity.getAccountType().getTypeCode(),
                entity.getAccountType().getTypeName(),
                entity.getBranchId(),
                entity.getRequestedDate(),
                entity.getInitialDepositAmount(),
                entity.getRequestStatus(),
                entity.getVerifiedBy(),
                entity.getVerifiedAt(),
                entity.getApprovedBy(),
                entity.getApprovedAt(),
                entity.getRemarks(),
                entity.getApplicantImageName(),
                entity.getStatus(),
                entity.getCreatedAt(),
                accountId
        );
    }

    private String generateRequestNo() {
        String last = openingRequestRepository.findLastRequestNo();
        int next = 1;
        if (last != null && last.startsWith("AOR-")) {
            try {
                next = Integer.parseInt(last.substring(4)) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }
        return String.format("AOR-%04d", next);
    }

    private String generateAccountNumber() {
        String last = accountRepository.findLastAccountNumber();
        int next = 1;
        if (last != null && last.startsWith("ACC-")) {
            try {
                next = Integer.parseInt(last.substring(4)) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }
        return String.format("ACC-%06d", next);
    }

    private String buildMergedRemarks(String current, String extra) {
        if (trim(current) == null) {
            return extra;
        }
        if (current.contains(extra)) {
            return current;
        }
        return current + " | " + extra;
    }

    private String trim(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String buildOpeningFormHtml(AccountOpeningRequestResponse response) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("response", response);
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(".doc-accent{background:linear-gradient(90deg,#0c5d44,#d4af37,#1f6d57);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        return documentTemplateService.render("account/account-opening-form", model);
    }

    private void sendDecisionMail(AccountOpeningRequest entity, String decision, String remarks, String routePath, String ctaLabel) {
        if (entity == null || entity.getCustomer() == null || trim(entity.getCustomer().getEmail()) == null) {
            return;
        }
        automatedMailService.sendApprovalDecisionEmailAsync(
                entity.getCustomer().getEmail(),
                "Account Opening Request",
                entity.getRequestNo(),
                decision,
                remarks,
                routePath,
                ctaLabel
        );
    }
}
