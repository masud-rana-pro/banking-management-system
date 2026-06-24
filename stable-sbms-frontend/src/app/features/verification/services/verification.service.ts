import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  ForgotPasswordRequest,
  ProviderTestRequest,
  ResetPasswordRequest,
  VerificationChannelResponse,
  VerificationContactStatusResponse,
  VerificationDashboardSummaryResponse,
  VerificationLogResponse,
  VerificationOtpVerifyRequest,
  VerificationSendOtpRequest,
  VerificationTemplateResponse
} from '../models/verification.model';

@Injectable({
  providedIn: 'root'
})
export class VerificationService {

  private readonly adminUrl = `${environment.apiBaseUrl}/verifications`;
  private readonly authUrl = `${environment.apiBaseUrl}/auth`;

  constructor(private http: HttpClient) {}

  getDashboardSummary(): Observable<VerificationDashboardSummaryResponse> {
    return this.http.get<ApiResponse<VerificationDashboardSummaryResponse>>(`${this.adminUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  getLogs(filters?: { channelType?: string; status?: string; keyword?: string }): Observable<VerificationLogResponse[]> {
    let params = new HttpParams();
    if (filters?.channelType?.trim()) params = params.set('channelType', filters.channelType.trim());
    if (filters?.status?.trim()) params = params.set('status', filters.status.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());
    return this.http.get<ApiResponse<VerificationLogResponse[]>>(`${this.adminUrl}/logs`, { params })
      .pipe(map(res => res.data || []));
  }

  getChannels(): Observable<VerificationChannelResponse[]> {
    return this.http.get<ApiResponse<VerificationChannelResponse[]>>(`${this.adminUrl}/channels`)
      .pipe(map(res => res.data || []));
  }

  getTemplates(): Observable<VerificationTemplateResponse[]> {
    return this.http.get<ApiResponse<VerificationTemplateResponse[]>>(`${this.adminUrl}/templates`)
      .pipe(map(res => res.data || []));
  }

  getContactStatuses(referenceModule?: string, referenceId?: number | null): Observable<VerificationContactStatusResponse[]> {
    let params = new HttpParams();
    if (referenceModule?.trim()) params = params.set('referenceModule', referenceModule.trim());
    if (referenceId != null) params = params.set('referenceId', String(referenceId));
    return this.http.get<ApiResponse<VerificationContactStatusResponse[]>>(`${this.adminUrl}/contact-status`, { params })
      .pipe(map(res => res.data || []));
  }

  sendEmailVerificationOtp(request: VerificationSendOtpRequest): Observable<VerificationLogResponse> {
    return this.http.post<ApiResponse<VerificationLogResponse>>(`${this.adminUrl}/send-email-verification-otp`, request)
      .pipe(map(res => res.data));
  }

  sendMobileVerificationOtp(request: VerificationSendOtpRequest): Observable<VerificationLogResponse> {
    return this.http.post<ApiResponse<VerificationLogResponse>>(`${this.adminUrl}/send-mobile-verification-otp`, request)
      .pipe(map(res => res.data));
  }

  verifyOtp(request: VerificationOtpVerifyRequest): Observable<VerificationLogResponse> {
    return this.http.post<ApiResponse<VerificationLogResponse>>(`${this.adminUrl}/verify-otp`, request)
      .pipe(map(res => res.data));
  }

  resendOtp(requestId: number): Observable<VerificationLogResponse> {
    return this.http.post<ApiResponse<VerificationLogResponse>>(`${this.adminUrl}/resend-otp/${requestId}`, {})
      .pipe(map(res => res.data));
  }

  expireOtp(requestId: number): Observable<VerificationLogResponse> {
    return this.http.post<ApiResponse<VerificationLogResponse>>(`${this.adminUrl}/expire-otp/${requestId}`, {})
      .pipe(map(res => res.data));
  }

  markFailed(requestId: number): Observable<VerificationLogResponse> {
    return this.http.post<ApiResponse<VerificationLogResponse>>(`${this.adminUrl}/mark-failed/${requestId}`, {})
      .pipe(map(res => res.data));
  }

  providerTest(request: ProviderTestRequest): Observable<VerificationLogResponse> {
    return this.http.post<ApiResponse<VerificationLogResponse>>(`${this.adminUrl}/provider-test`, request)
      .pipe(map(res => res.data));
  }

  forgotPassword(request: ForgotPasswordRequest): Observable<VerificationLogResponse> {
    return this.http.post<ApiResponse<VerificationLogResponse>>(`${this.authUrl}/forgot-password`, request)
      .pipe(map(res => res.data));
  }

  resetPassword(request: ResetPasswordRequest): Observable<VerificationLogResponse> {
    return this.http.post<ApiResponse<VerificationLogResponse>>(`${this.authUrl}/reset-password`, request)
      .pipe(map(res => res.data));
  }
}
