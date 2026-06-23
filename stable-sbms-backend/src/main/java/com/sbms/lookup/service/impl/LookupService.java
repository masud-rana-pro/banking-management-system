package com.sbms.lookup.service.impl;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.lookup.dto.request.LookupTypeRequest;
import com.sbms.lookup.dto.request.LookupValueRequest;
import com.sbms.lookup.dto.response.LookupDashboardSummaryResponse;
import com.sbms.lookup.dto.response.LookupTypeResponse;
import com.sbms.lookup.dto.response.LookupValueResponse;
import com.sbms.lookup.entity.LookupType;
import com.sbms.lookup.entity.LookupValue;
import com.sbms.lookup.repository.LookupTypeRepository;
import com.sbms.lookup.repository.LookupValueRepository;
import com.sbms.lookup.service.ILookupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LookupService implements ILookupService {

    private final LookupTypeRepository lookupTypeRepository;
    private final LookupValueRepository lookupValueRepository;

    public LookupService(LookupTypeRepository lookupTypeRepository, LookupValueRepository lookupValueRepository) {
        this.lookupTypeRepository = lookupTypeRepository;
        this.lookupValueRepository = lookupValueRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public LookupDashboardSummaryResponse getDashboardSummary() {
        return new LookupDashboardSummaryResponse(
                lookupTypeRepository.count(),
                lookupValueRepository.count(),
                lookupValueRepository.countByStatus(RecordStatus.ACTIVE),
                lookupValueRepository.countByUpdatedAtAfter(LocalDateTime.now().minusDays(7)),
                lookupTypeRepository.findTop10ByOrderByUpdatedAtDesc().stream().map(this::mapType).toList(),
                lookupValueRepository.findTop10ByOrderByUpdatedAtDesc().stream().map(this::mapValue).toList()
        );
    }

    @Override
    public LookupTypeResponse createType(LookupTypeRequest request) {
        validateTypeRequest(request, null);
        LookupType entity = new LookupType();
        applyType(entity, request);
        return mapType(lookupTypeRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LookupTypeResponse> listTypes() {
        return lookupTypeRepository.findAll().stream().map(this::mapType).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LookupTypeResponse getTypeById(Long id) {
        return mapType(getTypeEntity(id));
    }

    @Override
    public LookupTypeResponse updateType(Long id, LookupTypeRequest request) {
        validateTypeRequest(request, id);
        LookupType entity = getTypeEntity(id);
        applyType(entity, request);
        return mapType(lookupTypeRepository.save(entity));
    }

    @Override
    public LookupTypeResponse archiveType(Long id) {
        LookupType entity = getTypeEntity(id);
        entity.setStatus(RecordStatus.ARCHIVED);
        return mapType(lookupTypeRepository.save(entity));
    }

    @Override
    public LookupTypeResponse restoreType(Long id) {
        LookupType entity = getTypeEntity(id);
        entity.setStatus(RecordStatus.ACTIVE);
        return mapType(lookupTypeRepository.save(entity));
    }

    @Override
    public LookupValueResponse createValue(LookupValueRequest request) {
        validateValueRequest(request, null);
        LookupValue entity = new LookupValue();
        applyValue(entity, request);
        return mapValue(lookupValueRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LookupValueResponse> listValues(Long lookupTypeId, String keyword) {
        String normalizedKeyword = keyword == null || keyword.trim().isEmpty() ? null : keyword.trim();
        return lookupValueRepository.search(lookupTypeId, normalizedKeyword).stream().map(this::mapValue).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LookupValueResponse getValueById(Long id) {
        return mapValue(getValueEntity(id));
    }

    @Override
    public LookupValueResponse updateValue(Long id, LookupValueRequest request) {
        validateValueRequest(request, id);
        LookupValue entity = getValueEntity(id);
        applyValue(entity, request);
        return mapValue(lookupValueRepository.save(entity));
    }

    @Override
    public LookupValueResponse archiveValue(Long id) {
        LookupValue entity = getValueEntity(id);
        entity.setStatus(RecordStatus.ARCHIVED);
        return mapValue(lookupValueRepository.save(entity));
    }

    @Override
    public LookupValueResponse restoreValue(Long id) {
        LookupValue entity = getValueEntity(id);
        entity.setStatus(RecordStatus.ACTIVE);
        return mapValue(lookupValueRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LookupValueResponse> getValuesByTypeCode(String typeCode, boolean activeOnly) {
        if (typeCode == null || typeCode.trim().isEmpty()) {
            throw new BadRequestException("Type code is required");
        }
        List<LookupValue> values = activeOnly
                ? lookupValueRepository.findByTypeCodeAndStatus(typeCode.trim(), RecordStatus.ACTIVE)
                : lookupValueRepository.findByTypeCode(typeCode.trim());
        return values.stream().map(this::mapValue).toList();
    }

    private void validateTypeRequest(LookupTypeRequest request, Long id) {
        if (request == null) throw new BadRequestException("Lookup type request is required");
        if (isBlank(request.typeCode())) throw new BadRequestException("Type code is required");
        if (isBlank(request.typeName())) throw new BadRequestException("Type name is required");
        boolean exists = id == null
                ? lookupTypeRepository.existsByTypeCodeIgnoreCase(request.typeCode().trim())
                : lookupTypeRepository.existsByTypeCodeIgnoreCaseAndIdNot(request.typeCode().trim(), id);
        if (exists) throw new BadRequestException("Type code already exists");
    }

    private void validateValueRequest(LookupValueRequest request, Long id) {
        if (request == null) throw new BadRequestException("Lookup value request is required");
        if (request.lookupTypeId() == null) throw new BadRequestException("Lookup type is required");
        if (isBlank(request.valueCode())) throw new BadRequestException("Value code is required");
        if (isBlank(request.valueLabel())) throw new BadRequestException("Value label is required");
        if (request.sortOrder() != null && request.sortOrder() < 0) {
            throw new BadRequestException("Sort order must be numeric and non-negative");
        }
        getTypeEntity(request.lookupTypeId());
        boolean exists = id == null
                ? lookupValueRepository.existsByLookupTypeIdAndValueCodeIgnoreCase(request.lookupTypeId(), request.valueCode().trim())
                : lookupValueRepository.existsByLookupTypeIdAndValueCodeIgnoreCaseAndIdNot(request.lookupTypeId(), request.valueCode().trim(), id);
        if (exists) throw new BadRequestException("Value code already exists within the selected type");
    }

    private void applyType(LookupType entity, LookupTypeRequest request) {
        entity.setTypeCode(request.typeCode().trim().toUpperCase());
        entity.setTypeName(request.typeName().trim());
        entity.setDescription(isBlank(request.description()) ? null : request.description().trim());
        entity.setStatus(parseStatus(request.status(), RecordStatus.ACTIVE));
    }

    private void applyValue(LookupValue entity, LookupValueRequest request) {
        entity.setLookupType(getTypeEntity(request.lookupTypeId()));
        entity.setValueCode(request.valueCode().trim().toUpperCase());
        entity.setValueLabel(request.valueLabel().trim());
        entity.setValueBnLabel(isBlank(request.valueBnLabel()) ? null : request.valueBnLabel().trim());
        entity.setSortOrder(request.sortOrder());
        entity.setExtraData(isBlank(request.extraData()) ? null : request.extraData().trim());
        entity.setStatus(parseStatus(request.status(), RecordStatus.ACTIVE));
    }

    private LookupTypeResponse mapType(LookupType entity) {
        return new LookupTypeResponse(
                entity.getId(),
                entity.getTypeCode(),
                entity.getTypeName(),
                entity.getDescription(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                lookupValueRepository.countByLookupTypeIdAndStatus(entity.getId(), RecordStatus.ACTIVE),
                lookupValueRepository.countByLookupTypeId(entity.getId())
        );
    }

    private LookupValueResponse mapValue(LookupValue entity) {
        return new LookupValueResponse(
                entity.getId(),
                entity.getLookupType().getId(),
                entity.getLookupType().getTypeCode(),
                entity.getLookupType().getTypeName(),
                entity.getValueCode(),
                entity.getValueLabel(),
                entity.getValueBnLabel(),
                entity.getSortOrder(),
                entity.getExtraData(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private LookupType getTypeEntity(Long id) {
        if (id == null) throw new BadRequestException("Lookup type id is required");
        return lookupTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lookup type not found"));
    }

    private LookupValue getValueEntity(Long id) {
        if (id == null) throw new BadRequestException("Lookup value id is required");
        return lookupValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lookup value not found"));
    }

    private RecordStatus parseStatus(String value, RecordStatus fallback) {
        if (isBlank(value)) return fallback;
        try {
            return RecordStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid status value");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
