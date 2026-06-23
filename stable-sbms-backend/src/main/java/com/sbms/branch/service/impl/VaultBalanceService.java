package com.sbms.branch.service.impl;

import com.sbms.branch.dto.VaultBalanceRequestDto;
import com.sbms.branch.dto.VaultBalanceResponseDto;
import com.sbms.branch.dto.VaultCloseRequestDto;
import com.sbms.branch.entity.Branch;
import com.sbms.branch.entity.BranchCashLedger;
import com.sbms.branch.entity.VaultBalance;
import com.sbms.branch.repository.BranchCashLedgerRepository;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.branch.repository.VaultBalanceRepository;
import com.sbms.branch.service.IVaultBalanceService;
import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.common.document.PdfDocumentService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
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
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class VaultBalanceService implements IVaultBalanceService {

    private static final DateTimeFormatter REPORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    @Autowired
    private VaultBalanceRepository vaultBalanceRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BranchCashLedgerRepository cashLedgerRepository;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Override
    public VaultBalanceResponseDto open(VaultBalanceRequestDto request) {
        validateOpen(request);

        VaultBalance entity = new VaultBalance();
        entity.setBranchId(request.getBranchId());
        entity.setBalanceDate(request.getBalanceDate());
        entity.setOpeningBalance(request.getOpeningBalance());
        entity.setTotalCashIn(BigDecimal.ZERO);
        entity.setTotalCashOut(BigDecimal.ZERO);
        entity.setClosingBalance(request.getOpeningBalance());
        entity.setIsClosed(false);
        entity.setRemarks(request.getRemarks());
        entity.setStatus("ACTIVE");

        VaultBalance saved = vaultBalanceRepository.save(entity);

        createLedgerEntry(
                saved.getBranchId(),
                saved.getBalanceDate(),
                "DEBIT",
                "VAULT_OPENING",
                "VAULT-" + saved.getId(),
                saved.getOpeningBalance(),
                BigDecimal.ZERO,
                saved.getClosingBalance(),
                "Vault opening balance created"
        );

        return toDto(saved);
    }

    @Override
    public VaultBalanceResponseDto close(Long id, VaultCloseRequestDto request) {
        VaultBalance entity = vaultBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vault balance not found"));

        if (Boolean.TRUE.equals(entity.getIsClosed())) {
            throw new BadRequestException("Vault is already closed");
        }

        BigDecimal totalCashIn = request.getTotalCashIn() == null ? BigDecimal.ZERO : request.getTotalCashIn();
        BigDecimal totalCashOut = request.getTotalCashOut() == null ? BigDecimal.ZERO : request.getTotalCashOut();

        if (totalCashIn.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Total cash in cannot be negative");
        }

        if (totalCashOut.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Total cash out cannot be negative");
        }

        BigDecimal closingBalance = entity.getOpeningBalance()
                .add(totalCashIn)
                .subtract(totalCashOut);

        if (closingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Closing balance cannot be negative");
        }

        entity.setTotalCashIn(totalCashIn);
        entity.setTotalCashOut(totalCashOut);
        entity.setClosingBalance(closingBalance);
        entity.setIsClosed(true);
        entity.setClosedBy(1L);
        entity.setClosedAt(LocalDateTime.now());
        entity.setRemarks(request.getRemarks());
        entity.setStatus("CLOSED");

        VaultBalance saved = vaultBalanceRepository.save(entity);

        createLedgerEntry(
                saved.getBranchId(),
                saved.getBalanceDate(),
                "CREDIT",
                "VAULT_CLOSING",
                "VAULT-" + saved.getId(),
                totalCashIn,
                totalCashOut,
                saved.getClosingBalance(),
                "Vault closed after cash verification"
        );

        return toDto(saved);
    }

    @Override
    public VaultBalanceResponseDto getById(Long id) {
        VaultBalance entity = vaultBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vault balance not found"));

        return toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewReport(Long id) {
        VaultBalanceResponseDto item = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildVaultReportHtml(item));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("vault-balance-report-" + id + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadReport(Long id) {
        VaultBalanceResponseDto item = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildVaultReportHtml(item));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("vault-balance-report-" + id + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    public List<VaultBalanceResponseDto> getAll(Long branchId, String status, Boolean isClosed) {
        return vaultBalanceRepository.findAll(branchId, status, isClosed)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private void validateOpen(VaultBalanceRequestDto request) {
        if (request.getBranchId() == null || request.getBranchId() <= 0) {
            throw new BadRequestException("Valid branch is required");
        }

        branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        if (request.getBalanceDate() == null) {
            throw new BadRequestException("Balance date is required");
        }

        if (request.getOpeningBalance() == null) {
            throw new BadRequestException("Opening balance is required");
        }

        if (request.getOpeningBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Opening balance cannot be negative");
        }

        if (vaultBalanceRepository.existsByBranchAndDate(request.getBranchId(), request.getBalanceDate())) {
            throw new BadRequestException("Vault balance already opened for this branch and date");
        }
    }

    private void createLedgerEntry(
            Long branchId,
            LocalDate ledgerDate,
            String entryType,
            String sourceType,
            String referenceNo,
            BigDecimal debitAmount,
            BigDecimal creditAmount,
            BigDecimal balanceAfter,
            String remarks
    ) {
        BranchCashLedger ledger = new BranchCashLedger();

        ledger.setBranchId(branchId);
        ledger.setLedgerDate(ledgerDate);
        ledger.setEntryType(entryType);
        ledger.setSourceType(sourceType);
        ledger.setReferenceNo(referenceNo);
        ledger.setDebitAmount(debitAmount == null ? BigDecimal.ZERO : debitAmount);
        ledger.setCreditAmount(creditAmount == null ? BigDecimal.ZERO : creditAmount);
        ledger.setBalanceAfter(balanceAfter == null ? BigDecimal.ZERO : balanceAfter);
        ledger.setRemarks(remarks);
        ledger.setCreatedBy(1L);

        cashLedgerRepository.save(ledger);
    }

    private VaultBalanceResponseDto toDto(VaultBalance entity) {
        VaultBalanceResponseDto dto = new VaultBalanceResponseDto();

        dto.setId(entity.getId());
        dto.setBranchId(entity.getBranchId());
        dto.setBalanceDate(entity.getBalanceDate());
        dto.setOpeningBalance(entity.getOpeningBalance());
        dto.setTotalCashIn(entity.getTotalCashIn());
        dto.setTotalCashOut(entity.getTotalCashOut());
        dto.setClosingBalance(entity.getClosingBalance());
        dto.setIsClosed(entity.getIsClosed());
        dto.setClosedBy(entity.getClosedBy());
        dto.setClosedAt(entity.getClosedAt());
        dto.setRemarks(entity.getRemarks());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    private String buildVaultReportHtml(VaultBalanceResponseDto item) {
        Branch branch = item.getBranchId() == null ? null : branchRepository.findById(item.getBranchId()).orElse(null);
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("item", item);
        model.put("branch", branch);
        model.put("issuedDate", LocalDate.now().format(REPORT_DATE_FORMATTER));
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(".doc-accent{background:linear-gradient(90deg,#0a5d43,#d4af37,#0d6f50);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        return documentTemplateService.render("branch/vault-balance-report", model);
    }
}
