import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { withAccessToken } from 'src/app/core/utils/authenticated-url.util';
import { ApiResponse } from '../models/branch.model';
import { BranchCashLedgerResponse } from '../models/branch-cash-ledger.model';

@Injectable({ providedIn: 'root' })
export class BranchCashLedgerApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/branches/cash-ledger`;

  constructor(private http: HttpClient) {}

  getAll(branchId: number | null = null, entryType: string = '', sourceType: string = ''): Observable<BranchCashLedgerResponse[]> {
    let params = new HttpParams();

    if (branchId && branchId > 0) params = params.set('branchId', String(branchId));
    if (entryType?.trim()) params = params.set('entryType', entryType);
    if (sourceType?.trim()) params = params.set('sourceType', sourceType);

    return this.http
      .get<ApiResponse<BranchCashLedgerResponse[]>>(this.baseUrl, { params })
      .pipe(map(res => res.data || []));
  }

  getReportPreviewUrl(branchId: number | null = null, entryType: string = '', sourceType: string = ''): string {
    const params = new URLSearchParams();
    if (branchId && branchId > 0) params.set('branchId', String(branchId));
    if (entryType?.trim()) params.set('entryType', entryType);
    if (sourceType?.trim()) params.set('sourceType', sourceType);
    const query = params.toString();
    return withAccessToken(`${this.baseUrl}/report/preview${query ? '?' + query : ''}`);
  }

  getReportDownloadUrl(branchId: number | null = null, entryType: string = '', sourceType: string = ''): string {
    const params = new URLSearchParams();
    if (branchId && branchId > 0) params.set('branchId', String(branchId));
    if (entryType?.trim()) params.set('entryType', entryType);
    if (sourceType?.trim()) params.set('sourceType', sourceType);
    const query = params.toString();
    return withAccessToken(`${this.baseUrl}/report/download${query ? '?' + query : ''}`);
  }
}
