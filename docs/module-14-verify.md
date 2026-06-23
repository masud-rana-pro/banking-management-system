# Module 14 Verify Guide

Module 14 covers Financing Management.

## Routes

- `/financing/dashboard`
- `/financing/products`
- `/financing/products/new`
- `/financing/applications`
- `/financing/applications/new`
- `/financing/applications/:id`
- `/financing/applications/:id/review`
- `/financing/applications/:id/disburse`
- `/financing/applications/:id/schedule`
- `/financing/applications/:id/repayment`

## Seed State

After running `docs/module-14-manual-seed.sql` the database should contain:

- `financing_product = 15`
- `financing_application = 15`
- `financing_asset_verification = 15`
- `financing_disbursement = 7`
- `financing_schedule = 28`

Workflow mix:

- Pending flow states: `SUBMITTED`, `ASSET_VERIFIED`, `SHARIAH_REVIEW`
- Ready states: `APPROVED`
- Disbursed states: `DISBURSED`
- Active repayment states: `ACTIVE`
- Finished states: `CLOSED`
- Schedule mix: `PENDING`, `PARTIAL`, `OVERDUE`, `PAID`

## UI Checks

1. Open `/financing/dashboard`.
2. Confirm cards show pending applications, approved applications, disbursed amount, overdue installments and charity late fee.
3. Confirm the product metric widget is populated and recent applications table is visible.
4. Open `/financing/products`.
5. Confirm print/export buttons are visible and list actions work: `View`, `Edit`, `Applications`, `Archive/Restore`.
6. Open `/financing/applications`.
7. Confirm list actions work: `View`, `Edit`, `Review`, `Disbursement`, `Schedule`, `Repayment`, `Submit`, `Approve`, `Archive/Restore`.
8. Open any submitted or asset-verified application and go to `/review`.
9. Test asset verification, shariah review, approve, reject and return actions.
10. Open an approved application and go to `/disburse`.
11. Confirm credited account dropdown loads and disbursement generates schedule.
12. Open `/schedule` and confirm principal, profit, charity and paid summaries render.
13. Open `/repayment` and submit a repayment amount.
14. Confirm application status updates toward `ACTIVE` or `CLOSED` depending on outstanding amount.

## Cross Navigation Checks

1. Open any customer view page and check `Financing Apps` and `New Financing` buttons.
2. Open any account view page and check `Financing Apps` and `New Financing` buttons.
3. Open account list and confirm top-level `Financing` navigation and row-level `New Financing` action.
4. Confirm sidebar contains `Financing` with dashboard, product list, new product, application list and new application.

## SQL Checks

```sql
SELECT COUNT(*) FROM financing_product;
SELECT COUNT(*) FROM financing_application;
SELECT COUNT(*) FROM financing_asset_verification;
SELECT COUNT(*) FROM financing_disbursement;
SELECT COUNT(*) FROM financing_schedule;

SELECT application_status, COUNT(*) FROM financing_application GROUP BY application_status;
SELECT schedule_status, COUNT(*) FROM financing_schedule GROUP BY schedule_status;
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
