import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { TransactionResponse, formatEnumLabel } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-transaction-view',
  templateUrl: './transaction-view.component.html',
  styleUrls: ['./transaction-view.component.scss']
})
export class TransactionViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: TransactionResponse | null = null;
  branches: BranchResponse[] = [];
  customerImageMap: Record<string, string> = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private transactionApi: TransactionService,
    private branchApi: BranchApiService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private accessControl: AccessControlService
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
      item: this.transactionApi.getJournal(id),
      branches: this.branchApi.getAll().pipe(catchError(() => of([]))),
      customers: this.customerService.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ item, branches, customers }) => {
        this.item = item;
        this.branches = branches || [];
        this.customerImageMap = this.buildCustomerImageMap(customers as CustomerResponse[]);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load transaction view.', 'error');
      }
    });
  }

  previewVoucher(): void {
    if (!this.id) return;
    window.open(this.transactionApi.getVoucherPreviewUrl(this.id), '_blank', 'noopener');
  }

  downloadVoucher(): void {
    if (!this.id) return;
    window.open(this.transactionApi.getVoucherDownloadUrl(this.id), '_blank', 'noopener');
  }

  printVoucher(): void {
    this.previewVoucher();
  }

  openDebitAccount(): void {
    if (!this.item?.debitAccountId) return;
    this.router.navigate(['/accounts', this.item.debitAccountId]);
  }

  openCreditAccount(): void {
    if (!this.item?.creditAccountId) return;
    this.router.navigate(['/accounts', this.item.creditAccountId]);
  }

  openParentTransaction(): void {
    if (!this.item?.parentTransactionId) return;
    this.router.navigate(['/transactions', this.item.parentTransactionId]);
  }

  openReversal(): void {
    if (!this.canReverse || !this.id) return;
    if (!this.id) return;
    this.router.navigate(['/transactions', this.id, 'reverse']);
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) return 'Unassigned Branch';
    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  get canReverse(): boolean {
    return this.accessControl.hasPermission('TRANSACTION_REVERSE');
  }

  getReverseTitle(): string {
    return this.canReverse ? 'Reverse' : 'Reverse (No permission)';
  }

  getCustomerImageUrl(customerCode?: string | null): string {
    if (!customerCode) {
      return '';
    }
    return this.customerImageMap[customerCode] || '';
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<string, string> {
    return customers.reduce((acc, customer) => {
      if (customer.customerCode && customer.profileImageName) {
        acc[customer.customerCode] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<string, string>);
  }
}
