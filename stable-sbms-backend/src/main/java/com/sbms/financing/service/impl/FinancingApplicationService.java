package com.sbms.financing.service.impl;

import com.sbms.account.entity.Account;
import com.sbms.account.enums.AccountStatus;
import com.sbms.account.repository.AccountRepository;
import com.sbms.branch.entity.Branch;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.common.document.PdfDocumentService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.customer.repository.CustomerRepository;
import com.sbms.financing.dto.request.FinancingApplicationRequest;
import com.sbms.financing.dto.request.FinancingDisbursementRequest;
import com.sbms.financing.dto.request.FinancingRepaymentCollectionRequest;
import com.sbms.financing.dto.request.FinancingVerifyRequest;
import com.sbms.financing.dto.request.FinancingWorkflowActionRequest;
import com.sbms.financing.dto.response.*;
import com.sbms.financing.entity.*;
import com.sbms.financing.enums.FinancingApplicationStatus;
import com.sbms.financing.enums.FinancingScheduleStatus;
import com.sbms.financing.repository.*;
import com.sbms.financing.service.IFinancingApplicationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class FinancingApplicationService implements IFinancingApplicationService {

    private static final Pattern RATE_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");

    @Autowired
    private FinancingApplicationRepository applicationRepository;

    @Autowired
    private FinancingProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private FinancingAssetVerificationRepository assetVerificationRepository;

    @Autowired
    private FinancingDisbursementRepository disbursementRepository;

    @Autowired
    private FinancingScheduleRepository scheduleRepository;

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

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FinancingApplicationResponse create(FinancingApplicationRequest request) {
        FinancingApplication entity = new FinancingApplication();
        entity.setApplicationNo(generateApplicationNo());
        applyRequest(entity, request);
        entity.setApplicationStatus(FinancingApplicationStatus.DRAFT);
        entity.setStatus(RecordStatus.ACTIVE);
        return map(applicationRepository.save(entity));
    }

    @Override
    public List<FinancingApplicationResponse> list(Long productId, Long customerId, Long branchId, String keyword) {
        return applicationRepository.findAll(productId, customerId, branchId, keyword).stream().map(this::map).toList();
    }

    @Override
    public FinancingApplicationResponse getById(Long id) {
        return map(getApplication(id));
    }

    @Override
    public FinancingApplicationResponse update(Long id, FinancingApplicationRequest request) {
        FinancingApplication entity = getApplication(id);
        if (!(entity.getApplicationStatus() == FinancingApplicationStatus.DRAFT || entity.getApplicationStatus() == FinancingApplicationStatus.RETURNED)) {
            throw new BadRequestException("Only draft or returned applications can be edited");
        }
        applyRequest(entity, request);
        return map(applicationRepository.update(entity));
    }

    @Override
    public FinancingApplicationResponse submit(Long id, FinancingWorkflowActionRequest request) {
        FinancingApplication entity = getApplication(id);
        if (!(entity.getApplicationStatus() == FinancingApplicationStatus.DRAFT || entity.getApplicationStatus() == FinancingApplicationStatus.RETURNED)) {
            throw new BadRequestException("Only draft or returned applications can be submitted");
        }
        entity.setApplicationStatus(FinancingApplicationStatus.SUBMITTED);
        entity.setSubmittedAt(LocalDateTime.now());
        if (request != null && request.getRemarks() != null) entity.setRemarks(request.getRemarks().trim());
        return map(applicationRepository.update(entity));
    }

    @Override
    public FinancingApplicationResponse verify(Long id, FinancingVerifyRequest request) {
        FinancingApplication entity = getApplication(id);
        if (!(entity.getApplicationStatus() == FinancingApplicationStatus.SUBMITTED || entity.getApplicationStatus() == FinancingApplicationStatus.DOC_CHECK)) {
            throw new BadRequestException("Only submitted applications can be verified");
        }
        if (request == null || request.getAssetValue() == null || request.getAssetValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Asset value must be greater than zero");
        }
        if (request.getVerificationNote() == null || request.getVerificationNote().trim().isEmpty()) {
            throw new BadRequestException("Verification note is required");
        }
        if (request.getVerifiedBy() == null || request.getVerifiedBy().trim().isEmpty()) {
            throw new BadRequestException("Verified by is required");
        }

        FinancingAssetVerification verification = assetVerificationRepository.findByApplicationId(entity.getId()).orElseGet(FinancingAssetVerification::new);
        verification.setApplication(entity);
        verification.setAssetValue(scaleMoney(request.getAssetValue()));
        verification.setVerificationNote(request.getVerificationNote().trim());
        verification.setVerifiedBy(request.getVerifiedBy().trim());
        verification.setVerifiedAt(LocalDateTime.now());
        verification.setStatus(RecordStatus.ACTIVE);

        if (verification.getId() == null) assetVerificationRepository.save(verification);
        else assetVerificationRepository.update(verification);

        entity.setApplicationStatus(FinancingApplicationStatus.ASSET_VERIFIED);
        if (request.getRemarks() != null && !request.getRemarks().trim().isEmpty()) entity.setRemarks(request.getRemarks().trim());
        return map(applicationRepository.update(entity));
    }

    @Override
    public FinancingApplicationResponse review(Long id, FinancingWorkflowActionRequest request) {
        FinancingApplication entity = getApplication(id);
        if (entity.getApplicationStatus() != FinancingApplicationStatus.ASSET_VERIFIED) {
            throw new BadRequestException("Only asset verified applications can move to shariah review");
        }
        entity.setApplicationStatus(FinancingApplicationStatus.SHARIAH_REVIEW);
        if (request != null && request.getRemarks() != null && !request.getRemarks().trim().isEmpty()) {
            entity.setRemarks(request.getRemarks().trim());
        }
        return map(applicationRepository.update(entity));
    }

    @Override
    public FinancingApplicationResponse approve(Long id, FinancingWorkflowActionRequest request) {
        FinancingApplication entity = getApplication(id);
        if (entity.getApplicationStatus() != FinancingApplicationStatus.ASSET_VERIFIED && entity.getApplicationStatus() != FinancingApplicationStatus.SHARIAH_REVIEW) {
            throw new BadRequestException("Only verified applications can be approved");
        }
        entity.setApplicationStatus(FinancingApplicationStatus.APPROVED);
        entity.setApprovedBy(resolveActor(request, "SYSTEM"));
        entity.setApprovedAt(LocalDateTime.now());
        if (request != null && request.getRemarks() != null && !request.getRemarks().trim().isEmpty()) entity.setRemarks(request.getRemarks().trim());
        FinancingApplicationResponse response = map(applicationRepository.update(entity));
        sendDecisionMail(entity, "Approved", entity.getRemarks(), "/financing/applications/" + entity.getId(), "Open Financing");
        return response;
    }

    @Override
    public FinancingApplicationResponse reject(Long id, FinancingWorkflowActionRequest request) {
        FinancingApplication entity = getApplication(id);
        if (request == null || request.getRemarks() == null || request.getRemarks().trim().isEmpty()) {
            throw new BadRequestException("Reject remarks are required");
        }
        if (entity.getApplicationStatus() == FinancingApplicationStatus.DISBURSED || entity.getApplicationStatus() == FinancingApplicationStatus.ACTIVE || entity.getApplicationStatus() == FinancingApplicationStatus.CLOSED) {
            throw new BadRequestException("Disbursed or active applications cannot be rejected");
        }
        entity.setApplicationStatus(FinancingApplicationStatus.REJECTED);
        entity.setRemarks(request.getRemarks().trim());
        FinancingApplicationResponse response = map(applicationRepository.update(entity));
        sendDecisionMail(entity, "Rejected", entity.getRemarks(), "/financing/applications/" + entity.getId(), "Open Financing");
        return response;
    }

    @Override
    public FinancingApplicationResponse returnApplication(Long id, FinancingWorkflowActionRequest request) {
        FinancingApplication entity = getApplication(id);
        if (request == null || request.getRemarks() == null || request.getRemarks().trim().isEmpty()) {
            throw new BadRequestException("Correction remarks are required for return");
        }
        if (entity.getApplicationStatus() == FinancingApplicationStatus.DISBURSED || entity.getApplicationStatus() == FinancingApplicationStatus.ACTIVE || entity.getApplicationStatus() == FinancingApplicationStatus.CLOSED) {
            throw new BadRequestException("Disbursed or active applications cannot be returned");
        }
        entity.setApplicationStatus(FinancingApplicationStatus.RETURNED);
        entity.setRemarks(request.getRemarks().trim());
        FinancingApplicationResponse response = map(applicationRepository.update(entity));
        sendDecisionMail(entity, "Returned for correction", entity.getRemarks(), "/financing/applications/" + entity.getId(), "Open Financing");
        return response;
    }

    @Override
    public FinancingApplicationResponse archive(Long id) {
        FinancingApplication entity = getApplication(id);
        if (entity.getApplicationStatus() == FinancingApplicationStatus.DISBURSED
                || entity.getApplicationStatus() == FinancingApplicationStatus.ACTIVE
                || entity.getApplicationStatus() == FinancingApplicationStatus.CLOSED) {
            throw new BadRequestException("Disbursed or active applications cannot be archived");
        }
        entity.setStatus(RecordStatus.ARCHIVED);
        return map(applicationRepository.update(entity));
    }

    @Override
    public FinancingApplicationResponse restore(Long id) {
        FinancingApplication entity = getApplication(id);
        entity.setStatus(RecordStatus.ACTIVE);
        return map(applicationRepository.update(entity));
    }

    @Override
    public FinancingApplicationResponse disburse(Long id, FinancingDisbursementRequest request) {
        FinancingApplication entity = getApplication(id);
        if (entity.getApplicationStatus() != FinancingApplicationStatus.APPROVED) {
            throw new BadRequestException("Only approved applications can be disbursed");
        }
        if (request == null) throw new BadRequestException("Disbursement request is required");
        if (request.getDisbursementDate() == null) throw new BadRequestException("Disbursement date is required");
        if (request.getDisbursedAmount() == null || request.getDisbursedAmount().compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException("Disbursed amount must be greater than zero");
        if (request.getCreditedAccountId() == null) throw new BadRequestException("Credited account is required");
        if (request.getDisbursedBy() == null || request.getDisbursedBy().trim().isEmpty()) throw new BadRequestException("Disbursed by is required");

        Account account = accountRepository.findById(request.getCreditedAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Credited account not found"));
        if (!account.getCustomer().getId().equals(entity.getCustomer().getId())) {
            throw new BadRequestException("Credited account must belong to the same customer");
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Only active accounts can receive financing disbursement");
        }
        if (request.getDisbursedAmount().compareTo(entity.getRequestedAmount()) > 0) {
            throw new BadRequestException("Disbursed amount cannot exceed requested amount");
        }

        FinancingDisbursement disbursement = disbursementRepository.findByApplicationId(entity.getId()).orElseGet(FinancingDisbursement::new);
        disbursement.setApplication(entity);
        if (disbursement.getDisbursementNo() == null) disbursement.setDisbursementNo(generateDisbursementNo());
        disbursement.setDisbursementDate(request.getDisbursementDate());
        disbursement.setDisbursedAmount(scaleMoney(request.getDisbursedAmount()));
        disbursement.setCreditedAccountId(account.getId());
        disbursement.setDisbursedBy(request.getDisbursedBy().trim());
        disbursement.setStatus(RecordStatus.ACTIVE);
        if (disbursement.getId() == null) disbursementRepository.save(disbursement);
        else disbursementRepository.update(disbursement);

        List<FinancingSchedule> existingSchedules = scheduleRepository.findByApplicationId(entity.getId());
        if (existingSchedules.isEmpty()) {
            scheduleRepository.saveAll(buildSchedules(entity, disbursement.getDisbursedAmount(), request.getDisbursementDate()));
        }

        entity.setApplicationStatus(FinancingApplicationStatus.DISBURSED);
        if (request.getRemarks() != null && !request.getRemarks().trim().isEmpty()) entity.setRemarks(request.getRemarks().trim());
        FinancingApplicationResponse response = map(applicationRepository.update(entity));
        sendDecisionMail(entity, "Disbursed", entity.getRemarks(), "/financing/applications/" + entity.getId(), "Open Financing");
        return response;
    }

    @Override
    public List<FinancingScheduleResponse> getSchedule(Long id) {
        getApplication(id);
        return scheduleRepository.findByApplicationId(id).stream().map(this::mapSchedule).toList();
    }

    @Override
    public FinancingRepaymentCollectionResponse collectPayment(Long id, FinancingRepaymentCollectionRequest request) {
        FinancingApplication entity = getApplication(id);
        if (!(entity.getApplicationStatus() == FinancingApplicationStatus.DISBURSED || entity.getApplicationStatus() == FinancingApplicationStatus.ACTIVE)) {
            throw new BadRequestException("Only disbursed or active applications can collect repayment");
        }
        if (request == null || request.getPaymentAmount() == null || request.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero");
        }
        LocalDate paymentDate = request.getPaymentDate() == null ? LocalDate.now() : request.getPaymentDate();

        BigDecimal remaining = scaleMoney(request.getPaymentAmount());
        List<FinancingSchedule> schedules = scheduleRepository.findByApplicationId(id);
        List<FinancingScheduleResponse> touched = new ArrayList<>();

        for (FinancingSchedule schedule : schedules) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal scheduleDue = scaleMoney(schedule.getPrincipalAmount().add(schedule.getProfitAmount()).add(schedule.getCharityAmount()));
            BigDecimal alreadyPaid = scaleMoney(schedule.getPaidAmount());
            BigDecimal outstanding = scaleMoney(scheduleDue.subtract(alreadyPaid));
            if (outstanding.compareTo(BigDecimal.ZERO) <= 0) continue;

            if (paymentDate.isAfter(schedule.getDueDate()) && schedule.getCharityAmount().compareTo(BigDecimal.ZERO) == 0) {
                schedule.setCharityAmount(scaleMoney(schedule.getPrincipalAmount().multiply(new BigDecimal("0.01"))));
                scheduleDue = scaleMoney(schedule.getPrincipalAmount().add(schedule.getProfitAmount()).add(schedule.getCharityAmount()));
                outstanding = scaleMoney(scheduleDue.subtract(alreadyPaid));
            }

            BigDecimal paidNow = remaining.min(outstanding);
            schedule.setPaidAmount(scaleMoney(alreadyPaid.add(paidNow)));
            schedule.setPaidDate(paymentDate);

            BigDecimal finalDue = scaleMoney(schedule.getPrincipalAmount().add(schedule.getProfitAmount()).add(schedule.getCharityAmount()));
            if (schedule.getPaidAmount().compareTo(finalDue) >= 0) {
                schedule.setScheduleStatus(FinancingScheduleStatus.PAID);
            } else if (schedule.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                schedule.setScheduleStatus(FinancingScheduleStatus.PARTIAL);
            } else if (paymentDate.isAfter(schedule.getDueDate())) {
                schedule.setScheduleStatus(FinancingScheduleStatus.OVERDUE);
            }

            scheduleRepository.update(schedule);
            touched.add(mapSchedule(schedule));
            remaining = scaleMoney(remaining.subtract(paidNow));
        }

        BigDecimal outstandingTotal = calculateOutstanding(scheduleRepository.findByApplicationId(id));
        entity.setApplicationStatus(outstandingTotal.compareTo(BigDecimal.ZERO) == 0
                ? FinancingApplicationStatus.CLOSED
                : FinancingApplicationStatus.ACTIVE);
        if (request.getRemarks() != null && !request.getRemarks().trim().isEmpty()) entity.setRemarks(request.getRemarks().trim());
        applicationRepository.update(entity);

        return new FinancingRepaymentCollectionResponse(
                entity.getId(),
                entity.getApplicationNo(),
                scaleMoney(request.getPaymentAmount().subtract(remaining)),
                outstandingTotal,
                entity.getApplicationStatus().name(),
                touched
        );
    }

    @Override
    public FinancingDashboardSummaryResponse getDashboardSummary() {
        try {
            Long pendingApplications = toLong(entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM financing_application " +
                            "WHERE status <> 'ARCHIVED' AND application_status IN ('DRAFT','SUBMITTED','DOC_CHECK','ASSET_VERIFIED','SHARIAH_REVIEW','RETURNED')"
            ).getSingleResult());
            Long approvedApplications = toLong(entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM financing_application " +
                            "WHERE status <> 'ARCHIVED' AND application_status = 'APPROVED'"
            ).getSingleResult());
            BigDecimal disbursedAmount = scaleMoney(toBigDecimal(entityManager.createNativeQuery(
                    "SELECT COALESCE(SUM(d.disbursed_amount), 0) FROM financing_disbursement d WHERE d.status <> 'ARCHIVED'"
            ).getSingleResult()));
            Long overdueInstallments = toLong(entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM financing_schedule " +
                            "WHERE due_date < CURDATE() AND schedule_status IN ('PENDING','PARTIAL','OVERDUE')"
            ).getSingleResult());
            BigDecimal charityLateFeeAmount = scaleMoney(toBigDecimal(entityManager.createNativeQuery(
                    "SELECT COALESCE(SUM(charity_amount), 0) FROM financing_schedule"
            ).getSingleResult()));

            @SuppressWarnings("unchecked")
            List<Object[]> metricRows = entityManager.createNativeQuery(
                    "SELECT fp.product_name, COUNT(fa.id) " +
                            "FROM financing_application fa " +
                            "JOIN financing_product fp ON fp.id = fa.product_id " +
                            "WHERE fa.status <> 'ARCHIVED' " +
                            "GROUP BY fp.product_name ORDER BY COUNT(fa.id) DESC"
            ).getResultList();
            List<FinancingProductMetricResponse> metrics = metricRows.stream()
                    .map(row -> new FinancingProductMetricResponse(String.valueOf(row[0]), toLong(row[1])))
                    .toList();

            @SuppressWarnings("unchecked")
            List<Object[]> recentRows = entityManager.createNativeQuery(
                    "SELECT fa.id, fa.application_no, c.id AS customer_id, c.customer_code, c.full_name, " +
                            "fp.id AS product_id, fp.product_code, fp.product_name, fp.financing_type, fa.branch_id, " +
                            "fa.requested_amount, fa.asset_description, fa.purpose, fa.supporting_document_name, " +
                            "fa.application_status, fa.submitted_at, fa.approved_by, fa.approved_at, fa.remarks, " +
                            "fa.status, fa.created_at, fa.updated_at, " +
                            "COALESCE(SUM(fs.paid_amount), 0) AS total_paid_amount, " +
                            "COALESCE(SUM((fs.principal_amount + fs.profit_amount + fs.charity_amount) - fs.paid_amount), 0) AS total_outstanding_amount, " +
                            "COALESCE(SUM(fs.charity_amount), 0) AS total_charity_amount " +
                            "FROM financing_application fa " +
                            "JOIN customer c ON c.id = fa.customer_id " +
                            "JOIN financing_product fp ON fp.id = fa.product_id " +
                            "LEFT JOIN financing_schedule fs ON fs.application_id = fa.id " +
                            "WHERE fa.status <> 'ARCHIVED' " +
                            "GROUP BY fa.id, fa.application_no, c.id, c.customer_code, c.full_name, fp.id, fp.product_code, fp.product_name, " +
                            "fp.financing_type, fa.branch_id, fa.requested_amount, fa.asset_description, fa.purpose, fa.supporting_document_name, " +
                            "fa.application_status, fa.submitted_at, fa.approved_by, fa.approved_at, fa.remarks, fa.status, fa.created_at, fa.updated_at " +
                            "ORDER BY fa.created_at DESC LIMIT 6"
            ).getResultList();
            List<FinancingApplicationResponse> recentApplications = recentRows.stream()
                    .map(this::mapCurrentDashboardRow)
                    .toList();

            return new FinancingDashboardSummaryResponse(
                    pendingApplications,
                    approvedApplications,
                    disbursedAmount,
                    overdueInstallments,
                    metrics,
                    charityLateFeeAmount,
                    recentApplications
            );
        } catch (Exception ex) {
            Long pendingApplications = toLong(entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM financing_application WHERE status IN ('SUBMITTED','SHARIAH_REVIEW')"
            ).getSingleResult());
            Long approvedApplications = toLong(entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM financing_application WHERE status = 'APPROVED'"
            ).getSingleResult());
            BigDecimal disbursedAmount = scaleMoney(toBigDecimal(entityManager.createNativeQuery(
                    "SELECT COALESCE(SUM(COALESCE(approved_amount, requested_amount)), 0) FROM financing_application WHERE status = 'DISBURSED'"
            ).getSingleResult()));
            Long overdueInstallments = toLong(entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM financing_installment WHERE due_date < CURDATE() AND status IN ('DUE','LATE','SCHEDULED')"
            ).getSingleResult());
            BigDecimal charityLateFeeAmount = scaleMoney(toBigDecimal(entityManager.createNativeQuery(
                    "SELECT COALESCE(SUM(COALESCE(charity_amount, 0)), 0) FROM financing_installment"
            ).getSingleResult()));

            @SuppressWarnings("unchecked")
            List<Object[]> metricRows = entityManager.createNativeQuery(
                    "SELECT fp.product_name, COUNT(fa.id) " +
                            "FROM financing_application fa " +
                            "JOIN financing_product fp ON fp.id = fa.product_id " +
                            "GROUP BY fp.product_name ORDER BY COUNT(fa.id) DESC"
            ).getResultList();
            List<FinancingProductMetricResponse> metrics = metricRows.stream()
                    .map(row -> new FinancingProductMetricResponse(String.valueOf(row[0]), toLong(row[1])))
                    .toList();

            @SuppressWarnings("unchecked")
            List<Object[]> recentRows = entityManager.createNativeQuery(
                    "SELECT fa.id, fa.application_no, c.id AS customer_id, c.customer_code, c.full_name, " +
                            "fp.id AS product_id, fp.product_code, fp.product_name, fp.contract_type, fa.branch_id, " +
                            "fa.requested_amount, fa.purpose, fa.status AS application_status, fa.submitted_at, " +
                            "fa.approved_at, fa.remarks, fa.created_at, fa.updated_at, " +
                            "COALESCE(SUM(fi.paid_amount), 0) AS total_paid_amount, " +
                            "COALESCE(SUM(fi.installment_amount - COALESCE(fi.paid_amount, 0)), 0) AS total_outstanding_amount, " +
                            "COALESCE(SUM(fi.charity_amount), 0) AS total_charity_amount " +
                            "FROM financing_application fa " +
                            "JOIN customer c ON c.id = fa.customer_id " +
                            "JOIN financing_product fp ON fp.id = fa.product_id " +
                            "LEFT JOIN financing_installment fi ON fi.application_id = fa.id " +
                            "GROUP BY fa.id, fa.application_no, c.id, c.customer_code, c.full_name, fp.id, fp.product_code, fp.product_name, " +
                            "fp.contract_type, fa.branch_id, fa.requested_amount, fa.purpose, fa.status, fa.submitted_at, " +
                            "fa.approved_at, fa.remarks, fa.created_at, fa.updated_at " +
                            "ORDER BY fa.created_at DESC LIMIT 6"
            ).getResultList();

            List<FinancingApplicationResponse> recentApplications = recentRows.stream()
                    .map(this::mapLegacyDashboardRow)
                    .toList();

            return new FinancingDashboardSummaryResponse(
                    pendingApplications,
                    approvedApplications,
                    disbursedAmount,
                    overdueInstallments,
                    metrics,
                    charityLateFeeAmount,
                    recentApplications
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewSanctionLetter(Long id) {
        FinancingApplicationResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildSanctionLetterHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(response.getApplicationNo() + "-sanction-letter.pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadSanctionLetter(Long id) {
        FinancingApplicationResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildSanctionLetterHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(response.getApplicationNo() + "-sanction-letter.pdf").build().toString())
                .body(pdf);
    }

    private void applyRequest(FinancingApplication entity, FinancingApplicationRequest request) {
        if (request == null) throw new BadRequestException("Financing application request is required");
        if (request.getCustomerId() == null) throw new BadRequestException("Customer is required");
        if (request.getProductId() == null) throw new BadRequestException("Product is required");
        if (request.getBranchId() == null) throw new BadRequestException("Branch is required");
        if (request.getRequestedAmount() == null || request.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException("Requested amount must be greater than zero");
        if (request.getAssetDescription() == null || request.getAssetDescription().trim().isEmpty()) throw new BadRequestException("Asset description is required");
        if (request.getPurpose() == null || request.getPurpose().trim().isEmpty()) throw new BadRequestException("Purpose is required");

        Customer customer = customerRepository.findActiveById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        FinancingProduct product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Financing product not found"));
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        if (branch.getIsDeleted() != null && branch.getIsDeleted()) throw new BadRequestException("Deleted branch cannot be used");
        if (product.getStatus() == RecordStatus.ARCHIVED) throw new BadRequestException("Archived financing product cannot be used");

        BigDecimal requestedAmount = scaleMoney(request.getRequestedAmount());
        if (requestedAmount.compareTo(product.getMinimumAmount()) < 0 || requestedAmount.compareTo(product.getMaximumAmount()) > 0) {
            throw new BadRequestException("Requested amount must be within product minimum and maximum range");
        }

        entity.setCustomer(customer);
        entity.setProduct(product);
        entity.setBranchId(branch.getId());
        entity.setRequestedAmount(requestedAmount);
        entity.setAssetDescription(request.getAssetDescription().trim());
        entity.setPurpose(request.getPurpose().trim());
        entity.setSupportingDocumentName(request.getSupportingDocumentName() == null || request.getSupportingDocumentName().trim().isEmpty()
                ? null
                : request.getSupportingDocumentName().trim());
        entity.setRemarks(request.getRemarks() == null ? null : request.getRemarks().trim());
    }

    private FinancingApplication getApplication(Long id) {
        if (id == null) throw new BadRequestException("Application id is required");
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financing application not found"));
    }

    private String generateApplicationNo() {
        String last = applicationRepository.findLastApplicationNo();
        int next = 1;
        if (last != null && last.matches("FNA-\\d+")) next = Integer.parseInt(last.substring(4)) + 1;
        return String.format("FNA-%05d", next);
    }

    private String generateDisbursementNo() {
        String last = disbursementRepository.findLastDisbursementNo();
        int next = 1;
        if (last != null && last.matches("FND-\\d+")) next = Integer.parseInt(last.substring(4)) + 1;
        return String.format("FND-%05d", next);
    }

    private List<FinancingSchedule> buildSchedules(FinancingApplication entity, BigDecimal disbursedAmount, LocalDate disbursementDate) {
        List<FinancingSchedule> schedules = new ArrayList<>();
        int tenure = entity.getProduct().getTenureMonths();
        BigDecimal rate = extractProfitRate(entity.getProduct().getProfitRule());
        BigDecimal principalPerInstallment = scaleMoney(disbursedAmount.divide(BigDecimal.valueOf(tenure), 8, RoundingMode.HALF_UP));
        BigDecimal totalProfit = scaleMoney(disbursedAmount.multiply(rate).divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP));
        BigDecimal profitPerInstallment = scaleMoney(totalProfit.divide(BigDecimal.valueOf(tenure), 8, RoundingMode.HALF_UP));

        for (int i = 1; i <= tenure; i++) {
            FinancingSchedule schedule = new FinancingSchedule();
            schedule.setApplication(entity);
            schedule.setInstallmentNo(i);
            schedule.setDueDate(disbursementDate.plusMonths(i));
            schedule.setPrincipalAmount(principalPerInstallment);
            schedule.setProfitAmount(profitPerInstallment);
            schedule.setCharityAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            schedule.setPaidAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            schedule.setScheduleStatus(FinancingScheduleStatus.PENDING);
            schedules.add(schedule);
        }
        return schedules;
    }

    private BigDecimal extractProfitRate(String profitRule) {
        if (profitRule == null) return new BigDecimal("10.00");
        Matcher matcher = RATE_PATTERN.matcher(profitRule);
        if (matcher.find()) {
            return new BigDecimal(matcher.group(1)).setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal("10.00");
    }

    private BigDecimal calculateOutstanding(List<FinancingSchedule> schedules) {
        BigDecimal total = BigDecimal.ZERO;
        for (FinancingSchedule schedule : schedules) {
            BigDecimal due = scaleMoney(schedule.getPrincipalAmount().add(schedule.getProfitAmount()).add(schedule.getCharityAmount()));
            total = total.add(scaleMoney(due.subtract(schedule.getPaidAmount())));
        }
        return scaleMoney(total.max(BigDecimal.ZERO));
    }

    private String resolveActor(FinancingWorkflowActionRequest request, String fallback) {
        if (request == null || request.getPerformedBy() == null || request.getPerformedBy().trim().isEmpty()) return fallback;
        return request.getPerformedBy().trim();
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(String.valueOf(value));
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        return null;
    }

    private FinancingApplicationResponse mapLegacyDashboardRow(Object[] row) {
        return new FinancingApplicationResponse(
                toLong(row[0]),
                String.valueOf(row[1]),
                toLong(row[2]),
                String.valueOf(row[3]),
                String.valueOf(row[4]),
                toLong(row[5]),
                String.valueOf(row[6]),
                String.valueOf(row[7]),
                String.valueOf(row[8]),
                toLong(row[9]),
                scaleMoney(toBigDecimal(row[10])),
                String.valueOf(row[7]),
                String.valueOf(row[11]),
                null,
                String.valueOf(row[12]),
                toLocalDateTime(row[13]),
                null,
                toLocalDateTime(row[14]),
                row[15] == null ? null : String.valueOf(row[15]),
                RecordStatus.ACTIVE,
                toLocalDateTime(row[16]),
                toLocalDateTime(row[17]),
                null,
                null,
                List.of(),
                scaleMoney(toBigDecimal(row[18])),
                scaleMoney(toBigDecimal(row[19]).max(BigDecimal.ZERO)),
                scaleMoney(toBigDecimal(row[20]))
        );
    }

    private FinancingApplicationResponse mapCurrentDashboardRow(Object[] row) {
        return new FinancingApplicationResponse(
                toLong(row[0]),
                String.valueOf(row[1]),
                toLong(row[2]),
                String.valueOf(row[3]),
                String.valueOf(row[4]),
                toLong(row[5]),
                String.valueOf(row[6]),
                String.valueOf(row[7]),
                String.valueOf(row[8]),
                toLong(row[9]),
                scaleMoney(toBigDecimal(row[10])),
                row[11] == null ? null : String.valueOf(row[11]),
                row[12] == null ? null : String.valueOf(row[12]),
                row[13] == null ? null : String.valueOf(row[13]),
                String.valueOf(row[14]),
                toLocalDateTime(row[15]),
                row[16] == null ? null : String.valueOf(row[16]),
                toLocalDateTime(row[17]),
                row[18] == null ? null : String.valueOf(row[18]),
                parseRecordStatus(row[19]),
                toLocalDateTime(row[20]),
                toLocalDateTime(row[21]),
                null,
                null,
                List.of(),
                scaleMoney(toBigDecimal(row[22])),
                scaleMoney(toBigDecimal(row[23]).max(BigDecimal.ZERO)),
                scaleMoney(toBigDecimal(row[24]))
        );
    }

    private RecordStatus parseRecordStatus(Object value) {
        if (value == null) {
            return RecordStatus.ACTIVE;
        }
        try {
            return RecordStatus.valueOf(String.valueOf(value));
        } catch (IllegalArgumentException ex) {
            return RecordStatus.ACTIVE;
        }
    }

    private void sendDecisionMail(FinancingApplication entity, String decision, String remarks, String routePath, String ctaLabel) {
        if (entity == null || entity.getCustomer() == null) {
            return;
        }
        String email = entity.getCustomer().getEmail();
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        automatedMailService.sendApprovalDecisionEmail(
                email,
                "Financing Application",
                entity.getApplicationNo(),
                decision,
                remarks,
                routePath,
                ctaLabel
        );
    }

    private FinancingApplicationResponse map(FinancingApplication entity) {
        Optional<FinancingAssetVerification> verification = assetVerificationRepository.findByApplicationId(entity.getId());
        Optional<FinancingDisbursement> disbursement = disbursementRepository.findByApplicationId(entity.getId());
        List<FinancingSchedule> schedules = scheduleRepository.findByApplicationId(entity.getId());
        BigDecimal totalPaid = schedules.stream().map(FinancingSchedule::getPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCharity = schedules.stream().map(FinancingSchedule::getCharityAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstanding = calculateOutstanding(schedules);

        return new FinancingApplicationResponse(
                entity.getId(),
                entity.getApplicationNo(),
                entity.getCustomer().getId(),
                entity.getCustomer().getCustomerCode(),
                entity.getCustomer().getFullName(),
                entity.getProduct().getId(),
                entity.getProduct().getProductCode(),
                entity.getProduct().getProductName(),
                entity.getProduct().getFinancingType().name(),
                entity.getBranchId(),
                entity.getRequestedAmount(),
                entity.getAssetDescription(),
                entity.getPurpose(),
                entity.getSupportingDocumentName(),
                entity.getApplicationStatus().name(),
                entity.getSubmittedAt(),
                entity.getApprovedBy(),
                entity.getApprovedAt(),
                entity.getRemarks(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                verification.map(this::mapVerification).orElse(null),
                disbursement.map(this::mapDisbursement).orElse(null),
                schedules.stream().map(this::mapSchedule).toList(),
                scaleMoney(totalPaid),
                scaleMoney(outstanding),
                scaleMoney(totalCharity)
        );
    }

    private String buildSanctionLetterHtml(FinancingApplicationResponse response) {
        Branch branch = response.getBranchId() == null ? null : branchRepository.findById(response.getBranchId()).orElse(null);
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("response", response);
        model.put("branchName", branch == null ? "-" : branch.getBranchName());
        model.put("scheduleCount", response.getSchedules() == null ? 0 : response.getSchedules().size());
        model.put("disbursementNo", response.getDisbursement() == null ? "-" : response.getDisbursement().getDisbursementNo());
        model.put("disbursedAmount", response.getDisbursement() == null ? "0.00" : response.getDisbursement().getDisbursedAmount());
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(".doc-accent{background:linear-gradient(90deg,#0c5d44,#d4af37,#1f6d57);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        return documentTemplateService.render("financing/financing-sanction-letter", model);
    }

    private FinancingAssetVerificationResponse mapVerification(FinancingAssetVerification entity) {
        return new FinancingAssetVerificationResponse(
                entity.getId(),
                entity.getAssetValue(),
                entity.getVerificationNote(),
                entity.getVerifiedBy(),
                entity.getVerifiedAt(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private FinancingDisbursementResponse mapDisbursement(FinancingDisbursement entity) {
        String accountNumber = null;
        try {
            accountNumber = accountRepository.findById(entity.getCreditedAccountId()).map(Account::getAccountNumber).orElse(null);
        } catch (Exception ignored) {
        }
        return new FinancingDisbursementResponse(
                entity.getId(),
                entity.getDisbursementNo(),
                entity.getDisbursementDate(),
                entity.getDisbursedAmount(),
                entity.getCreditedAccountId(),
                accountNumber,
                entity.getDisbursedBy(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private FinancingScheduleResponse mapSchedule(FinancingSchedule entity) {
        return new FinancingScheduleResponse(
                entity.getId(),
                entity.getApplication().getId(),
                entity.getApplication().getApplicationNo(),
                entity.getInstallmentNo(),
                entity.getDueDate(),
                entity.getPrincipalAmount(),
                entity.getProfitAmount(),
                entity.getCharityAmount(),
                entity.getPaidAmount(),
                entity.getPaidDate(),
                entity.getScheduleStatus().name(),
                entity.getCreatedAt()
        );
    }
}
