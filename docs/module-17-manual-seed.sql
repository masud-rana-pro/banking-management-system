SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE charity_payout;
TRUNCATE TABLE charity_fund;
TRUNCATE TABLE zakat_profile;
TRUNCATE TABLE charity_beneficiary;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO zakat_profile
  (id, calculation_status, created_at, eligible_asset_amount, nisab_amount, remarks, updated_at, zakat_amount, zakat_year, customer_id)
VALUES
  (1, 'DEDUCTED', '2026-04-01 09:00:00.000000', 540000.00, 85000.00, 'Primary zakat profile settled through yearly deduction workflow.', '2026-04-10 11:30:00.000000', 13500.00, 2025, 1),
  (2, 'DEDUCTED', '2026-04-01 09:15:00.000000', 450000.00, 85000.00, 'Business reserve adjusted and zakat routed to charity fund.', '2026-04-10 11:45:00.000000', 11250.00, 2025, 2),
  (3, 'BELOW_NISAB', '2026-04-01 09:30:00.000000', 72000.00, 85000.00, 'Retail savings remained below nisab threshold for the year.', '2026-04-10 12:00:00.000000', 0.00, 2025, 3),
  (4, 'BELOW_NISAB', '2026-04-01 09:45:00.000000', 81000.00, 90000.00, 'Personal assets reviewed and kept for reminder tracking only.', '2026-04-10 12:15:00.000000', 0.00, 2026, 4),
  (5, 'DEDUCTED', '2026-04-01 10:00:00.000000', 650000.00, 85000.00, 'Corporate zakat amount deducted after asset review.', '2026-04-10 12:30:00.000000', 16250.00, 2025, 5),
  (6, 'CALCULATED', '2026-04-01 10:15:00.000000', 390000.00, 90000.00, 'Calculated and waiting finance confirmation before deduction.', '2026-04-10 12:45:00.000000', 9750.00, 2026, 6),
  (7, 'DEDUCTED', '2026-04-01 10:30:00.000000', 390000.00, 85000.00, 'Salary and deposit mix cleared for deduction posting.', '2026-04-10 13:00:00.000000', 9750.00, 2025, 7),
  (8, 'PROFILED', '2026-04-01 10:45:00.000000', 580000.00, 90000.00, 'Asset profile opened and waiting calculation run by officer.', '2026-04-10 13:15:00.000000', 0.00, 2026, 8),
  (9, 'DEDUCTED', '2026-04-01 11:00:00.000000', 575000.00, 90000.00, 'Customer requested immediate deduction after yearly review.', '2026-04-10 13:30:00.000000', 14375.00, 2025, 9),
  (10, 'CALCULATED', '2026-04-01 11:15:00.000000', 440000.00, 90000.00, 'Calculated amount waiting customer approval for deduction.', '2026-04-10 13:45:00.000000', 11000.00, 2026, 10),
  (11, 'DEDUCTED', '2026-04-01 11:30:00.000000', 500000.00, 90000.00, 'Trade inventory exposure converted into posted zakat amount.', '2026-04-10 14:00:00.000000', 12500.00, 2025, 11),
  (12, 'PROFILED', '2026-04-01 11:45:00.000000', 315000.00, 90000.00, 'New profile captured and waiting verified eligible asset review.', '2026-04-10 14:15:00.000000', 0.00, 2026, 12),
  (13, 'DEDUCTED', '2026-04-01 12:00:00.000000', 425000.00, 90000.00, 'Family zakat pool cleared and routed to charity fund.', '2026-04-10 14:30:00.000000', 10625.00, 2025, 13),
  (14, 'DEDUCTED', '2026-04-01 12:15:00.000000', 620000.00, 90000.00, 'High balance assets reviewed and deducted for current cycle.', '2026-04-10 14:45:00.000000', 15500.00, 2026, 14),
  (15, 'DEDUCTED', '2026-04-01 12:30:00.000000', 350000.00, 90000.00, 'Community savings portfolio settled through yearly zakat process.', '2026-04-10 15:00:00.000000', 8750.00, 2025, 15);

INSERT INTO charity_beneficiary
  (id, address, beneficiary_code, beneficiary_name, created_at, mobile, status, updated_at)
VALUES
  (1, 'Badda, Dhaka', 'BEN-00001', 'Ayesha Welfare Family', '2026-04-02 09:00:00.000000', '01711000001', 'ACTIVE', '2026-04-02 09:00:00.000000'),
  (2, 'Mirpur, Dhaka', 'BEN-00002', 'Rahman Community Clinic', '2026-04-02 09:05:00.000000', '01711000002', 'ACTIVE', '2026-04-02 09:05:00.000000'),
  (3, 'Jatrabari, Dhaka', 'BEN-00003', 'Noor Shelter Support', '2026-04-02 09:10:00.000000', '01711000003', 'ACTIVE', '2026-04-02 09:10:00.000000'),
  (4, 'Keraniganj, Dhaka', 'BEN-00004', 'Alor Path Student Aid', '2026-04-02 09:15:00.000000', '01711000004', 'ACTIVE', '2026-04-02 09:15:00.000000'),
  (5, 'Savar, Dhaka', 'BEN-00005', 'Mizan Elder Support', '2026-04-02 09:20:00.000000', '01711000005', 'ACTIVE', '2026-04-02 09:20:00.000000'),
  (6, 'Gazipur Sadar, Gazipur', 'BEN-00006', 'Green Village Relief', '2026-04-02 09:25:00.000000', '01711000006', 'ACTIVE', '2026-04-02 09:25:00.000000'),
  (7, 'Narayanganj', 'BEN-00007', 'Safa Medical Outreach', '2026-04-02 09:30:00.000000', '01711000007', 'ACTIVE', '2026-04-02 09:30:00.000000'),
  (8, 'Uttara, Dhaka', 'BEN-00008', 'Iqra Women Support Cell', '2026-04-02 09:35:00.000000', '01711000008', 'ACTIVE', '2026-04-02 09:35:00.000000'),
  (9, 'Cumilla Town', 'BEN-00009', 'Tawhid Rural Care', '2026-04-02 09:40:00.000000', '01711000009', 'ACTIVE', '2026-04-02 09:40:00.000000'),
  (10, 'Mymensingh', 'BEN-00010', 'Baraka Widow Support', '2026-04-02 09:45:00.000000', '01711000010', 'ACTIVE', '2026-04-02 09:45:00.000000'),
  (11, 'Narsingdi', 'BEN-00011', 'Madrasa Meal Program', '2026-04-02 09:50:00.000000', '01711000011', 'ACTIVE', '2026-04-02 09:50:00.000000'),
  (12, 'Tangail', 'BEN-00012', 'Sadaqah Care Trust', '2026-04-02 09:55:00.000000', '01711000012', 'ACTIVE', '2026-04-02 09:55:00.000000'),
  (13, 'Kishoreganj', 'BEN-00013', 'Nobojibon Health Link', '2026-04-02 10:00:00.000000', '01711000013', 'ACTIVE', '2026-04-02 10:00:00.000000'),
  (14, 'Manikganj', 'BEN-00014', 'Ar Rahmah Winter Drive', '2026-04-02 10:05:00.000000', '01711000014', 'ARCHIVED', '2026-04-10 15:10:00.000000'),
  (15, 'Munshiganj', 'BEN-00015', 'Community Skill Uplift', '2026-04-02 10:10:00.000000', '01711000015', 'ARCHIVED', '2026-04-10 15:15:00.000000');

INSERT INTO charity_payout
  (id, amount, approved_by, created_at, payout_date, remarks, status, beneficiary_id)
VALUES
  (1, 3500.00, 'Zakat Officer 01', '2026-04-12 10:00:00.000000', '2026-04-12', 'Monthly family food assistance disbursed from zakat fund.', 'ACTIVE', 1),
  (2, 4200.00, 'Zakat Officer 02', '2026-04-12 10:15:00.000000', '2026-04-12', 'Medical support released for clinic treatment inventory.', 'ACTIVE', 2),
  (3, 5000.00, 'Zakat Officer 03', '2026-04-12 10:30:00.000000', '2026-04-12', 'Emergency housing and shelter payout approved.', 'ACTIVE', 3),
  (4, 2750.00, 'Zakat Officer 04', '2026-04-12 10:45:00.000000', '2026-04-12', 'Student educational materials distributed to verified list.', 'ACTIVE', 4),
  (5, 6400.00, 'Zakat Officer 05', '2026-04-12 11:00:00.000000', '2026-04-12', 'Senior citizen support cycle released for medicine coverage.', 'ACTIVE', 5),
  (6, 7200.00, 'Zakat Officer 06', '2026-04-12 11:15:00.000000', '2026-04-12', 'Village relief pack and cash assistance disbursed.', 'ACTIVE', 6),
  (7, 3800.00, 'Zakat Officer 07', '2026-04-12 11:30:00.000000', '2026-04-12', 'Rural medical camp logistics funded from charity pool.', 'ACTIVE', 7),
  (8, 4550.00, 'Zakat Officer 08', '2026-04-12 11:45:00.000000', '2026-04-12', 'Women support allowance approved after branch verification.', 'ACTIVE', 8),
  (9, 5100.00, 'Zakat Officer 09', '2026-04-12 12:00:00.000000', '2026-04-12', 'Rural hardship payout delivered to field coordinator.', 'ACTIVE', 9),
  (10, 6200.00, 'Zakat Officer 10', '2026-04-12 12:15:00.000000', '2026-04-12', 'Widow household assistance released for quarter one.', 'ACTIVE', 10),
  (11, 7400.00, 'Zakat Officer 11', '2026-04-12 12:30:00.000000', '2026-04-12', 'Madrasa meal support funded for one monthly cycle.', 'ACTIVE', 11),
  (12, 2900.00, 'Zakat Officer 12', '2026-04-12 12:45:00.000000', '2026-04-12', 'Small grant approved for immediate social care need.', 'ACTIVE', 12),
  (13, 3350.00, 'Zakat Officer 13', '2026-04-12 13:00:00.000000', '2026-04-12', 'Healthcare mobility support released after approval.', 'ACTIVE', 13),
  (14, 4800.00, 'Zakat Officer 14', '2026-04-12 13:15:00.000000', '2026-04-12', 'Follow-up food and shelter support provided to family case.', 'ACTIVE', 1),
  (15, 5600.00, 'Zakat Officer 15', '2026-04-12 13:30:00.000000', '2026-04-12', 'Additional treatment support released for clinic beneficiary.', 'ACTIVE', 2);

INSERT INTO charity_fund
  (id, balance_after, created_at, credit_amount, debit_amount, fund_date, reference_id, remarks, source_type)
VALUES
  (1, 13500.00, '2026-04-10 11:30:00.000000', 13500.00, 0.00, '2026-04-10', 1, 'Zakat deduction for CUST001 year 2025', 'ZAKAT_DEDUCTION'),
  (2, 24750.00, '2026-04-10 11:45:00.000000', 11250.00, 0.00, '2026-04-10', 2, 'Zakat deduction for CUST002 year 2025', 'ZAKAT_DEDUCTION'),
  (3, 41000.00, '2026-04-10 12:30:00.000000', 16250.00, 0.00, '2026-04-10', 5, 'Zakat deduction for CUS-000003 year 2025', 'ZAKAT_DEDUCTION'),
  (4, 50750.00, '2026-04-10 13:00:00.000000', 9750.00, 0.00, '2026-04-10', 7, 'Zakat deduction for CUS-000005 year 2025', 'ZAKAT_DEDUCTION'),
  (5, 65125.00, '2026-04-10 13:30:00.000000', 14375.00, 0.00, '2026-04-10', 9, 'Zakat deduction for CUS-000007 year 2025', 'ZAKAT_DEDUCTION'),
  (6, 77625.00, '2026-04-10 14:00:00.000000', 12500.00, 0.00, '2026-04-10', 11, 'Zakat deduction for CUS-000009 year 2025', 'ZAKAT_DEDUCTION'),
  (7, 88250.00, '2026-04-10 14:30:00.000000', 10625.00, 0.00, '2026-04-10', 13, 'Zakat deduction for CUS-000011 year 2025', 'ZAKAT_DEDUCTION'),
  (8, 103750.00, '2026-04-10 14:45:00.000000', 15500.00, 0.00, '2026-04-10', 14, 'Zakat deduction for CUS-000012 year 2026', 'ZAKAT_DEDUCTION'),
  (9, 112500.00, '2026-04-10 15:00:00.000000', 8750.00, 0.00, '2026-04-10', 15, 'Zakat deduction for CUS-000013 year 2025', 'ZAKAT_DEDUCTION'),
  (10, 132500.00, '2026-04-11 09:00:00.000000', 20000.00, 0.00, '2026-04-11', NULL, 'Voluntary sadaqah donation transferred into charity fund.', 'DONATION'),
  (11, 147500.00, '2026-04-11 09:15:00.000000', 15000.00, 0.00, '2026-04-11', NULL, 'Board approved community donation credited for payout pool.', 'DONATION'),
  (12, 150750.00, '2026-04-11 09:30:00.000000', 3250.00, 0.00, '2026-04-11', 101, 'Late fee rerouted to charity fund per Shariah policy.', 'LATE_FEE'),
  (13, 168750.00, '2026-04-11 09:45:00.000000', 18000.00, 0.00, '2026-04-11', NULL, 'Special Ramadan donation credited by head office.', 'DONATION'),
  (14, 170850.00, '2026-04-11 10:00:00.000000', 2100.00, 0.00, '2026-04-11', 102, 'Penalty recovery routed to charity only.', 'LATE_FEE'),
  (15, 179850.00, '2026-04-11 10:15:00.000000', 9000.00, 0.00, '2026-04-11', NULL, 'Charity top-up received from branch welfare drive.', 'DONATION'),
  (16, 176350.00, '2026-04-12 10:00:00.000000', 0.00, 3500.00, '2026-04-12', 1, 'Charity payout to BEN-00001 - Ayesha Welfare Family', 'PAYOUT'),
  (17, 172150.00, '2026-04-12 10:15:00.000000', 0.00, 4200.00, '2026-04-12', 2, 'Charity payout to BEN-00002 - Rahman Community Clinic', 'PAYOUT'),
  (18, 167150.00, '2026-04-12 10:30:00.000000', 0.00, 5000.00, '2026-04-12', 3, 'Charity payout to BEN-00003 - Noor Shelter Support', 'PAYOUT'),
  (19, 164400.00, '2026-04-12 10:45:00.000000', 0.00, 2750.00, '2026-04-12', 4, 'Charity payout to BEN-00004 - Alor Path Student Aid', 'PAYOUT'),
  (20, 158000.00, '2026-04-12 11:00:00.000000', 0.00, 6400.00, '2026-04-12', 5, 'Charity payout to BEN-00005 - Mizan Elder Support', 'PAYOUT'),
  (21, 150800.00, '2026-04-12 11:15:00.000000', 0.00, 7200.00, '2026-04-12', 6, 'Charity payout to BEN-00006 - Green Village Relief', 'PAYOUT'),
  (22, 147000.00, '2026-04-12 11:30:00.000000', 0.00, 3800.00, '2026-04-12', 7, 'Charity payout to BEN-00007 - Safa Medical Outreach', 'PAYOUT'),
  (23, 142450.00, '2026-04-12 11:45:00.000000', 0.00, 4550.00, '2026-04-12', 8, 'Charity payout to BEN-00008 - Iqra Women Support Cell', 'PAYOUT'),
  (24, 137350.00, '2026-04-12 12:00:00.000000', 0.00, 5100.00, '2026-04-12', 9, 'Charity payout to BEN-00009 - Tawhid Rural Care', 'PAYOUT'),
  (25, 131150.00, '2026-04-12 12:15:00.000000', 0.00, 6200.00, '2026-04-12', 10, 'Charity payout to BEN-00010 - Baraka Widow Support', 'PAYOUT'),
  (26, 123750.00, '2026-04-12 12:30:00.000000', 0.00, 7400.00, '2026-04-12', 11, 'Charity payout to BEN-00011 - Madrasa Meal Program', 'PAYOUT'),
  (27, 120850.00, '2026-04-12 12:45:00.000000', 0.00, 2900.00, '2026-04-12', 12, 'Charity payout to BEN-00012 - Sadaqah Care Trust', 'PAYOUT'),
  (28, 117500.00, '2026-04-12 13:00:00.000000', 0.00, 3350.00, '2026-04-12', 13, 'Charity payout to BEN-00013 - Nobojibon Health Link', 'PAYOUT'),
  (29, 112700.00, '2026-04-12 13:15:00.000000', 0.00, 4800.00, '2026-04-12', 14, 'Charity payout to BEN-00001 - Ayesha Welfare Family', 'PAYOUT'),
  (30, 107100.00, '2026-04-12 13:30:00.000000', 0.00, 5600.00, '2026-04-12', 15, 'Charity payout to BEN-00002 - Rahman Community Clinic', 'PAYOUT');
