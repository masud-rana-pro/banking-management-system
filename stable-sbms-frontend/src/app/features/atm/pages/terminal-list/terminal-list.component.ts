import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { AtmTerminalService } from '../../services/atm-terminal.service';
import { TerminalResponse } from '../../models/terminal.model';

@Component({
  selector: 'app-terminal-list',
  templateUrl: './terminal-list.component.html',
  styleUrls: ['./terminal-list.component.scss']
})
export class TerminalListComponent implements OnInit {

  allTerminals: TerminalResponse[] = [];
  terminals: TerminalResponse[] = [];
  filteredTerminals: TerminalResponse[] = [];

  loading = false;

  page = 1;
  pageSize = 10;
  total = 0;

  totalAll = 0;
  activeCount = 0;
  maintenanceCount = 0;
  archivedCount = 0;

  filters = {
    search: '',
    terminalType: '',
    status: ''
  };

  terminalTypeOptions = [
    { value: '', label: 'All Terminal Types' },
    { value: 'ATM', label: 'ATM' },
    { value: 'CDM', label: 'CDM' },
    { value: 'ATM_CDM', label: 'ATM + CDM' }
  ];

  statusOptions = [
    { value: '', label: 'All Status' },
    { value: 'ACTIVE', label: 'ACTIVE' },
    { value: 'INACTIVE', label: 'INACTIVE' },
    { value: 'MAINTENANCE', label: 'MAINTENANCE' },
    { value: 'OUT_OF_SERVICE', label: 'OUT OF SERVICE' },
    { value: 'ARCHIVED', label: 'ARCHIVED' }
  ];

  constructor(
    private atmApi: AtmTerminalService,
    private router: Router,
    private tableExport: TableExportService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadTerminals();
  }

  loadTerminals(): void {
    this.loading = true;

    this.atmApi.getAll().subscribe({
      next: data => {
        this.allTerminals = data || [];
        this.calculateSummary();
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error('Failed to load ATM/CDM terminals', err);
        this.allTerminals = [];
        this.calculateSummary();
        this.applyFiltersAndPaging(true);
        this.loading = false;

        Swal.fire({
          icon: 'error',
          title: 'Failed to load terminals',
          text: 'Please check backend API and try again.'
        });
      }
    });
  }

  onSearch(): void {
    this.applyFiltersAndPaging(true);
  }

  onReset(): void {
    this.filters = {
      search: '',
      terminalType: '',
      status: ''
    };
    this.applyFiltersAndPaging(true);
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFiltersAndPaging(false);
  }

  onView(terminal: TerminalResponse): void {
    this.router.navigate(['/atm/terminals', terminal.id]);
  }

  onEdit(terminal: TerminalResponse): void {
    this.router.navigate(['/atm/terminals', terminal.id, 'edit']);
  }

  onAddCashBin(terminal: TerminalResponse): void {
    this.router.navigate(['/atm/cash-bin/new'], {
      queryParams: { terminalId: terminal.id }
    });
  }

  onReplenish(terminal: TerminalResponse): void {
    this.router.navigate(['/atm/replenishment/new'], {
      queryParams: { terminalId: terminal.id }
    });
  }

  onReconcile(terminal: TerminalResponse): void {
    this.router.navigate(['/atm/reconciliation/new'], {
      queryParams: { terminalId: terminal.id }
    });
  }

  onArchive(terminal: TerminalResponse): void {
    Swal.fire({
      icon: 'warning',
      title: 'Archive terminal?',
      text: `${terminal.terminalName} will be archived and hidden from active operation.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, archive',
      cancelButtonText: 'Cancel'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.atmApi.archive(terminal.id).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Archived',
            text: 'Terminal archived successfully.',
            timer: 1400,
            showConfirmButton: false
          });

          this.loadTerminals();
        },
        error: err => {
          console.error('Archive failed', err);
          Swal.fire({
            icon: 'error',
            title: 'Archive failed',
            text: err?.error?.message || 'Please check backend validation and try again.'
          });
        }
      });
    });
  }

  onRestore(terminal: TerminalResponse): void {
    Swal.fire({
      icon: 'question',
      title: 'Restore terminal?',
      text: `${terminal.terminalName} will be restored as active terminal.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, restore',
      cancelButtonText: 'Cancel'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.atmApi.restore(terminal.id).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Restored',
            text: 'Terminal restored successfully.',
            timer: 1400,
            showConfirmButton: false
          });

          this.loadTerminals();
        },
        error: err => {
          console.error('Restore failed', err);
          Swal.fire({
            icon: 'error',
            title: 'Restore failed',
            text: err?.error?.message || 'Please check backend API and try again.'
          });
        }
      });
    });
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private applyFiltersAndPaging(resetPage: boolean): void {
    if (resetPage) this.page = 1;

    const search = (this.filters.search || '').trim().toLowerCase();
    const terminalType = (this.filters.terminalType || '').trim();
    const status = (this.filters.status || '').trim();

    const filtered = this.allTerminals.filter(t => {
      const matchSearch =
        !search ||
        (t.terminalCode || '').toLowerCase().includes(search) ||
        (t.terminalName || '').toLowerCase().includes(search) ||
        (t.ipAddress || '').toLowerCase().includes(search) ||
        (t.serialNo || '').toLowerCase().includes(search) ||
        (t.vendorName || '').toLowerCase().includes(search) ||
        String(t.branchId || '').toLowerCase().includes(search);

      const matchType = !terminalType || t.terminalType === terminalType;
      const matchStatus = !status || t.status === status;

      return matchSearch && matchType && matchStatus;
    });

    this.filteredTerminals = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.terminals = filtered.slice(start, start + this.pageSize);
  }

  private calculateSummary(): void {
    this.totalAll = this.allTerminals.length;
    this.activeCount = this.allTerminals.filter(t => t.status === 'ACTIVE').length;
    this.maintenanceCount = this.allTerminals.filter(t => t.status === 'MAINTENANCE').length;
    this.archivedCount = this.allTerminals.filter(t => t.status === 'ARCHIVED').length;
  }

  onPrint(): void {
    const data = this.getExportableTerminals();

    if (!data || data.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'No data',
        text: 'No terminal data to print.'
      });
      return;
    }

    this.tableExport.printTableDocument(
      'ATM/CDM Terminal List',
      'Al-Barakah Shariah Banking Management System',
      ['Terminal Code', 'Terminal Name', 'Type', 'Branch ID', 'IP Address', 'Serial No', 'Vendor', 'Install Date', 'Status'],
      data.map(t => [
        t.terminalCode,
        t.terminalName,
        t.terminalType,
        String(t.branchId ?? ''),
        t.ipAddress || '',
        t.serialNo || '',
        t.vendorName || '',
        t.installDate || '',
        t.status
      ])
    );
  }

  onExport(type: 'csv' | 'excel' | 'pdf'): void {
    const data = this.getExportableTerminals();

    if (!data || data.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'No data',
        text: 'No terminal data to export.'
      });
      return;
    }

    this.tableExport.exportTable(
      'ATM Terminals',
      'atm-terminal-list',
      ['Terminal Code', 'Terminal Name', 'Type', 'Branch ID', 'Location', 'IP Address', 'Serial No', 'Vendor', 'Install Date', 'Status'],
      data.map(t => [
        t.terminalCode,
        t.terminalName,
        t.terminalType,
        String(t.branchId ?? ''),
        t.locationNote || '',
        t.ipAddress || '',
        t.serialNo || '',
        t.vendorName || '',
        t.installDate || '',
        t.status
      ]),
      type
    );
  }

  private getExportableTerminals(): TerminalResponse[] {
    const hasActiveFilter =
      !!this.filters.search?.trim() ||
      !!this.filters.terminalType?.trim() ||
      !!this.filters.status?.trim();

    return hasActiveFilter ? this.filteredTerminals : this.allTerminals;
  }

}
