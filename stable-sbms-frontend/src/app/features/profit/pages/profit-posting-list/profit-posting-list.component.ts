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
import { ProfitPostingResponse, formatEnumLabel } from '../../models/profit.model';
import { ProfitService } from '../../services/profit.service';

@Component({
  selector: 'app-profit-posting-list',
  templateUrl: './profit-posting-list.component.html',
  styleUrls: ['./profit-posting-list.component.scss']
})
export class ProfitPostingListComponent implements OnInit {

  loading = false;
  allItems: ProfitPostingResponse[] = [];
  items: ProfitPostingResponse[] = [];
  branches: BranchResponse[] = [];
  customerImageMap: Record<string, string> = {};

  filters = {
    search: '',
    status: '',
    branchId: '',
    accountId: '',
    scheduleId: ''
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
    this.filters.accountId = this.route.snapshot.queryParamMap.get('accountId') || '';
    this.filters.scheduleId = this.route.snapshot.queryParamMap.get('scheduleId') || '';
    this.loadData();
  }

  get postedCount(): number {
    return this.allItems.filter(item => item.status === 'POSTED').length;
  }

  get failedCount(): number {
    return this.allItems.filter(item => item.status === 'FAILED').length;
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      postings: this.profitApi.getPostings(),
      branches: this.branchApi.getAll().pipe(catchError(() => of([]))),
      customers: this.customerService.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ postings, branches, customers }) => {
        this.allItems = postings || [];
        this.branches = branches || [];
        this.customerImageMap = this.buildCustomerImageMap(customers as CustomerResponse[]);
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load profit posting list.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.postingRef.toLowerCase().includes(keyword)
        || item.accountNumber.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || String(item.ratioCode || '').toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      const matchesBranch = !this.filters.branchId || String(item.branchId || '') === this.filters.branchId;
      const matchesAccount = !this.filters.accountId || String(item.accountId) === this.filters.accountId;
      const matchesSchedule = !this.filters.scheduleId || String(item.scheduleId) === this.filters.scheduleId;
      return matchesKeyword && matchesStatus && matchesBranch && matchesAccount && matchesSchedule;
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
    this.filters = { search: '', status: '', branchId: '', accountId: '', scheduleId: '' };
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
      Swal.fire('No data', 'No profit posting data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Profit Posting List',
      'Al-Barakah Shariah Banking Management System',
      ['Posting Ref', 'Customer', 'Account', 'Branch', 'Amount', 'Status'],
      data.map(item => [
        item.postingRef,
        `${item.customerCode} - ${item.customerName}`,
        item.accountNumber,
        this.getBranchName(item.branchId),
        String(item.profitAmount ?? ''),
        item.status
      ])
    );
  }

  onExport(format: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No profit posting data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Profit Postings',
      'profit-postings',
      ['Posting Ref', 'Customer', 'Account', 'Branch', 'Amount', 'Status'],
      data.map(item => [
        item.postingRef,
        `${item.customerCode} - ${item.customerName}`,
        item.accountNumber,
        this.getBranchName(item.branchId),
        String(item.profitAmount ?? ''),
        item.status
      ]),
      format as 'csv' | 'excel' | 'pdf'
    );
  }

  onView(item: ProfitPostingResponse): void {
    this.router.navigate(['/profit/postings', item.id]);
  }

  onOpenAccount(item: ProfitPostingResponse): void {
    this.router.navigate(['/accounts', item.accountId]);
  }

  onOpenSchedule(item: ProfitPostingResponse): void {
    this.router.navigate(['/profit/schedules', item.scheduleId]);
  }

  onRunAgain(item: ProfitPostingResponse): void {
    this.router.navigate(['/profit/postings/run'], { queryParams: { scheduleId: item.scheduleId, accountId: item.accountId } });
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

  private getExportableData(): ProfitPostingResponse[] {
    const keyword = this.filters.search.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.postingRef.toLowerCase().includes(keyword)
        || item.accountNumber.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || String(item.ratioCode || '').toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      const matchesBranch = !this.filters.branchId || String(item.branchId || '') === this.filters.branchId;
      const matchesAccount = !this.filters.accountId || String(item.accountId) === this.filters.accountId;
      const matchesSchedule = !this.filters.scheduleId || String(item.scheduleId) === this.filters.scheduleId;
      return matchesKeyword && matchesStatus && matchesBranch && matchesAccount && matchesSchedule;
    });
  }
}
