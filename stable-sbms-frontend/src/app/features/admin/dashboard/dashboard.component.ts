import { Component } from '@angular/core';
import { Router } from '@angular/router';

interface KpiCard {
  title: string;
  value: number;
  subtitle: string;
  icon: string;
  route?: string;
  disabled?: boolean;
  colorClass: string;
}

interface ActivityLog {
  message: string;
  time: string;
}

interface SnapshotItem {
  label: string;
  value: number;
  icon: string;
  iconClass: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {

  kpis: KpiCard[] = [
    {
      title: 'Roles',
      value: 5,
      subtitle: 'System access roles',
      icon: 'bi bi-shield-lock',
      route: '/admin/roles',
      colorClass: 'kpi-teal'
    },
    {
      title: 'Users',
      value: 12,
      subtitle: 'Active system users',
      icon: 'bi bi-people',
      route: '/admin/users',
      colorClass: 'kpi-green'
    },
    {
      title: 'Branches',
      value: 3,
      subtitle: 'Configured branches',
      icon: 'bi bi-building',
      route: '/admin/branches',
      colorClass: 'kpi-blue'
    },
    {
      title: 'Accounts',
      value: 0,
      subtitle: 'Live accounts',
      icon: 'bi bi-journal-bookmark',
      disabled: true,
      colorClass: 'kpi-amber'
    },
    {
      title: 'Transactions',
      value: 0,
      subtitle: "Today's transactions",
      icon: 'bi bi-arrow-left-right',
      disabled: true,
      colorClass: 'kpi-red'
    }
  ];

  systemStatus = [
    { label: 'Role Management',    status: 'Configured' },
    { label: 'User Management',    status: 'Configured' },
    { label: 'Branch Management',  status: 'Configured' },
    { label: 'Account Module',     status: 'Pending' },
    { label: 'Transaction Module', status: 'Pending' }
  ];

  operationalSummary: SnapshotItem[] = [
    { label: 'Active Branches',   value: 3,  icon: 'bi bi-building-check', iconClass: 'icon-teal' },
    { label: 'Inactive Branches', value: 0,  icon: 'bi bi-building-x',     iconClass: 'icon-red' },
    { label: 'Active Users',      value: 10, icon: 'bi bi-person-check',   iconClass: 'icon-green' },
    { label: 'Locked Users',      value: 2,  icon: 'bi bi-person-lock',    iconClass: 'icon-amber' }
  ];

  recentActivities: ActivityLog[] = [
    { message: 'SYSTEM_ADMIN role created',              time: '10 minutes ago' },
    { message: 'User admin assigned to Branch DHK001',   time: '25 minutes ago' },
    { message: 'Branch CTG001 created',                  time: '1 hour ago' },
    { message: 'Role TELLER updated',                    time: 'Yesterday' }
  ];

  constructor(private router: Router) {}

  get configuredModuleCount(): number {
    return this.systemStatus.filter((item) => item.status === 'Configured').length;
  }

  get pendingModuleCount(): number {
    return this.systemStatus.filter((item) => item.status !== 'Configured').length;
  }

  get activeUserCount(): number {
    return this.operationalSummary[2]?.value ?? 0;
  }

  get lockedUserCount(): number {
    return this.operationalSummary[3]?.value ?? 0;
  }

  get branchCount(): number {
    return this.operationalSummary[0]?.value ?? 0;
  }

  navigate(card: KpiCard): void {
    if (card.disabled || !card.route) return;
    this.router.navigate([card.route]);
  }
}
