import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { ReportDashboardComponent } from './pages/report-dashboard/report-dashboard.component';
import { OperationalReportComponent } from './pages/operational-report/operational-report.component';
import { ProfitDistributionReportComponent } from './pages/profit-distribution-report/profit-distribution-report.component';
import { ManagementPlReportComponent } from './pages/management-pl-report/management-pl-report.component';
import { FinancingPortfolioReportComponent } from './pages/financing-portfolio-report/financing-portfolio-report.component';
import { ParReportComponent } from './pages/par-report/par-report.component';
import { ShariahAuditReportComponent } from './pages/shariah-audit-report/shariah-audit-report.component';
import { BranchReportComponent } from './pages/branch-report/branch-report.component';
import { KpiReportComponent } from './pages/kpi-report/kpi-report.component';
import { GrowthReportComponent } from './pages/growth-report/growth-report.component';
import { LoanRecoveryReportComponent } from './pages/loan-recovery-report/loan-recovery-report.component';
import { MonthlyClosingReportComponent } from './pages/monthly-closing-report/monthly-closing-report.component';
import { MonthlyClosingOpsComponent } from './pages/monthly-closing-ops/monthly-closing-ops.component';
import { ExportHistoryComponent } from './pages/export-history/export-history.component';
import { ManagementExpenseRegisterComponent } from './pages/management-expense-register/management-expense-register.component';
import { TrialBalanceReportComponent } from './pages/trial-balance-report/trial-balance-report.component';
import { LedgerProfitLossReportComponent } from './pages/ledger-profit-loss-report/ledger-profit-loss-report.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: ReportDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'operational', component: OperationalReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'profit-distribution', component: ProfitDistributionReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'management-pl', component: ManagementPlReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'financing-portfolio', component: FinancingPortfolioReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'par', component: ParReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'shariah-audit', component: ShariahAuditReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'branch', component: BranchReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'kpi', component: KpiReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'growth', component: GrowthReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'loan-recovery', component: LoanRecoveryReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'monthly-closing', component: MonthlyClosingReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'monthly-closing-ops', component: MonthlyClosingOpsComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['MONTHLY_CLOSING_CREATE', 'MONTHLY_CLOSING_SUBMIT', 'MONTHLY_CLOSING_APPROVE', 'MONTHLY_CLOSING_REJECT', 'MONTHLY_CLOSING_REOPEN'] } },
  { path: 'management-expenses', component: ManagementExpenseRegisterComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'trial-balance', component: TrialBalanceReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'ledger-profit-loss', component: LedgerProfitLossReportComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } },
  { path: 'export-history', component: ExportHistoryComponent, canActivate: [PermissionGuard], data: { permissionCode: 'REPORTING_REGULATORY_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportRoutingModule {}
