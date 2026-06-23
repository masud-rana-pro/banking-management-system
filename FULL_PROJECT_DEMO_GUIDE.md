# SBMS Full Project Demo Guide

Prepared for: full-system live demonstration  
Project workspace: `I:\SBMS Copy`  
Basis: actual frontend route files and backend controllers, not menu-only guesswork

## 1. What This Guide Covers

This guide tells you:

1. How many major business processes the system currently supports
2. Which role should perform which task
3. Which page opens first and which page comes next in a real flow
4. How to present the project tomorrow in a clean, confident order
5. Which logins you should keep ready for the demo

## 2. Codebase Evidence Used

The process map below was cross-checked from real route/controller files, including:

- Frontend routes
  - `I:\SBMS Copy\stable-sbms-frontend\src\app\features\accounts\accounts-routing.module.ts`
  - `I:\SBMS Copy\stable-sbms-frontend\src\app\features\branch\branch-routing.module.ts`
  - `I:\SBMS Copy\stable-sbms-frontend\src\app\features\customer\customer-routing.module.ts`
  - `I:\SBMS Copy\stable-sbms-frontend\src\app\features\financing\financing-routing.module.ts`
  - `I:\SBMS Copy\stable-sbms-frontend\src\app\features\reports\report-routing.module.ts`
- Backend controllers
  - `I:\SBMS Copy\stable-sbms-backend\src\main\java\com\sbms\customer\controller\impl\CustomerController.java`
  - `I:\SBMS Copy\stable-sbms-backend\src\main\java\com\sbms\kyc\controller\impl\KycController.java`
  - `I:\SBMS Copy\stable-sbms-backend\src\main\java\com\sbms\account\controller\impl\AccountOpeningRequestController.java`
  - `I:\SBMS Copy\stable-sbms-backend\src\main\java\com\sbms\financing\controller\impl\FinancingApplicationController.java`
  - `I:\SBMS Copy\stable-sbms-backend\src\main\java\com\sbms\contract\controller\impl\ContractController.java`
  - `I:\SBMS Copy\stable-sbms-backend\src\main\java\com\sbms\shariah\controller\impl\ShariahReviewController.java`
  - `I:\SBMS Copy\stable-sbms-backend\src\main\java\com\sbms\statement\controller\impl\CustomerStatementController.java`
  - `I:\SBMS Copy\stable-sbms-backend\src\main\java\com\sbms\statement\controller\impl\BranchStatementController.java`
  - `I:\SBMS Copy\stable-sbms-backend\src\main\java\com\sbms\report\controller\impl\ReportController.java`
  - `I:\SBMS Copy\stable-sbms-backend\src\main\java\com\sbms\accounting\controller\impl\AccountingCoreController.java`

## 3. Confirmed Major Process Count

Confirmed major process groups in the project: `14`

1. System Administration
2. Lookup / Configuration
3. Branch Setup And Cash Control
4. Customer Onboarding
5. KYC Verification Workflow
6. Account Opening Workflow
7. Core Transactions
8. Deposit Scheme Process
9. Profit Management
10. Cards + ATM/CDM
11. Statement Generation
12. Financing + Contract + Shariah
13. Zakat + Notification + Integration + Verification
14. Workflow + Security + Reporting + Accounting

## 4. Roles You Should Keep Ready

### Minimum safe demo set

If you want a practical demo without too much login switching, keep these `4` logins ready:

1. `SYSTEM_ADMIN`
2. `OPERATIONS_OFFICER`
3. `INVESTMENT_OFFICER`
4. `SHARIAH_BOARD_MEMBER`

### Full role-based demo set

If you want to show the full role story, keep these `7` logins ready:

1. `SYSTEM_ADMIN`
2. `BRANCH_MANAGER`
3. `OPERATIONS_OFFICER`
4. `TELLER`
5. `INVESTMENT_OFFICER`
6. `SHARIAH_BOARD_MEMBER`
7. `CUSTOMER`

## 5. Best Login Strategy For Tomorrow

### Strategy A: Best for a smooth live demo

Use only `4` logins:

- `SYSTEM_ADMIN`
  - Show admin, branch, dashboards, reports, accounting, integrations
- `OPERATIONS_OFFICER`
  - Show customer, KYC, account opening, transactions, statements, profit, workflow
- `INVESTMENT_OFFICER`
  - Show financing review, disbursement, contract generation
- `SHARIAH_BOARD_MEMBER`
  - Show shariah review, contract shariah sign, zakat governance

### Strategy B: Best if examiners ask “who does this task?”

Use all `7` logins:

- `SYSTEM_ADMIN`
- `BRANCH_MANAGER`
- `OPERATIONS_OFFICER`
- `TELLER`
- `INVESTMENT_OFFICER`
- `SHARIAH_BOARD_MEMBER`
- `CUSTOMER`

## 6. Full Demo Storyline

The strongest presentation order is:

1. System setup
2. Branch setup
3. Customer onboarding
4. KYC review
5. Account opening
6. Daily transactions
7. Deposit / profit / cards / ATM
8. Statements
9. Financing lifecycle
10. Contract and shariah governance
11. Zakat and support services
12. Workflow and security control
13. Reports
14. Management P&L
15. Trial Balance
16. Ledger Profit & Loss

This tells a complete business story from onboarding to accounting control.

## 7. Process-by-Process Demo Instructions

### Process 1: System Administration

Role: `SYSTEM_ADMIN`  
Start pages:

- `Admin -> Roles Dashboard`
- `Admin -> Roles`
- `Admin -> Users Dashboard`
- `Admin -> Users`

Flow:

1. Open roles dashboard
2. Open role list
3. Open a role and show permission mapping
4. Open users dashboard
5. Open user list
6. Open assign role / reset password / lock-unlock actions

What to say:

- “This is the access-control foundation of the system.”
- “Roles and permissions are not hardcoded in the UI.”
- “Users can be assigned, locked, unlocked, and reset from here.”

### Process 2: Lookup / Configuration

Role: `SYSTEM_ADMIN`  
Start pages:

- `Lookups -> Dashboard`
- `Lookups -> Types`
- `Lookups -> Values`

Flow:

1. Open lookup dashboard
2. Open lookup type list
3. Open lookup values
4. Explain this powers dropdowns and config-driven behavior

What to say:

- “Master data and configurable dropdown values are centrally controlled here.”

### Process 3: Branch Setup And Cash Control

Roles: `SYSTEM_ADMIN`, `BRANCH_MANAGER`  
Start pages:

- `Branch -> Dashboard`
- `Branch -> List`
- `Branch -> Assignments`
- `Branch -> Teller Limits`
- `Branch -> Vault`
- `Branch -> Cash Ledger`
- `Branch -> Inter-Branch Transfer`
- `Branch -> EOD Summary`

Flow:

1. As `SYSTEM_ADMIN`, open branch dashboard and branch list
2. Show branch creation and branch details
3. Open assignments and explain branch-user mapping
4. Open teller limits and explain control limits
5. Open vault list, then vault open/view
6. Open cash ledger
7. Open inter-branch transfer
8. Open EOD summary

What to say:

- “This is where branch structure, staff mapping, teller authority, and vault control are managed.”

### Process 4: Customer Onboarding

Roles: `TELLER`, `OPERATIONS_OFFICER`  
Start pages:

- `Customer -> Dashboard`
- `Customer -> List`
- `Customer -> New`
- `Customer -> Search`
- `Customer -> Status Action`

Flow:

1. As `OPERATIONS_OFFICER`, open customer dashboard
2. Open customer list
3. Open one customer record
4. Show address and identity sections
5. Show status action page
6. Show customer timeline

What to say:

- “Customer onboarding starts here.”
- “Customer profile, address, identity, status, and lifecycle trail are all tracked.”

### Process 5: KYC Verification Workflow

Roles: `OPERATIONS_OFFICER`, `BRANCH_MANAGER`  
Start pages:

- `KYC -> Dashboard`
- `KYC -> List`
- `KYC -> New`
- `KYC -> Approval Queue`
- `KYC -> Review`

Confirmed backend actions:

- create
- update
- submit
- verify
- approve
- reject
- return
- document upload
- decision history

Flow:

1. Open KYC dashboard
2. Open KYC list
3. Open one KYC profile
4. Show approval queue
5. Open review page
6. Explain submit -> verify -> approve/reject/return chain
7. Show document and history if available

What to say:

- “After customer creation, the compliance layer starts here.”
- “KYC is not just a form; it has a full workflow and history.”

### Process 6: Account Opening Workflow

Roles: `TELLER`, `OPERATIONS_OFFICER`, `BRANCH_MANAGER`  
Start pages:

- `Accounts -> Dashboard`
- `Accounts -> Account Types`
- `Accounts -> Opening Requests`
- `Accounts -> New Opening Request`
- `Accounts -> Review`
- `Accounts -> List`

Confirmed backend actions:

- create request
- update request
- submit
- verify
- approve
- reject
- return
- document preview/download

Flow:

1. Open account dashboard
2. Show account type list
3. Open opening request list
4. Open a request record
5. Open review page
6. Explain approval chain
7. Open account list and connect approved request to actual account records

What to say:

- “A KYC-cleared customer becomes an account holder through this controlled workflow.”

### Process 7: Core Transactions

Roles: `TELLER`, `OPERATIONS_OFFICER`  
Start pages:

- `Transactions -> Dashboard`
- `Transactions -> Deposit`
- `Transactions -> Withdraw`
- `Transactions -> Transfer`
- `Transactions -> Cheque Clearing`
- `Transactions -> Standing Instructions`
- `Transactions -> List`

Confirmed backend actions:

- deposit
- withdraw
- transfer
- cheque clearing
- standing instruction create/list
- reverse transaction
- journal view
- voucher preview/download

Flow:

1. Open transaction dashboard
2. Show deposit page
3. Show withdrawal page
4. Show transfer page
5. Show cheque clearing page
6. Show standing instructions list
7. Open transaction list
8. Open one transaction and show journal/voucher

What to say:

- “This is the daily operational transaction layer of the bank.”

### Process 8: Deposit Scheme Process

Role: `OPERATIONS_OFFICER`  
Start pages:

- `Deposit Schemes -> Dashboard`
- `Deposit Schemes -> List`
- `Deposit Schemes -> Enrollments`

Confirmed backend actions:

- create scheme
- update scheme
- archive/restore scheme
- create enrollment
- certificate preview/download
- schedule view
- profit distribution view

Flow:

1. Open scheme dashboard
2. Open scheme list
3. Open enrollment list
4. Open one enrollment
5. Show schedule and profit sections

What to say:

- “Deposit products and customer scheme enrollment are managed here.”

### Process 9: Profit Management

Roles: `OPERATIONS_OFFICER`, `BRANCH_MANAGER`  
Start pages:

- `Profit -> Dashboard`
- `Profit -> Ratios`
- `Profit -> Schedules`
- `Profit -> Postings`
- `Profit -> Run Posting`

Confirmed backend actions:

- run posting
- advice preview/download
- schedule create/list
- ratio create/list

Flow:

1. Open profit dashboard
2. Open ratio list
3. Open schedule list
4. Open posting list
5. Open run posting screen
6. Open one posting and show advice preview/download

What to say:

- “This is the Islamic profit calculation and posting layer.”

### Process 10: Cards + ATM/CDM

Role: `OPERATIONS_OFFICER`  
Start pages:

- `Cards -> Dashboard`
- `Cards -> List`
- `Cards -> Activate / Block-Unblock / PIN Events`
- `ATM -> Dashboard`
- `ATM -> Terminals`
- `ATM -> Replenishments`
- `ATM -> Reconciliations`

Confirmed backend actions:

- card create/update
- activate
- block/unblock
- replace
- renew
- PIN events
- ATM/CDM transaction list
- terminal create/update
- replenishment create
- reconciliation create

Flow:

1. Open cards dashboard
2. Open card list
3. Open one card and show activate/block or event actions
4. Open ATM dashboard
5. Open terminal list
6. Open replenishments
7. Open reconciliations

What to say:

- “The system covers both customer card lifecycle and ATM operational control.”

### Process 11: Statement Generation

Roles: `CUSTOMER`, `TELLER`, `OPERATIONS_OFFICER`  
Start pages:

- `Statement -> Dashboard`
- `Statement -> Customer Request`
- `Statement -> Customer List`
- `Statement -> Branch Request`
- `Statement -> Branch List`
- `Statement -> Export Center`

Confirmed backend actions:

- customer request
- branch request
- preview
- download
- export

Flow:

1. Open statement dashboard
2. Show customer request page
3. Open customer statement list
4. Open branch request page
5. Open branch statement list
6. Open export center

What to say:

- “Both customer and branch statement servicing are available.”

### Process 12: Financing + Contract + Shariah

Roles: `INVESTMENT_OFFICER`, `SHARIAH_BOARD_MEMBER`

#### Financing

Start pages:

- `Financing -> Dashboard`
- `Financing -> Products`
- `Financing -> Applications`
- `Financing -> Review`
- `Financing -> Disburse`
- `Financing -> Schedule`
- `Financing -> Repayment`

Confirmed backend actions:

- create application
- update
- submit
- verify
- review
- approve
- reject
- return
- disburse
- schedule
- collect-payment
- sanction-letter preview/download

Flow:

1. Open financing dashboard
2. Open product list
3. Open application list
4. Open one application
5. Show review page
6. Show disburse page
7. Show schedule page
8. Show repayment collection page

What to say:

- “This is the full financing lifecycle from application to repayment.”

#### Contract

Start pages:

- `Contracts -> Dashboard`
- `Contracts -> Templates`
- `Contracts -> Generate`
- `Contracts -> List`
- `Contracts -> Sign`

Confirmed backend actions:

- template create/update/archive/restore
- generate contract
- customer sign
- shariah sign
- versions
- print-copy preview/download

Flow:

1. Open contract dashboard
2. Open template list
3. Open generate contract page
4. Open contract list
5. Open a contract record and versions
6. Explain customer sign then shariah sign

#### Shariah

Start pages:

- `Shariah -> Dashboard`
- `Shariah -> Cases`
- `Shariah -> Review`
- `Shariah -> Correction Queue`
- `Shariah -> Fatwa Certificates`

Confirmed backend actions:

- create case
- checklist save
- approve
- reject
- return
- history

Flow:

1. Open shariah dashboard
2. Open case list
3. Open case review
4. Open correction queue
5. Open fatwa certificate list

What to say:

- “This is the Islamic governance and review layer.”

### Process 13: Zakat + Notification + Integration + Verification

#### Zakat

Roles: `SHARIAH_BOARD_MEMBER`, `OPERATIONS_OFFICER`  
Start pages:

- `Zakat -> Dashboard`
- `Zakat -> Profiles`
- `Zakat -> Calc Run`
- `Zakat -> Beneficiaries`
- `Zakat -> Payouts`

Confirmed backend actions:

- create/update zakat profile
- preview/download profile sheet
- calculate
- create beneficiary
- create payout
- preview/download payout receipt

#### Notifications

Roles: `SYSTEM_ADMIN`, `OPERATIONS_OFFICER`  
Start pages:

- `Notifications -> Dashboard`
- `Notifications -> Templates`
- `Notifications -> Event Rules`
- `Notifications -> Logs`
- `Notifications -> Retry Queue`

Confirmed backend actions:

- template create/update/archive/restore
- event rule create/list
- logs list/view
- retry log

#### Integrations

Roles: `SYSTEM_ADMIN`, `OPERATIONS_OFFICER`  
Start pages:

- `Integrations -> Dashboard`
- `Integrations -> Providers`
- `Integrations -> Logs`
- `Integrations -> Provider Test`

Confirmed backend actions:

- provider create/update/archive/restore
- provider test
- logs list/view
- retry log

#### Verification

Roles: `SYSTEM_ADMIN`, `OPERATIONS_OFFICER`  
Start pages:

- `Verification -> Dashboard`
- `Verification -> OTP Verify`
- `Verification -> Reset Password`
- `Verification -> Provider Test`

Confirmed backend actions:

- send email OTP
- send mobile OTP
- verify OTP
- resend OTP
- expire OTP
- mark failed
- provider test
- step-up request/resend/verify

### Process 14: Workflow + Security + Reporting + Accounting

#### Workflow

Roles: `OPERATIONS_OFFICER`, `BRANCH_MANAGER`, `SYSTEM_ADMIN`

Start pages:

- `Workflow -> Dashboard`
- `Workflow -> History`
- `Workflow -> Pending`
- `Workflow -> My Submissions`

Confirmed backend actions:

- history list/view
- pending list
- my submissions
- dashboard summary

#### Security

Roles: `SYSTEM_ADMIN`, `BRANCH_MANAGER`

Start pages:

- `Security -> Dashboard`
- `Security -> Events`
- `Security -> Suspicious Activities`
- `Security -> Audit Logs`
- `Security -> Investigation Cases`

Confirmed backend actions:

- list/view security events
- suspicious activity view
- audit log view
- assign investigation
- close investigation

#### Reporting + Accounting

Roles: `SYSTEM_ADMIN`, `OPERATIONS_OFFICER`

Start pages:

- `Reports -> Dashboard`
- `Reports -> Operational`
- `Reports -> Management P&L`
- `Reports -> Management Expenses`
- `Reports -> Trial Balance`
- `Reports -> Ledger Profit & Loss`
- `Reports -> Export History`

Confirmed backend actions:

- operational report
- management P&L
- trial balance report
- ledger profit & loss report
- export history preview/download
- accounting trial balance
- accounting profit-loss
- accounting journals
- journal summary

Flow:

1. Open report dashboard
2. Open one operational report
3. Open management P&L
4. Open expense register
5. Open trial balance
6. Open ledger profit & loss
7. Use export history
8. Use journal drill-down on trial balance and ledger P&L

What to say:

- “This layer gives management reporting, accounting visibility, and traceability down to journals.”

## 8. Exact Demo Sequence You Should Practice

### Part A: Foundation

1. Login as `SYSTEM_ADMIN`
2. Show header: search, time, theme, profile
3. Show roles
4. Show users
5. Show lookups

### Part B: Branch And Customer Base

6. Show branch dashboard
7. Show branch assignments
8. Show teller limits
9. Show vault
10. Show customer dashboard
11. Show customer list

### Part C: Compliance And Account Opening

12. Switch to `OPERATIONS_OFFICER`
13. Show KYC dashboard
14. Show KYC approval queue
15. Show accounts dashboard
16. Show opening requests
17. Show one review page
18. Show approved accounts in account list

### Part D: Daily Banking Operations

19. Switch to `TELLER`
20. Show deposit
21. Show withdraw
22. Show transfer
23. Show transaction list

### Part E: Product And Servicing

24. Switch to `OPERATIONS_OFFICER`
25. Show deposit schemes
26. Show profit dashboard
27. Show cards dashboard
28. Show ATM dashboard
29. Show statement dashboard

### Part F: Financing And Islamic Governance

30. Switch to `INVESTMENT_OFFICER`
31. Show financing dashboard
32. Show applications list
33. Show review/disburse/schedule
34. Show contract generate
35. Switch to `SHARIAH_BOARD_MEMBER`
36. Show shariah dashboard
37. Show case review

### Part G: Support And Monitoring

38. Switch back to `SYSTEM_ADMIN`
39. Show zakat dashboard
40. Show notifications dashboard
41. Show integrations dashboard
42. Show verification dashboard
43. Show workflow dashboard
44. Show security dashboard

### Part H: Final Executive Close

45. Show report dashboard
46. Show management P&L
47. Show expense register
48. Show trial balance
49. Show ledger profit & loss
50. End with journal drill-down and export history

## 9. Best Role Switching Plan

### Short version

Use only these login switches:

1. `SYSTEM_ADMIN`
2. `OPERATIONS_OFFICER`
3. `TELLER`
4. `INVESTMENT_OFFICER`
5. `SHARIAH_BOARD_MEMBER`
6. Back to `SYSTEM_ADMIN`

This means only `6` login phases, not constant switching.

## 10. What Each Role Should Perform In Demo

### `SYSTEM_ADMIN`

- role setup
- user setup
- lookups
- branch control overview
- integrations
- reporting
- accounting/P&L

### `BRANCH_MANAGER`

- branch staff mapping
- teller limits
- vault and EOD control
- approval visibility
- monthly closing visibility

### `OPERATIONS_OFFICER`

- customer onboarding
- KYC processing
- account opening review
- scheme operations
- profit posting visibility
- statement generation
- workflow queue

### `TELLER`

- deposit
- withdrawal
- transfer
- customer/account request initiation

### `INVESTMENT_OFFICER`

- financing product
- financing application review
- disbursement
- repayment/schedule visibility
- contract generation

### `SHARIAH_BOARD_MEMBER`

- shariah case review
- approve/reject/return
- shariah sign
- zakat governance

### `CUSTOMER`

- statement request origin
- financing request origin
- zakat profile context

## 11. What To Say During Each Step

Use this simple format every time:

1. “এই moduleটা কী কাজ করে”
2. “এই role এখানে কী perform করে”
3. “flowটা এখান থেকে কোথায় যায়”
4. “শেষে কোন report বা dashboard-এ এর output দেখা যায়”

Example:

- “Customer onboarding এখান থেকে শুরু হয়.”
- “Operations Officer customer data finalize করে.”
- “তারপর KYC approval queue-তে যায়.”
- “Approved হলে account opening request তৈরি করা যায়.”

## 12. Safest Finale

Always end with:

1. `Management P&L`
2. `Expense Register`
3. `Trial Balance`
4. `Ledger Profit & Loss`

Reason:

- management reporting
- manual expense input
- debit-credit control
- branch-wise ledger profitability
- journal drill-down

This is the strongest executive finish for the whole project.

## 13. Practice Checklist

Before tomorrow, manually practice this exact list:

1. Login with `SYSTEM_ADMIN`
2. Open every dashboard once
3. Open one list page in each major module
4. Open one view page in each major module
5. Practice the role-switch order
6. Practice `Management P&L -> Trial Balance -> Ledger Profit & Loss`
7. Practice `View Journals`
8. Practice `Preview / Export / Download / Export History`
9. Keep only tested pages in your live demo

## 14. Final Recommendation

If time is tight, never skip these:

1. Admin
2. Branch
3. Customer
4. KYC
5. Accounts
6. Transactions
7. Financing
8. Contracts / Shariah
9. Reports
10. Management P&L
11. Trial Balance
12. Ledger Profit & Loss

If time remains, then show:

- Cards
- ATM
- Statements
- Deposit Schemes
- Zakat
- Notifications
- Integrations
- Workflow
- Security
- Verification

