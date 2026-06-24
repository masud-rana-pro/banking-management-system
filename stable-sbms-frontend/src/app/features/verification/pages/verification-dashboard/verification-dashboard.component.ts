import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { VerificationLogResponse } from '../../models/verification.model';
import { VerificationDashboardSummaryResponse, formatEnumLabel } from '../../models/verification.model';
import { VerificationService } from '../../services/verification.service';

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
  selector: 'app-verification-dashboard',
  templateUrl: './verification-dashboard.component.html',
  styleUrls: ['./verification-dashboard.component.scss']
})
export class VerificationDashboardComponent implements OnInit {

  loading = false;
  summary: VerificationDashboardSummaryResponse | null = null;
  verificationLegend: DashboardLegendItem[] = [];
  verificationBands: DashboardBandItem[] = [];
  purposeColumns: DashboardColumnItem[] = [];
  attemptColumns: DashboardColumnItem[] = [];
  purposeAxisTicks: DashboardAxisTick[] = [];
  attemptAxisTicks: DashboardAxisTick[] = [];
  verificationRows: Array<{ label: string; value: string }> = [];
  verificationGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  customerImageMap: Record<number, string> = {};
  userImageMap: Record<string, string> = {};

  constructor(
    private verificationService: VerificationService,
    private customerService: CustomerService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadSupportImages();
    this.load();
  }

  load(): void {
    this.loading = true;
    this.verificationService.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load verification dashboard.', 'error');
      }
    });
  }

  openLogs(): void {
    this.router.navigate(['/verification/logs']);
  }

  openVerify(): void {
    this.router.navigate(['/verification/otp-verify']);
  }

  openProviderTest(): void {
    this.router.navigate(['/verification/provider-test']);
  }

  openForgotPassword(): void {
    this.router.navigate(['/verification/forgot-password']);
  }

  openResetPassword(): void {
    this.router.navigate(['/verification/reset-password']);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getActorImageUrl(item: VerificationLogResponse): string {
    if (item.customerId && this.customerImageMap[item.customerId]) {
      return this.customerImageMap[item.customerId];
    }
    const username = (item.username || '').trim().toLowerCase();
    return username ? this.userImageMap[username] || '' : '';
  }

  private prepareDashboard(summary: VerificationDashboardSummaryResponse): void {
    const requestBase = Math.max(summary.pendingRequests + summary.verifiedRequests + summary.failedRequests, 1);
    this.verificationLegend = [
      { label: 'Pending', value: summary.pendingRequests, note: 'OTP or reset requests awaiting completion', color: '#f59e0b' },
      { label: 'Verified', value: summary.verifiedRequests, note: 'requests successfully completed', color: '#22c55e' },
      { label: 'Failed', value: summary.failedRequests, note: 'expired, incorrect or failed attempts', color: '#ef4444' },
      { label: 'Password Reset', value: summary.passwordResetRequests, note: 'password reset requests generated', color: '#3b82f6' }
    ];
    this.verificationGradient = this.buildDonutGradient(this.verificationLegend);

    this.verificationBands = [
      {
        label: 'Pending Queue',
        value: summary.pendingRequests,
        note: 'watch for stuck OTP requests before they expire',
        share: this.getShare(summary.pendingRequests, requestBase),
        tone: 'warning'
      },
      {
        label: 'Successful Verification',
        value: summary.verifiedRequests,
        note: 'healthy completion across login, reset and provider checks',
        share: this.getShare(summary.verifiedRequests, requestBase),
        tone: 'success'
      },
      {
        label: 'Failure Load',
        value: summary.failedRequests,
        note: 'review attempt logs for repeated failures or abuse',
        share: this.getShare(summary.failedRequests, requestBase),
        tone: 'danger'
      },
      {
        label: 'Contact Gaps',
        value: summary.unverifiedEmailCount + summary.unverifiedMobileCount,
        note: 'users or customers still not ready for trusted delivery',
        share: this.getShare(summary.unverifiedEmailCount + summary.unverifiedMobileCount, Math.max(summary.unverifiedEmailCount + summary.unverifiedMobileCount, requestBase)),
        tone: 'info'
      }
    ];

    const purposeMap = new Map<string, number>();
    summary.recentRequests.forEach(item => {
      const key = this.getLabel(item.purpose);
      purposeMap.set(key, (purposeMap.get(key) || 0) + 1);
    });
    const purposeEntries = Array.from(purposeMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxPurpose = Math.max(...purposeEntries.map(([, value]) => value), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    this.purposeColumns = purposeEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxPurpose),
      tone: tones[index % tones.length]
    }));
    this.purposeAxisTicks = this.buildAxisTicks(maxPurpose);

    const attemptMap = new Map<string, number>();
    summary.recentAttempts.forEach(item => {
      const key = this.getLabel(item.attemptType);
      attemptMap.set(key, (attemptMap.get(key) || 0) + 1);
    });
    const attemptEntries = Array.from(attemptMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxAttempt = Math.max(...attemptEntries.map(([, value]) => value), 1);
    this.attemptColumns = attemptEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxAttempt),
      tone: tones[index % tones.length]
    }));
    this.attemptAxisTicks = this.buildAxisTicks(maxAttempt);

    this.verificationRows = [
      { label: 'Provider Dispatch Queue', value: `${summary.providerDispatchCount}` },
      { label: 'Unverified Email', value: `${summary.unverifiedEmailCount}` },
      { label: 'Unverified Mobile', value: `${summary.unverifiedMobileCount}` },
      { label: 'Recent Contact Statuses', value: `${summary.recentContactStatuses.length}` }
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

  private loadSupportImages(): void {
    this.customerService.getAll().subscribe({
      next: customers => {
        this.customerImageMap = this.buildCustomerImageMap(customers || []);
      },
      error: () => {
        this.customerImageMap = {};
      }
    });

    this.userApi.getAll().subscribe({
      next: users => {
        this.userImageMap = this.buildUserImageMap(users || []);
      },
      error: () => {
        this.userImageMap = {};
      }
    });
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce<Record<number, string>>((acc, customer) => {
      if (customer.id && customer.profileImageName) {
        acc[customer.id] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {});
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
