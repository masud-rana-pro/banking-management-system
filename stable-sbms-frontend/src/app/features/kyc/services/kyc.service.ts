import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  KycDashboardSummaryResponse,
  KycDecisionActionRequest,
  KycDecisionHistoryResponse,
  KycDocumentRequest,
  KycDocumentResponse,
  KycProfileRequest,
  KycProfileResponse
} from '../models/kyc.model';

@Injectable({
  providedIn: 'root'
})
export class KycService {

  private readonly baseUrl = `${environment.apiBaseUrl}/kyc`;

  constructor(private http: HttpClient) {}

  createProfile(request: KycProfileRequest): Observable<KycProfileResponse> {
    return this.http
      .post<ApiResponse<KycProfileResponse>>(`${this.baseUrl}/profile/create`, request)
      .pipe(map(res => res.data));
  }

  getProfiles(): Observable<KycProfileResponse[]> {
    return this.http
      .get<ApiResponse<KycProfileResponse[]>>(`${this.baseUrl}/profile/list`)
      .pipe(map(res => res.data || []));
  }

  getProfileByCustomerId(customerId: number): Observable<KycProfileResponse> {
    return this.http
      .get<ApiResponse<KycProfileResponse>>(`${this.baseUrl}/profile/customer/${customerId}`)
      .pipe(map(res => res.data));
  }

  getProfileById(id: number): Observable<KycProfileResponse> {
    return this.http
      .get<ApiResponse<KycProfileResponse>>(`${this.baseUrl}/profile/${id}`)
      .pipe(map(res => res.data));
  }

  updateProfile(id: number, request: KycProfileRequest): Observable<KycProfileResponse> {
    return this.http
      .put<ApiResponse<KycProfileResponse>>(`${this.baseUrl}/profile/${id}`, request)
      .pipe(map(res => res.data));
  }

  submitProfile(id: number): Observable<KycProfileResponse> {
    return this.http
      .post<ApiResponse<KycProfileResponse>>(`${this.baseUrl}/profile/${id}/submit`, {})
      .pipe(map(res => res.data));
  }

  verifyProfile(id: number): Observable<KycProfileResponse> {
    return this.http
      .post<ApiResponse<KycProfileResponse>>(`${this.baseUrl}/profile/${id}/verify`, {})
      .pipe(map(res => res.data));
  }

  approveProfile(id: number): Observable<KycProfileResponse> {
    return this.http
      .post<ApiResponse<KycProfileResponse>>(`${this.baseUrl}/profile/${id}/approve`, {})
      .pipe(map(res => res.data));
  }

  rejectProfile(id: number, request: KycDecisionActionRequest): Observable<KycProfileResponse> {
    return this.http
      .post<ApiResponse<KycProfileResponse>>(`${this.baseUrl}/profile/${id}/reject`, request)
      .pipe(map(res => res.data));
  }

  returnProfile(id: number, request: KycDecisionActionRequest): Observable<KycProfileResponse> {
    return this.http
      .post<ApiResponse<KycProfileResponse>>(`${this.baseUrl}/profile/${id}/return`, request)
      .pipe(map(res => res.data));
  }

  uploadDocument(request: KycDocumentRequest): Observable<KycDocumentResponse> {
    return this.http
      .post<ApiResponse<KycDocumentResponse>>(`${this.baseUrl}/document/upload`, request)
      .pipe(map(res => res.data));
  }

  getDocuments(profileId: number): Observable<KycDocumentResponse[]> {
    return this.http
      .get<ApiResponse<KycDocumentResponse[]>>(`${this.baseUrl}/profile/${profileId}/documents`)
      .pipe(map(res => res.data || []));
  }

  getDecisionHistory(kycId: number): Observable<KycDecisionHistoryResponse[]> {
    return this.http
      .get<ApiResponse<KycDecisionHistoryResponse[]>>(`${this.baseUrl}/decision-history/${kycId}`)
      .pipe(map(res => res.data || []));
  }

  getDashboardSummary(): Observable<KycDashboardSummaryResponse> {
    return this.http
      .get<ApiResponse<KycDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }
}
