import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { KycDashboardSummaryResponse, KycProfileResponse, formatEnumLabel } from '../../models/kyc.model';
import { KycService } from '../../services/kyc.service';

interface BranchDistributionItem {
  branchId: number;
  branchName: string;
  total: number;
}

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

interface HeatmapRow {
  label: string;
  cells: Array<{
    label: string;
    value: number;
    intensity: number;
  }>;
}

@Component({
  selector: 'app-kyc-dashboard',
  templateUrl: './kyc-dashboard.component.html',
  styleUrls: ['./kyc-dashboard.component.scss']
})
export class KycDashboardComponent implements OnInit {

  loading = false;
  summary: KycDashboardSummaryResponse = {
    pendingKyc: 0,
    verifiedKyc: 0,
    rejectedKyc: 0,
    resubmissionQueue: 0,
    highRiskCustomers: 0,
    lowRiskCount: 0,
    mediumRiskCount: 0,
    highRiskCount: 0
  };

  profiles: KycProfileResponse[] = [];
  pendingQueue: KycProfileResponse[] = [];
  highRiskProfiles: KycProfileResponse[] = [];
  sentBackProfiles: KycProfileResponse[] = [];
  recentDecisions: KycProfileResponse[] = [];
  branchDistribution: BranchDistributionItem[] = [];
  statusLegend: DashboardLegendItem[] = [];
  riskBands: DashboardBandItem[] = [];
  branchColumns: DashboardColumnItem[] = [];
  branchAxisTicks: DashboardAxisTick[] = [];
  riskRows: Array<{ label: string; value: string }> = [];
  kycStatusGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  customerImageMap: Record<number, string> = {};
  riskTypeColumns: DashboardColumnItem[] = [];
  riskTypeAxisTicks: DashboardAxisTick[] = [];
  kycTrendPoints: DashboardTrendPoint[] = [];
  kycTrendPath = '';
  kycTrendAreaPath = '';
  branchRiskHeatRows: HeatmapRow[] = [];
  spotlightProfile: KycProfileResponse | null = null;

  private branches: BranchResponse[] = [];

  constructor(
    private kycApi: KycService,
    private branchApi: BranchApiService,
    private customerApi: CustomerService,
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
      summary: this.kycApi.getDashboardSummary(),
      profiles: this.kycApi.getProfiles(),
      branches: this.branchApi.getAll(),
      customers: this.customerApi.getAll()
    }).subscribe({
      next: ({ summary, profiles, branches, customers }) => {
        this.summary = summary;
        this.profiles = profiles || [];
        this.branches = branches || [];
        this.customerImageMap = this.buildCustomerImageMap(customers || []);
        this.prepareData();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load KYC dashboard.', 'error');
      }
    });
  }

  openView(id: number): void {
    if (!this.isValidId(id)) {
      return;
    }
    this.router.navigate(['/kyc', id]);
  }

  openReview(id: number): void {
    if (!this.isValidId(id)) {
      return;
    }
    this.router.navigate(['/kyc', id, 'review']);
  }

  openUpload(id: number): void {
    if (!this.isValidId(id)) {
      return;
    }
    this.router.navigate(['/kyc', id, 'documents']);
  }

  openHistory(id: number): void {
    if (!this.isValidId(id)) {
      return;
    }
    this.router.navigate(['/kyc', id, 'history']);
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) {
      return 'Unassigned Branch';
    }

    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getRiskClass(value?: string | null): string {
    return String(value || '').toLowerCase();
  }

  getImageUrl(customerId?: number | null): string {
    return this.fileUploadService.resolveImageUrl(customerId ? this.customerImageMap[customerId] : '');
  }

  hasCustomerImage(customerId?: number | null): boolean {
    return !!(customerId && this.customerImageMap[customerId]);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  canAny(permissionCodes: string[]): boolean {
    return this.accessControl.hasAnyPermission(permissionCodes);
  }

  private prepareData(): void {
    const sortedByUpdated = [...this.profiles].sort((a, b) => {
      const dateA = new Date(a.updatedAt || a.reviewedAt || a.createdAt || '').getTime();
      const dateB = new Date(b.updatedAt || b.reviewedAt || b.createdAt || '').getTime();
      return dateB - dateA;
    });

    this.pendingQueue = this.profiles
      .filter(item => ['DRAFT', 'SUBMITTED', 'UNDER_REVIEW'].includes(item.reviewStatus))
      .slice(0, 6);

    this.highRiskProfiles = this.profiles
      .filter(item => item.riskLevel === 'HIGH')
      .slice(0, 6);

    this.sentBackProfiles = this.profiles
      .filter(item => item.reviewStatus === 'SENT_BACK')
      .slice(0, 6);

    this.recentDecisions = sortedByUpdated
      .filter(item => ['VERIFIED', 'APPROVED', 'REJECTED'].includes(item.reviewStatus))
      .slice(0, 6);

    const branchMap = new Map<number, number>();
    this.profiles.forEach(item => {
      const key = item.branchId || 0;
      branchMap.set(key, (branchMap.get(key) || 0) + 1);
    });

    this.branchDistribution = Array.from(branchMap.entries())
      .map(([branchId, total]) => ({
        branchId,
        branchName: this.getBranchName(branchId),
        total
      }))
      .sort((a, b) => b.total - a.total)
      .slice(0, 6);

    const totalProfiles = Math.max(this.profiles.length, 1);
    const maxBranchTotal = Math.max(...this.branchDistribution.map(item => item.total), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    const riskTypeTotals = [
      { label: 'Low Risk', value: this.summary.lowRiskCount },
      { label: 'Medium Risk', value: this.summary.mediumRiskCount },
      { label: 'High Risk', value: this.summary.highRiskCount },
      { label: 'Resubmission', value: this.summary.resubmissionQueue }
    ];
    const maxRiskTypeTotal = Math.max(...riskTypeTotals.map(item => item.value), 1);

    this.statusLegend = [
      { label: 'Pending Review', value: this.summary.pendingKyc, note: 'profiles awaiting analyst action', color: '#f59e0b' },
      { label: 'Verified', value: this.summary.verifiedKyc, note: 'customers cleared through review', color: '#22c55e' },
      { label: 'Rejected', value: this.summary.rejectedKyc, note: 'profiles declined during review', color: '#ef4444' },
      { label: 'Resubmission', value: this.summary.resubmissionQueue, note: 'records sent back for correction', color: '#3b82f6' }
    ];
    this.kycStatusGradient = this.buildDonutGradient(this.statusLegend);

    this.riskBands = [
      {
        label: 'Low Risk Book',
        value: this.summary.lowRiskCount,
        note: 'standard monitoring profile',
        share: this.getShare(this.summary.lowRiskCount, totalProfiles),
        tone: 'success'
      },
      {
        label: 'Medium Risk Book',
        value: this.summary.mediumRiskCount,
        note: 'enhanced monitoring candidate',
        share: this.getShare(this.summary.mediumRiskCount, totalProfiles),
        tone: 'warning'
      },
      {
        label: 'High Risk Book',
        value: this.summary.highRiskCount,
        note: `${this.highRiskProfiles.length} high-risk cases visible in the current shortlist`,
        share: this.getShare(this.summary.highRiskCount, totalProfiles),
        tone: 'danger'
      },
      {
        label: 'Resubmission Pressure',
        value: this.summary.resubmissionQueue,
        note: `${this.sentBackProfiles.length} recently sent back for correction`,
        share: this.getShare(this.summary.resubmissionQueue, totalProfiles),
        tone: 'info'
      }
    ];

    this.branchColumns = this.branchDistribution.map((item, index) => ({
      label: item.branchName,
      shortLabel: this.buildShortLabel(item.branchName),
      value: item.total,
      height: this.getShare(item.total, maxBranchTotal),
      tone: tones[index % tones.length]
    }));
    this.branchAxisTicks = this.buildAxisTicks(maxBranchTotal);
    this.riskTypeColumns = riskTypeTotals.map((item, index) => ({
      label: item.label,
      shortLabel: item.label,
      value: item.value,
      height: this.getShare(item.value, maxRiskTypeTotal),
      tone: tones[index % tones.length]
    }));
    this.riskTypeAxisTicks = this.buildAxisTicks(maxRiskTypeTotal);

    this.riskRows = [
      { label: 'High Risk Customers', value: `${this.summary.highRiskCustomers}` },
      { label: 'Pending Queue', value: `${this.pendingQueue.length}` },
      { label: 'Recent Decisions', value: `${this.recentDecisions.length}` },
      { label: 'Branch Coverage', value: `${this.branchDistribution.length} tracked branches` }
    ];

    this.spotlightProfile = [...this.profiles]
      .sort((a, b) => {
        const riskWeight = (value?: string | null) => value === 'HIGH' ? 3 : value === 'MEDIUM' ? 2 : 1;
        return riskWeight(b.riskLevel) - riskWeight(a.riskLevel);
      })[0] || null;

    this.buildKycTrend();
    this.buildBranchRiskHeatmap();
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

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, item) => {
      acc[item.id] = item.profileImageName || '';
      return acc;
    }, {} as Record<number, string>);
  }

  private buildKycTrend(): void {
    const monthKeys = this.buildRecentMonthKeys(6);
    const counts = new Map<string, number>();
    monthKeys.forEach(item => counts.set(item.key, 0));

    this.profiles.forEach(item => {
      const sourceDate = item.createdAt || item.reviewedAt || item.updatedAt;
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

    this.kycTrendPoints = monthKeys.map((item, index) => {
      const value = counts.get(item.key) || 0;
      const x = monthKeys.length === 1 ? 310 : 28 + (index * (564 / (monthKeys.length - 1)));
      const y = 188 - ((value / maxValue) * 148);
      return { label: item.label, value, x, y };
    });

    this.kycTrendPath = this.kycTrendPoints
      .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
      .join(' ');
    this.kycTrendAreaPath = this.kycTrendPoints.length
      ? `${this.kycTrendPath} L ${this.kycTrendPoints[this.kycTrendPoints.length - 1].x} 188 L ${this.kycTrendPoints[0].x} 188 Z`
      : '';
  }

  private buildBranchRiskHeatmap(): void {
    const topBranches = this.branchDistribution.slice(0, 5);
    const levels = ['LOW', 'MEDIUM', 'HIGH'];
    const counts: number[] = [];

    this.branchRiskHeatRows = topBranches.map(branch => {
      const cells = levels.map(level => {
        const value = this.profiles.filter(item => this.getBranchName(item.branchId) === branch.branchName && item.riskLevel === level).length;
        counts.push(value);
        return {
          label: `${branch.branchName} / ${formatEnumLabel(level)}`,
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
    this.branchRiskHeatRows = this.branchRiskHeatRows.map(row => ({
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

  private isValidId(id?: number | null): id is number {
    return typeof id === 'number' && Number.isFinite(id) && id > 0;
  }
}
