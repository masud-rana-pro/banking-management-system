package com.sbms.customer.dto.response;

import com.sbms.customer.enums.CustomerStatus;
import com.sbms.customer.enums.CustomerType;
import com.sbms.customer.enums.Gender;
import com.sbms.customer.enums.MaritalStatus;
import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerResponse {

    private Long id;
    private String customerCode;
    private CustomerType customerType;
    private String fullName;
    private String fatherName;
    private String motherName;
    private String spouseName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private String nationality;
    private String mobile;
    private String email;
    private String profileImageName;
    private String occupation;
    private BigDecimal monthlyIncome;
    private String sourceOfFunds;
    private Long branchId;
    private CustomerStatus customerStatus;
    private RecordStatus status;
    private Long addressCount;
    private Long identityCount;
    private Long verifiedIdentityCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public CustomerResponse() {
    }

    public CustomerResponse(Long id, String customerCode, CustomerType customerType, String fullName,
                            String fatherName, String motherName, String spouseName, LocalDate dateOfBirth,
                            Gender gender, MaritalStatus maritalStatus, String nationality, String mobile,
                            String email, String profileImageName, String occupation, BigDecimal monthlyIncome, String sourceOfFunds,
                            Long branchId, CustomerStatus customerStatus, RecordStatus status,
                            Long addressCount, Long identityCount, Long verifiedIdentityCount,
                            LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.customerCode = customerCode;
        this.customerType = customerType;
        this.fullName = fullName;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.spouseName = spouseName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.nationality = nationality;
        this.mobile = mobile;
        this.email = email;
        this.profileImageName = profileImageName;
        this.occupation = occupation;
        this.monthlyIncome = monthlyIncome;
        this.sourceOfFunds = sourceOfFunds;
        this.branchId = branchId;
        this.customerStatus = customerStatus;
        this.status = status;
        this.addressCount = addressCount;
        this.identityCount = identityCount;
        this.verifiedIdentityCount = verifiedIdentityCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public String getNationality() {
        return nationality;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageName() {
        return profileImageName;
    }

    public String getOccupation() {
        return occupation;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public String getSourceOfFunds() {
        return sourceOfFunds;
    }

    public Long getBranchId() {
        return branchId;
    }

    public CustomerStatus getCustomerStatus() {
        return customerStatus;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public Long getAddressCount() {
        return addressCount;
    }

    public Long getIdentityCount() {
        return identityCount;
    }

    public Long getVerifiedIdentityCount() {
        return verifiedIdentityCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
}
