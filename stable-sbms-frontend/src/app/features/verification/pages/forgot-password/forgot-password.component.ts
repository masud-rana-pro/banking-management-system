import { Component } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { ForgotPasswordRequest, VerificationLogResponse } from '../../models/verification.model';
import { VerificationService } from '../../services/verification.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {

  loading = false;
  result: VerificationLogResponse | null = null;
  form: ForgotPasswordRequest = {
    identifier: '',
    channelType: 'EMAIL'
  };

  constructor(
    private verificationService: VerificationService,
    private router: Router
  ) {}

  submit(): void {
    if (!this.form.identifier.trim()) {
      Swal.fire('Warning', 'Identifier is required.', 'warning');
      return;
    }
    this.loading = true;
    this.verificationService.forgotPassword(this.form).subscribe({
      next: data => {
        this.result = data;
        this.loading = false;
        Swal.fire('Sent', 'Password reset OTP sent successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'Failed to send password reset OTP.', 'error');
      }
    });
  }

  openReset(): void {
    this.router.navigate(['/verification/reset-password'], {
      queryParams: this.result ? { requestId: this.result.id } : {}
    });
  }
}
