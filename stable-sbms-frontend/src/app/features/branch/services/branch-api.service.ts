import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import { ApiResponse, BranchDashboardSummaryResponse, BranchRequest, BranchResponse } from '../models/branch.model';

@Injectable({
  providedIn: 'root'
})
export class BranchApiService {

  private readonly baseUrl = `${environment.apiBaseUrl}/branches`;

  constructor(private http: HttpClient) {}

  create(request: BranchRequest): Observable<ApiResponse<BranchResponse>> {
    return this.http.post<ApiResponse<BranchResponse>>(`${this.baseUrl}/create`, request);
  }

  update(id: number, request: BranchRequest): Observable<ApiResponse<BranchResponse>> {
    return this.http.put<ApiResponse<BranchResponse>>(`${this.baseUrl}/${id}`, request);
  }

  getById(id: number): Observable<BranchResponse> {
    return this.http
      .get<ApiResponse<BranchResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  getAll(search: string = '', status: string = ''): Observable<BranchResponse[]> {
    let params = new HttpParams();

    if (search) params = params.set('search', search);
    if (status) params = params.set('status', status);

    return this.http
      .get<ApiResponse<BranchResponse[]>>(`${this.baseUrl}/list`, { params })
      .pipe(map(res => res.data || []));
  }

  dropdown(): Observable<BranchResponse[]> {
    return this.http
      .get<ApiResponse<BranchResponse[]>>(`${this.baseUrl}/dropdown`)
      .pipe(map(res => res.data || []));
  }

  getDashboardSummary(): Observable<BranchDashboardSummaryResponse> {
    return this.http
      .get<ApiResponse<BranchDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  archive(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }

  restore(id: number): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.baseUrl}/${id}/restore`, {});
  }
}
