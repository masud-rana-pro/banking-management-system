# SBMS Showcase Practice Plan

Showcase date: 2026-06-08

Purpose: SBMS project-er full presentation flow step-by-step practice kora. Ek step verify/fix na hole next step-e jawa hobe na.

## Showcase Flow

1. Login, OTP and session security
2. General Dashboard and analytics overview
3. Branch operations setup
4. Customer onboarding
5. KYC document upload, preview and approval
6. Account opening and account list
7. Transaction operations: deposit, withdraw, transfer
8. Card and deposit scheme operations
9. Profit management and posting
10. Financing and contract workflow
11. Shariah review, fatwa certificate, zakat and charity
12. Statement generation and export
13. Reporting and regulatory module
14. Workflow, verification, audit and notification controls
15. Admin controls: user, role and lookup management

## Sidebar Order Target

Main menu order:

Dashboard -> Branch -> Customer -> KYC -> Account -> Transaction -> Card -> Deposit Scheme -> Profit -> Financing -> Contract -> Shariah -> Zakat -> Statement -> Reporting -> Workflow -> Verification -> Security -> Notification -> Integration -> ATM/CDM -> Calculation -> User -> Role -> Lookup

Submenu rule:

Dashboard first, then list/view pages, then create/action pages, then approval/report/history pages.

## Step Status

- Step 1: Completed
- Step 2: Completed
- Step 3: Completed
- Step 4: Completed
- Step 5: Completed
- Step 6: Completed
- Step 7: Completed
- Step 8: Completed
- Step 9: Completed
- Step 10: Completed
- Step 11: Completed
- Step 12: Completed
- Step 13: Completed
- Step 14: Pending verification

## Step 1: Login and General Dashboard

Goal: System login, OTP mail delivery, dashboard loading, sidebar order and dashboard analytics verify kora.

### Login Details

Use:

```text
Username: admin01
Password: Test@123
```

### Presentation Script

English:

```text
This is the central dashboard of the Shariah Based Banking Management System.
After secure login with OTP, the admin can monitor branch scope, transaction movement,
profitability, module workload, reporting indicators and operational health from one place.
```

Bangla:

```text
এটি Shariah Based Banking Management System-এর central dashboard.
Secure OTP login-এর পর admin এক জায়গা থেকেই branch scope, transaction movement,
profitability, module workload, reporting indicator এবং operational health দেখতে পারে।
```

### What To Verify

1. Login page opens without error.
2. `admin01 / Test@123` diye login dile OTP prompt/mail flow kaj kore.
3. Real email-e OTP ase.
4. OTP submit korle dashboard-e redirect hoy.
5. General Dashboard load hoy.
6. Welcome card readable and professional lage.
7. Branch Scope filter visible.
8. Time window buttons visible and clickable.
9. Analytics cards filter-er pore ek jaygay ache.
10. Statistical Analysis Report chart 1/2 column-e properly dekhay.
11. Chart legend readable.
12. Chart/card dark mode-e readable.
13. Sidebar order showcase flow onujayee:
    Dashboard -> Branch -> Customer -> KYC -> Account -> Transaction -> Card -> Deposit -> Profit -> Financing -> Contract -> Shariah -> Zakat -> Statement -> Reporting.
14. Main sidebar menu expand korle submenu order logical:
    Dashboard/List/Create/Action/Report.

### How To Verify

1. Browser-e frontend open koro.
2. Login koro.
3. OTP mail check kore code dao.
4. Dashboard-e 30 seconds wait kore dekho kono loader stuck hoy kina.
5. Branch Scope dropdown theke `All Branches` and ekta branch select kore dekho.
6. Time window `Today`, `Week`, `Month`, `Year` click kore dekho.
7. Statistical Analysis Report-er chart hover/click kore dekho.
8. Sidebar-e menu open kore order compare koro.
9. Topbar theme button click kore dark mode check koro.
10. Mobile/small width korte parle browser width komiye dekho chart 1 column-e name kina.

### Pass Criteria

Step 1 pass hobe jodi:

- Login successful hoy.
- OTP mail ase and verify hoy.
- Dashboard load hoy without visible error.
- Sidebar main menu and submenu expected flow follow kore.
- Analytics cards and Statistical Analysis Report visually clean lage.
- Dark mode-e text/card/chart readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 1 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step

Step 1 completed.

## Step 2: Branch Operations Setup

Goal: Branch module diye system-er operational foundation show kora. Ei step-e branch dashboard, branch list, branch details, assignment, teller limit, vault/cash ledger and EOD summary verify kora.

### Presentation Script

English:

```text
The Branch Operations module controls the bank's physical and operational structure.
Here we can monitor branch performance, manage branch profiles, assign staff,
control teller limits, track vault balance, review cash ledger movement and prepare EOD summaries.
```

Bangla:

```text
Branch Operations module bank-er physical and operational structure manage kore.
Ei module theke branch performance dekha, branch profile manage kora, staff assignment,
teller limit, vault balance, cash ledger movement ebong EOD summary monitor kora jay.
```

### What To Verify

1. Sidebar-e `Branch Management` menu expected order-e ache.
2. Submenu order:
   Branch Dashboard -> Branch List -> Create Branch -> Branch Assignments -> Teller Limits -> Vault Balance List -> Open Vault -> Cash Ledger -> Inter-Branch Transfer -> EOD Summary.
3. Branch Dashboard open hoy without error.
4. Branch Dashboard-e summary cards/charts data show kore.
5. Branch List open hoy.
6. Branch List-e table data ache.
7. Table action menu/button kaj kore.
8. Branch details/view page open hoy.
9. Create Branch button/form popup or page properly open hoy.
10. Form close korle page blink/loading issue hoy na.
11. Branch Assignments page open hoy and staff/branch relation bujha jay.
12. Teller Limits page open hoy and limit data readable.
13. Vault Balance List page open hoy.
14. Cash Ledger page open hoy and transaction/cash movement readable.
15. EOD Summary page open hoy and report-style data readable.
16. Dark mode-e Branch module readable thake.

### How To Verify

1. Sidebar theke `Branch Management` expand koro.
2. Submenu order visually compare koro.
3. `Branch Dashboard` click koro.
4. Page header, card, chart/table data dekho.
5. `Branch List` click koro.
6. Table-er first row action open kore View/Edit type action test koro.
7. `Create Branch` click kore form open hoy kina dekho. Save na kore close korlei enough.
8. `Branch Assignments` click kore data/form action check koro.
9. `Teller Limits` click kore data and action check koro.
10. `Vault Balance List` click kore vault data ache kina dekho.
11. `Cash Ledger` click kore cash movement row/data ache kina dekho.
12. `EOD Summary` click kore branch/date summary readable kina dekho.
13. Topbar theme button diye dark mode check koro.

### Pass Criteria

Step 2 pass hobe jodi:

- Branch module-er sob listed submenu open hoy.
- Kono page-e visible error/blank loader na thake.
- Branch List/table action kaj kore.
- Create/action form open-close korle blink/loading issue na hoy.
- Dashboard/list/vault/cash/EOD data presentation-friendly hoy.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 2 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 2

Step 2 completed.

## Step 3: Customer Onboarding

Goal: Customer module diye bank-er customer lifecycle show kora. Ei step-e customer dashboard, customer list, new customer form, customer search, profile view and status action verify kora.

### Presentation Script

English:

```text
The Customer Management module is the starting point of the banking lifecycle.
Here the bank can register customers, maintain profile information, search customer records,
review customer status and prepare the customer for KYC, account opening and future transactions.
```

Bangla:

```text
Customer Management module holo banking lifecycle-er starting point.
Ei module diye customer registration, profile maintain, customer search, status review
ebong porer KYC/account opening process-er jonno customer prepare kora hoy.
```

### Process Flow

1. Customer Dashboard diye overview dekhao.
2. Customer List diye existing customer records dekhao.
3. New Customer form open kore registration fields dekhao.
4. Customer Search diye quick lookup capability dekhao.
5. Customer profile/view page open kore full profile dekhao.
6. Status Action diye activation/block/approval type control explain koro.
7. Explain koro je customer onboarding complete hole KYC and account opening next step.

### What To Verify

1. Sidebar-e `Customer Management` submenu order:
   Customer Dashboard -> Customer List -> New Customer -> Customer Search -> Status Action.
2. Customer Dashboard open hoy without error.
3. Dashboard summary/cards/charts meaningful data show kore.
4. Customer List open hoy.
5. Customer List-e data ache.
6. List view and grid view thakle duita mode check koro.
7. Grid view-te action button 3-dot na hoye proper button style ache kina dekho.
8. Table view-te action menu single 3-dot and popup position thik ache kina dekho.
9. Row action theke View open hoy.
10. Row action theke Edit open hoy.
11. New Customer form popup/page open hoy.
12. Image upload preview box thik size-e dekhay kina check koro.
13. Customer Search page open hoy and search box/filter kaj kore.
14. Status Action page open hoy.
15. Dark mode-e card/table/form readable thake.

### How To Verify

1. Sidebar theke `Customer Management` expand koro.
2. Submenu order compare koro.
3. `Customer Dashboard` click koro and page data dekho.
4. `Customer List` click koro.
5. Table view-te first row-er action menu open koro.
6. View/Edit action click kore popup/page open hoy kina dekho.
7. Grid view toggle thakle grid mode-e jao and buttons proper ache kina dekho.
8. `New Customer` click kore form open koro. Save na korleo cholbe.
9. Form-e image upload/preview area visually check koro.
10. `Customer Search` open kore ekta known name/code diye search try koro.
11. `Status Action` open kore form/list readable kina dekho.
12. Dark mode toggle kore sob page readable kina dekho.

### Pass Criteria

Step 3 pass hobe jodi:

- Customer module-er sob listed submenu open hoy.
- Customer Dashboard/List/Search/Status page-e visible error na thake.
- Table and grid actions properly kaj kore.
- Form open-close korle blink/loading issue na hoy.
- Image preview area broken/oversized na hoy.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 3 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 3

Step 3 completed.

## Step 4: KYC Document Upload, Preview and Approval

Goal: Customer onboarding-er por regulatory verification flow show kora. Ei step-e KYC dashboard, KYC list, new KYC profile, document upload/preview and approval queue verify kora.

### Presentation Script

English:

```text
After customer onboarding, the KYC module verifies customer identity and compliance documents.
The bank can create a KYC profile, upload required documents, preview the uploaded files,
track verification status and move eligible records through the approval queue.
```

Bangla:

```text
Customer onboarding-er por KYC module customer-er identity and compliance document verify kore.
Ei module-e KYC profile create, document upload, uploaded file preview, verification status track
ebong approval queue diye eligible record approve kora jay.
```

### Process Flow

1. KYC Dashboard diye verification overview dekhao.
2. KYC List diye existing KYC profiles dekhao.
3. New KYC Profile form open kore customer/document fields dekhao.
4. Document upload section check koro.
5. Uploaded document preview button diye file preview dekhao.
6. Approval Queue diye pending verification workflow explain koro.
7. Explain koro je KYC approved hole account opening process safe hoy.

### What To Verify

1. Sidebar-e `KYC Management` submenu order:
   KYC Dashboard -> KYC List -> New KYC Profile -> Approval Queue.
2. KYC Dashboard open hoy without error.
3. KYC List open hoy and data ache.
4. KYC List action menu open hoy.
5. View/Edit action kaj kore.
6. New KYC Profile form open hoy.
7. Document Type, Document No, Issue/Expiry date fields visible.
8. Document upload control kaj kore.
9. Upload korle file name original document name-er moto show kore.
10. Preview click korle error chara document preview hoy.
11. Existing uploaded document preview kaj kore.
12. Approval Queue open hoy and pending/approved data readable.
13. Dark mode-e form/table/preview readable thake.

### How To Verify

1. Sidebar theke `KYC Management` expand koro.
2. Submenu order compare koro.
3. `KYC Dashboard` open kore summary dekho.
4. `KYC List` open kore table data dekho.
5. First row action menu theke View/Preview click kore dekho.
6. Document preview button thakle click kore file viewer/error state check koro.
7. `New KYC Profile` click kore form open koro.
8. Upload field-e ekta safe small document/image select kore preview state dekho. Submit na korleo cholbe.
9. Uploaded file name original name-er sathe match kore kina dekho.
10. `Approval Queue` open kore pending records/readability check koro.
11. Dark mode toggle kore same pages readable kina dekho.

### Pass Criteria

Step 4 pass hobe jodi:

- KYC module-er sob listed submenu open hoy.
- KYC list/action/form kaj kore.
- Document upload control broken na hoy.
- Preview click korle `Failed to upload/preview KYC document file` type error na dey.
- Document file name readable and expected thake.
- Approval Queue readable thake.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 4 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 4

Step 4 completed.

## Step 5: Account Opening and Account List

Goal: KYC-approved customer-er jonno account opening process show kora. Ei step-e account dashboard, account list, opening request, account type, approval/review and created account verify kora.

### Presentation Script

English:

```text
After customer onboarding and KYC verification, the customer becomes eligible for account opening.
The Account Management module handles account types, opening requests, review workflow,
account creation, status control and balance visibility for banking operations.
```

Bangla:

```text
Customer onboarding and KYC verification complete hole customer account opening-er jonno eligible hoy.
Account Management module account type, opening request, review workflow, account creation,
status control and balance visibility manage kore.
```

### Role Responsibility

```text
Teller or Branch Staff prepares the account opening request.
Operations Officer reviews the request and documents.
Branch Manager approves the request.
After approval, the account becomes active for transactions.
```

Bangla:

```text
Teller ba Branch Staff account opening request prepare kore.
Operations Officer request and document review kore.
Branch Manager request approve kore.
Approve hole account transaction-er jonno active hoy.
```

### Process Flow

1. Account Dashboard diye account portfolio overview dekhao.
2. Account Types diye product/category explain koro.
3. Opening Requests list diye pending/approved requests dekhao.
4. New Opening Request form open kore customer/account type selection dekhao.
5. KYC-approved customer-er sathe request link explain koro.
6. Review action diye approval workflow explain koro.
7. Account List diye created active accounts dekhao.
8. Account action diye deposit/withdraw/transfer/statement shortcut explain koro.

### What To Verify

1. Sidebar-e `Account Management` submenu order:
   Account Dashboard -> Account List -> Opening Requests -> New Opening Request -> Account Types -> New Account Type.
2. Account Dashboard open hoy.
3. Summary/cards/charts data meaningful.
4. Account Types page open hoy and data ache.
5. Opening Requests page open hoy and data ache.
6. New Opening Request form open hoy.
7. Customer select/search field kaj kore.
8. Account type select field kaj kore.
9. Form open-close korle blink/loading issue na hoy.
10. Opening request row action View/Edit/Review kaj kore.
11. Account List open hoy and active account data ache.
12. Account table action menu single 3-dot mode-e thik position-e open hoy.
13. Grid view thakle button style thik ache.
14. Account action shortcut deposit/withdraw/transfer/statement route-e jay kina.
15. Dark mode-e table/form/card readable thake.

### How To Verify

1. Sidebar theke `Account Management` expand koro.
2. Submenu order compare koro.
3. `Account Dashboard` open kore overview dekho.
4. `Account Types` open kore account product/category show koro.
5. `Opening Requests` open kore list data dekho.
6. First request row action theke View/Review open kore dekho.
7. `New Opening Request` open kore form fields dekho. Save na korleo cholbe.
8. Customer select/search-e recently used customer ache kina dekho.
9. Account type select kore form readiness check koro.
10. `Account List` open kore active account records dekho.
11. First row action menu open kore shortcuts dekho.
12. Deposit/Withdraw/Transfer/Statement shortcut thakle route open hoy kina check koro.
13. Dark mode toggle kore readability check koro.

### Pass Criteria

Step 5 pass hobe jodi:

- Account module-er sob listed submenu open hoy.
- Account Dashboard/List/Opening Requests/Account Types data show kore.
- New Opening Request form properly open hoy.
- Review/action menu kaj kore.
- Account List shortcut routes kaj kore.
- Form/table/card dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 5 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 5

Step 5 completed.

## Step 6: Transaction Operations

Goal: Active account-er upor daily banking transaction flow show kora. Ei step-e transaction dashboard, journal, cash deposit, cash withdraw, fund transfer, cheque clearing, standing instruction and voucher/ledger impact verify kora.

### Presentation Script

English:

```text
After an account is opened, transaction operations allow branch users to post deposits,
withdrawals and transfers. Every transaction updates account balance, creates journal evidence,
supports voucher preview and feeds downstream statements, reports and profitability analysis.
```

Bangla:

```text
Account open howar por transaction module diye deposit, withdraw and transfer post kora hoy.
Protita transaction account balance update kore, journal evidence create kore,
voucher preview support kore ebong statement/report/profitability analysis-e data pathay.
```

### Role Responsibility

```text
Teller posts cash deposit and cash withdraw.
Operations Officer reviews journal and clearing operations.
Branch Manager monitors daily transaction position and exceptions.
```

Bangla:

```text
Teller cash deposit and withdraw post kore.
Operations Officer journal and clearing operation review kore.
Branch Manager daily transaction position and exception monitor kore.
```

### Process Flow

1. Transaction Dashboard diye daily transaction summary dekhao.
2. Cash Deposit form diye active account-e deposit process explain koro.
3. Transaction Journal diye posted transaction verify koro.
4. Cash Withdraw form diye debit process explain koro.
5. Fund Transfer form diye account-to-account movement explain koro.
6. Cheque Clearing diye non-cash/clearing workflow explain koro.
7. Standing Instructions diye recurring/scheduled instruction explain koro.
8. Voucher preview/print thakle transaction evidence show koro.
9. Explain koro je ei transaction data account statement and reports-e reflect kore.

### What To Verify

1. Sidebar-e `Transactions Management` submenu order:
   Transaction Dashboard -> Transaction Journal -> Cash Deposit -> Cash Withdraw -> Fund Transfer -> Cheque Clearing -> Standing Instructions -> New Standing Instruction.
2. Transaction Dashboard open hoy and summary meaningful.
3. Transaction Journal open hoy and rows/data ache.
4. Journal row action menu kaj kore.
5. Voucher/preview/print action thakle error chara open hoy.
6. Cash Deposit form open hoy.
7. Account search/select kaj kore.
8. Deposit amount field kaj kore.
9. Form save/submit korle duplicate or risky live post korte na chaile submit na kore form readiness verify koro.
10. Cash Withdraw form open hoy and account select kaj kore.
11. Fund Transfer form open hoy and from/to account select kaj kore.
12. Cheque Clearing page open hoy.
13. Standing Instructions list open hoy.
14. New Standing Instruction form open hoy.
15. Dark mode-e form/table/voucher readable thake.

### How To Verify

1. Sidebar theke `Transactions Management` expand koro.
2. Submenu order compare koro.
3. `Transaction Dashboard` open kore summary dekho.
4. `Transaction Journal` open kore recent transaction data dekho.
5. First row action menu open kore View/Voucher/Preview thakle click kore dekho.
6. `Cash Deposit` open kore active account select/search korte paro kina dekho.
7. Amount/narration fields visible kina dekho. Demo-safe na hole submit korbe na.
8. `Cash Withdraw` open kore same readiness check koro.
9. `Fund Transfer` open kore source and destination account fields dekho.
10. `Cheque Clearing` open kore list/form readable kina dekho.
11. `Standing Instructions` open kore data dekho.
12. `New Standing Instruction` open kore form load hoy kina dekho.
13. Dark mode toggle kore readability check koro.

### Pass Criteria

Step 6 pass hobe jodi:

- Transaction module-er sob listed submenu open hoy.
- Dashboard/Journal data show kore.
- Transaction action menu and voucher preview kaj kore.
- Deposit/Withdraw/Transfer forms load hoy and account select kaj kore.
- Cheque Clearing and Standing Instruction pages readable.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 6 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 6

Step 6 completed.

## Step 7: Card and Deposit Scheme Operations

Goal: Active account-er sathe connected customer service operations show kora. Ei step-e card dashboard/list/issue card, ATM usage, deposit scheme dashboard/list/enrollment and certificate/preview verify kora.

### Presentation Script

English:

```text
After accounts and transactions are active, customers can use additional banking services.
The Card module supports card issuing, card monitoring and ATM/CDM usage.
The Deposit Scheme module manages savings or investment schemes, customer enrollment,
installment tracking and certificate preview for customer service.
```

Bangla:

```text
Account and transaction active howar por customer additional banking service use korte pare.
Card module card issue, card monitoring and ATM/CDM usage support kore.
Deposit Scheme module savings/investment scheme, enrollment, installment tracking
and certificate preview manage kore.
```

### Role Responsibility

```text
Teller or Operations Officer can initiate card issue and scheme enrollment.
Branch Manager or Operations Officer reviews service activity and customer support records.
```

Bangla:

```text
Teller ba Operations Officer card issue and scheme enrollment initiate kore.
Branch Manager ba Operations Officer service activity and customer support record review kore.
```

### Process Flow

1. Card Dashboard diye card service overview dekhao.
2. Card List diye issued cards dekhao.
3. Issue Card form open kore active customer/account link explain koro.
4. ATM/CDM Usage diye card transaction/service usage explain koro.
5. Deposit Scheme Dashboard diye scheme portfolio overview dekhao.
6. Scheme List diye available schemes dekhao.
7. Enrollment List diye customer enrollment dekhao.
8. New Enrollment form open kore account/customer/scheme link explain koro.
9. Certificate/preview action thakle customer copy show koro.

### What To Verify

1. Sidebar-e `Card Management` submenu order:
   Card Dashboard -> Card List -> Issue Card -> PIN Events -> ATM/CDM Usage.
2. Card Dashboard open hoy and data meaningful.
3. Card List open hoy and card records ache.
4. Card List action menu/button kaj kore.
5. Issue Card form open hoy.
6. Customer/account select field kaj kore.
7. ATM/CDM Usage page open hoy.
8. Sidebar-e `Deposit Schemes Management` submenu order:
   Scheme Dashboard -> Scheme List -> New Scheme -> Enrollment List -> New Enrollment.
9. Scheme Dashboard open hoy.
10. Scheme List open hoy and data ache.
11. Enrollment List open hoy and records ache.
12. Enrollment action/certificate preview thakle kaj kore.
13. New Enrollment form open hoy and customer/account/scheme fields kaj kore.
14. Dark mode-e card/scheme pages readable thake.

### How To Verify

1. Sidebar theke `Card Management` expand koro.
2. Submenu order compare koro.
3. `Card Dashboard` open kore summary dekho.
4. `Card List` open kore row/action check koro.
5. `Issue Card` open kore active account/customer fields check koro. Submit na korleo cholbe.
6. `ATM/CDM Usage` open kore usage data readable kina dekho.
7. Sidebar theke `Deposit Schemes Management` expand koro.
8. `Scheme Dashboard` open kore overview dekho.
9. `Scheme List` open kore scheme records dekho.
10. `Enrollment List` open kore enrolled customer records dekho.
11. Certificate/preview/download action thakle click kore dekho.
12. `New Enrollment` open kore form readiness check koro.
13. Dark mode toggle kore readability check koro.

### Pass Criteria

Step 7 pass hobe jodi:

- Card and Deposit Scheme module-er listed submenu open hoy.
- Card List, Issue Card, ATM/CDM Usage readable and working.
- Scheme Dashboard/List/Enrollment pages data show kore.
- Enrollment certificate/preview thakle kaj kore.
- Forms open-close korle blink/loading issue na hoy.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 7 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 7

Step 7 completed.

## Step 8: Profit Management and Posting

Goal: Islamic banking profit configuration, schedule generation and posting workflow show kora. Ei step-e profit dashboard, ratio setup, schedule list/new schedule, posting list and run profit posting verify kora.

### Presentation Script

English:

```text
The Profit Management module handles Shariah-based profit calculation and posting.
It allows the bank to configure profit ratios, generate account-wise profit schedules,
run periodic profit posting and keep an auditable posting history for reports and statements.
```

Bangla:

```text
Profit Management module Shariah-based profit calculation and posting manage kore.
Ei module diye profit ratio configure, account-wise profit schedule generate,
periodic profit posting run and posting history audit/report-er jonno preserve kora hoy.
```

### Role Responsibility

```text
Operations Officer prepares profit ratios and schedules.
Branch Manager or authorized operations user reviews and runs posting.
Reports module later uses this posting data for profit distribution and P&L reporting.
```

Bangla:

```text
Operations Officer profit ratio and schedule prepare kore.
Branch Manager ba authorized operations user posting review and run kore.
Reports module pore ei posting data profit distribution and P&L report-e use kore.
```

### Process Flow

1. Profit Dashboard diye overall profit position dekhao.
2. Profit Ratios diye account/product-wise ratio configuration explain koro.
3. New Profit Ratio form open kore ratio setup fields dekhao.
4. Profit Schedules diye account-wise scheduled profit rows dekhao.
5. New Profit Schedule form open kore account, ratio, period and frequency link explain koro.
6. Profit Postings diye already posted profit history dekhao.
7. Run Profit Posting page diye periodic posting execution explain koro.
8. Advice/preview/print thakle posting evidence show koro.
9. Explain koro je posted profit statement and reports-e reflect kore.

### What To Verify

1. Sidebar-e `Profit Management` submenu order:
   Profit Dashboard -> Profit Ratios -> New Profit Ratio -> Profit Schedules -> New Profit Schedule -> Profit Postings -> Run Profit Posting.
2. Profit Dashboard open hoy and summary/cards/charts data meaningful.
3. Profit Ratios page open hoy.
4. Profit ratio table data ache.
5. New Profit Ratio form open hoy and ratio/account type/frequency fields visible.
6. Profit Schedules page open hoy and schedule data ache.
7. New Profit Schedule form open hoy and account/ratio/date fields kaj kore.
8. Profit Postings page open hoy and posting history data ache.
9. Posting list action/preview/advice thakle kaj kore.
10. Run Profit Posting page open hoy.
11. Account/schedule/posting date fields properly fit kore, side-e dhuke jay na.
12. Run button demo-safe kina bujhe submit korbe; risky hole form readiness only verify koro.
13. Dark mode-e sob readable thake.

### How To Verify

1. Sidebar theke `Profit Management` expand koro.
2. Submenu order compare koro.
3. `Profit Dashboard` open kore cards/charts dekho.
4. `Profit Ratios` open kore ratio list dekho.
5. `New Profit Ratio` open kore form fields dekho. Save na korleo cholbe.
6. `Profit Schedules` open kore data/action check koro.
7. `New Profit Schedule` open kore account/ratio/date selection dekho.
8. `Profit Postings` open kore posting history dekho.
9. Posting row action theke advice/preview/print thakle click kore dekho.
10. `Run Profit Posting` open kore form layout check koro.
11. Run button click korar age existing data and duplicate risk bujhe nao. Demo-safe na hole submit korbe na.
12. Dark mode toggle kore readability check koro.

### Pass Criteria

Step 8 pass hobe jodi:

- Profit module-er sob submenu open hoy.
- Dashboard/ratio/schedule/posting data show kore.
- New ratio and schedule form properly open hoy.
- Run Profit Posting layout responsive thake.
- Posting preview/advice thakle kaj kore.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 8 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 8

Step 8 completed.

## Step 9: Financing and Contract Workflow

Goal: Islamic financing lifecycle show kora. Ei step-e financing dashboard, product setup, application list/new application, application workflow, sanction preview and contract generation verify kora.

### Presentation Script

English:

```text
The Financing module manages Islamic investment or loan-like products from application to approval.
It connects customer profile, branch, product configuration, risk review, Shariah review and sanction evidence.
After approval, the Contract module generates formal documents based on approved financing data and templates.
```

Bangla:

```text
Financing module Islamic investment ba loan-like product-er application theke approval porjonto lifecycle manage kore.
Eta customer profile, branch, product configuration, risk review, Shariah review and sanction evidence connect kore.
Approval-er por Contract module approved financing data and template diye formal document generate kore.
```

### Role Responsibility

```text
Investment Officer prepares financing products and applications.
Operations Officer checks operational completeness.
Shariah Board reviews Shariah compliance where required.
Branch Manager or authorized approver approves or moves the case forward.
Contract team/user generates final contract documents.
```

Bangla:

```text
Investment Officer financing product and application prepare kore.
Operations Officer operational completeness check kore.
Shariah Board proyojon hole Shariah compliance review kore.
Branch Manager ba authorized approver case approve/move forward kore.
Contract user final contract document generate kore.
```

### Financing Process Flow

1. Financing Dashboard diye portfolio/application overview dekhao.
2. Product List diye available financing products dekhao.
3. New Product form open kore product setup fields explain koro.
4. Application List diye customer financing requests dekhao.
5. New Application form open kore customer/product/amount/tenure fields explain koro.
6. Application row action diye View/Review/Disbursement/Schedule/Repayment explain koro.
7. Review page-e workflow sequence show koro:
   Operations Review -> Asset/Risk Review -> Shariah Review -> Approved/Sanctioned -> Disbursed.
8. Review page-e 3-dot menu na, direct named buttons verify koro.
9. Sanction letter preview/print thakle show koro.
10. Explain koro je approved financing next contract generation-e jay.

### Financing Workflow Button Check

Review page-e status onujayee button verify:

1. `SUBMITTED` ba `DOC_CHECK` status hole `Asset/Risk Review` button active hobe.
2. Asset/Risk Review complete korar age `Shariah Review`, `Approve / Sanction`, `Disbursed` active hobe na.
3. Asset/Risk Review complete hole status `ASSET_VERIFIED` hobe.
4. `ASSET_VERIFIED` status-e `Shariah Review` and `Approve / Sanction` button active hobe.
5. Shariah Review click korle status `SHARIAH_REVIEW` hobe.
6. Approve / Sanction click korle status `APPROVED` hobe.
7. `APPROVED` status-e `Disbursed` button active hobe and disbursement page open hobe.
8. Disbursement page-e credited account, amount, date diye save korle schedule generate hobe.
9. Return/Reject korte hole remarks required. Remarks chara click korle warning show korbe.

### Contract Process Flow

1. Contract Dashboard diye document generation overview dekhao.
2. Template List diye contract templates dekhao.
3. New Template form open kore template configuration explain koro.
4. Contract List diye generated contract records dekhao.
5. Generate Contract form open kore approved financing/customer/source selection explain koro.
6. Contract print/preview thakle show koro.

### What To Verify

1. Sidebar-e `Financing Management` submenu order:
   Financing Dashboard -> Product List -> New Product -> Application List -> New Application.
2. Financing Dashboard open hoy and meaningful cards/charts data show kore.
3. Product List open hoy and product records ache.
4. New Product form open hoy.
5. Application List open hoy and application records ache.
6. Application row actions kaj kore.
7. New Application form open hoy and customer/product/branch/amount fields visible.
8. Review page-e workflow stage tracker properly show kore.
9. Review page-e 3-dot menu chara direct action buttons show kore.
10. Wrong sequence-e action click korle useless loading na hoye proper warning/info show kore.
11. Approved application theke disbursement page open hoy.
12. Sanction letter preview/print/download action thakle kaj kore.
13. Sidebar-e `Contracts Management` submenu order:
   Contract Dashboard -> Template List -> New Template -> Contract List -> Generate Contract.
14. Contract Dashboard open hoy.
15. Template List open hoy and data ache.
16. New Template form open hoy.
17. Contract List open hoy and records ache.
18. Generate Contract form open hoy and source/customer/template fields visible.
19. Contract preview/print copy kaj kore.
20. Dark mode-e financing and contract pages readable thake.

### How To Verify

1. Sidebar theke `Financing Management` expand koro.
2. Submenu order compare koro.
3. `Financing Dashboard` open kore overview dekho.
4. `Product List` open kore product table/action check koro.
5. `New Product` open kore form readiness check koro.
6. `Application List` open kore rows/actions check koro.
7. `New Application` open kore customer/product/amount fields check koro.
8. Application row theke `Review` open koro.
9. Review page-e stage tracker check koro:
   Operations Review -> Asset/Risk Review -> Shariah Review -> Approved/Sanctioned -> Disbursed.
10. Current status onujayee active button check koro.
11. `Asset/Risk Review` active hole asset value/note diye complete koro.
12. `Shariah Review` active hole click kore status check koro.
13. `Approve / Sanction` active hole click kore status `APPROVED` check koro.
14. `Disbursed` button active hole disbursement page open koro.
15. Credited account, amount, date diye disbursement test koro.
16. Application view page-e sanction preview/print thakle click kore dekho.
17. Sidebar theke `Contracts Management` expand koro.
18. `Contract Dashboard` open kore overview dekho.
19. `Template List` open kore templates dekho.
20. `New Template` open kore form readiness check koro.
21. `Contract List` open kore generated contracts dekho.
22. `Generate Contract` open kore source/template selection check koro.
23. Contract preview/print thakle click kore dekho.
24. Dark mode toggle kore readability check koro.

### Pass Criteria

Step 9 pass hobe jodi:

- Financing and Contract module-er listed submenu open hoy.
- Financing dashboard/product/application data show kore.
- Application form and row actions kaj kore.
- Review page-e direct stage buttons kaj kore and wrong-stage action block hoy.
- Approved/sanctioned application theke disbursement page open hoy.
- Sanction letter preview thakle kaj kore.
- Contract dashboard/template/list/generate form kaj kore.
- Contract preview/print copy thakle kaj kore.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 9 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 9

Step 9 pass korle bolo: `Step 9 done`

## Step 10: Shariah Review, Certificate, Zakat and Charity

Goal: Islamic banking compliance layer show kora. Ei step-e Shariah case review, compliance decision, certificate/fatwa generation, zakat and charity handling verify kora.

### Presentation Script

English:

```text
The Shariah and Zakat modules provide the compliance layer of the banking system.
Financing or operational cases can be reviewed by the Shariah board, decisions can be recorded, and formal certificates or review documents can be generated.
The Zakat and charity area tracks charitable obligations and late-payment charity amounts separately from normal profit.
```

Bangla:

```text
Shariah and Zakat module system-er compliance layer.
Financing ba operational case Shariah board review korte pare, decision record hoy, and certificate/review document generate kora jay.
Zakat and charity section normal profit theke alada vabe charitable obligation and late-payment charity amount track kore.
```

### Role Responsibility

```text
Investment/Operations user creates or forwards the case.
Shariah Board reviews the case and records decision.
Authorized approver finalizes the decision or certificate.
Accounts/Compliance user verifies zakat and charity records.
```

Bangla:

```text
Investment/Operations user case create/forward kore.
Shariah Board case review kore decision record kore.
Authorized approver decision/certificate finalize kore.
Accounts/Compliance user zakat and charity records verify kore.
```

### Process Flow

1. Sidebar theke `Shariah` module open koro.
2. `Shariah Dashboard` thakle first overview dekhao.
3. `Case List` / `Review Cases` open kore existing cases dekhao.
4. Financing reference thaka case open kore customer/reference/status dekhao.
5. Review form/page-e decision remarks, ruling/status and reviewer information dekhao.
6. Approve/Reject/Return type action thakle status onujayee click kore verify koro.
7. Certificate/Fatwa/Review document preview thakle open kore template dekhao.
8. Download/print thakle click kore verify koro.
9. Sidebar theke `Zakat` module open koro.
10. Zakat dashboard/list open kore calculation/collection/distribution records dekhao.
11. Charity/late fee record thakle financing schedule-er charity amount-er sathe relation explain koro.
12. Dark mode-e Shariah/Zakat page readable kina check koro.

### What To Verify

1. Shariah module submenu logical order-e ache.
2. Shariah dashboard/list load hoy.
3. Case table-e data show kore.
4. Case view/review action kaj kore.
5. Financing reference/customer/source information visible.
6. Review decision form-e required fields visible.
7. Action button click korle endless loading hoy na.
8. Certificate/fatwa/preview document blank hoy na.
9. Print/download action kaj kore.
10. Zakat module submenu open hoy.
11. Zakat records/calculation/distribution data show kore.
12. Charity amount normal profit-er sathe mix hoy na.
13. Dark mode readable thake.

### How To Verify

1. Login koro `shariah.board01 / Test@123` diye.
2. Sidebar theke `Shariah` expand koro.
3. Dashboard thakle open kore summary cards/status dekho.
4. Case list/review list open koro.
5. Ekta case open kore customer, reference module, status and remarks dekho.
6. Review action button click kore dekhো required validation thik ache kina.
7. Certificate/Fatwa/Preview button thakle click kore document dekho.
8. Print/download click kore file/document blank na kina verify koro.
9. Logout kore `admin01 / Test@123` diye login koro if Shariah role-e kichu permission missing hoy.
10. Sidebar theke `Zakat` expand koro.
11. Zakat list/calculation/distribution pages open koro.
12. Existing record na thakle at least form/list load and field readiness verify koro.
13. Dark mode toggle kore text/card/table readable kina dekho.

### Pass Criteria

Step 10 pass hobe jodi:

- Shariah dashboard/list/review pages open hoy.
- Case review page-e reference and decision data visible hoy.
- Review action endless loading chara response dey.
- Certificate/fatwa/preview blank na hoy.
- Print/download kaj kore.
- Zakat pages open hoy and records/form/calculation information visible hoy.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 10 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 10

Step 10 pass korle bolo: `Step 10 done`

## Step 11: Statement Generation and Export

Goal: Customer/account/transaction statement generate, preview, print and export workflow verify kora. Ei step-e dekhab e je banking records user-friendly statement format-e produce hoy and branch/customer/account/date range onujayee filter kora jay.

### Presentation Script

English:

```text
The Statement module converts operational records into customer-facing and internal statements.
Users can filter by account, customer, branch and date range, then preview, print or export the generated statement.
This is useful for customer service, audit support and management review.
```

Bangla:

```text
Statement module operational data-ke customer-facing and internal statement format-e convert kore.
Account, customer, branch and date range diye filter kore statement preview, print and export kora jay.
Eta customer service, audit support and management review-er jonno useful.
```

### Process Flow

1. Sidebar theke `Statement` module expand koro.
2. Account statement page open koro.
3. Filter section-e branch/account/customer/date range check koro.
4. `View` ba `Generate` click kore statement data load koro.
5. Opening balance, debit/credit movement, closing balance and transaction rows verify koro.
6. Preview button thakle open koro.
7. Print button click kore print preview blank na kina check koro.
8. Export/Download button click kore file generate hoy kina check koro.
9. Customer statement page thakle same flow repeat koro.
10. Financing/deposit/card statement page thakle relevant record select kore verify koro.
11. Dark mode-e statement table/filter/preview readable kina check koro.

### What To Verify

1. Statement submenu logical order-e ache.
2. Filter bar screen-er baire jay na.
3. Date range filter kaj kore.
4. Account/customer dropdown data show kore.
5. Statement generate korte endless loading hoy na.
6. Statement rows meaningful data show kore.
7. Opening/closing balance show kore.
8. Preview blank na hoy.
9. Print action kaj kore.
10. Export/download kaj kore.
11. Dark mode readable thake.

### How To Verify

1. Login koro `admin01 / Test@123` diye.
2. Sidebar theke `Statement` expand koro.
3. `Account Statement` open koro.
4. Date range dao: recent 30 days or current month.
5. Account select koro jekhane transaction ache.
6. `View` / `Generate` click koro.
7. Table-e debit/credit/description/date data show kore kina dekho.
8. Balance summary match kore kina dekho.
9. `Preview` click kore document blank na kina check koro.
10. `Print` click kore browser print dialog/preview ashe kina dekho.
11. `Export` or `Download` click kore file generate hoy kina dekho.
12. Same vabe customer/financing/deposit statement page thakle verify koro.
13. Dark mode toggle kore filter, table and buttons readable kina check koro.

### Pass Criteria

Step 11 pass hobe jodi:

- Statement pages open hoy.
- Filter diye data generate hoy.
- Statement table and balance summary meaningful hoy.
- Preview blank na hoy.
- Print and export/download kaj kore.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 11 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 11

Step 11 pass korle bolo: `Step 11 done`

## Step 12: Reporting and Regulatory Module

Goal: System-er operational, accounting, profitability and regulatory reporting capability show kora. Ei step-e report dashboard, filter bar, report generation, preview, print, export/download and export history verify kora.

### Presentation Script

English:

```text
The Reporting module consolidates operational, accounting and profitability data into management and regulatory reports.
Users can select a reporting period and branch scope, then view, preview, print or export reports such as operational report, trial balance, ledger profit and loss, profit distribution and regulatory summaries.
The export history keeps generated files available for audit and repeat download.
```

Bangla:

```text
Reporting module operational, accounting and profitability data-ke management and regulatory report-e convert kore.
Date range and branch scope select kore report view, preview, print and export kora jay.
Export history generated file preserve kore, jate audit and repeat download support hoy.
```

### Process Flow

1. Sidebar theke `Reporting` module expand koro.
2. `Report Dashboard` open kore summary, charts and quick links dekhao.
3. `Operational Report` open kore common report filter and report table verify koro.
4. `Trial Balance` open kore debit/credit balance report verify koro.
5. `Ledger Profit & Loss` open kore income, expense and net profit verify koro.
6. `Profit Distribution` open kore posted profit data verify koro.
7. `Management P&L` open kore management income/expense/profit summary verify koro.
8. `Expense Register` open kore expense entries and filter verify koro.
9. `Financing Portfolio`, `PAR`, `Shariah Audit`, `Branch`, `KPI`, `Growth`, `Loan Recovery`, `Monthly Closing` reports open kore data/readability verify koro.
10. Protita important report-e `View`, `Preview`, `Print`, `Export/Download` test koro.
11. Report page-er history table theke `Preview`, `Print`, `Download` test koro.
12. `Export History` global page open kore previous report file preview/download verify koro.
13. Dark mode-e filter, table, cards and document preview readable kina check koro.

### What To Verify

1. Reporting submenu logical order-e ache.
2. Report Dashboard open hoy and charts/cards meaningful data show kore.
3. Filter bar screen-er baire jay na.
4. Date range shortcut buttons kaj kore.
5. Branch filter thakle dropdown data show kore.
6. `View` click korle report data load hoy.
7. `Preview` blank na hoy.
8. `Print` browser print preview/dialog open kore.
9. `Export/Download` file generate kore.
10. History table action buttons kaj kore.
11. Missing/generated file thakleo preview/download fail kore na.
12. Trial Balance debit and credit visually balanced/understandable.
13. Ledger Profit & Loss-e income, expense, net result/profit meaningful.
14. Profit Distribution-e posted profit data show kore.
15. Monthly Closing status/report data readable.
16. Dark mode readable thake.

### How To Verify

1. Login koro `admin01 / Test@123` diye.
2. Sidebar theke `Reporting` expand koro.
3. `Report Dashboard` open kore quick links and chart cards dekho.
4. `Operational Report` open koro.
5. Date range current month or recent 30 days select koro.
6. `View` click kore report table/data dekho.
7. `Preview`, `Print`, `Export` test koro.
8. History table-er latest row theke preview/print/download test koro.
9. Same vabe `Trial Balance` and `Ledger Profit & Loss` check koro.
10. `Profit Distribution` and `Management P&L` check kore profit data visible kina dekho.
11. `Expense Register` open kore entries and filter check koro.
12. Remaining reports quick pass koro: Financing Portfolio, PAR, Shariah Audit, Branch, KPI, Growth, Loan Recovery, Monthly Closing.
13. `Export History` page open kore global history action test koro.
14. Dark mode toggle kore filter/table/card/readability check koro.

### Pass Criteria

Step 12 pass hobe jodi:

- Reporting module-er listed pages open hoy.
- Report filter, view, preview, print, export/download kaj kore.
- History preview/print/download kaj kore.
- Trial Balance and Ledger Profit & Loss data meaningful hoy.
- Report Dashboard quick links kaj kore.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 12 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 12

Step 12 pass korle bolo: `Step 12 done`

## Step 13: Workflow, Verification, Audit and Notification Controls

Goal: System-er control layer show kora. Ei step-e approval workflow, maker-checker verification, audit trail, security/session control and notification delivery verify kora.

### Presentation Script

English:

```text
The Workflow, Verification, Audit and Notification areas provide the control layer of the banking system.
They help the bank track maker-checker actions, review pending approvals, preserve audit evidence and notify users about important operational events.
```

Bangla:

```text
Workflow, Verification, Audit and Notification area system-er control layer.
Ei part diye maker-checker action, pending approval, audit evidence and user notification track kora jay.
```

### Process Flow

1. Sidebar theke `Workflow` module open koro.
2. Pending/approval/review list thakle open kore workflow items dekhao.
3. Ekta record-er status, module/source, requester and action date verify koro.
4. Approve/Reject/Return action thakle validation and response check koro.
5. Sidebar theke `Verification` module open koro.
6. Verification queue/list open kore maker-checker review flow dekhao.
7. Sidebar theke `Security` module open kore session/login/security log view dekho.
8. Sidebar theke `Notification` module open kore notification list/template/auto-mail status dekho.
9. Audit/log page thakle recent activity verify koro.
10. Dark mode-e table, filter and action popup readable kina check koro.

### What To Verify

1. Workflow submenu logical order-e ache.
2. Workflow dashboard/list open hoy.
3. Pending/approved/rejected status readable.
4. Action buttons endless loading kore na.
5. Reject/Return remarks required validation thik ache.
6. Verification list open hoy and data/status show kore.
7. Audit/security log page open hoy.
8. Recent login/action history visible thake.
9. Notification page open hoy.
10. Notification list/template/auto-mail record readable.
11. Email/OTP notification template visually professional.
12. Dark mode readable thake.

### How To Verify

1. Login koro `admin01 / Test@123` diye.
2. Sidebar theke `Workflow` expand koro.
3. Workflow dashboard/list/approval queue pages open koro.
4. First row action/view open kore status and source module dekho.
5. Approve/Reject/Return thakle demo-safe hole only validation check koro; risky hole save korbe na.
6. Sidebar theke `Verification` expand kore queue/list open koro.
7. Pending verification record thakle view/review button click kore dekho.
8. Sidebar theke `Security` expand kore login/session/audit related page open koro.
9. Recent activity, username, timestamp, status visible kina dekho.
10. Sidebar theke `Notification` expand kore notification list/template/mail log open koro.
11. Recent OTP/report/statement email log thakle status dekho.
12. Dark mode toggle kore sob page readable kina check koro.

### Pass Criteria

Step 13 pass hobe jodi:

- Workflow, Verification, Security/Audit and Notification pages open hoy.
- Pending/status/action data visible hoy.
- Action buttons endless loading kore na.
- Remarks validation thik thake.
- Audit/security/notification records readable hoy.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 13 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Next Step After Step 13

Step 13 pass korle bolo: `Step 13 done`

## Step 14: Admin Controls - User, Role and Lookup Management

Goal: System administration layer show kora. Ei step-e user management, role/permission control, branch-wise user assignment, password/security actions and lookup configuration verify kora.

### Presentation Script

English:

```text
The Admin Controls area manages users, roles, permissions and reusable lookup values.
This allows the bank to control who can access each module, assign users to operational roles, manage security actions and keep dropdown/reference values consistent across the system.
```

Bangla:

```text
Admin Controls area diye user, role, permission and lookup value manage kora hoy.
Eta bank-ke module-wise access control, operational role assignment, security action and reusable dropdown/reference data maintain korte help kore.
```

### Process Flow

1. Sidebar theke `User` module open koro.
2. User dashboard/list open kore existing users dekhao.
3. List view and grid view thakle duita mode verify koro.
4. User table action menu theke View/Edit/Assign Role/Reset Password/Lock/Archive action available kina check koro.
5. New User form open kore username, role, branch, profile image and status fields dekhao.
6. Sidebar theke `Role` module open koro.
7. Role list open kore role records and permission summary dekhao.
8. Role create/edit form open kore permission assignment structure dekhao.
9. Sidebar theke `Lookup` module open koro.
10. Lookup type and lookup value list open kore reusable dropdown values explain koro.
11. New lookup value form open kore type, code, label, Bangla label and active status fields verify koro.
12. Dark mode-e user/card/table/form/action menu readable kina check koro.

### What To Verify

1. User module submenu logical order-e ache.
2. User list open hoy and data show kore.
3. Table action menu single 3-dot mode-e properly open hoy.
4. Grid view thakle 3-dot na, direct proper buttons show kore.
5. Last row action popup screen-er baire jay na.
6. New/Edit user form open hoy and blink/loading issue hoy na.
7. Profile image preview box proper size-e thake.
8. Reset password/lock/archive action dangerous hole save na kore modal/validation only verify koro.
9. Role list open hoy.
10. Role permission structure readable.
11. Lookup type/list open hoy.
12. Lookup value form clean and readable.
13. Dark mode readable thake.

### How To Verify

1. Login koro `admin01 / Test@123` diye.
2. Sidebar theke `User` expand koro.
3. `User List` open kore table data dekho.
4. First row action menu open kore available actions dekho.
5. Last row action menu open kore popup position check koro.
6. Grid view thakle switch kore card buttons proper kina dekho.
7. `Add User` / `New User` click kore form open koro. Save na korleo cholbe.
8. Image upload preview area check koro.
9. Sidebar theke `Role` expand kore role list open koro.
10. Role view/edit action open kore permission layout dekho.
11. Sidebar theke `Lookup` expand kore lookup type/value pages open koro.
12. New lookup value form open kore fields/readability check koro.
13. Dark mode toggle kore user, role and lookup pages readable kina dekho.

### Pass Criteria

Step 14 pass hobe jodi:

- User, Role and Lookup pages open hoy.
- User table/grid action design and popup position thik thake.
- User form/profile image preview clean thake.
- Role permission view/edit readable hoy.
- Lookup type/value pages and form readable hoy.
- Dark mode readable thake.

### If Issue Found

Ei format-e note koro:

```text
Step 14 issue:
Page:
Problem:
Screenshot:
Expected:
```

## Final Showcase Check

Step 14 pass korle bolo: `Step 14 done`

## Topbar Alert and Message Icon Check

Goal: Showcase-er age topbar-er notification bell and message icon live/status behavior verify kora.

### Alert Bell Icon

How it works:

```text
Bell icon latest notification delivery logs and live alert events show kore.
Failed, retry queued or pending notification thakle bell icon-e red dot show hobe.
Bell dropdown open korle current unread indicator clear hoy.
Alert item click korle related notification delivery log view page open hoy.
```

Verify:

1. Login koro `admin01 / Test@123` diye.
2. Topbar-er bell icon click koro.
3. Recent alert thakle dropdown-e event name, status, channel and recipient summary dekho.
4. Ekta alert item click koro.
5. `/notifications/logs/{id}` page open hoy kina verify koro.
6. `Notification -> Delivery Logs` page-e same log record match kore kina check koro.
7. OTP/report/statement mail generate korle new log bell dropdown-e ashe kina 30 seconds wait kore dekho.

Pass criteria:

- Bell dropdown open hoy.
- Alert item thakle related log page-e jay.
- No alert thakle `No new alerts` clean message dekhay.
- Failed/retry/pending log thakle red dot visible hoy.

### Message Icon

How it works:

```text
Message icon operational message-style live updates show kore.
Report, verification, message or chat type live event ashle message dropdown-e item add hoy.
Message icon-er blue dot unread message indicator hisebe kaj kore.
Message dropdown open korle unread indicator clear hoy.
Message item click korle related page thakle oi page-e navigate kore.
```

Verify:

1. Login koro `admin01 / Test@123` diye.
2. Topbar-er chat/message icon click koro.
3. Message thakle title, short text and related page hint dekho.
4. Item click korle related page-e jay kina check koro.
5. Message na thakle `No live messages yet` clean message dekhay kina dekho.
6. Statement/report/verification action-er por message icon-e new update ashe kina observe koro.

Pass criteria:

- Message dropdown open hoy.
- Live message thakle readable item show kore.
- Item route thakle click kore related page open hoy.
- No message thakle empty state clean thake.
- Blue unread dot dropdown open korle clear hoy.

## Topbar Language Switch Check

Goal: Showcase-safe language switch verify kora. Ei phase-e topbar, sidebar, footer, breadcrumb/page title, filter bar and common empty/action labels Bangla/English switch korbe.

How it works:

```text
Topbar language icon theke English or Bangla select kora jay.
Selected language localStorage-e save hoy, tai refresh korleo same language thake.
Bangla mode-e layout shell, sidebar menu, common page title/header/filter/empty state translate hoy.
Full backend data, generated report PDF, email body and every form validation message ei phase-e translate kora hoyni.
```

Verify:

1. Login koro `admin01 / Test@123` diye.
2. Topbar-er language/translate icon click koro.
3. `বাংলা` select koro.
4. Sidebar menu labels Bangla hoy kina dekho.
5. Topbar dropdown labels, message/notification empty text Bangla hoy kina dekho.
6. Konো page open kore breadcrumb/page title/filter title/button Bangla hoy kina dekho.
7. Browser refresh koro.
8. Refresh-er por language Bangla-i thake kina verify koro.
9. Language icon theke `English` select kore abar English-e phire ashe kina dekho.

Pass criteria:

- Language dropdown kaj kore.
- Bangla select korle sidebar/topbar/common UI text change hoy.
- Refresh korleo selected language thake.
- English select korle English-e back kore.
- Data/table values/backend messages unchanged thakleo acceptable for Phase 1.

## Full Project Test Step 6: Reports, Statements, Documents

Status: Completed by API verification on 13 Jun 2026.

Verified successfully:

1. Reporting dashboard and all report data endpoints:
   Operational, Profit Distribution, Management P&L, Trial Balance, Ledger Profit & Loss, Financing Portfolio, PAR, Shariah Audit, Branch, KPI, Growth, Loan Recovery and Monthly Closing.
2. Report export and export history:
   PRINT/HTML, PDF and EXCEL generation, export-history preview and download.
3. Statements:
   Customer statement preview/download, Branch statement preview/download, Customer statement export PDF/EXCEL and Branch statement export PDF/EXCEL.
4. Transaction and banking documents:
   Transaction voucher preview/download, Account opening document preview/download, Profit posting advice preview/download and Financing sanction letter preview/download.
5. Certificates and formal documents:
   Zakat profile sheet, Zakat payout receipt, Deposit scheme enrollment certificate, Contract print copy, Vault report, ATM terminal profile, Cash bin profile, ATM replenishment report and ATM reconciliation report.
6. Upload and document view:
   `/api/files/upload-document` accepts valid PDF, stores it with unique id plus original filename and `/api/files/documents/{file}` returns inline preview with original display filename.
7. KYC document record:
   Uploaded document reference saved through KYC document upload API and listed under the related KYC profile.

Manual video-check points:

1. Open `Reports -> Export History`, click Preview, Print and Download on the latest rows.
2. Open `Statement -> Customer Requests` and `Statement -> Branch Requests`, test Preview and Download.
3. Open a transaction row and test voucher Preview/Download.
4. Open deposit scheme enrollment, zakat payout/profile and financing application pages to visually confirm certificate/receipt/letter layout.
5. Toggle dark mode once and confirm tables, cards and filter bars remain readable.

Result:

No failed API response was found in Step 6 after verification. Backend was restarted once because it was not listening on port 8080, then health check returned UP.
