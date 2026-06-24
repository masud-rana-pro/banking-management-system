import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { ReportRoutingModule } from './report-routing.module';

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
import { ReportPageShellComponent } from './components/report-page-shell/report-page-shell.component';

@NgModule({
  declarations: [
    ReportDashboardComponent,
    OperationalReportComponent,
    ProfitDistributionReportComponent,
    ManagementPlReportComponent,
    FinancingPortfolioReportComponent,
    ParReportComponent,
    ShariahAuditReportComponent,
    BranchReportComponent,
    KpiReportComponent,
    GrowthReportComponent,
    LoanRecoveryReportComponent,
    MonthlyClosingReportComponent,
    MonthlyClosingOpsComponent,
    ManagementExpenseRegisterComponent,
    TrialBalanceReportComponent,
    LedgerProfitLossReportComponent,
    ExportHistoryComponent,
    ReportPageShellComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ReportRoutingModule
  ]
})
export class ReportModule {}
