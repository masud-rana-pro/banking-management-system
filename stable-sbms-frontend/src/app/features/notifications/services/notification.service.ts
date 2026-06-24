import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  NotificationDashboardSummaryResponse,
  NotificationEventRequest,
  NotificationEventResponse,
  NotificationLogResponse,
  NotificationTemplateRequest,
  NotificationTemplateResponse
} from '../models/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private readonly baseUrl = `${environment.apiBaseUrl}/notifications`;

  constructor(private http: HttpClient) {}

  getDashboardSummary(): Observable<NotificationDashboardSummaryResponse> {
    return this.http.get<ApiResponse<NotificationDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  getTemplates(): Observable<NotificationTemplateResponse[]> {
    return this.http.get<ApiResponse<NotificationTemplateResponse[]>>(`${this.baseUrl}/templates/list`)
      .pipe(map(res => res.data || []));
  }

  getTemplateById(id: number): Observable<NotificationTemplateResponse> {
    return this.http.get<ApiResponse<NotificationTemplateResponse>>(`${this.baseUrl}/templates/${id}`)
      .pipe(map(res => res.data));
  }

  createTemplate(request: NotificationTemplateRequest): Observable<NotificationTemplateResponse> {
    return this.http.post<ApiResponse<NotificationTemplateResponse>>(`${this.baseUrl}/templates/create`, request)
      .pipe(map(res => res.data));
  }

  updateTemplate(id: number, request: NotificationTemplateRequest): Observable<NotificationTemplateResponse> {
    return this.http.put<ApiResponse<NotificationTemplateResponse>>(`${this.baseUrl}/templates/${id}`, request)
      .pipe(map(res => res.data));
  }

  archiveTemplate(id: number): Observable<NotificationTemplateResponse> {
    return this.http.delete<ApiResponse<NotificationTemplateResponse>>(`${this.baseUrl}/templates/${id}`)
      .pipe(map(res => res.data));
  }

  restoreTemplate(id: number): Observable<NotificationTemplateResponse> {
    return this.http.put<ApiResponse<NotificationTemplateResponse>>(`${this.baseUrl}/templates/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  getEventRules(): Observable<NotificationEventResponse[]> {
    return this.http.get<ApiResponse<NotificationEventResponse[]>>(`${this.baseUrl}/event-rules/list`)
      .pipe(map(res => res.data || []));
  }

  createEventRule(request: NotificationEventRequest): Observable<NotificationEventResponse> {
    return this.http.post<ApiResponse<NotificationEventResponse>>(`${this.baseUrl}/event-rules/create`, request)
      .pipe(map(res => res.data));
  }

  getLogs(filters?: { deliveryStatus?: string; channelType?: string; keyword?: string }): Observable<NotificationLogResponse[]> {
    let params = new HttpParams();
    if (filters?.deliveryStatus?.trim()) params = params.set('deliveryStatus', filters.deliveryStatus.trim());
    if (filters?.channelType?.trim()) params = params.set('channelType', filters.channelType.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());

    return this.http.get<ApiResponse<NotificationLogResponse[]>>(`${this.baseUrl}/logs/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getLogById(id: number): Observable<NotificationLogResponse> {
    return this.http.get<ApiResponse<NotificationLogResponse>>(`${this.baseUrl}/logs/${id}`)
      .pipe(map(res => res.data));
  }

  retryLog(id: number): Observable<NotificationLogResponse> {
    return this.http.post<ApiResponse<NotificationLogResponse>>(`${this.baseUrl}/logs/${id}/retry`, {})
      .pipe(map(res => res.data));
  }
}
