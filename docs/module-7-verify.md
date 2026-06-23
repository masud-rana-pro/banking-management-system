# Module 7 Verify Guide

## 1. Open the module
- Go to `/kyc/dashboard`
- Open `/kyc/list`
- Open `/kyc/approval-queue`

## 2. Check playbook coverage
- Dashboard should show pending KYC, verified KYC, rejected KYC, resubmission queue, high-risk customers and risk summary.
- KYC list should show action buttons: `View`, `Edit`, `Review`, `Upload Document`, `History`, `Submit`, `Verify`, `Approve`, `Reject`, `Return`.
- KYC view should show customer + KYC profile, risk flags, documents, decision history, remarks, reviewer and timestamps, plus print button.
- KYC review page should allow saving review info and running verify / approve / reject / return workflow.
- Approval queue should show submitted / verified / sent-back KYC records.
- Document upload page should allow customer document metadata add.
- History page should show workflow audit trail.

## 3. Data verification
- Active database for this project is `sbms`.
- Module 7 sample data is already inserted in `sbms`.
- `kyc_profile`, `customer_document`, and `kyc_decision_history` should each be `15`.
- Customer pages now have actionable `Open KYC` buttons that should navigate to existing KYC or prefill new KYC create flow.
- KYC review, history, document upload and approval queue pages now have extra quick navigation buttons so you can move between `View`, `Review`, `Documents`, `History`, `List` and `Dashboard` without manual URL changes.

## 4. Important backend rules
- KYC cannot proceed without required documents.
- Risk level is required for profile create/update and workflow actions.
- Reviewer identity is stored during verify / approve / reject / return actions.
- Return action requires correction remarks.
- Approve action updates customer lifecycle forward, reject updates customer status to rejected.

## 5. What to test manually
- Create a new KYC profile from `/kyc/new`.
- Upload one document from `/kyc/:id/documents`.
- Submit from list or review page.
- Verify from review page.
- Approve / reject / return from review or approval queue.
- Open customer pages and use `Open KYC` buttons from list, view and status action.
- Use print/export on KYC list and print on KYC view.

## 6. Postman support
- Request samples are in `docs/module-7-postman-samples.json`.
- Seed SQL is in `docs/module-7-manual-seed.sql`.
- Use `{{customer_01_id}}` and `{{kyc_01_id}}` style placeholders with your created ids when calling APIs manually.
