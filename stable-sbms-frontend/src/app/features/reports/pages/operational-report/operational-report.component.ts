import { Component } from '@angular/core';
import { ReportPageConfig } from '../../models/report.model';

@Component({
  selector: 'app-operational-report',
  templateUrl: './operational-report.component.html',
  styleUrls: ['./operational-report.component.scss']
})
export class OperationalReportComponent {
  config: ReportPageConfig = {
    title: 'Operational Report',
    subtitle: 'Analyze operational transaction activity, posted volume and reversal trend with export-ready output',
    currentLabel: 'Operational Report',
    queryKey: 'OPERATIONAL',
    route: '/reports/operational',
    homeRoute: '/reports/dashboard',
    icon: 'fa fa-list-alt',
    enableBranchFilter: true
  };
}
