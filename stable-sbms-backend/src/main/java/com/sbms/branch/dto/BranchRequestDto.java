package com.sbms.branch.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class BranchRequestDto {

    @Size(max = 30)
    private String branchCode;

    @NotBlank(message = "Branch name is required")
    @Size(max = 150)
    private String branchName;

    @Size(max = 50)
    private String branchShortName;

    @NotBlank(message = "Branch type is required")
    @Size(max = 50)
    private String branchType;

    @NotBlank(message = "Routing no is required")
    @Size(max = 30)
    private String routingNo;

    @Size(max = 30)
    private String swiftCode;

    @Email(message = "Invalid email address")
    @Size(max = 120)
    private String email;

    @Pattern(regexp = "^$|^[0-9+\\-]{10,20}$", message = "Invalid mobile number")
    private String mobile;

    private String phone;

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;

    private Long countryId;
    private Long divisionId;
    private Long districtId;
    private Long upazilaId;
    private String postalCode;
    private Long managerUserId;
    private LocalDate openedDate;
    private String status;

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getBranchShortName() { return branchShortName; }
    public void setBranchShortName(String branchShortName) { this.branchShortName = branchShortName; }

    public String getBranchType() { return branchType; }
    public void setBranchType(String branchType) { this.branchType = branchType; }

    public String getRoutingNo() { return routingNo; }
    public void setRoutingNo(String routingNo) { this.routingNo = routingNo; }

    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public Long getCountryId() { return countryId; }
    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public Long getDivisionId() { return divisionId; }
    public void setDivisionId(Long divisionId) { this.divisionId = divisionId; }

    public Long getDistrictId() { return districtId; }
    public void setDistrictId(Long districtId) { this.districtId = districtId; }

    public Long getUpazilaId() { return upazilaId; }
    public void setUpazilaId(Long upazilaId) { this.upazilaId = upazilaId; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public Long getManagerUserId() { return managerUserId; }
    public void setManagerUserId(Long managerUserId) { this.managerUserId = managerUserId; }

    public LocalDate getOpenedDate() { return openedDate; }
    public void setOpenedDate(LocalDate openedDate) { this.openedDate = openedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}