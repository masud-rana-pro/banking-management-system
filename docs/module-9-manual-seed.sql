SET NAMES utf8mb4;

START TRANSACTION;

INSERT INTO transaction_journal (
  id, transaction_ref, transaction_date, transaction_type, channel_type, branch_id, terminal_id,
  debit_account_id, credit_account_id, amount, narration, posted_by, approved_by,
  reversal_flag, parent_transaction_id, transaction_status, status, created_at
) VALUES
  (1, 'TXN-000001', '2026-05-01 09:00:00', 'DEPOSIT', 'BRANCH_COUNTER', 1, 101, NULL, 1, 15000.00, 'Branch counter cash deposit for account 1', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'0', NULL, 'POSTED', 'ACTIVE', '2026-05-01 09:00:00'),
  (2, 'TXN-000002', '2026-05-01 09:15:00', 'WITHDRAWAL', 'BRANCH_COUNTER', 2, 102, 2, NULL, 2500.00, 'Customer withdrawal at Gulshan branch', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'1', NULL, 'REVERSED', 'ACTIVE', '2026-05-01 09:15:00'),
  (3, 'TXN-000003', '2026-05-01 09:30:00', 'TRANSFER', 'INTERNAL_TRANSFER', 1, NULL, 3, 4, 5000.00, 'Internal transfer between customer accounts', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'1', NULL, 'REVERSED', 'ACTIVE', '2026-05-01 09:30:00'),
  (4, 'TXN-000004', '2026-05-01 09:45:00', 'CHEQUE_CLEARING', 'CHEQUE_COUNTER', 2, NULL, NULL, 5, 8000.00, 'Cheque clearing credit for corporate account', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'0', NULL, 'POSTED', 'ACTIVE', '2026-05-01 09:45:00'),
  (5, 'TXN-000005', '2026-05-01 10:00:00', 'DEPOSIT', 'BRANCH_COUNTER', 1, 103, NULL, 6, 3200.00, 'Savings cash deposit', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'0', NULL, 'POSTED', 'ACTIVE', '2026-05-01 10:00:00'),
  (6, 'TXN-000006', '2026-05-01 10:15:00', 'WITHDRAWAL', 'BRANCH_COUNTER', 2, 104, 7, NULL, 1400.00, 'Umrah savings withdrawal', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'0', NULL, 'POSTED', 'ACTIVE', '2026-05-01 10:15:00'),
  (7, 'TXN-000007', '2026-05-01 10:30:00', 'TRANSFER', 'INTERNAL_TRANSFER', 1, NULL, 8, 9, 2200.00, 'Student to payroll transfer sample', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'0', NULL, 'POSTED', 'ACTIVE', '2026-05-01 10:30:00'),
  (8, 'TXN-000008', '2026-05-01 10:45:00', 'CHEQUE_CLEARING', 'CHEQUE_COUNTER', 2, NULL, NULL, 10, 18000.00, 'Business current cheque clearing', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'0', NULL, 'POSTED', 'ACTIVE', '2026-05-01 10:45:00'),
  (9, 'TXN-000009', '2026-05-01 11:00:00', 'DEPOSIT', 'BRANCH_COUNTER', 1, 105, NULL, 11, 4300.00, 'Women savings deposit sample', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'0', NULL, 'POSTED', 'ACTIVE', '2026-05-01 11:00:00'),
  (10, 'TXN-000010', '2026-05-01 11:15:00', 'WITHDRAWAL', 'BRANCH_COUNTER', 3, 106, 12, NULL, 1600.00, 'Senior citizen cash withdrawal', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'0', NULL, 'POSTED', 'ACTIVE', '2026-05-01 11:15:00'),
  (11, 'TXN-000011', '2026-05-01 11:30:00', 'TRANSFER', 'INTERNAL_TRANSFER', 2, NULL, 13, 14, 300.00, 'NRB FC to monthly deposit transfer', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'0', NULL, 'POSTED', 'ACTIVE', '2026-05-01 11:30:00'),
  (12, 'TXN-000012', '2026-05-01 11:45:00', 'CHEQUE_CLEARING', 'CHEQUE_COUNTER', 3, NULL, NULL, 15, 1100.00, 'Digital savings cheque in review', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'0', NULL, 'PENDING_REVIEW', 'ACTIVE', '2026-05-01 11:45:00'),
  (13, 'TXN-000013', '2026-05-01 12:00:00', 'REVERSAL', 'SYSTEM', 2, NULL, NULL, 2, 2500.00, 'Reversal of TXN-000002 due to teller entry correction', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'1', 2, 'POSTED', 'ACTIVE', '2026-05-01 12:00:00'),
  (14, 'TXN-000014', '2026-05-01 12:10:00', 'REVERSAL', 'SYSTEM', 1, NULL, 4, 3, 5000.00, 'Reversal of TXN-000003 after duplicate transfer detect', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'1', 3, 'POSTED', 'ACTIVE', '2026-05-01 12:10:00'),
  (15, 'TXN-000015', '2026-05-01 12:25:00', 'DEPOSIT', 'BRANCH_COUNTER', 2, 107, NULL, 5, 125000.00, 'High value suspicious deposit sample', 'SYSTEM_TELLER', 'SYSTEM_SUPERVISOR', b'0', NULL, 'POSTED', 'ACTIVE', '2026-05-01 12:25:00')
ON DUPLICATE KEY UPDATE
  transaction_ref = VALUES(transaction_ref),
  transaction_date = VALUES(transaction_date),
  transaction_type = VALUES(transaction_type),
  channel_type = VALUES(channel_type),
  branch_id = VALUES(branch_id),
  terminal_id = VALUES(terminal_id),
  debit_account_id = VALUES(debit_account_id),
  credit_account_id = VALUES(credit_account_id),
  amount = VALUES(amount),
  narration = VALUES(narration),
  posted_by = VALUES(posted_by),
  approved_by = VALUES(approved_by),
  reversal_flag = VALUES(reversal_flag),
  parent_transaction_id = VALUES(parent_transaction_id),
  transaction_status = VALUES(transaction_status),
  status = VALUES(status),
  created_at = VALUES(created_at);

INSERT INTO cash_transaction (
  id, transaction_id, cash_type, cash_direction, teller_user_id, branch_id, amount, remarks, created_at
) VALUES
  (1, 1, 'CASH', 'IN', 101, 1, 15000.00, 'Cash received for deposit voucher', '2026-05-01 09:00:00'),
  (2, 2, 'CASH', 'OUT', 102, 2, 2500.00, 'Cash handed to customer before reversal', '2026-05-01 09:15:00'),
  (3, 3, 'CASH', 'OUT', 101, 1, 5000.00, 'Transfer source cash trail', '2026-05-01 09:30:00'),
  (4, 4, 'CHEQUE', 'IN', 102, 2, 8000.00, 'Cheque received for clearing', '2026-05-01 09:45:00'),
  (5, 5, 'CASH', 'IN', 103, 1, 3200.00, 'Counter savings deposit', '2026-05-01 10:00:00'),
  (6, 6, 'CASH', 'OUT', 104, 2, 1400.00, 'Counter cash payout', '2026-05-01 10:15:00'),
  (7, 7, 'CASH', 'OUT', 101, 1, 2200.00, 'Transfer funding note', '2026-05-01 10:30:00'),
  (8, 8, 'CHEQUE', 'IN', 102, 2, 18000.00, 'Cheque accepted for business current', '2026-05-01 10:45:00'),
  (9, 9, 'CASH', 'IN', 105, 1, 4300.00, 'Women savings deposit note', '2026-05-01 11:00:00'),
  (10, 10, 'CASH', 'OUT', 106, 3, 1600.00, 'Senior savings cash payout', '2026-05-01 11:15:00'),
  (11, 11, 'CASH', 'OUT', 103, 2, 300.00, 'Transfer debit cash note', '2026-05-01 11:30:00'),
  (12, 12, 'CHEQUE', 'IN', 107, 3, 1100.00, 'Cheque pending review', '2026-05-01 11:45:00'),
  (13, 13, 'CASH', 'IN', 102, 2, 2500.00, 'Reversal cash recovery', '2026-05-01 12:00:00'),
  (14, 14, 'CASH', 'IN', 101, 1, 5000.00, 'Reversal recovery for transfer', '2026-05-01 12:10:00'),
  (15, 15, 'CASH', 'IN', 107, 2, 125000.00, 'Large cash deposit received', '2026-05-01 12:25:00')
ON DUPLICATE KEY UPDATE
  transaction_id = VALUES(transaction_id),
  cash_type = VALUES(cash_type),
  cash_direction = VALUES(cash_direction),
  teller_user_id = VALUES(teller_user_id),
  branch_id = VALUES(branch_id),
  amount = VALUES(amount),
  remarks = VALUES(remarks),
  created_at = VALUES(created_at);

INSERT INTO fund_transfer (
  id, transaction_id, from_account_id, to_account_id, transfer_mode, remarks, created_at
) VALUES
  (1, 1, 1, 2, 'INTERNAL', 'Deposit-linked internal memo', '2026-05-01 09:00:00'),
  (2, 2, 2, 1, 'INTERNAL', 'Withdrawal linked transfer reference', '2026-05-01 09:15:00'),
  (3, 3, 3, 4, 'INTERNAL', 'Core transfer sample', '2026-05-01 09:30:00'),
  (4, 4, 5, 4, 'BEFTN', 'Cheque settlement trace', '2026-05-01 09:45:00'),
  (5, 5, 6, 5, 'INTERNAL', 'Deposit cross note', '2026-05-01 10:00:00'),
  (6, 6, 7, 6, 'INTERNAL', 'Withdrawal trace', '2026-05-01 10:15:00'),
  (7, 7, 8, 9, 'RTGS', 'Transfer sample two', '2026-05-01 10:30:00'),
  (8, 8, 10, 5, 'BEFTN', 'Cheque settlement business current', '2026-05-01 10:45:00'),
  (9, 9, 11, 10, 'INTERNAL', 'Deposit note three', '2026-05-01 11:00:00'),
  (10, 10, 12, 11, 'INTERNAL', 'Withdrawal note three', '2026-05-01 11:15:00'),
  (11, 11, 13, 14, 'INTERNAL', 'NRB FC transfer', '2026-05-01 11:30:00'),
  (12, 12, 15, 13, 'BEFTN', 'Cheque review linkage', '2026-05-01 11:45:00'),
  (13, 13, 2, 1, 'INTERNAL', 'Reversal mirror entry', '2026-05-01 12:00:00'),
  (14, 14, 4, 3, 'INTERNAL', 'Transfer reversal mirror entry', '2026-05-01 12:10:00'),
  (15, 15, 5, 2, 'RTGS', 'High value deposit cross-check', '2026-05-01 12:25:00')
ON DUPLICATE KEY UPDATE
  transaction_id = VALUES(transaction_id),
  from_account_id = VALUES(from_account_id),
  to_account_id = VALUES(to_account_id),
  transfer_mode = VALUES(transfer_mode),
  remarks = VALUES(remarks),
  created_at = VALUES(created_at);

INSERT INTO cheque_clearing (
  id, transaction_id, credit_account_id, cheque_no, drawee_bank, amount, cheque_status, remarks, created_at
) VALUES
  (1, 1, 1, 'CHQ-0001', 'Dhaka Main Branch', 15000.00, 'CLEARED', 'Sample cheque detail for ref 1', '2026-05-01 09:00:00'),
  (2, 2, 2, 'CHQ-0002', 'Gulshan Corporate Branch', 2500.00, 'RETURNED', 'Returned cheque linked with reversed withdrawal', '2026-05-01 09:15:00'),
  (3, 3, 4, 'CHQ-0003', 'Dhanmondi Branch', 5000.00, 'CLEARED', 'Transfer-linked cheque trace', '2026-05-01 09:30:00'),
  (4, 4, 5, 'CHQ-0004', 'Uttara Branch', 8000.00, 'CLEARED', 'Actual cheque clearing sample', '2026-05-01 09:45:00'),
  (5, 5, 6, 'CHQ-0005', 'Mirpur Branch', 3200.00, 'RECEIVED', 'Deposit-linked received cheque', '2026-05-01 10:00:00'),
  (6, 6, 7, 'CHQ-0006', 'Agrabad Branch', 1400.00, 'RETURNED', 'Withdrawal linked cheque return', '2026-05-01 10:15:00'),
  (7, 7, 9, 'CHQ-0007', 'CEPZ Branch', 2200.00, 'CLEARED', 'Transfer sample cheque', '2026-05-01 10:30:00'),
  (8, 8, 10, 'CHQ-0008', 'Sylhet Branch', 18000.00, 'CLEARED', 'Business current cheque clearing', '2026-05-01 10:45:00'),
  (9, 9, 11, 'CHQ-0009', 'Rajshahi Branch', 4300.00, 'CLEARED', 'Deposit cheque support', '2026-05-01 11:00:00'),
  (10, 10, 12, 'CHQ-0010', 'Khulna Branch', 1600.00, 'RETURNED', 'Withdrawal control sample', '2026-05-01 11:15:00'),
  (11, 11, 14, 'CHQ-0011', 'Barishal Branch', 300.00, 'CLEARED', 'Transfer cheque note', '2026-05-01 11:30:00'),
  (12, 12, 15, 'CHQ-0012', 'Rangpur Branch', 1100.00, 'RECEIVED', 'Pending review cheque sample', '2026-05-01 11:45:00'),
  (13, 13, 2, 'CHQ-0013', 'Mymensingh Branch', 2500.00, 'CLEARED', 'Reversal linked cheque', '2026-05-01 12:00:00'),
  (14, 14, 3, 'CHQ-0014', 'Narayanganj Branch', 5000.00, 'CLEARED', 'Transfer reversal cheque trace', '2026-05-01 12:10:00'),
  (15, 15, 5, 'CHQ-0015', 'Gazipur Branch', 125000.00, 'RECEIVED', 'Suspicious large cheque deposit', '2026-05-01 12:25:00')
ON DUPLICATE KEY UPDATE
  transaction_id = VALUES(transaction_id),
  credit_account_id = VALUES(credit_account_id),
  cheque_no = VALUES(cheque_no),
  drawee_bank = VALUES(drawee_bank),
  amount = VALUES(amount),
  cheque_status = VALUES(cheque_status),
  remarks = VALUES(remarks),
  created_at = VALUES(created_at);

INSERT INTO transaction_reversal (
  id, original_transaction_id, reversal_transaction_id, requested_by, requested_at, approved_by, approved_at, reason, status, created_at
) VALUES
  (1, 1, NULL, 'SYSTEM_TELLER', '2026-05-01 12:30:00', NULL, NULL, 'Pending review on high cash deposit validation', 'PENDING', '2026-05-01 12:30:00'),
  (2, 2, 13, 'SYSTEM_TELLER', '2026-05-01 12:31:00', 'SYSTEM_SUPERVISOR', '2026-05-01 12:40:00', 'Approved reversal for wrong cash payout', 'APPROVED', '2026-05-01 12:31:00'),
  (3, 3, 14, 'SYSTEM_TELLER', '2026-05-01 12:32:00', 'SYSTEM_SUPERVISOR', '2026-05-01 12:41:00', 'Approved reversal after duplicate transfer detect', 'APPROVED', '2026-05-01 12:32:00'),
  (4, 4, NULL, 'SYSTEM_TELLER', '2026-05-01 12:33:00', NULL, NULL, 'Pending cheque dispute review', 'PENDING', '2026-05-01 12:33:00'),
  (5, 5, NULL, 'SYSTEM_TELLER', '2026-05-01 12:34:00', NULL, NULL, 'Pending teller narration correction', 'PENDING', '2026-05-01 12:34:00'),
  (6, 6, NULL, 'SYSTEM_TELLER', '2026-05-01 12:35:00', 'SYSTEM_SUPERVISOR', '2026-05-01 12:45:00', 'Approved operational correction', 'APPROVED', '2026-05-01 12:35:00'),
  (7, 7, NULL, 'SYSTEM_TELLER', '2026-05-01 12:36:00', NULL, NULL, 'Pending branch maker-checker', 'PENDING', '2026-05-01 12:36:00'),
  (8, 8, NULL, 'SYSTEM_TELLER', '2026-05-01 12:37:00', 'SYSTEM_SUPERVISOR', '2026-05-01 12:46:00', 'Approved after cheque clearing return', 'APPROVED', '2026-05-01 12:37:00'),
  (9, 9, NULL, 'SYSTEM_TELLER', '2026-05-01 12:38:00', 'SYSTEM_SUPERVISOR', '2026-05-01 12:47:00', 'Approved duplicate deposit cleanup', 'APPROVED', '2026-05-01 12:38:00'),
  (10, 10, NULL, 'SYSTEM_TELLER', '2026-05-01 12:39:00', 'SYSTEM_SUPERVISOR', '2026-05-01 12:48:00', 'Approved teller cash shortage adjustment', 'APPROVED', '2026-05-01 12:39:00'),
  (11, 11, NULL, 'SYSTEM_TELLER', '2026-05-01 12:40:00', NULL, NULL, 'Rejected because source branch mismatch not proven', 'REJECTED', '2026-05-01 12:40:00'),
  (12, 12, NULL, 'SYSTEM_TELLER', '2026-05-01 12:41:00', NULL, NULL, 'Rejected until cheque finality available', 'REJECTED', '2026-05-01 12:41:00'),
  (13, 13, NULL, 'SYSTEM_TELLER', '2026-05-01 12:42:00', NULL, NULL, 'Rejected, reversal voucher itself cannot reverse again', 'REJECTED', '2026-05-01 12:42:00'),
  (14, 14, NULL, 'SYSTEM_TELLER', '2026-05-01 12:43:00', NULL, NULL, 'Rejected, reversal voucher itself cannot reverse again', 'REJECTED', '2026-05-01 12:43:00'),
  (15, 15, NULL, 'SYSTEM_TELLER', '2026-05-01 12:44:00', NULL, NULL, 'Pending compliance review for high value cash deposit', 'PENDING', '2026-05-01 12:44:00')
ON DUPLICATE KEY UPDATE
  original_transaction_id = VALUES(original_transaction_id),
  reversal_transaction_id = VALUES(reversal_transaction_id),
  requested_by = VALUES(requested_by),
  requested_at = VALUES(requested_at),
  approved_by = VALUES(approved_by),
  approved_at = VALUES(approved_at),
  reason = VALUES(reason),
  status = VALUES(status),
  created_at = VALUES(created_at);

INSERT INTO standing_instruction (
  id, instruction_code, from_account_id, to_account_id, branch_id, amount, transfer_mode,
  schedule_date, frequency, next_execution_date, instruction_status, remarks, status, created_at
) VALUES
  (1, 'SI-0001', 1, 2, 1, 1000.00, 'INTERNAL', '2026-05-02', 'MONTHLY', '2026-05-02', 'ACTIVE', 'Monthly family transfer', 'ACTIVE', '2026-05-01 13:00:00'),
  (2, 'SI-0002', 2, 3, 2, 1500.00, 'INTERNAL', '2026-05-03', 'WEEKLY', '2026-05-03', 'ACTIVE', 'Weekly repayment transfer', 'ACTIVE', '2026-05-01 13:01:00'),
  (3, 'SI-0003', 3, 4, 1, 800.00, 'RTGS', '2026-05-04', 'MONTHLY', '2026-05-04', 'PAUSED', 'Paused corporate sweep', 'ACTIVE', '2026-05-01 13:02:00'),
  (4, 'SI-0004', 4, 5, 2, 2500.00, 'BEFTN', '2026-05-05', 'MONTHLY', '2026-05-05', 'ACTIVE', 'SME supplier settlement', 'ACTIVE', '2026-05-01 13:03:00'),
  (5, 'SI-0005', 5, 6, 2, 3000.00, 'INTERNAL', '2026-05-06', 'DAILY', '2026-05-06', 'ACTIVE', 'Corporate to savings provisioning', 'ACTIVE', '2026-05-01 13:04:00'),
  (6, 'SI-0006', 6, 7, 1, 700.00, 'INTERNAL', '2026-05-07', 'MONTHLY', '2026-05-07', 'EXECUTED', 'Executed hajj contribution setup', 'ACTIVE', '2026-05-01 13:05:00'),
  (7, 'SI-0007', 7, 8, 2, 650.00, 'INTERNAL', '2026-05-08', 'MONTHLY', '2026-05-08', 'ACTIVE', 'Umrah to student helper transfer', 'ACTIVE', '2026-05-01 13:06:00'),
  (8, 'SI-0008', 8, 9, 1, 400.00, 'INTERNAL', '2026-05-09', 'WEEKLY', '2026-05-09', 'ACTIVE', 'Weekly payroll savings top-up', 'ACTIVE', '2026-05-01 13:07:00'),
  (9, 'SI-0009', 9, 10, 3, 1200.00, 'BEFTN', '2026-05-10', 'MONTHLY', '2026-05-10', 'PAUSED', 'Paused payroll settlement', 'ACTIVE', '2026-05-01 13:08:00'),
  (10, 'SI-0010', 10, 11, 2, 5000.00, 'RTGS', '2026-05-11', 'MONTHLY', '2026-05-11', 'ACTIVE', 'Business current monthly support', 'ACTIVE', '2026-05-01 13:09:00'),
  (11, 'SI-0011', 11, 12, 1, 900.00, 'INTERNAL', '2026-05-12', 'MONTHLY', '2026-05-12', 'ACTIVE', 'Women to senior care transfer', 'ACTIVE', '2026-05-01 13:10:00'),
  (12, 'SI-0012', 12, 13, 3, 1100.00, 'BEFTN', '2026-05-13', 'MONTHLY', '2026-05-13', 'CANCELLED', 'Cancelled cross-currency support', 'ACTIVE', '2026-05-01 13:11:00'),
  (13, 'SI-0013', 13, 14, 2, 100.00, 'INTERNAL', '2026-05-14', 'MONTHLY', '2026-05-14', 'ACTIVE', 'NRB FC to deposit lite setup', 'ACTIVE', '2026-05-01 13:12:00'),
  (14, 'SI-0014', 14, 15, 1, 450.00, 'INTERNAL', '2026-05-15', 'MONTHLY', '2026-05-15', 'ACTIVE', 'Deposit lite to digital savings', 'ACTIVE', '2026-05-01 13:13:00'),
  (15, 'SI-0015', 15, 1, 3, 1300.00, 'RTGS', '2026-05-16', 'MONTHLY', '2026-05-16', 'ACTIVE', 'Digital savings high value standing setup', 'ACTIVE', '2026-05-01 13:14:00')
ON DUPLICATE KEY UPDATE
  instruction_code = VALUES(instruction_code),
  from_account_id = VALUES(from_account_id),
  to_account_id = VALUES(to_account_id),
  branch_id = VALUES(branch_id),
  amount = VALUES(amount),
  transfer_mode = VALUES(transfer_mode),
  schedule_date = VALUES(schedule_date),
  frequency = VALUES(frequency),
  next_execution_date = VALUES(next_execution_date),
  instruction_status = VALUES(instruction_status),
  remarks = VALUES(remarks),
  status = VALUES(status),
  created_at = VALUES(created_at);

COMMIT;
