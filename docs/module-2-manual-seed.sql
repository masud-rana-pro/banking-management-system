-- Module 2 sample data for User Management.
-- Safe to run multiple times. It tops up the module to total 20 users
-- and prepares user_role + user_session sample data.

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'user_code') = 0,
  'ALTER TABLE users ADD COLUMN user_code varchar(40) NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'employee_no') = 0,
  'ALTER TABLE users ADD COLUMN employee_no varchar(60) NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'designation') = 0,
  'ALTER TABLE users ADD COLUMN designation varchar(120) NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'is_active') = 0,
  'ALTER TABLE users ADD COLUMN is_active bit(1) NOT NULL DEFAULT b''1''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'is_locked') = 0,
  'ALTER TABLE users ADD COLUMN is_locked bit(1) NOT NULL DEFAULT b''0''',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'created_by') = 0,
  'ALTER TABLE users ADD COLUMN created_by varchar(120) NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'updated_by') = 0,
  'ALTER TABLE users ADD COLUMN updated_by varchar(120) NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS user_role (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  role_id bigint NOT NULL,
  created_at datetime(6) NOT NULL,
  created_by varchar(120) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_user_role_user (user_id),
  KEY idx_user_role_role (role_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS user_session (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  login_time datetime(6) NOT NULL,
  logout_time datetime(6) DEFAULT NULL,
  jwt_id varchar(180) DEFAULT NULL,
  ip_address varchar(80) DEFAULT NULL,
  device_info varchar(255) DEFAULT NULL,
  status varchar(40) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_user_session_user (user_id),
  CONSTRAINT fk_user_session_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

UPDATE users
SET user_code = CONCAT('USR-', LPAD(id, 5, '0')),
    employee_no = COALESCE(employee_no, CONCAT('EMP-', LPAD(id, 4, '0'))),
    designation = COALESCE(designation, 'Seeded User'),
    created_by = COALESCE(NULLIF(created_by, ''), 'MODULE_2_SEED'),
    updated_by = COALESCE(NULLIF(updated_by, ''), 'MODULE_2_SEED'),
    is_active = CASE WHEN status = 'ACTIVE' THEN b'1' ELSE b'0' END,
    is_locked = CASE WHEN status = 'LOCKED' THEN b'1' ELSE b'0' END;

UPDATE users u
JOIN roles r ON r.code = 'OPERATIONS_OFFICER'
SET u.role_id = r.id,
    u.branch_id = COALESCE(u.branch_id, 1),
    u.user_code = COALESCE(u.user_code, 'USR-00001'),
    u.employee_no = COALESCE(u.employee_no, 'EMP-0001'),
    u.designation = COALESCE(u.designation, 'Customer Service Officer'),
    u.user_type = 'STAFF',
    u.status = 'ACTIVE',
    u.is_active = b'1',
    u.is_locked = b'0',
    u.last_login_at = NOW() - INTERVAL 1 DAY,
    u.password_changed_at = NOW() - INTERVAL 20 DAY,
    u.password_hash = COALESCE(NULLIF(u.password_hash, ''), SHA2('Admin@123!', 256))
WHERE u.username = 'retwer';
UPDATE users u
JOIN roles r ON r.code = 'OPERATIONS_OFFICER'
SET u.role_id = r.id
WHERE u.username = 'retwer';

UPDATE users u
JOIN roles r ON r.code = 'TELLER'
SET u.role_id = r.id,
    u.branch_id = COALESCE(u.branch_id, 2),
    u.user_code = COALESCE(u.user_code, 'USR-00002'),
    u.employee_no = COALESCE(u.employee_no, 'EMP-0002'),
    u.designation = COALESCE(u.designation, 'Teller'),
    u.user_type = 'STAFF',
    u.status = 'ACTIVE',
    u.is_active = b'1',
    u.is_locked = b'0',
    u.last_login_at = NOW() - INTERVAL 2 DAY,
    u.password_changed_at = NOW() - INTERVAL 18 DAY,
    u.password_hash = COALESCE(NULLIF(u.password_hash, ''), SHA2('Admin@123!', 256))
WHERE u.username = 'fasf';

UPDATE users u
JOIN roles r ON r.code = 'SYSTEM_ADMIN'
SET u.role_id = r.id,
    u.branch_id = COALESCE(u.branch_id, 1),
    u.user_code = COALESCE(u.user_code, 'USR-00003'),
    u.employee_no = COALESCE(u.employee_no, 'EMP-0003'),
    u.designation = COALESCE(u.designation, 'System Administrator'),
    u.user_type = 'STAFF',
    u.status = 'ACTIVE',
    u.is_active = b'1',
    u.is_locked = b'0',
    u.last_login_at = NOW() - INTERVAL 4 HOUR,
    u.password_changed_at = NOW() - INTERVAL 5 DAY,
    u.password_hash = COALESCE(NULLIF(u.password_hash, ''), SHA2('Admin@123!', 256))
WHERE u.username = 'Admin01';

INSERT INTO users (
  user_code, username, full_name, email, mobile, password_hash,
  branch_id, employee_no, designation, is_locked, is_active,
  last_login_at, failed_login_count, status, user_type,
  created_at, updated_at, created_by, updated_by,
  must_change_password, email_verified, mobile_verified, password_changed_at, role_id
)
SELECT seed.user_code, seed.username, seed.full_name, seed.email, seed.mobile, SHA2('Admin@123!', 256),
       seed.branch_id, seed.employee_no, seed.designation,
       CASE WHEN seed.status = 'LOCKED' THEN b'1' ELSE b'0' END,
       CASE WHEN seed.status = 'ACTIVE' THEN b'1' ELSE b'0' END,
       seed.last_login_at, seed.failed_login_count, seed.status, seed.user_type,
       NOW(), NOW(), 'MODULE_2_SEED', 'MODULE_2_SEED',
       b'0', seed.email_verified, seed.mobile_verified, seed.password_changed_at, r.id
FROM (
  SELECT 'USR-00004' AS user_code, 'branch.manager01' AS username, 'Dhaka Branch Manager' AS full_name, 'branch.manager01@sbms.local' AS email, '01710000004' AS mobile, 1 AS branch_id, 'EMP-0004' AS employee_no, 'Branch Manager' AS designation, 'ACTIVE' AS status, 'STAFF' AS user_type, NOW() - INTERVAL 6 HOUR AS last_login_at, 0 AS failed_login_count, b'1' AS email_verified, b'1' AS mobile_verified, NOW() - INTERVAL 8 DAY AS password_changed_at, 'BRANCH_MANAGER' AS role_code
  UNION ALL SELECT 'USR-00005', 'ops.officer01', 'Operations Officer One', 'ops.officer01@sbms.local', '01710000005', 1, 'EMP-0005', 'Operations Officer', 'ACTIVE', 'STAFF', NOW() - INTERVAL 1 DAY, 1, b'1', b'1', NOW() - INTERVAL 7 DAY, 'OPERATIONS_OFFICER'
  UNION ALL SELECT 'USR-00006', 'customer.seed01', 'Retail Customer One', 'customer.seed01@sbms.local', '01710000006', NULL, 'CUS-0006', 'Retail Customer', 'ACTIVE', 'CUSTOMER', NOW() - INTERVAL 2 DAY, 0, b'1', b'1', NOW() - INTERVAL 6 DAY, 'CUSTOMER'
  UNION ALL SELECT 'USR-00007', 'investment.officer01', 'Investment Officer One', 'investment.officer01@sbms.local', '01710000007', 3, 'EMP-0007', 'Investment Officer', 'ACTIVE', 'STAFF', NOW() - INTERVAL 3 DAY, 0, b'1', b'1', NOW() - INTERVAL 5 DAY, 'INVESTMENT_OFFICER'
  UNION ALL SELECT 'USR-00008', 'shariah.board01', 'Shariah Board Member One', 'shariah.board01@sbms.local', '01710000008', 3, 'EMP-0008', 'Shariah Board Member', 'ACTIVE', 'STAFF', NOW() - INTERVAL 4 DAY, 0, b'1', b'1', NOW() - INTERVAL 5 DAY, 'SHARIAH_BOARD_MEMBER'
  UNION ALL SELECT 'USR-00009', 'audit.reviewer01', 'Audit Reviewer One', 'audit.reviewer01@sbms.local', '01710000009', 4, 'EMP-0009', 'Audit Reviewer', 'ACTIVE', 'STAFF', NOW() - INTERVAL 5 DAY, 0, b'1', b'1', NOW() - INTERVAL 9 DAY, 'SYSTEM_ADMIN'
  UNION ALL SELECT 'USR-00010', 'compliance.analyst01', 'Compliance Analyst One', 'compliance.analyst01@sbms.local', '01710000010', 4, 'EMP-0010', 'Compliance Analyst', 'ACTIVE', 'STAFF', NOW() - INTERVAL 6 DAY, 0, b'1', b'1', NOW() - INTERVAL 10 DAY, 'OPERATIONS_OFFICER'
  UNION ALL SELECT 'USR-00011', 'cso.seed01', 'Customer Service Officer One', 'cso.seed01@sbms.local', '01710000011', 5, 'EMP-0011', 'Customer Service Officer', 'ACTIVE', 'STAFF', NOW() - INTERVAL 7 DAY, 0, b'1', b'1', NOW() - INTERVAL 6 DAY, 'OPERATIONS_OFFICER'
  UNION ALL SELECT 'USR-00012', 'kyc.reviewer01', 'KYC Reviewer One', 'kyc.reviewer01@sbms.local', '01710000012', 5, 'EMP-0012', 'KYC Reviewer', 'ACTIVE', 'STAFF', NOW() - INTERVAL 8 DAY, 2, b'1', b'1', NOW() - INTERVAL 8 DAY, 'OPERATIONS_OFFICER'
  UNION ALL SELECT 'USR-00013', 'acct.supervisor01', 'Account Supervisor One', 'acct.supervisor01@sbms.local', '01710000013', 6, 'EMP-0013', 'Account Supervisor', 'ACTIVE', 'STAFF', NOW() - INTERVAL 9 DAY, 0, b'1', b'1', NOW() - INTERVAL 6 DAY, 'OPERATIONS_OFFICER'
  UNION ALL SELECT 'USR-00014', 'txn.supervisor01', 'Transaction Supervisor One', 'txn.supervisor01@sbms.local', '01710000014', 6, 'EMP-0014', 'Transaction Supervisor', 'LOCKED', 'STAFF', NOW() - INTERVAL 10 DAY, 4, b'1', b'1', NOW() - INTERVAL 12 DAY, 'TELLER'
  UNION ALL SELECT 'USR-00015', 'profit.officer01', 'Profit Officer One', 'profit.officer01@sbms.local', '01710000015', 7, 'EMP-0015', 'Profit Officer', 'ACTIVE', 'STAFF', NOW() - INTERVAL 11 DAY, 0, b'1', b'1', NOW() - INTERVAL 5 DAY, 'INVESTMENT_OFFICER'
  UNION ALL SELECT 'USR-00016', 'card.ops01', 'Card Operations Officer One', 'card.ops01@sbms.local', '01710000016', 7, 'EMP-0016', 'Card Operations Officer', 'ACTIVE', 'STAFF', NOW() - INTERVAL 12 DAY, 1, b'1', b'1', NOW() - INTERVAL 13 DAY, 'OPERATIONS_OFFICER'
  UNION ALL SELECT 'USR-00017', 'statement.desk01', 'Statement Desk One', 'statement.desk01@sbms.local', '01710000017', 8, 'EMP-0017', 'Statement Desk Officer', 'INACTIVE', 'STAFF', NOW() - INTERVAL 13 DAY, 0, b'1', b'1', NOW() - INTERVAL 9 DAY, 'OPERATIONS_OFFICER'
  UNION ALL SELECT 'USR-00018', 'deposit.scheme01', 'Deposit Scheme Officer One', 'deposit.scheme01@sbms.local', '01710000018', 8, 'EMP-0018', 'Deposit Scheme Officer', 'ACTIVE', 'STAFF', NOW() - INTERVAL 14 DAY, 0, b'1', b'1', NOW() - INTERVAL 14 DAY, 'INVESTMENT_OFFICER'
  UNION ALL SELECT 'USR-00019', 'contract.review01', 'Contract Reviewer One', 'contract.review01@sbms.local', '01710000019', 9, 'EMP-0019', 'Contract Reviewer', 'ACTIVE', 'STAFF', NOW() - INTERVAL 15 DAY, 0, b'1', b'1', NOW() - INTERVAL 11 DAY, 'INVESTMENT_OFFICER'
  UNION ALL SELECT 'USR-00020', 'notify.admin01', 'Notification Admin One', 'notify.admin01@sbms.local', '01710000020', 10, 'EMP-0020', 'Notification Administrator', 'LOCKED', 'STAFF', NOW() - INTERVAL 16 DAY, 5, b'1', b'1', NOW() - INTERVAL 20 DAY, 'SYSTEM_ADMIN'
) seed
JOIN roles r ON r.code = seed.role_code
WHERE NOT EXISTS (
  SELECT 1 FROM users u WHERE u.username = seed.username
);

DELETE FROM user_role WHERE created_by = 'MODULE_2_SEED';

INSERT INTO user_role (user_id, role_id, created_at, created_by)
SELECT u.id, u.role_id, NOW(), 'MODULE_2_SEED'
FROM users u
WHERE u.role_id IS NOT NULL
  AND u.username IN (
    'retwer', 'fasf', 'Admin01', 'branch.manager01', 'ops.officer01', 'customer.seed01',
    'investment.officer01', 'shariah.board01', 'audit.reviewer01', 'compliance.analyst01',
    'cso.seed01', 'kyc.reviewer01', 'acct.supervisor01', 'txn.supervisor01', 'profit.officer01',
    'card.ops01', 'statement.desk01', 'deposit.scheme01', 'contract.review01', 'notify.admin01'
  );

DELETE FROM user_session WHERE jwt_id LIKE 'MODULE2-%';

INSERT INTO user_session (user_id, login_time, logout_time, jwt_id, ip_address, device_info, status)
SELECT u.id,
       COALESCE(u.last_login_at, NOW() - INTERVAL u.id DAY),
       COALESCE(u.last_login_at, NOW() - INTERVAL u.id DAY) + INTERVAL 2 HOUR,
       CONCAT('MODULE2-', u.id),
       CONCAT('10.10.0.', u.id),
       CASE
         WHEN u.user_type = 'CUSTOMER' THEN 'Android App'
         WHEN u.status = 'LOCKED' THEN 'Web Portal - Locked Attempt'
         ELSE 'Web Portal'
       END,
       CASE WHEN u.status = 'LOCKED' THEN 'LOCKED' ELSE 'SUCCESS' END
FROM users u
WHERE u.username IN (
  'retwer', 'fasf', 'Admin01', 'branch.manager01', 'ops.officer01', 'customer.seed01',
  'investment.officer01', 'shariah.board01', 'audit.reviewer01', 'compliance.analyst01',
  'cso.seed01', 'kyc.reviewer01', 'acct.supervisor01', 'txn.supervisor01', 'profit.officer01',
  'card.ops01', 'statement.desk01', 'deposit.scheme01', 'contract.review01', 'notify.admin01'
);
