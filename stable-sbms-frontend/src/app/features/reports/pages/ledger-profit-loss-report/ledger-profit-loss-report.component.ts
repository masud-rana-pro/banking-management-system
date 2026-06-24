import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { BranchResponse } from 'src/app/features/branch/models/branch.model';
import { BranchApiService } from 'src/app/features/branch/services/branch-api.service';
import { GlJournalResponse, ProfitLossResponse, ReportRequestLogResponse, ReportResultResponse } from '../../models/report.model';
import { ReportService } from '../../services/report.service';

type LedgerExportType = 'VIEW' | 'CSV' | 'EXCEL' | 'PRINT';
type ReportDatePreset = 'TODAY' | 'LAST_7_DAYS' | 'LAST_30_DAYS' | 'YTD' | 'LAST_12_MONTHS';

@Component({
  selector: 'app-ledger-profit-loss-report',
  templateUrl: './ledger-profit-loss-report.component.html',
  styleUrls: ['./ledger-profit-loss-report.component.scss']
})
export class LedgerProfitLossReportComponent implements OnInit {
  branches: BranchResponse[] = [];
  loading = false;
  journalLoading = false;
  report: ProfitLossResponse | null = null;
  managementReport: ReportResultResponse | null = null;
  journals: GlJournalResponse[] = [];
  exportHistory: ReportRequestLogResponse[] = [];
  runStatus: 'GENERATED' | 'VIEW' = 'VIEW';
  generatedAt: string | null = null;
  generatedFileName: string | null = null;
  activeExportType: LedgerExportType = 'VIEW';
  currentLogId: number | null = null;
  selectedJournalTitle = 'Journal Drill-down';
  lastRefreshedAt: string | null = null;
  readonly datePresets: Array<{ value: ReportDatePreset; label: string }> = [
    { value: 'TODAY', label: 'Today' },
    { value: 'LAST_7_DAYS', label: '7D' },
    { value: 'LAST_30_DAYS', label: '30D' },
    { value: 'YTD', label: 'YTD' },
    { value: 'LAST_12_MONTHS', label: '12M' }
  ];

  filters = {
    dateFrom: this.firstDayOfYear(),
    dateTo: this.today(),
    branchId: null as number | null,
    requestedBy: 'SYSTEM'
  };

  constructor(
    private reportService: ReportService,
    private branchApi: BranchApiService,
    private accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.filters.requestedBy = this.accessControl.session?.username || 'SYSTEM';
    this.loadBranches();
    this.loadReport();
  }

  loadBranches(): void {
    this.branchApi.dropdown().subscribe({
      next: data => this.branches = data || [],
      error: () => this.branches = []
    });
  }

  loadReport(): void {
    this.loading = true;
    forkJoin({
      ledger: this.reportService.getLedgerProfitLoss(this.filters),
      management: this.reportService.getManagementPlReport({
        dateFrom: this.filters.dateFrom,
        dateTo: this.filters.dateTo,
        branchId: this.filters.branchId,
        exportType: null,
        requestedBy: this.filters.requestedBy
      }),
      history: this.reportService.getExportHistory({
        reportType: 'PROFIT',
        keyword: 'REP-LPL-001'
      })
    }).subscribe({
      next: ({ ledger, management, history }) => {
        this.report = ledger;
        this.managementReport = management;
        this.exportHistory = history || [];
        this.journals = [];
        this.selectedJournalTitle = 'Journal Drill-down';
        this.lastRefreshedAt = new Date().toISOString();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.report = null;
        this.managementReport = null;
        this.exportHistory = [];
        this.loading = false;
        Swal.fire('Error', 'Failed to load ledger-based profit and loss.', 'error');
      }
    });
  }

  onFilter(): void {
    this.loadReport();
  }

  onReset(): void {
    this.filters = {
      dateFrom: this.firstDayOfYear(),
      dateTo: this.today(),
      branchId: null,
      requestedBy: this.accessControl.session?.username || 'SYSTEM'
    };
    this.runStatus = 'VIEW';
    this.generatedAt = null;
    this.generatedFileName = null;
    this.currentLogId = null;
    this.activeExportType = 'VIEW';
    this.loadReport();
  }

  applyPreset(preset: ReportDatePreset): void {
    const today = new Date();
    let from = new Date(today);

    if (preset === 'TODAY') {
      from = new Date(today);
    } else if (preset === 'LAST_7_DAYS') {
      from.setDate(today.getDate() - 6);
    } else if (preset === 'LAST_30_DAYS') {
      from.setDate(today.getDate() - 29);
    } else if (preset === 'YTD') {
      from = new Date(today.getFullYear(), 0, 1);
    } else {
      from = new Date(today.getFullYear(), today.getMonth() - 11, 1);
    }

    this.filters.dateFrom = this.toDateInput(from);
    this.filters.dateTo = this.toDateInput(today);
    this.loadReport();
  }

  isPresetActive(preset: ReportDatePreset): boolean {
    const today = this.toDateInput(new Date());
    if (this.filters.dateTo !== today) {
      return false;
    }
    if (preset === 'TODAY') return this.filters.dateFrom === today;
    if (preset === 'LAST_7_DAYS') return this.filters.dateFrom === this.toDateInput(this.daysAgo(6));
    if (preset === 'LAST_30_DAYS') return this.filters.dateFrom === this.toDateInput(this.daysAgo(29));
    if (preset === 'YTD') return this.filters.dateFrom === `${new Date().getFullYear()}-01-01`;
    return this.filters.dateFrom === this.toDateInput(new Date(new Date().getFullYear(), new Date().getMonth() - 11, 1));
  }

  onPrint(): void {
    this.generatePersistentRun('PRINT', 'print');
  }

  onExport(type: LedgerExportType): void {
    if (type === 'VIEW') {
      this.loadReport();
      return;
    }
    this.generatePersistentRun(type, 'download');
  }

  previewCurrentFile(): void {
    this.generatePersistentRun('PRINT', 'preview');
  }

  downloadCurrentFile(): void {
    if (this.currentLogId) {
      this.downloadHistoryFile(this.currentLogId);
      return;
    }
    this.generatePersistentRun('PRINT', 'download');
  }

  openDashboard(): void {
    window.location.href = '/reports/dashboard';
  }

  openHistory(): void {
    const el = document.getElementById('ledgerExportHistory');
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  previewHistoryFile(id: number): void {
    this.reportService.previewExportHistoryFile(id).subscribe({
      next: res => this.openBlob(res.body || null),
      error: err => {
        console.error(err);
        Swal.fire('Error', 'Failed to preview export file.', 'error');
      }
    });
  }

  downloadHistoryFile(id: number): void {
    this.reportService.downloadExportHistoryFile(id).subscribe({
      next: res => this.saveBlob(res.body || null, this.extractFileName(res.headers.get('content-disposition')) || 'ledger-profit-loss'),
      error: err => {
        console.error(err);
        Swal.fire('Error', 'Failed to download export file.', 'error');
      }
    });
  }

  printHistoryFile(id: number): void {
    this.reportService.previewExportHistoryFile(id).subscribe({
      next: res => this.openBlob(res.body || null, true),
      error: err => {
        console.error(err);
        Swal.fire('Error', 'Failed to print export file.', 'error');
      }
    });
  }

  viewBranchJournals(branchId: number | null, branchName?: string | null): void {
    this.selectedJournalTitle = `Journals for ${branchName || 'Head Office / Unassigned'}`;
    this.loadJournals({
      branchId,
      dateFrom: this.filters.dateFrom,
      dateTo: this.filters.dateTo
    });
  }

  viewAccountJournals(accountCode: string, accountName: string): void {
    this.selectedJournalTitle = `Journals for ${accountCode} ${accountName}`;
    this.loadJournals({
      accountCode,
      branchId: this.filters.branchId,
      dateFrom: this.filters.dateFrom,
      dateTo: this.filters.dateTo
    });
  }

  get managementNetResult(): number {
    return this.findManagementAmount('Net Management Result After Recorded Operating Expense');
  }

  get managementSpreadProxy(): number {
    return this.findManagementAmount('Net Spread Proxy Before Recorded Operating Expense');
  }

  get managementRecordedExpense(): number {
    return this.findManagementAmount('Operating Expense Recorded');
  }

  get reconciliationDifference(): number {
    return (this.report?.netProfit || 0) - this.managementNetResult;
  }

  get runBranchScope(): string {
    if (!this.filters.branchId) {
      return 'All Branches';
    }
    const branch = this.branches.find(item => item.id === this.filters.branchId);
    return branch ? `${branch.branchName} (${branch.branchCode})` : `Branch ${this.filters.branchId}`;
  }

  get filterSnapshotJson(): string {
    return JSON.stringify({
      dateFrom: this.filters.dateFrom,
      dateTo: this.filters.dateTo,
      branchId: this.filters.branchId,
      requestedBy: this.filters.requestedBy,
      exportType: this.activeExportType
    });
  }

  get filterRangeLabel(): string {
    return `${this.filters.dateFrom} to ${this.filters.dateTo}`;
  }

  get liveClockLabel(): string {
    const source = this.generatedAt || this.lastRefreshedAt;
    if (!source) {
      return 'Awaiting first run';
    }
    return new Date(source).toLocaleString([], {
      dateStyle: 'medium',
      timeStyle: 'short'
    });
  }

  getJournalBranchLabel(branchId?: number | null): string {
    if (!branchId) {
      return 'Head Office / Unassigned';
    }
    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchName} (${branch.branchCode})` : `Branch ${branchId}`;
  }

  private generatePersistentRun(exportType: 'CSV' | 'EXCEL' | 'PRINT' | null, action: 'preview' | 'print' | 'download'): void {
    this.reportService.getLedgerProfitLossReport({
      dateFrom: this.filters.dateFrom,
      dateTo: this.filters.dateTo,
      branchId: this.filters.branchId,
      exportType,
      requestedBy: this.filters.requestedBy
    }).subscribe({
      next: response => {
        this.currentLogId = response.logId;
        this.runStatus = 'GENERATED';
        this.generatedAt = response.generatedAt || null;
        this.generatedFileName = response.generatedFile?.originalFileName || response.generatedFile?.fileName || null;
        this.activeExportType = exportType === null ? 'VIEW' : exportType;
        this.exportHistory = response.exportHistory || this.exportHistory;

        if (!response.logId) {
          return;
        }
        if (action === 'preview') {
          this.previewHistoryFile(response.logId);
        } else if (action === 'print') {
          this.printHistoryFile(response.logId);
        } else {
          this.downloadHistoryFile(response.logId);
        }
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', 'Failed to generate ledger report file.', 'error');
      }
    });
  }

  private loadJournals(filters: {
    accountCode?: string;
    branchId?: number | null;
    sourceType?: string;
    sourceReferenceId?: number | null;
    dateFrom?: string;
    dateTo?: string;
  }): void {
    this.journalLoading = true;
    this.reportService.getLedgerJournals(filters).subscribe({
      next: data => {
        this.journals = data || [];
        this.journalLoading = false;
        const el = document.getElementById('ledgerJournalDrilldown');
        if (el) {
          setTimeout(() => el.scrollIntoView({ behavior: 'smooth', block: 'start' }), 50);
        }
      },
      error: err => {
        console.error(err);
        this.journals = [];
        this.journalLoading = false;
        Swal.fire('Error', 'Failed to load ledger journals.', 'error');
      }
    });
  }

  private findManagementAmount(lineItem: string): number {
    const row = this.managementReport?.rows?.find(item => String(item['lineItem'] || '') === lineItem);
    const raw = String(row?.['amount'] || '0').replace(/,/g, '').trim();
    const value = Number(raw);
    return Number.isFinite(value) ? value : 0;
  }

  private extractFileName(disposition: string | null): string | null {
    const match = disposition?.match(/filename=\"?([^\";]+)\"?/i);
    return match?.[1] || null;
  }

  private saveBlob(blob: Blob | null, fileName: string): void {
    if (!blob) return;
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    link.click();
    URL.revokeObjectURL(url);
  }

  private openBlob(blob: Blob | null, printOnLoad = false): void {
    if (!blob) return;
    const url = URL.createObjectURL(blob);
    const previewWindow = window.open(url, '_blank');
    if (previewWindow && printOnLoad) {
      setTimeout(() => {
        previewWindow.focus();
        previewWindow.print();
      }, 700);
    }
    setTimeout(() => URL.revokeObjectURL(url), 60000);
  }

  private today(): string {
    return new Date().toISOString().slice(0, 10);
  }

  private firstDayOfYear(): string {
    const today = new Date();
    return `${today.getFullYear()}-01-01`;
  }

  private toDateInput(value: Date): string {
    return value.toISOString().slice(0, 10);
  }

  private daysAgo(days: number): Date {
    const today = new Date();
    const date = new Date(today);
    date.setDate(today.getDate() - days);
    return date;
  }
}
