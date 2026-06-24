import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import {
  DepositSchemeEnrollmentResponse,
  DepositSchemeProfitDistributionResponse,
  DepositSchemeScheduleResponse,
  formatEnumLabel
} from '../models/deposit-scheme.model';
import { DepositSchemeService } from '../services/deposit-scheme.service';

@Component({
  selector: 'app-enrollment-view',
  templateUrl: './enrollment-view.component.html',
  styleUrls: ['./enrollment-view.component.scss']
})
export class EnrollmentViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: DepositSchemeEnrollmentResponse | null = null;
  schedules: DepositSchemeScheduleResponse[] = [];
  profitDistributions: DepositSchemeProfitDistributionResponse[] = [];
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
      schedules: this.depositSchemeApi.getEnrollmentSchedule(id),
      profits: this.depositSchemeApi.getEnrollmentProfitDistribution(id)
    }).subscribe({
      next: ({ enrollment, schedules, profits }) => {
        this.item = enrollment;
        this.schedules = schedules || [];
        this.profitDistributions = profits || [];
        this.loadCustomerProfile(enrollment.customerId);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load enrollment view.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/deposit-schemes/enrollments/list']);
  }

  previewCertificate(): void {
    if (!this.item) return;
    window.open(this.depositSchemeApi.getEnrollmentCertificatePreviewUrl(this.item.id), '_blank');
  }

  downloadCertificate(): void {
    if (!this.item) return;
    const link = document.createElement('a');
    link.href = this.depositSchemeApi.getEnrollmentCertificateDownloadUrl(this.item.id);
    link.target = '_blank';
    link.rel = 'noopener';
    link.click();
  }

  printCertificate(): void {
    this.previewCertificate();
  }

  openSchedule(): void {
    if (!this.item) return;
    this.router.navigate(['/deposit-schemes/enrollments', this.item.id, 'schedule']);
  }

  openProfit(): void {
    if (!this.item) return;
    this.router.navigate(['/deposit-schemes/enrollments', this.item.id, 'profit']);
  }

  openScheme(): void {
    if (!this.item) return;
    this.router.navigate(['/deposit-schemes', this.item.schemeId]);
  }

  openCustomer(): void {
    if (!this.item) return;
    this.router.navigate(['/customers', this.item.customerId]);
  }

  openAccount(): void {
    if (!this.item) return;
    this.router.navigate(['/accounts', this.item.linkedAccountId]);
  }

  get paidScheduleCount(): number {
    return this.schedules.filter(item => item.paymentStatus === 'PAID').length;
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
