package com.sbms.financing.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.financing.controller.IFinancingProductController;
import com.sbms.financing.dto.request.FinancingProductRequest;
import com.sbms.financing.dto.response.FinancingProductResponse;
import com.sbms.financing.service.IFinancingProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financing-products")
@RequiresPermission("FINANCING_ACCESS")
public class FinancingProductController implements IFinancingProductController {

    @Autowired
    private IFinancingProductService productService;

    @Override
    @RequiresPermission("FINANCING_PRODUCT_CREATE")
    @PostMapping("/create")
    public ApiResponse<FinancingProductResponse> create(@RequestBody FinancingProductRequest request) {
        return ResponseBuilder.success("Financing product created successfully", productService.create(request));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<FinancingProductResponse>> list() {
        return ResponseBuilder.success("Financing product list fetched successfully", productService.list());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<FinancingProductResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Financing product fetched successfully", productService.getById(id));
    }

    @Override
    @RequiresPermission("FINANCING_PRODUCT_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<FinancingProductResponse> update(@PathVariable Long id, @RequestBody FinancingProductRequest request) {
        return ResponseBuilder.success("Financing product updated successfully", productService.update(id, request));
    }

    @Override
    @RequiresPermission("FINANCING_PRODUCT_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<FinancingProductResponse> archive(@PathVariable Long id) {
        return ResponseBuilder.success("Financing product archived successfully", productService.archive(id));
    }

    @Override
    @RequiresPermission("FINANCING_PRODUCT_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<FinancingProductResponse> restore(@PathVariable Long id) {
        return ResponseBuilder.success("Financing product restored successfully", productService.restore(id));
    }
}
