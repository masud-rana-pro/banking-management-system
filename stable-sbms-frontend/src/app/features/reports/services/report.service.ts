import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  AccountingFilters,
  ApiResponse,
  GlJournalResponse,
  ManagementExpenseEntryRequest,
  ManagementExpenseEntryResponse,
  ProfitLossResponse,
  ReportDashboardSummaryResponse,
  ReportExportType,
  ReportRequestLogResponse,
  ReportResultResponse,
  TrialBalanceResponse
} from '../models/report.model';

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  private readonly baseUrl = `${environment.apiBaseUrl}/reports`;
  private readonly accountingUrl = `${environment.apiBaseUrl}/accounting`;

  constructor(private http: HttpClient) {}

  getDashboardSummary(): Observable<ReportDashboardSummaryResponse> {
    return this.http.get<ApiResponse<ReportDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  getOperationalReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/operational', filters);
  }

  getProfitDistributionReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/profit-distribution', filters);
  }

  getManagementPlReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/management-pl', filters);
  }

  getTrialBalanceReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/trial-balance', filters);
  }

  getLedgerProfitLossReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/ledger-profit-loss', filters);
  }

  getFinancingPortfolioReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/financing-portfolio', filters);
  }

  getParReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/par', filters);
  }

  getShariahAuditReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/shariah-audit', filters);
  }

  getBranchReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/branch', filters);
  }

  getKpiReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/kpi', filters);
  }

  getGrowthReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/growth', filters);
  }

  getLoanRecoveryReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/loan-recovery', filters);
  }

  getMonthlyClosingReport(filters: ReportFilters): Observable<ReportResultResponse> {
    return this.getReport('/monthly-closing', filters);
  }

  createManagementExpenseEntry(request: ManagementExpenseEntryRequest, requestedBy?: string | null): Observable<ManagementExpenseEntryResponse> {
    let params = new HttpParams();
    if (requestedBy?.trim()) {
      params = params.set('requestedBy', requestedBy.trim());
    }
    return this.http.post<ApiResponse<ManagementExpenseEntryResponse>>(`${this.baseUrl}/management-expenses`, request, { params })
      .pipe(map(res => res.data));
  }

  getManagementExpenseEntries(filters?: {
    dateFrom?: string;
    dateTo?: string;
    branchId?: number | null;
    expenseCategory?: string;
    keyword?: string;
  }): Observable<ManagementExpenseEntryResponse[]> {
    let params = new HttpParams();
    if (filters?.dateFrom) params = params.set('dateFrom', filters.dateFrom);
    if (filters?.dateTo) params = params.set('dateTo', filters.dateTo);
    if (filters?.branchId) params = params.set('branchId', String(filters.branchId));
    if (filters?.expenseCategory?.trim()) params = params.set('expenseCategory', filters.expenseCategory.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());

    return this.http.get<ApiResponse<ManagementExpenseEntryResponse[]>>(`${this.baseUrl}/management-expenses`, { params })
      .pipe(map(res => res.data || []));
  }

  getTrialBalance(filters: AccountingFilters): Observable<TrialBalanceResponse> {
    let params = new HttpParams();
    if (filters.dateFrom) params = params.set('dateFrom', filters.dateFrom);
    if (filters.dateTo) params = params.set('dateTo', filters.dateTo);
    if (filters.branchId) params = params.set('branchId', String(filters.branchId));
    return this.http.get<ApiResponse<TrialBalanceResponse>>(`${this.accountingUrl}/trial-balance`, { params })
      .pipe(map(res => res.data));
  }

  getLedgerProfitLoss(filters: AccountingFilters): Observable<ProfitLossResponse> {
    let params = new HttpParams();
    if (filters.dateFrom) params = params.set('dateFrom', filters.dateFrom);
    if (filters.dateTo) params = params.set('dateTo', filters.dateTo);
    if (filters.branchId) params = params.set('branchId', String(filters.branchId));
    return this.http.get<ApiResponse<ProfitLossResponse>>(`${this.accountingUrl}/profit-loss`, { params })
      .pipe(map(res => res.data));
  }

  getLedgerJournals(filters?: {
    sourceType?: string;
    sourceReferenceId?: number | null;
    accountCode?: string;
    dateFrom?: string;
    dateTo?: string;
    branchId?: number | null;
  }): Observable<GlJournalResponse[]> {
    let params = new HttpParams();
    if (filters?.sourceType?.trim()) params = params.set('sourceType', filters.sourceType.trim());
    if (filters?.sourceReferenceId) params = params.set('sourceReferenceId', String(filters.sourceReferenceId));
    if (filters?.accountCode?.trim()) params = params.set('accountCode', filters.accountCode.trim());
    if (filters?.dateFrom) params = params.set('dateFrom', filters.dateFrom);
    if (filters?.dateTo) params = params.set('dateTo', filters.dateTo);
    if (filters?.branchId) params = params.set('branchId', String(filters.branchId));
    return this.http.get<ApiResponse<GlJournalResponse[]>>(`${this.accountingUrl}/journals`, { params })
      .pipe(map(res => res.data || []));
  }

  getExportHistory(filters?: { reportType?: string; requestStatus?: string; keyword?: string }): Observable<ReportRequestLogResponse[]> {
    let params = new HttpParams();
    if (filters?.reportType?.trim()) params = params.set('reportType', filters.reportType.trim());
    if (filters?.requestStatus?.trim()) params = params.set('requestStatus', filters.requestStatus.trim());
    if (filters?.keyword?.trim()) params = params.set('keyword', filters.keyword.trim());
    return this.http.get<ApiResponse<ReportRequestLogResponse[]>>(`${this.baseUrl}/export-history`, { params })
      .pipe(map(res => res.data || []));
  }

  getExportHistoryById(id: number): Observable<ReportRequestLogResponse> {
    return this.http.get<ApiResponse<ReportRequestLogResponse>>(`${this.baseUrl}/export-history/${id}`)
      .pipe(map(res => res.data));
  }

  downloadExportHistoryFile(id: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.baseUrl}/export-history/${id}/download`, {
      observe: 'response',
      responseType: 'blob'
    });
  }

  previewExportHistoryFile(id: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.baseUrl}/export-history/${id}/preview`, {
      observe: 'response',
      responseType: 'blob'
    });
  }

  private getReport(path: string, filters: ReportFilters): Observable<ReportResultResponse> {
    let params = new HttpParams();
    if (filters.dateFrom) params = params.set('dateFrom', filters.dateFrom);
    if (filters.dateTo) params = params.set('dateTo', filters.dateTo);
    if (filters.branchId) params = params.set('branchId', String(filters.branchId));
    if (filters.exportType) params = params.set('exportType', filters.exportType);
    if (filters.requestedBy?.trim()) params = params.set('requestedBy', filters.requestedBy.trim());

    return this.http.get<ApiResponse<ReportResultResponse>>(`${this.baseUrl}${path}`, { params })
      .pipe(map(res => res.data));
  }
}

export interface ReportFilters {
  dateFrom: string;
  dateTo: string;
  branchId?: number | null;
  exportType?: ReportExportType | null;
  requestedBy?: string | null;
}
