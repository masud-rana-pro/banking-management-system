import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { withAccessToken } from 'src/app/core/utils/authenticated-url.util';

import {
  ApiResponse,
  ProfitDashboardSummaryResponse,
  ProfitPostingResponse,
  ProfitPostingRunRequest,
  ProfitPostingRunResponse,
  ProfitRatioDropdownResponse,
  ProfitRatioRequest,
  ProfitRatioResponse,
  ProfitScheduleRequest,
  ProfitScheduleResponse
} from '../models/profit.model';

@Injectable({
  providedIn: 'root'
})
export class ProfitService {

  private readonly ratioUrl = `${environment.apiBaseUrl}/profit-ratios`;
  private readonly scheduleUrl = `${environment.apiBaseUrl}/profit-schedules`;
  private readonly postingUrl = `${environment.apiBaseUrl}/profit-postings`;
  private readonly dashboardUrl = `${environment.apiBaseUrl}/profit`;

  constructor(private http: HttpClient) {}

  createRatio(request: ProfitRatioRequest): Observable<ProfitRatioResponse> {
    return this.http.post<ApiResponse<ProfitRatioResponse>>(`${this.ratioUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  getRatios(): Observable<ProfitRatioResponse[]> {
    return this.http.get<ApiResponse<ProfitRatioResponse[]>>(`${this.ratioUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getRatioById(id: number): Observable<ProfitRatioResponse> {
    return this.http.get<ApiResponse<ProfitRatioResponse>>(`${this.ratioUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  updateRatio(id: number, request: ProfitRatioRequest): Observable<ProfitRatioResponse> {
    return this.http.put<ApiResponse<ProfitRatioResponse>>(`${this.ratioUrl}/${id}`, request)
      .pipe(map(res => res.data));
  }

  archiveRatio(id: number): Observable<ProfitRatioResponse> {
    return this.http.delete<ApiResponse<ProfitRatioResponse>>(`${this.ratioUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  restoreRatio(id: number): Observable<ProfitRatioResponse> {
    return this.http.put<ApiResponse<ProfitRatioResponse>>(`${this.ratioUrl}/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  ratioDropdown(): Observable<ProfitRatioDropdownResponse[]> {
    return this.http.get<ApiResponse<ProfitRatioDropdownResponse[]>>(`${this.ratioUrl}/dropdown`)
      .pipe(map(res => res.data || []));
  }

  createSchedule(request: ProfitScheduleRequest): Observable<ProfitScheduleResponse> {
    return this.http.post<ApiResponse<ProfitScheduleResponse>>(`${this.scheduleUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  getSchedules(): Observable<ProfitScheduleResponse[]> {
    return this.http.get<ApiResponse<ProfitScheduleResponse[]>>(`${this.scheduleUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getScheduleById(id: number): Observable<ProfitScheduleResponse> {
    return this.http.get<ApiResponse<ProfitScheduleResponse>>(`${this.scheduleUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  archiveSchedule(id: number): Observable<ProfitScheduleResponse> {
    return this.http.delete<ApiResponse<ProfitScheduleResponse>>(`${this.scheduleUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  restoreSchedule(id: number): Observable<ProfitScheduleResponse> {
    return this.http.put<ApiResponse<ProfitScheduleResponse>>(`${this.scheduleUrl}/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  runPosting(request: ProfitPostingRunRequest): Observable<ProfitPostingRunResponse> {
    return this.http.post<ApiResponse<ProfitPostingRunResponse>>(`${this.postingUrl}/run`, request)
      .pipe(map(res => res.data));
  }

  getPostings(): Observable<ProfitPostingResponse[]> {
    return this.http.get<ApiResponse<ProfitPostingResponse[]>>(`${this.postingUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getPostingById(id: number): Observable<ProfitPostingResponse> {
    return this.http.get<ApiResponse<ProfitPostingResponse>>(`${this.postingUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  getPostingAdvicePreviewUrl(id: number): string {
    return withAccessToken(`${this.postingUrl}/${id}/advice/preview`);
  }

  getPostingAdviceDownloadUrl(id: number): string {
    return withAccessToken(`${this.postingUrl}/${id}/advice/download`);
  }

  getDashboardSummary(): Observable<ProfitDashboardSummaryResponse> {
    return this.http.get<ApiResponse<ProfitDashboardSummaryResponse>>(`${this.dashboardUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }
}
