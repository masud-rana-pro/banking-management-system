import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { ReportRequestLogResponse, formatEnumLabel } from '../../models/report.model';
import { ReportService } from '../../services/report.service';

@Component({
  selector: 'app-export-history',
  templateUrl: './export-history.component.html',
  styleUrls: ['./export-history.component.scss']
})
export class ExportHistoryComponent implements OnInit {

  loading = false;
  allItems: ReportRequestLogResponse[] = [];
  items: ReportRequestLogResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  filters = {
    keyword: '',
    reportType: '',
    requestStatus: ''
  };

  constructor(
    private reportService: ReportService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.reportService.getExportHistory().subscribe({
      next: data => {
        this.allItems = data || [];
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load export history.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.keyword.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.reportCode.toLowerCase().includes(keyword)
        || item.reportName.toLowerCase().includes(keyword)
        || item.requestedBy.toLowerCase().includes(keyword)
        || (item.generatedFile?.originalFileName || '').toLowerCase().includes(keyword);
      const matchesType = !this.filters.reportType || item.reportType === this.filters.reportType;
      const matchesStatus = !this.filters.requestStatus || item.requestStatus === this.filters.requestStatus;
      return matchesKeyword && matchesType && matchesStatus;
    });

    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onSearch(): void {
    this.page = 1;
    this.applyFilters();
  }

  onReset(): void {
    this.filters = { keyword: '', reportType: '', requestStatus: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openDashboard(): void {
    this.router.navigate(['/reports/dashboard']);
  }

  openReport(item: ReportRequestLogResponse): void {
    this.router.navigate([this.routePathForQueryKey(item.queryKey)], {
      queryParams: this.queryParamsFromHistory(item)
    });
  }

  previewFile(item: ReportRequestLogResponse): void {
    this.loading = true;
    this.reportService.previewExportHistoryFile(item.id).subscribe({
      next: response => {
        this.loading = false;
        this.openBlob(response.body);
      },
      error: err => {
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'Failed to preview generated report file.', 'error');
      }
    });
  }

  downloadFile(item: ReportRequestLogResponse): void {
    this.loading = true;
    this.reportService.downloadExportHistoryFile(item.id).subscribe({
      next: response => {
        this.loading = false;
        this.saveBlob(response.body, this.resolveResponseFileName(response, item.generatedFile?.originalFileName || `report-${item.id}`));
      },
      error: err => {
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'Failed to download generated report file.', 'error');
      }
    });
  }

  printFile(item: ReportRequestLogResponse): void {
    this.loading = true;
    this.reportService.previewExportHistoryFile(item.id).subscribe({
      next: response => {
        this.loading = false;
        this.openBlob(response.body, true);
      },
      error: err => {
        this.loading = false;
        Swal.fire('Error', err?.error?.message || 'Failed to open printable report file.', 'error');
      }
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
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
    const queryParams: Record<string, string | number> = {};
    if (item.dateFrom) queryParams['dateFrom'] = item.dateFrom;
    if (item.dateTo) queryParams['dateTo'] = item.dateTo;

    if (item.filterJson) {
      try {
        const parsed = JSON.parse(item.filterJson);
        if (parsed?.branchId) {
          queryParams['branchId'] = Number(parsed.branchId);
        }
      } catch (error) {
        console.error(error);
      }
    }

    return queryParams;
  }
}
