# SBMS System Verification Playbook

Date: 16 May 2026  
Workspace: `I:\SBMS Copy`

## Purpose

This playbook is the practical verification manual for the current SBMS system. It is written so that you can:

- understand what is already usable in the system
- verify end-to-end business processes role by role
- test module by module without missing key workflow steps
- confirm RBAC, document generation, export, mail, OTP, workflow, and audit behavior
- record pass/fail results in a structured way

## Scope Summary

Current verification scope covers:

- 22 major application modules
- 4 cross-cutting support layers
- 8 main operational roles
- 20 major business process families
- generated document, preview, print, download, export, and dashboard flows

## Practical Process Count

For verification purposes, treat the current system as **20 major process families**:

1. Login, OTP, password reset
2. Role and permission administration
3. User administration
4. Lookup and configuration management
5. Branch and branch assignment operations
6. ATM/CDM operational control
7. Customer onboarding and profile management
8. KYC verification and approval
9. Account opening and account lifecycle
10. Teller transactions and reversal
11. Profit ratio, schedule and posting
12. Card lifecycle and PIN/event operations
13. Statement request, preview and export
14. Deposit scheme enrollment and profit distribution
15. Financing tracking, review, disbursement and recovery
16. Contract generation and signing
17. Shariah review workflow
18. Zakat and charity operations
19. Reporting, monthly closing and exports
20. Security, workflow, notification, integration and audit control

## Main Roles To Verify

Use these roles as the primary UAT actors:

1. `SYSTEM_ADMIN`
2. `BRANCH_MANAGER`
3. `BRANCH_STAFF`
4. `TELLER`
5. `OPERATIONS_OFFICER`
6. `INVESTMENT_OFFICER`
7. `SHARIAH_BOARD_MEMBER`
8. `CUSTOMER`

Also verify support roles where available:

- `COMPLIANCE_OFFICER`
- `MIS_OFFICER`
- `INTERNAL_AUDITOR`
- `RECOVERY_OFFICER`
- `TREASURY_FINANCE_OFFICER`

## What Each Role Should Do

### System Admin

- create and manage users, roles, permissions, lookups, providers and system settings
- monitor audit, security, workflow, notification and integration surfaces
- should not perform normal business approval unless explicitly configured

### Branch Staff

- create customer and account-opening intake records
- collect documents
- support enrollment, branch operations and service requests

### Teller

- post deposit, withdrawal, transfer intake and cheque clearing
- view transaction journal
- use only teller-level operational actions

### Operations Officer

- verify KYC
- review account-opening requests
- perform account/customer status actions
- manage contract generation and operational review queues

### Investment Officer

- create financing products and applications
- verify and review financing files
- disburse and collect repayment
- use recovery queue and related reports

### Shariah Board Member

- review submitted Shariah cases
- approve, reject or return
- sign contracts after customer signature where applicable

### Branch Manager

- approve branch-level operations
- manage vault closing and branch oversight
- sign off branch operational flows and monthly closing submission

### Customer

- view own profile, accounts, financing, schemes, contracts and statements
- receive OTP and automated mails
- use only customer-facing read/request surfaces

## Global Verification Rules

For every module, verify these seven items:

1. `Dashboard`: cards, recent activity, queue blocks, route guard
2. `Create/Edit`: form validation, save flow, success/error handling
3. `List`: search, filter, pagination, action column, export if applicable
4. `View`: detail surface, images/documents, related navigation
5. `Workflow`: verify, approve, reject, return, close, sign or disburse if applicable
6. `RBAC`: correct role sees correct route, button and backend result
7. `Audit/Document`: actor trail, preview/print/download/export, mail/notification side effects if expected

## Test Environment Preparation

Before full verification, confirm:

- backend starts clean
- frontend builds and loads clean
- MySQL schema is updated for latest columns
- sample users exist for each role
- branch, customer, account, financing and lookup seed data exist
- mail configuration is set if email verification is being tested
- uploaded file storage path is accessible

## Suggested Verification Sequence

Run verification in this order:

1. Auth and OTP
2. Admin RBAC foundation
3. Branch and ATM operations
4. Customer and KYC onboarding
5. Account opening and lifecycle
6. Transactions
7. Profit and statements
8. Deposit schemes
9. Financing, contracts and Shariah
10. Zakat and charity
11. Reports and monthly closing
12. Security, workflow, notifications and integrations
13. Customer-facing read-only flows

## Complete Process Flow Blueprints

This section explains how each major process should complete in real operation.

For every process below, verify three things:

1. `Flow ownership`: correct role performs the correct step
2. `System behavior`: status, document, mail, audit and queue move correctly
3. `Completion state`: process reaches the expected final business result

Use this reading pattern for every process:

- `Trigger`: what starts the process
- `Primary roles`: who performs the steps
- `Flow`: exact operational sequence
- `Status path`: how records should move
- `How to verify`: what the tester should do on screen
- `Evidence`: what to save as proof

## Process 1: Login, OTP And Password Recovery

Trigger:

- user wants to enter the system
- user forgot password or wants to reset credentials

Flow:

1. User enters username and password
2. System validates credentials
3. If OTP is required, system sends OTP to configured email channel
4. User enters OTP
5. System verifies OTP and opens authorized dashboard
6. If user forgot password, user requests reset
7. System sends password reset OTP
8. User verifies OTP and sets new password

Role ownership:

- `All users`: login and OTP input
- `System Admin`: provider setup and monitoring

Status path:

- `LOGIN_SCREEN -> OTP_PENDING -> AUTHENTICATED`
- `FORGOT_PASSWORD -> OTP_SENT -> OTP_VERIFIED -> PASSWORD_RESET`

How to verify:

- login with valid user and confirm role-based dashboard opens
- login with wrong password and confirm rejection
- request forgot password and confirm OTP mail arrives
- verify expired OTP and reused OTP are rejected

Evidence:

- login screen
- OTP mail
- successful dashboard
- reset success message

Completion state:

- user reaches dashboard with correct role-based menus
- reset password updates stored credentials successfully

## Process 2: Role And Permission Setup

Trigger:

- admin needs a new role or a user needs access correction

Flow:

1. System admin creates role
2. System admin maps permission bundle
3. System admin assigns role to user
4. User logs in again
5. Frontend menu, route and action button visibility change
6. Backend API access aligns with same permission set

Role ownership:

- `System Admin`: all actions

Status path:

- `ROLE_CREATED -> PERMISSIONS_MAPPED -> USER_ASSIGNED -> ACCESS_EFFECTIVE`

How to verify:

- create test role with limited permissions
- assign role to a test user
- verify menu visibility, route access, row action visibility and backend denial/allow behavior

Evidence:

- role setup screen
- role-permission map
- assigned user profile
- allowed and denied screen/API examples

Completion state:

- role works consistently in menu, page access, button access and API access

## Process 3: User Provisioning

Trigger:

- admin needs to onboard internal staff or support user

Flow:

1. System admin creates user profile
2. Branch-bound role is assigned where needed
3. Welcome/account-ready mail is sent
4. User logs in with credentials
5. User changes password if policy requires
6. Admin may later lock/unlock or reset password

Role ownership:

- `System Admin`: create, role assign, lock/unlock, password reset
- `User`: login, password change

Status path:

- `USER_CREATED -> ROLE_ASSIGNED -> MAIL_SENT -> FIRST_LOGIN -> ACTIVE`
- optional `ACTIVE -> LOCKED -> UNLOCKED`

How to verify:

- create a new branch user
- confirm welcome mail
- confirm first login works
- lock and unlock the user and verify access changes immediately after relogin

Evidence:

- created user record
- welcome mail
- successful login
- lock/unlock result

Completion state:

- user can access exactly the assigned branch/module permissions

## Process 4: Lookup And Configuration Maintenance

Trigger:

- admin needs to add or change shared master data

Flow:

1. System admin creates or updates master config
2. Dependent forms load new values
3. Archived values stop appearing in active form options

Role ownership:

- `System Admin`

Status path:

- `DRAFT_VALUE -> ACTIVE_VALUE -> ARCHIVED_VALUE`

How to verify:

- create a lookup item
- open two dependent forms and confirm the option appears
- archive the option and confirm it no longer appears for new entry

Evidence:

- lookup save screen
- dependent form before and after archive

Completion state:

- config is reusable across dependent modules without breaking form submission

## Process 5: Branch Setup And Assignment

Trigger:

- new branch is being opened or branch staffing needs to be aligned

Flow:

1. System admin creates branch
2. Branch manager or admin assigns users to branch
3. Teller limits and branch operational settings are configured
4. Branch dashboard reflects branch activity

Role ownership:

- `System Admin`: branch master setup
- `Branch Manager`: branch operations and assignments if permitted

Status path:

- `BRANCH_CREATED -> STAFF_ASSIGNED -> LIMITS_SET -> OPERATIONAL`

How to verify:

- create a branch
- assign at least one manager, one teller and one staff user
- verify branch-bound users see their own branch by default

Evidence:

- branch master view
- assignment record
- branch-scoped list screen

Completion state:

- branch data, staff ownership and limits are ready for operational use

## Process 6: Vault And Cash Control

Trigger:

- branch starts or closes cash operation for a business day

Flow:

1. Authorized user opens vault for branch/date
2. System stores opening balance
3. Cash movement summary accumulates during operations
4. Authorized user closes vault with cash in/out
5. Closing balance is computed
6. Ledger entry is generated
7. Vault report and daily cash report become available

Role ownership:

- `Branch Manager`
- `Operations Officer`
- `Treasury/Finance Officer` for review

Status path:

- `NOT_OPENED -> OPENED -> ACTIVE_MOVEMENT -> CLOSED`

How to verify:

- open vault with opening balance
- post or simulate branch cash movement
- close vault and verify closing math
- generate daily cash and vault reports

Evidence:

- vault open screen
- vault close screen
- cash ledger list
- generated PDFs

Completion state:

- vault shows correct closed/open state
- branch cash ledger reflects opening and closing movements

## Process 7: ATM/CDM Operational Cycle

Trigger:

- ATM terminal is configured, loaded, or reconciled

Flow:

1. Admin/operations creates terminal and cash bin setup
2. Replenishment is recorded when cash is loaded
3. Reconciliation is recorded against machine totals
4. Profile/report PDFs are generated for control evidence

Role ownership:

- `Operations Officer`
- `Branch Manager`

Status path:

- `TERMINAL_CREATED -> BIN_READY -> REPLENISHED -> RECONCILED`

How to verify:

- create terminal and cash bins
- record replenishment
- record reconciliation
- preview profile/report PDFs

Evidence:

- terminal profile
- bin profile
- replenishment report
- reconciliation report

Completion state:

- terminal, bin, replenishment and reconciliation surfaces all hold audit-ready records

## Process 8: Customer Onboarding

Trigger:

- branch begins customer intake for a new or updated client

Flow:

1. Branch staff creates customer profile
2. Address and identity data are added
3. Customer image and documents are uploaded
4. Customer profile enters review-ready state

Role ownership:

- `Branch Staff`
- `Operations Officer` for review support

Status path:

- `CUSTOMER_CREATED -> IDENTITY_CAPTURED -> DOCUMENT_UPLOADED -> REVIEW_READY`

How to verify:

- create customer with photo, address and identity data
- confirm list/view/dashboard reflect the customer
- confirm branch-bound users only see their branch records where expected

Evidence:

- customer create screen
- customer view
- uploaded image/document preview

Completion state:

- customer profile is complete enough to move into KYC and account opening

## Process 9: KYC Verification

Trigger:

- customer profile is ready for compliance verification

Flow:

1. KYC record is created for customer
2. KYC documents are uploaded
3. Operations officer reviews
4. KYC is verified/approved or returned/rejected
5. Decision mail is sent
6. Workflow history reflects action

Role ownership:

- `Branch Staff`: initial data/document collection
- `Operations Officer`: verify/approve/reject/return
- `Compliance Officer`: optional oversight

Status path:

- `DRAFT -> SUBMITTED -> VERIFIED/APPROVED`
- optional `DRAFT/SUBMITTED -> RETURNED` or `REJECTED`

How to verify:

- create KYC record and upload proof
- submit from intake role
- approve from operations role
- repeat with reject and return scenarios

Evidence:

- KYC list status changes
- decision mail
- workflow history row

Completion state:

- customer becomes KYC-approved or receives correction/rejection outcome

## Process 10: Account Opening

Trigger:

- KYC-cleared customer requests a bank account

Flow:

1. Branch staff creates account opening request
2. Supporting data and applicant file are attached
3. Operations officer verifies request
4. Branch manager or authorized reviewer approves, rejects or returns
5. Account opening form PDF can be generated
6. Customer receives decision mail
7. Approved request leads to active account creation

Role ownership:

- `Branch Staff`: create/open intake
- `Operations Officer`: verify/review
- `Branch Manager`: final approval where configured

Status path:

- `DRAFT -> SUBMITTED -> VERIFIED -> APPROVED`
- optional `RETURNED` or `REJECTED`
- approved flow continues to `ACCOUNT_CREATED`

How to verify:

- create request as branch staff
- verify as operations officer
- approve as authorized reviewer
- confirm account record exists and account-opening PDF can be opened

Evidence:

- request list
- approval screen
- created account
- account-opening form PDF

Completion state:

- live account exists for approved request, or request is closed with rejection/return decision

## Process 11: Account Lifecycle Management

Trigger:

- active account needs service-state change

Flow:

1. Active account is created from approved onboarding
2. Authorized user may activate, block, freeze or close account
3. Customer gets status mail
4. Statements and transactions respect account state

Role ownership:

- `Operations Officer`
- `Branch Manager`

Status path:

- `PENDING/CREATED -> ACTIVE`
- optional `ACTIVE -> BLOCKED/FROZEN/CLOSED`

How to verify:

- activate an approved account
- block or freeze it
- attempt downstream use and confirm system respects state

Evidence:

- account status before/after
- customer decision mail
- denied downstream action if state disallows it

Completion state:

- account state matches business decision and downstream modules honor it

## Process 12: Teller Transaction Processing

Trigger:

- customer requests cash or account transaction service

Flow:

1. Teller selects transaction type
2. Teller posts deposit/withdrawal/transfer/cheque clearing
3. Balance is updated
4. Journal entry is created
5. Customer confirmation mail is sent where applicable
6. Voucher PDF becomes available
7. If mistake occurs, authorized reversal is posted

Role ownership:

- `Teller`: post transaction
- `Operations Officer`: reversal
- `Branch Manager`: high-value oversight if configured

Status path:

- `INITIATED -> POSTED`
- optional `POSTED -> REVERSED`

How to verify:

- run deposit, withdrawal, transfer and cheque clearing
- confirm balance and journal updates
- open voucher PDF
- reverse one transaction with authorized role

Evidence:

- transaction list
- transaction detail
- voucher PDF
- reversal history

Completion state:

- transaction and journal are posted, reversible only through authorized workflow

## Process 13: Profit Management

Trigger:

- finance team prepares profit ratio, schedule or posting run

Flow:

1. Finance officer defines profit ratio
2. Profit schedule is created against eligible account
3. Profit posting run is executed
4. System calculates amount and updates account
5. Posting advice PDF is generated
6. Failed posting remains traceable with reason

Role ownership:

- `Treasury/Finance Officer`
- `MIS Officer` for reporting review

Status path:

- `RATIO_DEFINED -> SCHEDULE_CREATED -> POSTING_RUN -> POSTED/FAILED`

How to verify:

- create ratio and schedule
- execute posting
- confirm advice PDF and status result
- validate failed case remains traceable

Evidence:

- ratio list
- schedule list
- posting result
- posting advice PDF

Completion state:

- posting ends in `POSTED` or `FAILED` with explicit evidence

## Process 14: Card Lifecycle

Trigger:

- eligible account requires card issuance or card service action

Flow:

1. Card is issued for eligible account/customer
2. Card can be activated
3. PIN and event logs are tracked
4. Block/unblock/renew/replace actions are performed by authorized role
5. Customer receives outcome mail

Role ownership:

- `Operations Officer`
- `Branch Manager`

Status path:

- `ISSUED -> ACTIVE`
- optional `ACTIVE -> BLOCKED -> UNBLOCKED`
- optional `ACTIVE -> RENEWED/REPLACED`

How to verify:

- issue card
- activate card
- perform block/unblock or renew/replace
- verify mail and event log

Evidence:

- card list
- card view
- event/PIN log
- outcome mail

Completion state:

- card status and event trail match the latest business action

## Process 15: Statement Lifecycle

Trigger:

- customer, branch or operations user requests statement output

Flow:

1. User creates statement request
2. Backend generates branded statement document
3. Statement file is saved and logged
4. User previews, prints or downloads PDF
5. Register export can be produced for operational review

Role ownership:

- `Customer`: view own statements where allowed
- `Branch Staff`: request for customers
- `Operations Officer` / `MIS Officer`: branch-level and export-center use

Status path:

- `REQUEST_CREATED -> FILE_GENERATED -> PREVIEWED/DOWNLOADED`

How to verify:

- request customer and branch statements
- preview PDF
- print and download
- export statement register

Evidence:

- statement request record
- generated statement PDF
- export history/register output

Completion state:

- statement request ends with accessible branded document and export log

## Process 16: Deposit Scheme Lifecycle

Trigger:

- bank offers term investment or customer joins a scheme

Flow:

1. Deposit scheme is defined
2. Customer is enrolled
3. Installment schedule is auto-generated
4. Profit distribution rows are generated
5. Investment certificate PDF becomes available
6. Enrollment remains active until maturity or special status

Role ownership:

- `Branch Staff`
- `Operations Officer`

Status path:

- `SCHEME_CREATED -> ENROLLMENT_OPENED -> SCHEDULE_GENERATED -> DISTRIBUTION_TRACKED`

How to verify:

- create scheme
- enroll customer
- verify generated schedule
- open certificate PDF and distribution records

Evidence:

- scheme view
- enrollment view
- certificate PDF
- distribution rows

Completion state:

- scheme, enrollment, schedule, distribution and certificate are all connected

## Process 17: Financing Lifecycle

Trigger:

- customer requests Islamic financing and bank begins evaluation

Flow:

1. Financing product is configured
2. Investment officer creates application
3. Documents and supporting evidence are attached
4. Review/verification is performed
5. Sanction letter is generated after approval
6. Contract is generated
7. Shariah review happens if required
8. Financing is disbursed
9. Repayment collection begins
10. Overdue items move into recovery attention

Role ownership:

- `Investment Officer`: create, verify, review, disburse, collect
- `Branch Manager`: approval oversight where configured
- `Recovery Officer`: overdue follow-up

Status path:

- `APPLICATION_CREATED -> REVIEWED -> APPROVED`
- optional `RETURNED` or `REJECTED`
- approved flow continues to `SANCTIONED -> CONTRACTED -> DISBURSED -> REPAYING -> OVERDUE/RECOVERY`

How to verify:

- create application
- review and approve it
- generate sanction letter
- move into contract and Shariah chain
- disburse and collect repayment
- confirm overdue queue/report behavior

Evidence:

- application list status changes
- sanction letter PDF
- disbursement action
- repayment collection record
- recovery report

Completion state:

- financing reaches disbursed and collectible state or closes with rejection/return

## Process 18: Contract Lifecycle

Trigger:

- approved business arrangement needs legal/Islamic contract output

Flow:

1. Authorized user selects template and generates contract
2. Contract text and supporting file are stored
3. Customer signs
4. Shariah signer signs
5. Contract becomes locked/finalized
6. Print copy PDF is available

Role ownership:

- `Operations Officer` or `Investment Officer`: generate
- `Customer-facing authorized operator`: capture customer sign
- `Shariah Board Member`: Shariah sign

Status path:

- `GENERATED -> CUSTOMER_SIGNED -> SHARIAH_SIGNED -> FINALIZED`

How to verify:

- generate contract from reference record
- capture customer sign
- capture Shariah sign
- preview print copy PDF

Evidence:

- contract view
- signature trail
- final print copy PDF

Completion state:

- finalized contract has version history, signatures and print copy

## Process 19: Shariah Review Lifecycle

Trigger:

- financing or contract file requires Shariah decision

Flow:

1. Case is created from financing/contract reference
2. Reviewer inspects case and checklist
3. Decision is approve, reject or return
4. Workflow and mail update accordingly

Role ownership:

- `Shariah Board Member`
- `Operations Officer` for submission support

Status path:

- `SUBMITTED -> APPROVED`
- optional `SUBMITTED -> RETURNED/REJECTED`

How to verify:

- submit case
- approve one case
- reject or return another case
- verify decision mail and workflow history

Evidence:

- case list
- case detail
- decision output
- mail and workflow trail

Completion state:

- reference record receives a traceable Shariah outcome

## Process 20: Zakat And Charity Lifecycle

Trigger:

- annual zakat assessment or charity payout cycle begins

Flow:

1. Zakat profile is created for customer/year
2. Proof document is uploaded
3. Calculation is executed
4. If above nisab, zakat amount is determined and fund movement is recorded
5. Beneficiary is managed with proof
6. Charity payout is created
7. Payout receipt PDF is generated
8. Zakat profile sheet PDF is generated

Role ownership:

- `Operations Officer`
- `Charity Control / Management Reviewer`

Status path:

- `PROFILE_CREATED -> PROOF_ADDED -> CALCULATED`
- if payable: `CALCULATED -> DEDUCTED/PAYABLE -> PAYOUT_CREATED -> RECEIPTED`

How to verify:

- create zakat profile
- upload proof
- calculate amount
- create beneficiary and payout
- open profile sheet and payout receipt PDFs

Evidence:

- profile sheet
- payout receipt
- beneficiary record
- calculation result

Completion state:

- profile, fund, beneficiary and payout all connect with proper evidence and documents

## Process 21: Reporting And Monthly Closing

Trigger:

- management needs report output or branch needs month-end closure

Flow:

1. User opens report page with filters
2. Backend builds report dataset
3. Branded preview/PDF/Excel/CSV is generated
4. Export history is stored
5. For monthly closing, branch user creates or refreshes snapshot
6. Checklist is completed
7. Closing is submitted
8. Reviewer approves/rejects/reopens

Role ownership:

- `MIS Officer`: reports
- `Compliance Officer`: reporting review
- `Branch Manager`: monthly closing submit
- `Treasury/Finance Officer`: financial review

Status path:

- reporting: `FILTERED -> GENERATED -> EXPORTED/LOGGED`
- closing: `SNAPSHOT_CREATED -> CHECKLIST_READY -> SUBMITTED -> APPROVED/REJECTED/REOPENED`

How to verify:

- run KPI, growth, recovery and monthly closing reports
- export PDF, Excel and CSV
- create monthly closing snapshot
- submit after checklist readiness
- approve or reopen with another role

Evidence:

- report files
- export history
- monthly closing dashboard
- approval/reopen result

Completion state:

- report ends with generated file
- monthly closing ends in submitted/approved/rejected/reopened state

## Process 22: Security And Investigation

Trigger:

- suspicious event, security event or control breach is identified

Flow:

1. Security event or suspicious activity is logged
2. Investigation case is created
3. Evidence is uploaded
4. Reviewer assigns case
5. Investigator works the case
6. Case is closed with remarks
7. Assignment/closure mail is sent

Role ownership:

- `Compliance Officer`
- `Internal Auditor`
- `Security Reviewer`

Status path:

- `EVENT_LOGGED -> CASE_OPENED -> ASSIGNED -> IN_PROGRESS -> CLOSED`

How to verify:

- create event or suspicious activity
- open investigation case
- assign case
- close with remarks
- confirm assignment/closure mail

Evidence:

- event list
- investigation record
- closure note
- mail alert

Completion state:

- case has event context, evidence, assignee and closure trail

## Process 23: Workflow Support

Trigger:

- any source module changes to a review or approval state

Flow:

1. Source module changes status
2. Workflow history row is written
3. Pending queue surfaces actionable record
4. Approver opens source record
5. Action completes and returns to queue/history

Role ownership:

- any approver role depending on module

Status path:

- `SOURCE_SUBMITTED -> PENDING_QUEUE -> DECISIONED -> HISTORY_WRITTEN`

How to verify:

- submit a source record from KYC, account opening, financing or Shariah
- confirm pending queue visibility for approver
- complete action and confirm history updates

Evidence:

- pending queue row
- history row
- source record status

Completion state:

- workflow history matches source module decision chain

## Process 24: Notification And Integration Support

Trigger:

- a business event fires or admin tests provider/template/rule

Flow:

1. Template/rule/provider is configured
2. Business module triggers event
3. Notification dispatch or integration execution is attempted
4. Success/failure is logged
5. Retry queue can be used if needed
6. Live alert and automated support mail may be triggered

Role ownership:

- `System Admin`
- `Operations Support`

Status path:

- `EVENT_TRIGGERED -> DISPATCH_ATTEMPTED -> SENT/FAILED`
- optional `FAILED -> RETRY_QUEUED -> RETRIED`

How to verify:

- configure template, rule and provider
- trigger business mail or test provider
- review delivery log or integration log
- retry failed item where available

Evidence:

- delivery log
- integration log
- retry queue
- sample received mail

Completion state:

- business-triggered communication is traceable and retryable

## Module-Wise Playbook

## Module 1: Auth

Purpose:
- login, OTP, forgot password, reset password

Roles:
- all users

Verify:
- valid login works
- invalid password is rejected
- OTP screen appears where configured
- resend OTP works
- expired OTP is rejected
- reset password updates credentials

Pass criteria:
- only login and password recovery require OTP
- business action pages do not ask OTP

## Module 2: Role & Permission Management

Purpose:
- control access model

Roles:
- system admin

Verify:
- create role
- map permission bundle
- view role detail
- archive/restore if allowed
- verify menu/button visibility changes for assigned users

## Module 3: User Management

Purpose:
- user create, branch mapping, role assignment, lock/unlock, reset password

Roles:
- system admin

Verify:
- create user
- assign branch-bound role
- assign/unassign roles
- lock/unlock user
- admin reset password mail
- welcome mail for new user

## Module 4: Lookup / Config

Purpose:
- maintain master data

Roles:
- system admin

Verify:
- create and update lookup values
- archive/restore if supported
- verify dropdown usage in dependent modules

## Module 5: Branch Management

Purpose:
- branch master, assignment, teller limit, vault, cash ledger

Roles:
- branch manager, operations officer, system admin

Verify:
- branch create/edit/view/list
- branch assignment to users
- teller limit create/update
- vault open and close
- cash ledger list and export
- branch daily cash report PDF
- vault balance report PDF

Documents:
- daily cash report
- vault balance report

## Module 6: ATM / CDM

Purpose:
- terminal, cash bin, replenishment, reconciliation

Roles:
- branch manager, operations officer, branch staff

Verify:
- terminal create/list/view
- cash bin create/list/view
- replenishment create/list/view
- reconciliation create/list/view
- preview/download/print for terminal profile, cash bin profile, replenishment report, reconciliation report

## Module 7: Customer Management

Purpose:
- customer profile, identity and address management

Roles:
- branch staff, operations officer

Verify:
- customer create/edit/view/list
- image upload and preview
- identity document upload and preview
- status action according to RBAC
- linked navigation to KYC and account opening

## Module 8: KYC

Purpose:
- KYC document review and approval flow

Roles:
- operations officer, branch manager, compliance reviewer

Verify:
- KYC profile create/list/view/history
- document upload and preview
- verify, approve, reject, return
- decision mail
- queue and dashboard counts

## Module 9: Account Types, Account Opening Requests, Accounts

Purpose:
- account product config, onboarding workflow, live account lifecycle

Roles:
- branch staff, operations officer, branch manager

Verify:
- account type create/edit/archive
- account opening request create/list/view
- account opening form PDF preview/download/print
- verify, approve, reject, return
- account create/activate/block/freeze/close
- customer mail on approval/status decision

## Module 10: Transactions

Purpose:
- deposit, withdrawal, transfer, cheque clearing, reversal, journal

Roles:
- teller, operations officer, branch manager

Verify:
- deposit
- withdrawal
- transfer
- cheque clearing
- reversal
- journal view
- transaction confirmation mail
- transaction voucher PDF preview/download/print

## Module 11: Profit

Purpose:
- ratio, schedule, posting, posting history

Roles:
- treasury/finance officer, operations officer, MIS officer

Verify:
- ratio create/list/view
- schedule create/list/view
- posting run
- posting list/view
- failed posting behavior
- profit posting advice PDF preview/download/print

## Module 12: Cards

Purpose:
- issue, activate, block, unblock, renew, replace, PIN/event monitoring

Roles:
- operations officer, branch manager

Verify:
- card issue
- activate/block/unblock
- replace/renew
- event log and card dashboard
- customer mail on card outcome

## Module 13: Statements

Purpose:
- customer statement, branch statement, statement registers

Roles:
- branch staff, operations officer, customer, MIS officer

Verify:
- customer statement request
- branch statement request
- preview, print, download PDF
- statement register export CSV/Excel/PDF
- requester actor capture

Documents:
- customer statement
- branch statement
- statement register reports

## Module 14: Deposit Schemes

Purpose:
- scheme setup, enrollment, schedule, profit distribution

Roles:
- branch staff, operations officer, customer service

Verify:
- scheme create/list/view
- enrollment create/list/view
- schedule view
- profit distribution view
- investment certificate PDF preview/download/print

Documents:
- Islamic investment certificate

## Module 15: Financing

Purpose:
- product setup, application, review, disbursement, repayment, overdue handling

Roles:
- investment officer, operations officer, recovery officer, branch manager

Verify:
- financing product create/list/view
- application create/list/view
- review queue and decision actions
- disbursement
- repayment collection
- overdue-only queue
- loan recovery report
- financing sanction letter PDF
- transaction and approval mails

## Module 16: Contracts

Purpose:
- template, contract generation, versioning, customer sign, Shariah sign

Roles:
- operations officer, investment officer, Shariah board member

Verify:
- template create/list/view
- contract generate/list/view
- version history
- customer sign
- Shariah sign
- contract print copy PDF preview/download/print

## Module 17: Shariah Review

Purpose:
- case creation, review, correction queue, final decision

Roles:
- Shariah board member, operations officer

Verify:
- create or route reference case
- list/view/review
- approve, reject, return
- correction queue visibility
- decision mail

## Module 18: Zakat & Charity

Purpose:
- zakat profile, calculation, charity fund, beneficiary, payout

Roles:
- operations officer, charity control, management reviewer

Verify:
- zakat profile create/list/view
- profile sheet PDF preview/download/print
- zakat calculation
- charity fund list
- beneficiary create/list/view with proof preview
- payout create/list
- charity payout receipt preview/download/print

## Module 19: Notifications

Purpose:
- templates, event rules, retry queue, delivery logs

Roles:
- system admin, operations support

Verify:
- template create/list/view
- event rules list
- retry queue
- delivery log list/view
- fake print removed from config-only pages
- only operational export remains where meaningful

## Module 20: Integrations

Purpose:
- provider config, test, retry, execution log

Roles:
- system admin, integration operator

Verify:
- provider create/list/view
- provider test success/failure
- retry queue and log view
- automated support mails on provider test/retry

## Module 21: Reports & Monthly Closing

Purpose:
- branded report engine, KPI, growth, recovery, monthly closing ops

Roles:
- MIS officer, compliance officer, branch manager, treasury/finance officer

Verify:
- report dashboard
- KPI report
- growth report
- financing recovery report
- monthly closing report
- monthly closing ops screen
- create/refresh/submit/approve/reject/reopen
- report preview/print/download
- PDF/Excel/CSV export

## Module 22: Security / Audit / Investigation

Purpose:
- event log, suspicious activity, audit log, investigation lifecycle

Roles:
- compliance officer, internal auditor, security reviewer

Verify:
- security dashboard
- event list/view
- suspicious activity list/view
- audit log list/view
- investigation case list/view/action
- evidence upload and preview
- assign and close mail

## Module 23: Workflow

Purpose:
- pending approvals, my submissions, workflow history

Roles:
- all operational approvers

Verify:
- pending queue
- history list
- my submissions
- source record navigation
- actor and status timeline consistency

## Module 24: Verification

Purpose:
- OTP, provider test, delivery/retry, verification logs

Roles:
- system admin, support, end users

Verify:
- login OTP send/verify
- forgot password send/verify/reset
- verification dashboard and logs
- provider test console
- retry and delivery visibility

## Cross-Cutting Checks

For every module, confirm these cross-cutting behaviors:

### RBAC

- unauthorized route does not open
- unauthorized button is hidden
- direct API hit is still blocked

### Actor Trail

- create/review/approve/disburse/request actions save real logged-in username where implemented
- no fake `SYSTEM_*` actor should appear on user-facing current workflow unless intentionally system-generated

### Documents

- preview opens correct document
- print uses generated document, not raw page print
- download gives correct file type
- export only exists where meaningful

### Mail

- OTP mails
- welcome/reset mails
- transaction confirmations
- approval decision mails
- report ready mails
- contract/card/zakat/security outcome mails

### Images

- profile/document image preview works
- avatar sizing is consistent

## End-to-End Test Packs

Run these packs after module tests:

### Pack A: Customer Onboarding

1. Create customer
2. Upload identity
3. Create KYC
4. Verify/approve KYC
5. Create account-opening request
6. Approve request
7. Activate account
8. Generate account opening form PDF

### Pack B: Teller Operations

1. Deposit
2. Withdrawal
3. Transfer
4. Cheque clearing
5. Transaction reversal by authorized role
6. Verify voucher PDFs and mail

### Pack C: Investment Lifecycle

1. Create financing application
2. Review and approve
3. Generate sanction letter
4. Generate contract
5. Customer sign
6. Shariah sign
7. Disburse
8. Collect repayment
9. Verify recovery report

### Pack D: Deposit Investment Lifecycle

1. Create scheme
2. Enroll customer
3. Open schedule
4. Open profit distribution
5. Generate investment certificate

### Pack E: Branch Operations

1. Open vault
2. Close vault
3. Open cash ledger
4. Generate daily cash report
5. Generate vault report
6. Submit monthly closing

### Pack F: Zakat & Charity

1. Create zakat profile
2. Run calculation
3. Verify profile sheet
4. Create beneficiary
5. Create payout
6. Verify payout receipt

## Pass / Fail Template

For each module record:

- `Role`
- `Scenario`
- `Input`
- `Expected Result`
- `Actual Result`
- `Pass/Fail`
- `Evidence Path`
- `Remarks`

## Evidence To Save

Save these as verification evidence:

- screenshot of dashboard
- screenshot of create success
- screenshot of list and action column
- screenshot of detail view
- generated PDF sample
- exported Excel/CSV sample
- mail receipt screenshot if applicable
- API response sample for negative test

## Final Verification Outcome Levels

Use this final grading:

- `Green`: module flow complete, RBAC okay, document/export okay
- `Amber`: core flow works but one or two support items still incomplete
- `Red`: business flow blocked or role/process mismatch exists

## Recommended Final Sign-Off Order

1. System admin sign-off on RBAC and setup
2. Branch operations sign-off on onboarding, transaction, vault, statement
3. Investment sign-off on financing, contract, Shariah
4. Finance/MIS sign-off on profit, reports, monthly closing
5. Compliance sign-off on security, audit, workflow and notification trail

## Final Note

The current system is now strong enough to be verified as a connected platform, not just isolated CRUD modules.  
Use this playbook as the main UAT guide and mark each module and process family one by one.
