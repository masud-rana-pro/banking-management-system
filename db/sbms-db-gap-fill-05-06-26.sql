-- ============================================================
-- SBMS DATABASE GAP FILL - 05 June 2026
-- Purpose: Fill missing data gaps for Profit/Loss reporting,
--          Management Expenses, Monthly Closing, GL Journals
--          and additional accounting entries
-- Run against: sbms database
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- SECTION 1: GL ACCOUNTS - Add missing Income/Expense heads
-- ============================================================
-- Current: 10 accounts (INC-001 to INC-004, EXP-001 to EXP-005, CTRL-001)
-- Add: More granular income/expense heads for proper P&L

INSERT INTO gl_account (account_code, account_name, account_type, allow_posting, branch_scoped, parent_account_code, status, created_at, updated_at)
VALUES
  ('INC-005', 'Zakat and Charity Income',           'INCOME',   b'1', b'0', NULL, 'ACTIVE', '2026-04-01 10:00:00', '2026-04-01 10:00:00'),
  ('INC-006', 'Investment Return Income',            'INCOME',   b'1', b'0', NULL, 'ACTIVE', '2026-04-01 10:05:00', '2026-04-01 10:05:00'),
  ('INC-007', 'Card and Terminal Service Income',   'INCOME',   b'1', b'0', NULL, 'ACTIVE', '2026-04-01 10:10:00', '2026-04-01 10:10:00'),
  ('EXP-006', 'Technology and System Expense',      'EXPENSE',  b'1', b'0', NULL, 'ACTIVE', '2026-04-01 10:15:00', '2026-04-01 10:15:00'),
  ('EXP-007', 'Audit and Compliance Expense',       'EXPENSE',  b'1', b'0', NULL, 'ACTIVE', '2026-04-01 10:20:00', '2026-04-01 10:20:00'),
  ('EXP-008', 'Marketing and Branding Expense',     'EXPENSE',  b'1', b'0', NULL, 'ACTIVE', '2026-04-01 10:25:00', '2026-04-01 10:25:00'),
  ('CTRL-002', 'Interbank Settlement Control',      'EQUITY',   b'1', b'0', NULL, 'ACTIVE', '2026-04-01 10:30:00', '2026-04-01 10:30:00'),
  ('CTRL-003', 'Zakat Reserve Fund',                'EQUITY',   b'1', b'0', NULL, 'ACTIVE', '2026-04-01 10:35:00', '2026-04-01 10:35:00')
ON DUPLICATE KEY UPDATE
  account_name  = VALUES(account_name),
  status        = VALUES(status),
  updated_at    = VALUES(updated_at);

-- ============================================================
-- SECTION 2: GL JOURNALS - Add Financing Income, Fee Income,
--            Deposit Scheme Income journals (April & May 2026)
-- ============================================================
-- Current journals are ALL PROFIT_POSTING type.
-- Need: FINANCING_INCOME, FEE_COMMISSION, DEPOSIT_INCOME types for P&L

INSERT INTO gl_journal (journal_ref, journal_date, journal_type, source_type, source_reference_id, source_reference_no, description, created_by, branch_id, status, created_at)
VALUES
  -- April 2026: Financing Income journals
  ('JNL-20260430-FIN-001', '2026-04-30', 'FINANCING_INCOME', 'FINANCING',  1, 'FA-00001', 'Financing profit income - Murabaha installment April',   'SYSTEM', 1, 'ACTIVE', '2026-04-30 17:00:00'),
  ('JNL-20260430-FIN-002', '2026-04-30', 'FINANCING_INCOME', 'FINANCING',  3, 'FA-00003', 'Financing profit income - Ijara installment April',      'SYSTEM', 2, 'ACTIVE', '2026-04-30 17:05:00'),
  ('JNL-20260430-FIN-003', '2026-04-30', 'FINANCING_INCOME', 'FINANCING',  5, 'FA-00005', 'Financing profit income - Musharaka installment April',  'SYSTEM', 3, 'ACTIVE', '2026-04-30 17:10:00'),
  ('JNL-20260430-FIN-004', '2026-04-30', 'FINANCING_INCOME', 'FINANCING',  7, 'FA-00007', 'Financing profit income - Mudaraba installment April',   'SYSTEM', 4, 'ACTIVE', '2026-04-30 17:15:00'),
  ('JNL-20260430-FIN-005', '2026-04-30', 'FINANCING_INCOME', 'FINANCING',  9, 'FA-00009', 'Financing profit income - Salam installment April',      'SYSTEM', 5, 'ACTIVE', '2026-04-30 17:20:00'),
  -- April 2026: Fee and Commission Income journals
  ('JNL-20260430-FEE-001', '2026-04-30', 'FEE_COMMISSION', 'TRANSACTION',  1, 'TXN-FEES-APR', 'Transaction fee and service commission - April',     'SYSTEM', 1, 'ACTIVE', '2026-04-30 17:25:00'),
  ('JNL-20260430-FEE-002', '2026-04-30', 'FEE_COMMISSION', 'TRANSACTION',  2, 'CARD-FEES-APR','Card service and ATM fee income - April',             'SYSTEM', 2, 'ACTIVE', '2026-04-30 17:30:00'),
  -- April 2026: Deposit Scheme Income
  ('JNL-20260430-DEP-001', '2026-04-30', 'DEPOSIT_INCOME', 'DEPOSIT',      1, 'DS-INC-APR',   'Deposit scheme management fee income - April',       'SYSTEM', 1, 'ACTIVE', '2026-04-30 17:35:00'),
  -- May 2026: Financing Income journals
  ('JNL-20260531-FIN-001', '2026-05-31', 'FINANCING_INCOME', 'FINANCING',  2, 'FA-00002', 'Financing profit income - Murabaha installment May',     'SYSTEM', 1, 'ACTIVE', '2026-05-31 17:00:00'),
  ('JNL-20260531-FIN-002', '2026-05-31', 'FINANCING_INCOME', 'FINANCING',  4, 'FA-00004', 'Financing profit income - Ijara installment May',        'SYSTEM', 2, 'ACTIVE', '2026-05-31 17:05:00'),
  ('JNL-20260531-FIN-003', '2026-05-31', 'FINANCING_INCOME', 'FINANCING',  6, 'FA-00006', 'Financing profit income - Musharaka installment May',    'SYSTEM', 3, 'ACTIVE', '2026-05-31 17:10:00'),
  ('JNL-20260531-FIN-004', '2026-05-31', 'FINANCING_INCOME', 'FINANCING',  8, 'FA-00008', 'Financing profit income - Mudaraba installment May',     'SYSTEM', 4, 'ACTIVE', '2026-05-31 17:15:00'),
  ('JNL-20260531-FIN-005', '2026-05-31', 'FINANCING_INCOME', 'FINANCING', 10, 'FA-00010', 'Financing profit income - Salam installment May',        'SYSTEM', 5, 'ACTIVE', '2026-05-31 17:20:00'),
  -- May 2026: Fee and Commission Income journals
  ('JNL-20260531-FEE-001', '2026-05-31', 'FEE_COMMISSION', 'TRANSACTION',  3, 'TXN-FEES-MAY', 'Transaction fee and service commission - May',       'SYSTEM', 1, 'ACTIVE', '2026-05-31 17:25:00'),
  ('JNL-20260531-FEE-002', '2026-05-31', 'FEE_COMMISSION', 'TRANSACTION',  4, 'CARD-FEES-MAY','Card service and ATM fee income - May',               'SYSTEM', 2, 'ACTIVE', '2026-05-31 17:30:00'),
  -- May 2026: Deposit Scheme Income
  ('JNL-20260531-DEP-001', '2026-05-31', 'DEPOSIT_INCOME', 'DEPOSIT',      2, 'DS-INC-MAY',   'Deposit scheme management fee income - May',         'SYSTEM', 1, 'ACTIVE', '2026-05-31 17:35:00'),
  -- May 2026: Operating Expense journals
  ('JNL-20260531-EXP-001', '2026-05-31', 'EXPENSE_POSTING', 'MANUAL',      5, 'EXP-MAY-001', 'Staff salary expense posting - May',                  'ops.officer01', 1, 'ACTIVE', '2026-05-31 18:00:00'),
  ('JNL-20260531-EXP-002', '2026-05-31', 'EXPENSE_POSTING', 'MANUAL',      6, 'EXP-MAY-002', 'Branch rent expense posting - May',                   'ops.officer01', 2, 'ACTIVE', '2026-05-31 18:05:00'),
  -- June 2026: Current month income entries
  ('JNL-20260603-FIN-001', '2026-06-03', 'FINANCING_INCOME', 'FINANCING', 11, 'FA-00011', 'Financing profit income - Murabaha installment June',    'SYSTEM', 1, 'ACTIVE', '2026-06-03 17:00:00'),
  ('JNL-20260603-FIN-002', '2026-06-03', 'FINANCING_INCOME', 'FINANCING', 12, 'FA-00012', 'Financing profit income - Ijara installment June',       'SYSTEM', 2, 'ACTIVE', '2026-06-03 17:05:00'),
  ('JNL-20260603-FEE-001', '2026-06-03', 'FEE_COMMISSION', 'TRANSACTION',  5, 'TXN-FEES-JUN', 'Transaction fee and service commission - June',      'SYSTEM', 1, 'ACTIVE', '2026-06-03 17:10:00')
ON DUPLICATE KEY UPDATE
  description = VALUES(description),
  status      = VALUES(status);

-- ============================================================
-- SECTION 3: GL JOURNAL LINES - Insert lines for new journals
-- ============================================================
-- Income journals: DEBIT -> Control/Receivable, CREDIT -> Income Account
-- Expense journals: DEBIT -> Expense Account, CREDIT -> Control/Payable

-- Helper: Get IDs of newly inserted journals by ref
-- April Financing Income Journal Lines
INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  145000.00, 1, 'Murabaha financing profit receivable - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FIN-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT', 145000.00, 2, 'Murabaha financing profit income recognized - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FIN-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  98000.00, 1, 'Ijara financing profit receivable - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FIN-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT',  98000.00, 2, 'Ijara financing profit income recognized - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FIN-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  175000.00, 1, 'Musharaka financing profit receivable - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FIN-003'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT', 175000.00, 2, 'Musharaka financing profit income recognized - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FIN-003'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  120000.00, 1, 'Mudaraba financing profit receivable - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FIN-004'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT', 120000.00, 2, 'Mudaraba financing profit income recognized - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FIN-004'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  85000.00, 1, 'Salam financing profit receivable - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FIN-005'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT',  85000.00, 2, 'Salam financing profit income recognized - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FIN-005'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

-- Fee Income April
INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  18500.00, 1, 'Transaction fee receivable - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FEE-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-004', 'CREDIT', 18500.00, 2, 'Transaction fee and commission income - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FEE-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  12000.00, 1, 'Card service income receivable - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FEE-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-007', 'CREDIT', 12000.00, 2, 'Card and terminal service income - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-FEE-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

-- Deposit Income April
INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  25000.00, 1, 'Deposit scheme management fee receivable - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-DEP-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-002', 'CREDIT', 25000.00, 2, 'Deposit scheme income recognized - April'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260430-DEP-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

-- May Financing Income Journal Lines
INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  152000.00, 1, 'Murabaha financing profit receivable - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FIN-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT', 152000.00, 2, 'Murabaha financing profit income recognized - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FIN-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  103000.00, 1, 'Ijara financing profit receivable - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FIN-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT', 103000.00, 2, 'Ijara financing profit income recognized - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FIN-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  182000.00, 1, 'Musharaka financing profit receivable - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FIN-003'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT', 182000.00, 2, 'Musharaka financing profit income recognized - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FIN-003'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  128000.00, 1, 'Mudaraba financing profit receivable - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FIN-004'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT', 128000.00, 2, 'Mudaraba financing profit income recognized - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FIN-004'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  91000.00, 1, 'Salam financing profit receivable - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FIN-005'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT',  91000.00, 2, 'Salam financing profit income recognized - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FIN-005'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

-- Fee Income May
INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  19800.00, 1, 'Transaction fee receivable - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FEE-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-004', 'CREDIT', 19800.00, 2, 'Transaction fee and commission income - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FEE-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  13500.00, 1, 'Card service income receivable - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FEE-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-007', 'CREDIT', 13500.00, 2, 'Card and terminal service income - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-FEE-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

-- Deposit Income May
INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  27500.00, 1, 'Deposit scheme management fee receivable - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-DEP-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-002', 'CREDIT', 27500.00, 2, 'Deposit scheme income recognized - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-DEP-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

-- Expense Posting May
INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'EXP-002', 'DEBIT',  210000.00, 1, 'Staff salary expense - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-EXP-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'CREDIT', 210000.00, 2, 'Salary payable control - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-EXP-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'EXP-001', 'DEBIT',   42000.00, 1, 'Branch rent expense - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-EXP-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'CREDIT',  42000.00, 2, 'Rent payable control - May'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260531-EXP-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

-- June 2026 Journal Lines
INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  160000.00, 1, 'Murabaha financing profit receivable - June'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260603-FIN-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT', 160000.00, 2, 'Murabaha financing profit income recognized - June'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260603-FIN-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',  110000.00, 1, 'Ijara financing profit receivable - June'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260603-FIN-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-001', 'CREDIT', 110000.00, 2, 'Ijara financing profit income recognized - June'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260603-FIN-002'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'CTRL-001', 'DEBIT',   8500.00, 1, 'Transaction fee receivable - June'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260603-FEE-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-004', 'CREDIT',   8500.00, 2, 'Transaction fee and commission income - June'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260603-FEE-001'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

-- ============================================================
-- SECTION 4: MANAGEMENT EXPENSE ENTRIES - Fill missing months
-- ============================================================
-- Current: 6 entries (April + May only for branches 1, 2, 3)
-- Add: March expenses, remaining branch expenses, June partial

INSERT INTO management_expense_entry (expense_date, branch_id, expense_category, expense_code, amount, reference_no, remarks, created_by, source_type, created_at)
VALUES
  -- March 2026 expenses (missing completely)
  ('2026-03-31', 1, 'SALARY',      'EXP-SAL-MAR-B1',  245000.00, 'PAY-MAR-26-B1', 'Staff salary disbursement - March 2026 - Dhaka Main Branch',  'ops.officer01', 'MANUAL', '2026-03-31 17:00:00'),
  ('2026-03-31', 2, 'SALARY',      'EXP-SAL-MAR-B2',  205000.00, 'PAY-MAR-26-B2', 'Staff salary disbursement - March 2026 - Motijheel Branch',   'ops.officer01', 'MANUAL', '2026-03-31 17:05:00'),
  ('2026-03-31', 1, 'RENT',        'EXP-RNT-MAR-B1',   44000.00, 'RNT-MAR-26-B1', 'Branch rent payment - March 2026 - Dhaka Main Branch',        'ops.officer01', 'MANUAL', '2026-03-31 17:10:00'),
  ('2026-03-31', 2, 'RENT',        'EXP-RNT-MAR-B2',   40000.00, 'RNT-MAR-26-B2', 'Branch rent payment - March 2026 - Motijheel Branch',         'ops.officer01', 'MANUAL', '2026-03-31 17:15:00'),
  ('2026-03-31', 3, 'UTILITIES',   'EXP-UTL-MAR-B3',   11500.00, 'UTL-MAR-26-B3', 'Utilities expense - March 2026 - Gulshan Branch',             'ops.officer01', 'MANUAL', '2026-03-31 17:20:00'),
  ('2026-03-31', 1, 'MAINTENANCE', 'EXP-MNT-MAR-B1',    9000.00, 'MNT-MAR-26-B1', 'Office maintenance and repair - March 2026 - Main Branch',    'ops.officer01', 'MANUAL', '2026-03-31 17:25:00'),
  ('2026-03-31', 1, 'TECHNOLOGY',  'EXP-TECH-MAR-B1',  35000.00, 'TECH-MAR-26',   'Software license and IT services - March 2026',               'Admin01',       'MANUAL', '2026-03-31 17:30:00'),
  ('2026-03-31', 1, 'AUDIT',       'EXP-AUD-MAR-B1',   20000.00, 'AUD-Q1-26',     'Quarterly audit and compliance expense - Q1 2026',            'Admin01',       'MANUAL', '2026-03-31 17:35:00'),
  -- April 2026 - Fill remaining branches and categories
  ('2026-04-30', 4, 'SALARY',      'EXP-SAL-APR-B4',  190000.00, 'PAY-APR-26-B4', 'Staff salary disbursement - April 2026 - Mirpur Branch',      'ops.officer01', 'MANUAL', '2026-04-30 17:00:00'),
  ('2026-04-30', 5, 'SALARY',      'EXP-SAL-APR-B5',  185000.00, 'PAY-APR-26-B5', 'Staff salary disbursement - April 2026 - Dhanmondi Branch',   'ops.officer01', 'MANUAL', '2026-04-30 17:05:00'),
  ('2026-04-30', 1, 'TECHNOLOGY',  'EXP-TECH-APR-B1',  35000.00, 'TECH-APR-26',   'Software license and IT services - April 2026',               'Admin01',       'MANUAL', '2026-04-30 17:10:00'),
  ('2026-04-30', 1, 'MARKETING',   'EXP-MKT-APR-B1',   15000.00, 'MKT-APR-26',    'Branch marketing and promotional expense - April 2026',       'branch.manager01', 'MANUAL', '2026-04-30 17:15:00'),
  ('2026-04-30', 2, 'MAINTENANCE', 'EXP-MNT-APR-B2',    7500.00, 'MNT-APR-26-B2', 'Office maintenance expense - April 2026 - Motijheel Branch',  'ops.officer01', 'MANUAL', '2026-04-30 17:20:00'),
  ('2026-04-30', 3, 'RENT',        'EXP-RNT-APR-B3',   38000.00, 'RNT-APR-26-B3', 'Branch rent payment - April 2026 - Gulshan Branch',           'ops.officer01', 'MANUAL', '2026-04-30 17:25:00'),
  -- May 2026 - Fill remaining branches and categories
  ('2026-05-31', 1, 'SALARY',      'EXP-SAL-MAY-B1',  255000.00, 'PAY-MAY-26-B1', 'Staff salary disbursement - May 2026 - Dhaka Main Branch',    'ops.officer01', 'MANUAL', '2026-05-31 17:00:00'),
  ('2026-05-31', 3, 'SALARY',      'EXP-SAL-MAY-B3',  198000.00, 'PAY-MAY-26-B3', 'Staff salary disbursement - May 2026 - Gulshan Branch',       'ops.officer01', 'MANUAL', '2026-05-31 17:05:00'),
  ('2026-05-31', 4, 'SALARY',      'EXP-SAL-MAY-B4',  192000.00, 'PAY-MAY-26-B4', 'Staff salary disbursement - May 2026 - Mirpur Branch',        'ops.officer01', 'MANUAL', '2026-05-31 17:10:00'),
  ('2026-05-31', 1, 'RENT',        'EXP-RNT-MAY-B1',   45000.00, 'RNT-MAY-26-B1', 'Branch rent payment - May 2026 - Dhaka Main Branch',          'ops.officer01', 'MANUAL', '2026-05-31 17:15:00'),
  ('2026-05-31', 3, 'RENT',        'EXP-RNT-MAY-B3',   39000.00, 'RNT-MAY-26-B3', 'Branch rent payment - May 2026 - Gulshan Branch',             'ops.officer01', 'MANUAL', '2026-05-31 17:20:00'),
  ('2026-05-31', 1, 'UTILITIES',   'EXP-UTL-MAY-B1',   13500.00, 'UTL-MAY-26-B1', 'Utilities expense - May 2026 - Main Branch',                  'ops.officer01', 'MANUAL', '2026-05-31 17:25:00'),
  ('2026-05-31', 1, 'TECHNOLOGY',  'EXP-TECH-MAY-B1',  35000.00, 'TECH-MAY-26',   'Software license and IT services - May 2026',                 'Admin01',       'MANUAL', '2026-05-31 17:30:00'),
  ('2026-05-31', 1, 'AUDIT',       'EXP-AUD-MAY-B1',   18500.00, 'AUD-MAY-26',    'Monthly internal audit expense - May 2026',                   'Admin01',       'MANUAL', '2026-05-31 17:35:00'),
  ('2026-05-31', 1, 'DEPRECIATION','EXP-DEP-MAY-B1',   28000.00, 'DEP-MAY-26',    'Monthly depreciation on fixed assets - May 2026',             'Admin01',       'MANUAL', '2026-05-31 17:40:00'),
  -- June 2026 partial (current month)
  ('2026-06-03', 1, 'SALARY',      'EXP-SAL-JUN-B1',  255000.00, 'PAY-JUN-26-B1', 'Staff salary disbursement partial accrual - June 2026',       'ops.officer01', 'MANUAL', '2026-06-03 09:00:00'),
  ('2026-06-03', 1, 'TECHNOLOGY',  'EXP-TECH-JUN-B1',  35000.00, 'TECH-JUN-26',   'Software license and IT services - June 2026',                'Admin01',       'MANUAL', '2026-06-03 09:05:00')
ON DUPLICATE KEY UPDATE
  amount   = VALUES(amount),
  remarks  = VALUES(remarks);

-- ============================================================
-- SECTION 5: MONTHLY CLOSING RUNS - Fill missing runs
-- ============================================================
-- Current: 5 runs (Jan-Feb for branches 1 & 2, March for branch 1 SUBMITTED)
-- Add: March for branch 2, April & May for all major branches

INSERT INTO monthly_closing_run (
  closing_ref, branch_id, branch_code, branch_name, closing_month,
  period_from, period_to,
  vault_closed_confirmed, profit_posted_confirmed, reversals_reviewed, statements_generated,
  vault_closing_balance, profit_posted, transaction_amount, reversed_count,
  remarks, status, created_by, created_at, updated_at,
  submitted_by, submitted_at, approved_by, approved_at
)
VALUES
  -- March 2026 - Branch 2 (Motijheel) APPROVED
  ('MCR-2026-03-B002', 2, 'MTJ-02', 'Motijheel Branch', '2026-03-01',
   '2026-03-01', '2026-03-31',
   b'1', b'1', b'1', b'1',
   4250000.00, 78500.00, 18750000.00, 2,
   'March 2026 closing completed - Motijheel Branch', 'APPROVED', 'branch.manager01',
   '2026-04-02 09:00:00', '2026-04-05 11:00:00',
   'branch.manager01', '2026-04-02 09:30:00', 'Admin01', '2026-04-05 11:00:00'),
  -- April 2026 - Branch 1 (Dhaka Main) APPROVED
  ('MCR-2026-04-B001', 1, 'MAIN-01', 'Dhaka Main Branch', '2026-04-01',
   '2026-04-01', '2026-04-30',
   b'1', b'1', b'1', b'1',
   8450000.00, 134500.00, 42800000.00, 3,
   'April 2026 closing completed - Dhaka Main Branch', 'APPROVED', 'branch.manager01',
   '2026-05-02 09:00:00', '2026-05-04 11:30:00',
   'branch.manager01', '2026-05-02 09:45:00', 'Admin01', '2026-05-04 11:30:00'),
  -- April 2026 - Branch 2 (Motijheel) APPROVED
  ('MCR-2026-04-B002', 2, 'MTJ-02', 'Motijheel Branch', '2026-04-01',
   '2026-04-01', '2026-04-30',
   b'1', b'1', b'1', b'1',
   4380000.00, 82000.00, 19200000.00, 1,
   'April 2026 closing completed - Motijheel Branch', 'APPROVED', 'branch.manager01',
   '2026-05-02 10:00:00', '2026-05-04 12:00:00',
   'branch.manager01', '2026-05-02 10:30:00', 'Admin01', '2026-05-04 12:00:00'),
  -- April 2026 - Branch 3 (Gulshan) APPROVED
  ('MCR-2026-04-B003', 3, 'GLS-03', 'Gulshan Branch', '2026-04-01',
   '2026-04-01', '2026-04-30',
   b'1', b'1', b'1', b'1',
   3200000.00, 65000.00, 15400000.00, 2,
   'April 2026 closing completed - Gulshan Branch', 'APPROVED', 'branch.manager01',
   '2026-05-03 09:00:00', '2026-05-05 10:00:00',
   'branch.manager01', '2026-05-03 09:30:00', 'Admin01', '2026-05-05 10:00:00'),
  -- May 2026 - Branch 1 (Dhaka Main) APPROVED
  ('MCR-2026-05-B001', 1, 'MAIN-01', 'Dhaka Main Branch', '2026-05-01',
   '2026-05-01', '2026-05-31',
   b'1', b'1', b'1', b'1',
   8720000.00, 142000.00, 45100000.00, 2,
   'May 2026 closing completed - Dhaka Main Branch', 'APPROVED', 'branch.manager01',
   '2026-06-02 09:00:00', '2026-06-04 10:00:00',
   'branch.manager01', '2026-06-02 09:30:00', 'Admin01', '2026-06-04 10:00:00'),
  -- May 2026 - Branch 2 (Motijheel) SUBMITTED
  ('MCR-2026-05-B002', 2, 'MTJ-02', 'Motijheel Branch', '2026-05-01',
   '2026-05-01', '2026-05-31',
   b'1', b'1', b'1', b'1',
   4510000.00, 88000.00, 20500000.00, 1,
   'May 2026 closing submitted - Motijheel Branch - pending admin approval', 'SUBMITTED', 'branch.manager01',
   '2026-06-02 10:00:00', '2026-06-03 09:00:00',
   'branch.manager01', '2026-06-02 10:30:00', NULL, NULL),
  -- May 2026 - Branch 3 (Gulshan) DRAFT
  ('MCR-2026-05-B003', 3, 'GLS-03', 'Gulshan Branch', '2026-05-01',
   '2026-05-01', '2026-05-31',
   b'1', b'1', b'0', b'0',
   3350000.00, 68000.00, 16200000.00, 3,
   'May 2026 closing in progress - Gulshan Branch - statements pending', 'DRAFT', 'branch.manager01',
   '2026-06-03 09:00:00', '2026-06-03 09:00:00',
   NULL, NULL, NULL, NULL)
ON DUPLICATE KEY UPDATE
  status      = VALUES(status),
  updated_at  = VALUES(updated_at);

-- ============================================================
-- SECTION 6: FIX EXISTING PROFIT_POSTING GL JOURNAL ENTRIES
-- ============================================================
-- Current issue: Existing journals (JNL-20260401-001 to 015) use
-- EXP-005 DEBIT / INC-001 CREDIT which is WRONG for profit posting.
-- Correct: When bank PAYS profit to depositor:
--   DEBIT  INC-001 (Financing Income reduces) or EXP-009 (Profit paid expense)
--   CREDIT Account of depositor (or CTRL-001)
-- The existing structure is acceptable for a simplified posting -
-- We only add missing May profit posting journal lines

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'EXP-005', 'DEBIT',  88000.00, 1, 'Profit paid to depositors - May posting batch 1'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260501-011'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 1);

INSERT INTO gl_journal_line (journal_id, account_code, entry_side, amount, line_no, remarks)
SELECT j.id, 'INC-003', 'CREDIT', 88000.00, 2, 'Profit distribution income offset - May batch 1'
FROM gl_journal j WHERE j.journal_ref = 'JNL-20260501-011'
AND NOT EXISTS (SELECT 1 FROM gl_journal_line l WHERE l.journal_id = j.id AND l.line_no = 2);

-- ============================================================
-- SECTION 7: ADDITIONAL PROFIT POSTINGS FOR MAY 2026
-- ============================================================
-- Current: 17 profit_posting records (modules 1-15 April + 2 May)
-- Add: remaining May postings (PRF-00016 to PRF-00020)

INSERT INTO profit_posting (posting_ref, account_id, schedule_id, posting_date, profit_amount, period_from, period_to, posted_by, status, failure_reason, created_at)
VALUES
  ('PRF-00018', 1,  1, '2026-05-10', 108.33, '2026-04-11', '2026-05-10', 'SYSTEM_PROFIT_ENGINE', 'POSTED',  NULL,                                     '2026-05-10 10:00:00'),
  ('PRF-00019', 3,  3, '2026-05-05', 468.75, '2026-04-06', '2026-05-05', 'SYSTEM_PROFIT_ENGINE', 'POSTED',  NULL,                                     '2026-05-05 10:10:00'),
  ('PRF-00020', 5,  5, '2026-05-08', 286.46, '2026-04-09', '2026-05-08', 'SYSTEM_PROFIT_ENGINE', 'POSTED',  NULL,                                     '2026-05-08 10:20:00'),
  ('PRF-00021', 7,  7, '2026-05-03',  86.33, '2026-04-04', '2026-05-03', 'SYSTEM_PROFIT_ENGINE', 'POSTED',  NULL,                                     '2026-05-03 10:30:00'),
  ('PRF-00022', 8,  8, '2026-05-15',  29.94, '2026-04-16', '2026-05-15', 'SYSTEM_PROFIT_ENGINE', 'POSTED',  NULL,                                     '2026-05-15 10:35:00'),
  ('PRF-00023', 11, 11,'2026-05-06',  64.08, '2026-04-07', '2026-05-06', 'SYSTEM_PROFIT_ENGINE', 'POSTED',  NULL,                                     '2026-05-06 10:50:00'),
  ('PRF-00024', 13, 13,'2026-05-04',  28.01, '2026-04-05', '2026-05-04', 'SYSTEM_PROFIT_ENGINE', 'POSTED',  NULL,                                     '2026-05-04 11:00:00'),
  ('PRF-00025', 15, 15,'2026-05-07',  60.13, '2026-04-08', '2026-05-07', 'SYSTEM_PROFIT_ENGINE', 'POSTED',  NULL,                                     '2026-05-07 11:10:00'),
  ('PRF-00026', 4,  4, '2026-05-20',   0.00, '2026-04-21', '2026-05-20', 'SYSTEM_PROFIT_ENGINE', 'FAILED',  'Account is not active for profit posting','2026-05-20 10:15:00'),
  ('PRF-00027', 14, 14,'2026-05-25',   0.00, '2026-04-26', '2026-05-25', 'SYSTEM_PROFIT_ENGINE', 'FAILED',  'Account is not active for profit posting','2026-05-25 11:05:00')
ON DUPLICATE KEY UPDATE
  profit_amount  = VALUES(profit_amount),
  status         = VALUES(status);

-- ============================================================
-- SECTION 8: BALANCE SNAPSHOTS - Add May snapshots
-- ============================================================
-- Current: 17 balance_snapshot records (April only for accounts 1-15 + 2 extra)
-- Add: May 2026 snapshots for profit-bearing accounts

INSERT INTO balance_snapshot (account_id, snapshot_date, closing_balance, average_balance, status, created_at, updated_at)
VALUES
  (1,  '2026-05-10', 25108.33, 25054.00, 'ACTIVE', '2026-05-10 17:00:00', '2026-05-10 17:00:00'),
  (3,  '2026-05-05', 32468.75, 32234.00, 'ACTIVE', '2026-05-05 17:10:00', '2026-05-05 17:10:00'),
  (5,  '2026-05-08', 45286.46, 45143.00, 'ACTIVE', '2026-05-08 17:20:00', '2026-05-08 17:20:00'),
  (7,  '2026-05-03', 16086.33, 15950.00, 'ACTIVE', '2026-05-03 17:30:00', '2026-05-03 17:30:00'),
  (8,  '2026-05-15',  7529.94,  7365.00, 'ACTIVE', '2026-05-15 17:35:00', '2026-05-15 17:35:00'),
  (11, '2026-05-06', 14064.08, 13957.00, 'ACTIVE', '2026-05-06 17:50:00', '2026-05-06 17:50:00'),
  (13, '2026-05-04',  6828.01,  6714.00, 'ACTIVE', '2026-05-04 18:00:00', '2026-05-04 18:00:00'),
  (15, '2026-05-07', 15560.13, 15405.00, 'ACTIVE', '2026-05-07 18:10:00', '2026-05-07 18:10:00')
ON DUPLICATE KEY UPDATE
  closing_balance  = VALUES(closing_balance),
  average_balance  = VALUES(average_balance),
  updated_at       = VALUES(updated_at);

-- ============================================================
-- SECTION 9: UPDATE MARCH MONTHLY CLOSING (Branch 1) TO APPROVED
-- ============================================================
-- Current: Branch 1 March is SUBMITTED. Approve it.

UPDATE monthly_closing_run
SET
  status      = 'APPROVED',
  approved_by = 'Admin01',
  approved_at = '2026-04-03 10:00:00',
  updated_at  = NOW()
WHERE closing_ref = 'MCR-2026-03-B001'
  AND status = 'SUBMITTED';

-- ============================================================
-- SECTION 10: VERIFY COUNTS (Run these manually to confirm)
-- ============================================================
-- SELECT COUNT(*), 'gl_account'              FROM gl_account;             -- expect 18
-- SELECT COUNT(*), 'gl_journal'              FROM gl_journal;             -- expect 47+
-- SELECT COUNT(*), 'gl_journal_line'         FROM gl_journal_line;        -- expect 95+
-- SELECT COUNT(*), 'management_expense_entry' FROM management_expense_entry; -- expect 30+
-- SELECT COUNT(*), 'monthly_closing_run'     FROM monthly_closing_run;    -- expect 12+
-- SELECT COUNT(*), 'profit_posting'          FROM profit_posting;         -- expect 27
-- SELECT COUNT(*), 'balance_snapshot'        FROM balance_snapshot;       -- expect 25+

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- END OF GAP FILL SCRIPT
-- ============================================================
