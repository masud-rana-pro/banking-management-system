SET NAMES utf8mb4;

START TRANSACTION;

INSERT INTO profit_ratio (
  id, ratio_code, account_type_id, effective_from, effective_to, ratio_percent, status, created_at, updated_at
) VALUES
  (1, 'PSR-00001', 1, '2026-01-01', NULL, 5.2500, 'ACTIVE', '2026-04-01 09:00:00', '2026-04-01 09:00:00'),
  (2, 'PSR-00002', 2, '2025-01-01', '2025-12-31', 2.0000, 'ARCHIVED', '2026-04-01 09:05:00', '2026-04-01 09:05:00'),
  (3, 'PSR-00003', 3, '2026-01-01', NULL, 5.7500, 'ACTIVE', '2026-04-01 09:10:00', '2026-04-01 09:10:00'),
  (4, 'PSR-00004', 4, '2025-06-01', '2025-12-31', 1.5000, 'ARCHIVED', '2026-04-01 09:15:00', '2026-04-01 09:15:00'),
  (5, 'PSR-00005', 5, '2026-01-01', NULL, 6.2500, 'ACTIVE', '2026-04-01 09:20:00', '2026-04-01 09:20:00'),
  (6, 'PSR-00006', 6, '2026-01-01', NULL, 6.1000, 'ACTIVE', '2026-04-01 09:25:00', '2026-04-01 09:25:00'),
  (7, 'PSR-00007', 7, '2026-01-01', NULL, 6.3000, 'ACTIVE', '2026-04-01 09:30:00', '2026-04-01 09:30:00'),
  (8, 'PSR-00008', 8, '2026-01-01', NULL, 4.8500, 'ACTIVE', '2026-04-01 09:35:00', '2026-04-01 09:35:00'),
  (9, 'PSR-00009', 9, '2026-01-01', NULL, 5.0000, 'ACTIVE', '2026-04-01 09:40:00', '2026-04-01 09:40:00'),
  (10, 'PSR-00010', 10, '2025-01-01', '2025-12-31', 1.7500, 'ARCHIVED', '2026-04-01 09:45:00', '2026-04-01 09:45:00'),
  (11, 'PSR-00011', 11, '2026-01-01', NULL, 5.4000, 'ACTIVE', '2026-04-01 09:50:00', '2026-04-01 09:50:00'),
  (12, 'PSR-00012', 12, '2026-01-01', NULL, 5.1500, 'ACTIVE', '2026-04-01 09:55:00', '2026-04-01 09:55:00'),
  (13, 'PSR-00013', 13, '2026-01-01', NULL, 4.9500, 'ACTIVE', '2026-04-01 10:00:00', '2026-04-01 10:00:00'),
  (14, 'PSR-00014', 14, '2026-01-01', NULL, 7.1000, 'ACTIVE', '2026-04-01 10:05:00', '2026-04-01 10:05:00'),
  (15, 'PSR-00015', 15, '2026-01-01', NULL, 4.6000, 'ACTIVE', '2026-04-01 10:10:00', '2026-04-01 10:10:00')
ON DUPLICATE KEY UPDATE
  ratio_code = VALUES(ratio_code),
  account_type_id = VALUES(account_type_id),
  effective_from = VALUES(effective_from),
  effective_to = VALUES(effective_to),
  ratio_percent = VALUES(ratio_percent),
  status = VALUES(status),
  created_at = VALUES(created_at),
  updated_at = VALUES(updated_at);

INSERT INTO profit_schedule (
  id, account_id, profit_frequency, next_posting_date, last_posting_date, status, created_at, updated_at
) VALUES
  (1, 1, 'MONTHLY', '2026-05-10', '2026-04-10', 'ACTIVE', '2026-04-05 09:00:00', '2026-04-05 09:00:00'),
  (2, 2, 'MONTHLY', '2026-05-12', '2026-04-12', 'ARCHIVED', '2026-04-05 09:05:00', '2026-04-05 09:05:00'),
  (3, 3, 'QUARTERLY', '2026-05-05', '2026-02-05', 'ACTIVE', '2026-04-05 09:10:00', '2026-04-05 09:10:00'),
  (4, 4, 'MONTHLY', '2026-04-20', '2026-03-20', 'ACTIVE', '2026-04-05 09:15:00', '2026-04-05 09:15:00'),
  (5, 5, 'MONTHLY', '2026-05-08', '2026-04-08', 'ACTIVE', '2026-04-05 09:20:00', '2026-04-05 09:20:00'),
  (6, 6, 'YEARLY', '2026-12-31', '2025-12-31', 'ACTIVE', '2026-04-05 09:25:00', '2026-04-05 09:25:00'),
  (7, 7, 'MONTHLY', '2026-05-03', '2026-04-03', 'ACTIVE', '2026-04-05 09:30:00', '2026-04-05 09:30:00'),
  (8, 8, 'MONTHLY', '2026-05-15', '2026-04-15', 'ACTIVE', '2026-04-05 09:35:00', '2026-04-05 09:35:00'),
  (9, 9, 'HALF_YEARLY', '2026-06-30', '2025-12-31', 'ACTIVE', '2026-04-05 09:40:00', '2026-04-05 09:40:00'),
  (10, 10, 'MONTHLY', '2026-04-18', '2026-03-18', 'ARCHIVED', '2026-04-05 09:45:00', '2026-04-05 09:45:00'),
  (11, 11, 'MONTHLY', '2026-05-06', '2026-04-06', 'ACTIVE', '2026-04-05 09:50:00', '2026-04-05 09:50:00'),
  (12, 12, 'QUARTERLY', '2026-07-01', '2026-04-01', 'ACTIVE', '2026-04-05 09:55:00', '2026-04-05 09:55:00'),
  (13, 13, 'MONTHLY', '2026-05-04', '2026-04-04', 'ACTIVE', '2026-04-05 10:00:00', '2026-04-05 10:00:00'),
  (14, 14, 'MONTHLY', '2026-04-25', '2026-03-25', 'ACTIVE', '2026-04-05 10:05:00', '2026-04-05 10:05:00'),
  (15, 15, 'MONTHLY', '2026-05-07', '2026-04-07', 'ACTIVE', '2026-04-05 10:10:00', '2026-04-05 10:10:00')
ON DUPLICATE KEY UPDATE
  account_id = VALUES(account_id),
  profit_frequency = VALUES(profit_frequency),
  next_posting_date = VALUES(next_posting_date),
  last_posting_date = VALUES(last_posting_date),
  status = VALUES(status),
  created_at = VALUES(created_at),
  updated_at = VALUES(updated_at);

INSERT INTO balance_snapshot (
  id, account_id, snapshot_date, closing_balance, average_balance, status, created_at, updated_at
) VALUES
  (1, 1, '2026-04-10', 25000.00, 24000.00, 'ACTIVE', '2026-04-10 17:00:00', '2026-04-10 17:00:00'),
  (2, 2, '2026-04-12', 18000.00, 17500.00, 'ACTIVE', '2026-04-12 17:05:00', '2026-04-12 17:05:00'),
  (3, 3, '2026-04-05', 32000.00, 31500.00, 'ACTIVE', '2026-04-05 17:10:00', '2026-04-05 17:10:00'),
  (4, 4, '2026-04-20', 9000.00, 8800.00, 'ACTIVE', '2026-04-20 17:15:00', '2026-04-20 17:15:00'),
  (5, 5, '2026-04-08', 45000.00, 44500.00, 'ACTIVE', '2026-04-08 17:20:00', '2026-04-08 17:20:00'),
  (6, 6, '2025-12-31', 12500.00, 12000.00, 'ACTIVE', '2025-12-31 17:25:00', '2025-12-31 17:25:00'),
  (7, 7, '2026-04-03', 16000.00, 15800.00, 'ACTIVE', '2026-04-03 17:30:00', '2026-04-03 17:30:00'),
  (8, 8, '2026-04-15', 7500.00, 7200.00, 'ACTIVE', '2026-04-15 17:35:00', '2026-04-15 17:35:00'),
  (9, 9, '2025-12-31', 28500.00, 28000.00, 'ACTIVE', '2025-12-31 17:40:00', '2025-12-31 17:40:00'),
  (10, 10, '2026-04-18', 52000.00, 51000.00, 'ACTIVE', '2026-04-18 17:45:00', '2026-04-18 17:45:00'),
  (11, 11, '2026-04-06', 14000.00, 13850.00, 'ACTIVE', '2026-04-06 17:50:00', '2026-04-06 17:50:00'),
  (12, 12, '2026-04-01', 22000.00, 21400.00, 'ACTIVE', '2026-04-01 17:55:00', '2026-04-01 17:55:00'),
  (13, 13, '2026-04-04', 6800.00, 6600.00, 'ACTIVE', '2026-04-04 18:00:00', '2026-04-04 18:00:00'),
  (14, 14, '2026-04-25', 9800.00, 9600.00, 'ACTIVE', '2026-04-25 18:05:00', '2026-04-25 18:05:00'),
  (15, 15, '2026-04-07', 15500.00, 15250.00, 'ACTIVE', '2026-04-07 18:10:00', '2026-04-07 18:10:00')
ON DUPLICATE KEY UPDATE
  account_id = VALUES(account_id),
  snapshot_date = VALUES(snapshot_date),
  closing_balance = VALUES(closing_balance),
  average_balance = VALUES(average_balance),
  status = VALUES(status),
  created_at = VALUES(created_at),
  updated_at = VALUES(updated_at);

INSERT INTO profit_posting (
  id, posting_ref, account_id, schedule_id, posting_date, profit_amount, period_from, period_to, posted_by, status, failure_reason, created_at
) VALUES
  (1, 'PRF-00001', 1, 1, '2026-04-10', 105.00, '2026-03-11', '2026-04-10', 'SYSTEM_PROFIT_ENGINE', 'POSTED', NULL, '2026-04-10 10:00:00'),
  (2, 'PRF-00002', 2, 2, '2026-04-12', 0.00, '2026-03-13', '2026-04-12', 'SYSTEM_PROFIT_ENGINE', 'FAILED', 'Account type is not profit applicable', '2026-04-12 10:05:00'),
  (3, 'PRF-00003', 3, 3, '2026-04-05', 453.13, '2026-01-06', '2026-04-05', 'SYSTEM_PROFIT_ENGINE', 'POSTED', NULL, '2026-04-05 10:10:00'),
  (4, 'PRF-00004', 4, 4, '2026-04-20', 0.00, '2026-03-21', '2026-04-20', 'SYSTEM_PROFIT_ENGINE', 'FAILED', 'Account is not active for profit posting', '2026-04-20 10:15:00'),
  (5, 'PRF-00005', 5, 5, '2026-04-08', 278.13, '2026-03-09', '2026-04-08', 'SYSTEM_PROFIT_ENGINE', 'POSTED', NULL, '2026-04-08 10:20:00'),
  (6, 'PRF-00006', 6, 6, '2025-12-31', 0.00, '2025-01-01', '2025-12-31', 'SYSTEM_PROFIT_ENGINE', 'FAILED', 'Account is not active for profit posting', '2026-04-22 10:25:00'),
  (7, 'PRF-00007', 7, 7, '2026-04-03', 84.00, '2026-03-04', '2026-04-03', 'SYSTEM_PROFIT_ENGINE', 'POSTED', NULL, '2026-04-03 10:30:00'),
  (8, 'PRF-00008', 8, 8, '2026-04-15', 29.10, '2026-03-16', '2026-04-15', 'SYSTEM_PROFIT_ENGINE', 'POSTED', NULL, '2026-04-15 10:35:00'),
  (9, 'PRF-00009', 9, 9, '2025-12-31', 700.00, '2025-07-01', '2025-12-31', 'SYSTEM_PROFIT_ENGINE', 'POSTED', NULL, '2026-04-18 10:40:00'),
  (10, 'PRF-00010', 10, 10, '2026-04-18', 0.00, '2026-03-19', '2026-04-18', 'SYSTEM_PROFIT_ENGINE', 'FAILED', 'No active profit ratio found for this account type', '2026-04-18 10:45:00'),
  (11, 'PRF-00011', 11, 11, '2026-04-06', 62.33, '2026-03-07', '2026-04-06', 'SYSTEM_PROFIT_ENGINE', 'POSTED', NULL, '2026-04-06 10:50:00'),
  (12, 'PRF-00012', 12, 12, '2026-04-01', 0.00, '2026-01-02', '2026-04-01', 'SYSTEM_PROFIT_ENGINE', 'FAILED', 'Account is not active for profit posting', '2026-04-01 10:55:00'),
  (13, 'PRF-00013', 13, 13, '2026-04-04', 27.23, '2026-03-05', '2026-04-04', 'SYSTEM_PROFIT_ENGINE', 'POSTED', NULL, '2026-04-04 11:00:00'),
  (14, 'PRF-00014', 14, 14, '2026-04-25', 0.00, '2026-03-26', '2026-04-25', 'SYSTEM_PROFIT_ENGINE', 'FAILED', 'Account is not active for profit posting', '2026-04-25 11:05:00'),
  (15, 'PRF-00015', 15, 15, '2026-04-07', 58.46, '2026-03-08', '2026-04-07', 'SYSTEM_PROFIT_ENGINE', 'POSTED', NULL, '2026-04-07 11:10:00')
ON DUPLICATE KEY UPDATE
  posting_ref = VALUES(posting_ref),
  account_id = VALUES(account_id),
  schedule_id = VALUES(schedule_id),
  posting_date = VALUES(posting_date),
  profit_amount = VALUES(profit_amount),
  period_from = VALUES(period_from),
  period_to = VALUES(period_to),
  posted_by = VALUES(posted_by),
  status = VALUES(status),
  failure_reason = VALUES(failure_reason),
  created_at = VALUES(created_at);

UPDATE account
SET profit_ratio_id = CASE
  WHEN account_type_id IN (1, 3, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15) THEN account_type_id
  ELSE NULL
END
WHERE id BETWEEN 1 AND 15;

COMMIT;
