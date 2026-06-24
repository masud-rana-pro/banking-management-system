import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { ShariahDashboardSummaryResponse, ShariahReviewCaseResponse, formatEnumLabel } from '../../models/shariah.model';
import { ShariahService } from '../../services/shariah.service';

@Component({
  selector: 'app-shariah-dashboard',
  templateUrl: './shariah-dashboard.component.html',
  styleUrls: ['./shariah-dashboard.component.scss']
})
export class ShariahDashboardComponent implements OnInit {

  loading = false;
  summary: ShariahDashboardSummaryResponse | null = null;

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
      next: data => {
        this.summary = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load Shariah dashboard.', 'error');
      }
    });
  }

  openCases(): void {
    this.router.navigate(['/shariah/cases']);
  }

  openCorrectionQueue(): void {
    this.router.navigate(['/shariah/correction-queue']);
  }

  openCertificates(): void {
    this.router.navigate(['/shariah/fatwa-certificates']);
  }

  openAnnualReport(): void {
    this.router.navigate(['/shariah/annual-report']);
  }

  openCase(item: ShariahReviewCaseResponse): void {
    this.router.navigate(['/shariah/cases', item.id]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
