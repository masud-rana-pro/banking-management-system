import { Component } from '@angular/core';
import { ReportPageConfig } from '../../models/report.model';

@Component({
  selector: 'app-branch-report',
  templateUrl: './branch-report.component.html',
  styleUrls: ['./branch-report.component.scss']
})
export class BranchReportComponent {
  config: ReportPageConfig = {
    title: 'Branch Report',
    subtitle: 'Compare branch-wise transaction volume, self-service usage and performance trend with consistent reporting output',
    currentLabel: 'Branch Report',
    queryKey: 'BRANCH',
    route: '/reports/branch',
    homeRoute: '/reports/dashboard',
    icon: 'fa fa-building',
    enableBranchFilter: true
  };
}
