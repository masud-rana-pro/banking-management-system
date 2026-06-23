CREATE TABLE IF NOT EXISTS report_definition (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  report_code VARCHAR(40) NOT NULL,
  report_name VARCHAR(180) NOT NULL,
  report_type VARCHAR(30) NOT NULL,
  query_key VARCHAR(80) NOT NULL,
  export_types VARCHAR(80) NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at DATETIME NOT NULL,
  UNIQUE KEY uk_report_definition_code (report_code),
  UNIQUE KEY uk_report_definition_query_key (query_key)
);

CREATE TABLE IF NOT EXISTS report_request_log (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  report_id BIGINT NOT NULL,
  requested_by VARCHAR(120) NOT NULL,
  date_from DATE NULL,
  date_to DATE NULL,
  filter_json TEXT NULL,
  generated_file_id BIGINT NULL,
  request_status VARCHAR(20) NOT NULL,
  requested_at DATETIME NOT NULL,
  generated_at DATETIME NULL,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT fk_report_request_log_report FOREIGN KEY (report_id) REFERENCES report_definition(id),
  CONSTRAINT fk_report_request_log_file FOREIGN KEY (generated_file_id) REFERENCES file_reference(id)
);

DELETE FROM report_request_log WHERE requested_by = 'REPORT_SEED';
DELETE FROM file_reference WHERE module_name = 'REPORTS' AND file_name LIKE 'report-seed-%';

INSERT INTO report_definition (report_code, report_name, report_type, query_key, export_types, status, created_at) VALUES
('REP-OPR-001', 'Operational Report', 'OPERATIONAL', 'OPERATIONAL', 'PDF,CSV,PRINT', 'ACTIVE', NOW()),
('REP-PRO-001', 'Profit Distribution Report', 'PROFIT', 'PROFIT_DISTRIBUTION', 'PDF,CSV,PRINT', 'ACTIVE', NOW()),
('REP-FIN-001', 'Financing Portfolio Report', 'FINANCING', 'FINANCING_PORTFOLIO', 'PDF,CSV,PRINT', 'ACTIVE', NOW()),
('REP-PAR-001', 'PAR Report', 'PAR', 'PAR', 'PDF,CSV,PRINT', 'ACTIVE', NOW()),
('REP-SHA-001', 'Shariah Audit Report', 'SHARIAH_AUDIT', 'SHARIAH_AUDIT', 'PDF,CSV,PRINT', 'ACTIVE', NOW()),
('REP-BRA-001', 'Branch Performance Report', 'BRANCH', 'BRANCH', 'PDF,CSV,PRINT', 'ACTIVE', NOW()),
('RPT20-007', 'Operational Exception Summary', 'REGULATORY', 'OPERATIONAL_EXCEPTION_SUMMARY', 'PDF,CSV', 'ACTIVE', NOW()),
('RPT20-008', 'Regulatory Compliance Snapshot', 'REGULATORY', 'REGULATORY_COMPLIANCE_SNAPSHOT', 'PDF,CSV', 'ACTIVE', NOW()),
('RPT20-009', 'Daily Cash Activity', 'OPERATIONAL', 'DAILY_CASH_ACTIVITY', 'PDF,CSV', 'ACTIVE', NOW()),
('RPT20-010', 'Profit Variance Analysis', 'PROFIT', 'PROFIT_VARIANCE_ANALYSIS', 'PDF,CSV', 'ACTIVE', NOW()),
('RPT20-011', 'Financing Sector Mix', 'FINANCING', 'FINANCING_SECTOR_MIX', 'PDF,CSV', 'ACTIVE', NOW()),
('RPT20-012', 'Branch Channel Utilization', 'BRANCH', 'BRANCH_CHANNEL_UTILIZATION', 'PDF,CSV', 'ACTIVE', NOW()),
('RPT20-013', 'Shariah Observation Register', 'SHARIAH_AUDIT', 'SHARIAH_OBSERVATION_REGISTER', 'PDF,CSV', 'ACTIVE', NOW()),
('RPT20-014', 'Portfolio Risk Aging', 'PAR', 'PORTFOLIO_RISK_AGING', 'PDF,CSV', 'ACTIVE', NOW()),
('RPT20-015', 'Financing Aging Monitor', 'REGULATORY', 'FINANCING_AGING_MONITOR', 'PDF,CSV', 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE
  report_name = VALUES(report_name),
  report_type = VALUES(report_type),
  export_types = VALUES(export_types),
  status = VALUES(status);

INSERT INTO file_reference (file_name, original_file_name, file_path, file_type, file_size, module_name, reference_table, reference_id, status, created_at) VALUES
('report-seed-01.html', 'report-seed-01.html', 'generated-reports/report-seed-01.html', 'text/html', 2048, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-02.html', 'report-seed-02.html', 'generated-reports/report-seed-02.html', 'text/html', 2084, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-03.html', 'report-seed-03.html', 'generated-reports/report-seed-03.html', 'text/html', 2120, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-04.html', 'report-seed-04.html', 'generated-reports/report-seed-04.html', 'text/html', 2140, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-05.html', 'report-seed-05.html', 'generated-reports/report-seed-05.html', 'text/html', 2160, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-06.html', 'report-seed-06.html', 'generated-reports/report-seed-06.html', 'text/html', 2200, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-07.csv', 'report-seed-07.csv', 'generated-reports/report-seed-07.csv', 'text/csv', 1220, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-08.csv', 'report-seed-08.csv', 'generated-reports/report-seed-08.csv', 'text/csv', 1240, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-09.html', 'report-seed-09.html', 'generated-reports/report-seed-09.html', 'text/html', 2060, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-10.csv', 'report-seed-10.csv', 'generated-reports/report-seed-10.csv', 'text/csv', 1280, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-11.html', 'report-seed-11.html', 'generated-reports/report-seed-11.html', 'text/html', 2050, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-12.csv', 'report-seed-12.csv', 'generated-reports/report-seed-12.csv', 'text/csv', 1300, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-13.html', 'report-seed-13.html', 'generated-reports/report-seed-13.html', 'text/html', 2180, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-14.csv', 'report-seed-14.csv', 'generated-reports/report-seed-14.csv', 'text/csv', 1320, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW()),
('report-seed-15.html', 'report-seed-15.html', 'generated-reports/report-seed-15.html', 'text/html', 2220, 'REPORTS', 'report_request_log', NULL, 'ACTIVE', NOW());

INSERT INTO report_request_log (report_id, requested_by, date_from, date_to, filter_json, generated_file_id, request_status, requested_at, generated_at, status) VALUES
((SELECT id FROM report_definition WHERE query_key = 'OPERATIONAL' LIMIT 1), 'REPORT_SEED', '2026-04-01', '2026-04-30', '{"dateFrom":"2026-04-01","dateTo":"2026-04-30","branchId":1,"exportType":"VIEW"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-01.html' LIMIT 1), 'GENERATED', '2026-05-01 08:10:00', '2026-05-01 08:11:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'PROFIT_DISTRIBUTION' LIMIT 1), 'REPORT_SEED', '2026-04-01', '2026-04-30', '{"dateFrom":"2026-04-01","dateTo":"2026-04-30","exportType":"PDF"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-02.html' LIMIT 1), 'EXPORTED', '2026-05-01 08:20:00', '2026-05-01 08:21:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'FINANCING_PORTFOLIO' LIMIT 1), 'REPORT_SEED', '2026-03-01', '2026-04-30', '{"dateFrom":"2026-03-01","dateTo":"2026-04-30","exportType":"VIEW"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-03.html' LIMIT 1), 'GENERATED', '2026-05-01 08:35:00', '2026-05-01 08:36:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'PAR' LIMIT 1), 'REPORT_SEED', '2026-01-01', '2026-04-30', '{"dateFrom":"2026-01-01","dateTo":"2026-04-30","exportType":"PDF"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-04.html' LIMIT 1), 'EXPORTED', '2026-05-01 08:50:00', '2026-05-01 08:52:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'SHARIAH_AUDIT' LIMIT 1), 'REPORT_SEED', '2026-04-01', '2026-04-30', '{"dateFrom":"2026-04-01","dateTo":"2026-04-30","exportType":"VIEW"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-05.html' LIMIT 1), 'GENERATED', '2026-05-01 09:00:00', '2026-05-01 09:02:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'BRANCH' LIMIT 1), 'REPORT_SEED', '2026-04-01', '2026-04-30', '{"dateFrom":"2026-04-01","dateTo":"2026-04-30","branchId":2,"exportType":"VIEW"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-06.html' LIMIT 1), 'GENERATED', '2026-05-01 09:15:00', '2026-05-01 09:16:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'OPERATIONAL' LIMIT 1), 'REPORT_SEED', '2026-04-15', '2026-04-30', '{"dateFrom":"2026-04-15","dateTo":"2026-04-30","branchId":3,"exportType":"CSV"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-07.csv' LIMIT 1), 'EXPORTED', '2026-05-01 09:25:00', '2026-05-01 09:26:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'FINANCING_PORTFOLIO' LIMIT 1), 'REPORT_SEED', '2026-02-01', '2026-04-30', '{"dateFrom":"2026-02-01","dateTo":"2026-04-30","exportType":"CSV"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-08.csv' LIMIT 1), 'EXPORTED', '2026-05-01 09:40:00', '2026-05-01 09:41:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'BRANCH' LIMIT 1), 'REPORT_SEED', '2026-03-01', '2026-04-30', '{"dateFrom":"2026-03-01","dateTo":"2026-04-30","branchId":1,"exportType":"VIEW"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-09.html' LIMIT 1), 'GENERATED', '2026-05-01 10:00:00', '2026-05-01 10:01:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'PROFIT_DISTRIBUTION' LIMIT 1), 'REPORT_SEED', '2026-02-01', '2026-04-30', '{"dateFrom":"2026-02-01","dateTo":"2026-04-30","exportType":"CSV"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-10.csv' LIMIT 1), 'EXPORTED', '2026-05-01 10:15:00', '2026-05-01 10:16:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'SHARIAH_AUDIT' LIMIT 1), 'REPORT_SEED', '2026-01-01', '2026-04-30', '{"dateFrom":"2026-01-01","dateTo":"2026-04-30","exportType":"VIEW"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-11.html' LIMIT 1), 'GENERATED', '2026-05-01 10:28:00', '2026-05-01 10:29:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'PAR' LIMIT 1), 'REPORT_SEED', '2026-03-01', '2026-04-30', '{"dateFrom":"2026-03-01","dateTo":"2026-04-30","exportType":"CSV"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-12.csv' LIMIT 1), 'EXPORTED', '2026-05-01 10:42:00', '2026-05-01 10:43:00', 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'OPERATIONAL_EXCEPTION_SUMMARY' LIMIT 1), 'REPORT_SEED', '2026-04-01', '2026-04-30', '{"dateFrom":"2026-04-01","dateTo":"2026-04-30","exportType":"VIEW"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-13.html' LIMIT 1), 'REQUESTED', '2026-05-01 11:00:00', NULL, 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'REGULATORY_COMPLIANCE_SNAPSHOT' LIMIT 1), 'REPORT_SEED', '2026-04-01', '2026-04-30', '{"dateFrom":"2026-04-01","dateTo":"2026-04-30","exportType":"CSV"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-14.csv' LIMIT 1), 'REQUESTED', '2026-05-01 11:10:00', NULL, 'ACTIVE'),
((SELECT id FROM report_definition WHERE query_key = 'FINANCING_AGING_MONITOR' LIMIT 1), 'REPORT_SEED', '2026-02-01', '2026-04-30', '{"dateFrom":"2026-02-01","dateTo":"2026-04-30","exportType":"VIEW"}', (SELECT id FROM file_reference WHERE file_name = 'report-seed-15.html' LIMIT 1), 'FAILED', '2026-05-01 11:20:00', '2026-05-01 11:21:00', 'ACTIVE');
