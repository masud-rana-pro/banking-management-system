import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  BranchStatementRequestRequest,
  BranchStatementRequestResponse,
  CustomerStatementRequestRequest,
  CustomerStatementRequestResponse,
  FileReferenceResponse,
  StatementDashboardSummaryResponse
} from '../models/statement.model';

@Injectable({
  providedIn: 'root'
})
export class StatementService {

  private readonly customerUrl = `${environment.apiBaseUrl}/customer-statements`;
  private readonly branchUrl = `${environment.apiBaseUrl}/branch-statements`;
  private readonly statementUrl = `${environment.apiBaseUrl}/statements`;

  constructor(private http: HttpClient) {}

  requestCustomerStatement(request: CustomerStatementRequestRequest): Observable<CustomerStatementRequestResponse> {
    return this.http.post<ApiResponse<CustomerStatementRequestResponse>>(`${this.customerUrl}/request`, request)
      .pipe(map(res => res.data));
  }

  getCustomerStatements(): Observable<CustomerStatementRequestResponse[]> {
    return this.http.get<ApiResponse<CustomerStatementRequestResponse[]>>(`${this.customerUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getCustomerStatementById(id: number): Observable<CustomerStatementRequestResponse> {
    return this.http.get<ApiResponse<CustomerStatementRequestResponse>>(`${this.customerUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  downloadCustomerStatement(id: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.customerUrl}/${id}/download`, {
      observe: 'response',
      responseType: 'blob'
    });
  }

  exportCustomerStatements(exportType: string, search = '', status = ''): Observable<HttpResponse<Blob>> {
    const params: Record<string, string> = { exportType };
    if (search.trim()) params['search'] = search.trim();
    if (status.trim()) params['status'] = status.trim();
    return this.http.get(`${this.customerUrl}/export`, {
      params,
      observe: 'response',
      responseType: 'blob'
    });
  }

  previewCustomerStatement(id: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.customerUrl}/${id}/preview`, {
      observe: 'response',
      responseType: 'blob'
    });
  }

  requestBranchStatement(request: BranchStatementRequestRequest): Observable<BranchStatementRequestResponse> {
    return this.http.post<ApiResponse<BranchStatementRequestResponse>>(`${this.branchUrl}/request`, request)
      .pipe(map(res => res.data));
  }

  getBranchStatements(): Observable<BranchStatementRequestResponse[]> {
    return this.http.get<ApiResponse<BranchStatementRequestResponse[]>>(`${this.branchUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getBranchStatementById(id: number): Observable<BranchStatementRequestResponse> {
    return this.http.get<ApiResponse<BranchStatementRequestResponse>>(`${this.branchUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  downloadBranchStatement(id: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.branchUrl}/${id}/download`, {
      observe: 'response',
      responseType: 'blob'
    });
  }

  exportBranchStatements(exportType: string, search = '', status = ''): Observable<HttpResponse<Blob>> {
    const params: Record<string, string> = { exportType };
    if (search.trim()) params['search'] = search.trim();
    if (status.trim()) params['status'] = status.trim();
    return this.http.get(`${this.branchUrl}/export`, {
      params,
      observe: 'response',
      responseType: 'blob'
    });
  }

  previewBranchStatement(id: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.branchUrl}/${id}/preview`, {
      observe: 'response',
      responseType: 'blob'
    });
  }

  getDashboardSummary(): Observable<StatementDashboardSummaryResponse> {
    return this.http.get<ApiResponse<StatementDashboardSummaryResponse>>(`${this.statementUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  getFiles(): Observable<FileReferenceResponse[]> {
    return this.http.get<ApiResponse<FileReferenceResponse[]>>(`${this.statementUrl}/files`)
      .pipe(map(res => res.data || []));
  }
}
