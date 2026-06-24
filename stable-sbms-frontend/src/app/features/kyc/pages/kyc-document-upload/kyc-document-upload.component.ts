import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import {
  KYC_DOCUMENT_TYPE_OPTIONS,
  RECORD_STATUS_OPTIONS,
  KycDocumentRequest,
  KycDocumentResponse,
  KycProfileResponse,
  formatEnumLabel
} from '../../models/kyc.model';
import { KycService } from '../../services/kyc.service';

@Component({
  selector: 'app-kyc-document-upload',
  templateUrl: './kyc-document-upload.component.html',
  styleUrls: ['./kyc-document-upload.component.scss']
})
export class KycDocumentUploadComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;
  uploadingImage = false;
  profile: KycProfileResponse | null = null;
  customer: CustomerResponse | null = null;
  customerImageUrl = '';
  documents: KycDocumentResponse[] = [];
  imagePreviewUrl = '';

  documentTypeOptions = KYC_DOCUMENT_TYPE_OPTIONS;
  recordStatusOptions = RECORD_STATUS_OPTIONS;

  form: KycDocumentRequest = this.getInitialForm();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private kycApi: KycService,
    private fileUploadService: FileUploadService,
    private customerService: CustomerService
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

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveDocumentUrl(fileName);
  }

  isImageFile(fileName?: string | null): boolean {
    return this.fileUploadService.isImageFile(fileName);
  }

  isPdfFile(fileName?: string | null): boolean {
    return this.fileUploadService.isPdfFile(fileName);
  }

  openDocument(fileName?: string | null): void {
    const url = this.getImageUrl(fileName);
    if (url) {
      window.open(url, '_blank');
    }
  }

  back(): void {
    if (!this.id) return;
    this.router.navigate(['/kyc', this.id]);
  }

  openView(): void {
    if (!this.id) return;
    this.router.navigate(['/kyc', this.id]);
  }

  openReview(): void {
    if (!this.id) return;
    this.router.navigate(['/kyc', this.id, 'review']);
  }

  openHistory(): void {
    if (!this.id) return;
    this.router.navigate(['/kyc', this.id, 'history']);
  }

  openCustomer(): void {
    if (!this.profile) return;
    this.router.navigate(['/customers', this.profile.customerId]);
  }

  save(): void {
    if (!this.validateForm()) return;

    this.saving = true;
    const request: KycDocumentRequest = {
      customerId: this.profile?.customerId || null,
      documentType: this.form.documentType,
      fileReferenceId: this.form.fileReferenceId.trim(),
      documentNo: this.form.documentNo.trim(),
      issueDate: this.form.issueDate,
      expiryDate: this.form.expiryDate,
      verifiedFlag: !!this.form.verifiedFlag,
      status: this.form.status
    };

    this.kycApi.uploadDocument(request).subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Uploaded', 'KYC document uploaded successfully.', 'success');
        this.form = this.getInitialForm();
        this.imagePreviewUrl = '';
        if (this.id) this.loadData(this.id);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Document upload failed.', 'error');
      }
    });
  }

  onDocumentImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.uploadingImage = true;
    this.fileUploadService.uploadDocument(file).subscribe({
      next: result => {
        this.form.fileReferenceId = result.fileName;
        this.imagePreviewUrl = this.fileUploadService.isImageFile(result.fileName)
          ? this.fileUploadService.resolveDocumentUrl(result.fileName)
          : '';
        this.uploadingImage = false;
        input.value = '';
      },
      error: err => {
        console.error(err);
        this.uploadingImage = false;
        input.value = '';
        Swal.fire('Error', err?.error?.message || 'Failed to upload KYC document file.', 'error');
      }
    });
  }

  private loadData(id: number): void {
    this.loading = true;

    forkJoin({
      profile: this.kycApi.getProfileById(id),
      documents: this.kycApi.getDocuments(id)
    }).subscribe({
      next: ({ profile, documents }) => {
        this.profile = profile;
        this.documents = documents || [];
        this.form.customerId = profile.customerId;
        this.loadCustomerImage(profile.customerId);
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load document upload page.', 'error');
      }
    });
  }

  private loadCustomerImage(customerId?: number | null): void {
    if (!customerId) {
      this.customer = null;
      this.customerImageUrl = '';
      this.loading = false;
      return;
    }

    this.customerService.getById(customerId).pipe(
      catchError(() => of(null as CustomerResponse | null))
    ).subscribe({
      next: customer => {
        this.customer = customer;
        this.customerImageUrl = customer?.profileImageName
          ? this.fileUploadService.resolveImageUrl(customer.profileImageName)
          : '';
        this.loading = false;
      },
      error: () => {
        this.customer = null;
        this.customerImageUrl = '';
        this.loading = false;
      }
    });
  }

  private validateForm(): boolean {
    if (!this.form.documentType) {
      Swal.fire('Validation', 'Document type is required.', 'warning');
      return false;
    }

    return true;
  }

  private getInitialForm(): KycDocumentRequest {
    return {
      customerId: null,
      documentType: '',
      fileReferenceId: '',
      documentNo: '',
      issueDate: '',
      expiryDate: '',
      verifiedFlag: false,
      status: 'ACTIVE'
    };
  }
}
