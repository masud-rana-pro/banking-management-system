import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerDropdownResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import {
  AccountOpeningRequestRequest,
  AccountTypeDropdownResponse,
  ACCOUNT_OPENING_STATUS_OPTIONS,
  RECORD_STATUS_OPTIONS
} from '../../models/account.model';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-account-opening-request-create',
  templateUrl: './account-opening-request-create.component.html',
  styleUrls: ['./account-opening-request-create.component.scss']
})
export class AccountOpeningRequestCreateComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;
  uploadingImage = false;
  applicantImagePreviewUrl = '';

  customers: CustomerDropdownResponse[] = [];
  accountTypes: AccountTypeDropdownResponse[] = [];
  branches: BranchResponse[] = [];

  requestStatusOptions = ACCOUNT_OPENING_STATUS_OPTIONS;
  recordStatusOptions = RECORD_STATUS_OPTIONS;

  form: AccountOpeningRequestRequest = {
    customerId: null,
    accountTypeId: null,
    branchId: null,
    requestedDate: new Date().toISOString().slice(0, 10),
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
    private customerApi: CustomerService,
    private branchApi: BranchApiService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    const customerId = Number(this.route.snapshot.queryParamMap.get('customerId') || 0) || null;
    const accountTypeId = Number(this.route.snapshot.queryParamMap.get('accountTypeId') || 0) || null;

    this.loading = true;
    forkJoin({
      customers: this.customerApi.dropdown().pipe(catchError(() => of([]))),
      accountTypes: this.accountApi.accountTypeDropdown().pipe(catchError(() => of([]))),
      branches: this.branchApi.getAll().pipe(catchError(() => of([]))),
      request: this.id ? this.accountApi.getOpeningRequestById(this.id) : of(null)
    }).subscribe({
      next: ({ customers, accountTypes, branches, request }) => {
        this.customers = customers || [];
        this.accountTypes = accountTypes || [];
        this.branches = branches || [];
        if (request) {
          this.form = {
            customerId: request.customerId,
            accountTypeId: request.accountTypeId,
            branchId: request.branchId,
            requestedDate: request.requestedDate || new Date().toISOString().slice(0, 10),
            initialDepositAmount: request.initialDepositAmount,
            requestStatus: request.requestStatus || 'DRAFT',
            remarks: request.remarks || '',
            applicantImageName: request.applicantImageName || '',
            status: request.status || 'ACTIVE'
          };
          this.applicantImagePreviewUrl = this.fileUploadService.resolveImageUrl(request.applicantImageName);
        } else {
          if (customerId) this.form.customerId = customerId;
          if (accountTypeId) this.form.accountTypeId = accountTypeId;
        }
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load opening request form.', 'error');
      }
    });
  }

  save(): void {
    if (!this.form.customerId || !this.form.accountTypeId || !this.form.branchId) {
      Swal.fire('Validation', 'Customer, account type and branch are required.', 'warning');
      return;
    }
    if ((this.form.initialDepositAmount || 0) < 0) {
      Swal.fire('Validation', 'Initial deposit cannot be negative.', 'warning');
      return;
    }

    this.saving = true;
    const request: AccountOpeningRequestRequest = {
      ...this.form,
      remarks: this.form.remarks.trim(),
      applicantImageName: this.form.applicantImageName.trim(),
      initialDepositAmount: Number(this.form.initialDepositAmount || 0)
    };

    const action$ = this.id
      ? this.accountApi.updateOpeningRequest(this.id, request)
      : this.accountApi.createOpeningRequest(request);

    action$.subscribe({
      next: res => {
        this.saving = false;
        Swal.fire('Success', `Opening request ${this.id ? 'updated' : 'created'} successfully.`, 'success');
        this.router.navigate(['/accounts/opening-requests', res.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save opening request.', 'error');
      }
    });
  }

  onApplicantImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.uploadingImage = true;
    this.fileUploadService.uploadImage(file).subscribe({
      next: result => {
        this.form.applicantImageName = result.fileName;
        this.applicantImagePreviewUrl = this.fileUploadService.resolveImageUrl(result.fileName);
        this.uploadingImage = false;
        input.value = '';
      },
      error: err => {
        console.error(err);
        this.uploadingImage = false;
        input.value = '';
        Swal.fire('Error', err?.error?.message || 'Failed to upload applicant image.', 'error');
      }
    });
  }

  openApplicantPreview(): void {
    if (!this.form.applicantImageName) {
      return;
    }
    window.open(this.fileUploadService.resolveImageUrl(this.form.applicantImageName), '_blank');
  }

  back(): void {
    this.router.navigate([this.id ? `/accounts/opening-requests/${this.id}` : '/accounts/opening-requests']);
  }
}
