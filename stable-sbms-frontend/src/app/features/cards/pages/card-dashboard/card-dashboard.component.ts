import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { CardDashboardSummaryResponse, CardResponse, CardTransactionResponse, formatEnumLabel } from '../../models/card.model';
import { CardService } from '../../services/card.service';

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
  selector: 'app-card-dashboard',
  templateUrl: './card-dashboard.component.html',
  styleUrls: ['./card-dashboard.component.scss']
})
export class CardDashboardComponent implements OnInit {

  loading = false;
  summary: CardDashboardSummaryResponse | null = null;
  statusLegend: DashboardLegendItem[] = [];
  cardBands: DashboardBandItem[] = [];
  eventColumns: DashboardColumnItem[] = [];
  branchColumns: DashboardColumnItem[] = [];
  eventAxisTicks: DashboardAxisTick[] = [];
  branchAxisTicks: DashboardAxisTick[] = [];
  cardRows: Array<{ label: string; value: string }> = [];
  usageTrend: DashboardTrendPoint[] = [];
  usageTrendTicks: DashboardAxisTick[] = [];
  usageTrendPath = '';
  usageTrendAreaPath = '';
  matrixColumns: DashboardHeatmapColumn[] = [];
  matrixRows: DashboardHeatmapRow[] = [];
  selectedCard: CardResponse | null = null;
  cardStatusGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  customerImageMap: Record<string, string> = {};
  userImageMap: Record<string, string> = {};

  constructor(
    private cardApi: CardService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private userApi: UserApiService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.loadUsers();
    this.load();
  }

  load(): void {
    this.loading = true;
    this.cardApi.getDashboardSummary().subscribe({
      next: summary => {
        this.summary = summary;
        this.prepareDashboard(summary);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load card dashboard.', 'error');
      }
    });
  }

  openList(): void {
    this.router.navigate(['/cards/list']);
  }

  openCreate(): void {
    this.router.navigate(['/cards/new']);
  }

  openTransactions(): void {
    this.router.navigate(['/cards/atm-cdm-transactions/list']);
  }

  openCard(item: CardResponse): void {
    this.router.navigate(['/cards', item.id]);
  }

  openTransaction(item: CardTransactionResponse): void {
    this.router.navigate(['/cards', item.cardId]);
  }

  focusCard(item: CardResponse): void {
    this.selectedCard = item;
  }

  focusTransaction(item: CardTransactionResponse): void {
    const matchedCard =
      this.summary?.expiringCards.find(card => card.id === item.cardId) ||
      this.summary?.pendingActivationCards.find(card => card.id === item.cardId) ||
      null;
    this.selectedCard = matchedCard || {
      id: item.cardId,
      cardRefNo: item.cardRefNo,
      customerId: item.customerId,
      customerCode: item.customerCode,
      customerName: item.customerName,
      accountId: item.accountId,
      accountNumber: item.accountNumber,
      accountTypeCode: '',
      accountTypeName: 'Card Holder',
      branchId: null,
      currentBalance: 0,
      cardType: 'ATM_CARD',
      maskedCardNo: item.maskedCardNo,
      issueDate: '',
      expiryDate: '',
      cardStatus: 'ACTIVE',
      blockReason: null,
      status: 'ACTIVE',
      expiringSoon: false,
      eventCount: 0,
      pinEventCount: 0,
      usageAlertCount: 1
    };
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  getCustomerImageUrl(customerCode?: string | null): string {
    if (!customerCode) {
      return '';
    }
    return this.customerImageMap[customerCode] || '';
  }

  getUserImageUrl(username?: string | null): string {
    const key = (username || '').trim().toLowerCase();
    return key ? this.userImageMap[key] || '' : '';
  }

  get selectedCardBadges(): Array<{ label: string; value: string }> {
    if (!this.selectedCard) {
      return [];
    }
    return [
      { label: 'Card Type', value: this.getLabel(this.selectedCard.cardType) },
      { label: 'Card Status', value: this.getLabel(this.selectedCard.cardStatus) },
      { label: 'Account', value: this.selectedCard.accountNumber || '-' },
      { label: 'Expiry', value: this.selectedCard.expiryDate || '-' }
    ];
  }

  get selectedCardBadgeText(): string {
    const label = this.selectedCard?.customerName || this.selectedCard?.cardRefNo || '';
    const compact = label
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(part => part.charAt(0).toUpperCase())
      .join('');
    return compact || 'CD';
  }

  private prepareDashboard(summary: CardDashboardSummaryResponse): void {
    const totalCards = Math.max(summary.totalCards, 1);
    this.statusLegend = [
      { label: 'Active Cards', value: summary.activeCards, note: 'ready for normal customer usage', color: '#22c55e' },
      { label: 'Blocked Cards', value: summary.blockedCards, note: 'cards currently under restriction', color: '#ef4444' },
      { label: 'Pending Activation', value: summary.pendingActivations, note: 'issued but not yet customer-ready', color: '#f59e0b' },
      { label: 'Expiring Soon', value: summary.expiringSoon, note: 'cards needing renewal attention', color: '#3b82f6' }
    ];
    this.cardStatusGradient = this.buildDonutGradient(this.statusLegend);

    this.cardBands = [
      {
        label: 'Activation Queue',
        value: summary.pendingActivations,
        note: `${summary.pendingActivationCards.length} cards visible in the current queue`,
        share: this.getShare(summary.pendingActivations, totalCards),
        tone: 'warning'
      },
      {
        label: 'Expiry Pressure',
        value: summary.expiringSoon,
        note: `${summary.expiringCards.length} expiring cards in the watchlist`,
        share: this.getShare(summary.expiringSoon, totalCards),
        tone: 'info'
      },
      {
        label: 'Blocked Exposure',
        value: summary.blockedCards,
        note: 'requires review for unblock, replace or renew',
        share: this.getShare(summary.blockedCards, totalCards),
        tone: 'danger'
      },
      {
        label: 'Usage Alerts Today',
        value: summary.cardUsageAlertsToday,
        note: `${summary.recentUsageAlerts.length} recent alert events in the current feed`,
        share: this.getShare(summary.cardUsageAlertsToday, Math.max(summary.cardTxnCount, 1)),
        tone: 'primary'
      }
    ];

    const eventMap = new Map<string, number>();
    summary.recentUsageAlerts.forEach(item => {
      const key = this.getLabel(item.eventType);
      eventMap.set(key, (eventMap.get(key) || 0) + 1);
    });
    const eventEntries = Array.from(eventMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxEvent = Math.max(...eventEntries.map(([, value]) => value), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'danger', 'warning', 'info', 'success'];
    this.eventColumns = eventEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxEvent),
      tone: tones[index % tones.length]
    }));
    this.eventAxisTicks = this.buildAxisTicks(maxEvent);

    const branchMap = new Map<string, number>();
    [...summary.expiringCards, ...summary.pendingActivationCards].forEach(card => {
      const key = this.toBranchChartName(card.branchId);
      branchMap.set(key, (branchMap.get(key) || 0) + 1);
    });
    const branchEntries = Array.from(branchMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxBranch = Math.max(...branchEntries.map(([, value]) => value), 1);
    this.branchColumns = branchEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxBranch),
      tone: tones[index % tones.length]
    }));
    this.branchAxisTicks = this.buildAxisTicks(maxBranch);

    const trendMap = new Map<string, number>();
    summary.recentUsageAlerts.forEach(item => {
      const key = item.eventDate ? item.eventDate.slice(0, 10) : 'Unknown';
      trendMap.set(key, (trendMap.get(key) || 0) + 1);
    });
    const trendEntries = Array.from(trendMap.entries()).sort((a, b) => a[0].localeCompare(b[0])).slice(-7);
    const maxTrend = Math.max(...trendEntries.map(([, value]) => value), 1);
    this.usageTrend = trendEntries.map(([label, value], index, items) => ({
      label,
      shortLabel: this.toDayLabel(label),
      value,
      x: items.length === 1 ? 50 : (index / (items.length - 1)) * 100,
      y: 100 - this.getChartHeight(value, maxTrend)
    }));
    this.usageTrendTicks = this.buildAxisTicks(maxTrend);
    this.usageTrendPath = this.usageTrend
      .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
      .join(' ');
    this.usageTrendAreaPath = this.usageTrend.length
      ? `${this.usageTrendPath} L ${this.usageTrend[this.usageTrend.length - 1].x} 100 L ${this.usageTrend[0].x} 100 Z`
      : '';

    this.matrixColumns = [
      { key: 'Expiring', label: 'Expiring' },
      { key: 'Pending', label: 'Pending Activation' },
      { key: 'Alerts', label: 'Usage Alerts' }
    ];
    this.matrixRows = branchEntries.slice(0, 4).map(([label]) => ({
      label,
      shortLabel: this.toChartLabel(label, 12),
      values: [
        summary.expiringCards.filter(card => this.toBranchChartName(card.branchId) === label).length,
        summary.pendingActivationCards.filter(card => this.toBranchChartName(card.branchId) === label).length,
        summary.recentUsageAlerts.filter(txn => {
          const matchedCard =
            summary.expiringCards.find(card => card.id === txn.cardId) ||
            summary.pendingActivationCards.find(card => card.id === txn.cardId);
          return this.toBranchChartName(matchedCard?.branchId ?? null) === label;
        }).length
      ]
    }));

    this.selectedCard = [...summary.expiringCards, ...summary.pendingActivationCards]
      .sort((a, b) => (b.currentBalance || 0) - (a.currentBalance || 0))[0]
      || summary.expiringCards[0]
      || summary.pendingActivationCards[0]
      || null;

    this.cardRows = [
      { label: 'Total Usage Events', value: `${summary.cardTxnCount}` },
      { label: 'Alert Intensity Today', value: `${summary.cardUsageAlertsToday}` },
      { label: 'Expiring Queue', value: `${summary.expiringCards.length}` },
      { label: 'Pending Activation', value: `${summary.pendingActivationCards.length}` }
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

  private toChartLabel(value: string, limit = 14): string {
    const compact = value.replace(/\s+/g, ' ').trim();
    if (!compact) {
      return 'Metric';
    }

    const words = compact.split(' ');
    const readable = words.slice(0, 2).join(' ');
    return readable.length <= limit ? readable : readable.slice(0, limit).trim();
  }

  private toDayLabel(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return this.toChartLabel(value);
    }
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }

  private toBranchChartName(branchId?: number | null): string {
    if (!branchId || branchId < 1) {
      return 'Unassigned';
    }
    return `BR-${branchId}`;
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

  private loadCustomers(): void {
    this.customerService.getAll().subscribe({
      next: customers => {
        this.customerImageMap = this.buildCustomerImageMap(customers || []);
      },
      error: () => {
        this.customerImageMap = {};
      }
    });
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<string, string> {
    return customers.reduce((acc, customer) => {
      if (customer.customerCode && customer.profileImageName) {
        acc[customer.customerCode] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<string, string>);
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
