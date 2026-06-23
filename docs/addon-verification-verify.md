# Verification Add-on Verification

1. Start backend from `I:\SBMS Copy\stable-sbms-backend` using `mvn spring-boot:run`.
2. Confirm verification APIs respond:
   - `GET /api/verifications/dashboard-summary`
   - `GET /api/verifications/logs`
   - `GET /api/verifications/channels`
   - `GET /api/verifications/templates`
   - `GET /api/verifications/contact-status`
3. Confirm auth verification APIs respond:
   - `POST /api/auth/login`
   - `POST /api/auth/verify-login-otp`
   - `POST /api/auth/resend-login-otp/{requestId}`
   - `POST /api/auth/send-otp`
   - `POST /api/auth/verify-otp`
   - `POST /api/auth/forgot-password`
   - `POST /api/auth/reset-password`
4. Start frontend from `I:\SBMS Copy\stable-sbms-frontend` using `npm start`.
5. Open these pages and check navigation:
   - `/verification/dashboard`
   - `/verification/logs`
   - `/verification/otp-verify`
   - `/verification/forgot-password`
   - `/verification/reset-password`
   - `/verification/provider-test`
6. Verify dashboard cards show pending, verified, failed, password reset, dispatch queue, unverified email and unverified mobile counts.
7. Verify dashboard recent sections show recent requests, recent attempts and recent contact verification statuses.
8. Send one email OTP and one mobile OTP from UI or Postman, then verify both using the OTP preview from `providerResponse`.
9. Confirm successful verification updates:
   - `users.email_verified` or `users.mobile_verified` when user-linked
   - `customer.email_verified` or `customer.mobile_verified` when customer-linked
   - `contact_verification_status.is_verified`
10. Test login OTP flow from `/auth/login`:
   - enter username/password
   - confirm OTP challenge is shown
   - use local simulator preview code if present
   - verify OTP and confirm authenticated session loads
11. Use resend, expire and mark-failed actions from the verification log list and confirm statuses change correctly.
12. Run `docs/addon-verification-manual-seed.sql` if `contact_verification_status` is still empty.
13. Use `docs/addon-verification-postman-samples.json` for repeatable Postman flows.

# Current Data Notes

- Existing seeded totals before contact-status seed:
  - `verification_channel = 2`
  - `verification_template = 8`
  - `otp_verification_request = 15`
  - `verification_dispatch_queue = 15`
  - `password_reset_request = 3`
  - `verification_attempt_log = 17`
- `contact_verification_status` is now the add-on-specific tracking table aligned to the playbook.

# Validation Commands Used

- Backend compile: `mvn -q -DskipTests compile`
- Frontend TypeScript check: `npx tsc -p tsconfig.app.json --noEmit`
- Frontend Angular compiler check: `npx ngc -p tsconfig.app.json`
