package com.sbms.financing.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.financing.dto.request.FinancingProductRequest;
import com.sbms.financing.dto.response.FinancingProductResponse;

import java.util.List;

public interface IFinancingProductController {
    ApiResponse<FinancingProductResponse> create(FinancingProductRequest request);
    ApiResponse<List<FinancingProductResponse>> list();
    ApiResponse<FinancingProductResponse> getById(Long id);
    ApiResponse<FinancingProductResponse> update(Long id, FinancingProductRequest request);
    ApiResponse<FinancingProductResponse> archive(Long id);
    ApiResponse<FinancingProductResponse> restore(Long id);
}
