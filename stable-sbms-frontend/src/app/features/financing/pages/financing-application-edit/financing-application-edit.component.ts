import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from 'src/app/features/branch/services/branch-api.service';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { BranchResponse } from 'src/app/features/branch/models/branch.model';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { FinancingApplicationRequest, FinancingProductResponse, formatEnumLabel } from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-financing-application-edit',
  templateUrl: './financing-application-edit.component.html',
  styleUrls: ['./financing-application-edit.component.scss']
})
export class FinancingApplicationEditComponent implements OnInit {

  id = 0;
  loading = false;
  saving = false;
  currentStatus = '';
  products: FinancingProductResponse[] = [];
  customers: CustomerResponse[] = [];
  branches: BranchResponse[] = [];
  uploadingDocument = false;
  form: FinancingApplicationRequest = {
    customerId: null,
    productId: null,
    branchId: null,
    requestedAmount: null,
    assetDescription: '',
    purpose: '',
    supportingDocumentName: '',
    remarks: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private financingService: FinancingService,
    private customerService: CustomerService,
    private branchApi: BranchApiService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadLookups();
    this.load();
  }

  loadLookups(): void {
    this.financingService.getProducts().subscribe(data => this.products = data);
    this.customerService.getAll().subscribe(data => this.customers = data);
    this.branchApi.getAll().subscribe(data => this.branches = data);
  }

  load(): void {
    this.loading = true;
    this.financingService.getApplicationById(this.id).subscribe({
      next: data => {
        this.currentStatus = data.applicationStatus;
        this.form = {
          customerId: data.customerId,
          productId: data.productId,
          branchId: data.branchId,
          requestedAmount: data.requestedAmount,
          assetDescription: data.assetDescription,
          purpose: data.purpose,
          supportingDocumentName: data.supportingDocumentName || '',
          remarks: data.remarks || ''
        };
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load financing application.', 'error');
      }
    });
  }

  save(): void {
    this.saving = true;
    this.financingService.updateApplication(this.id, this.form).subscribe({
      next: data => {
        this.saving = false;
        Swal.fire('Success', 'Financing application updated successfully.', 'success');
        this.router.navigate(['/financing/applications', data.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to update financing application.', 'error');
      }
    });
  }

  onSupportingDocumentSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.uploadingDocument = true;
    this.fileUploadService.uploadDocument(file).subscribe({
      next: result => {
        this.form.supportingDocumentName = result.fileName;
        this.uploadingDocument = false;
      },
      error: err => {
        console.error(err);
        this.uploadingDocument = false;
        Swal.fire('Error', err?.error?.message || 'Failed to upload supporting document.', 'error');
      }
    });
  }

  previewSupportingDocument(): void {
    if (!this.form.supportingDocumentName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.form.supportingDocumentName), '_blank');
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
