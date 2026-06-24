import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { withAccessToken } from 'src/app/core/utils/authenticated-url.util';

import {
  ApiResponse,
  FinancingApplicationRequest,
  FinancingApplicationResponse,
  FinancingDashboardSummaryResponse,
  FinancingDisbursementRequest,
  FinancingProductRequest,
  FinancingProductResponse,
  FinancingRepaymentCollectionRequest,
  FinancingRepaymentCollectionResponse,
  FinancingScheduleResponse,
  FinancingVerifyRequest,
  FinancingWorkflowActionRequest
} from '../models/financing.model';

@Injectable({
  providedIn: 'root'
})
export class FinancingService {

  private readonly productUrl = `${environment.apiBaseUrl}/financing-products`;
  private readonly applicationUrl = `${environment.apiBaseUrl}/financing-applications`;
  private readonly dashboardUrl = `${environment.apiBaseUrl}/financing`;

  constructor(private http: HttpClient) {}

  createProduct(request: FinancingProductRequest): Observable<FinancingProductResponse> {
    return this.http.post<ApiResponse<FinancingProductResponse>>(`${this.productUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  getProducts(): Observable<FinancingProductResponse[]> {
    return this.http.get<ApiResponse<FinancingProductResponse[]>>(`${this.productUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getProductById(id: number): Observable<FinancingProductResponse> {
    return this.http.get<ApiResponse<FinancingProductResponse>>(`${this.productUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  updateProduct(id: number, request: FinancingProductRequest): Observable<FinancingProductResponse> {
    return this.http.put<ApiResponse<FinancingProductResponse>>(`${this.productUrl}/${id}`, request)
      .pipe(map(res => res.data));
  }

  archiveProduct(id: number): Observable<FinancingProductResponse> {
    return this.http.delete<ApiResponse<FinancingProductResponse>>(`${this.productUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  restoreProduct(id: number): Observable<FinancingProductResponse> {
    return this.http.put<ApiResponse<FinancingProductResponse>>(`${this.productUrl}/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  createApplication(request: FinancingApplicationRequest): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  getApplications(filters?: { productId?: number | null; customerId?: number | null; branchId?: number | null; keyword?: string }): Observable<FinancingApplicationResponse[]> {
    let params = new HttpParams();
    if (filters?.productId) params = params.set('productId', String(filters.productId));
    if (filters?.customerId) params = params.set('customerId', String(filters.customerId));
    if (filters?.branchId) params = params.set('branchId', String(filters.branchId));
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());

    return this.http.get<ApiResponse<FinancingApplicationResponse[]>>(`${this.applicationUrl}/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getApplicationById(id: number): Observable<FinancingApplicationResponse> {
    return this.http.get<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  getSanctionLetterPreviewUrl(id: number): string {
    return withAccessToken(`${this.applicationUrl}/${id}/sanction-letter/preview`);
  }

  getSanctionLetterDownloadUrl(id: number): string {
    return withAccessToken(`${this.applicationUrl}/${id}/sanction-letter/download`);
  }

  updateApplication(id: number, request: FinancingApplicationRequest): Observable<FinancingApplicationResponse> {
    return this.http.put<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}`, request)
      .pipe(map(res => res.data));
  }

  submitApplication(id: number, request: FinancingWorkflowActionRequest): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/submit`, request)
      .pipe(map(res => res.data));
  }

  verifyApplication(id: number, request: FinancingVerifyRequest): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/verify`, request)
      .pipe(map(res => res.data));
  }

  reviewApplication(id: number, request: FinancingWorkflowActionRequest): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/review`, request)
      .pipe(map(res => res.data));
  }

  approveApplication(id: number, request: FinancingWorkflowActionRequest): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/approve`, request)
      .pipe(map(res => res.data));
  }

  approveApplicationWithStepUp(id: number, request: FinancingWorkflowActionRequest, stepUpToken: string): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/approve`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  rejectApplication(id: number, request: FinancingWorkflowActionRequest): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/reject`, request)
      .pipe(map(res => res.data));
  }

  rejectApplicationWithStepUp(id: number, request: FinancingWorkflowActionRequest, stepUpToken: string): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/reject`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  returnApplication(id: number, request: FinancingWorkflowActionRequest): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/return`, request)
      .pipe(map(res => res.data));
  }

  returnApplicationWithStepUp(id: number, request: FinancingWorkflowActionRequest, stepUpToken: string): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/return`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  archiveApplication(id: number): Observable<FinancingApplicationResponse> {
    return this.http.delete<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  restoreApplication(id: number): Observable<FinancingApplicationResponse> {
    return this.http.put<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  disburseApplication(id: number, request: FinancingDisbursementRequest): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/disburse`, request)
      .pipe(map(res => res.data));
  }

  disburseApplicationWithStepUp(id: number, request: FinancingDisbursementRequest, stepUpToken: string): Observable<FinancingApplicationResponse> {
    return this.http.post<ApiResponse<FinancingApplicationResponse>>(`${this.applicationUrl}/${id}/disburse`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  getSchedule(id: number): Observable<FinancingScheduleResponse[]> {
    return this.http.get<ApiResponse<FinancingScheduleResponse[]>>(`${this.applicationUrl}/${id}/schedule`)
      .pipe(map(res => res.data || []));
  }

  collectPayment(id: number, request: FinancingRepaymentCollectionRequest): Observable<FinancingRepaymentCollectionResponse> {
    return this.http.post<ApiResponse<FinancingRepaymentCollectionResponse>>(`${this.applicationUrl}/${id}/collect-payment`, request)
      .pipe(map(res => res.data));
  }

  collectPaymentWithStepUp(id: number, request: FinancingRepaymentCollectionRequest, stepUpToken: string): Observable<FinancingRepaymentCollectionResponse> {
    return this.http.post<ApiResponse<FinancingRepaymentCollectionResponse>>(`${this.applicationUrl}/${id}/collect-payment`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  getDashboardSummary(): Observable<FinancingDashboardSummaryResponse> {
    return this.http.get<ApiResponse<FinancingDashboardSummaryResponse>>(`${this.dashboardUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }
}
