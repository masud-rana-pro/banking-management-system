import { Component } from '@angular/core';
import { ReportPageConfig } from '../../models/report.model';

@Component({
  selector: 'app-kpi-report',
  templateUrl: './kpi-report.component.html',
  styleUrls: ['./kpi-report.component.scss']
})
export class KpiReportComponent {
  config: ReportPageConfig = {
    title: 'Enterprise KPI Report',
    subtitle: 'Track period activity, active base and flagged exposure across customers, accounts, financing and transactions',
    currentLabel: 'Enterprise KPI Report',
    queryKey: 'KPI',
    route: '/reports/kpi',
    homeRoute: '/reports/dashboard',
    icon: 'fa fa-tachometer'
  };
}
