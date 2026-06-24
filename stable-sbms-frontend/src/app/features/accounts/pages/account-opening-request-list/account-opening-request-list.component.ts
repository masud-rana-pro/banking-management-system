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
import { AccountOpeningRequestResponse, formatEnumLabel } from '../../models/account.model';
import { AccountHolderImageService } from '../../services/account-holder-image.service';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-account-opening-request-list',
  templateUrl: './account-opening-request-list.component.html',
  styleUrls: ['./account-opening-request-list.component.scss']
})
export class AccountOpeningRequestListComponent implements OnInit {

  loading = false;
  allItems: AccountOpeningRequestResponse[] = [];
  items: AccountOpeningRequestResponse[] = [];
  branches: BranchResponse[] = [];
  customerImageMap: Record<number, string> = {};
  customerImageByCode: Record<string, string> = {};
  customerImageByName: Record<string, string> = {};

  filters = {
    search: '',
    requestStatus: '',
    branchId: ''
  };

  page = 1;
  pageSize = 10;
  total = 0;

  get pendingCount(): number {
    return this.allItems.filter(item =>
      item.requestStatus === 'DRAFT' ||
      item.requestStatus === 'SUBMITTED' ||
      item.requestStatus === 'SENT_BACK'
    ).length;
  }

  get approvedCount(): number {
    return this.allItems.filter(item => item.requestStatus === 'APPROVED').length;
  }

  get verifiedCount(): number {
    return this.allItems.filter(item => item.requestStatus === 'VERIFIED').length;
  }

  constructor(
    private accountApi: AccountService,
    private branchApi: BranchApiService,
    private router: Router,
    private fileUploadService: FileUploadService,
    private tableExport: TableExportService,
    public accessControl: AccessControlService,
    private customerApi: CustomerService,
    private accountHolderImage: AccountHolderImageService
  ) {}

  ngOnInit(): void {
    if (this.isBranchSelectionLocked) {
      this.filters.branchId = String(this.accessControl.session?.branchId || '');
    }
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      items: this.accountApi.getOpeningRequests(),
      branches: this.branchApi.getAll().pipe(catchError(() => of([]))),
      customers: this.customerApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ items, branches, customers }) => {
        this.allItems = items || [];
        this.branches = branches || [];
        this.customerImageMap = this.accountHolderImage.buildCustomerImageMap(customers || []);
        this.customerImageByCode = this.accountHolderImage.buildCustomerImageByCode(customers || []);
        this.customerImageByName = this.accountHolderImage.buildCustomerImageByName(customers || []);
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load account opening request list.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.requestNo.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.accountTypeCode.toLowerCase().includes(keyword)
        || item.accountTypeName.toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.requestStatus || item.requestStatus === this.filters.requestStatus;
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
    this.filters = { search: '', requestStatus: '', branchId: '' };
    if (this.isBranchSelectionLocked) {
      this.filters.branchId = String(this.accessControl.session?.branchId || '');
    }
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
      Swal.fire('No data', 'No account opening request data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Account Opening Request List',
      'Al-Barakah Shariah Banking Management System',
      ['Request No', 'Customer', 'Branch', 'Account Type', 'Status'],
      data.map(item => [
        item.requestNo,
        `${item.customerCode} - ${item.customerName}`,
        this.getBranchName(item.branchId),
        `${item.accountTypeCode} - ${item.accountTypeName}`,
        item.requestStatus
      ])
    );
  }

  onExport(format: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No account opening request data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Account Opening Requests',
      'account-opening-requests',
      ['Request No', 'Customer', 'Branch', 'Account Type', 'Status'],
      data.map(item => [
        item.requestNo,
        `${item.customerCode} - ${item.customerName}`,
        this.getBranchName(item.branchId),
        `${item.accountTypeCode} - ${item.accountTypeName}`,
        item.requestStatus
      ]),
      format as 'csv' | 'excel' | 'pdf'
    );
  }

  onView(item: AccountOpeningRequestResponse): void {
    this.router.navigate(['/accounts/opening-requests', item.id]);
  }

  onEdit(item: AccountOpeningRequestResponse): void {
    this.router.navigate(['/accounts/opening-requests', item.id, 'edit']);
  }

  onReview(item: AccountOpeningRequestResponse): void {
    this.router.navigate(['/accounts/opening-requests', item.id, 'review']);
  }

  onOpenCustomer(item: AccountOpeningRequestResponse): void {
    this.router.navigate(['/customers', item.customerId]);
  }

  onOpenType(item: AccountOpeningRequestResponse): void {
    this.router.navigate(['/accounts/account-types', item.accountTypeId]);
  }

  onOpenAccount(item: AccountOpeningRequestResponse): void {
    if (!item.accountId) return;
    this.router.navigate(['/accounts', item.accountId]);
  }

  onSubmit(item: AccountOpeningRequestResponse): void {
    this.accountApi.submitOpeningRequest(item.id).subscribe({
      next: () => {
        Swal.fire('Submitted', 'Opening request submitted successfully.', 'success');
        this.loadData();
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Submit failed.', 'error')
    });
  }

  onVerify(item: AccountOpeningRequestResponse): void {
    this.accountApi.verifyOpeningRequest(item.id).subscribe({
      next: () => {
        Swal.fire('Verified', 'Opening request verified successfully.', 'success');
        this.loadData();
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Verify failed.', 'error')
    });
  }

  onApprove(item: AccountOpeningRequestResponse): void {
    this.accountApi.approveOpeningRequest(item.id).subscribe({
      next: () => {
        Swal.fire('Approved', 'Opening request approved successfully.', 'success');
        this.loadData();
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Approve failed.', 'error')
    });
  }

  onReject(item: AccountOpeningRequestResponse): void {
    Swal.fire({
      icon: 'question',
      title: 'Reject request?',
      input: 'textarea',
      inputLabel: 'Remarks',
      showCancelButton: true,
      confirmButtonText: 'Reject'
    }).then(result => {
      if (!result.isConfirmed) return;
      this.accountApi.rejectOpeningRequest(item.id, { remarks: String(result.value || '').trim() }).subscribe({
        next: () => {
          Swal.fire('Rejected', 'Opening request rejected successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Reject failed.', 'error')
      });
    });
  }

  onReturn(item: AccountOpeningRequestResponse): void {
    Swal.fire({
      icon: 'question',
      title: 'Return request?',
      input: 'textarea',
      inputLabel: 'Correction Remarks',
      inputValidator: value => value && value.trim() ? null : 'Correction remarks are required',
      showCancelButton: true,
      confirmButtonText: 'Return'
    }).then(result => {
      if (!result.isConfirmed) return;
      this.accountApi.returnOpeningRequest(item.id, { remarks: String(result.value || '').trim() }).subscribe({
        next: () => {
          Swal.fire('Returned', 'Opening request returned successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Return failed.', 'error')
      });
    });
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) {
      return 'Unassigned Branch';
    }
    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  get isBranchSelectionLocked(): boolean {
    const roleCode = (this.accessControl.session?.roleCode || '').toUpperCase();
    const globalRoles = ['SYSTEM_ADMIN', 'MIS_OFFICER', 'COMPLIANCE_OFFICER', 'INTERNAL_AUDITOR', 'TREASURY_FINANCE_OFFICER'];
    return !!this.accessControl.session?.branchId && !globalRoles.includes(roleCode);
  }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }

  hasRequestHolderImage(item?: AccountOpeningRequestResponse | null): boolean {
    return !!this.getRequestHolderImageName(item);
  }

  getRequestHolderImageUrl(item?: AccountOpeningRequestResponse | null): string {
    return this.fileUploadService.resolveImageUrl(this.getRequestHolderImageName(item));
  }

  openApplicantPreview(fileName?: string | null): void {
    const url = this.getImageUrl(fileName);
    if (url) {
      window.open(url, '_blank');
    }
  }

  private getExportableData(): AccountOpeningRequestResponse[] {
    const keyword = this.filters.search.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.requestNo.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.accountTypeCode.toLowerCase().includes(keyword)
        || item.accountTypeName.toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.requestStatus || item.requestStatus === this.filters.requestStatus;
      const matchesBranch = !this.filters.branchId || String(item.branchId || '') === this.filters.branchId;
      return matchesKeyword && matchesStatus && matchesBranch;
    });
  }

  private getRequestHolderImageName(item?: AccountOpeningRequestResponse | null): string {
    return this.accountHolderImage.resolveOpeningRequestImageName(
      item,
      this.customerImageMap,
      this.customerImageByCode,
      this.customerImageByName
    );
  }
}
