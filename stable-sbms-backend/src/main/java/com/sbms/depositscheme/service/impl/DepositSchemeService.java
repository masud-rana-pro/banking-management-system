package com.sbms.depositscheme.service.impl;

import com.sbms.account.entity.Account;
import com.sbms.account.enums.AccountStatus;
import com.sbms.account.repository.AccountRepository;
import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.common.document.PdfDocumentService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.CustomerStatus;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.customer.repository.CustomerRepository;
import com.sbms.depositscheme.dto.request.DepositSchemeEnrollmentRequest;
import com.sbms.depositscheme.dto.request.DepositSchemeRequest;
import com.sbms.depositscheme.dto.response.DepositSchemeDashboardSummaryResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeEnrollmentResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeProfitDistributionResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeScheduleResponse;
import com.sbms.depositscheme.entity.DepositScheme;
import com.sbms.depositscheme.entity.DepositSchemeEnrollment;
import com.sbms.depositscheme.entity.DepositSchemeProfitDistribution;
import com.sbms.depositscheme.entity.DepositSchemeSchedule;
import com.sbms.depositscheme.enums.DepositEnrollmentStatus;
import com.sbms.depositscheme.enums.DepositSchedulePaymentStatus;
import com.sbms.depositscheme.enums.ProfitDistributionStatus;
import com.sbms.depositscheme.repository.DepositSchemeEnrollmentRepository;
import com.sbms.depositscheme.repository.DepositSchemeProfitDistributionRepository;
import com.sbms.depositscheme.repository.DepositSchemeRepository;
import com.sbms.depositscheme.repository.DepositSchemeScheduleRepository;
import com.sbms.depositscheme.service.IDepositSchemeService;
import com.sbms.profit.enums.ProfitFrequency;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class DepositSchemeService implements IDepositSchemeService {

    private static final DateTimeFormatter CERTIFICATE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    @Autowired
    private DepositSchemeRepository schemeRepository;

    @Autowired
    private DepositSchemeEnrollmentRepository enrollmentRepository;

    @Autowired
    private DepositSchemeScheduleRepository scheduleRepository;

    @Autowired
    private DepositSchemeProfitDistributionRepository profitDistributionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Override
    public DepositSchemeResponse create(DepositSchemeRequest request) {
        validateSchemeRequest(request, null);

        DepositScheme entity = new DepositScheme();
        entity.setSchemeCode(resolveSchemeCode(request.getSchemeCode()));
        entity.setSchemeName(request.getSchemeName().trim());
        entity.setSchemeType(request.getSchemeType());
        entity.setTenureMonths(request.getTenureMonths());
        entity.setMinimumInstallment(scaleMoney(request.getMinimumInstallment()));
        entity.setProfitRatio(request.getProfitRatio().setScale(4, RoundingMode.HALF_UP));
        entity.setProfitFrequency(request.getProfitFrequency());
        entity.setStatus(RecordStatus.ACTIVE);

        return mapSchemeResponse(schemeRepository.save(entity));
    }

    @Override
    public List<DepositSchemeResponse> list() {
        return schemeRepository.findAll().stream().map(this::mapSchemeResponse).toList();
    }

    @Override
    public DepositSchemeResponse getById(Long id) {
        return mapSchemeResponse(getSchemeEntity(id));
    }

    @Override
    public DepositSchemeResponse update(Long id, DepositSchemeRequest request) {
        DepositScheme entity = getSchemeEntity(id);
        validateSchemeRequest(request, id);

        entity.setSchemeCode(resolveSchemeCodeForUpdate(entity, request.getSchemeCode()));
        entity.setSchemeName(request.getSchemeName().trim());
        entity.setSchemeType(request.getSchemeType());
        entity.setTenureMonths(request.getTenureMonths());
        entity.setMinimumInstallment(scaleMoney(request.getMinimumInstallment()));
        entity.setProfitRatio(request.getProfitRatio().setScale(4, RoundingMode.HALF_UP));
        entity.setProfitFrequency(request.getProfitFrequency());
        return mapSchemeResponse(schemeRepository.update(entity));
    }

    @Override
    public DepositSchemeResponse archive(Long id) {
        DepositScheme entity = getSchemeEntity(id);
        entity.setStatus(RecordStatus.ARCHIVED);
        return mapSchemeResponse(schemeRepository.update(entity));
    }

    @Override
    public DepositSchemeResponse restore(Long id) {
        DepositScheme entity = getSchemeEntity(id);
        schemeRepository.findBySchemeCode(entity.getSchemeCode())
                .filter(other -> !other.getId().equals(entity.getId()) && other.getStatus() != RecordStatus.ARCHIVED)
                .ifPresent(other -> {
                    throw new BadRequestException("Another active scheme already uses this scheme code");
                });
        entity.setStatus(RecordStatus.ACTIVE);
        return mapSchemeResponse(schemeRepository.update(entity));
    }

    @Override
    public DepositSchemeEnrollmentResponse createEnrollment(DepositSchemeEnrollmentRequest request) {
        if (request == null) {
            throw new BadRequestException("Enrollment request is required");
        }
        if (request.getSchemeId() == null) {
            throw new BadRequestException("Scheme is required");
        }
        if (request.getCustomerId() == null) {
            throw new BadRequestException("Customer is required");
        }
        if (request.getLinkedAccountId() == null) {
            throw new BadRequestException("Linked account is required");
        }
        if (request.getStartDate() == null) {
            throw new BadRequestException("Start date is required");
        }
        if (request.getInstallmentAmount() == null || request.getInstallmentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Installment amount must be greater than zero");
        }

        DepositScheme scheme = getSchemeEntity(request.getSchemeId());
        if (scheme.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived scheme cannot accept new enrollment");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (customer.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived customer cannot be enrolled");
        }
        if (customer.getCustomerStatus() == CustomerStatus.BLOCKED || customer.getCustomerStatus() == CustomerStatus.CLOSED) {
            throw new BadRequestException("Blocked or closed customer cannot be enrolled");
        }

        Account account = accountRepository.findById(request.getLinkedAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Linked account not found"));
        if (account.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived account cannot be linked");
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Only active accounts can be linked with deposit scheme");
        }
        if (!account.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("Selected account does not belong to the selected customer");
        }

        BigDecimal installmentAmount = scaleMoney(request.getInstallmentAmount());
        if (installmentAmount.compareTo(scaleMoney(scheme.getMinimumInstallment())) < 0) {
            throw new BadRequestException("Installment amount cannot be less than scheme minimum installment");
        }

        DepositSchemeEnrollment entity = new DepositSchemeEnrollment();
        entity.setEnrollmentNo(generateEnrollmentNo());
        entity.setScheme(scheme);
        entity.setCustomer(customer);
        entity.setLinkedAccount(account);
        entity.setStartDate(request.getStartDate());
        entity.setInstallmentAmount(installmentAmount);
        entity.setMaturityDate(request.getStartDate().plusMonths(scheme.getTenureMonths()));
        entity.setEnrollmentStatus(resolveEnrollmentStatus(entity.getMaturityDate()));
        entity.setRemarks(request.getRemarks());
        entity.setStatus(RecordStatus.ACTIVE);
        entity.setEarlyWithdrawalRequested(entity.getEnrollmentStatus() == DepositEnrollmentStatus.EARLY_WITHDRAWAL_REQUESTED);
        if (Boolean.TRUE.equals(entity.getEarlyWithdrawalRequested())) {
            entity.setEarlyWithdrawalRequestedAt(LocalDateTime.now());
        }

        DepositSchemeEnrollment saved = enrollmentRepository.save(entity);

        List<DepositSchemeSchedule> schedules = buildSchedules(saved);
        scheduleRepository.saveAll(schedules);

        List<DepositSchemeProfitDistribution> distributions = buildProfitDistributions(saved, schedules);
        profitDistributionRepository.saveAll(distributions);

        BigDecimal maturityAmount = schedules.stream()
                .map(DepositSchemeSchedule::getTotalDueAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        saved.setMaturityAmount(scaleMoney(maturityAmount));
        enrollmentRepository.update(saved);

        return mapEnrollmentResponse(saved);
    }

    @Override
    public List<DepositSchemeEnrollmentResponse> listEnrollments(Long schemeId, Long customerId, Long accountId) {
        return enrollmentRepository.findAll(schemeId, customerId, accountId).stream().map(this::mapEnrollmentResponse).toList();
    }

    @Override
    public DepositSchemeEnrollmentResponse getEnrollmentById(Long id) {
        return mapEnrollmentResponse(getEnrollmentEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewEnrollmentCertificate(Long id) {
        DepositSchemeEnrollmentResponse enrollment = getEnrollmentById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildEnrollmentCertificateHtml(enrollment));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("deposit-investment-certificate-" + enrollment.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadEnrollmentCertificate(Long id) {
        DepositSchemeEnrollmentResponse enrollment = getEnrollmentById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildEnrollmentCertificateHtml(enrollment));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("deposit-investment-certificate-" + enrollment.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    public List<DepositSchemeScheduleResponse> getEnrollmentSchedule(Long enrollmentId) {
        getEnrollmentEntity(enrollmentId);
        return scheduleRepository.findByEnrollmentId(enrollmentId).stream().map(this::mapScheduleResponse).toList();
    }

    @Override
    public List<DepositSchemeProfitDistributionResponse> getEnrollmentProfitDistribution(Long enrollmentId) {
        getEnrollmentEntity(enrollmentId);
        return profitDistributionRepository.findByEnrollmentId(enrollmentId).stream().map(this::mapProfitDistributionResponse).toList();
    }

    @Override
    public DepositSchemeDashboardSummaryResponse getDashboardSummary() {
        return new DepositSchemeDashboardSummaryResponse(
                schemeRepository.countTotalSchemes(),
                enrollmentRepository.countActiveEnrollments(),
                scheduleRepository.countDueInstallments(LocalDate.now()),
                enrollmentRepository.countByEnrollmentStatus(DepositEnrollmentStatus.EARLY_WITHDRAWAL_REQUESTED),
                enrollmentRepository.countByEnrollmentStatus(DepositEnrollmentStatus.MATURED),
                schemeRepository.findLatest(5).stream().map(this::mapSchemeResponse).toList(),
                enrollmentRepository.findLatest(5).stream().map(this::mapEnrollmentResponse).toList()
        );
    }

    private void validateSchemeRequest(DepositSchemeRequest request, Long existingId) {
        if (request == null) {
            throw new BadRequestException("Scheme request is required");
        }
        if (request.getSchemeName() == null || request.getSchemeName().trim().isEmpty()) {
            throw new BadRequestException("Scheme name is required");
        }
        if (request.getSchemeType() == null) {
            throw new BadRequestException("Scheme type is required");
        }
        if (request.getTenureMonths() == null || request.getTenureMonths() <= 0) {
            throw new BadRequestException("Tenure months must be greater than zero");
        }
        if (request.getMinimumInstallment() == null || request.getMinimumInstallment().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Minimum installment must be greater than zero");
        }
        if (request.getProfitRatio() == null || request.getProfitRatio().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Profit ratio must be zero or greater");
        }
        if (request.getProfitFrequency() == null) {
            throw new BadRequestException("Profit frequency is required");
        }
        if (request.getSchemeCode() != null && !request.getSchemeCode().trim().isEmpty()) {
            schemeRepository.findBySchemeCode(request.getSchemeCode().trim())
                    .filter(existing -> existingId == null || !existing.getId().equals(existingId))
                    .ifPresent(existing -> {
                        throw new BadRequestException("Scheme code already exists");
                    });
        }
    }

    private DepositScheme getSchemeEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Scheme id is required");
        }
        return schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deposit scheme not found"));
    }

    private DepositSchemeEnrollment getEnrollmentEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Enrollment id is required");
        }
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deposit scheme enrollment not found"));
    }

    private String resolveSchemeCode(String requestCode) {
        if (requestCode != null && !requestCode.trim().isEmpty()) {
            return requestCode.trim().toUpperCase();
        }
        String lastCode = schemeRepository.findLastSchemeCode();
        int next = 1;
        if (lastCode != null && lastCode.matches("DPS-\\d+")) {
            next = Integer.parseInt(lastCode.substring(4)) + 1;
        }
        return String.format("DPS-%05d", next);
    }

    private String resolveSchemeCodeForUpdate(DepositScheme existing, String requestCode) {
        if (requestCode == null || requestCode.trim().isEmpty()) {
            return existing.getSchemeCode();
        }
        return requestCode.trim().toUpperCase();
    }

    private String generateEnrollmentNo() {
        String lastNo = enrollmentRepository.findLastEnrollmentNo();
        int next = 1;
        if (lastNo != null && lastNo.matches("DSE-\\d+")) {
            next = Integer.parseInt(lastNo.substring(4)) + 1;
        }
        return String.format("DSE-%05d", next);
    }

    private DepositEnrollmentStatus resolveEnrollmentStatus(LocalDate maturityDate) {
        if (maturityDate != null && maturityDate.isBefore(LocalDate.now())) {
            return DepositEnrollmentStatus.MATURED;
        }
        return DepositEnrollmentStatus.ACTIVE;
    }

    private List<DepositSchemeSchedule> buildSchedules(DepositSchemeEnrollment enrollment) {
        List<DepositSchemeSchedule> result = new ArrayList<>();
        BigDecimal monthlyProfit = scaleMoney(
                enrollment.getInstallmentAmount()
                        .multiply(enrollment.getScheme().getProfitRatio())
                        .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(12), 8, RoundingMode.HALF_UP)
        );

        for (int i = 1; i <= enrollment.getScheme().getTenureMonths(); i++) {
            LocalDate dueDate = enrollment.getStartDate().plusMonths(i - 1);
            DepositSchemeSchedule schedule = new DepositSchemeSchedule();
            schedule.setEnrollment(enrollment);
            schedule.setInstallmentNo(i);
            schedule.setDueDate(dueDate);
            schedule.setInstallmentAmount(scaleMoney(enrollment.getInstallmentAmount()));
            schedule.setProfitAmount(scaleMoney(monthlyProfit));
            schedule.setTotalDueAmount(scaleMoney(enrollment.getInstallmentAmount().add(monthlyProfit)));
            schedule.setPaymentStatus(resolvePaymentStatus(dueDate, i));
            if (schedule.getPaymentStatus() == DepositSchedulePaymentStatus.PAID) {
                schedule.setPaidAt(dueDate.atStartOfDay());
            }
            schedule.setStatus(RecordStatus.ACTIVE);
            result.add(schedule);
        }
        return result;
    }

    private List<DepositSchemeProfitDistribution> buildProfitDistributions(DepositSchemeEnrollment enrollment,
                                                                           List<DepositSchemeSchedule> schedules) {
        List<DepositSchemeProfitDistribution> result = new ArrayList<>();
        int span = resolveMonthSpan(enrollment.getScheme().getProfitFrequency());
        int distributionNo = 1;

        for (int i = 0; i < schedules.size(); i += span) {
            List<DepositSchemeSchedule> bucket = schedules.subList(i, Math.min(i + span, schedules.size()));
            BigDecimal profitAmount = bucket.stream()
                    .map(DepositSchemeSchedule::getProfitAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            DepositSchemeProfitDistribution distribution = new DepositSchemeProfitDistribution();
            distribution.setEnrollment(enrollment);
            distribution.setDistributionNo(distributionNo++);
            distribution.setPeriodFrom(bucket.get(0).getDueDate());
            distribution.setPeriodTo(bucket.get(bucket.size() - 1).getDueDate());
            distribution.setDistributionDate(bucket.get(bucket.size() - 1).getDueDate());
            distribution.setProfitAmount(scaleMoney(profitAmount));
            distribution.setDistributionStatus(distribution.getDistributionDate().isBefore(LocalDate.now())
                    ? ProfitDistributionStatus.DISTRIBUTED
                    : ProfitDistributionStatus.PENDING);
            distribution.setCreditedAccountId(enrollment.getLinkedAccount().getId());
            distribution.setRemarks("Auto-generated from scheme enrollment");
            distribution.setStatus(RecordStatus.ACTIVE);
            result.add(distribution);
        }

        return result;
    }

    private int resolveMonthSpan(ProfitFrequency frequency) {
        return frequency == null ? 1 : Math.max(1, frequency.getMonthSpan());
    }

    private DepositSchedulePaymentStatus resolvePaymentStatus(LocalDate dueDate, int installmentNo) {
        if (dueDate.isBefore(LocalDate.now())) {
            return installmentNo % 3 == 0 ? DepositSchedulePaymentStatus.PAID : DepositSchedulePaymentStatus.OVERDUE;
        }
        return DepositSchedulePaymentStatus.PENDING;
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private DepositSchemeResponse mapSchemeResponse(DepositScheme entity) {
        return new DepositSchemeResponse(
                entity.getId(),
                entity.getSchemeCode(),
                entity.getSchemeName(),
                entity.getSchemeType().name(),
                entity.getTenureMonths(),
                entity.getMinimumInstallment(),
                entity.getProfitRatio(),
                entity.getProfitFrequency().name(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                enrollmentRepository.countActiveBySchemeId(entity.getId()),
                enrollmentRepository.countMaturedBySchemeId(entity.getId())
        );
    }

    private DepositSchemeEnrollmentResponse mapEnrollmentResponse(DepositSchemeEnrollment entity) {
        Long totalInstallments = scheduleRepository.countByEnrollmentId(entity.getId());
        Long paidInstallments = scheduleRepository.countPaidByEnrollmentId(entity.getId());
        Account account = entity.getLinkedAccount();
        Customer customer = entity.getCustomer();
        DepositScheme scheme = entity.getScheme();

        return new DepositSchemeEnrollmentResponse(
                entity.getId(),
                entity.getEnrollmentNo(),
                scheme.getId(),
                scheme.getSchemeCode(),
                scheme.getSchemeName(),
                scheme.getSchemeType().name(),
                customer.getId(),
                customer.getCustomerCode(),
                customer.getFullName(),
                account.getId(),
                account.getAccountNumber(),
                account.getBranchId(),
                entity.getStartDate(),
                entity.getInstallmentAmount(),
                entity.getMaturityDate(),
                entity.getEnrollmentStatus().name(),
                entity.getMaturityAmount(),
                entity.getEarlyWithdrawalRequested(),
                entity.getEarlyWithdrawalRequestedAt(),
                entity.getRemarks(),
                entity.getStatus(),
                scheme.getTenureMonths(),
                scheme.getProfitRatio(),
                scheme.getProfitFrequency().name(),
                totalInstallments,
                paidInstallments,
                Math.max(0, totalInstallments - paidInstallments),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private DepositSchemeScheduleResponse mapScheduleResponse(DepositSchemeSchedule entity) {
        return new DepositSchemeScheduleResponse(
                entity.getId(),
                entity.getEnrollment().getId(),
                entity.getEnrollment().getEnrollmentNo(),
                entity.getInstallmentNo(),
                entity.getDueDate(),
                entity.getInstallmentAmount(),
                entity.getProfitAmount(),
                entity.getTotalDueAmount(),
                entity.getPaymentStatus().name(),
                entity.getPaidAt(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private DepositSchemeProfitDistributionResponse mapProfitDistributionResponse(DepositSchemeProfitDistribution entity) {
        return new DepositSchemeProfitDistributionResponse(
                entity.getId(),
                entity.getEnrollment().getId(),
                entity.getEnrollment().getEnrollmentNo(),
                entity.getDistributionNo(),
                entity.getPeriodFrom(),
                entity.getPeriodTo(),
                entity.getDistributionDate(),
                entity.getProfitAmount(),
                entity.getDistributionStatus().name(),
                entity.getCreditedAccountId(),
                entity.getEnrollment().getLinkedAccount().getAccountNumber(),
                entity.getRemarks(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private String buildEnrollmentCertificateHtml(DepositSchemeEnrollmentResponse enrollment) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("item", enrollment);
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(
                ".doc-accent{background:linear-gradient(90deg,#8f6b12,#d8b44c,#0c6b4c);}" +
                ".certificate-shell{position:relative;}" +
                ".certificate-panel{margin:0 30px 24px;padding:30px 38px;border:1px solid #d8c07b;border-radius:24px;background:radial-gradient(circle at top,#fffef6,#fffdf7 48%,#f9f6ec 100%);box-shadow:inset 0 0 0 8px rgba(212,175,55,.10);}" +
                ".certificate-frame{border:2px solid #d4af37;border-radius:20px;padding:32px 38px;position:relative;}" +
                ".certificate-frame:before,.certificate-frame:after{content:'';position:absolute;width:120px;height:120px;border-color:#d4af37;border-style:solid;opacity:.7;}" +
                ".certificate-frame:before{top:12px;left:12px;border-width:2px 0 0 2px;border-top-left-radius:14px;}" +
                ".certificate-frame:after{right:12px;bottom:12px;border-width:0 2px 2px 0;border-bottom-right-radius:14px;}" +
                ".certificate-title{text-align:center;font-size:26px;font-weight:800;letter-spacing:.08em;color:#17422c;margin:18px 0 10px;text-transform:uppercase;}" +
                ".certificate-sub{text-align:center;font-size:14px;color:#54675c;margin-bottom:22px;}" +
                ".certificate-person{text-align:center;font-size:30px;font-weight:800;color:#0b5d44;margin:18px 0;}" +
                ".certificate-amount{text-align:center;font-size:22px;font-weight:800;color:#a67910;margin:16px 0;}" +
                ".certificate-body{text-align:center;font-size:15px;line-height:1.9;color:#283a30;max-width:760px;margin:0 auto 18px;}" +
                ".certificate-meta{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:14px;margin-top:26px;}" +
                ".certificate-meta .metric-card{background:#fffdf8;border-color:#eadab0;}" +
                ".seal-mark{width:96px;height:96px;border-radius:50%;margin:22px auto 16px;background:radial-gradient(circle,#f2d27f 0,#d4af37 55%,#9e7510 100%);display:flex;align-items:center;justify-content:center;color:#fff;font-size:34px;font-weight:800;box-shadow:0 10px 22px rgba(124,89,11,.25);}"
        ));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        model.put("issuedDate", LocalDate.now().format(CERTIFICATE_DATE_FORMATTER));
        return documentTemplateService.render("deposit-schemes/investment-certificate", model);
    }
}
