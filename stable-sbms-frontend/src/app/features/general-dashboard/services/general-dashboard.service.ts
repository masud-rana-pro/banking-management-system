import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import { ApiResponse, GeneralDashboardResponse } from '../models/general-dashboard.model';

export interface GeneralDashboardQuery {
  branchId?: number | null;
  dateFrom?: string | null;
  dateTo?: string | null;
  window?: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class GeneralDashboardService {
  private readonly dashboardUrl = `${environment.apiBaseUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  getOverview(query: GeneralDashboardQuery): Observable<GeneralDashboardResponse> {
    let params = new HttpParams();
    if (query.branchId != null) {
      params = params.set('branchId', String(query.branchId));
    }
    if (query.dateFrom) {
      params = params.set('dateFrom', query.dateFrom);
    }
    if (query.dateTo) {
      params = params.set('dateTo', query.dateTo);
    }
    if (query.window) {
      params = params.set('window', query.window);
    }

    return this.http.get<ApiResponse<GeneralDashboardResponse>>(`${this.dashboardUrl}/general-overview`, { params })
      .pipe(map(res => res.data));
  }
}
