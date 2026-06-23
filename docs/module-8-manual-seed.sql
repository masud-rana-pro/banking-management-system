SET NAMES utf8mb4;

START TRANSACTION;

SET @has_requested_date := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'account_opening_request'
    AND COLUMN_NAME = 'requested_date'
);

SET @alter_account_opening_request := IF(
  @has_requested_date = 0,
  'ALTER TABLE account_opening_request ADD COLUMN requested_date date NOT NULL DEFAULT ''2026-04-01'' AFTER branch_id',
  'SELECT 1'
);

PREPARE stmt_requested_date FROM @alter_account_opening_request;
EXECUTE stmt_requested_date;
DEALLOCATE PREPARE stmt_requested_date;

UPDATE account_type
SET
  type_code = 'ACT-001',
  type_name = 'Mudarabah Savings Basic',
  code = 'ACT-001',
  name = 'Mudarabah Savings Basic',
  account_category = 'SAVINGS',
  account_subcategory = 'MUDARABAH_BASIC',
  shariah_contract_type = 'MUDARABAH',
  currency_code = 'BDT',
  minimum_opening_balance = 1000.00,
  minimum_balance = 1000.00,
  profit_applicable = b'1',
  psr_required = b'1',
  withdrawal_allowed = b'1',
  status = 'ACTIVE',
  updated_at = NOW()
WHERE id = 1;

UPDATE account_type
SET
  type_code = 'ACT-002',
  type_name = 'Wadia Current Personal',
  code = 'ACT-002',
  name = 'Wadia Current Personal',
  account_category = 'CURRENT',
  account_subcategory = 'WADIAH_PERSONAL',
  shariah_contract_type = 'WADIAH',
  currency_code = 'BDT',
  minimum_opening_balance = 2000.00,
  minimum_balance = 2000.00,
  profit_applicable = b'0',
  psr_required = b'0',
  withdrawal_allowed = b'1',
  status = 'ACTIVE',
  updated_at = NOW()
WHERE id = 2;

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-003', 'Mudarabah Savings Plus', 'ACT-003', 'Mudarabah Savings Plus', 'SAVINGS', 'MUDARABAH_PLUS',
       'MUDARABAH', 'BDT', 3000.00, 3000.00, b'1', b'1', b'1', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-003' OR code = 'ACT-003');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-004', 'SME Current Wadia', 'ACT-004', 'SME Current Wadia', 'CURRENT', 'SME_WADIAH',
       'WADIAH', 'BDT', 5000.00, 5000.00, b'0', b'0', b'1', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-004' OR code = 'ACT-004');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-005', 'Corporate Mudarabah', 'ACT-005', 'Corporate Mudarabah', 'CURRENT', 'CORPORATE_MUDARABAH',
       'MUDARABAH', 'BDT', 10000.00, 10000.00, b'1', b'1', b'1', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-005' OR code = 'ACT-005');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-006', 'Hajj Savings', 'ACT-006', 'Hajj Savings', 'SAVINGS', 'HAJJ',
       'MUDARABAH', 'BDT', 1500.00, 1500.00, b'1', b'1', b'0', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-006' OR code = 'ACT-006');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-007', 'Umrah Savings', 'ACT-007', 'Umrah Savings', 'SAVINGS', 'UMRAH',
       'MUDARABAH', 'BDT', 1500.00, 1500.00, b'1', b'1', b'0', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-007' OR code = 'ACT-007');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-008', 'Student Savings', 'ACT-008', 'Student Savings', 'SAVINGS', 'STUDENT',
       'MUDARABAH', 'BDT', 500.00, 500.00, b'1', b'1', b'1', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-008' OR code = 'ACT-008');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-009', 'Payroll Savings', 'ACT-009', 'Payroll Savings', 'SAVINGS', 'PAYROLL',
       'MUDARABAH', 'BDT', 1000.00, 1000.00, b'1', b'1', b'1', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-009' OR code = 'ACT-009');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-010', 'Business Current', 'ACT-010', 'Business Current', 'CURRENT', 'BUSINESS',
       'WADIAH', 'BDT', 7500.00, 7500.00, b'0', b'0', b'1', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-010' OR code = 'ACT-010');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-011', 'Women Savings', 'ACT-011', 'Women Savings', 'SAVINGS', 'WOMEN',
       'MUDARABAH', 'BDT', 800.00, 800.00, b'1', b'1', b'1', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-011' OR code = 'ACT-011');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-012', 'Senior Citizen Savings', 'ACT-012', 'Senior Citizen Savings', 'SAVINGS', 'SENIOR_CITIZEN',
       'MUDARABAH', 'BDT', 700.00, 700.00, b'1', b'1', b'1', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-012' OR code = 'ACT-012');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-013', 'NRB FC Savings', 'ACT-013', 'NRB FC Savings', 'SAVINGS', 'NRB_FC',
       'MUDARABAH', 'USD', 100.00, 100.00, b'1', b'1', b'1', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-013' OR code = 'ACT-013');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-014', 'Monthly Deposit Lite', 'ACT-014', 'Monthly Deposit Lite', 'DEPOSIT', 'MONTHLY_DEPOSIT',
       'MUDARABAH', 'BDT', 500.00, 500.00, b'1', b'1', b'0', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-014' OR code = 'ACT-014');

INSERT INTO account_type (
  type_code, type_name, code, name, account_category, account_subcategory,
  shariah_contract_type, currency_code, minimum_opening_balance, minimum_balance,
  profit_applicable, psr_required, withdrawal_allowed, status, created_at, updated_at
)
SELECT 'ACT-015', 'Digital Savings', 'ACT-015', 'Digital Savings', 'SAVINGS', 'DIGITAL',
       'MUDARABAH', 'BDT', 300.00, 300.00, b'1', b'1', b'1', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE type_code = 'ACT-015' OR code = 'ACT-015');

UPDATE account_opening_request
SET
  request_no = 'AOR-0001',
  branch_id = COALESCE(NULLIF(branch_id, 0), 1),
  requested_date = COALESCE(requested_date, '2026-04-01'),
  initial_deposit_amount = COALESCE(initial_deposit_amount, 3000.00),
  request_status = 'APPROVED',
  verified_by = 'SYSTEM_REVIEWER',
  verified_at = NOW(),
  approved_by = 'SYSTEM_REVIEWER',
  approved_at = NOW(),
  remarks = 'Existing request normalized for module 8 seed',
  status = 'ACTIVE',
  updated_at = NOW()
WHERE id = 1;

UPDATE account_opening_request
SET
  request_no = 'AOR-0002',
  branch_id = COALESCE(NULLIF(branch_id, 0), 1),
  requested_date = COALESCE(requested_date, '2026-04-02'),
  initial_deposit_amount = COALESCE(initial_deposit_amount, 4000.00),
  request_status = 'APPROVED',
  verified_by = 'SYSTEM_REVIEWER',
  verified_at = NOW(),
  approved_by = 'SYSTEM_REVIEWER',
  approved_at = NOW(),
  remarks = 'Existing request normalized for module 8 seed',
  status = 'ACTIVE',
  updated_at = NOW()
WHERE id = 2;

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0003', 3, (SELECT id FROM account_type WHERE type_code = 'ACT-003'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-01',
       5000.00, 'APPROVED', 'SYSTEM_REVIEWER', NOW(), 'SYSTEM_REVIEWER', NOW(), 'Approved savings request', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 3
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0003');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0004', 4, (SELECT id FROM account_type WHERE type_code = 'ACT-004'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-02',
       8000.00, 'VERIFIED', 'SYSTEM_REVIEWER', NOW(), NULL, NULL, 'Verified and waiting for final approval', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 4
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0004');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0005', 5, (SELECT id FROM account_type WHERE type_code = 'ACT-005'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-03',
       15000.00, 'SUBMITTED', NULL, NULL, NULL, NULL, 'Submitted corporate opening request', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 5
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0005');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0006', 6, (SELECT id FROM account_type WHERE type_code = 'ACT-006'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-04',
       2000.00, 'DRAFT', NULL, NULL, NULL, NULL, 'Draft Hajj savings request', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 6
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0006');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0007', 7, (SELECT id FROM account_type WHERE type_code = 'ACT-007'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-05',
       3000.00, 'APPROVED', 'SYSTEM_REVIEWER', NOW(), 'SYSTEM_REVIEWER', NOW(), 'Approved Umrah savings request', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 7
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0007');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0008', 8, (SELECT id FROM account_type WHERE type_code = 'ACT-008'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-06',
       700.00, 'SENT_BACK', 'SYSTEM_REVIEWER', NOW(), NULL, NULL, 'Need corrected guardian document before approval', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 8
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0008');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0009', 9, (SELECT id FROM account_type WHERE type_code = 'ACT-009'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-07',
       2500.00, 'REJECTED', 'SYSTEM_REVIEWER', NOW(), 'SYSTEM_REVIEWER', NOW(), 'Rejected because employer proof mismatch', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 9
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0009');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0010', 10, (SELECT id FROM account_type WHERE type_code = 'ACT-010'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-08',
       12000.00, 'APPROVED', 'SYSTEM_REVIEWER', NOW(), 'SYSTEM_REVIEWER', NOW(), 'Approved business current request', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 10
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0010');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0011', 11, (SELECT id FROM account_type WHERE type_code = 'ACT-011'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-09',
       1500.00, 'VERIFIED', 'SYSTEM_REVIEWER', NOW(), NULL, NULL, 'Verified and pending branch approval', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 11
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0011');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0012', 12, (SELECT id FROM account_type WHERE type_code = 'ACT-012'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-10',
       2000.00, 'SUBMITTED', NULL, NULL, NULL, NULL, 'Submitted senior citizen savings request', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 12
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0012');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0013', 13, (SELECT id FROM account_type WHERE type_code = 'ACT-013'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-11',
       500.00, 'APPROVED', 'SYSTEM_REVIEWER', NOW(), 'SYSTEM_REVIEWER', NOW(), 'Approved NRB FC savings request', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 13
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0013');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0014', 14, (SELECT id FROM account_type WHERE type_code = 'ACT-014'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-12',
       900.00, 'DRAFT', NULL, NULL, NULL, NULL, 'Draft monthly deposit request', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 14
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0014');

INSERT INTO account_opening_request (
  request_no, customer_id, account_type_id, branch_id, requested_date,
  initial_deposit_amount, request_status, verified_by, verified_at,
  approved_by, approved_at, remarks, status, created_at, updated_at
)
SELECT 'AOR-0015', 15, (SELECT id FROM account_type WHERE type_code = 'ACT-015'), COALESCE(NULLIF(branch_id, 0), 1), '2026-04-13',
       1000.00, 'APPROVED', 'SYSTEM_REVIEWER', NOW(), 'SYSTEM_REVIEWER', NOW(), 'Approved digital savings request', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 15
  AND NOT EXISTS (SELECT 1 FROM account_opening_request WHERE request_no = 'AOR-0015');

UPDATE account
SET
  account_number = 'ACC-000001',
  account_name = COALESCE(NULLIF(account_name, ''), 'Existing Customer 01 - Mudarabah Savings Basic'),
  branch_id = COALESCE(NULLIF(branch_id, 0), 1),
  opening_request_id = 1,
  currency_code = COALESCE(NULLIF(currency_code, ''), 'BDT'),
  opened_at = COALESCE(opened_at, NOW()),
  account_status = 'ACTIVE',
  remarks = 'Existing active account normalized for module 8 seed',
  status = 'ACTIVE',
  updated_at = NOW()
WHERE id = 1;

UPDATE account
SET
  account_number = 'ACC-000002',
  account_name = COALESCE(NULLIF(account_name, ''), 'Existing Customer 02 - Wadia Current Personal'),
  branch_id = COALESCE(NULLIF(branch_id, 0), 1),
  opening_request_id = 2,
  currency_code = COALESCE(NULLIF(currency_code, ''), 'BDT'),
  opened_at = COALESCE(opened_at, NOW()),
  account_status = 'PENDING',
  remarks = 'Existing pending activation account normalized for module 8 seed',
  status = 'ACTIVE',
  updated_at = NOW()
WHERE id = 2;

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000003', 'Customer 03 - Mudarabah Savings Plus', 3,
       (SELECT id FROM account_type WHERE type_code = 'ACT-003'), COALESCE(NULLIF(branch_id, 0), 1),
       (SELECT id FROM account_opening_request WHERE request_no = 'AOR-0003'),
       'BDT', '2026-04-14 10:00:00', 5000.00, 5000.00, 101,
       'ACTIVE', NULL, 'Approved savings account', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 3
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000003');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000004', 'Customer 04 - SME Current Wadia', 4,
       (SELECT id FROM account_type WHERE type_code = 'ACT-004'), COALESCE(NULLIF(branch_id, 0), 1),
       NULL, 'BDT', '2026-04-15 10:00:00', 8000.00, 7600.00, NULL,
       'SUSPENDED', NULL, 'Blocked pending business verification', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 4
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000004');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000005', 'Customer 05 - Corporate Mudarabah', 5,
       (SELECT id FROM account_type WHERE type_code = 'ACT-005'), COALESCE(NULLIF(branch_id, 0), 1),
       NULL, 'BDT', '2026-04-16 10:00:00', 15000.00, 15000.00, 205,
       'SUSPENDED', NULL, 'Frozen pending compliance review', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 5
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000005');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000006', 'Customer 06 - Hajj Savings', 6,
       (SELECT id FROM account_type WHERE type_code = 'ACT-006'), COALESCE(NULLIF(branch_id, 0), 1),
       NULL, 'BDT', '2026-04-17 10:00:00', 2500.00, 0.00, NULL,
       'CLOSED', '2026-04-28', 'Closed after customer instruction', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 6
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000006');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000007', 'Customer 07 - Umrah Savings', 7,
       (SELECT id FROM account_type WHERE type_code = 'ACT-007'), COALESCE(NULLIF(branch_id, 0), 1),
       (SELECT id FROM account_opening_request WHERE request_no = 'AOR-0007'),
       'BDT', '2026-04-18 10:00:00', 3000.00, 3000.00, 107,
       'ACTIVE', NULL, 'Approved Umrah savings account', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 7
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000007');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000008', 'Customer 08 - Student Savings', 8,
       (SELECT id FROM account_type WHERE type_code = 'ACT-008'), COALESCE(NULLIF(branch_id, 0), 1),
       NULL, 'BDT', '2026-04-19 10:00:00', 700.00, 700.00, NULL,
       'PENDING', NULL, 'Pending guardian confirmation', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 8
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000008');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000009', 'Customer 09 - Payroll Savings', 9,
       (SELECT id FROM account_type WHERE type_code = 'ACT-009'), COALESCE(NULLIF(branch_id, 0), 1),
       NULL, 'BDT', '2026-04-20 10:00:00', 4500.00, 4300.00, 109,
       'ACTIVE', NULL, 'Payroll savings account active', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 9
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000009');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000010', 'Customer 10 - Business Current', 10,
       (SELECT id FROM account_type WHERE type_code = 'ACT-010'), COALESCE(NULLIF(branch_id, 0), 1),
       (SELECT id FROM account_opening_request WHERE request_no = 'AOR-0010'),
       'BDT', '2026-04-21 10:00:00', 12000.00, 11800.00, NULL,
       'SUSPENDED', NULL, 'Blocked business current temporarily', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 10
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000010');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000011', 'Customer 11 - Women Savings', 11,
       (SELECT id FROM account_type WHERE type_code = 'ACT-011'), COALESCE(NULLIF(branch_id, 0), 1),
       NULL, 'BDT', '2026-04-22 10:00:00', 1800.00, 1800.00, 111,
       'ACTIVE', NULL, 'Women savings account active', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 11
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000011');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000012', 'Customer 12 - Senior Citizen Savings', 12,
       (SELECT id FROM account_type WHERE type_code = 'ACT-012'), COALESCE(NULLIF(branch_id, 0), 1),
       NULL, 'BDT', '2026-04-23 10:00:00', 2200.00, 2100.00, 112,
       'SUSPENDED', NULL, 'Frozen due to signature update', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 12
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000012');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000013', 'Customer 13 - NRB FC Savings', 13,
       (SELECT id FROM account_type WHERE type_code = 'ACT-013'), COALESCE(NULLIF(branch_id, 0), 1),
       (SELECT id FROM account_opening_request WHERE request_no = 'AOR-0013'),
       'USD', '2026-04-24 10:00:00', 550.00, 550.00, 113,
       'ACTIVE', NULL, 'NRB FC savings active', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 13
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000013');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000014', 'Customer 14 - Monthly Deposit Lite', 14,
       (SELECT id FROM account_type WHERE type_code = 'ACT-014'), COALESCE(NULLIF(branch_id, 0), 1),
       NULL, 'BDT', '2026-04-25 10:00:00', 900.00, 0.00, NULL,
       'CLOSED', '2026-04-29', 'Monthly deposit account closed after maturity', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 14
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000014');

INSERT INTO account (
  account_number, account_name, customer_id, account_type_id, branch_id, opening_request_id,
  currency_code, opened_at, current_balance, available_balance, profit_ratio_id,
  account_status, closed_date, remarks, status, created_at, updated_at
)
SELECT 'ACC-000015', 'Customer 15 - Digital Savings', 15,
       (SELECT id FROM account_type WHERE type_code = 'ACT-015'), COALESCE(NULLIF(branch_id, 0), 1),
       (SELECT id FROM account_opening_request WHERE request_no = 'AOR-0015'),
       'BDT', '2026-04-26 10:00:00', 1000.00, 980.00, 115,
       'ACTIVE', NULL, 'Digital savings account active', 'ACTIVE', NOW(), NOW()
FROM customer
WHERE id = 15
  AND NOT EXISTS (SELECT 1 FROM account WHERE account_number = 'ACC-000015');

COMMIT;
