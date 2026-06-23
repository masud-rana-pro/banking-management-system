# Module 22 Verify Guide

## Scope
Playbook module: `Module 22: Workflow Support`

Frontend pages:
- `/workflow/dashboard`
- `/workflow/history`
- `/workflow/history/:id`
- `/workflow/pending`
- `/workflow/my-submissions`

## Seed
Run:

```powershell
Get-Content -Raw 'I:\SBMS Copy\docs\module-22-manual-seed.sql' | & 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' -uroot -proot -D sbms
```

Expected seeded counts:

```sql
SELECT COUNT(*) FROM workflow_history WHERE remarks LIKE '%WORKFLOW_SEED%';
SELECT COUNT(*) FROM workflow_comment WHERE comment_by = 'WORKFLOW_SEED';
```

Expected:
- `workflow_history seeded = 15`
- `workflow_comment seeded = 15`

## API smoke
Use [module-22-postman-samples.json](</I:\SBMS Copy\docs\module-22-postman-samples.json>) or hit:

```http
GET /api/workflows/dashboard-summary
GET /api/workflows/history/list
GET /api/workflows/history/{id}
GET /api/workflows/pending
GET /api/workflows/my-submissions
```

## UI checks
1. Open `/workflow/dashboard` and confirm all summary cards load.
2. Open `/workflow/history` and verify search, print, export, `View`, and `Open Source` actions.
3. Open `/workflow/history/:id` and confirm transition detail, comments timeline, `Open Source`, and export JSON work.
4. Open `/workflow/pending` and verify only pending-like statuses appear.
5. Open `/workflow/my-submissions?actor=SYSTEM` and confirm seeded rows load for the actor filter.

## Business rule checks
1. `Pending approvals` should count records with statuses like `SUBMITTED`, `PENDING`, `PENDING_REVIEW`, `UNDER_REVIEW`, `RETURNED`, `ASSIGNED`.
2. `Recent completed tasks` should count `APPROVED`, `CLOSED`, `COMPLETED`, `VERIFIED`, `POSTED`.
3. `Workflow bottlenecks` should count `RETURNED`, `FAILED`, and `REJECTED`.
