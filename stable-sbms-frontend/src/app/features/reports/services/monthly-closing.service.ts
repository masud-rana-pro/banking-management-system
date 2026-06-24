import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import { ApiResponse } from '../../branch/models/branch.model';
import {
  MonthlyClosingDashboardSummaryResponse,
  MonthlyClosingDecisionRequest,
  MonthlyClosingRunRequest,
  MonthlyClosingRunResponse
} from '../models/monthly-closing.model';

@Injectable({
  providedIn: 'root'
})
export class MonthlyClosingService {

  private readonly baseUrl = `${environment.apiBaseUrl}/monthly-closing-runs`;

  constructor(private http: HttpClient) {}

  createOrRefresh(request: MonthlyClosingRunRequest): Observable<MonthlyClosingRunResponse> {
    return this.http.post<ApiResponse<MonthlyClosingRunResponse>>(`${this.baseUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  list(filters?: { branchId?: number | null; status?: string; closingMonth?: string | null }): Observable<MonthlyClosingRunResponse[]> {
    let params = new HttpParams();
    if (filters?.branchId) params = params.set('branchId', String(filters.branchId));
    if (filters?.status?.trim()) params = params.set('status', filters.status.trim());
    if (filters?.closingMonth?.trim()) params = params.set('closingMonth', filters.closingMonth.trim());
    return this.http.get<ApiResponse<MonthlyClosingRunResponse[]>>(`${this.baseUrl}/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getById(id: number): Observable<MonthlyClosingRunResponse> {
    return this.http.get<ApiResponse<MonthlyClosingRunResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  submit(id: number): Observable<MonthlyClosingRunResponse> {
    return this.http.post<ApiResponse<MonthlyClosingRunResponse>>(`${this.baseUrl}/${id}/submit`, {})
      .pipe(map(res => res.data));
  }

  approve(id: number, request: MonthlyClosingDecisionRequest): Observable<MonthlyClosingRunResponse> {
    return this.http.post<ApiResponse<MonthlyClosingRunResponse>>(`${this.baseUrl}/${id}/approve`, request)
      .pipe(map(res => res.data));
  }

  reject(id: number, request: MonthlyClosingDecisionRequest): Observable<MonthlyClosingRunResponse> {
    return this.http.post<ApiResponse<MonthlyClosingRunResponse>>(`${this.baseUrl}/${id}/reject`, request)
      .pipe(map(res => res.data));
  }

  reopen(id: number, request: MonthlyClosingDecisionRequest): Observable<MonthlyClosingRunResponse> {
    return this.http.post<ApiResponse<MonthlyClosingRunResponse>>(`${this.baseUrl}/${id}/reopen`, request)
      .pipe(map(res => res.data));
  }

  getDashboardSummary(): Observable<MonthlyClosingDashboardSummaryResponse> {
    return this.http.get<ApiResponse<MonthlyClosingDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }
}

