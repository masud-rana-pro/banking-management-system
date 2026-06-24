import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { withAccessToken } from 'src/app/core/utils/authenticated-url.util';

import {
  AccountDashboardSummaryResponse,
  AccountOpeningRequestRequest,
  AccountOpeningRequestResponse,
  AccountResponse,
  AccountTypeDropdownResponse,
  AccountTypeRequest,
  AccountTypeResponse,
  AccountWorkflowActionRequest,
  ApiResponse
} from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  private readonly accountTypeUrl = `${environment.apiBaseUrl}/account-types`;
  private readonly openingRequestUrl = `${environment.apiBaseUrl}/account-opening-requests`;
  private readonly accountUrl = `${environment.apiBaseUrl}/accounts`;

  constructor(private http: HttpClient) {}

  createAccountType(request: AccountTypeRequest): Observable<AccountTypeResponse> {
    return this.http.post<ApiResponse<AccountTypeResponse>>(`${this.accountTypeUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  getAccountTypes(): Observable<AccountTypeResponse[]> {
    return this.http.get<ApiResponse<AccountTypeResponse[]>>(`${this.accountTypeUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getAccountTypeById(id: number): Observable<AccountTypeResponse> {
    return this.http.get<ApiResponse<AccountTypeResponse>>(`${this.accountTypeUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  updateAccountType(id: number, request: AccountTypeRequest): Observable<AccountTypeResponse> {
    return this.http.put<ApiResponse<AccountTypeResponse>>(`${this.accountTypeUrl}/${id}`, request)
      .pipe(map(res => res.data));
  }

  archiveAccountType(id: number): Observable<AccountTypeResponse> {
    return this.http.delete<ApiResponse<AccountTypeResponse>>(`${this.accountTypeUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  restoreAccountType(id: number): Observable<AccountTypeResponse> {
    return this.http.put<ApiResponse<AccountTypeResponse>>(`${this.accountTypeUrl}/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  accountTypeDropdown(): Observable<AccountTypeDropdownResponse[]> {
    return this.http.get<ApiResponse<AccountTypeDropdownResponse[]>>(`${this.accountTypeUrl}/dropdown`)
      .pipe(map(res => res.data || []));
  }

  createOpeningRequest(request: AccountOpeningRequestRequest): Observable<AccountOpeningRequestResponse> {
    return this.http.post<ApiResponse<AccountOpeningRequestResponse>>(`${this.openingRequestUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  getOpeningRequests(): Observable<AccountOpeningRequestResponse[]> {
    return this.http.get<ApiResponse<AccountOpeningRequestResponse[]>>(`${this.openingRequestUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getOpeningRequestById(id: number): Observable<AccountOpeningRequestResponse> {
    return this.http.get<ApiResponse<AccountOpeningRequestResponse>>(`${this.openingRequestUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  getOpeningRequestDocumentPreviewUrl(id: number): string {
    return withAccessToken(`${this.openingRequestUrl}/${id}/document/preview`);
  }

  getOpeningRequestDocumentDownloadUrl(id: number): string {
    return withAccessToken(`${this.openingRequestUrl}/${id}/document/download`);
  }

  updateOpeningRequest(id: number, request: AccountOpeningRequestRequest): Observable<AccountOpeningRequestResponse> {
    return this.http.put<ApiResponse<AccountOpeningRequestResponse>>(`${this.openingRequestUrl}/${id}`, request)
      .pipe(map(res => res.data));
  }

  submitOpeningRequest(id: number): Observable<AccountOpeningRequestResponse> {
    return this.http.post<ApiResponse<AccountOpeningRequestResponse>>(`${this.openingRequestUrl}/${id}/submit`, {})
      .pipe(map(res => res.data));
  }

  verifyOpeningRequest(id: number): Observable<AccountOpeningRequestResponse> {
    return this.http.post<ApiResponse<AccountOpeningRequestResponse>>(`${this.openingRequestUrl}/${id}/verify`, {})
      .pipe(map(res => res.data));
  }

  approveOpeningRequest(id: number): Observable<AccountOpeningRequestResponse> {
    return this.http.post<ApiResponse<AccountOpeningRequestResponse>>(`${this.openingRequestUrl}/${id}/approve`, {})
      .pipe(map(res => res.data));
  }

  rejectOpeningRequest(id: number, request: AccountWorkflowActionRequest): Observable<AccountOpeningRequestResponse> {
    return this.http.post<ApiResponse<AccountOpeningRequestResponse>>(`${this.openingRequestUrl}/${id}/reject`, request)
      .pipe(map(res => res.data));
  }

  returnOpeningRequest(id: number, request: AccountWorkflowActionRequest): Observable<AccountOpeningRequestResponse> {
    return this.http.post<ApiResponse<AccountOpeningRequestResponse>>(`${this.openingRequestUrl}/${id}/return`, request)
      .pipe(map(res => res.data));
  }

  getAccounts(): Observable<AccountResponse[]> {
    return this.http.get<ApiResponse<AccountResponse[]>>(`${this.accountUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getAccountById(id: number): Observable<AccountResponse> {
    return this.http.get<ApiResponse<AccountResponse>>(`${this.accountUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  searchAccounts(keyword = ''): Observable<AccountResponse[]> {
    let params = new HttpParams();
    if (keyword.trim()) {
      params = params.set('keyword', keyword.trim());
    }
    return this.http.get<ApiResponse<AccountResponse[]>>(`${this.accountUrl}/search`, { params })
      .pipe(map(res => res.data || []));
  }

  activateAccount(id: number, request: AccountWorkflowActionRequest): Observable<AccountResponse> {
    return this.http.post<ApiResponse<AccountResponse>>(`${this.accountUrl}/${id}/activate`, request)
      .pipe(map(res => res.data));
  }

  blockAccount(id: number, request: AccountWorkflowActionRequest): Observable<AccountResponse> {
    return this.http.post<ApiResponse<AccountResponse>>(`${this.accountUrl}/${id}/block`, request)
      .pipe(map(res => res.data));
  }

  freezeAccount(id: number, request: AccountWorkflowActionRequest): Observable<AccountResponse> {
    return this.http.post<ApiResponse<AccountResponse>>(`${this.accountUrl}/${id}/freeze`, request)
      .pipe(map(res => res.data));
  }

  closeAccount(id: number, request: AccountWorkflowActionRequest): Observable<AccountResponse> {
    return this.http.post<ApiResponse<AccountResponse>>(`${this.accountUrl}/${id}/close`, request)
      .pipe(map(res => res.data));
  }

  getDashboardSummary(): Observable<AccountDashboardSummaryResponse> {
    return this.http.get<ApiResponse<AccountDashboardSummaryResponse>>(`${this.accountUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }
}
