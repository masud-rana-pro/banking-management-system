package com.sbms.customer.service;

import com.sbms.customer.dto.request.CustomerAddressRequest;
import com.sbms.customer.dto.request.CustomerIdentityRequest;
import com.sbms.customer.dto.request.CustomerRequest;
import com.sbms.customer.dto.response.*;

import java.util.List;

public interface ICustomerService {

    CustomerResponse create(CustomerRequest request, String username);

    List<CustomerResponse> getAll();

    CustomerResponse getById(Long id);

    CustomerResponse update(Long id, CustomerRequest request, String username);

    CustomerResponse archive(Long id, String username);

    CustomerResponse restore(Long id, String username);

    CustomerResponse activate(Long id, String username);

    CustomerResponse block(Long id, String username);

    List<CustomerDropdownResponse> dropdown(String keyword);

    List<CustomerResponse> search(String keyword);

    CustomerDashboardSummaryResponse dashboardSummary();

    List<CustomerTimelineResponse> timeline(Long id);

    CustomerAddressResponse createAddress(CustomerAddressRequest request);

    CustomerAddressResponse updateAddress(Long id, CustomerAddressRequest request);

    CustomerAddressResponse getAddressById(Long id);

    List<CustomerAddressResponse> getAddressByCustomer(Long customerId);

    CustomerIdentityResponse createIdentity(CustomerIdentityRequest request);

    CustomerIdentityResponse updateIdentity(Long id, CustomerIdentityRequest request);

    CustomerIdentityResponse getIdentityById(Long id);

    List<CustomerIdentityResponse> getIdentityByCustomer(Long customerId);
}