import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import {
  CustomerDashboardSummaryResponse,
  CustomerResponse,
  formatEnumLabel
} from '../../models/customer.model';
import { CustomerService } from '../../services/customer.service';

interface BranchDistributionItem {
  branchId: number;
  branchName: string;
  total: number;
}

interface DashboardBarItem {
  label: string;
  value: number;
  note: string;
  share: number;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

interface DashboardBandItem {
  label: string;
  value: number;
  note: string;
  share: number;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

interface DashboardTimelineItem {
  title: string;
  subtitle: string;
  meta: string;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

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

interface HeatmapRow {
  label: string;
  cells: Array<{
    label: string;
    value: number;
    intensity: number;
  }>;
}

@Component({
  selector: 'app-customer-dashboard',
  templateUrl: './customer-dashboard.component.html',
  styleUrls: ['./customer-dashboard.component.scss']
})
export class CustomerDashboardComponent implements OnInit {

  loading = false;
  summary: CustomerDashboardSummaryResponse = {
    totalCustomers: 0,
    activeCustomers: 0,
    pendingKycCustomers: 0,
    blockedCustomers: 0,
    newCustomersThisMonth: 0,
    incompleteProfiles: 0
  };

  recentCustomers: CustomerResponse[] = [];
  pendingKycList: CustomerResponse[] = [];
  blockedCustomers: CustomerResponse[] = [];
  incompleteProfiles: CustomerResponse[] = [];
  branchDistribution: BranchDistributionItem[] = [];
  healthBands: DashboardBandItem[] = [];
  branchBars: DashboardBarItem[] = [];
  recentTimeline: DashboardTimelineItem[] = [];
  verificationRows: Array<{ label: string; value: string }> = [];
  healthLegend: DashboardLegendItem[] = [];
  branchColumns: DashboardColumnItem[] = [];
  branchAxisTicks: DashboardAxisTick[] = [];
  customerHealthGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  verifiedEmailCount = 0;
  verifiedMobileCount = 0;
  fullyDocumentedCount = 0;
  typeColumns: DashboardColumnItem[] = [];
  typeAxisTicks: DashboardAxisTick[] = [];
  customerTrendPoints: DashboardTrendPoint[] = [];
  customerTrendPath = '';
  customerTrendAreaPath = '';
  branchStatusHeatRows: HeatmapRow[] = [];
  spotlightCustomer: CustomerResponse | null = null;

  private branches: BranchResponse[] = [];

  constructor(
    private customerApi: CustomerService,
    private branchApi: BranchApiService,
    private router: Router,
    public accessControl: AccessControlService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;

    forkJoin({
      summary: this.customerApi.getDashboardSummary(),
      customers: this.customerApi.getAll(),
      branches: this.branchApi.getAll()
    }).subscribe({
      next: ({ summary, customers, branches }) => {
        this.summary = summary;
        this.branches = branches || [];
        this.prepareData(customers || []);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load customer dashboard.', 'error');
      }
    });
  }

  openCustomer(id: number): void {
    this.router.navigate(['/customers', id]);
  }

  openStatus(id: number): void {
    this.router.navigate(['/customers', id, 'status']);
  }

  manageAddress(id: number): void {
    this.router.navigate(['/customers', id, 'addresses']);
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) {
      return 'Unassigned Branch';
    }

    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId || '-'}`;
  }

  getStatusLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private prepareData(customers: CustomerResponse[]): void {
    const sorted = [...customers].sort((a, b) => {
      const dateA = new Date(a.createdAt || '').getTime();
      const dateB = new Date(b.createdAt || '').getTime();
      return dateB - dateA;
    });

    this.recentCustomers = sorted.slice(0, 6);
    this.pendingKycList = customers.filter(item => item.customerStatus === 'PENDING_KYC').slice(0, 6);
    this.blockedCustomers = customers.filter(item => item.customerStatus === 'BLOCKED').slice(0, 6);
    this.incompleteProfiles = customers.filter(item => this.isIncomplete(item)).slice(0, 6);

    const branchMap = new Map<number, number>();
    customers.forEach(item => {
      const current = branchMap.get(item.branchId || 0) || 0;
      branchMap.set(item.branchId || 0, current + 1);
    });

    this.branchDistribution = Array.from(branchMap.entries())
      .map(([branchId, total]) => ({
        branchId,
        branchName: this.getBranchName(branchId),
        total
      }))
      .sort((a, b) => b.total - a.total)
      .slice(0, 6);

    this.verifiedEmailCount = customers.filter(item => item.emailVerified).length;
    this.verifiedMobileCount = customers.filter(item => item.mobileVerified).length;
    this.fullyDocumentedCount = customers.filter(item => Number(item.addressCount || 0) > 0 && Number(item.identityCount || 0) > 0).length;

    const totalCustomers = Math.max(this.summary.totalCustomers || customers.length, 1);
    const maxBranchTotal = Math.max(...this.branchDistribution.map(item => item.total), 1);
    const tones: Array<DashboardBarItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];

    const typeMap = new Map<string, number>();
    customers.forEach(item => {
      const key = formatEnumLabel(item.customerType) || 'Unknown';
      typeMap.set(key, (typeMap.get(key) || 0) + 1);
    });
    const customerTypes = Array.from(typeMap.entries())
      .map(([label, total]) => ({ label, total }))
      .sort((a, b) => b.total - a.total)
      .slice(0, 6);
    const maxTypeTotal = Math.max(...customerTypes.map(item => item.total), 1);

    this.healthBands = [
      {
        label: 'Active Relationship',
        value: this.summary.activeCustomers,
        note: `${this.getShare(this.summary.activeCustomers, totalCustomers)}% of live portfolio`,
        share: this.getShare(this.summary.activeCustomers, totalCustomers),
        tone: 'success'
      },
      {
        label: 'Pending KYC Queue',
        value: this.summary.pendingKycCustomers,
        note: `${this.pendingKycList.length} profiles need immediate review`,
        share: this.getShare(this.summary.pendingKycCustomers, totalCustomers),
        tone: 'warning'
      },
      {
        label: 'Incomplete Profiles',
        value: this.summary.incompleteProfiles,
        note: `${this.incompleteProfiles.length} profiles missing critical data`,
        share: this.getShare(this.summary.incompleteProfiles, totalCustomers),
        tone: 'info'
      },
      {
        label: 'Blocked Relationships',
        value: this.summary.blockedCustomers,
        note: `${this.blockedCustomers.length} records are under restriction`,
        share: this.getShare(this.summary.blockedCustomers, totalCustomers),
        tone: 'danger'
      }
    ];
    this.healthLegend = [
      { label: 'Active', value: this.summary.activeCustomers, note: 'live customer relationships', color: '#22c55e' },
      { label: 'Pending KYC', value: this.summary.pendingKycCustomers, note: 'awaiting compliance completion', color: '#f59e0b' },
      { label: 'Incomplete', value: this.summary.incompleteProfiles, note: 'missing profile details', color: '#3b82f6' },
      { label: 'Blocked', value: this.summary.blockedCustomers, note: 'restricted customer records', color: '#ef4444' }
    ];
    this.customerHealthGradient = this.buildDonutGradient(this.healthLegend);

    this.branchBars = this.branchDistribution.map((item, index) => ({
      label: item.branchName,
      value: item.total,
      note: `${this.getShare(item.total, totalCustomers)}% of customer base`,
      share: this.getShare(item.total, maxBranchTotal),
      tone: tones[index % tones.length]
    }));
    this.branchColumns = this.branchDistribution.map((item, index) => ({
      label: item.branchName,
      shortLabel: this.buildShortLabel(item.branchName),
      value: item.total,
      height: this.getShare(item.total, maxBranchTotal),
      tone: tones[index % tones.length]
    }));
    this.branchAxisTicks = this.buildAxisTicks(maxBranchTotal);
    this.typeColumns = customerTypes.map((item, index) => ({
      label: item.label,
      shortLabel: item.label,
      value: item.total,
      height: this.getShare(item.total, maxTypeTotal),
      tone: tones[index % tones.length]
    }));
    this.typeAxisTicks = this.buildAxisTicks(maxTypeTotal);

    this.recentTimeline = this.recentCustomers.map(item => ({
      title: item.fullName,
      subtitle: `${item.customerCode} | ${this.getStatusLabel(item.customerType)}`,
      meta: `${this.getBranchName(item.branchId)} | ${this.getStatusLabel(item.customerStatus)}`,
      tone: item.customerStatus === 'ACTIVE'
        ? 'success'
        : item.customerStatus === 'PENDING_KYC'
          ? 'warning'
          : item.customerStatus === 'BLOCKED'
            ? 'danger'
            : 'info'
    }));

    this.verificationRows = [
      { label: 'Email Verified', value: `${this.verifiedEmailCount} / ${this.summary.totalCustomers}` },
      { label: 'Mobile Verified', value: `${this.verifiedMobileCount} / ${this.summary.totalCustomers}` },
      { label: 'Document Complete', value: `${this.fullyDocumentedCount} / ${this.summary.totalCustomers}` },
      { label: 'New This Month', value: `${this.summary.newCustomersThisMonth}` }
    ];

    this.spotlightCustomer = [...customers]
      .sort((a, b) => (Number(b.monthlyIncome || 0) + Number(b.identityCount || 0)) - (Number(a.monthlyIncome || 0) + Number(a.identityCount || 0)))[0] || null;

    this.buildCustomerTrend(customers);
    this.buildBranchStatusHeatmap(customers);
  }

  private isIncomplete(item: CustomerResponse): boolean {
    return !item.fatherName ||
      !item.motherName ||
      !item.dateOfBirth ||
      !item.gender ||
      !item.email ||
      !item.branchId ||
      !item.sourceOfFunds ||
      !item.addressCount ||
      !item.identityCount;
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

  private buildShortLabel(label: string): string {
    const trimmed = (label || '').trim();
    if (!trimmed) {
      return 'Branch';
    }

    if (trimmed.toLowerCase() === 'unassigned branch') {
      return 'No Branch';
    }

    if (trimmed.includes(' - ')) {
      const branchName = trimmed.split(' - ').slice(1).join(' - ').trim();
      return this.toChartLabel(branchName || trimmed.split(' - ')[0].trim());
    }

    return this.toChartLabel(trimmed);
  }

  private toChartLabel(value: string): string {
    const compact = value.replace(/\s+/g, ' ').trim();
    if (!compact) {
      return 'Branch';
    }

    const words = compact.split(' ');
    const readable = words.slice(0, 2).join(' ');
    return readable.length <= 14 ? readable : readable.slice(0, 14).trim();
  }

  private buildAxisTicks(maxValue: number): DashboardAxisTick[] {
    const safeMax = Math.max(1, Math.ceil(maxValue));
    return [0, 25, 50, 75, 100].map(percent => ({
      label: `${Math.round((safeMax * percent) / 100)}`,
      bottom: percent
    }));
  }

  private buildCustomerTrend(customers: CustomerResponse[]): void {
    const monthKeys = this.buildRecentMonthKeys(6);
    const counts = new Map<string, number>();
    monthKeys.forEach(item => counts.set(item.key, 0));

    customers.forEach(item => {
      const sourceDate = item.createdAt || item.updatedAt;
      if (!sourceDate) {
        return;
      }
      const date = new Date(sourceDate);
      if (Number.isNaN(date.getTime())) {
        return;
      }
      const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
      if (counts.has(key)) {
        counts.set(key, (counts.get(key) || 0) + 1);
      }
    });

    const values = monthKeys.map(item => counts.get(item.key) || 0);
    const maxValue = Math.max(...values, 1);

    this.customerTrendPoints = monthKeys.map((item, index) => {
      const value = counts.get(item.key) || 0;
      const x = monthKeys.length === 1 ? 310 : 28 + (index * (564 / (monthKeys.length - 1)));
      const y = 188 - ((value / maxValue) * 148);
      return { label: item.label, value, x, y };
    });

    this.customerTrendPath = this.customerTrendPoints
      .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
      .join(' ');
    this.customerTrendAreaPath = this.customerTrendPoints.length
      ? `${this.customerTrendPath} L ${this.customerTrendPoints[this.customerTrendPoints.length - 1].x} 188 L ${this.customerTrendPoints[0].x} 188 Z`
      : '';
  }

  private buildBranchStatusHeatmap(customers: CustomerResponse[]): void {
    const topBranches = this.branchDistribution.slice(0, 5);
    const statuses = ['ACTIVE', 'PENDING_KYC', 'BLOCKED'];
    const counts: number[] = [];

    this.branchStatusHeatRows = topBranches.map(branch => {
      const cells = statuses.map(status => {
        const value = customers.filter(item => this.getBranchName(item.branchId) === branch.branchName && item.customerStatus === status).length;
        counts.push(value);
        return {
          label: `${branch.branchName} / ${formatEnumLabel(status)}`,
          value,
          intensity: 0
        };
      });
      return {
        label: this.buildShortLabel(branch.branchName),
        cells
      };
    });

    const maxValue = Math.max(...counts, 1);
    this.branchStatusHeatRows = this.branchStatusHeatRows.map(row => ({
      ...row,
      cells: row.cells.map(cell => ({
        ...cell,
        intensity: Math.max(0.18, cell.value / maxValue)
      }))
    }));
  }

  private buildRecentMonthKeys(monthCount: number): Array<{ key: string; label: string }> {
    const baseDate = new Date();
    const months: Array<{ key: string; label: string }> = [];

    for (let index = monthCount - 1; index >= 0; index -= 1) {
      const current = new Date(baseDate.getFullYear(), baseDate.getMonth() - index, 1);
      months.push({
        key: `${current.getFullYear()}-${String(current.getMonth() + 1).padStart(2, '0')}`,
        label: current.toLocaleString('en', { month: 'short' })
      });
    }

    return months;
  }
}
