package com.sbms.customer.dto.response;

public class CustomerDropdownResponse {

    private Long id;
    private String customerCode;
    private String fullName;
    private String mobile;
    private String displayName;

    public CustomerDropdownResponse() {
    }

    public CustomerDropdownResponse(Long id, String customerCode, String fullName, String mobile) {
        this.id = id;
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.mobile = mobile;
        this.displayName = customerCode + " - " + fullName + " - " + mobile;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public String getDisplayName() {
        return displayName;
    }
}