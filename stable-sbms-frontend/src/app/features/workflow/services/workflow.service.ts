import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  WorkflowDashboardSummaryResponse,
  WorkflowHistoryResponse
} from '../models/workflow.model';

@Injectable({
  providedIn: 'root'
})
export class WorkflowService {

  private readonly baseUrl = `${environment.apiBaseUrl}/workflows`;

  constructor(private http: HttpClient) {}

  getDashboardSummary(actor?: string | null): Observable<WorkflowDashboardSummaryResponse> {
    let params = new HttpParams();
    if (actor?.trim()) params = params.set('actor', actor.trim());
    return this.http.get<ApiResponse<WorkflowDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`, { params })
      .pipe(map(res => res.data));
  }

  getHistory(filters?: { moduleName?: string; keyword?: string }): Observable<WorkflowHistoryResponse[]> {
    let params = new HttpParams();
    if (filters?.moduleName?.trim()) params = params.set('moduleName', filters.moduleName.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());
    return this.http.get<ApiResponse<WorkflowHistoryResponse[]>>(`${this.baseUrl}/history/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getHistoryById(id: number): Observable<WorkflowHistoryResponse> {
    return this.http.get<ApiResponse<WorkflowHistoryResponse>>(`${this.baseUrl}/history/${id}`)
      .pipe(map(res => res.data));
  }

  getPending(keyword?: string | null): Observable<WorkflowHistoryResponse[]> {
    let params = new HttpParams();
    if (keyword?.trim()) params = params.set('keyword', keyword.trim());
    return this.http.get<ApiResponse<WorkflowHistoryResponse[]>>(`${this.baseUrl}/pending`, { params })
      .pipe(map(res => res.data || []));
  }

  getMySubmissions(actionBy?: string | null): Observable<WorkflowHistoryResponse[]> {
    let params = new HttpParams();
    if (actionBy?.trim()) params = params.set('actionBy', actionBy.trim());
    return this.http.get<ApiResponse<WorkflowHistoryResponse[]>>(`${this.baseUrl}/my-submissions`, { params })
      .pipe(map(res => res.data || []));
  }
}
