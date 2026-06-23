# Module 21 Verify Guide

## Scope
Playbook module: `Module 21: Security / Audit / Investigation`

Frontend pages:
- `/security/dashboard`
- `/security/events`
- `/security/events/:id`
- `/security/suspicious-activities`
- `/security/suspicious-activities/:id`
- `/security/audit-logs`
- `/security/audit-logs/:id`
- `/security/investigation-cases`
- `/security/investigation-cases/:id`
- `/security/investigation-cases/:id/action`

## Seed
Run:

```powershell
Get-Content -Raw 'I:\SBMS Copy\docs\module-21-manual-seed.sql' | & 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' -uroot -proot -D sbms
```

Expected seeded counts:

```sql
SELECT COUNT(*) FROM security_event_log WHERE remarks LIKE '%SECURITY_SEED%';
SELECT COUNT(*) FROM audit_log WHERE performed_by = 'SECURITY_SEED';
SELECT COUNT(*) FROM investigation_case WHERE opened_by = 'SECURITY_SEED';
```

Expected:
- `security_event_log seeded = 15`
- `audit_log seeded = 15`
- `investigation_case seeded = 15`

## API smoke
Use [module-21-postman-samples.json](</I:\SBMS Copy\docs\module-21-postman-samples.json>) or hit:

```http
GET /api/security/dashboard-summary
GET /api/security/events/list
GET /api/security/suspicious-activities/list
GET /api/security/audit-logs/list
GET /api/security/investigation-cases/list
POST /api/security/investigation-cases/{id}/assign
POST /api/security/investigation-cases/{id}/close
```

## UI checks
1. Open `/security/dashboard` and confirm all 6 summary cards load.
2. Open `/security/events` and verify filter, print, export and view button.
3. Open `/security/suspicious-activities` and confirm only high-risk / suspicious items appear.
4. Open `/security/audit-logs` and inspect old/new JSON diff in a detail page.
5. Open `/security/investigation-cases` and test assign/close from `/security/investigation-cases/:id/action`.
6. Re-open the case detail page and confirm audit trail updates after assign/close.

## Business rule checks
1. Try assign without `assignedTo` and expect validation failure.
2. Try close with short remarks and expect validation failure.
3. Try open `/api/security/suspicious-activities/{id}` for a non-suspicious event and expect validation failure.
