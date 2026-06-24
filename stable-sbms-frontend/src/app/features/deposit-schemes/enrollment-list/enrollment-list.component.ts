import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { DepositSchemeEnrollmentResponse, formatEnumLabel } from '../models/deposit-scheme.model';
import { DepositSchemeService } from '../services/deposit-scheme.service';

@Component({
  selector: 'app-enrollment-list',
  templateUrl: './enrollment-list.component.html',
  styleUrls: ['./enrollment-list.component.scss']
})
export class EnrollmentListComponent implements OnInit {

  loading = false;
  allItems: DepositSchemeEnrollmentResponse[] = [];
  items: DepositSchemeEnrollmentResponse[] = [];
  customerImageMap: Record<number, string> = {};

  filters = {
    search: '',
    enrollmentStatus: '',
    schemeId: '',
    customerId: '',
    accountId: ''
  };

  page = 1;
  pageSize = 10;
  total = 0;

  constructor(
    private depositSchemeApi: DepositSchemeService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private route: ActivatedRoute,
    private router: Router,
    private tableExport: TableExportService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.route.queryParamMap.subscribe(params => {
      this.filters.schemeId = params.get('schemeId') || '';
      this.filters.customerId = params.get('customerId') || '';
      this.filters.accountId = params.get('accountId') || '';
      this.load();
    });
  }

  load(): void {
    this.loading = true;
    this.depositSchemeApi.getEnrollments({
      schemeId: this.filters.schemeId ? Number(this.filters.schemeId) : null,
      customerId: this.filters.customerId ? Number(this.filters.customerId) : null,
      accountId: this.filters.accountId ? Number(this.filters.accountId) : null
    }).subscribe({
      next: items => {
        this.allItems = items || [];
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load enrollments.', 'error');
      }
    });
  }

  get activeCount(): number {
    return this.allItems.filter(item => item.enrollmentStatus === 'ACTIVE').length;
  }

  get maturedCount(): number {
    return this.allItems.filter(item => item.enrollmentStatus === 'MATURED').length;
  }

  get earlyWithdrawalCount(): number {
    return this.allItems.filter(item => item.enrollmentStatus === 'EARLY_WITHDRAWAL_REQUESTED' || item.earlyWithdrawalRequested).length;
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.enrollmentNo.toLowerCase().includes(keyword)
        || item.schemeCode.toLowerCase().includes(keyword)
        || item.schemeName.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.linkedAccountNumber.toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.enrollmentStatus || item.enrollmentStatus === this.filters.enrollmentStatus;
      return matchesKeyword && matchesStatus;
    });

    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onSearch(): void {
    this.page = 1;
    this.applyFilters();
  }

  onReset(): void {
    this.filters.search = '';
    this.filters.enrollmentStatus = '';
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  onPrint(): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No enrollment data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Deposit Scheme Enrollment List',
      'Al-Barakah Shariah Banking Management System',
      ['Enrollment No', 'Customer', 'Scheme', 'Account', 'Status'],
      data.map(item => [
        item.enrollmentNo,
        `${item.customerCode} - ${item.customerName}`,
        `${item.schemeCode} - ${item.schemeName}`,
        item.linkedAccountNumber,
        item.enrollmentStatus
      ])
    );
  }

  onExport(format: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No enrollment data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Deposit Scheme Enrollments',
      'deposit-scheme-enrollments',
      ['Enrollment No', 'Customer', 'Scheme', 'Account', 'Status'],
      data.map(item => [
        item.enrollmentNo,
        `${item.customerCode} - ${item.customerName}`,
        `${item.schemeCode} - ${item.schemeName}`,
        item.linkedAccountNumber,
        item.enrollmentStatus
      ]),
      format as 'csv' | 'excel' | 'pdf'
    );
  }

  openNew(): void {
    const queryParams: Record<string, string> = {};
    if (this.filters.schemeId) queryParams['schemeId'] = this.filters.schemeId;
    if (this.filters.customerId) queryParams['customerId'] = this.filters.customerId;
    if (this.filters.accountId) queryParams['accountId'] = this.filters.accountId;
    this.router.navigate(['/deposit-schemes/enrollments/new'], { queryParams });
  }

  openSchedule(item: DepositSchemeEnrollmentResponse): void {
    this.router.navigate(['/deposit-schemes/enrollments', item.id, 'schedule']);
  }

  openView(item: DepositSchemeEnrollmentResponse): void {
    this.router.navigate(['/deposit-schemes/enrollments', item.id]);
  }

  openProfit(item: DepositSchemeEnrollmentResponse): void {
    this.router.navigate(['/deposit-schemes/enrollments', item.id, 'profit']);
  }

  openScheme(item: DepositSchemeEnrollmentResponse): void {
    this.router.navigate(['/deposit-schemes', item.schemeId]);
  }

  openCustomer(item: DepositSchemeEnrollmentResponse): void {
    this.router.navigate(['/customers', item.customerId]);
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
        this.customerImageMap = this.buildCustomerImageMap(customers);
      },
      error: () => {
        this.customerImageMap = {};
      }
    });
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, customer) => {
      if (customer.id && customer.profileImageName) {
        acc[customer.id] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<number, string>);
  }

  private getExportableData(): DepositSchemeEnrollmentResponse[] {
    const keyword = this.filters.search.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.enrollmentNo.toLowerCase().includes(keyword)
        || item.schemeCode.toLowerCase().includes(keyword)
        || item.schemeName.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.linkedAccountNumber.toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.enrollmentStatus || item.enrollmentStatus === this.filters.enrollmentStatus;
      return matchesKeyword && matchesStatus;
    });
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
