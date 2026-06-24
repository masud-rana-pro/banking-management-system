import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { AccountOpeningRequestResponse, AccountResponse, formatEnumLabel } from '../../models/account.model';
import { AccountHolderImageService } from '../../services/account-holder-image.service';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-account-list',
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.scss']
})
export class AccountListComponent implements OnInit {
  readonly viewStorageKey = 'sbms.account-list.view-mode';

  loading = false;
  allItems: AccountResponse[] = [];
  items: AccountResponse[] = [];
  branches: BranchResponse[] = [];
  customerImageMap: Record<number, string> = {};
  customerImageByCode: Record<string, string> = {};
  customerImageByName: Record<string, string> = {};
  requestImageMap: Record<number, string> = {};
  requestImageByCode: Record<string, string> = {};
  requestImageByName: Record<string, string> = {};
  openingRequests: AccountOpeningRequestResponse[] = [];
  viewMode: 'list' | 'grid' = 'list';

  filters = {
    search: '',
    accountStatus: '',
    branchId: ''
  };

  page = 1;
  pageSize = 10;
  total = 0;

  get activeCount(): number {
    return this.allItems.filter(item => item.accountStatus === 'ACTIVE').length;
  }

  get pendingActivationCount(): number {
    return this.allItems.filter(item => item.accountStatus === 'PENDING_ACTIVATION').length;
  }

  get blockedFrozenCount(): number {
    return this.allItems.filter(item => item.accountStatus === 'BLOCKED' || item.accountStatus === 'FROZEN').length;
  }

  constructor(
    private accountApi: AccountService,
    private branchApi: BranchApiService,
    private customerApi: CustomerService,
    private router: Router,
    public accessControl: AccessControlService,
    private fileUploadService: FileUploadService,
    private tableExport: TableExportService,
    private accountHolderImage: AccountHolderImageService
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
      accounts: this.accountApi.getAccounts(),
      openingRequests: this.accountApi.getOpeningRequests().pipe(catchError(() => of([]))),
      branches: this.branchApi.getAll().pipe(catchError(() => of([]))),
      customers: this.customerApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ accounts, openingRequests, branches, customers }) => {
        this.allItems = accounts || [];
        this.openingRequests = openingRequests || [];
        this.branches = branches || [];
        this.customerImageMap = this.accountHolderImage.buildCustomerImageMap(customers || []);
        this.customerImageByCode = this.accountHolderImage.buildCustomerImageByCode(customers || []);
        this.customerImageByName = this.accountHolderImage.buildCustomerImageByName(customers || []);
        this.requestImageMap = this.accountHolderImage.buildRequestImageMap(this.openingRequests);
        this.requestImageByCode = this.accountHolderImage.buildRequestImageByCode(this.openingRequests);
        this.requestImageByName = this.accountHolderImage.buildRequestImageByName(this.openingRequests);
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load account list.', 'error');
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
      const matchesStatus = !this.filters.accountStatus || item.accountStatus === this.filters.accountStatus;
      const matchesBranch = !this.filters.branchId || String(item.branchId || '') === this.filters.branchId;
      return matchesKeyword && matchesStatus && matchesBranch;
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
    this.filters = { search: '', accountStatus: '', branchId: '' };
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
      Swal.fire('No data', 'No account data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Account List',
      'Al-Barakah Shariah Banking Management System',
      ['Account Number', 'Customer', 'Branch', 'Type', 'Status', 'Current Balance'],
      data.map(item => [
        item.accountNumber,
        `${item.customerCode} - ${item.customerName}`,
        this.getBranchName(item.branchId),
        `${item.accountTypeCode} - ${item.accountTypeName}`,
        item.accountStatus,
        String(item.currentBalance ?? '')
      ])
    );
  }

  onExport(format: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No account data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Accounts',
      'account-list',
      ['Account Number', 'Customer', 'Branch', 'Type', 'Status', 'Current Balance'],
      data.map(item => [
        item.accountNumber,
        `${item.customerCode} - ${item.customerName}`,
        this.getBranchName(item.branchId),
        `${item.accountTypeCode} - ${item.accountTypeName}`,
        item.accountStatus,
        String(item.currentBalance ?? '')
      ]),
      format as 'csv' | 'excel' | 'pdf'
    );
  }

  onView(item: AccountResponse): void {
    this.router.navigate(['/accounts', item.id]);
  }

  onStatus(item: AccountResponse): void {
    this.router.navigate(['/accounts', item.id, 'status']);
  }

  onOpenCustomer(item: AccountResponse): void {
    this.router.navigate(['/customers', item.customerId]);
  }

  onOpenRequest(item: AccountResponse): void {
    if (!item.openingRequestId) return;
    this.router.navigate(['/accounts/opening-requests', item.openingRequestId]);
  }

  onDeposit(item: AccountResponse): void {
    this.router.navigate(['/transactions/deposit'], { queryParams: { accountId: item.id } });
  }

  onWithdraw(item: AccountResponse): void {
    this.router.navigate(['/transactions/withdraw'], { queryParams: { accountId: item.id } });
  }

  onTransfer(item: AccountResponse): void {
    this.router.navigate(['/transactions/transfer'], { queryParams: { fromAccountId: item.id } });
  }

  onProfitSchedule(item: AccountResponse): void {
    this.router.navigate(['/profit/schedules/new'], { queryParams: { accountId: item.id, accountTypeId: item.accountTypeId } });
  }

  onProfitPostings(item: AccountResponse): void {
    this.router.navigate(['/profit/postings'], { queryParams: { accountId: item.id } });
  }

  onCards(item: AccountResponse): void {
    this.router.navigate(['/cards/list'], { queryParams: { accountId: item.id, customerId: item.customerId } });
  }

  onIssueCard(item: AccountResponse): void {
    this.router.navigate(['/cards/new'], { queryParams: { accountId: item.id, customerId: item.customerId } });
  }

  onStatement(item: AccountResponse): void {
    this.router.navigate(['/statement/customer/request'], { queryParams: { accountId: item.id, customerId: item.customerId } });
  }

  onDepositScheme(item: AccountResponse): void {
    this.router.navigate(['/deposit-schemes/enrollments/new'], { queryParams: { accountId: item.id, customerId: item.customerId } });
  }

  onFinancing(item: AccountResponse): void {
    this.router.navigate(['/financing/applications/new'], { queryParams: { customerId: item.customerId, branchId: item.branchId } });
  }

  runStatusAction(item: AccountResponse, action: 'activate' | 'block' | 'freeze' | 'close'): void {
    const titleMap = {
      activate: 'Activate account?',
      block: 'Block account?',
      freeze: 'Freeze account?',
      close: 'Close account?'
    };

    Swal.fire({
      icon: 'question',
      title: titleMap[action],
      input: 'textarea',
      inputLabel: 'Remarks',
      showCancelButton: true,
      confirmButtonText: 'Continue'
    }).then(result => {
      if (!result.isConfirmed) return;
      const request = { remarks: String(result.value || '').trim() };
      const action$ = action === 'activate'
        ? this.accountApi.activateAccount(item.id, request)
        : action === 'block'
          ? this.accountApi.blockAccount(item.id, request)
          : action === 'freeze'
            ? this.accountApi.freezeAccount(item.id, request)
            : this.accountApi.closeAccount(item.id, request);

      action$.subscribe({
        next: () => {
          Swal.fire('Success', `Account ${action}d successfully.`, 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || `Failed to ${action} account.`, 'error')
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

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  canAny(permissionCodes: string[]): boolean {
    return this.accessControl.hasAnyPermission(permissionCodes);
  }

  hasAccountHolderImage(item?: AccountResponse | null): boolean {
    return !!this.getAccountHolderImageName(item);
  }

  getAccountHolderImageUrl(item?: AccountResponse | null): string {
    return this.fileUploadService.resolveImageUrl(this.getAccountHolderImageName(item));
  }

  private getExportableData(): AccountResponse[] {
    const keyword = this.filters.search.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.accountNumber.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.accountTypeCode.toLowerCase().includes(keyword)
        || item.accountTypeName.toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.accountStatus || item.accountStatus === this.filters.accountStatus;
      const matchesBranch = !this.filters.branchId || String(item.branchId || '') === this.filters.branchId;
      return matchesKeyword && matchesStatus && matchesBranch;
    });
  }

  private getAccountHolderImageName(item?: AccountResponse | null): string {
    return this.accountHolderImage.resolveAccountImageName(
      item,
      this.customerImageMap,
      this.customerImageByCode,
      this.customerImageByName,
      this.requestImageMap,
      this.requestImageByCode,
      this.requestImageByName,
      this.openingRequests
    );
  }
}
