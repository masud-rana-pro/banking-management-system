import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { withAccessToken } from 'src/app/core/utils/authenticated-url.util';

import {
  ApiResponse,
  CharityBeneficiaryRequest,
  CharityBeneficiaryResponse,
  CharityFundResponse,
  CharityPayoutRequest,
  CharityPayoutResponse,
  ZakatCalculationRequest,
  ZakatDashboardSummaryResponse,
  ZakatProfileRequest,
  ZakatProfileResponse
} from '../models/zakat.model';

@Injectable({
  providedIn: 'root'
})
export class ZakatService {

  private readonly baseUrl = `${environment.apiBaseUrl}/zakat`;

  constructor(private http: HttpClient) {}

  createProfile(request: ZakatProfileRequest): Observable<ZakatProfileResponse> {
    return this.http.post<ApiResponse<ZakatProfileResponse>>(`${this.baseUrl}/profile/create`, request)
      .pipe(map(res => res.data));
  }

  updateProfile(id: number, request: ZakatProfileRequest): Observable<ZakatProfileResponse> {
    return this.http.put<ApiResponse<ZakatProfileResponse>>(`${this.baseUrl}/profile/${id}`, request)
      .pipe(map(res => res.data));
  }

  getProfiles(filters?: { customerId?: number | null; zakatYear?: number | null; keyword?: string }): Observable<ZakatProfileResponse[]> {
    let params = new HttpParams();
    if (filters?.customerId) params = params.set('customerId', String(filters.customerId));
    if (filters?.zakatYear) params = params.set('zakatYear', String(filters.zakatYear));
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());

    return this.http.get<ApiResponse<ZakatProfileResponse[]>>(`${this.baseUrl}/profile/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getProfileById(id: number): Observable<ZakatProfileResponse> {
    return this.http.get<ApiResponse<ZakatProfileResponse>>(`${this.baseUrl}/profile/${id}`)
      .pipe(map(res => res.data));
  }

  getProfileSheetPreviewUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/profile/${id}/sheet/preview`);
  }

  getProfileSheetDownloadUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/profile/${id}/sheet/download`);
  }

  calculate(request: ZakatCalculationRequest): Observable<ZakatProfileResponse> {
    return this.http.post<ApiResponse<ZakatProfileResponse>>(`${this.baseUrl}/calculate`, request)
      .pipe(map(res => res.data));
  }

  calculateWithStepUp(request: ZakatCalculationRequest, stepUpToken: string): Observable<ZakatProfileResponse> {
    return this.http.post<ApiResponse<ZakatProfileResponse>>(`${this.baseUrl}/calculate`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  getCharityFund(): Observable<CharityFundResponse[]> {
    return this.http.get<ApiResponse<CharityFundResponse[]>>(`${this.baseUrl}/charity-fund`)
      .pipe(map(res => res.data || []));
  }

  getBeneficiaries(keyword = ''): Observable<CharityBeneficiaryResponse[]> {
    let params = new HttpParams();
    if (keyword.trim()) params = params.set('keyword', keyword.trim());
    return this.http.get<ApiResponse<CharityBeneficiaryResponse[]>>(`${this.baseUrl}/beneficiaries/list`, { params })
      .pipe(map(res => res.data || []));
  }

  createBeneficiary(request: CharityBeneficiaryRequest): Observable<CharityBeneficiaryResponse> {
    return this.http.post<ApiResponse<CharityBeneficiaryResponse>>(`${this.baseUrl}/beneficiaries/create`, request)
      .pipe(map(res => res.data));
  }

  updateBeneficiary(id: number, request: CharityBeneficiaryRequest): Observable<CharityBeneficiaryResponse> {
    return this.http.put<ApiResponse<CharityBeneficiaryResponse>>(`${this.baseUrl}/beneficiaries/${id}`, request)
      .pipe(map(res => res.data));
  }

  archiveBeneficiary(id: number): Observable<CharityBeneficiaryResponse> {
    return this.http.delete<ApiResponse<CharityBeneficiaryResponse>>(`${this.baseUrl}/beneficiaries/${id}`)
      .pipe(map(res => res.data));
  }

  restoreBeneficiary(id: number): Observable<CharityBeneficiaryResponse> {
    return this.http.put<ApiResponse<CharityBeneficiaryResponse>>(`${this.baseUrl}/beneficiaries/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  getPayouts(): Observable<CharityPayoutResponse[]> {
    return this.http.get<ApiResponse<CharityPayoutResponse[]>>(`${this.baseUrl}/payouts/list`)
      .pipe(map(res => res.data || []));
  }

  getPayoutReceiptPreviewUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/payouts/${id}/receipt/preview`);
  }

  getPayoutReceiptDownloadUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/payouts/${id}/receipt/download`);
  }

  createPayout(request: CharityPayoutRequest): Observable<CharityPayoutResponse> {
    return this.http.post<ApiResponse<CharityPayoutResponse>>(`${this.baseUrl}/payouts/create`, request)
      .pipe(map(res => res.data));
  }

  createPayoutWithStepUp(request: CharityPayoutRequest, stepUpToken: string): Observable<CharityPayoutResponse> {
    return this.http.post<ApiResponse<CharityPayoutResponse>>(`${this.baseUrl}/payouts/create`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  getDashboardSummary(): Observable<ZakatDashboardSummaryResponse> {
    return this.http.get<ApiResponse<ZakatDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }
}
