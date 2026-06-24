import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';
import { firstValueFrom, map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface StepUpChallengeRequest {
  actionCode: string;
  targetModule: string;
  targetId?: number | null;
  channelPreference?: 'EMAIL' | 'SMS' | 'AUTO';
  remarks?: string | null;
}

export interface StepUpChallengeResponse {
  requestId: number;
  actionCode: string;
  targetModule: string;
  targetId?: number | null;
  otpChannelType: string;
  otpDestinationMasked: string;
  otpExpiresAt?: string | null;
}

export interface StepUpVerifyRequest {
  requestId: number;
  actionCode: string;
  targetModule: string;
  targetId?: number | null;
  otpCode: string;
}

export interface StepUpVerifyResponse {
  verificationToken: string;
  actionCode: string;
  targetModule: string;
  targetId?: number | null;
  verifiedAt?: string | null;
  tokenExpiresAt?: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class StepUpVerificationService {

  private readonly baseUrl = `${environment.apiBaseUrl}/verifications/step-up`;

  constructor(private http: HttpClient) {}

  requestChallenge(payload: StepUpChallengeRequest): Observable<StepUpChallengeResponse> {
    return this.http.post<ApiResponse<StepUpChallengeResponse>>(`${this.baseUrl}/request`, payload)
      .pipe(map(res => res.data));
  }

  resendChallenge(requestId: number): Observable<StepUpChallengeResponse> {
    return this.http.post<ApiResponse<StepUpChallengeResponse>>(`${this.baseUrl}/resend/${requestId}`, {})
      .pipe(map(res => res.data));
  }

  verifyChallenge(payload: StepUpVerifyRequest): Observable<StepUpVerifyResponse> {
    return this.http.post<ApiResponse<StepUpVerifyResponse>>(`${this.baseUrl}/verify`, payload)
      .pipe(map(res => res.data));
  }

  async completeStepUp(payload: StepUpChallengeRequest, actionLabel: string): Promise<string | null> {
    let challenge = await firstValueFrom(this.requestChallenge(payload));

    while (true) {
      const result = await Swal.fire({
        title: `${actionLabel} OTP`,
        text: `OTP sent to ${challenge.otpDestinationMasked}. Enter the code to continue.`,
        input: 'text',
        inputLabel: 'One-time password',
        inputPlaceholder: 'Enter the 6-digit OTP',
        showCancelButton: true,
        showDenyButton: true,
        confirmButtonText: 'Verify OTP',
        denyButtonText: 'Resend OTP',
        cancelButtonText: 'Cancel',
        inputAttributes: {
          autocomplete: 'one-time-code'
        },
        preConfirm: async (otpCode) => {
          if (!otpCode || !String(otpCode).trim()) {
            Swal.showValidationMessage('OTP code is required.');
            return;
          }
          try {
            return await firstValueFrom(this.verifyChallenge({
              requestId: challenge.requestId,
              actionCode: challenge.actionCode,
              targetModule: challenge.targetModule,
              targetId: challenge.targetId,
              otpCode: String(otpCode).trim()
            }));
          } catch (err: any) {
            Swal.showValidationMessage(err?.error?.message || 'Failed to verify OTP.');
            return null;
          }
        }
      });

      if (result.isDismissed) {
        return null;
      }

      if (result.isDenied) {
        try {
          challenge = await firstValueFrom(this.resendChallenge(challenge.requestId));
          await Swal.fire({
            icon: 'success',
            title: 'OTP Resent',
            text: `A fresh OTP was sent to ${challenge.otpDestinationMasked}.`,
            timer: 1600,
            showConfirmButton: false,
            position: 'top-end',
            toast: true
          });
        } catch (err: any) {
          await Swal.fire('Error', err?.error?.message || 'Failed to resend OTP.', 'error');
          return null;
        }
        continue;
      }

      return result.value?.verificationToken || null;
    }
  }
}
