import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { withAccessToken } from 'src/app/core/utils/authenticated-url.util';
import { ApiResponse } from '../models/branch.model';
import { VaultBalanceRequest, VaultBalanceResponse, VaultCloseRequest } from '../models/vault-balance.model';

@Injectable({ providedIn: 'root' })
export class VaultBalanceApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/branches/vault`;

  constructor(private http: HttpClient) {}

  open(payload: VaultBalanceRequest): Observable<VaultBalanceResponse> {
    return this.http
      .post<ApiResponse<VaultBalanceResponse>>(`${this.baseUrl}/open`, payload)
      .pipe(map(res => res.data));
  }

  close(id: number, payload: VaultCloseRequest): Observable<VaultBalanceResponse> {
    return this.http
      .post<ApiResponse<VaultBalanceResponse>>(`${this.baseUrl}/${id}/close`, payload)
      .pipe(map(res => res.data));
  }

  getAll(branchId: number | null = null, status: string = '', isClosed: boolean | null = null): Observable<VaultBalanceResponse[]> {
    let params = new HttpParams();

    if (branchId && branchId > 0) params = params.set('branchId', String(branchId));
    if (status?.trim()) params = params.set('status', status);
    if (isClosed !== null) params = params.set('isClosed', String(isClosed));

    return this.http
      .get<ApiResponse<VaultBalanceResponse[]>>(`${this.baseUrl}/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getById(id: number): Observable<VaultBalanceResponse> {
    return this.http
      .get<ApiResponse<VaultBalanceResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  getReportPreviewUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/${id}/report/preview`);
  }

  getReportDownloadUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/${id}/report/download`);
  }
}
