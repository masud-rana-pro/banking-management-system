import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  AuditLogResponse,
  InvestigationCaseActionRequest,
  InvestigationCaseResponse,
  SecurityDashboardSummaryResponse,
  SecurityEventResponse
} from '../models/security.model';

@Injectable({
  providedIn: 'root'
})
export class SecurityService {

  private readonly baseUrl = `${environment.apiBaseUrl}/security`;

  constructor(private http: HttpClient) {}

  getDashboardSummary(): Observable<SecurityDashboardSummaryResponse> {
    return this.http.get<ApiResponse<SecurityDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  getSecurityEvents(filters?: { severityLevel?: string; keyword?: string }): Observable<SecurityEventResponse[]> {
    let params = new HttpParams();
    if (filters?.severityLevel?.trim()) params = params.set('severityLevel', filters.severityLevel.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());
    return this.http.get<ApiResponse<SecurityEventResponse[]>>(`${this.baseUrl}/events/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getSecurityEventById(id: number): Observable<SecurityEventResponse> {
    return this.http.get<ApiResponse<SecurityEventResponse>>(`${this.baseUrl}/events/${id}`)
      .pipe(map(res => res.data));
  }

  getSuspiciousActivities(filters?: { keyword?: string }): Observable<SecurityEventResponse[]> {
    let params = new HttpParams();
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());
    return this.http.get<ApiResponse<SecurityEventResponse[]>>(`${this.baseUrl}/suspicious-activities/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getSuspiciousActivityById(id: number): Observable<SecurityEventResponse> {
    return this.http.get<ApiResponse<SecurityEventResponse>>(`${this.baseUrl}/suspicious-activities/${id}`)
      .pipe(map(res => res.data));
  }

  getAuditLogs(filters?: { moduleName?: string; keyword?: string }): Observable<AuditLogResponse[]> {
    let params = new HttpParams();
    if (filters?.moduleName?.trim()) params = params.set('moduleName', filters.moduleName.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());
    return this.http.get<ApiResponse<AuditLogResponse[]>>(`${this.baseUrl}/audit-logs/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getAuditLogById(id: number): Observable<AuditLogResponse> {
    return this.http.get<ApiResponse<AuditLogResponse>>(`${this.baseUrl}/audit-logs/${id}`)
      .pipe(map(res => res.data));
  }

  getInvestigationCases(filters?: { caseStatus?: string; caseType?: string; keyword?: string }): Observable<InvestigationCaseResponse[]> {
    let params = new HttpParams();
    if (filters?.caseStatus?.trim()) params = params.set('caseStatus', filters.caseStatus.trim());
    if (filters?.caseType?.trim()) params = params.set('caseType', filters.caseType.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());
    return this.http.get<ApiResponse<InvestigationCaseResponse[]>>(`${this.baseUrl}/investigation-cases/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getInvestigationCaseById(id: number): Observable<InvestigationCaseResponse> {
    return this.http.get<ApiResponse<InvestigationCaseResponse>>(`${this.baseUrl}/investigation-cases/${id}`)
      .pipe(map(res => res.data));
  }

  assignInvestigationCase(id: number, request: InvestigationCaseActionRequest): Observable<InvestigationCaseResponse> {
    return this.http.post<ApiResponse<InvestigationCaseResponse>>(`${this.baseUrl}/investigation-cases/${id}/assign`, request)
      .pipe(map(res => res.data));
  }

  closeInvestigationCase(id: number, request: InvestigationCaseActionRequest): Observable<InvestigationCaseResponse> {
    return this.http.post<ApiResponse<InvestigationCaseResponse>>(`${this.baseUrl}/investigation-cases/${id}/close`, request)
      .pipe(map(res => res.data));
  }
}
