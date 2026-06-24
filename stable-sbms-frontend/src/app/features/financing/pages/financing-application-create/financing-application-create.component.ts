import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from 'src/app/features/branch/services/branch-api.service';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { BranchResponse } from 'src/app/features/branch/models/branch.model';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { FinancingApplicationRequest, FinancingProductResponse } from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-financing-application-create',
  templateUrl: './financing-application-create.component.html',
  styleUrls: ['./financing-application-create.component.scss']
})
export class FinancingApplicationCreateComponent implements OnInit {

  saving = false;
  products: FinancingProductResponse[] = [];
  customers: CustomerResponse[] = [];
  branches: BranchResponse[] = [];
  uploadingDocument = false;
  uploadedDocumentUrl = '';
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
    private financingService: FinancingService,
    private customerService: CustomerService,
    private branchApi: BranchApiService,
    private fileUploadService: FileUploadService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form.productId = Number(this.route.snapshot.queryParamMap.get('productId')) || null;
    this.form.customerId = Number(this.route.snapshot.queryParamMap.get('customerId')) || null;
    this.form.branchId = Number(this.route.snapshot.queryParamMap.get('branchId')) || null;
    this.form.requestedAmount = Number(this.route.snapshot.queryParamMap.get('requestedAmount')) || null;
    this.form.remarks = this.route.snapshot.queryParamMap.get('remarks') || '';
    this.loadLookups();
  }

  loadLookups(): void {
    this.financingService.getProducts().subscribe(data => this.products = data.filter(item => item.status !== 'ARCHIVED'));
    this.customerService.getAll().subscribe(data => this.customers = data.filter(item => item.status !== 'ARCHIVED'));
    this.branchApi.getAll().subscribe(data => this.branches = data);
  }

  save(submitAfterSave = false): void {
    this.saving = true;
    this.financingService.createApplication(this.form).subscribe({
      next: data => {
        if (!submitAfterSave) {
          this.saving = false;
          Swal.fire('Success', 'Financing application created successfully.', 'success');
          this.router.navigate(['/financing/applications', data.id]);
          return;
        }

        this.financingService.submitApplication(data.id, { remarks: this.form.remarks || '' }).subscribe({
          next: submitted => {
            this.saving = false;
            Swal.fire('Success', 'Financing application created and submitted successfully.', 'success');
            this.router.navigate(['/financing/applications', submitted.id, 'review']);
          },
          error: err => {
            console.error(err);
            this.saving = false;
            Swal.fire('Warning', 'Application created but submit step failed. You can submit it from the view page.', 'warning');
            this.router.navigate(['/financing/applications', data.id]);
          }
        });
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to create financing application.', 'error');
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
        this.uploadedDocumentUrl = result.fileUrl || this.fileUploadService.resolveDocumentUrl(result.fileName);
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
}
