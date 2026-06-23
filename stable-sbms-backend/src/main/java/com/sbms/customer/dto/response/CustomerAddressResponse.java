package com.sbms.customer.dto.response;

import com.sbms.customer.enums.AddressType;
import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public class CustomerAddressResponse {

    private Long id;
    private Long customerId;
    private String customerCode;
    private String customerName;
    private AddressType addressType;
    private String addressLine1;
    private String addressLine2;
    private Long countryId;
    private Long divisionId;
    private Long districtId;
    private Long upazilaId;
    private String postalCode;
    private Boolean primaryAddress;
    private RecordStatus status;
    private LocalDateTime createdAt;

    public CustomerAddressResponse() {
    }

    public CustomerAddressResponse(Long id, Long customerId, String customerCode, String customerName,
                                   AddressType addressType, String addressLine1, String addressLine2,
                                   Long countryId, Long divisionId, Long districtId, Long upazilaId,
                                   String postalCode, Boolean primaryAddress, RecordStatus status,
                                   LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.addressType = addressType;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.countryId = countryId;
        this.divisionId = divisionId;
        this.districtId = districtId;
        this.upazilaId = upazilaId;
        this.postalCode = postalCode;
        this.primaryAddress = primaryAddress;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public Long getCountryId() {
        return countryId;
    }

    public Long getDivisionId() {
        return divisionId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public Long getUpazilaId() {
        return upazilaId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Boolean getPrimaryAddress() {
        return primaryAddress;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}