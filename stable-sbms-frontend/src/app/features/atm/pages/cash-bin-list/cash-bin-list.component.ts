import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { AtmTerminalService } from '../../services/atm-terminal.service';
import { CashBinResponse } from '../../models/terminal.model';

@Component({
  selector: 'app-cash-bin-list',
  templateUrl: './cash-bin-list.component.html',
  styleUrls: ['./cash-bin-list.component.scss']
})
export class CashBinListComponent implements OnInit {

  allCashBins: CashBinResponse[] = [];
  cashBins: CashBinResponse[] = [];
  filteredCashBins: CashBinResponse[] = [];

  loading = false;

  page = 1;
  pageSize = 10;
  total = 0;

  totalAll = 0;
  activeCount = 0;
  lowCashCount = 0;
  fullCount = 0;

  filters = {
    search: '',
    status: ''
  };

  statusOptions = [
    { value: '', label: 'All Status' },
    { value: 'ACTIVE', label: 'ACTIVE' },
    { value: 'LOW_CASH', label: 'LOW CASH' },
    { value: 'FULL', label: 'FULL' },
    { value: 'INACTIVE', label: 'INACTIVE' },
    { value: 'ARCHIVED', label: 'ARCHIVED' }
  ];

  constructor(
    private atmApi: AtmTerminalService,
    private router: Router,
    private tableExport: TableExportService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadCashBins();
  }

  loadCashBins(): void {
    this.loading = true;

    this.atmApi.getCashBins().subscribe({
      next: data => {
        this.allCashBins = data || [];
        this.calculateSummary();
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error('Failed to load cash bins', err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load cash bins.', 'error');
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

  onView(c: CashBinResponse): void {
    this.router.navigate(['/atm/cash-bin', c.id]);
  }

  onEdit(c: CashBinResponse): void {
    this.router.navigate(['/atm/cash-bin', c.id, 'edit']);
  }

  onArchive(c: CashBinResponse): void {
    Swal.fire({
      icon: 'warning',
      title: 'Archive cash bin?',
      text: `${c.binNo} will be archived.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, archive'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.atmApi.archiveCashBin(c.id).subscribe({
        next: () => {
          Swal.fire('Archived', 'Cash bin archived successfully.', 'success');
          this.loadCashBins();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Archive failed.', 'error')
      });
    });
  }

  onRestore(c: CashBinResponse): void {
    Swal.fire({
      icon: 'question',
      title: 'Restore cash bin?',
      text: `${c.binNo} will be restored.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, restore'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.atmApi.restoreCashBin(c.id).subscribe({
        next: () => {
          Swal.fire('Restored', 'Cash bin restored successfully.', 'success');
          this.loadCashBins();
        },
        error: err => Swal.fire('Error', err?.error?.message || 'Restore failed.', 'error')
      });
    });
  }

  private applyFiltersAndPaging(resetPage: boolean): void {
    if (resetPage) this.page = 1;

    const search = this.filters.search.trim().toLowerCase();
    const status = this.filters.status.trim();

    const filtered = this.allCashBins.filter(c => {
      const matchSearch =
        !search ||
        (c.binNo || '').toLowerCase().includes(search) ||
        (c.terminalCode || '').toLowerCase().includes(search) ||
        (c.terminalName || '').toLowerCase().includes(search) ||
        String(c.terminalId || '').includes(search);

      const matchStatus = !status || c.status === status;

      return matchSearch && matchStatus;
    });

    this.filteredCashBins = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.cashBins = filtered.slice(start, start + this.pageSize);
  }

  private calculateSummary(): void {
    this.totalAll = this.allCashBins.length;
    this.activeCount = this.allCashBins.filter(c => c.status === 'ACTIVE').length;
    this.lowCashCount = this.allCashBins.filter(c => c.status === 'LOW_CASH').length;
    this.fullCount = this.allCashBins.filter(c => c.status === 'FULL').length;
  }

  private getExportableCashBins(): CashBinResponse[] {
    const hasFilter = !!this.filters.search?.trim() || !!this.filters.status?.trim();
    return hasFilter ? this.filteredCashBins : this.allCashBins;
  }

  onPrint(): void {
    const data = this.getExportableCashBins();

    if (!data.length) {
      Swal.fire('No data', 'No cash bin data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Cash Bin List',
      'Al-Barakah Shariah Banking Management System',
      ['Terminal Code', 'Terminal Name', 'Bin No', 'Denomination', 'Capacity', 'Current Count', 'Current Amount', 'Status'],
      data.map(c => [
        c.terminalCode,
        c.terminalName,
        c.binNo,
        String(c.denomination ?? ''),
        String(c.maxCapacity ?? ''),
        String(c.currentCount ?? ''),
        String(c.currentAmount ?? ''),
        c.status
      ])
    );
  }

  onExport(type: 'csv' | 'excel' | 'pdf'): void {
    const data = this.getExportableCashBins();

    if (!data.length) {
      Swal.fire('No data', 'No cash bin data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Cash Bins',
      'cash-bin-list',
      ['Terminal Code', 'Terminal Name', 'Bin No', 'Denomination', 'Capacity', 'Current Count', 'Current Amount', 'Status'],
      data.map(c => [
        c.terminalCode,
        c.terminalName,
        c.binNo,
        String(c.denomination ?? ''),
        String(c.maxCapacity ?? ''),
        String(c.currentCount ?? ''),
        String(c.currentAmount ?? ''),
        c.status
      ]),
      type
    );
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
