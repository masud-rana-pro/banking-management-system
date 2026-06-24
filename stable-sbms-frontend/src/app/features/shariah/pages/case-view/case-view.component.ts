import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { ShariahReviewCaseResponse, formatEnumLabel } from '../../models/shariah.model';
import { ShariahService } from '../../services/shariah.service';

@Component({
  selector: 'app-case-view',
  templateUrl: './case-view.component.html',
  styleUrls: ['./case-view.component.scss']
})
export class CaseViewComponent implements OnInit {

  id = 0;
  loading = false;
  item: ShariahReviewCaseResponse | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private shariahService: ShariahService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.loading = true;
    this.shariahService.getCaseById(this.id).subscribe({
      next: data => {
        this.item = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load Shariah review case view.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/shariah/cases']);
  }

  openReview(): void {
    this.router.navigate(['/shariah/cases', this.id, 'review']);
  }

  openCorrectionQueue(): void {
    this.router.navigate(['/shariah/correction-queue']);
  }

  openCertificates(): void {
    this.router.navigate(['/shariah/fatwa-certificates']);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  get selectedChecklistCount(): number {
    return (this.item?.checklistItems || []).filter(item => item.selected).length;
  }
}
