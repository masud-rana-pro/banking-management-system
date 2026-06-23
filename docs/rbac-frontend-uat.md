# RBAC Frontend UAT Matrix

Use these seeded credentials for frontend UAT:

- `Admin01 / Admin@123!` -> `SYSTEM_ADMIN`
- `branch.manager01 / Admin@123!` -> `BRANCH_MANAGER`
- `fasf / Admin@123!` -> `TELLER`
- `ops.officer01 / Admin@123!` -> `OPERATIONS_OFFICER`
- `investment.officer01 / Admin@123!` -> `INVESTMENT_OFFICER`
- `shariah.board01 / Admin@123!` -> `SHARIAH_BOARD_MEMBER`
- `customer.seed01 / Admin@123!` -> `CUSTOMER`

## Shared checks

1. Open `/auth/login`.
2. Sign in with one user at a time.
3. Confirm email OTP arrives and login completes.
4. Confirm sidebar shows only allowed modules.
5. Try at least one allowed direct URL and one forbidden direct URL.
6. For sensitive actions, confirm OTP modal appears before final submit.

## SYSTEM_ADMIN

- Expected: all admin, config, operational, security, verification, reporting, workflow modules visible.
- Allowed pages:
  - `/admin/roles`
  - `/admin/users`
  - `/lookups/dashboard`
  - `/integrations/provider-test`
- Sensitive checks:
  - Open `/admin/users/1/reset-password`
  - Open `/admin/roles/1/permissions`
  - Confirm action triggers step-up OTP.

## BRANCH_MANAGER

- Expected modules:
  - branch, atm, customer, kyc, accounts, transactions, cards, statements, financing, workflow
- Allowed direct URLs:
  - `/branch/dashboard`
  - `/customer/list`
  - `/accounts/opening-requests`
- Forbidden direct URLs:
  - `/admin/users`
  - `/integrations/providers/new`
  - `/financing/applications/1/approve`
- Sensitive behavior:
  - `/financing/applications/1/disburse` may open if permission exists, but action still needs OTP.

## TELLER

- Expected modules:
  - branch, atm, customer, accounts, transactions, statements
- Allowed direct URLs:
  - `/transactions/deposit`
  - `/transactions/withdraw`
  - `/transactions/transfer`
- Forbidden direct URLs:
  - `/branch/new`
  - `/admin/roles`
  - `/transactions/1/reverse`

## OPERATIONS_OFFICER

- Expected modules:
  - branch, atm, customer, kyc, accounts, transactions, profit, cards, statements, notifications, verification, workflow, zakat
- Allowed direct URLs:
  - `/notifications/dashboard`
  - `/verification/provider-test`
  - `/cards/list`
- Forbidden direct URLs:
  - `/admin/users/new`
  - `/financing/products/new`

## INVESTMENT_OFFICER

- Expected modules:
  - accounts, calculations, contracts, deposit schemes, financing, profit, reports, shariah, workflow
- Allowed direct URLs:
  - `/financing/products/new`
  - `/deposit-schemes/new`
  - `/profit/postings/run`
  - `/calculations/simulator`
- Forbidden direct URLs:
  - `/admin/users`
  - `/notifications/templates/new`
- Sensitive behavior:
  - `/financing/applications/1/disburse` should require step-up OTP.

## SHARIAH_BOARD_MEMBER

- Expected modules:
  - calculations, reports, shariah, workflow, zakat
- Allowed direct URLs:
  - `/shariah/cases`
  - `/shariah/cases/1/review`
  - `/reports/shariah-audit`
- Forbidden direct URLs:
  - `/contracts/templates/new`
  - `/admin/roles`
- Sensitive behavior:
  - approve/reject/return from shariah review should require step-up OTP.

## CUSTOMER

- Expected modules:
  - customer, accounts, contracts, deposit schemes, financing, statements, zakat
- Allowed direct URLs:
  - `/customers/list`
  - `/statement/customer/list`
  - `/deposit-schemes/enrollments/list`
- Forbidden direct URLs:
  - `/admin/users`
  - `/transactions/reverse`
  - `/notifications/dashboard`

## Result guide

- If a forbidden page redirects to the landing page, RBAC route guard is working.
- If a visible action button is disabled with tooltip, frontend permission lock is working.
- If a sensitive action asks for OTP, step-up verification is working.
- If a direct API-backed action returns `403`, backend RBAC is working.
