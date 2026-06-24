import { Component } from '@angular/core';
import { ReportPageConfig } from '../../models/report.model';

@Component({
  selector: 'app-monthly-closing-report',
  templateUrl: './monthly-closing-report.component.html',
  styleUrls: ['./monthly-closing-report.component.scss']
})
export class MonthlyClosingReportComponent {
  config: ReportPageConfig = {
    title: 'Monthly Closing Snapshot',
    subtitle: 'Review branch-wise transaction volume, reversal load, latest vault closing balance and posted profit before month-end signoff',
    currentLabel: 'Monthly Closing Snapshot',
    queryKey: 'MONTHLY_CLOSING',
    route: '/reports/monthly-closing',
    homeRoute: '/reports/dashboard',
    icon: 'fa fa-calendar-check-o',
    enableBranchFilter: true
  };
}
