package com.sbms.customer.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.customer.dto.request.CustomerAddressRequest;
import com.sbms.customer.dto.request.CustomerIdentityRequest;
import com.sbms.customer.dto.request.CustomerRequest;
import com.sbms.customer.dto.response.*;

import java.util.List;

public interface ICustomerController {

    ApiResponse<CustomerResponse> create(CustomerRequest request);

    ApiResponse<List<CustomerResponse>> list();

    ApiResponse<CustomerResponse> getById(Long id);

    ApiResponse<CustomerResponse> update(Long id, CustomerRequest request);

    ApiResponse<CustomerResponse> archive(Long id);

    ApiResponse<CustomerResponse> restore(Long id);

    ApiResponse<List<CustomerDropdownResponse>> dropdown(String keyword);

    ApiResponse<List<CustomerResponse>> search(String keyword);

    ApiResponse<CustomerResponse> activate(Long id);

    ApiResponse<CustomerResponse> block(Long id);

    ApiResponse<List<CustomerTimelineResponse>> timeline(Long id);

    ApiResponse<CustomerDashboardSummaryResponse> dashboardSummary();

    ApiResponse<CustomerAddressResponse> createAddress(CustomerAddressRequest request);

    ApiResponse<CustomerAddressResponse> updateAddress(Long id, CustomerAddressRequest request);

    ApiResponse<CustomerAddressResponse> getAddressById(Long id);

    ApiResponse<List<CustomerAddressResponse>> getAddressByCustomer(Long customerId);

    ApiResponse<CustomerIdentityResponse> createIdentity(CustomerIdentityRequest request);

    ApiResponse<CustomerIdentityResponse> updateIdentity(Long id, CustomerIdentityRequest request);

    ApiResponse<CustomerIdentityResponse> getIdentityById(Long id);

    ApiResponse<List<CustomerIdentityResponse>> getIdentityByCustomer(Long customerId);
}