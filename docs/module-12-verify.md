# Module 12 Verify Guide

## 1. Backend / frontend open

- Backend should be running at `http://localhost:8080`
- Frontend should be running at `http://localhost:4200` or `http://127.0.0.1:4201`

## 2. Seed data

- Current environment seed file: [module-12-manual-seed.sql](/I:/SBMS%20Copy/docs/module-12-manual-seed.sql)
- API/Postman payload file: [module-12-postman-samples.json](/I:/SBMS%20Copy/docs/module-12-postman-samples.json)
- Expected counts after seeding:
  - `customer_statement_request = 15`
  - `branch_statement_request = 15`
  - `file_reference = 30`

## 3. Pages to check

- `/statement/dashboard`
- `/statement/customer/request`
- `/statement/customer/list`
- `/statement/customer/:id`
- `/statement/branch/request`
- `/statement/branch/list`
- `/statement/branch/:id`
- `/statement/export-center`

## 4. Functional checks

- Statement dashboard should show total customer requests, total branch requests, generated files, downloaded statements, pending/generated mixes and recent request panels.
- Customer statement request page should enforce customer, account and date range selection and create a generated request.
- Customer statement list should support search, print, export, view and download.
- Customer statement view should show summary metrics, statement lines, print and download.
- Branch statement request page should enforce branch and date range selection and create a generated request.
- Branch statement list should support search, print, export, view and download.
- Branch statement view should show operational summary, statement lines, print and download.
- Export center should list generated file references with module, file name, reference table and creation time.

## 5. Navigation checks

- Customer view should show `Statements` and `New Statement`.
- Account list/view should show statement navigation buttons.
- Branch list/view should show statement navigation buttons.
- Sidebar should show `Statements` with dashboard, customer statement, branch statement and export center entries.

## 6. API smoke checks

- `GET /api/statements/dashboard-summary`
- `GET /api/statements/files`
- `GET /api/customer-statements/list`
- `GET /api/customer-statements/{id}`
- `GET /api/customer-statements/{id}/download`
- `GET /api/branch-statements/list`
- `GET /api/branch-statements/{id}`
- `GET /api/branch-statements/{id}/download`

## 7. DB quick check

```sql
SELECT COUNT(*) FROM customer_statement_request;
SELECT COUNT(*) FROM branch_statement_request;
SELECT COUNT(*) FROM file_reference;
```

## 8. Important note

- `file_reference` rows point to actual generated HTML files under `stable-sbms-backend/generated-statements`.
- If you want brand new file generation instead of reusing the current seeded files, create requests again from the UI or from [module-12-postman-samples.json](/I:/SBMS%20Copy/docs/module-12-postman-samples.json).
