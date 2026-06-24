import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, finalize, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

import { AccessControlService, AccessSession } from './access-control.service';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface AuthLoginRequest {
  username: string;
  password: string;
  rememberMe?: boolean;
  otpChannelPreference?: 'AUTO' | 'EMAIL' | 'SMS';
}

export interface AuthLoginOtpVerifyRequest {
  requestId: number;
  otpCode: string;
  rememberMe?: boolean;
}

export interface AuthSessionResponse {
  token: string;
  userId: number;
  username: string;
  fullName: string;
  profileImageName?: string | null;
  roleId: number;
  roleCode: string;
  roleName: string;
  branchId?: number | null;
  branchName?: string | null;
  mustChangePassword?: boolean;
  permissions: string[];
  loginAt?: string | null;
}

export interface AuthLoginResponse {
  otpRequired: boolean;
  otpRequestId?: number | null;
  otpChannelType?: string | null;
  otpDestinationMasked?: string | null;
  otpExpiresAt?: string | null;
  session?: AuthSessionResponse | null;
}

export interface AuthChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly authUrl = `${environment.apiBaseUrl}/auth`;

  constructor(
    private http: HttpClient,
    private accessControl: AccessControlService
  ) {}

  login(payload: AuthLoginRequest): Observable<AuthLoginResponse> {
    return this.http.post<ApiResponse<AuthLoginResponse>>(`${this.authUrl}/login`, payload).pipe(
      map(res => res.data)
    );
  }

  verifyLoginOtp(payload: AuthLoginOtpVerifyRequest): Observable<AccessSession> {
    return this.http.post<ApiResponse<AuthLoginResponse>>(`${this.authUrl}/verify-login-otp`, payload).pipe(
      map(res => {
        if (!res.data?.session) {
          throw new Error('Authenticated session was not returned');
        }
        return this.toAccessSession(res.data.session, Boolean(payload.rememberMe));
      })
    );
  }

  resendLoginOtp(requestId: number): Observable<AuthLoginResponse> {
    return this.http.post<ApiResponse<AuthLoginResponse>>(`${this.authUrl}/resend-login-otp/${requestId}`, {}).pipe(
      map(res => res.data)
    );
  }

  me(): Observable<AccessSession> {
    return this.http.get<ApiResponse<AuthSessionResponse>>(`${this.authUrl}/me`).pipe(
      map(res => this.toAccessSession(res.data, this.accessControl.session?.rememberMe ?? false))
    );
  }

  getOnlineUsers(): Observable<string[]> {
    return this.http.get<ApiResponse<string[]>>(`${this.authUrl}/online-users`).pipe(
      map(res => res.data || [])
    );
  }

  changePassword(payload: AuthChangePasswordRequest): Observable<AccessSession> {
    return this.http.post<ApiResponse<AuthSessionResponse>>(`${this.authUrl}/change-password`, payload).pipe(
      map(res => this.toAccessSession(res.data, this.accessControl.session?.rememberMe ?? false))
    );
  }

  logout(): Observable<void> {
    return this.http.post<ApiResponse<null>>(`${this.authUrl}/logout`, {}).pipe(
      map(() => void 0),
      catchError(() => of(void 0)),
      finalize(() => this.accessControl.clearSession())
    );
  }

  private toAccessSession(data: AuthSessionResponse, rememberMe: boolean): AccessSession {
    return {
      token: data.token,
      userId: data.userId,
      username: data.username,
      fullName: data.fullName,
      profileImageName: data.profileImageName,
      roleId: data.roleId,
      roleCode: data.roleCode,
      roleName: data.roleName,
      branchId: data.branchId,
      branchName: data.branchName,
      mustChangePassword: !!data.mustChangePassword,
      permissions: data.permissions || [],
      rememberMe
    };
  }
}


