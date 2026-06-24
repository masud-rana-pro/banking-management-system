import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { AtmTerminalService } from '../../services/atm-terminal.service';
import {
  AtmDashboardSummaryResponse,
  CashBinResponse,
  DeviceJournalResponse,
  ReconciliationResponse,
  ReplenishmentResponse,
  TerminalResponse
} from '../../models/terminal.model';

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
  selector: 'app-terminal-dashboard',
  templateUrl: './terminal-dashboard.component.html',
  styleUrls: ['./terminal-dashboard.component.scss']
})
export class TerminalDashboardComponent implements OnInit {

  loading = false;
  summary: AtmDashboardSummaryResponse = {
    totalTerminals: 0,
    activeTerminals: 0,
    lowCashAlerts: 0,
    unreconciledTerminals: 0,
    downtimeTerminals: 0,
    todayVolumeCount: 0,
    todayVolumeAmount: 0
  };

  recentJournal: DeviceJournalResponse[] = [];
  recentReplenishments: ReplenishmentResponse[] = [];
  lowCashBins: CashBinResponse[] = [];
  downtimeList: TerminalResponse[] = [];
  varianceList: ReconciliationResponse[] = [];
  terminalHealthLegend: DashboardLegendItem[] = [];
  terminalTypeColumns: DashboardColumnItem[] = [];
  journalEventColumns: DashboardColumnItem[] = [];
  terminalTypeAxisTicks: DashboardAxisTick[] = [];
  eventAxisTicks: DashboardAxisTick[] = [];
  atmHealthGradient = 'conic-gradient(#14b8a6 0deg 360deg)';

  constructor(
    private atmApi: AtmTerminalService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;

    forkJoin({
      summary: this.atmApi.getDashboardSummary().pipe(catchError(() => of(this.summary))),
      terminals: this.atmApi.getAll().pipe(catchError(() => of([]))),
      cashBins: this.atmApi.getCashBins().pipe(catchError(() => of([]))),
      replenishments: this.atmApi.getReplenishments().pipe(catchError(() => of([]))),
      reconciliations: this.atmApi.getReconciliations().pipe(catchError(() => of([]))),
      journal: this.atmApi.getDeviceJournal().pipe(catchError(() => of([])))
    }).subscribe(result => {
      this.summary = result.summary;
      this.recentJournal = result.journal.slice(0, 6);
      this.recentReplenishments = result.replenishments.slice(0, 5);
      this.lowCashBins = result.cashBins.filter(item => item.status === 'LOW_CASH').slice(0, 5);
      this.downtimeList = result.terminals
        .filter(item => item.status === 'MAINTENANCE' || item.status === 'OUT_OF_SERVICE')
        .slice(0, 5);
      this.varianceList = result.reconciliations
        .filter(item => Number(item.varianceAmount || 0) !== 0)
        .slice(0, 5);
      this.prepareDashboard(result.terminals || [], result.journal || []);
      this.loading = false;
    });
  }

  openTerminal(id: number): void {
    this.router.navigate(['/atm/terminals', id]);
  }

  openReconciliation(id: number): void {
    this.router.navigate(['/atm/reconciliation', id]);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private prepareDashboard(terminals: TerminalResponse[], journal: DeviceJournalResponse[]): void {
    this.terminalHealthLegend = [
      { label: 'Active', value: this.summary.activeTerminals, note: 'terminals currently in service', color: '#22c55e' },
      { label: 'Low Cash', value: this.summary.lowCashAlerts, note: 'cash bins with low remaining funds', color: '#f59e0b' },
      { label: 'Unreconciled', value: this.summary.unreconciledTerminals, note: 'variance or pending reconciliation pressure', color: '#ef4444' },
      { label: 'Downtime', value: this.summary.downtimeTerminals, note: 'maintenance or out-of-service endpoints', color: '#3b82f6' }
    ];
    this.atmHealthGradient = this.buildDonutGradient(this.terminalHealthLegend);

    const typeMap = new Map<string, number>();
    terminals.forEach(item => {
      const key = item.terminalType || 'UNKNOWN';
      typeMap.set(key, (typeMap.get(key) || 0) + 1);
    });
    const typeStats = Array.from(typeMap.entries())
      .map(([label, value]) => ({ label, value }))
      .sort((a, b) => b.value - a.value);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    const typeMax = Math.max(...typeStats.map(item => item.value), 1);
    this.terminalTypeColumns = typeStats.map((item, index) => ({
      label: item.label === 'ATM_CDM' ? 'ATM / CDM' : item.label,
      shortLabel: item.label === 'ATM_CDM' ? 'ATM/CDM' : item.label,
      value: item.value,
      height: this.getShare(item.value, typeMax),
      tone: tones[index % tones.length]
    }));
    this.terminalTypeAxisTicks = this.buildAxisTicks(typeMax);

    const eventMap = new Map<string, number>();
    journal.forEach(item => {
      const key = item.eventType || 'UNKNOWN';
      eventMap.set(key, (eventMap.get(key) || 0) + 1);
    });
    const eventStats = Array.from(eventMap.entries())
      .map(([label, value]) => ({ label, value }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 6);
    const eventMax = Math.max(...eventStats.map(item => item.value), 1);
    this.journalEventColumns = eventStats.map((item, index) => ({
      label: this.toReadableLabel(item.label),
      shortLabel: this.toChartLabel(this.toReadableLabel(item.label), 12),
      value: item.value,
      height: this.getShare(item.value, eventMax),
      tone: tones[index % tones.length]
    }));
    this.eventAxisTicks = this.buildAxisTicks(eventMax);
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

  private toReadableLabel(value: string): string {
    return String(value || '').replace(/_/g, ' ').trim();
  }

  private toChartLabel(value: string, limit = 14): string {
    const compact = value.replace(/\s+/g, ' ').trim();
    if (!compact) return 'N/A';
    return compact.length <= limit ? compact : compact.slice(0, limit).trim();
  }
}
