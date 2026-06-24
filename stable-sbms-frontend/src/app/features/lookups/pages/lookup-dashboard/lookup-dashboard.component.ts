import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { LookupDashboardSummaryResponse } from '../../models/lookup.model';
import { LookupService } from '../../services/lookup.service';

interface DashboardLegendItem {
  label: string;
  value: number;
  note: string;
  color: string;
}

interface DashboardBandItem {
  label: string;
  value: number;
  share: number;
  note: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

interface DashboardColumnItem {
  label: string;
  shortLabel: string;
  value: number;
  height: number;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

interface DashboardAxisTick {
  label: string;
  bottom: number;
}

interface DashboardTimelineItem {
  title: string;
  subtitle: string;
  meta: string;
  tone: 'teal' | 'green' | 'blue' | 'amber' | 'red' | 'purple';
}

interface DashboardInfoRow {
  label: string;
  value: string;
}

@Component({
  selector: 'app-lookup-dashboard',
  templateUrl: './lookup-dashboard.component.html',
  styleUrls: ['./lookup-dashboard.component.scss']
})
export class LookupDashboardComponent implements OnInit {

  loading = false;
  summary: LookupDashboardSummaryResponse | null = null;
  lookupHealthBands: DashboardBandItem[] = [];
  lookupHealthLegend: DashboardLegendItem[] = [];
  typeColumns: DashboardColumnItem[] = [];
  typeAxisTicks: DashboardAxisTick[] = [];
  recentTimeline: DashboardTimelineItem[] = [];
  configRows: DashboardInfoRow[] = [];
  lookupGradient = 'conic-gradient(#dbe4f0 0 100%)';

  constructor(
    private lookupService: LookupService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.lookupService.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load lookup dashboard.', 'error');
      }
    });
  }

  open(route: string): void {
    this.router.navigate([route]);
  }

  private prepareDashboard(data: LookupDashboardSummaryResponse): void {
    const totalValues = Math.max(data.lookupValueCount, 1);
    const activeValues = data.activeValueCount || 0;
    const inactiveValues = Math.max(data.lookupValueCount - activeValues, 0);
    const recentChanges = data.recentlyChangedCount || 0;
    const totalTypes = Math.max(data.lookupTypeCount, 1);
    const typeCoverage = data.recentTypes.reduce((sum, item) => sum + Number(item.totalValueCount || 0), 0);

    this.lookupHealthBands = [
      {
        label: 'Active value coverage',
        value: activeValues,
        share: this.getShare(activeValues, totalValues),
        note: 'Dropdown entries currently available to business modules.',
        tone: 'green'
      },
      {
        label: 'Inactive values',
        value: inactiveValues,
        share: this.getShare(inactiveValues, totalValues),
        note: 'Values kept out of current operational use.',
        tone: 'red'
      },
      {
        label: 'Recently changed records',
        value: recentChanges,
        share: this.getShare(recentChanges, totalValues),
        note: 'Configuration churn that may require review or regression checks.',
        tone: 'amber'
      },
      {
        label: 'Type catalog footprint',
        value: data.lookupTypeCount,
        share: this.getShare(data.lookupTypeCount, totalTypes),
        note: 'Reusable config families supporting the rest of the platform.',
        tone: 'teal'
      }
    ];

    this.lookupHealthLegend = [
      {
        label: 'Lookup types',
        value: data.lookupTypeCount,
        note: 'Reusable configuration families.',
        color: '#0f766e'
      },
      {
        label: 'Active values',
        value: activeValues,
        note: 'Ready for dropdown and rule use.',
        color: '#2563eb'
      },
      {
        label: 'Inactive values',
        value: inactiveValues,
        note: 'Temporarily out of circulation.',
        color: '#dc2626'
      },
      {
        label: 'Changed this week',
        value: recentChanges,
        note: 'Recent configuration updates.',
        color: '#d97706'
      }
    ];
    this.lookupGradient = this.buildDonutGradient(this.lookupHealthLegend);

    const chartSource = [...data.recentTypes]
      .sort((left, right) => Number(right.totalValueCount || 0) - Number(left.totalValueCount || 0))
      .slice(0, 6);
    const maxValue = Math.max(1, ...chartSource.map(item => Number(item.totalValueCount || 0)));
    const tones: Array<DashboardColumnItem['tone']> = ['teal', 'green', 'blue', 'amber', 'purple', 'red'];
    this.typeColumns = chartSource.map((item, index) => ({
      label: item.typeName || item.typeCode,
      shortLabel: this.toChartLabel(item.typeName || item.typeCode),
      value: Number(item.totalValueCount || 0),
      height: Math.max(12, Math.round((Number(item.totalValueCount || 0) / maxValue) * 100)),
      tone: tones[index % tones.length]
    }));
    this.typeAxisTicks = this.buildAxisTicks(maxValue);

    this.recentTimeline = data.recentValues.slice(0, 6).map(item => ({
      title: item.valueLabel,
      subtitle: `${item.typeName || item.typeCode} | ${item.valueCode}`,
      meta: `Status: ${this.readable(item.status)}`,
      tone: item.status === 'ACTIVE' ? 'green' : 'amber'
    }));

    this.configRows = [
      {
        label: 'Tracked type records',
        value: String(data.recentTypes.length)
      },
      {
        label: 'Tracked value records',
        value: String(data.recentValues.length)
      },
      {
        label: 'Catalog value footprint',
        value: String(typeCoverage)
      },
      {
        label: 'Activation ratio',
        value: `${this.getShare(activeValues, totalValues).toFixed(0)}%`
      }
    ];
  }

  private getShare(value: number, total: number): number {
    if (!total) {
      return 0;
    }
    return Number(((value / total) * 100).toFixed(2));
  }

  private buildDonutGradient(items: DashboardLegendItem[]): string {
    const total = Math.max(1, items.reduce((sum, item) => sum + Number(item.value || 0), 0));
    let start = 0;
    const stops = items.map(item => {
      const end = start + ((Number(item.value || 0) / total) * 100);
      const stop = `${item.color} ${start}% ${end}%`;
      start = end;
      return stop;
    });
    return `conic-gradient(${stops.join(', ')})`;
  }

  private buildAxisTicks(maxValue: number): DashboardAxisTick[] {
    const safeMax = Math.max(1, maxValue);
    return [0, 25, 50, 75, 100].map(percent => ({
      label: `${Math.round((safeMax * percent) / 100)}`,
      bottom: percent
    }));
  }

  private toChartLabel(label: string): string {
    const value = String(label || '').trim();
    if (value.length <= 14) {
      return value;
    }
    return value.split(' ').slice(0, 2).join(' ');
  }

  private readable(value?: string | null): string {
    return String(value || '').replace(/_/g, ' ');
  }
}
