# Module 5 Verification

1. Start backend from `I:\SBMS Copy\stable-sbms-backend` using `mvn spring-boot:run`.
2. Confirm ATM APIs respond:
   - `GET /api/atm-terminals/dashboard-summary`
   - `GET /api/atm-terminals/list`
   - `GET /api/atm-terminals/cash-bin/list`
   - `GET /api/atm-terminals/replenishment/list`
   - `GET /api/atm-terminals/reconciliation/list`
   - `GET /api/atm-terminals/device-journal`
3. Start frontend from `I:\SBMS Copy\stable-sbms-frontend` using `npm start`.
4. Open these pages and check navigation:
   - `/atm/dashboard`
   - `/atm/terminals`
   - `/atm/cash-bins`
   - `/atm/replenishments`
   - `/atm/reconciliations`
   - `/atm/device-journal`
5. Verify list pages show search, filter, print, export and action buttons.
6. Verify view pages show print button and module summary data.
7. Create one terminal, one cash bin, one replenishment and one reconciliation from UI.
8. Open terminal view and confirm cash bin summary, latest replenishment, reconciliation summary and device journal all update.
9. Use `docs/module-5-postman-samples.json` to add the remaining 13 records per table from Postman.
10. If `terminal_reconciliation` table is not visible before first run, restart backend once; Hibernate should create it automatically because `spring.jpa.hibernate.ddl-auto=update`.

# Validation Commands Used

- Backend compile: `mvn -q -DskipTests compile`
- Frontend TypeScript check: `npx tsc -p tsconfig.app.json --noEmit`
- Frontend Angular compiler check: `npx ngc -p tsconfig.app.json`

# Known Existing Project Issue

- `npm run build` is currently blocked by an existing project dependency/version issue: `Invalid version: "18.5-18.7"`.
- This did not come from the new Module 5 changes; backend compile and Angular compile checks passed.
