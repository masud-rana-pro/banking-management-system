# Module 6 Verify Guide

## 1. Open the module
- Go to `/customers/dashboard`
- Open `/customers/list`
- Open `/customers/search`

## 2. Check playbook coverage
- Dashboard should show summary cards for total, active, pending KYC, blocked, new this month, incomplete profile.
- Customer list should show action buttons: `View`, `Edit`, `Manage Address`, `Manage Identity`, `Status Action`, `Activate`, `Block`, `Archive`, `Restore`.
- Customer view should show profile, address summary, identity summary, timeline, print button, and workflow buttons.
- Address manage page should allow create and edit.
- Identity manage page should allow create and edit.
- Status action page should allow activate, block, archive and restore.

## 3. Data verification
- Active database for this project is `sbms`, not `sbms-backend`.
- `customer` table already had existing data in this project, so no delete was done.
- After running `module-6-manual-seed.sql`, `customer`, `customer_address`, and `customer_identity` should each be `15`.

## 4. Important backend rule
- Activate action fails if the customer has no address record or no identity document.
- Archive/restore works on record status.
- Search endpoint ignores archived records.

## 5. What to test manually
- Create a new customer.
- Add address from `Manage Address`.
- Add identity from `Manage Identity`.
- Activate from `Status Action`.
- Use `Print` / `Export` on list and `Print` on view.
- Confirm menu navigation works from `Customer Management`.

## 6. Postman support
- Request samples are in `docs/module-6-postman-samples.json`.
- Use created customer ids in the `{{customer_01_id}}` style placeholders for address/identity requests.
