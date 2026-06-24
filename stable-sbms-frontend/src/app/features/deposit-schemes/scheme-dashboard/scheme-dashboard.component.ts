import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { DepositSchemeDashboardSummaryResponse, DepositSchemeEnrollmentResponse, DepositSchemeResponse, formatEnumLabel } from '../models/deposit-scheme.model';
import { DepositSchemeService } from '../services/deposit-scheme.service';

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
  selector: 'app-scheme-dashboard',
  templateUrl: './scheme-dashboard.component.html',
  styleUrls: ['./scheme-dashboard.component.scss']
})
export class SchemeDashboardComponent implements OnInit {

  loading = false;
  summary: DepositSchemeDashboardSummaryResponse | null = null;
  schemeLegend: DashboardLegendItem[] = [];
  schemeBands: DashboardBandItem[] = [];
  typeColumns: DashboardColumnItem[] = [];
  enrollmentColumns: DashboardColumnItem[] = [];
  typeAxisTicks: DashboardAxisTick[] = [];
  enrollmentAxisTicks: DashboardAxisTick[] = [];
  schemeRows: Array<{ label: string; value: string }> = [];
  schemeGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  customerImageMap: Record<number, string> = {};

  constructor(
    private depositSchemeApi: DepositSchemeService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.load();
  }

  load(): void {
    this.loading = true;
    this.depositSchemeApi.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load deposit scheme dashboard.', 'error');
      }
    });
  }

  openSchemeList(): void {
    this.router.navigate(['/deposit-schemes/list']);
  }

  openNewScheme(): void {
    this.router.navigate(['/deposit-schemes/new']);
  }

  openEnrollments(): void {
    this.router.navigate(['/deposit-schemes/enrollments/list']);
  }

  openNewEnrollment(): void {
    this.router.navigate(['/deposit-schemes/enrollments/new']);
  }

  viewScheme(item: DepositSchemeResponse): void {
    this.router.navigate(['/deposit-schemes', item.id]);
  }

  viewEnrollment(item: DepositSchemeEnrollmentResponse): void {
    this.router.navigate(['/deposit-schemes/enrollments', item.id, 'schedule']);
  }

  getCustomerImageUrl(customerId?: number | null): string {
    if (!customerId) {
      return '';
    }
    return this.customerImageMap[customerId] || '';
  }

  formatLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private prepareDashboard(summary: DepositSchemeDashboardSummaryResponse): void {
    const totalSchemes = Math.max(summary.totalSchemes, 1);
    const totalEnrollments = Math.max(summary.activeEnrollments + summary.maturedSchemes + summary.earlyWithdrawalRequests, 1);
    this.schemeLegend = [
      { label: 'Active Enrollments', value: summary.activeEnrollments, note: 'live contribution book under collection', color: '#22c55e' },
      { label: 'Due Installments', value: summary.dueInstallments, note: 'installments waiting for customer payment', color: '#f59e0b' },
      { label: 'Early Withdrawal', value: summary.earlyWithdrawalRequests, note: 'requests needing exception handling', color: '#ef4444' },
      { label: 'Matured Schemes', value: summary.maturedSchemes, note: 'plans reaching final maturity', color: '#3b82f6' }
    ];
    this.schemeGradient = this.buildDonutGradient(this.schemeLegend);

    this.schemeBands = [
      {
        label: 'Enrollment Pressure',
        value: summary.activeEnrollments,
        note: `${summary.recentEnrollments.length} recent enrollment records tracked here`,
        share: this.getShare(summary.activeEnrollments, totalEnrollments),
        tone: 'success'
      },
      {
        label: 'Collection Due',
        value: summary.dueInstallments,
        note: 'installment reminder and collection workload',
        share: this.getShare(summary.dueInstallments, Math.max(summary.dueInstallments, totalEnrollments)),
        tone: 'warning'
      },
      {
        label: 'Early Exit Risk',
        value: summary.earlyWithdrawalRequests,
        note: 'watch this queue to protect scheme retention',
        share: this.getShare(summary.earlyWithdrawalRequests, totalEnrollments),
        tone: 'danger'
      },
      {
        label: 'Maturity Readiness',
        value: summary.maturedSchemes,
        note: 'cases nearing payout or closure processing',
        share: this.getShare(summary.maturedSchemes, totalEnrollments),
        tone: 'info'
      }
    ];

    const typeMap = new Map<string, number>();
    summary.recentSchemes.forEach(item => {
      const key = this.formatLabel(item.schemeType);
      typeMap.set(key, (typeMap.get(key) || 0) + 1);
    });
    const typeEntries = Array.from(typeMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxType = Math.max(...typeEntries.map(([, value]) => value), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    this.typeColumns = typeEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxType),
      tone: tones[index % tones.length]
    }));
    this.typeAxisTicks = this.buildAxisTicks(maxType);

    const enrollmentMap = new Map<string, number>();
    summary.recentEnrollments.forEach(item => {
      const key = this.formatLabel(item.enrollmentStatus);
      enrollmentMap.set(key, (enrollmentMap.get(key) || 0) + 1);
    });
    const enrollmentEntries = Array.from(enrollmentMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxEnrollment = Math.max(...enrollmentEntries.map(([, value]) => value), 1);
    this.enrollmentColumns = enrollmentEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxEnrollment),
      tone: tones[index % tones.length]
    }));
    this.enrollmentAxisTicks = this.buildAxisTicks(maxEnrollment);

    this.schemeRows = [
      { label: 'Recent Schemes', value: `${summary.recentSchemes.length}` },
      { label: 'Recent Enrollments', value: `${summary.recentEnrollments.length}` },
      { label: 'Due Installments', value: `${summary.dueInstallments}` },
      { label: 'Matured Schemes', value: `${summary.maturedSchemes}` }
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
        this.customerImageMap = this.buildCustomerImageMap(customers);
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
