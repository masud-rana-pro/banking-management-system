import { Component } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AuthLoginResponse, AuthService } from 'src/app/core/services/auth.service';
import { AccessControlService } from 'src/app/core/services/access-control.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  credentials = { username: '', password: '' };
  otpChannelPreference: 'EMAIL' = 'EMAIL';
  otpCode = '';
  rememberMe = false;
  showPassword = false;
  loading = false;
  errorMsg = '';
  loginChallenge: AuthLoginResponse | null = null;

  constructor(
    private router: Router,
    private authService: AuthService,
    private accessControl: AccessControlService
  ) {
    if (this.accessControl.session) {
      this.router.navigate([this.accessControl.getLandingRoute()]);
    }
  }

  onLogin(): void {
    this.errorMsg = '';
    const username = (this.credentials.username || '').trim();
    const password = this.credentials.password || '';
    if (!username || !password) {
      this.errorMsg = 'Please enter your username and password.';
      return;
    }

    this.loading = true;

    this.authService.login({
      username,
      password,
      rememberMe: this.rememberMe,
      otpChannelPreference: this.otpChannelPreference
    }).subscribe({
      next: response => {
        this.loading = false;
        this.loginChallenge = response;
        this.otpCode = '';
        Swal.fire({
          icon: 'info',
          title: this.resolveOtpDialogTitle(response),
          text: this.resolveOtpDialogText(response),
          confirmButtonText: 'Continue'
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        this.errorMsg = err?.error?.message || 'Failed to sign in.';
      }
    });
  }

  onVerifyOtp(): void {
    this.errorMsg = '';
    if (!this.loginChallenge?.otpRequestId || !this.otpCode.trim()) {
      this.errorMsg = 'Please enter the OTP code.';
      return;
    }

    this.loading = true;
    this.authService.verifyLoginOtp({
      requestId: this.loginChallenge.otpRequestId,
      otpCode: this.otpCode.trim(),
      rememberMe: this.rememberMe
    }).subscribe({
      next: session => {
        this.accessControl.setSession(session);
        this.loading = false;
        Swal.fire({
          icon: 'success',
          title: 'Welcome back!',
          text: `Signed in as ${session.fullName || session.username} (${session.roleCode})`,
          timer: 1500,
          showConfirmButton: false,
          position: 'top-end',
          toast: true
        }).then(() => {
          this.router.navigate([session.mustChangePassword ? '/auth/change-password' : this.accessControl.getLandingRoute()]);
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        this.errorMsg = err?.error?.message || 'Failed to verify login OTP.';
      }
    });
  }

  resendOtp(): void {
    if (!this.loginChallenge?.otpRequestId) {
      return;
    }
    this.loading = true;
    this.authService.resendLoginOtp(this.loginChallenge.otpRequestId).subscribe({
      next: response => {
        this.loginChallenge = response;
        this.loading = false;
        Swal.fire({
          icon: 'success',
          title: 'OTP Resent',
          text: this.resolveOtpResendText(response),
          timer: 1600,
          showConfirmButton: false,
          position: 'top-end',
          toast: true
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        this.errorMsg = err?.error?.message || 'Failed to resend OTP.';
      }
    });
  }

  backToCredentials(): void {
    this.loginChallenge = null;
    this.otpCode = '';
    this.errorMsg = '';
  }

  openForgotPassword(event?: Event): void {
    event?.preventDefault();
    this.router.navigate(['/verification/forgot-password']);
  }

  getOtpInstructionText(): string {
    return this.resolveOtpDialogText(this.loginChallenge);
  }

  private resolveOtpDialogTitle(response: AuthLoginResponse | null): string {
    const channel = (response?.otpChannelType || 'EMAIL').toUpperCase();
    return channel === 'SMS' ? 'Mobile OTP required' : 'Email OTP required';
  }

  private resolveOtpDialogText(response: AuthLoginResponse | null): string {
    const destination = response?.otpDestinationMasked || 'your registered contact';
    const channel = (response?.otpChannelType || 'EMAIL').toUpperCase();
    const username = (this.credentials.username || '').trim() || 'this account';
    return channel === 'SMS'
      ? `Enter the OTP sent to the registered mobile number for ${username}: ${destination}.`
      : `Enter the OTP sent to the registered email address for ${username}: ${destination}.`;
  }

  private resolveOtpResendText(response: AuthLoginResponse | null): string {
    const destination = response?.otpDestinationMasked || 'your registered contact';
    const channel = (response?.otpChannelType || 'EMAIL').toUpperCase();
    return channel === 'SMS'
      ? `A fresh OTP was sent to the registered mobile number: ${destination}.`
      : `A fresh OTP was sent to the registered email address: ${destination}.`;
  }
}


