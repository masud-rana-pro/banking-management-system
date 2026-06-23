CREATE TABLE IF NOT EXISTS security_event_log (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  event_code VARCHAR(50) NOT NULL,
  event_name VARCHAR(160) NOT NULL,
  user_id BIGINT NULL,
  ip_address VARCHAR(80) NULL,
  device_info VARCHAR(255) NULL,
  reference_module VARCHAR(80) NULL,
  reference_id BIGINT NULL,
  event_time DATETIME NOT NULL,
  severity_level VARCHAR(20) NOT NULL,
  remarks VARCHAR(1000) NULL,
  status VARCHAR(20) NOT NULL
);


CREATE TABLE IF NOT EXISTS audit_log (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  module_name VARCHAR(80) NOT NULL,
  reference_id BIGINT NULL,
  action_name VARCHAR(80) NOT NULL,
  old_value_json TEXT NULL,
  new_value_json TEXT NULL,
  performed_by VARCHAR(120) NOT NULL,
  performed_at DATETIME NOT NULL,
  status VARCHAR(20) NOT NULL
);


CREATE TABLE IF NOT EXISTS investigation_case (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  case_no VARCHAR(40) NOT NULL,
  case_type VARCHAR(30) NOT NULL,
  reference_module VARCHAR(80) NOT NULL,
  reference_id BIGINT NOT NULL,
  opened_by VARCHAR(120) NOT NULL,
  opened_at DATETIME NOT NULL,
  assigned_to BIGINT NULL,
  case_status VARCHAR(30) NOT NULL,
  remarks VARCHAR(1000) NULL,
  status VARCHAR(20) NOT NULL,
  created_at DATETIME NOT NULL,
  UNIQUE KEY uk_investigation_case_no (case_no)
);

DELETE FROM audit_log WHERE performed_by = 'SECURITY_SEED';
DELETE FROM investigation_case WHERE opened_by = 'SECURITY_SEED';
DELETE FROM security_event_log WHERE remarks LIKE '%SECURITY_SEED%';

INSERT INTO security_event_log (description, event_at, event_type, success, username, event_code, event_name, user_id, ip_address, device_info, reference_module, reference_id, event_time, severity_level, remarks, status) VALUES
('Repeated password failure | SECURITY_SEED', '2026-05-04 08:10:00', 'FAILED_LOGIN', 0, 'seed.user', 'FAILED_LOGIN', 'Failed Login Attempt', (SELECT id FROM users ORDER BY id LIMIT 1), '10.10.1.10', 'Chrome / Windows 11', 'AUTH', 1001, '2026-05-04 08:10:00', 'HIGH', 'Repeated password failure | SECURITY_SEED', 'ACTIVE'),
('Five failed attempts from same IP | SECURITY_SEED', '2026-05-04 08:14:00', 'FAILED_LOGIN', 0, 'seed.user', 'FAILED_LOGIN', 'Failed Login Attempt', (SELECT id FROM users ORDER BY id LIMIT 1), '10.10.1.11', 'Edge / Windows 11', 'AUTH', 1002, '2026-05-04 08:14:00', 'CRITICAL', 'Five failed attempts from same IP | SECURITY_SEED', 'ACTIVE'),
('Account locked after security threshold | SECURITY_SEED', '2026-05-04 08:15:00', 'LOCKED_USER', 0, 'seed.user', 'LOCKED_USER', 'User Locked After Failed Login', (SELECT id FROM users ORDER BY id LIMIT 1), '10.10.1.11', 'Edge / Windows 11', 'AUTH', 1002, '2026-05-04 08:15:00', 'CRITICAL', 'Account locked after security threshold | SECURITY_SEED', 'ACTIVE'),
('High-value transfer pattern detected | SECURITY_SEED', '2026-05-04 09:05:00', 'SUSPICIOUS_TXN', 0, NULL, 'SUSPICIOUS_TXN', 'Suspicious High Value Transfer', NULL, '10.20.5.14', 'API Gateway', 'TRANSACTION', 2001, '2026-05-04 09:05:00', 'HIGH', 'High-value transfer pattern detected | SECURITY_SEED', 'ACTIVE'),
('Transaction matched AML watch rule | SECURITY_SEED', '2026-05-04 09:10:00', 'AML_FLAG', 0, NULL, 'AML_FLAG', 'AML Pattern Match', NULL, '10.20.5.14', 'API Gateway', 'TRANSACTION', 2002, '2026-05-04 09:10:00', 'CRITICAL', 'Transaction matched AML watch rule | SECURITY_SEED', 'ACTIVE'),
('Name matched sanction screening list | SECURITY_SEED', '2026-05-04 09:20:00', 'SANCTION_HIT', 0, NULL, 'SANCTION_HIT', 'Sanction Screening Hit', NULL, '10.20.5.14', 'API Gateway', 'CUSTOMER', 3001, '2026-05-04 09:20:00', 'CRITICAL', 'Name matched sanction screening list | SECURITY_SEED', 'ACTIVE'),
('Reversal spike observed on same teller | SECURITY_SEED', '2026-05-04 09:45:00', 'MULTIPLE_REVERSAL', 0, NULL, 'MULTIPLE_REVERSAL', 'Multiple Reversal Pattern', NULL, '10.20.6.15', 'Branch Switch', 'TRANSACTION', 2003, '2026-05-04 09:45:00', 'HIGH', 'Reversal spike observed on same teller | SECURITY_SEED', 'ACTIVE'),
('Administrative password reset triggered | SECURITY_SEED', '2026-05-03 15:20:00', 'PASSWORD_RESET', 1, 'seed.user', 'PASSWORD_RESET', 'Admin Password Reset', (SELECT id FROM users ORDER BY id LIMIT 1), '10.10.1.20', 'Firefox / Windows 11', 'USER', 4001, '2026-05-03 15:20:00', 'MEDIUM', 'Administrative password reset triggered | SECURITY_SEED', 'ACTIVE'),
('Sensitive permission map changed | SECURITY_SEED', '2026-05-03 16:00:00', 'PERMISSION_CHANGE', 1, 'seed.user', 'PERMISSION_CHANGE', 'Role Permission Updated', (SELECT id FROM users ORDER BY id LIMIT 1), '10.10.1.21', 'Chrome / Windows 11', 'ROLE', 5001, '2026-05-03 16:00:00', 'MEDIUM', 'Sensitive permission map changed | SECURITY_SEED', 'ACTIVE'),
('Login attempted from unusual device | SECURITY_SEED', '2026-05-03 16:10:00', 'DEVICE_MISMATCH', 0, 'seed.user', 'DEVICE_MISMATCH', 'Device Fingerprint Mismatch', (SELECT id FROM users ORDER BY id LIMIT 1), '10.10.1.22', 'Safari / macOS', 'AUTH', 1003, '2026-05-03 16:10:00', 'HIGH', 'Login attempted from unusual device | SECURITY_SEED', 'ACTIVE'),
('Suspicious repeated mobile login failure | SECURITY_SEED', '2026-05-02 10:00:00', 'FAILED_LOGIN', 0, 'seed.user', 'FAILED_LOGIN', 'Failed Login Attempt', (SELECT id FROM users ORDER BY id LIMIT 1), '10.10.1.23', 'Chrome / Android', 'AUTH', 1004, '2026-05-02 10:00:00', 'HIGH', 'Suspicious repeated mobile login failure | SECURITY_SEED', 'ACTIVE'),
('Rapid cash withdrawal sequence detected | SECURITY_SEED', '2026-05-02 11:15:00', 'SUSPICIOUS_TXN', 0, NULL, 'SUSPICIOUS_TXN', 'Unusual Cash Withdrawal Pattern', NULL, '10.20.7.16', 'ATM Switch', 'TRANSACTION', 2004, '2026-05-02 11:15:00', 'HIGH', 'Rapid cash withdrawal sequence detected | SECURITY_SEED', 'ACTIVE'),
('Layering pattern detected in txn set | SECURITY_SEED', '2026-05-01 12:05:00', 'AML_FLAG', 0, NULL, 'AML_FLAG', 'AML Scenario Triggered', NULL, '10.20.7.16', 'ATM Switch', 'TRANSACTION', 2005, '2026-05-01 12:05:00', 'CRITICAL', 'Layering pattern detected in txn set | SECURITY_SEED', 'ACTIVE'),
('Customer screening returned hit | SECURITY_SEED', '2026-05-01 13:30:00', 'SANCTION_HIT', 0, NULL, 'SANCTION_HIT', 'Sanction Screening Hit', NULL, '10.10.1.24', 'Back Office', 'CUSTOMER', 3002, '2026-05-01 13:30:00', 'CRITICAL', 'Customer screening returned hit | SECURITY_SEED', 'ACTIVE'),
('Concurrent session detected | SECURITY_SEED', '2026-05-01 14:00:00', 'SESSION_ALERT', 0, 'seed.user', 'SESSION_ALERT', 'Concurrent Session Alert', (SELECT id FROM users ORDER BY id LIMIT 1), '10.10.1.25', 'Chrome / Windows 11', 'AUTH', 1005, '2026-05-01 14:00:00', 'MEDIUM', 'Concurrent session detected | SECURITY_SEED', 'ACTIVE');

INSERT INTO investigation_case (case_no, case_type, reference_module, reference_id, opened_by, opened_at, assigned_to, case_status, remarks, status, created_at) VALUES
('INV-S21-001', 'FAILED_LOGIN', 'AUTH', 1001, 'SECURITY_SEED', '2026-05-04 08:30:00', (SELECT id FROM users ORDER BY id LIMIT 1), 'ASSIGNED', 'Investigate repeated failed login for branch staff', 'ACTIVE', '2026-05-04 08:30:00'),
('INV-S21-002', 'FAILED_LOGIN', 'AUTH', 1002, 'SECURITY_SEED', '2026-05-04 08:35:00', (SELECT id FROM users ORDER BY id LIMIT 1), 'UNDER_REVIEW', 'User lock needs credential compromise review', 'ACTIVE', '2026-05-04 08:35:00'),
('INV-S21-003', 'SUSPICIOUS_TRANSACTION', 'TRANSACTION', 2001, 'SECURITY_SEED', '2026-05-04 09:20:00', NULL, 'OPEN', 'High value transfer needs verification', 'ACTIVE', '2026-05-04 09:20:00'),
('INV-S21-004', 'AML_FLAG', 'TRANSACTION', 2002, 'SECURITY_SEED', '2026-05-04 09:25:00', (SELECT id FROM users ORDER BY id LIMIT 1), 'ASSIGNED', 'AML watchlist event assigned to compliance reviewer', 'ACTIVE', '2026-05-04 09:25:00'),
('INV-S21-005', 'SANCTION_HIT', 'CUSTOMER', 3001, 'SECURITY_SEED', '2026-05-04 09:35:00', (SELECT id FROM users ORDER BY id LIMIT 1), 'UNDER_REVIEW', 'Sanction hit under manual escalation', 'ACTIVE', '2026-05-04 09:35:00'),
('INV-S21-006', 'SUSPICIOUS_TRANSACTION', 'TRANSACTION', 2003, 'SECURITY_SEED', '2026-05-04 09:55:00', NULL, 'OPEN', 'Multiple reversals need branch explanation', 'ACTIVE', '2026-05-04 09:55:00'),
('INV-S21-007', 'AUDIT_ANOMALY', 'USER', 4001, 'SECURITY_SEED', '2026-05-03 15:30:00', (SELECT id FROM users ORDER BY id LIMIT 1), 'CLOSED', 'Password reset validated and closed', 'ACTIVE', '2026-05-03 15:30:00'),
('INV-S21-008', 'AUDIT_ANOMALY', 'ROLE', 5001, 'SECURITY_SEED', '2026-05-03 16:20:00', (SELECT id FROM users ORDER BY id LIMIT 1), 'ASSIGNED', 'Permission change requires role approval review', 'ACTIVE', '2026-05-03 16:20:00'),
('INV-S21-009', 'FAILED_LOGIN', 'AUTH', 1003, 'SECURITY_SEED', '2026-05-03 16:25:00', NULL, 'OPEN', 'Device mismatch linked with failed login', 'ACTIVE', '2026-05-03 16:25:00'),
('INV-S21-010', 'FAILED_LOGIN', 'AUTH', 1004, 'SECURITY_SEED', '2026-05-02 10:15:00', (SELECT id FROM users ORDER BY id LIMIT 1), 'ASSIGNED', 'Mobile failed login pattern under review', 'ACTIVE', '2026-05-02 10:15:00'),
('INV-S21-011', 'SUSPICIOUS_TRANSACTION', 'TRANSACTION', 2004, 'SECURITY_SEED', '2026-05-02 11:25:00', NULL, 'OPEN', 'Unusual cash withdrawals need source validation', 'ACTIVE', '2026-05-02 11:25:00'),
('INV-S21-012', 'AML_FLAG', 'TRANSACTION', 2005, 'SECURITY_SEED', '2026-05-01 12:20:00', (SELECT id FROM users ORDER BY id LIMIT 1), 'UNDER_REVIEW', 'Layering scenario escalated to compliance', 'ACTIVE', '2026-05-01 12:20:00'),
('INV-S21-013', 'SANCTION_HIT', 'CUSTOMER', 3002, 'SECURITY_SEED', '2026-05-01 13:45:00', (SELECT id FROM users ORDER BY id LIMIT 1), 'CLOSED', 'False positive resolved after document review', 'ACTIVE', '2026-05-01 13:45:00'),
('INV-S21-014', 'OTHER', 'AUTH', 1005, 'SECURITY_SEED', '2026-05-01 14:15:00', NULL, 'OPEN', 'Concurrent session alert pending review', 'ACTIVE', '2026-05-01 14:15:00'),
('INV-S21-015', 'AUDIT_ANOMALY', 'SECURITY', 9001, 'SECURITY_SEED', '2026-05-01 15:00:00', (SELECT id FROM users ORDER BY id LIMIT 1), 'ASSIGNED', 'Security configuration audit mismatch detected', 'ACTIVE', '2026-05-01 15:00:00');

INSERT INTO audit_log (action, action_at, description, entity_id, entity_type, username, module_name, reference_id, action_name, old_value_json, new_value_json, performed_by, performed_at, status) VALUES
('ASSIGN', '2026-05-04 08:32:00', 'Investigation case assigned | SECURITY_SEED', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-001' LIMIT 1), 'SECURITY_INVESTIGATION', 'SECURITY_SEED', 'SECURITY_INVESTIGATION', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-001' LIMIT 1), 'ASSIGN', '{"caseStatus":"OPEN","assignedTo":null}', '{"caseStatus":"ASSIGNED","assignedTo":1}', 'SECURITY_SEED', '2026-05-04 08:32:00', 'ACTIVE'),
('REVIEW_START', '2026-05-04 08:40:00', 'Review started | SECURITY_SEED', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-002' LIMIT 1), 'SECURITY_INVESTIGATION', 'SECURITY_SEED', 'SECURITY_INVESTIGATION', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-002' LIMIT 1), 'REVIEW_START', '{"caseStatus":"ASSIGNED"}', '{"caseStatus":"UNDER_REVIEW"}', 'SECURITY_SEED', '2026-05-04 08:40:00', 'ACTIVE'),
('ASSIGN', '2026-05-04 09:27:00', 'Investigation case assigned | SECURITY_SEED', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-004' LIMIT 1), 'SECURITY_INVESTIGATION', 'SECURITY_SEED', 'SECURITY_INVESTIGATION', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-004' LIMIT 1), 'ASSIGN', '{"caseStatus":"OPEN","assignedTo":null}', '{"caseStatus":"ASSIGNED","assignedTo":1}', 'SECURITY_SEED', '2026-05-04 09:27:00', 'ACTIVE'),
('REVIEW_START', '2026-05-04 09:38:00', 'Review started | SECURITY_SEED', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-005' LIMIT 1), 'SECURITY_INVESTIGATION', 'SECURITY_SEED', 'SECURITY_INVESTIGATION', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-005' LIMIT 1), 'REVIEW_START', '{"caseStatus":"ASSIGNED"}', '{"caseStatus":"UNDER_REVIEW"}', 'SECURITY_SEED', '2026-05-04 09:38:00', 'ACTIVE'),
('CLOSE', '2026-05-03 15:45:00', 'Case closed | SECURITY_SEED', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-007' LIMIT 1), 'SECURITY_INVESTIGATION', 'SECURITY_SEED', 'SECURITY_INVESTIGATION', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-007' LIMIT 1), 'CLOSE', '{"caseStatus":"UNDER_REVIEW"}', '{"caseStatus":"CLOSED"}', 'SECURITY_SEED', '2026-05-03 15:45:00', 'ACTIVE'),
('ASSIGN', '2026-05-03 16:25:00', 'Investigation case assigned | SECURITY_SEED', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-008' LIMIT 1), 'SECURITY_INVESTIGATION', 'SECURITY_SEED', 'SECURITY_INVESTIGATION', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-008' LIMIT 1), 'ASSIGN', '{"caseStatus":"OPEN","assignedTo":null}', '{"caseStatus":"ASSIGNED","assignedTo":1}', 'SECURITY_SEED', '2026-05-03 16:25:00', 'ACTIVE'),
('ASSIGN', '2026-05-02 10:17:00', 'Investigation case assigned | SECURITY_SEED', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-010' LIMIT 1), 'SECURITY_INVESTIGATION', 'SECURITY_SEED', 'SECURITY_INVESTIGATION', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-010' LIMIT 1), 'ASSIGN', '{"caseStatus":"OPEN","assignedTo":null}', '{"caseStatus":"ASSIGNED","assignedTo":1}', 'SECURITY_SEED', '2026-05-02 10:17:00', 'ACTIVE'),
('REVIEW_START', '2026-05-01 12:25:00', 'Review started | SECURITY_SEED', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-012' LIMIT 1), 'SECURITY_INVESTIGATION', 'SECURITY_SEED', 'SECURITY_INVESTIGATION', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-012' LIMIT 1), 'REVIEW_START', '{"caseStatus":"ASSIGNED"}', '{"caseStatus":"UNDER_REVIEW"}', 'SECURITY_SEED', '2026-05-01 12:25:00', 'ACTIVE'),
('CLOSE', '2026-05-01 14:10:00', 'Case closed | SECURITY_SEED', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-013' LIMIT 1), 'SECURITY_INVESTIGATION', 'SECURITY_SEED', 'SECURITY_INVESTIGATION', (SELECT id FROM investigation_case WHERE case_no = 'INV-S21-013' LIMIT 1), 'CLOSE', '{"caseStatus":"UNDER_REVIEW"}', '{"caseStatus":"CLOSED"}', 'SECURITY_SEED', '2026-05-01 14:10:00', 'ACTIVE'),
('LOCK_USER', '2026-05-04 08:16:00', 'User locked after threshold | SECURITY_SEED', 1002, 'AUTH', 'SECURITY_SEED', 'AUTH', 1002, 'LOCK_USER', '{"status":"ACTIVE"}', '{"status":"LOCKED"}', 'SECURITY_SEED', '2026-05-04 08:16:00', 'ACTIVE'),
('RAISE_AML_ALERT', '2026-05-04 09:11:00', 'AML alert raised | SECURITY_SEED', 2002, 'TRANSACTION', 'SECURITY_SEED', 'TRANSACTION', 2002, 'RAISE_AML_ALERT', '{"alert":"NONE"}', '{"alert":"AML_FLAG"}', 'SECURITY_SEED', '2026-05-04 09:11:00', 'ACTIVE'),
('SANCTION_SCREEN', '2026-05-04 09:21:00', 'Sanction hit logged | SECURITY_SEED', 3001, 'CUSTOMER', 'SECURITY_SEED', 'CUSTOMER', 3001, 'SANCTION_SCREEN', '{"screening":"CLEAR"}', '{"screening":"HIT"}', 'SECURITY_SEED', '2026-05-04 09:21:00', 'ACTIVE'),
('PERMISSION_MAP_UPDATE', '2026-05-03 16:02:00', 'Role permission updated | SECURITY_SEED', 5001, 'ROLE', 'SECURITY_SEED', 'ROLE', 5001, 'PERMISSION_MAP_UPDATE', '{"permissions":"LIMITED"}', '{"permissions":"ELEVATED"}', 'SECURITY_SEED', '2026-05-03 16:02:00', 'ACTIVE'),
('PASSWORD_RESET', '2026-05-03 15:22:00', 'Admin password reset | SECURITY_SEED', 4001, 'USER', 'SECURITY_SEED', 'USER', 4001, 'PASSWORD_RESET', '{"passwordReset":"PENDING"}', '{"passwordReset":"DONE"}', 'SECURITY_SEED', '2026-05-03 15:22:00', 'ACTIVE'),
('CONFIG_REVIEW', '2026-05-01 15:05:00', 'Security configuration reviewed | SECURITY_SEED', 9001, 'SECURITY', 'SECURITY_SEED', 'SECURITY', 9001, 'CONFIG_REVIEW', '{"ruleVersion":"1.0"}', '{"ruleVersion":"1.1"}', 'SECURITY_SEED', '2026-05-01 15:05:00', 'ACTIVE');
