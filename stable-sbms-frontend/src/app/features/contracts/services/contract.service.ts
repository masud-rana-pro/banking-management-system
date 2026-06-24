import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { withAccessToken } from 'src/app/core/utils/authenticated-url.util';

import {
  ApiResponse,
  ContractDashboardSummaryResponse,
  ContractGenerateRequest,
  ContractResponse,
  ContractSignRequest,
  ContractTemplateRequest,
  ContractTemplateResponse,
  ContractVersionResponse
} from '../models/contract.model';

@Injectable({
  providedIn: 'root'
})
export class ContractService {

  private readonly baseUrl = `${environment.apiBaseUrl}/contracts`;

  constructor(private http: HttpClient) {}

  createTemplate(request: ContractTemplateRequest): Observable<ContractTemplateResponse> {
    return this.http.post<ApiResponse<ContractTemplateResponse>>(`${this.baseUrl}/templates/create`, request)
      .pipe(map(res => res.data));
  }

  getTemplates(): Observable<ContractTemplateResponse[]> {
    return this.http.get<ApiResponse<ContractTemplateResponse[]>>(`${this.baseUrl}/templates/list`)
      .pipe(map(res => res.data || []));
  }

  getTemplateById(id: number): Observable<ContractTemplateResponse> {
    return this.http.get<ApiResponse<ContractTemplateResponse>>(`${this.baseUrl}/templates/${id}`)
      .pipe(map(res => res.data));
  }

  updateTemplate(id: number, request: ContractTemplateRequest): Observable<ContractTemplateResponse> {
    return this.http.put<ApiResponse<ContractTemplateResponse>>(`${this.baseUrl}/templates/${id}`, request)
      .pipe(map(res => res.data));
  }

  archiveTemplate(id: number): Observable<ContractTemplateResponse> {
    return this.http.delete<ApiResponse<ContractTemplateResponse>>(`${this.baseUrl}/templates/${id}`)
      .pipe(map(res => res.data));
  }

  restoreTemplate(id: number): Observable<ContractTemplateResponse> {
    return this.http.put<ApiResponse<ContractTemplateResponse>>(`${this.baseUrl}/templates/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  generate(request: ContractGenerateRequest): Observable<ContractResponse> {
    return this.http.post<ApiResponse<ContractResponse>>(`${this.baseUrl}/generate`, request)
      .pipe(map(res => res.data));
  }

  generateWithStepUp(request: ContractGenerateRequest, stepUpToken: string): Observable<ContractResponse> {
    return this.http.post<ApiResponse<ContractResponse>>(`${this.baseUrl}/generate`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  getContracts(filters?: { templateId?: number | null; customerId?: number | null; referenceModule?: string; keyword?: string }): Observable<ContractResponse[]> {
    let params = new HttpParams();
    if (filters?.templateId) params = params.set('templateId', String(filters.templateId));
    if (filters?.customerId) params = params.set('customerId', String(filters.customerId));
    if (filters?.referenceModule?.trim()) params = params.set('referenceModule', filters.referenceModule.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());

    return this.http.get<ApiResponse<ContractResponse[]>>(`${this.baseUrl}/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getById(id: number): Observable<ContractResponse> {
    return this.http.get<ApiResponse<ContractResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  getPrintCopyPreviewUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/${id}/print-copy/preview`);
  }

  getPrintCopyDownloadUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/${id}/print-copy/download`);
  }

  customerSign(id: number, request: ContractSignRequest): Observable<ContractResponse> {
    return this.http.post<ApiResponse<ContractResponse>>(`${this.baseUrl}/${id}/customer-sign`, request)
      .pipe(map(res => res.data));
  }

  customerSignWithStepUp(id: number, request: ContractSignRequest, stepUpToken: string): Observable<ContractResponse> {
    return this.http.post<ApiResponse<ContractResponse>>(`${this.baseUrl}/${id}/customer-sign`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  shariahSign(id: number, request: ContractSignRequest): Observable<ContractResponse> {
    return this.http.post<ApiResponse<ContractResponse>>(`${this.baseUrl}/${id}/shariah-sign`, request)
      .pipe(map(res => res.data));
  }

  shariahSignWithStepUp(id: number, request: ContractSignRequest, stepUpToken: string): Observable<ContractResponse> {
    return this.http.post<ApiResponse<ContractResponse>>(`${this.baseUrl}/${id}/shariah-sign`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  getVersions(id: number): Observable<ContractVersionResponse[]> {
    return this.http.get<ApiResponse<ContractVersionResponse[]>>(`${this.baseUrl}/${id}/versions`)
      .pipe(map(res => res.data || []));
  }

  getDashboardSummary(): Observable<ContractDashboardSummaryResponse> {
    return this.http.get<ApiResponse<ContractDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }
}
