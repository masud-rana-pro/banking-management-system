# SBMS Backend

Spring Boot backend API for the Al-Barakah Shariah Banking Management System.

The backend handles authentication, OTP delivery, role-based authorization, customer and KYC workflows, account lifecycle, transactions, Islamic financing, profit posting, reports, statements, document generation, notification events and operational dashboard data.

## Stack

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA and JDBC
- MySQL 8
- Java Mail / Gmail SMTP
- WebSocket
- Thymeleaf
- OpenHTMLToPDF
- Apache POI
- Validation
- Flyway
- Actuator
- Lombok

## Package Structure

Main source path:

```text
src/main/java/com/sbms/
```

Important packages:

```text
com.sbms/
|-- account/
|-- accounting/
|-- atm/
|-- auth/
|-- branch/
|-- card/
|-- common/
|-- config/
|-- customer/
|-- dashboard/
|-- depositscheme/
|-- financing/
|-- integration/
|-- kyc/
|-- lookup/
|-- notification/
|-- profit/
|-- report/
|-- role/
|-- security/
|-- shariah/
|-- statement/
|-- transaction/
|-- user/
|-- verification/
|-- workflow/
`-- zakat/
```

Most business modules follow a layered style:

```text
controller -> service/interface -> service/implementation -> repository -> entity/dto
```

## Configuration

Main configuration file:

```text
src/main/resources/application.properties
```

Key areas:

- MySQL datasource
- Hibernate/JPA settings
- Upload directory
- Multipart upload limits
- SMTP mail settings for OTP and notification delivery
- Application base URL and support contact settings

Never commit production credentials or real app passwords to a public repository. Use environment-specific configuration or secret management for production deployment.

## Database

Default local database name:

```text
sbms
```

The root `db/` directory contains SQL backup and seed/update scripts for local setup and showcase data.

## Run Locally

```powershell
.\mvnw.cmd spring-boot:run
```

Default API URL:

```text
http://localhost:8080
```

## Build

```powershell
.\mvnw.cmd -DskipTests package
```

The packaged output is generated under `target/`.

## Important Runtime Directories

```text
src/main/resources/uploads/          # Uploaded customer/KYC/user/document files
src/main/resources/templates/        # HTML templates for email, reports and documents
src/main/resources/db/               # Migration or database support resources
```

Generated reports, statements or temporary exports should not be committed unless they are intentional documentation samples.

## Main Backend Responsibilities

- Authenticate users and validate OTP.
- Serve RBAC-driven module and action access.
- Manage customers, KYC records and document upload/preview.
- Control account opening, approval and account status lifecycle.
- Process deposit, withdraw and transfer transactions.
- Generate vouchers, statements, reports and certificates.
- Manage Islamic financing workflow and profit posting.
- Provide dashboard summary metrics and analytics data.
- Send transactional emails through SMTP.
- Emit or support real-time notification workflows where enabled.

## Verification Checklist

After backend changes, verify:

- Application starts without datasource or bean errors.
- Login and OTP email delivery works.
- File upload endpoints accept multipart requests.
- Preview/download APIs return valid files, not blank responses.
- Customer, KYC, account, transaction and financing status transitions work end-to-end.
- Report and statement APIs return meaningful data for the selected date/branch scope.
- Profit and loss data remains positive and consistent for showcase scenarios.
- Console logs do not show unexpected stack traces during normal workflows.

## Development Notes

Keep business rules in service classes, persistence in repositories and request/response mapping in DTOs. When adding a workflow step, update both validation and status transition logic so frontend actions do not get stuck in loading or disabled states.