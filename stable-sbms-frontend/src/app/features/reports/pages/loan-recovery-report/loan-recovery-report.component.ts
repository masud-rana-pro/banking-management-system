import { Component } from '@angular/core';
import { ReportPageConfig } from '../../models/report.model';

@Component({
  selector: 'app-loan-recovery-report',
  templateUrl: './loan-recovery-report.component.html',
  styleUrls: ['./loan-recovery-report.component.scss']
})
export class LoanRecoveryReportComponent {
  config: ReportPageConfig = {
    title: 'Loan Recovery Report',
    subtitle: 'Measure recovered amount, overdue exposure and financed account concentration by product for recovery follow-up',
    currentLabel: 'Loan Recovery Report',
    queryKey: 'LOAN_RECOVERY',
    route: '/reports/loan-recovery',
    homeRoute: '/reports/dashboard',
    icon: 'fa fa-life-ring',
    enableBranchFilter: true
  };
}
