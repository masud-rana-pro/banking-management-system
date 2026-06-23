SET NAMES utf8mb4;

START TRANSACTION;

INSERT INTO contact_verification_status (
  reference_module,
  reference_id,
  contact_type,
  contact_value,
  is_primary,
  is_verified,
  verified_at,
  verified_by,
  verification_method,
  last_verification_request_id,
  status,
  created_at,
  updated_at
)
SELECT
  r.reference_module,
  r.reference_id,
  CASE WHEN r.purpose = 'VERIFY_EMAIL' THEN 'EMAIL' ELSE 'MOBILE' END,
  r.contact_value,
  TRUE,
  CASE WHEN r.request_status = 'VERIFIED' THEN TRUE ELSE FALSE END,
  CASE WHEN r.request_status = 'VERIFIED' THEN COALESCE(r.used_at, r.updated_at, NOW()) ELSE NULL END,
  CASE
    WHEN r.request_status = 'VERIFIED' AND r.user_id IS NOT NULL THEN u.username
    WHEN r.request_status = 'VERIFIED' THEN 'SYSTEM'
    ELSE NULL
  END,
  CONCAT(r.channel_type, '_OTP'),
  r.id,
  'ACTIVE',
  NOW(),
  NOW()
FROM otp_verification_request r
LEFT JOIN users u ON u.id = r.user_id
WHERE r.purpose IN ('VERIFY_EMAIL', 'VERIFY_MOBILE')
  AND NOT EXISTS (
    SELECT 1
    FROM contact_verification_status c
    WHERE c.reference_module = r.reference_module
      AND c.reference_id = r.reference_id
      AND c.contact_type = CASE WHEN r.purpose = 'VERIFY_EMAIL' THEN 'EMAIL' ELSE 'MOBILE' END
      AND c.contact_value = r.contact_value
  );

COMMIT;
