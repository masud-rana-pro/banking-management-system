import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { BranchApiService } from '../services/branch-api.service';
import { TellerLimitApiService } from '../services/teller-limit-api.service';
import { BranchResponse } from '../models/branch.model';
import { TellerLimitRequest, TellerLimitResponse } from '../models/teller-limit.model';

@Component({
  selector: 'app-teller-limit',
  templateUrl: './teller-limit.component.html',
  styleUrls: ['./teller-limit.component.scss']
})
export class TellerLimitComponent implements OnInit {

  branches: BranchResponse[] = [];

  allLimits: TellerLimitResponse[] = [];
  limits: TellerLimitResponse[] = [];
  filteredLimits: TellerLimitResponse[] = [];

  branchSearch = '';

  loading = false;
  saving = false;
  userImageMap: Record<string, string> = {};
  userDisplayMap: Record<number, string> = {};

  page = 1;
  pageSize = 10;
  total = 0;

  editingId: number | null = null;

  filters: { search: string; status: string } = {
    search: '',
    status: ''
  };

  form: TellerLimitRequest = {
    branchId: 0,
    userId: 0,
    limitDate: '',
    dailyDepositLimit: 0,
    dailyWithdrawLimit: 0,
    singleTxnLimit: 0,
    status: 'ACTIVE'
  };

  statusOptions = [
    { value: '', label: 'All Status' },
    { value: 'ACTIVE', label: 'ACTIVE' },
    { value: 'INACTIVE', label: 'INACTIVE' }
  ];

  constructor(
    private branchApi: BranchApiService,
    private tellerLimitApi: TellerLimitApiService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService
  ) { }

  ngOnInit(): void {
    this.loadUsers();
    this.loadBranches();
    this.loadLimits();
  }

  loadBranches(): void {
    this.branchApi.getAll('', 'ACTIVE').subscribe({
      next: data => this.branches = data || [],
      error: () => this.branches = []
    });
  }
  private safe(value: any): string {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  loadLimits(): void {
    this.loading = true;

    this.tellerLimitApi.getAll(this.filters.status).subscribe({
      next: data => {
        this.allLimits = data || [];
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.allLimits = [];
        this.applyFiltersAndPaging(true);
        this.loading = false;
        Swal.fire('Error', 'Failed to load teller limits.', 'error');
      }
    });
  }

  onBranchPicked(): void {
    const value = (this.branchSearch || '').trim().toLowerCase();

    const selected = this.branches.find(b =>
      `${b.branchCode} - ${b.branchName}`.toLowerCase() === value ||
      (b.branchCode || '').toLowerCase() === value ||
      (b.branchName || '').toLowerCase() === value
    );

    this.form.branchId = selected ? selected.id : 0;
  }

  onSearch(): void {
    this.applyFiltersAndPaging(true);
  }

  onReset(): void {
    this.filters = { search: '', status: '' };
    this.applyFiltersAndPaging(true);
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFiltersAndPaging(false);
  }

  saveLimit(): void {
    this.onBranchPicked();

    if (!this.validateForm()) return;

    this.saving = true;

    const request: TellerLimitRequest = {
      branchId: Number(this.form.branchId),
      userId: Number(this.form.userId),
      limitDate: this.form.limitDate,
      dailyDepositLimit: Number(this.form.dailyDepositLimit),
      dailyWithdrawLimit: Number(this.form.dailyWithdrawLimit),
      singleTxnLimit: Number(this.form.singleTxnLimit),
      status: this.form.status || 'ACTIVE'
    };

    const apiCall = this.editingId
      ? this.tellerLimitApi.update(this.editingId, request)
      : this.tellerLimitApi.create(request);

    apiCall.subscribe({
      next: () => {
        this.saving = false;

        Swal.fire({
          icon: 'success',
          title: this.editingId ? 'Updated' : 'Saved',
          text: this.editingId ? 'Teller limit updated successfully.' : 'Teller limit saved successfully.',
          timer: 1400,
          showConfirmButton: false
        });

        this.resetForm();
        this.loadLimits();
      },
      error: err => {
        this.saving = false;

        Swal.fire({
          icon: 'error',
          title: 'Save failed',
          text: err?.error?.message || 'Please check teller limit rules and try again.'
        });
      }
    });
  }
  editLimit(item: TellerLimitResponse): void {
    this.editingId = item.id;

    this.form = {
      branchId: item.branchId,
      userId: item.userId,
      limitDate: item.limitDate,
      dailyDepositLimit: item.dailyDepositLimit,
      dailyWithdrawLimit: item.dailyWithdrawLimit,
      singleTxnLimit: item.singleTxnLimit,
      status: item.status
    };

    const selectedBranch = this.branches.find(b => b.id === item.branchId);
    this.branchSearch = selectedBranch ? `${selectedBranch.branchCode} - ${selectedBranch.branchName}` : '';
  }

  deactivate(item: TellerLimitResponse): void {
    Swal.fire({
      icon: 'warning',
      title: 'Deactivate teller limit?',
      text: `Limit for user #${item.userId} will become inactive.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, deactivate',
      cancelButtonText: 'Cancel'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.tellerLimitApi.deactivate(item.id).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Deactivated',
            text: 'Teller limit deactivated successfully.',
            timer: 1400,
            showConfirmButton: false
          });

          this.loadLimits();
        },
        error: () => Swal.fire('Error', 'Deactivate failed.', 'error')
      });
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.branchSearch = '';

    this.form = {
      branchId: 0,
      userId: 0,
      limitDate: '',
      dailyDepositLimit: 0,
      dailyWithdrawLimit: 0,
      singleTxnLimit: 0,
      status: 'ACTIVE'
    };
  }

  getBranchName(branchId: number): string {
    return this.branches.find(b => b.id === branchId)?.branchName || `Branch #${branchId}`;
  }

  getBranchCode(branchId: number): string {
    return this.branches.find(b => b.id === branchId)?.branchCode || String(branchId);
  }

  getUserImageUrl(userId?: number | null): string {
    if (!userId) return '';
    return this.userImageMap[String(userId)] || '';
  }

  getUserDisplay(userId?: number | null): string {
    if (!userId) return '-';
    return this.userDisplayMap[userId] || `USER-${userId}`;
  }

  formatAmount(value: number): string {
    return Number(value || 0).toLocaleString('en-BD', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
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

  onExport(type: 'csv' | 'excel' | 'pdf'): void {
    const data = this.getExportableLimits();

    if (!data.length) {
      Swal.fire('No data', 'No teller limit data to export.', 'warning');
      return;
    }

    if (type === 'csv') this.exportCSV(data);
    if (type === 'excel') this.exportExcel(data);
    if (type === 'pdf') this.exportPDF(data);
  }

  private getExportableLimits(): TellerLimitResponse[] {
    const hasFilter =
      !!this.filters.search?.trim() ||
      !!this.filters.status?.trim();

    return hasFilter ? this.filteredLimits : this.allLimits;
  }

  private exportCSV(data: TellerLimitResponse[]): void {
    const headers = [
      'Branch Code',
      'Branch Name',
      'Teller User ID',
      'Limit Date',
      'Daily Deposit Limit',
      'Daily Withdraw Limit',
      'Single Txn Limit',
      'Status'
    ];

    const rows = data.map(item => [
      this.getBranchCode(item.branchId),
      this.getBranchName(item.branchId),
      item.userId,
      item.limitDate,
      this.formatAmount(item.dailyDepositLimit),
      this.formatAmount(item.dailyWithdrawLimit),
      this.formatAmount(item.singleTxnLimit),
      item.status
    ]);

    const csvContent = [headers, ...rows]
      .map(row => row.map(value => `"${String(value ?? '').replace(/"/g, '""')}"`).join(','))
      .join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'teller-limits.csv';
    link.click();

    Swal.fire('Exported', 'CSV file downloaded.', 'success');
  }

  private exportExcel(data: TellerLimitResponse[]): void {
    import('xlsx').then(xlsx => {
      const rows = data.map(item => ({
        'Branch Code': this.getBranchCode(item.branchId),
        'Branch Name': this.getBranchName(item.branchId),
        'Teller User ID': item.userId,
        'Limit Date': item.limitDate,
        'Daily Deposit Limit': this.formatAmount(item.dailyDepositLimit),
        'Daily Withdraw Limit': this.formatAmount(item.dailyWithdrawLimit),
        'Single Txn Limit': this.formatAmount(item.singleTxnLimit),
        'Status': item.status
      }));

      const worksheet = xlsx.utils.json_to_sheet(rows);
      const workbook = { Sheets: { 'Teller Limits': worksheet }, SheetNames: ['Teller Limits'] };

      const excelBuffer = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });
      const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });

      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = 'teller-limits.xlsx';
      link.click();

      Swal.fire('Exported', 'Excel file downloaded.', 'success');
    });
  }

  private exportPDF(data: TellerLimitResponse[]): void {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(() => {
        const doc = new jsPDF.default('l');

        const rows = data.map(item => [
          this.getBranchCode(item.branchId),
          this.getBranchName(item.branchId),
          item.userId,
          item.limitDate,
          this.formatAmount(item.dailyDepositLimit),
          this.formatAmount(item.dailyWithdrawLimit),
          this.formatAmount(item.singleTxnLimit),
          item.status
        ]);

        (doc as any).autoTable({
          head: [[
            'Branch Code',
            'Branch Name',
            'Teller',
            'Limit Date',
            'Deposit',
            'Withdraw',
            'Single Txn',
            'Status'
          ]],
          body: rows,
          styles: { fontSize: 8 }
        });

        doc.save('teller-limits.pdf');
        Swal.fire('Exported', 'PDF downloaded.', 'success');
      });
    });
  }

  private validateForm(): boolean {
    if (!this.form.branchId || Number(this.form.branchId) <= 0) {
      Swal.fire('Validation', 'Please select a valid branch from the suggestion list.', 'warning');
      return false;
    }

    if (!this.form.userId || Number(this.form.userId) <= 0) {
      Swal.fire('Validation', 'Please enter a valid teller user ID.', 'warning');
      return false;
    }

    if (!this.form.limitDate) {
      Swal.fire('Validation', 'Limit date is required.', 'warning');
      return false;
    }

    if (!this.form.dailyDepositLimit || Number(this.form.dailyDepositLimit) <= 0) {
      Swal.fire('Validation', 'Daily deposit limit must be greater than zero.', 'warning');
      return false;
    }

    if (!this.form.dailyWithdrawLimit || Number(this.form.dailyWithdrawLimit) <= 0) {
      Swal.fire('Validation', 'Daily withdraw limit must be greater than zero.', 'warning');
      return false;
    }

    if (!this.form.singleTxnLimit || Number(this.form.singleTxnLimit) <= 0) {
      Swal.fire('Validation', 'Single transaction limit must be greater than zero.', 'warning');
      return false;
    }

    if (Number(this.form.singleTxnLimit) > Number(this.form.dailyDepositLimit)) {
      Swal.fire('Validation', 'Single transaction limit cannot be greater than daily deposit limit.', 'warning');
      return false;
    }

    if (Number(this.form.singleTxnLimit) > Number(this.form.dailyWithdrawLimit)) {
      Swal.fire('Validation', 'Single transaction limit cannot be greater than daily withdraw limit.', 'warning');
      return false;
    }

    return true;
  }

  private applyFiltersAndPaging(resetPage: boolean): void {
    if (resetPage) this.page = 1;

    const keyword = (this.filters.search || '').trim().toLowerCase();
    const status = (this.filters.status || '').trim();

    const filtered = this.allLimits.filter(item => {
      const branchName = this.getBranchName(item.branchId).toLowerCase();
      const branchCode = this.getBranchCode(item.branchId).toLowerCase();

      const matchSearch =
        !keyword ||
        branchName.includes(keyword) ||
        branchCode.includes(keyword) ||
        String(item.branchId).includes(keyword) ||
        String(item.userId).includes(keyword) ||
        (item.limitDate || '').includes(keyword);

      const matchStatus = !status || item.status === status;

      return matchSearch && matchStatus;
    });

    this.filteredLimits = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.limits = filtered.slice(start, start + this.pageSize);
  }
}
