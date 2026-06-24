import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { RoleResponse } from '../../roles/model/role.model';
import { RoleApiService } from '../../roles/service/role-api.service';
import { BranchOption, UserCreateRequest, UserUpdateRequest } from '../model/user.model';
import { UserApiService } from '../service/user-api.service';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss']
})
export class UserFormComponent implements OnInit {

  isEdit = false;
  userId?: number;
  roles: RoleResponse[] = [];
  branches: BranchOption[] = [];
  loading = false;
  saving = false;
  uploadingImage = false;
  profileImagePreviewUrl = '';

  form = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    fullName: ['', [Validators.required, Validators.minLength(3)]],
    email: [''],
    mobile: [''],
    profileImageName: [''],
    employeeNo: [''],
    designation: [''],
    branchId: [null as number | null],
    roleId: [null as number | null, [Validators.required]],
    status: ['ACTIVE', Validators.required],
    userType: ['STAFF', Validators.required],
    password: [''],
    confirmPassword: [''],
    emailVerified: [false],
    mobileVerified: [false]
  });

  constructor(
    private fb: FormBuilder,
    private roleApi: RoleApiService,
    private userApi: UserApiService,
    private route: ActivatedRoute,
    private router: Router,
    private access: AccessControlService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.isEdit = true;
      this.userId = id;
    }
    this.setPasswordValidators();
    this.loadDropdowns();
    if (this.userId) this.loadUser(this.userId);
  }

  private setPasswordValidators(): void {
    if (this.isEdit) {
      this.form.get('password')?.clearValidators();
      this.form.get('confirmPassword')?.clearValidators();
    } else {
      this.form.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
      this.form.get('confirmPassword')?.setValidators([Validators.required, Validators.minLength(6)]);
    }
    this.form.get('password')?.updateValueAndValidity();
    this.form.get('confirmPassword')?.updateValueAndValidity();
  }

  loadDropdowns(): void {
    this.roleApi.getDropdown().subscribe({ next: data => this.roles = data || [] });
    this.userApi.getBranchDropdown().subscribe({ next: data => this.branches = (data || []).filter(item => item.status === 'ACTIVE') });
  }

  loadUser(id: number): void {
    this.loading = true;
    this.userApi.getById(id).subscribe({
      next: user => {
        this.form.patchValue({
          username: user.username,
          fullName: user.fullName,
          email: user.email || '',
          mobile: user.mobile || '',
          profileImageName: user.profileImageName || '',
          employeeNo: user.employeeNo || '',
          designation: user.designation || '',
          branchId: user.branchId || null,
          roleId: user.roleId || null,
          status: user.status,
          userType: user.userType,
          emailVerified: !!user.emailVerified,
          mobileVerified: !!user.mobileVerified
        });
        this.profileImagePreviewUrl = this.fileUploadService.resolveImageUrl(user.profileImageName);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load user detail.', 'error');
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const password = this.form.get('password')?.value || '';
    const confirmPassword = this.form.get('confirmPassword')?.value || '';
    if (!this.isEdit && password !== confirmPassword) {
      Swal.fire('Error', 'Password and confirm password must match.', 'error');
      return;
    }

    this.saving = true;
    if (this.isEdit && this.userId) {
      const payload: UserUpdateRequest = {
        username: this.form.value.username || '',
        fullName: this.form.value.fullName || '',
        email: this.form.value.email || '',
        mobile: this.form.value.mobile || '',
        profileImageName: this.form.value.profileImageName || '',
        employeeNo: this.form.value.employeeNo || '',
        designation: this.form.value.designation || '',
        branchId: this.form.value.branchId ?? null,
        roleId: this.form.value.roleId ?? null,
        status: this.form.value.status || 'ACTIVE',
        userType: this.form.value.userType || 'STAFF',
        emailVerified: !!this.form.value.emailVerified,
        mobileVerified: !!this.form.value.mobileVerified,
        actionBy: this.access.session?.username || 'SYSTEM'
      };
      this.userApi.update(this.userId, payload).subscribe({
        next: user => {
          const currentSession = this.access.session;
          if (currentSession?.userId === user.id) {
            this.access.setSession({
              ...currentSession,
              username: user.username,
              fullName: user.fullName,
              profileImageName: user.profileImageName || null,
              branchId: user.branchId ?? currentSession.branchId ?? null,
              branchName: user.branchName || currentSession.branchName || null,
              rememberMe: currentSession.rememberMe
            });
          }
          this.saving = false;
          Swal.fire('Success', 'User updated successfully.', 'success');
          this.router.navigate(['/admin/users', user.id]);
        },
        error: err => {
          console.error(err);
          this.saving = false;
          Swal.fire('Error', err?.error?.message || 'Failed to update user.', 'error');
        }
      });
      return;
    }

    const payload: UserCreateRequest = {
      username: this.form.value.username || '',
      fullName: this.form.value.fullName || '',
      email: this.form.value.email || '',
      mobile: this.form.value.mobile || '',
      profileImageName: this.form.value.profileImageName || '',
      employeeNo: this.form.value.employeeNo || '',
      designation: this.form.value.designation || '',
      branchId: this.form.value.branchId ?? null,
      roleId: this.form.value.roleId as number,
      status: this.form.value.status || 'ACTIVE',
      userType: this.form.value.userType || 'STAFF',
      password,
      actionBy: this.access.session?.username || 'SYSTEM'
    };
    this.userApi.create(payload).subscribe({
      next: user => {
        this.saving = false;
        Swal.fire('Success', 'User created successfully.', 'success');
        this.router.navigate(['/admin/users', user.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to create user.', 'error');
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
        this.form.patchValue({ profileImageName: result.fileName });
        this.profileImagePreviewUrl = this.fileUploadService.resolveImageUrl(result.fileName);
        this.uploadingImage = false;
        input.value = '';
      },
      error: err => {
        console.error(err);
        this.uploadingImage = false;
        input.value = '';
        Swal.fire('Error', err?.error?.message || 'Failed to upload user image.', 'error');
      }
    });
  }

  hasError(controlName: string, errorName: string): boolean {
    const c = this.form.get(controlName);
    return !!(c && c.touched && c.hasError(errorName));
  }

  onCancel(): void {
    this.router.navigate(['/admin/users']);
  }
}
