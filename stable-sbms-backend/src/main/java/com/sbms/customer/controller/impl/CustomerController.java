package com.sbms.customer.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.common.aop.AopRequestContext;
import com.sbms.config.RequiresPermission;
import com.sbms.customer.controller.ICustomerController;
import com.sbms.customer.dto.request.CustomerAddressRequest;
import com.sbms.customer.dto.request.CustomerIdentityRequest;
import com.sbms.customer.dto.request.CustomerRequest;
import com.sbms.customer.dto.response.*;
import com.sbms.customer.service.ICustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("CUSTOMER_MANAGEMENT_ACCESS")
public class CustomerController implements ICustomerController {

    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    @RequiresPermission("CUSTOMER_CREATE")
    @PostMapping("/create")
    public ApiResponse<CustomerResponse> create(@RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.create(request, actor("SYSTEM"));
        return ResponseBuilder.success("Customer created successfully", response);
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<CustomerResponse>> list() {
        return ResponseBuilder.success("Customer list fetched successfully", customerService.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Customer fetched successfully", customerService.getById(id));
    }

    @Override
    @RequiresPermission("CUSTOMER_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<CustomerResponse> update(
            @PathVariable Long id,
            @RequestBody CustomerRequest request
    ) {
        CustomerResponse response = customerService.update(id, request, actor("SYSTEM"));
        return ResponseBuilder.success("Customer updated successfully", response);
    }

    @Override
    @RequiresPermission("CUSTOMER_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<CustomerResponse> archive(@PathVariable Long id) {
        CustomerResponse response = customerService.archive(id, actor("SYSTEM"));
        return ResponseBuilder.success("Customer archived successfully", response);
    }

    @Override
    @RequiresPermission("CUSTOMER_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<CustomerResponse> restore(@PathVariable Long id) {
        CustomerResponse response = customerService.restore(id, actor("SYSTEM"));
        return ResponseBuilder.success("Customer restored successfully", response);
    }

    @Override
    @GetMapping("/dropdown")
    public ApiResponse<List<CustomerDropdownResponse>> dropdown(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Customer dropdown fetched successfully", customerService.dropdown(keyword));
    }

    @Override
    @GetMapping("/search")
    public ApiResponse<List<CustomerResponse>> search(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Customer search result fetched successfully", customerService.search(keyword));
    }

    @Override
    @RequiresPermission("CUSTOMER_ACTIVATE")
    @PostMapping("/{id}/activate")
    public ApiResponse<CustomerResponse> activate(@PathVariable Long id) {
        CustomerResponse response = customerService.activate(id, actor("SYSTEM"));
        return ResponseBuilder.success("Customer activated successfully", response);
    }

    @Override
    @RequiresPermission("CUSTOMER_BLOCK")
    @PostMapping("/{id}/block")
    public ApiResponse<CustomerResponse> block(@PathVariable Long id) {
        CustomerResponse response = customerService.block(id, actor("SYSTEM"));
        return ResponseBuilder.success("Customer blocked successfully", response);
    }

    @Override
    @GetMapping("/{id}/timeline")
    public ApiResponse<List<CustomerTimelineResponse>> timeline(@PathVariable Long id) {
        return ResponseBuilder.success("Customer timeline fetched successfully", customerService.timeline(id));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<CustomerDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success(
                "Customer dashboard summary fetched successfully",
                customerService.dashboardSummary()
        );
    }

    @Override
    @RequiresPermission("CUSTOMER_ADDRESS_MANAGE")
    @PostMapping("/address/create")
    public ApiResponse<CustomerAddressResponse> createAddress(
            @RequestBody CustomerAddressRequest request
    ) {
        CustomerAddressResponse response = customerService.createAddress(request);
        return ResponseBuilder.success("Customer address created successfully", response);
    }

    @Override
    @RequiresPermission("CUSTOMER_ADDRESS_MANAGE")
    @PutMapping("/address/{id}")
    public ApiResponse<CustomerAddressResponse> updateAddress(
            @PathVariable Long id,
            @RequestBody CustomerAddressRequest request
    ) {
        CustomerAddressResponse response = customerService.updateAddress(id, request);
        return ResponseBuilder.success("Customer address updated successfully", response);
    }

    @Override
    @GetMapping("/address/{id}")
    public ApiResponse<CustomerAddressResponse> getAddressById(@PathVariable Long id) {
        return ResponseBuilder.success(
                "Customer address fetched successfully",
                customerService.getAddressById(id)
        );
    }

    @Override
    @GetMapping("/{customerId}/addresses")
    public ApiResponse<List<CustomerAddressResponse>> getAddressByCustomer(
            @PathVariable Long customerId
    ) {
        return ResponseBuilder.success(
                "Customer addresses fetched successfully",
                customerService.getAddressByCustomer(customerId)
        );
    }

    @Override
    @RequiresPermission("CUSTOMER_IDENTITY_MANAGE")
    @PostMapping("/identity/create")
    public ApiResponse<CustomerIdentityResponse> createIdentity(
            @RequestBody CustomerIdentityRequest request
    ) {
        CustomerIdentityResponse response = customerService.createIdentity(request);
        return ResponseBuilder.success("Customer identity created successfully", response);
    }

    @Override
    @RequiresPermission("CUSTOMER_IDENTITY_MANAGE")
    @PutMapping("/identity/{id}")
    public ApiResponse<CustomerIdentityResponse> updateIdentity(
            @PathVariable Long id,
            @RequestBody CustomerIdentityRequest request
    ) {
        CustomerIdentityResponse response = customerService.updateIdentity(id, request);
        return ResponseBuilder.success("Customer identity updated successfully", response);
    }

    @Override
    @GetMapping("/identity/{id}")
    public ApiResponse<CustomerIdentityResponse> getIdentityById(@PathVariable Long id) {
        return ResponseBuilder.success(
                "Customer identity fetched successfully",
                customerService.getIdentityById(id)
        );
    }

    @Override
    @GetMapping("/{customerId}/identities")
    public ApiResponse<List<CustomerIdentityResponse>> getIdentityByCustomer(
            @PathVariable Long customerId
    ) {
        return ResponseBuilder.success(
                "Customer identities fetched successfully",
                customerService.getIdentityByCustomer(customerId)
        );
    }

    private String actor(String fallback) {
        String username = AopRequestContext.currentUsername();
        return username == null || username.trim().isEmpty() ? fallback : username.trim();
    }
}
