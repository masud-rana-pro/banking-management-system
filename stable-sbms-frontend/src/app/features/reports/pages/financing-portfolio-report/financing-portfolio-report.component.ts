import { Component } from '@angular/core';
import { ReportPageConfig } from '../../models/report.model';

@Component({
  selector: 'app-financing-portfolio-report',
  templateUrl: './financing-portfolio-report.component.html',
  styleUrls: ['./financing-portfolio-report.component.scss']
})
export class FinancingPortfolioReportComponent {
  config: ReportPageConfig = {
    title: 'Financing Portfolio Report',
    subtitle: 'View financing application volume, requested amount and approval progression across the selected period',
    currentLabel: 'Financing Portfolio Report',
    queryKey: 'FINANCING_PORTFOLIO',
    route: '/reports/financing-portfolio',
    homeRoute: '/reports/dashboard',
    icon: 'fa fa-bank'
  };
}
