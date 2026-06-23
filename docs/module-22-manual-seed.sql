CREATE TABLE IF NOT EXISTS workflow_history (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  module_name VARCHAR(80) NOT NULL,
  reference_id BIGINT NOT NULL,
  action_name VARCHAR(80) NOT NULL,
  from_status VARCHAR(40) NULL,
  to_status VARCHAR(40) NULL,
  action_by VARCHAR(120) NOT NULL,
  action_at DATETIME NOT NULL,
  remarks VARCHAR(1000) NULL,
  status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS workflow_comment (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  module_name VARCHAR(80) NOT NULL,
  reference_id BIGINT NOT NULL,
  comment_text VARCHAR(1000) NOT NULL,
  comment_by VARCHAR(120) NOT NULL,
  comment_at DATETIME NOT NULL,
  status VARCHAR(20) NOT NULL
);

DELETE FROM workflow_comment WHERE comment_by = 'WORKFLOW_SEED';
DELETE FROM workflow_history WHERE remarks LIKE '%WORKFLOW_SEED%';

INSERT INTO workflow_history (module_name, reference_id, action_name, from_status, to_status, action_by, action_at, remarks, status) VALUES
('CUSTOMER', 1, 'SUBMIT_PROFILE', 'DRAFT', 'SUBMITTED', 'SYSTEM', '2026-05-04 08:10:00', 'Customer onboarding submitted from branch desk | WORKFLOW_SEED', 'ACTIVE'),
('KYC', 1, 'ROUTE_FOR_REVIEW', 'SUBMITTED', 'PENDING_REVIEW', 'SYSTEM', '2026-05-04 08:20:00', 'KYC profile routed to compliance review | WORKFLOW_SEED', 'ACTIVE'),
('ACCOUNT', 1, 'VERIFY_ACCOUNT', 'PENDING_REVIEW', 'VERIFIED', 'OPS_OFFICER', '2026-05-04 08:40:00', 'Account opening request validated against KYC and branch profile | WORKFLOW_SEED', 'ACTIVE'),
('FINANCING', 1, 'APPROVE_APPLICATION', 'UNDER_REVIEW', 'APPROVED', 'INVESTMENT_OFFICER', '2026-05-04 09:00:00', 'Financing application approved after review committee sign-off | WORKFLOW_SEED', 'ACTIVE'),
('SHARIAH', 1, 'START_REVIEW', 'SUBMITTED', 'UNDER_REVIEW', 'SHARIAH_BOARD_MEMBER', '2026-05-04 09:15:00', 'Shariah case picked for detailed review and checklist validation | WORKFLOW_SEED', 'ACTIVE'),
('SECURITY_INVESTIGATION', 1, 'RETURN_CASE', 'ASSIGNED', 'RETURNED', 'SYSTEM', '2026-05-04 09:30:00', 'Investigation returned for branch clarification and supporting evidence | WORKFLOW_SEED', 'ACTIVE'),
('CONTRACT', 1, 'ASSIGN_DRAFT', 'DRAFT', 'ASSIGNED', 'SYSTEM', '2026-05-04 09:50:00', 'Contract drafting task assigned to legal workflow pool | WORKFLOW_SEED', 'ACTIVE'),
('REPORTS', 1, 'EXPORT_REPORT', 'APPROVED', 'COMPLETED', 'OPS_OFFICER', '2026-05-04 10:05:00', 'Regulatory export finished and file reference logged | WORKFLOW_SEED', 'ACTIVE'),
('NOTIFICATION', 1, 'RETRY_DELIVERY', 'FAILED', 'FAILED', 'SYSTEM', '2026-05-04 10:20:00', 'Notification delivery retried but provider failure persisted | WORKFLOW_SEED', 'ACTIVE'),
('ATM', 1, 'POST_RECONCILIATION', 'VERIFIED', 'POSTED', 'OPS_OFFICER', '2026-05-04 10:45:00', 'ATM reconciliation posted to terminal journal and cash ledger | WORKFLOW_SEED', 'ACTIVE'),
('ZAKAT', 1, 'SUBMIT_PAYOUT', 'DRAFT', 'PENDING', 'SYSTEM', '2026-05-04 11:00:00', 'Charity payout request submitted for operational approval | WORKFLOW_SEED', 'ACTIVE'),
('FINANCING', 2, 'CLOSE_DISBURSEMENT', 'APPROVED', 'CLOSED', 'BRANCH_MANAGER', '2026-05-04 11:20:00', 'Disbursement file closed after release confirmation | WORKFLOW_SEED', 'ACTIVE'),
('DEPOSIT_SCHEME', 1, 'REJECT_ENROLLMENT', 'UNDER_REVIEW', 'REJECTED', 'OPS_OFFICER', '2026-05-04 11:35:00', 'Enrollment rejected because nominee verification remained incomplete | WORKFLOW_SEED', 'ACTIVE'),
('KYC', 2, 'APPROVE_PROFILE', 'UNDER_REVIEW', 'APPROVED', 'SYSTEM', '2026-05-04 11:50:00', 'KYC profile approved after identity and address confirmation | WORKFLOW_SEED', 'ACTIVE'),
('ACCOUNT', 2, 'ASSIGN_FOR_POSTING', 'VERIFIED', 'ASSIGNED', 'SYSTEM', '2026-05-04 12:05:00', 'Account assigned to maker-checker posting queue | WORKFLOW_SEED', 'ACTIVE');

INSERT INTO workflow_comment (module_name, reference_id, comment_text, comment_by, comment_at, status) VALUES
('CUSTOMER', 1, 'Customer profile documents were received and checked at branch desk. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 08:12:00', 'ACTIVE'),
('KYC', 1, 'Risk screening completed before forwarding to review desk. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 08:23:00', 'ACTIVE'),
('ACCOUNT', 1, 'Linked customer and KYC profile matched successfully. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 08:42:00', 'ACTIVE'),
('FINANCING', 1, 'Committee approval remarks attached with recommendation sheet. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 09:03:00', 'ACTIVE'),
('SHARIAH', 1, 'Checklist item mismatch noted for revised contract wording. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 09:18:00', 'ACTIVE'),
('SECURITY_INVESTIGATION', 1, 'Branch clarification required for missing CCTV reference. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 09:33:00', 'ACTIVE'),
('CONTRACT', 1, 'Draft ownership assigned to legal support queue. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 09:53:00', 'ACTIVE'),
('REPORTS', 1, 'File export completed and queued for regulatory dispatch. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 10:08:00', 'ACTIVE'),
('NOTIFICATION', 1, 'Retry failed due to upstream SMTP timeout. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 10:22:00', 'ACTIVE'),
('ATM', 1, 'Reconciliation variance cleared by operations officer. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 10:48:00', 'ACTIVE'),
('ZAKAT', 1, 'Payout request requires final beneficiary approval. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 11:02:00', 'ACTIVE'),
('FINANCING', 2, 'Disbursement supporting file archived and locked. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 11:23:00', 'ACTIVE'),
('DEPOSIT_SCHEME', 1, 'Nominee photo ID mismatch caused rejection. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 11:38:00', 'ACTIVE'),
('KYC', 2, 'Approval completed after second-level address verification. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 11:53:00', 'ACTIVE'),
('ACCOUNT', 2, 'Posting queue assignment completed for maker-checker stage. | WORKFLOW_SEED', 'WORKFLOW_SEED', '2026-05-04 12:08:00', 'ACTIVE');
