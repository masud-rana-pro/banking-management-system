import { Component } from '@angular/core';
import { ReportPageConfig } from '../../models/report.model';

@Component({
  selector: 'app-management-pl-report',
  templateUrl: './management-pl-report.component.html',
  styleUrls: ['./management-pl-report.component.scss']
})
export class ManagementPlReportComponent {
  config: ReportPageConfig = {
    title: 'Management Profit & Loss',
    subtitle: 'Review realized financing profit proxy, depositor profit outflow and net spread before unbooked operating expenses.',
    currentLabel: 'Management Profit & Loss',
    queryKey: 'MANAGEMENT_PL',
    route: '/reports/management-pl',
    homeRoute: '/reports/dashboard',
    icon: 'fa fa-balance-scale'
  };
}
