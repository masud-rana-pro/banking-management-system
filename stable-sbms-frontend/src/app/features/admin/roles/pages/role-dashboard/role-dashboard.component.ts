import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { RoleDashboardSummary } from '../../model/role.model';
import { RoleApiService } from '../../service/role-api.service';

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
}

interface DashboardInfoRow {
  label: string;
  value: string;
}

@Component({
  selector: 'app-role-dashboard',
  templateUrl: './role-dashboard.component.html',
  styleUrls: ['./role-dashboard.component.scss']
})
export class RoleDashboardComponent implements OnInit {

  loading = false;
  summary: RoleDashboardSummary | null = null;
  roleHealthBands: DashboardBandItem[] = [];
  roleHealthLegend: DashboardLegendItem[] = [];
  permissionColumns: DashboardColumnItem[] = [];
  permissionAxisTicks: DashboardAxisTick[] = [];
  recentTimeline: DashboardTimelineItem[] = [];
  governanceRows: DashboardInfoRow[] = [];
  roleStatusGradient = 'conic-gradient(#dbe4f0 0 100%)';

  constructor(
    private roleApi: RoleApiService,
    private router: Router,
    public access: AccessControlService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.roleApi.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load role dashboard.', 'error');
      }
    });
  }

  open(route: string): void {
    this.router.navigate([route]);
  }

  private prepareDashboard(data: RoleDashboardSummary): void {
    const total = Math.max(data.totalRoles, 1);
    const active = data.activeRoles || 0;
    const inactive = data.inactiveRoles || 0;
    const heavy = data.permissionHeavyRoles || 0;
    const balanced = Math.max(total - heavy - inactive, 0);
    const assignedUsers = data.recentRoles.reduce((sum, role) => sum + Number(role.assignedUserCount || 0), 0);
    const permissions = data.recentRoles.reduce((sum, role) => sum + Number(role.permissionCount || 0), 0);

    this.roleHealthBands = [
      {
        label: 'Active access roles',
        value: active,
        share: this.getShare(active, total),
        note: 'Roles currently available for session-based access control.',
        tone: 'green'
      },
      {
        label: 'Inactive or archived roles',
        value: inactive,
        share: this.getShare(inactive, total),
        note: 'These roles should remain outside daily user assignment.',
        tone: 'red'
      },
      {
        label: 'Permission-heavy roles',
        value: heavy,
        share: this.getShare(heavy, total),
        note: 'High-power roles needing tighter review and OTP governance.',
        tone: 'amber'
      },
      {
        label: 'Balanced operational roles',
        value: balanced,
        share: this.getShare(balanced, total),
        note: 'Roles with moderate access spread across business operations.',
        tone: 'teal'
      }
    ];

    this.roleHealthLegend = [
      {
        label: 'Active roles',
        value: active,
        note: 'Ready for assignment and module access.',
        color: '#0f766e'
      },
      {
        label: 'Inactive roles',
        value: inactive,
        note: 'Disabled from normal business use.',
        color: '#dc2626'
      },
      {
        label: 'Permission-heavy roles',
        value: heavy,
        note: 'Elevated authority clusters that need close review.',
        color: '#d97706'
      },
      {
        label: 'Balanced roles',
        value: balanced,
        note: 'Operational or scoped-access roles.',
        color: '#2563eb'
      }
    ];

    this.roleStatusGradient = this.buildDonutGradient(this.roleHealthLegend);

    const chartSource = [...data.recentRoles]
      .sort((left, right) => Number(right.permissionCount || 0) - Number(left.permissionCount || 0))
      .slice(0, 6);
    const maxPermission = Math.max(1, ...chartSource.map(item => Number(item.permissionCount || 0)));
    const tones: Array<DashboardColumnItem['tone']> = ['teal', 'green', 'blue', 'amber', 'purple', 'red'];
    this.permissionColumns = chartSource.map((role, index) => ({
      label: role.name || role.code,
      shortLabel: this.toChartLabel(role.name || role.code, role.code),
      value: Number(role.permissionCount || 0),
      height: Math.max(12, Math.round((Number(role.permissionCount || 0) / maxPermission) * 100)),
      tone: tones[index % tones.length]
    }));
    this.permissionAxisTicks = this.buildAxisTicks(maxPermission);

    this.recentTimeline = data.recentRoles.slice(0, 6).map(role => ({
      title: `${role.name} (${role.code})`,
      subtitle: `${role.assignedUserCount || 0} assigned users and ${role.permissionCount || 0} permission grants`,
      meta: `Status: ${this.readable(role.status)}`,
      tone: role.status === 'ACTIVE' ? 'green' : 'red'
    }));

    this.governanceRows = [
      {
        label: 'Recent role changes tracked',
        value: String(data.recentRoles.length)
      },
      {
        label: 'Users mapped to recent roles',
        value: String(assignedUsers)
      },
      {
        label: 'Permission grants across recent roles',
        value: String(permissions)
      },
      {
        label: 'High-risk role ratio',
        value: `${this.getShare(heavy, total).toFixed(0)}%`
      }
    ];
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

  private toChartLabel(name: string, code?: string | null): string {
    const cleaned = String(name || code || '').trim();
    if (cleaned.length <= 12) {
      return cleaned;
    }
    return cleaned.split(' ').slice(0, 2).join(' ');
  }

  private readable(value?: string | null): string {
    return String(value || '').replace(/_/g, ' ');
  }
}
