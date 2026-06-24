import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { DepositSchemeEnrollmentResponse, DepositSchemeResponse, formatEnumLabel } from '../models/deposit-scheme.model';
import { DepositSchemeService } from '../services/deposit-scheme.service';

@Component({
  selector: 'app-scheme-view',
  templateUrl: './scheme-view.component.html',
  styleUrls: ['./scheme-view.component.scss']
})
export class SchemeViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: DepositSchemeResponse | null = null;
  enrollments: DepositSchemeEnrollmentResponse[] = [];
  customerImageMap: Record<number, string> = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private depositSchemeApi: DepositSchemeService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) {
      this.load(this.id);
    }
  }

  load(id: number): void {
    this.loading = true;
    Promise.all([
      this.depositSchemeApi.getSchemeById(id).toPromise(),
      this.depositSchemeApi.getEnrollments({ schemeId: id }).toPromise()
    ]).then(([scheme, enrollments]) => {
      this.item = scheme || null;
      this.enrollments = enrollments || [];
      this.loading = false;
    }).catch(err => {
      console.error(err);
      this.loading = false;
      Swal.fire('Error', 'Failed to load scheme view.', 'error');
    });
  }

  back(): void {
    this.router.navigate(['/deposit-schemes/list']);
  }

  edit(): void {
    if (!this.item) return;
    this.router.navigate(['/deposit-schemes', this.item.id, 'edit']);
  }

  enroll(): void {
    if (!this.item) return;
    this.router.navigate(['/deposit-schemes/enrollments/new'], { queryParams: { schemeId: this.item.id } });
  }

  openCalculator(): void {
    if (!this.item) return;
    this.router.navigate(['/calculations/simulator'], {
      queryParams: {
        sourceModule: 'DEPOSIT_SCHEME',
        productType: this.item.schemeType,
        principalAmount: this.item.minimumInstallment,
        ratePercent: this.item.profitRatio,
        tenureMonths: this.item.tenureMonths,
        frequency: this.item.profitFrequency,
        schemeId: this.item.id,
        sourceName: `${this.item.schemeCode} - ${this.item.schemeName}`,
        returnRoute: `/deposit-schemes/${this.item.id}`
      }
    });
  }

  toggleArchive(): void {
    if (!this.item) return;
    const action$ = this.item.status === 'ARCHIVED'
      ? this.depositSchemeApi.restoreScheme(this.item.id)
      : this.depositSchemeApi.archiveScheme(this.item.id);

    action$.subscribe({
      next: () => {
        Swal.fire('Success', `Scheme ${this.item?.status === 'ARCHIVED' ? 'restored' : 'archived'} successfully.`, 'success');
        if (this.id) this.load(this.id);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Action failed.', 'error')
    });
  }

  openSchedule(enrollment: DepositSchemeEnrollmentResponse): void {
    this.router.navigate(['/deposit-schemes/enrollments', enrollment.id, 'schedule']);
  }

  openProfit(enrollment: DepositSchemeEnrollmentResponse): void {
    this.router.navigate(['/deposit-schemes/enrollments', enrollment.id, 'profit']);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getCustomerImageUrl(customerId?: number | null): string {
    if (!customerId) {
      return '';
    }
    return this.customerImageMap[customerId] || '';
  }

  private loadCustomers(): void {
    this.customerService.getAll().subscribe({
      next: customers => {
        this.customerImageMap = this.buildCustomerImageMap(customers || []);
      },
      error: () => {
        this.customerImageMap = {};
      }
    });
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce<Record<number, string>>((acc, customer) => {
      if (customer.id && customer.profileImageName) {
        acc[customer.id] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {});
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
