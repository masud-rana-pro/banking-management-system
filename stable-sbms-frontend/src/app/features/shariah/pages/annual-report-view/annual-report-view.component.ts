import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { ShariahDashboardSummaryResponse, ShariahReviewCaseResponse, formatEnumLabel } from '../../models/shariah.model';
import { ShariahService } from '../../services/shariah.service';

@Component({
  selector: 'app-annual-report-view',
  templateUrl: './annual-report-view.component.html',
  styleUrls: ['./annual-report-view.component.scss']
})
export class AnnualReportViewComponent implements OnInit {

  loading = false;
  summary: ShariahDashboardSummaryResponse | null = null;
  cases: ShariahReviewCaseResponse[] = [];

  constructor(
    private shariahService: ShariahService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.shariahService.getDashboardSummary().subscribe({
      next: summary => {
        this.summary = summary;
        this.shariahService.getCases().subscribe({
          next: cases => {
            this.cases = cases;
            this.loading = false;
          },
          error: err => {
            console.error(err);
            this.loading = false;
            Swal.fire('Error', 'Failed to load annual report cases.', 'error');
          }
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load annual report summary.', 'error');
      }
    });
  }

  openDashboard(): void {
    this.router.navigate(['/shariah/dashboard']);
  }

  print(): void {
    window.print();
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
