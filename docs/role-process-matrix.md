# SBMS Role Process Matrix

This matrix is the current working ownership model for role-based create/open/verify/approve/action flow after removing action-level OTP.

## Core rule

- Login OTP stays enabled.
- Forgot password / password recovery OTP stays enabled.
- Business action pages do not require OTP.
- Access is controlled by route guard, visible action button state, and backend permission checks.

## Role ownership

### Customer / Account Holder

- Can view own account, financing, card, statement, and investment/deposit scheme surfaces.
- Can request customer statements.
- Cannot perform admin approval actions.

### Branch Staff

- Creates customer intake.
- Manages customer address and identity collection.
- Creates and submits account opening requests.
- Uploads KYC documents.
- Can request customer statements for service support.

### Teller

- Performs cash deposit, withdrawal, transfer, cheque clearing.
- Creates standing instructions.
- Handles card PIN event operations.
- Does not approve KYC, financing, or role actions.

### Operations Officer

- Verifies and decides KYC.
- Verifies and decides account opening requests.
- Activates/blocks/freezes/closes accounts.
- Activates/blocks/unblocks cards.
- Reverses transactions.
- Generates contracts.
- Requests branch statements.

### Investment Officer (Financing)

- Creates, edits, submits financing applications.
- Verifies financing applications.
- Moves financing cases to review.
- Approves, rejects, returns, and disburses financing.
- Collects financing repayment.
- Generates contracts.
- Creates shariah review submissions when needed.

### Shariah Board Member

- Saves checklist.
- Approves, rejects, and returns shariah review cases.
- Performs shariah contract signing.

### Branch Manager

- Assigns branch users.
- Manages teller limit and vault operations.
- Approves branch-level account actions.
- Can reverse transactions.
- Creates and submits monthly closing runs.

### MIS Officer

- Accesses reporting dashboard and export history.
- Runs report/statements and branch statement requests.
- Reviews generated outputs and reporting trends.

### Compliance Officer

- Reviews KYC, shariah, reporting, and security-sensitive outcomes.
- Approves, rejects, and reopens monthly closing runs.
- Uses reporting and security audit surfaces.

### Internal Auditor

- Reviews reports, statements, verification history, and security audit data.
- Does not perform operational approvals by default.

### Recovery Officer

- Uses financing repayment/recovery actions.
- Reviews financing and related reporting data.

### Treasury / Finance Officer

- Runs profit posting.
- Reviews monthly closing outcomes.
- Approves, rejects, and reopens monthly closing runs.

### System Admin

- Manages roles, permissions, users, configuration, and audit layers.
- Can access all modules through full permission bundle.

## Current implementation note

- UI routes already use permission guard.
- Action buttons on key admin, financing, shariah, transaction, contract, and zakat pages are permission-gated.
- Backend controllers enforce the same permissions without step-up OTP.
