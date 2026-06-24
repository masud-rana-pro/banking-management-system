import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { KycProfileResponse, formatEnumLabel } from '../../models/kyc.model';
import { KycService } from '../../services/kyc.service';

@Component({
  selector: 'app-kyc-approval-queue',
  templateUrl: './kyc-approval-queue.component.html',
  styleUrls: ['./kyc-approval-queue.component.scss']
})
export class KycApprovalQueueComponent implements OnInit {

  loading = false;
  queueItems: KycProfileResponse[] = [];
  branches: BranchResponse[] = [];
  customerImageMap: Record<number, string> = {};

  pendingCount = 0;
  verifiedCount = 0;
  sentBackCount = 0;
  highRiskCount = 0;

  constructor(
    private kycApi: KycService,
    private branchApi: BranchApiService,
    private customerApi: CustomerService,
    private router: Router,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;

    forkJoin({
      profiles: this.kycApi.getProfiles(),
      branches: this.branchApi.getAll(),
      customers: this.customerApi.getAll()
    }).subscribe({
      next: ({ profiles, branches, customers }) => {
        const allProfiles = profiles || [];
        this.branches = branches || [];
        this.customerImageMap = this.buildCustomerImageMap(customers || []);
        this.queueItems = allProfiles.filter(item => ['SUBMITTED', 'UNDER_REVIEW', 'VERIFIED', 'SENT_BACK'].includes(item.reviewStatus));
        this.pendingCount = this.queueItems.filter(item => ['SUBMITTED', 'UNDER_REVIEW'].includes(item.reviewStatus)).length;
        this.verifiedCount = this.queueItems.filter(item => item.reviewStatus === 'VERIFIED').length;
        this.sentBackCount = this.queueItems.filter(item => item.reviewStatus === 'SENT_BACK').length;
        this.highRiskCount = this.queueItems.filter(item => item.riskLevel === 'HIGH').length;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load approval queue.', 'error');
      }
    });
  }

  openReview(item: KycProfileResponse): void {
    this.router.navigate(['/kyc', item.id, 'review']);
  }

  openView(item: KycProfileResponse): void {
    this.router.navigate(['/kyc', item.id]);
  }

  openUpload(item: KycProfileResponse): void {
    this.router.navigate(['/kyc', item.id, 'documents']);
  }

  openHistory(item: KycProfileResponse): void {
    this.router.navigate(['/kyc', item.id, 'history']);
  }

  openCustomer(item: KycProfileResponse): void {
    this.router.navigate(['/customers', item.customerId]);
  }

  approve(item: KycProfileResponse): void {
    Swal.fire({
      icon: 'question',
      title: 'Approve KYC profile?',
      text: `${item.customerName} will be approved.`,
      showCancelButton: true,
      confirmButtonText: 'Approve'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.kycApi.approveProfile(item.id).subscribe({
        next: () => {
          Swal.fire('Approved', 'KYC profile approved successfully.', 'success');
          this.loadData();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Approve failed.', 'error')
      });
    });
  }

  returnProfile(item: KycProfileResponse): void {
    Swal.fire({
      icon: 'question',
      title: 'Return KYC profile?',
      input: 'textarea',
      inputLabel: 'Correction Remarks',
      inputValidator: value => value && value.trim() ? null : 'Correction remarks are required',
      showCancelButton: true,
      confirmButtonText: 'Return'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.kycApi.returnProfile(item.id, { remarks: String(result.value || '').trim() }).subscribe({
        next: () => {
          Swal.fire('Returned', 'KYC profile returned successfully.', 'success');
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

  getRiskClass(value?: string | null): string {
    return String(value || '').toLowerCase();
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getImageUrl(customerId?: number | null): string {
    return this.fileUploadService.resolveImageUrl(customerId ? this.customerImageMap[customerId] : '');
  }

  hasCustomerImage(customerId?: number | null): boolean {
    return !!(customerId && this.customerImageMap[customerId]);
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, item) => {
      acc[item.id] = item.profileImageName || '';
      return acc;
    }, {} as Record<number, string>);
  }
}
