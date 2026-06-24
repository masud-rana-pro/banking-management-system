import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { ProfitPostingResponse, ProfitRatioResponse, ProfitScheduleResponse, formatEnumLabel } from '../../models/profit.model';
import { ProfitService } from '../../services/profit.service';

@Component({
  selector: 'app-profit-ratio-view',
  templateUrl: './profit-ratio-view.component.html',
  styleUrls: ['./profit-ratio-view.component.scss']
})
export class ProfitRatioViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: ProfitRatioResponse | null = null;
  schedules: ProfitScheduleResponse[] = [];
  postings: ProfitPostingResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private profitApi: ProfitService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) {
      this.loadData(this.id);
    }
  }

  get linkedSchedules(): ProfitScheduleResponse[] {
    if (!this.item) return [];
    return this.schedules.filter(schedule => schedule.accountTypeId === this.item?.accountTypeId);
  }

  get postingHistory(): ProfitPostingResponse[] {
    if (!this.item) return [];
    return this.postings.filter(posting => posting.ratioCode === this.item?.ratioCode);
  }

  get failedPostings(): ProfitPostingResponse[] {
    return this.postingHistory.filter(item => item.status === 'FAILED');
  }

  loadData(id: number): void {
    this.loading = true;
    forkJoin({
      ratio: this.profitApi.getRatioById(id),
      schedules: this.profitApi.getSchedules(),
      postings: this.profitApi.getPostings()
    }).subscribe({
      next: ({ ratio, schedules, postings }) => {
        this.item = ratio;
        this.schedules = schedules || [];
        this.postings = postings || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load profit ratio view.', 'error');
      }
    });
  }

  print(): void {
    window.print();
  }

  openSchedule(id: number): void {
    this.router.navigate(['/profit/schedules', id]);
  }

  openPosting(id: number): void {
    this.router.navigate(['/profit/postings', id]);
  }

  openEdit(): void {
    if (!this.id) return;
    this.router.navigate(['/profit/ratios', this.id, 'edit']);
  }

  openCreateSchedule(): void {
    if (!this.item) return;
    this.router.navigate(['/profit/schedules/new'], { queryParams: { accountTypeId: this.item.accountTypeId } });
  }

  openPostingRun(): void {
    if (!this.item) return;
    this.router.navigate(['/profit/postings/run'], { queryParams: { accountTypeId: this.item.accountTypeId } });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
