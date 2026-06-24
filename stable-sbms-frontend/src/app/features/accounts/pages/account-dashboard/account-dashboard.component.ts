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
import {
  AccountDashboardSummaryResponse,
  AccountOpeningRequestResponse,
  AccountResponse,
  AccountTypeResponse,
  formatEnumLabel
} from '../../models/account.model';
import { AccountService } from '../../services/account.service';

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

@Component({
  selector: 'app-account-dashboard',
  templateUrl: './account-dashboard.component.html',
  styleUrls: ['./account-dashboard.component.scss']
})
export class AccountDashboardComponent implements OnInit {

  loading = false;
  summary: AccountDashboardSummaryResponse = {
    totalAccounts: 0,
    pendingOpeningRequests: 0,
    activeAccounts: 0,
    blockedAccounts: 0,
    frozenAccounts: 0,
    awaitingVerificationAccounts: 0
  };

  accountTypes: AccountTypeResponse[] = [];
  openingRequests: AccountOpeningRequestResponse[] = [];
  accounts: AccountResponse[] = [];
  branches: BranchResponse[] = [];

  accountsByType: Array<{ label: string; total: number }> = [];
  pendingRequests: AccountOpeningRequestResponse[] = [];
  blockedFrozenAccounts: AccountResponse[] = [];
  awaitingActivation: AccountResponse[] = [];
  recentAccounts: AccountResponse[] = [];
  totalLedgerBalance = 0;
  totalAvailableBalance = 0;
  pipelineBands: DashboardBandItem[] = [];
  typeBars: DashboardBarItem[] = [];
  branchExposureBars: DashboardBarItem[] = [];
  recentTimeline: DashboardTimelineItem[] = [];
  statusLegend: DashboardLegendItem[] = [];
  exposureColumns: DashboardColumnItem[] = [];
  exposureAxisTicks: DashboardAxisTick[] = [];
  typeAxisTicks: DashboardAxisTick[] = [];
  accountStatusGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  customerImageMap: Record<number, string> = {};
  customerImageByCode: Record<string, string> = {};
  customerImageByName: Record<string, string> = {};
  requestImageMap: Record<number, string> = {};
  requestImageByCode: Record<string, string> = {};
  requestImageByName: Record<string, string> = {};
  accountTrendPoints: DashboardTrendPoint[] = [];
  accountTrendPath = '';
  accountTrendAreaPath = '';
  selectedTypeBar: DashboardBarItem | null = null;
  spotlightAccount: AccountResponse | null = null;

  constructor(
    private accountApi: AccountService,
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
      summary: this.accountApi.getDashboardSummary(),
      accountTypes: this.accountApi.getAccountTypes(),
      openingRequests: this.accountApi.getOpeningRequests(),
      accounts: this.accountApi.getAccounts(),
      branches: this.branchApi.getAll().pipe(catchError(() => of([]))),
      customers: this.customerApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ summary, accountTypes, openingRequests, accounts, branches, customers }) => {
        this.summary = summary;
        this.accountTypes = accountTypes || [];
        this.openingRequests = openingRequests || [];
        this.accounts = accounts || [];
        this.branches = branches || [];
        this.customerImageMap = this.buildCustomerImageMap(customers || []);
        this.customerImageByCode = this.buildCustomerImageByCode(customers || []);
        this.customerImageByName = this.buildCustomerImageByName(customers || []);
        this.requestImageMap = this.buildRequestImageMap(this.openingRequests);
        this.requestImageByCode = this.buildRequestImageByCode(this.openingRequests);
        this.requestImageByName = this.buildRequestImageByName(this.openingRequests);
        this.prepareData();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load account dashboard.', 'error');
      }
    });
  }

  openAccount(id: number): void {
    this.router.navigate(['/accounts', id]);
  }

  openRequest(id: number): void {
    this.router.navigate(['/accounts/opening-requests', id]);
  }

  openReview(id: number): void {
    this.router.navigate(['/accounts/opening-requests', id, 'review']);
  }

  selectTypeBar(item: DashboardBarItem): void {
    this.selectedTypeBar = item;
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

  hasCustomerImage(customerId?: number | null): boolean {
    return !!(customerId && this.customerImageMap[customerId]);
  }

  getImageUrl(customerId?: number | null): string {
    return this.fileUploadService.resolveImageUrl(customerId ? this.customerImageMap[customerId] : '');
  }

  hasAccountHolderImage(account?: AccountResponse | null): boolean {
    return !!this.getAccountHolderImageName(account);
  }

  getAccountHolderImageUrl(account?: AccountResponse | null): string {
    return this.fileUploadService.resolveImageUrl(this.getAccountHolderImageName(account));
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private prepareData(): void {
    this.totalLedgerBalance = this.accounts.reduce((sum, item) => sum + Number(item.currentBalance || 0), 0);
    this.totalAvailableBalance = this.accounts.reduce((sum, item) => sum + Number(item.availableBalance || 0), 0);

    const typeMap = new Map<string, number>();
    this.accounts.forEach(item => {
      const label = item.accountTypeName || item.accountTypeCode || 'Unknown';
      typeMap.set(label, (typeMap.get(label) || 0) + 1);
    });

    this.accountsByType = Array.from(typeMap.entries())
      .map(([label, total]) => ({ label, total }))
      .sort((a, b) => b.total - a.total)
      .slice(0, 6);

    this.pendingRequests = this.openingRequests
      .filter(item => ['DRAFT', 'SUBMITTED', 'SENT_BACK', 'VERIFIED'].includes(item.requestStatus))
      .slice(0, 6);

    this.blockedFrozenAccounts = this.accounts
      .filter(item => ['BLOCKED', 'FROZEN'].includes(item.accountStatus))
      .slice(0, 6);

    this.awaitingActivation = this.accounts
      .filter(item => item.accountStatus === 'PENDING_ACTIVATION')
      .slice(0, 6);

    this.recentAccounts = [...this.accounts]
      .sort((a, b) => new Date(b.updatedAt || b.createdAt || '').getTime() - new Date(a.updatedAt || a.createdAt || '').getTime())
      .slice(0, 6);

    const totalAccounts = Math.max(this.summary.totalAccounts || this.accounts.length, 1);
    const typeMax = Math.max(...this.accountsByType.map(item => item.total), 1);

    this.pipelineBands = [
      {
        label: 'Pending Request Flow',
        value: this.summary.pendingOpeningRequests,
        note: `${this.pendingRequests.length} items need review or correction`,
        share: this.getShare(this.summary.pendingOpeningRequests, totalAccounts),
        tone: 'warning'
      },
      {
        label: 'Awaiting Verification',
        value: this.summary.awaitingVerificationAccounts,
        note: 'Opening requests still in document or control review',
        share: this.getShare(this.summary.awaitingVerificationAccounts, totalAccounts),
        tone: 'info'
      },
      {
        label: 'Pending Activation',
        value: this.awaitingActivation.length,
        note: 'Approved accounts waiting to go live',
        share: this.getShare(this.awaitingActivation.length, totalAccounts),
        tone: 'primary'
      },
      {
        label: 'Blocked or Frozen',
        value: this.summary.blockedAccounts + this.summary.frozenAccounts,
        note: 'Accounts under restriction or operational hold',
        share: this.getShare(this.summary.blockedAccounts + this.summary.frozenAccounts, totalAccounts),
        tone: 'danger'
      }
    ];
    this.statusLegend = [
      { label: 'Active', value: this.summary.activeAccounts, note: 'live usable accounts', color: '#22c55e' },
      { label: 'Pending', value: this.awaitingActivation.length, note: 'awaiting activation release', color: '#3b82f6' },
      { label: 'Verification', value: this.summary.awaitingVerificationAccounts, note: 'under review before activation', color: '#f59e0b' },
      { label: 'Restricted', value: this.summary.blockedAccounts + this.summary.frozenAccounts, note: 'blocked or frozen relationships', color: '#ef4444' }
    ];
    this.accountStatusGradient = this.buildDonutGradient(this.statusLegend);

    const tones: Array<DashboardBarItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    this.typeBars = this.accountsByType.map((item, index) => ({
      label: item.label,
      value: item.total,
      note: `${this.getShare(item.total, totalAccounts)}% of account base`,
      share: this.getShare(item.total, typeMax),
      tone: tones[index % tones.length]
    }));
    this.selectedTypeBar = this.typeBars[0] || null;
    this.typeAxisTicks = this.buildAxisTicks(typeMax);

    const branchMap = new Map<string, { value: number; count: number }>();
    this.accounts.forEach(item => {
      const label = this.getBranchName(item.branchId);
      const current = branchMap.get(label) || { value: 0, count: 0 };
      current.value += Number(item.availableBalance || item.currentBalance || 0);
      current.count += 1;
      branchMap.set(label, current);
    });
    const branchExposure = Array.from(branchMap.entries())
      .map(([label, snapshot]) => ({
        label,
        value: snapshot.value,
        count: snapshot.count
      }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 5);
    const branchMax = Math.max(...branchExposure.map(item => item.value), 1);
    this.branchExposureBars = branchExposure.map((item, index) => ({
      label: item.label,
      value: item.value,
      note: `${item.count} accounts | ${this.getShare(item.count, totalAccounts)}% coverage`,
      share: this.getShare(item.value, branchMax),
      tone: tones[index % tones.length]
    }));
    this.exposureColumns = branchExposure.map((item, index) => ({
      label: item.label,
      shortLabel: this.buildShortLabel(item.label),
      value: item.value,
      height: this.getShare(item.value, branchMax),
      tone: tones[index % tones.length]
    }));
    this.exposureAxisTicks = this.buildAxisTicks(branchMax);

    this.recentTimeline = this.recentAccounts.map(item => ({
      title: item.accountNumber,
      subtitle: `${item.customerName} | ${item.accountTypeCode || item.accountTypeName}`,
      meta: `${this.getBranchName(item.branchId)} | ${this.getLabel(item.accountStatus)}`,
      tone: item.accountStatus === 'ACTIVE'
        ? 'success'
        : item.accountStatus === 'PENDING_ACTIVATION'
          ? 'warning'
          : item.accountStatus === 'BLOCKED' || item.accountStatus === 'FROZEN'
            ? 'danger'
            : 'info'
    }));

    this.spotlightAccount = [...this.accounts]
      .sort((a, b) => Number(b.availableBalance || b.currentBalance || 0) - Number(a.availableBalance || a.currentBalance || 0))[0] || null;

    this.buildAccountTrend();
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
    const safeMax = Math.max(1, maxValue);
    return [0, 25, 50, 75, 100].map(percent => ({
      label: this.formatCompactNumber((safeMax * percent) / 100),
      bottom: percent
    }));
  }

  private buildAccountTrend(): void {
    const monthKeys = this.buildRecentMonthKeys(6);
    const counts = new Map<string, number>();

    monthKeys.forEach(item => counts.set(item.key, 0));

    this.accounts.forEach(item => {
      const sourceDate = item.openedDate || item.createdAt || item.updatedAt;
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

    this.accountTrendPoints = monthKeys.map((item, index) => {
      const value = counts.get(item.key) || 0;
      const x = monthKeys.length === 1 ? 310 : 28 + (index * (564 / (monthKeys.length - 1)));
      const y = 188 - ((value / maxValue) * 148);
      return {
        label: item.label,
        value,
        x,
        y
      };
    });

    this.accountTrendPath = this.accountTrendPoints
      .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
      .join(' ');
    this.accountTrendAreaPath = this.accountTrendPoints.length
      ? `${this.accountTrendPath} L ${this.accountTrendPoints[this.accountTrendPoints.length - 1].x} 188 L ${this.accountTrendPoints[0].x} 188 Z`
      : '';
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

  formatCompactNumber(value: number): string {
    return Intl.NumberFormat('en', {
      notation: 'compact',
      maximumFractionDigits: value >= 1000 ? 1 : 0
    }).format(value);
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, item) => {
      acc[item.id] = item.profileImageName || '';
      return acc;
    }, {} as Record<number, string>);
  }

  private buildCustomerImageByCode(customers: CustomerResponse[]): Record<string, string> {
    return customers.reduce((acc, item) => {
      if (item.customerCode) {
        acc[item.customerCode.trim().toLowerCase()] = item.profileImageName || '';
      }
      return acc;
    }, {} as Record<string, string>);
  }

  private buildCustomerImageByName(customers: CustomerResponse[]): Record<string, string> {
    return customers.reduce((acc, item) => {
      if (item.fullName) {
        acc[item.fullName.trim().toLowerCase()] = item.profileImageName || '';
      }
      return acc;
    }, {} as Record<string, string>);
  }

  private buildRequestImageMap(requests: AccountOpeningRequestResponse[]): Record<number, string> {
    return requests.reduce((acc, item) => {
      acc[item.id] = item.applicantImageName || '';
      return acc;
    }, {} as Record<number, string>);
  }

  private buildRequestImageByCode(requests: AccountOpeningRequestResponse[]): Record<string, string> {
    return requests.reduce((acc, item) => {
      if (item.customerCode) {
        acc[item.customerCode.trim().toLowerCase()] = item.applicantImageName || '';
      }
      return acc;
    }, {} as Record<string, string>);
  }

  private buildRequestImageByName(requests: AccountOpeningRequestResponse[]): Record<string, string> {
    return requests.reduce((acc, item) => {
      if (item.customerName) {
        acc[item.customerName.trim().toLowerCase()] = item.applicantImageName || '';
      }
      return acc;
    }, {} as Record<string, string>);
  }

  private getAccountHolderImageName(account?: AccountResponse | null): string {
    if (!account) {
      return '';
    }

    const customerImage = account.customerId ? this.customerImageMap[account.customerId] : '';
    if (customerImage) {
      return customerImage;
    }

    const customerCodeImage = account.customerCode
      ? this.customerImageByCode[account.customerCode.trim().toLowerCase()]
      : '';
    if (customerCodeImage) {
      return customerCodeImage;
    }

    const customerNameImage = account.customerName
      ? this.customerImageByName[account.customerName.trim().toLowerCase()]
      : '';
    if (customerNameImage) {
      return customerNameImage;
    }

    const openingRequestImage = account.openingRequestId ? this.requestImageMap[account.openingRequestId] : '';
    if (openingRequestImage) {
      return openingRequestImage;
    }

    const requestCodeImage = account.customerCode
      ? this.requestImageByCode[account.customerCode.trim().toLowerCase()]
      : '';
    if (requestCodeImage) {
      return requestCodeImage;
    }

    const requestNameImage = account.customerName
      ? this.requestImageByName[account.customerName.trim().toLowerCase()]
      : '';
    if (requestNameImage) {
      return requestNameImage;
    }

    const matchedRequest = this.openingRequests.find(item =>
      (account.requestNo && item.requestNo === account.requestNo) ||
      (!!account.customerId && !!item.customerId && item.customerId === account.customerId)
    );

    return matchedRequest?.applicantImageName || '';
  }
}
