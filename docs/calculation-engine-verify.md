# Calculation Engine Verification

1. Start backend from `I:\SBMS Copy\stable-sbms-backend` using `mvn spring-boot:run`.
2. Confirm calculation APIs respond:
   - `GET /api/calculations/dashboard-summary`
   - `POST /api/calculations/simulate`
3. Start frontend from `I:\SBMS Copy\stable-sbms-frontend` using `npm start`.
4. Open these pages and check navigation:
   - `/calculations/dashboard`
   - `/calculations/simulator`
5. Verify dashboard shows recent calculations, failed calculations, source-module splits and recent routed items.
6. Run multiple simulation payloads from `docs/calculation-engine-postman-samples.json`.
7. Confirm simulator returns:
   - formula name
   - total principal
   - total profit
   - total payable
   - periodic amount
   - reproducible schedule lines
8. Verify source-module shortcuts still work:
   - financing pages can open calculator
   - deposit scheme pages can open calculator
   - profit schedule pages can open calculator
9. Verify `Apply Result` routes users back into the relevant source-module flow instead of creating a standalone calculation record.

# Current Data Notes

- The playbook defines Calculation Engine as a support concept, not a dedicated master table module.
- No dedicated seed SQL is required; it reuses existing Financing, Deposit Scheme and Profit module data.
- `docs/calculation-engine-manual-seed.sql` is intentionally informational only.

# Validation Commands Used

- Backend compile: `mvn -q -DskipTests compile`
- Frontend TypeScript check: `npx tsc -p tsconfig.app.json --noEmit`
- Frontend Angular compiler check: `npx ngc -p tsconfig.app.json`
