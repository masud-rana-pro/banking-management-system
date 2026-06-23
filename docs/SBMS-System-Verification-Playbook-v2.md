# SBMS System Verification Playbook v2

Date: 19 May 2026  
Workspace: `I:\SBMS Copy`

## Purpose

This v2 playbook is the current-state demo and verification guide for SBMS.

It is designed for two practical uses:

- live project demonstration
- structured verification and rehearsal

This version is based on the current implemented project state, including the latest dashboards, reporting flows, accounting views, export history, and journal drill-down capability.

## Current State Summary

Current working scope includes:

- 24 frontend feature groups
- 14 practical process groups for live demo
- role-based access and route guards
- workflow-driven approval surfaces
- preview, print, download and export flows
- enhanced dashboards across major business modules
- management reporting and ledger-based internal P and L

## Practical Process Count

For live demonstration and rehearsal, treat the current system as **14 major process groups**:

1. System Administration
2. Lookup and Configuration
3. Branch Setup and Cash Control
4. Customer Onboarding
5. KYC Verification Workflow
6. Account Opening Workflow
7. Core Transactions
8. Deposit Scheme Process
9. Profit Management
10. Cards and ATM/CDM
11. Statement Generation
12. Financing, Contract and Shariah
13. Zakat, Notification, Integration and Verification
14. Workflow, Security, Reporting and Accounting

## Main Roles For Demo

Primary roles used in the current project demonstration:

1. `SYSTEM_ADMIN`
2. `BRANCH_MANAGER`
3. `OPERATIONS_OFFICER`
4. `TELLER`
5. `INVESTMENT_OFFICER`
6. `SHARIAH_BOARD_MEMBER`
7. `CUSTOMER`

## Role-Based Demo Strategy

### Minimum safe login set

If the goal is a smooth live demo without excessive switching, keep these logins ready:

1. `SYSTEM_ADMIN`
2. `OPERATIONS_OFFICER`
3. `TELLER`
4. `INVESTMENT_OFFICER`
5. `SHARIAH_BOARD_MEMBER`

### Full extended login set

If the audience asks "who performs which task", keep these additional roles ready:

- `BRANCH_MANAGER`
- `CUSTOMER`

### Exact login switching order

Use this order in a real presentation:

1. `SYSTEM_ADMIN`
2. `OPERATIONS_OFFICER`
3. `TELLER`
4. `INVESTMENT_OFFICER`
5. `SHARIAH_BOARD_MEMBER`
6. back to `SYSTEM_ADMIN`

### Which login should cover which modules

#### SYSTEM_ADMIN

- header and global shell
- role management
- user management
- lookup and configuration
- branch control overview
- integrations
- notifications overview
- reporting
- accounting close-out

#### OPERATIONS_OFFICER

- customer onboarding
- KYC
- account opening
- deposit schemes
- profit module
- statement generation
- workflow queue visibility

#### TELLER

- deposit
- withdraw
- transfer
- transaction list

#### INVESTMENT_OFFICER

- financing dashboard
- financing applications
- review and disbursement
- contract generation

#### SHARIAH_BOARD_MEMBER

- shariah review
- contract shariah sign
- zakat governance

### Demo discipline rules

- do not switch logins unless the next role actually owns the next task
- do not create unnecessary records during the live demo
- use already-tested records where possible
- always finish on reports and accounting, not on a raw form screen

## What Each Role Performs

### SYSTEM_ADMIN

- create and manage roles
- create and manage users
- map permissions
- maintain lookups and configuration
- monitor reports, export history, integrations, workflow, security and accounting

### BRANCH_MANAGER

- review branch-level control surfaces
- monitor teller limits and vault operations
- monitor branch-level approvals and closing readiness

### OPERATIONS_OFFICER

- onboard customers
- manage KYC progression
- review account opening requests
- manage statement and operational servicing
- review workflow queues

### TELLER

- perform deposit, withdrawal and transfer actions
- initiate service-facing operational steps

### INVESTMENT_OFFICER

- manage financing products
- manage financing review and approval progression
- handle disbursement and repayment context
- generate business contracts

### SHARIAH_BOARD_MEMBER

- review shariah cases
- approve, reject or return shariah files
- sign contracts after customer-side completion
- review zakat and charity governance flows

### CUSTOMER

- customer statement request origin
- financing request origin where applicable
- customer-context flow explanation only

## Demo Environment Preparation

Before the final rehearsal, confirm:

- backend is running
- frontend is running
- the latest code is loaded
- sample users for the roles above are available
- branch, customer, KYC, account, transaction, financing and report data are visible
- report export history is already working
- Management P and L, Trial Balance and Ledger Profit and Loss pages are loading correctly

## 20-Minute Live Demo Flow

Use this if you need one clean narrative for tomorrow:

1. Login as `SYSTEM_ADMIN`
2. Show header, search, time, theme and profile
3. Show Roles and Users
4. Show Lookup and Config
5. Show Branch Dashboard, Assignments, Teller Limits and Vault
6. Switch to `OPERATIONS_OFFICER`
7. Show Customer Dashboard and Customer List
8. Show KYC Dashboard and Approval Queue
9. Show Account Opening Requests and Account List
10. Show Deposit Scheme and Profit Dashboard
11. Show Statement Dashboard
12. Switch to `TELLER`
13. Show Deposit, Withdraw, Transfer and Transaction List
14. Switch to `INVESTMENT_OFFICER`
15. Show Financing Dashboard, Application Review and Disbursement
16. Show Contract Generate
17. Switch to `SHARIAH_BOARD_MEMBER`
18. Show Shariah Dashboard and Case Review
19. Show Zakat Dashboard
20. Switch back to `SYSTEM_ADMIN`
21. Show Notification, Integration, Workflow and Security dashboards
22. Show Report Dashboard
23. Show Management P and L
24. Show Expense Register
25. Show Trial Balance with journal drill-down
26. Show Ledger Profit and Loss as the final executive close

## Suggested Verification Sequence

If you are practicing module by module, use this order:

1. Administration
2. Branch
3. Customer
4. KYC
5. Accounts
6. Transactions
7. Deposit Schemes
8. Profit
9. Cards and ATM
10. Statements
11. Financing
12. Contracts
13. Shariah
14. Zakat
15. Notifications
16. Integrations
17. Verification
18. Workflow
19. Security
20. Reports
21. Management P and L
22. Trial Balance
23. Ledger Profit and Loss

## Process Flow Blueprints

Use the same verification pattern for every process:

- `Business Goal`
- `Start Role`
- `Start Screen`
- `Step-by-step flow`
- `Next module handoff`
- `What status or result should appear`
- `What to show in demo`
- `What evidence to save`
- `Fallback page if one page is risky in live demo`

## Process 1: System Administration

Business Goal:

- establish access, user identity, permission boundaries and admin control

Start Role:

- `SYSTEM_ADMIN`

Start Screen:

- `Admin -> Roles Dashboard`

Step-by-step flow:

1. Open Roles Dashboard
2. Open Roles list
3. Open one role and show permission mapping
4. Open Users Dashboard
5. Open Users list
6. Open one user and show assign role, reset password and lock-unlock actions

Next module handoff:

- moves into `Lookup and Configuration`

What status or result should appear:

- role and user ownership are visible
- user administration actions are available

What to show in demo:

- roles dashboard
- role list
- user dashboard
- user list

What evidence to save:

- roles screen
- users screen
- one user detail with role action area

Fallback page if one page is risky in live demo:

- use `Users Dashboard` if user edit form is risky

## Process 2: Lookup And Configuration

Business Goal:

- maintain central master data and configurable values used across modules

Start Role:

- `SYSTEM_ADMIN`

Start Screen:

- `Lookups -> Dashboard`

Step-by-step flow:

1. Open Lookup Dashboard
2. Open Lookup Types
3. Open Lookup Values
4. Explain that these values feed forms and dropdown-driven logic

Next module handoff:

- moves into `Branch Setup and Cash Control`

What status or result should appear:

- lookup lists are loaded and reusable configuration surfaces are visible

What to show in demo:

- type list
- value list

What evidence to save:

- lookup type screen
- lookup value screen

Fallback page if one page is risky in live demo:

- use Lookup Dashboard only

## Process 3: Branch Setup And Cash Control

Business Goal:

- prepare branch structure, staff assignment, teller authority and vault visibility

Start Role:

- `SYSTEM_ADMIN`

Start Screen:

- `Branch -> Dashboard`

Step-by-step flow:

1. Show Branch Dashboard
2. Open Branch List
3. Open Assignments and explain branch-user mapping
4. Open Teller Limits
5. Open Vault list and one vault record
6. Open Cash Ledger
7. Open Inter-Branch Transfer
8. Open EOD Summary

Next module handoff:

- moves into `Customer Onboarding`

What status or result should appear:

- branches, assignments, teller limits and vault operations are visible

What to show in demo:

- dashboard
- assignments
- teller limits
- vault

What evidence to save:

- branch dashboard
- teller limits page
- vault page

Fallback page if one page is risky in live demo:

- use Branch Dashboard plus Vault List only

## Process 4: Customer Onboarding

Business Goal:

- create and maintain the core customer profile before compliance and account creation

Start Role:

- `OPERATIONS_OFFICER`

Start Screen:

- `Customer -> Dashboard`

Step-by-step flow:

1. Show Customer Dashboard
2. Open Customer List
3. Open a customer detail page
4. Show address and identity areas
5. Show status action
6. Show customer timeline

Next module handoff:

- `Customer -> KYC -> Account Opening Request -> Account`

What status or result should appear:

- customer profile is visible with lifecycle and verification context

What to show in demo:

- dashboard
- list/grid if available
- customer detail page

What evidence to save:

- customer dashboard
- one customer profile

Fallback page if one page is risky in live demo:

- use Customer List and one Customer View only

## Process 5: KYC Verification Workflow

Business Goal:

- verify compliance readiness and move the customer profile through controlled approval states

Start Role:

- `OPERATIONS_OFFICER`

Start Screen:

- `KYC -> Dashboard`

Step-by-step flow:

1. Open KYC Dashboard
2. Open KYC List
3. Open one KYC profile
4. Open Approval Queue
5. Open Review page
6. Explain submit, verify, approve, reject and return
7. Show documents and decision history if available

Next module handoff:

- `KYC -> Account Opening Request`

What status or result should appear:

- clear approval state progression and review trace

What to show in demo:

- dashboard
- approval queue
- review page

What evidence to save:

- KYC queue
- KYC review screen
- decision history if visible

Fallback page if one page is risky in live demo:

- use KYC Dashboard and Approval Queue only

## Process 6: Account Opening Workflow

Business Goal:

- convert a compliant customer into an approved and visible account holder

Start Role:

- `OPERATIONS_OFFICER`

Start Screen:

- `Accounts -> Dashboard`

Step-by-step flow:

1. Show Account Dashboard
2. Open Account Types
3. Open Opening Requests
4. Open one request
5. Open Review page
6. Explain submit, verify, approve, reject and return
7. Open Account List and connect request to final account visibility

Next module handoff:

- `Account -> Transactions`

What status or result should appear:

- approved request becomes visible in account-side operations

What to show in demo:

- dashboard
- opening requests list
- review page
- final account list

What evidence to save:

- request list
- request review screen
- account list

Fallback page if one page is risky in live demo:

- use Account Dashboard plus Account List

## Process 7: Core Transactions

Business Goal:

- perform daily deposit, withdrawal, transfer and traceable journal-backed banking actions

Start Role:

- `TELLER`

Start Screen:

- `Transactions -> Dashboard`

Step-by-step flow:

1. Show Transaction Dashboard
2. Open Deposit
3. Open Withdraw
4. Open Transfer
5. Open Cheque Clearing
6. Open Standing Instructions
7. Open Transaction List
8. Open one transaction and show journal or voucher actions

Next module handoff:

- `Transactions -> Statements`

What status or result should appear:

- transaction list, journal and voucher surfaces are visible

What to show in demo:

- deposit page
- withdraw page
- transfer page
- transaction list

What evidence to save:

- transaction dashboard
- one transaction detail
- voucher preview/download action

Fallback page if one page is risky in live demo:

- use Transaction Dashboard and Transaction List only

## Process 8: Deposit Scheme Process

Business Goal:

- manage structured deposit products, enrollments, schedules and profit views

Start Role:

- `OPERATIONS_OFFICER`

Start Screen:

- `Deposit Schemes -> Dashboard`

Step-by-step flow:

1. Show Scheme Dashboard
2. Open Scheme List
3. Open Enrollments
4. Open one enrollment
5. Show schedule and profit distribution view

Next module handoff:

- `Deposit Schemes -> Profit Management`

What status or result should appear:

- scheme and enrollment lifecycle are visible

What to show in demo:

- dashboard
- scheme list
- enrollment details

What evidence to save:

- scheme list
- enrollment schedule or profit view

Fallback page if one page is risky in live demo:

- use Scheme Dashboard and Enrollment List

## Process 9: Profit Management

Business Goal:

- maintain profit ratios, schedules and run depositor-side posting logic

Start Role:

- `OPERATIONS_OFFICER`

Start Screen:

- `Profit -> Dashboard`

Step-by-step flow:

1. Show Profit Dashboard
2. Open Ratios
3. Open Schedules
4. Open Postings
5. Open Run Posting
6. Open one posting and show advice preview/download

Next module handoff:

- `Profit -> Cards / Statements / Reports`

What status or result should appear:

- profit ratio, schedule and posting layers are visible

What to show in demo:

- dashboard
- posting list
- run posting page

What evidence to save:

- profit dashboard
- posting advice

Fallback page if one page is risky in live demo:

- use Profit Dashboard and Posting List only

## Process 10: Cards And ATM/CDM

Business Goal:

- manage customer card lifecycle and ATM operational control

Start Role:

- `OPERATIONS_OFFICER`

Start Screen:

- `Cards -> Dashboard`

Step-by-step flow:

1. Show Cards Dashboard
2. Open Card List
3. Open one card and explain activate, block, unblock, renew or replace
4. Show PIN events if available
5. Open ATM Dashboard
6. Open Terminals
7. Open Replenishments
8. Open Reconciliations

Next module handoff:

- `Cards / ATM -> Statements or Security`

What status or result should appear:

- card and ATM operational layers are visible

What to show in demo:

- card dashboard
- card list
- ATM dashboard
- replenishment or reconciliation list

What evidence to save:

- card detail
- ATM dashboard

Fallback page if one page is risky in live demo:

- use Cards Dashboard and ATM Dashboard only

## Process 11: Statement Generation

Business Goal:

- serve customer and branch statement requests with preview, download and export

Start Role:

- `OPERATIONS_OFFICER`

Start Screen:

- `Statement -> Dashboard`

Step-by-step flow:

1. Show Statement Dashboard
2. Open Customer Request
3. Open Customer Statement List
4. Open Branch Request
5. Open Branch Statement List
6. Open Export Center

Next module handoff:

- `Statements -> Reporting`

What status or result should appear:

- customer and branch requests are visible with preview/download/export options

What to show in demo:

- dashboard
- customer request flow
- branch request flow
- export center

What evidence to save:

- one customer statement preview/download
- one branch statement preview/download

Fallback page if one page is risky in live demo:

- use Statement Dashboard and Customer Statement List

## Process 12: Financing, Contract And Shariah

Business Goal:

- manage financing from intake through review, disbursement, contract generation and shariah governance

Start Role:

- `INVESTMENT_OFFICER`

Start Screen:

- `Financing -> Dashboard`

Step-by-step flow:

1. Show Financing Dashboard
2. Open Products
3. Open Applications
4. Open one application
5. Show review page
6. Show disbursement page
7. Show schedule page
8. Show repayment collection page
9. Open Contracts -> Generate
10. Explain contract customer sign and shariah sign flow
11. Switch to `SHARIAH_BOARD_MEMBER`
12. Open Shariah Dashboard
13. Open case list and case review
14. Show correction queue and fatwa certificate list

Next module handoff:

- `Financing -> Contract -> Shariah -> Reporting`

What status or result should appear:

- financing lifecycle, contract flow and shariah approval chain are visible

What to show in demo:

- financing dashboard
- application review/disburse/schedule
- contract generate
- shariah case review

What evidence to save:

- financing review page
- sanction letter preview if available
- contract preview/sign context
- shariah review screen

Fallback page if one page is risky in live demo:

- use Financing Dashboard and Shariah Dashboard only

## Process 13: Zakat, Notification, Integration And Verification

Business Goal:

- demonstrate support services that complete the Islamic banking and platform ecosystem

Start Role:

- `SHARIAH_BOARD_MEMBER`

Start Screen:

- `Zakat -> Dashboard`

Step-by-step flow:

1. Show Zakat Dashboard
2. Open Profiles
3. Open Run Calculation
4. Open Beneficiaries
5. Open Payouts
6. Switch to `SYSTEM_ADMIN`
7. Show Notifications Dashboard, Templates, Event Rules and Logs
8. Show Integrations Dashboard, Providers, Logs and Provider Test
9. Show Verification Dashboard, OTP Verify, Reset Password and Provider Test

Next module handoff:

- `Zakat / Notification / Integration / Verification -> Workflow and Security`

What status or result should appear:

- support and control services are visible and connected to business operations

What to show in demo:

- zakat dashboard
- notification dashboard
- integration dashboard
- verification dashboard

What evidence to save:

- payout receipt preview or profile sheet preview
- one notification log
- one integration log or provider test
- one verification log

Fallback page if one page is risky in live demo:

- use dashboards only for these four modules

## Process 14: Workflow, Security, Reporting And Accounting

Business Goal:

- prove governance, traceability, reporting control and accounting visibility

Start Role:

- `SYSTEM_ADMIN`

Start Screen:

- `Workflow -> Dashboard`

Step-by-step flow:

1. Show Workflow Dashboard
2. Open Pending and My Submissions
3. Show Security Dashboard
4. Open Events, Audit Logs and Investigation Cases
5. Open Report Dashboard
6. Open one operational report
7. Open Management P and L
8. Open Expense Register
9. Open Trial Balance
10. Use View Journals
11. Open Ledger Profit and Loss
12. Show reconciliation view
13. Show branch ledger snapshot
14. Show income heads, expense heads and journal drill-down
15. Show export history

Next module handoff:

- final executive close, no next business module

What status or result should appear:

- workflow visibility, audit control, reporting outputs and ledger-backed accounting proof are all visible

What to show in demo:

- workflow dashboard
- security dashboard
- report dashboard
- management P and L
- expense register
- trial balance
- ledger profit and loss

What evidence to save:

- workflow queue
- audit log or investigation case
- report preview/export history
- management P and L result
- trial balance with journals
- ledger profit and loss with journals

Fallback page if one page is risky in live demo:

- if time is short, use only:
  - Report Dashboard
  - Management P and L
  - Trial Balance
  - Ledger Profit and Loss

## Management Reporting Layer

Current management reporting surfaces include:

- Report Dashboard
- Operational Report
- Management P and L
- Management Expense Register
- Export History

Use this layer to explain:

- operational summaries
- proxy profitability
- manual operating expense capture
- preview, print, download and export

## Accounting Control Layer

Current accounting control surfaces include:

- Trial Balance
- Ledger Profit and Loss
- journal drill-down
- export history

Use this layer to explain:

- control totals
- debit and credit proof
- branch-wise profitability
- journal-backed internal reporting

## Executive Close-Out Sequence

Always end the final live demo in this order:

1. `Management P and L`
2. `Expense Register`
3. `Trial Balance`
4. `Ledger Profit and Loss`

Explain these points:

- Management P and L is the operational and proxy reporting layer
- Expense Register captures manual operating expenses for management view
- Trial Balance proves debit and credit control
- Ledger Profit and Loss is the final internal accounting result
- branch-level ledger snapshot and journal drill-down prove explainability

## Module-Wise Appendix

This appendix is reference-only and should not replace the process narrative above.

| Module | Main Pages | Actor Role | Primary Demo Purpose |
| --- | --- | --- | --- |
| Auth | Login, OTP, reset password | All users | entry and verification |
| Admin | Roles, users | SYSTEM_ADMIN | access control |
| Lookups | Types, values | SYSTEM_ADMIN | configurable master data |
| Branch | Dashboard, list, assignments, vault | SYSTEM_ADMIN, BRANCH_MANAGER | branch control |
| ATM | Dashboard, terminals, replenishment, reconciliation | OPERATIONS_OFFICER | ATM operations |
| Customer | Dashboard, list, detail | OPERATIONS_OFFICER | onboarding base |
| KYC | Dashboard, queue, review | OPERATIONS_OFFICER | compliance workflow |
| Accounts | Dashboard, opening requests, list | OPERATIONS_OFFICER | account creation |
| Transactions | Deposit, withdraw, transfer, list | TELLER | core banking actions |
| Deposit Schemes | Dashboard, list, enrollments | OPERATIONS_OFFICER | product enrollment |
| Profit | Dashboard, schedules, postings | OPERATIONS_OFFICER | profit posting |
| Cards | Dashboard, list, activation | OPERATIONS_OFFICER | card lifecycle |
| Statements | Dashboard, requests, export center | OPERATIONS_OFFICER | statement servicing |
| Financing | Dashboard, applications, review | INVESTMENT_OFFICER | financing lifecycle |
| Contracts | Templates, generate, sign | INVESTMENT_OFFICER | contract flow |
| Shariah | Dashboard, cases, review | SHARIAH_BOARD_MEMBER | Islamic governance |
| Zakat | Dashboard, profiles, payouts | SHARIAH_BOARD_MEMBER | zakat and charity |
| Notifications | Dashboard, templates, logs | SYSTEM_ADMIN | alert automation |
| Integrations | Dashboard, providers, logs | SYSTEM_ADMIN | provider connectivity |
| Verification | Dashboard, OTP, logs | SYSTEM_ADMIN | OTP and contact verification |
| Workflow | Dashboard, pending, my submissions | OPERATIONS_OFFICER | approval visibility |
| Security | Dashboard, audit logs, cases | SYSTEM_ADMIN | auditability and control |
| Reports | Dashboard, reports, export history | SYSTEM_ADMIN | reporting layer |
| Accounting | Trial Balance, Ledger Profit and Loss | SYSTEM_ADMIN | final control proof |

## Cross-Cutting Checks

For every important module, confirm:

1. route guard and RBAC visibility
2. dashboard loads
3. list loads
4. one detail view loads
5. one workflow action path exists if applicable
6. preview, print, download or export exists where expected
7. actor trail or history can be shown

## Final Rehearsal Checklist

Before tomorrow:

1. open each role login once
2. open each major dashboard once
3. test Management P and L
4. test Expense Register
5. test Trial Balance
6. test Ledger Profit and Loss
7. test journal drill-down
8. test export history
9. test one statement preview
10. test one financing review path

## Final Note

This v2 playbook is meant to present SBMS as a connected operational platform:

- onboarding
- compliance
- account servicing
- transactions
- financing
- Islamic governance
- support services
- reporting
- accounting control

The strongest final impression should always be:

`Business flow -> Control trail -> Executive report -> Ledger proof`
