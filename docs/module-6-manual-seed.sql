SET NAMES utf8mb4;

START TRANSACTION;

UPDATE customer_address
SET
  city = 'Dhaka',
  country = 'Bangladesh',
  district = 'Dhaka District',
  line_1 = 'House 12, Road 4, Dhanmondi',
  line_2 = 'Near main road',
  address_line1 = 'House 12, Road 4, Dhanmondi',
  address_line2 = 'Near main road',
  country_id = 1,
  division_id = 101,
  district_id = 1001,
  upazila_id = 10001,
  postal_code = '1207',
  is_primary = b'1',
  updated_at = NOW()
WHERE customer_id = 1 AND address_type = 'PRESENT' AND (address_line1 IS NULL OR address_line1 = '');

UPDATE customer_address
SET
  city = 'Dhaka',
  country = 'Bangladesh',
  district = 'Gazipur',
  line_1 = 'Plot 18, Sector 7, Uttara',
  line_2 = 'North side lane',
  address_line1 = 'Plot 18, Sector 7, Uttara',
  address_line2 = 'North side lane',
  country_id = 1,
  division_id = 101,
  district_id = 1002,
  upazila_id = 10003,
  postal_code = '1230',
  is_primary = b'1',
  updated_at = NOW()
WHERE customer_id = 2 AND address_type = 'PRESENT' AND (address_line1 IS NULL OR address_line1 = '');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'PRESENT', 'Dhaka', 'Bangladesh', NOW(), c.id, 'Dhaka District',
       'House 21, Road 3, Dhanmondi', 'Block A', '1209', NOW(),
       'House 21, Road 3, Dhanmondi', 'Block A', 1, 1001, 101,
       b'1', 'ACTIVE', 10001
FROM customer c
WHERE c.id = 3
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'PRESENT');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'PERMANENT', 'Dhaka', 'Bangladesh', NOW(), c.id, 'Dhaka District',
       'Village Home 7, Keraniganj', 'Post Office para', '1310', NOW(),
       'Village Home 7, Keraniganj', 'Post Office para', 1, 1001, 101,
       b'0', 'ACTIVE', 10002
FROM customer c
WHERE c.id = 4
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'PERMANENT');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'OFFICE', 'Dhaka', 'Bangladesh', NOW(), c.id, 'Dhaka District',
       'Shop 44, Islami Market', 'Ground floor', '1100', NOW(),
       'Shop 44, Islami Market', 'Ground floor', 1, 1001, 101,
       b'0', 'ACTIVE', 10001
FROM customer c
WHERE c.id = 5
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'OFFICE');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'PRESENT', 'Dhaka', 'Bangladesh', NOW(), c.id, 'Gazipur',
       'Flat 5B, House 22, Uttara', 'Sector 10', '1230', NOW(),
       'Flat 5B, House 22, Uttara', 'Sector 10', 1, 1002, 101,
       b'1', 'ACTIVE', 10003
FROM customer c
WHERE c.id = 6
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'PRESENT');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'PRESENT', 'Dhaka', 'Bangladesh', NOW(), c.id, 'Dhaka District',
       'House 33, Mirpur DOHS', 'West gate', '1216', NOW(),
       'House 33, Mirpur DOHS', 'West gate', 1, 1001, 101,
       b'1', 'ACTIVE', 10002
FROM customer c
WHERE c.id = 7
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'PRESENT');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'REGISTERED', 'Chattogram', 'Bangladesh', NOW(), c.id, 'Chattogram District',
       'Corporate Registry Address, Agrabad', 'Suite 12', '4100', NOW(),
       'Corporate Registry Address, Agrabad', 'Suite 12', 1, 1003, 102,
       b'0', 'ACTIVE', 10004
FROM customer c
WHERE c.id = 8
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'REGISTERED');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'PRESENT', 'Khulna', 'Bangladesh', NOW(), c.id, 'Khulna District',
       'House 8, Sonadanga', 'Lane 5', '9000', NOW(),
       'House 8, Sonadanga', 'Lane 5', 1, 1004, 103,
       b'1', 'ACTIVE', 10005
FROM customer c
WHERE c.id = 9
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'PRESENT');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'PERMANENT', 'Dhaka', 'Bangladesh', NOW(), c.id, 'Dhaka District',
       'Village Bari, Mohammadpur', 'North cluster', '1207', NOW(),
       'Village Bari, Mohammadpur', 'North cluster', 1, 1001, 101,
       b'0', 'ACTIVE', 10001
FROM customer c
WHERE c.id = 10
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'PERMANENT');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'OFFICE', 'Dhaka', 'Bangladesh', NOW(), c.id, 'Dhaka District',
       'Office 3C, Motijheel', 'Commerce tower', '1000', NOW(),
       'Office 3C, Motijheel', 'Commerce tower', 1, 1001, 101,
       b'0', 'ACTIVE', 10001
FROM customer c
WHERE c.id = 11
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'OFFICE');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'PRESENT', 'Dhaka', 'Bangladesh', NOW(), c.id, 'Dhaka District',
       'House 14, Banasree', 'Road 9', '1219', NOW(),
       'House 14, Banasree', 'Road 9', 1, 1001, 101,
       b'1', 'ACTIVE', 10002
FROM customer c
WHERE c.id = 12
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'PRESENT');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'REGISTERED', 'Dhaka', 'Bangladesh', NOW(), c.id, 'Dhaka District',
       'Trade Center, Gulshan', 'Level 4', '1212', NOW(),
       'Trade Center, Gulshan', 'Level 4', 1, 1001, 101,
       b'0', 'ACTIVE', 10002
FROM customer c
WHERE c.id = 13
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'REGISTERED');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'PRESENT', 'Dhaka', 'Bangladesh', NOW(), c.id, 'Dhaka District',
       'House 77, Mohammadpur', 'East block', '1207', NOW(),
       'House 77, Mohammadpur', 'East block', 1, 1001, 101,
       b'1', 'ACTIVE', 10001
FROM customer c
WHERE c.id = 14
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'PRESENT');

INSERT INTO customer_address (
  address_type, city, country, created_at, customer_id, district,
  line_1, line_2, postal_code, updated_at,
  address_line1, address_line2, country_id, district_id, division_id,
  is_primary, status, upazila_id
)
SELECT 'REGISTERED', 'Chattogram', 'Bangladesh', NOW(), c.id, 'Chattogram District',
       'Registered Office, GEC Circle', 'Floor 6', '4000', NOW(),
       'Registered Office, GEC Circle', 'Floor 6', 1, 1003, 102,
       b'1', 'ACTIVE', 10004
FROM customer c
WHERE c.id = 15
  AND NOT EXISTS (SELECT 1 FROM customer_address a WHERE a.customer_id = c.id AND a.address_type = 'REGISTERED');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'NID-900000001', 'NID', NULL, 'Bangladesh', '2016-01-15', 'ACTIVE', b'1', 1
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'NID' AND document_no = 'NID-900000001');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'PASS-900000002', 'PASSPORT', '2031-06-30', 'Bangladesh', '2021-06-30', 'ACTIVE', b'1', 2
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'PASSPORT' AND document_no = 'PASS-900000002');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'NID-900000003', 'NID', NULL, 'Bangladesh', '2018-03-18', 'ACTIVE', b'0', 3
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'NID' AND document_no = 'NID-900000003');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'TIN-900000004', 'TIN', NULL, 'Bangladesh', '2020-02-14', 'ACTIVE', b'1', 4
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'TIN' AND document_no = 'TIN-900000004');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'TRADE-900000005', 'TRADE_LICENSE', '2028-12-31', 'Bangladesh', '2023-01-01', 'ACTIVE', b'1', 5
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'TRADE_LICENSE' AND document_no = 'TRADE-900000005');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'DL-900000006', 'DRIVING_LICENSE', '2030-09-10', 'Bangladesh', '2020-09-10', 'ACTIVE', b'0', 6
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'DRIVING_LICENSE' AND document_no = 'DL-900000006');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'NID-900000007', 'NID', NULL, 'Bangladesh', '2017-08-22', 'ACTIVE', b'1', 7
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'NID' AND document_no = 'NID-900000007');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'PASS-900000008', 'PASSPORT', '2032-04-05', 'Bangladesh', '2022-04-05', 'ACTIVE', b'0', 8
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'PASSPORT' AND document_no = 'PASS-900000008');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'BC-900000009', 'BIRTH_CERTIFICATE', NULL, 'Bangladesh', '2007-07-07', 'ACTIVE', b'1', 9
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'BIRTH_CERTIFICATE' AND document_no = 'BC-900000009');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'NID-900000010', 'NID', NULL, 'Bangladesh', '2015-10-11', 'ACTIVE', b'1', 10
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'NID' AND document_no = 'NID-900000010');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'TIN-900000011', 'TIN', NULL, 'Bangladesh', '2019-11-15', 'ACTIVE', b'0', 11
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'TIN' AND document_no = 'TIN-900000011');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'DL-900000012', 'DRIVING_LICENSE', '2031-03-19', 'Bangladesh', '2021-03-19', 'ACTIVE', b'1', 12
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'DRIVING_LICENSE' AND document_no = 'DL-900000012');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'PASS-900000013', 'PASSPORT', '2030-01-25', 'Bangladesh', '2020-01-25', 'ACTIVE', b'1', 13
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'PASSPORT' AND document_no = 'PASS-900000013');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'NID-900000014', 'NID', NULL, 'Bangladesh', '2019-02-20', 'ACTIVE', b'1', 14
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'NID' AND document_no = 'NID-900000014');

INSERT INTO customer_identity (created_at, document_no, document_type, expiry_date, issue_country, issue_date, status, verified_flag, customer_id)
SELECT NOW(), 'TRADE-900000015', 'TRADE_LICENSE', '2029-12-31', 'Bangladesh', '2024-01-01', 'ACTIVE', b'1', 15
WHERE NOT EXISTS (SELECT 1 FROM customer_identity WHERE document_type = 'TRADE_LICENSE' AND document_no = 'TRADE-900000015');

COMMIT;
