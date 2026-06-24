import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { VerificationOtpVerifyRequest, VerificationLogResponse, formatEnumLabel } from '../../models/verification.model';
import { VerificationService } from '../../services/verification.service';

@Component({
  selector: 'app-otp-verify',
  templateUrl: './otp-verify.component.html',
  styleUrls: ['./otp-verify.component.scss']
})
export class OtpVerifyComponent implements OnInit {

  loading = false;
  result: VerificationLogResponse | null = null;
  form: VerificationOtpVerifyRequest = {
    requestId: null,
    otpCode: '',
    ipAddress: '',
    deviceInfo: 'Verification Console',
    createdBy: 'SYSTEM'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private verificationService: VerificationService
  ) {}

  ngOnInit(): void {
    const requestId = this.route.snapshot.queryParamMap.get('requestId');
    this.form.requestId = requestId ? Number(requestId) : null;
  }

  submit(): void {
    if (!this.form.requestId || !this.form.otpCode.trim()) {
      Swal.fire('Warning', 'Request id and OTP code are required.', 'warning');
      return;
    }
    this.loading = true;
    this.verificationService.verifyOtp(this.form).subscribe({
      next: data => {
        this.result = data;
        this.loading = false;
        Swal.fire('Verified', 'OTP verified successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'OTP verification failed.', 'error');
      }
    });
  }

  openLogs(): void {
    this.router.navigate(['/verification/logs']);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
