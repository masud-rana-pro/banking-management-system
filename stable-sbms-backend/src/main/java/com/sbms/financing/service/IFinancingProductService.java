package com.sbms.financing.service;

import com.sbms.financing.dto.request.FinancingProductRequest;
import com.sbms.financing.dto.response.FinancingProductResponse;

import java.util.List;

public interface IFinancingProductService {
    FinancingProductResponse create(FinancingProductRequest request);
    List<FinancingProductResponse> list();
    FinancingProductResponse getById(Long id);
    FinancingProductResponse update(Long id, FinancingProductRequest request);
    FinancingProductResponse archive(Long id);
    FinancingProductResponse restore(Long id);
}
