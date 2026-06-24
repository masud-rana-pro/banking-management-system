import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { StandingInstructionResponse, TransactionDashboardSummaryResponse, TransactionResponse, formatEnumLabel } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';

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

@Component({
  selector: 'app-transaction-dashboard',
  templateUrl: './transaction-dashboard.component.html',
  styleUrls: ['./transaction-dashboard.component.scss']
})
export class TransactionDashboardComponent implements OnInit {

  loading = false;
  summary: TransactionDashboardSummaryResponse = {
    todayDepositTotal: 0,
    todayWithdrawalTotal: 0,
    todayTransferTotal: 0,
    pendingReversals: 0,
    tellerLimitUsed: 0,
    tellerLimit: 0,
    tellerLimitUsagePercent: 0,
    suspiciousTransactionCount: 0,
    topBranches: []
  };

  transactions: TransactionResponse[] = [];
  standingInstructions: StandingInstructionResponse[] = [];
  branches: BranchResponse[] = [];

  pendingReversalItems: TransactionResponse[] = [];
  suspiciousItems: TransactionResponse[] = [];
  recentTransactions: TransactionResponse[] = [];
  netFlow = 0;
  flowBands: DashboardBandItem[] = [];
  branchBars: DashboardBarItem[] = [];
  instructionBars: DashboardBarItem[] = [];
  recentAmountBars: DashboardBarItem[] = [];
  signalRows: Array<{ label: string; value: string }> = [];
  flowLegend: DashboardLegendItem[] = [];
  branchColumns: DashboardColumnItem[] = [];
  branchAxisTicks: DashboardAxisTick[] = [];
  transactionFlowGradient = 'conic-gradient(#14b8a6 0deg 360deg)';

  constructor(
    private transactionApi: TransactionService,
    private branchApi: BranchApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      summary: this.transactionApi.getDashboardSummary(),
      transactions: this.transactionApi.getTransactions(),
      standingInstructions: this.transactionApi.getStandingInstructions(),
      branches: this.branchApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ summary, transactions, standingInstructions, branches }) => {
        this.summary = summary;
        this.transactions = transactions || [];
        this.standingInstructions = standingInstructions || [];
        this.branches = branches || [];
        this.prepareData();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load transaction dashboard.', 'error');
      }
    });
  }

  openTransaction(id: number): void {
    this.router.navigate(['/transactions', id]);
  }

  openReverse(id: number): void {
    this.router.navigate(['/transactions', id, 'reverse']);
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

  private prepareData(): void {
    this.pendingReversalItems = this.transactions
      .filter(item => item.reversalRequestStatus === 'PENDING' || item.transactionStatus === 'REVERSED')
      .slice(0, 6);

    this.suspiciousItems = this.transactions
      .filter(item => Number(item.amount || 0) >= 100000)
      .slice(0, 6);

    this.recentTransactions = [...this.transactions]
      .sort((a, b) => new Date(b.transactionDate || b.createdAt || '').getTime() - new Date(a.transactionDate || a.createdAt || '').getTime())
      .slice(0, 8);

    this.netFlow = Number(this.summary.todayDepositTotal || 0) - Number(this.summary.todayWithdrawalTotal || 0);

    const totalVolume = Number(this.summary.todayDepositTotal || 0)
      + Number(this.summary.todayWithdrawalTotal || 0)
      + Number(this.summary.todayTransferTotal || 0);

    this.flowBands = [
      {
        label: 'Deposit Flow',
        value: Number(this.summary.todayDepositTotal || 0),
        note: 'Counter and credit-side inflow captured today',
        share: this.getShare(Number(this.summary.todayDepositTotal || 0), totalVolume || 1),
        tone: 'success'
      },
      {
        label: 'Withdrawal Flow',
        value: Number(this.summary.todayWithdrawalTotal || 0),
        note: 'Cash-out demand currently consuming teller capacity',
        share: this.getShare(Number(this.summary.todayWithdrawalTotal || 0), totalVolume || 1),
        tone: 'warning'
      },
      {
        label: 'Transfer Flow',
        value: Number(this.summary.todayTransferTotal || 0),
        note: 'Internal and scheduled transfer value posted today',
        share: this.getShare(Number(this.summary.todayTransferTotal || 0), totalVolume || 1),
        tone: 'primary'
      },
      {
        label: 'Teller Utilization',
        value: Number(this.summary.tellerLimitUsagePercent || 0),
        note: `${this.summary.tellerLimitUsed || 0} used of ${this.summary.tellerLimit || 0} approved teller limit`,
        share: Math.max(8, Math.round(Number(this.summary.tellerLimitUsagePercent || 0))),
        tone: Number(this.summary.tellerLimitUsagePercent || 0) >= 85 ? 'danger' : 'info'
      }
    ];
    this.flowLegend = [
      { label: 'Deposit', value: Number(this.summary.todayDepositTotal || 0), note: 'cash and credit inflow', color: '#22c55e' },
      { label: 'Withdrawal', value: Number(this.summary.todayWithdrawalTotal || 0), note: 'cash-out movement', color: '#f59e0b' },
      { label: 'Transfer', value: Number(this.summary.todayTransferTotal || 0), note: 'inter-account movement', color: '#14b8a6' },
      { label: 'Reversal Queue', value: Number(this.summary.pendingReversals || 0), note: 'items under control review', color: '#ef4444' }
    ];
    this.transactionFlowGradient = this.buildDonutGradient(this.flowLegend);

    const branchMax = Math.max(...this.summary.topBranches.map(item => item.transactionCount), 1);
    const tones: Array<DashboardBarItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    this.branchBars = this.summary.topBranches.slice(0, 5).map((item, index) => ({
      label: this.getBranchName(item.branchId),
      value: item.transactionCount,
      note: `${this.getShare(item.transactionCount, this.transactions.length || 1)}% of current journal rows`,
      share: this.getShare(item.transactionCount, branchMax),
      tone: tones[index % tones.length]
    }));
    this.branchColumns = this.summary.topBranches.slice(0, 6).map((item, index) => ({
      label: this.getBranchName(item.branchId),
      shortLabel: this.buildShortLabel(this.getBranchName(item.branchId)),
      value: item.transactionCount,
      height: this.getShare(item.transactionCount, branchMax),
      tone: tones[index % tones.length]
    }));
    this.branchAxisTicks = this.buildAxisTicks(branchMax);

    const instructionMap = new Map<string, number>();
    this.standingInstructions.forEach(item => {
      const key = item.instructionStatus || 'UNKNOWN';
      instructionMap.set(key, (instructionMap.get(key) || 0) + 1);
    });
    const instructionStats = Array.from(instructionMap.entries())
      .map(([label, value]) => ({ label: this.getLabel(label), value }))
      .sort((a, b) => b.value - a.value);
    const instructionMax = Math.max(...instructionStats.map(item => item.value), 1);
    this.instructionBars = instructionStats.map((item, index) => ({
      label: item.label,
      value: item.value,
      note: `${this.getShare(item.value, this.standingInstructions.length || 1)}% of instruction pool`,
      share: this.getShare(item.value, instructionMax),
      tone: tones[index % tones.length]
    }));

    const topAmounts = [...this.recentTransactions]
      .sort((a, b) => Number(b.amount || 0) - Number(a.amount || 0))
      .slice(0, 5);
    const amountMax = Math.max(...topAmounts.map(item => Number(item.amount || 0)), 1);
    this.recentAmountBars = topAmounts.map((item, index) => ({
      label: item.transactionRef,
      value: Number(item.amount || 0),
      note: `${this.getLabel(item.transactionType)} | ${this.getBranchName(item.branchId)}`,
      share: this.getShare(Number(item.amount || 0), amountMax),
      tone: tones[index % tones.length]
    }));

    this.signalRows = [
      { label: 'Pending Reversal Requests', value: `${this.summary.pendingReversals}` },
      { label: 'Suspicious Transactions', value: `${this.summary.suspiciousTransactionCount}` },
      { label: 'Standing Instructions', value: `${this.standingInstructions.length}` },
      { label: 'Net Operating Flow', value: `${this.netFlow.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}` }
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
}
