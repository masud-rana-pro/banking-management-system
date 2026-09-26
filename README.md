# Al-Barakah Shariah Banking Management System

Al-Barakah SBMS is a web-based banking operations system built with Angular, Spring Boot, and MySQL. It covers customer onboarding, KYC, account operations, Islamic financing, profit processing, statements, reports, and permission-controlled back-office workflows.

## Implemented Workflows

### Access and administration

- Username and password login followed by email OTP verification
- JWT-based bearer sessions, logout, password change, and online-user tracking
- User, role, permission, branch assignment, lock/unlock, and password reset
- Route guards in the frontend and permission checks in the backend
- Notifications, verification challenges, audit records, and security cases

### Customer and account operations

- Customer registration, profile maintenance, and customer status management
- KYC creation, document upload, review, approval, rejection, return, and history
- Account types, account-opening requests, review, approval, and account status actions
- Cash deposit, cash withdrawal, fund transfer, cheque clearing, standing instructions, and transaction reversal
- Transaction voucher preview and download
- Card, ATM terminal, cash-bin, replenishment, and reconciliation management
- Deposit schemes, enrollment, installment schedules, maturity calculation, profit distribution, and certificate generation

### Islamic financing and profit

- Financing products and applications
- Application submission, asset verification, Shariah review, approval, rejection, and return
- Disbursement to an active account, installment schedule generation, and repayment collection
- Financing sanction-letter preview and download
- Profit ratios, schedules, posting runs, and posting advice
- Shariah review, contracts, zakat profiles, charity funds, beneficiaries, and payouts

### Statements, reports, and monitoring

- Customer and branch statement requests, preview, download, and export history
- Operational, branch, KPI, growth, financing portfolio, PAR, loan recovery, and Shariah audit reports
- Trial balance, ledger profit and loss, management profit and loss, profit distribution, and monthly closing
- PDF document templates and Excel export support
- General dashboard for account, transaction, financing, profit, branch, and control-queue summaries

## Project Structure

```text
banking-management-system/
|-- stable-sbms-frontend/
|   `-- src/app/
|       |-- core/          authentication, guards, interceptors, and shared services
|       |-- features/      lazy-loaded business modules
|       `-- shared/        reusable components, models, and utilities
|-- stable-sbms-backend/
|   `-- src/main/
|       |-- java/com/sbms/ domain packages
|       `-- resources/
|           |-- templates/ printable HTML templates
|           `-- application.properties
|-- .gitignore
`-- README.md
```

Backend domain packages follow a consistent `controller`, `dto`, `entity`, `enums`, `repository`, and `service` structure. The Angular application is divided into lazy-loaded feature modules such as customer, KYC, accounts, transactions, financing, profit, statements, and reports.

## Technology

### Frontend

- Angular 13 and Angular CLI 13
- TypeScript 4.4, RxJS 7.4, and SCSS
- Bootstrap 5, Bootstrap Icons, and Font Awesome
- SweetAlert2, jsPDF, jsPDF AutoTable, SheetJS, and FileSaver

### Backend

- Java 17 and Spring Boot 4.0.5
- Spring Web, Data JPA, Data JDBC, Validation, Mail, AOP, and Actuator
- MySQL Connector/J 8.0.33
- Thymeleaf and OpenHTMLToPDF for HTML/PDF documents
- Apache POI for Excel exports
- BCrypt password hashing, JJWT 0.9.1, and JWT-based bearer authorization

## Local Configuration

Create a MySQL database named `sbms`:

```sql
CREATE DATABASE sbms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Copy the local configuration template:

```powershell
Copy-Item stable-sbms-backend\application-local.properties.example `
  stable-sbms-backend\application-local.properties
```

Set the local database credentials and SMTP app password in `application-local.properties`. This file is excluded from Git. The same settings can be supplied with these environment variables:

- `SBMS_DB_URL`
- `SBMS_DB_USERNAME`
- `SBMS_DB_PASSWORD`
- `SBMS_MAIL_USERNAME`
- `SBMS_MAIL_PASSWORD`
- `SBMS_MAIL_FROM`
- `SBMS_EMAIL_ENABLED`

Hibernate is configured with `ddl-auto=update`. The public repository does not include operational users, customer records, uploaded documents, or database backups.

## Run the Application

Start the backend:

```powershell
cd stable-sbms-backend
.\mvnw.cmd spring-boot:run
```

The API is available at `http://localhost:8080`.

Start the frontend in another terminal:

```powershell
cd stable-sbms-frontend
npm install
npm start
```

The frontend is available at `http://localhost:4200` and uses `http://localhost:8080/api` in development.

## Build

```powershell
cd stable-sbms-backend
.\mvnw.cmd -DskipTests package
```

```powershell
cd stable-sbms-frontend
npm run build
```
