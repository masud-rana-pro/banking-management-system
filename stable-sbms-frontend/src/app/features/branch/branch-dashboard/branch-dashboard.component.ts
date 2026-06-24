import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import Swal from 'sweetalert2';
import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';

import { BranchApiService } from '../services/branch-api.service';
import { BranchAssignmentApiService } from '../services/branch-assignment-api.service';
import { TellerLimitApiService } from '../services/teller-limit-api.service';
import { VaultBalanceApiService } from '../services/vault-balance-api.service';
import { BranchResponse, BranchDashboardSummaryResponse } from '../models/branch.model';
import { BranchAssignmentResponse } from '../models/branch-assignment.model';
import { TellerLimitResponse } from '../models/teller-limit.model';
import { VaultBalanceResponse } from '../models/vault-balance.model';

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
  branchId?: number;
  note?: string;
}

interface DashboardAxisTick {
  label: string;
  bottom: number;
}

interface DashboardBandItem {
  label: string;
  value: number;
  note: string;
  share: number;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

@Component({
  selector: 'app-branch-dashboard',
  templateUrl: './branch-dashboard.component.html',
  styleUrls: ['./branch-dashboard.component.scss']
})
export class BranchDashboardComponent implements OnInit {

  loading = false;
  summary: BranchDashboardSummaryResponse | null = null;
  branches: BranchResponse[] = [];
  assignments: BranchAssignmentResponse[] = [];
  tellerLimits: TellerLimitResponse[] = [];
  vaults: VaultBalanceResponse[] = [];
  operationsLegend: DashboardLegendItem[] = [];
  branchTypeColumns: DashboardColumnItem[] = [];
  vaultExposureColumns: DashboardColumnItem[] = [];
  branchTypeAxisTicks: DashboardAxisTick[] = [];
  vaultAxisTicks: DashboardAxisTick[] = [];
  assignmentBands: DashboardBandItem[] = [];
  branchOpsGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  userImageMap: Record<string, string> = {};
  userDisplayMap: Record<number, string> = {};
  selectedLegendLabel = '';
  selectedBranchTypeLabel = '';
  selectedVaultBranchId: number | null = null;
  selectedBranchCardId: number | null = null;

  constructor(
    private branchApi: BranchApiService,
    private assignmentApi: BranchAssignmentApiService,
    private tellerLimitApi: TellerLimitApiService,
    private vaultApi: VaultBalanceApiService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.load();
  }

  load(): void {
    this.loading = true;
    forkJoin({
      summary: this.branchApi.getDashboardSummary(),
      branches: this.branchApi.getAll('', ''),
      assignments: this.assignmentApi.getAll(null, '').pipe(catchError(() => of([]))),
      tellerLimits: this.tellerLimitApi.getAll('').pipe(catchError(() => of([]))),
      vaults: this.vaultApi.getAll(null, '', null).pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ summary, branches, assignments, tellerLimits, vaults }) => {
        this.summary = summary;
        this.branches = branches || [];
        this.assignments = assignments || [];
        this.tellerLimits = tellerLimits || [];
        this.vaults = vaults || [];
        this.prepareDashboard();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load branch dashboard.', 'error');
      }
    });
  }

  openList(): void {
    this.router.navigate(['/branches/list']);
  }

  openAssignments(): void {
    this.router.navigate(['/branches/assignments']);
  }

  openTellerLimits(): void {
    this.router.navigate(['/branches/teller-limits']);
  }

  goVaults(): void {
    this.router.navigate(['/branches/vault']);
  }

  openLedger(): void {
    this.router.navigate(['/branches/cash-ledger']);
  }

  openEodSummary(): void {
    this.router.navigate(['/branches/eod-summary']);
  }

  get recentBranches(): BranchResponse[] {
    return [...this.branches]
      .sort((a, b) => String(b.createdAt || '').localeCompare(String(a.createdAt || '')))
      .slice(0, 6);
  }

  get pendingAssignments(): BranchAssignmentResponse[] {
    return this.assignments.filter(item => item.status === 'INACTIVE').slice(0, 6);
  }

  get openVaultRows(): VaultBalanceResponse[] {
    return this.vaults.filter(item => !item.isClosed).slice(0, 6);
  }

  get tellerAlerts(): TellerLimitResponse[] {
    const today = new Date().toISOString().slice(0, 10);
    return this.tellerLimits
      .filter(item => item.status === 'ACTIVE' && item.limitDate <= today)
      .slice(0, 6);
  }

  get activeLegendItem(): DashboardLegendItem | null {
    return this.operationsLegend.find(item => item.label === this.selectedLegendLabel) || this.operationsLegend[0] || null;
  }

  get activeBranchTypeItem(): DashboardColumnItem | null {
    return this.branchTypeColumns.find(item => item.label === this.selectedBranchTypeLabel) || this.branchTypeColumns[0] || null;
  }

  get activeVaultExposureItem(): DashboardColumnItem | null {
    return this.vaultExposureColumns.find(item => item.branchId === this.selectedVaultBranchId) || this.vaultExposureColumns[0] || null;
  }

  get selectedBranchCard(): BranchResponse | null {
    return this.branches.find(item => item.id === this.selectedBranchCardId) || this.recentBranches[0] || null;
  }

  getBranchName(branchId: number): string {
    return this.branches.find(item => item.id === branchId)?.branchName || `Branch #${branchId}`;
  }

  getBranchCode(branchId: number): string {
    return this.branches.find(item => item.id === branchId)?.branchCode || `BR-${branchId}`;
  }

  getUserImageUrl(userId?: number | null): string {
    if (!userId) return '';
    return this.userImageMap[String(userId)] || '';
  }

  getUserDisplay(userId?: number | null): string {
    if (!userId) return '-';
    return this.userDisplayMap[userId] || `USER-${userId}`;
  }

  getBranchManagerImage(branch?: BranchResponse | null): string {
    return branch?.managerUserId ? this.getUserImageUrl(branch.managerUserId) : '';
  }

  getBranchManagerDisplay(branch?: BranchResponse | null): string {
    return branch?.managerUserId ? this.getUserDisplay(branch.managerUserId) : 'Manager not assigned';
  }

  getBranchAssignmentCount(branchId?: number | null): number {
    if (!branchId) return 0;
    return this.assignments.filter(item => item.branchId === branchId).length;
  }

  getBranchAlertCount(branchId?: number | null): number {
    if (!branchId) return 0;
    return this.tellerLimits.filter(item => item.branchId === branchId && item.status === 'ACTIVE').length;
  }

  getBranchOpenVaultCount(branchId?: number | null): number {
    if (!branchId) return 0;
    return this.vaults.filter(item => item.branchId === branchId && !item.isClosed).length;
  }

  getBranchCashExposure(branchId?: number | null): number {
    if (!branchId) return 0;
    return this.vaults
      .filter(item => item.branchId === branchId)
      .reduce((sum, item) => sum + Number(item.closingBalance || item.openingBalance || 0), 0);
  }

  selectLegend(item: DashboardLegendItem): void {
    this.selectedLegendLabel = item.label;
  }

  selectBranchType(item: DashboardColumnItem): void {
    this.selectedBranchTypeLabel = item.label;
    const matchingBranch = this.branches.find(branch => (branch.branchType || 'Unknown') === item.label);
    if (matchingBranch) {
      this.selectedBranchCardId = matchingBranch.id;
    }
  }

  selectVaultExposure(item: DashboardColumnItem): void {
    this.selectedVaultBranchId = item.branchId ?? null;
    if (item.branchId) {
      this.selectedBranchCardId = item.branchId;
    }
  }

  selectBranchCard(branch: BranchResponse): void {
    this.selectedBranchCardId = branch.id;
    this.selectedVaultBranchId = branch.id;
    if (branch.branchType) {
      this.selectedBranchTypeLabel = branch.branchType;
    }
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private prepareDashboard(): void {
    if (!this.summary) {
      return;
    }

    this.operationsLegend = [
      { label: 'Active Branches', value: this.summary.activeBranches, note: 'branches currently in live service', color: '#22c55e' },
      { label: 'Pending Assignments', value: this.summary.pendingAssignments, note: 'ownership or role setup still pending', color: '#f59e0b' },
      { label: 'Teller Alerts', value: this.summary.tellerLimitAlerts, note: 'teller limits reaching alert state', color: '#3b82f6' },
      { label: 'Pending Vault Close', value: this.summary.todayVaultPendingClose, note: 'vault rows still open for the day', color: '#ef4444' }
    ];
    this.branchOpsGradient = this.buildDonutGradient(this.operationsLegend);

    const typeMap = new Map<string, number>();
    this.branches.forEach(item => {
      const key = item.branchType || 'Unknown';
      typeMap.set(key, (typeMap.get(key) || 0) + 1);
    });
    const typeStats = Array.from(typeMap.entries())
      .map(([label, value]) => ({ label, value }))
      .sort((a, b) => b.value - a.value);
    const typeMax = Math.max(...typeStats.map(item => item.value), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    this.branchTypeColumns = typeStats.map((item, index) => ({
      label: item.label,
      shortLabel: this.toChartLabel(item.label, 12),
      value: item.value,
      height: this.getShare(item.value, typeMax),
      tone: tones[index % tones.length]
    }));
    this.branchTypeAxisTicks = this.buildAxisTicks(typeMax);

    const vaultMap = new Map<number, number>();
    this.vaults.forEach(item => {
      vaultMap.set(item.branchId, (vaultMap.get(item.branchId) || 0) + Number(item.closingBalance || item.openingBalance || 0));
    });
    const vaultStats = Array.from(vaultMap.entries())
      .map(([branchId, value]) => ({ label: this.getBranchName(branchId), value }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 6);
    const vaultMax = Math.max(...vaultStats.map(item => item.value), 1);
    this.vaultExposureColumns = vaultStats.map((item, index) => ({
      label: item.label,
      shortLabel: this.toChartLabel(item.label, 14),
      value: item.value,
      height: this.getShare(item.value, vaultMax),
      tone: tones[index % tones.length],
      branchId: this.branches.find(branch => branch.branchName === item.label)?.id,
      note: 'current balance footprint from vault records'
    }));
    this.vaultAxisTicks = this.buildCompactAxisTicks(vaultMax);

    this.assignmentBands = [
      {
        label: 'Branch Footprint',
        value: this.summary.totalBranches,
        note: `${this.summary.activeBranches} of them are active right now`,
        share: this.getShare(this.summary.activeBranches, Math.max(this.summary.totalBranches, 1)),
        tone: 'success'
      },
      {
        label: 'Assignment Queue',
        value: this.summary.pendingAssignments,
        note: `${this.pendingAssignments.length} latest ownership signals shown below`,
        share: this.getShare(this.summary.pendingAssignments, Math.max(this.summary.totalBranches, 1)),
        tone: 'warning'
      },
      {
        label: 'Teller Alert Pressure',
        value: this.summary.tellerLimitAlerts,
        note: `${this.tellerAlerts.length} active alert rows are visible today`,
        share: this.getShare(this.summary.tellerLimitAlerts, Math.max(this.tellerLimits.length, 1)),
        tone: 'info'
      },
      {
        label: 'Open Vault Watch',
        value: this.openVaultRows.length,
        note: `${this.summary.todayVaultPendingClose} pending close signals still remain`,
        share: this.getShare(this.openVaultRows.length, Math.max(this.vaults.length, 1)),
        tone: 'danger'
      }
    ];

    this.syncSelections();
  }

  private getShare(value: number, total: number): number {
    if (!total) return 0;
    return Math.max(6, Math.round((value / total) * 100));
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

  private buildAxisTicks(maxValue: number): DashboardAxisTick[] {
    const safeMax = Math.max(1, Math.ceil(maxValue));
    return [0, 25, 50, 75, 100].map(percent => ({
      label: `${Math.round((safeMax * percent) / 100)}`,
      bottom: percent
    }));
  }

  private buildCompactAxisTicks(maxValue: number): DashboardAxisTick[] {
    const safeMax = Math.max(1, maxValue);
    return [0, 25, 50, 75, 100].map(percent => ({
      label: Intl.NumberFormat('en', {
        notation: 'compact',
        maximumFractionDigits: safeMax >= 1000 ? 1 : 0
      }).format((safeMax * percent) / 100),
      bottom: percent
    }));
  }

  private toChartLabel(value: string, limit = 14): string {
    const compact = String(value || '').replace(/\s+/g, ' ').trim();
    if (!compact) return 'N/A';
    const words = compact.split(' ');
    const readable = words.slice(0, 2).join(' ');
    return readable.length <= limit ? readable : readable.slice(0, limit).trim();
  }

  private syncSelections(): void {
    this.selectedLegendLabel = this.activeLegendItem?.label || this.operationsLegend[0]?.label || '';
    this.selectedBranchTypeLabel = this.activeBranchTypeItem?.label || this.branchTypeColumns[0]?.label || '';
    this.selectedVaultBranchId = this.activeVaultExposureItem?.branchId ?? this.vaultExposureColumns[0]?.branchId ?? null;
    this.selectedBranchCardId = this.selectedBranchCardId
      || this.activeVaultExposureItem?.branchId
      || this.recentBranches[0]?.id
      || null;
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

  private buildUserImageMap(users: UserResponse[]): Record<string, string> {
    return users.reduce<Record<string, string>>((acc, user) => {
      if (user.id && user.profileImageName) {
        acc[String(user.id)] = this.fileUploadService.resolveImageUrl(user.profileImageName);
      }
      if (user.id) {
        this.userDisplayMap[user.id] = user.fullName || user.username;
      }
      return acc;
    }, {});
  }
}
