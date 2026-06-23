package com.sbms.sbms_backend;

import com.sbms.accounting.dto.request.PostFinancingIncomeJournalRequest;
import com.sbms.accounting.dto.request.PostManagementExpenseJournalRequest;
import com.sbms.accounting.dto.request.PostProfitPostingJournalRequest;
import com.sbms.accounting.dto.response.GlJournalResponse;
import com.sbms.accounting.dto.response.ProfitLossResponse;
import com.sbms.accounting.dto.response.TrialBalanceResponse;
import com.sbms.accounting.repository.GlJournalRepository;
import com.sbms.accounting.service.IAccountingCoreService;
import com.sbms.financing.entity.FinancingSchedule;
import com.sbms.financing.enums.FinancingScheduleStatus;
import com.sbms.financing.repository.FinancingScheduleRepository;
import com.sbms.profit.entity.ProfitPosting;
import com.sbms.profit.enums.ProfitPostingStatus;
import com.sbms.profit.repository.ProfitPostingRepository;
import com.sbms.report.entity.ManagementExpenseEntry;
import com.sbms.report.repository.ManagementExpenseEntryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;

@SpringBootTest
@Transactional
class AccountingCoreServiceIntegrationTest {

    @Autowired
    private IAccountingCoreService accountingCoreService;

    @Autowired
    private ManagementExpenseEntryRepository managementExpenseEntryRepository;

    @Autowired
    private GlJournalRepository glJournalRepository;

    @Autowired
    private ProfitPostingRepository profitPostingRepository;

    @Autowired
    private FinancingScheduleRepository financingScheduleRepository;

    @Test
    void postManagementExpenseJournal_createsBalancedDebitCreditLines() {
        ManagementExpenseEntry expense = new ManagementExpenseEntry();
        expense.setExpenseDate(LocalDate.of(2026, 5, 18));
        expense.setExpenseCategory("IT_SYSTEMS");
        expense.setExpenseCode("EXP-IT-001");
        expense.setAmount(new BigDecimal("12500.00"));
        expense.setReferenceNo("TEST-EXP-001");
        expense.setRemarks("Integration test expense");
        expense.setCreatedBy("test-runner");
        managementExpenseEntryRepository.save(expense);

        PostManagementExpenseJournalRequest request = new PostManagementExpenseJournalRequest();
        request.setSettlementAccountCode("1020");

        GlJournalResponse response = accountingCoreService.postManagementExpenseJournal(expense.getId(), request, "test-runner");

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getId());
        Assertions.assertEquals("MANAGEMENT_EXPENSE", response.getJournalType());
        Assertions.assertEquals("MANAGEMENT_EXPENSE", response.getSourceType());
        Assertions.assertEquals(expense.getId(), response.getSourceReferenceId());
        Assertions.assertEquals(2, response.getLines().size());
        Assertions.assertEquals("5020", response.getLines().get(0).getAccountCode());
        Assertions.assertEquals("DEBIT", response.getLines().get(0).getEntrySide());
        Assertions.assertEquals(new BigDecimal("12500.00"), response.getLines().get(0).getAmount());
        Assertions.assertEquals("1020", response.getLines().get(1).getAccountCode());
        Assertions.assertEquals("CREDIT", response.getLines().get(1).getEntrySide());
        Assertions.assertEquals(new BigDecimal("12500.00"), response.getLines().get(1).getAmount());
        Assertions.assertTrue(glJournalRepository.findBySource("MANAGEMENT_EXPENSE", expense.getId()).isPresent());
    }

    @Test
    void postProfitPostingJournal_createsBalancedDebitCreditLinesFromExistingPostedData() {
        ProfitPosting posting = profitPostingRepository.findAll().stream()
                .filter(item -> item.getStatus() == ProfitPostingStatus.POSTED)
                .filter(item -> item.getProfitAmount() != null && item.getProfitAmount().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(ProfitPosting::getId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No posted profit posting found in test database"));

        GlJournalResponse response;
        if (glJournalRepository.findBySource("PROFIT_POSTING", posting.getId()).isPresent()) {
            response = accountingCoreService.getJournals("PROFIT_POSTING", posting.getId(), null, null, null, null).stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Existing profit posting journal could not be read"));
        } else {
            PostProfitPostingJournalRequest request = new PostProfitPostingJournalRequest();
            request.setLiabilityAccountCode("2010");
            response = accountingCoreService.postProfitPostingJournal(posting.getId(), request, "test-runner");
        }

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getId());
        Assertions.assertEquals("PROFIT_POSTING", response.getJournalType());
        Assertions.assertEquals("PROFIT_POSTING", response.getSourceType());
        Assertions.assertEquals(posting.getId(), response.getSourceReferenceId());
        Assertions.assertEquals(2, response.getLines().size());
        Assertions.assertEquals("5010", response.getLines().get(0).getAccountCode());
        Assertions.assertEquals("DEBIT", response.getLines().get(0).getEntrySide());
        Assertions.assertEquals(posting.getProfitAmount(), response.getLines().get(0).getAmount());
        Assertions.assertEquals("2010", response.getLines().get(1).getAccountCode());
        Assertions.assertEquals("CREDIT", response.getLines().get(1).getEntrySide());
        Assertions.assertEquals(posting.getProfitAmount(), response.getLines().get(1).getAmount());
        Assertions.assertTrue(glJournalRepository.findBySource("PROFIT_POSTING", posting.getId()).isPresent());
    }

    @Test
    void getTrialBalance_returnsBalancedTotalsFromPostedJournals() {
        ManagementExpenseEntry expense = new ManagementExpenseEntry();
        expense.setExpenseDate(LocalDate.of(2026, 5, 18));
        expense.setExpenseCategory("BRANCH_ADMIN");
        expense.setExpenseCode("EXP-BR-001");
        expense.setAmount(new BigDecimal("5000.00"));
        expense.setReferenceNo("TEST-TB-EXP-001");
        expense.setRemarks("Trial balance expense");
        expense.setCreatedBy("test-runner");
        managementExpenseEntryRepository.save(expense);

        PostManagementExpenseJournalRequest request = new PostManagementExpenseJournalRequest();
        request.setSettlementAccountCode("1020");
        accountingCoreService.postManagementExpenseJournal(expense.getId(), request, "test-runner");

        TrialBalanceResponse response = accountingCoreService.getTrialBalance(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31),
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getRowCount() >= 2);
        Assertions.assertEquals(0, response.getTotalDebit().compareTo(response.getTotalCredit()));
        Assertions.assertTrue(response.getRows().stream()
                .anyMatch(row -> "5020".equals(row.getAccountCode()) && row.getTotalDebit().compareTo(new BigDecimal("5000.00")) >= 0));
        Assertions.assertTrue(response.getRows().stream()
                .anyMatch(row -> "1020".equals(row.getAccountCode()) && row.getTotalCredit().compareTo(new BigDecimal("5000.00")) >= 0));
    }

    @Test
    void postFinancingIncomeJournal_createsBalancedDebitCreditLinesFromExistingPaidSchedule() {
        FinancingSchedule schedule = financingScheduleRepository.findPaidWithProfit().stream()
                .sorted(Comparator.comparing(FinancingSchedule::getId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Paid financing schedule not found in test database"));

        GlJournalResponse response;
        if (glJournalRepository.findBySource("FINANCING_INCOME", schedule.getId()).isPresent()) {
            response = accountingCoreService.getJournals("FINANCING_INCOME", schedule.getId(), null, null, null, null).stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Existing financing income journal could not be read"));
        } else {
            PostFinancingIncomeJournalRequest request = new PostFinancingIncomeJournalRequest();
            request.setAssetAccountCode("1020");
            response = accountingCoreService.postFinancingIncomeJournal(schedule.getId(), request, "test-runner");
        }

        Assertions.assertNotNull(response);
        Assertions.assertEquals("FINANCING_INCOME", response.getJournalType());
        Assertions.assertEquals("FINANCING_INCOME", response.getSourceType());
        Assertions.assertEquals(schedule.getId(), response.getSourceReferenceId());
        Assertions.assertEquals(2, response.getLines().size());
        Assertions.assertEquals("1020", response.getLines().get(0).getAccountCode());
        Assertions.assertEquals("DEBIT", response.getLines().get(0).getEntrySide());
        Assertions.assertEquals(schedule.getProfitAmount(), response.getLines().get(0).getAmount());
        Assertions.assertEquals("4010", response.getLines().get(1).getAccountCode());
        Assertions.assertEquals("CREDIT", response.getLines().get(1).getEntrySide());
        Assertions.assertEquals(schedule.getProfitAmount(), response.getLines().get(1).getAmount());
        Assertions.assertTrue(glJournalRepository.findBySource("FINANCING_INCOME", schedule.getId()).isPresent());
    }

    @Test
    void getProfitLoss_returnsIncomeExpenseAndNetFromLedgerData() {
        FinancingSchedule schedule = financingScheduleRepository.findPaidWithProfit().stream()
                .sorted(Comparator.comparing(FinancingSchedule::getId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Paid financing schedule not found in test database"));
        if (glJournalRepository.findBySource("FINANCING_INCOME", schedule.getId()).isEmpty()) {
            accountingCoreService.postFinancingIncomeJournal(schedule.getId(), new PostFinancingIncomeJournalRequest(), "test-runner");
        }

        ManagementExpenseEntry expense = new ManagementExpenseEntry();
        expense.setExpenseDate(LocalDate.of(2026, 5, 18));
        expense.setExpenseCategory("OFFICE_SUPPLIES");
        expense.setExpenseCode("EXP-OFC-001");
        expense.setAmount(new BigDecimal("1000.00"));
        expense.setReferenceNo("TEST-PL-EXP-001");
        expense.setRemarks("Profit loss test expense");
        expense.setCreatedBy("test-runner");
        managementExpenseEntryRepository.save(expense);
        accountingCoreService.postManagementExpenseJournal(expense.getId(), new PostManagementExpenseJournalRequest(), "test-runner");

        ProfitLossResponse response = accountingCoreService.getProfitLoss(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 5, 31),
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getTotalIncome().compareTo(BigDecimal.ZERO) > 0);
        Assertions.assertTrue(response.getTotalExpense().compareTo(new BigDecimal("1000.00")) >= 0);
        Assertions.assertEquals(0, response.getNetProfit().compareTo(response.getTotalIncome().subtract(response.getTotalExpense())));
        Assertions.assertTrue(response.getIncomeRows().stream().anyMatch(row -> "4010".equals(row.getAccountCode())));
        Assertions.assertTrue(response.getExpenseRows().stream().anyMatch(row -> "5020".equals(row.getAccountCode())));
    }
}
