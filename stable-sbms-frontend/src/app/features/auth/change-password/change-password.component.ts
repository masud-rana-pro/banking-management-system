import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AuthService } from 'src/app/core/services/auth.service';
import { AccessControlService } from 'src/app/core/services/access-control.service';

function passwordsMatch(group: AbstractControl): ValidationErrors | null {
  const np = group.get('newPassword')?.value;
  const cp = group.get('confirmPassword')?.value;
  return np && cp && np !== cp ? { mismatch: true } : null;
}

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.page.html',
  styleUrls: ['./change-password.page.scss']
})
export class ChangePasswordComponent {

  form: FormGroup;
  saving = false;
  showCurrent = false;
  showNew     = false;
  showConfirm = false;
  readonly inShellLayout: boolean;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private accessControl: AccessControlService,
    private router: Router
  ) {
    this.inShellLayout = this.router.url.startsWith('/account/');
    this.form = this.fb.group(
      {
        currentPassword: ['', Validators.required],
        newPassword:     ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required]
      },
      { validators: passwordsMatch }
    );
  }

  get strengthClass(): string {
    const val: string = this.form.get('newPassword')?.value || '';
    if (val.length < 6)  return 'weak';
    if (val.length < 10) return 'fair';
    return 'strong';
  }

  get strengthLabel(): string {
    const m = this.strengthClass;
    if (m === 'weak')   return 'Weak';
    if (m === 'fair')   return 'Fair';
    return 'Strong';
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    this.saving = true;

    this.authService.changePassword({
      currentPassword: this.form.value.currentPassword,
      newPassword: this.form.value.newPassword,
      confirmPassword: this.form.value.confirmPassword
    }).subscribe({
      next: session => {
        this.accessControl.setSession({ ...session, rememberMe: this.accessControl.session?.rememberMe });
        this.saving = false;
        Swal.fire({
          icon: 'success',
          title: 'Password Updated',
          text: 'Your password has been changed successfully.',
          timer: 2200,
          showConfirmButton: false,
          position: 'top-end',
          toast: true
        }).then(() => {
          this.router.navigate([this.accessControl.getLandingRoute()]);
        });
      },
      error: err => {
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to update password.', 'error');
      }
    });
  }

  resetForm(): void { this.form.reset(); }
}
