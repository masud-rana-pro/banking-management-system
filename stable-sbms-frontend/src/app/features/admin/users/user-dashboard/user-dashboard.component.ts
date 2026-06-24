import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserDashboardSummary } from '../model/user.model';
import { UserApiService } from '../service/user-api.service';

interface DashboardLegendItem {
  label: string;
  value: number;
  note: string;
  color: string;
}

interface DashboardBandItem {
  label: string;
  value: number;
  share: number;
  note: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

interface DashboardColumnItem {
  label: string;
  shortLabel: string;
  value: number;
  height: number;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

interface DashboardAxisTick {
  label: string;
  bottom: number;
}

interface DashboardTimelineItem {
  title: string;
  subtitle: string;
  meta: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
  userId?: number;
  roleLabel?: string;
  branchLabel?: string;
}

interface DashboardInfoRow {
  label: string;
  value: string;
}

interface DashboardLinePoint {
  label: string;
  value: number;
  shortLabel: string;
}

interface DashboardHeatCell {
  label: string;
  value: number;
  share: number;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

interface DashboardHeatRow {
  label: string;
  cells: DashboardHeatCell[];
}

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.scss']
})
export class UserDashboardComponent implements OnInit {
  summary: UserDashboardSummary | null = null;
  loading = false;
  userHealthBands: DashboardBandItem[] = [];
  userStatusLegend: DashboardLegendItem[] = [];
  roleColumns: DashboardColumnItem[] = [];
  roleAxisTicks: DashboardAxisTick[] = [];
  recentTimeline: DashboardTimelineItem[] = [];
  securityRows: DashboardInfoRow[] = [];
  userStatusGradient = 'conic-gradient(#dbe4f0 0 100%)';
  branchCurvePoints: DashboardLinePoint[] = [];
  branchAxisTicks: DashboardAxisTick[] = [];
  accessHeatRows: DashboardHeatRow[] = [];
  selectedRoleLabel = '';
  selectedBranchLabel = '';
  selectedUserId: number | null = null;

  constructor(
    private api: UserApiService,
    private router: Router,
    public access: AccessControlService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.api.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load user dashboard.', 'error');
      }
    });
  }

  open(route: string): void {
    this.router.navigate([route]);
  }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }

  get activeRoleColumn(): DashboardColumnItem | null {
    return this.roleColumns.find(item => item.label === this.selectedRoleLabel) || this.roleColumns[0] || null;
  }

  get activeBranchPoint(): DashboardLinePoint | null {
    return this.branchCurvePoints.find(item => item.label === this.selectedBranchLabel) || this.branchCurvePoints[0] || null;
  }

  get selectedRecentUser(): UserDashboardSummary['recentLogins'][number] | null {
    return this.summary?.recentLogins.find(user => user.id === this.selectedUserId) || this.summary?.recentLogins[0] || null;
  }

  selectRoleColumn(item: DashboardColumnItem): void {
    this.selectedRoleLabel = item.label;
    const match = this.summary?.recentLogins.find(user => (user.roleName || user.roleCode || 'Unassigned role') === item.label);
    if (match?.id) {
      this.selectedUserId = match.id;
    }
  }

  selectBranchPoint(item: DashboardLinePoint): void {
    this.selectedBranchLabel = item.label;
    const match = this.summary?.recentLogins.find(user => (user.branchName || 'Unassigned Branch') === item.label);
    if (match?.id) {
      this.selectedUserId = match.id;
    }
  }

  selectRecentUser(userId?: number | null): void {
    this.selectedUserId = userId ?? null;
  }

  private prepareDashboard(data: UserDashboardSummary): void {
    const total = Math.max(data.totalUsers, 1);
    const active = data.activeUsers || 0;
    const locked = data.lockedUsers || 0;
    const openUsers = Math.max(active - locked, 0);
    const inactive = Math.max(data.totalUsers - active, 0);
    const branchCoverage = data.usersByBranch.reduce((sum, item) => sum + Number(item.count || 0), 0);
    const roleCoverage = data.usersByRole.reduce((sum, item) => sum + Number(item.count || 0), 0);

    this.userHealthBands = [
      {
        label: 'Active user base',
        value: active,
        share: this.getShare(active, total),
        note: 'Users currently able to enter the system.',
        tone: 'green'
      },
      {
        label: 'Locked user pool',
        value: locked,
        share: this.getShare(locked, total),
        note: 'Security-locked entries needing admin attention.',
        tone: 'amber'
      },
      {
        label: 'Inactive user records',
        value: inactive,
        share: this.getShare(inactive, total),
        note: 'Dormant users outside the live workforce.',
        tone: 'red'
      },
      {
        label: 'Open and usable accounts',
        value: openUsers,
        share: this.getShare(openUsers, total),
        note: 'Users ready for role-based business execution.',
        tone: 'teal'
      }
    ];

    this.userStatusLegend = [
      {
        label: 'Active users',
        value: active,
        note: 'Currently active in the system.',
        color: '#0f766e'
      },
      {
        label: 'Locked users',
        value: locked,
        note: 'Restricted by security or failed login pressure.',
        color: '#d97706'
      },
      {
        label: 'Inactive users',
        value: inactive,
        note: 'No active system participation.',
        color: '#dc2626'
      },
      {
        label: 'Open users',
        value: openUsers,
        note: 'Active and not locked.',
        color: '#2563eb'
      }
    ];
    this.userStatusGradient = this.buildDonutGradient(this.userStatusLegend);

    const chartSource = [...data.usersByRole].sort((left, right) => right.count - left.count).slice(0, 6);
    const maxValue = Math.max(1, ...chartSource.map(item => Number(item.count || 0)));
    const tones: Array<DashboardColumnItem['tone']> = ['teal', 'green', 'blue', 'amber', 'purple', 'red'];
    this.roleColumns = chartSource.map((item, index) => ({
      label: item.label,
      shortLabel: this.toChartLabel(item.label),
      value: Number(item.count || 0),
      height: Math.max(12, Math.round((Number(item.count || 0) / maxValue) * 100)),
      tone: tones[index % tones.length]
    }));
    this.roleAxisTicks = this.buildAxisTicks(maxValue);

    const branchSource = [...data.usersByBranch].sort((left, right) => right.count - left.count).slice(0, 6);
    const maxBranchValue = Math.max(1, ...branchSource.map(item => Number(item.count || 0)));
    this.branchCurvePoints = branchSource.map(item => ({
      label: this.toBranchChartName(item.label),
      shortLabel: this.toChartLabel(this.toBranchChartName(item.label)),
      value: Number(item.count || 0)
    }));
    this.branchAxisTicks = this.buildAxisTicks(maxBranchValue);

    this.recentTimeline = data.recentLogins.slice(0, 6).map(user => ({
      title: user.fullName || user.username,
      subtitle: `${user.roleName || 'Unassigned role'} | ${user.branchName || 'No branch mapped'}`,
      meta: user.lastLoginAt || 'No recent login timestamp',
      tone: user.locked ? 'red' : 'green',
      userId: user.id,
      roleLabel: user.roleName || user.roleCode || 'Unassigned role',
      branchLabel: user.branchName || 'Unassigned Branch'
    }));

    this.securityRows = [
      {
        label: 'Role allocation entries',
        value: String(roleCoverage)
      },
      {
        label: 'Branch-mapped users',
        value: String(branchCoverage)
      },
      {
        label: 'Recent login traces',
        value: String(data.recentLogins.length)
      },
      {
        label: 'Locked ratio',
        value: `${this.getShare(locked, total).toFixed(0)}%`
      }
    ];

    this.accessHeatRows = [
      {
        label: 'Roles',
        cells: chartSource.map((item, index) => ({
          label: this.toChartLabel(item.label),
          value: Number(item.count || 0),
          share: this.getShare(Number(item.count || 0), maxValue),
          tone: tones[index % tones.length]
        }))
      },
      {
        label: 'Branches',
        cells: branchSource.map((item, index) => ({
          label: this.toChartLabel(this.toBranchChartName(item.label)),
          value: Number(item.count || 0),
          share: this.getShare(Number(item.count || 0), maxBranchValue),
          tone: tones[index % tones.length]
        }))
      }
    ];

    this.syncSelections();
  }

  private getShare(value: number, total: number): number {
    if (!total) {
      return 0;
    }
    return Number(((value / total) * 100).toFixed(2));
  }

  private buildDonutGradient(items: DashboardLegendItem[]): string {
    const total = Math.max(1, items.reduce((sum, item) => sum + Number(item.value || 0), 0));
    let start = 0;
    const stops = items.map(item => {
      const end = start + ((Number(item.value || 0) / total) * 100);
      const stop = `${item.color} ${start}% ${end}%`;
      start = end;
      return stop;
    });
    return `conic-gradient(${stops.join(', ')})`;
  }

  private buildAxisTicks(maxValue: number): DashboardAxisTick[] {
    const safeMax = Math.max(1, maxValue);
    return [0, 25, 50, 75, 100].map(percent => ({
      label: `${Math.round((safeMax * percent) / 100)}`,
      bottom: percent
    }));
  }

  private toChartLabel(label: string): string {
    const value = String(label || '').trim();
    if (value.length <= 14) {
      return value;
    }
    return value.split(' ').slice(0, 2).join(' ');
  }

  private toBranchChartName(label: string): string {
    const value = String(label || '').trim();
    if (!value) {
      return 'Unassigned Branch';
    }

    const matched = value.match(/^[A-Z]{2,}\d+\s*[-:]\s*(.+)$/);
    if (matched?.[1]) {
      return matched[1].trim();
    }

    return value;
  }

  branchPointX(index: number, total: number): number {
    if (total <= 1) return 310;
    const start = 56;
    const end = 570;
    return start + ((end - start) / (total - 1)) * index;
  }

  branchPointY(value: number): number {
    const max = Math.max(1, ...this.branchCurvePoints.map(item => item.value));
    const top = 28;
    const bottom = 188;
    return bottom - ((value / max) * (bottom - top));
  }

  branchCurvePath(): string {
    if (!this.branchCurvePoints.length) return '';
    return this.branchCurvePoints
      .map((point, index) => `${index === 0 ? 'M' : 'L'} ${this.branchPointX(index, this.branchCurvePoints.length)} ${this.branchPointY(point.value)}`)
      .join(' ');
  }

  branchCurveAreaPath(): string {
    if (!this.branchCurvePoints.length) return '';
    const startX = this.branchPointX(0, this.branchCurvePoints.length);
    const endX = this.branchPointX(this.branchCurvePoints.length - 1, this.branchCurvePoints.length);
    return `${this.branchCurvePath()} L ${endX} 188 L ${startX} 188 Z`;
  }

  private syncSelections(): void {
    this.selectedRoleLabel = this.activeRoleColumn?.label || this.roleColumns[0]?.label || '';
    this.selectedBranchLabel = this.activeBranchPoint?.label || this.branchCurvePoints[0]?.label || '';
    this.selectedUserId = this.selectedRecentUser?.id || this.summary?.recentLogins[0]?.id || null;
  }
}
