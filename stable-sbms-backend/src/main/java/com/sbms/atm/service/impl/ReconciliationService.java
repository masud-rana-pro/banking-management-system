package com.sbms.atm.service.impl;

import com.sbms.atm.dto.request.ReconciliationRequest;
import com.sbms.atm.dto.response.ReconciliationResponse;
import com.sbms.atm.entity.Terminal;
import com.sbms.atm.entity.TerminalReconciliation;
import com.sbms.atm.enums.ReconciliationStatus;
import com.sbms.atm.repository.ReconciliationRepository;
import com.sbms.atm.service.IReconciliationService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReconciliationService implements IReconciliationService {

    @Autowired
    private ReconciliationRepository repository;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Override
    public ReconciliationResponse create(ReconciliationRequest request) {
        validate(request);

        Terminal terminal = repository.findTerminalById(request.getTerminalId())
                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

        if (!repository.isTerminalActive(terminal)) {
            throw new BadRequestException("Only active terminal can be reconciled");
        }

        BigDecimal varianceAmount = request.getPhysicalAmount().subtract(request.getSystemAmount());

        TerminalReconciliation reconciliation = new TerminalReconciliation();
        reconciliation.setTerminalId(request.getTerminalId());
        reconciliation.setReconDate(request.getReconDate() == null ? LocalDate.now() : request.getReconDate());
        reconciliation.setSystemAmount(request.getSystemAmount());
        reconciliation.setPhysicalAmount(request.getPhysicalAmount());
        reconciliation.setVarianceAmount(varianceAmount);
        reconciliation.setApprovedBy(request.getApprovedBy());
        reconciliation.setRemarks(request.getRemarks());

        if (request.getApprovedBy() != null && request.getApprovedBy() > 0) {
            reconciliation.setApprovedAt(LocalDateTime.now());
            reconciliation.setStatus(ReconciliationStatus.APPROVED);
        } else if (varianceAmount.compareTo(BigDecimal.ZERO) == 0) {
            reconciliation.setStatus(ReconciliationStatus.MATCHED);
        } else {
            reconciliation.setStatus(
                    request.getStatus() == null ? ReconciliationStatus.VARIANCE_FOUND : request.getStatus()
            );
        }

        return mapToResponse(repository.save(reconciliation));
    }

    @Override
    public List<ReconciliationResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ReconciliationResponse getById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Valid reconciliation id is required");
        }

        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Reconciliation record not found"));
    }

    @Override
    public List<ReconciliationResponse> getByTerminal(Long terminalId) {
        if (terminalId == null || terminalId <= 0) {
            throw new BadRequestException("Valid terminal id is required");
        }

        return repository.findByTerminalId(terminalId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewReport(Long id) {
        ReconciliationResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildReportHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("atm-reconciliation-" + response.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadReport(Long id) {
        ReconciliationResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildReportHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("atm-reconciliation-" + response.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    private void validate(ReconciliationRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        if (request.getTerminalId() == null || request.getTerminalId() <= 0) {
            throw new BadRequestException("Terminal is required");
        }

        if (request.getSystemAmount() == null || request.getSystemAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Valid system amount is required");
        }

        if (request.getPhysicalAmount() == null || request.getPhysicalAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Valid physical amount is required");
        }

        if (request.getApprovedBy() != null && request.getApprovedBy() <= 0) {
            throw new BadRequestException("Approved by must be a valid user id");
        }
    }

    private ReconciliationResponse mapToResponse(TerminalReconciliation r) {
        return new ReconciliationResponse(
                r.getId(),
                r.getTerminalId(),
                repository.terminalCode(r.getTerminalId()),
                repository.terminalName(r.getTerminalId()),
                r.getReconDate(),
                r.getSystemAmount(),
                r.getPhysicalAmount(),
                r.getVarianceAmount(),
                r.getApprovedBy(),
                r.getApprovedAt(),
                r.getRemarks(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }

    private String buildReportHtml(ReconciliationResponse response) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("response", response);
        model.put("approvedByDisplay", response.getApprovedBy() == null ? "-" : "USER-" + response.getApprovedBy());
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(".doc-accent{background:linear-gradient(90deg,#0c5d44,#d4af37,#1f6d57);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        return documentTemplateService.render("atm/atm-reconciliation-report", model);
    }
}
