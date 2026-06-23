SET FOREIGN_KEY_CHECKS = 0;

DELETE cep
FROM card_event_log cep
JOIN card c ON c.id = cep.card_id
WHERE c.card_ref_no LIKE 'CRD-90%';

DELETE cpp
FROM card_pin_event cpp
JOIN card c ON c.id = cpp.card_id
WHERE c.card_ref_no LIKE 'CRD-90%';

DELETE FROM card
WHERE card_ref_no LIKE 'CRD-90%';

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO card (
    card_ref_no, customer_id, account_id, card_type, masked_card_no,
    issue_date, expiry_date, card_status, block_reason, status, created_at, updated_at
) VALUES
('CRD-90001', 1, 1, 'DEBIT_CARD',   '4000-****-9001-0001', '2026-01-05', '2031-01-05', 'ACTIVE',             NULL,                    'ACTIVE',   NOW(), NOW()),
('CRD-90002', 1, 1, 'ATM_CARD',     '4000-****-9002-0001', '2026-01-18', '2031-01-18', 'PENDING_ACTIVATION', NULL,                    'ACTIVE',   NOW(), NOW()),
('CRD-90003', 7, 7, 'DEBIT_CARD',   '4000-****-9003-0007', '2026-02-02', '2031-02-02', 'BLOCKED',            'ATM cash-out mismatch', 'ACTIVE',   NOW(), NOW()),
('CRD-90004', 7, 7, 'PREPAID_CARD', '4000-****-9004-0007', '2026-02-14', '2031-02-14', 'ACTIVE',             NULL,                    'ACTIVE',   NOW(), NOW()),
('CRD-90005', 1, 1, 'VIRTUAL_CARD', '4000-****-9005-0001', '2026-02-26', '2031-02-26', 'ACTIVE',             NULL,                    'ACTIVE',   NOW(), NOW()),
('CRD-90006', 7, 7, 'ATM_CARD',     '4000-****-9006-0007', '2026-03-03', '2031-03-03', 'PENDING_ACTIVATION', NULL,                    'ACTIVE',   NOW(), NOW()),
('CRD-90007', 1, 1, 'DEBIT_CARD',   '4000-****-9007-0001', '2026-03-10', '2031-03-10', 'ACTIVE',             NULL,                    'ACTIVE',   NOW(), NOW()),
('CRD-90008', 7, 7, 'ATM_CARD',     '4000-****-9008-0007', '2026-03-22', '2031-03-22', 'BLOCKED',            'PIN retry exceeded',    'ACTIVE',   NOW(), NOW()),
('CRD-90009', 1, 1, 'PREPAID_CARD', '4000-****-9009-0001', '2026-04-01', '2031-04-01', 'ACTIVE',             NULL,                    'ACTIVE',   NOW(), NOW()),
('CRD-90010', 7, 7, 'VIRTUAL_CARD', '4000-****-9010-0007', '2026-04-09', '2031-04-09', 'PENDING_ACTIVATION', NULL,                    'ACTIVE',   NOW(), NOW()),
('CRD-90011', 1, 1, 'ATM_CARD',     '4000-****-9011-0001', '2026-04-15', '2031-04-15', 'ACTIVE',             NULL,                    'ACTIVE',   NOW(), NOW()),
('CRD-90012', 7, 7, 'DEBIT_CARD',   '4000-****-9012-0007', '2026-04-20', '2031-04-20', 'PENDING_ACTIVATION', NULL,                    'ACTIVE',   NOW(), NOW()),
('CRD-90013', 1, 1, 'ATM_CARD',     '4000-****-9013-0001', '2025-04-10', '2026-04-20', 'ACTIVE',             NULL,                    'ACTIVE',   NOW(), NOW()),
('CRD-90014', 7, 7, 'DEBIT_CARD',   '4000-****-9014-0007', '2026-04-24', '2031-04-24', 'BLOCKED',            'Cardholder request',    'ACTIVE',   NOW(), NOW()),
('CRD-90015', 1, 1, 'VIRTUAL_CARD', '4000-****-9015-0001', '2026-04-28', '2031-04-28', 'PENDING_ACTIVATION', NULL,                    'ACTIVE',   NOW(), NOW());

INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'ACTIVATED', '2026-01-05 10:15:00', 'SYSTEM', 'Card activated after issuance', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90001';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'ISSUED', '2026-01-18 09:10:00', 'SYSTEM', 'Card waiting for activation', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90002';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'BLOCKED', '2026-02-03 16:30:00', 'OPS_USER', 'Blocked due to ATM cash-out mismatch', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90003';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'ATM_TRANSACTION', '2026-02-15 11:45:00', 'ATM-001', 'Cash withdrawal completed', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90004';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'CDM_TRANSACTION', '2026-02-27 13:05:00', 'CDM-001', 'Cash deposit completed', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90005';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'ISSUED', '2026-03-03 09:20:00', 'SYSTEM', 'Issued and queued for customer activation', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90006';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'ATM_USAGE_ALERT', '2026-03-11 20:10:00', 'ATM-004', 'Late-night large withdrawal alert', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90007';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'BLOCKED', '2026-03-23 12:40:00', 'OPS_USER', 'Blocked after PIN retry exceeded threshold', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90008';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'CDM_USAGE_ALERT', '2026-04-01 14:55:00', 'CDM-003', 'Repeated deposit reversal alert', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90009';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'ISSUED', '2026-04-09 09:00:00', 'SYSTEM', 'Virtual card created and pending activation', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90010';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'ATM_TRANSACTION', '2026-04-15 17:20:00', 'ATM-002', 'Cash withdrawal completed', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90011';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'ISSUED', '2026-04-20 10:30:00', 'SYSTEM', 'Issued and pending branch handover', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90012';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'ATM_USAGE_ALERT', '2026-04-21 18:15:00', 'ATM-005', 'Expiry proximity usage alert', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90013';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'BLOCKED', '2026-04-24 15:40:00', 'OPS_USER', 'Blocked upon cardholder request', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90014';
INSERT INTO card_event_log (card_id, event_type, event_date, performed_by, remarks, status, created_at)
SELECT id, 'ISSUED', '2026-04-28 09:35:00', 'SYSTEM', 'Virtual card issued and queued for activation', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90015';

INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_GENERATED', '2026-01-05 10:16:00', 'SYSTEM', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90001';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_GENERATED', '2026-01-18 09:15:00', 'SYSTEM', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90002';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'WRONG_PIN', '2026-02-03 16:10:00', 'ATM-006', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90003';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_CHANGE', '2026-02-16 10:00:00', 'SELF_SERVICE', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90004';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_RESET', '2026-02-27 16:30:00', 'CALL_CENTER', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90005';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_GENERATED', '2026-03-03 09:22:00', 'SYSTEM', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90006';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_CHANGE', '2026-03-12 08:45:00', 'SELF_SERVICE', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90007';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_BLOCKED', '2026-03-23 12:20:00', 'ATM-008', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90008';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_RESET', '2026-04-01 15:15:00', 'CALL_CENTER', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90009';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_GENERATED', '2026-04-09 09:05:00', 'SYSTEM', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90010';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_CHANGE', '2026-04-15 17:40:00', 'SELF_SERVICE', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90011';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_GENERATED', '2026-04-20 10:35:00', 'SYSTEM', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90012';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'WRONG_PIN', '2026-04-21 18:10:00', 'ATM-005', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90013';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_RESET', '2026-04-24 16:00:00', 'CALL_CENTER', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90014';
INSERT INTO card_pin_event (card_id, event_type, event_date, performed_by, status, created_at)
SELECT id, 'PIN_GENERATED', '2026-04-28 09:38:00', 'SYSTEM', 'ACTIVE', NOW() FROM card WHERE card_ref_no = 'CRD-90015';
