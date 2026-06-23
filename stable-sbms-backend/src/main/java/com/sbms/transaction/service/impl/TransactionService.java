package com.sbms.transaction.service.impl;

import com.sbms.account.entity.Account;
import com.sbms.account.enums.AccountStatus;
import com.sbms.account.repository.AccountRepository;
import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.common.document.PdfDocumentService;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.transaction.dto.request.*;
import com.sbms.transaction.dto.response.BranchTransactionSummaryResponse;
import com.sbms.transaction.dto.response.StandingInstructionResponse;
import com.sbms.transaction.dto.response.TransactionDashboardSummaryResponse;
import com.sbms.transaction.dto.response.TransactionResponse;
import com.sbms.transaction.entity.*;
import com.sbms.transaction.enums.*;
import com.sbms.transaction.repository.*;
import com.sbms.transaction.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@Service
@Transactional
public class TransactionService implements ITransactionService {

    private static final DateTimeFormatter MAIL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a", Locale.ENGLISH);
    private static final BigDecimal DEFAULT_TELLER_LIMIT = new BigDecimal("200000.00");
    private static final BigDecimal SUSPICIOUS_THRESHOLD = new BigDecimal("100000.00");
    private static final String DEFAULT_POSTED_BY = "SYSTEM_TELLER";
    private static final String DEFAULT_APPROVED_BY = "SYSTEM_SUPERVISOR";

    @Autowired
    private TransactionJournalRepository transactionJournalRepository;

    @Autowired
    private CashTransactionRepository cashTransactionRepository;

    @Autowired
    private FundTransferRepository fundTransferRepository;

    @Autowired
    private TransactionReversalRepository transactionReversalRepository;

    @Autowired
    private ChequeClearingRepository chequeClearingRepository;

    @Autowired
    private StandingInstructionRepository standingInstructionRepository;

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
    public TransactionResponse deposit(DepositRequest request, String username) {
        validateCashRequest(request == null ? null : request.getBranchId(), request == null ? null : request.getAmount());
        Account creditAccount = getTransactionalAccount(request.getCreditAccountId(), request.getBranchId(), "Credit account");
        enforceTellerLimit(request.getTellerUserId(), CashDirection.IN, request.getAmount());

        creditAccount.setCurrentBalance(creditAccount.getCurrentBalance().add(request.getAmount()));
        creditAccount.setAvailableBalance(creditAccount.getAvailableBalance().add(request.getAmount()));
        accountRepository.update(creditAccount);

        TransactionJournal journal = createJournal(
                TransactionType.DEPOSIT,
                ChannelType.BRANCH_COUNTER,
                request.getBranchId(),
                request.getTerminalId(),
                null,
                creditAccount,
                request.getAmount(),
                trim(request.getNarration()),
                request.getTransactionDate(),
                username
        );

        CashTransaction cashTransaction = new CashTransaction();
        cashTransaction.setTransaction(journal);
        cashTransaction.setCashType(CashType.CASH);
        cashTransaction.setCashDirection(CashDirection.IN);
        cashTransaction.setTellerUserId(resolveTellerUserId(request.getTellerUserId()));
        cashTransaction.setBranchId(request.getBranchId());
        cashTransaction.setAmount(request.getAmount());
        cashTransaction.setRemarks(trim(request.getNarration()));
        cashTransactionRepository.save(cashTransaction);

        TransactionResponse response = mapResponse(journal);
        sendTransactionConfirmation(creditAccount.getCustomer(), response, creditAccount.getAccountNumber());
        return response;
    }

    @Override
    public TransactionResponse withdraw(WithdrawalRequest request, String username) {
        validateCashRequest(request == null ? null : request.getBranchId(), request == null ? null : request.getAmount());
        Account debitAccount = getTransactionalAccount(request.getDebitAccountId(), request.getBranchId(), "Debit account");
        enforceTellerLimit(request.getTellerUserId(), CashDirection.OUT, request.getAmount());
        ensureEnoughBalance(debitAccount, request.getAmount());

        debitAccount.setCurrentBalance(debitAccount.getCurrentBalance().subtract(request.getAmount()));
        debitAccount.setAvailableBalance(debitAccount.getAvailableBalance().subtract(request.getAmount()));
        accountRepository.update(debitAccount);

        TransactionJournal journal = createJournal(
                TransactionType.WITHDRAWAL,
                ChannelType.BRANCH_COUNTER,
                request.getBranchId(),
                request.getTerminalId(),
                debitAccount,
                null,
                request.getAmount(),
                trim(request.getNarration()),
                request.getTransactionDate(),
                username
        );

        CashTransaction cashTransaction = new CashTransaction();
        cashTransaction.setTransaction(journal);
        cashTransaction.setCashType(CashType.CASH);
        cashTransaction.setCashDirection(CashDirection.OUT);
        cashTransaction.setTellerUserId(resolveTellerUserId(request.getTellerUserId()));
        cashTransaction.setBranchId(request.getBranchId());
        cashTransaction.setAmount(request.getAmount());
        cashTransaction.setRemarks(trim(request.getNarration()));
        cashTransactionRepository.save(cashTransaction);

        TransactionResponse response = mapResponse(journal);
        sendTransactionConfirmation(debitAccount.getCustomer(), response, debitAccount.getAccountNumber());
        return response;
    }

    @Override
    public TransactionResponse transfer(FundTransferRequest request, String username) {
        if (request == null) {
            throw new BadRequestException("Transfer request is required");
        }
        validateBranchAndAmount(request.getBranchId(), request.getAmount());
        if (request.getFromAccountId() == null || request.getToAccountId() == null) {
            throw new BadRequestException("Both source and destination accounts are required");
        }
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BadRequestException("Transfer cannot be posted to the same account");
        }

        Account fromAccount = getTransactionalAccount(request.getFromAccountId(), request.getBranchId(), "From account");
        Account toAccount = getTransactionalAccount(request.getToAccountId(), request.getBranchId(), "To account");
        ensureEnoughBalance(fromAccount, request.getAmount());

        fromAccount.setCurrentBalance(fromAccount.getCurrentBalance().subtract(request.getAmount()));
        fromAccount.setAvailableBalance(fromAccount.getAvailableBalance().subtract(request.getAmount()));
        toAccount.setCurrentBalance(toAccount.getCurrentBalance().add(request.getAmount()));
        toAccount.setAvailableBalance(toAccount.getAvailableBalance().add(request.getAmount()));
        accountRepository.update(fromAccount);
        accountRepository.update(toAccount);

        TransactionJournal journal = createJournal(
                TransactionType.TRANSFER,
                ChannelType.INTERNAL_TRANSFER,
                request.getBranchId(),
                null,
                fromAccount,
                toAccount,
                request.getAmount(),
                trim(request.getRemarks()),
                request.getTransactionDate(),
                username
        );

        FundTransfer fundTransfer = new FundTransfer();
        fundTransfer.setTransaction(journal);
        fundTransfer.setFromAccountId(fromAccount.getId());
        fundTransfer.setToAccountId(toAccount.getId());
        fundTransfer.setTransferMode(request.getTransferMode() == null ? TransferMode.INTERNAL : request.getTransferMode());
        fundTransfer.setRemarks(trim(request.getRemarks()));
        fundTransferRepository.save(fundTransfer);

        TransactionResponse response = mapResponse(journal);
        sendTransactionConfirmation(fromAccount.getCustomer(), response, fromAccount.getAccountNumber());
        if (toAccount.getCustomer() != null
                && trim(toAccount.getCustomer().getEmail()) != null
                && (fromAccount.getCustomer() == null
                || trim(fromAccount.getCustomer().getEmail()) == null
                || !toAccount.getCustomer().getEmail().equalsIgnoreCase(fromAccount.getCustomer().getEmail()))) {
            sendTransactionConfirmation(toAccount.getCustomer(), response, toAccount.getAccountNumber());
        }
        return response;
    }

    @Override
    public TransactionResponse chequeClearing(ChequeClearingRequest request, String username) {
        if (request == null) {
            throw new BadRequestException("Cheque clearing request is required");
        }
        validateBranchAndAmount(request.getBranchId(), request.getAmount());
        if (trim(request.getChequeNo()) == null) {
            throw new BadRequestException("Cheque number is required");
        }
        if (trim(request.getDraweeBank()) == null) {
            throw new BadRequestException("Drawee bank is required");
        }
        Account creditAccount = getTransactionalAccount(request.getCreditAccountId(), request.getBranchId(), "Credit account");

        creditAccount.setCurrentBalance(creditAccount.getCurrentBalance().add(request.getAmount()));
        creditAccount.setAvailableBalance(creditAccount.getAvailableBalance().add(request.getAmount()));
        accountRepository.update(creditAccount);

        TransactionJournal journal = createJournal(
                TransactionType.CHEQUE_CLEARING,
                ChannelType.CHEQUE_COUNTER,
                request.getBranchId(),
                null,
                null,
                creditAccount,
                request.getAmount(),
                trim(request.getRemarks()),
                request.getTransactionDate(),
                username
        );

        ChequeClearing chequeClearing = new ChequeClearing();
        chequeClearing.setTransaction(journal);
        chequeClearing.setCreditAccountId(creditAccount.getId());
        chequeClearing.setChequeNo(trim(request.getChequeNo()));
        chequeClearing.setDraweeBank(trim(request.getDraweeBank()));
        chequeClearing.setAmount(request.getAmount());
        chequeClearing.setChequeStatus(ChequeClearingStatus.CLEARED);
        chequeClearing.setRemarks(trim(request.getRemarks()));
        chequeClearingRepository.save(chequeClearing);

        TransactionResponse response = mapResponse(journal);
        sendTransactionConfirmation(creditAccount.getCustomer(), response, creditAccount.getAccountNumber());
        return response;
    }

    @Override
    public StandingInstructionResponse createStandingInstruction(StandingInstructionRequest request, String username) {
        if (request == null) {
            throw new BadRequestException("Standing instruction request is required");
        }
        validateBranchAndAmount(request.getBranchId(), request.getAmount());
        if (request.getFromAccountId() == null || request.getToAccountId() == null) {
            throw new BadRequestException("Both accounts are required");
        }
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BadRequestException("Standing instruction cannot use the same account");
        }
        if (trim(request.getFrequency()) == null) {
            throw new BadRequestException("Frequency is required");
        }

        Account fromAccount = getTransactionalAccount(request.getFromAccountId(), request.getBranchId(), "From account");
        Account toAccount = getTransactionalAccount(request.getToAccountId(), request.getBranchId(), "To account");

        StandingInstruction instruction = new StandingInstruction();
        instruction.setInstructionCode(generateInstructionCode());
        instruction.setFromAccountId(fromAccount.getId());
        instruction.setToAccountId(toAccount.getId());
        instruction.setBranchId(request.getBranchId());
        instruction.setAmount(request.getAmount());
        instruction.setTransferMode(request.getTransferMode() == null ? TransferMode.INTERNAL : request.getTransferMode());
        instruction.setScheduleDate(request.getScheduleDate() == null ? LocalDate.now() : request.getScheduleDate());
        instruction.setFrequency(trim(request.getFrequency()));
        instruction.setRemarks(trim(request.getRemarks()));
        instruction.setStatus(request.getStatus() == null ? RecordStatus.ACTIVE : request.getStatus());
        instruction.setNextExecutionDate(instruction.getScheduleDate());
        standingInstructionRepository.save(instruction);

        return mapStandingInstruction(instruction);
    }

    @Override
    public List<StandingInstructionResponse> listStandingInstructions() {
        return standingInstructionRepository.findAll().stream().map(this::mapStandingInstruction).toList();
    }

    @Override
    public List<TransactionResponse> list() {
        return transactionJournalRepository.findAll().stream().map(this::mapResponse).toList();
    }

    @Override
    public TransactionResponse getById(Long id) {
        return mapResponse(getJournal(id));
    }

    @Override
    public List<TransactionResponse> search(String keyword) {
        return transactionJournalRepository.search(keyword).stream().map(this::mapResponse).toList();
    }

    @Override
    public TransactionResponse reverse(Long id, TransactionReversalRequest request, String username) {
        TransactionJournal original = getJournal(id);
        if (Boolean.TRUE.equals(original.getReversalFlag()) || original.getTransactionStatus() == TransactionStatus.REVERSED) {
            throw new BadRequestException("Transaction is already reversed");
        }
        if (original.getParentTransaction() != null || original.getTransactionType() == TransactionType.REVERSAL) {
            throw new BadRequestException("Reversal transaction cannot be reversed again");
        }
        String reason = trim(request == null ? null : request.getReason());
        if (reason == null) {
            throw new BadRequestException("Reversal reason is required");
        }

        TransactionReversal reversal = transactionReversalRepository.findByOriginalTransactionId(id).orElse(null);
        if (reversal != null && reversal.getStatus() == ReversalStatus.APPROVED) {
            throw new BadRequestException("Approved reversal already exists for this transaction");
        }

        Account debitAccount = original.getDebitAccount();
        Account creditAccount = original.getCreditAccount();

        if (original.getTransactionType() == TransactionType.DEPOSIT || original.getTransactionType() == TransactionType.CHEQUE_CLEARING) {
            if (creditAccount == null) {
                throw new BadRequestException("Credit account is missing for reversal");
            }
            ensureEnoughBalance(creditAccount, original.getAmount());
            creditAccount.setCurrentBalance(creditAccount.getCurrentBalance().subtract(original.getAmount()));
            creditAccount.setAvailableBalance(creditAccount.getAvailableBalance().subtract(original.getAmount()));
            accountRepository.update(creditAccount);
        } else if (original.getTransactionType() == TransactionType.WITHDRAWAL) {
            if (debitAccount == null) {
                throw new BadRequestException("Debit account is missing for reversal");
            }
            debitAccount.setCurrentBalance(debitAccount.getCurrentBalance().add(original.getAmount()));
            debitAccount.setAvailableBalance(debitAccount.getAvailableBalance().add(original.getAmount()));
            accountRepository.update(debitAccount);
        } else if (original.getTransactionType() == TransactionType.TRANSFER) {
            if (debitAccount == null || creditAccount == null) {
                throw new BadRequestException("Transfer reversal requires both accounts");
            }
            ensureEnoughBalance(creditAccount, original.getAmount());
            creditAccount.setCurrentBalance(creditAccount.getCurrentBalance().subtract(original.getAmount()));
            creditAccount.setAvailableBalance(creditAccount.getAvailableBalance().subtract(original.getAmount()));
            debitAccount.setCurrentBalance(debitAccount.getCurrentBalance().add(original.getAmount()));
            debitAccount.setAvailableBalance(debitAccount.getAvailableBalance().add(original.getAmount()));
            accountRepository.update(creditAccount);
            accountRepository.update(debitAccount);
        } else {
            throw new BadRequestException("This transaction type cannot be reversed");
        }

        TransactionJournal reversalJournal = new TransactionJournal();
        reversalJournal.setTransactionRef(generateTransactionRef());
        reversalJournal.setTransactionDate(LocalDateTime.now());
        reversalJournal.setTransactionType(TransactionType.REVERSAL);
        reversalJournal.setChannelType(ChannelType.SYSTEM);
        reversalJournal.setBranchId(original.getBranchId());
        reversalJournal.setTerminalId(original.getTerminalId());
        reversalJournal.setDebitAccount(creditAccount);
        reversalJournal.setCreditAccount(debitAccount);
        reversalJournal.setAmount(original.getAmount());
        reversalJournal.setNarration("Reversal of " + original.getTransactionRef() + ": " + reason);
        reversalJournal.setPostedBy(resolveUsername(username));
        reversalJournal.setApprovedBy(DEFAULT_APPROVED_BY);
        reversalJournal.setReversalFlag(Boolean.TRUE);
        reversalJournal.setParentTransaction(original);
        reversalJournal.setTransactionStatus(TransactionStatus.POSTED);
        reversalJournal.setStatus(RecordStatus.ACTIVE);
        transactionJournalRepository.save(reversalJournal);

        if (reversal == null) {
            reversal = new TransactionReversal();
            reversal.setOriginalTransaction(original);
            reversal.setRequestedBy(resolveUsername(username));
            reversal.setRequestedAt(LocalDateTime.now());
            reversal.setReason(reason);
        }
        reversal.setReversalTransaction(reversalJournal);
        reversal.setApprovedBy(DEFAULT_APPROVED_BY);
        reversal.setApprovedAt(LocalDateTime.now());
        reversal.setStatus(ReversalStatus.APPROVED);
        if (reversal.getId() == null) {
            transactionReversalRepository.save(reversal);
        } else {
            transactionReversalRepository.update(reversal);
        }

        original.setReversalFlag(Boolean.TRUE);
        original.setTransactionStatus(TransactionStatus.REVERSED);
        transactionJournalRepository.update(original);

        TransactionResponse response = mapResponse(reversalJournal);
        if (debitAccount != null && debitAccount.getCustomer() != null) {
            sendTransactionConfirmation(debitAccount.getCustomer(), response, debitAccount.getAccountNumber());
        }
        if (creditAccount != null
                && creditAccount.getCustomer() != null
                && (debitAccount == null
                || debitAccount.getCustomer() == null
                || trim(debitAccount.getCustomer().getEmail()) == null
                || !creditAccount.getCustomer().getEmail().equalsIgnoreCase(debitAccount.getCustomer().getEmail()))) {
            sendTransactionConfirmation(creditAccount.getCustomer(), response, creditAccount.getAccountNumber());
        }
        return response;
    }

    @Override
    public TransactionResponse journal(Long id) {
        return mapResponse(getJournal(id));
    }

    @Override
    public TransactionDashboardSummaryResponse dashboardSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        BigDecimal tellerUsed = cashTransactionRepository.sumByDateRange(start, end);
        BigDecimal usagePercent = BigDecimal.ZERO;
        if (DEFAULT_TELLER_LIMIT.compareTo(BigDecimal.ZERO) > 0) {
            usagePercent = tellerUsed.multiply(BigDecimal.valueOf(100))
                    .divide(DEFAULT_TELLER_LIMIT, 2, RoundingMode.HALF_UP);
        }

        return new TransactionDashboardSummaryResponse(
                transactionJournalRepository.sumByTypeAndDateRange(TransactionType.DEPOSIT, start, end),
                transactionJournalRepository.sumByTypeAndDateRange(TransactionType.WITHDRAWAL, start, end),
                transactionJournalRepository.sumByTypeAndDateRange(TransactionType.TRANSFER, start, end),
                transactionReversalRepository.countByStatus(ReversalStatus.PENDING),
                tellerUsed,
                DEFAULT_TELLER_LIMIT,
                usagePercent,
                transactionJournalRepository.countSuspicious(SUSPICIOUS_THRESHOLD, start, end),
                transactionJournalRepository.topBranches(start, end).stream()
                        .map(row -> new BranchTransactionSummaryResponse((Long) row[0], (Long) row[1]))
                        .toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewVoucher(Long id) {
        TransactionResponse response = getById(id);
        byte[] pdf = renderVoucherPdf(response);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("transaction-voucher-" + response.id() + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadVoucher(Long id) {
        TransactionResponse response = getById(id);
        byte[] pdf = renderVoucherPdf(response);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("transaction-voucher-" + response.id() + ".pdf").build().toString())
                .body(pdf);
    }

    private void validateCashRequest(Long branchId, BigDecimal amount) {
        validateBranchAndAmount(branchId, amount);
    }

    private void validateBranchAndAmount(Long branchId, BigDecimal amount) {
        if (branchId == null || branchId <= 0) {
            throw new BadRequestException("Branch is required");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }
    }

    private void enforceTellerLimit(Long tellerUserId, CashDirection direction, BigDecimal amount) {
        Long resolvedTellerId = resolveTellerUserId(tellerUserId);
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        BigDecimal currentUsage = cashTransactionRepository.sumByTellerAndDirection(resolvedTellerId, direction, start, end);
        if (currentUsage.add(amount).compareTo(DEFAULT_TELLER_LIMIT) > 0) {
            throw new BadRequestException("Teller limit exceeded for today's transaction load");
        }
    }

    private void ensureEnoughBalance(Account account, BigDecimal amount) {
        if (account.getAvailableBalance() == null || account.getAvailableBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Withdrawal or reversal amount exceeds available balance");
        }
    }

    private Account getTransactionalAccount(Long accountId, Long branchId, String label) {
        if (accountId == null) {
            throw new BadRequestException(label + " is required");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(label + " not found"));
        if (account.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException(label + " is archived");
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException(label + " must be active");
        }
        if (branchId != null && branchId > 0 && !branchId.equals(account.getBranchId())) {
            throw new BadRequestException(label + " does not belong to the selected branch");
        }
        return account;
    }

    private TransactionJournal createJournal(
            TransactionType transactionType,
            ChannelType channelType,
            Long branchId,
            Long terminalId,
            Account debitAccount,
            Account creditAccount,
            BigDecimal amount,
            String narration,
            LocalDateTime transactionDate,
            String username
    ) {
        TransactionJournal journal = new TransactionJournal();
        journal.setTransactionRef(generateTransactionRef());
        journal.setTransactionDate(transactionDate == null ? LocalDateTime.now() : transactionDate);
        journal.setTransactionType(transactionType);
        journal.setChannelType(channelType);
        journal.setBranchId(branchId);
        journal.setTerminalId(terminalId);
        journal.setDebitAccount(debitAccount);
        journal.setCreditAccount(creditAccount);
        journal.setAmount(amount);
        journal.setNarration(narration);
        journal.setPostedBy(resolveUsername(username));
        journal.setApprovedBy(DEFAULT_APPROVED_BY);
        journal.setReversalFlag(Boolean.FALSE);
        journal.setTransactionStatus(TransactionStatus.POSTED);
        journal.setStatus(RecordStatus.ACTIVE);
        transactionJournalRepository.save(journal);
        return journal;
    }

    private TransactionJournal getJournal(Long id) {
        if (id == null) {
            throw new BadRequestException("Transaction id is required");
        }
        return transactionJournalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    private TransactionResponse mapResponse(TransactionJournal entity) {
        CashTransaction cash = cashTransactionRepository.findByTransactionId(entity.getId()).orElse(null);
        FundTransfer transfer = fundTransferRepository.findByTransactionId(entity.getId()).orElse(null);
        ChequeClearing cheque = chequeClearingRepository.findByTransactionId(entity.getId()).orElse(null);
        TransactionReversal reversal = transactionReversalRepository.findByOriginalTransactionId(entity.getId()).orElse(null);
        boolean cashVisible = entity.getTransactionType() == TransactionType.DEPOSIT
                || entity.getTransactionType() == TransactionType.WITHDRAWAL;
        boolean transferVisible = entity.getTransactionType() == TransactionType.TRANSFER;
        boolean chequeVisible = entity.getTransactionType() == TransactionType.CHEQUE_CLEARING;

        return new TransactionResponse(
                entity.getId(),
                entity.getTransactionRef(),
                entity.getTransactionType() == null ? null : entity.getTransactionType().name(),
                entity.getChannelType() == null ? null : entity.getChannelType().name(),
                entity.getBranchId(),
                entity.getTerminalId(),
                entity.getDebitAccount() == null ? null : entity.getDebitAccount().getId(),
                entity.getDebitAccount() == null ? null : entity.getDebitAccount().getAccountNumber(),
                entity.getDebitAccount() == null ? null : entity.getDebitAccount().getCustomer().getCustomerCode(),
                entity.getDebitAccount() == null ? null : entity.getDebitAccount().getCustomer().getFullName(),
                entity.getCreditAccount() == null ? null : entity.getCreditAccount().getId(),
                entity.getCreditAccount() == null ? null : entity.getCreditAccount().getAccountNumber(),
                entity.getCreditAccount() == null ? null : entity.getCreditAccount().getCustomer().getCustomerCode(),
                entity.getCreditAccount() == null ? null : entity.getCreditAccount().getCustomer().getFullName(),
                entity.getAmount(),
                entity.getNarration(),
                entity.getPostedBy(),
                entity.getApprovedBy(),
                entity.getReversalFlag(),
                entity.getParentTransaction() == null ? null : entity.getParentTransaction().getId(),
                entity.getParentTransaction() == null ? null : entity.getParentTransaction().getTransactionRef(),
                entity.getTransactionStatus() == null ? null : entity.getTransactionStatus().name(),
                entity.getStatus(),
                entity.getTransactionDate(),
                entity.getCreatedAt(),
                !cashVisible || cash == null || cash.getCashType() == null ? null : cash.getCashType().name(),
                !cashVisible || cash == null || cash.getCashDirection() == null ? null : cash.getCashDirection().name(),
                !cashVisible || cash == null ? null : cash.getTellerUserId(),
                !cashVisible || cash == null ? null : cash.getRemarks(),
                !transferVisible || transfer == null || transfer.getTransferMode() == null ? null : transfer.getTransferMode().name(),
                !transferVisible || transfer == null ? null : transfer.getRemarks(),
                !chequeVisible || cheque == null ? null : cheque.getChequeNo(),
                !chequeVisible || cheque == null ? null : cheque.getDraweeBank(),
                !chequeVisible || cheque == null || cheque.getChequeStatus() == null ? null : cheque.getChequeStatus().name(),
                !chequeVisible || cheque == null ? null : cheque.getRemarks(),
                null,
                null,
                reversal == null || reversal.getStatus() == null ? null : reversal.getStatus().name(),
                reversal == null ? null : reversal.getReason(),
                reversal == null ? null : reversal.getRequestedBy(),
                reversal == null ? null : reversal.getRequestedAt(),
                reversal == null || reversal.getReversalTransaction() == null ? null : reversal.getReversalTransaction().getId()
        );
    }

    private StandingInstructionResponse mapStandingInstruction(StandingInstruction entity) {
        Account fromAccount = accountRepository.findById(entity.getFromAccountId()).orElse(null);
        Account toAccount = accountRepository.findById(entity.getToAccountId()).orElse(null);
        return new StandingInstructionResponse(
                entity.getId(),
                entity.getInstructionCode(),
                entity.getFromAccountId(),
                fromAccount == null ? null : fromAccount.getAccountNumber(),
                fromAccount == null ? null : fromAccount.getCustomer().getFullName(),
                entity.getToAccountId(),
                toAccount == null ? null : toAccount.getAccountNumber(),
                toAccount == null ? null : toAccount.getCustomer().getFullName(),
                entity.getBranchId(),
                entity.getAmount(),
                entity.getTransferMode(),
                entity.getScheduleDate(),
                entity.getFrequency(),
                entity.getNextExecutionDate(),
                entity.getInstructionStatus(),
                entity.getRemarks(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private Long resolveTellerUserId(Long tellerUserId) {
        return tellerUserId == null || tellerUserId <= 0 ? 101L : tellerUserId;
    }

    private String resolveUsername(String username) {
        String value = trim(username);
        return value == null ? DEFAULT_POSTED_BY : value;
    }

    private String generateTransactionRef() {
        String last = transactionJournalRepository.findLastTransactionRef();
        int next = 1;
        if (last != null && last.startsWith("TXN-")) {
            try {
                next = Integer.parseInt(last.substring(4)) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }
        return String.format("TXN-%06d", next);
    }

    private String generateInstructionCode() {
        String last = standingInstructionRepository.findLastInstructionCode();
        int next = 1;
        if (last != null && last.startsWith("SI-")) {
            try {
                next = Integer.parseInt(last.substring(3)) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }
        return String.format("SI-%04d", next);
    }

    private String trim(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private byte[] renderVoucherPdf(TransactionResponse response) {
        try {
            byte[] pdf = pdfDocumentService.renderPdf(buildVoucherHtml(response));
            if (pdf != null && pdf.length > 1000) {
                return pdf;
            }
        } catch (Exception ignored) {
            // Fallback below keeps voucher preview reliable when HTML rendering fails.
        }
        return buildFallbackVoucherPdf(response);
    }

    private byte[] buildFallbackVoucherPdf(TransactionResponse response) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float y = 780;
                content.setFont(PDType1Font.HELVETICA_BOLD, 18);
                content.beginText();
                content.newLineAtOffset(54, y);
                content.showText(documentBrandingService.getBankName());
                content.endText();

                y -= 24;
                content.setFont(PDType1Font.HELVETICA, 10);
                y = drawLine(content, "Transaction Voucher | " + safe(response.transactionRef()), y);
                y = drawLine(content, documentBrandingService.getBankTagline(), y);
                y -= 12;

                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                y = drawLine(content, "Voucher Details", y);
                content.setFont(PDType1Font.HELVETICA, 10);
                y = drawLine(content, "Type: " + safe(response.transactionType()), y);
                y = drawLine(content, "Status: " + safe(response.transactionStatus()), y);
                y = drawLine(content, "Date: " + safe(response.transactionDate() == null ? null : MAIL_DATE_FORMATTER.format(response.transactionDate())), y);
                y = drawLine(content, "Branch ID: " + safe(response.branchId() == null ? null : String.valueOf(response.branchId())), y);
                y = drawLine(content, "Amount: " + safe(response.amount() == null ? null : response.amount().toPlainString()), y);
                y -= 10;

                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                y = drawLine(content, "Account Movement", y);
                content.setFont(PDType1Font.HELVETICA, 10);
                y = drawLine(content, "Debit Account: " + safe(response.debitAccountNumber()), y);
                y = drawLine(content, "Debit Customer: " + safe(resolveParty(response.debitCustomerCode(), response.debitCustomerName())), y);
                y = drawLine(content, "Credit Account: " + safe(response.creditAccountNumber()), y);
                y = drawLine(content, "Credit Customer: " + safe(resolveParty(response.creditCustomerCode(), response.creditCustomerName())), y);
                y -= 10;

                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                y = drawLine(content, "Audit Information", y);
                content.setFont(PDType1Font.HELVETICA, 10);
                y = drawLine(content, "Posted By: " + safe(response.postedBy()), y);
                y = drawLine(content, "Approved By: " + safe(response.approvedBy()), y);
                y = drawLine(content, "Narration: " + safe(response.narration()), y);
                y = drawLine(content, "Reversal Flag: " + (Boolean.TRUE.equals(response.reversalFlag()) ? "YES" : "NO"), y);
                y -= 20;

                content.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                drawLine(content, "This voucher is system generated and valid for branch operation, review and customer support.", y);
            }

            document.save(output);
            return output.toByteArray();
        } catch (IOException ex) {
            throw new BadRequestException("Failed to render transaction voucher");
        }
    }

    private float drawLine(PDPageContentStream content, String text, float y) throws IOException {
        content.beginText();
        content.newLineAtOffset(54, y);
        content.showText(sanitizePdfText(text));
        content.endText();
        return y - 16;
    }

    private String sanitizePdfText(String value) {
        return safe(value)
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replaceAll("[^\\x20-\\x7E]", "?");
    }

    private String safe(String value) {
        return trim(value) == null ? "-" : value.trim();
    }

    private String buildVoucherHtml(TransactionResponse response) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("response", response);
        model.put("debitCustomer", resolveParty(response.debitCustomerCode(), response.debitCustomerName()));
        model.put("creditCustomer", resolveParty(response.creditCustomerCode(), response.creditCustomerName()));
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(".doc-accent{background:linear-gradient(90deg,#0f5b42,#d4af37,#0c6f56);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        return documentTemplateService.render("transaction/transaction-voucher", model);
    }

    private String resolveParty(String code, String name) {
        String codePart = trim(code);
        String namePart = trim(name);
        if (codePart == null && namePart == null) {
            return "-";
        }
        if (codePart == null) {
            return namePart;
        }
        if (namePart == null) {
            return codePart;
        }
        return codePart + " - " + namePart;
    }

    private void sendTransactionConfirmation(Customer customer, TransactionResponse response, String accountNumber) {
        if (customer == null || trim(customer.getEmail()) == null || response == null) {
            return;
        }
        automatedMailService.sendTransactionConfirmationEmail(
                customer.getEmail(),
                customer.getFullName(),
                response.transactionType(),
                response.transactionRef(),
                response.amount() == null ? null : response.amount().toPlainString(),
                accountNumber,
                response.transactionDate() == null ? null : MAIL_DATE_FORMATTER.format(response.transactionDate()),
                response.narration()
        );
    }
}
