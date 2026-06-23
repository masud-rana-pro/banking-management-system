# Module 19 Verify Guide

Module 19 covers `Integration Management`.

## Routes

- `/integrations/dashboard`
- `/integrations/providers`
- `/integrations/providers/new`
- `/integrations/logs`
- `/integrations/provider-test`

## Seed State

After running [module-19-manual-seed.sql](/I:\SBMS Copy\docs\module-19-manual-seed.sql) the database should contain:

- `integration_provider = 15`
- `integration_execution_log = 15`

Workflow mix:

- `SUCCESS = 10`
- `FAILED = 3`
- `RETRY_PENDING = 2`
- archived providers = `2`
- active providers = `13`

## UI Checks

1. Open `/integrations/dashboard`.
2. Confirm cards show active providers, failed integrations, retry pending, last successful sync and success rate.
3. Confirm provider type summary and recent execution tables are populated.
4. Open `/integrations/providers`.
5. Confirm `View`, `Edit`, `Archive`, `Restore`, `Test Provider`, `View Logs`, print/export actions are visible.
6. Open any provider view and confirm masked API key/password, last test status and recent logs appear.
7. Open `/integrations/logs`.
8. Confirm filters work for keyword, execution status and provider id.
9. Open any `FAILED` or `RETRY_PENDING` row and use `Retry`.
10. Open `/integrations/provider-test`, select a provider and run test.
11. Confirm the generated execution log opens from the result panel.
12. Open `/notifications/dashboard` and `/verification/provider-test` and confirm integration shortcut buttons navigate correctly.

## API Checks

- `GET /api/integrations/dashboard-summary`
- `GET /api/integrations/providers/list`
- `GET /api/integrations/providers/{id}`
- `POST /api/integrations/providers/create`
- `PUT /api/integrations/providers/{id}`
- `DELETE /api/integrations/providers/{id}`
- `PUT /api/integrations/providers/{id}/restore`
- `POST /api/integrations/providers/{id}/test`
- `GET /api/integrations/logs/list`
- `GET /api/integrations/logs/{id}`
- `POST /api/integrations/logs/{id}/retry`

## SQL Checks

```sql
SELECT COUNT(*) FROM integration_provider;
SELECT COUNT(*) FROM integration_execution_log;

SELECT execution_status, COUNT(*) FROM integration_execution_log GROUP BY execution_status;
SELECT provider_type, COUNT(*) FROM integration_provider GROUP BY provider_type;
SELECT status, COUNT(*) FROM integration_provider GROUP BY status;
```

## Build Checks

```bash
cd stable-sbms-backend
mvn.cmd -q -DskipTests compile

cd ../stable-sbms-frontend
npx.cmd tsc -p tsconfig.app.json --noEmit
npx.cmd ngc -p tsconfig.app.json
```
