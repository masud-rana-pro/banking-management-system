import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import {
  CalculationDashboardSummaryResponse,
  CalculationMetricResponse,
  CalculationRecentItemResponse,
  formatEnumLabel
} from '../../models/calculation.model';
import { CalculationService } from '../../services/calculation.service';

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
  selector: 'app-calculation-dashboard',
  templateUrl: './calculation-dashboard.component.html',
  styleUrls: ['./calculation-dashboard.component.scss']
})
export class CalculationDashboardComponent implements OnInit {

  loading = false;
  summary: CalculationDashboardSummaryResponse | null = null;
  calculationLegend: DashboardLegendItem[] = [];
  calculationBands: DashboardBandItem[] = [];
  driverColumns: DashboardColumnItem[] = [];
  sourceColumns: DashboardColumnItem[] = [];
  driverAxisTicks: DashboardAxisTick[] = [];
  sourceAxisTicks: DashboardAxisTick[] = [];
  calculationRows: Array<{ label: string; value: string }> = [];
  activityTrend: DashboardTrendPoint[] = [];
  activityTrendTicks: DashboardAxisTick[] = [];
  activityTrendPath = '';
  activityTrendAreaPath = '';
  matrixColumns: DashboardHeatmapColumn[] = [];
  matrixRows: DashboardHeatmapRow[] = [];
  selectedRecentItem: CalculationRecentItemResponse | null = null;
  calculationGradient = 'conic-gradient(#14b8a6 0deg 360deg)';

  constructor(
    private calculationService: CalculationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.calculationService.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load calculation dashboard.', 'error');
      }
    });
  }

  openSimulator(): void {
    this.router.navigate(['/calculations/simulator']);
  }

  openFinancing(): void {
    this.router.navigate(['/financing/products']);
  }

  openDepositSchemes(): void {
    this.router.navigate(['/deposit-schemes/list']);
  }

  openProfitSchedules(): void {
    this.router.navigate(['/profit/schedules']);
  }

  openItem(item: CalculationRecentItemResponse): void {
    if (!item.routeHint) {
      return;
    }
    this.router.navigateByUrl(item.routeHint);
  }

  focusItem(item: CalculationRecentItemResponse): void {
    this.selectedRecentItem = item;
  }

  print(): void {
    window.print();
  }

  get metrics(): CalculationMetricResponse[] {
    return this.summary?.productWiseSimulationCounts || [];
  }

  get maxMetric(): number {
    const counts = this.metrics.map(item => item.usageCount || 0);
    return counts.length ? Math.max(...counts, 1) : 1;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  get selectedSourceBadges(): Array<{ label: string; value: string }> {
    if (!this.selectedRecentItem) {
      return [];
    }
    return [
      { label: 'Source Module', value: this.getLabel(this.selectedRecentItem.sourceModule) },
      { label: 'Product Type', value: this.getLabel(this.selectedRecentItem.productType) },
      { label: 'Status', value: this.getLabel(this.selectedRecentItem.status) },
      { label: 'Event Date', value: this.selectedRecentItem.eventDate || 'N/A' }
    ];
  }

  get selectedSourceBadgeText(): string {
    const label = this.getLabel(this.selectedRecentItem?.sourceModule);
    const compact = label
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(part => part.charAt(0))
      .join('');
    return compact || 'CE';
  }

  private prepareDashboard(summary: CalculationDashboardSummaryResponse): void {
    const denominator = Math.max(summary.recentCalculations + summary.failedCalculations + summary.financingSimulationCount + summary.depositSimulationCount + summary.profitSimulationCount, 1);
    this.calculationLegend = [
      { label: 'Recent Calculations', value: summary.recentCalculations, note: 'recent simulation executions observed', color: '#22c55e' },
      { label: 'Failed Calculations', value: summary.failedCalculations, note: 'calculation or preview failures requiring attention', color: '#ef4444' },
      { label: 'Financing Drivers', value: summary.financingSimulationCount, note: 'financing records feeding the engine', color: '#3b82f6' },
      { label: 'Deposit Drivers', value: summary.depositSimulationCount, note: 'deposit scheme records using projection logic', color: '#f59e0b' }
    ];
    this.calculationGradient = this.buildDonutGradient(this.calculationLegend);

    this.calculationBands = [
      {
        label: 'Financing Load',
        value: summary.financingSimulationCount,
        note: 'simulation pressure coming from financing flows',
        share: this.getShare(summary.financingSimulationCount, denominator),
        tone: 'primary'
      },
      {
        label: 'Deposit Load',
        value: summary.depositSimulationCount,
        note: 'scheme calculation demand currently present',
        share: this.getShare(summary.depositSimulationCount, denominator),
        tone: 'warning'
      },
      {
        label: 'Profit Load',
        value: summary.profitSimulationCount,
        note: 'profit schedule support generated by the engine',
        share: this.getShare(summary.profitSimulationCount, denominator),
        tone: 'info'
      },
      {
        label: 'Failure Strain',
        value: summary.failedCalculations,
        note: `${summary.recentItems.length} recent source records visible for traceability`,
        share: this.getShare(summary.failedCalculations, denominator),
        tone: 'danger'
      }
    ];

    const maxDriver = Math.max(...summary.productWiseSimulationCounts.map(item => item.usageCount || 0), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    this.driverColumns = summary.productWiseSimulationCounts.slice(0, 6).map((item, index) => ({
      label: this.getLabel(item.productType),
      shortLabel: this.toChartLabel(this.getLabel(item.productType)),
      value: item.usageCount || 0,
      height: this.getShare(item.usageCount || 0, maxDriver),
      tone: tones[index % tones.length]
    }));
    this.driverAxisTicks = this.buildAxisTicks(maxDriver);

    const sourceMap = new Map<string, number>();
    summary.recentItems.forEach(item => {
      const key = this.getLabel(item.sourceModule);
      sourceMap.set(key, (sourceMap.get(key) || 0) + 1);
    });
    const sourceEntries = Array.from(sourceMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxSource = Math.max(...sourceEntries.map(([, value]) => value), 1);
    this.sourceColumns = sourceEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxSource),
      tone: tones[index % tones.length]
    }));
    this.sourceAxisTicks = this.buildAxisTicks(maxSource);

    const trendMap = new Map<string, number>();
    summary.recentItems.forEach(item => {
      const dayKey = item.eventDate || 'Unknown';
      trendMap.set(dayKey, (trendMap.get(dayKey) || 0) + 1);
    });
    const trendEntries = Array.from(trendMap.entries())
      .sort((a, b) => a[0].localeCompare(b[0]))
      .slice(-7);
    const maxTrend = Math.max(...trendEntries.map(([, value]) => value), 1);
    this.activityTrend = trendEntries.map(([label, value], index, items) => ({
      label,
      shortLabel: this.toDayLabel(label),
      value,
      x: items.length === 1 ? 50 : (index / (items.length - 1)) * 100,
      y: 100 - this.getChartHeight(value, maxTrend)
    }));
    this.activityTrendTicks = this.buildAxisTicks(maxTrend);
    this.activityTrendPath = this.activityTrend
      .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
      .join(' ');
    this.activityTrendAreaPath = this.activityTrend.length
      ? `${this.activityTrendPath} L ${this.activityTrend[this.activityTrend.length - 1].x} 100 L ${this.activityTrend[0].x} 100 Z`
      : '';

    const statusEntries = Array.from(
      summary.recentItems.reduce((map, item) => {
        const key = this.getLabel(item.status);
        map.set(key, (map.get(key) || 0) + 1);
        return map;
      }, new Map<string, number>())
    )
      .sort((a, b) => b[1] - a[1])
      .slice(0, 4);
    this.matrixColumns = statusEntries.map(([label]) => ({ key: label, label }));

    this.matrixRows = sourceEntries.slice(0, 4).map(([label]) => ({
      label,
      shortLabel: this.toChartLabel(label),
      values: this.matrixColumns.map(column =>
        summary.recentItems.filter(item =>
          this.getLabel(item.sourceModule) === label && this.getLabel(item.status) === column.key
        ).length
      )
    }));

    this.selectedRecentItem = [...summary.recentItems]
      .sort((a, b) => (b.amount || 0) - (a.amount || 0))[0] || summary.recentItems[0] || null;

    this.calculationRows = [
      { label: 'Recent Calculations', value: `${summary.recentCalculations}` },
      { label: 'Failed Calculations', value: `${summary.failedCalculations}` },
      { label: 'Metric Rows', value: `${summary.productWiseSimulationCounts.length}` },
      { label: 'Recent Source Items', value: `${summary.recentItems.length}` }
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

  private toChartLabel(value: string): string {
    const compact = value.replace(/\s+/g, ' ').trim();
    if (!compact) {
      return 'Type';
    }
    const words = compact.split(' ');
    const readable = words.slice(0, 2).join(' ');
    return readable.length <= 14 ? readable : readable.slice(0, 14).trim();
  }

  private toDayLabel(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return this.toChartLabel(value);
    }
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }
}
