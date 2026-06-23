package com.sbms.contract.service.impl;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.contract.dto.request.ContractTemplateRequest;
import com.sbms.contract.dto.response.ContractTemplateResponse;
import com.sbms.contract.entity.ContractTemplate;
import com.sbms.contract.repository.ContractRepository;
import com.sbms.contract.repository.ContractTemplateRepository;
import com.sbms.contract.service.IContractTemplateService;
import com.sbms.customer.enums.RecordStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContractTemplateService implements IContractTemplateService {

    @Autowired
    private ContractTemplateRepository templateRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Override
    public ContractTemplateResponse create(ContractTemplateRequest request) {
        validate(request, null);
        ContractTemplate entity = new ContractTemplate();
        entity.setTemplateCode(resolveTemplateCode(request.getTemplateCode()));
        apply(entity, request);
        entity.setStatus(RecordStatus.ACTIVE);
        return map(templateRepository.save(entity));
    }

    @Override
    public List<ContractTemplateResponse> list() {
        return templateRepository.findAll().stream().map(this::map).toList();
    }

    @Override
    public ContractTemplateResponse getById(Long id) {
        return map(getEntity(id));
    }

    @Override
    public ContractTemplateResponse update(Long id, ContractTemplateRequest request) {
        ContractTemplate entity = getEntity(id);
        validate(request, id);
        if (request.getTemplateCode() != null && !request.getTemplateCode().trim().isEmpty()) {
            entity.setTemplateCode(request.getTemplateCode().trim().toUpperCase());
        }
        apply(entity, request);
        return map(templateRepository.update(entity));
    }

    @Override
    public ContractTemplateResponse archive(Long id) {
        ContractTemplate entity = getEntity(id);
        entity.setStatus(RecordStatus.ARCHIVED);
        return map(templateRepository.update(entity));
    }

    @Override
    public ContractTemplateResponse restore(Long id) {
        ContractTemplate entity = getEntity(id);
        entity.setStatus(RecordStatus.ACTIVE);
        return map(templateRepository.update(entity));
    }

    private void validate(ContractTemplateRequest request, Long existingId) {
        if (request == null) throw new BadRequestException("Contract template request is required");
        if (request.getTemplateName() == null || request.getTemplateName().trim().isEmpty()) throw new BadRequestException("Template name is required");
        if (request.getContractType() == null) throw new BadRequestException("Contract type is required");
        if (request.getVersionNo() == null || request.getVersionNo() <= 0) throw new BadRequestException("Version no must be greater than zero");
        if (request.getTemplateBody() == null || request.getTemplateBody().trim().isEmpty()) throw new BadRequestException("Template body is required");
        if (request.getTemplateCode() != null && !request.getTemplateCode().trim().isEmpty()) {
            templateRepository.findByTemplateCode(request.getTemplateCode().trim())
                    .filter(item -> existingId == null || !item.getId().equals(existingId))
                    .ifPresent(item -> { throw new BadRequestException("Template code already exists"); });
        }
    }

    private ContractTemplate getEntity(Long id) {
        if (id == null) throw new BadRequestException("Template id is required");
        return templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract template not found"));
    }

    private String resolveTemplateCode(String requested) {
        if (requested != null && !requested.trim().isEmpty()) {
            return requested.trim().toUpperCase();
        }
        String last = templateRepository.findLastTemplateCode();
        int next = 1;
        if (last != null && last.matches("CTM-\\d+")) next = Integer.parseInt(last.substring(4)) + 1;
        return String.format("CTM-%05d", next);
    }

    private void apply(ContractTemplate entity, ContractTemplateRequest request) {
        entity.setTemplateName(request.getTemplateName().trim());
        entity.setContractType(request.getContractType());
        entity.setVersionNo(request.getVersionNo());
        entity.setTemplateBody(request.getTemplateBody().trim());
    }

    private ContractTemplateResponse map(ContractTemplate entity) {
        long generatedCount = contractRepository.findAll(entity.getId(), null, null, null).size();
        return new ContractTemplateResponse(
                entity.getId(),
                entity.getTemplateCode(),
                entity.getTemplateName(),
                entity.getContractType().name(),
                entity.getVersionNo(),
                entity.getTemplateBody(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                generatedCount
        );
    }
}
