import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { SecurityDashboardSummaryResponse, formatEnumLabel } from '../../models/security.model';
import { SecurityService } from '../../services/security.service';

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
  selector: 'app-security-dashboard',
  templateUrl: './security-dashboard.component.html',
  styleUrls: ['./security-dashboard.component.scss']
})
export class SecurityDashboardComponent implements OnInit {

  loading = false;
  summary: SecurityDashboardSummaryResponse | null = null;
  securityLegend: DashboardLegendItem[] = [];
  securityBands: DashboardBandItem[] = [];
  severityColumns: DashboardColumnItem[] = [];
  caseColumns: DashboardColumnItem[] = [];
  moduleColumns: DashboardColumnItem[] = [];
  severityAxisTicks: DashboardAxisTick[] = [];
  caseAxisTicks: DashboardAxisTick[] = [];
  moduleAxisTicks: DashboardAxisTick[] = [];
  securityRows: Array<{ label: string; value: string }> = [];
  securityGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  userImageMap: Record<string, string> = {};
  securityTrendPoints: DashboardTrendPoint[] = [];
  securityTrendPath = '';
  securityTrendAreaPath = '';
  controlHeatRows: DashboardHeatRow[] = [];
  spotlightCase = null as SecurityDashboardSummaryResponse['recentInvestigationCases'][number] | null;

  constructor(
    private securityService: SecurityService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.load();
  }

  load(): void {
    this.loading = true;
    this.securityService.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load security dashboard.', 'error');
      }
    });
  }

  open(route: string): void {
    this.router.navigate([route]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getUserImageUrl(username?: string | null): string {
    const key = (username || '').trim().toLowerCase();
    return key ? this.userImageMap[key] || '' : '';
  }

  getSpotlightImageUrl(): string {
    if (!this.spotlightCase) {
      return '';
    }
    return this.getUserImageUrl(this.spotlightCase.assignedUsername || this.spotlightCase.openedBy);
  }

  selectSpotlightCaseByType(caseTypeLabel: string): void {
    if (!this.summary) {
      return;
    }
    const selected = this.summary.recentInvestigationCases.find(item => this.getLabel(item.caseType) === caseTypeLabel);
    if (selected) {
      this.spotlightCase = selected;
    }
  }

  private prepareDashboard(summary: SecurityDashboardSummaryResponse): void {
    const denominator = Math.max(summary.failedLoginsToday + summary.lockedUsers + summary.suspiciousTxnCount + summary.openInvestigationCases, 1);
    this.securityLegend = [
      { label: 'Failed Logins', value: summary.failedLoginsToday, note: 'today’s authentication failures', color: '#ef4444' },
      { label: 'Locked Users', value: summary.lockedUsers, note: 'accounts currently under lock control', color: '#f59e0b' },
      { label: 'Suspicious Transactions', value: summary.suspiciousTxnCount, note: 'transactions requiring analyst scrutiny', color: '#3b82f6' },
      { label: 'Open Cases', value: summary.openInvestigationCases, note: 'investigation work still unresolved', color: '#22c55e' }
    ];
    this.securityGradient = this.buildDonutGradient(this.securityLegend);

    this.securityBands = [
      {
        label: 'Authentication Pressure',
        value: summary.failedLoginsToday,
        note: 'watch for login anomalies or credential attack patterns',
        share: this.getShare(summary.failedLoginsToday, denominator),
        tone: 'danger'
      },
      {
        label: 'Lock Control Load',
        value: summary.lockedUsers,
        note: 'users currently held by security lock policies',
        share: this.getShare(summary.lockedUsers, denominator),
        tone: 'warning'
      },
      {
        label: 'Investigation Queue',
        value: summary.openInvestigationCases,
        note: `${summary.recentInvestigationCases.length} recent cases visible in the dashboard`,
        share: this.getShare(summary.openInvestigationCases, denominator),
        tone: 'success'
      },
      {
        label: 'AML and Suspicion',
        value: summary.amlFlagsToday + summary.suspiciousTxnCount,
        note: 'combined pressure from AML and suspicious transaction events',
        share: this.getShare(summary.amlFlagsToday + summary.suspiciousTxnCount, Math.max(summary.amlFlagsToday + summary.suspiciousTxnCount, denominator)),
        tone: 'info'
      }
    ];

    const severityMap = new Map<string, number>();
    summary.recentSecurityEvents.forEach(item => {
      const key = this.getLabel(item.severityLevel);
      severityMap.set(key, (severityMap.get(key) || 0) + 1);
    });
    const severityEntries = Array.from(severityMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxSeverity = Math.max(...severityEntries.map(([, value]) => value), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['danger', 'warning', 'info', 'success', 'primary'];
    this.severityColumns = severityEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxSeverity),
      tone: tones[index % tones.length]
    }));
    this.severityAxisTicks = this.buildAxisTicks(maxSeverity);

    const caseMap = new Map<string, number>();
    summary.recentInvestigationCases.forEach(item => {
      const key = this.getLabel(item.caseType);
      caseMap.set(key, (caseMap.get(key) || 0) + 1);
    });
    const caseEntries = Array.from(caseMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxCase = Math.max(...caseEntries.map(([, value]) => value), 1);
    this.caseColumns = caseEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxCase),
      tone: ['primary', 'warning', 'danger', 'info', 'success'][index % 5] as DashboardColumnItem['tone']
    }));
    this.caseAxisTicks = this.buildAxisTicks(maxCase);

    const moduleMap = new Map<string, number>();
    summary.recentSecurityEvents.forEach(item => {
      const key = this.getLabel(item.referenceModule || 'Security Events');
      moduleMap.set(key, (moduleMap.get(key) || 0) + 1);
    });
    summary.recentInvestigationCases.forEach(item => {
      const key = this.getLabel(item.referenceModule || 'Investigations');
      moduleMap.set(key, (moduleMap.get(key) || 0) + 1);
    });
    summary.recentAuditLogs.forEach(item => {
      const key = this.getLabel(item.moduleName || 'Audit');
      moduleMap.set(key, (moduleMap.get(key) || 0) + 1);
    });
    const moduleEntries = Array.from(moduleMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxModule = Math.max(...moduleEntries.map(([, value]) => value), 1);
    this.moduleColumns = moduleEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxModule),
      tone: ['info', 'primary', 'warning', 'success', 'danger'][index % 5] as DashboardColumnItem['tone']
    }));
    this.moduleAxisTicks = this.buildAxisTicks(maxModule);

    this.securityTrendPoints = this.buildTrendPoints(summary);
    const trendPath = this.buildTrendPath(this.securityTrendPoints);
    this.securityTrendPath = trendPath.linePath;
    this.securityTrendAreaPath = trendPath.areaPath;

    this.controlHeatRows = this.buildControlHeatRows(summary);
    this.spotlightCase = summary.recentInvestigationCases.find(item => item.caseStatus !== 'CLOSED') || summary.recentInvestigationCases[0] || null;

    this.securityRows = [
      { label: 'Audit Events Today', value: `${summary.auditEventsToday}` },
      { label: 'AML Flags Today', value: `${summary.amlFlagsToday}` },
      { label: 'Recent Security Events', value: `${summary.recentSecurityEvents.length}` },
      { label: 'Recent Audit Logs', value: `${summary.recentAuditLogs.length}` }
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

  private buildTrendPoints(summary: SecurityDashboardSummaryResponse): DashboardTrendPoint[] {
    const labels = this.buildRecentDayLabels();
    const counts = labels.map(() => 0);
    const allDates = [
      ...summary.recentSecurityEvents.map(item => item.eventTime || ''),
      ...summary.recentInvestigationCases.map(item => item.openedAt || item.createdAt || ''),
      ...summary.recentAuditLogs.map(item => item.performedAt || '')
    ];
    allDates.forEach(raw => {
      const date = raw ? new Date(raw) : null;
      if (!date || Number.isNaN(date.getTime())) {
        return;
      }
      const idx = this.findDayIndex(labels, date);
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

  private buildControlHeatRows(summary: SecurityDashboardSummaryResponse): DashboardHeatRow[] {
    const moduleOrder = Array.from(
      new Set(
        [
          ...summary.recentSecurityEvents.map(item => this.getLabel(item.referenceModule || 'Security Events')),
          ...summary.recentInvestigationCases.map(item => this.getLabel(item.referenceModule || 'Investigations')),
          ...summary.recentAuditLogs.map(item => this.getLabel(item.moduleName || 'Audit'))
        ].filter(Boolean)
      )
    ).slice(0, 5);

    const rows = moduleOrder.map(label => {
      const eventCount = summary.recentSecurityEvents.filter(item => this.getLabel(item.referenceModule || 'Security Events') === label).length;
      const caseCount = summary.recentInvestigationCases.filter(item => this.getLabel(item.referenceModule || 'Investigations') === label).length;
      const auditCount = summary.recentAuditLogs.filter(item => this.getLabel(item.moduleName || 'Audit') === label).length;
      return {
        label,
        counts: [
          { label: 'Events', value: eventCount },
          { label: 'Cases', value: caseCount },
          { label: 'Audits', value: auditCount }
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
      labels.push({
        short: formatter.format(date),
        dateKey: this.toDateKey(date)
      });
    }
    return labels;
  }

  private findDayIndex(labels: Array<{ short: string; dateKey: string }>, date: Date): number {
    return labels.findIndex(item => item.dateKey === this.toDateKey(date));
  }

  private toDateKey(date: Date): string {
    const y = date.getFullYear();
    const m = `${date.getMonth() + 1}`.padStart(2, '0');
    const d = `${date.getDate()}`.padStart(2, '0');
    return `${y}-${m}-${d}`;
  }

  private loadUsers(): void {
    this.userApi.getAll().subscribe({
      next: users => {
        this.userImageMap = this.buildUserImageMap(users || []);
      },
      error: () => {
        this.userImageMap = {};
      }
    });
  }

  private buildUserImageMap(users: UserResponse[]): Record<string, string> {
    return users.reduce<Record<string, string>>((acc, user) => {
      if (user.username && user.profileImageName) {
        acc[user.username.trim().toLowerCase()] = this.fileUploadService.resolveImageUrl(user.profileImageName);
      }
      return acc;
    }, {});
  }
}
