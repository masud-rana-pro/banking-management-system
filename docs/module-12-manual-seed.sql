SET NAMES utf8mb4;

START TRANSACTION;

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM customer_statement_request;
DELETE FROM branch_statement_request;
DELETE FROM file_reference
WHERE module_name IN ('CUSTOMER_STATEMENT', 'BRANCH_STATEMENT');

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO file_reference (
  id, file_name, original_file_name, file_path, file_type, file_size,
  module_name, reference_table, reference_id, status, created_at
) VALUES
  (1, 'CSR-00001.html', 'Customer-Statement-CSR-00001.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00001.html', 'text/html', 999, 'CUSTOMER_STATEMENT', 'customer_statement_request', 1, 'ACTIVE', '2026-05-01 23:13:50'),
  (2, 'CSR-00002.html', 'Customer-Statement-CSR-00002.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00002.html', 'text/html', 1003, 'CUSTOMER_STATEMENT', 'customer_statement_request', 2, 'ACTIVE', '2026-05-01 23:13:50'),
  (3, 'CSR-00003.html', 'Customer-Statement-CSR-00003.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00003.html', 'text/html', 999, 'CUSTOMER_STATEMENT', 'customer_statement_request', 3, 'ACTIVE', '2026-05-01 23:13:50'),
  (4, 'CSR-00004.html', 'Customer-Statement-CSR-00004.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00004.html', 'text/html', 1003, 'CUSTOMER_STATEMENT', 'customer_statement_request', 4, 'ACTIVE', '2026-05-01 23:13:50'),
  (5, 'CSR-00005.html', 'Customer-Statement-CSR-00005.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00005.html', 'text/html', 999, 'CUSTOMER_STATEMENT', 'customer_statement_request', 5, 'ACTIVE', '2026-05-01 23:13:51'),
  (6, 'CSR-00006.html', 'Customer-Statement-CSR-00006.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00006.html', 'text/html', 1003, 'CUSTOMER_STATEMENT', 'customer_statement_request', 6, 'ACTIVE', '2026-05-01 23:13:51'),
  (7, 'CSR-00007.html', 'Customer-Statement-CSR-00007.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00007.html', 'text/html', 999, 'CUSTOMER_STATEMENT', 'customer_statement_request', 7, 'ACTIVE', '2026-05-01 23:13:51'),
  (8, 'CSR-00008.html', 'Customer-Statement-CSR-00008.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00008.html', 'text/html', 1003, 'CUSTOMER_STATEMENT', 'customer_statement_request', 8, 'ACTIVE', '2026-05-01 23:13:51'),
  (9, 'CSR-00009.html', 'Customer-Statement-CSR-00009.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00009.html', 'text/html', 999, 'CUSTOMER_STATEMENT', 'customer_statement_request', 9, 'ACTIVE', '2026-05-01 23:13:51'),
  (10, 'CSR-00010.html', 'Customer-Statement-CSR-00010.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00010.html', 'text/html', 1003, 'CUSTOMER_STATEMENT', 'customer_statement_request', 10, 'ACTIVE', '2026-05-01 23:13:51'),
  (11, 'CSR-00011.html', 'Customer-Statement-CSR-00011.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00011.html', 'text/html', 999, 'CUSTOMER_STATEMENT', 'customer_statement_request', 11, 'ACTIVE', '2026-05-01 23:13:51'),
  (12, 'CSR-00012.html', 'Customer-Statement-CSR-00012.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00012.html', 'text/html', 1003, 'CUSTOMER_STATEMENT', 'customer_statement_request', 12, 'ACTIVE', '2026-05-01 23:13:51'),
  (13, 'CSR-00013.html', 'Customer-Statement-CSR-00013.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00013.html', 'text/html', 999, 'CUSTOMER_STATEMENT', 'customer_statement_request', 13, 'ACTIVE', '2026-05-01 23:13:51'),
  (14, 'CSR-00014.html', 'Customer-Statement-CSR-00014.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00014.html', 'text/html', 1003, 'CUSTOMER_STATEMENT', 'customer_statement_request', 14, 'ACTIVE', '2026-05-01 23:13:51'),
  (15, 'CSR-00015.html', 'Customer-Statement-CSR-00015.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\CSR-00015.html', 'text/html', 999, 'CUSTOMER_STATEMENT', 'customer_statement_request', 15, 'ACTIVE', '2026-05-01 23:13:51'),
  (16, 'BSR-00001.html', 'Branch-Statement-BSR-00001.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00001.html', 'text/html', 990, 'BRANCH_STATEMENT', 'branch_statement_request', 1, 'ACTIVE', '2026-05-01 23:13:51'),
  (17, 'BSR-00002.html', 'Branch-Statement-BSR-00002.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00002.html', 'text/html', 997, 'BRANCH_STATEMENT', 'branch_statement_request', 2, 'ACTIVE', '2026-05-01 23:13:51'),
  (18, 'BSR-00003.html', 'Branch-Statement-BSR-00003.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00003.html', 'text/html', 989, 'BRANCH_STATEMENT', 'branch_statement_request', 3, 'ACTIVE', '2026-05-01 23:13:51'),
  (19, 'BSR-00004.html', 'Branch-Statement-BSR-00004.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00004.html', 'text/html', 986, 'BRANCH_STATEMENT', 'branch_statement_request', 4, 'ACTIVE', '2026-05-01 23:13:52'),
  (20, 'BSR-00005.html', 'Branch-Statement-BSR-00005.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00005.html', 'text/html', 986, 'BRANCH_STATEMENT', 'branch_statement_request', 5, 'ACTIVE', '2026-05-01 23:13:52'),
  (21, 'BSR-00006.html', 'Branch-Statement-BSR-00006.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00006.html', 'text/html', 998, 'BRANCH_STATEMENT', 'branch_statement_request', 6, 'ACTIVE', '2026-05-01 23:13:52'),
  (22, 'BSR-00007.html', 'Branch-Statement-BSR-00007.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00007.html', 'text/html', 994, 'BRANCH_STATEMENT', 'branch_statement_request', 7, 'ACTIVE', '2026-05-01 23:13:52'),
  (23, 'BSR-00008.html', 'Branch-Statement-BSR-00008.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00008.html', 'text/html', 997, 'BRANCH_STATEMENT', 'branch_statement_request', 8, 'ACTIVE', '2026-05-01 23:13:52'),
  (24, 'BSR-00009.html', 'Branch-Statement-BSR-00009.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00009.html', 'text/html', 988, 'BRANCH_STATEMENT', 'branch_statement_request', 9, 'ACTIVE', '2026-05-01 23:13:52'),
  (25, 'BSR-00010.html', 'Branch-Statement-BSR-00010.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00010.html', 'text/html', 986, 'BRANCH_STATEMENT', 'branch_statement_request', 10, 'ACTIVE', '2026-05-01 23:13:52'),
  (26, 'BSR-00011.html', 'Branch-Statement-BSR-00011.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00011.html', 'text/html', 988, 'BRANCH_STATEMENT', 'branch_statement_request', 11, 'ACTIVE', '2026-05-01 23:13:52'),
  (27, 'BSR-00012.html', 'Branch-Statement-BSR-00012.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00012.html', 'text/html', 987, 'BRANCH_STATEMENT', 'branch_statement_request', 12, 'ACTIVE', '2026-05-01 23:13:52'),
  (28, 'BSR-00013.html', 'Branch-Statement-BSR-00013.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00013.html', 'text/html', 990, 'BRANCH_STATEMENT', 'branch_statement_request', 13, 'ACTIVE', '2026-05-01 23:13:52'),
  (29, 'BSR-00014.html', 'Branch-Statement-BSR-00014.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00014.html', 'text/html', 991, 'BRANCH_STATEMENT', 'branch_statement_request', 14, 'ACTIVE', '2026-05-01 23:13:52'),
  (30, 'BSR-00015.html', 'Branch-Statement-BSR-00015.html', 'I:\\SBMS Copy\\stable-sbms-backend\\generated-statements\\BSR-00015.html', 'text/html', 987, 'BRANCH_STATEMENT', 'branch_statement_request', 15, 'ACTIVE', '2026-05-01 23:13:52')
ON DUPLICATE KEY UPDATE
  file_name = VALUES(file_name),
  original_file_name = VALUES(original_file_name),
  file_path = VALUES(file_path),
  file_type = VALUES(file_type),
  file_size = VALUES(file_size),
  module_name = VALUES(module_name),
  reference_table = VALUES(reference_table),
  reference_id = VALUES(reference_id),
  status = VALUES(status),
  created_at = VALUES(created_at);

INSERT INTO customer_statement_request (
  id, request_no, customer_id, account_id, date_from, date_to, request_status,
  generated_file_id, requested_by, requested_at, generated_at, status
) VALUES
  (1, 'CSR-00001', 1, 1, '2026-01-01', '2026-01-21', 'GENERATED', 1, 'SYSTEM', '2026-05-01 23:13:50', '2026-05-01 23:13:50', 'ACTIVE'),
  (2, 'CSR-00002', 7, 7, '2026-01-05', '2026-01-25', 'GENERATED', 2, 'SYSTEM', '2026-05-01 23:13:50', '2026-05-01 23:13:50', 'ACTIVE'),
  (3, 'CSR-00003', 1, 1, '2026-01-09', '2026-01-29', 'GENERATED', 3, 'SYSTEM', '2026-05-01 23:13:50', '2026-05-01 23:13:50', 'ACTIVE'),
  (4, 'CSR-00004', 7, 7, '2026-01-13', '2026-02-02', 'GENERATED', 4, 'SYSTEM', '2026-05-01 23:13:50', '2026-05-01 23:13:50', 'ACTIVE'),
  (5, 'CSR-00005', 1, 1, '2026-01-17', '2026-02-06', 'GENERATED', 5, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (6, 'CSR-00006', 7, 7, '2026-01-21', '2026-02-10', 'GENERATED', 6, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (7, 'CSR-00007', 1, 1, '2026-01-25', '2026-02-14', 'GENERATED', 7, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (8, 'CSR-00008', 7, 7, '2026-01-29', '2026-02-18', 'GENERATED', 8, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (9, 'CSR-00009', 1, 1, '2026-02-02', '2026-02-22', 'GENERATED', 9, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (10, 'CSR-00010', 7, 7, '2026-02-06', '2026-02-26', 'GENERATED', 10, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (11, 'CSR-00011', 1, 1, '2026-02-10', '2026-03-02', 'DOWNLOADED', 11, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (12, 'CSR-00012', 7, 7, '2026-02-14', '2026-03-06', 'DOWNLOADED', 12, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (13, 'CSR-00013', 1, 1, '2026-02-18', '2026-03-10', 'DOWNLOADED', 13, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (14, 'CSR-00014', 7, 7, '2026-02-22', '2026-03-14', 'DOWNLOADED', 14, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (15, 'CSR-00015', 1, 1, '2026-02-26', '2026-03-18', 'DOWNLOADED', 15, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE')
ON DUPLICATE KEY UPDATE
  request_no = VALUES(request_no),
  customer_id = VALUES(customer_id),
  account_id = VALUES(account_id),
  date_from = VALUES(date_from),
  date_to = VALUES(date_to),
  request_status = VALUES(request_status),
  generated_file_id = VALUES(generated_file_id),
  requested_by = VALUES(requested_by),
  requested_at = VALUES(requested_at),
  generated_at = VALUES(generated_at),
  status = VALUES(status);

INSERT INTO branch_statement_request (
  id, request_no, branch_id, date_from, date_to, request_status,
  generated_file_id, requested_by, requested_at, generated_at, status
) VALUES
  (1, 'BSR-00001', 1, '2026-01-01', '2026-01-19', 'GENERATED', 16, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (2, 'BSR-00002', 2, '2026-01-04', '2026-01-22', 'GENERATED', 17, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (3, 'BSR-00003', 3, '2026-01-07', '2026-01-25', 'GENERATED', 18, 'SYSTEM', '2026-05-01 23:13:51', '2026-05-01 23:13:51', 'ACTIVE'),
  (4, 'BSR-00004', 4, '2026-01-10', '2026-01-28', 'GENERATED', 19, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE'),
  (5, 'BSR-00005', 5, '2026-01-13', '2026-01-31', 'GENERATED', 20, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE'),
  (6, 'BSR-00006', 6, '2026-01-16', '2026-02-03', 'GENERATED', 21, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE'),
  (7, 'BSR-00007', 7, '2026-01-19', '2026-02-06', 'GENERATED', 22, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE'),
  (8, 'BSR-00008', 8, '2026-01-22', '2026-02-09', 'GENERATED', 23, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE'),
  (9, 'BSR-00009', 9, '2026-01-25', '2026-02-12', 'GENERATED', 24, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE'),
  (10, 'BSR-00010', 10, '2026-01-28', '2026-02-15', 'GENERATED', 25, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE'),
  (11, 'BSR-00011', 11, '2026-01-31', '2026-02-18', 'DOWNLOADED', 26, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE'),
  (12, 'BSR-00012', 12, '2026-02-03', '2026-02-21', 'DOWNLOADED', 27, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE'),
  (13, 'BSR-00013', 13, '2026-02-06', '2026-02-24', 'DOWNLOADED', 28, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE'),
  (14, 'BSR-00014', 14, '2026-02-09', '2026-02-27', 'DOWNLOADED', 29, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE'),
  (15, 'BSR-00015', 15, '2026-02-12', '2026-03-02', 'DOWNLOADED', 30, 'SYSTEM', '2026-05-01 23:13:52', '2026-05-01 23:13:52', 'ACTIVE')
ON DUPLICATE KEY UPDATE
  request_no = VALUES(request_no),
  branch_id = VALUES(branch_id),
  date_from = VALUES(date_from),
  date_to = VALUES(date_to),
  request_status = VALUES(request_status),
  generated_file_id = VALUES(generated_file_id),
  requested_by = VALUES(requested_by),
  requested_at = VALUES(requested_at),
  generated_at = VALUES(generated_at),
  status = VALUES(status);

ALTER TABLE file_reference AUTO_INCREMENT = 31;
ALTER TABLE customer_statement_request AUTO_INCREMENT = 16;
ALTER TABLE branch_statement_request AUTO_INCREMENT = 16;

COMMIT;

-- Note:
-- This SQL mirrors the current seeded environment and keeps the same
-- generated HTML file references under stable-sbms-backend/generated-statements.
-- If you want to regenerate files from scratch instead of reusing these paths,
-- use docs/module-12-postman-samples.json against the live APIs.
