SET NAMES utf8mb4;

START TRANSACTION;

INSERT INTO kyc_profile (
  customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag,
  review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at
)
SELECT c.id, 'HIGH', 'Legacy corporate fund declaration reviewed from onboarding documents', b'0', b'0', b'0',
       'APPROVED', 'SYSTEM_REVIEWER', NOW(), 'Legacy corporate KYC approved', 'ACTIVE', NOW(), NOW()
FROM customer c
WHERE c.id = 1
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'MEDIUM', 'Corporate operating income reviewed from source declarations', b'0', b'0', b'0',
       'VERIFIED', 'SYSTEM_REVIEWER', NOW(), 'Verified and waiting for final approval', 'ACTIVE', NOW(), NOW()
FROM customer c
WHERE c.id = 2
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'LOW', 'Trading business income declared by customer', b'0', b'0', b'0',
       'SUBMITTED', NULL, NULL, 'Submitted for KYC review', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 3
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'MEDIUM', 'Freelancing and personal income notes captured', b'0', b'0', b'0',
       'UNDER_REVIEW', 'SYSTEM_REVIEWER', NOW(), 'Reviewer is checking source documents', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 4
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'HIGH', 'Wholesale business cash flow raised additional concern', b'1', b'0', b'1',
       'REJECTED', 'SYSTEM_REVIEWER', NOW(), 'Rejected due to unresolved compliance concern', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 5
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'MEDIUM', 'Salary and household income note captured', b'0', b'0', b'0',
       'SENT_BACK', 'SYSTEM_REVIEWER', NOW(), 'Need corrected address proof before resubmission', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 6
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'LOW', 'Teacher salary verified from declaration and records', b'0', b'0', b'0',
       'APPROVED', 'SYSTEM_REVIEWER', NOW(), 'Approved after document verification', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 7
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'HIGH', 'Business turnover requires additional monitoring but basic KYC verified', b'0', b'0', b'1',
       'VERIFIED', 'SYSTEM_REVIEWER', NOW(), 'Verified and escalated for approval attention', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 8
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'MEDIUM', 'Bank salary source declared and uploaded for validation', b'0', b'0', b'0',
       'SUBMITTED', NULL, NULL, 'Pending reviewer assignment', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 9
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'LOW', 'Driving income note captured but submission not done yet', b'0', b'0', b'0',
       'DRAFT', NULL, NULL, 'Draft profile waiting for submission', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 10
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'HIGH', 'Online work income and enhanced due diligence note captured', b'0', b'0', b'1',
       'UNDER_REVIEW', 'SYSTEM_REVIEWER', NOW(), 'Enhanced due diligence in progress', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 11
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'MEDIUM', 'Government salary note validated and approved', b'0', b'0', b'0',
       'APPROVED', 'SYSTEM_REVIEWER', NOW(), 'Approved after successful KYC review', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 12
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'LOW', 'Student tuition income note could not be fully supported', b'0', b'0', b'0',
       'REJECTED', 'SYSTEM_REVIEWER', NOW(), 'Rejected because supporting document is insufficient', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 13
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'HIGH', 'Shop owner cash source requires corrected evidence', b'0', b'0', b'1',
       'SENT_BACK', 'SYSTEM_REVIEWER', NOW(), 'Return with request for updated source document', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 14
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO kyc_profile (customer_id, risk_level, source_of_funds_note, pep_flag, sanction_flag, aml_flag, review_status, reviewed_by, reviewed_at, remarks, status, created_at, updated_at)
SELECT c.id, 'LOW', 'Tailoring income submitted with initial customer declaration', b'0', b'0', b'0',
       'SUBMITTED', NULL, NULL, 'Submitted and awaiting review', 'ACTIVE', NOW(), NOW()
FROM customer c WHERE c.id = 15
  AND NOT EXISTS (SELECT 1 FROM kyc_profile p WHERE p.customer_id = c.id);

INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 1, 'NID', 'KYC-DOC-001', 'KDOC-001', '2017-01-10', NULL, b'1', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 1 AND file_reference_id = 'KYC-DOC-001');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 2, 'TRADE_LICENSE', 'KYC-DOC-002', 'KDOC-002', '2023-01-01', '2028-12-31', b'1', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 2 AND file_reference_id = 'KYC-DOC-002');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 3, 'TIN', 'KYC-DOC-003', 'KDOC-003', '2020-02-14', NULL, b'0', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 3 AND file_reference_id = 'KYC-DOC-003');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 4, 'UTILITY_BILL', 'KYC-DOC-004', 'KDOC-004', '2026-03-05', '2027-03-05', b'0', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 4 AND file_reference_id = 'KYC-DOC-004');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 5, 'BANK_STATEMENT', 'KYC-DOC-005', 'KDOC-005', '2026-01-01', '2026-12-31', b'1', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 5 AND file_reference_id = 'KYC-DOC-005');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 6, 'NID', 'KYC-DOC-006', 'KDOC-006', '2018-08-08', NULL, b'0', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 6 AND file_reference_id = 'KYC-DOC-006');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 7, 'PASSPORT', 'KYC-DOC-007', 'KDOC-007', '2022-03-15', '2032-03-15', b'1', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 7 AND file_reference_id = 'KYC-DOC-007');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 8, 'TRADE_LICENSE', 'KYC-DOC-008', 'KDOC-008', '2024-01-01', '2029-12-31', b'1', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 8 AND file_reference_id = 'KYC-DOC-008');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 9, 'NID', 'KYC-DOC-009', 'KDOC-009', '2016-09-19', NULL, b'0', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 9 AND file_reference_id = 'KYC-DOC-009');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 10, 'DRIVING_LICENSE', 'KYC-DOC-010', 'KDOC-010', '2020-04-25', '2030-04-25', b'0', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 10 AND file_reference_id = 'KYC-DOC-010');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 11, 'BANK_STATEMENT', 'KYC-DOC-011', 'KDOC-011', '2026-02-01', '2027-02-01', b'1', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 11 AND file_reference_id = 'KYC-DOC-011');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 12, 'PASSPORT', 'KYC-DOC-012', 'KDOC-012', '2021-03-19', '2031-03-19', b'1', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 12 AND file_reference_id = 'KYC-DOC-012');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 13, 'OTHER', 'KYC-DOC-013', 'KDOC-013', '2026-01-10', '2026-12-31', b'0', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 13 AND file_reference_id = 'KYC-DOC-013');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 14, 'UTILITY_BILL', 'KYC-DOC-014', 'KDOC-014', '2026-02-12', '2027-02-12', b'0', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 14 AND file_reference_id = 'KYC-DOC-014');
INSERT INTO customer_document (customer_id, document_type, file_reference_id, document_no, issue_date, expiry_date, verified_flag, status, created_at)
SELECT 15, 'NID', 'KYC-DOC-015', 'KDOC-015', '2019-02-20', NULL, b'0', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_document WHERE customer_id = 15 AND file_reference_id = 'KYC-DOC-015');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'APPROVE', 'SYSTEM_REVIEWER', NOW(), 'Legacy corporate KYC approved', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 1
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'APPROVE');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'VERIFY', 'SYSTEM_REVIEWER', NOW(), 'Verified and waiting for approval', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 2
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'VERIFY');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'SUBMIT', 'SYSTEM_REVIEWER', NOW(), 'Submitted for reviewer assignment', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 3
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'SUBMIT');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'VERIFY', 'SYSTEM_REVIEWER', NOW(), 'Review currently in progress', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 4
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'VERIFY');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'REJECT', 'SYSTEM_REVIEWER', NOW(), 'Compliance concern unresolved', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 5
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'REJECT');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'RETURN', 'SYSTEM_REVIEWER', NOW(), 'Corrected address proof required', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 6
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'RETURN');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'APPROVE', 'SYSTEM_REVIEWER', NOW(), 'Approved after successful review', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 7
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'APPROVE');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'VERIFY', 'SYSTEM_REVIEWER', NOW(), 'High-risk but verified for next step', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 8
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'VERIFY');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'SUBMIT', 'SYSTEM_REVIEWER', NOW(), 'Pending reviewer assignment', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 9
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'SUBMIT');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'SUBMIT', 'SYSTEM_REVIEWER', NOW(), 'Draft saved with initial document linkage', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 10
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'SUBMIT');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'VERIFY', 'SYSTEM_REVIEWER', NOW(), 'Enhanced due diligence underway', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 11
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'VERIFY');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'APPROVE', 'SYSTEM_REVIEWER', NOW(), 'Approved after final review', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 12
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'APPROVE');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'REJECT', 'SYSTEM_REVIEWER', NOW(), 'Supporting evidence insufficient', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 13
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'REJECT');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'RETURN', 'SYSTEM_REVIEWER', NOW(), 'Updated source document requested', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 14
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'RETURN');

INSERT INTO kyc_decision_history (kyc_profile_id, decision, decision_by, decision_at, remarks, status)
SELECT p.id, 'SUBMIT', 'SYSTEM_REVIEWER', NOW(), 'Submitted and awaiting KYC review', 'ACTIVE'
FROM kyc_profile p
WHERE p.customer_id = 15
  AND NOT EXISTS (SELECT 1 FROM kyc_decision_history h WHERE h.kyc_profile_id = p.id AND h.decision = 'SUBMIT');

UPDATE customer
SET customer_status = 'ACTIVE'
WHERE id IN (
  SELECT customer_id FROM kyc_profile WHERE review_status = 'APPROVED'
);

UPDATE customer
SET customer_status = 'REJECTED'
WHERE id IN (
  SELECT customer_id FROM kyc_profile WHERE review_status = 'REJECTED'
);

UPDATE customer
SET customer_status = 'PENDING_KYC'
WHERE id IN (
  SELECT customer_id FROM kyc_profile WHERE review_status IN ('DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'VERIFIED', 'SENT_BACK')
)
AND id NOT IN (1, 2);

COMMIT;
