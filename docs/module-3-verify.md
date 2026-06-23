# Module 3 Verify Guide

## Scope
Playbook module: `Module 3: Lookup / Config`

Frontend pages:
- `/lookups/dashboard`
- `/lookups/types`
- `/lookups/types/new`
- `/lookups/types/:id`
- `/lookups/types/:id/edit`
- `/lookups/values`
- `/lookups/values/new`
- `/lookups/values/:id`
- `/lookups/values/:id/edit`

## Seed
Run:

```powershell
Get-Content -Raw 'I:\SBMS Copy\docs\module-3-manual-seed.sql' | & 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' -uroot -proot -D sbms
```

Expected seeded counts:

```sql
SELECT COUNT(*) FROM lookup_type WHERE description LIKE '%LOOKUP_SEED%';
SELECT COUNT(*) FROM lookup_value WHERE extra_data LIKE '%LOOKUP_SEED%';
```

Expected:
- `lookup_type seeded = 15`
- `lookup_value seeded = 32`

## API smoke
Use [module-3-postman-samples.json](</I:\SBMS Copy\docs\module-3-postman-samples.json>) or hit:

```http
GET /api/lookups/dashboard-summary
GET /api/lookups/types/list
GET /api/lookups/types/{id}
POST /api/lookups/types/create
PUT /api/lookups/types/{id}
GET /api/lookups/values/list
GET /api/lookups/values/{id}
GET /api/lookups/values/by-type/{typeCode}
GET /api/lookups/dropdown/{typeCode}
```

## UI checks
1. Open `/lookups/dashboard` and confirm summary cards load.
2. Open `/lookups/types` and verify search, print, export, archive/restore and view/edit actions.
3. Open `/lookups/types/:id` and confirm child values appear.
4. Open `/lookups/values` and verify filter by type, keyword search, print/export and archive/restore.
5. Open `/lookups/values/:id` and confirm extra data and sort order render.

## Business rule checks
1. Try duplicate `typeCode` and expect validation failure.
2. Try duplicate `valueCode` inside the same type and expect validation failure.
3. Verify `GET /api/lookups/dropdown/{typeCode}` only returns `ACTIVE` values.
