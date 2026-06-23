package com.sbms.financing.service.impl;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.financing.dto.request.FinancingProductRequest;
import com.sbms.financing.dto.response.FinancingProductResponse;
import com.sbms.financing.entity.FinancingProduct;
import com.sbms.financing.repository.FinancingApplicationRepository;
import com.sbms.financing.repository.FinancingProductRepository;
import com.sbms.financing.service.IFinancingProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class FinancingProductService implements IFinancingProductService {

    @Autowired
    private FinancingProductRepository productRepository;

    @Autowired
    private FinancingApplicationRepository applicationRepository;

    @Override
    public FinancingProductResponse create(FinancingProductRequest request) {
        validateRequest(request, null);
        FinancingProduct entity = new FinancingProduct();
        entity.setProductCode(resolveProductCode(request.getProductCode()));
        apply(entity, request);
        entity.setStatus(RecordStatus.ACTIVE);
        return map(productRepository.save(entity));
    }

    @Override
    public List<FinancingProductResponse> list() {
        return productRepository.findAll().stream().map(this::map).toList();
    }

    @Override
    public FinancingProductResponse getById(Long id) {
        return map(getEntity(id));
    }

    @Override
    public FinancingProductResponse update(Long id, FinancingProductRequest request) {
        FinancingProduct entity = getEntity(id);
        validateRequest(request, id);
        if (request.getProductCode() != null && !request.getProductCode().trim().isEmpty()) {
            entity.setProductCode(request.getProductCode().trim().toUpperCase());
        }
        apply(entity, request);
        return map(productRepository.update(entity));
    }

    @Override
    public FinancingProductResponse archive(Long id) {
        FinancingProduct entity = getEntity(id);
        entity.setStatus(RecordStatus.ARCHIVED);
        return map(productRepository.update(entity));
    }

    @Override
    public FinancingProductResponse restore(Long id) {
        FinancingProduct entity = getEntity(id);
        entity.setStatus(RecordStatus.ACTIVE);
        return map(productRepository.update(entity));
    }

    private void validateRequest(FinancingProductRequest request, Long existingId) {
        if (request == null) throw new BadRequestException("Financing product request is required");
        if (request.getProductName() == null || request.getProductName().trim().isEmpty()) throw new BadRequestException("Product name is required");
        if (request.getFinancingType() == null) throw new BadRequestException("Financing type is required");
        if (request.getMinimumAmount() == null || request.getMinimumAmount().compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException("Minimum amount must be greater than zero");
        if (request.getMaximumAmount() == null || request.getMaximumAmount().compareTo(request.getMinimumAmount()) < 0) throw new BadRequestException("Maximum amount must be equal or greater than minimum amount");
        if (request.getTenureMonths() == null || request.getTenureMonths() <= 0) throw new BadRequestException("Tenure months must be greater than zero");
        if (request.getProfitRule() == null || request.getProfitRule().trim().isEmpty()) throw new BadRequestException("Profit rule is required");
        if (request.getProductCode() != null && !request.getProductCode().trim().isEmpty()) {
            productRepository.findByProductCode(request.getProductCode().trim())
                    .filter(item -> existingId == null || !item.getId().equals(existingId))
                    .ifPresent(item -> { throw new BadRequestException("Product code already exists"); });
        }
    }

    private FinancingProduct getEntity(Long id) {
        if (id == null) throw new BadRequestException("Product id is required");
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Financing product not found"));
    }

    private String resolveProductCode(String requestedCode) {
        if (requestedCode != null && !requestedCode.trim().isEmpty()) return requestedCode.trim().toUpperCase();
        String last = productRepository.findLastProductCode();
        int next = 1;
        if (last != null && last.matches("FNP-\\d+")) next = Integer.parseInt(last.substring(4)) + 1;
        return String.format("FNP-%05d", next);
    }

    private void apply(FinancingProduct entity, FinancingProductRequest request) {
        entity.setProductName(request.getProductName().trim());
        entity.setFinancingType(request.getFinancingType());
        entity.setMinimumAmount(request.getMinimumAmount().setScale(2, RoundingMode.HALF_UP));
        entity.setMaximumAmount(request.getMaximumAmount().setScale(2, RoundingMode.HALF_UP));
        entity.setTenureMonths(request.getTenureMonths());
        entity.setProfitRule(request.getProfitRule().trim());
    }

    private FinancingProductResponse map(FinancingProduct entity) {
        long count = applicationRepository.findAll(entity.getId(), null, null, null).size();
        return new FinancingProductResponse(
                entity.getId(),
                entity.getProductCode(),
                entity.getProductName(),
                entity.getFinancingType().name(),
                entity.getMinimumAmount(),
                entity.getMaximumAmount(),
                entity.getTenureMonths(),
                entity.getProfitRule(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                count
        );
    }
}
