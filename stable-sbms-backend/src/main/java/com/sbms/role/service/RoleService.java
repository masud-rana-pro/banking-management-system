package com.sbms.role.service;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.role.dto.RoleCreateRequestDto;
import com.sbms.role.dto.RoleDashboardSummaryDto;
import com.sbms.role.dto.RolePermissionAssignDto;
import com.sbms.role.dto.RolePermissionResponseDto;
import com.sbms.role.dto.RoleResponseDto;
import com.sbms.role.entity.Role;
import com.sbms.role.entity.RolePermission;
import com.sbms.role.enums.RoleStatus;
import com.sbms.role.repository.RolePermissionRepository;
import com.sbms.role.repository.RoleRepository;
import com.sbms.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {

    private final RoleRepository repo;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;

    private static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";

    private static final List<PermissionDefinition> PERMISSION_CATALOG = List.of(
            module("ADMIN", "Admin Dashboard", "ADMIN_DASHBOARD_ACCESS", "Dashboard Access"),
            module("ROLE_MANAGEMENT", "Role Management", "ROLE_MANAGEMENT_ACCESS", "Module Access"),
            action("ROLE_MANAGEMENT", "Role Management", "VIEW", "ROLE_VIEW", "View Roles"),
            action("ROLE_MANAGEMENT", "Role Management", "CREATE", "ROLE_CREATE", "Create Role"),
            action("ROLE_MANAGEMENT", "Role Management", "EDIT", "ROLE_EDIT", "Edit Role"),
            action("ROLE_MANAGEMENT", "Role Management", "ARCHIVE", "ROLE_ARCHIVE", "Archive Role"),
            action("ROLE_MANAGEMENT", "Role Management", "RESTORE", "ROLE_RESTORE", "Restore Role"),
            action("ROLE_MANAGEMENT", "Role Management", "MAP_PERMISSIONS", "ROLE_MAP_PERMISSIONS", "Map Permissions"),
            module("USER_MANAGEMENT", "User Management", "USER_MANAGEMENT_ACCESS", "Module Access"),
            action("USER_MANAGEMENT", "User Management", "VIEW", "USER_VIEW", "View Users"),
            action("USER_MANAGEMENT", "User Management", "CREATE", "USER_CREATE", "Create User"),
            action("USER_MANAGEMENT", "User Management", "EDIT", "USER_EDIT", "Edit User"),
            action("USER_MANAGEMENT", "User Management", "ARCHIVE", "USER_ARCHIVE", "Archive User"),
            action("USER_MANAGEMENT", "User Management", "RESTORE", "USER_RESTORE", "Restore User"),
            action("USER_MANAGEMENT", "User Management", "LOCK", "USER_LOCK", "Lock User"),
            action("USER_MANAGEMENT", "User Management", "UNLOCK", "USER_UNLOCK", "Unlock User"),
            action("USER_MANAGEMENT", "User Management", "RESET_PASSWORD", "USER_RESET_PASSWORD", "Reset Password"),
            action("USER_MANAGEMENT", "User Management", "ASSIGN_ROLE", "USER_ASSIGN_ROLE", "Assign Role"),
            module("LOOKUP_CONFIG", "Lookup / Config", "LOOKUP_CONFIG_ACCESS", "Module Access"),
            action("LOOKUP_CONFIG", "Lookup / Config", "TYPE_CREATE", "LOOKUP_TYPE_CREATE", "Create Lookup Type"),
            action("LOOKUP_CONFIG", "Lookup / Config", "TYPE_EDIT", "LOOKUP_TYPE_EDIT", "Edit Lookup Type"),
            action("LOOKUP_CONFIG", "Lookup / Config", "TYPE_ARCHIVE", "LOOKUP_TYPE_ARCHIVE", "Archive Lookup Type"),
            action("LOOKUP_CONFIG", "Lookup / Config", "TYPE_RESTORE", "LOOKUP_TYPE_RESTORE", "Restore Lookup Type"),
            action("LOOKUP_CONFIG", "Lookup / Config", "VALUE_CREATE", "LOOKUP_VALUE_CREATE", "Create Lookup Value"),
            action("LOOKUP_CONFIG", "Lookup / Config", "VALUE_EDIT", "LOOKUP_VALUE_EDIT", "Edit Lookup Value"),
            action("LOOKUP_CONFIG", "Lookup / Config", "VALUE_ARCHIVE", "LOOKUP_VALUE_ARCHIVE", "Archive Lookup Value"),
            action("LOOKUP_CONFIG", "Lookup / Config", "VALUE_RESTORE", "LOOKUP_VALUE_RESTORE", "Restore Lookup Value"),
            module("BRANCH_MANAGEMENT", "Branch Management", "BRANCH_MANAGEMENT_ACCESS", "Module Access"),
            action("BRANCH_MANAGEMENT", "Branch Management", "CREATE", "BRANCH_CREATE", "Create Branch"),
            action("BRANCH_MANAGEMENT", "Branch Management", "EDIT", "BRANCH_EDIT", "Edit Branch"),
            action("BRANCH_MANAGEMENT", "Branch Management", "ARCHIVE", "BRANCH_ARCHIVE", "Archive Branch"),
            action("BRANCH_MANAGEMENT", "Branch Management", "RESTORE", "BRANCH_RESTORE", "Restore Branch"),
            action("BRANCH_MANAGEMENT", "Branch Management", "ASSIGN_USER", "BRANCH_ASSIGN_USER", "Assign Branch User"),
            action("BRANCH_MANAGEMENT", "Branch Management", "TELLER_LIMIT_MANAGE", "BRANCH_TELLER_LIMIT_MANAGE", "Manage Teller Limit"),
            action("BRANCH_MANAGEMENT", "Branch Management", "VAULT_MANAGE", "BRANCH_VAULT_MANAGE", "Manage Vault"),
            module("ATM_CDM", "ATM / CDM", "ATM_CDM_ACCESS", "Module Access"),
            action("ATM_CDM", "ATM / CDM", "TERMINAL_CREATE", "ATM_TERMINAL_CREATE", "Create ATM/CDM Terminal"),
            action("ATM_CDM", "ATM / CDM", "TERMINAL_EDIT", "ATM_TERMINAL_EDIT", "Edit ATM/CDM Terminal"),
            action("ATM_CDM", "ATM / CDM", "TERMINAL_ARCHIVE", "ATM_TERMINAL_ARCHIVE", "Archive ATM/CDM Terminal"),
            action("ATM_CDM", "ATM / CDM", "TERMINAL_RESTORE", "ATM_TERMINAL_RESTORE", "Restore ATM/CDM Terminal"),
            action("ATM_CDM", "ATM / CDM", "CASH_BIN_CREATE", "ATM_CASH_BIN_CREATE", "Create Cash Bin"),
            action("ATM_CDM", "ATM / CDM", "CASH_BIN_EDIT", "ATM_CASH_BIN_EDIT", "Edit Cash Bin"),
            action("ATM_CDM", "ATM / CDM", "CASH_BIN_ARCHIVE", "ATM_CASH_BIN_ARCHIVE", "Archive Cash Bin"),
            action("ATM_CDM", "ATM / CDM", "CASH_BIN_RESTORE", "ATM_CASH_BIN_RESTORE", "Restore Cash Bin"),
            action("ATM_CDM", "ATM / CDM", "REPLENISHMENT_CREATE", "ATM_REPLENISHMENT_CREATE", "Create Replenishment"),
            action("ATM_CDM", "ATM / CDM", "RECONCILIATION_CREATE", "ATM_RECONCILIATION_CREATE", "Create Reconciliation"),
            module("CUSTOMER_MANAGEMENT", "Customer Management", "CUSTOMER_MANAGEMENT_ACCESS", "Module Access"),
            action("CUSTOMER_MANAGEMENT", "Customer Management", "CREATE", "CUSTOMER_CREATE", "Create Customer"),
            action("CUSTOMER_MANAGEMENT", "Customer Management", "EDIT", "CUSTOMER_EDIT", "Edit Customer"),
            action("CUSTOMER_MANAGEMENT", "Customer Management", "ARCHIVE", "CUSTOMER_ARCHIVE", "Archive Customer"),
            action("CUSTOMER_MANAGEMENT", "Customer Management", "RESTORE", "CUSTOMER_RESTORE", "Restore Customer"),
            action("CUSTOMER_MANAGEMENT", "Customer Management", "ACTIVATE", "CUSTOMER_ACTIVATE", "Activate Customer"),
            action("CUSTOMER_MANAGEMENT", "Customer Management", "BLOCK", "CUSTOMER_BLOCK", "Block Customer"),
            action("CUSTOMER_MANAGEMENT", "Customer Management", "ADDRESS_MANAGE", "CUSTOMER_ADDRESS_MANAGE", "Manage Customer Address"),
            action("CUSTOMER_MANAGEMENT", "Customer Management", "IDENTITY_MANAGE", "CUSTOMER_IDENTITY_MANAGE", "Manage Customer Identity"),
            module("KYC_MANAGEMENT", "KYC Management", "KYC_MANAGEMENT_ACCESS", "Module Access"),
            action("KYC_MANAGEMENT", "KYC Management", "CREATE", "KYC_CREATE", "Create KYC Profile"),
            action("KYC_MANAGEMENT", "KYC Management", "EDIT", "KYC_EDIT", "Edit KYC Profile"),
            action("KYC_MANAGEMENT", "KYC Management", "SUBMIT", "KYC_SUBMIT", "Submit KYC Profile"),
            action("KYC_MANAGEMENT", "KYC Management", "VERIFY", "KYC_VERIFY", "Verify KYC Profile"),
            action("KYC_MANAGEMENT", "KYC Management", "APPROVE", "KYC_APPROVE", "Approve KYC Profile"),
            action("KYC_MANAGEMENT", "KYC Management", "REJECT", "KYC_REJECT", "Reject KYC Profile"),
            action("KYC_MANAGEMENT", "KYC Management", "RETURN", "KYC_RETURN", "Return KYC Profile"),
            action("KYC_MANAGEMENT", "KYC Management", "DOCUMENT_UPLOAD", "KYC_DOCUMENT_UPLOAD", "Upload KYC Document"),
            module("ACCOUNT_MANAGEMENT", "Account Management", "ACCOUNT_MANAGEMENT_ACCESS", "Module Access"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "TYPE_CREATE", "ACCOUNT_TYPE_CREATE", "Create Account Type"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "TYPE_EDIT", "ACCOUNT_TYPE_EDIT", "Edit Account Type"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "TYPE_ARCHIVE", "ACCOUNT_TYPE_ARCHIVE", "Archive Account Type"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "TYPE_RESTORE", "ACCOUNT_TYPE_RESTORE", "Restore Account Type"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "REQUEST_CREATE", "ACCOUNT_REQUEST_CREATE", "Create Account Opening Request"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "REQUEST_EDIT", "ACCOUNT_REQUEST_EDIT", "Edit Account Opening Request"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "REQUEST_SUBMIT", "ACCOUNT_REQUEST_SUBMIT", "Submit Account Opening Request"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "REQUEST_VERIFY", "ACCOUNT_REQUEST_VERIFY", "Verify Account Opening Request"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "REQUEST_APPROVE", "ACCOUNT_REQUEST_APPROVE", "Approve Account Opening Request"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "REQUEST_REJECT", "ACCOUNT_REQUEST_REJECT", "Reject Account Opening Request"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "REQUEST_RETURN", "ACCOUNT_REQUEST_RETURN", "Return Account Opening Request"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "ACCOUNT_ACTIVATE", "ACCOUNT_ACTIVATE", "Activate Account"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "ACCOUNT_BLOCK", "ACCOUNT_BLOCK", "Block Account"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "ACCOUNT_FREEZE", "ACCOUNT_FREEZE", "Freeze Account"),
            action("ACCOUNT_MANAGEMENT", "Account Management", "ACCOUNT_CLOSE", "ACCOUNT_CLOSE", "Close Account"),
            module("TRANSACTIONS", "Transactions", "TRANSACTIONS_ACCESS", "Module Access"),
            action("TRANSACTIONS", "Transactions", "DEPOSIT", "TRANSACTION_DEPOSIT", "Post Cash Deposit"),
            action("TRANSACTIONS", "Transactions", "WITHDRAW", "TRANSACTION_WITHDRAW", "Post Cash Withdrawal"),
            action("TRANSACTIONS", "Transactions", "TRANSFER", "TRANSACTION_TRANSFER", "Post Fund Transfer"),
            action("TRANSACTIONS", "Transactions", "CHEQUE_CLEARING", "TRANSACTION_CHEQUE_CLEARING", "Post Cheque Clearing"),
            action("TRANSACTIONS", "Transactions", "STANDING_INSTRUCTION_CREATE", "TRANSACTION_STANDING_INSTRUCTION_CREATE", "Create Standing Instruction"),
            action("TRANSACTIONS", "Transactions", "REVERSE", "TRANSACTION_REVERSE", "Reverse Transaction"),
            module("PROFIT_MANAGEMENT", "Profit Management", "PROFIT_MANAGEMENT_ACCESS", "Module Access"),
            action("PROFIT_MANAGEMENT", "Profit Management", "RATIO_CREATE", "PROFIT_RATIO_CREATE", "Create Profit Ratio"),
            action("PROFIT_MANAGEMENT", "Profit Management", "RATIO_EDIT", "PROFIT_RATIO_EDIT", "Edit Profit Ratio"),
            action("PROFIT_MANAGEMENT", "Profit Management", "RATIO_ARCHIVE", "PROFIT_RATIO_ARCHIVE", "Archive Profit Ratio"),
            action("PROFIT_MANAGEMENT", "Profit Management", "RATIO_RESTORE", "PROFIT_RATIO_RESTORE", "Restore Profit Ratio"),
            action("PROFIT_MANAGEMENT", "Profit Management", "SCHEDULE_CREATE", "PROFIT_SCHEDULE_CREATE", "Create Profit Schedule"),
            action("PROFIT_MANAGEMENT", "Profit Management", "SCHEDULE_ARCHIVE", "PROFIT_SCHEDULE_ARCHIVE", "Archive Profit Schedule"),
            action("PROFIT_MANAGEMENT", "Profit Management", "SCHEDULE_RESTORE", "PROFIT_SCHEDULE_RESTORE", "Restore Profit Schedule"),
            action("PROFIT_MANAGEMENT", "Profit Management", "POSTING_RUN", "PROFIT_POSTING_RUN", "Run Profit Posting"),
            module("CARD_MANAGEMENT", "Card Management", "CARD_MANAGEMENT_ACCESS", "Module Access"),
            action("CARD_MANAGEMENT", "Card Management", "CREATE", "CARD_CREATE", "Create Card"),
            action("CARD_MANAGEMENT", "Card Management", "EDIT", "CARD_EDIT", "Edit Card"),
            action("CARD_MANAGEMENT", "Card Management", "ARCHIVE", "CARD_ARCHIVE", "Archive Card"),
            action("CARD_MANAGEMENT", "Card Management", "RESTORE", "CARD_RESTORE", "Restore Card"),
            action("CARD_MANAGEMENT", "Card Management", "ACTIVATE", "CARD_ACTIVATE", "Activate Card"),
            action("CARD_MANAGEMENT", "Card Management", "BLOCK", "CARD_BLOCK", "Block Card"),
            action("CARD_MANAGEMENT", "Card Management", "UNBLOCK", "CARD_UNBLOCK", "Unblock Card"),
            action("CARD_MANAGEMENT", "Card Management", "REPLACE", "CARD_REPLACE", "Replace Card"),
            action("CARD_MANAGEMENT", "Card Management", "RENEW", "CARD_RENEW", "Renew Card"),
            action("CARD_MANAGEMENT", "Card Management", "PIN_EVENT", "CARD_PIN_EVENT", "Record Card PIN Event"),
            module("STATEMENTS", "Statements", "STATEMENTS_ACCESS", "Module Access"),
            action("STATEMENTS", "Statements", "CUSTOMER_REQUEST", "STATEMENT_CUSTOMER_REQUEST", "Request Customer Statement"),
            action("STATEMENTS", "Statements", "BRANCH_REQUEST", "STATEMENT_BRANCH_REQUEST", "Request Branch Statement"),
            module("DEPOSIT_SCHEMES", "Deposit Schemes", "DEPOSIT_SCHEMES_ACCESS", "Module Access"),
            action("DEPOSIT_SCHEMES", "Deposit Schemes", "SCHEME_CREATE", "DEPOSIT_SCHEME_CREATE", "Create Deposit Scheme"),
            action("DEPOSIT_SCHEMES", "Deposit Schemes", "SCHEME_EDIT", "DEPOSIT_SCHEME_EDIT", "Edit Deposit Scheme"),
            action("DEPOSIT_SCHEMES", "Deposit Schemes", "SCHEME_ARCHIVE", "DEPOSIT_SCHEME_ARCHIVE", "Archive Deposit Scheme"),
            action("DEPOSIT_SCHEMES", "Deposit Schemes", "SCHEME_RESTORE", "DEPOSIT_SCHEME_RESTORE", "Restore Deposit Scheme"),
            action("DEPOSIT_SCHEMES", "Deposit Schemes", "ENROLLMENT_CREATE", "DEPOSIT_SCHEME_ENROLLMENT_CREATE", "Create Deposit Scheme Enrollment"),
            module("FINANCING", "Financing", "FINANCING_ACCESS", "Module Access"),
            action("FINANCING", "Financing", "PRODUCT_CREATE", "FINANCING_PRODUCT_CREATE", "Create Financing Product"),
            action("FINANCING", "Financing", "PRODUCT_EDIT", "FINANCING_PRODUCT_EDIT", "Edit Financing Product"),
            action("FINANCING", "Financing", "PRODUCT_ARCHIVE", "FINANCING_PRODUCT_ARCHIVE", "Archive Financing Product"),
            action("FINANCING", "Financing", "PRODUCT_RESTORE", "FINANCING_PRODUCT_RESTORE", "Restore Financing Product"),
            action("FINANCING", "Financing", "APPLICATION_CREATE", "FINANCING_APPLICATION_CREATE", "Create Financing Application"),
            action("FINANCING", "Financing", "APPLICATION_EDIT", "FINANCING_APPLICATION_EDIT", "Edit Financing Application"),
            action("FINANCING", "Financing", "APPLICATION_SUBMIT", "FINANCING_APPLICATION_SUBMIT", "Submit Financing Application"),
            action("FINANCING", "Financing", "APPLICATION_ARCHIVE", "FINANCING_APPLICATION_ARCHIVE", "Archive Financing Application"),
            action("FINANCING", "Financing", "APPLICATION_RESTORE", "FINANCING_APPLICATION_RESTORE", "Restore Financing Application"),
            action("FINANCING", "Financing", "VERIFY", "FINANCING_VERIFY", "Verify Financing Application"),
            action("FINANCING", "Financing", "REVIEW", "FINANCING_REVIEW", "Send for Shariah Review"),
            action("FINANCING", "Financing", "APPROVE", "FINANCING_APPROVE", "Approve Financing Application"),
            action("FINANCING", "Financing", "REJECT", "FINANCING_REJECT", "Reject Financing Application"),
            action("FINANCING", "Financing", "RETURN", "FINANCING_RETURN", "Return Financing Application"),
            action("FINANCING", "Financing", "DISBURSE", "FINANCING_DISBURSE", "Disburse Financing"),
            action("FINANCING", "Financing", "COLLECT_PAYMENT", "FINANCING_COLLECT_PAYMENT", "Collect Financing Repayment"),
            module("CONTRACTS", "Contracts", "CONTRACTS_ACCESS", "Module Access"),
            action("CONTRACTS", "Contracts", "TEMPLATE_CREATE", "CONTRACT_TEMPLATE_CREATE", "Create Contract Template"),
            action("CONTRACTS", "Contracts", "TEMPLATE_EDIT", "CONTRACT_TEMPLATE_EDIT", "Edit Contract Template"),
            action("CONTRACTS", "Contracts", "TEMPLATE_ARCHIVE", "CONTRACT_TEMPLATE_ARCHIVE", "Archive Contract Template"),
            action("CONTRACTS", "Contracts", "TEMPLATE_RESTORE", "CONTRACT_TEMPLATE_RESTORE", "Restore Contract Template"),
            action("CONTRACTS", "Contracts", "GENERATE", "CONTRACT_GENERATE", "Generate Contract"),
            action("CONTRACTS", "Contracts", "CUSTOMER_SIGN", "CONTRACT_CUSTOMER_SIGN", "Capture Customer Signature"),
            action("CONTRACTS", "Contracts", "SHARIAH_SIGN", "CONTRACT_SHARIAH_SIGN", "Capture Shariah Signature"),
            module("SHARIAH_REVIEW", "Shariah Review", "SHARIAH_REVIEW_ACCESS", "Module Access"),
            action("SHARIAH_REVIEW", "Shariah Review", "CHECKLIST_SAVE", "SHARIAH_CHECKLIST_SAVE", "Save Shariah Checklist"),
            action("SHARIAH_REVIEW", "Shariah Review", "APPROVE", "SHARIAH_APPROVE", "Approve Shariah Review"),
            action("SHARIAH_REVIEW", "Shariah Review", "REJECT", "SHARIAH_REJECT", "Reject Shariah Review"),
            action("SHARIAH_REVIEW", "Shariah Review", "RETURN", "SHARIAH_RETURN", "Return Shariah Review"),
            module("ZAKAT_CHARITY", "Zakat & Charity", "ZAKAT_CHARITY_ACCESS", "Module Access"),
            action("ZAKAT_CHARITY", "Zakat & Charity", "PROFILE_CREATE", "ZAKAT_PROFILE_CREATE", "Create Zakat Profile"),
            action("ZAKAT_CHARITY", "Zakat & Charity", "PROFILE_EDIT", "ZAKAT_PROFILE_EDIT", "Edit Zakat Profile"),
            action("ZAKAT_CHARITY", "Zakat & Charity", "BENEFICIARY_CREATE", "CHARITY_BENEFICIARY_CREATE", "Create Charity Beneficiary"),
            action("ZAKAT_CHARITY", "Zakat & Charity", "BENEFICIARY_EDIT", "CHARITY_BENEFICIARY_EDIT", "Edit Charity Beneficiary"),
            action("ZAKAT_CHARITY", "Zakat & Charity", "BENEFICIARY_ARCHIVE", "CHARITY_BENEFICIARY_ARCHIVE", "Archive Charity Beneficiary"),
            action("ZAKAT_CHARITY", "Zakat & Charity", "BENEFICIARY_RESTORE", "CHARITY_BENEFICIARY_RESTORE", "Restore Charity Beneficiary"),
            action("ZAKAT_CHARITY", "Zakat & Charity", "CALCULATE", "ZAKAT_CALCULATE", "Run Zakat Calculation"),
            action("ZAKAT_CHARITY", "Zakat & Charity", "PAYOUT_CREATE", "CHARITY_PAYOUT_CREATE", "Create Charity Payout"),
            module("NOTIFICATION_ALERTS", "Notification & Alerts", "NOTIFICATION_ALERTS_ACCESS", "Module Access"),
            action("NOTIFICATION_ALERTS", "Notification & Alerts", "TEMPLATE_CREATE", "NOTIFICATION_TEMPLATE_CREATE", "Create Notification Template"),
            action("NOTIFICATION_ALERTS", "Notification & Alerts", "TEMPLATE_EDIT", "NOTIFICATION_TEMPLATE_EDIT", "Edit Notification Template"),
            action("NOTIFICATION_ALERTS", "Notification & Alerts", "TEMPLATE_ARCHIVE", "NOTIFICATION_TEMPLATE_ARCHIVE", "Archive Notification Template"),
            action("NOTIFICATION_ALERTS", "Notification & Alerts", "TEMPLATE_RESTORE", "NOTIFICATION_TEMPLATE_RESTORE", "Restore Notification Template"),
            action("NOTIFICATION_ALERTS", "Notification & Alerts", "EVENT_RULE_CREATE", "NOTIFICATION_EVENT_RULE_CREATE", "Create Notification Event Rule"),
            action("NOTIFICATION_ALERTS", "Notification & Alerts", "RETRY", "NOTIFICATION_RETRY", "Retry Notification Delivery"),
            module("INTEGRATION_MANAGEMENT", "Integration Management", "INTEGRATION_MANAGEMENT_ACCESS", "Module Access"),
            action("INTEGRATION_MANAGEMENT", "Integration Management", "PROVIDER_CREATE", "INTEGRATION_PROVIDER_CREATE", "Create Integration Provider"),
            action("INTEGRATION_MANAGEMENT", "Integration Management", "PROVIDER_EDIT", "INTEGRATION_PROVIDER_EDIT", "Edit Integration Provider"),
            action("INTEGRATION_MANAGEMENT", "Integration Management", "PROVIDER_ARCHIVE", "INTEGRATION_PROVIDER_ARCHIVE", "Archive Integration Provider"),
            action("INTEGRATION_MANAGEMENT", "Integration Management", "PROVIDER_RESTORE", "INTEGRATION_PROVIDER_RESTORE", "Restore Integration Provider"),
            action("INTEGRATION_MANAGEMENT", "Integration Management", "PROVIDER_TEST", "INTEGRATION_PROVIDER_TEST", "Test Integration Provider"),
            action("INTEGRATION_MANAGEMENT", "Integration Management", "LOG_RETRY", "INTEGRATION_LOG_RETRY", "Retry Integration Execution"),
            module("REPORTING_REGULATORY", "Reporting & Regulatory", "REPORTING_REGULATORY_ACCESS", "Module Access"),
            action("REPORTING_REGULATORY", "Reporting & Regulatory", "MONTHLY_CLOSING_CREATE", "MONTHLY_CLOSING_CREATE", "Create Monthly Closing Run"),
            action("REPORTING_REGULATORY", "Reporting & Regulatory", "MONTHLY_CLOSING_SUBMIT", "MONTHLY_CLOSING_SUBMIT", "Submit Monthly Closing Run"),
            action("REPORTING_REGULATORY", "Reporting & Regulatory", "MONTHLY_CLOSING_APPROVE", "MONTHLY_CLOSING_APPROVE", "Approve Monthly Closing Run"),
            action("REPORTING_REGULATORY", "Reporting & Regulatory", "MONTHLY_CLOSING_REJECT", "MONTHLY_CLOSING_REJECT", "Reject Monthly Closing Run"),
            action("REPORTING_REGULATORY", "Reporting & Regulatory", "MONTHLY_CLOSING_REOPEN", "MONTHLY_CLOSING_REOPEN", "Reopen Monthly Closing Run"),
            module("SECURITY_AUDIT", "Security / Audit", "SECURITY_AUDIT_ACCESS", "Module Access"),
            action("SECURITY_AUDIT", "Security / Audit", "INVESTIGATION_ASSIGN", "SECURITY_INVESTIGATION_ASSIGN", "Assign Investigation Case"),
            action("SECURITY_AUDIT", "Security / Audit", "INVESTIGATION_CLOSE", "SECURITY_INVESTIGATION_CLOSE", "Close Investigation Case"),
            module("WORKFLOW_SUPPORT", "Workflow Support", "WORKFLOW_SUPPORT_ACCESS", "Module Access"),
            module("VERIFICATION", "Verification", "VERIFICATION_ACCESS", "Module Access"),
            action("VERIFICATION", "Verification", "SEND_EMAIL_OTP", "VERIFICATION_SEND_EMAIL_OTP", "Send Email Verification OTP"),
            action("VERIFICATION", "Verification", "SEND_MOBILE_OTP", "VERIFICATION_SEND_MOBILE_OTP", "Send Mobile Verification OTP"),
            action("VERIFICATION", "Verification", "VERIFY_OTP", "VERIFICATION_VERIFY_OTP", "Verify OTP"),
            action("VERIFICATION", "Verification", "RESEND_OTP", "VERIFICATION_RESEND_OTP", "Resend OTP"),
            action("VERIFICATION", "Verification", "EXPIRE_OTP", "VERIFICATION_EXPIRE_OTP", "Expire OTP"),
            action("VERIFICATION", "Verification", "MARK_FAILED", "VERIFICATION_MARK_FAILED", "Mark OTP Failed"),
            action("VERIFICATION", "Verification", "PROVIDER_TEST", "VERIFICATION_PROVIDER_TEST", "Run Verification Provider Test"),
            module("CALCULATION_ENGINE", "Calculation Engine", "CALCULATION_ENGINE_ACCESS", "Module Access")
            ,
            action("CALCULATION_ENGINE", "Calculation Engine", "SIMULATE", "CALCULATION_SIMULATE", "Run Calculation Simulation")
    );

    private static final Map<String, StarterRoleDefinition> STARTER_ROLES = createStarterRoles();

    public RoleService(RoleRepository repo, RolePermissionRepository rolePermissionRepository, UserRepository userRepository) {
        this.repo = repo;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initializeSystemAdminPermissions() {
        initializeStarterRoles();
        repo.findByCode(SYSTEM_ADMIN)
                .ifPresent(role -> syncMissingPermissions(role, "SYSTEM_SYNC"));
    }

    @Transactional(readOnly = true)
    public RoleDashboardSummaryDto getDashboardSummary() {
        RoleDashboardSummaryDto dto = new RoleDashboardSummaryDto();
        dto.setTotalRoles((long) repo.findAll().size());
        dto.setActiveRoles(repo.countByStatus("ACTIVE"));
        dto.setInactiveRoles(repo.countByStatus("INACTIVE"));
        dto.setPermissionHeavyRoles((long) repo.findAll().stream()
                .filter(role -> rolePermissionRepository.countByRoleId(role.getId()) >= 10)
                .count());
        dto.setRecentRoles(repo.findRecent(8).stream().map(this::toDtoWithCounts).toList());
        return dto;
    }

    public RoleResponseDto create(RoleCreateRequestDto dto) {
        validate(dto, null);
        Role role = new Role();
        role.setCode(dto.getCode().trim().toUpperCase(Locale.ROOT));
        role.setName(dto.getName().trim());
        role.setDescription(blankToNull(dto.getDescription()));
        role.setStatus(parseStatus(dto.getStatus()));
        role.setCreatedBy(isBlank(dto.getActionBy()) ? "SYSTEM" : dto.getActionBy().trim());
        role.setUpdatedBy(isBlank(dto.getActionBy()) ? "SYSTEM" : dto.getActionBy().trim());
        return toDtoWithCounts(repo.save(role));
    }

    public List<RoleResponseDto> getAll() {
        return repo.findAll().stream().map(this::toDtoWithCounts).toList();
    }

    public List<RoleResponseDto> getDropdown() {
        return repo.findActive().stream().map(this::toDtoWithCounts).toList();
    }

    public RoleResponseDto getById(Long id) {
        return toDtoWithDetails(getRole(id));
    }

    public RoleResponseDto update(Long id, RoleCreateRequestDto dto) {
        validate(dto, id);
        Role role = getRole(id);
        if (SYSTEM_ADMIN.equalsIgnoreCase(role.getCode()) && !SYSTEM_ADMIN.equalsIgnoreCase(dto.getCode())) {
            throw new BadRequestException("SYSTEM_ADMIN role code is protected");
        }
        role.setCode(dto.getCode().trim().toUpperCase(Locale.ROOT));
        role.setName(dto.getName().trim());
        role.setDescription(blankToNull(dto.getDescription()));
        role.setStatus(parseStatus(dto.getStatus()));
        role.setUpdatedBy(isBlank(dto.getActionBy()) ? "SYSTEM" : dto.getActionBy().trim());
        return toDtoWithCounts(repo.save(role));
    }

    public RoleResponseDto deactivate(Long id) {
        Role role = getRole(id);
        protectArchive(role);
        role.setStatus(RoleStatus.INACTIVE);
        role.setUpdatedBy("SYSTEM");
        return toDtoWithCounts(repo.save(role));
    }

    public RoleResponseDto restore(Long id) {
        Role role = getRole(id);
        role.setStatus(RoleStatus.ACTIVE);
        role.setUpdatedBy("SYSTEM");
        return toDtoWithCounts(repo.save(role));
    }

    public List<RolePermissionResponseDto> getPermissions(Long roleId) {
        getRole(roleId);
        Map<String, RolePermission> existing = rolePermissionRepository.findByRoleId(roleId).stream()
                .collect(Collectors.toMap(RolePermission::getPermissionCode, item -> item, (a, b) -> a));
        return PERMISSION_CATALOG.stream()
                .map(def -> new RolePermissionResponseDto(
                        def.moduleName(),
                        def.actionName(),
                        def.permissionCode(),
                        def.displayName(),
                        existing.containsKey(def.permissionCode())
                                ? Boolean.TRUE.equals(existing.get(def.permissionCode()).getAllowFlag())
                                : false
                ))
                .sorted(Comparator.comparing(RolePermissionResponseDto::getModuleName).thenComparing(RolePermissionResponseDto::getActionName))
                .toList();
    }

    public List<RolePermissionResponseDto> mapPermissions(Long roleId, RolePermissionAssignDto dto) {
        Role role = getRole(roleId);
        rolePermissionRepository.deleteByRoleId(roleId);
        Set<String> requested = dto == null || dto.getPermissionCodes() == null
                ? Set.of()
                : dto.getPermissionCodes().stream().filter(code -> !isBlank(code)).map(code -> code.trim().toUpperCase(Locale.ROOT)).collect(Collectors.toSet());
        Map<String, PermissionDefinition> catalog = PERMISSION_CATALOG.stream()
                .collect(Collectors.toMap(PermissionDefinition::permissionCode, item -> item));
        for (String permissionCode : requested) {
            PermissionDefinition def = catalog.get(permissionCode);
            if (def == null) continue;
            RolePermission entity = new RolePermission();
            entity.setRole(role);
            entity.setModuleName(def.moduleName());
            entity.setActionName(def.actionName());
            entity.setPermissionCode(def.permissionCode());
            entity.setDisplayName(def.displayName());
            entity.setAllowFlag(true);
            entity.setCreatedBy(isBlank(dto.getCreatedBy()) ? "SYSTEM" : dto.getCreatedBy().trim());
            rolePermissionRepository.save(entity);
        }
        return getPermissions(roleId);
    }

    private void syncMissingPermissions(Role role, String createdBy) {
        if (role == null || role.getId() == null) {
            return;
        }
        Set<String> existingCodes = rolePermissionRepository.findByRoleId(role.getId()).stream()
                .map(RolePermission::getPermissionCode)
                .filter(code -> code != null && !code.isBlank())
                .map(code -> code.trim().toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());
        for (PermissionDefinition def : PERMISSION_CATALOG) {
            if (existingCodes.contains(def.permissionCode())) {
                continue;
            }
            RolePermission entity = new RolePermission();
            entity.setRole(role);
            entity.setModuleName(def.moduleName());
            entity.setActionName(def.actionName());
            entity.setPermissionCode(def.permissionCode());
            entity.setDisplayName(def.displayName());
            entity.setAllowFlag(true);
            entity.setCreatedBy(createdBy);
            rolePermissionRepository.save(entity);
        }
    }

    private void initializeStarterRoles() {
        for (StarterRoleDefinition starter : STARTER_ROLES.values()) {
            Role role = repo.findByCode(starter.code()).orElseGet(() -> {
                Role entity = new Role();
                entity.setCode(starter.code());
                entity.setName(starter.name());
                entity.setDescription(starter.description());
                entity.setStatus(RoleStatus.ACTIVE);
                entity.setCreatedBy("SYSTEM_BOOTSTRAP");
                entity.setUpdatedBy("SYSTEM_BOOTSTRAP");
                return repo.save(entity);
            });
            syncPermissionBundle(role, starter.permissionCodes(), "SYSTEM_BOOTSTRAP");
        }
    }

    private void syncPermissionBundle(Role role, Set<String> permissionCodes, String createdBy) {
        if (role == null || role.getId() == null || permissionCodes == null || permissionCodes.isEmpty()) {
            return;
        }
        Map<String, RolePermission> existing = rolePermissionRepository.findByRoleId(role.getId()).stream()
                .filter(item -> item.getPermissionCode() != null && !item.getPermissionCode().isBlank())
                .collect(Collectors.toMap(
                        item -> item.getPermissionCode().trim().toUpperCase(Locale.ROOT),
                        item -> item,
                        (first, second) -> first
                ));
        Map<String, PermissionDefinition> catalog = PERMISSION_CATALOG.stream()
                .collect(Collectors.toMap(PermissionDefinition::permissionCode, item -> item, (first, second) -> first));

        for (String code : permissionCodes) {
            String normalized = code == null ? null : code.trim().toUpperCase(Locale.ROOT);
            if (normalized == null || normalized.isEmpty() || existing.containsKey(normalized)) {
                continue;
            }
            PermissionDefinition def = catalog.get(normalized);
            if (def == null) {
                continue;
            }
            RolePermission entity = new RolePermission();
            entity.setRole(role);
            entity.setModuleName(def.moduleName());
            entity.setActionName(def.actionName());
            entity.setPermissionCode(def.permissionCode());
            entity.setDisplayName(def.displayName());
            entity.setAllowFlag(true);
            entity.setCreatedBy(createdBy);
            rolePermissionRepository.save(entity);
        }
    }

    private void validate(RoleCreateRequestDto dto, Long id) {
        if (dto == null) throw new BadRequestException("Role body is required");
        if (isBlank(dto.getCode())) throw new BadRequestException("Role code is required");
        if (isBlank(dto.getName())) throw new BadRequestException("Role name is required");
        boolean codeExists = id == null ? repo.existsByCode(dto.getCode()) : repo.existsByCodeAndIdNot(dto.getCode(), id);
        if (codeExists) throw new BadRequestException("Role code already exists");
        boolean nameExists = id == null ? repo.existsByName(dto.getName()) : repo.existsByNameAndIdNot(dto.getName(), id);
        if (nameExists) throw new BadRequestException("Role name already exists");
    }

    private void protectArchive(Role role) {
        if (SYSTEM_ADMIN.equalsIgnoreCase(role.getCode())) {
            throw new BadRequestException("SYSTEM_ADMIN role is protected");
        }
        if (userRepository.countActiveByRoleId(role.getId()) > 0) {
            throw new BadRequestException("Cannot deactivate a role assigned to active users");
        }
    }

    private Role getRole(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    private RoleStatus parseStatus(String value) {
        if (isBlank(value)) return RoleStatus.ACTIVE;
        try {
            return RoleStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid role status");
        }
    }

    private RoleResponseDto toDtoWithDetails(Role r) {
        RoleResponseDto dto = toDtoWithCounts(r);
        dto.setPermissions(getPermissions(r.getId()));
        return dto;
    }

    private RoleResponseDto toDtoWithCounts(Role r) {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setId(r.getId());
        dto.setCode(r.getCode());
        dto.setName(r.getName());
        dto.setDescription(r.getDescription());
        dto.setStatus(r.getStatus().name());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        dto.setCreatedBy(r.getCreatedBy());
        dto.setUpdatedBy(r.getUpdatedBy());
        dto.setAssignedUserCount(userRepository.countByRoleId(r.getId()));
        dto.setPermissionCount(rolePermissionRepository.countByRoleId(r.getId()));
        return dto;
    }

    private static PermissionDefinition module(String moduleName, String displayName, String permissionCode, String label) {
        return new PermissionDefinition(moduleName, "ACCESS", permissionCode, displayName + " - " + label);
    }

    private static PermissionDefinition action(String moduleName, String displayName, String actionName, String permissionCode, String label) {
        return new PermissionDefinition(moduleName, actionName, permissionCode, displayName + " - " + label);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private static Map<String, StarterRoleDefinition> createStarterRoles() {
        Map<String, StarterRoleDefinition> roles = new LinkedHashMap<>();

        roles.put("SYSTEM_ADMIN", starter(
                "SYSTEM_ADMIN",
                "System Admin",
                "Manage roles, permissions, configuration, audit and cross-module administration",
                allPermissionCodes()
        ));
        roles.put("CUSTOMER", starter(
                "CUSTOMER",
                "Customer / Account Holder",
                "View customer-facing account, financing, card, transaction and statement surfaces",
                perms(
                        "ACCOUNT_MANAGEMENT_ACCESS",
                        "TRANSACTIONS_ACCESS",
                        "STATEMENTS_ACCESS",
                        "STATEMENT_CUSTOMER_REQUEST",
                        "FINANCING_ACCESS",
                        "CARD_MANAGEMENT_ACCESS",
                        "DEPOSIT_SCHEMES_ACCESS"
                )
        ));
        roles.put("BRANCH_STAFF", starter(
                "BRANCH_STAFF",
                "Branch Staff",
                "Collect onboarding documents and assist branch-side operational intake",
                perms(
                        "CUSTOMER_MANAGEMENT_ACCESS",
                        "CUSTOMER_CREATE",
                        "CUSTOMER_EDIT",
                        "CUSTOMER_ADDRESS_MANAGE",
                        "CUSTOMER_IDENTITY_MANAGE",
                        "KYC_MANAGEMENT_ACCESS",
                        "KYC_CREATE",
                        "KYC_EDIT",
                        "KYC_SUBMIT",
                        "KYC_DOCUMENT_UPLOAD",
                        "ACCOUNT_MANAGEMENT_ACCESS",
                        "ACCOUNT_REQUEST_CREATE",
                        "ACCOUNT_REQUEST_EDIT",
                        "ACCOUNT_REQUEST_SUBMIT",
                        "STATEMENTS_ACCESS",
                        "STATEMENT_CUSTOMER_REQUEST"
                )
        ));
        roles.put("TELLER", starter(
                "TELLER",
                "Teller / Branch Staff",
                "Perform branch cash and transaction posting tasks",
                perms(
                        "TRANSACTIONS_ACCESS",
                        "TRANSACTION_DEPOSIT",
                        "TRANSACTION_WITHDRAW",
                        "TRANSACTION_TRANSFER",
                        "TRANSACTION_CHEQUE_CLEARING",
                        "TRANSACTION_STANDING_INSTRUCTION_CREATE",
                        "STATEMENTS_ACCESS",
                        "STATEMENT_CUSTOMER_REQUEST",
                        "CARD_MANAGEMENT_ACCESS",
                        "CARD_PIN_EVENT"
                )
        ));
        roles.put("OPERATIONS_OFFICER", starter(
                "OPERATIONS_OFFICER",
                "Operations Officer",
                "Handle account verification, approval actions, transaction reversal and contract operations",
                perms(
                        "CUSTOMER_MANAGEMENT_ACCESS",
                        "CUSTOMER_ACTIVATE",
                        "CUSTOMER_BLOCK",
                        "KYC_MANAGEMENT_ACCESS",
                        "KYC_VERIFY",
                        "KYC_APPROVE",
                        "KYC_REJECT",
                        "KYC_RETURN",
                        "ACCOUNT_MANAGEMENT_ACCESS",
                        "ACCOUNT_REQUEST_VERIFY",
                        "ACCOUNT_REQUEST_APPROVE",
                        "ACCOUNT_REQUEST_REJECT",
                        "ACCOUNT_REQUEST_RETURN",
                        "ACCOUNT_ACTIVATE",
                        "ACCOUNT_BLOCK",
                        "ACCOUNT_FREEZE",
                        "ACCOUNT_CLOSE",
                        "TRANSACTIONS_ACCESS",
                        "TRANSACTION_REVERSE",
                        "CONTRACTS_ACCESS",
                        "CONTRACT_GENERATE",
                        "CARD_MANAGEMENT_ACCESS",
                        "CARD_ACTIVATE",
                        "CARD_BLOCK",
                        "CARD_UNBLOCK",
                        "STATEMENTS_ACCESS",
                        "STATEMENT_BRANCH_REQUEST",
                        "REPORTING_REGULATORY_ACCESS"
                )
        ));
        roles.put("INVESTMENT_OFFICER", starter(
                "INVESTMENT_OFFICER",
                "Investment Officer (Financing)",
                "Prepare, verify and disburse financing applications with related contracts and reports",
                perms(
                        "FINANCING_ACCESS",
                        "FINANCING_APPLICATION_CREATE",
                        "FINANCING_APPLICATION_EDIT",
                        "FINANCING_APPLICATION_SUBMIT",
                        "FINANCING_VERIFY",
                        "FINANCING_REVIEW",
                        "FINANCING_APPROVE",
                        "FINANCING_REJECT",
                        "FINANCING_RETURN",
                        "FINANCING_DISBURSE",
                        "FINANCING_COLLECT_PAYMENT",
                        "CONTRACTS_ACCESS",
                        "CONTRACT_GENERATE",
                        "SHARIAH_REVIEW_ACCESS",
                        "REPORTING_REGULATORY_ACCESS"
                )
        ));
        roles.put("SHARIAH_BOARD_MEMBER", starter(
                "SHARIAH_BOARD_MEMBER",
                "Shariah Board Member",
                "Review compliance, decide shariah cases and sign shariah-side contracts",
                perms(
                        "SHARIAH_REVIEW_ACCESS",
                        "SHARIAH_CHECKLIST_SAVE",
                        "SHARIAH_APPROVE",
                        "SHARIAH_REJECT",
                        "SHARIAH_RETURN",
                        "CONTRACTS_ACCESS",
                        "CONTRACT_SHARIAH_SIGN",
                        "REPORTING_REGULATORY_ACCESS"
                )
        ));
        roles.put("BRANCH_MANAGER", starter(
                "BRANCH_MANAGER",
                "Branch Manager",
                "Drive branch approvals, vault oversight and month-end branch submission",
                perms(
                        "BRANCH_MANAGEMENT_ACCESS",
                        "BRANCH_ASSIGN_USER",
                        "BRANCH_TELLER_LIMIT_MANAGE",
                        "BRANCH_VAULT_MANAGE",
                        "ACCOUNT_MANAGEMENT_ACCESS",
                        "ACCOUNT_REQUEST_APPROVE",
                        "ACCOUNT_REQUEST_REJECT",
                        "ACCOUNT_REQUEST_RETURN",
                        "ACCOUNT_ACTIVATE",
                        "ACCOUNT_BLOCK",
                        "TRANSACTIONS_ACCESS",
                        "TRANSACTION_REVERSE",
                        "STATEMENTS_ACCESS",
                        "STATEMENT_BRANCH_REQUEST",
                        "REPORTING_REGULATORY_ACCESS",
                        "MONTHLY_CLOSING_CREATE",
                        "MONTHLY_CLOSING_SUBMIT"
                )
        ));
        roles.put("MIS_OFFICER", starter(
                "MIS_OFFICER",
                "MIS Officer",
                "Generate and monitor enterprise reports, exports and monthly summaries",
                perms(
                        "REPORTING_REGULATORY_ACCESS",
                        "STATEMENTS_ACCESS",
                        "STATEMENT_BRANCH_REQUEST"
                )
        ));
        roles.put("COMPLIANCE_OFFICER", starter(
                "COMPLIANCE_OFFICER",
                "Compliance Officer",
                "Review compliance-sensitive KYC, shariah, reporting and security outcomes",
                perms(
                        "KYC_MANAGEMENT_ACCESS",
                        "SHARIAH_REVIEW_ACCESS",
                        "REPORTING_REGULATORY_ACCESS",
                        "MONTHLY_CLOSING_APPROVE",
                        "MONTHLY_CLOSING_REJECT",
                        "MONTHLY_CLOSING_REOPEN",
                        "SECURITY_AUDIT_ACCESS"
                )
        ));
        roles.put("INTERNAL_AUDITOR", starter(
                "INTERNAL_AUDITOR",
                "Internal Auditor",
                "Review exported reports, statements and audit evidence across modules",
                perms(
                        "REPORTING_REGULATORY_ACCESS",
                        "SECURITY_AUDIT_ACCESS",
                        "STATEMENTS_ACCESS",
                        "VERIFICATION_ACCESS"
                )
        ));
        roles.put("RECOVERY_OFFICER", starter(
                "RECOVERY_OFFICER",
                "Recovery Officer",
                "Track delinquent financing accounts and collect recovery payments",
                perms(
                        "FINANCING_ACCESS",
                        "FINANCING_COLLECT_PAYMENT",
                        "REPORTING_REGULATORY_ACCESS"
                )
        ));
        roles.put("TREASURY_FINANCE_OFFICER", starter(
                "TREASURY_FINANCE_OFFICER",
                "Treasury / Finance Officer",
                "Run profit, treasury-style oversight and month-end financial sign-off support",
                perms(
                        "PROFIT_MANAGEMENT_ACCESS",
                        "PROFIT_POSTING_RUN",
                        "REPORTING_REGULATORY_ACCESS",
                        "MONTHLY_CLOSING_APPROVE",
                        "MONTHLY_CLOSING_REJECT",
                        "MONTHLY_CLOSING_REOPEN"
                )
        ));

        return roles;
    }

    private static StarterRoleDefinition starter(String code, String name, String description, Set<String> permissionCodes) {
        return new StarterRoleDefinition(code, name, description, permissionCodes);
    }

    private static Set<String> perms(String... values) {
        return Set.of(values);
    }

    private static Set<String> allPermissionCodes() {
        return PERMISSION_CATALOG.stream()
                .map(PermissionDefinition::permissionCode)
                .collect(Collectors.toSet());
    }

    private record PermissionDefinition(String moduleName, String actionName, String permissionCode, String displayName) {}
    private record StarterRoleDefinition(String code, String name, String description, Set<String> permissionCodes) {}
}
