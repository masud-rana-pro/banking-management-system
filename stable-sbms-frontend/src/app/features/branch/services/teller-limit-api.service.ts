import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../models/branch.model';
import { TellerLimitRequest, TellerLimitResponse } from '../models/teller-limit.model';

@Injectable({ providedIn: 'root' })
export class TellerLimitApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/branches/teller-limit`;

  constructor(private http: HttpClient) {}

  getAll(
    status: string = '',
    branchId: number | null = null,
    userId: number | null = null
  ): Observable<TellerLimitResponse[]> {
    let params = new HttpParams();

    if (status?.trim()) params = params.set('status', status);
    if (branchId && branchId > 0) params = params.set('branchId', String(branchId));
    if (userId && userId > 0) params = params.set('userId', String(userId));

    return this.http
      .get<ApiResponse<TellerLimitResponse[]>>(`${this.baseUrl}/list`, { params })
      .pipe(map(res => res.data || []));
  }

  create(payload: TellerLimitRequest): Observable<TellerLimitResponse> {
    return this.http
      .post<ApiResponse<TellerLimitResponse>>(`${this.baseUrl}/create`, payload)
      .pipe(map(res => res.data));
  }

  update(id: number, payload: TellerLimitRequest): Observable<TellerLimitResponse> {
    return this.http
      .put<ApiResponse<TellerLimitResponse>>(`${this.baseUrl}/${id}`, payload)
      .pipe(map(res => res.data));
  }

  deactivate(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
