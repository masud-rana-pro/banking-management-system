-- Module 1 sample data for Role & Permission Management.
-- Canonical role set is limited to the 7 playbook-facing user roles:
-- SYSTEM_ADMIN, BRANCH_MANAGER, TELLER, OPERATIONS_OFFICER,
-- INVESTMENT_OFFICER, SHARIAH_BOARD_MEMBER, CUSTOMER

SET @has_created_by := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'roles'
      AND column_name = 'created_by'
);
SET @sql_created_by := IF(@has_created_by = 0, 'ALTER TABLE roles ADD COLUMN created_by varchar(120) NULL', 'SELECT 1');
PREPARE stmt_created_by FROM @sql_created_by;
EXECUTE stmt_created_by;
DEALLOCATE PREPARE stmt_created_by;

SET @has_updated_by := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'roles'
      AND column_name = 'updated_by'
);
SET @sql_updated_by := IF(@has_updated_by = 0, 'ALTER TABLE roles ADD COLUMN updated_by varchar(120) NULL', 'SELECT 1');
PREPARE stmt_updated_by FROM @sql_updated_by;
EXECUTE stmt_updated_by;
DEALLOCATE PREPARE stmt_updated_by;

UPDATE roles
SET created_by = COALESCE(NULLIF(created_by, ''), 'MODULE_1_SEED'),
    updated_by = COALESCE(NULLIF(updated_by, ''), 'MODULE_1_SEED');

CREATE TABLE IF NOT EXISTS role_permission (
    id bigint NOT NULL AUTO_INCREMENT,
    role_id bigint NOT NULL,
    module_name varchar(100) NOT NULL,
    action_name varchar(100) NOT NULL,
    permission_code varchar(150) NOT NULL,
    display_name varchar(180) NOT NULL,
    allow_flag bit(1) NOT NULL DEFAULT b'1',
    created_at datetime(6) NOT NULL,
    created_by varchar(120) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_role_permission_role_id (role_id),
    KEY idx_role_permission_code (permission_code),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DELETE rp
FROM role_permission rp
INNER JOIN roles r ON r.id = rp.role_id
WHERE r.code NOT IN (
    'SYSTEM_ADMIN',
    'BRANCH_MANAGER',
    'TELLER',
    'OPERATIONS_OFFICER',
    'INVESTMENT_OFFICER',
    'SHARIAH_BOARD_MEMBER',
    'CUSTOMER'
);

DELETE ur
FROM user_role ur
INNER JOIN roles r ON r.id = ur.role_id
WHERE r.code NOT IN (
    'SYSTEM_ADMIN',
    'BRANCH_MANAGER',
    'TELLER',
    'OPERATIONS_OFFICER',
    'INVESTMENT_OFFICER',
    'SHARIAH_BOARD_MEMBER',
    'CUSTOMER'
);

DELETE FROM roles
WHERE code NOT IN (
    'SYSTEM_ADMIN',
    'BRANCH_MANAGER',
    'TELLER',
    'OPERATIONS_OFFICER',
    'INVESTMENT_OFFICER',
    'SHARIAH_BOARD_MEMBER',
    'CUSTOMER'
);

UPDATE roles
SET name = CASE code
    WHEN 'SYSTEM_ADMIN' THEN 'System Admin'
    WHEN 'BRANCH_MANAGER' THEN 'Branch Manager'
    WHEN 'TELLER' THEN 'Teller'
    WHEN 'OPERATIONS_OFFICER' THEN 'Operations Officer'
    WHEN 'INVESTMENT_OFFICER' THEN 'Investment Officer'
    WHEN 'SHARIAH_BOARD_MEMBER' THEN 'Shariah Board Member'
    WHEN 'CUSTOMER' THEN 'Customer'
    ELSE name
END,
description = CASE code
    WHEN 'SYSTEM_ADMIN' THEN 'Full administrative control over role, user, configuration and module access.'
    WHEN 'BRANCH_MANAGER' THEN 'Oversees branch operations, staff, vault and branch performance.'
    WHEN 'TELLER' THEN 'Handles teller operations, cash movement and customer-assisted transactions.'
    WHEN 'OPERATIONS_OFFICER' THEN 'Manages customer, KYC, account and transaction operations.'
    WHEN 'INVESTMENT_OFFICER' THEN 'Manages investment, financing, profit and contract operations.'
    WHEN 'SHARIAH_BOARD_MEMBER' THEN 'Reviews shariah compliance, decisions and supporting reports.'
    WHEN 'CUSTOMER' THEN 'Customer-facing access to statements, schemes, requests and self-service journeys.'
    ELSE description
END,
status = CASE code
    WHEN 'CUSTOMER' THEN 'ACTIVE'
    ELSE 'ACTIVE'
END,
updated_by = 'MODULE_1_SEED';

DELETE FROM role_permission WHERE created_by = 'MODULE_1_SEED';

INSERT INTO role_permission (role_id, module_name, action_name, permission_code, display_name, allow_flag, created_at, created_by)
SELECT r.id, p.module_name, p.action_name, p.permission_code, p.display_name, b'1', NOW(), 'MODULE_1_SEED'
FROM roles r
JOIN (
    SELECT 'SYSTEM_ADMIN' AS role_code, 'ADMIN', 'ACCESS', 'ADMIN_DASHBOARD_ACCESS', 'Admin Dashboard - Dashboard Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'ROLE_MANAGEMENT', 'ACCESS', 'ROLE_MANAGEMENT_ACCESS', 'Role Management - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'ROLE_MANAGEMENT', 'VIEW', 'ROLE_VIEW', 'Role Management - View Roles'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'ROLE_MANAGEMENT', 'CREATE', 'ROLE_CREATE', 'Role Management - Create Role'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'ROLE_MANAGEMENT', 'EDIT', 'ROLE_EDIT', 'Role Management - Edit Role'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'ROLE_MANAGEMENT', 'ARCHIVE', 'ROLE_ARCHIVE', 'Role Management - Archive Role'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'ROLE_MANAGEMENT', 'RESTORE', 'ROLE_RESTORE', 'Role Management - Restore Role'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'ROLE_MANAGEMENT', 'MAP_PERMISSIONS', 'ROLE_MAP_PERMISSIONS', 'Role Management - Map Permissions'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'USER_MANAGEMENT', 'ACCESS', 'USER_MANAGEMENT_ACCESS', 'User Management - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'USER_MANAGEMENT', 'VIEW', 'USER_VIEW', 'User Management - View Users'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'USER_MANAGEMENT', 'CREATE', 'USER_CREATE', 'User Management - Create User'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'USER_MANAGEMENT', 'EDIT', 'USER_EDIT', 'User Management - Edit User'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'USER_MANAGEMENT', 'ARCHIVE', 'USER_ARCHIVE', 'User Management - Archive User'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'USER_MANAGEMENT', 'RESTORE', 'USER_RESTORE', 'User Management - Restore User'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'USER_MANAGEMENT', 'LOCK', 'USER_LOCK', 'User Management - Lock User'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'USER_MANAGEMENT', 'UNLOCK', 'USER_UNLOCK', 'User Management - Unlock User'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'USER_MANAGEMENT', 'RESET_PASSWORD', 'USER_RESET_PASSWORD', 'User Management - Reset Password'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'USER_MANAGEMENT', 'ASSIGN_ROLE', 'USER_ASSIGN_ROLE', 'User Management - Assign Role'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'LOOKUP_CONFIG', 'ACCESS', 'LOOKUP_CONFIG_ACCESS', 'Lookup / Config - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'BRANCH_MANAGEMENT', 'ACCESS', 'BRANCH_MANAGEMENT_ACCESS', 'Branch Management - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'ATM_CDM', 'ACCESS', 'ATM_CDM_ACCESS', 'ATM / CDM - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'CUSTOMER_MANAGEMENT', 'ACCESS', 'CUSTOMER_MANAGEMENT_ACCESS', 'Customer Management - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'KYC_MANAGEMENT', 'ACCESS', 'KYC_MANAGEMENT_ACCESS', 'KYC Management - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'ACCOUNT_MANAGEMENT', 'ACCESS', 'ACCOUNT_MANAGEMENT_ACCESS', 'Account Management - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'TRANSACTIONS', 'ACCESS', 'TRANSACTIONS_ACCESS', 'Transactions - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'PROFIT_MANAGEMENT', 'ACCESS', 'PROFIT_MANAGEMENT_ACCESS', 'Profit Management - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'CARD_MANAGEMENT', 'ACCESS', 'CARD_MANAGEMENT_ACCESS', 'Card Management - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'STATEMENTS', 'ACCESS', 'STATEMENTS_ACCESS', 'Statements - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'DEPOSIT_SCHEMES', 'ACCESS', 'DEPOSIT_SCHEMES_ACCESS', 'Deposit Schemes - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'FINANCING', 'ACCESS', 'FINANCING_ACCESS', 'Financing - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'CONTRACTS', 'ACCESS', 'CONTRACTS_ACCESS', 'Contracts - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'SHARIAH_REVIEW', 'ACCESS', 'SHARIAH_REVIEW_ACCESS', 'Shariah Review - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'ZAKAT_CHARITY', 'ACCESS', 'ZAKAT_CHARITY_ACCESS', 'Zakat & Charity - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'NOTIFICATION_ALERTS', 'ACCESS', 'NOTIFICATION_ALERTS_ACCESS', 'Notification & Alerts - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'INTEGRATION_MANAGEMENT', 'ACCESS', 'INTEGRATION_MANAGEMENT_ACCESS', 'Integration Management - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'REPORTING_REGULATORY', 'ACCESS', 'REPORTING_REGULATORY_ACCESS', 'Reporting & Regulatory - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'SECURITY_AUDIT', 'ACCESS', 'SECURITY_AUDIT_ACCESS', 'Security / Audit - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'WORKFLOW_SUPPORT', 'ACCESS', 'WORKFLOW_SUPPORT_ACCESS', 'Workflow Support - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'VERIFICATION', 'ACCESS', 'VERIFICATION_ACCESS', 'Verification - Module Access'
    UNION ALL SELECT 'SYSTEM_ADMIN', 'CALCULATION_ENGINE', 'ACCESS', 'CALCULATION_ENGINE_ACCESS', 'Calculation Engine - Module Access'

    UNION ALL SELECT 'BRANCH_MANAGER', 'ADMIN', 'ACCESS', 'ADMIN_DASHBOARD_ACCESS', 'Admin Dashboard - Dashboard Access'
    UNION ALL SELECT 'BRANCH_MANAGER', 'BRANCH_MANAGEMENT', 'ACCESS', 'BRANCH_MANAGEMENT_ACCESS', 'Branch Management - Module Access'
    UNION ALL SELECT 'BRANCH_MANAGER', 'ATM_CDM', 'ACCESS', 'ATM_CDM_ACCESS', 'ATM / CDM - Module Access'
    UNION ALL SELECT 'BRANCH_MANAGER', 'CUSTOMER_MANAGEMENT', 'ACCESS', 'CUSTOMER_MANAGEMENT_ACCESS', 'Customer Management - Module Access'
    UNION ALL SELECT 'BRANCH_MANAGER', 'KYC_MANAGEMENT', 'ACCESS', 'KYC_MANAGEMENT_ACCESS', 'KYC Management - Module Access'
    UNION ALL SELECT 'BRANCH_MANAGER', 'ACCOUNT_MANAGEMENT', 'ACCESS', 'ACCOUNT_MANAGEMENT_ACCESS', 'Account Management - Module Access'
    UNION ALL SELECT 'BRANCH_MANAGER', 'TRANSACTIONS', 'ACCESS', 'TRANSACTIONS_ACCESS', 'Transactions - Module Access'
    UNION ALL SELECT 'BRANCH_MANAGER', 'STATEMENTS', 'ACCESS', 'STATEMENTS_ACCESS', 'Statements - Module Access'
    UNION ALL SELECT 'BRANCH_MANAGER', 'CARD_MANAGEMENT', 'ACCESS', 'CARD_MANAGEMENT_ACCESS', 'Card Management - Module Access'
    UNION ALL SELECT 'BRANCH_MANAGER', 'WORKFLOW_SUPPORT', 'ACCESS', 'WORKFLOW_SUPPORT_ACCESS', 'Workflow Support - Module Access'

    UNION ALL SELECT 'TELLER', 'ADMIN', 'ACCESS', 'ADMIN_DASHBOARD_ACCESS', 'Admin Dashboard - Dashboard Access'
    UNION ALL SELECT 'TELLER', 'BRANCH_MANAGEMENT', 'ACCESS', 'BRANCH_MANAGEMENT_ACCESS', 'Branch Management - Module Access'
    UNION ALL SELECT 'TELLER', 'ATM_CDM', 'ACCESS', 'ATM_CDM_ACCESS', 'ATM / CDM - Module Access'
    UNION ALL SELECT 'TELLER', 'CUSTOMER_MANAGEMENT', 'ACCESS', 'CUSTOMER_MANAGEMENT_ACCESS', 'Customer Management - Module Access'
    UNION ALL SELECT 'TELLER', 'ACCOUNT_MANAGEMENT', 'ACCESS', 'ACCOUNT_MANAGEMENT_ACCESS', 'Account Management - Module Access'
    UNION ALL SELECT 'TELLER', 'TRANSACTIONS', 'ACCESS', 'TRANSACTIONS_ACCESS', 'Transactions - Module Access'
    UNION ALL SELECT 'TELLER', 'STATEMENTS', 'ACCESS', 'STATEMENTS_ACCESS', 'Statements - Module Access'

    UNION ALL SELECT 'OPERATIONS_OFFICER', 'ADMIN', 'ACCESS', 'ADMIN_DASHBOARD_ACCESS', 'Admin Dashboard - Dashboard Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'BRANCH_MANAGEMENT', 'ACCESS', 'BRANCH_MANAGEMENT_ACCESS', 'Branch Management - Module Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'ATM_CDM', 'ACCESS', 'ATM_CDM_ACCESS', 'ATM / CDM - Module Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'CUSTOMER_MANAGEMENT', 'ACCESS', 'CUSTOMER_MANAGEMENT_ACCESS', 'Customer Management - Module Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'KYC_MANAGEMENT', 'ACCESS', 'KYC_MANAGEMENT_ACCESS', 'KYC Management - Module Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'ACCOUNT_MANAGEMENT', 'ACCESS', 'ACCOUNT_MANAGEMENT_ACCESS', 'Account Management - Module Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'TRANSACTIONS', 'ACCESS', 'TRANSACTIONS_ACCESS', 'Transactions - Module Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'PROFIT_MANAGEMENT', 'ACCESS', 'PROFIT_MANAGEMENT_ACCESS', 'Profit Management - Module Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'CARD_MANAGEMENT', 'ACCESS', 'CARD_MANAGEMENT_ACCESS', 'Card Management - Module Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'STATEMENTS', 'ACCESS', 'STATEMENTS_ACCESS', 'Statements - Module Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'NOTIFICATION_ALERTS', 'ACCESS', 'NOTIFICATION_ALERTS_ACCESS', 'Notification & Alerts - Module Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'WORKFLOW_SUPPORT', 'ACCESS', 'WORKFLOW_SUPPORT_ACCESS', 'Workflow Support - Module Access'
    UNION ALL SELECT 'OPERATIONS_OFFICER', 'VERIFICATION', 'ACCESS', 'VERIFICATION_ACCESS', 'Verification - Module Access'

    UNION ALL SELECT 'INVESTMENT_OFFICER', 'ADMIN', 'ACCESS', 'ADMIN_DASHBOARD_ACCESS', 'Admin Dashboard - Dashboard Access'
    UNION ALL SELECT 'INVESTMENT_OFFICER', 'ACCOUNT_MANAGEMENT', 'ACCESS', 'ACCOUNT_MANAGEMENT_ACCESS', 'Account Management - Module Access'
    UNION ALL SELECT 'INVESTMENT_OFFICER', 'PROFIT_MANAGEMENT', 'ACCESS', 'PROFIT_MANAGEMENT_ACCESS', 'Profit Management - Module Access'
    UNION ALL SELECT 'INVESTMENT_OFFICER', 'DEPOSIT_SCHEMES', 'ACCESS', 'DEPOSIT_SCHEMES_ACCESS', 'Deposit Schemes - Module Access'
    UNION ALL SELECT 'INVESTMENT_OFFICER', 'FINANCING', 'ACCESS', 'FINANCING_ACCESS', 'Financing - Module Access'
    UNION ALL SELECT 'INVESTMENT_OFFICER', 'CONTRACTS', 'ACCESS', 'CONTRACTS_ACCESS', 'Contracts - Module Access'
    UNION ALL SELECT 'INVESTMENT_OFFICER', 'SHARIAH_REVIEW', 'ACCESS', 'SHARIAH_REVIEW_ACCESS', 'Shariah Review - Module Access'
    UNION ALL SELECT 'INVESTMENT_OFFICER', 'REPORTING_REGULATORY', 'ACCESS', 'REPORTING_REGULATORY_ACCESS', 'Reporting & Regulatory - Module Access'
    UNION ALL SELECT 'INVESTMENT_OFFICER', 'WORKFLOW_SUPPORT', 'ACCESS', 'WORKFLOW_SUPPORT_ACCESS', 'Workflow Support - Module Access'
    UNION ALL SELECT 'INVESTMENT_OFFICER', 'CALCULATION_ENGINE', 'ACCESS', 'CALCULATION_ENGINE_ACCESS', 'Calculation Engine - Module Access'

    UNION ALL SELECT 'SHARIAH_BOARD_MEMBER', 'ADMIN', 'ACCESS', 'ADMIN_DASHBOARD_ACCESS', 'Admin Dashboard - Dashboard Access'
    UNION ALL SELECT 'SHARIAH_BOARD_MEMBER', 'SHARIAH_REVIEW', 'ACCESS', 'SHARIAH_REVIEW_ACCESS', 'Shariah Review - Module Access'
    UNION ALL SELECT 'SHARIAH_BOARD_MEMBER', 'ZAKAT_CHARITY', 'ACCESS', 'ZAKAT_CHARITY_ACCESS', 'Zakat & Charity - Module Access'
    UNION ALL SELECT 'SHARIAH_BOARD_MEMBER', 'REPORTING_REGULATORY', 'ACCESS', 'REPORTING_REGULATORY_ACCESS', 'Reporting & Regulatory - Module Access'
    UNION ALL SELECT 'SHARIAH_BOARD_MEMBER', 'WORKFLOW_SUPPORT', 'ACCESS', 'WORKFLOW_SUPPORT_ACCESS', 'Workflow Support - Module Access'
    UNION ALL SELECT 'SHARIAH_BOARD_MEMBER', 'CALCULATION_ENGINE', 'ACCESS', 'CALCULATION_ENGINE_ACCESS', 'Calculation Engine - Module Access'

    UNION ALL SELECT 'CUSTOMER', 'ADMIN', 'ACCESS', 'ADMIN_DASHBOARD_ACCESS', 'Admin Dashboard - Dashboard Access'
    UNION ALL SELECT 'CUSTOMER', 'CUSTOMER_MANAGEMENT', 'ACCESS', 'CUSTOMER_MANAGEMENT_ACCESS', 'Customer Management - Module Access'
    UNION ALL SELECT 'CUSTOMER', 'ACCOUNT_MANAGEMENT', 'ACCESS', 'ACCOUNT_MANAGEMENT_ACCESS', 'Account Management - Module Access'
    UNION ALL SELECT 'CUSTOMER', 'STATEMENTS', 'ACCESS', 'STATEMENTS_ACCESS', 'Statements - Module Access'
    UNION ALL SELECT 'CUSTOMER', 'DEPOSIT_SCHEMES', 'ACCESS', 'DEPOSIT_SCHEMES_ACCESS', 'Deposit Schemes - Module Access'
    UNION ALL SELECT 'CUSTOMER', 'FINANCING', 'ACCESS', 'FINANCING_ACCESS', 'Financing - Module Access'
    UNION ALL SELECT 'CUSTOMER', 'CONTRACTS', 'ACCESS', 'CONTRACTS_ACCESS', 'Contracts - Module Access'
    UNION ALL SELECT 'CUSTOMER', 'ZAKAT_CHARITY', 'ACCESS', 'ZAKAT_CHARITY_ACCESS', 'Zakat & Charity - Module Access'
) p(role_code, module_name, action_name, permission_code, display_name)
ON p.role_code = r.code;
