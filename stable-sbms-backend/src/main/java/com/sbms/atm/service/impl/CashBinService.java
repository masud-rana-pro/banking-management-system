package com.sbms.atm.service.impl;

import com.sbms.atm.dto.request.CashBinRequest;
import com.sbms.atm.dto.response.CashBinResponse;
import com.sbms.atm.entity.Terminal;
import com.sbms.atm.entity.TerminalCashBin;
import com.sbms.atm.enums.CashBinStatus;
import com.sbms.atm.repository.CashBinRepository;
import com.sbms.atm.service.ICashBinService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CashBinService implements ICashBinService {

    @Autowired
    private CashBinRepository repository;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Override
    public CashBinResponse create(CashBinRequest request) {
        validateRequest(request, null);

        TerminalCashBin cashBin = new TerminalCashBin();
        mapToEntity(cashBin, request);

        return mapToResponse(repository.save(cashBin));
    }

    @Override
    public List<CashBinResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CashBinResponse getById(Long id) {
        return mapToResponse(findCashBin(id));
    }

    @Override
    public CashBinResponse update(Long id, CashBinRequest request) {
        validateRequest(request, id);

        TerminalCashBin cashBin = findCashBin(id);
        mapToEntity(cashBin, request);

        return mapToResponse(repository.update(cashBin));
    }

    @Override
    public CashBinResponse archive(Long id) {
        TerminalCashBin cashBin = findCashBin(id);

        if (cashBin.getStatus() == CashBinStatus.ARCHIVED) {
            throw new BadRequestException("Cash bin already archived");
        }

        cashBin.setStatus(CashBinStatus.ARCHIVED);
        return mapToResponse(repository.update(cashBin));
    }

    @Override
    public CashBinResponse restore(Long id) {
        TerminalCashBin cashBin = findCashBin(id);

        if (cashBin.getStatus() != CashBinStatus.ARCHIVED) {
            throw new BadRequestException("Only archived cash bin can be restored");
        }

        cashBin.setStatus(CashBinStatus.ACTIVE);
        return mapToResponse(repository.update(cashBin));
    }

    @Override
    public List<CashBinResponse> getByTerminal(Long terminalId) {
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
    public ResponseEntity<byte[]> previewProfile(Long id) {
        CashBinResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildProfileHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("atm-cash-bin-" + response.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadProfile(Long id) {
        CashBinResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildProfileHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("atm-cash-bin-" + response.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    private void validateRequest(CashBinRequest request, Long updatingId) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        if (request.getTerminalId() == null || request.getTerminalId() <= 0) {
            throw new BadRequestException("Terminal is required");
        }

        Terminal terminal = repository.findTerminalById(request.getTerminalId())
                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

        if (!repository.isTerminalActive(terminal)) {
            throw new BadRequestException("Only active terminal can have cash bin setup");
        }

        if (request.getBinNo() == null || request.getBinNo().trim().isEmpty()) {
            throw new BadRequestException("Bin number is required");
        }

        if (request.getDenomination() == null || request.getDenomination().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Valid denomination is required");
        }

        if (request.getMaxCapacity() == null || request.getMaxCapacity() <= 0) {
            throw new BadRequestException("Max capacity must be greater than zero");
        }

        int currentCount = request.getCurrentCount() == null ? 0 : request.getCurrentCount();

        if (currentCount < 0) {
            throw new BadRequestException("Current count cannot be negative");
        }

        if (currentCount > request.getMaxCapacity()) {
            throw new BadRequestException("Cash bin capacity cannot be exceeded");
        }

        boolean duplicate = updatingId == null
                ? repository.existsByTerminalIdAndBinNo(request.getTerminalId(), request.getBinNo())
                : repository.existsByTerminalIdAndBinNoAndIdNot(request.getTerminalId(), request.getBinNo(), updatingId);

        if (duplicate) {
            throw new BadRequestException("Bin number already exists for this terminal");
        }
    }

    private TerminalCashBin findCashBin(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Valid cash bin id is required");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash bin not found"));
    }

    private void mapToEntity(TerminalCashBin cashBin, CashBinRequest request) {
        int currentCount = request.getCurrentCount() == null ? 0 : request.getCurrentCount();

        cashBin.setTerminalId(request.getTerminalId());
        cashBin.setBinNo(request.getBinNo().trim());
        cashBin.setDenomination(request.getDenomination());
        cashBin.setMaxCapacity(request.getMaxCapacity());
        cashBin.setCurrentCount(currentCount);

        if (request.getStatus() != null) {
            cashBin.setStatus(request.getStatus());
        } else if (currentCount == 0) {
            cashBin.setStatus(CashBinStatus.LOW_CASH);
        } else if (currentCount >= request.getMaxCapacity()) {
            cashBin.setStatus(CashBinStatus.FULL);
        } else {
            cashBin.setStatus(CashBinStatus.ACTIVE);
        }
    }

    private CashBinResponse mapToResponse(TerminalCashBin cashBin) {
        BigDecimal currentAmount = cashBin.getDenomination()
                .multiply(BigDecimal.valueOf(cashBin.getCurrentCount()));

        return new CashBinResponse(
                cashBin.getId(),
                cashBin.getTerminalId(),
                repository.terminalCode(cashBin.getTerminalId()),
                repository.terminalName(cashBin.getTerminalId()),
                cashBin.getBinNo(),
                cashBin.getDenomination(),
                cashBin.getMaxCapacity(),
                cashBin.getCurrentCount(),
                currentAmount,
                cashBin.getStatus(),
                cashBin.getCreatedAt()
        );
    }

    private String buildProfileHtml(CashBinResponse response) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("response", response);
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(".doc-accent{background:linear-gradient(90deg,#0f5b42,#d4af37,#236f59);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        return documentTemplateService.render("atm/atm-cash-bin-profile", model);
    }
}
