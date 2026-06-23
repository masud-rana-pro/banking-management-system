package com.sbms.statement.service.impl;

import com.sbms.account.entity.Account;
import com.sbms.account.repository.AccountRepository;
import com.sbms.branch.entity.Branch;
import com.sbms.branch.entity.BranchCashLedger;
import com.sbms.branch.entity.VaultBalance;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.card.entity.Card;
import com.sbms.card.repository.CardRepository;
import com.sbms.common.aop.AopRequestContext;
import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.common.document.ExcelDocumentService;
import com.sbms.common.document.PdfDocumentService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.customer.repository.CustomerRepository;
import com.sbms.profit.entity.ProfitPosting;
import com.sbms.statement.dto.request.BranchStatementRequestDto;
import com.sbms.statement.dto.request.CustomerStatementRequestDto;
import com.sbms.statement.dto.response.*;
import com.sbms.statement.entity.BranchStatementRequest;
import com.sbms.statement.entity.CustomerStatementRequest;
import com.sbms.statement.entity.FileReference;
import com.sbms.statement.enums.StatementRequestStatus;
import com.sbms.statement.repository.*;
import com.sbms.statement.service.IStatementService;
import com.sbms.transaction.entity.TransactionJournal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class StatementService implements IStatementService {

    @Autowired
    private CustomerStatementRequestRepository customerStatementRequestRepository;

    @Autowired
    private BranchStatementRequestRepository branchStatementRequestRepository;

    @Autowired
    private FileReferenceRepository fileReferenceRepository;

    @Autowired
    private StatementDataRepository statementDataRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Autowired
    private ExcelDocumentService excelDocumentService;

    private static final String EXPORT_PDF = "PDF";
    private static final String EXPORT_CSV = "CSV";
    private static final String EXPORT_EXCEL = "EXCEL";

    private static final String SYSTEM_USER = "SYSTEM";
    private static final Path GENERATED_DIR = Paths.get("generated-statements");

    @Override
    public CustomerStatementRequestResponse requestCustomerStatement(CustomerStatementRequestDto request, String username) {
        validateDateRange(request == null ? null : request.getDateFrom(), request == null ? null : request.getDateTo());
        if (request == null || request.getCustomerId() == null) {
            throw new BadRequestException("Customer is required");
        }
        if (request.getAccountId() == null) {
            throw new BadRequestException("Account is required");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("Selected account does not belong to selected customer");
        }
        if (customer.getStatus() == RecordStatus.ARCHIVED || account.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived customer/account cannot be used for statement request");
        }

        CustomerStatementRequest entity = new CustomerStatementRequest();
        entity.setRequestNo(generateCustomerRequestNo());
        entity.setCustomer(customer);
        entity.setAccount(account);
        entity.setDateFrom(request.getDateFrom());
        entity.setDateTo(request.getDateTo());
        entity.setRequestedBy(resolveUser(request.getRequestedBy(), username));
        entity.setRequestStatus(StatementRequestStatus.REQUESTED);
        customerStatementRequestRepository.save(entity);

        CustomerStatementRequestResponse response = buildCustomerResponse(entity);
        generateCustomerStatementFileAsync(entity, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerStatementRequestResponse> listCustomerStatements() {
        return customerStatementRequestRepository.findAll().stream()
                .map(this::buildCustomerResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerStatementRequestResponse getCustomerStatement(Long id) {
        return buildCustomerResponse(getCustomerRequest(id));
    }

    @Override
    public ResponseEntity<byte[]> previewCustomerStatement(Long id) {
        CustomerStatementRequest request = getCustomerRequest(id);
        return previewStatement(ensureCustomerStatementFile(request));
    }

    @Override
    public ResponseEntity<byte[]> downloadCustomerStatement(Long id) {
        CustomerStatementRequest request = getCustomerRequest(id);
        return downloadStatement(ensureCustomerStatementFile(request), () -> {
            request.setRequestStatus(StatementRequestStatus.DOWNLOADED);
            customerStatementRequestRepository.update(request);
        });
    }

    @Override
    public ResponseEntity<byte[]> exportCustomerStatements(String exportType, String search, String status) {
        List<CustomerStatementRequestResponse> items = filterCustomerStatements(search, status);
        return exportCustomerStatementRegister(exportType, search, status, items);
    }

    @Override
    public BranchStatementRequestResponse requestBranchStatement(BranchStatementRequestDto request, String username) {
        validateDateRange(request == null ? null : request.getDateFrom(), request == null ? null : request.getDateTo());
        if (request == null || request.getBranchId() == null) {
            throw new BadRequestException("Branch is required");
        }
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        if (Boolean.TRUE.equals(branch.getIsDeleted())) {
            throw new BadRequestException("Deleted branch cannot be used for statement request");
        }

        BranchStatementRequest entity = new BranchStatementRequest();
        entity.setRequestNo(generateBranchRequestNo());
        entity.setBranch(branch);
        entity.setDateFrom(request.getDateFrom());
        entity.setDateTo(request.getDateTo());
        entity.setRequestedBy(resolveUser(request.getRequestedBy(), username));
        entity.setRequestStatus(StatementRequestStatus.REQUESTED);
        branchStatementRequestRepository.save(entity);

        BranchStatementRequestResponse response = buildBranchResponse(entity);
        generateBranchStatementFileAsync(entity, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchStatementRequestResponse> listBranchStatements() {
        return branchStatementRequestRepository.findAll().stream()
                .map(this::buildBranchResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BranchStatementRequestResponse getBranchStatement(Long id) {
        return buildBranchResponse(getBranchRequest(id));
    }

    @Override
    public ResponseEntity<byte[]> previewBranchStatement(Long id) {
        BranchStatementRequest request = getBranchRequest(id);
        return previewStatement(ensureBranchStatementFile(request));
    }

    @Override
    public ResponseEntity<byte[]> downloadBranchStatement(Long id) {
        BranchStatementRequest request = getBranchRequest(id);
        return downloadStatement(ensureBranchStatementFile(request), () -> {
            request.setRequestStatus(StatementRequestStatus.DOWNLOADED);
            branchStatementRequestRepository.update(request);
        });
    }

    @Override
    public ResponseEntity<byte[]> exportBranchStatements(String exportType, String search, String status) {
        List<BranchStatementRequestResponse> items = filterBranchStatements(search, status);
        return exportBranchStatementRegister(exportType, search, status, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileReferenceResponse> listFiles() {
        return fileReferenceRepository.findAll().stream().map(this::mapFileResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StatementDashboardSummaryResponse dashboardSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime fromTime = today.atStartOfDay();
        LocalDateTime toTime = LocalDateTime.now();
        Long customerCount = customerStatementRequestRepository.countAll();
        Long branchCount = branchStatementRequestRepository.countAll();
        Long generatedToday = fileReferenceRepository.countCreatedBetween(fromTime, toTime);
        Long downloadCount = safeCount(customerStatementRequestRepository.countByStatus(StatementRequestStatus.DOWNLOADED))
                + safeCount(branchStatementRequestRepository.countByStatus(StatementRequestStatus.DOWNLOADED));
        List<StatementMetricResponse> metrics = List.of(
                new StatementMetricResponse("CUSTOMER_STATEMENT", customerCount),
                new StatementMetricResponse("BRANCH_STATEMENT", branchCount)
        );
        return new StatementDashboardSummaryResponse(
                generatedToday,
                customerCount,
                branchCount,
                downloadCount,
                metrics,
                customerStatementRequestRepository.findRecent(5).stream().map(this::buildCustomerResponse).toList(),
                branchStatementRequestRepository.findRecent(5).stream().map(this::buildBranchResponse).toList()
        );
    }

    private CustomerStatementRequest getCustomerRequest(Long id) {
        if (id == null) {
            throw new BadRequestException("Customer statement request id is required");
        }
        return customerStatementRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer statement request not found"));
    }

    private BranchStatementRequest getBranchRequest(Long id) {
        if (id == null) {
            throw new BadRequestException("Branch statement request id is required");
        }
        return branchStatementRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch statement request not found"));
    }

    private CustomerStatementRequestResponse buildCustomerResponse(CustomerStatementRequest entity) {
        List<TransactionJournal> transactions = statementDataRepository.findTransactionsByAccount(
                entity.getAccount().getId(),
                entity.getDateFrom(),
                entity.getDateTo()
        );
        List<ProfitPosting> profitPostings = statementDataRepository.findProfitPostingsByAccount(
                entity.getAccount().getId(),
                entity.getDateFrom(),
                entity.getDateTo()
        );

        List<StatementLineResponse> lines = new ArrayList<>();
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (TransactionJournal transaction : transactions) {
            boolean debit = transaction.getDebitAccount() != null && transaction.getDebitAccount().getId().equals(entity.getAccount().getId());
            BigDecimal debitAmount = debit ? transaction.getAmount() : BigDecimal.ZERO;
            BigDecimal creditAmount = debit ? BigDecimal.ZERO : transaction.getAmount();
            totalDebit = totalDebit.add(debitAmount);
            totalCredit = totalCredit.add(creditAmount);
            lines.add(new StatementLineResponse(
                    transaction.getTransactionDate().toLocalDate(),
                    transaction.getTransactionDate(),
                    transaction.getTransactionType().name(),
                    transaction.getTransactionRef(),
                    transaction.getChannelType().name(),
                    debitAmount,
                    creditAmount,
                    transaction.getNarration()
            ));
        }

        BigDecimal profitTotal = BigDecimal.ZERO;
        for (ProfitPosting posting : profitPostings) {
            profitTotal = profitTotal.add(posting.getProfitAmount());
            lines.add(new StatementLineResponse(
                    posting.getPostingDate(),
                    posting.getCreatedAt(),
                    "PROFIT_POSTING",
                    posting.getPostingRef(),
                    "SYSTEM",
                    BigDecimal.ZERO,
                    posting.getProfitAmount(),
                    "Profit posted for period " + posting.getPeriodFrom() + " to " + posting.getPeriodTo()
            ));
        }

        lines.sort(Comparator.comparing(StatementLineResponse::lineDateTime, Comparator.nullsLast(Comparator.naturalOrder())));

        return new CustomerStatementRequestResponse(
                entity.getId(),
                entity.getRequestNo(),
                entity.getCustomer().getId(),
                entity.getCustomer().getCustomerCode(),
                entity.getCustomer().getFullName(),
                entity.getAccount().getId(),
                entity.getAccount().getAccountNumber(),
                entity.getAccount().getBranchId(),
                entity.getDateFrom(),
                entity.getDateTo(),
                entity.getRequestStatus(),
                entity.getGeneratedFile() == null ? null : entity.getGeneratedFile().getId(),
                entity.getRequestedBy(),
                entity.getRequestedAt(),
                entity.getGeneratedAt(),
                entity.getStatus(),
                entity.getGeneratedFile() == null ? null : mapFileResponse(entity.getGeneratedFile()),
                (long) transactions.size(),
                totalDebit,
                totalCredit,
                totalCredit.subtract(totalDebit),
                profitTotal,
                entity.getAccount().getCurrentBalance(),
                lines
        );
    }

    private BranchStatementRequestResponse buildBranchResponse(BranchStatementRequest entity) {
        List<TransactionJournal> transactions = statementDataRepository.findTransactionsByBranch(
                entity.getBranch().getId(),
                entity.getDateFrom(),
                entity.getDateTo()
        );
        List<VaultBalance> vaultBalances = statementDataRepository.findVaultBalancesByBranch(
                entity.getBranch().getId(),
                entity.getDateFrom(),
                entity.getDateTo()
        );
        List<BranchCashLedger> cashLedger = statementDataRepository.findCashLedgerByBranch(
                entity.getBranch().getId(),
                entity.getDateFrom(),
                entity.getDateTo()
        );

        List<StatementLineResponse> lines = new ArrayList<>();
        for (TransactionJournal transaction : transactions) {
            lines.add(new StatementLineResponse(
                    transaction.getTransactionDate().toLocalDate(),
                    transaction.getTransactionDate(),
                    transaction.getTransactionType().name(),
                    transaction.getTransactionRef(),
                    transaction.getChannelType().name(),
                    transaction.getAmount(),
                    transaction.getAmount(),
                    transaction.getNarration()
            ));
        }
        for (VaultBalance vault : vaultBalances) {
            lines.add(new StatementLineResponse(
                    vault.getBalanceDate(),
                    vault.getCreatedAt(),
                    "VAULT_BALANCE",
                    "VAULT-" + vault.getId(),
                    "BRANCH",
                    vault.getTotalCashOut(),
                    vault.getTotalCashIn(),
                    "Vault closing balance " + vault.getClosingBalance()
            ));
        }
        for (BranchCashLedger ledger : cashLedger) {
            lines.add(new StatementLineResponse(
                    ledger.getLedgerDate(),
                    ledger.getCreatedAt(),
                    ledger.getEntryType(),
                    ledger.getReferenceNo(),
                    ledger.getSourceType(),
                    ledger.getDebitAmount(),
                    ledger.getCreditAmount(),
                    ledger.getRemarks()
            ));
        }
        lines.sort(Comparator.comparing(StatementLineResponse::lineDateTime, Comparator.nullsLast(Comparator.naturalOrder())));

        return new BranchStatementRequestResponse(
                entity.getId(),
                entity.getRequestNo(),
                entity.getBranch().getId(),
                entity.getBranch().getBranchCode(),
                entity.getBranch().getBranchName(),
                entity.getDateFrom(),
                entity.getDateTo(),
                entity.getRequestStatus(),
                entity.getGeneratedFile() == null ? null : entity.getGeneratedFile().getId(),
                entity.getRequestedBy(),
                entity.getRequestedAt(),
                entity.getGeneratedAt(),
                entity.getStatus(),
                entity.getGeneratedFile() == null ? null : mapFileResponse(entity.getGeneratedFile()),
                (long) transactions.size(),
                transactions.stream().map(TransactionJournal::getAmount).map(this::safeAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
                cashLedger.stream().map(BranchCashLedger::getCreditAmount).map(this::safeAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
                cashLedger.stream().map(BranchCashLedger::getDebitAmount).map(this::safeAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
                vaultBalances.stream()
                        .max(Comparator.comparing(VaultBalance::getBalanceDate).thenComparing(VaultBalance::getId))
                        .map(VaultBalance::getClosingBalance)
                        .orElse(BigDecimal.ZERO),
                (long) statementDataRepository.findActiveTerminalsByBranch(entity.getBranch().getId()).size(),
                lines
        );
    }

    private void generateCustomerStatementFileAsync(CustomerStatementRequest entity, CustomerStatementRequestResponse response) {
        CompletableFuture.runAsync(() -> {
            try {
                FileReference fileReference = generateCustomerStatementFile(entity, response);
                entity.setGeneratedFile(fileReference);
                entity.setGeneratedAt(LocalDateTime.now());
                entity.setRequestStatus(StatementRequestStatus.GENERATED);
                customerStatementRequestRepository.update(entity);
            } catch (RuntimeException ex) {
                entity.setRequestStatus(StatementRequestStatus.FAILED);
                customerStatementRequestRepository.update(entity);
            }
        });
    }

    private void generateBranchStatementFileAsync(BranchStatementRequest entity, BranchStatementRequestResponse response) {
        CompletableFuture.runAsync(() -> {
            try {
                FileReference fileReference = generateBranchStatementFile(entity, response);
                entity.setGeneratedFile(fileReference);
                entity.setGeneratedAt(LocalDateTime.now());
                entity.setRequestStatus(StatementRequestStatus.GENERATED);
                branchStatementRequestRepository.update(entity);
            } catch (RuntimeException ex) {
                entity.setRequestStatus(StatementRequestStatus.FAILED);
                branchStatementRequestRepository.update(entity);
            }
        });
    }
    private CustomerStatementRequestResponse withGeneratedCustomerFile(CustomerStatementRequestResponse source,
                                                                       FileReference fileReference,
                                                                       LocalDateTime generatedAt) {
        return new CustomerStatementRequestResponse(
                source.id(), source.requestNo(), source.customerId(), source.customerCode(), source.customerName(),
                source.accountId(), source.accountNumber(), source.branchId(), source.dateFrom(), source.dateTo(),
                StatementRequestStatus.GENERATED,
                fileReference == null ? null : fileReference.getId(),
                source.requestedBy(), source.requestedAt(), generatedAt, source.status(),
                fileReference == null ? null : mapFileResponse(fileReference),
                source.transactionCount(), source.totalDebit(), source.totalCredit(), source.netMovement(),
                source.profitPosted(), source.currentBalance(), source.lines()
        );
    }

    private BranchStatementRequestResponse withGeneratedBranchFile(BranchStatementRequestResponse source,
                                                                   FileReference fileReference,
                                                                   LocalDateTime generatedAt) {
        return new BranchStatementRequestResponse(
                source.id(), source.requestNo(), source.branchId(), source.branchCode(), source.branchName(),
                source.dateFrom(), source.dateTo(), StatementRequestStatus.GENERATED,
                fileReference == null ? null : fileReference.getId(),
                source.requestedBy(), source.requestedAt(), generatedAt, source.status(),
                fileReference == null ? null : mapFileResponse(fileReference),
                source.transactionCount(), source.totalTransactionAmount(), source.totalCashIn(), source.totalCashOut(),
                source.vaultClosingBalance(), source.activeTerminalCount(), source.lines()
        );
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
    private FileReference generateCustomerStatementFile(CustomerStatementRequest entity, CustomerStatementRequestResponse response) {
        String html = buildCustomerStatementHtml(response);
        return savePdfFileReference(
                "CUSTOMER_STATEMENT",
                "customer_statement_request",
                entity.getId(),
                entity.getRequestNo() + ".pdf",
                "Customer-Statement-" + entity.getRequestNo() + ".pdf",
                html
        );
    }

    private FileReference ensureCustomerStatementFile(CustomerStatementRequest request) {
        if (hasReadableFile(request.getGeneratedFile())) {
            return request.getGeneratedFile();
        }
        CustomerStatementRequestResponse response = buildCustomerResponse(request);
        FileReference fileReference = generateCustomerStatementFile(request, response);
        request.setGeneratedFile(fileReference);
        request.setGeneratedAt(LocalDateTime.now());
        request.setRequestStatus(StatementRequestStatus.GENERATED);
        customerStatementRequestRepository.update(request);
        return fileReference;
    }

    private FileReference generateBranchStatementFile(BranchStatementRequest entity, BranchStatementRequestResponse response) {
        String html = buildBranchStatementHtml(response);
        return savePdfFileReference(
                "BRANCH_STATEMENT",
                "branch_statement_request",
                entity.getId(),
                entity.getRequestNo() + ".pdf",
                "Branch-Statement-" + entity.getRequestNo() + ".pdf",
                html
        );
    }

    private FileReference ensureBranchStatementFile(BranchStatementRequest request) {
        if (hasReadableFile(request.getGeneratedFile())) {
            return request.getGeneratedFile();
        }
        BranchStatementRequestResponse response = buildBranchResponse(request);
        FileReference fileReference = generateBranchStatementFile(request, response);
        request.setGeneratedFile(fileReference);
        request.setGeneratedAt(LocalDateTime.now());
        request.setRequestStatus(StatementRequestStatus.GENERATED);
        branchStatementRequestRepository.update(request);
        return fileReference;
    }

    private boolean hasReadableFile(FileReference fileReference) {
        if (fileReference == null || fileReference.getFilePath() == null || fileReference.getFilePath().trim().isEmpty()) {
            return false;
        }
        return Files.isRegularFile(Paths.get(fileReference.getFilePath()));
    }

    private FileReference saveFileReference(String moduleName, String referenceTable, Long referenceId, String fileName, String originalFileName, String htmlContent) {
        try {
            Files.createDirectories(GENERATED_DIR);
            Path outputPath = GENERATED_DIR.resolve(fileName);
            Files.writeString(outputPath, htmlContent, StandardCharsets.UTF_8);

            FileReference fileReference = new FileReference();
            fileReference.setFileName(fileName);
            fileReference.setOriginalFileName(originalFileName);
            fileReference.setFilePath(outputPath.toAbsolutePath().toString());
            fileReference.setFileType(MediaType.TEXT_HTML_VALUE);
            fileReference.setFileSize(Files.size(outputPath));
            fileReference.setModuleName(moduleName);
            fileReference.setReferenceTable(referenceTable);
            fileReference.setReferenceId(referenceId);
            return fileReferenceRepository.save(fileReference);
        } catch (IOException ex) {
            throw new BadRequestException("Failed to generate statement file");
        }
    }

    private FileReference savePdfFileReference(String moduleName, String referenceTable, Long referenceId, String fileName, String originalFileName, String htmlContent) {
        try {
            Files.createDirectories(GENERATED_DIR);
            Path outputPath = GENERATED_DIR.resolve(fileName);
            Files.write(outputPath, pdfDocumentService.renderPdf(htmlContent));

            FileReference fileReference = new FileReference();
            fileReference.setFileName(fileName);
            fileReference.setOriginalFileName(originalFileName);
            fileReference.setFilePath(outputPath.toAbsolutePath().toString());
            fileReference.setFileType(MediaType.APPLICATION_PDF_VALUE);
            fileReference.setFileSize(Files.size(outputPath));
            fileReference.setModuleName(moduleName);
            fileReference.setReferenceTable(referenceTable);
            fileReference.setReferenceId(referenceId);
            return fileReferenceRepository.save(fileReference);
        } catch (IOException ex) {
            throw new BadRequestException("Failed to generate statement PDF file");
        } catch (RuntimeException ex) {
            String htmlFileName = fileName.replaceAll("(?i)\\.pdf$", ".html");
            String htmlOriginalName = originalFileName.replaceAll("(?i)\\.pdf$", ".html");
            return saveFileReference(moduleName, referenceTable, referenceId, htmlFileName, htmlOriginalName, htmlContent);
        }
    }

    private ResponseEntity<byte[]> downloadStatement(FileReference fileReference, Runnable onDownloaded) {
        if (fileReference == null) {
            throw new BadRequestException("Generated file reference is required before download");
        }

        try {
            Path path = Paths.get(fileReference.getFilePath());
            if (!Files.exists(path)) {
                throw new BadRequestException("Generated statement file is missing");
            }
            byte[] content = Files.readAllBytes(path);
            onDownloaded.run();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileReference.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileReference.getOriginalFileName()).build().toString())
                    .body(content);
        } catch (IOException ex) {
            throw new BadRequestException("Failed to download generated statement file");
        }
    }

    private ResponseEntity<byte[]> previewStatement(FileReference fileReference) {
        if (fileReference == null) {
            throw new BadRequestException("Generated file reference is required before preview");
        }
        try {
            Path path = Paths.get(fileReference.getFilePath());
            if (!Files.exists(path)) {
                throw new BadRequestException("Generated statement file is missing");
            }
            byte[] content = Files.readAllBytes(path);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileReference.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(fileReference.getOriginalFileName()).build().toString())
                    .body(content);
        } catch (IOException ex) {
            throw new BadRequestException("Failed to preview generated statement file");
        }
    }

    private ResponseEntity<byte[]> exportCustomerStatementRegister(String exportType, String search, String status,
                                                                  List<CustomerStatementRequestResponse> items) {
        String normalized = normalizeExportType(exportType);
        String fileNameBase = "customer-statement-register";
        BigDecimal totalDebit = items.stream().map(CustomerStatementRequestResponse::totalDebit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = items.stream().map(CustomerStatementRequestResponse::totalCredit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal profitPosted = items.stream().map(CustomerStatementRequestResponse::profitPosted).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalBalance = items.stream().map(CustomerStatementRequestResponse::currentBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        long transactionCount = items.stream().map(CustomerStatementRequestResponse::transactionCount).reduce(0L, Long::sum);

        return switch (normalized) {
            case EXPORT_CSV -> buildDownloadResponse(
                    buildCustomerRegisterCsv(items),
                    "text/csv",
                    fileNameBase + ".csv"
            );
            case EXPORT_EXCEL -> buildDownloadResponse(
                    excelDocumentService.buildTabularWorkbook(
                            "Customer Statement Register",
                            buildMetadata(search, status, items.size()),
                            List.of("Request No", "Customer", "Account", "Period", "Status", "Transactions", "Current Balance"),
                            items.stream().map(item -> List.of(
                                    item.requestNo(),
                                    item.customerCode() + " - " + item.customerName(),
                                    item.accountNumber(),
                                    item.dateFrom() + " to " + item.dateTo(),
                                    String.valueOf(item.requestStatus()),
                                    String.valueOf(item.transactionCount()),
                                    formatAmount(item.currentBalance())
                            )).toList()
                    ),
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    fileNameBase + ".xlsx"
            );
            default -> buildPdfExportResponse(
                    documentTemplateService.render("statement/customer-statement-list",
                            buildCustomerRegisterTemplateModel(items, search, status, transactionCount, totalDebit, totalCredit, profitPosted, totalBalance)),
                    fileNameBase
            );
        };
    }

    private ResponseEntity<byte[]> exportBranchStatementRegister(String exportType, String search, String status,
                                                                List<BranchStatementRequestResponse> items) {
        String normalized = normalizeExportType(exportType);
        String fileNameBase = "branch-statement-register";
        BigDecimal totalAmount = items.stream().map(BranchStatementRequestResponse::totalTransactionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCashIn = items.stream().map(BranchStatementRequestResponse::totalCashIn).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCashOut = items.stream().map(BranchStatementRequestResponse::totalCashOut).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalVaultBalance = items.stream().map(BranchStatementRequestResponse::vaultClosingBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        long transactionCount = items.stream().map(BranchStatementRequestResponse::transactionCount).reduce(0L, Long::sum);

        return switch (normalized) {
            case EXPORT_CSV -> buildDownloadResponse(
                    buildBranchRegisterCsv(items),
                    "text/csv",
                    fileNameBase + ".csv"
            );
            case EXPORT_EXCEL -> buildDownloadResponse(
                    excelDocumentService.buildTabularWorkbook(
                            "Branch Statement Register",
                            buildMetadata(search, status, items.size()),
                            List.of("Request No", "Branch", "Period", "Status", "Transactions", "Total Amount", "Vault Balance"),
                            items.stream().map(item -> List.of(
                                    item.requestNo(),
                                    item.branchCode() + " - " + item.branchName(),
                                    item.dateFrom() + " to " + item.dateTo(),
                                    String.valueOf(item.requestStatus()),
                                    String.valueOf(item.transactionCount()),
                                    formatAmount(item.totalTransactionAmount()),
                                    formatAmount(item.vaultClosingBalance())
                            )).toList()
                    ),
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    fileNameBase + ".xlsx"
            );
            default -> buildPdfExportResponse(
                    documentTemplateService.render("statement/branch-statement-list",
                            buildBranchRegisterTemplateModel(items, search, status, transactionCount, totalAmount, totalCashIn, totalCashOut, totalVaultBalance)),
                    fileNameBase
            );
        };
    }

    private Map<String, Object> buildCustomerRegisterTemplateModel(List<CustomerStatementRequestResponse> items, String search, String status,
                                                                   long transactionCount, BigDecimal totalDebit, BigDecimal totalCredit,
                                                                   BigDecimal profitPosted, BigDecimal totalBalance) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("items", items);
        model.put("documentStyle", documentStyle(".customer-accent{background:linear-gradient(90deg,#0f9d94,#f0b429);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        model.put("generatedAt", LocalDateTime.now().toString());
        model.put("searchLabel", labelOrAll(search));
        model.put("statusLabel", labelOrAll(status));
        model.put("totalCount", items.size());
        model.put("transactionCount", transactionCount);
        model.put("totalDebit", formatAmount(totalDebit));
        model.put("totalCredit", formatAmount(totalCredit));
        model.put("profitPosted", formatAmount(profitPosted));
        model.put("totalBalance", formatAmount(totalBalance));
        return model;
    }

    private Map<String, Object> buildBranchRegisterTemplateModel(List<BranchStatementRequestResponse> items, String search, String status,
                                                                 long transactionCount, BigDecimal totalAmount, BigDecimal totalCashIn,
                                                                 BigDecimal totalCashOut, BigDecimal totalVaultBalance) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("items", items);
        model.put("documentStyle", documentStyle(".branch-accent{background:linear-gradient(90deg,#064e3b,#0d9488,#f59e0b);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        model.put("generatedAt", LocalDateTime.now().toString());
        model.put("searchLabel", labelOrAll(search));
        model.put("statusLabel", labelOrAll(status));
        model.put("totalCount", items.size());
        model.put("transactionCount", transactionCount);
        model.put("totalAmount", formatAmount(totalAmount));
        model.put("totalCashIn", formatAmount(totalCashIn));
        model.put("totalCashOut", formatAmount(totalCashOut));
        model.put("vaultBalance", formatAmount(totalVaultBalance));
        return model;
    }

    private List<CustomerStatementRequestResponse> filterCustomerStatements(String search, String status) {
        String keyword = safeLower(search);
        String statusFilter = safeUpper(status);
        return listCustomerStatements().stream()
                .filter(item -> keyword.isEmpty()
                        || safeLower(item.requestNo()).contains(keyword)
                        || safeLower(item.customerCode()).contains(keyword)
                        || safeLower(item.customerName()).contains(keyword)
                        || safeLower(item.accountNumber()).contains(keyword)
                        || String.valueOf(item.customerId()).equals(keyword))
                .filter(item -> statusFilter.isEmpty() || item.requestStatus().name().equals(statusFilter))
                .toList();
    }

    private List<BranchStatementRequestResponse> filterBranchStatements(String search, String status) {
        String keyword = safeLower(search);
        String statusFilter = safeUpper(status);
        return listBranchStatements().stream()
                .filter(item -> keyword.isEmpty()
                        || safeLower(item.requestNo()).contains(keyword)
                        || safeLower(item.branchCode()).contains(keyword)
                        || safeLower(item.branchName()).contains(keyword))
                .filter(item -> statusFilter.isEmpty() || item.requestStatus().name().equals(statusFilter))
                .toList();
    }

    private byte[] buildCustomerRegisterCsv(List<CustomerStatementRequestResponse> items) {
        StringBuilder builder = new StringBuilder();
        builder.append("Request No,Customer,Account,Period,Status,Transactions,Current Balance\n");
        for (CustomerStatementRequestResponse item : items) {
            builder.append(csv(item.requestNo())).append(',')
                    .append(csv(item.customerCode() + " - " + item.customerName())).append(',')
                    .append(csv(item.accountNumber())).append(',')
                    .append(csv(item.dateFrom() + " to " + item.dateTo())).append(',')
                    .append(csv(item.requestStatus().name())).append(',')
                    .append(csv(String.valueOf(item.transactionCount()))).append(',')
                    .append(csv(formatAmount(item.currentBalance()))).append('\n');
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] buildBranchRegisterCsv(List<BranchStatementRequestResponse> items) {
        StringBuilder builder = new StringBuilder();
        builder.append("Request No,Branch,Period,Status,Transactions,Total Amount,Vault Balance\n");
        for (BranchStatementRequestResponse item : items) {
            builder.append(csv(item.requestNo())).append(',')
                    .append(csv(item.branchCode() + " - " + item.branchName())).append(',')
                    .append(csv(item.dateFrom() + " to " + item.dateTo())).append(',')
                    .append(csv(item.requestStatus().name())).append(',')
                    .append(csv(String.valueOf(item.transactionCount()))).append(',')
                    .append(csv(formatAmount(item.totalTransactionAmount()))).append(',')
                    .append(csv(formatAmount(item.vaultClosingBalance()))).append('\n');
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private ResponseEntity<byte[]> buildDownloadResponse(byte[] content, String contentType, String fileName) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileName).build().toString())
                .body(content);
    }

    private ResponseEntity<byte[]> buildPdfExportResponse(String html, String fileNameBase) {
        try {
            return buildDownloadResponse(
                    pdfDocumentService.renderPdf(html),
                    MediaType.APPLICATION_PDF_VALUE,
                    fileNameBase + ".pdf"
            );
        } catch (RuntimeException ex) {
            return buildDownloadResponse(
                    html.getBytes(StandardCharsets.UTF_8),
                    MediaType.TEXT_HTML_VALUE,
                    fileNameBase + ".html"
            );
        }
    }

    private Map<String, String> buildMetadata(String search, String status, int totalCount) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("Search", labelOrAll(search));
        metadata.put("Status", labelOrAll(status));
        metadata.put("Total Rows", String.valueOf(totalCount));
        metadata.put("Generated At", LocalDateTime.now().toString());
        return metadata;
    }

    private String normalizeExportType(String exportType) {
        String normalized = safeUpper(exportType);
        if (normalized.isEmpty()) {
            return EXPORT_PDF;
        }
        if (!EXPORT_PDF.equals(normalized) && !EXPORT_CSV.equals(normalized) && !EXPORT_EXCEL.equals(normalized)) {
            throw new BadRequestException("Unsupported statement export type");
        }
        return normalized;
    }

    private String safeLower(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String safeUpper(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private String csv(String value) {
        String text = value == null ? "" : value;
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    private String labelOrAll(String value) {
        return value == null || value.trim().isEmpty() ? "All" : value.trim();
    }

    private String formatAmount(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private String buildCustomerStatementHtml(CustomerStatementRequestResponse response) {
        return documentTemplateService.render("statement/customer-statement", Map.of(
                "response", response,
                "documentStyle", documentStyle(".customer-accent{background:linear-gradient(90deg,#0f9d94,#f0b429);}"),
                "logoDataUri", documentBrandingService.getLogoDataUri(),
                "bankName", documentBrandingService.getBankName(),
                "bankTagline", documentBrandingService.getBankTagline(),
                "bankAddress", documentBrandingService.getBankAddress(),
                "bankContact", documentBrandingService.getBankContact(),
                "debitShare", percent(response.totalDebit(), response.totalDebit().add(response.totalCredit())),
                "creditShare", percent(response.totalCredit(), response.totalDebit().add(response.totalCredit()))
        ));
    }

    private String buildBranchStatementHtml(BranchStatementRequestResponse response) {
        return documentTemplateService.render("statement/branch-statement", Map.of(
                "response", response,
                "documentStyle", documentStyle(".branch-accent{background:linear-gradient(90deg,#064e3b,#0d9488,#f59e0b);}"),
                "logoDataUri", documentBrandingService.getLogoDataUri(),
                "bankName", documentBrandingService.getBankName(),
                "bankTagline", documentBrandingService.getBankTagline(),
                "bankAddress", documentBrandingService.getBankAddress(),
                "bankContact", documentBrandingService.getBankContact(),
                "cashInShare", percent(response.totalCashIn(), response.totalCashIn().add(response.totalCashOut())),
                "cashOutShare", percent(response.totalCashOut(), response.totalCashIn().add(response.totalCashOut()))
        ));
    }

    private String documentStyle(String accentCss) {
        return documentBrandingService.getPremiumDocumentStyle(accentCss);
    }

    private FileReferenceResponse mapFileResponse(FileReference entity) {
        return new FileReferenceResponse(
                entity.getId(),
                entity.getFileName(),
                entity.getOriginalFileName(),
                entity.getFilePath(),
                entity.getFileType(),
                entity.getFileSize(),
                entity.getModuleName(),
                entity.getReferenceTable(),
                entity.getReferenceId(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private void validateDateRange(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null || dateTo == null) {
            throw new BadRequestException("Date range is required");
        }
        if (dateTo.isBefore(dateFrom)) {
            throw new BadRequestException("Date to cannot be earlier than date from");
        }
    }

    private String generateCustomerRequestNo() {
        String lastCode = customerStatementRequestRepository.findLastRequestNo();
        int nextNumber = 1;
        if (lastCode != null && lastCode.startsWith("CSR-")) {
            nextNumber = Integer.parseInt(lastCode.substring(4)) + 1;
        }
        return String.format("CSR-%05d", nextNumber);
    }

    private String generateBranchRequestNo() {
        String lastCode = branchStatementRequestRepository.findLastRequestNo();
        int nextNumber = 1;
        if (lastCode != null && lastCode.startsWith("BSR-")) {
            nextNumber = Integer.parseInt(lastCode.substring(4)) + 1;
        }
        return String.format("BSR-%05d", nextNumber);
    }

    private String resolveUser(String requestedBy, String fallback) {
        String trimmed = requestedBy == null || requestedBy.trim().isEmpty() ? null : requestedBy.trim();
        if (trimmed != null) {
            return trimmed;
        }
        String currentUsername = AopRequestContext.currentUsername();
        if (currentUsername != null && !currentUsername.trim().isEmpty()) {
            return currentUsername.trim();
        }
        return fallback;
    }

    private Long safeCount(Long value) {
        return value == null ? 0L : value;
    }

    private int percent(BigDecimal value, BigDecimal total) {
        if (value == null || total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return value.multiply(BigDecimal.valueOf(100))
                .divide(total, 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }
}
