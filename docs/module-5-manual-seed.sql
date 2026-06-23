SET NAMES utf8mb4;

START TRANSACTION;

INSERT INTO terminal (
  branch_id,
  created_at,
  install_date,
  ip_address,
  location_note,
  serial_no,
  status,
  terminal_code,
  terminal_name,
  terminal_type,
  updated_at,
  vendor_name
)
SELECT
  2,
  NOW(),
  '2026-03-18',
  '10.50.12.12',
  'North Atrium Booth',
  'SN-M5-ATM-012',
  'ACTIVE',
  'ATMM5-012',
  'North Atrium ATM',
  'ATM',
  NOW(),
  'NCR'
WHERE NOT EXISTS (
  SELECT 1 FROM terminal WHERE terminal_code = 'ATMM5-012'
);

INSERT INTO terminal (
  branch_id,
  created_at,
  install_date,
  ip_address,
  location_note,
  serial_no,
  status,
  terminal_code,
  terminal_name,
  terminal_type,
  updated_at,
  vendor_name
)
SELECT
  4,
  NOW(),
  '2026-03-26',
  '10.50.13.13',
  'Corporate Annex Zone',
  'SN-M5-CDM-013',
  'INACTIVE',
  'CDMM5-013',
  'Corporate Annex CDM',
  'CDM',
  NOW(),
  'GRG'
WHERE NOT EXISTS (
  SELECT 1 FROM terminal WHERE terminal_code = 'CDMM5-013'
);

INSERT INTO terminal_cash_bin (
  bin_no,
  created_at,
  current_count,
  denomination,
  max_capacity,
  status,
  terminal_id
)
SELECT
  'M5-A1',
  NOW(),
  180,
  500.00,
  2000,
  'LOW_CASH',
  t.id
FROM terminal t
WHERE t.terminal_code = 'ATMM5-012'
  AND NOT EXISTS (
    SELECT 1
    FROM terminal_cash_bin cb
    WHERE cb.terminal_id = t.id
      AND cb.bin_no = 'M5-A1'
  );

INSERT INTO terminal_cash_bin (
  bin_no,
  created_at,
  current_count,
  denomination,
  max_capacity,
  status,
  terminal_id
)
SELECT
  'M5-A2',
  NOW(),
  1100,
  1000.00,
  2200,
  'ACTIVE',
  t.id
FROM terminal t
WHERE t.terminal_code = 'ATMM5-012'
  AND NOT EXISTS (
    SELECT 1
    FROM terminal_cash_bin cb
    WHERE cb.terminal_id = t.id
      AND cb.bin_no = 'M5-A2'
  );

INSERT INTO terminal_cash_bin (
  bin_no,
  created_at,
  current_count,
  denomination,
  max_capacity,
  status,
  terminal_id
)
SELECT
  'M5-C1',
  NOW(),
  2400,
  200.00,
  2400,
  'FULL',
  t.id
FROM terminal t
WHERE t.terminal_code = 'CDMM5-013'
  AND NOT EXISTS (
    SELECT 1
    FROM terminal_cash_bin cb
    WHERE cb.terminal_id = t.id
      AND cb.bin_no = 'M5-C1'
  );

INSERT INTO terminal_replenishment (
  amount_added,
  bin_no,
  created_at,
  denomination,
  performed_by,
  quantity_added,
  remarks,
  replenishment_date,
  status,
  terminal_id
)
SELECT
  600000.00,
  'M5-A2',
  NOW(),
  1000.00,
  3,
  600,
  'Manual module 5 seed replenishment entry',
  '2026-04-29',
  'COMPLETED',
  t.id
FROM terminal t
WHERE t.terminal_code = 'ATMM5-012'
  AND NOT EXISTS (
    SELECT 1
    FROM terminal_replenishment r
    WHERE r.terminal_id = t.id
      AND r.bin_no = 'M5-A2'
      AND r.replenishment_date = '2026-04-29'
  );

INSERT INTO terminal_reconciliation (
  approved_at,
  approved_by,
  created_at,
  physical_amount,
  recon_date,
  remarks,
  status,
  system_amount,
  terminal_id,
  variance_amount
)
SELECT
  NULL,
  NULL,
  NOW(),
  250000.00,
  '2026-04-15',
  'Balanced end-of-day reconciliation',
  'MATCHED',
  250000.00,
  t.id,
  0.00
FROM terminal t
WHERE t.terminal_code = 'ATM001'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-15'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NULL, NULL, NOW(), 179500.00, '2026-04-16', 'Minor shortage identified during vault balancing', 'VARIANCE_FOUND', 180000.00, t.id, -500.00
FROM terminal t
WHERE t.terminal_code = 'CDM001'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-16'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NOW(), 3, NOW(), 320000.00, '2026-04-17', 'Approved after physical and system amount matched', 'APPROVED', 320000.00, t.id, 0.00
FROM terminal t
WHERE t.terminal_code = 'ATM002'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-17'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NULL, NULL, NOW(), 497500.00, '2026-04-18', 'Expected cassette gap recorded for follow-up', 'VARIANCE_FOUND', 500000.00, t.id, -2500.00
FROM terminal t
WHERE t.terminal_code = 'ATMCDM001'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-18'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NOW(), 1, NOW(), 150000.00, '2026-04-19', 'Maintenance-day balance approved by supervisor', 'APPROVED', 150000.00, t.id, 0.00
FROM terminal t
WHERE t.terminal_code = 'ATM003'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-19'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NULL, NULL, NOW(), 88000.00, '2026-04-20', 'Matched after cash loading close review', 'MATCHED', 88000.00, t.id, 0.00
FROM terminal t
WHERE t.terminal_code = 'CDM002'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-20'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NULL, NULL, NOW(), 124000.00, '2026-04-21', 'Out-of-service terminal requires variance investigation', 'VARIANCE_FOUND', 125000.00, t.id, -1000.00
FROM terminal t
WHERE t.terminal_code = 'ATM004'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-21'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NOW(), 2, NOW(), 412000.00, '2026-04-22', 'Large branch balance verified and approved', 'APPROVED', 412000.00, t.id, 0.00
FROM terminal t
WHERE t.terminal_code = 'ATM005'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-22'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NULL, NULL, NOW(), 235000.00, '2026-04-23', 'Matched Mirpur ATM cash review', 'MATCHED', 235000.00, t.id, 0.00
FROM terminal t
WHERE t.terminal_code = 'CDM003'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-23'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NULL, NULL, NOW(), 556500.00, '2026-04-24', 'Slight overage recorded during branch cash close', 'VARIANCE_FOUND', 555000.00, t.id, 1500.00
FROM terminal t
WHERE t.terminal_code = 'ATM006'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-24'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NOW(), 3, NOW(), 268000.00, '2026-04-25', 'Motijheel booth end-of-day approved', 'APPROVED', 268000.00, t.id, 0.00
FROM terminal t
WHERE t.terminal_code = 'ATM007'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-25'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NULL, NULL, NOW(), 218000.00, '2026-04-26', 'New terminal matched after soft launch monitoring', 'MATCHED', 218000.00, t.id, 0.00
FROM terminal t
WHERE t.terminal_code = 'ATMM5-012'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-26'
  );

INSERT INTO terminal_reconciliation (approved_at, approved_by, created_at, physical_amount, recon_date, remarks, status, system_amount, terminal_id, variance_amount)
SELECT NULL, NULL, NOW(), 96400.00, '2026-04-27', 'Inactive CDM variance entered for operator review', 'VARIANCE_FOUND', 98000.00, t.id, -1600.00
FROM terminal t
WHERE t.terminal_code = 'CDMM5-013'
  AND NOT EXISTS (
    SELECT 1 FROM terminal_reconciliation r
    WHERE r.terminal_id = t.id AND r.recon_date = '2026-04-27'
  );

COMMIT;
