CREATE TABLE IF NOT EXISTS integration_provider (
  id BIGINT NOT NULL AUTO_INCREMENT,
  provider_code VARCHAR(40) NOT NULL,
  provider_name VARCHAR(160) NOT NULL,
  provider_type ENUM('EMAIL','SMS','PAYMENT','MOBILE_BANKING','PUSH','GENERAL') NOT NULL,
  base_url VARCHAR(255) NOT NULL,
  auth_type ENUM('NONE','API_KEY','BASIC','BEARER','USERNAME_PASSWORD') NOT NULL,
  api_key VARCHAR(255) DEFAULT NULL,
  username VARCHAR(120) DEFAULT NULL,
  password_enc VARCHAR(255) DEFAULT NULL,
  timeout_sec INT NOT NULL,
  status ENUM('ACTIVE','PENDING','ARCHIVED') NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_integration_provider_code (provider_code)
);

CREATE TABLE IF NOT EXISTS integration_execution_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  provider_id BIGINT NOT NULL,
  reference_module VARCHAR(80) DEFAULT NULL,
  reference_id BIGINT DEFAULT NULL,
  request_payload LONGTEXT,
  response_payload LONGTEXT,
  http_status INT DEFAULT NULL,
  execution_status ENUM('SUCCESS','FAILED','RETRY_PENDING') NOT NULL,
  executed_at DATETIME(6) NOT NULL,
  retry_count INT NOT NULL,
  status ENUM('ACTIVE','PENDING','ARCHIVED') NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  KEY fk_integration_execution_log_provider (provider_id),
  CONSTRAINT fk_integration_execution_log_provider FOREIGN KEY (provider_id) REFERENCES integration_provider (id)
);

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE integration_execution_log;
TRUNCATE TABLE integration_provider;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO integration_provider
  (id, provider_code, provider_name, provider_type, base_url, auth_type, api_key, username, password_enc, timeout_sec, status, created_at, updated_at)
VALUES
  (1, 'INT-00001', 'SMTP Sandbox Alpha', 'EMAIL', 'https://smtp-alpha.sandbox.local/api', 'API_KEY', 'alpha-mail-key-2026', NULL, TO_BASE64('alpha-mail-pass'), 25, 'ACTIVE', '2026-05-02 08:00:00.000000', '2026-05-02 08:00:00.000000'),
  (2, 'INT-00002', 'SMS Gateway Prime', 'SMS', 'https://sms-prime.sandbox.local/send', 'API_KEY', 'prime-sms-key-2026', NULL, TO_BASE64('prime-sms-pass'), 20, 'ACTIVE', '2026-05-02 08:05:00.000000', '2026-05-02 08:05:00.000000'),
  (3, 'INT-00003', 'Push Relay One', 'PUSH', 'https://push-relay-one.sandbox.local/send', 'BEARER', 'push-relay-token', NULL, TO_BASE64('push-secret-1'), 15, 'ACTIVE', '2026-05-02 08:10:00.000000', '2026-05-02 08:10:00.000000'),
  (4, 'INT-00004', 'Payment Switch Core', 'PAYMENT', 'https://payment-core.sandbox.local/api', 'USERNAME_PASSWORD', NULL, 'paycore', TO_BASE64('paycore-pass'), 40, 'ACTIVE', '2026-05-02 08:15:00.000000', '2026-05-02 08:15:00.000000'),
  (5, 'INT-00005', 'Mobile Banking Link A', 'MOBILE_BANKING', 'https://mb-link-a.sandbox.local/api', 'BASIC', NULL, 'mbanka', TO_BASE64('mbank-pass-a'), 35, 'ACTIVE', '2026-05-02 08:20:00.000000', '2026-05-02 08:20:00.000000'),
  (6, 'INT-00006', 'SMTP Sandbox Beta', 'EMAIL', 'https://smtp-beta-timeout.sandbox.local/api', 'API_KEY', 'beta-mail-key-2026', NULL, TO_BASE64('beta-mail-pass'), 10, 'ACTIVE', '2026-05-02 08:25:00.000000', '2026-05-02 08:25:00.000000'),
  (7, 'INT-00007', 'SMS Gateway Backup', 'SMS', 'https://sms-backup.sandbox.local/send', 'API_KEY', 'backup-sms-key-2026', NULL, TO_BASE64('backup-sms-pass'), 22, 'ACTIVE', '2026-05-02 08:30:00.000000', '2026-05-02 08:30:00.000000'),
  (8, 'INT-00008', 'Notification Router', 'GENERAL', 'https://notify-router.sandbox.local/api', 'NONE', NULL, NULL, NULL, 18, 'ACTIVE', '2026-05-02 08:35:00.000000', '2026-05-02 08:35:00.000000'),
  (9, 'INT-00009', 'Payment Switch Backup', 'PAYMENT', 'https://payment-backup-fail.sandbox.local/api', 'USERNAME_PASSWORD', NULL, 'paybackup', TO_BASE64('paybackup-pass'), 45, 'ACTIVE', '2026-05-02 08:40:00.000000', '2026-05-02 08:40:00.000000'),
  (10, 'INT-00010', 'Mobile Banking Link B', 'MOBILE_BANKING', 'https://mb-link-b.sandbox.local/api', 'BASIC', NULL, 'mbankb', TO_BASE64('mbank-pass-b'), 30, 'ACTIVE', '2026-05-02 08:45:00.000000', '2026-05-02 08:45:00.000000'),
  (11, 'INT-00011', 'Shariah Report Gateway', 'GENERAL', 'https://report-gateway.sandbox.local/api', 'BEARER', 'report-bearer-token', NULL, TO_BASE64('report-secret'), 28, 'ACTIVE', '2026-05-02 08:50:00.000000', '2026-05-02 08:50:00.000000'),
  (12, 'INT-00012', 'Legacy Email Provider', 'EMAIL', 'https://legacy-email.sandbox.local/api', 'API_KEY', 'legacy-email-key', NULL, TO_BASE64('legacy-email-pass'), 25, 'ARCHIVED', '2026-05-01 08:55:00.000000', '2026-05-02 08:55:00.000000'),
  (13, 'INT-00013', 'AML Screening Connector', 'GENERAL', 'https://aml-screen.sandbox.local/api', 'BEARER', 'aml-screen-token', NULL, TO_BASE64('aml-secret'), 50, 'ACTIVE', '2026-05-02 09:00:00.000000', '2026-05-02 09:00:00.000000'),
  (14, 'INT-00014', 'Card Push Mirror', 'PUSH', 'https://card-push-mirror.sandbox.local/api', 'BEARER', 'card-push-token', NULL, TO_BASE64('card-push-secret'), 16, 'ACTIVE', '2026-05-02 09:05:00.000000', '2026-05-02 09:05:00.000000'),
  (15, 'INT-00015', 'Inactive Test Node', 'GENERAL', 'https://inactive-test-node.sandbox.local/api', 'NONE', NULL, NULL, NULL, 12, 'ARCHIVED', '2026-05-01 09:10:00.000000', '2026-05-02 09:10:00.000000');

INSERT INTO integration_execution_log
  (id, provider_id, reference_module, reference_id, request_payload, response_payload, http_status, execution_status, executed_at, retry_count, status, created_at, updated_at)
VALUES
  (1, 1, 'NOTIFICATION', 101, '{"channel":"EMAIL","template":"CUSTOMER_WELCOME"}', 'SMTP sandbox accepted notification request successfully.', 200, 'SUCCESS', '2026-05-02 10:00:00.000000', 0, 'ACTIVE', '2026-05-02 10:00:00.000000', '2026-05-02 10:00:00.000000'),
  (2, 2, 'VERIFICATION', 202, '{"channel":"SMS","purpose":"VERIFY_MOBILE"}', 'SMS gateway delivered OTP notification.', 200, 'SUCCESS', '2026-05-02 10:05:00.000000', 0, 'ACTIVE', '2026-05-02 10:05:00.000000', '2026-05-02 10:05:00.000000'),
  (3, 3, 'CARD', 303, '{"channel":"PUSH","event":"CARD_ISSUED"}', 'Push relay delivered device notification.', 200, 'SUCCESS', '2026-05-02 10:10:00.000000', 0, 'ACTIVE', '2026-05-02 10:10:00.000000', '2026-05-02 10:10:00.000000'),
  (4, 4, 'PAYMENT', 404, '{"transaction":"DISBURSEMENT","amount":75000}', 'Payment switch acknowledged outbound settlement.', 200, 'SUCCESS', '2026-05-02 10:15:00.000000', 0, 'ACTIVE', '2026-05-02 10:15:00.000000', '2026-05-02 10:15:00.000000'),
  (5, 5, 'ACCOUNT', 505, '{"accountNo":"ACC-2026-0005","event":"MB_LINK"}', 'Mobile banking link completed successfully.', 200, 'SUCCESS', '2026-05-02 10:20:00.000000', 0, 'ACTIVE', '2026-05-02 10:20:00.000000', '2026-05-02 10:20:00.000000'),
  (6, 6, 'NOTIFICATION', 606, '{"channel":"EMAIL","template":"KYC_SUBMITTED"}', 'SMTP sandbox beta timed out while posting payload.', 504, 'FAILED', '2026-05-02 10:25:00.000000', 1, 'ACTIVE', '2026-05-02 10:25:00.000000', '2026-05-02 10:25:00.000000'),
  (7, 7, 'NOTIFICATION', 707, '{"channel":"SMS","template":"ACCOUNT_BLOCK_ALERT"}', 'Backup SMS gateway delivered alert after failover.', 200, 'SUCCESS', '2026-05-02 10:30:00.000000', 1, 'ACTIVE', '2026-05-02 10:30:00.000000', '2026-05-02 10:30:00.000000'),
  (8, 8, 'WORKFLOW', 808, '{"action":"ROUTE_NOTIFICATION","module":"CONTRACT"}', 'Router normalized and forwarded event successfully.', 202, 'SUCCESS', '2026-05-02 10:35:00.000000', 0, 'ACTIVE', '2026-05-02 10:35:00.000000', '2026-05-02 10:35:00.000000'),
  (9, 9, 'PAYMENT', 909, '{"transaction":"PAYOUT","amount":120000}', 'Backup payment endpoint rejected settlement window.', 503, 'FAILED', '2026-05-02 10:40:00.000000', 2, 'ACTIVE', '2026-05-02 10:40:00.000000', '2026-05-02 10:40:00.000000'),
  (10, 10, 'ACCOUNT', 1001, '{"accountNo":"ACC-2026-0010","event":"BALANCE_SYNC"}', 'Mobile banking balance sync queued for retry.', 202, 'RETRY_PENDING', '2026-05-02 10:45:00.000000', 2, 'ACTIVE', '2026-05-02 10:45:00.000000', '2026-05-02 10:45:00.000000'),
  (11, 11, 'REPORT', 1101, '{"report":"SHARIAH_ANNUAL","format":"PDF"}', 'Report gateway rendered and delivered pack successfully.', 200, 'SUCCESS', '2026-05-02 10:50:00.000000', 0, 'ACTIVE', '2026-05-02 10:50:00.000000', '2026-05-02 10:50:00.000000'),
  (12, 13, 'SECURITY', 1201, '{"screening":"AML","customerId":15}', 'AML screening connector returned temporary backlog notice.', 429, 'RETRY_PENDING', '2026-05-02 10:55:00.000000', 1, 'ACTIVE', '2026-05-02 10:55:00.000000', '2026-05-02 10:55:00.000000'),
  (13, 14, 'CARD', 1301, '{"event":"PIN_CHANGED","channel":"PUSH"}', 'Card push mirror delivered alert successfully.', 200, 'SUCCESS', '2026-05-02 11:00:00.000000', 0, 'ACTIVE', '2026-05-02 11:00:00.000000', '2026-05-02 11:00:00.000000'),
  (14, 4, 'FINANCING', 1401, '{"applicationNo":"FIN-1401","event":"DISBURSEMENT"}', 'Payment switch core timeout from downstream partner.', 504, 'FAILED', '2026-05-02 11:05:00.000000', 1, 'ACTIVE', '2026-05-02 11:05:00.000000', '2026-05-02 11:05:00.000000'),
  (15, 1, 'NOTIFICATION', 1501, '{"channel":"EMAIL","template":"STATEMENT_READY"}', 'SMTP sandbox accepted statement-ready email.', 200, 'SUCCESS', '2026-05-02 11:10:00.000000', 0, 'ACTIVE', '2026-05-02 11:10:00.000000', '2026-05-02 11:10:00.000000');

ALTER TABLE integration_provider AUTO_INCREMENT = 16;
ALTER TABLE integration_execution_log AUTO_INCREMENT = 16;
