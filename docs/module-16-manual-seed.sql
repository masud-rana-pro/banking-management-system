SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE shariah_review_checklist;
TRUNCATE TABLE shariah_review_decision;
TRUNCATE TABLE shariah_review_case;
TRUNCATE TABLE shariah_checklist_item;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO shariah_checklist_item
  (id, created_at, description, item_code, item_name, status, updated_at)
VALUES
  (1, '2026-04-01 09:00:00', 'Review whether the business purpose aligns with approved Shariah banking objectives.', 'SCI-00001', 'Purpose Alignment Review', 'ACTIVE', '2026-04-01 09:00:00'),
  (2, '2026-04-01 09:05:00', 'Ensure the financed or linked asset is halal, lawful and supported by source evidence.', 'SCI-00002', 'Asset Halal Validation', 'ACTIVE', '2026-04-01 09:05:00'),
  (3, '2026-04-01 09:10:00', 'Check whether the contract clauses follow approved Islamic structure and wording.', 'SCI-00003', 'Contract Clause Compliance', 'ACTIVE', '2026-04-01 09:10:00'),
  (4, '2026-04-01 09:15:00', 'Review markup, rental, sharing or profit rule against approved product guidance.', 'SCI-00004', 'Profit Method Review', 'ACTIVE', '2026-04-01 09:15:00'),
  (5, '2026-04-01 09:20:00', 'Confirm document completeness before final board decision is recorded.', 'SCI-00005', 'Documentation Completeness', 'ACTIVE', '2026-04-01 09:20:00'),
  (6, '2026-04-01 09:25:00', 'Check collateral or supporting security for prohibited elements or wording.', 'SCI-00006', 'Collateral Compliance', 'ACTIVE', '2026-04-01 09:25:00'),
  (7, '2026-04-01 09:30:00', 'Verify charity late fee wording and disclosure are correctly applied.', 'SCI-00007', 'Charity Penalty Disclosure', 'ACTIVE', '2026-04-01 09:30:00'),
  (8, '2026-04-01 09:35:00', 'Confirm customer declaration and disclosures are visible and traceable.', 'SCI-00008', 'Customer Disclosure Review', 'ACTIVE', '2026-04-01 09:35:00'),
  (9, '2026-04-01 09:40:00', 'Check ownership and possession evidence where required before financing execution.', 'SCI-00009', 'Ownership Evidence Check', 'ACTIVE', '2026-04-01 09:40:00'),
  (10, '2026-04-01 09:45:00', 'Validate supplier or vendor document support for linked purchase transaction.', 'SCI-00010', 'Supplier Validation', 'ACTIVE', '2026-04-01 09:45:00'),
  (11, '2026-04-01 09:50:00', 'Review risk mitigation controls and approval note before case closure.', 'SCI-00011', 'Risk Mitigation Review', 'ACTIVE', '2026-04-01 09:50:00'),
  (12, '2026-04-01 09:55:00', 'Ensure takaful or protection note is compliant where the product requires it.', 'SCI-00012', 'Takaful Compliance Note', 'ACTIVE', '2026-04-01 09:55:00'),
  (13, '2026-04-01 10:00:00', 'Check early settlement wording and rebate note are fair and compliant.', 'SCI-00013', 'Early Settlement Clause', 'ACTIVE', '2026-04-01 10:00:00'),
  (14, '2026-04-01 10:05:00', 'Verify late payment treatment routes charity amount outside bank income.', 'SCI-00014', 'Late Fee Charity Routing', 'ACTIVE', '2026-04-01 10:05:00'),
  (15, '2026-04-01 10:10:00', 'Ensure final board note and certificate language are ready for archive and report use.', 'SCI-00015', 'Board Note Completion', 'ACTIVE', '2026-04-01 10:10:00');

INSERT INTO shariah_review_case
  (id, case_no, case_status, created_at, reference_id, reference_module, remarks, status, submitted_at, submitted_by, updated_at)
VALUES
  (1, 'SHR-00001', 'PENDING_REVIEW', '2026-04-02 09:00:00', 1, 'FINANCING', 'Murabaha transport case submitted and waiting for full board checklist completion.', 'ACTIVE', '2026-04-02 09:00:00', 'Review Officer 01', '2026-04-02 09:00:00'),
  (2, 'SHR-00002', 'APPROVED', '2026-04-02 09:15:00', 2, 'FINANCING', 'Board approved after confirming vehicle asset, pricing and ownership evidence.', 'ACTIVE', '2026-04-02 09:15:00', 'Review Officer 02', '2026-04-03 11:00:00'),
  (3, 'SHR-00003', 'REJECTED', '2026-04-02 09:30:00', 3, 'FINANCING', 'Rejected because asset quotation source and supporting document trail were inconsistent.', 'ACTIVE', '2026-04-02 09:30:00', 'Review Officer 03', '2026-04-03 11:20:00'),
  (4, 'SHR-00004', 'RETURNED', '2026-04-02 09:45:00', 4, 'FINANCING', 'Returned for correction to add customer disclosure and revised charity clause wording.', 'ACTIVE', '2026-04-02 09:45:00', 'Review Officer 04', '2026-04-03 11:40:00'),
  (5, 'SHR-00005', 'APPROVED', '2026-04-02 10:00:00', 5, 'CONTRACT', 'Contract wording cleared and certificate can be generated for archive.', 'ACTIVE', '2026-04-02 10:00:00', 'Review Officer 05', '2026-04-03 12:00:00'),
  (6, 'SHR-00006', 'PENDING_REVIEW', '2026-04-02 10:15:00', 6, 'CONTRACT', 'Checklist updated and board note still pending before decision.', 'ACTIVE', '2026-04-02 10:15:00', 'Review Officer 06', '2026-04-03 12:15:00'),
  (7, 'SHR-00007', 'APPROVED', '2026-04-02 10:30:00', 7, 'FINANCING', 'Approved after collateral and supplier validation completed.', 'ACTIVE', '2026-04-02 10:30:00', 'Review Officer 07', '2026-04-03 12:30:00'),
  (8, 'SHR-00008', 'RETURNED', '2026-04-02 10:45:00', 8, 'CONTRACT', 'Returned for correction because the template omitted updated board note language.', 'ACTIVE', '2026-04-02 10:45:00', 'Review Officer 08', '2026-04-03 12:45:00'),
  (9, 'SHR-00009', 'APPROVED', '2026-04-02 11:00:00', 1, 'DEPOSIT_SCHEME', 'Savings scheme case approved after disclosure and charity clause check.', 'ACTIVE', '2026-04-02 11:00:00', 'Review Officer 09', '2026-04-03 13:00:00'),
  (10, 'SHR-00010', 'REJECTED', '2026-04-02 11:15:00', 10, 'FINANCING', 'Rejected due to unsupported supplier invoice and incomplete ownership note.', 'ACTIVE', '2026-04-02 11:15:00', 'Review Officer 10', '2026-04-03 13:15:00'),
  (11, 'SHR-00011', 'PENDING_REVIEW', '2026-04-02 11:30:00', 11, 'CONTRACT', 'Customer and contract documents loaded, waiting for board review slot.', 'ACTIVE', '2026-04-02 11:30:00', 'Review Officer 11', '2026-04-02 11:30:00'),
  (12, 'SHR-00012', 'APPROVED', '2026-04-02 11:45:00', 1, 'CARD_ISSUE', 'Card issue undertaking approved with updated disclosure wording.', 'ACTIVE', '2026-04-02 11:45:00', 'Review Officer 12', '2026-04-03 13:45:00'),
  (13, 'SHR-00013', 'RETURNED', '2026-04-02 12:00:00', 13, 'ACCOUNT_OPENING', 'Returned to collect revised early settlement clause note and customer declaration.', 'ACTIVE', '2026-04-02 12:00:00', 'Review Officer 13', '2026-04-03 14:00:00'),
  (14, 'SHR-00014', 'REJECTED', '2026-04-02 12:15:00', 14, 'FINANCING', 'Rejected because financing purpose narrative was not aligned with approved structure.', 'ACTIVE', '2026-04-02 12:15:00', 'Review Officer 14', '2026-04-03 14:15:00'),
  (15, 'SHR-00015', 'PENDING_REVIEW', '2026-04-02 12:30:00', 15, 'GENERAL', 'General advisory case submitted for board comment and annual report inclusion.', 'ACTIVE', '2026-04-02 12:30:00', 'Review Officer 15', '2026-04-02 12:30:00');

INSERT INTO shariah_review_decision
  (id, created_at, decision, decision_at, decision_by, remarks, status, case_id)
VALUES
  (1, '2026-04-02 09:00:00', 'SUBMITTED', '2026-04-02 09:00:00', 'Review Officer 01', 'Case submitted to Shariah board queue.', 'ACTIVE', 1),
  (2, '2026-04-03 11:00:00', 'APPROVED', '2026-04-03 11:00:00', 'Board Member A', 'Approved after full transport asset and purpose validation.', 'ACTIVE', 2),
  (3, '2026-04-03 11:20:00', 'REJECTED', '2026-04-03 11:20:00', 'Board Member B', 'Rejected due to inconsistent quotation trail.', 'ACTIVE', 3),
  (4, '2026-04-03 11:40:00', 'RETURNED', '2026-04-03 11:40:00', 'Board Member C', 'Returned for revised disclosure wording and correction note.', 'ACTIVE', 4),
  (5, '2026-04-03 12:00:00', 'APPROVED', '2026-04-03 12:00:00', 'Board Member D', 'Approved and certificate language is ready.', 'ACTIVE', 5),
  (6, '2026-04-03 12:15:00', 'CHECKLIST_UPDATED', '2026-04-03 12:15:00', 'Board Member E', 'Checklist reviewed and waiting final meeting note.', 'ACTIVE', 6),
  (7, '2026-04-03 12:30:00', 'APPROVED', '2026-04-03 12:30:00', 'Board Member F', 'Approved after collateral and supplier validation.', 'ACTIVE', 7),
  (8, '2026-04-03 12:45:00', 'RETURNED', '2026-04-03 12:45:00', 'Board Member G', 'Returned to update contract template note.', 'ACTIVE', 8),
  (9, '2026-04-03 13:00:00', 'APPROVED', '2026-04-03 13:00:00', 'Board Member H', 'Approved for savings scheme execution.', 'ACTIVE', 9),
  (10, '2026-04-03 13:15:00', 'REJECTED', '2026-04-03 13:15:00', 'Board Member I', 'Rejected because supplier evidence remained incomplete.', 'ACTIVE', 10),
  (11, '2026-04-02 11:30:00', 'SUBMITTED', '2026-04-02 11:30:00', 'Review Officer 11', 'Case submitted and waiting board agenda.', 'ACTIVE', 11),
  (12, '2026-04-03 13:45:00', 'APPROVED', '2026-04-03 13:45:00', 'Board Member J', 'Approved with compliant disclosure and certificate note.', 'ACTIVE', 12),
  (13, '2026-04-03 14:00:00', 'RETURNED', '2026-04-03 14:00:00', 'Board Member K', 'Returned to collect revised customer declaration.', 'ACTIVE', 13),
  (14, '2026-04-03 14:15:00', 'REJECTED', '2026-04-03 14:15:00', 'Board Member L', 'Rejected because purpose wording did not align with approved structure.', 'ACTIVE', 14),
  (15, '2026-04-02 12:30:00', 'SUBMITTED', '2026-04-02 12:30:00', 'Review Officer 15', 'General case submitted for board comment.', 'ACTIVE', 15);

INSERT INTO shariah_review_checklist
  (id, created_at, note, selected_flag, status, updated_at, item_id, case_id)
VALUES
  (1, '2026-04-02 09:05:00', 'Purpose note aligned with approved Murabaha transport activity.', b'1', 'ACTIVE', '2026-04-02 09:05:00', 1, 1),
  (2, '2026-04-02 09:06:00', 'Asset description verified against transport quotation.', b'1', 'ACTIVE', '2026-04-02 09:06:00', 2, 1),
  (3, '2026-04-02 09:07:00', 'Document set complete before final board sitting.', b'1', 'ACTIVE', '2026-04-02 09:07:00', 5, 1),
  (4, '2026-04-02 09:20:00', 'Purpose and transport use case align with policy.', b'1', 'ACTIVE', '2026-04-02 09:20:00', 1, 2),
  (5, '2026-04-02 09:21:00', 'Ownership evidence and chassis quotation matched.', b'1', 'ACTIVE', '2026-04-02 09:21:00', 9, 2),
  (6, '2026-04-02 09:22:00', 'Supplier records validated before approval.', b'1', 'ACTIVE', '2026-04-02 09:22:00', 10, 2),
  (7, '2026-04-02 09:35:00', 'Purpose acceptable but quotation trail was weak.', b'1', 'ACTIVE', '2026-04-02 09:35:00', 1, 3),
  (8, '2026-04-02 09:36:00', 'Asset note captured but invoice evidence incomplete.', b'1', 'ACTIVE', '2026-04-02 09:36:00', 2, 3),
  (9, '2026-04-02 09:37:00', 'Supplier validation failed during review.', b'1', 'ACTIVE', '2026-04-02 09:37:00', 10, 3),
  (10, '2026-04-02 09:50:00', 'Purpose and customer note aligned but disclosure revision needed.', b'1', 'ACTIVE', '2026-04-02 09:50:00', 1, 4),
  (11, '2026-04-02 09:51:00', 'Charity fee wording required updated customer-facing language.', b'1', 'ACTIVE', '2026-04-02 09:51:00', 7, 4),
  (12, '2026-04-02 09:52:00', 'Customer declaration needed correction and resubmission.', b'1', 'ACTIVE', '2026-04-02 09:52:00', 8, 4),
  (13, '2026-04-02 10:05:00', 'Contract clause wording aligned with approved board template.', b'1', 'ACTIVE', '2026-04-02 10:05:00', 3, 5),
  (14, '2026-04-02 10:06:00', 'Profit or fee treatment reviewed and acceptable.', b'1', 'ACTIVE', '2026-04-02 10:06:00', 4, 5),
  (15, '2026-04-02 10:07:00', 'Board note completion confirmed for certificate issue.', b'1', 'ACTIVE', '2026-04-02 10:07:00', 15, 5),
  (16, '2026-04-02 10:20:00', 'Contract text prepared for board review.', b'1', 'ACTIVE', '2026-04-02 10:20:00', 3, 6),
  (17, '2026-04-02 10:21:00', 'Risk mitigation note captured.', b'1', 'ACTIVE', '2026-04-02 10:21:00', 11, 6),
  (18, '2026-04-02 10:22:00', 'Board note completion pending final meeting.', b'1', 'ACTIVE', '2026-04-02 10:22:00', 15, 6),
  (19, '2026-04-02 10:35:00', 'Collateral wording accepted by board.', b'1', 'ACTIVE', '2026-04-02 10:35:00', 6, 7),
  (20, '2026-04-02 10:36:00', 'Supplier and vendor records reviewed.', b'1', 'ACTIVE', '2026-04-02 10:36:00', 10, 7),
  (21, '2026-04-02 10:37:00', 'Risk note aligned with approval condition.', b'1', 'ACTIVE', '2026-04-02 10:37:00', 11, 7),
  (22, '2026-04-02 10:50:00', 'Contract clause review found outdated certificate wording.', b'1', 'ACTIVE', '2026-04-02 10:50:00', 3, 8),
  (23, '2026-04-02 10:51:00', 'Board note required updated final archive language.', b'1', 'ACTIVE', '2026-04-02 10:51:00', 15, 8),
  (24, '2026-04-02 10:52:00', 'Customer disclosure needs to reflect latest template revision.', b'1', 'ACTIVE', '2026-04-02 10:52:00', 8, 8),
  (25, '2026-04-02 11:05:00', 'Deposit scheme purpose and disclosure are compliant.', b'1', 'ACTIVE', '2026-04-02 11:05:00', 1, 9),
  (26, '2026-04-02 11:06:00', 'Charity clause routed correctly outside bank income.', b'1', 'ACTIVE', '2026-04-02 11:06:00', 14, 9),
  (27, '2026-04-02 11:07:00', 'Board note captured for annual report use.', b'1', 'ACTIVE', '2026-04-02 11:07:00', 15, 9),
  (28, '2026-04-02 11:20:00', 'Supplier invoice review exposed inconsistency.', b'1', 'ACTIVE', '2026-04-02 11:20:00', 10, 10),
  (29, '2026-04-02 11:21:00', 'Ownership evidence was incomplete at rejection time.', b'1', 'ACTIVE', '2026-04-02 11:21:00', 9, 10),
  (30, '2026-04-02 11:22:00', 'Document completeness remained below threshold.', b'1', 'ACTIVE', '2026-04-02 11:22:00', 5, 10),
  (31, '2026-04-02 11:35:00', 'Contract checklist prepared and queued for board.', b'1', 'ACTIVE', '2026-04-02 11:35:00', 3, 11),
  (32, '2026-04-02 11:36:00', 'Documentation captured and waiting decision.', b'1', 'ACTIVE', '2026-04-02 11:36:00', 5, 11),
  (33, '2026-04-02 11:37:00', 'Board note section left open for final meeting.', b'1', 'ACTIVE', '2026-04-02 11:37:00', 15, 11),
  (34, '2026-04-02 11:50:00', 'Card contract clause and disclosure comply with board guidance.', b'1', 'ACTIVE', '2026-04-02 11:50:00', 3, 12),
  (35, '2026-04-02 11:51:00', 'Customer declaration visible and compliant.', b'1', 'ACTIVE', '2026-04-02 11:51:00', 8, 12),
  (36, '2026-04-02 11:52:00', 'Board note completed for certificate archive.', b'1', 'ACTIVE', '2026-04-02 11:52:00', 15, 12),
  (37, '2026-04-02 12:05:00', 'Account opening declaration required correction.', b'1', 'ACTIVE', '2026-04-02 12:05:00', 8, 13),
  (38, '2026-04-02 12:06:00', 'Early settlement wording needed updated note.', b'1', 'ACTIVE', '2026-04-02 12:06:00', 13, 13),
  (39, '2026-04-02 12:07:00', 'Board returned case for corrected customer wording.', b'1', 'ACTIVE', '2026-04-02 12:07:00', 15, 13),
  (40, '2026-04-02 12:20:00', 'Purpose wording did not align with approved structure.', b'1', 'ACTIVE', '2026-04-02 12:20:00', 1, 14),
  (41, '2026-04-02 12:21:00', 'Profit method note conflicted with approved rule.', b'1', 'ACTIVE', '2026-04-02 12:21:00', 4, 14),
  (42, '2026-04-02 12:22:00', 'Risk mitigation note was insufficient for approval.', b'1', 'ACTIVE', '2026-04-02 12:22:00', 11, 14),
  (43, '2026-04-02 12:35:00', 'General advisory purpose logged for board comment.', b'1', 'ACTIVE', '2026-04-02 12:35:00', 1, 15),
  (44, '2026-04-02 12:36:00', 'Document completeness acceptable for initial queue.', b'1', 'ACTIVE', '2026-04-02 12:36:00', 5, 15),
  (45, '2026-04-02 12:37:00', 'Board note to be finalized after review meeting.', b'1', 'ACTIVE', '2026-04-02 12:37:00', 15, 15);
