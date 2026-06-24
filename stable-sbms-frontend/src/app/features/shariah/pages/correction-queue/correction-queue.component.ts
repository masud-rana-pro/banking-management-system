import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { ShariahReviewCaseResponse, formatEnumLabel } from '../../models/shariah.model';
import { ShariahService } from '../../services/shariah.service';

@Component({
  selector: 'app-correction-queue',
  templateUrl: './correction-queue.component.html',
  styleUrls: ['./correction-queue.component.scss']
})
export class CorrectionQueueComponent implements OnInit {

  loading = false;
  items: ShariahReviewCaseResponse[] = [];

  constructor(
    private shariahService: ShariahService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.shariahService.getCases({ caseStatus: 'RETURNED' }).subscribe({
      next: data => {
        this.items = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load correction queue.', 'error');
      }
    });
  }

  openCase(item: ShariahReviewCaseResponse): void {
    this.router.navigate(['/shariah/cases', item.id]);
  }

  openReview(item: ShariahReviewCaseResponse): void {
    this.router.navigate(['/shariah/cases', item.id, 'review']);
  }

  openCaseList(): void {
    this.router.navigate(['/shariah/cases']);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
