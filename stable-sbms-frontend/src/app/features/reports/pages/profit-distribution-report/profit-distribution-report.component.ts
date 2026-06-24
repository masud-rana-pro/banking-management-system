import { Component } from '@angular/core';
import { ReportPageConfig } from '../../models/report.model';

@Component({
  selector: 'app-profit-distribution-report',
  templateUrl: './profit-distribution-report.component.html',
  styleUrls: ['./profit-distribution-report.component.scss']
})
export class ProfitDistributionReportComponent {
  config: ReportPageConfig = {
    title: 'Profit Distribution Report',
    subtitle: 'Track posting status, distributed profit amount and the latest run period for profit management',
    currentLabel: 'Profit Distribution Report',
    queryKey: 'PROFIT_DISTRIBUTION',
    route: '/reports/profit-distribution',
    homeRoute: '/reports/dashboard',
    icon: 'fa fa-line-chart'
  };
}
