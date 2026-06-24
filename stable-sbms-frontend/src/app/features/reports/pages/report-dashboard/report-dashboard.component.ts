import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { ReportDashboardSummaryResponse, ReportQueryKey, formatEnumLabel } from '../../models/report.model';
import { ReportService } from '../../services/report.service';

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
  shortLabel: string;
  value: number;
  x: number;
  y: number;
}

interface DashboardTrendMeta {
  label: string;
  value: string;
  note: string;
}

interface DashboardHeatmapColumn {
  key: string;
  label: string;
}

interface DashboardHeatmapRow {
  label: string;
  shortLabel: string;
  values: number[];
}

@Component({
  selector: 'app-report-dashboard',
  templateUrl: './report-dashboard.component.html',
  styleUrls: ['./report-dashboard.component.scss']
})
export class ReportDashboardComponent implements OnInit {

  loading = false;
  summary: ReportDashboardSummaryResponse | null = null;
  usageLegend: DashboardLegendItem[] = [];
  usageColumns: DashboardColumnItem[] = [];
  exportStatusColumns: DashboardColumnItem[] = [];
  usageAxisTicks: DashboardAxisTick[] = [];
  exportAxisTicks: DashboardAxisTick[] = [];
  exportTrend: DashboardTrendPoint[] = [];
  exportTrendTicks: DashboardAxisTick[] = [];
  exportTrendPath = '';
  exportTrendAreaPath = '';
  exportTrendMeta: DashboardTrendMeta[] = [];
  matrixColumns: DashboardHeatmapColumn[] = [];
  matrixRows: DashboardHeatmapRow[] = [];
  selectedExport = this.summary?.recentExports?.[0] || null;
  reportGradient = 'conic-gradient(#14b8a6 0deg 360deg)';

  private readonly allQuickLinks: Array<{
    label: string;
    route: string;
    icon: string;
    queryKey: ReportQueryKey;
    permissionCode?: string;
    permissionCodes?: string[];
  }> = [
    { label: 'Operational Report', route: '/reports/operational', icon: 'fa fa-list-alt', queryKey: 'OPERATIONAL', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Trial Balance', route: '/reports/trial-balance', icon: 'fa fa-columns', queryKey: 'TRIAL_BALANCE', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Ledger Profit & Loss', route: '/reports/ledger-profit-loss', icon: 'fa fa-calculator', queryKey: 'LEDGER_PROFIT_LOSS', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Profit Distribution', route: '/reports/profit-distribution', icon: 'fa fa-line-chart', queryKey: 'PROFIT_DISTRIBUTION', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Management P&L', route: '/reports/management-pl', icon: 'fa fa-balance-scale', queryKey: 'MANAGEMENT_PL', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Expense Register', route: '/reports/management-expenses', icon: 'fa fa-bdt', queryKey: 'MANAGEMENT_PL', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Financing Portfolio', route: '/reports/financing-portfolio', icon: 'fa fa-bank', queryKey: 'FINANCING_PORTFOLIO', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'PAR Report', route: '/reports/par', icon: 'fa fa-exclamation-circle', queryKey: 'PAR', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Shariah Audit', route: '/reports/shariah-audit', icon: 'fa fa-balance-scale', queryKey: 'SHARIAH_AUDIT', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Branch Report', route: '/reports/branch', icon: 'fa fa-building', queryKey: 'BRANCH', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'KPI Report', route: '/reports/kpi', icon: 'fa fa-tachometer', queryKey: 'KPI', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Growth Report', route: '/reports/growth', icon: 'fa fa-area-chart', queryKey: 'GROWTH', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Loan Recovery', route: '/reports/loan-recovery', icon: 'fa fa-life-ring', queryKey: 'LOAN_RECOVERY', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Monthly Closing', route: '/reports/monthly-closing', icon: 'fa fa-calendar-check-o', queryKey: 'MONTHLY_CLOSING', permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Closing Ops', route: '/reports/monthly-closing-ops', icon: 'fa fa-tasks', queryKey: 'MONTHLY_CLOSING', permissionCodes: ['MONTHLY_CLOSING_CREATE', 'MONTHLY_CLOSING_SUBMIT', 'MONTHLY_CLOSING_APPROVE', 'MONTHLY_CLOSING_REJECT', 'MONTHLY_CLOSING_REOPEN'] }
  ];

  constructor(
    private reportService: ReportService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  get quickLinks(): Array<{ label: string; route: string; icon: string; queryKey: ReportQueryKey }> {
    return this.allQuickLinks.filter(item => {
      if (item.permissionCodes?.length) {
        return this.accessControl.hasAnyPermission(item.permissionCodes);
      }
      return this.accessControl.hasPermission(item.permissionCode);
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.reportService.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load report dashboard.', 'error');
      }
    });
  }

  openHistory(): void {
    this.router.navigate(['/reports/export-history']);
  }

  openRoute(route: string): void {
    this.router.navigate([route]);
  }

  openUsage(queryKey: ReportQueryKey): void {
    const route = this.quickLinks.find(item => item.queryKey === queryKey)?.route || '/reports/dashboard';
    this.router.navigate([route]);
  }

  focusExport(item: ReportDashboardSummaryResponse['recentExports'][number]): void {
    this.selectedExport = item;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  get selectedExportBadges(): Array<{ label: string; value: string }> {
    if (!this.selectedExport) {
      return [];
    }
    return [
      { label: 'Report Type', value: this.getLabel(this.selectedExport.reportType) },
      { label: 'Request Status', value: this.getLabel(this.selectedExport.requestStatus) },
      { label: 'Requested By', value: this.selectedExport.requestedBy || 'SYSTEM' },
      { label: 'Export Time', value: this.selectedExport.requestedAt || '-' }
    ];
  }

  get selectedExportBadgeText(): string {
    const label = this.selectedExport?.requestedBy || this.selectedExport?.reportCode || '';
    const compact = label
      .split(/[.\s_-]+/)
      .filter(Boolean)
      .slice(0, 2)
      .map(part => part.charAt(0).toUpperCase())
      .join('');
    return compact || 'RP';
  }

  private prepareDashboard(data: ReportDashboardSummaryResponse): void {
    this.usageLegend = [
      { label: 'Generated Today', value: data.generatedToday, note: 'reports generated today', color: '#22c55e' },
      { label: 'Regulatory Pending', value: data.regulatoryPending, note: 'regulatory items waiting', color: '#ef4444' },
      { label: 'Branch Summary', value: Math.round(data.branchPerformanceSummary || 0), note: 'branch metric footprint', color: '#3b82f6' },
      { label: 'Financing Summary', value: Math.round(data.financingSummary || 0), note: 'financing metric footprint', color: '#14b8a6' }
    ];
    this.reportGradient = this.buildDonutGradient(this.usageLegend);

    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    const usageMax = Math.max(...data.mostUsedReports.map(item => item.usageCount), 1);
    this.usageColumns = data.mostUsedReports.map((item, index) => ({
      label: item.reportName,
      shortLabel: this.toChartLabel(item.reportName, 14),
      value: item.usageCount,
      height: this.getShare(item.usageCount, usageMax),
      tone: tones[index % tones.length]
    }));
    this.usageAxisTicks = this.buildAxisTicks(usageMax);

    const statusMap = new Map<string, number>();
    data.recentExports.forEach(item => {
      const key = String(item.requestStatus || 'REQUESTED').toUpperCase();
      statusMap.set(key, (statusMap.get(key) || 0) + 1);
    });
    const exportStatusOrder = [
      { key: 'REQUESTED', label: 'Requested', tone: 'warning' as DashboardColumnItem['tone'] },
      { key: 'GENERATED', label: 'Generated', tone: 'info' as DashboardColumnItem['tone'] },
      { key: 'EXPORTED', label: 'Exported', tone: 'success' as DashboardColumnItem['tone'] },
      { key: 'FAILED', label: 'Failed', tone: 'danger' as DashboardColumnItem['tone'] }
    ];
    const exportStats = exportStatusOrder
      .map(item => ({
        label: item.label,
        value: statusMap.get(item.key) || 0,
        tone: item.tone
      }))
      .filter(item => item.value > 0);
    const exportMax = Math.max(...exportStats.map(item => item.value), 1);
    this.exportStatusColumns = exportStats.map(item => ({
      label: item.label,
      shortLabel: item.label,
      value: item.value,
      height: this.getShare(item.value, exportMax),
      tone: item.tone
    }));
    this.exportAxisTicks = this.buildAxisTicks(exportMax);

    const trendMap = new Map<string, number>();
    data.recentExports.forEach(item => {
      const key = (item.requestedAt || '').slice(0, 10) || 'Unknown';
      trendMap.set(key, (trendMap.get(key) || 0) + 1);
    });
    const recentDates = Array.from(trendMap.keys())
      .filter(key => key !== 'Unknown')
      .sort((a, b) => a.localeCompare(b));
    const lastKnownDate = recentDates.length
      ? new Date(`${recentDates[recentDates.length - 1]}T00:00:00`)
      : new Date();
    const trendEntries = Array.from({ length: 7 }, (_, index) => {
      const pointDate = new Date(lastKnownDate);
      pointDate.setDate(lastKnownDate.getDate() - (6 - index));
      const isoDate = pointDate.toISOString().slice(0, 10);
      return [isoDate, trendMap.get(isoDate) || 0] as [string, number];
    });
    const maxTrend = Math.max(...trendEntries.map(([, value]) => value), 1);
    this.exportTrend = trendEntries.map(([label, value], index, items) => ({
      label,
      shortLabel: this.toDayLabel(label),
      value,
      x: items.length === 1 ? 50 : (index / (items.length - 1)) * 100,
      y: 100 - this.getChartHeight(value, maxTrend)
    }));
    this.exportTrendTicks = this.buildAxisTicks(maxTrend);
    this.exportTrendPath = this.exportTrend
      .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
      .join(' ');
    this.exportTrendAreaPath = this.exportTrend.length
      ? `${this.exportTrendPath} L ${this.exportTrend[this.exportTrend.length - 1].x} 100 L ${this.exportTrend[0].x} 100 Z`
      : '';
    const busiestDay = [...this.exportTrend].sort((a, b) => b.value - a.value)[0];
    const totalTrendVolume = this.exportTrend.reduce((sum, item) => sum + item.value, 0);
    const activeDays = this.exportTrend.filter(item => item.value > 0).length;
    this.exportTrendMeta = [
      {
        label: '7-Day Volume',
        value: `${totalTrendVolume}`,
        note: 'total export requests in the current 7-day view'
      },
      {
        label: 'Peak Day',
        value: busiestDay ? `${busiestDay.shortLabel} (${busiestDay.value})` : '-',
        note: 'strongest visible request day in the recent activity window'
      },
      {
        label: 'Active Days',
        value: `${activeDays}/7`,
        note: 'days in the trend window with at least one export request'
      }
    ];

    this.matrixColumns = [
      { key: 'REQUESTED', label: 'Requested' },
      { key: 'GENERATED', label: 'Generated' },
      { key: 'EXPORTED', label: 'Exported' },
      { key: 'FAILED', label: 'Failed' }
    ];

    const typeMap = new Map<string, DashboardHeatmapRow>();
    data.recentExports.forEach(item => {
      const key = this.getLabel(item.reportType);
      if (!typeMap.has(key)) {
        typeMap.set(key, {
          label: key,
          shortLabel: this.toChartLabel(key, 12),
          values: [0, 0, 0, 0]
        });
      }
      const row = typeMap.get(key)!;
      const idx = this.matrixColumns.findIndex(column => column.key === item.requestStatus);
      if (idx >= 0) {
        row.values[idx] += 1;
      }
    });
    this.matrixRows = Array.from(typeMap.values()).slice(0, 6);

    this.selectedExport = [...data.recentExports]
      .sort((a, b) => String(b.requestedAt || '').localeCompare(String(a.requestedAt || '')))[0]
      || data.recentExports[0]
      || null;
  }

  private getShare(value: number, total: number): number {
    if (!total) return 0;
    return Math.max(6, Math.round((value / total) * 100));
  }

  private getChartHeight(value: number, total: number): number {
    if (!total) return 0;
    return Math.max(10, Math.round((value / total) * 86));
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

  private toDayLabel(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return this.toChartLabel(value, 10);
    }
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }
}
