# Module 8 Verify Guide

## 1. Open the module
- Go to `/accounts/dashboard`
- Open `/accounts/account-types`
- Open `/accounts/opening-requests`
- Open `/accounts/list`

## 2. Check playbook coverage
- Dashboard should show total accounts, accounts by type, pending opening requests, active accounts, blocked or frozen accounts and accounts awaiting verification.
- Account type pages should support create, edit, view, archive and restore.
- Opening request list should support `View`, `Edit`, `Review`, `Submit`, `Verify`, `Approve`, `Reject`, `Return`, plus navigation to customer, account type and linked account.
- Opening review page should allow save review info and run workflow actions.
- Account list should support `View`, `Status Action`, print, export, and direct activate, block, freeze, close actions.
- Account view page should show customer, account type, opening request, balances, profit ratio link and print.
- Account status page should keep activate, block, freeze and close actionable from one screen.

## 3. Data verification
- Active database for this project is `sbms`.
- Module 8 sample data is already inserted in `sbms`.
- `account_type`, `account_opening_request`, and `account` should each be `15`.
- The seed script also adds the missing legacy `requested_date` column to `account_opening_request` when needed.
- Existing older account rows were normalized so frontend can load valid account type codes and account statuses cleanly.
- Legacy DB stores blocked and frozen rows as `SUSPENDED`; the API maps them back to `BLOCKED` or `FROZEN` from remarks so the UI shows playbook-friendly statuses.

## 4. Navigation verification
- Customer list, customer view and customer status action pages now have `New Account Request` navigation.
- KYC view and KYC review pages now also have `New Account Request` navigation.
- Account pages include quick navigation between customer, account type, opening request, account view and status action.

## 5. What to test manually
- Create a new account type from `/accounts/account-types/new`.
- Create a new opening request from `/accounts/opening-requests/new`.
- Submit, verify, approve, reject and return requests from list or review page.
- Confirm approved requests create linked accounts.
- Open `/accounts/list` and test activate, block, freeze and close.
- Open any account view and verify branch, balances, request linkage and print.

## 6. Postman support
- Request samples are in `docs/module-8-postman-samples.json`.
- Seed SQL is in `docs/module-8-manual-seed.sql`.
- Use placeholders like `{{account_type_01_id}}`, `{{opening_request_01_id}}`, `{{account_01_id}}` with your own created ids when testing manually.
