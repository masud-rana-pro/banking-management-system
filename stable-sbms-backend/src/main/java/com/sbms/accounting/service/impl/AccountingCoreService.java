package com.sbms.accounting.service.impl;

import com.sbms.branch.entity.Branch;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.accounting.dto.request.GlAccountRequest;
import com.sbms.accounting.dto.request.PostFinancingIncomeJournalRequest;
import com.sbms.accounting.dto.request.PostManagementExpenseJournalRequest;
import com.sbms.accounting.dto.request.PostProfitPostingJournalRequest;
import com.sbms.accounting.dto.response.GlAccountResponse;
import com.sbms.accounting.dto.response.GlJournalLineResponse;
import com.sbms.accounting.dto.response.GlJournalResponse;
import com.sbms.accounting.dto.response.GlJournalSummaryResponse;
import com.sbms.accounting.dto.response.GlAccountSummaryResponse;
import com.sbms.accounting.dto.response.ProfitLossBranchSummaryResponse;
import com.sbms.accounting.dto.response.ProfitLossResponse;
import com.sbms.accounting.dto.response.ProfitLossRowResponse;
import com.sbms.accounting.dto.response.TrialBalanceResponse;
import com.sbms.accounting.dto.response.TrialBalanceRowResponse;
import com.sbms.accounting.entity.GlJournal;
import com.sbms.accounting.entity.GlJournalLine;
import com.sbms.accounting.entity.GlAccount;
import com.sbms.accounting.repository.GlAccountRepository;
import com.sbms.accounting.repository.GlJournalRepository;
import com.sbms.accounting.service.IAccountingCoreService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.financing.entity.FinancingSchedule;
import com.sbms.financing.enums.FinancingScheduleStatus;
import com.sbms.financing.repository.FinancingScheduleRepository;
import com.sbms.profit.entity.ProfitPosting;
import com.sbms.profit.enums.ProfitPostingStatus;
import com.sbms.profit.repository.ProfitPostingRepository;
import com.sbms.report.entity.ManagementExpenseEntry;
import com.sbms.report.repository.ManagementExpenseEntryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class AccountingCoreService implements IAccountingCoreService {

    @Autowired
    private GlAccountRepository glAccountRepository;

    @Autowired
    private GlJournalRepository glJournalRepository;

    @Autowired
    private ManagementExpenseEntryRepository managementExpenseEntryRepository;

    @Autowired
    private ProfitPostingRepository profitPostingRepository;

    @Autowired
    private FinancingScheduleRepository financingScheduleRepository;

    @Autowired
    private BranchRepository branchRepository;

    @PostConstruct
    public void init() {
        ensureSeeded();
    }

    @Override
    public GlAccountResponse createGlAccount(GlAccountRequest request) {
        ensureSeeded();
        validate(request);

        GlAccount entity = new GlAccount();
        entity.setAccountCode(request.getAccountCode().trim().toUpperCase(Locale.ROOT));
        entity.setAccountName(request.getAccountName().trim());
        entity.setAccountType(normalizeType(request.getAccountType()));
        entity.setParentAccountCode(trimToNullUpper(request.getParentAccountCode()));
        entity.setAllowPosting(request.getAllowPosting() == null ? Boolean.TRUE : request.getAllowPosting());
        entity.setBranchScoped(request.getBranchScoped() == null ? Boolean.FALSE : request.getBranchScoped());
        entity.setStatus(RecordStatus.ACTIVE);
        return map(glAccountRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GlAccountResponse> getGlAccounts(String accountType, Boolean allowPosting, String status, String keyword) {
        ensureSeeded();
        RecordStatus effectiveStatus = parseStatus(status);
        return glAccountRepository.findAll(accountType, allowPosting, effectiveStatus, keyword).stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GlAccountSummaryResponse getGlAccountSummary() {
        ensureSeeded();
        return new GlAccountSummaryResponse(
                glAccountRepository.countAll(),
                glAccountRepository.countByType("ASSET"),
                glAccountRepository.countByType("LIABILITY"),
                glAccountRepository.countByType("EQUITY"),
                glAccountRepository.countByType("INCOME"),
                glAccountRepository.countByType("EXPENSE")
        );
    }

    @Override
    public GlJournalResponse postManagementExpenseJournal(Long expenseEntryId, PostManagementExpenseJournalRequest request, String requestedBy) {
        ensureSeeded();
        if (expenseEntryId == null) {
            throw new BadRequestException("Expense entry id is required");
        }
        ManagementExpenseEntry expense = managementExpenseEntryRepository.findById(expenseEntryId)
                .orElseThrow(() -> new BadRequestException("Management expense entry not found"));
        if (glJournalRepository.findBySource("MANAGEMENT_EXPENSE", expenseEntryId).isPresent()) {
            throw new BadRequestException("This management expense entry is already posted to GL journal");
        }

        String settlementAccountCode = request == null || request.getSettlementAccountCode() == null || request.getSettlementAccountCode().trim().isEmpty()
                ? "1020"
                : request.getSettlementAccountCode().trim().toUpperCase(Locale.ROOT);

        GlAccount expenseAccount = glAccountRepository.findByCode("5020")
                .orElseThrow(() -> new BadRequestException("Operating expense GL account is not available"));
        GlAccount settlementAccount = glAccountRepository.findByCode(settlementAccountCode)
                .orElseThrow(() -> new BadRequestException("Settlement GL account is not available"));

        GlJournal journal = new GlJournal();
        journal.setJournalDate(expense.getExpenseDate() == null ? LocalDate.now() : expense.getExpenseDate());
        journal.setJournalType("MANAGEMENT_EXPENSE");
        journal.setSourceType("MANAGEMENT_EXPENSE");
        journal.setSourceReferenceId(expense.getId());
        journal.setSourceReferenceNo(expense.getReferenceNo());
        journal.setBranchId(expense.getBranchId());
        journal.setDescription("Management expense posting for " + expense.getExpenseCategory());
        journal.setCreatedBy(requestedBy == null || requestedBy.trim().isEmpty() ? "SYSTEM" : requestedBy.trim());
        glJournalRepository.saveJournal(journal);

        GlJournalLine debit = new GlJournalLine();
        debit.setJournalId(journal.getId());
        debit.setLineNo(1);
        debit.setAccountCode(expenseAccount.getAccountCode());
        debit.setEntrySide("DEBIT");
        debit.setAmount(expense.getAmount() == null ? BigDecimal.ZERO : expense.getAmount());
        debit.setRemarks("Expense recognition");
        glJournalRepository.saveLine(debit);

        GlJournalLine credit = new GlJournalLine();
        credit.setJournalId(journal.getId());
        credit.setLineNo(2);
        credit.setAccountCode(settlementAccount.getAccountCode());
        credit.setEntrySide("CREDIT");
        credit.setAmount(expense.getAmount() == null ? BigDecimal.ZERO : expense.getAmount());
        credit.setRemarks("Settlement placeholder");
        glJournalRepository.saveLine(credit);

        return mapJournal(journal);
    }

    @Override
    public GlJournalResponse postProfitPostingJournal(Long profitPostingId, PostProfitPostingJournalRequest request, String requestedBy) {
        ensureSeeded();
        if (profitPostingId == null) {
            throw new BadRequestException("Profit posting id is required");
        }
        ProfitPosting posting = profitPostingRepository.findById(profitPostingId)
                .orElseThrow(() -> new BadRequestException("Profit posting not found"));
        if (posting.getStatus() != ProfitPostingStatus.POSTED) {
            throw new BadRequestException("Only posted profit postings can be transferred to GL journal");
        }
        if (posting.getProfitAmount() == null || posting.getProfitAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Profit posting amount must be greater than zero");
        }
        if (glJournalRepository.findBySource("PROFIT_POSTING", profitPostingId).isPresent()) {
            throw new BadRequestException("This profit posting is already posted to GL journal");
        }

        String liabilityAccountCode = request == null || request.getLiabilityAccountCode() == null || request.getLiabilityAccountCode().trim().isEmpty()
                ? "2010"
                : request.getLiabilityAccountCode().trim().toUpperCase(Locale.ROOT);

        GlAccount expenseAccount = glAccountRepository.findByCode("5010")
                .orElseThrow(() -> new BadRequestException("Profit distribution expense GL account is not available"));
        GlAccount liabilityAccount = glAccountRepository.findByCode(liabilityAccountCode)
                .orElseThrow(() -> new BadRequestException("Liability GL account is not available"));

        GlJournal journal = new GlJournal();
        journal.setJournalDate(posting.getPostingDate() == null ? LocalDate.now() : posting.getPostingDate());
        journal.setJournalType("PROFIT_POSTING");
        journal.setSourceType("PROFIT_POSTING");
        journal.setSourceReferenceId(posting.getId());
        journal.setSourceReferenceNo(posting.getPostingRef());
        journal.setBranchId(posting.getAccount() != null
                ? posting.getAccount().getBranchId()
                : null);
        journal.setDescription("Profit distribution posting for " + posting.getPostingRef());
        journal.setCreatedBy(requestedBy == null || requestedBy.trim().isEmpty()
                ? (posting.getPostedBy() == null || posting.getPostedBy().trim().isEmpty() ? "SYSTEM" : posting.getPostedBy().trim())
                : requestedBy.trim());
        glJournalRepository.saveJournal(journal);

        GlJournalLine debit = new GlJournalLine();
        debit.setJournalId(journal.getId());
        debit.setLineNo(1);
        debit.setAccountCode(expenseAccount.getAccountCode());
        debit.setEntrySide("DEBIT");
        debit.setAmount(posting.getProfitAmount());
        debit.setRemarks("Profit distribution expense recognition");
        glJournalRepository.saveLine(debit);

        GlJournalLine credit = new GlJournalLine();
        credit.setJournalId(journal.getId());
        credit.setLineNo(2);
        credit.setAccountCode(liabilityAccount.getAccountCode());
        credit.setEntrySide("CREDIT");
        credit.setAmount(posting.getProfitAmount());
        credit.setRemarks("Customer deposit liability increase");
        glJournalRepository.saveLine(credit);

        return mapJournal(journal);
    }

    @Override
    public GlJournalResponse postFinancingIncomeJournal(Long financingScheduleId, PostFinancingIncomeJournalRequest request, String requestedBy) {
        ensureSeeded();
        if (financingScheduleId == null) {
            throw new BadRequestException("Financing schedule id is required");
        }
        FinancingSchedule schedule = financingScheduleRepository.findById(financingScheduleId)
                .orElseThrow(() -> new BadRequestException("Financing schedule not found"));
        if (schedule.getScheduleStatus() != FinancingScheduleStatus.PAID) {
            throw new BadRequestException("Only paid financing schedules can be transferred to GL journal");
        }
        if (schedule.getProfitAmount() == null || schedule.getProfitAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Financing schedule profit amount must be greater than zero");
        }
        if (glJournalRepository.findBySource("FINANCING_INCOME", financingScheduleId).isPresent()) {
            throw new BadRequestException("This financing schedule income is already posted to GL journal");
        }

        String assetAccountCode = request == null || request.getAssetAccountCode() == null || request.getAssetAccountCode().trim().isEmpty()
                ? "1020"
                : request.getAssetAccountCode().trim().toUpperCase(Locale.ROOT);

        GlAccount assetAccount = glAccountRepository.findByCode(assetAccountCode)
                .orElseThrow(() -> new BadRequestException("Asset GL account is not available"));
        GlAccount incomeAccount = glAccountRepository.findByCode("4010")
                .orElseThrow(() -> new BadRequestException("Financing income GL account is not available"));

        GlJournal journal = new GlJournal();
        journal.setJournalDate(schedule.getPaidDate() == null ? schedule.getDueDate() : schedule.getPaidDate());
        journal.setJournalType("FINANCING_INCOME");
        journal.setSourceType("FINANCING_INCOME");
        journal.setSourceReferenceId(schedule.getId());
        journal.setSourceReferenceNo(schedule.getApplication().getApplicationNo() + "-INS-" + schedule.getInstallmentNo());
        journal.setBranchId(schedule.getApplication().getBranchId());
        journal.setDescription("Realized financing income for schedule " + schedule.getInstallmentNo());
        journal.setCreatedBy(requestedBy == null || requestedBy.trim().isEmpty() ? "SYSTEM" : requestedBy.trim());
        glJournalRepository.saveJournal(journal);

        GlJournalLine debit = new GlJournalLine();
        debit.setJournalId(journal.getId());
        debit.setLineNo(1);
        debit.setAccountCode(assetAccount.getAccountCode());
        debit.setEntrySide("DEBIT");
        debit.setAmount(schedule.getProfitAmount());
        debit.setRemarks("Financing income realization asset side");
        glJournalRepository.saveLine(debit);

        GlJournalLine credit = new GlJournalLine();
        credit.setJournalId(journal.getId());
        credit.setLineNo(2);
        credit.setAccountCode(incomeAccount.getAccountCode());
        credit.setEntrySide("CREDIT");
        credit.setAmount(schedule.getProfitAmount());
        credit.setRemarks("Financing income recognition");
        glJournalRepository.saveLine(credit);

        return mapJournal(journal);
    }

    @Override
    public List<GlJournalResponse> getJournals(String sourceType, Long sourceReferenceId, String accountCode, LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        ensureSeeded();
        syncExistingLedgerSources();
        return glJournalRepository.findAll(sourceType, sourceReferenceId, accountCode, dateFrom, dateTo, branchId).stream()
                .map(this::mapJournal)
                .toList();
    }

    @Override
    public GlJournalSummaryResponse getJournalSummary() {
        ensureSeeded();
        syncExistingLedgerSources();
        return new GlJournalSummaryResponse(
                glJournalRepository.countAll(),
                glJournalRepository.countBySourceType("MANAGEMENT_EXPENSE"),
                glJournalRepository.countBySourceType("PROFIT_POSTING"),
                glJournalRepository.countBySourceType("FINANCING_INCOME"),
                glJournalRepository.countByStatus(RecordStatus.ACTIVE)
        );
    }

    @Override
    public TrialBalanceResponse getTrialBalance(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        ensureSeeded();
        syncExistingLedgerSources();
        List<TrialBalanceRowResponse> rows = glJournalRepository.fetchTrialBalance(dateFrom, dateTo, branchId);
        BigDecimal totalDebit = rows.stream()
                .map(TrialBalanceRowResponse::getTotalDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = rows.stream()
                .map(TrialBalanceRowResponse::getTotalCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TrialBalanceResponse(
                dateFrom,
                dateTo,
                branchId,
                totalDebit,
                totalCredit,
                rows.size(),
                rows
        );
    }

    @Override
    public ProfitLossResponse getProfitLoss(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        ensureSeeded();
        syncExistingLedgerSources();
        List<TrialBalanceRowResponse> rows = glJournalRepository.fetchTrialBalance(dateFrom, dateTo, branchId);
        List<ProfitLossRowResponse> incomeRows = rows.stream()
                .filter(row -> "INCOME".equalsIgnoreCase(row.getAccountType()))
                .map(row -> new ProfitLossRowResponse(
                        row.getAccountCode(),
                        row.getAccountName(),
                        row.getTotalCredit().subtract(row.getTotalDebit())
                ))
                .filter(row -> row.getAmount().compareTo(BigDecimal.ZERO) != 0)
                .toList();
        List<ProfitLossRowResponse> expenseRows = rows.stream()
                .filter(row -> "EXPENSE".equalsIgnoreCase(row.getAccountType()))
                .map(row -> new ProfitLossRowResponse(
                        row.getAccountCode(),
                        row.getAccountName(),
                        row.getTotalDebit().subtract(row.getTotalCredit())
                ))
                .filter(row -> row.getAmount().compareTo(BigDecimal.ZERO) != 0)
                .toList();

        BigDecimal totalIncome = incomeRows.stream()
                .map(ProfitLossRowResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenseRows.stream()
                .map(ProfitLossRowResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ProfitLossBranchSummaryResponse> branchSummaries = branchId == null
                ? buildBranchSummaries(dateFrom, dateTo)
                : List.of();

        return new ProfitLossResponse(
                dateFrom,
                dateTo,
                branchId,
                totalIncome,
                totalExpense,
                totalIncome.subtract(totalExpense),
                incomeRows,
                expenseRows,
                branchSummaries
        );
    }

    private List<ProfitLossBranchSummaryResponse> buildBranchSummaries(LocalDate dateFrom, LocalDate dateTo) {
        Map<Long, BranchAccumulator> branchMap = new LinkedHashMap<>();
        glJournalRepository.fetchProfitLossByBranch(dateFrom, dateTo).forEach(row -> {
            Long currentBranchId = row[0] == null ? 0L : ((Number) row[0]).longValue();
            String accountType = row[1] == null ? "" : String.valueOf(row[1]).trim().toUpperCase(Locale.ROOT);
            BigDecimal amount = row[2] instanceof BigDecimal decimal ? decimal : new BigDecimal(String.valueOf(row[2]));
            BranchAccumulator summary = branchMap.computeIfAbsent(currentBranchId, id -> buildAccumulator(id == 0L ? null : id));
            if ("INCOME".equals(accountType)) {
                summary.totalIncome = summary.totalIncome.add(amount.max(BigDecimal.ZERO));
            } else if ("EXPENSE".equals(accountType)) {
                summary.totalExpense = summary.totalExpense.add(amount.max(BigDecimal.ZERO));
            }
        });

        return branchMap.values().stream()
                .map(summary -> new ProfitLossBranchSummaryResponse(
                        summary.branchId,
                        summary.branchCode,
                        summary.branchName,
                        summary.totalIncome,
                        summary.totalExpense,
                        summary.totalIncome.subtract(summary.totalExpense)
                ))
                .sorted(Comparator.comparing(ProfitLossBranchSummaryResponse::getNetProfit).reversed())
                .toList();
    }

    private BranchAccumulator buildAccumulator(Long branchId) {
        if (branchId == null) {
            return new BranchAccumulator(null, "HO", "Head Office / Unassigned");
        }
        Branch branch = branchRepository.findById(branchId).orElse(null);
        return new BranchAccumulator(
                branchId,
                branch == null ? "BR-" + branchId : branch.getBranchCode(),
                branch == null ? "Branch " + branchId : branch.getBranchName()
        );
    }

    private void syncExistingLedgerSources() {
        managementExpenseEntryRepository.findAll(null, null, null, null, null).forEach(expense -> {
            if (expense.getId() != null && glJournalRepository.findBySource("MANAGEMENT_EXPENSE", expense.getId()).isEmpty()) {
                postManagementExpenseJournal(expense.getId(), null, "SYSTEM_BOOTSTRAP");
            }
        });

        profitPostingRepository.findAll().stream()
                .filter(posting -> posting.getId() != null)
                .filter(posting -> posting.getStatus() == ProfitPostingStatus.POSTED)
                .filter(posting -> posting.getProfitAmount() != null && posting.getProfitAmount().compareTo(BigDecimal.ZERO) > 0)
                .forEach(posting -> {
                    if (glJournalRepository.findBySource("PROFIT_POSTING", posting.getId()).isEmpty()) {
                        postProfitPostingJournal(posting.getId(), null, "SYSTEM_BOOTSTRAP");
                    }
                });

        financingScheduleRepository.findPaidWithProfit().forEach(schedule -> {
            if (schedule.getId() != null && glJournalRepository.findBySource("FINANCING_INCOME", schedule.getId()).isEmpty()) {
                postFinancingIncomeJournal(schedule.getId(), null, "SYSTEM_BOOTSTRAP");
            }
        });
    }

    private void ensureSeeded() {
        seedIfMissing("1010", "Cash In Hand", "ASSET", null, true, true);
        seedIfMissing("1020", "Bank Balance", "ASSET", null, true, false);
        seedIfMissing("1200", "Financing Receivable", "ASSET", null, true, true);
        seedIfMissing("2010", "Customer Deposit Liability", "LIABILITY", null, true, true);
        seedIfMissing("2020", "Profit Payable to Depositors", "LIABILITY", null, true, false);
        seedIfMissing("3010", "Retained Earnings", "EQUITY", null, true, false);
        seedIfMissing("4010", "Financing Income", "INCOME", null, true, true);
        seedIfMissing("4020", "Fee And Commission Income", "INCOME", null, true, true);
        seedIfMissing("5010", "Profit Distribution Expense", "EXPENSE", null, true, false);
        seedIfMissing("5020", "Operating Expense", "EXPENSE", null, true, true);
    }

    private void seedIfMissing(String code, String name, String type, String parentCode, boolean allowPosting, boolean branchScoped) {
        if (glAccountRepository.findByCode(code).isPresent()) {
            return;
        }
        GlAccount entity = new GlAccount();
        entity.setAccountCode(code);
        entity.setAccountName(name);
        entity.setAccountType(type);
        entity.setParentAccountCode(parentCode);
        entity.setAllowPosting(allowPosting);
        entity.setBranchScoped(branchScoped);
        entity.setStatus(RecordStatus.ACTIVE);
        glAccountRepository.save(entity);
    }

    private void validate(GlAccountRequest request) {
        if (request == null) {
            throw new BadRequestException("GL account request is required");
        }
        if (request.getAccountCode() == null || request.getAccountCode().trim().isEmpty()) {
            throw new BadRequestException("Account code is required");
        }
        if (request.getAccountName() == null || request.getAccountName().trim().isEmpty()) {
            throw new BadRequestException("Account name is required");
        }
        normalizeType(request.getAccountType());
        if (glAccountRepository.existsByCode(request.getAccountCode(), null)) {
            throw new BadRequestException("GL account code already exists");
        }
    }

    private String normalizeType(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        if (!List.of("ASSET", "LIABILITY", "EQUITY", "INCOME", "EXPENSE").contains(normalized)) {
            throw new BadRequestException("Account type must be ASSET, LIABILITY, EQUITY, INCOME or EXPENSE");
        }
        return normalized;
    }

    private RecordStatus parseStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return RecordStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid status filter");
        }
    }

    private String trimToNullUpper(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private GlAccountResponse map(GlAccount entity) {
        return new GlAccountResponse(
                entity.getId(),
                entity.getAccountCode(),
                entity.getAccountName(),
                entity.getAccountType(),
                entity.getParentAccountCode(),
                entity.getAllowPosting(),
                entity.getBranchScoped(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private GlJournalResponse mapJournal(GlJournal entity) {
        List<GlJournalLineResponse> lines = glJournalRepository.findLines(entity.getId()).stream()
                .map(line -> new GlJournalLineResponse(
                        line.getId(),
                        line.getLineNo(),
                        line.getAccountCode(),
                        line.getEntrySide(),
                        line.getAmount(),
                        line.getRemarks()
                ))
                .toList();

        return new GlJournalResponse(
                entity.getId(),
                entity.getJournalDate(),
                entity.getJournalType(),
                entity.getSourceType(),
                entity.getSourceReferenceId(),
                entity.getSourceReferenceNo(),
                entity.getBranchId(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                lines
        );
    }

    private static final class BranchAccumulator {
        private final Long branchId;
        private final String branchCode;
        private final String branchName;
        private BigDecimal totalIncome = BigDecimal.ZERO;
        private BigDecimal totalExpense = BigDecimal.ZERO;

        private BranchAccumulator(Long branchId, String branchCode, String branchName) {
            this.branchId = branchId;
            this.branchCode = branchCode;
            this.branchName = branchName;
        }
    }
}
