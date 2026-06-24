import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { ZakatDashboardSummaryResponse, formatEnumLabel } from '../../models/zakat.model';
import { ZakatService } from '../../services/zakat.service';

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

@Component({
  selector: 'app-zakat-dashboard',
  templateUrl: './zakat-dashboard.component.html',
  styleUrls: ['./zakat-dashboard.component.scss']
})
export class ZakatDashboardComponent implements OnInit {

  loading = false;
  summary: ZakatDashboardSummaryResponse | null = null;
  zakatLegend: DashboardLegendItem[] = [];
  zakatBands: DashboardBandItem[] = [];
  statusColumns: DashboardColumnItem[] = [];
  fundColumns: DashboardColumnItem[] = [];
  statusAxisTicks: DashboardAxisTick[] = [];
  fundAxisTicks: DashboardAxisTick[] = [];
  zakatRows: Array<{ label: string; value: string }> = [];
  zakatGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  customerImageMap: Record<number, string> = {};

  constructor(
    private zakatService: ZakatService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.load();
  }

  load(): void {
    this.loading = true;
    this.zakatService.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load zakat dashboard.', 'error');
      }
    });
  }

  openProfiles(): void {
    this.router.navigate(['/zakat/profiles']);
  }

  openCalcRun(): void {
    this.router.navigate(['/zakat/calc-run']);
  }

  openCharityFund(): void {
    this.router.navigate(['/zakat/charity-fund']);
  }

  openBeneficiaries(): void {
    this.router.navigate(['/zakat/beneficiaries']);
  }

  openPayouts(): void {
    this.router.navigate(['/zakat/payouts']);
  }

  print(): void {
    window.print();
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getCustomerImageUrl(customerId?: number | null): string {
    if (!customerId) {
      return '';
    }
    return this.customerImageMap[customerId] || '';
  }

  private prepareDashboard(summary: ZakatDashboardSummaryResponse): void {
    const amountBase = Math.max(summary.totalZakatCalculated + summary.charityFundBalance + summary.beneficiaryPayoutTotal, 1);
    this.zakatLegend = [
      { label: 'Zakat Due Accounts', value: summary.zakatDueAccounts, note: 'profiles needing immediate zakat attention', color: '#f59e0b' },
      { label: 'Upcoming Reminders', value: summary.upcomingZakatReminders, note: 'customers nearing reminder thresholds', color: '#3b82f6' },
      { label: 'Recent Profiles', value: summary.recentProfiles.length, note: 'recently refreshed zakat profiles', color: '#22c55e' },
      { label: 'Recent Payouts', value: summary.recentPayouts.length, note: 'charity disbursements visible in the feed', color: '#ef4444' }
    ];
    this.zakatGradient = this.buildDonutGradient(this.zakatLegend);

    this.zakatBands = [
      {
        label: 'Total Zakat Calculated',
        value: Math.round(summary.totalZakatCalculated),
        note: 'current zakat assessed across due customers',
        share: this.getShare(summary.totalZakatCalculated, amountBase),
        tone: 'success'
      },
      {
        label: 'Charity Fund Balance',
        value: Math.round(summary.charityFundBalance),
        note: 'available fund position for future payout support',
        share: this.getShare(summary.charityFundBalance, amountBase),
        tone: 'primary'
      },
      {
        label: 'Payout Outflow',
        value: Math.round(summary.beneficiaryPayoutTotal),
        note: 'beneficiary support already disbursed',
        share: this.getShare(summary.beneficiaryPayoutTotal, amountBase),
        tone: 'info'
      },
      {
        label: 'Reminder Pressure',
        value: summary.upcomingZakatReminders,
        note: 'customers needing timely follow-up',
        share: this.getShare(summary.upcomingZakatReminders, Math.max(summary.zakatDueAccounts + summary.upcomingZakatReminders, 1)),
        tone: 'warning'
      }
    ];

    const statusMap = new Map<string, number>();
    summary.recentProfiles.forEach(item => {
      const key = this.getLabel(item.calculationStatus);
      statusMap.set(key, (statusMap.get(key) || 0) + 1);
    });
    const statusEntries = Array.from(statusMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxStatus = Math.max(...statusEntries.map(([, value]) => value), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'danger', 'info'];
    this.statusColumns = statusEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxStatus),
      tone: tones[index % tones.length]
    }));
    this.statusAxisTicks = this.buildAxisTicks(maxStatus);

    const fundMap = new Map<string, number>();
    summary.recentFundMovements.forEach(item => {
      const key = this.getLabel(item.sourceType);
      fundMap.set(key, (fundMap.get(key) || 0) + 1);
    });
    const fundEntries = Array.from(fundMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxFund = Math.max(...fundEntries.map(([, value]) => value), 1);
    this.fundColumns = fundEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxFund),
      tone: tones[index % tones.length]
    }));
    this.fundAxisTicks = this.buildAxisTicks(maxFund);

    this.zakatRows = [
      { label: 'Recent Profiles', value: `${summary.recentProfiles.length}` },
      { label: 'Fund Movements', value: `${summary.recentFundMovements.length}` },
      { label: 'Recent Payouts', value: `${summary.recentPayouts.length}` },
      { label: 'Upcoming Reminders', value: `${summary.upcomingZakatReminders}` }
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

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, customer) => {
      if (customer.id && customer.profileImageName) {
        acc[customer.id] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<number, string>);
  }
}
