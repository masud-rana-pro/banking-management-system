import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  CalculationDashboardSummaryResponse,
  CalculationSimulateRequest,
  CalculationSimulationResponse
} from '../models/calculation.model';

@Injectable({
  providedIn: 'root'
})
export class CalculationService {

  private readonly apiUrl = `${environment.apiBaseUrl}/calculations`;

  constructor(private http: HttpClient) {}

  getDashboardSummary(): Observable<CalculationDashboardSummaryResponse> {
    return this.http.get<ApiResponse<CalculationDashboardSummaryResponse>>(`${this.apiUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  simulate(request: CalculationSimulateRequest): Observable<CalculationSimulationResponse> {
    return this.http.post<ApiResponse<CalculationSimulationResponse>>(`${this.apiUrl}/simulate`, request)
      .pipe(map(res => res.data));
  }
}
