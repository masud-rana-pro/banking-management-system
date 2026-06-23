$env:SBMS_EMAIL_OTP_ENABLED="true"
$env:SBMS_EMAIL_FROM="masud.jee68@gmail.com"
$env:SBMS_MAIL_HOST="smtp.gmail.com"
$env:SBMS_MAIL_PORT="587"
$env:SBMS_MAIL_USERNAME="masud.jee68@gmail.com"
$env:SBMS_MAIL_PASSWORD="ubko njkh qkbj rrxa"
$env:SBMS_MAIL_SMTP_AUTH="true"
$env:SBMS_MAIL_STARTTLS="true"
$env:SBMS_EMAIL_PROVIDER="GMAIL_SMTP"
$env:SBMS_EMAIL_SUBJECT_PREFIX="SBMS"
$env:SBMS_OTP_SIMULATOR_FALLBACK="false"

Set-Location "I:\SBMS Copy\stable-sbms-backend"
mvn.cmd spring-boot:run
