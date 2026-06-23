# Module 13 Verify Guide

## 1. Backend / frontend open

- Backend should be running at `http://localhost:8080`
- Frontend should be running at `http://localhost:4200` or `http://127.0.0.1:4201`

## 2. Seed data

- Reset helper SQL: [module-13-manual-seed.sql](/I:/SBMS%20Copy/docs/module-13-manual-seed.sql)
- API/Postman payload file: [module-13-postman-samples.json](/I:/SBMS%20Copy/docs/module-13-postman-samples.json)
- Current seeded environment counts:
  - `deposit_scheme = 15`
  - `deposit_scheme_enrollment = 15`
  - `deposit_scheme_schedule = 186`
  - `deposit_scheme_profit_distribution = 86`

## 3. Pages to check

- `/deposit-schemes/dashboard`
- `/deposit-schemes/list`
- `/deposit-schemes/new`
- `/deposit-schemes/:id`
- `/deposit-schemes/:id/edit`
- `/deposit-schemes/enrollments/list`
- `/deposit-schemes/enrollments/new`
- `/deposit-schemes/enrollments/:id/schedule`
- `/deposit-schemes/enrollments/:id/profit`

## 4. Functional checks

- Scheme dashboard should show total schemes, active enrollments, due installments, early withdrawal requests and matured schemes.
- Scheme list should support search, print, export, view, edit, archive or restore, enroll, view schedule and view profit.
- Scheme create/edit should save master definition with scheme type, tenure, minimum installment, profit ratio and profit frequency.
- Scheme view should show scheme detail and linked enrollments with schedule/profit action buttons.
- Enrollment create should validate customer/account ownership and minimum installment.
- Enrollment list should show maturity amount, maturity date, active vs matured vs early withdrawal statuses and quick actions.
- Schedule view should show installment rows, due date, installment amount, profit amount and payment status.
- Profit distribution view should show distribution periods, profit amount, credited account and distribution status.

## 5. Navigation checks

- Sidebar should show `Deposit Schemes` under `OPERATIONS`.
- Customer view should show `Deposit Schemes` and `Enroll Scheme`.
- Account view should show `Deposit Schemes` and `Enroll Scheme`.
- Account list should show `Deposit Schemes` top navigation and `Enroll Scheme` row action.

## 6. API smoke checks

- `GET /api/deposit-schemes/list`
- `GET /api/deposit-schemes/{id}`
- `POST /api/deposit-schemes/create`
- `PUT /api/deposit-schemes/{id}`
- `DELETE /api/deposit-schemes/{id}`
- `PUT /api/deposit-schemes/{id}/restore`
- `POST /api/deposit-schemes/enrollment/create`
- `GET /api/deposit-schemes/enrollment/list`
- `GET /api/deposit-schemes/enrollment/{id}`
- `GET /api/deposit-schemes/enrollment/{id}/schedule`
- `GET /api/deposit-schemes/enrollment/{id}/profit`
- `GET /api/deposit-schemes/dashboard-summary`

## 7. DB quick check

```sql
SELECT COUNT(*) FROM deposit_scheme;
SELECT COUNT(*) FROM deposit_scheme_enrollment;
SELECT COUNT(*) FROM deposit_scheme_schedule;
SELECT COUNT(*) FROM deposit_scheme_profit_distribution;
```

## 8. Current seed notes

- Scheme ids `2` and `5` are archived for archive/restore verification.
- Enrollment ids `6`, `10` and `14` are marked as `EARLY_WITHDRAWAL_REQUESTED`.
- Multiple enrollments have old start dates, so due installment and matured dashboard cards are non-zero.
