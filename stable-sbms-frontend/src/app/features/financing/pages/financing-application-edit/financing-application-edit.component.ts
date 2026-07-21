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
    this.financingService.getProducts().subscribe(data => this.products = data.filter(item => item.status !== 'ARCHIVED'));
    this.customerService.getAll().subscribe(data => this.customers = data.filter(item => item.status !== 'ARCHIVED' && item.customerStatus === 'ACTIVE'));
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
    const validationMessage = this.validateForm();
    if (validationMessage) {
      Swal.fire('Required', validationMessage, 'warning');
      return;
    }

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
        input.value = '';
      },
      error: err => {
        console.error(err);
        this.uploadingDocument = false;
        input.value = '';
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

  get selectedProduct(): FinancingProductResponse | undefined {
    return this.products.find(product => product.id === this.form.productId);
  }

  private validateForm(): string {
    if (this.uploadingDocument) {
      return 'Please wait until supporting document upload is completed.';
    }
    if (!this.form.customerId) {
      return 'Please select an active KYC-approved customer.';
    }
    if (!this.form.productId) {
      return 'Please select a financing product.';
    }
    if (!this.form.branchId) {
      return 'Please select a branch.';
    }
    if (!this.form.requestedAmount || this.form.requestedAmount <= 0) {
      return 'Requested amount must be greater than zero.';
    }
    const product = this.selectedProduct;
    if (product && (this.form.requestedAmount < product.minimumAmount || this.form.requestedAmount > product.maximumAmount)) {
      return `Requested amount must be between Tk ${product.minimumAmount.toLocaleString()} and Tk ${product.maximumAmount.toLocaleString()} for ${product.productName}.`;
    }
    if (!this.form.assetDescription?.trim()) {
      return 'Asset description is required.';
    }
    if (!this.form.purpose?.trim()) {
      return 'Purpose is required.';
    }
    return '';
  }
}
