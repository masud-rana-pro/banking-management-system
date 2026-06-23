# Module 9 Verify Guide

## 1. Open the module
- Go to `/transactions/dashboard`
- Open `/transactions/list`
- Open `/transactions/deposit`
- Open `/transactions/withdraw`
- Open `/transactions/transfer`
- Open `/transactions/cheque-clearing`
- Open `/transactions/standing-instructions`

## 2. Check playbook coverage
- Dashboard should show today deposit total, today withdrawal total, today transfer total, pending reversals, teller usage, suspicious count and top branches.
- Deposit, withdrawal, transfer and cheque clearing forms should post transactions and open the created voucher view.
- Standing instruction list and create pages should work and show linked accounts, frequency and status.
- Transaction list should support search, print, export, view and reverse buttons.
- Transaction view should show transaction header, debit and credit account, cash or transfer detail, cheque detail, reversal status and audit trail.
- Transaction reversal page should require reason and create reversal posting when allowed.

## 3. Data verification
- Active database for this project is `sbms`.
- Run `docs/module-9-manual-seed.sql` after backend startup if you want the sample dataset.
- Expected seeded counts:
  - `transaction_journal = 15`
  - `cash_transaction = 15`
  - `fund_transfer = 15`
  - `transaction_reversal = 15`
  - `cheque_clearing = 15`
  - `standing_instruction = 15`

## 4. Navigation verification
- Account list and account view now have direct `Deposit`, `Withdraw` and `Transfer` navigation buttons.
- Transactions menu now includes dashboard, journal, deposit, withdraw, transfer, cheque clearing and standing instruction routes.

## 5. What to test manually
- Post one deposit from `/transactions/deposit`.
- Post one withdrawal from `/transactions/withdraw`.
- Post one transfer from `/transactions/transfer`.
- Post one cheque clearing entry from `/transactions/cheque-clearing`.
- Create one standing instruction from `/transactions/standing-instructions/new`.
- Open `/transactions/list` and reverse a non-reversed transaction.
- Open a transaction view and verify debit or credit account navigation buttons.

## 6. Postman support
- Request samples are in `docs/module-9-postman-samples.json`.
- Seed SQL is in `docs/module-9-manual-seed.sql`.
- Replace any account ids with your own ids if your environment differs from the seeded `1..15` account records.
