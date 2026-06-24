import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { ProfitScheduleResponse, formatEnumLabel } from '../../models/profit.model';
import { ProfitService } from '../../services/profit.service';

@Component({
  selector: 'app-profit-schedule-list',
  templateUrl: './profit-schedule-list.component.html',
  styleUrls: ['./profit-schedule-list.component.scss']
})
export class ProfitScheduleListComponent implements OnInit {

  loading = false;
  allItems: ProfitScheduleResponse[] = [];
  items: ProfitScheduleResponse[] = [];
  branches: BranchResponse[] = [];
  customerImageMap: Record<string, string> = {};

  filters = {
    search: '',
    accountTypeId: '',
    branchId: '',
    frequency: '',
    status: ''
  };

  page = 1;
  pageSize = 10;
  total = 0;

  constructor(
    private profitApi: ProfitService,
    private branchApi: BranchApiService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private route: ActivatedRoute,
    private router: Router,
    public accessControl: AccessControlService,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.filters.accountTypeId = this.route.snapshot.queryParamMap.get('accountTypeId') || '';
    this.loadData();
  }

  get activeCount(): number {
    return this.allItems.filter(item => item.status === 'ACTIVE').length;
  }

  get dueTodayCount(): number {
    const today = new Date().toISOString().slice(0, 10);
    return this.allItems.filter(item => item.status === 'ACTIVE' && item.nextPostingDate <= today).length;
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      schedules: this.profitApi.getSchedules(),
      branches: this.branchApi.getAll().pipe(catchError(() => of([]))),
      customers: this.customerService.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ schedules, branches, customers }) => {
        this.allItems = schedules || [];
        this.branches = branches || [];
        this.customerImageMap = this.buildCustomerImageMap(customers as CustomerResponse[]);
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load profit schedule list.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.accountNumber.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.accountTypeCode.toLowerCase().includes(keyword)
        || item.accountTypeName.toLowerCase().includes(keyword);
      const matchesAccountType = !this.filters.accountTypeId || String(item.accountTypeId) === this.filters.accountTypeId;
      const matchesBranch = !this.filters.branchId || String(item.branchId || '') === this.filters.branchId;
      const matchesFrequency = !this.filters.frequency || item.profitFrequency === this.filters.frequency;
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      return matchesKeyword && matchesAccountType && matchesBranch && matchesFrequency && matchesStatus;
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
    this.filters = { search: '', accountTypeId: '', branchId: '', frequency: '', status: '' };
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
      Swal.fire('No data', 'No profit schedule data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Profit Schedule List',
      'Al-Barakah Shariah Banking Management System',
      ['Account Number', 'Customer', 'Branch', 'Account Type', 'Frequency', 'Next Posting', 'Status'],
      data.map(item => [
        item.accountNumber,
        `${item.customerCode} - ${item.customerName}`,
        this.getBranchName(item.branchId),
        `${item.accountTypeCode} - ${item.accountTypeName}`,
        item.profitFrequency,
        item.nextPostingDate || '',
        item.status
      ])
    );
  }

  onExport(format: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No profit schedule data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Profit Schedules',
      'profit-schedule-list',
      ['Account Number', 'Customer', 'Branch', 'Account Type', 'Frequency', 'Next Posting', 'Status'],
      data.map(item => [
        item.accountNumber,
        `${item.customerCode} - ${item.customerName}`,
        this.getBranchName(item.branchId),
        `${item.accountTypeCode} - ${item.accountTypeName}`,
        item.profitFrequency,
        item.nextPostingDate || '',
        item.status
      ]),
      format as 'csv' | 'excel' | 'pdf'
    );
  }

  onView(item: ProfitScheduleResponse): void {
    this.router.navigate(['/profit/schedules', item.id]);
  }

  onOpenAccount(item: ProfitScheduleResponse): void {
    this.router.navigate(['/accounts', item.accountId]);
  }

  onOpenRatio(item: ProfitScheduleResponse): void {
    this.router.navigate(['/profit/ratios'], { queryParams: { accountTypeId: item.accountTypeId } });
  }

  onRunPosting(item: ProfitScheduleResponse): void {
    this.router.navigate(['/profit/postings/run'], { queryParams: { scheduleId: item.id, accountId: item.accountId } });
  }

  onCalculate(item: ProfitScheduleResponse): void {
    this.router.navigate(['/calculations/simulator'], {
      queryParams: {
        sourceModule: 'PROFIT',
        productType: 'PROFIT_POSTING',
        principalAmount: item.currentBalance,
        ratePercent: 0,
        tenureMonths: 12,
        frequency: item.profitFrequency,
        scheduleId: item.id,
        accountId: item.accountId,
        accountTypeId: item.accountTypeId,
        sourceName: `${item.accountNumber} - ${item.customerName}`,
        returnRoute: '/profit/schedules'
      }
    });
  }

  toggleArchive(item: ProfitScheduleResponse): void {
    const restoring = item.status === 'ARCHIVED';
    const action$ = restoring
      ? this.profitApi.restoreSchedule(item.id)
      : this.profitApi.archiveSchedule(item.id);
    Swal.fire({
      icon: 'question',
      title: restoring ? 'Restore schedule?' : 'Archive schedule?',
      showCancelButton: true,
      confirmButtonText: 'Continue'
    }).then(result => {
      if (!result.isConfirmed) return;
      action$.subscribe({
        next: () => {
          Swal.fire('Success', restoring ? 'Profit schedule restored successfully.' : 'Profit schedule archived successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Failed to update profit schedule.', 'error')
      });
    });
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) return 'Unassigned Branch';
    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getCustomerImageUrl(customerCode?: string | null): string {
    if (!customerCode) {
      return '';
    }
    return this.customerImageMap[customerCode] || '';
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<string, string> {
    return customers.reduce((acc, customer) => {
      if (customer.customerCode && customer.profileImageName) {
        acc[customer.customerCode] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<string, string>);
  }

  private getExportableData(): ProfitScheduleResponse[] {
    const keyword = this.filters.search.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.accountNumber.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.accountTypeCode.toLowerCase().includes(keyword)
        || item.accountTypeName.toLowerCase().includes(keyword);
      const matchesAccountType = !this.filters.accountTypeId || String(item.accountTypeId) === this.filters.accountTypeId;
      const matchesBranch = !this.filters.branchId || String(item.branchId || '') === this.filters.branchId;
      const matchesFrequency = !this.filters.frequency || item.profitFrequency === this.filters.frequency;
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      return matchesKeyword && matchesAccountType && matchesBranch && matchesFrequency && matchesStatus;
    });
  }
}
