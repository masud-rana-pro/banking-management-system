# Module 2 Verify Guide

## Scope
Playbook module: `Module 2: User Management`

Frontend pages:
- `/admin/users/dashboard`
- `/admin/users`
- `/admin/users/new`
- `/admin/users/:id`
- `/admin/users/:id/edit`
- `/admin/users/:id/assign-role`
- `/admin/users/:id/reset-password`
- `/admin/users/:id/lock-unlock`

Backend APIs:
- `GET /api/users/dashboard-summary`
- `POST /api/users/create`
- `GET /api/users/list`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`
- `PUT /api/users/{id}/restore`
- `GET /api/users/dropdown`
- `GET /api/users/search`
- `POST /api/users/{id}/lock`
- `POST /api/users/{id}/unlock`
- `POST /api/users/{id}/reset-password`
- `POST /api/users/{id}/assign-role`
- `GET /api/users/{id}/history`

## Seed
Run:

```powershell
Get-Content -Raw 'I:\SBMS Copy\docs\module-2-manual-seed.sql' | & 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' -uroot -proot -D sbms
```

Expected seeded result:
- `users = 20`
- `user_role = 20`
- `user_session = 20`
- users are distributed only across the 7 canonical roles from Module 1

Default working admin credential:
- Username: `Admin01`
- Password: `Admin@123!`

Note:
- login is now real credential-based with bearer token protection
- several active seeded users were also aligned to `Admin@123!` for verification convenience

## UI Checks
1. Log in from `/auth/login` using `Admin01 / Admin@123!`.
2. Open `/admin/users/dashboard` and verify summary cards, role breakdown, branch breakdown, and last login list.
3. Open `/admin/users` and verify search, role filter, branch filter, status filter, view, edit, assign-role, reset-password, lock/unlock, archive/restore actions.
4. Open `/admin/users/new` and create a branch-bound staff user.
5. Open `/admin/users/:id` and confirm profile, mapped roles, branch, verification flags, and history summary.
6. Open `/admin/users/:id/assign-role` and change the role.
7. Open `/admin/users/:id/reset-password` and submit matching passwords.
8. Open `/admin/users/:id/lock-unlock` and switch lock state.

## Business Rule Checks
1. Try duplicate `username` and expect validation failure.
2. Try duplicate `email` and expect validation failure.
3. Try assigning a branch-bound role without a branch and expect validation failure.
4. Try assigning `CUSTOMER` role to a `STAFF` user and expect validation failure.
5. Verify password reset never returns plain password in any response.

## API Smoke
Use [module-2-postman-samples.json](</I:\SBMS Copy\docs\module-2-postman-samples.json>) or hit:

```http
GET /api/users/dashboard-summary
GET /api/users/list
GET /api/users/dropdown
GET /api/users/1
GET /api/users/1/history
POST /api/users/create
PUT /api/users/1
POST /api/users/1/assign-role
POST /api/users/1/lock
POST /api/users/1/unlock
POST /api/users/1/reset-password
DELETE /api/users/1
PUT /api/users/1/restore
```
