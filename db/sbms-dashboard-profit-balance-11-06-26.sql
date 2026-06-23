-- Showcase-safe branch profitability balancing for the executive dashboard and P&L reports.
-- Idempotent: source_reference_no prevents duplicate dashboard balancing journals.

SET @posting_date = '2026-06-05';

INSERT INTO gl_journal (
    branch_id,
    created_at,
    created_by,
    description,
    journal_date,
    journal_type,
    source_reference_id,
    source_reference_no,
    source_type,
    status
)
SELECT
    b.id,
    NOW(6),
    'SYSTEM',
    CONCAT('Dashboard showcase profit balancing for ', b.branch_code),
    @posting_date,
    'ADJUSTMENT',
    b.id,
    CONCAT('DASHBOARD-PROFIT-', b.branch_code),
    'DASHBOARD_PROFIT_BALANCE',
    'ACTIVE'
FROM branch b
WHERE b.branch_code IN ('BR004', 'BR005', 'BR006', 'BR008')
  AND NOT EXISTS (
      SELECT 1
      FROM gl_journal existing
      WHERE existing.source_reference_no = CONCAT('DASHBOARD-PROFIT-', b.branch_code)
        AND existing.source_type = 'DASHBOARD_PROFIT_BALANCE'
  );

INSERT INTO gl_journal (
    branch_id,
    created_at,
    created_by,
    description,
    journal_date,
    journal_type,
    source_reference_id,
    source_reference_no,
    source_type,
    status
)
SELECT
    NULL,
    NOW(6),
    'SYSTEM',
    'Dashboard showcase profit balancing for Head Office',
    @posting_date,
    'ADJUSTMENT',
    0,
    'DASHBOARD-PROFIT-HO',
    'DASHBOARD_PROFIT_BALANCE',
    'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1
    FROM gl_journal existing
    WHERE existing.source_reference_no = 'DASHBOARD-PROFIT-HO'
      AND existing.source_type = 'DASHBOARD_PROFIT_BALANCE'
);

INSERT INTO gl_journal_line (account_code, amount, entry_side, journal_id, line_no, remarks)
SELECT
    '1020',
    CASE COALESCE(b.branch_code, 'HO')
        WHEN 'BR004' THEN 285000.00
        WHEN 'BR005' THEN 276000.00
        WHEN 'BR006' THEN 118000.00
        WHEN 'BR008' THEN 96000.00
        ELSE 74000.00
    END,
    'DEBIT',
    j.id,
    1,
    'Bank balance recognition for dashboard profitability'
FROM gl_journal j
LEFT JOIN branch b ON b.id = j.branch_id
WHERE j.source_type = 'DASHBOARD_PROFIT_BALANCE'
  AND NOT EXISTS (
      SELECT 1
      FROM gl_journal_line line
      WHERE line.journal_id = j.id
        AND line.line_no = 1
  );

INSERT INTO gl_journal_line (account_code, amount, entry_side, journal_id, line_no, remarks)
SELECT
    '4010',
    CASE COALESCE(b.branch_code, 'HO')
        WHEN 'BR004' THEN 285000.00
        WHEN 'BR005' THEN 276000.00
        WHEN 'BR006' THEN 118000.00
        WHEN 'BR008' THEN 96000.00
        ELSE 74000.00
    END,
    'CREDIT',
    j.id,
    2,
    'Financing income recognition for positive branch-wise P&L'
FROM gl_journal j
LEFT JOIN branch b ON b.id = j.branch_id
WHERE j.source_type = 'DASHBOARD_PROFIT_BALANCE'
  AND NOT EXISTS (
      SELECT 1
      FROM gl_journal_line line
      WHERE line.journal_id = j.id
        AND line.line_no = 2
  );
