import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { withAccessToken } from 'src/app/core/utils/authenticated-url.util';
import {
  AtmDashboardSummaryResponse,
  CashBinRequest,
  CashBinResponse,
  DeviceJournalResponse,
  ReconciliationRequest,
  ReconciliationResponse,
  ReplenishmentRequest,
  ReplenishmentResponse,
  TerminalDropdownResponse,
  TerminalRequest,
  TerminalResponse,
  UserSummaryResponse
} from '../models/terminal.model';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class AtmTerminalService {

  private readonly baseUrl = `${environment.apiBaseUrl}/atm-terminals`;
  private readonly userUrl = `${environment.apiBaseUrl}/users`;

  constructor(private http: HttpClient) { }

  create(request: TerminalRequest): Observable<TerminalResponse> {
    return this.http
      .post<ApiResponse<TerminalResponse>>(`${this.baseUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  getAll(): Observable<TerminalResponse[]> {
    return this.http
      .get<ApiResponse<TerminalResponse[]>>(`${this.baseUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getById(id: number): Observable<TerminalResponse> {
    return this.http
      .get<ApiResponse<TerminalResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  getTerminalProfilePreviewUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/${id}/profile/preview`);
  }

  getTerminalProfileDownloadUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/${id}/profile/download`);
  }

  update(id: number, request: TerminalRequest): Observable<TerminalResponse> {
    return this.http
      .put<ApiResponse<TerminalResponse>>(`${this.baseUrl}/${id}`, request)
      .pipe(map(res => res.data));
  }

  archive(id: number): Observable<TerminalResponse> {
    return this.http
      .delete<ApiResponse<TerminalResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  restore(id: number): Observable<TerminalResponse> {
    return this.http
      .put<ApiResponse<TerminalResponse>>(`${this.baseUrl}/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  dropdown(): Observable<TerminalDropdownResponse[]> {
    return this.http
      .get<ApiResponse<TerminalDropdownResponse[]>>(`${this.baseUrl}/dropdown`)
      .pipe(map(res => res.data || []));
  }

  createCashBin(request: CashBinRequest): Observable<CashBinResponse> {
    return this.http
      .post<ApiResponse<CashBinResponse>>(`${this.baseUrl}/cash-bin/create`, request)
      .pipe(map(res => res.data));
  }

  getCashBins(): Observable<CashBinResponse[]> {
    return this.http
      .get<ApiResponse<CashBinResponse[]>>(`${this.baseUrl}/cash-bin/list`)
      .pipe(map(res => res.data || []));
  }

  getCashBinById(id: number): Observable<CashBinResponse> {
    return this.http
      .get<ApiResponse<CashBinResponse>>(`${this.baseUrl}/cash-bin/${id}`)
      .pipe(map(res => res.data));
  }

  getCashBinProfilePreviewUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/cash-bin/${id}/profile/preview`);
  }

  getCashBinProfileDownloadUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/cash-bin/${id}/profile/download`);
  }

  updateCashBin(id: number, request: CashBinRequest): Observable<CashBinResponse> {
    return this.http
      .put<ApiResponse<CashBinResponse>>(`${this.baseUrl}/cash-bin/${id}`, request)
      .pipe(map(res => res.data));
  }

  archiveCashBin(id: number): Observable<CashBinResponse> {
    return this.http
      .delete<ApiResponse<CashBinResponse>>(`${this.baseUrl}/cash-bin/${id}`)
      .pipe(map(res => res.data));
  }

  restoreCashBin(id: number): Observable<CashBinResponse> {
    return this.http
      .put<ApiResponse<CashBinResponse>>(`${this.baseUrl}/cash-bin/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  getCashBinsByTerminal(terminalId: number): Observable<CashBinResponse[]> {
    return this.http
      .get<ApiResponse<CashBinResponse[]>>(`${this.baseUrl}/cash-bin/terminal/${terminalId}`)
      .pipe(map(res => res.data || []));
  }

  createReplenishment(request: ReplenishmentRequest): Observable<ReplenishmentResponse> {
    return this.http
      .post<ApiResponse<ReplenishmentResponse>>(`${this.baseUrl}/replenishment/create`, request)
      .pipe(map(res => res.data));
  }

  getReplenishments(): Observable<ReplenishmentResponse[]> {
    return this.http
      .get<ApiResponse<ReplenishmentResponse[]>>(`${this.baseUrl}/replenishment/list`)
      .pipe(map(res => res.data || []));
  }

  getReplenishmentById(id: number): Observable<ReplenishmentResponse> {
    return this.http
      .get<ApiResponse<ReplenishmentResponse>>(`${this.baseUrl}/replenishment/${id}`)
      .pipe(map(res => res.data));
  }

  getReplenishmentReportPreviewUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/replenishment/${id}/report/preview`);
  }

  getReplenishmentReportDownloadUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/replenishment/${id}/report/download`);
  }

  getReplenishmentsByTerminal(terminalId: number): Observable<ReplenishmentResponse[]> {
    return this.http
      .get<ApiResponse<ReplenishmentResponse[]>>(`${this.baseUrl}/replenishment/terminal/${terminalId}`)
      .pipe(map(res => res.data || []));
  }

  createReconciliation(request: ReconciliationRequest): Observable<ReconciliationResponse> {
    return this.http
      .post<ApiResponse<ReconciliationResponse>>(`${this.baseUrl}/reconciliation/create`, request)
      .pipe(map(res => res.data));
  }

  getReconciliations(): Observable<ReconciliationResponse[]> {
    return this.http
      .get<ApiResponse<ReconciliationResponse[]>>(`${this.baseUrl}/reconciliation/list`)
      .pipe(map(res => res.data || []));
  }

  getReconciliationById(id: number): Observable<ReconciliationResponse> {
    return this.http
      .get<ApiResponse<ReconciliationResponse>>(`${this.baseUrl}/reconciliation/${id}`)
      .pipe(map(res => res.data));
  }

  getReconciliationReportPreviewUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/reconciliation/${id}/report/preview`);
  }

  getReconciliationReportDownloadUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/reconciliation/${id}/report/download`);
  }

  getReconciliationsByTerminal(terminalId: number): Observable<ReconciliationResponse[]> {
    return this.http
      .get<ApiResponse<ReconciliationResponse[]>>(`${this.baseUrl}/reconciliation/terminal/${terminalId}`)
      .pipe(map(res => res.data || []));
  }

  getDashboardSummary(): Observable<AtmDashboardSummaryResponse> {
    return this.http
      .get<ApiResponse<AtmDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  getDeviceJournal(terminalId?: number | null): Observable<DeviceJournalResponse[]> {
    let params = new HttpParams();

    if (terminalId) {
      params = params.set('terminalId', terminalId);
    }

    return this.http
      .get<ApiResponse<DeviceJournalResponse[]>>(`${this.baseUrl}/device-journal`, { params })
      .pipe(map(res => res.data || []));
  }

  getUsers(): Observable<UserSummaryResponse[]> {
    return this.http.get<UserSummaryResponse[]>(`${this.userUrl}/getall`);
  }
}
