import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import {
  COUNTRY_OPTIONS,
  CUSTOMER_STATUS_OPTIONS,
  CUSTOMER_TYPE_OPTIONS,
  CustomerRequest,
  GENDER_OPTIONS,
  MARITAL_STATUS_OPTIONS,
  RECORD_STATUS_OPTIONS
} from '../../models/customer.model';
import { CustomerService } from '../../services/customer.service';

@Component({
  selector: 'app-customer-edit',
  templateUrl: './customer-edit.component.html',
  styleUrls: ['./customer-edit.component.scss']
})
export class CustomerEditComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;
  uploadingImage = false;
  branches: BranchResponse[] = [];
  countries = COUNTRY_OPTIONS;
  customerTypeOptions = CUSTOMER_TYPE_OPTIONS;
  genderOptions = GENDER_OPTIONS;
  maritalStatusOptions = MARITAL_STATUS_OPTIONS;
  customerStatusOptions = CUSTOMER_STATUS_OPTIONS;
  recordStatusOptions = RECORD_STATUS_OPTIONS;

  form: CustomerRequest = this.getInitialForm();
  customerCode = '';
  profileImagePreviewUrl = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerApi: CustomerService,
    private branchApi: BranchApiService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    this.loadBranches();
    if (this.id) {
      this.loadCustomer(this.id);
    }
  }

  get selectedBranchLabel(): string {
    const branch = this.branches.find(item => item.id === this.form.branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : 'No branch selected';
  }

  get customerCodePreview(): string {
    return this.customerCode || 'Auto generated after save';
  }

  cancel(): void {
    this.router.navigate(['/customers/list']);
  }

  save(): void {
    if (!this.validateForm()) return;

    this.saving = true;
    const request = {
      ...this.form,
      email: this.form.email.trim(),
      profileImageName: this.form.profileImageName.trim(),
      fullName: this.form.fullName.trim(),
      fatherName: this.form.fatherName.trim(),
      motherName: this.form.motherName.trim(),
      spouseName: this.form.spouseName.trim(),
      nationality: this.form.nationality.trim(),
      mobile: this.form.mobile.trim(),
      occupation: this.form.occupation.trim(),
      sourceOfFunds: this.form.sourceOfFunds.trim()
    };

    const save$ = this.id
      ? this.customerApi.update(this.id, request)
      : this.customerApi.create(request);

    save$.subscribe({
      next: response => {
        this.saving = false;
        Swal.fire({
          icon: 'success',
          title: this.id ? 'Customer updated' : 'Customer created',
          text: this.id ? 'Customer information updated successfully.' : 'Customer created successfully.'
        }).then(() => {
          this.router.navigate(['/customers', response.id]);
        });
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save customer.', 'error');
      }
    });
  }

  onProfileImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.uploadingImage = true;
    this.fileUploadService.uploadImage(file).subscribe({
      next: result => {
        this.form.profileImageName = result.fileName;
        this.profileImagePreviewUrl = this.fileUploadService.resolveImageUrl(result.fileName);
        this.uploadingImage = false;
        input.value = '';
      },
      error: err => {
        console.error(err);
        this.uploadingImage = false;
        input.value = '';
        Swal.fire('Error', err?.error?.message || 'Failed to upload customer image.', 'error');
      }
    });
  }

  private loadBranches(): void {
    this.branchApi.getAll().subscribe({
      next: data => this.branches = data || [],
      error: err => console.error(err)
    });
  }

  private loadCustomer(id: number): void {
    this.loading = true;

    this.customerApi.getById(id).subscribe({
      next: data => {
        this.customerCode = data.customerCode;
        this.form = {
          customerType: data.customerType || '',
          fullName: data.fullName || '',
          fatherName: data.fatherName || '',
          motherName: data.motherName || '',
          spouseName: data.spouseName || '',
          dateOfBirth: data.dateOfBirth || '',
          gender: data.gender || '',
          maritalStatus: data.maritalStatus || '',
          nationality: data.nationality || '',
          mobile: data.mobile || '',
          email: data.email || '',
          profileImageName: data.profileImageName || '',
          occupation: data.occupation || '',
          monthlyIncome: data.monthlyIncome ?? null,
          sourceOfFunds: data.sourceOfFunds || '',
          branchId: data.branchId || null,
          customerStatus: data.customerStatus || 'PENDING_KYC',
          status: data.status || 'ACTIVE'
        };
        this.profileImagePreviewUrl = this.fileUploadService.resolveImageUrl(data.profileImageName);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load customer information.', 'error');
      }
    });
  }

  private validateForm(): boolean {
    if (!this.form.customerType) {
      Swal.fire('Validation', 'Customer type is required.', 'warning');
      return false;
    }

    if (!this.form.fullName.trim()) {
      Swal.fire('Validation', 'Full name is required.', 'warning');
      return false;
    }

    if (!this.form.mobile.trim()) {
      Swal.fire('Validation', 'Mobile number is required.', 'warning');
      return false;
    }

    if (!this.form.branchId) {
      Swal.fire('Validation', 'Branch is required.', 'warning');
      return false;
    }

    return true;
  }

  private getInitialForm(): CustomerRequest {
    return {
      customerType: '',
      fullName: '',
      fatherName: '',
      motherName: '',
      spouseName: '',
      dateOfBirth: '',
      gender: '',
      maritalStatus: '',
      nationality: 'Bangladesh',
      mobile: '',
      email: '',
      profileImageName: '',
      occupation: '',
      monthlyIncome: null,
      sourceOfFunds: '',
      branchId: null,
      customerStatus: 'PENDING_KYC',
      status: 'ACTIVE'
    };
  }
}
