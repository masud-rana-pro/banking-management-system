SET NAMES utf8mb4;

START TRANSACTION;

INSERT INTO teller_limit (
  branch_id,
  user_id,
  limit_date,
  daily_deposit_limit,
  daily_withdraw_limit,
  single_txn_limit,
  approved_by,
  approved_at,
  status,
  created_at
)
SELECT 12, 11, '2026-05-08', 600000.00, 450000.00, 150000.00, 3, NOW(), 'ACTIVE', NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM teller_limit
  WHERE branch_id = 12 AND user_id = 11 AND limit_date = '2026-05-08'
);

INSERT INTO teller_limit (
  branch_id,
  user_id,
  limit_date,
  daily_deposit_limit,
  daily_withdraw_limit,
  single_txn_limit,
  approved_by,
  approved_at,
  status,
  created_at
)
SELECT 13, 12, '2026-05-08', 550000.00, 400000.00, 140000.00, 3, NOW(), 'ACTIVE', NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM teller_limit
  WHERE branch_id = 13 AND user_id = 12 AND limit_date = '2026-05-08'
);

INSERT INTO teller_limit (
  branch_id,
  user_id,
  limit_date,
  daily_deposit_limit,
  daily_withdraw_limit,
  single_txn_limit,
  approved_by,
  approved_at,
  status,
  created_at
)
SELECT 14, 13, '2026-05-08', 720000.00, 500000.00, 175000.00, 3, NOW(), 'ACTIVE', NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM teller_limit
  WHERE branch_id = 14 AND user_id = 13 AND limit_date = '2026-05-08'
);

INSERT INTO teller_limit (
  branch_id,
  user_id,
  limit_date,
  daily_deposit_limit,
  daily_withdraw_limit,
  single_txn_limit,
  approved_by,
  approved_at,
  status,
  created_at
)
SELECT 15, 16, '2026-05-08', 800000.00, 650000.00, 200000.00, 3, NOW(), 'ACTIVE', NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM teller_limit
  WHERE branch_id = 15 AND user_id = 16 AND limit_date = '2026-05-08'
);

INSERT INTO vault_balance (
  branch_id,
  balance_date,
  opening_balance,
  total_cash_in,
  total_cash_out,
  closing_balance,
  is_closed,
  closed_by,
  closed_at,
  remarks,
  status,
  created_at
)
SELECT 12, '2026-05-08', 3500000.00, 650000.00, 420000.00, 3730000.00, TRUE, 12, NOW(),
       'Closed after standard EOD balancing', 'ACTIVE', NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM vault_balance
  WHERE branch_id = 12 AND balance_date = '2026-05-08'
);

INSERT INTO vault_balance (
  branch_id,
  balance_date,
  opening_balance,
  total_cash_in,
  total_cash_out,
  closing_balance,
  is_closed,
  closed_by,
  closed_at,
  remarks,
  status,
  created_at
)
SELECT 13, '2026-05-08', 2850000.00, 480000.00, 250000.00, 3080000.00, TRUE, 13, NOW(),
       'Corporate branch vault closed after cash receipt consolidation', 'ACTIVE', NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM vault_balance
  WHERE branch_id = 13 AND balance_date = '2026-05-08'
);

INSERT INTO vault_balance (
  branch_id,
  balance_date,
  opening_balance,
  total_cash_in,
  total_cash_out,
  closing_balance,
  is_closed,
  closed_by,
  closed_at,
  remarks,
  status,
  created_at
)
SELECT 14, '2026-05-08', 2600000.00, 395000.00, 310000.00, 2685000.00, FALSE, NULL, NULL,
       'Vault remains open pending branch manager sign-off', 'ACTIVE', NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM vault_balance
  WHERE branch_id = 14 AND balance_date = '2026-05-08'
);

INSERT INTO vault_balance (
  branch_id,
  balance_date,
  opening_balance,
  total_cash_in,
  total_cash_out,
  closing_balance,
  is_closed,
  closed_by,
  closed_at,
  remarks,
  status,
  created_at
)
SELECT 15, '2026-05-08', 4150000.00, 720000.00, 580000.00, 4290000.00, TRUE, 15, NOW(),
       'Branch closed vault after same-day remittance reconciliation', 'ACTIVE', NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM vault_balance
  WHERE branch_id = 15 AND balance_date = '2026-05-08'
);

INSERT INTO branch_cash_ledger (
  branch_id,
  ledger_date,
  entry_type,
  source_type,
  reference_no,
  debit_amount,
  credit_amount,
  balance_after,
  remarks,
  created_at,
  created_by
)
SELECT 1, '2026-05-08', 'CREDIT', 'VAULT_OPENING', 'M4-LEDGER-001', 0.00, 250000.00, 250000.00,
       'Opening vault cash loaded for Dhaka Main Branch', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-001');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 2, '2026-05-08', 'DEBIT', 'CASH_WITHDRAW', 'M4-LEDGER-002', 95000.00, 0.00, 905000.00,
       'Bulk withdrawal settled at cash counter', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-002');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 3, '2026-05-08', 'CREDIT', 'CASH_DEPOSIT', 'M4-LEDGER-003', 0.00, 180000.00, 1180000.00,
       'High-value deposit posted before noon', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-003');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 4, '2026-05-08', 'DEBIT', 'INTER_BRANCH_TRANSFER', 'M4-LEDGER-004', 140000.00, 0.00, 760000.00,
       'Inter-branch cash dispatch memo prepared for Uttara Branch', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-004');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 5, '2026-05-08', 'CREDIT', 'INTER_BRANCH_RECEIPT', 'M4-LEDGER-005', 0.00, 145000.00, 1245000.00,
       'Mirpur Branch received approved transfer from Dhaka Main', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-005');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 6, '2026-05-08', 'DEBIT', 'TELLER_SETTLEMENT', 'M4-LEDGER-006', 65000.00, 0.00, 670000.00,
       'Teller settlement remitted to vault before close', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-006');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 7, '2026-05-08', 'CREDIT', 'ATM_SURPLUS', 'M4-LEDGER-007', 0.00, 88000.00, 788000.00,
       'Recovered ATM surplus brought into branch vault', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-007');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 8, '2026-05-08', 'DEBIT', 'CASH_WITHDRAW', 'M4-LEDGER-008', 73000.00, 0.00, 727000.00,
       'Large retail withdrawal served at branch cash desk', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-008');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 9, '2026-05-08', 'CREDIT', 'CASH_DEPOSIT', 'M4-LEDGER-009', 0.00, 99000.00, 899000.00,
       'Rajshahi Branch corporate deposit added to ledger', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-009');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 10, '2026-05-08', 'DEBIT', 'PETTY_CASH', 'M4-LEDGER-010', 12000.00, 0.00, 438000.00,
       'Petty cash issued for branch service operations', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-010');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 11, '2026-05-08', 'CREDIT', 'CASH_DEPOSIT', 'M4-LEDGER-011', 0.00, 135000.00, 1035000.00,
       'Barishal Branch same-day merchant deposit posted', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-011');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 12, '2026-05-08', 'DEBIT', 'VAULT_CLOSE', 'M4-LEDGER-012', 45000.00, 0.00, 3685000.00,
       'Vault adjustment after day-end reconciliation', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-012');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 13, '2026-05-08', 'CREDIT', 'INTER_BRANCH_RECEIPT', 'M4-LEDGER-013', 0.00, 125000.00, 3205000.00,
       'Received cash from Narayanganj for corporate salary load', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-013');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 14, '2026-05-08', 'DEBIT', 'INTER_BRANCH_TRANSFER', 'M4-LEDGER-014', 110000.00, 0.00, 2575000.00,
       'Narayanganj cash dispatch awaiting approval trail archive', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-014');

INSERT INTO branch_cash_ledger (branch_id, ledger_date, entry_type, source_type, reference_no, debit_amount, credit_amount, balance_after, remarks, created_at, created_by)
SELECT 15, '2026-05-08', 'CREDIT', 'VAULT_CLOSE', 'M4-LEDGER-015', 0.00, 160000.00, 4450000.00,
       'Gazipur Branch final vault balancing surplus posted', NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM branch_cash_ledger WHERE reference_no = 'M4-LEDGER-015');

COMMIT;
