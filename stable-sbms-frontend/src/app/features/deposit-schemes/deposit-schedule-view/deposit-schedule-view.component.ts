import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { DepositSchemeEnrollmentResponse, DepositSchemeScheduleResponse, formatEnumLabel } from '../models/deposit-scheme.model';
import { DepositSchemeService } from '../services/deposit-scheme.service';

@Component({
  selector: 'app-deposit-schedule-view',
  templateUrl: './deposit-schedule-view.component.html',
  styleUrls: ['./deposit-schedule-view.component.scss']
})
export class DepositScheduleViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  enrollment: DepositSchemeEnrollmentResponse | null = null;
  schedules: DepositSchemeScheduleResponse[] = [];
  customer: CustomerResponse | null = null;
  customerImageUrl = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private depositSchemeApi: DepositSchemeService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) {
      this.load(this.id);
    }
  }

  load(id: number): void {
    this.loading = true;
    forkJoin({
      enrollment: this.depositSchemeApi.getEnrollmentById(id),
      schedules: this.depositSchemeApi.getEnrollmentSchedule(id)
    }).subscribe({
      next: ({ enrollment, schedules }) => {
        this.enrollment = enrollment;
        this.schedules = schedules || [];
        this.loadCustomerProfile(enrollment.customerId);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load deposit schedule.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/deposit-schemes/enrollments/list']);
  }

  openProfit(): void {
    if (!this.enrollment) return;
    this.router.navigate(['/deposit-schemes/enrollments', this.enrollment.id, 'profit']);
  }

  openScheme(): void {
    if (!this.enrollment) return;
    this.router.navigate(['/deposit-schemes', this.enrollment.schemeId]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  private loadCustomerProfile(customerId?: number | null): void {
    if (!customerId) {
      this.customer = null;
      this.customerImageUrl = '';
      return;
    }

    this.customerService.getById(customerId).subscribe({
      next: customer => {
        this.customer = customer;
        this.customerImageUrl = customer.profileImageName
          ? this.fileUploadService.resolveImageUrl(customer.profileImageName)
          : '';
      },
      error: () => {
        this.customer = null;
        this.customerImageUrl = '';
      }
    });
  }
}
