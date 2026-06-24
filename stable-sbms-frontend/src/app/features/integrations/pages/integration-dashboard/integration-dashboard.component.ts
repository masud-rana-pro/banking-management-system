import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { IntegrationDashboardSummaryResponse, formatEnumLabel } from '../../models/integration.model';
import { IntegrationService } from '../../services/integration.service';

interface DashboardLegendItem {
  label: string;
  value: number;
  note: string;
  color: string;
}

interface DashboardBandItem {
  label: string;
  value: number;
  note: string;
  share: number;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'info';
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
  selector: 'app-integration-dashboard',
  templateUrl: './integration-dashboard.component.html',
  styleUrls: ['./integration-dashboard.component.scss']
})
export class IntegrationDashboardComponent implements OnInit {

  loading = false;
  summary: IntegrationDashboardSummaryResponse | null = null;
  integrationLegend: DashboardLegendItem[] = [];
  integrationBands: DashboardBandItem[] = [];
  providerColumns: DashboardColumnItem[] = [];
  providerAxisTicks: DashboardAxisTick[] = [];
  executionColumns: DashboardColumnItem[] = [];
  executionAxisTicks: DashboardAxisTick[] = [];
  moduleColumns: DashboardColumnItem[] = [];
  moduleAxisTicks: DashboardAxisTick[] = [];
  integrationRows: Array<{ label: string; value: string }> = [];
  integrationGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  integrationTrendPoints: DashboardTrendPoint[] = [];
  integrationTrendPath = '';
  integrationTrendAreaPath = '';
  executionHeatRows: DashboardHeatRow[] = [];
  spotlightLog = null as IntegrationDashboardSummaryResponse['recentLogs'][number] | null;

  constructor(
    private integrationService: IntegrationService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.integrationService.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load integration dashboard.', 'error');
      }
    });
  }

  openProviders(): void {
    this.router.navigate(['/integrations/providers']);
  }

  openProviderTest(): void {
    this.router.navigate(['/integrations/provider-test']);
  }

  openLogs(): void {
    this.router.navigate(['/integrations/logs']);
  }

  openRetryLogs(): void {
    this.router.navigate(['/integrations/logs'], { queryParams: { executionStatus: 'RETRY_PENDING' } });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private prepareDashboard(summary: IntegrationDashboardSummaryResponse): void {
    const denominator = Math.max(summary.activeProviders + summary.failedIntegrations + summary.retryPending, 1);
    this.integrationLegend = [
      { label: 'Active Providers', value: summary.activeProviders, note: 'providers available for live integration traffic', color: '#22c55e' },
      { label: 'Failed Integrations', value: summary.failedIntegrations, note: 'providers with failed recent execution history', color: '#ef4444' },
      { label: 'Retry Pending', value: summary.retryPending, note: 'provider calls waiting for retry', color: '#f59e0b' },
      { label: 'Success Rate', value: Math.round(summary.successRate), note: 'approximate health signal across execution history', color: '#3b82f6' }
    ];
    this.integrationGradient = this.buildDonutGradient(this.integrationLegend);

    this.integrationBands = [
      {
        label: 'Active Connectivity',
        value: summary.activeProviders,
        note: `${summary.providerTypeSummary.length} provider categories currently represented`,
        share: this.getShare(summary.activeProviders, denominator),
        tone: 'success'
      },
      {
        label: 'Failure Pressure',
        value: summary.failedIntegrations,
        note: 'watch these providers for degraded sync reliability',
        share: this.getShare(summary.failedIntegrations, denominator),
        tone: 'danger'
      },
      {
        label: 'Retry Load',
        value: summary.retryPending,
        note: `${summary.recentLogs.length} recent execution logs currently visible`,
        share: this.getShare(summary.retryPending, denominator),
        tone: 'warning'
      },
      {
        label: 'Overall Success Rate',
        value: Math.round(summary.successRate),
        note: summary.lastSuccessfulSync ? `last successful sync ${summary.lastSuccessfulSync}` : 'no successful sync timestamp available',
        share: Math.max(6, Math.round(summary.successRate)),
        tone: 'info'
      }
    ];

    const maxProvider = Math.max(...summary.providerTypeSummary.map(item => item.totalProviders), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    this.providerColumns = summary.providerTypeSummary.map((item, index) => ({
      label: this.getLabel(item.providerType),
      shortLabel: this.toChartLabel(this.getLabel(item.providerType)),
      value: item.totalProviders,
      height: this.getShare(item.totalProviders, maxProvider),
      tone: tones[index % tones.length]
    }));
    this.providerAxisTicks = this.buildAxisTicks(maxProvider);

    const executionMap = new Map<string, number>();
    summary.recentLogs.forEach(item => {
      const key = this.getLabel(item.executionStatus);
      executionMap.set(key, (executionMap.get(key) || 0) + 1);
    });
    const executionEntries = Array.from(executionMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxExecution = Math.max(...executionEntries.map(([, value]) => value), 1);
    this.executionColumns = executionEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxExecution),
      tone: tones[index % tones.length]
    }));
    this.executionAxisTicks = this.buildAxisTicks(maxExecution);

    const moduleMap = new Map<string, number>();
    summary.recentLogs.forEach(item => {
      const key = this.getLabel(item.referenceModule || 'General');
      moduleMap.set(key, (moduleMap.get(key) || 0) + 1);
    });
    const moduleEntries = Array.from(moduleMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxModule = Math.max(...moduleEntries.map(([, value]) => value), 1);
    this.moduleColumns = moduleEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxModule),
      tone: tones[index % tones.length]
    }));
    this.moduleAxisTicks = this.buildAxisTicks(maxModule);

    this.integrationTrendPoints = this.buildTrendPoints(summary);
    const trendPaths = this.buildTrendPath(this.integrationTrendPoints);
    this.integrationTrendPath = trendPaths.linePath;
    this.integrationTrendAreaPath = trendPaths.areaPath;

    this.executionHeatRows = this.buildExecutionHeatRows(summary);
    this.spotlightLog = summary.recentLogs[0] || null;

    this.integrationRows = [
      { label: 'Provider Categories', value: `${summary.providerTypeSummary.length}` },
      { label: 'Recent Execution Logs', value: `${summary.recentLogs.length}` },
      { label: 'Retry Pending', value: `${summary.retryPending}` },
      { label: 'Success Rate', value: `${summary.successRate.toFixed(2)}%` }
    ];
  }

  private getShare(value: number, total: number): number {
    if (!total) {
      return 0;
    }
    return Math.max(6, Math.round((value / total) * 100));
  }

  private buildDonutGradient(items: DashboardLegendItem[]): string {
    const total = items.reduce((sum, item) => sum + item.value, 0);
    if (!total) {
      return 'conic-gradient(#cbd5e1 0deg 360deg)';
    }

    let cursor = 0;
    const segments = items.map(item => {
      const sweep = (item.value / total) * 360;
      const start = cursor;
      const end = cursor + sweep;
      cursor = end;
      return `${item.color} ${start}deg ${end}deg`;
    });

    return `conic-gradient(${segments.join(', ')})`;
  }

  private buildAxisTicks(maxValue: number): DashboardAxisTick[] {
    const safeMax = Math.max(1, Math.ceil(maxValue));
    return [0, 25, 50, 75, 100].map(percent => ({
      label: `${Math.round((safeMax * percent) / 100)}`,
      bottom: percent
    }));
  }

  private toChartLabel(value: string): string {
    const compact = value.replace(/\s+/g, ' ').trim();
    if (!compact) {
      return 'Type';
    }
    const words = compact.split(' ');
    const readable = words.slice(0, 2).join(' ');
    return readable.length <= 14 ? readable : readable.slice(0, 14).trim();
  }

  getProviderBadgeText(log?: IntegrationDashboardSummaryResponse['recentLogs'][number] | null): string {
    const base = (log?.providerCode || log?.providerName || 'PR').trim();
    const cleaned = base.replace(/[^A-Za-z0-9 ]/g, ' ').trim();
    if (!cleaned) {
      return 'PR';
    }
    const words = cleaned.split(/\s+/).filter(Boolean);
    if (words.length >= 2) {
      return `${words[0][0]}${words[1][0]}`.toUpperCase();
    }
    return cleaned.slice(0, 2).toUpperCase();
  }

  selectSpotlightByStatus(statusLabel: string): void {
    if (!this.summary) {
      return;
    }
    const matched = this.summary.recentLogs.find(item => this.getLabel(item.executionStatus) === statusLabel);
    if (matched) {
      this.spotlightLog = matched;
    }
  }

  private buildTrendPoints(summary: IntegrationDashboardSummaryResponse): DashboardTrendPoint[] {
    const labels = this.buildRecentDayLabels();
    const counts = labels.map(() => 0);
    summary.recentLogs.forEach(item => {
      const raw = item.executedAt || item.createdAt || item.updatedAt || '';
      const date = raw ? new Date(raw) : null;
      if (!date || Number.isNaN(date.getTime())) {
        return;
      }
      const idx = labels.findIndex(label => label.dateKey === this.toDateKey(date));
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

  private buildExecutionHeatRows(summary: IntegrationDashboardSummaryResponse): DashboardHeatRow[] {
    const providerLabels = summary.providerTypeSummary
      .map(item => this.getLabel(item.providerType))
      .filter(Boolean)
      .slice(0, 5);

    const rows = providerLabels.map(label => {
      const logs = summary.recentLogs.filter(item => this.getLabel(item.providerType) === label);
      const success = logs.filter(item => item.executionStatus === 'SUCCESS').length;
      const failed = logs.filter(item => item.executionStatus === 'FAILED').length;
      const retry = logs.filter(item => item.executionStatus === 'RETRY_PENDING').length;
      return {
        label,
        counts: [
          { label: 'Success', value: success },
          { label: 'Failed', value: failed },
          { label: 'Retry', value: retry }
        ]
      };
    });

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
}
