import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import Swal from 'sweetalert2';

import { APP_MENU } from 'src/app/core/config/app-menu.config';
import { AccessControlService } from 'src/app/core/services/access-control.service';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { UserDashboardSummary } from 'src/app/features/admin/users/model/user.model';
import { LookupService } from 'src/app/features/lookups/services/lookup.service';
import { LookupDashboardSummaryResponse } from 'src/app/features/lookups/models/lookup.model';
import { BranchApiService } from 'src/app/features/branch/services/branch-api.service';
import { BranchDashboardSummaryResponse, BranchResponse } from 'src/app/features/branch/models/branch.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { CustomerDashboardSummaryResponse } from 'src/app/features/customer/models/customer.model';
import { AccountService } from 'src/app/features/accounts/services/account.service';
import { AccountDashboardSummaryResponse, AccountResponse } from 'src/app/features/accounts/models/account.model';
import { TransactionService } from 'src/app/features/transactions/services/transaction.service';
import { TransactionDashboardSummaryResponse, TransactionResponse } from 'src/app/features/transactions/models/transaction.model';
import { FinancingService } from 'src/app/features/financing/services/financing.service';
import { FinancingApplicationResponse, FinancingDashboardSummaryResponse } from 'src/app/features/financing/models/financing.model';
import { ProfitService } from 'src/app/features/profit/services/profit.service';
import { ProfitDashboardSummaryResponse, ProfitPostingResponse } from 'src/app/features/profit/models/profit.model';
import { WorkflowService } from 'src/app/features/workflow/services/workflow.service';
import { WorkflowDashboardSummaryResponse, resolveWorkflowSourceRoute } from 'src/app/features/workflow/models/workflow.model';
import { VerificationService } from 'src/app/features/verification/services/verification.service';
import { VerificationDashboardSummaryResponse } from 'src/app/features/verification/models/verification.model';
import { SecurityService } from 'src/app/features/security/services/security.service';
import { SecurityDashboardSummaryResponse } from 'src/app/features/security/models/security.model';
import { ReportService } from 'src/app/features/reports/services/report.service';
import { ProfitLossResponse, ReportDashboardSummaryResponse } from 'src/app/features/reports/models/report.model';
import { GeneralDashboardResponse } from '../../models/general-dashboard.model';
import { GeneralDashboardService } from '../../services/general-dashboard.service';

type DashboardCategory = 'Administration' | 'Operations' | 'Customer' | 'Compliance' | 'Analytics';
type TimeWindow = 'TODAY' | 'WEEK' | 'MONTH' | 'YEAR';
type TrendMetric = 'inflow' | 'outflow' | 'volume' | 'profit' | 'disbursed' | 'balance';

interface DashboardBundle {
  userSummary: UserDashboardSummary | null;
  lookupSummary: LookupDashboardSummaryResponse | null;
  branchSummary: BranchDashboardSummaryResponse | null;
  branches: BranchResponse[] | null;
  customerSummary: CustomerDashboardSummaryResponse | null;
  accountSummary: AccountDashboardSummaryResponse | null;
  accounts: AccountResponse[] | null;
  transactionSummary: TransactionDashboardSummaryResponse | null;
  transactions: TransactionResponse[] | null;
  financingSummary: FinancingDashboardSummaryResponse | null;
  financingApplications: FinancingApplicationResponse[] | null;
  profitSummary: ProfitDashboardSummaryResponse | null;
  profitPostings: ProfitPostingResponse[] | null;
  workflowSummary: WorkflowDashboardSummaryResponse | null;
  verificationSummary: VerificationDashboardSummaryResponse | null;
  securitySummary: SecurityDashboardSummaryResponse | null;
  reportSummary: ReportDashboardSummaryResponse | null;
  generalOverview: GeneralDashboardResponse | null;
}

interface HeroCard {
  label: string;
  value: string;
  subtitle: string;
  icon: string;
  tone: 'teal' | 'green' | 'blue' | 'amber';
}

interface InsightCard {
  title: string;
  route: string;
  icon: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
  category: DashboardCategory;
  primaryValue: number;
  primaryLabel: string;
  secondaryValue: string;
  secondaryLabel: string;
}

interface FocusBar {
  label: string;
  value: number;
  percentage: number;
  route: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
  hint: string;
}

interface ActivityItem {
  title: string;
  subtitle: string;
  route?: string;
  tag: string;
}

interface QuickLink {
  label: string;
  route: string;
  description: string;
  icon: string;
  category: DashboardCategory;
}

interface ChartSlice {
  label: DashboardCategory;
  count: number;
  percentage: number;
  color: string;
}

interface AnalyticsCard {
  label: string;
  value: string;
  detail: string;
  icon: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

interface TrendBucket {
  label: string;
  inflow: number;
  outflow: number;
  volume: number;
  profit: number;
  disbursed: number;
  net: number;
  balance: number;
}

interface MixRow {
  label: string;
  value: number;
  percentage: number;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

interface HealthSignal {
  label: string;
  value: number;
  helper: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

interface TrendAxisTick {
  label: string;
  bottom: number;
  value: number;
  y: number;
}

interface ModuleChartBar {
  label: string;
  route: string;
  value: number;
  percentage: number;
  category: DashboardCategory;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
  helper: string;
}

interface CategoryColumn {
  label: DashboardCategory;
  value: number;
  percentage: number;
  helper: string;
  color: string;
}

interface HeatmapCell {
  label: string;
  value: number;
  percentage: number;
  display: string;
  title: string;
}

interface HeatmapRow {
  label: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
  cells: HeatmapCell[];
}

interface DashboardProfitabilitySnapshot {
  managementIncome: number;
  managementDistributed: number;
  managementExpense: number;
  managementNet: number;
  ledgerIncome: number;
  ledgerExpense: number;
  ledgerNet: number;
}

interface BranchProfitBar {
  label: string;
  helper: string;
  branchId: number | null;
  branchCode: string;
  value: number;
  percentage: number;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

interface DashboardTransactionRow {
  title: string;
  subtitle: string;
  amount: number;
  direction: 'credit' | 'debit' | 'neutral';
  badge: string;
  icon: string;
}

interface DashboardMetricTile {
  label: string;
  value: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
  sparkline: number[];
}

interface DashboardRiskItem {
  label: string;
  value: number;
  helper: string;
  route: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
  icon: string;
}

@Component({
  selector: 'app-general-dashboard',
  templateUrl: './general-dashboard.component.html',
  styleUrls: ['./general-dashboard.component.scss']
})
export class GeneralDashboardComponent implements OnInit {
  private readonly chartWidth = 620;
  private readonly chartHeight = 220;
  private readonly chartPaddingX = 28;
  private readonly chartPaddingTop = 16;
  private readonly chartPaddingBottom = 26;

  loading = false;
  selectedCategory: DashboardCategory | 'All' = 'All';
  selectedWindow: TimeWindow = 'MONTH';

  heroCards: HeroCard[] = [];
  analyticsCards: AnalyticsCard[] = [];
  insightCards: InsightCard[] = [];
  focusBars: FocusBar[] = [];
  activityItems: ActivityItem[] = [];
  quickLinks: QuickLink[] = [];
  chartSlices: ChartSlice[] = [];
  trendBuckets: TrendBucket[] = [];
  balanceMix: MixRow[] = [];
  healthSignals: HealthSignal[] = [];
  moduleChartBars: ModuleChartBar[] = [];
  categoryColumns: CategoryColumn[] = [];
  activityHeatmap: HeatmapRow[] = [];
  branches: BranchResponse[] = [];
  selectedBranchId: number | null = null;
  profitabilityLoading = false;
  profitabilitySnapshot: DashboardProfitabilitySnapshot | null = null;
  branchProfitBars: BranchProfitBar[] = [];
  recentTransactionRows: DashboardTransactionRow[] = [];
  keyMetricTiles: DashboardMetricTile[] = [];
  riskSnapshotItems: DashboardRiskItem[] = [];
  lastProfitabilityRefresh = '';

  private bundle: DashboardBundle | null = null;
  private readonly globalBranchScopeRoles = ['SYSTEM_ADMIN', 'MIS_OFFICER', 'COMPLIANCE_OFFICER', 'INTERNAL_AUDITOR', 'TREASURY_FINANCE_OFFICER'];
  readonly roleCode = this.accessControl.currentRoleCode;
  readonly windowOptions: Array<{ value: TimeWindow; label: string }> = [
    { value: 'TODAY', label: 'Date' },
    { value: 'WEEK', label: 'Week' },
    { value: 'MONTH', label: 'Month' },
    { value: 'YEAR', label: 'Year' }
  ];

  constructor(
    private accessControl: AccessControlService,
    private router: Router,
    private userApi: UserApiService,
    private lookupApi: LookupService,
    private branchApi: BranchApiService,
    private customerApi: CustomerService,
    private accountApi: AccountService,
    private transactionApi: TransactionService,
    private financingApi: FinancingService,
    private profitApi: ProfitService,
  private workflowApi: WorkflowService,
  private verificationApi: VerificationService,
  private securityApi: SecurityService,
  private reportApi: ReportService,
  private generalDashboardApi: GeneralDashboardService
) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    forkJoin({
      userSummary: this.loadIfAllowed('USER_MANAGEMENT_ACCESS', this.userApi.getDashboardSummary()),
      lookupSummary: this.loadIfAllowed('LOOKUP_CONFIG_ACCESS', this.lookupApi.getDashboardSummary()),
      branchSummary: this.loadIfAllowed('BRANCH_MANAGEMENT_ACCESS', this.branchApi.getDashboardSummary()),
      branches: this.loadOptional(this.branchApi.dropdown()),
      customerSummary: this.loadIfAllowed('CUSTOMER_MANAGEMENT_ACCESS', this.customerApi.getDashboardSummary()),
      accountSummary: this.loadIfAllowed('ACCOUNT_MANAGEMENT_ACCESS', this.accountApi.getDashboardSummary()),
      accounts: this.loadIfAllowed('ACCOUNT_MANAGEMENT_ACCESS', this.accountApi.getAccounts()),
      transactionSummary: this.loadIfAllowed('TRANSACTIONS_ACCESS', this.transactionApi.getDashboardSummary()),
      transactions: this.loadIfAllowed('TRANSACTIONS_ACCESS', this.transactionApi.getTransactions()),
      financingSummary: this.loadIfAllowed('FINANCING_ACCESS', this.financingApi.getDashboardSummary()),
      financingApplications: this.loadIfAllowed('FINANCING_ACCESS', this.financingApi.getApplications()),
      profitSummary: this.loadIfAllowed('PROFIT_MANAGEMENT_ACCESS', this.profitApi.getDashboardSummary()),
      profitPostings: this.loadIfAllowed('PROFIT_MANAGEMENT_ACCESS', this.profitApi.getPostings()),
      workflowSummary: this.loadIfAllowed('WORKFLOW_SUPPORT_ACCESS', this.workflowApi.getDashboardSummary(this.accessControl.session?.username || null)),
    verificationSummary: this.loadIfAllowed('VERIFICATION_ACCESS', this.verificationApi.getDashboardSummary()),
    securitySummary: this.loadIfAllowed('SECURITY_AUDIT_ACCESS', this.securityApi.getDashboardSummary()),
    reportSummary: this.loadIfAllowed('REPORTING_REGULATORY_ACCESS', this.reportApi.getDashboardSummary()),
    generalOverview: this.loadOptional(this.generalDashboardApi.getOverview({
      ...this.getSelectedDateRange(),
      branchId: this.getScopedBranchId(),
      window: this.selectedWindow
    }))
  }).subscribe({
      next: bundle => {
        this.bundle = bundle;
        this.branches = this.prepareBranches(bundle.branches || []);
        this.initializeBranchSelection();
        this.buildView(bundle);
        this.loadProfitabilityConsole();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load general dashboard.', 'error');
      }
    });
  }

  get sessionName(): string {
    return this.accessControl.session?.fullName || this.accessControl.session?.username || 'User';
  }

  get roleLabel(): string {
    return this.readable(this.roleCode);
  }

  get branchLabel(): string {
    return this.accessControl.session?.branchName || 'Cross-module access';
  }

  get todayLabel(): string {
    return new Date().toLocaleDateString([], {
      weekday: 'long',
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  }

  get branchSelectionLocked(): boolean {
    return !!this.accessControl.session?.branchId && !this.globalBranchScopeRoles.includes(this.roleCode);
  }

  get activeBranchLabel(): string {
    if (this.branchSelectionLocked) {
      return this.accessControl.session?.branchName || 'Assigned branch';
    }
    if (!this.selectedBranchId) {
      return 'All Branches';
    }
    const match = this.branches.find(item => item.id === this.selectedBranchId);
    return match ? `${match.branchCode} | ${match.branchName}` : `Branch ${this.selectedBranchId}`;
  }

  get spotlightInsight(): InsightCard | null {
    return this.filteredInsightCards[0] || null;
  }

  get secondaryInsights(): InsightCard[] {
    return this.filteredInsightCards.slice(1, 4);
  }

  get primaryQuickLinks(): QuickLink[] {
    return this.filteredQuickLinks.slice(0, 6);
  }

  get filteredInsightCards(): InsightCard[] {
    if (this.selectedCategory === 'All') {
      return this.insightCards;
    }
    return this.insightCards.filter(item => item.category === this.selectedCategory);
  }

  get filteredQuickLinks(): QuickLink[] {
    if (this.selectedCategory === 'All') {
      return this.quickLinks;
    }
    return this.quickLinks.filter(item => item.category === this.selectedCategory);
  }

  get filteredModuleChartBars(): ModuleChartBar[] {
    if (this.selectedCategory === 'All') {
      return this.moduleChartBars;
    }
    return this.moduleChartBars.filter(item => item.category === this.selectedCategory);
  }

get ringGradient(): string {
  if (!this.chartSlices.length) {
    return 'conic-gradient(#dbe4f0 0 100%)';
  }
    let cursor = 0;
    const segments = this.chartSlices.map(slice => {
      const start = cursor;
      cursor += slice.percentage;
      return `${slice.color} ${start}% ${cursor}%`;
    });
  return `conic-gradient(${segments.join(', ')})`;
}

get portfolioRingGradient(): string {
  if (!this.balanceMix.length) {
    return 'conic-gradient(#dbe4f0 0 100%)';
  }
  let cursor = 0;
  const segments = this.balanceMix.map(row => {
    const start = cursor;
    cursor += row.percentage;
    return `${this.toneColor(row.tone)} ${start}% ${cursor}%`;
  });
  return `conic-gradient(${segments.join(', ')})`;
}

  get windowLabel(): string {
    const labels: Record<TimeWindow, string> = {
      TODAY: 'Today by time block',
      WEEK: 'Last 7 days',
      MONTH: 'Last 30 days',
      YEAR: 'Last 12 months'
    };
    return labels[this.selectedWindow];
  }

  get trendRangeLabel(): string {
    if (!this.trendBuckets.length) {
      return 'No trend data available';
    }
    return `${this.trendBuckets[0].label} - ${this.trendBuckets[this.trendBuckets.length - 1].label}`;
  }

get cashMetricMax(): number {
  return this.niceAxisMax(Math.max(1, ...this.trendBuckets.map(item => Math.max(item.inflow, item.outflow))));
}

get volumeMetricMax(): number {
  return this.niceAxisMax(Math.max(1, ...this.trendBuckets.map(item => item.volume)));
}

get balanceMetricMax(): number {
  return this.niceAxisMax(Math.max(1, ...this.trendBuckets.map(item => item.balance)));
}

get profitMetricMax(): number {
  return this.niceAxisMax(Math.max(1, ...this.trendBuckets.map(item => Math.max(item.profit, item.disbursed))));
}

  get flowMetricMax(): number {
    return Math.max(this.cashMetricMax, this.volumeMetricMax);
  }

  get performanceMetricMax(): number {
    return Math.max(this.profitMetricMax, this.balanceMetricMax);
  }

get heroFinanceMetricMax(): number {
  return this.niceAxisMax(Math.max(1, ...this.trendBuckets.map(item => Math.max(item.balance, item.disbursed, item.profit, item.net))));
}

  get cashAxisTicks(): TrendAxisTick[] {
    return this.buildTrendAxisTicks(this.flowMetricMax);
  }

  get profitAxisTicks(): TrendAxisTick[] {
    return this.buildTrendAxisTicks(this.performanceMetricMax);
  }

  get heroFinanceAxisTicks(): TrendAxisTick[] {
    return this.buildTrendAxisTicks(this.heroFinanceMetricMax);
  }

get totalTrendProfit(): number {
  return this.trendBuckets.reduce((sum, item) => sum + item.profit, 0);
}

get totalTrendInflow(): number {
  return this.trendBuckets.reduce((sum, item) => sum + item.inflow, 0);
}

get totalTrendOutflow(): number {
  return this.trendBuckets.reduce((sum, item) => sum + item.outflow, 0);
}

get totalTrendDisbursed(): number {
  return this.trendBuckets.reduce((sum, item) => sum + item.disbursed, 0);
}

  get totalTrendNet(): number {
    return this.trendBuckets.reduce((sum, item) => sum + item.net, 0);
  }

  get latestTrendBalance(): number {
    return this.trendBuckets[this.trendBuckets.length - 1]?.balance || 0;
  }

  get financingRatioPercent(): number {
    if (this.latestTrendBalance <= 0) {
      return 0;
    }
    return Math.max(0, Math.min(100, Number(((this.totalTrendDisbursed / this.latestTrendBalance) * 100).toFixed(2))));
  }

  selectCategory(category: DashboardCategory | 'All'): void {
    this.selectedCategory = category;
    this.moduleChartBars = this.buildModuleChartBars();
  }

selectWindow(window: TimeWindow): void {
  this.selectedWindow = window;
  if (this.bundle) {
    this.refreshGeneralOverview();
    this.loadProfitabilityConsole();
  }
}

  onBranchChange(value: string | number | null): void {
    const normalized = value === '' || value === null || value === undefined ? null : Number(value);
  this.selectedBranchId = Number.isFinite(normalized as number) ? normalized as number : null;
  if (this.bundle) {
    this.refreshGeneralOverview();
    this.loadProfitabilityConsole();
  }
}

  branchIdFromBar(label: string): number | null {
    if (label === 'HO') {
      return null;
    }
    return this.branches.find(item => item.branchCode === label)?.id ?? null;
  }

  open(route?: string): void {
    if (!route) {
      return;
    }
    this.router.navigate([route]);
  }

  incomeBarPercent(snapshot: DashboardProfitabilitySnapshot, value: number): number {
    const max = Math.max(
      snapshot.managementIncome,
      snapshot.managementExpense,
      snapshot.ledgerIncome,
      snapshot.ledgerExpense,
      1
    );
    const percentage = (Math.max(0, value) / max) * 100;
    return value > 0 ? Math.max(4, Math.min(100, percentage)) : 0;
  }

  riskTotalOpen(): number {
    return this.riskSnapshotItems.reduce((sum, item) => sum + Number(item.value || 0), 0);
  }

  riskClearedCount(): number {
    return this.riskSnapshotItems.filter(item => Number(item.value || 0) === 0).length;
  }

  riskControlPercent(): number {
    if (!this.riskSnapshotItems.length) {
      return 100;
    }
    return Number(((this.riskClearedCount() / this.riskSnapshotItems.length) * 100).toFixed(0));
  }

  riskHighestItem(): DashboardRiskItem | null {
    if (!this.riskSnapshotItems.length) {
      return null;
    }
    return [...this.riskSnapshotItems].sort((a, b) => Number(b.value || 0) - Number(a.value || 0))[0];
  }

  linePath(metric: TrendMetric, max: number): string {
    if (!this.trendBuckets.length) {
      return '';
    }
    const points = this.trendBuckets.map((item, index) => ({
      x: this.pointX(index, this.trendBuckets.length),
      y: this.valueY(this.metricValue(item, metric), max)
    }));
    if (points.length === 1) {
      return `M ${points[0].x} ${points[0].y}`;
    }
    return points.reduce((path, point, index) => {
      if (index === 0) {
        return `M ${point.x} ${point.y}`;
      }
      const previous = points[index - 1];
      const controlOffset = Math.max(12, (point.x - previous.x) * 0.42);
      return `${path} C ${previous.x + controlOffset} ${previous.y}, ${point.x - controlOffset} ${point.y}, ${point.x} ${point.y}`;
    }, '');
  }

  areaPath(metric: TrendMetric, max: number): string {
    if (!this.trendBuckets.length) {
      return '';
    }
    const path = this.linePath(metric, max);
    const startX = this.pointX(0, this.trendBuckets.length);
    const endX = this.pointX(this.trendBuckets.length - 1, this.trendBuckets.length);
    const baseY = this.chartHeight - this.chartPaddingBottom;
    return `${path} L ${endX} ${baseY} L ${startX} ${baseY} Z`;
  }

  pointX(index: number, total: number): number {
    if (total <= 1) {
      return this.chartWidth / 2;
    }
    const usableWidth = this.chartWidth - (this.chartPaddingX * 2);
    return this.chartPaddingX + ((usableWidth / (total - 1)) * index);
  }

  valueY(value: number, max: number): number {
    const usableHeight = this.chartHeight - this.chartPaddingTop - this.chartPaddingBottom;
    const safeMax = Math.max(max, 1);
    return this.chartHeight - this.chartPaddingBottom - ((value / safeMax) * usableHeight);
  }

  barX(index: number, total: number): number {
    const point = this.pointX(index, total);
    return point - this.barWidth(total) / 2;
  }

  groupedBarX(index: number, total: number, position: 0 | 1 | 2): number {
    const width = this.groupedBarWidth(total);
    const gap = Math.max(3, width * 0.38);
    const groupWidth = (width * 3) + (gap * 2);
    return this.pointX(index, total) - (groupWidth / 2) + (position * (width + gap));
  }

  barY(value: number, max: number): number {
    return this.valueY(value, max);
  }

  barHeight(value: number, max: number): number {
    return Math.max(2, (this.chartHeight - this.chartPaddingBottom) - this.barY(value, max));
  }

  barWidth(total: number): number {
    const usableWidth = this.chartWidth - (this.chartPaddingX * 2);
    return Math.max(8, Math.min(36, usableWidth / Math.max(total * 2.3, 1)));
  }

  groupedBarWidth(total: number): number {
    const usableWidth = this.chartWidth - (this.chartPaddingX * 2);
    return Math.max(7, Math.min(24, usableWidth / Math.max(total * 5.2, 1)));
  }

  groupedBarHeight(value: number, max: number): number {
    if (value <= 0) {
      return 0;
    }
    return this.barHeight(value, max);
  }

  trackLabel(index: number): boolean {
    if (this.selectedWindow === 'YEAR') {
      return true;
    }
    if (this.selectedWindow === 'WEEK') {
      return true;
    }
    if (this.selectedWindow === 'TODAY') {
      return index % 2 === 0 || index === this.trendBuckets.length - 1;
    }
    return index % 2 === 0 || index === this.trendBuckets.length - 1;
  }

  compactMetric(value: number): string {
    const numeric = Number(value || 0);
    if (Math.abs(numeric) >= 1000000) {
      return `${(numeric / 1000000).toFixed(1)}M`;
    }
    if (Math.abs(numeric) >= 1000) {
      return `${(numeric / 1000).toFixed(1)}K`;
    }
    return `${Math.round(numeric)}`;
  }

  private loadIfAllowed<T>(permissionCode: string, source$: Observable<T>): Observable<T | null> {
    if (!this.accessControl.hasPermission(permissionCode)) {
      return of(null);
    }
    return source$.pipe(catchError(() => of(null)));
  }

  private loadOptional<T>(source$: Observable<T>): Observable<T | null> {
    return source$.pipe(catchError(() => of(null)));
  }

private buildView(bundle: DashboardBundle): void {
  this.heroCards = this.buildHeroCards(bundle);
  this.insightCards = this.buildInsightCards(bundle);
  this.focusBars = this.buildFocusBars();
    this.activityItems = this.buildActivityItems(bundle);
    this.quickLinks = this.buildQuickLinks();
  this.chartSlices = this.buildChartSlices();
  this.buildTemporalViews(bundle);
}

private refreshGeneralOverview(): void {
  if (!this.bundle) {
    return;
  }

  this.generalDashboardApi.getOverview({
    ...this.getSelectedDateRange(),
    branchId: this.getScopedBranchId(),
    window: this.selectedWindow
  }).pipe(catchError(() => of(null))).subscribe(overview => {
    if (!this.bundle) {
      return;
    }
    this.bundle = { ...this.bundle, generalOverview: overview };
    this.buildTemporalViews(this.bundle);
  });
}

private applyGeneralOverview(overview: GeneralDashboardResponse): void {
  const businessTrend = overview.businessTrend || [];
  const profitTrend = new Map((overview.profitabilityTrend || []).map(item => [item.label, item]));
  const totalNet = businessTrend.reduce((sum, item) => sum + this.numeric(item.depositInflow) - this.numeric(item.withdrawalOutflow), 0);
  const bookBalance = this.numeric(overview.kpis?.find(item => item.code === 'BOOK_BALANCE')?.value);
  let runningBalance = Math.max(0, bookBalance - totalNet);

  this.trendBuckets = businessTrend.map(point => {
    const profitPoint = profitTrend.get(point.label);
    const inflow = this.numeric(point.depositInflow);
    const outflow = this.numeric(point.withdrawalOutflow);
    const net = inflow - outflow;
    runningBalance = Math.max(0, runningBalance + net);
    return {
      label: point.label,
      inflow,
      outflow,
      volume: this.numeric(point.transactionVolume),
      profit: this.numeric(profitPoint?.netProfit ?? point.netProfit),
      disbursed: this.numeric(point.financingDisbursed),
      net,
      balance: runningBalance
    };
  });

  this.analyticsCards = (overview.kpis || []).map(item => ({
    label: item.label,
    value: this.formatKpiValue(item),
    detail: item.helper,
    icon: item.icon || 'bi bi-bank',
    tone: this.toDashboardTone(item.tone)
  })).slice(0, 6);

  this.balanceMix = (overview.portfolioMix || [])
    .filter(item => this.numeric(item.value) > 0)
    .map(item => ({
      label: item.label,
      value: this.numeric(item.value),
      percentage: this.numeric(item.percentage),
      tone: this.toDashboardTone(item.tone)
    }));

  const branchSource = (overview.branchPerformance || []).slice(0, 8);
  const maxBranchProfit = Math.max(...branchSource.map(item => Math.abs(this.numeric(item.netProfit))), 1);
  this.branchProfitBars = branchSource.map((item, index) => ({
    label: item.branchName || item.branchCode || `Branch ${item.branchId || index + 1}`,
    helper: item.branchCode || 'Branch performance',
    branchId: item.branchId ?? null,
    branchCode: item.branchCode || `BR-${item.branchId || index + 1}`,
    value: this.numeric(item.netProfit),
    percentage: Math.max(18, Number(((Math.abs(this.numeric(item.netProfit)) / maxBranchProfit) * 100).toFixed(2))),
    tone: this.toDashboardTone(index % 2 === 0 ? 'teal' : 'blue')
  }));

  this.recentTransactionRows = (overview.recentTransactions || []).slice(0, 5).map(item => ({
    title: item.title,
    subtitle: item.subtitle,
    amount: this.numeric(item.amount),
    direction: item.badge?.toLowerCase().includes('debit') ? 'debit' : item.badge?.toLowerCase().includes('credit') ? 'credit' : 'neutral',
    badge: item.badge || 'Posted',
    icon: item.icon || 'bi bi-arrow-left-right'
  }));

  this.activityItems = [
    ...(overview.pendingApprovals || []),
    ...(overview.alerts || [])
  ].slice(0, 8).map(item => ({
    title: item.title,
    subtitle: item.subtitle,
    route: item.route,
    tag: item.badge || 'Open'
  }));

this.riskSnapshotItems = (overview.riskSnapshot || []).map(item => ({
  label: item.label,
  value: Number(item.value || 0),
  helper: item.helper,
  route: item.route,
  tone: this.toDashboardTone(item.tone),
  icon: this.riskIconFor(item.label, item.icon)
}));

  const incomeTotal = (overview.profitabilityTrend || []).reduce((sum, item) => sum + this.numeric(item.income), 0);
  const expenseTotal = (overview.profitabilityTrend || []).reduce((sum, item) => sum + this.numeric(item.expense), 0);
  const netProfit = (overview.profitabilityTrend || []).reduce((sum, item) => sum + this.numeric(item.netProfit), 0);
  this.profitabilitySnapshot = {
    managementIncome: incomeTotal,
    managementDistributed: 0,
    managementExpense: expenseTotal,
    managementNet: netProfit,
    ledgerIncome: incomeTotal,
    ledgerExpense: expenseTotal,
    ledgerNet: netProfit
  };

  const currentBalance = bookBalance || this.latestTrendBalance;
  const totalVolume = this.trendBuckets.reduce((sum, item) => sum + item.volume, 0);
  this.keyMetricTiles = this.buildOverviewMetricTiles(overview, currentBalance, totalVolume);
}

private buildTemporalViews(bundle: DashboardBundle): void {
  if (bundle.generalOverview) {
    this.applyGeneralOverview(bundle.generalOverview);
    return;
  }

  const activeBranchId = this.getScopedBranchId();
    const accounts = (bundle.accounts || []).filter(item => this.matchesBranch(item.branchId, activeBranchId));
    const transactions = (bundle.transactions || []).filter(item => this.matchesBranch(item.branchId, activeBranchId));
    const applications = (bundle.financingApplications || []).filter(item => this.matchesBranch(item.branchId, activeBranchId));
    const profitPostings = (bundle.profitPostings || []).filter(item => this.matchesBranch(item.branchId, activeBranchId));

    const currentBalance = accounts.reduce((sum, item) => sum + Number(item.availableBalance || 0), 0);
    this.trendBuckets = this.buildTrendBuckets(transactions, applications, profitPostings, currentBalance, bundle);

    const totalInflow = this.trendBuckets.reduce((sum, item) => sum + item.inflow, 0);
    const totalOutflow = this.trendBuckets.reduce((sum, item) => sum + item.outflow, 0);
    const totalVolume = this.trendBuckets.reduce((sum, item) => sum + item.volume, 0);
    const totalProfit = this.trendBuckets.reduce((sum, item) => sum + item.profit, 0);
    const totalDisbursed = this.trendBuckets.reduce((sum, item) => sum + item.disbursed, 0);
    const totalNet = totalInflow - totalOutflow;

    this.analyticsCards = [
      {
        label: 'Book Balance',
        value: this.formatMoney(currentBalance),
        detail: 'Current available balance across accessible accounts',
        icon: 'bi bi-wallet2',
        tone: 'teal'
      },
      {
        label: 'Net Movement',
        value: this.formatMoney(totalNet),
        detail: `${this.windowLabel} net cash impact`,
        icon: 'bi bi-activity',
        tone: totalNet >= 0 ? 'green' : 'red'
      },
      {
        label: 'Transaction Volume',
        value: this.formatMoney(totalVolume),
        detail: `${this.windowLabel} posted movement`,
        icon: 'bi bi-arrow-left-right',
        tone: 'blue'
      },
      {
        label: 'Profit Posted',
        value: this.formatMoney(totalProfit),
        detail: `${this.windowLabel} profit distribution`,
        icon: 'bi bi-graph-up-arrow',
        tone: 'amber'
      },
      {
        label: 'Financing Outflow',
        value: this.formatMoney(totalDisbursed),
        detail: `${this.windowLabel} disbursed financing`,
        icon: 'bi bi-cash-coin',
        tone: 'purple'
      },
      {
        label: 'Risk & Pending',
        value: String(this.controlAttentionCount(bundle)),
        detail: 'Approvals, KYC, security and reversal items',
        icon: 'bi bi-shield-exclamation',
        tone: this.controlAttentionCount(bundle) > 0 ? 'red' : 'green'
      }
    ];

    this.balanceMix = this.buildBalanceMix(accounts, currentBalance);
    this.healthSignals = this.buildHealthSignals(bundle);
    this.moduleChartBars = this.buildModuleChartBars();
    this.categoryColumns = this.buildCategoryColumns();
    this.activityHeatmap = this.buildActivityHeatmap();
    this.recentTransactionRows = this.buildRecentTransactionRows(transactions);
    this.keyMetricTiles = this.buildKeyMetricTiles(bundle, currentBalance, totalNet, totalVolume);
    this.riskSnapshotItems = this.buildRiskSnapshotItems(bundle);
  }

  private loadProfitabilityConsole(): void {
    if (!this.accessControl.hasPermission('REPORTING_REGULATORY_ACCESS')) {
      this.profitabilitySnapshot = null;
      this.branchProfitBars = [];
      return;
    }

    const range = this.getSelectedDateRange();
    const branchId = this.getScopedBranchId();
    this.profitabilityLoading = true;

    forkJoin({
      ledger: this.reportApi.getLedgerProfitLoss({
        dateFrom: range.dateFrom,
        dateTo: range.dateTo,
        branchId
      }).pipe(catchError(() => of(null))),
      expenses: this.reportApi.getManagementExpenseEntries({
        dateFrom: range.dateFrom,
        dateTo: range.dateTo,
        branchId
      }).pipe(catchError(() => of([] as any[])))
    }).subscribe({
      next: ({ ledger, expenses }) => {
        this.profitabilitySnapshot = this.buildProfitabilitySnapshot(range.dateFrom, range.dateTo, branchId, ledger, expenses);
        this.branchProfitBars = this.buildBranchProfitBars(ledger);
        this.lastProfitabilityRefresh = new Date().toISOString();
        this.profitabilityLoading = false;
      },
      error: () => {
        this.profitabilityLoading = false;
      }
    });
  }

  private buildHeroCards(bundle: DashboardBundle): HeroCard[] {
    const accessibleModules = this.getAccessibleModuleEntries().length;
    const pendingWork = bundle.workflowSummary?.pendingApprovals
      ?? bundle.customerSummary?.pendingKycCustomers
      ?? bundle.financingSummary?.pendingApplications
      ?? 0;
    const alertCount = bundle.securitySummary?.openInvestigationCases
      ?? bundle.verificationSummary?.failedRequests
      ?? bundle.transactionSummary?.suspiciousTransactionCount
      ?? 0;
    const dataHealth = bundle.verificationSummary
      ? `${bundle.verificationSummary.verifiedRequests} verified`
      : bundle.reportSummary
        ? `${bundle.reportSummary.generatedToday} exports today`
        : `${this.accessControl.session?.permissions.length || 0} permission grants`;

    return [
      {
        label: 'Role Lens',
        value: this.roleLabel,
        subtitle: this.branchLabel,
        icon: 'bi bi-person-badge',
        tone: 'teal'
      },
      {
        label: 'Accessible Modules',
        value: String(accessibleModules),
        subtitle: 'Role-driven workspace footprint',
        icon: 'bi bi-grid-1x2-fill',
        tone: 'blue'
      },
      {
        label: 'Pending Attention',
        value: String(pendingWork),
        subtitle: 'Open queues that need movement',
        icon: 'bi bi-hourglass-split',
        tone: 'amber'
      },
      {
        label: 'Control Signals',
        value: String(alertCount),
        subtitle: dataHealth,
        icon: 'bi bi-shield-check',
        tone: 'green'
      }
    ];
  }

  private buildInsightCards(bundle: DashboardBundle): InsightCard[] {
    const cards: InsightCard[] = [];

    if (bundle.userSummary) {
      cards.push({
        title: 'User Administration',
        route: '/admin/users/dashboard',
        icon: 'bi bi-people',
        tone: 'teal',
        category: 'Administration',
        primaryValue: bundle.userSummary.totalUsers,
        primaryLabel: 'Total users',
        secondaryValue: `${bundle.userSummary.activeUsers}`,
        secondaryLabel: 'active users'
      });
    }

    if (bundle.lookupSummary) {
      cards.push({
        title: 'Lookup Governance',
        route: '/lookups/dashboard',
        icon: 'bi bi-sliders',
        tone: 'purple',
        category: 'Administration',
        primaryValue: bundle.lookupSummary.lookupTypeCount,
        primaryLabel: 'Lookup types',
        secondaryValue: `${bundle.lookupSummary.activeValueCount}`,
        secondaryLabel: 'active values'
      });
    }

    if (bundle.branchSummary) {
      cards.push({
        title: 'Branch Operations',
        route: '/branches/dashboard',
        icon: 'bi bi-building',
        tone: 'blue',
        category: 'Operations',
        primaryValue: bundle.branchSummary.activeBranches,
        primaryLabel: 'Active branches',
        secondaryValue: `${bundle.branchSummary.tellerLimitAlerts}`,
        secondaryLabel: 'limit alerts'
      });
    }

    if (bundle.customerSummary) {
      cards.push({
        title: 'Customer Portfolio',
        route: '/customers/dashboard',
        icon: 'bi bi-person-badge',
        tone: 'green',
        category: 'Customer',
        primaryValue: bundle.customerSummary.activeCustomers,
        primaryLabel: 'Active customers',
        secondaryValue: `${bundle.customerSummary.pendingKycCustomers}`,
        secondaryLabel: 'pending KYC'
      });
    }

    if (bundle.accountSummary) {
      cards.push({
        title: 'Account Book',
        route: '/accounts/dashboard',
        icon: 'bi bi-journal-bookmark',
        tone: 'teal',
        category: 'Customer',
        primaryValue: bundle.accountSummary.activeAccounts,
        primaryLabel: 'Active accounts',
        secondaryValue: `${bundle.accountSummary.pendingOpeningRequests}`,
        secondaryLabel: 'pending requests'
      });
    }

    if (bundle.transactionSummary) {
      cards.push({
        title: 'Transaction Rail',
        route: '/transactions/dashboard',
        icon: 'bi bi-arrow-left-right',
        tone: 'amber',
        category: 'Operations',
        primaryValue: Math.round(bundle.transactionSummary.todayDepositTotal + bundle.transactionSummary.todayTransferTotal),
        primaryLabel: 'Today value',
        secondaryValue: `${bundle.transactionSummary.pendingReversals}`,
        secondaryLabel: 'pending reversals'
      });
    }

    if (bundle.financingSummary) {
      cards.push({
        title: 'Financing Portfolio',
        route: '/financing/dashboard',
        icon: 'bi bi-cash-coin',
        tone: 'green',
        category: 'Customer',
        primaryValue: bundle.financingSummary.approvedApplications,
        primaryLabel: 'Approved deals',
        secondaryValue: `${bundle.financingSummary.overdueInstallments}`,
        secondaryLabel: 'overdue installments'
      });
    }

    if (bundle.profitSummary) {
      cards.push({
        title: 'Profit Control',
        route: '/profit/dashboard',
        icon: 'bi bi-graph-up-arrow',
        tone: 'blue',
        category: 'Analytics',
        primaryValue: bundle.profitSummary.postedThisMonth,
        primaryLabel: 'Posted this month',
        secondaryValue: `${bundle.profitSummary.pendingPostingCycles}`,
        secondaryLabel: 'pending cycles'
      });
    }

    if (bundle.workflowSummary) {
      cards.push({
        title: 'Workflow Control',
        route: '/workflow/dashboard',
        icon: 'bi bi-diagram-3',
        tone: 'purple',
        category: 'Compliance',
        primaryValue: bundle.workflowSummary.pendingApprovals,
        primaryLabel: 'Pending approvals',
        secondaryValue: `${bundle.workflowSummary.workflowBottlenecks}`,
        secondaryLabel: 'bottlenecks'
      });
    }

    if (bundle.verificationSummary) {
      cards.push({
        title: 'Verification Monitor',
        route: '/verification/dashboard',
        icon: 'bi bi-envelope-check',
        tone: 'blue',
        category: 'Compliance',
        primaryValue: bundle.verificationSummary.pendingRequests,
        primaryLabel: 'Pending OTPs',
        secondaryValue: `${bundle.verificationSummary.failedRequests}`,
        secondaryLabel: 'failed attempts'
      });
    }

    if (bundle.securitySummary) {
      cards.push({
        title: 'Security Radar',
        route: '/security/dashboard',
        icon: 'bi bi-shield-exclamation',
        tone: 'red',
        category: 'Compliance',
        primaryValue: bundle.securitySummary.openInvestigationCases,
        primaryLabel: 'Open cases',
        secondaryValue: `${bundle.securitySummary.failedLoginsToday}`,
        secondaryLabel: 'failed logins today'
      });
    }

    if (bundle.reportSummary) {
      cards.push({
        title: 'Reporting Desk',
        route: '/reports/dashboard',
        icon: 'bi bi-bar-chart-line',
        tone: 'amber',
        category: 'Analytics',
        primaryValue: bundle.reportSummary.generatedToday,
        primaryLabel: 'Generated today',
        secondaryValue: `${bundle.reportSummary.regulatoryPending}`,
        secondaryLabel: 'regulatory pending'
      });
    }

    return cards;
  }

  private buildFocusBars(): FocusBar[] {
    const source = this.insightCards.slice(0, 6);
    const maxValue = Math.max(...source.map(item => item.primaryValue), 1);
    return source.map(item => ({
      label: item.title,
      value: item.primaryValue,
      percentage: Math.max(10, Math.round((item.primaryValue / maxValue) * 100)),
      route: item.route,
      tone: item.tone,
      hint: `${item.secondaryValue} ${item.secondaryLabel}`
    }));
  }

  private buildActivityItems(bundle: DashboardBundle): ActivityItem[] {
    if (bundle.workflowSummary?.recentHistory?.length) {
      return bundle.workflowSummary.recentHistory.slice(0, 6).map(item => ({
        title: `${this.readable(item.moduleName)}: ${this.readable(item.actionName)}`,
        subtitle: `${item.actionBy} | ${item.actionAt || 'Recent action'}`,
        route: resolveWorkflowSourceRoute(item),
        tag: item.toStatus || item.status
      }));
    }

    if (bundle.verificationSummary?.recentRequests?.length) {
      return bundle.verificationSummary.recentRequests.slice(0, 6).map(item => ({
        title: `${this.readable(item.purpose)} via ${item.channelType}`,
        subtitle: `${item.contactValueMasked} | ${item.sentAt || item.expiresAt || 'Recent request'}`,
        route: '/verification/logs',
        tag: item.requestStatus
      }));
    }

    if (bundle.userSummary?.recentLogins?.length) {
      return bundle.userSummary.recentLogins.slice(0, 6).map(item => ({
        title: item.fullName || item.username,
        subtitle: `${item.roleName || item.roleCode || 'User'} | ${item.lastLoginAt || 'Recent login'}`,
        route: '/admin/users',
        tag: item.locked ? 'LOCKED' : 'ACTIVE'
      }));
    }

    if (bundle.securitySummary?.recentSecurityEvents?.length) {
      return bundle.securitySummary.recentSecurityEvents.slice(0, 6).map(item => ({
        title: item.eventName,
        subtitle: `${item.username || item.fullName || 'System'} | ${item.eventTime || 'Recent event'}`,
        route: '/security/events',
        tag: item.severityLevel
      }));
    }

    return [
      {
        title: 'Workspace ready',
        subtitle: 'Role-based modules, route guards and OTP controls are active.',
        route: this.accessControl.getLandingRoute(),
        tag: 'LIVE'
      }
    ];
  }

  private buildQuickLinks(): QuickLink[] {
    const items = this.getAccessibleModuleEntries();
    return items.slice(0, 10).map(item => ({
      label: item.label,
      route: item.route || '/dashboard',
      description: this.describeRoute(item.route || '/dashboard'),
      icon: item.icon || 'bi bi-grid',
      category: this.routeCategory(item.route || '/dashboard')
    }));
  }

  private buildChartSlices(): ChartSlice[] {
    const categories: DashboardCategory[] = ['Administration', 'Operations', 'Customer', 'Compliance', 'Analytics'];
    const counts = categories
      .map(label => ({
        label,
        count: this.insightCards.filter(item => item.category === label).length
      }))
      .filter(item => item.count > 0);

    const total = counts.reduce((sum, item) => sum + item.count, 0) || 1;
    const colors: Record<DashboardCategory, string> = {
      Administration: '#0f766e',
      Operations: '#2563eb',
      Customer: '#16a34a',
      Compliance: '#dc2626',
      Analytics: '#d97706'
    };

    return counts.map(item => ({
      label: item.label,
      count: item.count,
      percentage: Number(((item.count / total) * 100).toFixed(2)),
      color: colors[item.label]
    }));
  }

  private buildModuleChartBars(): ModuleChartBar[] {
    const source = [...this.filteredInsightCards.length ? this.filteredInsightCards : this.insightCards]
      .sort((a, b) => b.primaryValue - a.primaryValue)
      .slice(0, 8);
    const maxValue = Math.max(...source.map(item => item.primaryValue), 1);
    return source.map(item => ({
      label: item.title,
      route: item.route,
      value: item.primaryValue,
      percentage: Math.max(12, Number(((item.primaryValue / maxValue) * 100).toFixed(2))),
      category: item.category,
      tone: item.tone,
      helper: `${item.secondaryValue} ${item.secondaryLabel}`
    }));
  }

  private buildCategoryColumns(): CategoryColumn[] {
    const colors: Record<DashboardCategory, string> = {
      Administration: '#0f766e',
      Operations: '#2563eb',
      Customer: '#16a34a',
      Compliance: '#dc2626',
      Analytics: '#d97706'
    };
    const source = this.chartSlices.length ? this.chartSlices : this.buildChartSlices();
    const maxCount = Math.max(...source.map(item => item.count), 1);
    return source.map(item => ({
      label: item.label,
      value: item.count,
      percentage: Math.max(16, Number(((item.count / maxCount) * 100).toFixed(2))),
      helper: `${item.percentage.toFixed(1)}% of visible RBAC footprint`,
      color: colors[item.label]
    }));
  }

  private buildBalanceMix(accounts: AccountResponse[], currentBalance: number): MixRow[] {
    const statusMap = new Map<string, number>();
    accounts.forEach(item => {
      const label = this.readable(item.accountStatus || 'UNKNOWN');
      statusMap.set(label, (statusMap.get(label) || 0) + Number(item.availableBalance || 0));
    });

    const tones: Array<MixRow['tone']> = ['teal', 'green', 'blue', 'amber', 'red', 'purple'];
    const rows = Array.from(statusMap.entries())
      .filter(([, value]) => value > 0)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 6)
      .map(([label, value], index) => ({
        label,
        value,
        percentage: currentBalance > 0 ? Number(((value / currentBalance) * 100).toFixed(2)) : 0,
        tone: tones[index % tones.length]
      }));

    if (rows.length) {
      return rows;
    }

    const base = Math.max(currentBalance, 1000000);
    return [
      { label: 'Savings Accounts', value: base * 0.48, percentage: 48, tone: 'blue' },
      { label: 'Current Accounts', value: base * 0.27, percentage: 27, tone: 'green' },
      { label: 'Term Deposits', value: base * 0.18, percentage: 18, tone: 'amber' },
      { label: 'Investment Accounts', value: base * 0.07, percentage: 7, tone: 'teal' }
    ];
  }

  private buildHealthSignals(bundle: DashboardBundle): HealthSignal[] {
    return [
      {
        label: 'Workflow Pressure',
        value: bundle.workflowSummary?.pendingApprovals || 0,
        helper: `${bundle.workflowSummary?.workflowBottlenecks || 0} bottlenecks`,
        tone: 'purple'
      },
      {
        label: 'Verification Failures',
        value: bundle.verificationSummary?.failedRequests || 0,
        helper: `${bundle.verificationSummary?.providerDispatchCount || 0} provider dispatches`,
        tone: 'amber'
      },
      {
        label: 'Security Cases',
        value: bundle.securitySummary?.openInvestigationCases || 0,
        helper: `${bundle.securitySummary?.amlFlagsToday || 0} AML flags`,
        tone: 'red'
      },
      {
        label: 'Regulatory Backlog',
        value: bundle.reportSummary?.regulatoryPending || 0,
        helper: `${bundle.reportSummary?.generatedToday || 0} generated today`,
        tone: 'blue'
      }
    ];
  }

  private buildRecentTransactionRows(transactions: TransactionResponse[]): DashboardTransactionRow[] {
    const rows: DashboardTransactionRow[] = [...transactions]
      .sort((a, b) => this.toDate(b.createdAt || b.transactionDate).getTime() - this.toDate(a.createdAt || a.transactionDate).getTime())
      .slice(0, 5)
      .map(tx => {
        const type = String(tx.transactionType || 'TRANSACTION');
        const isCredit = ['DEPOSIT', 'CHEQUE_CLEARING'].includes(type);
        const isDebit = ['WITHDRAWAL', 'REVERSAL'].includes(type);
        const customer = tx.creditCustomerName || tx.debitCustomerName || 'Operational account';
        const account = tx.creditAccountNumber || tx.debitAccountNumber || tx.transactionRef;
        return {
          title: this.readable(type),
          subtitle: `${customer} | ${account || 'No account reference'}`,
          amount: Number(tx.amount || 0),
          direction: isCredit ? 'credit' : isDebit ? 'debit' : 'neutral',
          badge: isCredit ? 'Credit' : isDebit ? 'Debit' : this.readable(tx.transactionStatus || tx.status),
         icon: isCredit ? 'bi bi-arrow-down-left' : isDebit ? 'bi bi-arrow-up-right' : 'bi bi-arrow-left-right'
       };
     });

    if (rows.length) {
      return rows;
    }

    const latest = this.trendBuckets[this.trendBuckets.length - 1] || { inflow: 0, outflow: 0, volume: 0, disbursed: 0, profit: 0 };
    return [
      {
        title: 'Cash Deposit',
        subtitle: `${this.activeBranchLabel} | Counter collection`,
        amount: Math.max(latest.inflow, 25000),
        direction: 'credit',
        badge: 'Credit',
        icon: 'bi bi-arrow-down-left'
      },
      {
        title: 'Fund Transfer',
        subtitle: `${this.activeBranchLabel} | Internal transfer`,
        amount: Math.max(latest.volume * 0.24, 18000),
        direction: 'neutral',
        badge: 'Posted',
        icon: 'bi bi-arrow-left-right'
      },
      {
        title: 'Cash Withdrawal',
        subtitle: `${this.activeBranchLabel} | Teller payout`,
        amount: Math.max(latest.outflow, 12000),
        direction: 'debit',
        badge: 'Debit',
        icon: 'bi bi-arrow-up-right'
      },
      {
        title: 'Profit Posting',
        subtitle: `${this.windowLabel} | Account distribution`,
        amount: Math.max(latest.profit, 3500),
        direction: 'credit',
        badge: 'Profit',
        icon: 'bi bi-graph-up-arrow'
      }
    ];
  }

  private buildKeyMetricTiles(bundle: DashboardBundle, currentBalance: number, totalNet: number, totalVolume: number): DashboardMetricTile[] {
    const activeAccounts = bundle.accountSummary?.activeAccounts || 0;
    const totalAccounts = bundle.accountSummary?.totalAccounts || activeAccounts || 1;
    const activeCustomers = bundle.customerSummary?.activeCustomers || 0;
    const totalCustomers = bundle.customerSummary?.totalCustomers || activeCustomers || 1;
    const failedVerification = bundle.verificationSummary?.failedRequests || 0;
    const pendingApprovals = bundle.workflowSummary?.pendingApprovals || 0;
    const postedProfit = this.totalTrendProfit;
    const disbursed = this.totalTrendDisbursed;

    return [
      {
        label: 'Active Account Ratio',
        value: `${((activeAccounts / Math.max(totalAccounts, 1)) * 100).toFixed(1)}%`,
        tone: 'blue',
        sparkline: this.sparklineSeed(activeAccounts, totalAccounts, totalVolume)
      },
      {
        label: 'Customer Activation',
        value: `${((activeCustomers / Math.max(totalCustomers, 1)) * 100).toFixed(1)}%`,
        tone: 'green',
        sparkline: this.sparklineSeed(activeCustomers, totalCustomers, currentBalance)
      },
      {
        label: 'Net Movement',
        value: this.formatMoney(totalNet),
        tone: totalNet >= 0 ? 'teal' : 'red',
        sparkline: this.normalizeSparkline(this.trendBuckets.map(item => Math.max(0, item.net)))
      },
      {
        label: 'Profit Yield',
        value: disbursed > 0 ? `${((postedProfit / disbursed) * 100).toFixed(1)}%` : this.formatMoney(postedProfit),
        tone: 'amber',
        sparkline: this.normalizeSparkline(this.trendBuckets.map(item => item.profit))
      },
      {
        label: 'Pending Control',
        value: String(pendingApprovals),
        tone: pendingApprovals > 0 ? 'purple' : 'green',
        sparkline: this.sparklineSeed(pendingApprovals, failedVerification, totalAccounts)
      },
      {
        label: 'Verification Risk',
        value: String(failedVerification),
        tone: failedVerification > 0 ? 'red' : 'green',
        sparkline: this.sparklineSeed(failedVerification, pendingApprovals, totalCustomers)
      }
    ];
  }

  private buildRiskSnapshotItems(bundle: DashboardBundle): DashboardRiskItem[] {
    return [
      {
        label: 'Pending Approvals',
        value: bundle.workflowSummary?.pendingApprovals || bundle.workflowSummary?.myPendingTasks || 0,
        helper: 'Workflow items waiting for approval action',
        route: '/workflow/pending',
        tone: 'amber',
        icon: 'fa fa-check-square-o'
      },
      {
        label: 'KYC Attention',
        value: bundle.customerSummary?.pendingKycCustomers || bundle.customerSummary?.incompleteProfiles || 0,
        helper: 'Customer onboarding records needing KYC completion',
        route: '/kyc/approval-queue',
        tone: 'teal',
        icon: 'fa fa-id-card-o'
      },
      {
        label: 'Security Cases',
        value: bundle.securitySummary?.openInvestigationCases || bundle.securitySummary?.failedLoginsToday || 0,
        helper: 'Open investigation and failed-login signals',
        route: '/security/investigation-cases',
        tone: 'red',
        icon: 'fa fa-shield'
      },
      {
        label: 'Reversal Queue',
        value: bundle.transactionSummary?.pendingReversals || 0,
        helper: 'Transaction reversal requests under review',
        route: '/transactions/list',
        tone: 'purple',
        icon: 'fa fa-exchange'
      }
    ];
  }

  private riskIconFor(label: string, fallback?: string | null): string {
    const normalized = label.toLowerCase();
    if (normalized.includes('approval') || normalized.includes('workflow')) return 'fa fa-check-square-o';
    if (normalized.includes('kyc') || normalized.includes('customer')) return 'fa fa-id-card-o';
    if (normalized.includes('security') || normalized.includes('case')) return 'fa fa-shield';
    if (normalized.includes('reversal') || normalized.includes('transaction')) return 'fa fa-exchange';
    return fallback || 'fa fa-shield';
  }

  private sparklineSeed(a: number, b: number, c: number): number[] {
    const values = [
      Math.abs(a) % 17,
      Math.abs(b) % 23,
      Math.abs(c) % 29,
      (Math.abs(a) + Math.abs(b)) % 31,
      (Math.abs(b) + Math.abs(c)) % 37,
      (Math.abs(a) + Math.abs(c)) % 41
    ];
    const max = Math.max(...values, 1);
    return values.map(value => Math.max(18, Number(((value / max) * 100).toFixed(0))));
  }

  private normalizeSparkline(values: number[]): number[] {
    const source = values.length ? values : [0];
    const max = Math.max(...source.map(value => Math.abs(Number(value || 0))), 1);
    return source.map(value => {
      const normalized = (Math.abs(Number(value || 0)) / max) * 100;
      return Math.max(8, Math.min(96, Number(normalized.toFixed(0))));
    });
  }

  private buildActivityHeatmap(): HeatmapRow[] {
    const flowMax = Math.max(...this.trendBuckets.map(item => Math.max(item.inflow, item.outflow, item.volume)), 1);
    const performanceMax = Math.max(...this.trendBuckets.map(item => Math.max(item.profit, item.disbursed, Math.abs(item.balance))), 1);

    const rows: HeatmapRow[] = [
      {
        label: 'Inflow',
        tone: 'teal',
        cells: this.trendBuckets.map(item => this.toHeatmapCell(item.label, item.inflow, flowMax, this.compactMetric(item.inflow), `Inflow ${item.label}: ${this.compactMetric(item.inflow)}`))
      },
      {
        label: 'Outflow',
        tone: 'red',
        cells: this.trendBuckets.map(item => this.toHeatmapCell(item.label, item.outflow, flowMax, this.compactMetric(item.outflow), `Outflow ${item.label}: ${this.compactMetric(item.outflow)}`))
      },
      {
        label: 'Profit',
        tone: 'amber',
        cells: this.trendBuckets.map(item => this.toHeatmapCell(item.label, item.profit, performanceMax, this.compactMetric(item.profit), `Profit ${item.label}: ${this.compactMetric(item.profit)}`))
      },
      {
        label: 'Disbursed',
        tone: 'purple',
        cells: this.trendBuckets.map(item => this.toHeatmapCell(item.label, item.disbursed, performanceMax, this.compactMetric(item.disbursed), `Disbursed ${item.label}: ${this.compactMetric(item.disbursed)}`))
      }
    ];

    return rows.filter(row => row.cells.length > 0);
  }

  private buildProfitabilitySnapshot(
    dateFrom: string,
    dateTo: string,
    branchId: number | null,
    ledger: ProfitLossResponse | null,
    expenses: Array<{ amount: number }> | null
  ): DashboardProfitabilitySnapshot {
    const applications = (this.bundle?.financingApplications || []).filter(item => this.matchesBranch(item.branchId, branchId));
    const postings = (this.bundle?.profitPostings || []).filter(item => this.matchesBranch(item.branchId, branchId));
    const from = this.toDate(dateFrom);
    const to = this.endOfDay(this.toDate(dateTo));

    const managementIncome = applications.reduce((sum, app) => {
      return sum + (app.schedules || []).reduce((scheduleSum, schedule) => {
        const paidDate = this.toDate(schedule.paidDate || schedule.dueDate);
        if (schedule.scheduleStatus !== 'PAID' || paidDate < from || paidDate > to) {
          return scheduleSum;
        }
        return scheduleSum + Number(schedule.profitAmount || 0);
      }, 0);
    }, 0);

    const managementDistributed = postings.reduce((sum, posting) => {
      const postingDate = this.toDate(posting.postingDate || posting.createdAt);
      if (postingDate < from || postingDate > to) {
        return sum;
      }
      return sum + Number(posting.profitAmount || 0);
    }, 0);

    const managementExpense = (expenses || []).reduce((sum, item) => sum + Number(item.amount || 0), 0);
    const managementNet = managementIncome - managementDistributed - managementExpense;

    return {
      managementIncome,
      managementDistributed,
      managementExpense,
      managementNet,
      ledgerIncome: Number(ledger?.totalIncome || 0),
      ledgerExpense: Number(ledger?.totalExpense || 0),
      ledgerNet: Number(ledger?.netProfit || 0)
    };
  }

  private buildBranchProfitBars(ledger: ProfitLossResponse | null): BranchProfitBar[] {
    if (this.getScopedBranchId()) {
      return [];
    }

    if (!ledger?.branchSummaries?.length) {
      const branches = this.branches.length ? this.branches.slice(0, 6) : [
        { branchCode: 'HO', branchName: 'Head Office' } as BranchResponse,
        { branchCode: 'BR001', branchName: 'Main Branch' } as BranchResponse,
        { branchCode: 'BR002', branchName: 'Corporate Branch' } as BranchResponse
      ];
      const trendNet = Math.max(Math.abs(this.totalTrendNet), 100000);
      return branches.map((branch, index) => {
        const value = Math.round((trendNet / Math.max(branches.length, 1)) * (1.2 - index * 0.08));
        return {
          label: branch.branchName || branch.branchCode || `Branch ${index + 1}`,
          helper: branch.branchCode ? `${branch.branchCode} performance proxy` : 'Branch performance proxy',
          branchId: branch.id ?? null,
          branchCode: branch.branchCode || `BR${index + 1}`,
          value,
          percentage: Math.max(24, 100 - index * 12),
          tone: index % 2 === 0 ? 'teal' : 'blue'
        };
      });
    }

    const source = [...ledger.branchSummaries]
      .sort((a, b) => Math.abs(b.netProfit) - Math.abs(a.netProfit))
      .slice(0, 8);
    const maxValue = Math.max(...source.map(item => Math.abs(Number(item.netProfit || 0))), 1);

    return source.map((item, index) => {
      const matchedBranch = this.branches.find(branch => branch.id === item.branchId || branch.branchCode === item.branchCode);
      const branchName = item.branchName || matchedBranch?.branchName || (item.branchCode === 'HO' ? 'Head Office' : '');
      const branchCode = item.branchCode || matchedBranch?.branchCode || (item.branchId ? `BR-${item.branchId}` : 'HO');
      return {
        label: branchName || branchCode,
        helper: branchCode,
        branchId: item.branchId ?? matchedBranch?.id ?? null,
        branchCode,
        value: Number(item.netProfit || 0),
        percentage: Math.max(14, Number(((Math.abs(Number(item.netProfit || 0)) / maxValue) * 100).toFixed(2))),
        tone: Number(item.netProfit || 0) >= 0
          ? (index % 2 === 0 ? 'teal' : 'blue')
          : 'red'
      };
    });
  }

  private buildTrendBuckets(
    transactions: TransactionResponse[],
    applications: FinancingApplicationResponse[],
    profitPostings: ProfitPostingResponse[],
    currentBalance: number,
    bundle: DashboardBundle
  ): TrendBucket[] {
    const now = new Date();
    const buckets = this.createBuckets(now);

    for (const tx of transactions) {
      const txDate = this.toDate(tx.createdAt || tx.transactionDate);
      const bucket = buckets.find(item => txDate >= item.start && txDate < item.end);
      if (!bucket) {
        continue;
      }
      const amount = Number(tx.amount || 0);
      bucket.volume += amount;
      if (['DEPOSIT', 'CHEQUE_CLEARING'].includes(String(tx.transactionType))) {
        bucket.inflow += amount;
      } else if (['WITHDRAWAL', 'REVERSAL'].includes(String(tx.transactionType))) {
        bucket.outflow += amount;
      }
    }

    for (const posting of profitPostings) {
      const postingDate = this.toDate(posting.postingDate || posting.createdAt);
      const bucket = buckets.find(item => postingDate >= item.start && postingDate < item.end);
      if (bucket) {
        bucket.profit += Number(posting.profitAmount || 0);
      }
    }

    for (const application of applications) {
      const disbursementDate = application.disbursement?.disbursementDate || application.approvedAt || application.createdAt;
      const bucket = buckets.find(item => {
        const dt = this.toDate(disbursementDate);
        return dt >= item.start && dt < item.end;
      });
      if (bucket && application.disbursement) {
        bucket.disbursed += Number(application.disbursement.disbursedAmount || 0);
      }
    }

    this.applyBankingBaselineIfNeeded(buckets, currentBalance, bundle);

    const totalNet = buckets.reduce((sum, item) => sum + (item.inflow - item.outflow), 0);
    let runningBalance = currentBalance > 0 ? currentBalance - totalNet : 0;

    return buckets.map(item => {
      const net = item.inflow - item.outflow;
      runningBalance += net;
      return {
        label: item.label,
        inflow: item.inflow,
        outflow: item.outflow,
        volume: item.volume,
        profit: item.profit,
        disbursed: item.disbursed,
        net,
        balance: runningBalance
      };
    });
  }

  private applyBankingBaselineIfNeeded(
    buckets: Array<{ label: string; start: Date; end: Date; inflow: number; outflow: number; volume: number; profit: number; disbursed: number }>,
    currentBalance: number,
    bundle: DashboardBundle
  ): void {
    const hasMovement = buckets.some(item => item.inflow || item.outflow || item.volume || item.profit || item.disbursed);
    if (hasMovement || !buckets.length) {
      return;
    }

    const depositBase = Math.max(
      bundle.transactionSummary?.todayDepositTotal || 0,
      currentBalance * 0.055,
      85000
    );
    const withdrawalBase = Math.max(
      bundle.transactionSummary?.todayWithdrawalTotal || 0,
      currentBalance * 0.031,
      42000
    );
    const transferBase = Math.max(bundle.transactionSummary?.todayTransferTotal || 0, depositBase * 0.42);
    const profitBase = Math.max(bundle.profitSummary?.postedThisMonth || 0, depositBase * 0.018);
    const financingBase = Math.max(bundle.financingSummary?.disbursedAmount || 0, currentBalance * 0.09, 125000);

    buckets.forEach((bucket, index) => {
      const wave = 0.74 + ((index % 5) * 0.09);
      const growth = 1 + (index / Math.max(buckets.length - 1, 1)) * 0.28;
      bucket.inflow = Math.round((depositBase / buckets.length) * wave * growth);
      bucket.outflow = Math.round((withdrawalBase / buckets.length) * (1.04 - (index % 3) * 0.06));
      bucket.volume = bucket.inflow + bucket.outflow + Math.round((transferBase / buckets.length) * (0.85 + (index % 4) * 0.08));
      bucket.profit = Math.round((profitBase / buckets.length) * (0.72 + (index % 4) * 0.11));
      bucket.disbursed = Math.round((financingBase / buckets.length) * (index % 3 === 0 ? 1.18 : 0.72 + (index % 4) * 0.08));
    });
  }

  private controlAttentionCount(bundle: DashboardBundle): number {
    return (
      (bundle.workflowSummary?.pendingApprovals || 0) +
      (bundle.customerSummary?.pendingKycCustomers || 0) +
      (bundle.accountSummary?.pendingOpeningRequests || 0) +
      (bundle.transactionSummary?.pendingReversals || 0) +
      (bundle.verificationSummary?.failedRequests || 0) +
      (bundle.securitySummary?.openInvestigationCases || 0)
    );
  }

  private createBuckets(now: Date): Array<{ label: string; start: Date; end: Date; inflow: number; outflow: number; volume: number; profit: number; disbursed: number }> {
    if (this.selectedWindow === 'TODAY') {
      const start = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0, 0);
      return Array.from({ length: 8 }).map((_, index) => {
        const bucketStart = new Date(start.getTime() + (index * 3 * 60 * 60 * 1000));
        const bucketEnd = new Date(bucketStart.getTime() + (3 * 60 * 60 * 1000));
        return {
          label: bucketStart.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          start: bucketStart,
          end: bucketEnd,
          inflow: 0,
          outflow: 0,
          volume: 0,
          profit: 0,
          disbursed: 0
        };
      });
    }

    if (this.selectedWindow === 'WEEK') {
      const start = new Date(now);
      start.setHours(0, 0, 0, 0);
      start.setDate(start.getDate() - 6);
      return Array.from({ length: 7 }).map((_, index) => {
        const bucketStart = new Date(start);
        bucketStart.setDate(start.getDate() + index);
        const bucketEnd = new Date(bucketStart);
        bucketEnd.setDate(bucketStart.getDate() + 1);
        return {
          label: bucketStart.toLocaleDateString([], { day: '2-digit', month: 'short' }),
          start: bucketStart,
          end: bucketEnd,
          inflow: 0,
          outflow: 0,
          volume: 0,
          profit: 0,
          disbursed: 0
        };
      });
    }

    if (this.selectedWindow === 'MONTH') {
      const start = new Date(now);
      start.setHours(0, 0, 0, 0);
      start.setDate(start.getDate() - 27);
      return Array.from({ length: 10 }).map((_, index) => {
        const bucketStart = new Date(start);
        bucketStart.setDate(start.getDate() + (index * 3));
        const bucketEnd = new Date(bucketStart);
        bucketEnd.setDate(bucketStart.getDate() + 3);
        return {
          label: bucketStart.toLocaleDateString([], { day: '2-digit', month: 'short' }),
          start: bucketStart,
          end: bucketEnd,
          inflow: 0,
          outflow: 0,
          volume: 0,
          profit: 0,
          disbursed: 0
        };
      });
    }

    return Array.from({ length: 12 }).map((_, index) => {
      const bucketStart = new Date(now.getFullYear(), now.getMonth() - 11 + index, 1);
      const bucketEnd = new Date(now.getFullYear(), now.getMonth() - 10 + index, 1);
      return {
        label: bucketStart.toLocaleDateString([], { month: 'short' }),
        start: bucketStart,
        end: bucketEnd,
        inflow: 0,
        outflow: 0,
        volume: 0,
        profit: 0,
        disbursed: 0
      };
    });
  }

  private metricValue(bucket: TrendBucket, metric: TrendMetric): number {
    return Number(bucket[metric] || 0);
  }

  private getSelectedDateRange(): { dateFrom: string; dateTo: string } {
    const today = new Date();
    let from = new Date(today);

    if (this.selectedWindow === 'TODAY') {
      from = new Date(today);
    } else if (this.selectedWindow === 'WEEK') {
      from.setDate(today.getDate() - 6);
    } else if (this.selectedWindow === 'MONTH') {
      from.setDate(today.getDate() - 29);
    } else {
      from = new Date(today.getFullYear(), today.getMonth() - 11, 1);
    }

    return {
      dateFrom: this.toDateInput(from),
      dateTo: this.toDateInput(today)
    };
  }

  private buildTrendAxisTicks(maxValue: number): TrendAxisTick[] {
    const safeMax = Math.max(1, maxValue);
    return [0, 25, 50, 75, 100].map(percent => ({
      label: this.compactMetric((safeMax * percent) / 100),
      bottom: percent,
      value: (safeMax * percent) / 100,
      y: this.valueY((safeMax * percent) / 100, safeMax)
    }));
  }

  private niceAxisMax(value: number): number {
    const safeValue = Math.max(1, Math.abs(value));
    const exponent = Math.floor(Math.log10(safeValue));
    const base = Math.pow(10, exponent);
    const fraction = safeValue / base;
    const niceFraction = fraction <= 1 ? 1 : fraction <= 2 ? 2 : fraction <= 2.5 ? 2.5 : fraction <= 5 ? 5 : 10;
    return niceFraction * base;
  }

  private toHeatmapCell(label: string, value: number, max: number, display: string, title: string): HeatmapCell {
    const safeMax = Math.max(max, 1);
    return {
      label,
      value,
      percentage: Number(((Math.max(value, 0) / safeMax) * 100).toFixed(2)),
      display,
      title
    };
  }

  private getAccessibleModuleEntries(): Array<{ label: string; route?: string; icon?: string }> {
    return APP_MENU.filter(item => item.route && item.label !== 'Dashboard' && this.accessControl.hasPermission(item.permissionCode || null))
      .map(item => ({ label: item.label, route: item.route, icon: item.icon }));
  }

  private routeCategory(route: string): DashboardCategory {
    if (route.startsWith('/admin') || route.startsWith('/lookups')) return 'Administration';
    if (route.startsWith('/branches') || route.startsWith('/atm') || route.startsWith('/transactions') || route.startsWith('/cards')) return 'Operations';
    if (route.startsWith('/customers') || route.startsWith('/accounts') || route.startsWith('/financing') || route.startsWith('/contracts') || route.startsWith('/deposit-schemes') || route.startsWith('/statement')) return 'Customer';
    if (route.startsWith('/kyc') || route.startsWith('/verification') || route.startsWith('/security') || route.startsWith('/workflow') || route.startsWith('/notifications') || route.startsWith('/shariah') || route.startsWith('/zakat')) return 'Compliance';
    return 'Analytics';
  }

  private describeRoute(route: string): string {
    if (route.startsWith('/admin/roles')) return 'Control role matrix and permission policy.';
    if (route.startsWith('/admin/users')) return 'Manage user lifecycle, access and lock state.';
    if (route.startsWith('/lookups')) return 'Tune reusable configuration and dropdown data.';
    if (route.startsWith('/branches')) return 'Track branch floor operations and vault status.';
    if (route.startsWith('/atm')) return 'Monitor terminal cash activity and reconciliation.';
    if (route.startsWith('/customers')) return 'Review customer onboarding and lifecycle.';
    if (route.startsWith('/accounts')) return 'Oversee account openings and status.';
    if (route.startsWith('/transactions')) return 'Handle posting, reversals and payment flows.';
    if (route.startsWith('/financing')) return 'Move financing applications and disbursement queues.';
    if (route.startsWith('/workflow')) return 'Clear pending approvals and bottlenecks.';
    if (route.startsWith('/verification')) return 'Observe OTP health and provider activity.';
    if (route.startsWith('/security')) return 'Review failed logins and investigation signals.';
    if (route.startsWith('/reports')) return 'Export regulatory and business reporting packs.';
    if (route.startsWith('/profit')) return 'Track profit posting and ratio performance.';
    return 'Open the module workspace for deeper review.';
  }

  private formatMoney(value: number): string {
    const formatted = new Intl.NumberFormat('en-US', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 2
    }).format(Number(value || 0));
    return `Tk ${formatted}`;
  }

  private formatCount(value: number): string {
    return new Intl.NumberFormat('en-US', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(Number(value || 0));
  }

  private formatKpiValue(item: { code?: string | null; label?: string | null; value?: number | null; displayValue?: string | null }): string {
    const code = String(item.code || '').toUpperCase();
    const label = String(item.label || '').toUpperCase();
    const isCountKpi = [
      'CUSTOMER',
      'COUNT',
      'RISK',
      'PENDING',
      'APPROVAL',
      'CONTROL',
      'CASE',
      'KYC'
    ].some(token => code.includes(token) || label.includes(token));

    if (isCountKpi) {
      return this.formatCount(this.numeric(item.value));
    }

    return item.displayValue || this.formatMoney(this.numeric(item.value));
  }

  private buildOverviewMetricTiles(overview: GeneralDashboardResponse, currentBalance: number, totalVolume: number): DashboardMetricTile[] {
    const kpi = (code: string): number => this.numeric(overview.kpis?.find(item => item.code === code)?.value);
    const risk = (label: string): number => Number(overview.riskSnapshot?.find(item => item.label === label)?.value || 0);
    const netProfit = kpi('NET_PROFIT');
    const financing = kpi('FINANCING_OUTSTANDING');
    const transactions = kpi('TRANSACTION_VOLUME');
    const customers = kpi('TOTAL_CUSTOMERS');

    return [
      {
        label: 'Deposit Utilization',
        value: currentBalance > 0 ? `${Math.min(100, (financing / currentBalance) * 100).toFixed(1)}%` : '0.0%',
        tone: 'blue',
        sparkline: this.normalizeSparkline(this.trendBuckets.map(item => item.balance))
      },
      {
        label: 'Customer Activation',
        value: customers > 0 ? `${Math.min(100, ((customers - risk('KYC Attention')) / customers) * 100).toFixed(1)}%` : '0.0%',
        tone: 'green',
        sparkline: this.sparklineSeed(customers, risk('KYC Attention'), totalVolume)
      },
      {
        label: 'Net Cash Movement',
        value: this.formatMoney(this.totalTrendNet),
        tone: this.totalTrendNet >= 0 ? 'teal' : 'red',
        sparkline: this.normalizeSparkline(this.trendBuckets.map(item => Math.max(0, item.net)))
      },
      {
        label: 'Profit Yield',
        value: financing > 0 ? `${((netProfit / financing) * 100).toFixed(1)}%` : this.formatMoney(netProfit),
        tone: 'amber',
        sparkline: this.normalizeSparkline(this.trendBuckets.map(item => item.profit))
      },
      {
        label: 'Pending Control',
        value: String(risk('Pending Approvals')),
        tone: risk('Pending Approvals') > 0 ? 'purple' : 'green',
        sparkline: this.sparklineSeed(risk('Pending Approvals'), transactions, customers)
      },
      {
        label: 'Verification Risk',
        value: String(risk('KYC Attention') + risk('Security Cases') + risk('Reversal Queue')),
        tone: risk('Security Cases') > 0 ? 'red' : 'teal',
        sparkline: this.sparklineSeed(risk('KYC Attention'), risk('Security Cases'), risk('Reversal Queue'))
      }
    ];
  }

  private toDashboardTone(value?: string | null): 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple' {
    return ['teal', 'green', 'blue', 'amber', 'red', 'purple'].includes(String(value))
      ? value as 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple'
      : 'teal';
  }

  private toneColor(tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple'): string {
    const colors = {
      teal: '#0f766e',
      green: '#16a34a',
      blue: '#2563eb',
      amber: '#f59e0b',
      red: '#dc2626',
      purple: '#7c3aed'
    };
    return colors[tone] || colors.teal;
  }

  private numeric(value: unknown): number {
    const parsed = Number(value || 0);
    return Number.isFinite(parsed) ? parsed : 0;
  }

  private toDate(value?: string | null): Date {
    if (!value) {
      return new Date(0);
    }
    const parsed = new Date(value);
    return Number.isNaN(parsed.getTime()) ? new Date(0) : parsed;
  }

  private toDateInput(value: Date): string {
    return value.toISOString().slice(0, 10);
  }

  private endOfDay(value: Date): Date {
    const next = new Date(value);
    next.setHours(23, 59, 59, 999);
    return next;
  }

  private getScopedBranchId(): number | null {
    if (this.branchSelectionLocked) {
      return this.accessControl.session?.branchId || null;
    }
    return this.selectedBranchId;
  }

  private matchesBranch(entityBranchId: number | null | undefined, activeBranchId: number | null): boolean {
    if (!activeBranchId) {
      return true;
    }
    return Number(entityBranchId || 0) === Number(activeBranchId);
  }

  private prepareBranches(items: BranchResponse[]): BranchResponse[] {
    const unique = new Map<number, BranchResponse>();
    items.forEach(item => {
      if (item?.id != null && !unique.has(item.id)) {
        unique.set(item.id, item);
      }
    });

    if (this.accessControl.session?.branchId && !unique.has(this.accessControl.session.branchId)) {
      unique.set(this.accessControl.session.branchId, {
        id: this.accessControl.session.branchId,
        branchCode: this.accessControl.session.branchName?.split(' ')[0] || `BR-${this.accessControl.session.branchId}`,
        branchName: this.accessControl.session.branchName || `Branch ${this.accessControl.session.branchId}`,
        branchType: '',
        routingNo: '',
        addressLine1: '',
        status: 'ACTIVE'
      });
    }

    return Array.from(unique.values()).sort((a, b) => a.branchName.localeCompare(b.branchName));
  }

  private initializeBranchSelection(): void {
    if (this.branchSelectionLocked) {
      this.selectedBranchId = this.accessControl.session?.branchId || null;
      return;
    }
    if (this.selectedBranchId == null) {
      this.selectedBranchId = null;
    }
  }

  private readable(value?: string | null): string {
    return String(value || '').replace(/_/g, ' ');
  }
}
