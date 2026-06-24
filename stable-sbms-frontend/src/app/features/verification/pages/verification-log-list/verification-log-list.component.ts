import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { VerificationLogResponse, formatEnumLabel } from '../../models/verification.model';
import { VerificationService } from '../../services/verification.service';

@Component({
  selector: 'app-verification-log-list',
  templateUrl: './verification-log-list.component.html',
  styleUrls: ['./verification-log-list.component.scss']
})
export class VerificationLogListComponent implements OnInit {

  loading = false;
  allItems: VerificationLogResponse[] = [];
  items: VerificationLogResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  customerImageMap: Record<number, string> = {};
  userImageMap: Record<string, string> = {};
  filters = {
    keyword: '',
    channelType: '',
    status: ''
  };

  constructor(
    private verificationService: VerificationService,
    private customerService: CustomerService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService,
    private route: ActivatedRoute,
    private router: Router,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.filters.keyword = this.route.snapshot.queryParamMap.get('keyword') || '';
    this.loadSupportImages();
    this.load();
  }

  load(): void {
    this.loading = true;
    this.verificationService.getLogs().subscribe({
      next: data => {
        this.allItems = data;
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load verification logs.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.keyword.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchKeyword = !keyword
        || item.contactValueMasked.toLowerCase().includes(keyword)
        || (item.customerName || '').toLowerCase().includes(keyword)
        || (item.username || '').toLowerCase().includes(keyword)
        || item.purpose.toLowerCase().includes(keyword);
      const matchChannel = !this.filters.channelType || item.channelType === this.filters.channelType;
      const matchStatus = !this.filters.status || item.requestStatus === this.filters.status;
      return matchKeyword && matchChannel && matchStatus;
    });
    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onSearch(): void {
    this.page = 1;
    this.applyFilters();
  }

  onReset(): void {
    this.filters = { keyword: '', channelType: '', status: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openVerify(item?: VerificationLogResponse): void {
    this.router.navigate(['/verification/otp-verify'], {
      queryParams: item ? { requestId: item.id } : {}
    });
  }

  openProviderTest(): void {
    this.router.navigate(['/verification/provider-test']);
  }

  onPrint(): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No verification logs to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Verification Log List',
      'Al-Barakah Shariah Banking Management System',
      ['Channel', 'Actor', 'Masked Contact', 'Purpose', 'Status', 'Requested At'],
      data.map(item => [
        item.channelType,
        item.customerName || item.username || 'System',
        item.contactValueMasked,
        item.purpose,
        item.requestStatus,
        item.sentAt || item.expiresAt || item.usedAt || ''
      ])
    );
  }

  onExport(type: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No verification logs to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Verification Logs',
      'verification-log-list',
      ['Channel', 'Actor', 'Masked Contact', 'Purpose', 'Status', 'Requested At'],
      data.map(item => [
        item.channelType,
        item.customerName || item.username || 'System',
        item.contactValueMasked,
        item.purpose,
        item.requestStatus,
        item.sentAt || item.expiresAt || item.usedAt || ''
      ]),
      type as 'csv' | 'excel' | 'pdf'
    );
  }

  inspect(item: VerificationLogResponse): void {
    const attempts = (item.attempts || []).map(entry =>
      `<tr><td>${this.getLabel(entry.attemptType)}</td><td>${entry.attemptValueMasked || '-'}</td><td>${this.getLabel(entry.attemptStatus)}</td><td>${entry.createdAt || '-'}</td></tr>`
    ).join('');
    Swal.fire({
      title: `Request #${item.id}`,
      width: 860,
      html: `
        <div style="text-align:left">
          <p><strong>Provider Response:</strong></p>
          <pre style="white-space:pre-wrap;background:#f8fafc;border:1px solid #e2e8f0;padding:12px;border-radius:10px;">${item.providerResponse || '-'}</pre>
          <p><strong>Attempts:</strong></p>
          <table style="width:100%;border-collapse:collapse;font-size:12px;">
            <thead><tr><th style="text-align:left;border-bottom:1px solid #e2e8f0;padding:6px;">Type</th><th style="text-align:left;border-bottom:1px solid #e2e8f0;padding:6px;">Masked</th><th style="text-align:left;border-bottom:1px solid #e2e8f0;padding:6px;">Status</th><th style="text-align:left;border-bottom:1px solid #e2e8f0;padding:6px;">Time</th></tr></thead>
            <tbody>${attempts || '<tr><td colspan="4" style="padding:10px;">No attempt log found.</td></tr>'}</tbody>
          </table>
        </div>`,
      confirmButtonText: 'Close'
    });
  }

  resend(item: VerificationLogResponse): void {
    this.verificationService.resendOtp(item.id).subscribe({
      next: () => {
        Swal.fire('Resent', 'OTP resent successfully.', 'success');
        this.load();
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Resend failed.', 'error')
    });
  }

  expire(item: VerificationLogResponse): void {
    this.verificationService.expireOtp(item.id).subscribe({
      next: () => {
        Swal.fire('Expired', 'OTP expired successfully.', 'success');
        this.load();
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Expire failed.', 'error')
    });
  }

  markFailed(item: VerificationLogResponse): void {
    this.verificationService.markFailed(item.id).subscribe({
      next: () => {
        Swal.fire('Updated', 'OTP request marked failed.', 'success');
        this.load();
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Mark failed action failed.', 'error')
    });
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

  private getExportableData(): VerificationLogResponse[] {
    const keyword = this.filters.keyword.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchKeyword = !keyword
        || item.contactValueMasked.toLowerCase().includes(keyword)
        || (item.customerName || '').toLowerCase().includes(keyword)
        || (item.username || '').toLowerCase().includes(keyword)
        || item.purpose.toLowerCase().includes(keyword);
      const matchChannel = !this.filters.channelType || item.channelType === this.filters.channelType;
      const matchStatus = !this.filters.status || item.requestStatus === this.filters.status;
      return matchKeyword && matchChannel && matchStatus;
    });
  }
}
