SET NAMES utf8mb4;

START TRANSACTION;

INSERT INTO verification_template (
  purpose,
  channel_type,
  template_code,
  template_name,
  subject_line,
  template_body,
  status,
  created_at,
  updated_at
)
SELECT
  'LOGIN_OTP',
  'EMAIL',
  'VT-EMAIL-LOGIN-01',
  'Login OTP Email',
  'Your secure login OTP',
  'Assalamu Alaikum, your SBMS login OTP is {{otp}}. This code will expire in {{expiresMinutes}} minutes. If you did not try to sign in, please contact support immediately.',
  'ACTIVE',
  NOW(),
  NOW()
WHERE NOT EXISTS (
  SELECT 1
  FROM verification_template
  WHERE purpose = 'LOGIN_OTP'
    AND channel_type = 'EMAIL'
    AND template_code = 'VT-EMAIL-LOGIN-01'
);

INSERT INTO verification_template (
  purpose,
  channel_type,
  template_code,
  template_name,
  subject_line,
  template_body,
  status,
  created_at,
  updated_at
)
SELECT
  'LOGIN_OTP',
  'SMS',
  'VT-SMS-LOGIN-01',
  'Login OTP SMS',
  NULL,
  'SBMS login OTP: {{otp}}. Valid for {{expiresMinutes}} minutes. Do not share this code with anyone.',
  'ACTIVE',
  NOW(),
  NOW()
WHERE NOT EXISTS (
  SELECT 1
  FROM verification_template
  WHERE purpose = 'LOGIN_OTP'
    AND channel_type = 'SMS'
    AND template_code = 'VT-SMS-LOGIN-01'
);

COMMIT;
