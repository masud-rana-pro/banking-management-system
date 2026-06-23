package com.sbms.report.service.impl;

import com.sbms.accounting.dto.response.ProfitLossBranchSummaryResponse;
import com.sbms.accounting.dto.response.ProfitLossResponse;
import com.sbms.accounting.dto.response.ProfitLossRowResponse;
import com.sbms.accounting.dto.response.TrialBalanceResponse;
import com.sbms.accounting.dto.response.TrialBalanceRowResponse;
import com.sbms.accounting.service.IAccountingCoreService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.ExcelDocumentService;
import com.sbms.common.document.PdfDocumentService;
import com.sbms.common.aop.AopRequestContext;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.common.websocket.LiveUpdateGateway;
import com.sbms.branch.entity.Branch;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.report.dto.request.ManagementExpenseEntryRequest;
import com.sbms.report.dto.response.ManagementExpenseEntryResponse;
import com.sbms.report.dto.response.*;
import com.sbms.report.entity.ManagementExpenseEntry;
import com.sbms.report.entity.ReportDefinition;
import com.sbms.report.entity.ReportRequestLog;
import com.sbms.report.enums.ReportRequestStatus;
import com.sbms.report.enums.ReportType;
import com.sbms.report.repository.ManagementExpenseEntryRepository;
import com.sbms.report.repository.ReportDataRepository;
import com.sbms.report.repository.ReportDefinitionRepository;
import com.sbms.report.repository.ReportRequestLogRepository;
import com.sbms.report.service.IReportService;
import com.sbms.statement.entity.FileReference;
import com.sbms.statement.repository.FileReferenceRepository;
import com.sbms.user.entity.User;
import com.sbms.user.repository.UserRepository;
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
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class ReportService implements IReportService {

    private static final String EXPORT_PDF = "PDF";
    private static final String EXPORT_CSV = "CSV";
    private static final String EXPORT_EXCEL = "EXCEL";
    private static final String SYSTEM_USER = "SYSTEM";
    private static final Set<String> GLOBAL_REPORTING_ROLES = Set.of(
            "SYSTEM_ADMIN",
            "MIS_OFFICER",
            "COMPLIANCE_OFFICER",
            "INTERNAL_AUDITOR",
            "TREASURY_FINANCE_OFFICER"
    );
    private static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("#,##0.00");
    private static final Path GENERATED_DIR = Paths.get("generated-reports");

    @Autowired
    private ReportDefinitionRepository definitionRepository;

    @Autowired
    private ReportRequestLogRepository requestLogRepository;

    @Autowired
    private ReportDataRepository reportDataRepository;

    @Autowired
    private ManagementExpenseEntryRepository managementExpenseEntryRepository;

    @Autowired
    private FileReferenceRepository fileReferenceRepository;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Autowired
    private ExcelDocumentService excelDocumentService;

    @Autowired
    private LiveUpdateGateway liveUpdateGateway;

    @Autowired
    private AutomatedMailService automatedMailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private IAccountingCoreService accountingCoreService;

    private volatile boolean defaultDefinitionsEnsured;

    @Override
    @Transactional(readOnly = true)
    public ReportDashboardSummaryResponse getDashboardSummary() {
        ensureDefaultDefinitions();
        LocalDate today = LocalDate.now();
        LocalDateTime fromTime = today.atStartOfDay();
        LocalDateTime toTime = LocalDateTime.now();
        List<ReportUsageSummaryResponse> mostUsed = requestLogRepository.findMostUsedReports(6).stream()
                .map(row -> new ReportUsageSummaryResponse(
                        String.valueOf(row[0]),
                        String.valueOf(row[1]),
                        String.valueOf(row[2]),
                        ((Number) row[3]).longValue()
                ))
                .toList();

        return new ReportDashboardSummaryResponse(
                safeLong(requestLogRepository.countGeneratedToday(fromTime, toTime)),
                safeLong(requestLogRepository.countRegulatoryPending()),
                reportDataRepository.sumBranchVolume(today.minusDays(30), today),
                reportDataRepository.sumFinancingAmount(today.minusDays(30), today),
                reportDataRepository.sumProfitAmount(today.minusDays(30), today),
                mostUsed,
                requestLogRepository.findRecent(10).stream().map(this::mapLog).toList()
        );
    }

    @Override
    public ReportResultResponse getOperationalReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy) {
        return generateReport("OPERATIONAL", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), branchId, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getProfitDistributionReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy) {
        return generateReport("PROFIT_DISTRIBUTION", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), null, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getManagementPlReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy) {
        return generateReport("MANAGEMENT_PL", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), null, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getTrialBalanceReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy) {
        return generateReport("TRIAL_BALANCE", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), branchId, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getLedgerProfitLossReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy) {
        return generateReport("LEDGER_PROFIT_LOSS", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), branchId, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getFinancingPortfolioReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy) {
        return generateReport("FINANCING_PORTFOLIO", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), null, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getParReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy) {
        return generateReport("PAR", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), null, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getShariahAuditReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy) {
        return generateReport("SHARIAH_AUDIT", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), null, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getBranchReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy) {
        return generateReport("BRANCH", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), branchId, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getKpiReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy) {
        return generateReport("KPI", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), null, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getGrowthReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy) {
        return generateReport("GROWTH", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), null, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getLoanRecoveryReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy) {
        return generateReport("LOAN_RECOVERY", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), branchId, exportType, requestedBy);
    }

    @Override
    public ReportResultResponse getMonthlyClosingReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy) {
        return generateReport("MONTHLY_CLOSING", normalizeDateFrom(dateFrom), normalizeDateTo(dateTo), branchId, exportType, requestedBy);
    }

    @Override
    public ManagementExpenseEntryResponse createManagementExpenseEntry(ManagementExpenseEntryRequest request, String requestedBy) {
        if (request == null) {
            throw new BadRequestException("Expense request is required");
        }
        if (request.getExpenseDate() == null) {
            throw new BadRequestException("Expense date is required");
        }
        if (request.getExpenseCategory() == null || request.getExpenseCategory().trim().isEmpty()) {
            throw new BadRequestException("Expense category is required");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Expense amount must be greater than zero");
        }
        if (request.getBranchId() != null) {
            branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        }

        ManagementExpenseEntry entity = new ManagementExpenseEntry();
        entity.setExpenseDate(request.getExpenseDate());
        entity.setBranchId(request.getBranchId());
        entity.setExpenseCategory(request.getExpenseCategory().trim().toUpperCase(Locale.ROOT));
        entity.setExpenseCode(trimToNull(request.getExpenseCode()));
        entity.setAmount(request.getAmount());
        entity.setReferenceNo(trimToNull(request.getReferenceNo()));
        entity.setRemarks(trimToNull(request.getRemarks()));
        entity.setCreatedBy(resolveUser(requestedBy));
        return mapManagementExpense(managementExpenseEntryRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagementExpenseEntryResponse> getManagementExpenseEntries(LocalDate dateFrom, LocalDate dateTo, Long branchId, String expenseCategory, String keyword) {
        LocalDate effectiveFrom = dateFrom == null ? LocalDate.now().minusDays(30) : dateFrom;
        LocalDate effectiveTo = dateTo == null ? LocalDate.now() : dateTo;
        validateDateRange(effectiveFrom, effectiveTo);
        return managementExpenseEntryRepository.findAll(effectiveFrom, effectiveTo, branchId, expenseCategory, keyword).stream()
                .map(this::mapManagementExpense)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportRequestLogResponse> getExportHistory(String reportType, String requestStatus, String keyword) {
        ensureDefaultDefinitions();
        return requestLogRepository.findAll(reportType, requestStatus, keyword).stream()
                .map(this::mapLog)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportRequestLogResponse getExportHistoryById(Long id) {
        if (id == null) {
            throw new BadRequestException("Export history id is required");
        }
        return mapLog(requestLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report export history not found")));
    }

    @Override
    @Transactional
    public ResponseEntity<byte[]> previewExportHistoryFile(Long id) {
        if (id == null) {
            throw new BadRequestException("Export history id is required");
        }
        ReportRequestLog log = requestLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report export history not found"));

        try {
            FileReference generatedFile = ensureExportHistoryFile(log);
            Path path = Paths.get(generatedFile.getFilePath());
            byte[] content = Files.readAllBytes(path);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(generatedFile.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.inline().filename(generatedFile.getOriginalFileName()).build().toString())
                    .body(content);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new BadRequestException("Failed to preview generated report file");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<byte[]> downloadExportHistoryFile(Long id) {
        if (id == null) {
            throw new BadRequestException("Export history id is required");
        }
        ReportRequestLog log = requestLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report export history not found"));

        try {
            FileReference generatedFile = ensureExportHistoryFile(log);
            Path path = Paths.get(generatedFile.getFilePath());

            byte[] content = Files.readAllBytes(path);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(generatedFile.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment().filename(generatedFile.getOriginalFileName()).build().toString())
                    .body(content);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new BadRequestException("Failed to download generated report file");
        }
    }

    private FileReference ensureExportHistoryFile(ReportRequestLog log) {
        FileReference generatedFile = log.getGeneratedFile();
        if (generatedFile != null && Files.exists(Paths.get(generatedFile.getFilePath()))) {
            return generatedFile;
        }
        if (log.getReport() == null) {
            throw new BadRequestException("Generated report file is missing");
        }

        Long branchId = extractBranchId(log.getFilterJson());
        String exportType = exportTypeFromFile(generatedFile);
        ReportPayload payload = buildPayload(log.getReport().getQueryKey(), log.getDateFrom(), log.getDateTo(), branchId);
        FileReference regeneratedFile = generateReportFile(log, log.getReport(), payload, exportType);
        log.setGeneratedFile(regeneratedFile);
        log.setGeneratedAt(LocalDateTime.now());
        log.setRequestStatus(ReportRequestStatus.GENERATED);
        requestLogRepository.update(log);
        return regeneratedFile;
    }

    private String exportTypeFromFile(FileReference fileReference) {
        if (fileReference == null || fileReference.getFileType() == null) {
            return "PRINT";
        }
        String fileType = fileReference.getFileType().toLowerCase(Locale.ROOT);
        if (fileType.contains("csv")) {
            return EXPORT_CSV;
        }
        if (fileType.contains("pdf")) {
            return EXPORT_PDF;
        }
        if (fileType.contains("spreadsheet") || fileType.contains("excel")) {
            return EXPORT_EXCEL;
        }
        return "PRINT";
    }

    private Long extractBranchId(String filterJson) {
        if (filterJson == null || filterJson.isBlank()) {
            return null;
        }
        int keyIndex = filterJson.indexOf("\"branchId\"");
        if (keyIndex < 0) {
            return null;
        }
        int colonIndex = filterJson.indexOf(':', keyIndex);
        if (colonIndex < 0) {
            return null;
        }
        int endIndex = filterJson.indexOf(',', colonIndex);
        if (endIndex < 0) {
            endIndex = filterJson.indexOf('}', colonIndex);
        }
        if (endIndex < 0) {
            return null;
        }
        String raw = filterJson.substring(colonIndex + 1, endIndex).trim();
        if (raw.isEmpty() || "null".equalsIgnoreCase(raw)) {
            return null;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
    private ReportResultResponse generateReport(String queryKey, LocalDate dateFrom, LocalDate dateTo, Long branchId,
                                                String exportType, String requestedBy) {
        ensureDefaultDefinitions();
        validateDateRange(dateFrom, dateTo);

        ReportDefinition definition = definitionRepository.findByQueryKey(queryKey)
                .orElseThrow(() -> new ResourceNotFoundException("Report definition not found for " + queryKey));

        String resolvedUser = resolveUser(requestedBy);
        Long effectiveBranchId = resolveEffectiveBranchId(branchId, resolvedUser, queryKey);
        validateSensitiveAccess(definition, resolvedUser);

        ReportRequestLog log = new ReportRequestLog();
        log.setReport(definition);
        log.setRequestedBy(resolvedUser);
        log.setDateFrom(dateFrom);
        log.setDateTo(dateTo);
        log.setFilterJson(buildFilterJson(dateFrom, dateTo, effectiveBranchId, exportType));
        log.setRequestStatus(ReportRequestStatus.REQUESTED);
        requestLogRepository.save(log);

        try {
            ReportPayload payload = buildPayload(definition.getQueryKey(), dateFrom, dateTo, effectiveBranchId);
            String resolvedExportType = normalizeExportType(exportType);
            FileReference fileReference = resolvedExportType == null ? null : generateReportFile(log, definition, payload, resolvedExportType);
            log.setGeneratedFile(fileReference);
            log.setGeneratedAt(LocalDateTime.now());
            log.setRequestStatus(resolvedExportType == null ? ReportRequestStatus.GENERATED : ReportRequestStatus.EXPORTED);
            requestLogRepository.update(log);
            publishReportReady(definition, requestedBy, resolvedExportType);

            return new ReportResultResponse(
                    log.getId(),
                    definition.getId(),
                    definition.getReportCode(),
                    definition.getReportName(),
                    definition.getReportType(),
                    definition.getQueryKey(),
                    dateFrom,
                    dateTo,
                    effectiveBranchId,
                    resolvedExportType,
                    resolvedUser,
                    log.getFilterJson(),
                    log.getRequestStatus(),
                    log.getGeneratedAt(),
                    mapFile(fileReference),
                    payload.metrics(),
                    payload.columns(),
                    payload.rows(),
                    requestLogRepository.findRecentByReportId(definition.getId(), 8).stream().map(this::mapLog).toList()
            );
        } catch (RuntimeException ex) {
            log.setRequestStatus(ReportRequestStatus.FAILED);
            requestLogRepository.update(log);
            throw ex;
        }
    }

    private void ensureDefaultDefinitions() {
        if (defaultDefinitionsEnsured) {
            return;
        }
        synchronized (this) {
            if (defaultDefinitionsEnsured) {
                return;
            }
            ensureDefinition("REP-OPR-001", "Operational Report", ReportType.OPERATIONAL, "OPERATIONAL");
            ensureDefinition("REP-PRO-001", "Profit Distribution Report", ReportType.PROFIT, "PROFIT_DISTRIBUTION");
            ensureDefinition("REP-MPL-001", "Management Profit & Loss", ReportType.PROFIT, "MANAGEMENT_PL");
            ensureDefinition("REP-TBL-001", "Trial Balance", ReportType.REGULATORY, "TRIAL_BALANCE");
            ensureDefinition("REP-LPL-001", "Ledger Profit & Loss", ReportType.PROFIT, "LEDGER_PROFIT_LOSS");
            ensureDefinition("REP-FIN-001", "Financing Portfolio Report", ReportType.FINANCING, "FINANCING_PORTFOLIO");
            ensureDefinition("REP-PAR-001", "PAR Report", ReportType.PAR, "PAR");
            ensureDefinition("REP-SHA-001", "Shariah Audit Report", ReportType.SHARIAH_AUDIT, "SHARIAH_AUDIT");
            ensureDefinition("REP-BRA-001", "Branch Performance Report", ReportType.BRANCH, "BRANCH");
            ensureDefinition("REP-KPI-001", "Enterprise KPI Report", ReportType.KPI, "KPI");
            ensureDefinition("REP-GRO-001", "Growth Report", ReportType.GROWTH, "GROWTH");
            ensureDefinition("REP-REC-001", "Loan Recovery Report", ReportType.RECOVERY, "LOAN_RECOVERY");
            ensureDefinition("REP-CLO-001", "Monthly Closing Snapshot", ReportType.CLOSING, "MONTHLY_CLOSING");
            defaultDefinitionsEnsured = true;
        }
    }

    private void ensureDefinition(String code, String name, ReportType reportType, String queryKey) {
        if (definitionRepository.findByQueryKey(queryKey).isPresent()) {
            return;
        }
        ReportDefinition definition = new ReportDefinition();
        definition.setReportCode(code);
        definition.setReportName(name);
        definition.setReportType(reportType);
        definition.setQueryKey(queryKey);
        definition.setExportTypes("PDF,CSV,EXCEL,PRINT");
        definition.setStatus(RecordStatus.ACTIVE);
        definitionRepository.save(definition);
    }

    private ReportPayload buildPayload(String queryKey, LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        return switch (queryKey.toUpperCase(Locale.ROOT)) {
            case "OPERATIONAL" -> buildOperationalPayload(dateFrom, dateTo, branchId);
            case "PROFIT_DISTRIBUTION" -> buildProfitPayload(dateFrom, dateTo);
            case "MANAGEMENT_PL" -> buildManagementPlPayload(dateFrom, dateTo);
            case "TRIAL_BALANCE" -> buildTrialBalancePayload(dateFrom, dateTo, branchId);
            case "LEDGER_PROFIT_LOSS" -> buildLedgerProfitLossPayload(dateFrom, dateTo, branchId);
            case "FINANCING_PORTFOLIO" -> buildFinancingPayload(dateFrom, dateTo);
            case "PAR" -> buildParPayload(dateFrom, dateTo);
            case "SHARIAH_AUDIT" -> buildShariahPayload(dateFrom, dateTo);
            case "BRANCH" -> buildBranchPayload(dateFrom, dateTo, branchId);
            case "KPI" -> buildKpiPayload(dateFrom, dateTo);
            case "GROWTH" -> buildGrowthPayload(dateFrom, dateTo);
            case "LOAN_RECOVERY" -> buildLoanRecoveryPayload(dateFrom, dateTo, branchId);
            case "MONTHLY_CLOSING" -> buildMonthlyClosingPayload(dateFrom, dateTo, branchId);
            default -> throw new BadRequestException("Unsupported report query key: " + queryKey);
        };
    }

    private ReportPayload buildOperationalPayload(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("transactionType", "Transaction Type"),
                new ReportColumnResponse("transactionCount", "Transactions"),
                new ReportColumnResponse("totalAmount", "Total Amount"),
                new ReportColumnResponse("postedCount", "Posted"),
                new ReportColumnResponse("reversedCount", "Reversed")
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        long totalCount = 0;
        long posted = 0;
        long reversed = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Object[] row : reportDataRepository.findOperationalRows(dateFrom, dateTo, branchId)) {
            long rowCount = number(row[1]).longValue();
            BigDecimal amount = decimal(row[2]);
            long postedCount = number(row[3]).longValue();
            long reversedCount = number(row[4]).longValue();

            totalCount += rowCount;
            posted += postedCount;
            reversed += reversedCount;
            totalAmount = totalAmount.add(amount);

            rows.add(mapRow(
                    "transactionType", row[0],
                    "transactionCount", rowCount,
                    "totalAmount", formatAmount(amount),
                    "postedCount", postedCount,
                    "reversedCount", reversedCount
            ));
        }

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Transaction Volume", formatAmount(totalAmount), "primary"),
                new ReportMetricResponse("Total Transactions", String.valueOf(totalCount), "success"),
                new ReportMetricResponse("Posted Entries", String.valueOf(posted), "info"),
                new ReportMetricResponse("Reversed Entries", String.valueOf(reversed), "warning")
        );
        return new ReportPayload(metrics, columns, rows, "Transaction Type Volume", buildChartItems(rows, "transactionType", "transactionCount", false));
    }

    private ReportPayload buildProfitPayload(LocalDate dateFrom, LocalDate dateTo) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("postingStatus", "Posting Status"),
                new ReportColumnResponse("postingCount", "Posting Count"),
                new ReportColumnResponse("profitAmount", "Profit Amount"),
                new ReportColumnResponse("latestPostingDate", "Latest Posting Date")
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        long totalCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Object[] row : reportDataRepository.findProfitDistributionRows(dateFrom, dateTo)) {
            long count = number(row[1]).longValue();
            BigDecimal amount = decimal(row[2]);
            totalCount += count;
            totalAmount = totalAmount.add(amount);
            rows.add(mapRow(
                    "postingStatus", row[0],
                    "postingCount", count,
                    "profitAmount", formatAmount(amount),
                    "latestPostingDate", asString(row[3])
            ));
        }

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Total Profit", formatAmount(totalAmount), "success"),
                new ReportMetricResponse("Posting Records", String.valueOf(totalCount), "primary"),
                new ReportMetricResponse("Period Start", String.valueOf(dateFrom), "info"),
                new ReportMetricResponse("Period End", String.valueOf(dateTo), "warning")
        );
        return new ReportPayload(metrics, columns, rows, "Profit Posting Distribution", buildChartItems(rows, "postingStatus", "postingCount", false));
    }

    private ReportPayload buildManagementPlPayload(LocalDate dateFrom, LocalDate dateTo) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("section", "Section"),
                new ReportColumnResponse("lineItem", "Line Item"),
                new ReportColumnResponse("supportingCount", "Supporting Count"),
                new ReportColumnResponse("amount", "Amount"),
                new ReportColumnResponse("note", "Note")
        );

        BigDecimal realizedFinancingProfit = reportDataRepository.sumRealizedFinancingProfitProxy(dateFrom, dateTo);
        long paidScheduleCount = reportDataRepository.countRealizedFinancingSchedules(dateFrom, dateTo);
        BigDecimal profitDistributed = reportDataRepository.sumProfitAmount(dateFrom, dateTo);
        long profitPostingCount = reportDataRepository.countProfitPostingRecords(dateFrom, dateTo);
        BigDecimal operatingExpenseRecorded = managementExpenseEntryRepository.sumAmount(dateFrom, dateTo);
        long operatingExpenseCount = managementExpenseEntryRepository.countEntries(dateFrom, dateTo);
        BigDecimal netSpreadProxy = realizedFinancingProfit.subtract(profitDistributed);
        BigDecimal netResultAfterRecordedExpense = netSpreadProxy.subtract(operatingExpenseRecorded);
        BigDecimal financingDemandObserved = reportDataRepository.sumFinancingAmount(dateFrom, dateTo);
        long financingApplicationCount = reportDataRepository.countFinancingApplications(dateFrom, dateTo);
        BigDecimal transactionVolumeObserved = reportDataRepository.sumBranchVolume(dateFrom, dateTo);
        long transactionCount = reportDataRepository.countTransactions(dateFrom, dateTo);

        List<Map<String, Object>> rows = List.of(
                mapRow(
                        "section", "INCOME",
                        "lineItem", "Realized Financing Profit Proxy",
                        "supportingCount", paidScheduleCount,
                        "amount", formatAmount(realizedFinancingProfit),
                        "note", "Built from fully paid financing schedules where paid date falls inside the selected period."
                ),
                mapRow(
                        "section", "EXPENSE",
                        "lineItem", "Profit Distributed to Depositors",
                        "supportingCount", profitPostingCount,
                        "amount", formatAmount(profitDistributed),
                        "note", "Built from profit posting records credited to deposit or scheme accounts in the selected period."
                ),
                mapRow(
                        "section", "EXPENSE",
                        "lineItem", "Operating Expense Recorded",
                        "supportingCount", operatingExpenseCount,
                        "amount", formatAmount(operatingExpenseRecorded),
                        "note", "Built from manual operating expense entries recorded in the management expense register."
                ),
                mapRow(
                        "section", "RESULT",
                        "lineItem", "Net Spread Proxy Before Recorded Operating Expense",
                        "supportingCount", 1,
                        "amount", formatAmount(netSpreadProxy),
                        "note", "Realized financing profit proxy minus distributed depositor profit. Salaries, rent, tax, provision and accruals are excluded."
                ),
                mapRow(
                        "section", "RESULT",
                        "lineItem", "Net Management Result After Recorded Operating Expense",
                        "supportingCount", 1,
                        "amount", formatAmount(netResultAfterRecordedExpense),
                        "note", "Net spread proxy further reduced by manually recorded operating expenses. Still excludes accruals, provision and tax."
                ),
                mapRow(
                        "section", "CONTEXT",
                        "lineItem", "Financing Demand Observed",
                        "supportingCount", financingApplicationCount,
                        "amount", formatAmount(financingDemandObserved),
                        "note", "Operational context only. Requested financing volume is not treated as booked income."
                ),
                mapRow(
                        "section", "CONTEXT",
                        "lineItem", "Transaction Volume Observed",
                        "supportingCount", transactionCount,
                        "amount", formatAmount(transactionVolumeObserved),
                        "note", "Operational turnover context from transaction journal. This is not treated as accounting revenue."
                )
        );

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Realized Financing Profit Proxy", formatAmount(realizedFinancingProfit), "success"),
                new ReportMetricResponse("Profit Distributed", formatAmount(profitDistributed), "warning"),
                new ReportMetricResponse("Operating Expense Recorded", formatAmount(operatingExpenseRecorded), "danger"),
                new ReportMetricResponse("Net Result After Recorded Expense", formatAmount(netResultAfterRecordedExpense), netResultAfterRecordedExpense.signum() >= 0 ? "primary" : "danger")
        );

        List<Map<String, Object>> chartRows = rows.stream()
                .filter(row -> {
                    String section = String.valueOf(row.getOrDefault("section", ""));
                    String lineItem = String.valueOf(row.getOrDefault("lineItem", ""));
                    return ("INCOME".equals(section) || "EXPENSE".equals(section))
                            || "Net Management Result After Recorded Operating Expense".equals(lineItem);
                })
                .toList();

        return new ReportPayload(
                metrics,
                columns,
                rows,
                "Management P&L Proxy",
                buildChartItems(chartRows, "lineItem", "amount", true)
        );
    }

    private ReportPayload buildTrialBalancePayload(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        TrialBalanceResponse summary = accountingCoreService.getTrialBalance(dateFrom, dateTo, branchId);
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("accountCode", "Account Code"),
                new ReportColumnResponse("accountName", "Account Name"),
                new ReportColumnResponse("accountType", "Type"),
                new ReportColumnResponse("totalDebit", "Debit"),
                new ReportColumnResponse("totalCredit", "Credit"),
                new ReportColumnResponse("netBalance", "Net Balance")
        );

        List<Map<String, Object>> rows = summary.getRows().stream()
                .map(row -> mapRow(
                        "accountCode", row.getAccountCode(),
                        "accountName", row.getAccountName(),
                        "accountType", row.getAccountType(),
                        "totalDebit", formatAmount(row.getTotalDebit()),
                        "totalCredit", formatAmount(row.getTotalCredit()),
                        "netBalance", formatAmount(row.getNetBalance())
                ))
                .toList();

        BigDecimal controlDifference = summary.getTotalDebit().subtract(summary.getTotalCredit()).abs();
        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Total Debit", formatAmount(summary.getTotalDebit()), "success"),
                new ReportMetricResponse("Total Credit", formatAmount(summary.getTotalCredit()), "primary"),
                new ReportMetricResponse("Ledger Heads", String.valueOf(summary.getRowCount()), "warning"),
                new ReportMetricResponse("Control Difference", formatAmount(controlDifference), controlDifference.signum() == 0 ? "success" : "danger")
        );

        List<Map<String, Object>> chartRows = summary.getRows().stream()
                .limit(8)
                .map(row -> mapRow(
                        "accountCode", row.getAccountCode(),
                        "netBalance", formatAmount(row.getNetBalance().abs())
                ))
                .toList();

        return new ReportPayload(
                metrics,
                columns,
                rows,
                "Largest Ledger Head Balances",
                buildChartItems(chartRows, "accountCode", "netBalance", true)
        );
    }

    private ReportPayload buildLedgerProfitLossPayload(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        ProfitLossResponse summary = accountingCoreService.getProfitLoss(dateFrom, dateTo, branchId);
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("section", "Section"),
                new ReportColumnResponse("accountCode", "Account Code"),
                new ReportColumnResponse("accountName", "Account Name"),
                new ReportColumnResponse("amount", "Amount"),
                new ReportColumnResponse("note", "Note")
        );

        List<Map<String, Object>> rows = new ArrayList<>();
        for (ProfitLossRowResponse row : summary.getIncomeRows()) {
            rows.add(mapRow(
                    "section", "INCOME",
                    "accountCode", row.getAccountCode(),
                    "accountName", row.getAccountName(),
                    "amount", formatAmount(row.getAmount()),
                    "note", "Credit-led income account contributing to the posted ledger result."
            ));
        }
        for (ProfitLossRowResponse row : summary.getExpenseRows()) {
            rows.add(mapRow(
                    "section", "EXPENSE",
                    "accountCode", row.getAccountCode(),
                    "accountName", row.getAccountName(),
                    "amount", formatAmount(row.getAmount()),
                    "note", "Debit-led expense account reducing the posted ledger result."
            ));
        }
        rows.add(mapRow(
                "section", "RESULT",
                "accountCode", "NET",
                "accountName", "Net Profit / Loss",
                "amount", formatAmount(summary.getNetProfit()),
                "note", "Posted ledger income less posted ledger expense inside the selected period."
        ));

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Total Income", formatAmount(summary.getTotalIncome()), "success"),
                new ReportMetricResponse("Total Expense", formatAmount(summary.getTotalExpense()), "warning"),
                new ReportMetricResponse("Net Profit", formatAmount(summary.getNetProfit()), summary.getNetProfit().signum() >= 0 ? "primary" : "danger"),
                new ReportMetricResponse("Branch Snapshots", String.valueOf(summary.getBranchSummaries().size()), "info")
        );

        List<Map<String, Object>> chartRows = summary.getBranchSummaries().stream()
                .limit(8)
                .map(row -> mapRow(
                        "branchName", row.getBranchName() == null ? "Head Office / Unassigned" : row.getBranchName(),
                        "netProfit", formatAmount(row.getNetProfit().abs())
                ))
                .toList();

        return new ReportPayload(
                metrics,
                columns,
                rows,
                "Branch Net Profit Distribution",
                buildChartItems(chartRows, "branchName", "netProfit", true)
        );
    }

    private ReportPayload buildFinancingPayload(LocalDate dateFrom, LocalDate dateTo) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("applicationStatus", "Application Status"),
                new ReportColumnResponse("applicationCount", "Applications"),
                new ReportColumnResponse("requestedAmount", "Requested Amount"),
                new ReportColumnResponse("approvedCount", "Approved Count")
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        long totalCount = 0;
        long approvedCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Object[] row : reportDataRepository.findFinancingPortfolioRows(dateFrom, dateTo)) {
            long count = number(row[1]).longValue();
            BigDecimal amount = decimal(row[2]);
            long approved = number(row[3]).longValue();
            totalCount += count;
            approvedCount += approved;
            totalAmount = totalAmount.add(amount);
            rows.add(mapRow(
                    "applicationStatus", row[0],
                    "applicationCount", count,
                    "requestedAmount", formatAmount(amount),
                    "approvedCount", approved
            ));
        }

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Portfolio Amount", formatAmount(totalAmount), "primary"),
                new ReportMetricResponse("Applications", String.valueOf(totalCount), "success"),
                new ReportMetricResponse("Approved", String.valueOf(approvedCount), "info"),
                new ReportMetricResponse("Approval Rate", percentage(approvedCount, totalCount), "warning")
        );
        return new ReportPayload(metrics, columns, rows, "Application Status Mix", buildChartItems(rows, "applicationStatus", "applicationCount", false));
    }

    private ReportPayload buildParPayload(LocalDate dateFrom, LocalDate dateTo) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("productName", "Financing Product"),
                new ReportColumnResponse("scheduleCount", "Schedules"),
                new ReportColumnResponse("outstandingAmount", "Outstanding Amount"),
                new ReportColumnResponse("overdueCount", "Overdue Count")
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        long totalSchedules = 0;
        long totalOverdue = 0;
        BigDecimal totalOutstanding = BigDecimal.ZERO;

        for (Object[] row : reportDataRepository.findParRows(dateFrom, dateTo)) {
            long count = number(row[1]).longValue();
            BigDecimal outstanding = decimal(row[2]);
            long overdue = number(row[3]).longValue();
            totalSchedules += count;
            totalOverdue += overdue;
            totalOutstanding = totalOutstanding.add(outstanding);
            rows.add(mapRow(
                    "productName", row[0],
                    "scheduleCount", count,
                    "outstandingAmount", formatAmount(outstanding),
                    "overdueCount", overdue
            ));
        }

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Outstanding PAR", formatAmount(totalOutstanding), "danger"),
                new ReportMetricResponse("Schedules Covered", String.valueOf(totalSchedules), "primary"),
                new ReportMetricResponse("Overdue Items", String.valueOf(totalOverdue), "warning"),
                new ReportMetricResponse("Overdue Ratio", percentage(totalOverdue, totalSchedules), "info")
        );
        return new ReportPayload(metrics, columns, rows, "Outstanding by Product", buildChartItems(rows, "productName", "outstandingAmount", true));
    }

    private ReportPayload buildShariahPayload(LocalDate dateFrom, LocalDate dateTo) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("referenceModule", "Reference Module"),
                new ReportColumnResponse("caseStatus", "Case Status"),
                new ReportColumnResponse("caseCount", "Case Count"),
                new ReportColumnResponse("latestSubmittedAt", "Latest Submitted At")
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        long totalCases = 0;

        for (Object[] row : reportDataRepository.findShariahAuditRows(dateFrom, dateTo)) {
            long count = number(row[2]).longValue();
            totalCases += count;
            rows.add(mapRow(
                    "referenceModule", row[0],
                    "caseStatus", row[1],
                    "caseCount", count,
                    "latestSubmittedAt", asString(row[3])
            ));
        }

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Audit Cases", String.valueOf(totalCases), "primary"),
                new ReportMetricResponse("Sensitive Access", "Restricted", "warning"),
                new ReportMetricResponse("Date From", String.valueOf(dateFrom), "info"),
                new ReportMetricResponse("Date To", String.valueOf(dateTo), "success")
        );
        return new ReportPayload(metrics, columns, rows, "Shariah Case Distribution", buildChartItems(rows, "referenceModule", "caseCount", false));
    }

    private ReportPayload buildBranchPayload(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("branchCode", "Branch Code"),
                new ReportColumnResponse("branchName", "Branch Name"),
                new ReportColumnResponse("transactionCount", "Transactions"),
                new ReportColumnResponse("transactionAmount", "Transaction Amount"),
                new ReportColumnResponse("selfServiceCount", "ATM/CDM Count")
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        long totalTransactions = 0;
        long selfService = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Object[] row : reportDataRepository.findBranchRows(dateFrom, dateTo, branchId)) {
            long count = number(row[2]).longValue();
            BigDecimal amount = decimal(row[3]);
            long selfServiceCount = number(row[4]).longValue();
            totalTransactions += count;
            selfService += selfServiceCount;
            totalAmount = totalAmount.add(amount);
            rows.add(mapRow(
                    "branchCode", row[0],
                    "branchName", row[1],
                    "transactionCount", count,
                    "transactionAmount", formatAmount(amount),
                    "selfServiceCount", selfServiceCount
            ));
        }

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Branch Volume", formatAmount(totalAmount), "primary"),
                new ReportMetricResponse("Transactions", String.valueOf(totalTransactions), "success"),
                new ReportMetricResponse("Self Service Txn", String.valueOf(selfService), "info"),
                new ReportMetricResponse("Range", dateFrom + " to " + dateTo, "warning")
        );
        return new ReportPayload(metrics, columns, rows, "Branch Transaction Volume", buildChartItems(rows, "branchCode", "transactionAmount", true));
    }

    private ReportPayload buildKpiPayload(LocalDate dateFrom, LocalDate dateTo) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("metricGroup", "Metric Group"),
                new ReportColumnResponse("periodCount", "Period Count"),
                new ReportColumnResponse("activeCount", "Active Count"),
                new ReportColumnResponse("flaggedCount", "Flagged Count")
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        long totalPeriod = 0;
        long totalActive = 0;
        long totalFlagged = 0;

        for (Object[] row : reportDataRepository.findKpiRows(dateFrom, dateTo)) {
            long periodCount = number(row[1]).longValue();
            long activeCount = number(row[2]).longValue();
            long flaggedCount = number(row[3]).longValue();
            totalPeriod += periodCount;
            totalActive += activeCount;
            totalFlagged += flaggedCount;
            rows.add(mapRow(
                    "metricGroup", row[0],
                    "periodCount", periodCount,
                    "activeCount", activeCount,
                    "flaggedCount", flaggedCount
            ));
        }

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Period Activity", String.valueOf(totalPeriod), "primary"),
                new ReportMetricResponse("Active Base", String.valueOf(totalActive), "success"),
                new ReportMetricResponse("Flagged Base", String.valueOf(totalFlagged), "warning"),
                new ReportMetricResponse("Risk Ratio", percentage(totalFlagged, Math.max(totalActive + totalFlagged, 1)), "info")
        );
        return new ReportPayload(metrics, columns, rows, "KPI Group Activity", buildChartItems(rows, "metricGroup", "periodCount", false));
    }

    private ReportPayload buildGrowthPayload(LocalDate dateFrom, LocalDate dateTo) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("periodLabel", "Period"),
                new ReportColumnResponse("customerGrowth", "New Customers"),
                new ReportColumnResponse("accountGrowth", "New Accounts"),
                new ReportColumnResponse("financingGrowth", "New Financing")
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        long totalCustomers = 0;
        long totalAccounts = 0;
        long totalFinancing = 0;

        for (Object[] row : reportDataRepository.findGrowthRows(dateFrom, dateTo)) {
            long customerCount = number(row[1]).longValue();
            long accountCount = number(row[2]).longValue();
            long financingCount = number(row[3]).longValue();
            totalCustomers += customerCount;
            totalAccounts += accountCount;
            totalFinancing += financingCount;
            rows.add(mapRow(
                    "periodLabel", row[0],
                    "customerGrowth", customerCount,
                    "accountGrowth", accountCount,
                    "financingGrowth", financingCount
            ));
        }

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("New Customers", String.valueOf(totalCustomers), "success"),
                new ReportMetricResponse("New Accounts", String.valueOf(totalAccounts), "primary"),
                new ReportMetricResponse("New Financing", String.valueOf(totalFinancing), "info"),
                new ReportMetricResponse("Growth Mix", percentage(totalFinancing, Math.max(totalCustomers + totalAccounts + totalFinancing, 1)), "warning")
        );
        return new ReportPayload(metrics, columns, rows, "Customer Growth Trend", buildChartItems(rows, "periodLabel", "customerGrowth", false));
    }

    private ReportPayload buildLoanRecoveryPayload(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("productName", "Financing Product"),
                new ReportColumnResponse("applicationCount", "Applications"),
                new ReportColumnResponse("recoveredAmount", "Recovered Amount"),
                new ReportColumnResponse("overdueAmount", "Overdue Amount")
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        long totalApplications = 0;
        BigDecimal totalRecovered = BigDecimal.ZERO;
        BigDecimal totalOverdue = BigDecimal.ZERO;

        for (Object[] row : reportDataRepository.findLoanRecoveryRows(dateFrom, dateTo, branchId)) {
            long applicationCount = number(row[1]).longValue();
            BigDecimal recoveredAmount = decimal(row[2]);
            BigDecimal overdueAmount = decimal(row[3]);
            totalApplications += applicationCount;
            totalRecovered = totalRecovered.add(recoveredAmount);
            totalOverdue = totalOverdue.add(overdueAmount);
            rows.add(mapRow(
                    "productName", row[0],
                    "applicationCount", applicationCount,
                    "recoveredAmount", formatAmount(recoveredAmount),
                    "overdueAmount", formatAmount(overdueAmount)
            ));
        }

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Recovered Amount", formatAmount(totalRecovered), "success"),
                new ReportMetricResponse("Overdue Exposure", formatAmount(totalOverdue), "warning"),
                new ReportMetricResponse("Applications", String.valueOf(totalApplications), "primary"),
                new ReportMetricResponse("Recovery Coverage", totalOverdue.compareTo(BigDecimal.ZERO) <= 0 ? "100.00%" :
                        percentage(totalRecovered.longValue(), Math.max(totalRecovered.add(totalOverdue).longValue(), 1L)), "info")
        );
        return new ReportPayload(metrics, columns, rows, "Loan Recovery by Product", buildChartItems(rows, "productName", "overdueAmount", true));
    }

    private ReportPayload buildMonthlyClosingPayload(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("branchCode", "Branch Code"),
                new ReportColumnResponse("branchName", "Branch Name"),
                new ReportColumnResponse("transactionAmount", "Transaction Amount"),
                new ReportColumnResponse("reversedCount", "Reversed Count"),
                new ReportColumnResponse("latestVaultClosingBalance", "Vault Closing"),
                new ReportColumnResponse("profitPosted", "Profit Posted")
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        BigDecimal totalTransactionAmount = BigDecimal.ZERO;
        BigDecimal totalVaultClosing = BigDecimal.ZERO;
        BigDecimal totalProfitPosted = BigDecimal.ZERO;
        long totalReversed = 0;

        for (Object[] row : reportDataRepository.findMonthlyClosingRows(dateFrom, dateTo, branchId)) {
            BigDecimal transactionAmount = decimal(row[2]);
            long reversedCount = number(row[3]).longValue();
            BigDecimal vaultClosing = decimal(row[4]);
            BigDecimal profitPosted = decimal(row[5]);
            totalTransactionAmount = totalTransactionAmount.add(transactionAmount);
            totalVaultClosing = totalVaultClosing.add(vaultClosing);
            totalProfitPosted = totalProfitPosted.add(profitPosted);
            totalReversed += reversedCount;
            rows.add(mapRow(
                    "branchCode", row[0],
                    "branchName", row[1],
                    "transactionAmount", formatAmount(transactionAmount),
                    "reversedCount", reversedCount,
                    "latestVaultClosingBalance", formatAmount(vaultClosing),
                    "profitPosted", formatAmount(profitPosted)
            ));
        }

        List<ReportMetricResponse> metrics = List.of(
                new ReportMetricResponse("Txn Volume", formatAmount(totalTransactionAmount), "primary"),
                new ReportMetricResponse("Vault Closing", formatAmount(totalVaultClosing), "success"),
                new ReportMetricResponse("Profit Posted", formatAmount(totalProfitPosted), "info"),
                new ReportMetricResponse("Reversed Count", String.valueOf(totalReversed), "warning")
        );
        return new ReportPayload(metrics, columns, rows, "Monthly Closing Branch Volume", buildChartItems(rows, "branchCode", "transactionAmount", true));
    }

    private FileReference generateReportFile(ReportRequestLog log, ReportDefinition definition, ReportPayload payload, String exportType) {
        try {
            Files.createDirectories(GENERATED_DIR);
            boolean csv = EXPORT_CSV.equals(exportType);
            boolean pdf = EXPORT_PDF.equals(exportType);
            boolean excel = EXPORT_EXCEL.equals(exportType);
            String extension = csv ? ".csv" : (pdf ? ".pdf" : (excel ? ".xlsx" : ".html"));
            String fileName = definition.getQueryKey().toLowerCase(Locale.ROOT) + "-" + log.getId() + extension;
            Path filePath = GENERATED_DIR.resolve(fileName);
            String htmlContent = buildHtml(definition, log, payload);
            if (csv) {
                Files.writeString(filePath, buildCsv(payload), StandardCharsets.UTF_8);
            } else if (pdf) {
                try {
                    Files.write(filePath, pdfDocumentService.renderPdf(htmlContent));
                } catch (RuntimeException ex) {
                    pdf = false;
                    extension = ".html";
                    fileName = definition.getQueryKey().toLowerCase(Locale.ROOT) + "-" + log.getId() + extension;
                    filePath = GENERATED_DIR.resolve(fileName);
                    Files.writeString(filePath, htmlContent, StandardCharsets.UTF_8);
                }
            } else if (excel) {
                Files.write(filePath, excelDocumentService.buildReportWorkbook(
                        definition.getReportName(),
                        String.valueOf(log.getDateFrom()),
                        String.valueOf(log.getDateTo()),
                        log.getRequestedBy(),
                        payload.metrics(),
                        payload.columns(),
                        payload.rows()
                ));
            } else {
                Files.writeString(filePath, htmlContent, StandardCharsets.UTF_8);
            }

            FileReference fileReference = new FileReference();
            fileReference.setFileName(fileName);
            fileReference.setOriginalFileName(fileName);
            fileReference.setFilePath(filePath.toString());
            fileReference.setFileType(
                    csv ? "text/csv" :
                            (pdf ? MediaType.APPLICATION_PDF_VALUE :
                                    (excel ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "text/html"))
            );
            fileReference.setFileSize(Files.size(filePath));
            fileReference.setModuleName("REPORTS");
            fileReference.setReferenceTable("report_request_log");
            fileReference.setReferenceId(log.getId());
            fileReference.setStatus(RecordStatus.ACTIVE);
            return fileReferenceRepository.save(fileReference);
        } catch (IOException ex) {
            throw new BadRequestException("Failed to generate report file: " + ex.getMessage());
        }
    }

    private void publishReportReady(ReportDefinition definition, String requestedBy, String exportType) {
        String targetUsername = SYSTEM_USER.equalsIgnoreCase(resolveUser(requestedBy)) ? null : resolveUser(requestedBy);
        String requiredPermission = targetUsername == null ? "REPORTING_REGULATORY_ACCESS" : null;
        liveUpdateGateway.publish(
                "REPORT",
                "Report Ready",
                definition.getReportName() + " is ready as " + (exportType == null ? "HTML" : exportType) + ".",
                "SUCCESS",
                "/reports/export-history",
                targetUsername,
                null,
                requiredPermission
        );
        if (targetUsername != null) {
            CompletableFuture.runAsync(() -> userRepository.findByUsername(targetUsername)
                    .filter(user -> user.getEmail() != null && !user.getEmail().trim().isEmpty())
                    .ifPresent(user -> automatedMailService.sendReportReadyEmail(
                            user.getEmail(),
                            definition.getReportName(),
                            exportType == null ? "HTML" : exportType
                    )));
        }
    }

    private String buildCsv(ReportPayload payload) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(",", payload.columns().stream().map(ReportColumnResponse::getLabel).toList())).append('\n');
        for (Map<String, Object> row : payload.rows()) {
            List<String> values = new ArrayList<>();
            for (ReportColumnResponse column : payload.columns()) {
                values.add(sanitizeCsv(row.get(column.getKey())));
            }
            builder.append(String.join(",", values)).append('\n');
        }
        return builder.toString();
    }

    private String buildHtml(ReportDefinition definition, ReportRequestLog log, ReportPayload payload) {
        return documentTemplateService.render("report/generic-report", Map.of(
                "definition", definition,
                "log", log,
                "payload", payload,
                "documentStyle", reportDocumentStyle(),
                "logoDataUri", documentBrandingService.getLogoDataUri(),
                "bankName", documentBrandingService.getBankName(),
                "bankTagline", documentBrandingService.getBankTagline(),
                "bankAddress", documentBrandingService.getBankAddress(),
                "bankContact", documentBrandingService.getBankContact()
        ));
    }

    private String reportDocumentStyle() {
        return documentBrandingService.getPremiumDocumentStyle(".doc-accent{background:linear-gradient(90deg,#064e3b 0 38%,#0d9488 38% 72%,#f59e0b 72% 100%);}");
    }

    private String buildFilterJson(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType) {
        return "{\"dateFrom\":\"" + dateFrom + "\",\"dateTo\":\"" + dateTo + "\",\"branchId\":"
                + (branchId == null ? "null" : branchId) + ",\"exportType\":\"" + (normalizeExportType(exportType) == null ? "VIEW" : normalizeExportType(exportType)) + "\"}";
    }

    private void validateDateRange(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null || dateTo == null) {
            throw new BadRequestException("Date from and date to are required");
        }
        if (dateFrom.isAfter(dateTo)) {
            throw new BadRequestException("Date from cannot be after date to");
        }
        if (dateFrom.plusYears(1).isBefore(dateTo)) {
            throw new BadRequestException("Date range cannot exceed 12 months");
        }
    }

    private void validateSensitiveAccess(ReportDefinition definition, String requestedBy) {
        if (definition.getReportType() != ReportType.REGULATORY
                && definition.getReportType() != ReportType.PAR
                && definition.getReportType() != ReportType.SHARIAH_AUDIT) {
            return;
        }
        String user = requestedBy.toLowerCase(Locale.ROOT);
        if (user.contains("customer") || user.contains("guest")) {
            throw new BadRequestException("Sensitive report access is restricted");
        }
    }

    private Long resolveEffectiveBranchId(Long requestedBranchId, String requestedBy, String queryKey) {
        User user = resolveCurrentUser(requestedBy);
        if (user == null || user.getBranchId() == null || canAccessAllBranches(user)) {
            return requestedBranchId;
        }
        if (isBranchScopedReport(queryKey)) {
            if (requestedBranchId != null && !user.getBranchId().equals(requestedBranchId)) {
                throw new BadRequestException("You can only run this report for your assigned branch");
            }
            return user.getBranchId();
        }
        return requestedBranchId;
    }

    private User resolveCurrentUser(String requestedBy) {
        String resolvedUser = resolveUser(requestedBy);
        if (SYSTEM_USER.equalsIgnoreCase(resolvedUser)) {
            return null;
        }
        return userRepository.findByUsername(resolvedUser).orElse(null);
    }

    private boolean canAccessAllBranches(User user) {
        String roleCode = user.getRole() == null || user.getRole().getCode() == null
                ? AopRequestContext.currentRoleCode()
                : user.getRole().getCode();
        return roleCode != null && GLOBAL_REPORTING_ROLES.contains(roleCode.trim().toUpperCase(Locale.ROOT));
    }

    private boolean isBranchScopedReport(String queryKey) {
        return "OPERATIONAL".equalsIgnoreCase(queryKey)
                || "BRANCH".equalsIgnoreCase(queryKey)
                || "MONTHLY_CLOSING".equalsIgnoreCase(queryKey)
                || "LOAN_RECOVERY".equalsIgnoreCase(queryKey);
    }

    private String resolveUser(String requestedBy) {
        return requestedBy == null || requestedBy.trim().isEmpty() ? SYSTEM_USER : requestedBy.trim();
    }

    private LocalDate normalizeDateFrom(LocalDate dateFrom) {
        return dateFrom == null ? LocalDate.now().minusDays(30) : dateFrom;
    }

    private LocalDate normalizeDateTo(LocalDate dateTo) {
        return dateTo == null ? LocalDate.now() : dateTo;
    }

    private String normalizeExportType(String exportType) {
        if (exportType == null || exportType.trim().isEmpty()) {
            return null;
        }
        String normalized = exportType.trim().toUpperCase(Locale.ROOT);
        if (!EXPORT_PDF.equals(normalized) && !EXPORT_CSV.equals(normalized) && !EXPORT_EXCEL.equals(normalized) && !"PRINT".equals(normalized)) {
            throw new BadRequestException("Unsupported export type");
        }
        return normalized;
    }

    private Map<String, Object> mapRow(Object... pairs) {
        Map<String, Object> row = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            row.put(String.valueOf(pairs[i]), pairs[i + 1]);
        }
        return row;
    }

    private ReportRequestLogResponse mapLog(ReportRequestLog entity) {
        return new ReportRequestLogResponse(
                entity.getId(),
                entity.getReport().getId(),
                entity.getReport().getReportCode(),
                entity.getReport().getReportName(),
                entity.getReport().getReportType(),
                entity.getReport().getQueryKey(),
                entity.getRequestedBy(),
                entity.getDateFrom(),
                entity.getDateTo(),
                entity.getFilterJson(),
                mapFile(entity.getGeneratedFile()),
                entity.getRequestStatus(),
                entity.getRequestedAt(),
                entity.getGeneratedAt(),
                entity.getStatus()
        );
    }

    private ReportFileResponse mapFile(FileReference entity) {
        if (entity == null) {
            return null;
        }
        return new ReportFileResponse(
                entity.getId(),
                entity.getFileName(),
                entity.getOriginalFileName(),
                entity.getFilePath(),
                entity.getFileType(),
                entity.getFileSize()
        );
    }

    private BigDecimal decimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        return new BigDecimal(String.valueOf(value));
    }

    private Number number(Object value) {
        return value instanceof Number number ? number : Integer.parseInt(String.valueOf(value));
    }

    private ManagementExpenseEntryResponse mapManagementExpense(ManagementExpenseEntry entity) {
        Branch branch = entity.getBranchId() == null ? null : branchRepository.findById(entity.getBranchId()).orElse(null);
        return new ManagementExpenseEntryResponse(
                entity.getId(),
                entity.getExpenseDate(),
                entity.getBranchId(),
                branch == null ? null : branch.getBranchCode(),
                branch == null ? null : branch.getBranchName(),
                entity.getExpenseCategory(),
                entity.getExpenseCode(),
                entity.getAmount(),
                entity.getSourceType(),
                entity.getReferenceNo(),
                entity.getRemarks(),
                entity.getCreatedBy(),
                entity.getCreatedAt()
        );
    }

    private String formatAmount(BigDecimal amount) {
        return AMOUNT_FORMAT.format(amount == null ? BigDecimal.ZERO : amount);
    }

    private String percentage(long part, long whole) {
        if (whole <= 0) {
            return "0%";
        }
        double value = (part * 100.0d) / whole;
        return String.format(Locale.ENGLISH, "%.2f%%", value);
    }

    private String asString(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private String sanitizeCsv(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    private List<ReportChartItem> buildChartItems(List<Map<String, Object>> rows, String labelKey, String valueKey, boolean decimalValue) {
        List<ReportChartItem> items = new ArrayList<>();
        double max = 0d;
        for (Map<String, Object> row : rows) {
            double value = toChartNumber(row.get(valueKey), decimalValue);
            if (value > max) {
                max = value;
            }
            items.add(new ReportChartItem(String.valueOf(row.getOrDefault(labelKey, "-")), formatChartValue(value, decimalValue), value, 0));
        }
        final double safeMax = max <= 0d ? 1d : max;
        return items.stream()
                .limit(6)
                .map(item -> new ReportChartItem(item.label(), item.displayValue(), item.numericValue(), Math.max(10, (int) Math.round((item.numericValue() / safeMax) * 100d))))
                .toList();
    }

    private double toChartNumber(Object value, boolean decimalValue) {
        if (value == null) {
            return 0d;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        String raw = String.valueOf(value).replace(",", "").replace("%", "").trim();
        if (raw.isEmpty()) {
            return 0d;
        }
        try {
            return decimalValue ? Double.parseDouble(raw) : Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            return 0d;
        }
    }

    private String formatChartValue(double value, boolean decimalValue) {
        if (!decimalValue) {
            return String.valueOf((long) value);
        }
        return AMOUNT_FORMAT.format(value);
    }

    private Long safeLong(Long value) {
        return value == null ? 0L : value;
    }

    private record ReportPayload(
            List<ReportMetricResponse> metrics,
            List<ReportColumnResponse> columns,
            List<Map<String, Object>> rows,
            String chartTitle,
            List<ReportChartItem> chartItems
    ) {}

    private record ReportChartItem(
            String label,
            String displayValue,
            double numericValue,
            int share
    ) {}
}
