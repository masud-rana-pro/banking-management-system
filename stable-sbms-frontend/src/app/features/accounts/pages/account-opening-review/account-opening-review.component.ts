import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, finalize, forkJoin, of, Observable } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerService } from '../../../customer/services/customer.service';
import {
  AccountOpeningRequestRequest,
  AccountOpeningRequestResponse,
  ACCOUNT_OPENING_STATUS_OPTIONS,
  RECORD_STATUS_OPTIONS,
  formatEnumLabel
} from '../../models/account.model';
import { AccountHolderImageService } from '../../services/account-holder-image.service';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-account-opening-review',
  templateUrl: './account-opening-review.component.html',
  styleUrls: ['./account-opening-review.component.scss']
})
export class AccountOpeningReviewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;
  item: AccountOpeningRequestResponse | null = null;
  branches: BranchResponse[] = [];
  resolvedImageName = '';
  customerImageMap: Record<number, string> = {};
  customerImageByCode: Record<string, string> = {};
  customerImageByName: Record<string, string> = {};

  requestStatusOptions = ACCOUNT_OPENING_STATUS_OPTIONS;
  recordStatusOptions = RECORD_STATUS_OPTIONS;

  form: AccountOpeningRequestRequest = {
    customerId: null,
    accountTypeId: null,
    branchId: null,
    requestedDate: '',
    initialDepositAmount: 0,
    requestStatus: 'DRAFT',
    remarks: '',
    applicantImageName: '',
    status: 'ACTIVE'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountApi: AccountService,
    private branchApi: BranchApiService,
    private fileUploadService: FileUploadService,
    private customerApi: CustomerService,
    private accountHolderImage: AccountHolderImageService
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
      item: this.accountApi.getOpeningRequestById(id),
      branches: this.branchApi.getAll().pipe(catchError(() => of([]))),
      customers: this.customerApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ item, branches, customers }) => {
        this.item = item;
        this.branches = branches || [];
        this.customerImageMap = this.accountHolderImage.buildCustomerImageMap(customers || []);
        this.customerImageByCode = this.accountHolderImage.buildCustomerImageByCode(customers || []);
        this.customerImageByName = this.accountHolderImage.buildCustomerImageByName(customers || []);
        this.resolvedImageName = this.accountHolderImage.resolveOpeningRequestImageName(
          item,
          this.customerImageMap,
          this.customerImageByCode,
          this.customerImageByName
        );
        this.form = {
          customerId: item.customerId,
          accountTypeId: item.accountTypeId,
          branchId: item.branchId,
          requestedDate: item.requestedDate || '',
          initialDepositAmount: item.initialDepositAmount,
          requestStatus: item.requestStatus || 'DRAFT',
          remarks: item.remarks || '',
          applicantImageName: item.applicantImageName || '',
          status: item.status || 'ACTIVE'
        };
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load opening review page.', 'error');
      }
    });
  }

  saveReview(): void {
    if (!this.id) return;
    this.saving = true;
    this.accountApi.updateOpeningRequest(this.id, { ...this.form, remarks: this.form.remarks.trim() }).subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Saved', 'Opening request review saved successfully.', 'success');
        this.load(this.id!);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save review.', 'error');
      }
    });
  }

  runWorkflow(title: string, action: () => Observable<AccountOpeningRequestResponse>): void {
    if (!this.id) return;
    this.saving = true;
    action()
      .pipe(finalize(() => this.saving = false))
      .subscribe({
        next: () => {
          Swal.fire('Success', `${title} completed successfully.`, 'success');
          this.load(this.id!);
        },
        error: err => {
          console.error(err);
          Swal.fire('Error', err?.error?.message || `${title} failed.`, 'error');
        }
      });
  }

  submit(): void {
    this.runWorkflow('Submit', () => this.accountApi.submitOpeningRequest(this.id!));
  }

  verify(): void {
    this.runWorkflow('Verify', () => this.accountApi.verifyOpeningRequest(this.id!));
  }

  approve(): void {
    this.runWorkflow('Approve', () => this.accountApi.approveOpeningRequest(this.id!));
  }

  reject(): void {
    this.runWorkflow('Reject', () => this.accountApi.rejectOpeningRequest(this.id!, { remarks: this.form.remarks.trim() }));
  }

  returnForCorrection(): void {
    if (!this.form.remarks.trim()) {
      Swal.fire('Validation', 'Correction remarks are required before return.', 'warning');
      return;
    }
    this.runWorkflow('Return', () => this.accountApi.returnOpeningRequest(this.id!, { remarks: this.form.remarks.trim() }));
  }

  openView(): void {
    if (!this.id) return;
    this.router.navigate(['/accounts/opening-requests', this.id]);
  }

  openCustomer(): void {
    if (!this.item) return;
    this.router.navigate(['/customers', this.item.customerId]);
  }

  openType(): void {
    if (!this.item) return;
    this.router.navigate(['/accounts/account-types', this.item.accountTypeId]);
  }

  openAccount(): void {
    if (!this.item?.accountId) return;
    this.router.navigate(['/accounts', this.item.accountId]);
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) return 'Unassigned Branch';
    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }

  getResolvedImageUrl(): string {
    return this.fileUploadService.resolveImageUrl(this.resolvedImageName);
  }

  openApplicantPreview(fileName?: string | null): void {
    const url = this.getImageUrl(fileName);
    if (url) {
      window.open(url, '_blank');
    }
  }
}
