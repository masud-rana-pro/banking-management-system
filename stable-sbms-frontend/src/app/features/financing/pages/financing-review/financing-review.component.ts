import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import {
  FinancingApplicationResponse,
  FinancingApplicationStatus,
  FinancingVerifyRequest,
  FinancingWorkflowActionRequest,
  formatEnumLabel
} from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-financing-review',
  templateUrl: './financing-review.component.html',
  styleUrls: ['./financing-review.component.scss']
})
export class FinancingReviewComponent implements OnInit {

  id = 0;
  loading = false;
  processing = false;
  item: FinancingApplicationResponse | null = null;
  customer: CustomerResponse | null = null;
  customerImageUrl = '';
  verifyForm: FinancingVerifyRequest = {
    assetValue: null,
    verificationNote: '',
    verifiedBy: 'SYSTEM_VERIFIER',
    remarks: ''
  };
  actionForm: FinancingWorkflowActionRequest = {
    remarks: '',
    performedBy: 'SYSTEM_REVIEWER'
  };
  readonly workflowStages = [
    {
      key: 'operations',
      title: 'Operations Review',
      subtitle: 'Submitted',
      icon: 'fa-list-ul',
      rank: 1
    },
    {
      key: 'asset',
      title: 'Asset/Risk Review',
      subtitle: 'Risk checked',
      icon: 'fa-shield',
      rank: 2
    },
    {
      key: 'shariah',
      title: 'Shariah Review',
      subtitle: 'Compliance',
      icon: 'fa-balance-scale',
      rank: 3
    },
    {
      key: 'approved',
      title: 'Approved / Sanctioned',
      subtitle: 'Sanctioned',
      icon: 'fa-certificate',
      rank: 4
    },
    {
      key: 'disbursed',
      title: 'Disbursed',
      subtitle: 'Released',
      icon: 'fa-money',
      rank: 5
    }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private financingService: FinancingService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    const actor = this.accessControl.session?.username || 'SYSTEM';
    this.verifyForm.verifiedBy = actor;
    this.actionForm.performedBy = actor;
    this.load();
  }

  load(): void {
    this.loading = true;
    this.financingService.getApplicationById(this.id).subscribe({
      next: data => {
        this.item = data;
        this.loadCustomerProfile(data.customerId);
        this.verifyForm.assetValue = data.assetVerification?.assetValue || data.requestedAmount;
        this.verifyForm.verificationNote = data.assetVerification?.verificationNote || '';
        this.verifyForm.verifiedBy = data.assetVerification?.verifiedBy || this.accessControl.session?.username || 'SYSTEM_VERIFIER';
        this.actionForm.remarks = data.remarks || '';
        this.actionForm.performedBy = this.accessControl.session?.username || 'SYSTEM_REVIEWER';
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load financing review page.', 'error');
      }
    });
  }

  verify(): void {
    if (!this.isAssetReviewStage) {
      Swal.fire('Not ready', 'Asset/Risk Review can be completed only after operations submission.', 'info');
      return;
    }
    if (!this.verifyForm.assetValue || this.verifyForm.assetValue <= 0) {
      Swal.fire('Required', 'Verified asset value must be greater than zero.', 'warning');
      return;
    }
    if (!this.verifyForm.verificationNote?.trim()) {
      Swal.fire('Required', 'Verification note is required for Asset/Risk Review.', 'warning');
      return;
    }
    this.processing = true;
    this.financingService.verifyApplication(this.id, this.verifyForm).subscribe({
      next: data => {
        this.processing = false;
        this.item = data;
        Swal.fire('Success', 'Asset verification completed successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.processing = false;
        Swal.fire('Error', err?.error?.message || 'Failed to verify financing application.', 'error');
      }
    });
  }

  moveToReview(): void {
    if (!this.isShariahReviewStage) {
      Swal.fire('Not ready', 'Complete Asset/Risk Review first, then move to Shariah Review.', 'info');
      return;
    }
    this.processing = true;
    this.financingService.reviewApplication(this.id, this.actionForm).subscribe({
      next: data => {
        this.processing = false;
        this.item = data;
        Swal.fire('Success', 'Application moved to shariah review successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.processing = false;
        Swal.fire('Error', err?.error?.message || 'Failed to move application to review.', 'error');
      }
    });
  }

  approve(): void {
    if (!this.isApprovalStage) {
      Swal.fire('Not ready', 'Application can be approved only after Asset/Risk or Shariah Review.', 'info');
      return;
    }
    this.processing = true;
    this.financingService.approveApplication(this.id, this.actionForm).subscribe({
      next: data => {
        this.processing = false;
        this.item = data;
        Swal.fire('Success', 'Financing application approved successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.processing = false;
        Swal.fire('Error', err?.error?.message || 'Failed to approve financing application.', 'error');
      }
    });
  }

  reject(): void {
    if (!this.canReturnOrReject) {
      Swal.fire('Not allowed', 'Disbursed or closed applications cannot be rejected.', 'info');
      return;
    }
    if (!this.actionForm.remarks?.trim()) {
      Swal.fire('Required', 'Reject remarks are required.', 'warning');
      return;
    }
    this.processing = true;
    this.financingService.rejectApplication(this.id, this.actionForm).subscribe({
      next: data => {
        this.processing = false;
        this.item = data;
        Swal.fire('Success', 'Financing application rejected successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.processing = false;
        Swal.fire('Error', err?.error?.message || 'Failed to reject financing application.', 'error');
      }
    });
  }

  returnBack(): void {
    if (!this.canReturnOrReject) {
      Swal.fire('Not allowed', 'Disbursed or closed applications cannot be returned.', 'info');
      return;
    }
    if (!this.actionForm.remarks?.trim()) {
      Swal.fire('Required', 'Correction remarks are required before returning the file.', 'warning');
      return;
    }
    this.processing = true;
    this.financingService.returnApplication(this.id, this.actionForm).subscribe({
      next: data => {
        this.processing = false;
        this.item = data;
        Swal.fire('Success', 'Financing application returned successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.processing = false;
        Swal.fire('Error', err?.error?.message || 'Failed to return financing application.', 'error');
      }
    });
  }

  openView(): void {
    this.router.navigate(['/financing/applications', this.id]);
  }

  openDisbursement(): void {
    if (!this.isDisbursementStage) {
      Swal.fire('Not ready', 'Only approved/sanctioned applications can be disbursed.', 'info');
      return;
    }
    this.router.navigate(['/financing/applications', this.id, 'disburse']);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getWorkflowHeadline(application: FinancingApplicationResponse): string {
    const statusText: Partial<Record<FinancingApplicationStatus, string>> = {
      DRAFT: 'Draft application awaiting submission',
      SUBMITTED: 'Submitted for operations review',
      DOC_CHECK: 'Document check in progress',
      ASSET_VERIFIED: 'Asset and risk review completed',
      SHARIAH_REVIEW: 'Under Shariah review',
      APPROVED: 'Approved and ready for disbursement',
      REJECTED: 'Application rejected',
      RETURNED: 'Returned for correction',
      DISBURSED: 'Disbursed financing file',
      ACTIVE: 'Active repayment file',
      CLOSED: 'Closed financing file'
    };
    return `${application.applicationNo} - ${statusText[application.applicationStatus] || this.getLabel(application.applicationStatus)}`;
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  getActionTitle(label: string, permissionCode: string): string {
    return this.can(permissionCode) ? label : `${label} (No permission)`;
  }

  previewSupportingDocument(): void {
    if (!this.item?.supportingDocumentName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.item.supportingDocumentName), '_blank');
  }

  get verificationLocked(): boolean {
    return !this.isAssetReviewStage;
  }

  get decisionLocked(): boolean {
    return ['DISBURSED', 'ACTIVE', 'CLOSED'].includes(this.currentStatus);
  }

  get currentStatus(): FinancingApplicationStatus | '' {
    return this.item?.applicationStatus || '';
  }

  get canVerifyAsset(): boolean {
    return this.isAssetReviewStage;
  }

  get canMoveToShariahReview(): boolean {
    return this.isShariahReviewStage;
  }

  get canApproveApplication(): boolean {
    return this.isApprovalStage;
  }

  get canOpenDisbursement(): boolean {
    return this.isDisbursementStage;
  }

  get canReturnOrReject(): boolean {
    return !['DISBURSED', 'ACTIVE', 'CLOSED'].includes(this.currentStatus);
  }

  get isAssetReviewStage(): boolean {
    return ['SUBMITTED', 'DOC_CHECK'].includes(this.currentStatus);
  }

  get isShariahReviewStage(): boolean {
    return this.currentStatus === 'ASSET_VERIFIED';
  }

  get isApprovalStage(): boolean {
    return ['ASSET_VERIFIED', 'SHARIAH_REVIEW'].includes(this.currentStatus);
  }

  get isDisbursementStage(): boolean {
    return this.currentStatus === 'APPROVED';
  }

  getWorkflowState(stageRank: number): 'done' | 'current' | 'pending' | 'stopped' {
    if (['REJECTED', 'RETURNED'].includes(this.currentStatus)) {
      return stageRank <= this.workflowRank ? 'stopped' : 'pending';
    }
    if (this.workflowRank > stageRank) return 'done';
    if (this.workflowRank === stageRank) return 'current';
    return 'pending';
  }

  getStageStateLabel(stageRank: number): string {
    const state = this.getWorkflowState(stageRank);
    if (state === 'done') return 'Completed';
    if (state === 'current') return 'Current';
    if (state === 'stopped') return this.currentStatus === 'RETURNED' ? 'Returned' : 'Stopped';
    return 'Pending';
  }

  getStageSummary(stageRank: number): string {
    const state = this.getWorkflowState(stageRank);
    if (state === 'pending') {
      return 'Awaiting completion';
    }
    if (state === 'current') {
      return 'Current processing stage';
    }
    if (state === 'stopped') {
      return this.currentStatus === 'RETURNED' ? 'Correction required' : 'File closed at this point';
    }
    const summaries: Record<number, string> = {
      1: 'Operations check complete',
      2: 'Asset/risk review complete',
      3: 'Shariah review complete',
      4: 'Sanction approved',
      5: 'Disbursement released'
    };
    return summaries[stageRank] || 'Completed';
  }

  private get workflowRank(): number {
    const statusRank: Partial<Record<FinancingApplicationStatus, number>> = {
      DRAFT: 0,
      SUBMITTED: 1,
      DOC_CHECK: 1,
      ASSET_VERIFIED: 2,
      SHARIAH_REVIEW: 3,
      APPROVED: 4,
      DISBURSED: 5,
      ACTIVE: 5,
      CLOSED: 5,
      RETURNED: 1,
      REJECTED: 1
    };
    return statusRank[this.currentStatus as FinancingApplicationStatus] ?? 0;
  }

  private loadCustomerProfile(customerId?: number | null): void {
    if (!customerId) {
      this.customer = null;
      this.customerImageUrl = '';
      return;
    }

    this.customerService.getById(customerId).subscribe({
      next: customer => {
        this.customer = customer;
        this.customerImageUrl = customer.profileImageName
          ? this.fileUploadService.resolveImageUrl(customer.profileImageName)
          : '';
      },
      error: () => {
        this.customer = null;
        this.customerImageUrl = '';
      }
    });
  }
}
