import { Component } from '@angular/core';
import { ReportPageConfig } from '../../models/report.model';

@Component({
  selector: 'app-par-report',
  templateUrl: './par-report.component.html',
  styleUrls: ['./par-report.component.scss']
})
export class ParReportComponent {
  config: ReportPageConfig = {
    title: 'PAR Report',
    subtitle: 'Measure portfolio-at-risk exposure, overdue schedule concentration and outstanding financing balances',
    currentLabel: 'PAR Report',
    queryKey: 'PAR',
    route: '/reports/par',
    homeRoute: '/reports/dashboard',
    icon: 'fa fa-exclamation-circle'
  };
}
