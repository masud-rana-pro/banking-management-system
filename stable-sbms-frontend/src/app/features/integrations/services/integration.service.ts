import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  IntegrationDashboardSummaryResponse,
  IntegrationExecutionLogResponse,
  IntegrationProviderRequest,
  IntegrationProviderResponse,
  IntegrationProviderTestRequest
} from '../models/integration.model';

@Injectable({
  providedIn: 'root'
})
export class IntegrationService {

  private readonly baseUrl = `${environment.apiBaseUrl}/integrations`;

  constructor(private http: HttpClient) {}

  getDashboardSummary(): Observable<IntegrationDashboardSummaryResponse> {
    return this.http.get<ApiResponse<IntegrationDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  getProviders(filters?: { providerType?: string; status?: string; keyword?: string }): Observable<IntegrationProviderResponse[]> {
    let params = new HttpParams();
    if (filters?.providerType?.trim()) params = params.set('providerType', filters.providerType.trim());
    if (filters?.status?.trim()) params = params.set('status', filters.status.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());
    return this.http.get<ApiResponse<IntegrationProviderResponse[]>>(`${this.baseUrl}/providers/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getProviderById(id: number): Observable<IntegrationProviderResponse> {
    return this.http.get<ApiResponse<IntegrationProviderResponse>>(`${this.baseUrl}/providers/${id}`)
      .pipe(map(res => res.data));
  }

  createProvider(request: IntegrationProviderRequest): Observable<IntegrationProviderResponse> {
    return this.http.post<ApiResponse<IntegrationProviderResponse>>(`${this.baseUrl}/providers/create`, request)
      .pipe(map(res => res.data));
  }

  updateProvider(id: number, request: IntegrationProviderRequest): Observable<IntegrationProviderResponse> {
    return this.http.put<ApiResponse<IntegrationProviderResponse>>(`${this.baseUrl}/providers/${id}`, request)
      .pipe(map(res => res.data));
  }

  archiveProvider(id: number): Observable<IntegrationProviderResponse> {
    return this.http.delete<ApiResponse<IntegrationProviderResponse>>(`${this.baseUrl}/providers/${id}`)
      .pipe(map(res => res.data));
  }

  restoreProvider(id: number): Observable<IntegrationProviderResponse> {
    return this.http.put<ApiResponse<IntegrationProviderResponse>>(`${this.baseUrl}/providers/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  testProvider(id: number, request?: IntegrationProviderTestRequest): Observable<IntegrationExecutionLogResponse> {
    return this.http.post<ApiResponse<IntegrationExecutionLogResponse>>(`${this.baseUrl}/providers/${id}/test`, request || {})
      .pipe(map(res => res.data));
  }

  getLogs(filters?: { providerId?: number | null; executionStatus?: string; keyword?: string }): Observable<IntegrationExecutionLogResponse[]> {
    let params = new HttpParams();
    if (filters?.providerId) params = params.set('providerId', String(filters.providerId));
    if (filters?.executionStatus?.trim()) params = params.set('executionStatus', filters.executionStatus.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());
    return this.http.get<ApiResponse<IntegrationExecutionLogResponse[]>>(`${this.baseUrl}/logs/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getLogById(id: number): Observable<IntegrationExecutionLogResponse> {
    return this.http.get<ApiResponse<IntegrationExecutionLogResponse>>(`${this.baseUrl}/logs/${id}`)
      .pipe(map(res => res.data));
  }

  retryLog(id: number): Observable<IntegrationExecutionLogResponse> {
    return this.http.post<ApiResponse<IntegrationExecutionLogResponse>>(`${this.baseUrl}/logs/${id}/retry`, {})
      .pipe(map(res => res.data));
  }
}
