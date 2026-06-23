package com.sbms.profit.service.impl;

import com.sbms.account.entity.Account;
import com.sbms.account.enums.AccountStatus;
import com.sbms.account.repository.AccountRepository;
import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.common.document.PdfDocumentService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.profit.dto.request.ProfitPostingRunRequest;
import com.sbms.profit.dto.response.*;
import com.sbms.profit.entity.BalanceSnapshot;
import com.sbms.profit.entity.ProfitPosting;
import com.sbms.profit.entity.ProfitRatio;
import com.sbms.profit.entity.ProfitSchedule;
import com.sbms.profit.enums.ProfitFrequency;
import com.sbms.profit.enums.ProfitPostingStatus;
import com.sbms.profit.repository.BalanceSnapshotRepository;
import com.sbms.profit.repository.ProfitPostingRepository;
import com.sbms.profit.repository.ProfitRatioRepository;
import com.sbms.profit.repository.ProfitScheduleRepository;
import com.sbms.profit.service.IProfitPostingService;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class ProfitPostingService implements IProfitPostingService {

    private static final String DEFAULT_POSTED_BY = "SYSTEM_PROFIT_ENGINE";
    private static final DateTimeFormatter ADVICE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    @Autowired
    private ProfitPostingRepository profitPostingRepository;

    @Autowired
    private ProfitScheduleRepository profitScheduleRepository;

    @Autowired
    private ProfitRatioRepository profitRatioRepository;

    @Autowired
    private BalanceSnapshotRepository balanceSnapshotRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Override
    public ProfitPostingRunResponse run(ProfitPostingRunRequest request, String username) {
        LocalDate postingDate = request != null && request.getPostingDate() != null
                ? request.getPostingDate()
                : LocalDate.now();
        String postedBy = resolvePostedBy(request == null ? null : request.getPostedBy(), username);

        List<ProfitSchedule> schedules = resolveSchedules(request, postingDate);
        if (schedules.isEmpty()) {
            throw new BadRequestException("No eligible profit schedule found for posting");
        }

        List<ProfitPostingResponse> responses = new ArrayList<>();
        int postedCount = 0;
        int failedCount = 0;

        for (ProfitSchedule schedule : schedules) {
            ProfitPostingResponse response = processSchedule(schedule, postingDate, postedBy);
            responses.add(response);
            if (ProfitPostingStatus.POSTED.name().equals(response.status())) {
                postedCount++;
            } else if (ProfitPostingStatus.FAILED.name().equals(response.status())) {
                failedCount++;
            }
        }

        return new ProfitPostingRunResponse(
                responses.size(),
                postedCount,
                failedCount,
                responses
        );
    }

    @Override
    public List<ProfitPostingResponse> list() {
        return profitPostingRepository.findAll().stream().map(this::mapResponse).toList();
    }

    @Override
    public ProfitPostingResponse getById(Long id) {
        if (id == null) {
            throw new BadRequestException("Profit posting id is required");
        }
        ProfitPosting entity = profitPostingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profit posting not found"));
        return mapResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewAdvice(Long id) {
        ProfitPostingResponse item = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildProfitAdviceHtml(item));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("profit-posting-advice-" + id + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadAdvice(Long id) {
        ProfitPostingResponse item = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildProfitAdviceHtml(item));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("profit-posting-advice-" + id + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    public ProfitDashboardSummaryResponse dashboardSummary() {
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());

        List<ProfitRatioResponse> currentRatios = profitRatioRepository.findAll().stream()
                .map(this::mapRatioResponse)
                .filter(ProfitRatioResponse::activeNow)
                .limit(6)
                .toList();

        LocalDate nextDate = profitScheduleRepository.findUpcomingDate().orElse(null);
        UpcomingPostingRunResponse upcoming = new UpcomingPostingRunResponse(
                nextDate,
                nextDate == null ? 0L : profitScheduleRepository.countDueSchedules(nextDate)
        );

        return new ProfitDashboardSummaryResponse(
                profitRatioRepository.countActive(today),
                profitScheduleRepository.countDueSchedules(today),
                profitPostingRepository.countPostedThisMonth(firstDay, lastDay),
                profitPostingRepository.countByStatus(ProfitPostingStatus.FAILED),
                currentRatios,
                upcoming,
                profitPostingRepository.findRecentFailed(5).stream().map(this::mapResponse).toList()
        );
    }

    private List<ProfitSchedule> resolveSchedules(ProfitPostingRunRequest request, LocalDate postingDate) {
        if (request != null && request.getScheduleId() != null) {
            ProfitSchedule schedule = profitScheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profit schedule not found"));
            validateRequestedSchedule(schedule, request.getAccountId(), postingDate);
            return List.of(schedule);
        }
        if (request != null && request.getAccountId() != null) {
            ProfitSchedule schedule = profitScheduleRepository.findByAccountId(request.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profit schedule not found for the selected account"));
            validateRequestedSchedule(schedule, request.getAccountId(), postingDate);
            return List.of(schedule);
        }
        return profitScheduleRepository.findDueSchedules(postingDate);
    }

    private void validateRequestedSchedule(ProfitSchedule schedule, Long accountId, LocalDate postingDate) {
        if (accountId != null && !schedule.getAccount().getId().equals(accountId)) {
            throw new BadRequestException("Selected schedule does not belong to the requested account");
        }
        if (schedule.getStatus() != RecordStatus.ACTIVE) {
            throw new BadRequestException("Only active schedules can be posted");
        }
        if (schedule.getNextPostingDate() != null && schedule.getNextPostingDate().isAfter(postingDate)) {
            throw new BadRequestException("Selected schedule is not due for posting yet");
        }
    }

    private ProfitPostingResponse processSchedule(ProfitSchedule schedule, LocalDate postingDate, String postedBy) {
        Account account = schedule.getAccount();
        ProfitFrequency frequency = schedule.getProfitFrequency();
        LocalDate periodTo = postingDate;
        LocalDate periodFrom = schedule.getLastPostingDate() == null
                ? postingDate.minusMonths(frequency.getMonthSpan()).plusDays(1)
                : schedule.getLastPostingDate().plusDays(1);

        BalanceSnapshot snapshot = createOrUpdateSnapshot(account, postingDate);

        if (schedule.getStatus() != RecordStatus.ACTIVE) {
            return createFailedPosting(schedule, postingDate, periodFrom, periodTo, postedBy, "Schedule is not active");
        }
        if (account.getStatus() != RecordStatus.ACTIVE || account.getAccountStatus() != AccountStatus.ACTIVE) {
            return createFailedPosting(schedule, postingDate, periodFrom, periodTo, postedBy, "Account is not active for profit posting");
        }
        if (!Boolean.TRUE.equals(account.getAccountType().getProfitApplicable())) {
            return createFailedPosting(schedule, postingDate, periodFrom, periodTo, postedBy, "Account type is not profit applicable");
        }
        if (schedule.getLastPostingDate() != null && !schedule.getLastPostingDate().isBefore(postingDate)) {
            return createFailedPosting(schedule, postingDate, periodFrom, periodTo, postedBy, "Posting date overlaps with the last posting cycle");
        }
        if (profitPostingRepository.findByScheduleAndPeriod(schedule.getId(), periodFrom, periodTo).isPresent()) {
            return createFailedPosting(schedule, postingDate, periodFrom, periodTo, postedBy, "Duplicate posting detected for the same schedule and period");
        }

        ProfitRatio ratio = profitRatioRepository.findActiveRatio(account.getAccountType().getId(), postingDate)
                .orElse(null);
        if (ratio == null) {
            return createFailedPosting(schedule, postingDate, periodFrom, periodTo, postedBy, "No active profit ratio found for this account type");
        }
        if (snapshot.getAverageBalance().compareTo(BigDecimal.ZERO) <= 0) {
            return createFailedPosting(schedule, postingDate, periodFrom, periodTo, postedBy, "Average balance is not eligible for profit calculation");
        }

        BigDecimal profitAmount = calculateProfit(snapshot.getAverageBalance(), ratio.getRatioPercent(), frequency);
        if (profitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return createFailedPosting(schedule, postingDate, periodFrom, periodTo, postedBy, "Calculated profit amount is zero");
        }

        account.setCurrentBalance(account.getCurrentBalance().add(profitAmount));
        account.setAvailableBalance(account.getAvailableBalance().add(profitAmount));
        account.setProfitRatioId(ratio.getId());
        accountRepository.update(account);

        schedule.setLastPostingDate(postingDate);
        schedule.setNextPostingDate(frequency.nextDate(postingDate));
        profitScheduleRepository.update(schedule);

        ProfitPosting posting = new ProfitPosting();
        posting.setPostingRef(nextPostingRef());
        posting.setAccount(account);
        posting.setSchedule(schedule);
        posting.setPostingDate(postingDate);
        posting.setProfitAmount(profitAmount);
        posting.setPeriodFrom(periodFrom);
        posting.setPeriodTo(periodTo);
        posting.setPostedBy(postedBy);
        posting.setStatus(ProfitPostingStatus.POSTED);
        profitPostingRepository.save(posting);

        return mapResponse(posting);
    }

    private ProfitPostingResponse createFailedPosting(
            ProfitSchedule schedule,
            LocalDate postingDate,
            LocalDate periodFrom,
            LocalDate periodTo,
            String postedBy,
            String failureReason
    ) {
        ProfitPosting posting = new ProfitPosting();
        posting.setPostingRef(nextPostingRef());
        posting.setAccount(schedule.getAccount());
        posting.setSchedule(schedule);
        posting.setPostingDate(postingDate);
        posting.setProfitAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        posting.setPeriodFrom(periodFrom);
        posting.setPeriodTo(periodTo);
        posting.setPostedBy(postedBy);
        posting.setStatus(ProfitPostingStatus.FAILED);
        posting.setFailureReason(failureReason);
        profitPostingRepository.save(posting);
        return mapResponse(posting);
    }

    private BalanceSnapshot createOrUpdateSnapshot(Account account, LocalDate postingDate) {
        BalanceSnapshot snapshot = balanceSnapshotRepository.findByAccountIdAndDate(account.getId(), postingDate)
                .orElseGet(BalanceSnapshot::new);
        snapshot.setAccount(account);
        snapshot.setSnapshotDate(postingDate);
        snapshot.setClosingBalance(account.getCurrentBalance());
        snapshot.setAverageBalance(account.getCurrentBalance());
        snapshot.setStatus(RecordStatus.ACTIVE);
        if (snapshot.getId() == null) {
            return balanceSnapshotRepository.save(snapshot);
        }
        return balanceSnapshotRepository.update(snapshot);
    }

    private BigDecimal calculateProfit(BigDecimal averageBalance, BigDecimal ratioPercent, ProfitFrequency frequency) {
        BigDecimal periodsPerYear = BigDecimal.valueOf(12L / frequency.getMonthSpan());
        return averageBalance
                .multiply(ratioPercent)
                .divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP)
                .divide(periodsPerYear, 2, RoundingMode.HALF_UP);
    }

    private ProfitPostingResponse mapResponse(ProfitPosting entity) {
        Account account = entity.getAccount();
        BalanceSnapshot snapshot = balanceSnapshotRepository.findByAccountIdAndDate(account.getId(), entity.getPostingDate())
                .orElse(null);
        ProfitRatio ratio = profitRatioRepository.findActiveRatio(account.getAccountType().getId(), entity.getPostingDate())
                .orElse(null);
        return new ProfitPostingResponse(
                entity.getId(),
                entity.getPostingRef(),
                account.getId(),
                account.getAccountNumber(),
                account.getCustomer().getId(),
                account.getCustomer().getCustomerCode(),
                account.getCustomer().getFullName(),
                account.getBranchId(),
                entity.getSchedule().getId(),
                entity.getSchedule().getProfitFrequency().name(),
                ratio == null ? null : ratio.getRatioCode(),
                entity.getPostingDate(),
                entity.getProfitAmount(),
                entity.getPeriodFrom(),
                entity.getPeriodTo(),
                snapshot == null ? null : snapshot.getClosingBalance(),
                snapshot == null ? null : snapshot.getAverageBalance(),
                entity.getPostedBy(),
                entity.getStatus().name(),
                entity.getFailureReason(),
                entity.getCreatedAt()
        );
    }

    private ProfitRatioResponse mapRatioResponse(ProfitRatio entity) {
        LocalDate today = LocalDate.now();
        boolean activeNow = entity.getStatus() == RecordStatus.ACTIVE
                && !entity.getEffectiveFrom().isAfter(today)
                && (entity.getEffectiveTo() == null || !entity.getEffectiveTo().isBefore(today));
        return new ProfitRatioResponse(
                entity.getId(),
                entity.getRatioCode(),
                entity.getAccountType().getId(),
                entity.getAccountType().getTypeCode(),
                entity.getAccountType().getTypeName(),
                entity.getEffectiveFrom(),
                entity.getEffectiveTo(),
                entity.getRatioPercent(),
                entity.getStatus(),
                activeNow,
                profitScheduleRepository.countByAccountTypeId(entity.getAccountType().getId()),
                profitPostingRepository.countByAccountTypeId(entity.getAccountType().getId()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String nextPostingRef() {
        String lastRef = profitPostingRepository.findLastPostingRef();
        int nextNumber = 1;
        if (lastRef != null && lastRef.startsWith("PRF-")) {
            nextNumber = Integer.parseInt(lastRef.substring(4)) + 1;
        }
        return String.format("PRF-%05d", nextNumber);
    }

    private String resolvePostedBy(String requestPostedBy, String username) {
        if (requestPostedBy != null && !requestPostedBy.trim().isEmpty()) {
            return requestPostedBy.trim();
        }
        if (username != null && !username.trim().isEmpty()) {
            return username.trim();
        }
        return DEFAULT_POSTED_BY;
    }

    private String buildProfitAdviceHtml(ProfitPostingResponse item) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("item", item);
        model.put("issuedDate", LocalDate.now().format(ADVICE_DATE_FORMATTER));
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(".doc-accent{background:linear-gradient(90deg,#0b5d44,#d4af37,#0d6f50);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        return documentTemplateService.render("profit/profit-posting-advice", model);
    }
}
