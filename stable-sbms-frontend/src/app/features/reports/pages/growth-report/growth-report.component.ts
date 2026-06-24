import { Component } from '@angular/core';
import { ReportPageConfig } from '../../models/report.model';

@Component({
  selector: 'app-growth-report',
  templateUrl: './growth-report.component.html',
  styleUrls: ['./growth-report.component.scss']
})
export class GrowthReportComponent {
  config: ReportPageConfig = {
    title: 'Growth Report',
    subtitle: 'Review monthly customer, account and financing acquisition trend across the selected reporting window',
    currentLabel: 'Growth Report',
    queryKey: 'GROWTH',
    route: '/reports/growth',
    homeRoute: '/reports/dashboard',
    icon: 'fa fa-area-chart'
  };
}
