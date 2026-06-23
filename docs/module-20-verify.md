# Module 20 Verify Guide

## Scope
Playbook module: `Module 20: Reporting & Regulatory`  
Base API: `/api/reports`

Implemented pages:
- `/reports/dashboard`
- `/reports/operational`
- `/reports/profit-distribution`
- `/reports/financing-portfolio`
- `/reports/par`
- `/reports/shariah-audit`
- `/reports/branch`
- `/reports/export-history`

## Seed data
Run:

```powershell
& 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' -uroot -proot -D sbms -e "source I:/SBMS Copy/docs/module-20-manual-seed.sql"
```

Expected seeded module data:

```sql
SELECT COUNT(*) FROM report_definition;
SELECT COUNT(*) FROM report_request_log WHERE requested_by = 'REPORT_SEED';
SELECT COUNT(*) FROM file_reference WHERE module_name = 'REPORTS';
```

Expected minimum:
- `report_definition >= 15`
- `report_request_log where requested_by='REPORT_SEED' = 15`
- `file_reference where module_name='REPORTS' = 15`

## UI checks
1. Open `/reports/dashboard` and verify summary cards load.
2. Open each report page and check:
   - same Module 5-style card/form/table layout
   - `Print` button
   - `Export` dropdown with CSV/PDF
   - date range filter works
   - branch dropdown appears on operational and branch report pages
3. Open `/reports/export-history` and verify view button opens the related report page.
4. Print any report page and confirm only content is printed.

## API checks
Use [module-20-postman-samples.json](</I:\SBMS Copy\docs\module-20-postman-samples.json>) or hit these directly:

```http
GET /api/reports/dashboard-summary
GET /api/reports/operational?dateFrom=2026-04-01&dateTo=2026-04-30
GET /api/reports/profit-distribution?dateFrom=2026-04-01&dateTo=2026-04-30
GET /api/reports/financing-portfolio?dateFrom=2026-03-01&dateTo=2026-04-30
GET /api/reports/par?dateFrom=2026-01-01&dateTo=2026-04-30
GET /api/reports/shariah-audit?dateFrom=2026-04-01&dateTo=2026-04-30
GET /api/reports/branch?dateFrom=2026-04-01&dateTo=2026-04-30&branchId=1
GET /api/reports/export-history
```

## Validation checks
1. Use a `dateFrom` greater than `dateTo` and expect validation failure.
2. Use more than 12 months in one request and expect validation failure.
3. Call `shariah-audit` with `requestedBy=customer_portal` and expect restricted access.
