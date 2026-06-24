import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { AtmTerminalService } from '../../services/atm-terminal.service';
import { ReconciliationResponse } from '../../models/terminal.model';

@Component({
  selector: 'app-reconciliation-list',
  templateUrl: './reconciliation-list.component.html',
  styleUrls: ['./reconciliation-list.component.scss']
})
export class ReconciliationListComponent implements OnInit {

  allItems: ReconciliationResponse[] = [];
  items: ReconciliationResponse[] = [];
  filteredItems: ReconciliationResponse[] = [];

  loading = false;
  page = 1;
  pageSize = 10;
  total = 0;

  totalAll = 0;
  matchedCount = 0;
  varianceCount = 0;
  approvedCount = 0;
  totalVariance = 0;
  userImageMap: Record<string, string> = {};
  userDisplayMap: Record<number, string> = {};

  filters = {
    search: '',
    status: ''
  };

  statusOptions = [
    { value: '', label: 'All Status' },
    { value: 'MATCHED', label: 'MATCHED' },
    { value: 'VARIANCE_FOUND', label: 'VARIANCE FOUND' },
    { value: 'APPROVED', label: 'APPROVED' }
  ];

  constructor(
    private atmApi: AtmTerminalService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadData();
  }

  loadData(): void {
    this.loading = true;

    this.atmApi.getReconciliations().subscribe({
      next: data => {
        this.allItems = data || [];
        this.calculateSummary();
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load reconciliation list.', 'error');
      }
    });
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

  onView(item: ReconciliationResponse): void {
    this.router.navigate(['/atm/reconciliation', item.id]);
  }

  onViewTerminal(item: ReconciliationResponse): void {
    this.router.navigate(['/atm/terminals', item.terminalId]);
  }

  getUserImageUrl(userId?: number | null): string {
    if (!userId) return '';
    return this.userImageMap[String(userId)] || '';
  }

  getUserDisplay(userId?: number | null): string {
    if (!userId) return '-';
    return this.userDisplayMap[userId] || `USER-${userId}`;
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private safe(value: any): string {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  private applyFiltersAndPaging(resetPage: boolean): void {
    if (resetPage) this.page = 1;

    const search = this.filters.search.trim().toLowerCase();
    const status = this.filters.status.trim();

    const filtered = this.allItems.filter(item => {
      const matchSearch =
        !search ||
        (item.terminalCode || '').toLowerCase().includes(search) ||
        (item.terminalName || '').toLowerCase().includes(search) ||
        String(item.approvedBy || '').includes(search);

      const matchStatus = !status || item.status === status;
      return matchSearch && matchStatus;
    });

    this.filteredItems = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  private calculateSummary(): void {
    this.totalAll = this.allItems.length;
    this.matchedCount = this.allItems.filter(item => item.status === 'MATCHED').length;
    this.varianceCount = this.allItems.filter(item => item.status === 'VARIANCE_FOUND').length;
    this.approvedCount = this.allItems.filter(item => item.status === 'APPROVED').length;
    this.totalVariance = this.allItems.reduce((sum, item) => sum + Math.abs(Number(item.varianceAmount || 0)), 0);
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

  private getExportableData(): ReconciliationResponse[] {
    const hasFilter = !!this.filters.search.trim() || !!this.filters.status.trim();
    return hasFilter ? this.filteredItems : this.allItems;
  }

  onExport(type: 'csv' | 'excel' | 'pdf'): void {
    const data = this.getExportableData();

    if (!data.length) {
      Swal.fire('No data', 'No reconciliation data to export.', 'warning');
      return;
    }

    if (type === 'csv') this.exportCSV(data);
    if (type === 'excel') this.exportExcel(data);
    if (type === 'pdf') this.exportPDF(data);
  }

  private exportCSV(data: ReconciliationResponse[]): void {
    const headers = ['Terminal Code', 'Terminal Name', 'System Amount', 'Physical Amount', 'Variance', 'Approved By', 'Date', 'Status'];
    const rows = data.map(item => [
      item.terminalCode,
      item.terminalName,
      item.systemAmount,
      item.physicalAmount,
      item.varianceAmount,
      item.approvedBy || '',
      item.reconDate,
      item.status
    ]);

    const csv = [headers, ...rows].map(row => row.map(value => `"${value}"`).join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });

    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'atm-reconciliation-list.csv';
    link.click();
  }

  private exportExcel(data: ReconciliationResponse[]): void {
    import('xlsx').then(xlsx => {
      const rows = data.map(item => ({
        'Terminal Code': item.terminalCode,
        'Terminal Name': item.terminalName,
        'System Amount': item.systemAmount,
        'Physical Amount': item.physicalAmount,
        'Variance Amount': item.varianceAmount,
        'Approved By': item.approvedBy || '',
        'Recon Date': item.reconDate,
        Status: item.status
      }));

      const worksheet = xlsx.utils.json_to_sheet(rows);
      const workbook = { Sheets: { Reconciliations: worksheet }, SheetNames: ['Reconciliations'] };
      const buffer = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });

      const link = document.createElement('a');
      link.href = URL.createObjectURL(new Blob([buffer], { type: 'application/octet-stream' }));
      link.download = 'atm-reconciliation-list.xlsx';
      link.click();
    });
  }

  private exportPDF(data: ReconciliationResponse[]): void {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(() => {
        const doc = new jsPDF.default('l');

        (doc as any).autoTable({
          head: [['Terminal', 'Name', 'System Amount', 'Physical Amount', 'Variance', 'Approved By', 'Date', 'Status']],
          body: data.map(item => [
            item.terminalCode,
            item.terminalName,
            item.systemAmount,
            item.physicalAmount,
            item.varianceAmount,
            item.approvedBy || '',
            item.reconDate,
            item.status
          ]),
          styles: { fontSize: 8 }
        });

        doc.save('atm-reconciliation-list.pdf');
      });
    });
  }
}
