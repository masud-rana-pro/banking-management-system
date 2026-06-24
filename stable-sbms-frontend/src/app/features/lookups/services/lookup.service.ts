import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  LookupDashboardSummaryResponse,
  LookupTypeRequest,
  LookupTypeResponse,
  LookupValueRequest,
  LookupValueResponse
} from '../models/lookup.model';

@Injectable({
  providedIn: 'root'
})
export class LookupService {

  private readonly baseUrl = `${environment.apiBaseUrl}/lookups`;

  constructor(private http: HttpClient) {}

  getDashboardSummary(): Observable<LookupDashboardSummaryResponse> {
    return this.http.get<ApiResponse<LookupDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  listTypes(): Observable<LookupTypeResponse[]> {
    return this.http.get<ApiResponse<LookupTypeResponse[]>>(`${this.baseUrl}/types/list`)
      .pipe(map(res => res.data || []));
  }

  getTypeById(id: number): Observable<LookupTypeResponse> {
    return this.http.get<ApiResponse<LookupTypeResponse>>(`${this.baseUrl}/types/${id}`)
      .pipe(map(res => res.data));
  }

  createType(payload: LookupTypeRequest): Observable<LookupTypeResponse> {
    return this.http.post<ApiResponse<LookupTypeResponse>>(`${this.baseUrl}/types/create`, payload)
      .pipe(map(res => res.data));
  }

  updateType(id: number, payload: LookupTypeRequest): Observable<LookupTypeResponse> {
    return this.http.put<ApiResponse<LookupTypeResponse>>(`${this.baseUrl}/types/${id}`, payload)
      .pipe(map(res => res.data));
  }

  archiveType(id: number): Observable<LookupTypeResponse> {
    return this.http.delete<ApiResponse<LookupTypeResponse>>(`${this.baseUrl}/types/${id}`)
      .pipe(map(res => res.data));
  }

  restoreType(id: number): Observable<LookupTypeResponse> {
    return this.http.put<ApiResponse<LookupTypeResponse>>(`${this.baseUrl}/types/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  listValues(filters?: { lookupTypeId?: number | null; keyword?: string | null }): Observable<LookupValueResponse[]> {
    let params = new HttpParams();
    if (filters?.lookupTypeId) params = params.set('lookupTypeId', filters.lookupTypeId);
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());
    return this.http.get<ApiResponse<LookupValueResponse[]>>(`${this.baseUrl}/values/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getValueById(id: number): Observable<LookupValueResponse> {
    return this.http.get<ApiResponse<LookupValueResponse>>(`${this.baseUrl}/values/${id}`)
      .pipe(map(res => res.data));
  }

  createValue(payload: LookupValueRequest): Observable<LookupValueResponse> {
    return this.http.post<ApiResponse<LookupValueResponse>>(`${this.baseUrl}/values/create`, payload)
      .pipe(map(res => res.data));
  }

  updateValue(id: number, payload: LookupValueRequest): Observable<LookupValueResponse> {
    return this.http.put<ApiResponse<LookupValueResponse>>(`${this.baseUrl}/values/${id}`, payload)
      .pipe(map(res => res.data));
  }

  archiveValue(id: number): Observable<LookupValueResponse> {
    return this.http.delete<ApiResponse<LookupValueResponse>>(`${this.baseUrl}/values/${id}`)
      .pipe(map(res => res.data));
  }

  restoreValue(id: number): Observable<LookupValueResponse> {
    return this.http.put<ApiResponse<LookupValueResponse>>(`${this.baseUrl}/values/${id}/restore`, {})
      .pipe(map(res => res.data));
  }
}
