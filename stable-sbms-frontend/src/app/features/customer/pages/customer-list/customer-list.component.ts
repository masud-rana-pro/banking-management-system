import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import {
  CUSTOMER_STATUS_OPTIONS,
  CUSTOMER_TYPE_OPTIONS,
  CustomerResponse,
  RECORD_STATUS_OPTIONS,
  formatEnumLabel
} from '../../models/customer.model';
import { CustomerService } from '../../services/customer.service';
import { KycService } from '../../../kyc/services/kyc.service';

@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.scss']
})
export class CustomerListComponent implements OnInit {
  readonly viewStorageKey = 'sbms.customer-list.view-mode';

  allCustomers: CustomerResponse[] = [];
  customers: CustomerResponse[] = [];
  filteredCustomers: CustomerResponse[] = [];
  branches: BranchResponse[] = [];

  loading = false;
  viewMode: 'list' | 'grid' = 'list';
  page = 1;
  pageSize = 10;
  total = 0;

  totalAll = 0;
  activeCount = 0;
  pendingKycCount = 0;
  blockedCount = 0;

  filters = {
    search: '',
    customerType: '',
    customerStatus: '',
    recordStatus: '',
    branchId: ''
  };

  customerTypeOptions = [{ value: '', label: 'All Customer Types' }, ...CUSTOMER_TYPE_OPTIONS.map(item => ({ value: item.value, label: item.label }))];
  customerStatusOptions = [{ value: '', label: 'All Customer Status' }, ...CUSTOMER_STATUS_OPTIONS.map(item => ({ value: item.value, label: item.label }))];
  recordStatusOptions = [{ value: '', label: 'All Record Status' }, ...RECORD_STATUS_OPTIONS.map(item => ({ value: item.value, label: item.label }))];

  constructor(
    private customerApi: CustomerService,
    private branchApi: BranchApiService,
    private kycApi: KycService,
    private router: Router,
    public accessControl: AccessControlService,
    private fileUploadService: FileUploadService,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.viewMode = (localStorage.getItem(this.viewStorageKey) as 'list' | 'grid') || 'list';
    this.loadData();
  }

  setViewMode(mode: 'list' | 'grid'): void {
    this.viewMode = mode;
    localStorage.setItem(this.viewStorageKey, mode);
  }

  loadData(): void {
    this.loading = true;

    forkJoin({
      customers: this.customerApi.getAll(),
      branches: this.branchApi.getAll()
    }).subscribe({
      next: ({ customers, branches }) => {
        this.allCustomers = customers || [];
        this.branches = branches || [];
        this.calculateSummary();
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load customer list.', 'error');
      }
    });
  }

  onSearch(): void {
    this.applyFiltersAndPaging(true);
  }

  onReset(): void {
    this.filters = {
      search: '',
      customerType: '',
      customerStatus: '',
      recordStatus: '',
      branchId: ''
    };
    this.applyFiltersAndPaging(true);
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFiltersAndPaging(false);
  }

  onView(item: CustomerResponse): void {
    this.router.navigate(['/customers', item.id]);
  }

  onEdit(item: CustomerResponse): void {
    this.router.navigate(['/customers', item.id, 'edit']);
  }

  onManageAddress(item: CustomerResponse): void {
    this.router.navigate(['/customers', item.id, 'addresses']);
  }

  onManageIdentity(item: CustomerResponse): void {
    this.router.navigate(['/customers', item.id, 'identities']);
  }

  onStatusAction(item: CustomerResponse): void {
    this.router.navigate(['/customers', item.id, 'status']);
  }

  onOpenKyc(item: CustomerResponse): void {
    this.kycApi.getProfileByCustomerId(item.id).subscribe({
      next: profile => this.router.navigate(['/kyc', profile.id]),
      error: () => this.router.navigate(['/kyc/new'], { queryParams: { customerId: item.id } })
    });
  }

  onNewAccountRequest(item: CustomerResponse): void {
    this.router.navigate(['/accounts/opening-requests/new'], { queryParams: { customerId: item.id } });
  }

  onArchive(item: CustomerResponse): void {
    Swal.fire({
      icon: 'warning',
      title: 'Archive customer?',
      text: `${item.fullName} will be archived from active customer records.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, archive'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.customerApi.archive(item.id).subscribe({
        next: () => {
          Swal.fire('Archived', 'Customer archived successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Archive failed.', 'error')
      });
    });
  }

  onRestore(item: CustomerResponse): void {
    Swal.fire({
      icon: 'question',
      title: 'Restore customer?',
      text: `${item.fullName} will be restored as active record.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, restore'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.customerApi.restore(item.id).subscribe({
        next: () => {
          Swal.fire('Restored', 'Customer restored successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Restore failed.', 'error')
      });
    });
  }

  onActivate(item: CustomerResponse): void {
    Swal.fire({
      icon: 'question',
      title: 'Activate customer?',
      text: `${item.fullName} will be activated after validation.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, activate'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.customerApi.activate(item.id).subscribe({
        next: () => {
          Swal.fire('Activated', 'Customer activated successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Activation failed.', 'error')
      });
    });
  }

  onBlock(item: CustomerResponse): void {
    Swal.fire({
      icon: 'warning',
      title: 'Block customer?',
      text: `${item.fullName} will be blocked from further operation.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, block'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.customerApi.block(item.id).subscribe({
        next: () => {
          Swal.fire('Blocked', 'Customer blocked successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Block failed.', 'error')
      });
    });
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) {
      return 'Unassigned Branch';
    }

    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId || '-'}`;
  }

  getStatusLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  canAny(permissionCodes: string[]): boolean {
    return this.accessControl.hasAnyPermission(permissionCodes);
  }

  onPrint(): void {
    const data = this.getExportableData();

    if (!data.length) {
      Swal.fire('No data', 'No customer data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Customer List',
      'Al-Barakah Shariah Banking Management System',
      ['Customer Code', 'Full Name', 'Type', 'Branch', 'Mobile', 'Email', 'Customer Status', 'Record Status'],
      data.map(item => [
        item.customerCode,
        item.fullName,
        item.customerType,
        this.getBranchName(item.branchId),
        item.mobile,
        item.email || '',
        item.customerStatus,
        item.status
      ])
    );
  }

  onExport(type: 'csv' | 'excel' | 'pdf'): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No customer data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Customers',
      'customer-list',
      ['Customer Code', 'Full Name', 'Type', 'Branch', 'Mobile', 'Email', 'Customer Status', 'Record Status'],
      data.map(item => [
        item.customerCode,
        item.fullName,
        item.customerType,
        this.getBranchName(item.branchId),
        item.mobile,
        item.email || '',
        item.customerStatus,
        item.status
      ]),
      type
    );
  }

  private applyFiltersAndPaging(resetPage: boolean): void {
    if (resetPage) this.page = 1;

    const search = this.filters.search.trim().toLowerCase();
    const customerType = this.filters.customerType.trim();
    const customerStatus = this.filters.customerStatus.trim();
    const recordStatus = this.filters.recordStatus.trim();
    const branchId = this.filters.branchId.trim();

    const filtered = this.allCustomers.filter(item => {
      const matchSearch =
        !search ||
        (item.customerCode || '').toLowerCase().includes(search) ||
        (item.fullName || '').toLowerCase().includes(search) ||
        (item.mobile || '').toLowerCase().includes(search) ||
        (item.email || '').toLowerCase().includes(search) ||
        (item.occupation || '').toLowerCase().includes(search);

      const matchType = !customerType || item.customerType === customerType;
      const matchCustomerStatus = !customerStatus || item.customerStatus === customerStatus;
      const matchRecordStatus = !recordStatus || item.status === recordStatus;
      const matchBranch = !branchId || String(item.branchId || '') === branchId;

      return matchSearch && matchType && matchCustomerStatus && matchRecordStatus && matchBranch;
    });

    this.filteredCustomers = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.customers = filtered.slice(start, start + this.pageSize);
  }

  private calculateSummary(): void {
    this.totalAll = this.allCustomers.length;
    this.activeCount = this.allCustomers.filter(item => item.customerStatus === 'ACTIVE').length;
    this.pendingKycCount = this.allCustomers.filter(item => item.customerStatus === 'PENDING_KYC').length;
    this.blockedCount = this.allCustomers.filter(item => item.customerStatus === 'BLOCKED').length;
  }

  private getExportableData(): CustomerResponse[] {
    const hasFilter = Object.values(this.filters).some(item => !!item.trim());
    return hasFilter ? this.filteredCustomers : this.allCustomers;
  }

}
