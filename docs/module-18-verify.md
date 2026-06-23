# Module 18 Verify Guide

Module 18 covers `Notification & Alerts`.

## Routes

- `/notifications/dashboard`
- `/notifications/templates`
- `/notifications/templates/new`
- `/notifications/event-rules`
- `/notifications/event-rules/new`
- `/notifications/logs`
- `/notifications/retry-queue`

## Seed State

After running [module-18-manual-seed.sql](/I:\SBMS Copy\docs\module-18-manual-seed.sql) the database should contain:

- `notification_template = 15`
- `notification_event = 15`
- `notification_log = 15`

Workflow mix:

- `SENT = 10`
- `FAILED = 3`
- `RETRY_QUEUED = 2`
- `PUSH channel logs = 2`
- archived template/event rows = `1` each

## UI Checks

1. Open `/notifications/dashboard`.
2. Confirm cards show sent today, failed today, retry queue and failed deliveries.
3. Confirm channel-wise summary and recent delivery activity are populated.
4. Open `/notifications/templates`.
5. Confirm print/export buttons and `View`, `Edit`, `Archive`, `Restore` actions show.
6. Open `/notifications/event-rules`.
7. Confirm event list is populated and `View Logs` action filters logs by event code.
8. Open `/notifications/logs`.
9. Confirm keyword, channel and delivery-status filters work.
10. Open any failed or retry-queued row and use `Retry`.
11. Open `/notifications/retry-queue`.
12. Confirm only failed/retry-queued items are shown.

## API Checks

- `GET /api/notifications/dashboard-summary`
- `GET /api/notifications/templates/list`
- `GET /api/notifications/event-rules/list`
- `GET /api/notifications/logs/list`
- `POST /api/notifications/logs/{id}/retry`

## SQL Checks

```sql
SELECT COUNT(*) FROM notification_template;
SELECT COUNT(*) FROM notification_event;
SELECT COUNT(*) FROM notification_log;

SELECT delivery_status, COUNT(*) FROM notification_log GROUP BY delivery_status;
SELECT channel_type, COUNT(*) FROM notification_log GROUP BY channel_type;
```

## Build Checks

```bash
cd stable-sbms-backend
mvn.cmd -q -DskipTests compile

cd ../stable-sbms-frontend
npx.cmd tsc -p tsconfig.app.json --noEmit
npx.cmd ngc -p tsconfig.app.json
```
