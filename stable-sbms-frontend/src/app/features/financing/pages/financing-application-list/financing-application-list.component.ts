import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { BranchApiService } from 'src/app/features/branch/services/branch-api.service';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { BranchResponse } from 'src/app/features/branch/models/branch.model';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { FinancingApplicationResponse, FinancingProductResponse, formatEnumLabel } from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-financing-application-list',
  templateUrl: './financing-application-list.component.html',
  styleUrls: ['./financing-application-list.component.scss']
})
export class FinancingApplicationListComponent implements OnInit {

  loading = false;
  allItems: FinancingApplicationResponse[] = [];
  items: FinancingApplicationResponse[] = [];
  products: FinancingProductResponse[] = [];
  customers: CustomerResponse[] = [];
  branches: BranchResponse[] = [];
  customerImageMap: Record<number, string> = {};
  page = 1;
  pageSize = 10;
  total = 0;
  filters = {
    search: '',
    productId: '',
    customerId: '',
    branchId: '',
    applicationStatus: '',
    recordStatus: '',
    overdueOnly: false
  };

  constructor(
    private financingService: FinancingService,
    private branchApi: BranchApiService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private accessControl: AccessControlService,
    private route: ActivatedRoute,
    private router: Router,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.filters.productId = this.route.snapshot.queryParamMap.get('productId') || '';
    this.filters.customerId = this.route.snapshot.queryParamMap.get('customerId') || '';
    this.filters.overdueOnly = this.route.snapshot.queryParamMap.get('overdue') === '1';
    if (this.isBranchSelectionLocked) {
      this.filters.branchId = String(this.accessControl.session?.branchId || '');
    }
    this.loadLookups();
    this.load();
  }

  loadLookups(): void {
    this.financingService.getProducts().subscribe(data => this.products = data);
    this.customerService.getAll().subscribe(data => {
      this.customers = data;
      this.customerImageMap = this.buildCustomerImageMap(data);
    });
    this.branchApi.getAll().subscribe(data => this.branches = data);
  }

  load(): void {
    this.loading = true;
    this.financingService.getApplications({
      productId: this.filters.productId ? Number(this.filters.productId) : null,
      customerId: this.filters.customerId ? Number(this.filters.customerId) : null,
      branchId: this.getEffectiveBranchId(),
      keyword: this.filters.search
    }).subscribe({
      next: data => {
        this.allItems = data;
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load financing applications.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.applicationNo.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.productCode.toLowerCase().includes(keyword)
        || item.productName.toLowerCase().includes(keyword);
      const matchesProduct = !this.filters.productId || String(item.productId) === String(this.filters.productId);
      const matchesCustomer = !this.filters.customerId || String(item.customerId) === String(this.filters.customerId);
      const matchesBranch = !this.filters.branchId || String(item.branchId) === String(this.filters.branchId);
      const matchesStatus = !this.filters.applicationStatus || item.applicationStatus === this.filters.applicationStatus;
      const matchesRecord = !this.filters.recordStatus || item.status === this.filters.recordStatus;
      const matchesOverdue = !this.filters.overdueOnly || this.hasOverdueSchedules(item);
      return matchesKeyword && matchesProduct && matchesCustomer && matchesBranch && matchesStatus && matchesRecord && matchesOverdue;
    });

    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onSearch(): void {
    this.page = 1;
    this.load();
  }

  onReset(): void {
    this.filters = {
      search: '',
      productId: '',
      customerId: '',
      branchId: '',
      applicationStatus: '',
      recordStatus: '',
      overdueOnly: false
    };
    if (this.isBranchSelectionLocked) {
      this.filters.branchId = String(this.accessControl.session?.branchId || '');
    }
    this.page = 1;
    this.load();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  onView(item: FinancingApplicationResponse): void {
    this.router.navigate(['/financing/applications', item.id]);
  }

  onEdit(item: FinancingApplicationResponse): void {
    this.router.navigate(['/financing/applications', item.id, 'edit']);
  }

  onReview(item: FinancingApplicationResponse): void {
    if (!this.can('FINANCING_REVIEW')) return;
    this.router.navigate(['/financing/applications', item.id, 'review']);
  }

  onDisburse(item: FinancingApplicationResponse): void {
    if (!this.can('FINANCING_DISBURSE')) return;
    this.router.navigate(['/financing/applications', item.id, 'disburse']);
  }

  onSchedule(item: FinancingApplicationResponse): void {
    this.router.navigate(['/financing/applications', item.id, 'schedule']);
  }

  onRepayment(item: FinancingApplicationResponse): void {
    if (!this.can('FINANCING_COLLECT_PAYMENT')) return;
    this.router.navigate(['/financing/applications', item.id, 'repayment']);
  }

  onContracts(item: FinancingApplicationResponse): void {
    this.router.navigate(['/contracts/list'], {
      queryParams: {
        customerId: item.customerId,
        referenceModule: 'FINANCING'
      }
    });
  }

  onGenerateContract(item: FinancingApplicationResponse): void {
    if (!this.can('CONTRACT_GENERATE')) return;
    this.router.navigate(['/contracts/generate'], {
      queryParams: {
        customerId: item.customerId,
        referenceModule: 'FINANCING',
        referenceId: item.id
      }
    });
  }

  onShariahCases(item?: FinancingApplicationResponse): void {
    this.router.navigate(['/shariah/cases'], {
      queryParams: {
        referenceModule: 'FINANCING'
      }
    });
  }

  onSubmitShariahCase(item: FinancingApplicationResponse): void {
    if (!this.can('SHARIAH_REVIEW_ACCESS')) return;
    this.router.navigate(['/shariah/cases'], {
      queryParams: {
        referenceModule: 'FINANCING',
        referenceId: item.id,
        create: 1
      }
    });
  }

  submit(item: FinancingApplicationResponse): void {
    this.financingService.submitApplication(item.id, { remarks: item.remarks || '' }).subscribe({
      next: () => {
        Swal.fire('Success', 'Financing application submitted successfully.', 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to submit financing application.', 'error');
      }
    });
  }

  approve(item: FinancingApplicationResponse): void {
    if (!this.can('FINANCING_APPROVE')) return;
    this.financingService.approveApplication(item.id, {
      remarks: 'Approved from financing list',
      performedBy: this.accessControl.session?.username || 'SYSTEM_REVIEWER'
    }).subscribe({
      next: () => {
        Swal.fire('Success', 'Financing application approved successfully.', 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to approve financing application.', 'error');
      }
    });
  }

  toggleArchive(item: FinancingApplicationResponse): void {
    const request$ = item.status === 'ARCHIVED'
      ? this.financingService.restoreApplication(item.id)
      : this.financingService.archiveApplication(item.id);

    request$.subscribe({
      next: () => {
        Swal.fire('Success', `Financing application ${item.status === 'ARCHIVED' ? 'restored' : 'archived'} successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to change financing application record status.', 'error');
      }
    });
  }

  onPrint(): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No financing application data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Financing Application List',
      'Al-Barakah Shariah Banking Management System',
      ['Application No', 'Customer', 'Branch', 'Product', 'Status', 'Record Status'],
      data.map(item => [
        item.applicationNo,
        `${item.customerCode} - ${item.customerName}`,
        this.getBranchName(item.branchId),
        `${item.productCode} - ${item.productName}`,
        item.applicationStatus,
        item.status
      ])
    );
  }

  onExport(type: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No financing application data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Financing Applications',
      'financing-application-list',
      ['Application No', 'Customer', 'Branch', 'Product', 'Status', 'Record Status'],
      data.map(item => [
        item.applicationNo,
        `${item.customerCode} - ${item.customerName}`,
        this.getBranchName(item.branchId),
        `${item.productCode} - ${item.productName}`,
        item.applicationStatus,
        item.status
      ]),
      type.toLowerCase() as 'csv' | 'excel' | 'pdf'
    );
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  getActionTitle(label: string, permissionCode?: string): string {
    if (!permissionCode || this.can(permissionCode)) {
      return label;
    }
    return `${label} (No permission)`;
  }

  getBranchName(branchId?: number | null): string {
    return this.branches.find(branch => branch.id === branchId)?.branchName || `Branch #${branchId ?? '-'}`;
  }

  getCustomerImageUrl(customerId?: number | null): string {
    if (!customerId) {
      return '';
    }
    return this.customerImageMap[customerId] || '';
  }

  get pendingCount(): number {
    return this.allItems.filter(item => ['DRAFT', 'SUBMITTED', 'DOC_CHECK', 'ASSET_VERIFIED', 'SHARIAH_REVIEW', 'RETURNED'].includes(item.applicationStatus)).length;
  }

  get approvedCount(): number {
    return this.allItems.filter(item => item.applicationStatus === 'APPROVED').length;
  }

  get disbursedCount(): number {
    return this.allItems.filter(item => ['DISBURSED', 'ACTIVE', 'CLOSED'].includes(item.applicationStatus)).length;
  }

  get archivedCount(): number {
    return this.allItems.filter(item => item.status === 'ARCHIVED').length;
  }

  get overdueApplicationCount(): number {
    return this.allItems.filter(item => this.hasOverdueSchedules(item)).length;
  }

  get isBranchSelectionLocked(): boolean {
    const roleCode = (this.accessControl.session?.roleCode || '').toUpperCase();
    const globalRoles = ['SYSTEM_ADMIN', 'MIS_OFFICER', 'COMPLIANCE_OFFICER', 'INTERNAL_AUDITOR', 'TREASURY_FINANCE_OFFICER'];
    return !!this.accessControl.session?.branchId && !globalRoles.includes(roleCode);
  }

  toggleOverdueOnly(): void {
    this.filters.overdueOnly = !this.filters.overdueOnly;
    this.page = 1;
    this.applyFilters();
  }

  hasOverdueSchedules(item: FinancingApplicationResponse): boolean {
    return (item.schedules || []).some(schedule => schedule.scheduleStatus === 'OVERDUE');
  }

  getRecoveryStatusLabel(item: FinancingApplicationResponse): string {
    if (this.hasOverdueSchedules(item)) {
      return 'Needs recovery';
    }
    if ((item.totalOutstandingAmount || 0) > 0) {
      return 'Active repayment';
    }
    return 'Settled';
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, customer) => {
      if (customer.id && customer.profileImageName) {
        acc[customer.id] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<number, string>);
  }

  private getExportableData(): FinancingApplicationResponse[] {
    const keyword = this.filters.search.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.applicationNo.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.productCode.toLowerCase().includes(keyword)
        || item.productName.toLowerCase().includes(keyword);
      const matchesProduct = !this.filters.productId || String(item.productId) === String(this.filters.productId);
      const matchesCustomer = !this.filters.customerId || String(item.customerId) === String(this.filters.customerId);
      const matchesBranch = !this.filters.branchId || String(item.branchId) === String(this.filters.branchId);
      const matchesStatus = !this.filters.applicationStatus || item.applicationStatus === this.filters.applicationStatus;
      const matchesRecord = !this.filters.recordStatus || item.status === this.filters.recordStatus;
      const matchesOverdue = !this.filters.overdueOnly || this.hasOverdueSchedules(item);
      return matchesKeyword && matchesProduct && matchesCustomer && matchesBranch && matchesStatus && matchesRecord && matchesOverdue;
    });
  }

  private getEffectiveBranchId(): number | null {
    if (this.isBranchSelectionLocked) {
      return this.accessControl.session?.branchId || null;
    }
    return this.filters.branchId ? Number(this.filters.branchId) : null;
  }
}
