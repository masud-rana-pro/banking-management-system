package com.sbms.branch.service.impl;

import com.sbms.branch.dto.BranchCashLedgerResponseDto;
import com.sbms.branch.entity.Branch;
import com.sbms.branch.entity.BranchCashLedger;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.branch.repository.BranchCashLedgerRepository;
import com.sbms.branch.service.IBranchCashLedgerService;
import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.common.document.PdfDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class BranchCashLedgerService implements IBranchCashLedgerService {

    private static final DateTimeFormatter REPORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    @Autowired
    private BranchCashLedgerRepository ledgerRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Override
    public List<BranchCashLedgerResponseDto> getAll(Long branchId, String entryType, String sourceType) {
        return ledgerRepository.findAll(branchId, entryType, sourceType)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewReport(Long branchId, String entryType, String sourceType) {
        byte[] pdf = pdfDocumentService.renderPdf(buildLedgerReportHtml(branchId, entryType, sourceType));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("branch-daily-cash-report.pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadReport(Long branchId, String entryType, String sourceType) {
        byte[] pdf = pdfDocumentService.renderPdf(buildLedgerReportHtml(branchId, entryType, sourceType));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("branch-daily-cash-report.pdf").build().toString())
                .body(pdf);
    }

    private BranchCashLedgerResponseDto toDto(BranchCashLedger entity) {
        BranchCashLedgerResponseDto dto = new BranchCashLedgerResponseDto();

        dto.setId(entity.getId());
        dto.setBranchId(entity.getBranchId());
        dto.setLedgerDate(entity.getLedgerDate());
        dto.setEntryType(entity.getEntryType());
        dto.setSourceType(entity.getSourceType());
        dto.setReferenceNo(entity.getReferenceNo());
        dto.setDebitAmount(entity.getDebitAmount());
        dto.setCreditAmount(entity.getCreditAmount());
        dto.setBalanceAfter(entity.getBalanceAfter());
        dto.setRemarks(entity.getRemarks());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());

        return dto;
    }

    private String buildLedgerReportHtml(Long branchId, String entryType, String sourceType) {
        List<BranchCashLedgerResponseDto> items = getAll(branchId, entryType, sourceType);
        Branch branch = branchId == null ? null : branchRepository.findById(branchId).orElse(null);
        BigDecimal totalDebit = items.stream().map(BranchCashLedgerResponseDto::getDebitAmount).filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = items.stream().map(BranchCashLedgerResponseDto::getCreditAmount).filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal closingBalance = items.isEmpty() ? BigDecimal.ZERO : (items.get(0).getBalanceAfter() == null ? BigDecimal.ZERO : items.get(0).getBalanceAfter());

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("items", items);
        model.put("branch", branch);
        model.put("selectedEntryType", blankOrAll(entryType));
        model.put("selectedSourceType", blankOrAll(sourceType));
        model.put("totalDebit", totalDebit);
        model.put("totalCredit", totalCredit);
        model.put("closingBalance", closingBalance);
        model.put("issuedDate", LocalDate.now().format(REPORT_DATE_FORMATTER));
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(".doc-accent{background:linear-gradient(90deg,#0c5b44,#d4af37,#0d714d);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        return documentTemplateService.render("branch/daily-cash-report", model);
    }

    private String blankOrAll(String value) {
        return value == null || value.trim().isEmpty() ? "ALL" : value.trim();
    }
}
