# Module 4 Verification

1. Start backend from `I:\SBMS Copy\stable-sbms-backend` using `mvn spring-boot:run`.
2. Confirm branch APIs respond:
   - `GET /api/branches/dashboard-summary`
   - `GET /api/branches/list`
   - `GET /api/branches/dropdown`
   - `GET /api/branches/assignments/list`
   - `GET /api/branches/teller-limit/list`
   - `GET /api/branches/vault/list`
   - `GET /api/branches/cash-ledger`
3. Start frontend from `I:\SBMS Copy\stable-sbms-frontend` using `npm start`.
4. Open these pages and check navigation:
   - `/branches/dashboard`
   - `/branches/list`
   - `/branches/assignments`
   - `/branches/teller-limits`
   - `/branches/vault`
   - `/branches/cash-ledger`
   - `/branches/inter-branch-transfer`
   - `/branches/eod-summary`
5. Verify branch list shows search, filters, action buttons, archive/restore and view/edit flows.
6. Verify branch view shows profile, manager/assignment summary, teller limit summary, latest vault balance, latest cash ledger summary and EOD snapshot.
7. Verify branch assignment page can create/update assignment and reflect primary assignment changes.
8. Verify teller limit page can create/update teller limits and show status badges.
9. Verify vault page can open a vault, close a vault, print the view page and refresh closing status.
10. Verify cash ledger list shows ledger source type, debit/credit, running balance and print/export behavior if present.
11. Verify inter-branch transfer page can prepare a review memo and print the prepared content.
12. Verify EOD summary page can filter by branch/date and compute opening balance, cash in, cash out and closing balance totals.
13. Apply `docs/module-4-manual-seed.sql` if you want extra teller limit, vault and cash ledger coverage.
14. Use `docs/module-4-postman-samples.json` if you want extra branch, assignment, teller limit or vault test records through API.

# Current Data Notes

- Current DB already has `20` branches and `20` branch assignments.
- `docs/module-4-manual-seed.sql` tops up teller limit, vault balance and cash ledger data for better dashboard/EOD coverage.
- Cash ledger is read-only in current implementation; it is expected to come from seed/business flows rather than a direct create API.

# Validation Commands Used

- Backend compile: `mvn -q -DskipTests compile`
- Frontend TypeScript check: `npx tsc -p tsconfig.app.json --noEmit`
- Frontend Angular compiler check: `npx ngc -p tsconfig.app.json`
