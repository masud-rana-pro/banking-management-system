import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { BranchApiService } from 'src/app/features/branch/services/branch-api.service';
import { BranchResponse } from 'src/app/features/branch/models/branch.model';
import {
  ReportExportType,
  ReportPageConfig,
  ReportRequestLogResponse,
  ReportResultResponse,
  formatEnumLabel
} from '../../models/report.model';
import { ReportFilters, ReportService } from '../../services/report.service';

type ReportDatePreset = 'TODAY' | 'LAST_7_DAYS' | 'LAST_30_DAYS' | 'YTD' | 'LAST_12_MONTHS';

@Component({
  selector: 'app-report-page-shell',
  templateUrl: './report-page-shell.component.html',
  styleUrls: ['./report-page-shell.component.scss']
})
export class ReportPageShellComponent implements OnInit {

  @Input() config!: ReportPageConfig;

  loading = false;
  result: ReportResultResponse | null = null;
  branches: BranchResponse[] = [];
  lastRefreshedAt: string | null = null;
  filters = {
    dateFrom: '',
    dateTo: '',
    branchId: null as number | null,
    requestedBy: 'SYSTEM'
  };
  readonly datePresets: Array<{ value: ReportDatePreset; label: string }> = [
    { value: 'TODAY', label: 'Today' },
    { value: 'LAST_7_DAYS', label: '7D' },
    { value: 'LAST_30_DAYS', label: '30D' },
    { value: 'YTD', label: 'YTD' },
    { value: 'LAST_12_MONTHS', label: '12M' }
  ];

  constructor(
    private reportService: ReportService,
    private branchApiService: BranchApiService,
    private accessControl: AccessControlService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.setDefaultFilters();
    this.route.queryParamMap.subscribe(params => {
      this.filters.dateFrom = params.get('dateFrom') || this.filters.dateFrom;
      this.filters.dateTo = params.get('dateTo') || this.filters.dateTo;
      this.filters.branchId = params.get('branchId')
        ? Number(params.get('branchId'))
        : (this.isBranchSelectionLocked ? this.accessControl.session?.branchId || null : null);
      this.load();
    });
    if (this.config.enableBranchFilter) {
      this.branchApiService.dropdown().subscribe({
        next: data => this.branches = data || [],
        error: err => console.error(err)
      });
    }
  }

  load(exportType?: ReportExportType): void {
    this.loading = true;
    this.requestReport(exportType).subscribe({
      next: data => {
        this.result = data;
        this.lastRefreshedAt = new Date().toISOString();
        this.loading = false;
        if (exportType) {
          Swal.fire('Export Ready', `${this.config.title} ${exportType} export has been logged successfully.`, 'success');
        }
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', err?.error?.message || `Failed to load ${this.config.title.toLowerCase()}.`, 'error');
      }
    });
  }

  onFilter(): void {
    this.load();
  }

  onReset(): void {
    this.setDefaultFilters();
    this.load();
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
    this.load();
  }

  isPresetActive(preset: ReportDatePreset): boolean {
    const today = this.toDateInput(new Date());
    if (this.filters.dateTo !== today) {
      return false;
    }
    const from = this.filters.dateFrom;
    if (preset === 'TODAY') return from === today;
    if (preset === 'LAST_7_DAYS') return from === this.toDateInput(this.daysAgo(6));
    if (preset === 'LAST_30_DAYS') return from === this.toDateInput(this.daysAgo(29));
    if (preset === 'YTD') return from === `${new Date().getFullYear()}-01-01`;
    return from === this.toDateInput(new Date(new Date().getFullYear(), new Date().getMonth() - 11, 1));
  }

  onPrint(): void {
    this.openCurrentReportFile('print');
  }

  onExport(type: ReportExportType): void {
    this.load(type);
  }

  previewCurrentFile(): void {
    this.openCurrentReportFile('preview');
  }

  downloadCurrentFile(): void {
    if (this.result?.generatedFile) {
      this.reportService.downloadExportHistoryFile(this.result.logId).subscribe({
        next: response => this.saveBlob(response.body, this.resolveResponseFileName(response, this.result?.generatedFile?.originalFileName || 'report-export')),
        error: err => Swal.fire('Error', err?.error?.message || 'Failed to download generated report file.', 'error')
      });
      return;
    }
    this.openCurrentReportFile('download');
  }

  private openCurrentReportFile(action: 'preview' | 'print' | 'download'): void {
    this.loading = true;
    this.requestReport('PRINT').subscribe({
      next: data => {
        this.result = data;
        this.lastRefreshedAt = new Date().toISOString();
        this.loading = false;
        if (!data.logId) {
          Swal.fire('Error', 'Printable report file was not generated.', 'error');
          return;
        }
        if (action === 'download') {
          this.reportService.downloadExportHistoryFile(data.logId).subscribe({
            next: response => this.saveBlob(response.body, this.resolveResponseFileName(response, data.generatedFile?.originalFileName || 'report-export')),
            error: err => Swal.fire('Error', err?.error?.message || 'Failed to download generated report file.', 'error')
          });
          return;
        }
        this.reportService.previewExportHistoryFile(data.logId).subscribe({
          next: response => this.openBlob(response.body, action === 'print'),
          error: err => Swal.fire('Error', err?.error?.message || 'Failed to preview generated report file.', 'error')
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'Failed to generate printable report file.', 'error');
      }
    });
  }
  openDashboard(): void {
    this.router.navigate(['/reports/dashboard']);
  }

  openHistory(): void {
    this.router.navigate(['/reports/export-history']);
  }

  openHistoryEntry(item: ReportRequestLogResponse): void {
    this.router.navigate([this.routePathForQueryKey(item.queryKey)], {
      queryParams: this.queryParamsFromHistory(item)
    });
  }

  previewHistoryFile(item: ReportRequestLogResponse): void {
    this.loading = true;
    this.reportService.previewExportHistoryFile(item.id).subscribe({
      next: response => {
        this.loading = false;
        this.openBlob(response.body);
      },
      error: err => {
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'Failed to preview export history file.', 'error');
      }
    });
  }

  downloadHistoryFile(item: ReportRequestLogResponse): void {
    this.loading = true;
    this.reportService.downloadExportHistoryFile(item.id).subscribe({
      next: response => {
        this.loading = false;
        this.saveBlob(response.body, this.resolveResponseFileName(response, item.generatedFile?.originalFileName || `report-${item.id}`));
      },
      error: err => {
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'Failed to download export history file.', 'error');
      }
    });
  }

  printHistoryFile(item: ReportRequestLogResponse): void {
    this.loading = true;
    this.reportService.previewExportHistoryFile(item.id).subscribe({
      next: response => {
        this.loading = false;
        this.openBlob(response.body, true);
      },
      error: err => {
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'Failed to open printable export history file.', 'error');
      }
    });
  }

  getCell(row: Record<string, unknown>, key: string): string {
    const value = row[key];
    return value === null || value === undefined || value === '' ? '-' : String(value);
  }

  formatLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  get isBranchSelectionLocked(): boolean {
    return !!this.config.enableBranchFilter
      && !!this.accessControl.session?.branchId
      && !this.accessControl.hasAnyPermission(['MONTHLY_CLOSING_APPROVE', 'MONTHLY_CLOSING_REJECT', 'MONTHLY_CLOSING_REOPEN']);
  }

  get filterRangeLabel(): string {
    return `${this.filters.dateFrom} to ${this.filters.dateTo}`;
  }

  get liveClockLabel(): string {
    const source = this.result?.generatedAt || this.lastRefreshedAt;
    if (!source) {
      return 'Awaiting first run';
    }
    return new Date(source).toLocaleString([], {
      dateStyle: 'medium',
      timeStyle: 'short'
    });
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

  private resolveResponseFileName(response: any, fallback: string): string {
    const disposition = response?.headers?.get?.('content-disposition') || '';
    const match = /filename="?([^";]+)"?/i.exec(disposition);
    return match?.[1] || fallback;
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

  private requestReport(exportType?: ReportExportType) {
    const filters: ReportFilters = {
      dateFrom: this.filters.dateFrom,
      dateTo: this.filters.dateTo,
      branchId: this.filters.branchId,
      exportType,
      requestedBy: this.filters.requestedBy
    };

    switch (this.config.queryKey) {
      case 'OPERATIONAL':
        return this.reportService.getOperationalReport(filters);
      case 'PROFIT_DISTRIBUTION':
        return this.reportService.getProfitDistributionReport(filters);
      case 'MANAGEMENT_PL':
        return this.reportService.getManagementPlReport(filters);
      case 'FINANCING_PORTFOLIO':
        return this.reportService.getFinancingPortfolioReport(filters);
      case 'PAR':
        return this.reportService.getParReport(filters);
      case 'SHARIAH_AUDIT':
        return this.reportService.getShariahAuditReport(filters);
      case 'BRANCH':
        return this.reportService.getBranchReport(filters);
      case 'KPI':
        return this.reportService.getKpiReport(filters);
      case 'GROWTH':
        return this.reportService.getGrowthReport(filters);
      case 'LOAN_RECOVERY':
        return this.reportService.getLoanRecoveryReport(filters);
      case 'MONTHLY_CLOSING':
        return this.reportService.getMonthlyClosingReport(filters);
      default:
        throw new Error('Unsupported report page');
    }
  }

  private setDefaultFilters(): void {
    const today = new Date();
    const from = new Date(today);
    from.setDate(today.getDate() - 30);
    this.filters = {
      dateFrom: this.toDateInput(from),
      dateTo: this.toDateInput(today),
      branchId: this.isBranchSelectionLocked ? this.accessControl.session?.branchId || null : null,
      requestedBy: this.accessControl.session?.username || 'SYSTEM'
    };
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

  private routePathForQueryKey(queryKey: string): string {
    switch (queryKey) {
      case 'OPERATIONAL':
        return '/reports/operational';
      case 'PROFIT_DISTRIBUTION':
        return '/reports/profit-distribution';
      case 'MANAGEMENT_PL':
        return '/reports/management-pl';
      case 'TRIAL_BALANCE':
        return '/reports/trial-balance';
      case 'LEDGER_PROFIT_LOSS':
        return '/reports/ledger-profit-loss';
      case 'FINANCING_PORTFOLIO':
        return '/reports/financing-portfolio';
      case 'PAR':
        return '/reports/par';
      case 'SHARIAH_AUDIT':
        return '/reports/shariah-audit';
      case 'BRANCH':
        return '/reports/branch';
      case 'KPI':
        return '/reports/kpi';
      case 'GROWTH':
        return '/reports/growth';
      case 'LOAN_RECOVERY':
        return '/reports/loan-recovery';
      case 'MONTHLY_CLOSING':
        return '/reports/monthly-closing';
      default:
        return '/reports/dashboard';
    }
  }

  private queryParamsFromHistory(item: ReportRequestLogResponse): Record<string, string | number> {
    let branchId: number | undefined;
    if (item.filterJson) {
      try {
        const parsed = JSON.parse(item.filterJson);
        if (parsed?.branchId) {
          branchId = Number(parsed.branchId);
        }
      } catch (error) {
        console.error(error);
      }
    }

    const queryParams: Record<string, string | number> = {};
    if (item.dateFrom) queryParams['dateFrom'] = item.dateFrom;
    if (item.dateTo) queryParams['dateTo'] = item.dateTo;
    if (branchId) queryParams['branchId'] = branchId;
    return queryParams;
  }
}
