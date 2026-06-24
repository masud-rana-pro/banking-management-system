import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { withAccessToken } from 'src/app/core/utils/authenticated-url.util';

import {
  ApiResponse,
  DepositSchemeDashboardSummaryResponse,
  DepositSchemeEnrollmentRequest,
  DepositSchemeEnrollmentResponse,
  DepositSchemeProfitDistributionResponse,
  DepositSchemeRequest,
  DepositSchemeResponse,
  DepositSchemeScheduleResponse
} from '../models/deposit-scheme.model';

@Injectable({
  providedIn: 'root'
})
export class DepositSchemeService {

  private readonly baseUrl = `${environment.apiBaseUrl}/deposit-schemes`;

  constructor(private http: HttpClient) {}

  createScheme(request: DepositSchemeRequest): Observable<DepositSchemeResponse> {
    return this.http.post<ApiResponse<DepositSchemeResponse>>(`${this.baseUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  getSchemes(): Observable<DepositSchemeResponse[]> {
    return this.http.get<ApiResponse<DepositSchemeResponse[]>>(`${this.baseUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getSchemeById(id: number): Observable<DepositSchemeResponse> {
    return this.http.get<ApiResponse<DepositSchemeResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  updateScheme(id: number, request: DepositSchemeRequest): Observable<DepositSchemeResponse> {
    return this.http.put<ApiResponse<DepositSchemeResponse>>(`${this.baseUrl}/${id}`, request)
      .pipe(map(res => res.data));
  }

  archiveScheme(id: number): Observable<DepositSchemeResponse> {
    return this.http.delete<ApiResponse<DepositSchemeResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  restoreScheme(id: number): Observable<DepositSchemeResponse> {
    return this.http.put<ApiResponse<DepositSchemeResponse>>(`${this.baseUrl}/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  createEnrollment(request: DepositSchemeEnrollmentRequest): Observable<DepositSchemeEnrollmentResponse> {
    return this.http.post<ApiResponse<DepositSchemeEnrollmentResponse>>(`${this.baseUrl}/enrollment/create`, request)
      .pipe(map(res => res.data));
  }

  getEnrollments(filters?: { schemeId?: number | null; customerId?: number | null; accountId?: number | null }): Observable<DepositSchemeEnrollmentResponse[]> {
    let params = new HttpParams();
    if (filters?.schemeId) params = params.set('schemeId', String(filters.schemeId));
    if (filters?.customerId) params = params.set('customerId', String(filters.customerId));
    if (filters?.accountId) params = params.set('accountId', String(filters.accountId));

    return this.http.get<ApiResponse<DepositSchemeEnrollmentResponse[]>>(`${this.baseUrl}/enrollment/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getEnrollmentById(id: number): Observable<DepositSchemeEnrollmentResponse> {
    return this.http.get<ApiResponse<DepositSchemeEnrollmentResponse>>(`${this.baseUrl}/enrollment/${id}`)
      .pipe(map(res => res.data));
  }

  getEnrollmentCertificatePreviewUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/enrollment/${id}/certificate/preview`);
  }

  getEnrollmentCertificateDownloadUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/enrollment/${id}/certificate/download`);
  }

  getEnrollmentSchedule(id: number): Observable<DepositSchemeScheduleResponse[]> {
    return this.http.get<ApiResponse<DepositSchemeScheduleResponse[]>>(`${this.baseUrl}/enrollment/${id}/schedule`)
      .pipe(map(res => res.data || []));
  }

  getEnrollmentProfitDistribution(id: number): Observable<DepositSchemeProfitDistributionResponse[]> {
    return this.http.get<ApiResponse<DepositSchemeProfitDistributionResponse[]>>(`${this.baseUrl}/enrollment/${id}/profit`)
      .pipe(map(res => res.data || []));
  }

  getDashboardSummary(): Observable<DepositSchemeDashboardSummaryResponse> {
    return this.http.get<ApiResponse<DepositSchemeDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }
}
