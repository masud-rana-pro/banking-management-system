import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { ProfitDashboardSummaryResponse, ProfitScheduleResponse, formatEnumLabel } from '../../models/profit.model';
import { ProfitService } from '../../services/profit.service';

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
  selector: 'app-profit-dashboard',
  templateUrl: './profit-dashboard.component.html',
  styleUrls: ['./profit-dashboard.component.scss']
})
export class ProfitDashboardComponent implements OnInit {

  loading = false;
  branches: BranchResponse[] = [];
  schedules: ProfitScheduleResponse[] = [];
  customerImageMap: Record<string, string> = {};
  summary: ProfitDashboardSummaryResponse = {
    activeProfitRatios: 0,
    pendingPostingCycles: 0,
    postedThisMonth: 0,
    failedPostingLogs: 0,
    currentPsrTable: [],
    upcomingPostingRun: {
      nextPostingDate: null,
      pendingSchedules: 0
    },
    recentFailedPostings: []
  };
  profitLegend: DashboardLegendItem[] = [];
  psrColumns: DashboardColumnItem[] = [];
  scheduleColumns: DashboardColumnItem[] = [];
  branchColumns: DashboardColumnItem[] = [];
  psrAxisTicks: DashboardAxisTick[] = [];
  scheduleAxisTicks: DashboardAxisTick[] = [];
  branchAxisTicks: DashboardAxisTick[] = [];
  postingTrend: DashboardTrendPoint[] = [];
  postingTrendTicks: DashboardAxisTick[] = [];
  postingTrendPath = '';
  postingTrendAreaPath = '';
  matrixColumns: DashboardHeatmapColumn[] = [];
  matrixRows: DashboardHeatmapRow[] = [];
  selectedSchedule: ProfitScheduleResponse | null = null;
  profitGradient = 'conic-gradient(#14b8a6 0deg 360deg)';

  constructor(
    private profitApi: ProfitService,
    private branchApi: BranchApiService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  get upcomingSchedules(): ProfitScheduleResponse[] {
    return [...this.schedules]
      .filter(item => item.status === 'ACTIVE')
      .sort((a, b) => String(a.nextPostingDate).localeCompare(String(b.nextPostingDate)))
      .slice(0, 6);
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      summary: this.profitApi.getDashboardSummary(),
      schedules: this.profitApi.getSchedules(),
      branches: this.branchApi.getAll().pipe(catchError(() => of([]))),
      customers: this.customerService.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ summary, schedules, branches, customers }) => {
        this.summary = summary;
        this.schedules = schedules || [];
        this.branches = branches || [];
        this.customerImageMap = this.buildCustomerImageMap(customers as CustomerResponse[]);
        this.prepareDashboard();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load profit dashboard.', 'error');
      }
    });
  }

  openRatio(id: number): void {
    this.router.navigate(['/profit/ratios', id]);
  }

  openSchedule(id: number): void {
    this.router.navigate(['/profit/schedules', id]);
  }

  openPosting(id: number): void {
    this.router.navigate(['/profit/postings', id]);
  }

  openPostingRun(): void {
    this.router.navigate(['/profit/postings/run']);
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) return 'Unassigned Branch';
    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  getSelectedCustomerImageUrl(customerCode?: string | null): string {
    if (!customerCode) {
      return '';
    }
    return this.customerImageMap[customerCode] || '';
  }

  focusSchedule(item: ProfitScheduleResponse): void {
    this.selectedSchedule = item;
  }

  get selectedScheduleBadges(): Array<{ label: string; value: string }> {
    if (!this.selectedSchedule) {
      return [];
    }
    return [
      { label: 'Frequency', value: this.getLabel(this.selectedSchedule.profitFrequency) },
      { label: 'Status', value: this.getLabel(this.selectedSchedule.status) },
      { label: 'Branch', value: this.getBranchName(this.selectedSchedule.branchId) },
      { label: 'Next Posting', value: this.selectedSchedule.nextPostingDate || '-' }
    ];
  }

  get selectedScheduleBadgeText(): string {
    const label = this.selectedSchedule?.customerName || this.selectedSchedule?.accountNumber || '';
    const compact = label
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(part => part.charAt(0).toUpperCase())
      .join('');
    return compact || 'PR';
  }

  private prepareDashboard(): void {
    this.profitLegend = [
      { label: 'Active Ratios', value: this.summary.activeProfitRatios, note: 'PSR entries live for use', color: '#22c55e' },
      { label: 'Pending Cycles', value: this.summary.pendingPostingCycles, note: 'posting cycles still waiting to run', color: '#f59e0b' },
      { label: 'Posted This Month', value: this.summary.postedThisMonth, note: 'posting count completed in the month', color: '#14b8a6' },
      { label: 'Failed Logs', value: this.summary.failedPostingLogs, note: 'posting failures needing remediation', color: '#ef4444' }
    ];
    this.profitGradient = this.buildDonutGradient(this.profitLegend);

    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    const psrMax = Math.max(...this.summary.currentPsrTable.map(item => item.ratioPercent), 1);
    this.psrColumns = this.summary.currentPsrTable.slice(0, 6).map((item, index) => ({
      label: item.accountTypeName,
      shortLabel: this.toChartLabel(item.accountTypeName, 14),
      value: item.ratioPercent,
      height: this.getShare(item.ratioPercent, psrMax),
      tone: tones[index % tones.length]
    }));
    this.psrAxisTicks = this.buildPercentAxisTicks(psrMax);

    const freqMap = new Map<string, number>();
    this.upcomingSchedules.forEach(item => {
      const key = this.getLabel(item.profitFrequency);
      freqMap.set(key, (freqMap.get(key) || 0) + 1);
    });
    const freqStats = Array.from(freqMap.entries()).map(([label, value]) => ({ label, value }));
    const freqMax = Math.max(...freqStats.map(item => item.value), 1);
    this.scheduleColumns = freqStats.map((item, index) => ({
      label: item.label,
      shortLabel: this.toChartLabel(item.label, 12),
      value: item.value,
      height: this.getShare(item.value, freqMax),
      tone: tones[index % tones.length]
    }));
    this.scheduleAxisTicks = this.buildCountAxisTicks(freqMax);

    const branchMap = new Map<string, number>();
    this.upcomingSchedules.forEach(item => {
      const key = this.toBranchChartName(this.getBranchName(item.branchId));
      branchMap.set(key, (branchMap.get(key) || 0) + 1);
    });
    const branchStats = Array.from(branchMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const branchMax = Math.max(...branchStats.map(([, value]) => value), 1);
    this.branchColumns = branchStats.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label, 12),
      value,
      height: this.getShare(value, branchMax),
      tone: tones[index % tones.length]
    }));
    this.branchAxisTicks = this.buildCountAxisTicks(branchMax);

    const postingTrendMap = new Map<string, number>();
    this.upcomingSchedules.forEach(item => {
      const key = item.nextPostingDate || 'Unknown';
      postingTrendMap.set(key, (postingTrendMap.get(key) || 0) + 1);
    });
    const trendEntries = Array.from(postingTrendMap.entries())
      .sort((a, b) => a[0].localeCompare(b[0]))
      .slice(-7);
    const maxTrend = Math.max(...trendEntries.map(([, value]) => value), 1);
    this.postingTrend = trendEntries.map(([label, value], index, items) => ({
      label,
      shortLabel: this.toDayLabel(label),
      value,
      x: items.length === 1 ? 50 : (index / (items.length - 1)) * 100,
      y: 100 - this.getChartHeight(value, maxTrend)
    }));
    this.postingTrendTicks = this.buildCountAxisTicks(maxTrend);
    this.postingTrendPath = this.postingTrend
      .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
      .join(' ');
    this.postingTrendAreaPath = this.postingTrend.length
      ? `${this.postingTrendPath} L ${this.postingTrend[this.postingTrend.length - 1].x} 100 L ${this.postingTrend[0].x} 100 Z`
      : '';

    this.matrixColumns = [
      { key: 'ACTIVE', label: 'Active' },
      { key: 'PENDING', label: 'Pending' },
      { key: 'ARCHIVED', label: 'Archived' },
      { key: 'DUE', label: 'Due Soon' }
    ];
    this.matrixRows = branchStats.slice(0, 4).map(([label]) => {
      const rows = this.upcomingSchedules.filter(item => this.toBranchChartName(this.getBranchName(item.branchId)) === label);
      return {
        label,
        shortLabel: this.toChartLabel(label, 12),
        values: [
          rows.filter(item => item.status === 'ACTIVE').length,
          rows.filter(item => item.status === 'PENDING').length,
          rows.filter(item => item.status === 'ARCHIVED').length,
          rows.filter(item => this.isDueSoon(item.nextPostingDate)).length
        ]
      };
    });

    this.selectedSchedule = [...this.upcomingSchedules]
      .sort((a, b) => (b.currentBalance || 0) - (a.currentBalance || 0))[0]
      || this.schedules[0]
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

  private buildCountAxisTicks(maxValue: number): DashboardAxisTick[] {
    const safeMax = Math.max(1, Math.ceil(maxValue));
    return [0, 25, 50, 75, 100].map(percent => ({
      label: `${Math.round((safeMax * percent) / 100)}`,
      bottom: percent
    }));
  }

  private buildPercentAxisTicks(maxValue: number): DashboardAxisTick[] {
    const safeMax = Math.max(1, maxValue);
    return [0, 25, 50, 75, 100].map(percent => ({
      label: `${(((safeMax * percent) / 100)).toFixed(safeMax < 5 ? 2 : 1)}%`,
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

  private toBranchChartName(value: string): string {
    const compact = String(value || '').trim();
    if (!compact.includes(' - ')) {
      return compact;
    }
    return compact.split(' - ').slice(1).join(' - ').trim() || compact;
  }

  private isDueSoon(date?: string | null): boolean {
    if (!date) {
      return false;
    }
    const target = new Date(date);
    if (Number.isNaN(target.getTime())) {
      return false;
    }
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const diffDays = Math.ceil((target.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
    return diffDays >= 0 && diffDays <= 7;
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<string, string> {
    return customers.reduce((acc, customer) => {
      if (customer.customerCode && customer.profileImageName) {
        acc[customer.customerCode] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<string, string>);
  }
}
