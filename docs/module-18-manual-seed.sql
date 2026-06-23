SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE notification_log;
TRUNCATE TABLE notification_event;
TRUNCATE TABLE notification_template;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO notification_template
  (id, template_code, template_name, channel_type, subject_text, body_text, status, created_at, updated_at)
VALUES
  (1, 'NTF-00001', 'Customer Welcome Email', 'EMAIL', 'Welcome to SBMS', 'Dear customer, welcome to SBMS Islamic banking services.', 'ACTIVE', '2026-05-02 08:00:00.000000', '2026-05-02 08:00:00.000000'),
  (2, 'NTF-00002', 'Customer Welcome SMS', 'SMS', NULL, 'Welcome to SBMS. Your onboarding is now active.', 'ACTIVE', '2026-05-02 08:05:00.000000', '2026-05-02 08:05:00.000000'),
  (3, 'NTF-00003', 'KYC Submitted Alert', 'EMAIL', 'KYC Submitted', 'KYC submission received and pending review.', 'ACTIVE', '2026-05-02 08:10:00.000000', '2026-05-02 08:10:00.000000'),
  (4, 'NTF-00004', 'KYC Approval SMS', 'SMS', NULL, 'Your KYC has been approved successfully.', 'ACTIVE', '2026-05-02 08:15:00.000000', '2026-05-02 08:15:00.000000'),
  (5, 'NTF-00005', 'Account Opened Email', 'EMAIL', 'Account Opened', 'Your Islamic account has been opened successfully.', 'ACTIVE', '2026-05-02 08:20:00.000000', '2026-05-02 08:20:00.000000'),
  (6, 'NTF-00006', 'Account Block Alert', 'SMS', NULL, 'Your account has been blocked. Please contact branch.', 'ACTIVE', '2026-05-02 08:25:00.000000', '2026-05-02 08:25:00.000000'),
  (7, 'NTF-00007', 'ATM Replenishment Notice', 'EMAIL', 'ATM Replenishment', 'ATM replenishment has been recorded for your branch terminal.', 'ACTIVE', '2026-05-02 08:30:00.000000', '2026-05-02 08:30:00.000000'),
  (8, 'NTF-00008', 'Profit Posting Complete', 'EMAIL', 'Profit Posted', 'Scheduled profit posting completed for the selected cycle.', 'ACTIVE', '2026-05-02 08:35:00.000000', '2026-05-02 08:35:00.000000'),
  (9, 'NTF-00009', 'Financing Approval Email', 'EMAIL', 'Financing Approved', 'Your financing application has been approved.', 'ACTIVE', '2026-05-02 08:40:00.000000', '2026-05-02 08:40:00.000000'),
  (10, 'NTF-00010', 'Financing Disbursement SMS', 'SMS', NULL, 'Financing disbursement completed. Check your linked account.', 'ACTIVE', '2026-05-02 08:45:00.000000', '2026-05-02 08:45:00.000000'),
  (11, 'NTF-00011', 'Card Issued Push', 'PUSH', 'Card Issued', 'Your debit card is ready for activation.', 'ACTIVE', '2026-05-02 08:50:00.000000', '2026-05-02 08:50:00.000000'),
  (12, 'NTF-00012', 'Statement Ready Email', 'EMAIL', 'Statement Ready', 'Your requested statement is ready for download.', 'ACTIVE', '2026-05-02 08:55:00.000000', '2026-05-02 08:55:00.000000'),
  (13, 'NTF-00013', 'Zakat Due Alert', 'SMS', NULL, 'Zakat due amount has been calculated for your profile.', 'ACTIVE', '2026-05-02 09:00:00.000000', '2026-05-02 09:00:00.000000'),
  (14, 'NTF-00014', 'Retry Queue Support Alert', 'EMAIL', 'Delivery Retry Needed', 'A notification failed and has been moved to retry queue.', 'ACTIVE', '2026-05-02 09:05:00.000000', '2026-05-02 09:05:00.000000'),
  (15, 'NTF-00015', 'Legacy Template Archived', 'EMAIL', 'Legacy Notice', 'Legacy notification template kept for archive demonstration.', 'ARCHIVED', '2026-05-01 09:10:00.000000', '2026-05-02 09:10:00.000000');

INSERT INTO notification_event
  (id, event_code, event_name, reference_module, status, created_at, updated_at)
VALUES
  (1, 'EVT-00001', 'Customer Created', 'CUSTOMER', 'ACTIVE', '2026-05-02 09:15:00.000000', '2026-05-02 09:15:00.000000'),
  (2, 'EVT-00002', 'Customer KYC Submitted', 'KYC', 'ACTIVE', '2026-05-02 09:16:00.000000', '2026-05-02 09:16:00.000000'),
  (3, 'EVT-00003', 'Customer KYC Approved', 'KYC', 'ACTIVE', '2026-05-02 09:17:00.000000', '2026-05-02 09:17:00.000000'),
  (4, 'EVT-00004', 'Account Opened', 'ACCOUNT', 'ACTIVE', '2026-05-02 09:18:00.000000', '2026-05-02 09:18:00.000000'),
  (5, 'EVT-00005', 'Account Blocked', 'ACCOUNT', 'ACTIVE', '2026-05-02 09:19:00.000000', '2026-05-02 09:19:00.000000'),
  (6, 'EVT-00006', 'ATM Replenishment Completed', 'ATM', 'ACTIVE', '2026-05-02 09:20:00.000000', '2026-05-02 09:20:00.000000'),
  (7, 'EVT-00007', 'Profit Posting Completed', 'PROFIT', 'ACTIVE', '2026-05-02 09:21:00.000000', '2026-05-02 09:21:00.000000'),
  (8, 'EVT-00008', 'Financing Approved', 'FINANCING', 'ACTIVE', '2026-05-02 09:22:00.000000', '2026-05-02 09:22:00.000000'),
  (9, 'EVT-00009', 'Financing Disbursed', 'FINANCING', 'ACTIVE', '2026-05-02 09:23:00.000000', '2026-05-02 09:23:00.000000'),
  (10, 'EVT-00010', 'Card Issued', 'CARD', 'ACTIVE', '2026-05-02 09:24:00.000000', '2026-05-02 09:24:00.000000'),
  (11, 'EVT-00011', 'Statement Generated', 'STATEMENT', 'ACTIVE', '2026-05-02 09:25:00.000000', '2026-05-02 09:25:00.000000'),
  (12, 'EVT-00012', 'Zakat Calculated', 'ZAKAT', 'ACTIVE', '2026-05-02 09:26:00.000000', '2026-05-02 09:26:00.000000'),
  (13, 'EVT-00013', 'Delivery Retry Queued', 'NOTIFICATION', 'ACTIVE', '2026-05-02 09:27:00.000000', '2026-05-02 09:27:00.000000'),
  (14, 'EVT-00014', 'Push Notification Sent', 'CARD', 'ACTIVE', '2026-05-02 09:28:00.000000', '2026-05-02 09:28:00.000000'),
  (15, 'EVT-00015', 'Legacy Event Archived', 'GENERAL', 'ARCHIVED', '2026-05-01 09:29:00.000000', '2026-05-02 09:29:00.000000');

INSERT INTO notification_log
  (id, event_id, template_id, recipient_to, channel_type, delivery_status, provider_response, retry_count, sent_at, status, created_at, updated_at)
VALUES
  (1, 1, 1, 'rahim.customer@test.local', 'EMAIL', 'SENT', 'SMTP sandbox accepted customer welcome email.', 0, '2026-05-02 10:00:00.000000', 'ACTIVE', '2026-05-02 10:00:00.000000', '2026-05-02 10:00:00.000000'),
  (2, 1, 2, '01710000001', 'SMS', 'SENT', 'SMS gateway delivered onboarding alert.', 0, '2026-05-02 10:05:00.000000', 'ACTIVE', '2026-05-02 10:05:00.000000', '2026-05-02 10:05:00.000000'),
  (3, 2, 3, 'kyc.ops@test.local', 'EMAIL', 'SENT', 'KYC submission alert delivered to operations inbox.', 0, '2026-05-02 10:10:00.000000', 'ACTIVE', '2026-05-02 10:10:00.000000', '2026-05-02 10:10:00.000000'),
  (4, 3, 4, '01710000002', 'SMS', 'FAILED', 'Provider timeout while sending KYC approval SMS.', 1, '2026-05-02 10:15:00.000000', 'ACTIVE', '2026-05-02 10:15:00.000000', '2026-05-02 10:15:00.000000'),
  (5, 4, 5, 'nadia.customer@test.local', 'EMAIL', 'SENT', 'Account opening email delivered.', 0, '2026-05-02 10:20:00.000000', 'ACTIVE', '2026-05-02 10:20:00.000000', '2026-05-02 10:20:00.000000'),
  (6, 5, 6, '01710000003', 'SMS', 'FAILED', 'Subscriber unreachable during account block alert.', 2, '2026-05-02 10:25:00.000000', 'ACTIVE', '2026-05-02 10:25:00.000000', '2026-05-02 10:25:00.000000'),
  (7, 6, 7, 'atm.branch@test.local', 'EMAIL', 'SENT', 'ATM replenishment branch alert delivered.', 0, '2026-05-02 10:30:00.000000', 'ACTIVE', '2026-05-02 10:30:00.000000', '2026-05-02 10:30:00.000000'),
  (8, 7, 8, 'profit.ops@test.local', 'EMAIL', 'SENT', 'Profit posting completion alert delivered.', 0, '2026-05-02 10:35:00.000000', 'ACTIVE', '2026-05-02 10:35:00.000000', '2026-05-02 10:35:00.000000'),
  (9, 8, 9, 'fin.customer@test.local', 'EMAIL', 'RETRY_QUEUED', 'Mailbox temporary failure; operator retry queued.', 2, '2026-05-02 10:40:00.000000', 'ACTIVE', '2026-05-02 10:40:00.000000', '2026-05-02 10:40:00.000000'),
  (10, 9, 10, '01710000004', 'SMS', 'SENT', 'Financing disbursement SMS delivered.', 0, '2026-05-02 10:45:00.000000', 'ACTIVE', '2026-05-02 10:45:00.000000', '2026-05-02 10:45:00.000000'),
  (11, 10, 11, 'app-device-009', 'PUSH', 'SENT', 'Push service acknowledged card issue push.', 0, '2026-05-02 10:50:00.000000', 'ACTIVE', '2026-05-02 10:50:00.000000', '2026-05-02 10:50:00.000000'),
  (12, 11, 12, 'statement.customer@test.local', 'EMAIL', 'RETRY_QUEUED', 'Attachment too large for first attempt; retry queued.', 1, '2026-05-02 10:55:00.000000', 'ACTIVE', '2026-05-02 10:55:00.000000', '2026-05-02 10:55:00.000000'),
  (13, 12, 13, '01710000005', 'SMS', 'SENT', 'Zakat due alert delivered.', 0, '2026-05-02 11:00:00.000000', 'ACTIVE', '2026-05-02 11:00:00.000000', '2026-05-02 11:00:00.000000'),
  (14, 13, 14, 'support.ops@test.local', 'EMAIL', 'FAILED', 'Retry support alert rejected by local sandbox for malformed address.', 3, '2026-05-02 11:05:00.000000', 'ACTIVE', '2026-05-02 11:05:00.000000', '2026-05-02 11:05:00.000000'),
  (15, 14, 11, 'app-device-015', 'PUSH', 'SENT', 'Push notification delivered to mobile app channel.', 0, '2026-05-02 11:10:00.000000', 'ACTIVE', '2026-05-02 11:10:00.000000', '2026-05-02 11:10:00.000000');

ALTER TABLE notification_template AUTO_INCREMENT = 16;
ALTER TABLE notification_event AUTO_INCREMENT = 16;
ALTER TABLE notification_log AUTO_INCREMENT = 16;
