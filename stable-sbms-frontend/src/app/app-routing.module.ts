import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PermissionGuard } from './core/guards/permission.guard';
import { AppShellComponent } from './core/layout/app-shell/app-shell.component';
import { LoginComponent } from './features/auth/login/login.component';
import { ChangePasswordComponent } from './features/auth/change-password/change-password.component';
import { SelfProfileComponent } from './features/auth/self-profile/self-profile.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'auth/login',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'change-password', component: ChangePasswordComponent },
      { path: '', redirectTo: 'login', pathMatch: 'full' }
    ]
  },

  {
    path: '',
    component: AppShellComponent,
    children: [
      {
        path: 'account/profile',
        canActivate: [PermissionGuard],
        component: SelfProfileComponent
      },
      {
        path: 'account/change-password',
        canActivate: [PermissionGuard],
        component: ChangePasswordComponent
      },
      {
        path: 'dashboard',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'ADMIN_DASHBOARD_ACCESS' },
        loadChildren: () =>
          import('./features/general-dashboard/general-dashboard.module').then(m => m.GeneralDashboardModule)
      },
      {
        path: 'admin',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        loadChildren: () =>
          import('./features/admin/admin.module').then(m => m.AdminModule)
      },
      {
        path: 'lookups',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'LOOKUP_CONFIG_ACCESS' },
        loadChildren: () =>
          import('./features/lookups/lookup.module').then(m => m.LookupModule)
      },
      {
        path: 'branches',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'BRANCH_MANAGEMENT_ACCESS' },
        loadChildren: () =>
          import('./features/branch/branch.module').then(m => m.BranchModule)
      },
      {
        path: 'atm',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'ATM_CDM_ACCESS' },
        loadChildren: () =>
          import('./features/atm/atm.module').then(m => m.AtmModule)
      },
      {
        path: 'customers',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'CUSTOMER_MANAGEMENT_ACCESS' },
        loadChildren: () =>
          import('./features/customer/customer.module').then(m => m.CustomerModule)
      },
      {
        path: 'kyc',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'KYC_MANAGEMENT_ACCESS' },
        loadChildren: () =>
          import('./features/kyc/kyc.module').then(m => m.KycModule)
      },
      {
        path: 'accounts',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS' },
        loadChildren: () =>
          import('./features/accounts/accounts.module').then(m => m.AccountsModule)
      },
      {
        path: 'transactions',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'TRANSACTIONS_ACCESS' },
        loadChildren: () =>
          import('./features/transactions/transactions.module').then(m => m.TransactionsModule)
      },
      {
        path: 'profit',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'PROFIT_MANAGEMENT_ACCESS' },
        loadChildren: () =>
          import('./features/profit/profit.module').then(m => m.ProfitModule)
      },
      {
        path: 'cards',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'CARD_MANAGEMENT_ACCESS' },
        loadChildren: () =>
          import('./features/cards/card.module').then(m => m.CardModule)
      },
      {
        path: 'statement',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'STATEMENTS_ACCESS' },
        loadChildren: () =>
          import('./features/statement/statement.module').then(m => m.StatementModule)
      },
      {
        path: 'deposit-schemes',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'DEPOSIT_SCHEMES_ACCESS' },
        loadChildren: () =>
          import('./features/deposit-schemes/deposit-scheme.module').then(m => m.DepositSchemeModule)
      },
      {
        path: 'financing',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'FINANCING_ACCESS' },
        loadChildren: () =>
          import('./features/financing/financing.module').then(m => m.FinancingModule)
      },
      {
        path: 'contracts',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'CONTRACTS_ACCESS' },
        loadChildren: () =>
          import('./features/contracts/contract.module').then(m => m.ContractModule)
      },
      {
        path: 'shariah',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'SHARIAH_REVIEW_ACCESS' },
        loadChildren: () =>
          import('./features/shariah/shariah.module').then(m => m.ShariahModule)
      },
      {
        path: 'zakat',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'ZAKAT_CHARITY_ACCESS' },
        loadChildren: () =>
          import('./features/zakat/zakat.module').then(m => m.ZakatModule)
      },
      {
        path: 'notifications',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'NOTIFICATION_ALERTS_ACCESS' },
        loadChildren: () =>
          import('./features/notifications/notification.module').then(m => m.NotificationModule)
      },
      {
        path: 'integrations',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'INTEGRATION_MANAGEMENT_ACCESS' },
        loadChildren: () =>
          import('./features/integrations/integration.module').then(m => m.IntegrationModule)
      },
      {
        path: 'reports',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' },
        loadChildren: () =>
          import('./features/reports/report.module').then(m => m.ReportModule)
      },
      {
        path: 'security',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'SECURITY_AUDIT_ACCESS' },
        loadChildren: () =>
          import('./features/security/security.module').then(m => m.SecurityModule)
      },
      {
        path: 'workflow',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'WORKFLOW_SUPPORT_ACCESS' },
        loadChildren: () =>
          import('./features/workflow/workflow.module').then(m => m.WorkflowModule)
      },
      {
        path: 'verification',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'VERIFICATION_ACCESS' },
        loadChildren: () =>
          import('./features/verification/verification.module').then(m => m.VerificationModule)
      },
      {
        path: 'calculations',
        canActivate: [PermissionGuard],
        canLoad: [PermissionGuard],
        data: { permissionCode: 'CALCULATION_ENGINE_ACCESS' },
        loadChildren: () =>
          import('./features/calculations/calculation.module').then(m => m.CalculationModule)
      },
      {
        path: '',
        redirectTo: '/auth/login',
        pathMatch: 'full'
      }
    ]
  },

  {
    path: '**',
    redirectTo: 'auth/login'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
