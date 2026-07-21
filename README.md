# Al-Barakah Shariah Banking Management System (SBMS)

A secure, integrated and Shariah-compliant banking management platform for branch operations, customer onboarding, account lifecycle, Islamic financing, profit management, reporting and audit-controlled back-office workflows.

SBMS is built as a full-stack enterprise application with an Angular frontend, Spring Boot backend and MySQL database. It is designed for operational showcase, training and banking workflow demonstration with role-based access, real email OTP, document handling, voucher/statement generation and management dashboards.

## Core Capabilities

- Secure login with username/password, email OTP verification, JWT-based session handling and role-based access control.
- Branch, vault, teller and administrative controls for banking operations.
- Customer onboarding with profile management, KYC verification and document upload/preview.
- Account opening workflow, account approval, account status tracking and account statement generation.
- Deposit, withdraw and transfer operations with voucher preview/download support.
- Card and deposit scheme management.
- Islamic financing workflow from application to operations review, asset/risk review, Shariah review, sanction, disbursement, repayment and profit posting.
- Shariah, zakat, compliance, security case and workflow monitoring modules.
- Report and statement modules with print, preview, download, export and history-oriented workflows.
- Executive dashboard with banking KPIs, branch performance, profit/loss, portfolio, risk and operational activity overview.
- Responsive UI with light/dark theme support and reusable shared table/action/filter/form components.

## Project Structure

```text
banking-management-system/
|-- stable-sbms-frontend/     # Angular 13 client application
|-- stable-sbms-backend/      # Spring Boot Java backend API
|-- db/                       # MySQL backup, seed and update SQL scripts
|-- docs/                     # Project notes, showcase and supporting documentation
`-- README.md                 # Main project guide
```

## Technology Stack

### Frontend

- Angular 13
- TypeScript
- SCSS
- Bootstrap 5
- Bootstrap Icons and Font Awesome
- RxJS
- SweetAlert2
- jsPDF, jsPDF AutoTable and XLSX for document/export features

### Backend

- Java 17
- Spring Boot
- Spring Web, Spring Data JPA/JDBC and Spring Security Crypto
- MySQL 8
- JWT-based authentication support
- Java Mail / Gmail SMTP for OTP and notification email
- WebSocket support
- Thymeleaf and OpenHTMLToPDF for printable documents
- Apache POI for Excel export
- Flyway, Actuator, Validation and Lombok

## Main Business Modules

- General Dashboard
- Authentication and OTP Verification
- User, Role and Permission Management
- Branch Management
- Customer Management
- KYC and Document Management
- Account Management
- Transaction Management
- Card Management
- Deposit Scheme Management
- Islamic Financing
- Profit Management
- Shariah and Zakat Management
- Statements and Reports
- Security, Notification and Workflow Control

## Prerequisites

Install the following before running the project locally:

- Java 17
- Maven or the included Maven Wrapper
- Node.js and npm compatible with Angular 13
- MySQL 8
- Git

## Database Setup

1. Create a MySQL database named `sbms`.
2. Import the latest suitable SQL backup or seed script from the `db/` directory.
3. The commonly used database files are:
   - `sbms_db_backup_24.06.26.sql`
   - `sbms-db-04-06-26.sql`
   - `sbms-db-gap-fill-05-06-26.sql`
   - `sbms-dashboard-profit-balance-11-06-26.sql`

Example:

```powershell
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS sbms;"
mysql -uroot -p sbms < db\sbms_db_backup_24.06.26.sql
```

## Backend Setup

```powershell
cd stable-sbms-backend
.\mvnw.cmd spring-boot:run
```

Default backend URL:

```text
http://localhost:8080
```

Backend configuration is located at:

```text
stable-sbms-backend/src/main/resources/application.properties
```

Update the following values according to your local machine:

- MySQL URL, username and password
- Upload directory
- Gmail SMTP username and app password
- Application base URL and support contact information

Do not publish real email app passwords or production credentials in public repositories.

## Frontend Setup

```powershell
cd stable-sbms-frontend
npm install
npm start
```

Default frontend URL:

```text
http://localhost:4200
```

## Demo Login Users

Use these demo accounts for local showcase and testing:

| Role / Purpose | Username | Password |
| --- | --- | --- |
| System Admin | `admin01` | `Test@123` |
| Branch Manager | `branch.manager01` | `Test@123` |
| Operations Officer | `ops.officer01` | `Test@123` |
| Investment Officer | `investment.officer02` | `Test@123` |
| Shariah Board | `shariah.board01` | `Test@123` |
| Teller | `teller.080951` | `Test@123` |
| Customer | `customer.1` | `Test@123` |

OTP is delivered through the configured SMTP email provider.

## Recommended Showcase Flow

1. Login as `admin01` and verify OTP.
2. Open the General Dashboard and explain KPIs, branch scope, profit, risk and operational analytics.
3. Create or view a customer profile.
4. Upload and verify KYC documents.
5. Create an account opening request and approve it.
6. Perform deposit, withdraw and transfer operations.
7. Preview/download a transaction voucher and customer statement.
8. Submit an Islamic financing application.
9. Complete operations, asset/risk and Shariah review.
10. Sanction, disburse and show repayment/profit posting.
11. Open reports such as Trial Balance, Ledger Profit & Loss, Management P&L and Operational Report.
12. Demonstrate alerts, messages, theme mode, language setting and audit-oriented controls.

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

Before recording or deployment, verify:

- Login and OTP email flow works.
- Dashboard loads without blank charts or console errors.
- Core action menus, popup forms and upload/preview controls work.
- Transaction voucher, statement, report and certificate preview/download work.
- Light and dark mode remain readable.
- No generated `node_modules`, `target`, `dist`, temporary reports or local secret files are committed.

## Notes

This project is intended for educational, training and showcase use. Before using it in a production banking environment, review security, audit, compliance, data privacy, encryption, deployment and operational controls according to the target institution's policy and regulatory requirements.