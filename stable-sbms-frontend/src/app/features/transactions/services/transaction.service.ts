import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { withAccessToken } from 'src/app/core/utils/authenticated-url.util';

import {
  ApiResponse,
  ChequeClearingRequest,
  DepositRequest,
  FundTransferRequest,
  StandingInstructionRequest,
  StandingInstructionResponse,
  TransactionDashboardSummaryResponse,
  TransactionResponse,
  TransactionReversalRequest,
  WithdrawalRequest
} from '../models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  private readonly baseUrl = `${environment.apiBaseUrl}/transactions`;

  constructor(private http: HttpClient) {}

  deposit(request: DepositRequest): Observable<TransactionResponse> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.baseUrl}/deposit`, request)
      .pipe(map(res => res.data));
  }

  withdraw(request: WithdrawalRequest): Observable<TransactionResponse> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.baseUrl}/withdraw`, request)
      .pipe(map(res => res.data));
  }

  transfer(request: FundTransferRequest): Observable<TransactionResponse> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.baseUrl}/transfer`, request)
      .pipe(map(res => res.data));
  }

  chequeClearing(request: ChequeClearingRequest): Observable<TransactionResponse> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.baseUrl}/cheque-clearing`, request)
      .pipe(map(res => res.data));
  }

  createStandingInstruction(request: StandingInstructionRequest): Observable<StandingInstructionResponse> {
    return this.http.post<ApiResponse<StandingInstructionResponse>>(`${this.baseUrl}/standing-instruction/create`, request)
      .pipe(map(res => res.data));
  }

  getStandingInstructions(): Observable<StandingInstructionResponse[]> {
    return this.http.get<ApiResponse<StandingInstructionResponse[]>>(`${this.baseUrl}/standing-instruction/list`)
      .pipe(map(res => res.data || []));
  }

  getTransactions(): Observable<TransactionResponse[]> {
    return this.http.get<ApiResponse<TransactionResponse[]>>(`${this.baseUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getTransactionById(id: number): Observable<TransactionResponse> {
    return this.http.get<ApiResponse<TransactionResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  searchTransactions(keyword = ''): Observable<TransactionResponse[]> {
    let params = new HttpParams();
    if (keyword.trim()) {
      params = params.set('keyword', keyword.trim());
    }
    return this.http.get<ApiResponse<TransactionResponse[]>>(`${this.baseUrl}/search`, { params })
      .pipe(map(res => res.data || []));
  }

  reverseTransaction(id: number, request: TransactionReversalRequest): Observable<TransactionResponse> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.baseUrl}/${id}/reverse`, request)
      .pipe(map(res => res.data));
  }

  reverseTransactionWithStepUp(id: number, request: TransactionReversalRequest, stepUpToken: string): Observable<TransactionResponse> {
    return this.http.post<ApiResponse<TransactionResponse>>(`${this.baseUrl}/${id}/reverse`, request, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  getJournal(id: number): Observable<TransactionResponse> {
    return this.http.get<ApiResponse<TransactionResponse>>(`${this.baseUrl}/${id}/journal`)
      .pipe(map(res => res.data));
  }

  getDashboardSummary(): Observable<TransactionDashboardSummaryResponse> {
    return this.http.get<ApiResponse<TransactionDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  getVoucherPreviewUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/${id}/voucher/preview`);
  }

  getVoucherDownloadUrl(id: number): string {
    return withAccessToken(`${this.baseUrl}/${id}/voucher/download`);
  }

  previewVoucherBlob(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/voucher/preview`, { responseType: 'blob' });
  }

  downloadVoucherBlob(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/voucher/download`, { responseType: 'blob' });
  }
}
