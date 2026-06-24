import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';

import { BranchApiService } from '../services/branch-api.service';
import { BranchRequest } from '../models/branch.model';

@Component({
  selector: 'app-branch-edit',
  templateUrl: './branch-edit.component.html',
  styleUrls: ['./branch-edit.component.scss']
})
export class BranchEditComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;
  submitted = false;
  userImageMap: Record<string, string> = {};
  userDisplayMap: Record<number, string> = {};

  form: BranchRequest = {
    branchCode: '',
    branchName: '',
    branchShortName: '',
    branchType: '',
    routingNo: '',
    swiftCode: '',
    email: '',
    mobile: '',
    phone: '',
    addressLine1: '',
    addressLine2: '',
    countryId: null,
    divisionId: null,
    districtId: null,
    upazilaId: null,
    postalCode: '',
    managerUserId: null,
    openedDate: '',
    status: 'ACTIVE'
  };

  branchTypeOptions = [
    { value: '', label: 'Select Branch Type' },
    { value: 'MAIN', label: 'Main Branch' },
    { value: 'URBAN', label: 'Urban Branch' },
    { value: 'CORPORATE', label: 'Corporate Branch' },
    { value: 'INDUSTRIAL', label: 'Industrial Branch' }
  ];

  statusOptions = [
    { value: 'ACTIVE', label: 'ACTIVE' },
    { value: 'INACTIVE', label: 'INACTIVE' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private branchApi: BranchApiService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;

    if (this.id) {
      this.loadBranch(this.id);
    }
  }

  loadBranch(id: number): void {
    this.loading = true;

    this.branchApi.getById(id).subscribe({
      next: data => {
        this.form = {
          branchCode: data.branchCode,
          branchName: data.branchName,
          branchShortName: data.branchShortName || '',
          branchType: data.branchType,
          routingNo: data.routingNo,
          swiftCode: data.swiftCode || '',
          email: data.email || '',
          mobile: data.mobile || '',
          phone: data.phone || '',
          addressLine1: data.addressLine1,
          addressLine2: data.addressLine2 || '',
          countryId: data.countryId || null,
          divisionId: data.divisionId || null,
          districtId: data.districtId || null,
          upazilaId: data.upazilaId || null,
          postalCode: data.postalCode || '',
          managerUserId: data.managerUserId || null,
          openedDate: data.openedDate || '',
          status: data.status || 'ACTIVE'
        };

        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load branch.', 'error');
      }
    });
  }

  save(): void {
    this.submitted = true;

    if (!this.validate()) return;

    this.saving = true;

    const request: BranchRequest = {
      ...this.form,
      branchCode: this.form.branchCode?.trim(),
      branchName: this.form.branchName.trim(),
      branchShortName: this.form.branchShortName?.trim(),
      routingNo: this.form.routingNo.trim(),
      swiftCode: this.form.swiftCode?.trim(),
      email: this.form.email?.trim(),
      mobile: this.form.mobile?.trim(),
      phone: this.form.phone?.trim(),
      addressLine1: this.form.addressLine1.trim(),
      addressLine2: this.form.addressLine2?.trim(),
      postalCode: this.form.postalCode?.trim(),
      countryId: this.toNumberOrNull(this.form.countryId),
      divisionId: this.toNumberOrNull(this.form.divisionId),
      districtId: this.toNumberOrNull(this.form.districtId),
      upazilaId: this.toNumberOrNull(this.form.upazilaId),
      managerUserId: this.toNumberOrNull(this.form.managerUserId)
    };

    const call = this.id
      ? this.branchApi.update(this.id, request)
      : this.branchApi.create(request);

    call.subscribe({
      next: () => {
        this.saving = false;

        Swal.fire({
          icon: 'success',
          title: this.id ? 'Updated' : 'Created',
          text: this.id ? 'Branch updated successfully.' : 'Branch created successfully.',
          timer: 1400,
          showConfirmButton: false
        });

        this.router.navigate(['/admin/branches']);
      },
      error: err => {
        console.error(err);
        this.saving = false;

        Swal.fire({
          icon: 'error',
          title: 'Save failed',
          text: err?.error?.message || 'Please check branch information and try again.'
        });
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/branches']);
  }

  getUserImageUrl(userId?: number | null): string {
    if (!userId) return '';
    return this.userImageMap[String(userId)] || '';
  }

  getUserDisplay(userId?: number | null): string {
    if (!userId) return '-';
    return this.userDisplayMap[userId] || `USER-${userId}`;
  }

  private validate(): boolean {
    if (!this.form.branchName?.trim()) {
      Swal.fire('Validation', 'Branch name is required.', 'warning');
      return false;
    }

    if (!this.form.branchType) {
      Swal.fire('Validation', 'Branch type is required.', 'warning');
      return false;
    }

    if (!this.form.routingNo?.trim()) {
      Swal.fire('Validation', 'Routing number is required.', 'warning');
      return false;
    }

    if (!this.form.addressLine1?.trim()) {
      Swal.fire('Validation', 'Address Line 1 is required.', 'warning');
      return false;
    }

    if (!this.form.openedDate) {
      Swal.fire('Validation', 'Opened date is required.', 'warning');
      return false;
    }

    return true;
  }

  private loadUsers(): void {
    this.userApi.getAll().subscribe({
      next: users => {
        this.userImageMap = this.buildUserImageMap(users || []);
      },
      error: () => {
        this.userImageMap = {};
      }
    });
  }

  private buildUserImageMap(users: UserResponse[]): Record<string, string> {
    return users.reduce<Record<string, string>>((acc, user) => {
      if (user.id && user.profileImageName) {
        acc[String(user.id)] = this.fileUploadService.resolveImageUrl(user.profileImageName);
      }
      if (user.id) {
        this.userDisplayMap[user.id] = user.fullName || user.username;
      }
      return acc;
    }, {});
  }

  private toNumberOrNull(value: any): number | null {
    if (value === null || value === undefined || value === '') return null;
    return Number(value);
  }
}
