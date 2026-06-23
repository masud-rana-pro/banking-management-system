# Real OTP Setup

This project now supports real OTP delivery for `EMAIL` and `SMS`.

## Current behavior

- If real provider configuration is missing, OTP falls back to the local simulator.
- In simulator mode, the login screen shows a preview OTP code.
- If real email or SMS provider configuration is active and working, the preview code will no longer be shown.

## Current login contact for `Admin01`

- Email: `masud.jee68@gmail.com`
- Mobile: `01575634380`

Login screen now supports channel selection:

- `Email OTP`
- `SMS OTP`
- `Auto Select`

If you choose `Auto Select`, login OTP prefers:
1. verified email
2. verified mobile
3. email
4. mobile

## Real email OTP

Set these environment variables before starting the backend:

```powershell
$env:SBMS_EMAIL_OTP_ENABLED="true"
$env:SBMS_EMAIL_FROM="your-sender@gmail.com"
$env:SBMS_MAIL_HOST="smtp.gmail.com"
$env:SBMS_MAIL_PORT="587"
$env:SBMS_MAIL_USERNAME="your-sender@gmail.com"
$env:SBMS_MAIL_PASSWORD="your-app-password"
$env:SBMS_MAIL_SMTP_AUTH="true"
$env:SBMS_MAIL_STARTTLS="true"
```

For Gmail you must use an **App Password**, not your normal Gmail password.

Ready-made helper file:

- [start-backend-email-otp.example.ps1](</I:\SBMS Copy\docs\start-backend-email-otp.example.ps1>)

Optional:

```powershell
$env:SBMS_EMAIL_PROVIDER="GMAIL_SMTP"
$env:SBMS_EMAIL_SUBJECT_PREFIX="SBMS"
$env:SBMS_OTP_SIMULATOR_FALLBACK="false"
```

If `SBMS_OTP_SIMULATOR_FALLBACK=false`, failed email delivery will show an error instead of silently falling back to preview OTP.

## Real SMS OTP

Set these environment variables before starting the backend:

```powershell
$env:SBMS_SMS_OTP_ENABLED="true"
$env:SBMS_SMS_PROVIDER="YOUR_SMS_GATEWAY"
$env:SBMS_SMS_ENDPOINT_URL="https://your-sms-gateway.example/send"
$env:SBMS_SMS_METHOD="POST"
$env:SBMS_SMS_AUTH_HEADER_NAME="Authorization"
$env:SBMS_SMS_AUTH_HEADER_VALUE="Bearer your-api-token"
$env:SBMS_SMS_SENDER_ID="SBMS"
$env:SBMS_SMS_CONTENT_TYPE="application/json"
$env:SBMS_SMS_SUCCESS_KEYWORD="SUCCESS"
```

Default payload template:

```text
{"to":"{{to}}","message":"{{message}}","senderId":"{{senderId}}"}
```

If your gateway needs a different body format:

```powershell
$env:SBMS_SMS_PAYLOAD_TEMPLATE='{"mobile":"{{to}}","sms":"{{message}}","sender":"{{senderId}}"}'
```

Supported placeholders:

- `{{to}}`
- `{{message}}`
- `{{senderId}}`
- `{{otp}}`

## Start backend with real OTP config

After setting the environment variables:

```powershell
mvn.cmd spring-boot:run
```

## Verify email OTP

1. Open `/auth/login`
2. Login with:
   - Username: `Admin01`
   - Password: `Admin@123!`
3. If email provider is working, the OTP should arrive in `masud.jee68@gmail.com`
4. The login page should not show a preview OTP code

## Verify SMS OTP

For SMS login testing:

1. Open `/auth/login`
2. Select `SMS OTP`
3. Login with `Admin01 / Admin@123!`
4. OTP should go to `01575634380`

## Login OTP templates

Dedicated login OTP templates are stored in:

- [email-otp-login-template.sql](</I:\SBMS Copy\docs\email-otp-login-template.sql>)
