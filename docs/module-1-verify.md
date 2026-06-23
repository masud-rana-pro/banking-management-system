# Module 1 Verify Guide

## Scope
Playbook module: `Module 1: Role & Permission Management`

Frontend pages:
- `/admin/roles/dashboard`
- `/admin/roles`
- `/admin/roles/new`
- `/admin/roles/:id`
- `/admin/roles/:id/edit`
- `/admin/roles/:id/permissions`

Backend APIs:
- `GET /api/roles/dashboard-summary`
- `POST /api/roles/create`
- `GET /api/roles/list`
- `GET /api/roles/{id}`
- `PUT /api/roles/{id}`
- `DELETE /api/roles/{id}`
- `PUT /api/roles/{id}/restore`
- `GET /api/roles/dropdown`
- `GET /api/roles/permissions/{roleId}`
- `POST /api/roles/{id}/permissions/map`

## Optional Seed
If you want the module to have the seeded sample set, run:

```powershell
Get-Content -Raw 'I:\SBMS Copy\docs\module-1-manual-seed.sql' | & 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' -uroot -proot -D sbms
```

Expected seeded result:
- `roles = 7`
- `role_permission = 94`
- canonical roles only:
  - `SYSTEM_ADMIN`
  - `BRANCH_MANAGER`
  - `TELLER`
  - `OPERATIONS_OFFICER`
  - `INVESTMENT_OFFICER`
  - `SHARIAH_BOARD_MEMBER`
  - `CUSTOMER`

## API Smoke
Use [module-1-postman-samples.json](</I:\SBMS Copy\docs\module-1-postman-samples.json>) or test these:

```http
GET /api/roles/dashboard-summary
GET /api/roles/list
GET /api/roles/dropdown
GET /api/roles/1
GET /api/roles/permissions/1
POST /api/roles/create
PUT /api/roles/1
POST /api/roles/1/permissions/map
DELETE /api/roles/1
PUT /api/roles/1/restore
```

## UI Checks
1. Open `/auth/login`, select `SYSTEM_ADMIN`, and sign in.
2. Confirm you land on the first permitted module automatically.
3. Open `/admin/roles/dashboard` and verify summary cards load.
4. Open `/admin/roles` and verify search, filter, view, edit, archive/restore, and map-permissions actions.
5. Open `/admin/roles/new` and create a new role with unique `code` and `name`.
6. Open the created role detail page and verify basic info, permission matrix, status, and audit info.
7. Open `/admin/roles/{id}/edit` and update description or status.
8. Open `/admin/roles/{id}/permissions`, toggle some permissions, and save.
9. Sign out, sign back in with that role selected, and confirm sidebar/module visibility follows the mapped permissions.

## Business Rule Checks
1. Try creating a duplicate `role code` and expect validation failure.
2. Try creating a duplicate `role name` and expect validation failure.
3. Try archiving `SYSTEM_ADMIN` and expect rejection.
4. Try archiving a role assigned to an active user and expect rejection.
5. Verify inactive roles do not appear in `/api/roles/dropdown`.
6. Verify menu visibility depends on `role_permission` rows, not hardcoded role lists.

## RBAC Checks
1. Log in with a restricted role that only has `BRANCH_MANAGEMENT_ACCESS`.
2. Confirm branch menu/pages are visible.
3. Confirm unrelated menus like `Profit`, `Security`, `Reports` stay hidden.
4. Confirm a direct URL to an unauthorized module redirects away because of the permission guard.
