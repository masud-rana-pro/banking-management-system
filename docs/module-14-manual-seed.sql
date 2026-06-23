SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE financing_schedule;
TRUNCATE TABLE financing_disbursement;
TRUNCATE TABLE financing_asset_verification;
TRUNCATE TABLE financing_application;
TRUNCATE TABLE financing_product;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO financing_product
  (id, created_at, financing_type, maximum_amount, minimum_amount, product_code, product_name, profit_rule, status, tenure_months, updated_at)
VALUES
  (1, '2026-01-02 09:00:00', 'MURABAHA', 500000.00, 50000.00, 'FNP-00001', 'Murabaha SME Asset', 'Flat 11.50% Murabaha markup with equal monthly installments', 'ACTIVE', 12, '2026-01-02 09:00:00'),
  (2, '2026-01-02 09:05:00', 'MURABAHA', 700000.00, 80000.00, 'FNP-00002', 'Murabaha Transport Support', 'Flat 12.00% Murabaha markup with monthly recovery', 'ACTIVE', 18, '2026-01-02 09:05:00'),
  (3, '2026-01-02 09:10:00', 'MURABAHA', 350000.00, 40000.00, 'FNP-00003', 'Murabaha Retail Asset', 'Flat 10.75% markup with equal schedule recovery', 'ACTIVE', 10, '2026-01-02 09:10:00'),
  (4, '2026-01-02 09:15:00', 'IJARAH', 900000.00, 150000.00, 'FNP-00004', 'Ijarah Equipment Lease', 'Lease rental equivalent to 13.25% yearly profit rate', 'ACTIVE', 24, '2026-01-02 09:15:00'),
  (5, '2026-01-02 09:20:00', 'IJARAH', 1200000.00, 250000.00, 'FNP-00005', 'Ijarah Fleet Lease', 'Lease rental equivalent to 12.80% yearly profit rate', 'ACTIVE', 30, '2026-01-02 09:20:00'),
  (6, '2026-01-02 09:25:00', 'SALAM', 450000.00, 60000.00, 'FNP-00006', 'Salam Agro Input', 'Advance commodity purchase support at 9.80% expected margin', 'ACTIVE', 9, '2026-01-02 09:25:00'),
  (7, '2026-01-02 09:30:00', 'ISTISNA', 1000000.00, 200000.00, 'FNP-00007', 'Istisna Construction Support', 'Istisna production margin 14.10% over delivery period', 'ACTIVE', 20, '2026-01-02 09:30:00'),
  (8, '2026-01-02 09:35:00', 'MUSHARAKA', 800000.00, 100000.00, 'FNP-00008', 'Musharaka Trade Expansion', 'Shared profit allocation benchmark 15.25% for monthly settlement', 'ACTIVE', 16, '2026-01-02 09:35:00'),
  (9, '2026-01-02 09:40:00', 'MUDARABA', 650000.00, 90000.00, 'FNP-00009', 'Mudaraba Working Capital', 'Mudaraba partner share benchmark 14.40% with scheduled recovery', 'ACTIVE', 14, '2026-01-02 09:40:00'),
  (10, '2026-01-02 09:45:00', 'MURABAHA', 550000.00, 75000.00, 'FNP-00010', 'Murabaha Medical Equipment', 'Flat 11.90% Murabaha markup with equal installments', 'ACTIVE', 12, '2026-01-02 09:45:00'),
  (11, '2026-01-02 09:50:00', 'MURABAHA', 300000.00, 30000.00, 'FNP-00011', 'Murabaha Education Asset', 'Student asset support at 9.50% annualized markup', 'ACTIVE', 8, '2026-01-02 09:50:00'),
  (12, '2026-01-02 09:55:00', 'IJARAH', 650000.00, 120000.00, 'FNP-00012', 'Ijarah Generator Lease', 'Lease rental equivalent to 12.35% yearly profit rate', 'ACTIVE', 18, '2026-01-02 09:55:00'),
  (13, '2026-01-02 10:00:00', 'MURABAHA', 480000.00, 55000.00, 'FNP-00013', 'Murabaha Shop Renovation', 'Flat 10.60% markup for renovation asset financing', 'ACTIVE', 11, '2026-01-02 10:00:00'),
  (14, '2026-01-02 10:05:00', 'SALAM', 720000.00, 95000.00, 'FNP-00014', 'Salam Commodity Purchase', 'Advance commodity purchase margin 10.90% with staged recovery', 'ACTIVE', 13, '2026-01-02 10:05:00'),
  (15, '2026-01-02 10:10:00', 'ISTISNA', 950000.00, 180000.00, 'FNP-00015', 'Istisna Workshop Build', 'Production support margin 13.80% with milestone recovery', 'ACTIVE', 22, '2026-01-02 10:10:00');

INSERT INTO financing_application
  (id, application_no, application_status, approved_at, approved_by, asset_description, branch_id, created_at, purpose, remarks, requested_amount, status, submitted_at, updated_at, customer_id, product_id)
VALUES
  (1, 'FNA-00001', 'SUBMITTED', NULL, NULL, 'Motorbike purchase for courier service delivery support', 1, '2026-01-08 10:00:00', 'Transport support for small delivery business', 'Documents received and waiting for verification', 250000.00, 'ACTIVE', '2026-01-10 09:20:00', '2026-01-10 09:20:00', 1, 1),
  (2, 'FNA-00002', 'ASSET_VERIFIED', NULL, NULL, 'Pickup van for district logistics support', 2, '2026-01-09 10:15:00', 'Expand local transport capacity for trading activity', 'Asset visit completed and quotation matched', 420000.00, 'ACTIVE', '2026-01-11 09:40:00', '2026-01-14 12:10:00', 2, 2),
  (3, 'FNA-00003', 'SHARIAH_REVIEW', NULL, NULL, 'Display freezer and retail shelving for grocery outlet', 1, '2026-01-10 11:00:00', 'Retail outlet asset enhancement before Ramadan peak season', 'Moved to shariah review after asset confirmation', 180000.00, 'ACTIVE', '2026-01-12 10:00:00', '2026-01-15 11:30:00', 3, 3),
  (4, 'FNA-00004', 'APPROVED', '2026-01-18 15:20:00', 'SYSTEM_REVIEWER', 'Rice milling machine on lease support', 1, '2026-01-11 09:30:00', 'Production capacity improvement for seasonal demand', 'Approved for disbursement queue', 520000.00, 'ACTIVE', '2026-01-13 09:10:00', '2026-01-18 15:20:00', 4, 4),
  (5, 'FNA-00005', 'APPROVED', '2026-01-19 16:00:00', 'SYSTEM_REVIEWER', 'Mini truck lease for supply chain movement', 2, '2026-01-12 09:45:00', 'Fleet support for corporate distribution route', 'Approved after review board confirmation', 780000.00, 'ACTIVE', '2026-01-14 10:10:00', '2026-01-19 16:00:00', 5, 5),
  (6, 'FNA-00006', 'DISBURSED', '2026-01-20 13:15:00', 'SYSTEM_REVIEWER', 'Seed, fertilizer and irrigation pump package', 1, '2026-01-13 10:05:00', 'Advance crop support before planting season', 'Disbursed and schedule generated', 210000.00, 'ACTIVE', '2026-01-15 09:30:00', '2026-01-21 10:20:00', 6, 6),
  (7, 'FNA-00007', 'ACTIVE', '2026-01-21 14:30:00', 'SYSTEM_REVIEWER', 'Brick and steel procurement for site work', 2, '2026-01-14 10:25:00', 'Construction support for client warehouse expansion', 'Repayment started and one overdue installment exists', 680000.00, 'ACTIVE', '2026-01-16 10:10:00', '2026-03-20 17:45:00', 7, 7),
  (8, 'FNA-00008', 'ACTIVE', '2026-01-22 15:05:00', 'SYSTEM_REVIEWER', 'Wholesale goods inventory for shared venture', 1, '2026-01-15 10:45:00', 'Trade inventory support for Musharaka cycle', 'Partial repayment collected and monitoring continues', 460000.00, 'ACTIVE', '2026-01-17 11:00:00', '2026-03-28 13:15:00', 8, 8),
  (9, 'FNA-00009', 'CLOSED', '2026-01-23 11:40:00', 'SYSTEM_REVIEWER', 'Working capital float for distribution partnership', 3, '2026-01-16 11:10:00', 'Short-cycle trade support with scheduled recovery', 'All installments settled and financing closed', 340000.00, 'ACTIVE', '2026-01-18 09:15:00', '2026-04-18 16:45:00', 9, 9),
  (10, 'FNA-00010', 'CLOSED', '2026-01-24 12:10:00', 'SYSTEM_REVIEWER', 'Dental unit and suction machine purchase', 2, '2026-01-17 11:35:00', 'Medical setup support for neighborhood clinic', 'Closed after full schedule settlement', 290000.00, 'ACTIVE', '2026-01-19 09:40:00', '2026-04-15 14:10:00', 10, 10),
  (11, 'FNA-00011', 'APPROVED', '2026-01-25 15:30:00', 'SYSTEM_REVIEWER', 'Laptop and projector package for training center', 1, '2026-01-18 12:00:00', 'Education support asset financing for skills center', 'Approved and awaiting customer account selection', 145000.00, 'ACTIVE', '2026-01-20 10:30:00', '2026-01-25 15:30:00', 11, 11),
  (12, 'FNA-00012', 'SUBMITTED', NULL, NULL, 'Standby generator for backup power continuity', 3, '2026-01-19 12:20:00', 'Energy backup financing for small production unit', 'Submitted and document review pending', 380000.00, 'ACTIVE', '2026-01-21 09:10:00', '2026-01-21 09:10:00', 12, 12),
  (13, 'FNA-00013', 'ASSET_VERIFIED', NULL, NULL, 'Counter redesign and shelving for retail shop', 2, '2026-01-20 12:40:00', 'Store improvement to increase customer capacity', 'Verified and waiting for review board slot', 160000.00, 'ACTIVE', '2026-01-22 09:50:00', '2026-01-24 15:00:00', 13, 13),
  (14, 'FNA-00014', 'DISBURSED', '2026-01-28 14:20:00', 'SYSTEM_REVIEWER', 'Bulk food commodity purchase ahead of seasonal demand', 1, '2026-01-21 13:05:00', 'Commodity financing support for wholesale sale cycle', 'Recently disbursed and first installment not yet due', 510000.00, 'ACTIVE', '2026-01-23 10:20:00', '2026-01-29 11:45:00', 14, 14),
  (15, 'FNA-00015', 'ACTIVE', '2026-01-30 15:00:00', 'SYSTEM_REVIEWER', 'Workshop structure and finishing materials', 3, '2026-01-22 13:25:00', 'Workshop completion financing under Istisna structure', 'Collection active with mixed repayment performance', 730000.00, 'ACTIVE', '2026-01-24 10:45:00', '2026-04-10 12:15:00', 15, 15);

INSERT INTO financing_asset_verification
  (id, asset_value, created_at, status, verification_note, verified_at, verified_by, application_id)
VALUES
  (1, 248000.00, '2026-01-14 10:10:00', 'ACTIVE', 'Quotation and market price verified with supplier visit', '2026-01-14 10:10:00', 'Verifier A', 1),
  (2, 418000.00, '2026-01-14 12:10:00', 'ACTIVE', 'Vehicle quotation, chassis details and dealer statement confirmed', '2026-01-14 12:10:00', 'Verifier B', 2),
  (3, 178000.00, '2026-01-15 11:20:00', 'ACTIVE', 'Retail asset list and supplier invoice reviewed', '2026-01-15 11:20:00', 'Verifier C', 3),
  (4, 515000.00, '2026-01-17 10:15:00', 'ACTIVE', 'Lease asset inspected at factory floor', '2026-01-17 10:15:00', 'Verifier D', 4),
  (5, 772000.00, '2026-01-18 13:05:00', 'ACTIVE', 'Fleet lease proposal and insurance quote checked', '2026-01-18 13:05:00', 'Verifier E', 5),
  (6, 208000.00, '2026-01-19 09:45:00', 'ACTIVE', 'Agro support pack verified with field officer note', '2026-01-19 09:45:00', 'Verifier F', 6),
  (7, 674000.00, '2026-01-20 10:05:00', 'ACTIVE', 'Construction materials valuation completed on-site', '2026-01-20 10:05:00', 'Verifier G', 7),
  (8, 455000.00, '2026-01-21 10:50:00', 'ACTIVE', 'Inventory valuation cross-checked against vendor bills', '2026-01-21 10:50:00', 'Verifier H', 8),
  (9, 338000.00, '2026-01-22 11:30:00', 'ACTIVE', 'Working capital need aligned with trade cycle estimation', '2026-01-22 11:30:00', 'Verifier I', 9),
  (10, 287000.00, '2026-01-23 12:00:00', 'ACTIVE', 'Medical equipment invoice and installation letter confirmed', '2026-01-23 12:00:00', 'Verifier J', 10),
  (11, 142000.00, '2026-01-24 10:25:00', 'ACTIVE', 'Training center equipment list verified', '2026-01-24 10:25:00', 'Verifier K', 11),
  (12, 375000.00, '2026-01-24 16:40:00', 'ACTIVE', 'Generator quotation and service warranty reviewed', '2026-01-24 16:40:00', 'Verifier L', 12),
  (13, 157000.00, '2026-01-25 11:45:00', 'ACTIVE', 'Renovation BOQ and contractor note validated', '2026-01-25 11:45:00', 'Verifier M', 13),
  (14, 505000.00, '2026-01-27 10:00:00', 'ACTIVE', 'Commodity purchase estimate verified with supplier ledger', '2026-01-27 10:00:00', 'Verifier N', 14),
  (15, 724000.00, '2026-01-28 09:20:00', 'ACTIVE', 'Workshop development budget and material list checked', '2026-01-28 09:20:00', 'Verifier O', 15);

INSERT INTO financing_disbursement
  (id, created_at, credited_account_id, disbursed_amount, disbursed_by, disbursement_date, disbursement_no, status, application_id)
VALUES
  (1, '2026-01-21 10:20:00', 6, 210000.00, 'Disburser A', '2026-01-21', 'FND-00001', 'ACTIVE', 6),
  (2, '2026-01-22 11:10:00', 7, 680000.00, 'Disburser B', '2026-01-22', 'FND-00002', 'ACTIVE', 7),
  (3, '2026-01-23 11:40:00', 8, 460000.00, 'Disburser C', '2026-01-23', 'FND-00003', 'ACTIVE', 8),
  (4, '2026-01-24 12:15:00', 9, 340000.00, 'Disburser D', '2026-01-24', 'FND-00004', 'ACTIVE', 9),
  (5, '2026-01-25 13:00:00', 10, 290000.00, 'Disburser E', '2026-01-25', 'FND-00005', 'ACTIVE', 10),
  (6, '2026-01-29 11:45:00', 14, 510000.00, 'Disburser F', '2026-01-29', 'FND-00006', 'ACTIVE', 14),
  (7, '2026-01-31 12:15:00', 15, 730000.00, 'Disburser G', '2026-01-31', 'FND-00007', 'ACTIVE', 15);

INSERT INTO financing_schedule
  (id, charity_amount, created_at, due_date, installment_no, paid_amount, paid_date, principal_amount, profit_amount, schedule_status, application_id)
VALUES
  (1, 0.00, '2026-01-21 10:25:00', '2026-02-21', 1, 0.00, NULL, 70000.00, 7000.00, 'PENDING', 6),
  (2, 0.00, '2026-01-21 10:25:00', '2026-03-21', 2, 0.00, NULL, 70000.00, 7000.00, 'PENDING', 6),
  (3, 0.00, '2026-01-21 10:25:00', '2026-04-21', 3, 0.00, NULL, 70000.00, 7000.00, 'PENDING', 6),
  (4, 0.00, '2026-01-21 10:25:00', '2026-05-21', 4, 0.00, NULL, 70000.00, 7000.00, 'PENDING', 6),

  (5, 0.00, '2026-01-22 11:15:00', '2026-02-22', 1, 180000.00, '2026-02-22', 170000.00, 10000.00, 'PAID', 7),
  (6, 0.00, '2026-01-22 11:15:00', '2026-03-22', 2, 100000.00, '2026-03-25', 170000.00, 10000.00, 'PARTIAL', 7),
  (7, 1700.00, '2026-01-22 11:15:00', '2026-04-22', 3, 0.00, NULL, 170000.00, 10000.00, 'OVERDUE', 7),
  (8, 0.00, '2026-01-22 11:15:00', '2026-05-22', 4, 0.00, NULL, 170000.00, 10000.00, 'PENDING', 7),

  (9, 0.00, '2026-01-23 11:45:00', '2026-02-23', 1, 120000.00, '2026-02-23', 115000.00, 5000.00, 'PAID', 8),
  (10, 0.00, '2026-01-23 11:45:00', '2026-03-23', 2, 60000.00, '2026-03-24', 115000.00, 5000.00, 'PARTIAL', 8),
  (11, 1150.00, '2026-01-23 11:45:00', '2026-04-23', 3, 0.00, NULL, 115000.00, 5000.00, 'OVERDUE', 8),
  (12, 0.00, '2026-01-23 11:45:00', '2026-05-23', 4, 0.00, NULL, 115000.00, 5000.00, 'PENDING', 8),

  (13, 0.00, '2026-01-24 12:20:00', '2026-02-24', 1, 90000.00, '2026-02-24', 85000.00, 5000.00, 'PAID', 9),
  (14, 0.00, '2026-01-24 12:20:00', '2026-03-24', 2, 90000.00, '2026-03-24', 85000.00, 5000.00, 'PAID', 9),
  (15, 0.00, '2026-01-24 12:20:00', '2026-04-24', 3, 90000.00, '2026-04-20', 85000.00, 5000.00, 'PAID', 9),
  (16, 0.00, '2026-01-24 12:20:00', '2026-05-24', 4, 90000.00, '2026-05-10', 85000.00, 5000.00, 'PAID', 9),

  (17, 0.00, '2026-01-25 13:05:00', '2026-02-25', 1, 76000.00, '2026-02-25', 72000.00, 4000.00, 'PAID', 10),
  (18, 0.00, '2026-01-25 13:05:00', '2026-03-25', 2, 76000.00, '2026-03-25', 72000.00, 4000.00, 'PAID', 10),
  (19, 0.00, '2026-01-25 13:05:00', '2026-04-25', 3, 76000.00, '2026-04-21', 72000.00, 4000.00, 'PAID', 10),
  (20, 0.00, '2026-01-25 13:05:00', '2026-05-25', 4, 76000.00, '2026-05-12', 72000.00, 4000.00, 'PAID', 10),

  (21, 0.00, '2026-01-29 11:50:00', '2026-02-28', 1, 0.00, NULL, 127500.00, 9500.00, 'PENDING', 14),
  (22, 0.00, '2026-01-29 11:50:00', '2026-03-28', 2, 0.00, NULL, 127500.00, 9500.00, 'PENDING', 14),
  (23, 0.00, '2026-01-29 11:50:00', '2026-04-28', 3, 0.00, NULL, 127500.00, 9500.00, 'PENDING', 14),
  (24, 0.00, '2026-01-29 11:50:00', '2026-05-28', 4, 0.00, NULL, 127500.00, 9500.00, 'PENDING', 14),

  (25, 0.00, '2026-01-31 12:20:00', '2026-02-28', 1, 190000.00, '2026-02-28', 182500.00, 7500.00, 'PAID', 15),
  (26, 0.00, '2026-01-31 12:20:00', '2026-03-31', 2, 90000.00, '2026-03-31', 182500.00, 7500.00, 'PARTIAL', 15),
  (27, 1825.00, '2026-01-31 12:20:00', '2026-04-30', 3, 0.00, NULL, 182500.00, 7500.00, 'OVERDUE', 15),
  (28, 0.00, '2026-01-31 12:20:00', '2026-05-31', 4, 0.00, NULL, 182500.00, 7500.00, 'PENDING', 15);
