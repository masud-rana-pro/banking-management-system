import { Component } from '@angular/core';
import { ReportPageConfig } from '../../models/report.model';

@Component({
  selector: 'app-shariah-audit-report',
  templateUrl: './shariah-audit-report.component.html',
  styleUrls: ['./shariah-audit-report.component.scss']
})
export class ShariahAuditReportComponent {
  config: ReportPageConfig = {
    title: 'Shariah Audit Report',
    subtitle: 'Review module-wise Shariah cases, case status distribution and audit-ready export trail',
    currentLabel: 'Shariah Audit Report',
    queryKey: 'SHARIAH_AUDIT',
    route: '/reports/shariah-audit',
    homeRoute: '/reports/dashboard',
    icon: 'fa fa-balance-scale'
  };
}
