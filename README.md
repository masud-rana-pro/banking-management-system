# Al-Barakah Shariah Banking Management System

Al-Barakah SBMS is a full-stack banking operations platform that models secure customer onboarding, account servicing, Islamic financing, transaction processing, profit management, reporting, and controlled back-office workflows.

The project combines an Angular client, a Spring Boot API, and MySQL persistence in a modular architecture suitable for portfolio review, workflow demonstration, and further engineering development.

## Highlights

- Password and email OTP authentication with JWT session handling
- Role-based access control for administrators, branch staff, reviewers, and customers
- Customer onboarding, KYC review, document handling, and account approval
- Deposit, withdrawal, transfer, voucher, and statement workflows
- Card and deposit-scheme management
- End-to-end Islamic financing lifecycle: application, review, Shariah approval, sanction, disbursement, repayment, and profit posting
- Branch operations, teller, vault, compliance, notification, audit, and security controls
- Management dashboard with banking KPIs, portfolio analysis, profit and risk indicators
- Printable banking documents and Excel/PDF report exports
- Responsive interface with light and dark themes

## Architecture

```text
Angular SPA
    |
    | REST / JWT
    v
Spring Boot API
    |-- controllers and request validation
    |-- domain services and workflow rules
    |-- repositories and persistence models
    |-- security, OTP, audit, reporting, and document generation
    v
MySQL
```

## Repository Structure

```text
banking-management-system/
|-- stable-sbms-frontend/   Angular application
|-- stable-sbms-backend/    Spring Boot application and document templates
|-- .gitignore              Public-repository hygiene rules
`-- README.md               Project overview and setup guide
```

Runtime uploads, generated statements/reports, database dumps, test exports, credentials, and personal data are intentionally excluded from version control.

## Technology Stack

### Frontend

- Angular 13, TypeScript, RxJS, and SCSS
- Bootstrap 5, Bootstrap Icons, and Font Awesome
- SweetAlert2
- jsPDF, jsPDF AutoTable, and XLSX

### Backend

- Java 17 and Spring Boot
- Spring Web, Data JPA/JDBC, Validation, and Security Crypto
- JWT authentication and Java Mail
- MySQL 8
- Thymeleaf and OpenHTMLToPDF
- Apache POI, WebSocket, Actuator, and Lombok

## Main Modules

- Authentication, OTP, users, roles, and permissions
- Branch, staff, teller, vault, and operational controls
- Customer, KYC, documents, and account lifecycle
- Deposits, withdrawals, transfers, cards, and deposit schemes
- Islamic financing and profit management
- Shariah, zakat, compliance, workflow, and security cases
- Statements, vouchers, reports, exports, and executive dashboard

## Local Setup

### Prerequisites

- Java 17
- Node.js and npm compatible with Angular 13
- MySQL 8
- Git

### 1. Create the Database

Create an empty local database. Hibernate creates or updates the schema during development.

```sql
CREATE DATABASE sbms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Database dumps and operational data are not published because they may contain customer or environment-specific information.

### 2. Configure the Backend

The committed configuration reads sensitive values from environment variables. You may also copy `stable-sbms-backend/application-local.properties.example` to `stable-sbms-backend/application-local.properties` and fill in local values. The local file is ignored by Git.

Required settings include:

- `SBMS_DB_URL`
- `SBMS_DB_USERNAME`
- `SBMS_DB_PASSWORD`
- `SBMS_MAIL_USERNAME`
- `SBMS_MAIL_PASSWORD`
- `SBMS_MAIL_FROM`

Never commit real database passwords, SMTP app passwords, private keys, OTP values, or customer files.

### 3. Run the Backend

```powershell
cd stable-sbms-backend
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080` by default.

### 4. Run the Frontend

```powershell
cd stable-sbms-frontend
npm install
npm start
```

The application runs at `http://localhost:4200` by default.

## Build and Verification

Backend package:

```powershell
cd stable-sbms-backend
.\mvnw.cmd -DskipTests package
```

Frontend production build:

```powershell
cd stable-sbms-frontend
npm run build
```

Before deployment, configure a managed secret store, disable development schema updates, restrict CORS, enforce HTTPS, review access policies, and run security, audit, privacy, and regulatory checks appropriate to the target institution.

## Security and Data Policy

This public repository contains application source code and reusable templates only. The following stay local and are ignored:

- Environment credentials and email app passwords
- Customer photos, KYC documents, and uploaded statements
- Generated reports, vouchers, and account statements
- Database backups and operational seed data
- Test evidence, recordings, and presentation working files

If a credential was ever committed, rotate it immediately; removing it from the latest revision does not invalidate copies in earlier Git history.

## Scope

This software is an educational and portfolio implementation of banking workflows. Production banking use requires independent security review, regulatory validation, infrastructure hardening, observability, disaster recovery, and institution-specific controls.
