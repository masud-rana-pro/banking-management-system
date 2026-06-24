import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';

import { FinancingApplicationResponse, FinancingScheduleResponse, formatEnumLabel } from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-installment-schedule-view',
  templateUrl: './installment-schedule-view.component.html',
  styleUrls: ['./installment-schedule-view.component.scss']
})
export class InstallmentScheduleViewComponent implements OnInit {

  id = 0;
  loading = false;
  item: FinancingApplicationResponse | null = null;
  schedules: FinancingScheduleResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private financingService: FinancingService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.loading = true;
    this.financingService.getApplicationById(this.id).subscribe({
      next: data => {
        this.item = data;
        this.financingService.getSchedule(this.id).subscribe({
          next: schedules => {
            this.schedules = schedules;
            this.loading = false;
          },
          error: err => {
            console.error(err);
            this.schedules = data.schedules || [];
            this.loading = false;
          }
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load financing schedule.', 'error');
      }
    });
  }

  print(): void {
    window.print();
  }

  get totalPrincipal(): number {
    return this.schedules.reduce((sum, item) => sum + (item.principalAmount || 0), 0);
  }

  get totalProfit(): number {
    return this.schedules.reduce((sum, item) => sum + (item.profitAmount || 0), 0);
  }

  get totalCharity(): number {
    return this.schedules.reduce((sum, item) => sum + (item.charityAmount || 0), 0);
  }

  get totalPaid(): number {
    return this.schedules.reduce((sum, item) => sum + (item.paidAmount || 0), 0);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
