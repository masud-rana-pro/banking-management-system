package com.sbms.atm.service.impl;

import com.sbms.atm.dto.request.AtmTerminalRequest;
import com.sbms.atm.dto.response.AtmDashboardSummaryResponse;
import com.sbms.atm.dto.response.AtmTerminalDropdownResponse;
import com.sbms.atm.dto.response.AtmTerminalResponse;
import com.sbms.atm.dto.response.DeviceJournalResponse;
import com.sbms.atm.dto.response.ReconciliationResponse;
import com.sbms.atm.dto.response.ReplenishmentResponse;
import com.sbms.atm.entity.TerminalCashBin;
import com.sbms.atm.entity.TerminalReconciliation;
import com.sbms.atm.entity.TerminalReplenishment;
import com.sbms.atm.enums.CashBinStatus;
import com.sbms.atm.entity.Terminal;
import com.sbms.atm.enums.TerminalStatus;
import com.sbms.atm.repository.AtmTerminalRepository;
import com.sbms.atm.service.IAtmTerminalService;
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
import java.util.stream.Stream;

@Service
@Transactional
public class AtmTerminalService implements IAtmTerminalService {

    @Autowired
    private AtmTerminalRepository repository;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Override
    public AtmTerminalResponse create(AtmTerminalRequest request) {
        validateRequest(request);

        if (repository.existsByTerminalCode(request.getTerminalCode())) {
            throw new BadRequestException("Terminal code already exists");
        }

        Terminal terminal = new Terminal();
        mapToEntity(terminal, request);

        return mapToResponse(repository.save(terminal));
    }

    @Override
    public List<AtmTerminalResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AtmTerminalResponse getById(Long id) {
        Terminal terminal = findTerminal(id);
        return mapToResponse(terminal);
    }

    @Override
    public AtmTerminalResponse update(Long id, AtmTerminalRequest request) {
        validateRequest(request);

        Terminal terminal = findTerminal(id);

        if (repository.existsByTerminalCodeAndIdNot(request.getTerminalCode(), id)) {
            throw new BadRequestException("Terminal code already exists");
        }

        mapToEntity(terminal, request);

        return mapToResponse(repository.update(terminal));
    }

    @Override
    public AtmTerminalResponse archive(Long id) {
        Terminal terminal = findTerminal(id);

        if (terminal.getStatus() == TerminalStatus.ARCHIVED) {
            throw new BadRequestException("Terminal already archived");
        }

        terminal.setStatus(TerminalStatus.ARCHIVED);
        return mapToResponse(repository.update(terminal));
    }

    @Override
    public AtmTerminalResponse restore(Long id) {
        Terminal terminal = findTerminal(id);

        if (terminal.getStatus() != TerminalStatus.ARCHIVED) {
            throw new BadRequestException("Only archived terminal can be restored");
        }

        terminal.setStatus(TerminalStatus.ACTIVE);
        return mapToResponse(repository.update(terminal));
    }

    @Override
    public List<AtmTerminalDropdownResponse> dropdown() {
        return repository.findDropdownActive()
                .stream()
                .map(t -> new AtmTerminalDropdownResponse(
                        t.getId(),
                        t.getTerminalCode(),
                        t.getTerminalName()
                ))
                .toList();
    }

    @Override
    public AtmDashboardSummaryResponse dashboardSummary() {
        List<Terminal> terminals = repository.findAll();
        List<TerminalReconciliation> reconciliations = repository.findAllReconciliations();
        AtmTerminalRepository.BigDecimalWrapper todayVolume = repository.todayReplenishment(
                new AtmTerminalRepository.LocalDateWrapper(LocalDate.now())
        );

        long unreconciledTerminals = terminals.stream()
                .filter(t -> t.getStatus() != TerminalStatus.ARCHIVED)
                .filter(t -> {
                    TerminalReconciliation latest = reconciliations.stream()
                            .filter(r -> r.getTerminalId().equals(t.getId()))
                            .findFirst()
                            .orElse(null);

                    return latest == null || latest.getVarianceAmount().compareTo(BigDecimal.ZERO) != 0;
                })
                .count();

        AtmDashboardSummaryResponse response = new AtmDashboardSummaryResponse();
        response.setTotalTerminals(terminals.size());
        response.setActiveTerminals(repository.countByStatus(TerminalStatus.ACTIVE));
        response.setLowCashAlerts(repository.countCashBinsByStatus(CashBinStatus.LOW_CASH));
        response.setUnreconciledTerminals(unreconciledTerminals);
        response.setDowntimeTerminals(
                repository.findByStatuses(List.of(TerminalStatus.MAINTENANCE, TerminalStatus.OUT_OF_SERVICE)).size()
        );
        response.setTodayVolumeCount(todayVolume.count() == null ? 0L : todayVolume.count());
        response.setTodayVolumeAmount(todayVolume.amount() == null ? BigDecimal.ZERO : todayVolume.amount());
        return response;
    }

    @Override
    public List<DeviceJournalResponse> deviceJournal(Long terminalId) {
        List<Terminal> terminals = repository.findAll();
        List<TerminalCashBin> cashBins = repository.findAllCashBins();
        List<TerminalReplenishment> replenishments = repository.findAllReplenishments();
        List<TerminalReconciliation> reconciliations = repository.findAllReconciliations();

        Stream<DeviceJournalResponse> terminalEvents = terminals.stream()
                .filter(t -> terminalId == null || t.getId().equals(terminalId))
                .map(t -> new DeviceJournalResponse(
                        t.getId(),
                        t.getTerminalCode(),
                        t.getTerminalName(),
                        "TERMINAL_REGISTERED",
                        "TERM-" + t.getId(),
                        BigDecimal.ZERO,
                        t.getStatus().name(),
                        t.getLocationNote(),
                        t.getCreatedAt()
                ));

        Stream<DeviceJournalResponse> cashBinEvents = cashBins.stream()
                .filter(c -> terminalId == null || c.getTerminalId().equals(terminalId))
                .map(c -> new DeviceJournalResponse(
                        c.getTerminalId(),
                        repository.findById(c.getTerminalId()).map(Terminal::getTerminalCode).orElse("-"),
                        repository.findById(c.getTerminalId()).map(Terminal::getTerminalName).orElse("-"),
                        "CASH_BIN_CONFIGURED",
                        "BIN-" + c.getId(),
                        c.getDenomination().multiply(BigDecimal.valueOf(c.getCurrentCount())),
                        c.getStatus().name(),
                        "Bin " + c.getBinNo() + " configured",
                        c.getCreatedAt()
                ));

        Stream<DeviceJournalResponse> replenishmentEvents = replenishments.stream()
                .filter(r -> terminalId == null || r.getTerminalId().equals(terminalId))
                .map(r -> new DeviceJournalResponse(
                        r.getTerminalId(),
                        repository.findById(r.getTerminalId()).map(Terminal::getTerminalCode).orElse("-"),
                        repository.findById(r.getTerminalId()).map(Terminal::getTerminalName).orElse("-"),
                        "REPLENISHMENT",
                        "REP-" + r.getId(),
                        r.getAmountAdded(),
                        r.getStatus().name(),
                        r.getRemarks(),
                        r.getCreatedAt()
                ));

        Stream<DeviceJournalResponse> reconciliationEvents = reconciliations.stream()
                .filter(r -> terminalId == null || r.getTerminalId().equals(terminalId))
                .map(r -> new DeviceJournalResponse(
                        r.getTerminalId(),
                        repository.findById(r.getTerminalId()).map(Terminal::getTerminalCode).orElse("-"),
                        repository.findById(r.getTerminalId()).map(Terminal::getTerminalName).orElse("-"),
                        "RECONCILIATION",
                        "REC-" + r.getId(),
                        r.getVarianceAmount(),
                        r.getStatus().name(),
                        r.getRemarks(),
                        r.getCreatedAt()
                ));

        return Stream.of(terminalEvents, cashBinEvents, replenishmentEvents, reconciliationEvents)
                .flatMap(stream -> stream)
                .sorted((left, right) -> right.getEventDate().compareTo(left.getEventDate()))
                .limit(100)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewProfile(Long id) {
        AtmTerminalResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildProfileHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("atm-terminal-" + response.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadProfile(Long id) {
        AtmTerminalResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildProfileHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("atm-terminal-" + response.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    private Terminal findTerminal(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Valid terminal id is required");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));
    }

    private void validateRequest(AtmTerminalRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        if (request.getTerminalCode() == null || request.getTerminalCode().trim().isEmpty()) {
            throw new BadRequestException("Terminal code is required");
        }

        if (request.getTerminalName() == null || request.getTerminalName().trim().isEmpty()) {
            throw new BadRequestException("Terminal name is required");
        }

        if (request.getTerminalType() == null) {
            throw new BadRequestException("Terminal type is required");
        }

        if (request.getBranchId() == null || request.getBranchId() <= 0) {
            throw new BadRequestException("Branch mapping is required");
        }

        if (!repository.branchExists(request.getBranchId())) {
            throw new ResourceNotFoundException("Selected branch not found");
        }
    }

    private void mapToEntity(Terminal terminal, AtmTerminalRequest request) {
        terminal.setTerminalCode(request.getTerminalCode().trim());
        terminal.setTerminalName(request.getTerminalName().trim());
        terminal.setTerminalType(request.getTerminalType());
        terminal.setBranchId(request.getBranchId());
        terminal.setLocationNote(request.getLocationNote());
        terminal.setIpAddress(request.getIpAddress());
        terminal.setSerialNo(request.getSerialNo());
        terminal.setVendorName(request.getVendorName());
        terminal.setInstallDate(request.getInstallDate());

        if (request.getStatus() == null) {
            terminal.setStatus(TerminalStatus.ACTIVE);
        } else {
            terminal.setStatus(request.getStatus());
        }
    }

    private AtmTerminalResponse mapToResponse(Terminal terminal) {
        return new AtmTerminalResponse(
                terminal.getId(),
                terminal.getTerminalCode(),
                terminal.getTerminalName(),
                terminal.getTerminalType(),
                terminal.getBranchId(),
                terminal.getLocationNote(),
                terminal.getIpAddress(),
                terminal.getSerialNo(),
                terminal.getVendorName(),
                terminal.getInstallDate(),
                terminal.getStatus(),
                terminal.getCreatedAt(),
                terminal.getUpdatedAt()
        );
    }

    private String buildProfileHtml(AtmTerminalResponse response) {
        List<com.sbms.atm.dto.response.CashBinResponse> cashBins = repository.findAllCashBins()
                .stream()
                .filter(item -> item.getTerminalId().equals(response.getId()))
                .map(item -> new com.sbms.atm.dto.response.CashBinResponse(
                        item.getId(),
                        item.getTerminalId(),
                        repository.findById(item.getTerminalId()).map(Terminal::getTerminalCode).orElse("-"),
                        repository.findById(item.getTerminalId()).map(Terminal::getTerminalName).orElse("-"),
                        item.getBinNo(),
                        item.getDenomination(),
                        item.getMaxCapacity(),
                        item.getCurrentCount(),
                        item.getDenomination().multiply(BigDecimal.valueOf(item.getCurrentCount())),
                        item.getStatus(),
                        item.getCreatedAt()
                ))
                .toList();
        List<ReplenishmentResponse> replenishments = repository.findAllReplenishments()
                .stream()
                .filter(item -> item.getTerminalId().equals(response.getId()))
                .map(item -> new ReplenishmentResponse(
                        item.getId(),
                        item.getTerminalId(),
                        repository.findById(item.getTerminalId()).map(Terminal::getTerminalCode).orElse("-"),
                        repository.findById(item.getTerminalId()).map(Terminal::getTerminalName).orElse("-"),
                        item.getReplenishmentDate(),
                        item.getBinNo(),
                        item.getDenomination(),
                        item.getQuantityAdded(),
                        item.getAmountAdded(),
                        item.getPerformedBy(),
                        item.getRemarks(),
                        item.getStatus(),
                        item.getCreatedAt()
                ))
                .toList();
        List<ReconciliationResponse> reconciliations = repository.findAllReconciliations()
                .stream()
                .filter(item -> item.getTerminalId().equals(response.getId()))
                .map(item -> new ReconciliationResponse(
                        item.getId(),
                        item.getTerminalId(),
                        repository.findById(item.getTerminalId()).map(Terminal::getTerminalCode).orElse("-"),
                        repository.findById(item.getTerminalId()).map(Terminal::getTerminalName).orElse("-"),
                        item.getReconDate(),
                        item.getSystemAmount(),
                        item.getPhysicalAmount(),
                        item.getVarianceAmount(),
                        item.getApprovedBy(),
                        item.getApprovedAt(),
                        item.getRemarks(),
                        item.getStatus(),
                        item.getCreatedAt()
                ))
                .toList();

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("response", response);
        model.put("cashBins", cashBins);
        model.put("replenishments", replenishments);
        model.put("reconciliations", reconciliations);
        model.put("journal", deviceJournal(response.getId()).stream().limit(8).toList());
        model.put("latestReplenishment", replenishments.isEmpty() ? null : replenishments.get(0));
        model.put("latestReconciliation", reconciliations.isEmpty() ? null : reconciliations.get(0));
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(".doc-accent{background:linear-gradient(90deg,#0d5c44,#d4af37,#15735a);}"));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        return documentTemplateService.render("atm/atm-terminal-profile", model);
    }
}
