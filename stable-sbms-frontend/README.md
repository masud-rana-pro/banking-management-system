# SBMS Frontend

Angular client application for the Al-Barakah Shariah Banking Management System.

The frontend provides the operational user interface for authentication, dashboard analytics, customer onboarding, KYC, account management, transactions, Islamic financing, reports, statements, certificates, notifications and administrative control.

## Stack

- Angular 13
- TypeScript
- SCSS
- Bootstrap 5
- Bootstrap Icons
- Font Awesome
- RxJS
- SweetAlert2
- jsPDF / jsPDF AutoTable
- XLSX

## Application Structure

```text
src/app/
|-- core/          # Layout, guards, interceptors, shell, header, sidebar and shared services
|-- shared/        # Reusable UI components, tables, filters, forms, dialogs and utilities
`-- features/      # Business feature modules and pages
```

Important feature areas:

```text
features/
|-- auth/
|-- general-dashboard/
|-- admin/
|-- branch/
|-- customer/
|-- kyc/
|-- accounts/
|-- transactions/
|-- cards/
|-- deposit-schemes/
|-- financing/
|-- profit/
|-- reports/
|-- statement/
|-- shariah/
|-- zakat/
|-- notifications/
|-- security/
|-- verification/
`-- workflow/
```

## Local Development

Install dependencies:

```powershell
npm install
```

Run development server:

```powershell
npm start
```

Open:

```text
http://localhost:4200
```

## Build

```powershell
npm run build
```

The build output is generated under `dist/`.

## Configuration

Check the environment files under:

```text
src/environments/
```

Make sure the API base URL points to the running backend service, usually:

```text
http://localhost:8080
```

## UI Standards Used in This Project

- Shared page header and action button patterns.
- Reusable filter bars for reports and operational lists.
- Shared table action menu with icon mode and three-dot menu mode.
- Popup form pattern for new/edit/action workflows.
- Responsive dashboard cards and charts.
- Light and dark theme-aware SCSS.
- Banking-style document, statement and certificate preview layouts.

## Common Verification Checklist

After frontend changes, verify:

- Login, OTP and logout.
- Topbar buttons: theme, language, notification, message and profile menu.
- Dashboard filters and chart responsiveness.
- List view and grid view actions.
- New/edit/view/delete/archive style actions where applicable.
- File upload and preview controls.
- Voucher, report, statement and certificate preview/download.
- Mobile and small-width responsive page headers.
- Browser console remains free of runtime errors.

## Useful Commands

```powershell
npm start
npm run build
npm test
```

## Development Notes

Keep feature-specific logic inside the relevant feature module, and put reusable UI behavior inside `shared/` or `core/` only when it is used across multiple modules. Avoid committing `node_modules`, `dist`, temporary generated files or local environment secrets.