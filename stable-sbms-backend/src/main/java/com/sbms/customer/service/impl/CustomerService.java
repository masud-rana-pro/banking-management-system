package com.sbms.customer.service.impl;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.customer.dto.request.CustomerAddressRequest;
import com.sbms.customer.dto.request.CustomerIdentityRequest;
import com.sbms.customer.dto.request.CustomerRequest;
import com.sbms.customer.dto.response.*;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.entity.CustomerAddress;
import com.sbms.customer.entity.CustomerIdentity;
import com.sbms.customer.enums.CustomerStatus;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.customer.repository.CustomerAddressRepository;
import com.sbms.customer.repository.CustomerIdentityRepository;
import com.sbms.customer.repository.CustomerRepository;
import com.sbms.customer.service.ICustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final CustomerIdentityRepository customerIdentityRepository;
    private final AutomatedMailService automatedMailService;

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^(\\+88)?01[3-9]\\d{8}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerAddressRepository customerAddressRepository,
            CustomerIdentityRepository customerIdentityRepository,
            AutomatedMailService automatedMailService
    ) {
        this.customerRepository = customerRepository;
        this.customerAddressRepository = customerAddressRepository;
        this.customerIdentityRepository = customerIdentityRepository;
        this.automatedMailService = automatedMailService;
    }

    @Override
    public CustomerResponse create(CustomerRequest request, String username) {
        validateCustomerCreate(request);

        Customer customer = new Customer();
        customer.setCustomerCode(generateCustomerCode());
        mapCustomerRequestToEntity(request, customer);

        if (customer.getCustomerStatus() == null) {
            customer.setCustomerStatus(CustomerStatus.PENDING_KYC);
        }

        if (customer.getStatus() == null) {
            customer.setStatus(RecordStatus.ACTIVE);
        }

        customer.setCreatedBy(username);

        return mapToCustomerResponse(customerRepository.save(customer));
    }

    @Override
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getById(Long id) {
        return mapToCustomerResponse(getCustomerEntity(id));
    }

    @Override
    public CustomerResponse update(Long id, CustomerRequest request, String username) {
        Customer customer = getCustomerEntity(id);
        validateCustomerUpdate(id, request);

        mapCustomerRequestToEntity(request, customer);
        customer.setUpdatedBy(username);

        return mapToCustomerResponse(customerRepository.update(customer));
    }

    @Override
    public CustomerResponse archive(Long id, String username) {
        Customer customer = getCustomerEntity(id);

        if (RecordStatus.ARCHIVED.equals(customer.getStatus())) {
            throw new BadRequestException("Customer already archived");
        }

        customer.setStatus(RecordStatus.ARCHIVED);
        customer.setUpdatedBy(username);

        return mapToCustomerResponse(customerRepository.update(customer));
    }

    @Override
    public CustomerResponse restore(Long id, String username) {
        Customer customer = getCustomerEntity(id);

        if (!RecordStatus.ARCHIVED.equals(customer.getStatus())) {
            throw new BadRequestException("Only archived customer can be restored");
        }

        customer.setStatus(RecordStatus.ACTIVE);
        customer.setUpdatedBy(username);

        return mapToCustomerResponse(customerRepository.update(customer));
    }

    @Override
    public CustomerResponse activate(Long id, String username) {
        Customer customer = getCustomerEntity(id);

        if (RecordStatus.ARCHIVED.equals(customer.getStatus())) {
            throw new BadRequestException("Archived customer cannot be activated");
        }

        Long addressCount = customerAddressRepository.countActiveAddressByCustomer(id);
        Long identityCount = customerIdentityRepository.countActiveIdentityByCustomer(id);

        if (addressCount == null || addressCount == 0) {
            throw new BadRequestException("Customer cannot be activated before adding at least one address record");
        }

        if (identityCount == null || identityCount == 0) {
            throw new BadRequestException("Customer cannot be activated before adding at least one identity document");
        }

        customer.setCustomerStatus(CustomerStatus.ACTIVE);
        customer.setStatus(RecordStatus.ACTIVE);
        customer.setUpdatedBy(username);

        CustomerResponse response = mapToCustomerResponse(customerRepository.update(customer));
        sendCustomerStatusMail(customer, "Activated", "Customer profile is now active");
        return response;
    }

    @Override
    public CustomerResponse block(Long id, String username) {
        Customer customer = getCustomerEntity(id);

        if (RecordStatus.ARCHIVED.equals(customer.getStatus())) {
            throw new BadRequestException("Archived customer cannot be blocked");
        }

        customer.setCustomerStatus(CustomerStatus.BLOCKED);
        customer.setUpdatedBy(username);

        CustomerResponse response = mapToCustomerResponse(customerRepository.update(customer));
        sendCustomerStatusMail(customer, "Blocked", "Customer profile has been blocked");
        return response;
    }

    @Override
    public List<CustomerDropdownResponse> dropdown(String keyword) {
        return customerRepository.findDropdownCustomers(keyword)
                .stream()
                .map(c -> new CustomerDropdownResponse(
                        c.getId(),
                        c.getCustomerCode(),
                        c.getFullName(),
                        c.getMobile()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> search(String keyword) {
        return customerRepository.search(keyword)
                .stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDashboardSummaryResponse dashboardSummary() {
        return new CustomerDashboardSummaryResponse(
                customerRepository.countAllNonArchived(),
                customerRepository.countByCustomerStatus(CustomerStatus.ACTIVE),
                customerRepository.countByCustomerStatus(CustomerStatus.PENDING_KYC),
                customerRepository.countByCustomerStatus(CustomerStatus.BLOCKED),
                customerRepository.countNewCustomersThisMonth(),
                customerRepository.countIncompleteProfiles()
        );
    }

    @Override
    public List<CustomerTimelineResponse> timeline(Long id) {
        Customer customer = getCustomerEntity(id);

        List<CustomerTimelineResponse> timeline = new ArrayList<>();

        timeline.add(new CustomerTimelineResponse(
                "Customer Created",
                "Customer profile created with code " + customer.getCustomerCode(),
                "CREATE",
                customer.getCreatedAt()
        ));

        if (customer.getUpdatedAt() != null) {
            timeline.add(new CustomerTimelineResponse(
                    "Customer Updated",
                    "Customer profile was updated",
                    "UPDATE",
                    customer.getUpdatedAt()
            ));
        }

        if (CustomerStatus.ACTIVE.equals(customer.getCustomerStatus())) {
            timeline.add(new CustomerTimelineResponse(
                    "Customer Activated",
                    "Customer is currently active",
                    "STATUS",
                    customer.getUpdatedAt() != null ? customer.getUpdatedAt() : customer.getCreatedAt()
            ));
        }

        if (CustomerStatus.BLOCKED.equals(customer.getCustomerStatus())) {
            timeline.add(new CustomerTimelineResponse(
                    "Customer Blocked",
                    "Customer is currently blocked",
                    "STATUS",
                    customer.getUpdatedAt() != null ? customer.getUpdatedAt() : customer.getCreatedAt()
            ));
        }

        return timeline;
    }

    @Override
    public CustomerAddressResponse createAddress(CustomerAddressRequest request) {
        validateAddressRequest(request);

        Customer customer = getCustomerEntity(request.getCustomerId());

        if (Boolean.TRUE.equals(request.getPrimaryAddress())) {
            customerAddressRepository.clearPrimaryAddress(customer.getId());
        }

        CustomerAddress address = new CustomerAddress();
        address.setCustomer(customer);
        mapAddressRequestToEntity(request, address);

        return mapToAddressResponse(customerAddressRepository.save(address));
    }

    @Override
    public CustomerAddressResponse updateAddress(Long id, CustomerAddressRequest request) {
        CustomerAddress address = getAddressEntity(id);
        validateAddressRequest(request);

        Customer customer = getCustomerEntity(request.getCustomerId());

        if (Boolean.TRUE.equals(request.getPrimaryAddress())) {
            customerAddressRepository.clearPrimaryAddress(customer.getId());
        }

        address.setCustomer(customer);
        mapAddressRequestToEntity(request, address);

        return mapToAddressResponse(customerAddressRepository.update(address));
    }

    @Override
    public CustomerAddressResponse getAddressById(Long id) {
        return mapToAddressResponse(getAddressEntity(id));
    }

    @Override
    public List<CustomerAddressResponse> getAddressByCustomer(Long customerId) {
        getCustomerEntity(customerId);

        return customerAddressRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerIdentityResponse createIdentity(CustomerIdentityRequest request) {
        validateIdentityCreate(request);

        Customer customer = getCustomerEntity(request.getCustomerId());

        CustomerIdentity identity = new CustomerIdentity();
        identity.setCustomer(customer);
        mapIdentityRequestToEntity(request, identity);

        return mapToIdentityResponse(customerIdentityRepository.save(identity));
    }

    @Override
    public CustomerIdentityResponse updateIdentity(Long id, CustomerIdentityRequest request) {
        CustomerIdentity identity = getIdentityEntity(id);
        validateIdentityUpdate(id, request);

        Customer customer = getCustomerEntity(request.getCustomerId());

        identity.setCustomer(customer);
        mapIdentityRequestToEntity(request, identity);

        return mapToIdentityResponse(customerIdentityRepository.update(identity));
    }

    @Override
    public CustomerIdentityResponse getIdentityById(Long id) {
        return mapToIdentityResponse(getIdentityEntity(id));
    }

    @Override
    public List<CustomerIdentityResponse> getIdentityByCustomer(Long customerId) {
        getCustomerEntity(customerId);

        return customerIdentityRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToIdentityResponse)
                .collect(Collectors.toList());
    }

    private void validateCustomerCreate(CustomerRequest request) {
        validateCustomerCommon(request);

        if (customerRepository.existsByMobile(request.getMobile().trim())) {
            throw new BadRequestException("Mobile number already exists");
        }

        if (!isBlank(request.getEmail()) && customerRepository.existsByEmail(request.getEmail().trim())) {
            throw new BadRequestException("Email already exists");
        }
    }

    private void validateCustomerUpdate(Long id, CustomerRequest request) {
        validateCustomerCommon(request);

        if (customerRepository.existsByMobileExceptId(request.getMobile().trim(), id)) {
            throw new BadRequestException("Mobile number already exists");
        }

        if (!isBlank(request.getEmail()) && customerRepository.existsByEmailExceptId(request.getEmail().trim(), id)) {
            throw new BadRequestException("Email already exists");
        }
    }

    private void validateCustomerCommon(CustomerRequest request) {
        if (request == null) {
            throw new BadRequestException("Customer request is required");
        }

        if (request.getCustomerType() == null) {
            throw new BadRequestException("Customer type is required");
        }

        if (isBlank(request.getFullName())) {
            throw new BadRequestException("Full name is required");
        }

        if (isBlank(request.getMobile())) {
            throw new BadRequestException("Mobile number is required");
        }

        if (!MOBILE_PATTERN.matcher(request.getMobile().trim()).matches()) {
            throw new BadRequestException("Invalid mobile number format");
        }

        if (!isBlank(request.getEmail()) && !EMAIL_PATTERN.matcher(request.getEmail().trim()).matches()) {
            throw new BadRequestException("Invalid email format");
        }

        if (request.getBranchId() == null) {
            throw new BadRequestException("Branch is required");
        }

        if (request.getMonthlyIncome() != null && request.getMonthlyIncome().signum() < 0) {
            throw new BadRequestException("Monthly income cannot be negative");
        }
    }

    private void validateAddressRequest(CustomerAddressRequest request) {
        if (request == null) {
            throw new BadRequestException("Address request is required");
        }

        if (request.getCustomerId() == null) {
            throw new BadRequestException("Customer is required");
        }

        if (request.getAddressType() == null) {
            throw new BadRequestException("Address type is required");
        }

        if (isBlank(request.getAddressLine1())) {
            throw new BadRequestException("Address line 1 is required");
        }
    }

    private void validateIdentityCreate(CustomerIdentityRequest request) {
        validateIdentityCommon(request);

        if (customerIdentityRepository.existsByDocument(request.getDocumentType(), request.getDocumentNo().trim())) {
            throw new BadRequestException("Identity document already exists");
        }
    }

    private void validateIdentityUpdate(Long id, CustomerIdentityRequest request) {
        validateIdentityCommon(request);

        if (customerIdentityRepository.existsByDocumentExceptId(
                request.getDocumentType(),
                request.getDocumentNo().trim(),
                id
        )) {
            throw new BadRequestException("Identity document already exists");
        }
    }

    private void validateIdentityCommon(CustomerIdentityRequest request) {
        if (request == null) {
            throw new BadRequestException("Identity request is required");
        }

        if (request.getCustomerId() == null) {
            throw new BadRequestException("Customer is required");
        }

        if (request.getDocumentType() == null) {
            throw new BadRequestException("Document type is required");
        }

        if (isBlank(request.getDocumentNo())) {
            throw new BadRequestException("Document number is required");
        }

        if (request.getIssueDate() != null
                && request.getExpiryDate() != null
                && request.getExpiryDate().isBefore(request.getIssueDate())) {
            throw new BadRequestException("Expiry date cannot be before issue date");
        }
    }

    private Customer getCustomerEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Customer id is required");
        }

        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private CustomerAddress getAddressEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Address id is required");
        }

        return customerAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer address not found"));
    }

    private CustomerIdentity getIdentityEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Identity id is required");
        }

        return customerIdentityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer identity not found"));
    }

    private String generateCustomerCode() {
        String lastCode = customerRepository.findLastCustomerCode();

        if (lastCode == null || lastCode.trim().isEmpty()) {
            return "CUS-000001";
        }

        try {
            String numberPart = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            long nextNumber = Long.parseLong(numberPart) + 1;
            return String.format("CUS-%06d", nextNumber);
        } catch (Exception e) {
            return "CUS-" + System.currentTimeMillis();
        }
    }

    private void mapCustomerRequestToEntity(CustomerRequest request, Customer customer) {
        customer.setCustomerType(request.getCustomerType());
        customer.setFullName(trim(request.getFullName()));
        customer.setFatherName(trim(request.getFatherName()));
        customer.setMotherName(trim(request.getMotherName()));
        customer.setSpouseName(trim(request.getSpouseName()));
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setMaritalStatus(request.getMaritalStatus());
        customer.setNationality(trim(request.getNationality()));
        customer.setMobile(trim(request.getMobile()));
        customer.setEmail(trim(request.getEmail()));
        customer.setProfileImageName(trim(request.getProfileImageName()));
        customer.setOccupation(trim(request.getOccupation()));
        customer.setMonthlyIncome(request.getMonthlyIncome());
        customer.setSourceOfFunds(trim(request.getSourceOfFunds()));
        customer.setBranchId(request.getBranchId());

        if (request.getCustomerStatus() != null) {
            customer.setCustomerStatus(request.getCustomerStatus());
        }

        if (request.getStatus() != null) {
            customer.setStatus(request.getStatus());
        }
    }

    private void mapAddressRequestToEntity(CustomerAddressRequest request, CustomerAddress address) {
        address.setAddressType(request.getAddressType());
        address.setAddressLine1(trim(request.getAddressLine1()));
        address.setAddressLine2(trim(request.getAddressLine2()));
        address.setCountryId(request.getCountryId());
        address.setDivisionId(request.getDivisionId());
        address.setDistrictId(request.getDistrictId());
        address.setUpazilaId(request.getUpazilaId());
        address.setPostalCode(trim(request.getPostalCode()));
        address.setPrimaryAddress(Boolean.TRUE.equals(request.getPrimaryAddress()));

        if (request.getStatus() != null) {
            address.setStatus(request.getStatus());
        }
    }

    private void mapIdentityRequestToEntity(CustomerIdentityRequest request, CustomerIdentity identity) {
        identity.setDocumentType(request.getDocumentType());
        identity.setDocumentNo(trim(request.getDocumentNo()));
        identity.setIssueDate(request.getIssueDate());
        identity.setExpiryDate(request.getExpiryDate());
        identity.setIssueCountry(trim(request.getIssueCountry()));
        identity.setImageFileName(trim(request.getImageFileName()));
        identity.setVerifiedFlag(Boolean.TRUE.equals(request.getVerifiedFlag()));

        if (request.getStatus() != null) {
            identity.setStatus(request.getStatus());
        }
    }

    private CustomerResponse mapToCustomerResponse(Customer c) {
        Long addressCount = customerAddressRepository.countActiveAddressByCustomer(c.getId());
        Long identityCount = customerIdentityRepository.countActiveIdentityByCustomer(c.getId());
        Long verifiedIdentityCount = customerIdentityRepository.countVerifiedIdentityByCustomer(c.getId());

        return new CustomerResponse(
                c.getId(),
                c.getCustomerCode(),
                c.getCustomerType(),
                c.getFullName(),
                c.getFatherName(),
                c.getMotherName(),
                c.getSpouseName(),
                c.getDateOfBirth(),
                c.getGender(),
                c.getMaritalStatus(),
                c.getNationality(),
                c.getMobile(),
                c.getEmail(),
                c.getProfileImageName(),
                c.getOccupation(),
                c.getMonthlyIncome(),
                c.getSourceOfFunds(),
                c.getBranchId(),
                c.getCustomerStatus(),
                c.getStatus(),
                addressCount,
                identityCount,
                verifiedIdentityCount,
                c.getCreatedAt(),
                c.getUpdatedAt(),
                c.getCreatedBy(),
                c.getUpdatedBy()
        );
    }

    private CustomerAddressResponse mapToAddressResponse(CustomerAddress a) {
        return new CustomerAddressResponse(
                a.getId(),
                a.getCustomer().getId(),
                a.getCustomer().getCustomerCode(),
                a.getCustomer().getFullName(),
                a.getAddressType(),
                a.getAddressLine1(),
                a.getAddressLine2(),
                a.getCountryId(),
                a.getDivisionId(),
                a.getDistrictId(),
                a.getUpazilaId(),
                a.getPostalCode(),
                a.getPrimaryAddress(),
                a.getStatus(),
                a.getCreatedAt()
        );
    }

    private CustomerIdentityResponse mapToIdentityResponse(CustomerIdentity i) {
        return new CustomerIdentityResponse(
                i.getId(),
                i.getCustomer().getId(),
                i.getCustomer().getCustomerCode(),
                i.getCustomer().getFullName(),
                i.getDocumentType(),
                i.getDocumentNo(),
                i.getIssueDate(),
                i.getExpiryDate(),
                i.getIssueCountry(),
                i.getImageFileName(),
                i.getVerifiedFlag(),
                i.getStatus(),
                i.getCreatedAt()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private void sendCustomerStatusMail(Customer customer, String decision, String remarks) {
        if (customer == null || isBlank(customer.getEmail())) {
            return;
        }
        automatedMailService.sendApprovalDecisionEmail(
                customer.getEmail(),
                "Customer Profile",
                customer.getCustomerCode(),
                decision,
                remarks,
                "/customers/" + customer.getId(),
                "Open Customer"
        );
    }
}
