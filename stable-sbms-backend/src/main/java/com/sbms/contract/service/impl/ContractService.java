package com.sbms.contract.service.impl;

import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.common.document.PdfDocumentService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.contract.dto.request.ContractGenerateRequest;
import com.sbms.contract.dto.request.ContractSignRequest;
import com.sbms.contract.dto.response.ContractDashboardSummaryResponse;
import com.sbms.contract.dto.response.ContractResponse;
import com.sbms.contract.dto.response.ContractTemplateResponse;
import com.sbms.contract.dto.response.ContractVersionResponse;
import com.sbms.contract.entity.Contract;
import com.sbms.contract.entity.ContractTemplate;
import com.sbms.contract.entity.ContractVersion;
import com.sbms.contract.enums.ContractStatus;
import com.sbms.contract.repository.ContractRepository;
import com.sbms.contract.repository.ContractTemplateRepository;
import com.sbms.contract.repository.ContractVersionRepository;
import com.sbms.contract.service.IContractService;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class ContractService implements IContractService {

    private static final DateTimeFormatter CONTRACT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractTemplateRepository templateRepository;

    @Autowired
    private ContractVersionRepository versionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AutomatedMailService automatedMailService;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @Autowired
    private DocumentBrandingService documentBrandingService;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @Override
    public ContractResponse generate(ContractGenerateRequest request) {
        if (request == null) throw new BadRequestException("Contract generate request is required");
        if (request.getTemplateId() == null) throw new BadRequestException("Template is required");
        if (request.getCustomerId() == null) throw new BadRequestException("Customer is required");
        if (request.getReferenceModule() == null || request.getReferenceModule().trim().isEmpty()) throw new BadRequestException("Reference module is required");
        if (request.getReferenceId() == null) throw new BadRequestException("Reference id is required");

        ContractTemplate template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract template not found"));
        if (template.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived contract template cannot generate contract");
        }

        Customer customer = customerRepository.findActiveById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Contract contract = new Contract();
        contract.setContractNo(generateContractNo());
        contract.setTemplate(template);
        contract.setCustomer(customer);
        contract.setContractType(template.getContractType());
        contract.setReferenceModule(request.getReferenceModule().trim().toUpperCase());
        contract.setReferenceId(request.getReferenceId());
        contract.setContractText(resolveContractText(template, customer, request, contract.getContractNo()));
        contract.setSupportingDocumentName(trim(request.getSupportingDocumentName()));
        contract.setRemarks(trim(request.getRemarks()));
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setStatus(RecordStatus.ACTIVE);

        Contract saved = contractRepository.save(contract);
        createVersion(saved, "GENERATED", resolveActor(request.getGeneratedBy(), "SYSTEM_GENERATOR"), trim(request.getRemarks()));
        sendContractMail(saved, "Generated", trim(request.getRemarks()), "/contracts/" + saved.getId(), "Open Contract");
        return map(saved);
    }

    @Override
    public List<ContractResponse> list(Long templateId, Long customerId, String referenceModule, String keyword) {
        return contractRepository.findAll(templateId, customerId, referenceModule, keyword).stream().map(this::map).toList();
    }

    @Override
    public ContractResponse getById(Long id) {
        return map(getContract(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> previewPrintCopy(Long id) {
        ContractResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildContractPrintHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename("contract-print-copy-" + response.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadPrintCopy(Long id) {
        ContractResponse response = getById(id);
        byte[] pdf = pdfDocumentService.renderPdf(buildContractPrintHtml(response));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("contract-print-copy-" + response.getId() + ".pdf").build().toString())
                .body(pdf);
    }

    @Override
    public ContractResponse customerSign(Long id, ContractSignRequest request) {
        Contract contract = getContract(id);
        if (contract.getContractStatus() == ContractStatus.LOCKED) {
            throw new BadRequestException("Locked contract cannot be edited directly");
        }
        if (contract.getSignedByCustomer() != null) {
            throw new BadRequestException("Customer signature already captured");
        }
        String signer = resolveSigner(request, "CUSTOMER_SIGNER");
        contract.setSignedByCustomer(signer);
        contract.setCustomerSignedAt(LocalDateTime.now());
        contract.setContractStatus(ContractStatus.ACTIVE);
        contract.setRemarks(trim(request == null ? null : request.getRemarks()));
        Contract updated = contractRepository.update(contract);
        createVersion(updated, "CUSTOMER_SIGN", signer, trim(request == null ? null : request.getRemarks()));
        sendContractMail(updated, "Customer signed", trim(request == null ? null : request.getRemarks()), "/contracts/" + updated.getId(), "Open Contract");
        return map(updated);
    }

    @Override
    public ContractResponse shariahSign(Long id, ContractSignRequest request) {
        Contract contract = getContract(id);
        if (contract.getContractStatus() == ContractStatus.LOCKED) {
            throw new BadRequestException("Locked contract cannot be signed again");
        }
        if (contract.getSignedByCustomer() == null) {
            throw new BadRequestException("Customer signature is required before shariah signature");
        }
        if (contract.getSignedByShariah() != null) {
            throw new BadRequestException("Shariah signature already captured");
        }
        String signer = resolveSigner(request, "SHARIAH_SIGNER");
        contract.setSignedByShariah(signer);
        contract.setShariahSignedAt(LocalDateTime.now());
        contract.setSignedDate(LocalDateTime.now());
        contract.setContractStatus(ContractStatus.LOCKED);
        contract.setRemarks(trim(request == null ? null : request.getRemarks()));
        Contract updated = contractRepository.update(contract);
        createVersion(updated, "SHARIAH_SIGN", signer, trim(request == null ? null : request.getRemarks()));
        sendContractMail(updated, "Shariah signed", trim(request == null ? null : request.getRemarks()), "/contracts/" + updated.getId(), "Open Contract");
        return map(updated);
    }

    @Override
    public List<ContractVersionResponse> getVersions(Long id) {
        getContract(id);
        return versionRepository.findByContractId(id).stream().map(this::mapVersion).toList();
    }

    @Override
    public ContractDashboardSummaryResponse dashboardSummary() {
        List<ContractResponse> recentContracts = contractRepository.findLatest(5).stream().map(this::map).toList();
        List<ContractTemplateResponse> recentTemplates = templateRepository.findLatest(5).stream()
                .map(template -> new ContractTemplateResponse(
                        template.getId(),
                        template.getTemplateCode(),
                        template.getTemplateName(),
                        template.getContractType().name(),
                        template.getVersionNo(),
                        template.getTemplateBody(),
                        template.getStatus(),
                        template.getCreatedAt(),
                        template.getUpdatedAt(),
                        contractRepository.findAll(template.getId(), null, null, null).stream().count()
                ))
                .toList();

        return new ContractDashboardSummaryResponse(
                contractRepository.countTotal(),
                contractRepository.countPendingSignatures(),
                contractRepository.countActiveLocked(),
                versionRepository.countAll(),
                contractRepository.countDraft(),
                recentContracts,
                recentTemplates
        );
    }

    private Contract getContract(Long id) {
        if (id == null) throw new BadRequestException("Contract id is required");
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
    }

    private String generateContractNo() {
        String last = contractRepository.findLastContractNo();
        int next = 1;
        if (last != null && last.matches("CTR-\\d+")) next = Integer.parseInt(last.substring(4)) + 1;
        return String.format("CTR-%05d", next);
    }

    private String resolveContractText(ContractTemplate template, Customer customer, ContractGenerateRequest request, String contractNo) {
        String override = trim(request.getContractText());
        if (override != null) {
            return override;
        }
        return template.getTemplateBody()
                .replace("{{contractNo}}", contractNo)
                .replace("{{customerName}}", safe(customer.getFullName()))
                .replace("{{customerCode}}", safe(customer.getCustomerCode()))
                .replace("{{referenceModule}}", safe(request.getReferenceModule()))
                .replace("{{referenceId}}", String.valueOf(request.getReferenceId()))
                .replace("{{contractType}}", template.getContractType().name())
                .replace("{{templateName}}", safe(template.getTemplateName()))
                .replace("{{today}}", LocalDate.now().toString());
    }

    private void createVersion(Contract contract, String changeType, String changedBy, String changeNote) {
        ContractVersion version = new ContractVersion();
        version.setContract(contract);
        version.setVersionNo(versionRepository.findNextVersionNo(contract.getId()));
        version.setContractText(contract.getContractText());
        version.setChangeType(changeType);
        version.setChangedBy(changedBy);
        version.setChangeNote(changeNote);
        version.setStatus(RecordStatus.ACTIVE);
        versionRepository.save(version);
    }

    private ContractResponse map(Contract entity) {
        List<ContractVersionResponse> versions = versionRepository.findByContractId(entity.getId()).stream().map(this::mapVersion).toList();
        return new ContractResponse(
                entity.getId(),
                entity.getContractNo(),
                entity.getContractType().name(),
                entity.getTemplate().getId(),
                entity.getTemplate().getTemplateCode(),
                entity.getTemplate().getTemplateName(),
                entity.getTemplate().getVersionNo(),
                entity.getCustomer().getId(),
                entity.getCustomer().getCustomerCode(),
                entity.getCustomer().getFullName(),
                entity.getReferenceModule(),
                entity.getReferenceId(),
                entity.getContractText(),
                entity.getSupportingDocumentName(),
                entity.getSignedByCustomer(),
                entity.getSignedByShariah(),
                entity.getCustomerSignedAt(),
                entity.getShariahSignedAt(),
                entity.getSignedDate(),
                entity.getContractStatus().name(),
                entity.getRemarks(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                versions
        );
    }

    private ContractVersionResponse mapVersion(ContractVersion entity) {
        return new ContractVersionResponse(
                entity.getId(),
                entity.getContract().getId(),
                entity.getContract().getContractNo(),
                entity.getVersionNo(),
                entity.getContractText(),
                entity.getChangeType(),
                entity.getChangedBy(),
                entity.getChangeNote(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private String resolveSigner(ContractSignRequest request, String fallback) {
        if (request == null || request.getSignedBy() == null || request.getSignedBy().trim().isEmpty()) {
            return fallback;
        }
        return request.getSignedBy().trim();
    }

    private String resolveActor(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private String trim(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void sendContractMail(Contract contract, String decision, String remarks, String routePath, String ctaLabel) {
        if (contract == null || contract.getCustomer() == null || trim(contract.getCustomer().getEmail()) == null) {
            return;
        }
        automatedMailService.sendApprovalDecisionEmail(
                contract.getCustomer().getEmail(),
                "Contract",
                contract.getContractNo(),
                decision,
                remarks,
                routePath,
                ctaLabel
        );
    }

    private String buildContractPrintHtml(ContractResponse contract) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("item", contract);
        model.put("documentStyle", documentBrandingService.getPremiumDocumentStyle(
                ".doc-accent{background:linear-gradient(90deg,#0b5d44,#d4af37,#0f6e4e);}" +
                ".legal-card{margin:18px 30px 0;padding:0 18px 20px;border:1px solid #dfe9e3;border-radius:18px;background:#fffefc;}" +
                ".legal-note{font-size:12px;color:#5b6b63;line-height:1.7;}" +
                ".legal-body{margin-top:16px;padding:20px 22px;border:1px solid #e6ece8;border-radius:16px;background:#ffffff;font-size:14px;line-height:1.9;color:#23352c;white-space:pre-wrap;}" +
                ".approval-ribbon{display:inline-flex;padding:8px 16px;border-radius:999px;background:linear-gradient(90deg,#0a5d43,#0e714f);color:#fff;font-size:12px;font-weight:800;letter-spacing:.08em;text-transform:uppercase;}" +
                ".dual-meta{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:14px;padding:18px 30px 0;}"
        ));
        model.put("logoDataUri", documentBrandingService.getLogoDataUri());
        model.put("bankName", documentBrandingService.getBankName());
        model.put("bankTagline", documentBrandingService.getBankTagline());
        model.put("bankAddress", documentBrandingService.getBankAddress());
        model.put("bankContact", documentBrandingService.getBankContact());
        model.put("issuedDate", LocalDate.now().format(CONTRACT_DATE_FORMATTER));
        return documentTemplateService.render("contract/contract-print-copy", model);
    }
}
