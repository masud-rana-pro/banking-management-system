package com.sbms.atm.service.impl;

import com.sbms.atm.dto.request.ReplenishmentRequest;
import com.sbms.atm.dto.response.ReplenishmentResponse;
import com.sbms.atm.entity.Terminal;
import com.sbms.atm.entity.TerminalCashBin;
import com.sbms.atm.entity.TerminalReplenishment;
import com.sbms.atm.enums.CashBinStatus;
import com.sbms.atm.enums.ReplenishmentStatus;
import com.sbms.atm.repository.ReplenishmentRepository;
import com.sbms.atm.service.IReplenishmentService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReplenishmentService implements IReplenishmentService {

    @Autowired
    private ReplenishmentRepository repository;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Override
    public ReplenishmentResponse create(ReplenishmentRequest request) {
        TerminalCashBin cashBin = validateAndGetCashBin(request);

        BigDecimal amountAdded = cashBin.getDenomination()
                .multiply(BigDecimal.valueOf(request.getQuantityAdded()));

        TerminalReplenishment replenishment = new TerminalReplenishment();
        replenishment.setTerminalId(request.getTerminalId());
        replenishment.setReplenishmentDate(
                request.getReplenishmentDate() == null ? LocalDate.now() : request.getReplenishmentDate()
        );
        replenishment.setBinNo(request.getBinNo().trim());
        replenishment.setDenomination(cashBin.getDenomination());
        replenishment.setQuantityAdded(request.getQuantityAdded());
        replenishment.setAmountAdded(amountAdded);
        replenishment.setPerformedBy(request.getPerformedBy());
        replenishment.setRemarks(request.getRemarks());
        replenishment.setStatus(request.getStatus() == null ? ReplenishmentStatus.COMPLETED : request.getStatus());

        int updatedCount = cashBin.getCurrentCount() + request.getQuantityAdded();
        cashBin.setCurrentCount(updatedCount);

        if (updatedCount >= cashBin.getMaxCapacity()) {
            cashBin.setStatus(CashBinStatus.FULL);
        } else if (updatedCount == 0) {
            cashBin.setStatus(CashBinStatus.LOW_CASH);
        } else {
            cashBin.setStatus(CashBinStatus.ACTIVE);
        }

        repository.updateCashBin(cashBin);

        return mapToResponse(repository.save(replenishment));
    }

    @Override
    public List<ReplenishmentResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ReplenishmentResponse getById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Valid replenishment id is required");
        }

        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Replenishment record not found"));
    }

    @Override
    public List<ReplenishmentResponse> getByTerminal(Long terminalId) {
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
        ReplenishmentResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildReportHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("atm-replenishment-" + response.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadReport(Long id) {
        ReplenishmentResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildReportHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("atm-replenishment-" + response.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    private TerminalCashBin validateAndGetCashBin(ReplenishmentRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        if (request.getTerminalId() == null || request.getTerminalId() <= 0) {
            throw new BadRequestException("Terminal is required");
        }

        Terminal terminal = repository.findTerminalById(request.getTerminalId())
                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

        if (!repository.isTerminalActive(terminal)) {
            throw new BadRequestException("Only active terminal can be replenished");
        }

        if (request.getBinNo() == null || request.getBinNo().trim().isEmpty()) {
            throw new BadRequestException("Bin number is required");
        }

        if (request.getQuantityAdded() == null || request.getQuantityAdded() <= 0) {
            throw new BadRequestException("Quantity added must be greater than zero");
        }

        if (request.getPerformedBy() == null || request.getPerformedBy() <= 0) {
            throw new BadRequestException("Performed by user id is required");
        }

        TerminalCashBin cashBin = repository.findCashBin(request.getTerminalId(), request.getBinNo().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Cash bin not found for selected terminal"));

        if (cashBin.getStatus() == CashBinStatus.ARCHIVED || cashBin.getStatus() == CashBinStatus.INACTIVE) {
            throw new BadRequestException("Only active cash bin can be replenished");
        }

        int updatedCount = cashBin.getCurrentCount() + request.getQuantityAdded();

        if (updatedCount > cashBin.getMaxCapacity()) {
            throw new BadRequestException("Cash bin capacity cannot be exceeded");
        }

        return cashBin;
    }

    private ReplenishmentResponse mapToResponse(TerminalReplenishment r) {
        return new ReplenishmentResponse(
                r.getId(),
                r.getTerminalId(),
                repository.terminalCode(r.getTerminalId()),
                repository.terminalName(r.getTerminalId()),
                r.getReplenishmentDate(),
                r.getBinNo(),
                r.getDenomination(),
                r.getQuantityAdded(),
                r.getAmountAdded(),
                r.getPerformedBy(),
                r.getRemarks(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }

    private String buildReportHtml(ReplenishmentResponse response) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("response", response);
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(".doc-accent{background:linear-gradient(90deg,#0d5f45,#d4af37,#17775d);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        return documentTemplateService.render("atm/atm-replenishment-report", model);
    }
}
