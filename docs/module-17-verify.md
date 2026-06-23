# Module 17 Verify Guide

Module 17 covers Zakat & Charity.

## Routes

- `/zakat/dashboard`
- `/zakat/profiles`
- `/zakat/profiles/:id`
- `/zakat/calc-run`
- `/zakat/charity-fund`
- `/zakat/beneficiaries`
- `/zakat/beneficiaries/new`
- `/zakat/beneficiaries/:id/edit`
- `/zakat/payouts`
- `/zakat/payouts/new`

## Seed State

After running [module-17-manual-seed.sql](/I:\SBMS%20Copy\docs\module-17-manual-seed.sql) the database should contain:

- `zakat_profile = 15`
- `charity_beneficiary = 15`
- `charity_payout = 15`
- `charity_fund = 30`

Workflow mix:

- Deducted profiles: `9`
- Calculated profiles: `2`
- Profiled only: `2`
- Below nisab profiles: `2`
- Archived beneficiaries: `2`
- Fund sources mix: `ZAKAT_DEDUCTION`, `DONATION`, `LATE_FEE`, `PAYOUT`

## UI Checks

1. Open `/zakat/dashboard`.
2. Confirm cards show zakat due accounts, total zakat calculated, charity fund balance, beneficiary payout total and upcoming reminders.
3. Confirm recent profiles, recent fund ledger entries and recent payouts are populated.
4. Open `/zakat/profiles`.
5. Confirm filters work for customer id, zakat year and keyword.
6. Confirm print/export buttons are visible.
7. Confirm list actions work: `View`, `Calculate`, `Beneficiaries`, `Payouts`.
8. Open `/zakat/profiles/:id`.
9. Confirm customer summary, nisab, eligible assets, zakat amount, status and related ledger details render.
10. Open `/zakat/calc-run`.
11. Create a new profile and run calculation.
12. Confirm below-nisab input keeps zakat amount at `0.00`.
13. Confirm eligible assets above nisab create a charity fund credit and move the profile into deducted status.
14. Open `/zakat/charity-fund`.
15. Confirm fund ledger rows, running balance and source badges render.
16. Open `/zakat/beneficiaries`.
17. Confirm `Edit`, `Archive`, `Restore` and `New Payout` actions work.
18. Open `/zakat/payouts` and confirm payout rows load with beneficiary, amount and approver information.
19. Open `/zakat/payouts/new` and confirm only active beneficiaries appear in the dropdown.
20. Create a payout and confirm charity fund balance decreases.

## Cross Navigation Checks

1. Open any customer view page and confirm `Zakat Profiles` and `Run Zakat` navigation buttons work.
2. Open any account view page and confirm `Zakat Profiles` and `Run Zakat` navigation buttons work.
3. Confirm sidebar contains `Zakat & Charity` with dashboard, profiles, charity fund, beneficiaries and payouts links.

## SQL Checks

```sql
SELECT COUNT(*) FROM zakat_profile;
SELECT COUNT(*) FROM charity_beneficiary;
SELECT COUNT(*) FROM charity_payout;
SELECT COUNT(*) FROM charity_fund;

SELECT calculation_status, COUNT(*) FROM zakat_profile GROUP BY calculation_status;
SELECT source_type, COUNT(*) FROM charity_fund GROUP BY source_type;
SELECT status, COUNT(*) FROM charity_beneficiary GROUP BY status;
```

## Build Checks

Run these if needed:

```bash
cd stable-sbms-backend
mvn.cmd -q -DskipTests compile

cd ../stable-sbms-frontend
npx.cmd tsc -p tsconfig.app.json --noEmit
npx.cmd ngc -p tsconfig.app.json
```
