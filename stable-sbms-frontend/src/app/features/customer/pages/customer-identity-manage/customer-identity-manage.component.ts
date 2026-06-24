import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import {
  COUNTRY_OPTIONS,
  CustomerIdentityRequest,
  CustomerIdentityResponse,
  CustomerResponse,
  DOCUMENT_TYPE_OPTIONS,
  RECORD_STATUS_OPTIONS,
  formatEnumLabel
} from '../../models/customer.model';
import { CustomerService } from '../../services/customer.service';

@Component({
  selector: 'app-customer-identity-manage',
  templateUrl: './customer-identity-manage.component.html',
  styleUrls: ['./customer-identity-manage.component.scss']
})
export class CustomerIdentityManageComponent implements OnInit {

  customerId: number | null = null;
  customer: CustomerResponse | null = null;
  identities: CustomerIdentityResponse[] = [];
  editingId: number | null = null;
  loading = false;
  saving = false;
  uploadingImage = false;
  imagePreviewUrl = '';

  documentTypeOptions = DOCUMENT_TYPE_OPTIONS;
  recordStatusOptions = RECORD_STATUS_OPTIONS;
  countries = COUNTRY_OPTIONS;

  form: CustomerIdentityRequest = this.getInitialForm();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerApi: CustomerService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.customerId = idParam ? Number(idParam) : null;

    if (this.customerId) {
      this.loadData(this.customerId);
      this.form.customerId = this.customerId;
    }
  }

  get verifiedCount(): number {
    return this.identities.filter(item => item.verifiedFlag).length;
  }

  get nidCount(): number {
    return this.identities.filter(item => item.documentType === 'NID').length;
  }

  get passportCount(): number {
    return this.identities.filter(item => item.documentType === 'PASSPORT').length;
  }

  back(): void {
    if (!this.customerId) return;
    this.router.navigate(['/customers', this.customerId]);
  }

  resetForm(): void {
    this.editingId = null;
    this.form = this.getInitialForm();
    this.form.customerId = this.customerId;
    this.imagePreviewUrl = '';
  }

  editIdentity(item: CustomerIdentityResponse): void {
    this.editingId = item.id;
    this.form = {
      customerId: item.customerId,
      documentType: item.documentType,
      documentNo: item.documentNo || '',
      issueDate: item.issueDate || '',
      expiryDate: item.expiryDate || '',
      issueCountry: item.issueCountry || 'Bangladesh',
      imageFileName: item.imageFileName || '',
      verifiedFlag: !!item.verifiedFlag,
      status: item.status || 'ACTIVE'
    };
    this.imagePreviewUrl = this.fileUploadService.isImageFile(item.imageFileName)
      ? this.fileUploadService.resolveDocumentUrl(item.imageFileName)
      : '';
  }

  save(): void {
    if (!this.validateForm()) return;

    this.saving = true;
    const save$ = this.editingId
      ? this.customerApi.updateIdentity(this.editingId, this.form)
      : this.customerApi.createIdentity(this.form);

    save$.subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Saved', 'Customer identity saved successfully.', 'success');
        this.resetForm();
        if (this.customerId) this.loadData(this.customerId);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save identity.', 'error');
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
        this.form.imageFileName = result.fileName;
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
        Swal.fire('Error', err?.error?.message || 'Failed to upload identity document.', 'error');
      }
    });
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

  private loadData(customerId: number): void {
    this.loading = true;

    forkJoin({
      customer: this.customerApi.getById(customerId),
      identities: this.customerApi.getIdentitiesByCustomer(customerId)
    }).subscribe({
      next: ({ customer, identities }) => {
        this.customer = customer;
        this.identities = identities || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load customer identity information.', 'error');
      }
    });
  }

  private validateForm(): boolean {
    if (!this.form.documentType) {
      Swal.fire('Validation', 'Document type is required.', 'warning');
      return false;
    }

    if (!this.form.documentNo.trim()) {
      Swal.fire('Validation', 'Document number is required.', 'warning');
      return false;
    }

    return true;
  }

  private getInitialForm(): CustomerIdentityRequest {
    return {
      customerId: null,
      documentType: '',
      documentNo: '',
      issueDate: '',
      expiryDate: '',
      issueCountry: 'Bangladesh',
      imageFileName: '',
      verifiedFlag: false,
      status: 'ACTIVE'
    };
  }
}
