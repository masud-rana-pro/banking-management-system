import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  ShariahChecklistItemResponse,
  ShariahChecklistSaveRequest,
  ShariahDashboardSummaryResponse,
  ShariahDecisionRequest,
  ShariahReviewCaseRequest,
  ShariahReviewCaseResponse,
  ShariahReviewDecisionResponse
} from '../models/shariah.model';

@Injectable({
  providedIn: 'root'
})
export class ShariahService {

  private readonly baseUrl = `${environment.apiBaseUrl}/shariah-reviews`;

  constructor(private http: HttpClient) {}

  createCase(request: ShariahReviewCaseRequest): Observable<ShariahReviewCaseResponse> {
    return this.http.post<ApiResponse<ShariahReviewCaseResponse>>(`${this.baseUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  getCases(filters?: { referenceModule?: string; caseStatus?: string; keyword?: string }): Observable<ShariahReviewCaseResponse[]> {
    let params = new HttpParams();
    if (filters?.referenceModule?.trim()) params = params.set('referenceModule', filters.referenceModule.trim());
    if (filters?.caseStatus?.trim()) params = params.set('caseStatus', filters.caseStatus.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());

    return this.http.get<ApiResponse<ShariahReviewCaseResponse[]>>(`${this.baseUrl}/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getCaseById(id: number): Observable<ShariahReviewCaseResponse> {
    return this.http.get<ApiResponse<ShariahReviewCaseResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  getChecklistItems(): Observable<ShariahChecklistItemResponse[]> {
    return this.http.get<ApiResponse<ShariahChecklistItemResponse[]>>(`${this.baseUrl}/checklist-items`)
      .pipe(map(res => res.data || []));
  }

  saveChecklist(id: number, request: ShariahChecklistSaveRequest): Observable<ShariahReviewCaseResponse> {
    return this.http.post<ApiResponse<ShariahReviewCaseResponse>>(`${this.baseUrl}/${id}/checklist`, request)
      .pipe(map(res => res.data));
  }

  approve(id: number, request: ShariahDecisionRequest): Observable<ShariahReviewCaseResponse> {
    return this.http.post<ApiResponse<ShariahReviewCaseResponse>>(`${this.baseUrl}/${id}/approve`, request)
      .pipe(map(res => res.data));
  }

  approveWithStepUp(id: number, request: ShariahDecisionRequest, stepUpToken: string): Observable<ShariahReviewCaseResponse> {
    return this.http.post<ApiResponse<ShariahReviewCaseResponse>>(`${this.baseUrl}/${id}/approve`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  reject(id: number, request: ShariahDecisionRequest): Observable<ShariahReviewCaseResponse> {
    return this.http.post<ApiResponse<ShariahReviewCaseResponse>>(`${this.baseUrl}/${id}/reject`, request)
      .pipe(map(res => res.data));
  }

  rejectWithStepUp(id: number, request: ShariahDecisionRequest, stepUpToken: string): Observable<ShariahReviewCaseResponse> {
    return this.http.post<ApiResponse<ShariahReviewCaseResponse>>(`${this.baseUrl}/${id}/reject`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  returnCase(id: number, request: ShariahDecisionRequest): Observable<ShariahReviewCaseResponse> {
    return this.http.post<ApiResponse<ShariahReviewCaseResponse>>(`${this.baseUrl}/${id}/return`, request)
      .pipe(map(res => res.data));
  }

  returnCaseWithStepUp(id: number, request: ShariahDecisionRequest, stepUpToken: string): Observable<ShariahReviewCaseResponse> {
    return this.http.post<ApiResponse<ShariahReviewCaseResponse>>(`${this.baseUrl}/${id}/return`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  getHistory(id: number): Observable<ShariahReviewDecisionResponse[]> {
    return this.http.get<ApiResponse<ShariahReviewDecisionResponse[]>>(`${this.baseUrl}/${id}/history`)
      .pipe(map(res => res.data || []));
  }

  getDashboardSummary(): Observable<ShariahDashboardSummaryResponse> {
    return this.http.get<ApiResponse<ShariahDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }
}
