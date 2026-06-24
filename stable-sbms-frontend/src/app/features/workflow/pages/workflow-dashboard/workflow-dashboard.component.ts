import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { WorkflowDashboardSummaryResponse, WorkflowHistoryResponse, formatEnumLabel, resolveWorkflowSourceRoute } from '../../models/workflow.model';
import { WorkflowService } from '../../services/workflow.service';

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
  selector: 'app-workflow-dashboard',
  templateUrl: './workflow-dashboard.component.html',
  styleUrls: ['./workflow-dashboard.component.scss']
})
export class WorkflowDashboardComponent implements OnInit {

  loading = false;
  actor = 'SYSTEM';
  summary: WorkflowDashboardSummaryResponse | null = null;
  workflowLegend: DashboardLegendItem[] = [];
  moduleColumns: DashboardColumnItem[] = [];
  moduleAxisTicks: DashboardAxisTick[] = [];
  actionColumns: DashboardColumnItem[] = [];
  actionAxisTicks: DashboardAxisTick[] = [];
  workflowGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  throughputPoints: DashboardTrendPoint[] = [];
  throughputPath = '';
  throughputAreaPath = '';
  workflowHeatRows: HeatmapRow[] = [];
  workflowStatusHeaders: string[] = [];
  spotlightItem: WorkflowHistoryResponse | null = null;

  constructor(
    private workflowService: WorkflowService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.workflowService.getDashboardSummary(this.actor).subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load workflow dashboard.', 'error');
      }
    });
  }

  print(): void {
    window.print();
  }

  open(route: string): void {
    this.router.navigate([route], { queryParams: route === '/workflow/my-submissions' ? { actor: this.actor } : {} });
  }

  openHistory(item: WorkflowHistoryResponse): void {
    this.router.navigate(['/workflow/history', item.id]);
  }

  openSource(item: WorkflowHistoryResponse): void {
    this.router.navigate([resolveWorkflowSourceRoute(item)]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  private prepareDashboard(data: WorkflowDashboardSummaryResponse): void {
    this.workflowLegend = [
      { label: 'Pending Approvals', value: data.pendingApprovals, note: 'queue items waiting for action', color: '#f59e0b' },
      { label: 'My Pending Tasks', value: data.myPendingTasks, note: 'tasks assigned to the current actor', color: '#3b82f6' },
      { label: 'Recent Completed', value: data.recentCompletedTasks, note: 'items recently closed successfully', color: '#22c55e' },
      { label: 'Bottlenecks', value: data.workflowBottlenecks, note: 'steps currently slowing the flow', color: '#ef4444' }
    ];
    this.workflowGradient = this.buildDonutGradient(this.workflowLegend);

    const moduleMap = new Map<string, number>();
    [...data.pendingQueue, ...data.recentHistory].forEach(item => {
      const key = item.moduleName || 'UNKNOWN';
      moduleMap.set(key, (moduleMap.get(key) || 0) + 1);
    });
    const moduleStats = Array.from(moduleMap.entries())
      .map(([label, value]) => ({ label, value }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 6);
    const maxValue = Math.max(...moduleStats.map(item => item.value), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    this.moduleColumns = moduleStats.map((item, index) => ({
      label: this.getLabel(item.label),
      shortLabel: this.toChartLabel(this.getLabel(item.label), 14),
      value: item.value,
      height: this.getShare(item.value, maxValue),
      tone: tones[index % tones.length]
    }));
    this.moduleAxisTicks = this.buildAxisTicks(maxValue);

    const actionMap = new Map<string, number>();
    data.recentHistory.forEach(item => {
      const key = this.getLabel(item.actionName || 'ACTION');
      actionMap.set(key, (actionMap.get(key) || 0) + 1);
    });
    const actionStats = Array.from(actionMap.entries())
      .map(([label, value]) => ({ label, value }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 6);
    const maxActionValue = Math.max(...actionStats.map(item => item.value), 1);
    this.actionColumns = actionStats.map((item, index) => ({
      label: item.label,
      shortLabel: this.toChartLabel(item.label, 16),
      value: item.value,
      height: this.getShare(item.value, maxActionValue),
      tone: tones[index % tones.length]
    }));
    this.actionAxisTicks = this.buildAxisTicks(maxActionValue);

    this.spotlightItem = data.pendingQueue[0] || data.recentHistory[0] || null;
    this.buildThroughputTrend(data.recentHistory);
    this.buildWorkflowHeatmap(data);
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

  toChartLabel(value: string, limit = 14): string {
    const compact = String(value || '').replace(/\s+/g, ' ').trim();
    if (!compact) return 'N/A';
    return compact.length <= limit ? compact : compact.slice(0, limit).trim();
  }

  private buildThroughputTrend(history: WorkflowHistoryResponse[]): void {
    const dayKeys = this.buildRecentDayKeys(6);
    const counts = new Map<string, number>();
    dayKeys.forEach(item => counts.set(item.key, 0));

    history.forEach(item => {
      if (!item.actionAt) {
        return;
      }
      const date = new Date(item.actionAt);
      if (Number.isNaN(date.getTime())) {
        return;
      }
      const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
      if (counts.has(key)) {
        counts.set(key, (counts.get(key) || 0) + 1);
      }
    });

    const values = dayKeys.map(item => counts.get(item.key) || 0);
    const maxValue = Math.max(...values, 1);
    this.throughputPoints = dayKeys.map((item, index) => {
      const value = counts.get(item.key) || 0;
      const x = dayKeys.length === 1 ? 310 : 28 + (index * (564 / (dayKeys.length - 1)));
      const y = 188 - ((value / maxValue) * 148);
      return { label: item.label, value, x, y };
    });

    this.throughputPath = this.throughputPoints
      .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
      .join(' ');
    this.throughputAreaPath = this.throughputPoints.length
      ? `${this.throughputPath} L ${this.throughputPoints[this.throughputPoints.length - 1].x} 188 L ${this.throughputPoints[0].x} 188 Z`
      : '';
  }

  private buildWorkflowHeatmap(data: WorkflowDashboardSummaryResponse): void {
    const combined = [...data.pendingQueue, ...data.recentHistory];
    const moduleCounts = new Map<string, number>();
    combined.forEach(item => {
      const key = item.moduleName || 'UNKNOWN';
      moduleCounts.set(key, (moduleCounts.get(key) || 0) + 1);
    });

    const topModules = Array.from(moduleCounts.entries())
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5)
      .map(([moduleName]) => moduleName);

    const statusCounts = new Map<string, number>();
    combined.forEach(item => {
      const key = item.toStatus || 'PENDING';
      statusCounts.set(key, (statusCounts.get(key) || 0) + 1);
    });

    this.workflowStatusHeaders = Array.from(statusCounts.entries())
      .sort((a, b) => b[1] - a[1])
      .slice(0, 4)
      .map(([status]) => status);

    const rawCounts: number[] = [];
    this.workflowHeatRows = topModules.map(moduleName => {
      const cells = this.workflowStatusHeaders.map(status => {
        const value = combined.filter(item => (item.moduleName || 'UNKNOWN') === moduleName && (item.toStatus || 'PENDING') === status).length;
        rawCounts.push(value);
        return {
          label: `${this.getLabel(moduleName)} / ${this.getLabel(status)}`,
          value,
          intensity: 0
        };
      });

      return {
        label: this.toChartLabel(this.getLabel(moduleName), 16),
        cells
      };
    });

    const maxValue = Math.max(...rawCounts, 1);
    this.workflowHeatRows = this.workflowHeatRows.map(row => ({
      ...row,
      cells: row.cells.map(cell => ({
        ...cell,
        intensity: Math.max(0.18, cell.value / maxValue)
      }))
    }));
  }

  private buildRecentDayKeys(dayCount: number): Array<{ key: string; label: string }> {
    const baseDate = new Date();
    const days: Array<{ key: string; label: string }> = [];

    for (let index = dayCount - 1; index >= 0; index -= 1) {
      const current = new Date(baseDate.getFullYear(), baseDate.getMonth(), baseDate.getDate() - index);
      days.push({
        key: `${current.getFullYear()}-${String(current.getMonth() + 1).padStart(2, '0')}-${String(current.getDate()).padStart(2, '0')}`,
        label: current.toLocaleString('en', { weekday: 'short' })
      });
    }

    return days;
  }
}
