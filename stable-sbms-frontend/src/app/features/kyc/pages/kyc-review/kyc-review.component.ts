import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, catchError, forkJoin, of, switchMap } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import {
  KYC_REVIEW_STATUS_OPTIONS,
  RISK_LEVEL_OPTIONS,
  KycDocumentResponse,
  KycProfileRequest,
  KycProfileResponse,
  KycDecisionHistoryResponse,
  formatEnumLabel
} from '../../models/kyc.model';
import { KycService } from '../../services/kyc.service';

@Component({
  selector: 'app-kyc-review',
  templateUrl: './kyc-review.component.html',
  styleUrls: ['./kyc-review.component.scss']
})
export class KycReviewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;
  profile: KycProfileResponse | null = null;
  documents: KycDocumentResponse[] = [];
  history: KycDecisionHistoryResponse[] = [];
  branches: BranchResponse[] = [];
  customerProfileImageName = '';

  riskLevelOptions = RISK_LEVEL_OPTIONS;
  reviewStatusOptions = KYC_REVIEW_STATUS_OPTIONS;

  form: KycProfileRequest = {
    customerId: null,
    riskLevel: '',
    sourceOfFundsNote: '',
    pepFlag: false,
    sanctionFlag: false,
    amlFlag: false,
    reviewStatus: 'DRAFT',
    remarks: '',
    status: 'ACTIVE'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private kycApi: KycService,
    private branchApi: BranchApiService,
    private customerApi: CustomerService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;

    if (this.id) {
      this.loadData(this.id);
    }
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getRiskClass(value?: string | null): string {
    return String(value || '').toLowerCase();
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) {
      return 'Unassigned Branch';
    }

    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getCustomerImageUrl(): string {
    return this.fileUploadService.resolveImageUrl(this.customerProfileImageName);
  }

  getDocumentImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveDocumentUrl(fileName);
  }

  isImageFile(fileName?: string | null): boolean {
    return this.fileUploadService.isImageFile(fileName);
  }

  isPdfFile(fileName?: string | null): boolean {
    return this.fileUploadService.isPdfFile(fileName);
  }

  openDocument(fileName?: string | null): void {
    const url = this.getDocumentImageUrl(fileName);
    if (url) {
      window.open(url, '_blank');
    }
  }

  back(): void {
    if (!this.id) {
      this.router.navigate(['/kyc/list']);
      return;
    }
    this.router.navigate(['/kyc', this.id]);
  }

  openCustomer(): void {
    if (!this.profile) return;
    this.router.navigate(['/customers', this.profile.customerId]);
  }

  openAccountRequest(): void {
    if (!this.profile) return;
    this.router.navigate(['/accounts/opening-requests/new'], { queryParams: { customerId: this.profile.customerId } });
  }

  openView(): void {
    if (!this.id) return;
    this.router.navigate(['/kyc', this.id]);
  }

  openUpload(): void {
    if (!this.id) return;
    this.router.navigate(['/kyc', this.id, 'documents']);
  }

  openHistory(): void {
    if (!this.id) return;
    this.router.navigate(['/kyc', this.id, 'history']);
  }

  saveReview(): void {
    if (!this.id || !this.validateForm()) return;

    this.saving = true;
    this.kycApi.updateProfile(this.id, this.buildReviewRequest(true)).subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Saved', 'Review information saved successfully.', 'success');
        this.loadData(this.id!);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save review profile.', 'error');
      }
    });
  }

  verify(): void {
    this.runWorkflow(
      'Verify KYC?',
      'The KYC profile will be marked as verified.',
      () => this.kycApi.verifyProfile(this.id!)
    );
  }

  approve(): void {
    this.runWorkflow(
      'Approve KYC?',
      'The customer will move forward after approval.',
      () => this.kycApi.approveProfile(this.id!)
    );
  }

  reject(): void {
    this.runWorkflow(
      'Reject KYC?',
      'The KYC profile will be rejected.',
      () => this.kycApi.rejectProfile(this.id!, { remarks: this.form.remarks.trim() })
    );
  }

  returnProfile(): void {
    if (!this.form.remarks.trim()) {
      Swal.fire('Validation', 'Correction remarks are required before return.', 'warning');
      return;
    }

    this.runWorkflow(
      'Return KYC?',
      'The KYC profile will be sent back for correction.',
      () => this.kycApi.returnProfile(this.id!, { remarks: this.form.remarks.trim() })
    );
  }

  private runWorkflow(title: string, text: string, actionFactory: () => Observable<KycProfileResponse>): void {
    if (!this.id || !this.validateForm()) return;

    Swal.fire({
      icon: 'question',
      title,
      text,
      showCancelButton: true,
      confirmButtonText: 'Continue'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.saving = true;
      this.kycApi.updateProfile(this.id!, this.buildReviewRequest(true))
        .pipe(switchMap(() => actionFactory()))
        .subscribe({
          next: () => {
            this.saving = false;
            Swal.fire('Success', 'KYC workflow action completed successfully.', 'success');
            this.loadData(this.id!);
          },
          error: err => {
            console.error(err);
            this.saving = false;
            Swal.fire('Error', err?.error?.message || 'KYC workflow action failed.', 'error');
          }
        });
    });
  }

  private loadData(id: number): void {
    this.loading = true;

    forkJoin({
      profile: this.kycApi.getProfileById(id),
      documents: this.kycApi.getDocuments(id).pipe(catchError(() => of([]))),
      history: this.kycApi.getDecisionHistory(id).pipe(catchError(() => of([]))),
      branches: this.branchApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ profile, documents, history, branches }) => {
        this.profile = profile;
        this.documents = documents;
        this.history = history;
        this.branches = branches;
        this.form = {
          customerId: profile.customerId,
          riskLevel: profile.riskLevel || '',
          sourceOfFundsNote: profile.sourceOfFundsNote || '',
          pepFlag: !!profile.pepFlag,
          sanctionFlag: !!profile.sanctionFlag,
          amlFlag: !!profile.amlFlag,
          reviewStatus: profile.reviewStatus || 'DRAFT',
          remarks: profile.remarks || '',
          status: profile.status || 'ACTIVE'
        };
        if (!profile.customerId) {
          this.customerProfileImageName = '';
          this.loading = false;
          return;
        }
        this.customerApi.getById(profile.customerId).pipe(catchError(() => of(null as CustomerResponse | null))).subscribe({
          next: customer => {
            this.customerProfileImageName = customer?.profileImageName || '';
            this.loading = false;
          },
          error: () => {
            this.customerProfileImageName = '';
            this.loading = false;
          }
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load KYC review page.', 'error');
      }
    });
  }

  private validateForm(): boolean {
    if (!this.form.customerId) {
      Swal.fire('Validation', 'Customer is required.', 'warning');
      return false;
    }

    if (!this.form.riskLevel) {
      Swal.fire('Validation', 'Risk level is required.', 'warning');
      return false;
    }

    return true;
  }

  private buildReviewRequest(markUnderReview: boolean): KycProfileRequest {
    let reviewStatus = this.form.reviewStatus;

    if (markUnderReview && ['DRAFT', 'SUBMITTED', 'SENT_BACK'].includes(reviewStatus)) {
      reviewStatus = 'UNDER_REVIEW';
    }

    return {
      customerId: this.form.customerId,
      riskLevel: this.form.riskLevel,
      sourceOfFundsNote: this.form.sourceOfFundsNote.trim(),
      pepFlag: !!this.form.pepFlag,
      sanctionFlag: !!this.form.sanctionFlag,
      amlFlag: !!this.form.amlFlag,
      reviewStatus,
      remarks: this.form.remarks.trim(),
      status: this.form.status
    };
  }
}
