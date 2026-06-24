import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';

import { ResetPasswordRequest, VerificationLogResponse } from '../../models/verification.model';
import { VerificationService } from '../../services/verification.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  loading = false;
  result: VerificationLogResponse | null = null;
  form: ResetPasswordRequest = {
    requestId: null,
    otpCode: '',
    newPassword: '',
    confirmPassword: ''
  };

  constructor(
    private route: ActivatedRoute,
    private verificationService: VerificationService
  ) {}

  ngOnInit(): void {
    const requestId = this.route.snapshot.queryParamMap.get('requestId');
    this.form.requestId = requestId ? Number(requestId) : null;
  }

  submit(): void {
    if (!this.form.requestId || !this.form.otpCode.trim() || !this.form.newPassword.trim() || !this.form.confirmPassword.trim()) {
      Swal.fire('Warning', 'All fields are required.', 'warning');
      return;
    }
    this.loading = true;
    this.verificationService.resetPassword(this.form).subscribe({
      next: data => {
        this.result = data;
        this.loading = false;
        Swal.fire('Success', 'Password reset completed successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'Failed to reset password.', 'error');
      }
    });
  }
}
