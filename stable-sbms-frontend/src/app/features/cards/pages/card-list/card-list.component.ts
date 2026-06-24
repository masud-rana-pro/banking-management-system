import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { AccountResponse } from '../../../accounts/models/account.model';
import { AccountService } from '../../../accounts/services/account.service';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { CardResponse, CardWorkflowActionRequest, formatEnumLabel } from '../../models/card.model';
import { CardService } from '../../services/card.service';

@Component({
  selector: 'app-card-list',
  templateUrl: './card-list.component.html',
  styleUrls: ['./card-list.component.scss']
})
export class CardListComponent implements OnInit {
  readonly viewStorageKey = 'sbms.card-list.view-mode';

  loading = false;
  allItems: CardResponse[] = [];
  items: CardResponse[] = [];
  customers: CustomerResponse[] = [];
  accounts: AccountResponse[] = [];
  customerImageMap: Record<number, string> = {};

  filters = {
    search: '',
    customerId: '',
    accountId: '',
    cardType: '',
    cardStatus: '',
    status: ''
  };

  page = 1;
  pageSize = 10;
  total = 0;
  viewMode: 'list' | 'grid' = 'list';

  constructor(
    private cardApi: CardService,
    private customerApi: CustomerService,
    private accountApi: AccountService,
    private route: ActivatedRoute,
    private router: Router,
    public accessControl: AccessControlService,
    private fileUploadService: FileUploadService,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    const savedViewMode = localStorage.getItem(this.viewStorageKey);
    if (savedViewMode === 'list' || savedViewMode === 'grid') {
      this.viewMode = savedViewMode;
    }
    this.filters.customerId = this.route.snapshot.queryParamMap.get('customerId') || '';
    this.filters.accountId = this.route.snapshot.queryParamMap.get('accountId') || '';
    this.loadData();
  }

  get activeCount(): number {
    return this.allItems.filter(item => item.cardStatus === 'ACTIVE').length;
  }

  get pendingCount(): number {
    return this.allItems.filter(item => item.cardStatus === 'PENDING_ACTIVATION').length;
  }

  get blockedCount(): number {
    return this.allItems.filter(item => item.cardStatus === 'BLOCKED').length;
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      cards: this.cardApi.getCards(),
      customers: this.customerApi.getAll().pipe(catchError(() => of([]))),
      accounts: this.accountApi.getAccounts().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ cards, customers, accounts }) => {
        this.allItems = cards || [];
        this.customers = customers || [];
        this.accounts = accounts || [];
        this.customerImageMap = this.buildCustomerImageMap(this.customers);
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load card list.', 'error');
      }
    });
  }

  get filteredAccounts(): AccountResponse[] {
    return !this.filters.customerId
      ? this.accounts
      : this.accounts.filter(item => String(item.customerId) === this.filters.customerId);
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.cardRefNo.toLowerCase().includes(keyword)
        || item.maskedCardNo.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.accountNumber.toLowerCase().includes(keyword);
      const matchesCustomer = !this.filters.customerId || String(item.customerId) === this.filters.customerId;
      const matchesAccount = !this.filters.accountId || String(item.accountId) === this.filters.accountId;
      const matchesType = !this.filters.cardType || item.cardType === this.filters.cardType;
      const matchesCardStatus = !this.filters.cardStatus || item.cardStatus === this.filters.cardStatus;
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      return matchesKeyword && matchesCustomer && matchesAccount && matchesType && matchesCardStatus && matchesStatus;
    });

    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onCustomerChange(): void {
    if (this.filters.accountId && !this.filteredAccounts.find(item => String(item.id) === this.filters.accountId)) {
      this.filters.accountId = '';
    }
    this.onSearch();
  }

  onSearch(): void {
    this.page = 1;
    this.applyFilters();
  }

  onReset(): void {
    this.filters = { search: '', customerId: '', accountId: '', cardType: '', cardStatus: '', status: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  setViewMode(mode: 'list' | 'grid'): void {
    this.viewMode = mode;
    localStorage.setItem(this.viewStorageKey, mode);
  }

  onPrint(): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No card data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Card List',
      'Al-Barakah Shariah Banking Management System',
      ['Reference', 'Card No', 'Customer', 'Account', 'Type', 'Card Status', 'Record Status'],
      data.map(item => [
        item.cardRefNo,
        item.maskedCardNo,
        `${item.customerCode} - ${item.customerName}`,
        item.accountNumber,
        item.cardType,
        item.cardStatus,
        item.status
      ])
    );
  }

  onExport(format: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No card data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Cards',
      'card-list',
      ['Reference', 'Card No', 'Customer', 'Account', 'Type', 'Card Status', 'Record Status'],
      data.map(item => [
        item.cardRefNo,
        item.maskedCardNo,
        `${item.customerCode} - ${item.customerName}`,
        item.accountNumber,
        item.cardType,
        item.cardStatus,
        item.status
      ]),
      format as 'csv' | 'excel' | 'pdf'
    );
  }

  onView(item: CardResponse): void {
    this.router.navigate(['/cards', item.id]);
  }

  onEdit(item: CardResponse): void {
    this.router.navigate(['/cards', item.id, 'edit']);
  }

  onActivation(item: CardResponse): void {
    this.router.navigate(['/cards', item.id, 'activate']);
  }

  onBlockUnblock(item: CardResponse): void {
    this.router.navigate(['/cards', item.id, 'block-unblock']);
  }

  onPinEvents(item: CardResponse): void {
    this.router.navigate(['/cards', item.id, 'pin-events']);
  }

  onArchiveToggle(item: CardResponse): void {
    const restoring = item.status === 'ARCHIVED';
    const action$ = restoring ? this.cardApi.restore(item.id) : this.cardApi.archive(item.id);
    Swal.fire({
      icon: 'question',
      title: restoring ? 'Restore card?' : 'Archive card?',
      showCancelButton: true,
      confirmButtonText: 'Continue'
    }).then(result => {
      if (!result.isConfirmed) return;
      action$.subscribe({
        next: () => {
          Swal.fire('Success', restoring ? 'Card restored successfully.' : 'Card archived successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Failed to update card record.', 'error')
      });
    });
  }

  onReplace(item: CardResponse): void {
    this.runCardAction(item, 'replace');
  }

  onRenew(item: CardResponse): void {
    this.runCardAction(item, 'renew');
  }

  private runCardAction(item: CardResponse, action: 'replace' | 'renew'): void {
    Swal.fire({
      icon: 'question',
      title: action === 'replace' ? 'Issue replacement card?' : 'Issue renewed card?',
      input: 'textarea',
      inputLabel: 'Remarks',
      inputPlaceholder: action === 'replace' ? 'Replacement reason' : 'Renewal note',
      showCancelButton: true,
      confirmButtonText: 'Continue'
    }).then(result => {
      if (!result.isConfirmed) return;
      const payload: CardWorkflowActionRequest = {
        blockReason: '',
        remarks: String(result.value || '').trim(),
        performedBy: 'SYSTEM',
        expiryDate: ''
      };
      const action$ = action === 'replace'
        ? this.cardApi.replace(item.id, payload)
        : this.cardApi.renew(item.id, payload);
      action$.subscribe({
        next: card => {
          Swal.fire('Success', `${action === 'replace' ? 'Replacement' : 'Renewed'} card ${card.cardRefNo} created successfully.`, 'success');
          this.router.navigate(['/cards', card.id]);
        },
        error: err => Swal.fire('Error', err?.error?.message || `Failed to ${action} card.`, 'error')
      });
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getCardTheme(item: CardResponse): string {
    if (item.cardStatus === 'BLOCKED') return 'theme-blocked';
    if (item.cardStatus === 'EXPIRED') return 'theme-expired';
    if (item.cardType === 'PREPAID_CARD') return 'theme-prepaid';
    if (item.cardType === 'ATM_CARD') return 'theme-atm';
    if (item.cardType === 'VIRTUAL_CARD') return 'theme-virtual';
    return 'theme-debit';
  }

  getStatusTone(item: CardResponse): string {
    return item.cardStatus === 'ACTIVE' ? 'active' : item.cardStatus === 'BLOCKED' ? 'blocked' : 'muted';
  }

  getCardInitial(item: CardResponse): string {
    const source = item.customerName || item.cardRefNo || 'C';
    return source.charAt(0).toUpperCase();
  }

  hasCustomerImage(customerId?: number | null): boolean {
    return !!(customerId && this.customerImageMap[customerId]);
  }

  getImageUrl(customerId?: number | null): string {
    return this.fileUploadService.resolveImageUrl(customerId ? this.customerImageMap[customerId] : '');
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  canAny(permissionCodes: string[]): boolean {
    return this.accessControl.hasAnyPermission(permissionCodes);
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, item) => {
      acc[item.id] = item.profileImageName || '';
      return acc;
    }, {} as Record<number, string>);
  }

  private getExportableData(): CardResponse[] {
    const keyword = this.filters.search.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.cardRefNo.toLowerCase().includes(keyword)
        || item.maskedCardNo.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.accountNumber.toLowerCase().includes(keyword);
      const matchesCustomer = !this.filters.customerId || String(item.customerId) === this.filters.customerId;
      const matchesAccount = !this.filters.accountId || String(item.accountId) === this.filters.accountId;
      const matchesType = !this.filters.cardType || item.cardType === this.filters.cardType;
      const matchesCardStatus = !this.filters.cardStatus || item.cardStatus === this.filters.cardStatus;
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      return matchesKeyword && matchesCustomer && matchesAccount && matchesType && matchesCardStatus && matchesStatus;
    });
  }
}
