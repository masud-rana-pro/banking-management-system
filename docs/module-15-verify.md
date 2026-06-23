# Module 15 Verify Guide

Module 15 covers Contract Management.

## Routes

- `/contracts/dashboard`
- `/contracts/templates`
- `/contracts/templates/new`
- `/contracts/templates/:id/edit`
- `/contracts/templates/:id`
- `/contracts/list`
- `/contracts/generate`
- `/contracts/:id`
- `/contracts/:id/sign`
- `/contracts/:id/versions`

## Seed State

After running [module-15-manual-seed.sql](/I:\SBMS%20Copy\docs\module-15-manual-seed.sql) the database should contain:

- `contract_template = 15`
- `contract = 15`
- `contract_version = 31`

Workflow mix:

- Draft contracts: `4`
- Active contracts pending final sign: `6`
- Locked contracts: `5`
- Version history events: `GENERATED`, `CUSTOMER_SIGN`, `SHARIAH_SIGN`

## UI Checks

1. Open `/contracts/dashboard`.
2. Confirm cards show total contracts, pending signatures, active/locked contracts, total versions and draft contracts.
3. Confirm recent contracts and recent templates widgets are populated.
4. Open `/contracts/templates`.
5. Confirm print/export buttons are visible and list actions work: `View`, `Edit`, `Generate`, `Archive/Restore`.
6. Open `/contracts/templates/new` and create a template using any sample from [module-15-postman-samples.json](/I:\SBMS%20Copy\docs\module-15-postman-samples.json).
7. Open `/contracts/list`.
8. Confirm filters work for customer, template, reference module and contract status.
9. Confirm list actions work: `View`, `Sign`, `Versions`, `Generate`.
10. Open any draft contract and verify the sign button is available.
11. Open `/contracts/:id/sign` for an active pending contract and test `Customer Sign` or `Shariah Sign`.
12. Open `/contracts/:id` and confirm contract text, sign status, signer identity/time, template version, source module and quick actions render.
13. Open `/contracts/:id/versions` and confirm version snapshots appear in timeline order.
14. Use `Print` from view page and confirm only content is printed.
15. Use `Download` from contract view and confirm a text file is generated.

## Cross Navigation Checks

1. Open any financing application list or view page and confirm `Contracts` and `Generate Contract` navigation works.
2. Open any customer view page and confirm `Contracts` and `Generate Contract` buttons work.
3. Open any account view page and confirm `Contracts` and `Generate Contract` buttons work.
4. Confirm sidebar contains `Contracts` with dashboard, template list, new template, contract list and generate contract links.

## SQL Checks

```sql
SELECT COUNT(*) FROM contract_template;
SELECT COUNT(*) FROM contract;
SELECT COUNT(*) FROM contract_version;

SELECT contract_status, COUNT(*) FROM contract GROUP BY contract_status;
SELECT change_type, COUNT(*) FROM contract_version GROUP BY change_type;
```

## Build Checks

Run these if needed:

```bash
cd stable-sbms-backend
mvn.cmd -q -DskipTests compile

cd ../stable-sbms-frontend
npx.cmd tsc -p tsconfig.app.json --noEmit
npx.cmd ngc -p tsconfig.app.json
```
