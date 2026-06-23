CREATE TABLE IF NOT EXISTS lookup_type (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  type_code VARCHAR(80) NOT NULL,
  type_name VARCHAR(160) NOT NULL,
  description VARCHAR(1000) NULL,
  status VARCHAR(20) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  UNIQUE KEY uk_lookup_type_code (type_code)
);

CREATE TABLE IF NOT EXISTS lookup_value (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  lookup_type_id BIGINT NOT NULL,
  value_code VARCHAR(80) NOT NULL,
  value_label VARCHAR(160) NOT NULL,
  value_bn_label VARCHAR(160) NULL,
  sort_order INT NULL,
  extra_data VARCHAR(2000) NULL,
  status VARCHAR(20) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  CONSTRAINT fk_lookup_value_type FOREIGN KEY (lookup_type_id) REFERENCES lookup_type(id),
  UNIQUE KEY uk_lookup_type_value_code (lookup_type_id, value_code)
);

DELETE FROM lookup_value WHERE extra_data LIKE '%LOOKUP_SEED%';
DELETE FROM lookup_type WHERE description LIKE '%LOOKUP_SEED%';

INSERT INTO lookup_type (type_code, type_name, description, status, created_at, updated_at) VALUES
('BRANCH_TYPE', 'Branch Type', 'Branch type classification for branch setup | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:00:00', '2026-05-04 08:00:00'),
('CUSTOMER_SEGMENT', 'Customer Segment', 'Customer segment used in onboarding and marketing | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:01:00', '2026-05-04 08:01:00'),
('KYC_RISK_RATING', 'KYC Risk Rating', 'Risk grading for customer due diligence | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:02:00', '2026-05-04 08:02:00'),
('ACCOUNT_STATUS_REASON', 'Account Status Reason', 'Reason code for account block or archive actions | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:03:00', '2026-05-04 08:03:00'),
('FINANCING_PURPOSE', 'Financing Purpose', 'Permitted purposes for Islamic financing applications | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:04:00', '2026-05-04 08:04:00'),
('NOTIFICATION_CHANNEL', 'Notification Channel', 'Delivery channel list for alerts and notices | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:05:00', '2026-05-04 08:05:00'),
('REPORT_CATEGORY', 'Report Category', 'Grouping for MIS and regulatory reports | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:06:00', '2026-05-04 08:06:00'),
('SHARIAH_DECISION', 'Shariah Decision', 'Decision outcomes used in Shariah review module | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:07:00', '2026-05-04 08:07:00'),
('ZAKAT_CATEGORY', 'Zakat Category', 'Zakat allocation categories and disbursement types | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:08:00', '2026-05-04 08:08:00'),
('CARD_EVENT_TYPE', 'Card Event Type', 'Card lifecycle and PIN event classification | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:09:00', '2026-05-04 08:09:00'),
('ATM_DEVICE_TYPE', 'ATM Device Type', 'ATM terminal and recycler device categorization | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:10:00', '2026-05-04 08:10:00'),
('SECURITY_ALERT_TYPE', 'Security Alert Type', 'Security event alert mapping reference | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:11:00', '2026-05-04 08:11:00'),
('WORKFLOW_STAGE', 'Workflow Stage', 'Generic workflow stage reference values | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:12:00', '2026-05-04 08:12:00'),
('STATEMENT_FORMAT', 'Statement Format', 'Statement export format reference values | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:13:00', '2026-05-04 08:13:00'),
('DEPOSIT_SCHEME_MODE', 'Deposit Scheme Mode', 'Mode of deposit scheme collection or maturity settlement | LOOKUP_SEED', 'ACTIVE', '2026-05-04 08:14:00', '2026-05-04 08:14:00');

INSERT INTO lookup_value (lookup_type_id, value_code, value_label, value_bn_label, sort_order, extra_data, status, created_at, updated_at) VALUES
((SELECT id FROM lookup_type WHERE type_code = 'BRANCH_TYPE'), 'URBAN', 'Urban Branch', 'শহর শাখা', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:00:00', '2026-05-04 09:00:00'),
((SELECT id FROM lookup_type WHERE type_code = 'BRANCH_TYPE'), 'RURAL', 'Rural Branch', 'গ্রাম শাখা', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:01:00', '2026-05-04 09:01:00'),
((SELECT id FROM lookup_type WHERE type_code = 'CUSTOMER_SEGMENT'), 'RETAIL', 'Retail Customer', 'রিটেইল গ্রাহক', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:02:00', '2026-05-04 09:02:00'),
((SELECT id FROM lookup_type WHERE type_code = 'CUSTOMER_SEGMENT'), 'SME', 'SME Customer', 'এসএমই গ্রাহক', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:03:00', '2026-05-04 09:03:00'),
((SELECT id FROM lookup_type WHERE type_code = 'KYC_RISK_RATING'), 'LOW', 'Low Risk', 'নিম্ন ঝুঁকি', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:04:00', '2026-05-04 09:04:00'),
((SELECT id FROM lookup_type WHERE type_code = 'KYC_RISK_RATING'), 'MEDIUM', 'Medium Risk', 'মাঝারি ঝুঁকি', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:05:00', '2026-05-04 09:05:00'),
((SELECT id FROM lookup_type WHERE type_code = 'KYC_RISK_RATING'), 'HIGH', 'High Risk', 'উচ্চ ঝুঁকি', 3, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:06:00', '2026-05-04 09:06:00'),
((SELECT id FROM lookup_type WHERE type_code = 'ACCOUNT_STATUS_REASON'), 'DORMANT', 'Dormant Period Exceeded', 'নিষ্ক্রিয় সময়সীমা অতিক্রম', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:07:00', '2026-05-04 09:07:00'),
((SELECT id FROM lookup_type WHERE type_code = 'ACCOUNT_STATUS_REASON'), 'AML_HOLD', 'AML Hold Applied', 'এএমএল হোল্ড', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:08:00', '2026-05-04 09:08:00'),
((SELECT id FROM lookup_type WHERE type_code = 'FINANCING_PURPOSE'), 'WORKING_CAPITAL', 'Working Capital', 'কার্যকরী মূলধন', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:09:00', '2026-05-04 09:09:00'),
((SELECT id FROM lookup_type WHERE type_code = 'FINANCING_PURPOSE'), 'ASSET_PURCHASE', 'Asset Purchase', 'সম্পদ ক্রয়', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:10:00', '2026-05-04 09:10:00'),
((SELECT id FROM lookup_type WHERE type_code = 'NOTIFICATION_CHANNEL'), 'EMAIL', 'Email', 'ইমেইল', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:11:00', '2026-05-04 09:11:00'),
((SELECT id FROM lookup_type WHERE type_code = 'NOTIFICATION_CHANNEL'), 'SMS', 'SMS', 'এসএমএস', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:12:00', '2026-05-04 09:12:00'),
((SELECT id FROM lookup_type WHERE type_code = 'NOTIFICATION_CHANNEL'), 'PUSH', 'Push Notification', 'পুশ নোটিফিকেশন', 3, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:13:00', '2026-05-04 09:13:00'),
((SELECT id FROM lookup_type WHERE type_code = 'REPORT_CATEGORY'), 'OPERATIONS', 'Operations', 'অপারেশনস', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:14:00', '2026-05-04 09:14:00'),
((SELECT id FROM lookup_type WHERE type_code = 'REPORT_CATEGORY'), 'REGULATORY', 'Regulatory', 'নিয়ন্ত্রক', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:15:00', '2026-05-04 09:15:00'),
((SELECT id FROM lookup_type WHERE type_code = 'SHARIAH_DECISION'), 'APPROVED', 'Approved', 'অনুমোদিত', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:16:00', '2026-05-04 09:16:00'),
((SELECT id FROM lookup_type WHERE type_code = 'SHARIAH_DECISION'), 'RETURNED', 'Returned for Correction', 'সংশোধনের জন্য ফেরত', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:17:00', '2026-05-04 09:17:00'),
((SELECT id FROM lookup_type WHERE type_code = 'ZAKAT_CATEGORY'), 'POOR', 'Poor & Needy', 'গরিব ও অভাবী', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:18:00', '2026-05-04 09:18:00'),
((SELECT id FROM lookup_type WHERE type_code = 'ZAKAT_CATEGORY'), 'MEDICAL', 'Medical Support', 'চিকিৎসা সহায়তা', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:19:00', '2026-05-04 09:19:00'),
((SELECT id FROM lookup_type WHERE type_code = 'CARD_EVENT_TYPE'), 'PIN_RESET', 'PIN Reset', 'পিন রিসেট', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:20:00', '2026-05-04 09:20:00'),
((SELECT id FROM lookup_type WHERE type_code = 'CARD_EVENT_TYPE'), 'BLOCK', 'Card Block', 'কার্ড ব্লক', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:21:00', '2026-05-04 09:21:00'),
((SELECT id FROM lookup_type WHERE type_code = 'ATM_DEVICE_TYPE'), 'ATM', 'ATM Terminal', 'এটিএম টার্মিনাল', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:22:00', '2026-05-04 09:22:00'),
((SELECT id FROM lookup_type WHERE type_code = 'ATM_DEVICE_TYPE'), 'CDM', 'CDM Terminal', 'সিডিএম টার্মিনাল', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:23:00', '2026-05-04 09:23:00'),
((SELECT id FROM lookup_type WHERE type_code = 'SECURITY_ALERT_TYPE'), 'FAILED_LOGIN', 'Failed Login Alert', 'ব্যর্থ লগইন সতর্কতা', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:24:00', '2026-05-04 09:24:00'),
((SELECT id FROM lookup_type WHERE type_code = 'SECURITY_ALERT_TYPE'), 'AML_FLAG', 'AML Flag Alert', 'এএমএল ফ্ল্যাগ সতর্কতা', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:25:00', '2026-05-04 09:25:00'),
((SELECT id FROM lookup_type WHERE type_code = 'WORKFLOW_STAGE'), 'SUBMITTED', 'Submitted', 'দাখিলকৃত', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:26:00', '2026-05-04 09:26:00'),
((SELECT id FROM lookup_type WHERE type_code = 'WORKFLOW_STAGE'), 'UNDER_REVIEW', 'Under Review', 'পর্যালোচনায়', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:27:00', '2026-05-04 09:27:00'),
((SELECT id FROM lookup_type WHERE type_code = 'STATEMENT_FORMAT'), 'PDF', 'PDF', 'পিডিএফ', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:28:00', '2026-05-04 09:28:00'),
((SELECT id FROM lookup_type WHERE type_code = 'STATEMENT_FORMAT'), 'XLSX', 'Excel', 'এক্সেল', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:29:00', '2026-05-04 09:29:00'),
((SELECT id FROM lookup_type WHERE type_code = 'DEPOSIT_SCHEME_MODE'), 'MONTHLY', 'Monthly Collection', 'মাসিক সংগ্রহ', 1, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:30:00', '2026-05-04 09:30:00'),
((SELECT id FROM lookup_type WHERE type_code = 'DEPOSIT_SCHEME_MODE'), 'MATURITY_TRANSFER', 'Transfer on Maturity', 'মেয়াদ শেষে স্থানান্তর', 2, '{"seed":"LOOKUP_SEED"}', 'ACTIVE', '2026-05-04 09:31:00', '2026-05-04 09:31:00');
