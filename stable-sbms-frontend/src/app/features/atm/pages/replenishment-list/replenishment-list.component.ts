import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { AtmTerminalService } from '../../services/atm-terminal.service';
import { ReplenishmentResponse } from '../../models/terminal.model';

@Component({
  selector: 'app-replenishment-list',
  templateUrl: './replenishment-list.component.html',
  styleUrls: ['./replenishment-list.component.scss']
})
export class ReplenishmentListComponent implements OnInit {

  allItems: ReplenishmentResponse[] = [];
  items: ReplenishmentResponse[] = [];
  filteredItems: ReplenishmentResponse[] = [];

  loading = false;
  page = 1;
  pageSize = 10;
  total = 0;

  totalAll = 0;
  completedCount = 0;
  todayCount = 0;
  totalAmount = 0;
  userImageMap: Record<string, string> = {};
  userDisplayMap: Record<number, string> = {};

  filters = {
    search: '',
    status: ''
  };

  statusOptions = [
    { value: '', label: 'All Status' },
    { value: 'COMPLETED', label: 'COMPLETED' },
    { value: 'CANCELLED', label: 'CANCELLED' }
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

    this.atmApi.getReplenishments().subscribe({
      next: data => {
        this.allItems = data || [];
        this.calculateSummary();
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load replenishment list.', 'error');
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

  onView(item: ReplenishmentResponse): void {
    this.router.navigate(['/atm/replenishment', item.id]);
  }

  onViewTerminal(item: ReplenishmentResponse): void {
    this.router.navigate(['/atm/terminals', item.terminalId]);
  }

  getUserImageUrl(userId?: number | null): string {
    if (!userId) {
      return '';
    }
    return this.userImageMap[String(userId)] || '';
  }

  getUserDisplay(userId?: number | null): string {
    if (!userId) {
      return '-';
    }
    return this.userDisplayMap[userId] || `USER-${userId}`;
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
        (item.binNo || '').toLowerCase().includes(search) ||
        String(item.performedBy || '').includes(search);

      const matchStatus = !status || item.status === status;
      return matchSearch && matchStatus;
    });

    this.filteredItems = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  private calculateSummary(): void {
    const today = new Date().toISOString().slice(0, 10);
    this.totalAll = this.allItems.length;
    this.completedCount = this.allItems.filter(item => item.status === 'COMPLETED').length;
    this.todayCount = this.allItems.filter(item => item.replenishmentDate === today).length;
    this.totalAmount = this.allItems.reduce((sum, item) => sum + Number(item.amountAdded || 0), 0);
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
      if (user.id) this.userDisplayMap[user.id] = user.fullName || user.username;
      return acc;
    }, {});
  }

  private getExportableData(): ReplenishmentResponse[] {
    const hasFilter = !!this.filters.search.trim() || !!this.filters.status.trim();
    return hasFilter ? this.filteredItems : this.allItems;
  }

  onExport(type: 'csv' | 'excel' | 'pdf'): void {
    const data = this.getExportableData();

    if (!data.length) {
      Swal.fire('No data', 'No replenishment data to export.', 'warning');
      return;
    }

    if (type === 'csv') this.exportCSV(data);
    if (type === 'excel') this.exportExcel(data);
    if (type === 'pdf') this.exportPDF(data);
  }

  private exportCSV(data: ReplenishmentResponse[]): void {
    const headers = ['Terminal Code', 'Terminal Name', 'Bin No', 'Quantity', 'Amount', 'Performed By', 'Date', 'Status'];
    const rows = data.map(item => [
      item.terminalCode,
      item.terminalName,
      item.binNo,
      item.quantityAdded,
      item.amountAdded,
      item.performedBy,
      item.replenishmentDate,
      item.status
    ]);

    const csv = [headers, ...rows].map(row => row.map(value => `"${value}"`).join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });

    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'atm-replenishment-list.csv';
    link.click();
  }

  private exportExcel(data: ReplenishmentResponse[]): void {
    import('xlsx').then(xlsx => {
      const rows = data.map(item => ({
        'Terminal Code': item.terminalCode,
        'Terminal Name': item.terminalName,
        'Bin No': item.binNo,
        'Quantity Added': item.quantityAdded,
        'Amount Added': item.amountAdded,
        'Performed By': item.performedBy,
        'Replenishment Date': item.replenishmentDate,
        Status: item.status
      }));

      const worksheet = xlsx.utils.json_to_sheet(rows);
      const workbook = { Sheets: { Replenishments: worksheet }, SheetNames: ['Replenishments'] };
      const buffer = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });

      const link = document.createElement('a');
      link.href = URL.createObjectURL(new Blob([buffer], { type: 'application/octet-stream' }));
      link.download = 'atm-replenishment-list.xlsx';
      link.click();
    });
  }

  private exportPDF(data: ReplenishmentResponse[]): void {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(() => {
        const doc = new jsPDF.default('l');

        (doc as any).autoTable({
          head: [['Terminal', 'Name', 'Bin', 'Quantity', 'Amount', 'Performed By', 'Date', 'Status']],
          body: data.map(item => [
            item.terminalCode,
            item.terminalName,
            item.binNo,
            item.quantityAdded,
            item.amountAdded,
            item.performedBy,
            item.replenishmentDate,
            item.status
          ]),
          styles: { fontSize: 8 }
        });

        doc.save('atm-replenishment-list.pdf');
      });
    });
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
