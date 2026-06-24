import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from '../model/user.model';
import { UserApiService } from '../service/user-api.service';

@Component({
  selector: 'app-user-password-reset',
  templateUrl: './user-password-reset.component.html',
  styleUrls: ['./user-password-reset.component.scss']
})
export class UserPasswordResetComponent implements OnInit {
  user?: UserResponse;
  loading = false;
  saving = false;

  form = this.fb.group({
    newPassword: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required, Validators.minLength(6)]]
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private api: UserApiService,
    private access: AccessControlService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) return;
    this.loading = true;
    this.api.getById(id).subscribe({
      next: user => {
        this.user = user;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load user detail.', 'error');
      }
    });
  }

  save(): void {
    if (!this.user || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    this.api.resetPassword(this.user.id, {
      newPassword: this.form.value.newPassword || '',
      confirmPassword: this.form.value.confirmPassword || '',
      actionBy: this.access.session?.username || 'SYSTEM'
    }).subscribe({
      next: user => {
        this.saving = false;
        Swal.fire('Success', 'User password reset successfully.', 'success');
        this.router.navigate(['/admin/users', user.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to reset password.', 'error');
      }
    });
  }

  back(): void {
    if (this.user) this.router.navigate(['/admin/users', this.user.id]);
    else this.router.navigate(['/admin/users']);
  }

  getProfileImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }
}
