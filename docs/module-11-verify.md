# Module 11 Verify Guide

## 1. Backend / frontend open

- Backend should be running at `http://localhost:8080`
- Frontend should be running at `http://localhost:4200` or `http://127.0.0.1:4201`

## 2. Seed data

- Current environment seed file: [module-11-manual-seed.sql](/I:/SBMS%20Copy/docs/module-11-manual-seed.sql)
- After running the SQL, expected counts:
  - `card = 15`
  - `card_event_log = 15`
  - `card_pin_event = 15`

## 3. Pages to check

- `/cards/dashboard`
- `/cards/list`
- `/cards/new`
- `/cards/:id`
- `/cards/:id/activate`
- `/cards/:id/block-unblock`
- `/cards/:id/pin-events`
- `/cards/atm-cdm-transactions/list`

## 4. Functional checks

- Dashboard should show total cards, active cards, blocked cards, expiring soon, card txn count, pending activations, card usage alerts today.
- Card list should support search, filter, print, export, view, edit, activate, block/unblock, replace, renew, PIN events, archive/restore.
- Card view should show card profile, customer/account linkage, event log, PIN log and print action.
- Activation page should activate card and optionally record PIN generated event.
- Block/unblock page should block with reason and unblock from blocked state.
- PIN event page should allow adding a new PIN event and show history.
- ATM/CDM usage page should show transaction and alert rows with print/export.

## 5. Navigation checks

- Customer view should show `View Cards` and `Issue Card`.
- Account view should show `Card List` and `Issue Card`.
- Account list row actions should open card list and issue card flow.
- Sidebar should show `Card Management` with dashboard/list/issue/ATM-CDM navigation.

## 6. API smoke checks

- `GET /api/cards/dashboard-summary`
- `GET /api/cards/list`
- `GET /api/cards/{id}`
- `GET /api/cards/{id}/events`
- `GET /api/cards/{id}/pin-events`
- `GET /api/cards/atm-cdm-transactions`

## 7. DB quick check

```sql
SELECT COUNT(*) FROM card;
SELECT COUNT(*) FROM card_event_log;
SELECT COUNT(*) FROM card_pin_event;
```
