import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { NotificationDashboardSummaryResponse, formatEnumLabel } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';

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
  shortLabel: string;
  value: number;
  x: number;
  y: number;
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
  selector: 'app-notification-dashboard',
  templateUrl: './notification-dashboard.component.html',
  styleUrls: ['./notification-dashboard.component.scss']
})
export class NotificationDashboardComponent implements OnInit {

  loading = false;
  summary: NotificationDashboardSummaryResponse | null = null;
  notificationLegend: DashboardLegendItem[] = [];
  notificationBands: DashboardBandItem[] = [];
  channelColumns: DashboardColumnItem[] = [];
  failureColumns: DashboardColumnItem[] = [];
  channelAxisTicks: DashboardAxisTick[] = [];
  failureAxisTicks: DashboardAxisTick[] = [];
  notificationRows: Array<{ label: string; value: string }> = [];
  deliveryTrend: DashboardTrendPoint[] = [];
  deliveryTrendTicks: DashboardAxisTick[] = [];
  deliveryTrendPath = '';
  deliveryTrendAreaPath = '';
  matrixColumns: DashboardHeatmapColumn[] = [];
  matrixRows: DashboardHeatmapRow[] = [];
  selectedLog = this.summary?.recentLogs?.[0] || null;
  notificationGradient = 'conic-gradient(#14b8a6 0deg 360deg)';

  constructor(
    private notificationService: NotificationService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.notificationService.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load notification dashboard.', 'error');
      }
    });
  }

  openTemplates(): void {
    this.router.navigate(['/notifications/templates']);
  }

  openEvents(): void {
    this.router.navigate(['/notifications/event-rules']);
  }

  openLogs(): void {
    this.router.navigate(['/notifications/logs']);
  }

  openRetryQueue(): void {
    this.router.navigate(['/notifications/retry-queue']);
  }

  openIntegrations(): void {
    this.router.navigate(['/integrations/dashboard']);
  }

  focusLog(log: NotificationDashboardSummaryResponse['recentLogs'][number]): void {
    this.selectedLog = log;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  get selectedLogBadges(): Array<{ label: string; value: string }> {
    if (!this.selectedLog) {
      return [];
    }
    return [
      { label: 'Channel', value: this.getLabel(this.selectedLog.channelType) },
      { label: 'Delivery Status', value: this.getLabel(this.selectedLog.deliveryStatus) },
      { label: 'Retry Count', value: `${this.selectedLog.retryCount || 0}` },
      { label: 'Template', value: this.selectedLog.templateCode || '-' }
    ];
  }

  get selectedLogBadgeText(): string {
    const label = this.getLabel(this.selectedLog?.channelType);
    return label.slice(0, 2).toUpperCase() || 'NT';
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  canAny(permissionCodes: string[]): boolean {
    return this.accessControl.hasAnyPermission(permissionCodes);
  }

  private prepareDashboard(summary: NotificationDashboardSummaryResponse): void {
    const denominator = Math.max(summary.messagesSentToday + summary.failedToday + summary.retryQueue, 1);
    this.notificationLegend = [
      { label: 'Sent Today', value: summary.sentToday, note: 'successful deliveries completed today', color: '#22c55e' },
      { label: 'Failed Today', value: summary.failedToday, note: 'deliveries rejected or failed today', color: '#ef4444' },
      { label: 'Retry Queue', value: summary.retryQueue, note: 'messages still waiting for replay', color: '#f59e0b' },
      { label: 'Failed Deliveries', value: summary.failedDeliveries, note: 'overall failed delivery footprint', color: '#3b82f6' }
    ];
    this.notificationGradient = this.buildDonutGradient(this.notificationLegend);

    this.notificationBands = [
      {
        label: 'Successful Throughput',
        value: summary.sentToday,
        note: `${summary.messagesSentToday} messages processed through configured providers`,
        share: this.getShare(summary.sentToday, denominator),
        tone: 'success'
      },
      {
        label: 'Failure Pressure',
        value: summary.failedToday,
        note: 'track provider failure spikes before customer impact grows',
        share: this.getShare(summary.failedToday, denominator),
        tone: 'danger'
      },
      {
        label: 'Retry Load',
        value: summary.retryQueue,
        note: 'watch replay queue for delayed customer notifications',
        share: this.getShare(summary.retryQueue, denominator),
        tone: 'warning'
      },
      {
        label: 'Historical Failure Book',
        value: summary.failedDeliveries,
        note: `${summary.recentLogs.length} recent attempts visible in the activity feed`,
        share: this.getShare(summary.failedDeliveries, Math.max(summary.failedDeliveries, denominator)),
        tone: 'info'
      }
    ];

    const maxSent = Math.max(...summary.channelWiseSummary.map(item => item.sentCount), 1);
    const maxFailed = Math.max(...summary.channelWiseSummary.map(item => Math.max(item.failedCount, item.retryQueuedCount)), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning'];
    this.channelColumns = summary.channelWiseSummary.map((item, index) => ({
      label: this.getLabel(item.channelType),
      shortLabel: this.getLabel(item.channelType),
      value: item.sentCount,
      height: this.getShare(item.sentCount, maxSent),
      tone: tones[index % tones.length]
    }));
    this.failureColumns = summary.channelWiseSummary.map((item, index) => ({
      label: `${this.getLabel(item.channelType)} Failure`,
      shortLabel: this.getLabel(item.channelType),
      value: item.failedCount + item.retryQueuedCount,
      height: this.getShare(item.failedCount + item.retryQueuedCount, maxFailed),
      tone: ['danger', 'warning', 'info'][index % 3] as DashboardColumnItem['tone']
    }));
    this.channelAxisTicks = this.buildAxisTicks(maxSent);
    this.failureAxisTicks = this.buildAxisTicks(maxFailed);

    const trendMap = new Map<string, number>();
    summary.recentLogs.forEach(item => {
      const key = item.sentAt || item.createdAt || 'Unknown';
      trendMap.set(key, (trendMap.get(key) || 0) + 1);
    });
    const trendEntries = Array.from(trendMap.entries())
      .sort((a, b) => a[0].localeCompare(b[0]))
      .slice(-7);
    const maxTrend = Math.max(...trendEntries.map(([, value]) => value), 1);
    this.deliveryTrend = trendEntries.map(([label, value], index, items) => ({
      label,
      shortLabel: this.toDayLabel(label),
      value,
      x: items.length === 1 ? 50 : (index / (items.length - 1)) * 100,
      y: 100 - this.getChartHeight(value, maxTrend)
    }));
    this.deliveryTrendTicks = this.buildAxisTicks(maxTrend);
    this.deliveryTrendPath = this.deliveryTrend
      .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
      .join(' ');
    this.deliveryTrendAreaPath = this.deliveryTrend.length
      ? `${this.deliveryTrendPath} L ${this.deliveryTrend[this.deliveryTrend.length - 1].x} 100 L ${this.deliveryTrend[0].x} 100 Z`
      : '';

    this.matrixColumns = [
      { key: 'SENT', label: 'Sent' },
      { key: 'FAILED', label: 'Failed' },
      { key: 'RETRY_QUEUED', label: 'Retry Queued' }
    ];
    this.matrixRows = summary.channelWiseSummary.map(item => ({
      label: this.getLabel(item.channelType),
      shortLabel: this.getLabel(item.channelType),
      values: [
        item.sentCount,
        item.failedCount,
        item.retryQueuedCount
      ]
    }));

    this.selectedLog = [...summary.recentLogs]
      .sort((a, b) => (b.retryCount || 0) - (a.retryCount || 0))[0]
      || summary.recentLogs[0]
      || null;

    this.notificationRows = [
      { label: 'Active Channels', value: `${summary.channelWiseSummary.length}` },
      { label: 'Recent Log Feed', value: `${summary.recentLogs.length}` },
      { label: 'Messages Sent Today', value: `${summary.messagesSentToday}` },
      { label: 'Retry Queue', value: `${summary.retryQueue}` }
    ];
  }

  private getShare(value: number, total: number): number {
    if (!total) {
      return 0;
    }
    return Math.max(6, Math.round((value / total) * 100));
  }

  private getChartHeight(value: number, total: number): number {
    if (!total) {
      return 0;
    }
    return Math.max(10, Math.round((value / total) * 86));
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

  private toDayLabel(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value.slice(0, 10) || 'N/A';
    }
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }
}
