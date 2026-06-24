import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from '../../customer/models/customer.model';
import { CustomerService } from '../../customer/services/customer.service';
import { StatementDashboardSummaryResponse, formatEnumLabel } from '../models/statement.model';
import { StatementService } from '../services/statement.service';

interface DashboardLegendItem {
  label: string;
  value: number;
  note: string;
  color: string;
}

interface DashboardColumnItem {
  label: string;
  shortLabel: string;
  value: number;
  height: number;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

interface DashboardAxisTick {
  label: string;
  bottom: number;
}

interface DashboardTrendPoint {
  label: string;
  value: number;
  x: number;
  y: number;
}

interface DashboardHeatCell {
  label: string;
  value: number;
  intensity: number;
}

interface DashboardHeatRow {
  label: string;
  cells: DashboardHeatCell[];
}

@Component({
  selector: 'app-statement-dashboard',
  templateUrl: './statement-dashboard.component.html',
  styleUrls: ['./statement-dashboard.component.scss']
})
export class StatementDashboardComponent implements OnInit {

  loading = false;
  summary: StatementDashboardSummaryResponse | null = null;
  requestLegend: DashboardLegendItem[] = [];
  requestTypeColumns: DashboardColumnItem[] = [];
  requestAxisTicks: DashboardAxisTick[] = [];
  branchColumns: DashboardColumnItem[] = [];
  branchAxisTicks: DashboardAxisTick[] = [];
  statementGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  customerImageMap: Record<number, string> = {};
  statementTrendPoints: DashboardTrendPoint[] = [];
  statementTrendPath = '';
  statementTrendAreaPath = '';
  statementHeatRows: DashboardHeatRow[] = [];
  spotlightRequest: StatementDashboardSummaryResponse['recentCustomerRequests'][number] | null = null;

  constructor(
    private statementApi: StatementService,
    private customerApi: CustomerService,
    private fileUploadService: FileUploadService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.load();
  }

  load(): void {
    this.loading = true;
    this.statementApi.getDashboardSummary().subscribe({
      next: summary => {
        this.summary = summary;
        this.prepareDashboard(summary);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load statement dashboard.', 'error');
      }
    });
  }

  openCustomerList(): void {
    this.router.navigate(['/statement/customer/list']);
  }

  openBranchList(): void {
    this.router.navigate(['/statement/branch/list']);
  }

  openExportCenter(): void {
    this.router.navigate(['/statement/export-center']);
  }

  openCustomerRequest(): void {
    this.router.navigate(['/statement/customer/request']);
  }

  openBranchRequest(): void {
    this.router.navigate(['/statement/branch/request']);
  }

  openCustomerView(id: number): void {
    this.router.navigate(['/statement/customer', id]);
  }

  openBranchView(id: number): void {
    this.router.navigate(['/statement/branch', id]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getCustomerImageUrl(customerId?: number | null): string {
    if (!customerId) {
      return '';
    }
    return this.customerImageMap[customerId] || '';
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  selectSpotlightRequest(requestId: number): void {
    if (!this.summary) {
      return;
    }
    const match = this.summary.recentCustomerRequests.find(item => item.id === requestId);
    if (match) {
      this.spotlightRequest = match;
    }
  }

  private prepareDashboard(summary: StatementDashboardSummaryResponse): void {
    this.requestLegend = [
      { label: 'Generated Today', value: summary.statementsGeneratedToday, note: 'files produced in the current day', color: '#22c55e' },
      { label: 'Customer Requests', value: summary.customerStatementRequests, note: 'customer-driven statement demand', color: '#14b8a6' },
      { label: 'Branch Requests', value: summary.branchStatementRequests, note: 'branch operations statement requests', color: '#f59e0b' },
      { label: 'Downloads', value: summary.exportDownloadCounts, note: 'retrievals from generated export inventory', color: '#3b82f6' }
    ];
    this.statementGradient = this.buildDonutGradient(this.requestLegend);

    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    const maxCount = Math.max(...summary.mostRequestedStatementTypes.map(item => item.count), 1);
    this.requestTypeColumns = summary.mostRequestedStatementTypes.map((item, index) => ({
      label: this.getLabel(item.label),
      shortLabel: this.toChartLabel(this.getLabel(item.label), 14),
      value: item.count,
      height: this.getShare(item.count, maxCount),
      tone: tones[index % tones.length]
    }));
    this.requestAxisTicks = this.buildAxisTicks(maxCount);

    const branchMap = new Map<string, number>();
    summary.recentBranchRequests.forEach(item => {
      const key = item.branchName || item.branchCode || 'Branch';
      branchMap.set(key, (branchMap.get(key) || 0) + 1);
    });
    const branchEntries = Array.from(branchMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxBranch = Math.max(...branchEntries.map(([, value]) => value), 1);
    this.branchColumns = branchEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label, 16),
      value,
      height: this.getShare(value, maxBranch),
      tone: tones[index % tones.length]
    }));
    this.branchAxisTicks = this.buildAxisTicks(maxBranch);

    this.statementTrendPoints = this.buildTrendPoints(summary);
    const trendPaths = this.buildTrendPath(this.statementTrendPoints);
    this.statementTrendPath = trendPaths.linePath;
    this.statementTrendAreaPath = trendPaths.areaPath;

    this.statementHeatRows = this.buildStatementHeatRows(summary);
    this.spotlightRequest = summary.recentCustomerRequests[0] || null;
  }

  private getShare(value: number, total: number): number {
    if (!total) return 0;
    return Math.max(6, Math.round((value / total) * 100));
  }

  private buildDonutGradient(items: DashboardLegendItem[]): string {
    const total = items.reduce((sum, item) => sum + item.value, 0);
    if (!total) return 'conic-gradient(#cbd5e1 0deg 360deg)';
    let cursor = 0;
    return `conic-gradient(${items.map(item => {
      const sweep = (item.value / total) * 360;
      const start = cursor;
      const end = cursor + sweep;
      cursor = end;
      return `${item.color} ${start}deg ${end}deg`;
    }).join(', ')})`;
  }

  private buildAxisTicks(maxValue: number): DashboardAxisTick[] {
    const safeMax = Math.max(1, Math.ceil(maxValue));
    return [0, 25, 50, 75, 100].map(percent => ({
      label: `${Math.round((safeMax * percent) / 100)}`,
      bottom: percent
    }));
  }

  private toChartLabel(value: string, limit = 14): string {
    const compact = String(value || '').replace(/\s+/g, ' ').trim();
    if (!compact) return 'N/A';
    return compact.length <= limit ? compact : compact.slice(0, limit).trim();
  }

  private buildTrendPoints(summary: StatementDashboardSummaryResponse): DashboardTrendPoint[] {
    const labels = this.buildRecentDayLabels();
    const counts = labels.map(() => 0);
    const dates = [
      ...summary.recentCustomerRequests.map(item => item.requestedAt || item.generatedAt || ''),
      ...summary.recentBranchRequests.map(item => item.requestedAt || item.generatedAt || '')
    ];

    dates.forEach(raw => {
      const date = raw ? new Date(raw) : null;
      if (!date || Number.isNaN(date.getTime())) {
        return;
      }
      const idx = labels.findIndex(item => item.dateKey === this.toDateKey(date));
      if (idx >= 0) {
        counts[idx] += 1;
      }
    });

    const maxValue = Math.max(...counts, 1);
    const startX = 38;
    const endX = 592;
    const baseY = 188;
    const topY = 42;
    return labels.map((label, index) => {
      const x = startX + ((endX - startX) / Math.max(labels.length - 1, 1)) * index;
      const value = counts[index];
      const y = baseY - ((baseY - topY) * value) / maxValue;
      return { label: label.short, value, x, y };
    });
  }

  private buildTrendPath(points: DashboardTrendPoint[]): { linePath: string; areaPath: string } {
    if (!points.length) {
      return { linePath: '', areaPath: '' };
    }
    const linePath = points.map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`).join(' ');
    const first = points[0];
    const last = points[points.length - 1];
    const areaPath = `${linePath} L ${last.x} 188 L ${first.x} 188 Z`;
    return { linePath, areaPath };
  }

  private buildStatementHeatRows(summary: StatementDashboardSummaryResponse): DashboardHeatRow[] {
    const rows = [
      {
        label: 'Customer Desk',
        counts: [
          { label: 'Requested', value: summary.customerStatementRequests },
          { label: 'Generated', value: summary.recentCustomerRequests.filter(item => item.requestStatus === 'GENERATED').length },
          { label: 'Downloaded', value: summary.recentCustomerRequests.filter(item => item.requestStatus === 'DOWNLOADED').length }
        ]
      },
      {
        label: 'Branch Desk',
        counts: [
          { label: 'Requested', value: summary.branchStatementRequests },
          { label: 'Generated', value: summary.recentBranchRequests.filter(item => item.requestStatus === 'GENERATED').length },
          { label: 'Downloaded', value: summary.recentBranchRequests.filter(item => item.requestStatus === 'DOWNLOADED').length }
        ]
      }
    ];
    const max = Math.max(...rows.flatMap(row => row.counts.map(cell => cell.value)), 1);
    return rows.map(row => ({
      label: row.label,
      cells: row.counts.map(cell => ({
        label: cell.label,
        value: cell.value,
        intensity: Math.max(0.18, cell.value / max)
      }))
    }));
  }

  private buildRecentDayLabels(): Array<{ short: string; dateKey: string }> {
    const formatter = new Intl.DateTimeFormat('en-US', { weekday: 'short' });
    const labels: Array<{ short: string; dateKey: string }> = [];
    for (let offset = 6; offset >= 0; offset -= 1) {
      const date = new Date();
      date.setHours(0, 0, 0, 0);
      date.setDate(date.getDate() - offset);
      labels.push({ short: formatter.format(date), dateKey: this.toDateKey(date) });
    }
    return labels;
  }

  private toDateKey(date: Date): string {
    const y = date.getFullYear();
    const m = `${date.getMonth() + 1}`.padStart(2, '0');
    const d = `${date.getDate()}`.padStart(2, '0');
    return `${y}-${m}-${d}`;
  }

  private loadCustomers(): void {
    this.customerApi.getAll().subscribe({
      next: customers => {
        this.customerImageMap = this.buildCustomerImageMap(customers || []);
      },
      error: () => {
        this.customerImageMap = {};
      }
    });
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, customer) => {
      if (customer.id && customer.profileImageName) {
        acc[customer.id] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<number, string>);
  }
}
