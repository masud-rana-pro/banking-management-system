# Module 16 Verify Guide

Module 16 covers Shariah Review.

## Routes

- `/shariah/dashboard`
- `/shariah/cases`
- `/shariah/cases/:id`
- `/shariah/cases/:id/review`
- `/shariah/correction-queue`
- `/shariah/fatwa-certificates`
- `/shariah/annual-report`

## Seed State

After running [module-16-manual-seed.sql](/I:\SBMS%20Copy\docs\module-16-manual-seed.sql) the database should contain:

- `shariah_checklist_item = 15`
- `shariah_review_case = 15`
- `shariah_review_decision = 15`
- `shariah_review_checklist = 45`

Workflow mix:

- Pending review cases: `4`
- Approved cases: `5`
- Rejected cases: `3`
- Returned cases: `3`
- Decision trail mix: `SUBMITTED`, `CHECKLIST_UPDATED`, `APPROVED`, `REJECTED`, `RETURNED`

## UI Checks

1. Open `/shariah/dashboard`.
2. Confirm cards show pending cases, approved cases, rejected cases, correction requests and upcoming reviews.
3. Confirm recent cases, recent decisions and module breakdown widgets are populated.
4. Open `/shariah/cases`.
5. Confirm print/export buttons are visible and `Submit Case` form can open.
6. Confirm filters work for keyword, reference module and case status.
7. Confirm list actions work: `View`, `Review`, `Approve`, `Reject`, `Return`, `History`.
8. Open `/shariah/cases/:id`.
9. Confirm case source, checklist, remarks, reviewer identity and history render.
10. Open `/shariah/cases/:id/review`.
11. Toggle checklist items, add notes and click `Save Checklist`.
12. Test `Approve`, `Reject` and `Return` actions from the review page.
13. Open `/shariah/correction-queue` and confirm returned cases appear.
14. Open `/shariah/fatwa-certificates` and confirm approved cases can print certificate output.
15. Open `/shariah/annual-report` and confirm module breakdown, decision trail and case register render.

## Cross Navigation Checks

1. Open financing application list and view pages and confirm `Shariah Cases` and `Submit Review` navigation works.
2. Open contract list and view pages and confirm `Shariah Cases` and `Submit Review` navigation works.
3. Confirm sidebar contains `Shariah Review` with dashboard, case list, correction queue, fatwa certificates and annual report links.

## SQL Checks

```sql
SELECT COUNT(*) FROM shariah_checklist_item;
SELECT COUNT(*) FROM shariah_review_case;
SELECT COUNT(*) FROM shariah_review_decision;
SELECT COUNT(*) FROM shariah_review_checklist;

SELECT case_status, COUNT(*) FROM shariah_review_case GROUP BY case_status;
SELECT decision, COUNT(*) FROM shariah_review_decision GROUP BY decision;
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
