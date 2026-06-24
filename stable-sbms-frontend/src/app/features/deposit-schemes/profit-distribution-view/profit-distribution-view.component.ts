import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { DepositSchemeEnrollmentResponse, DepositSchemeProfitDistributionResponse, formatEnumLabel } from '../models/deposit-scheme.model';
import { DepositSchemeService } from '../services/deposit-scheme.service';

@Component({
  selector: 'app-profit-distribution-view',
  templateUrl: './profit-distribution-view.component.html',
  styleUrls: ['./profit-distribution-view.component.scss']
})
export class ProfitDistributionViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  enrollment: DepositSchemeEnrollmentResponse | null = null;
  profits: DepositSchemeProfitDistributionResponse[] = [];
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
      profits: this.depositSchemeApi.getEnrollmentProfitDistribution(id)
    }).subscribe({
      next: ({ enrollment, profits }) => {
        this.enrollment = enrollment;
        this.profits = profits || [];
        this.loadCustomerProfile(enrollment.customerId);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load profit distribution.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/deposit-schemes/enrollments/list']);
  }

  openSchedule(): void {
    if (!this.enrollment) return;
    this.router.navigate(['/deposit-schemes/enrollments', this.enrollment.id, 'schedule']);
  }

  openScheme(): void {
    if (!this.enrollment) return;
    this.router.navigate(['/deposit-schemes', this.enrollment.schemeId]);
  }

  get totalProfit(): number {
    return this.profits.reduce((sum, item) => sum + Number(item.profitAmount || 0), 0);
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
