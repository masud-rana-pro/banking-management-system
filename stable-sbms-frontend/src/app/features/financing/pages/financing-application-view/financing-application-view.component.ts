import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from 'src/app/features/branch/services/branch-api.service';
import { BranchResponse } from 'src/app/features/branch/models/branch.model';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { FinancingApplicationResponse, formatEnumLabel } from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-financing-application-view',
  templateUrl: './financing-application-view.component.html',
  styleUrls: ['./financing-application-view.component.scss']
})
export class FinancingApplicationViewComponent implements OnInit {

  id = 0;
  loading = false;
  item: FinancingApplicationResponse | null = null;
  branches: BranchResponse[] = [];
  customer: CustomerResponse | null = null;
  customerImageUrl = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private financingService: FinancingService,
    private branchApi: BranchApiService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.branchApi.getAll().subscribe(data => this.branches = data);
    this.load();
  }

  load(): void {
    this.loading = true;
    this.financingService.getApplicationById(this.id).subscribe({
      next: data => {
        this.item = data;
        this.loadCustomerProfile(data.customerId);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load financing application view.', 'error');
      }
    });
  }

  print(): void {
    if (!this.item) return;
    window.open(this.financingService.getSanctionLetterPreviewUrl(this.id), '_blank');
  }

  previewSanctionLetter(): void {
    if (!this.item) return;
    window.open(this.financingService.getSanctionLetterPreviewUrl(this.id), '_blank');
  }

  downloadSanctionLetter(): void {
    if (!this.item) return;
    window.open(this.financingService.getSanctionLetterDownloadUrl(this.id), '_blank');
  }

  openEdit(): void {
    this.router.navigate(['/financing/applications', this.id, 'edit']);
  }

  openReview(): void {
    this.router.navigate(['/financing/applications', this.id, 'review']);
  }

  openDisbursement(): void {
    this.router.navigate(['/financing/applications', this.id, 'disburse']);
  }

  openSchedule(): void {
    this.router.navigate(['/financing/applications', this.id, 'schedule']);
  }

  openRepayment(): void {
    this.router.navigate(['/financing/applications', this.id, 'repayment']);
  }

  openContracts(): void {
    if (!this.item) return;
    this.router.navigate(['/contracts/list'], {
      queryParams: {
        customerId: this.item.customerId,
        referenceModule: 'FINANCING'
      }
    });
  }

  generateContract(): void {
    if (!this.item) return;
    this.router.navigate(['/contracts/generate'], {
      queryParams: {
        customerId: this.item.customerId,
        referenceModule: 'FINANCING',
        referenceId: this.item.id
      }
    });
  }

  previewSupportingDocument(): void {
    if (!this.item?.supportingDocumentName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.item.supportingDocumentName), '_blank');
  }

  openShariahCases(): void {
    this.router.navigate(['/shariah/cases'], {
      queryParams: {
        referenceModule: 'FINANCING'
      }
    });
  }

  submitShariahCase(): void {
    if (!this.item) return;
    this.router.navigate(['/shariah/cases'], {
      queryParams: {
        referenceModule: 'FINANCING',
        referenceId: this.item.id,
        create: 1
      }
    });
  }

  submit(): void {
    this.financingService.submitApplication(this.id, { remarks: this.item?.remarks || '' }).subscribe({
      next: data => {
        this.item = data;
        Swal.fire('Success', 'Financing application submitted successfully.', 'success');
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to submit financing application.', 'error');
      }
    });
  }

  toggleArchive(): void {
    if (!this.item) return;
    const request$ = this.item.status === 'ARCHIVED'
      ? this.financingService.restoreApplication(this.id)
      : this.financingService.archiveApplication(this.id);

    request$.subscribe({
      next: data => {
        this.item = data;
        Swal.fire('Success', `Financing application ${data.status === 'ARCHIVED' ? 'archived' : 'restored'} successfully.`, 'success');
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to change application record status.', 'error');
      }
    });
  }

  getBranchName(branchId?: number | null): string {
    return this.branches.find(branch => branch.id === branchId)?.branchName || `Branch #${branchId ?? '-'}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
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
