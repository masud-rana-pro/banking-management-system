import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { TableExportService } from 'src/app/core/services/table-export.service';
import { AtmTerminalService } from '../../services/atm-terminal.service';
import { DeviceJournalResponse } from '../../models/terminal.model';

@Component({
  selector: 'app-device-journal-list',
  templateUrl: './device-journal-list.component.html',
  styleUrls: ['./device-journal-list.component.scss']
})
export class DeviceJournalListComponent implements OnInit {

  allItems: DeviceJournalResponse[] = [];
  items: DeviceJournalResponse[] = [];
  filteredItems: DeviceJournalResponse[] = [];

  loading = false;
  page = 1;
  pageSize = 10;
  total = 0;

  totalAll = 0;
  setupCount = 0;
  replenishmentCount = 0;
  reconciliationCount = 0;

  filters = {
    search: '',
    eventType: ''
  };

  eventTypeOptions = [
    { value: '', label: 'All Event Type' },
    { value: 'TERMINAL_REGISTERED', label: 'TERMINAL REGISTERED' },
    { value: 'CASH_BIN_CONFIGURED', label: 'CASH BIN CONFIGURED' },
    { value: 'REPLENISHMENT', label: 'REPLENISHMENT' },
    { value: 'RECONCILIATION', label: 'RECONCILIATION' }
  ];

  constructor(
    private atmApi: AtmTerminalService,
    private router: Router,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;

    this.atmApi.getDeviceJournal().subscribe({
      next: data => {
        this.allItems = data || [];
        this.calculateSummary();
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load device journal.', 'error');
      }
    });
  }

  onSearch(): void {
    this.applyFiltersAndPaging(true);
  }

  onReset(): void {
    this.filters = { search: '', eventType: '' };
    this.applyFiltersAndPaging(true);
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFiltersAndPaging(false);
  }

  openTerminal(item: DeviceJournalResponse): void {
    this.router.navigate(['/atm/terminals', item.terminalId]);
  }

  private applyFiltersAndPaging(resetPage: boolean): void {
    if (resetPage) this.page = 1;

    const search = this.filters.search.trim().toLowerCase();
    const eventType = this.filters.eventType.trim();

    const filtered = this.allItems.filter(item => {
      const matchSearch =
        !search ||
        (item.terminalCode || '').toLowerCase().includes(search) ||
        (item.terminalName || '').toLowerCase().includes(search) ||
        (item.referenceNo || '').toLowerCase().includes(search) ||
        (item.status || '').toLowerCase().includes(search);

      const matchEventType = !eventType || item.eventType === eventType;
      return matchSearch && matchEventType;
    });

    this.filteredItems = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  private calculateSummary(): void {
    this.totalAll = this.allItems.length;
    this.setupCount = this.allItems.filter(item => item.eventType === 'TERMINAL_REGISTERED' || item.eventType === 'CASH_BIN_CONFIGURED').length;
    this.replenishmentCount = this.allItems.filter(item => item.eventType === 'REPLENISHMENT').length;
    this.reconciliationCount = this.allItems.filter(item => item.eventType === 'RECONCILIATION').length;
  }

  private getExportableData(): DeviceJournalResponse[] {
    const hasFilter = !!this.filters.search.trim() || !!this.filters.eventType.trim();
    return hasFilter ? this.filteredItems : this.allItems;
  }

  onPrint(): void {
    const data = this.getExportableData();

    if (!data.length) {
      Swal.fire('No data', 'No device journal data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'ATM/CDM Device Journal',
      'Al-Barakah Shariah Banking Management System',
      ['Event Type', 'Terminal Code', 'Terminal Name', 'Reference', 'Amount', 'Status', 'Event Date', 'Remarks'],
      data.map(item => [
        item.eventType,
        item.terminalCode,
        item.terminalName,
        item.referenceNo,
        String(item.amount ?? ''),
        item.status,
        item.eventDate || '',
        item.remarks || ''
      ])
    );
  }

  onExport(type: 'csv' | 'excel' | 'pdf'): void {
    const data = this.getExportableData();

    if (!data.length) {
      Swal.fire('No data', 'No device journal data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'ATM Device Journal',
      'atm-device-journal',
      ['Event Type', 'Terminal Code', 'Terminal Name', 'Reference', 'Amount', 'Status', 'Event Date', 'Remarks'],
      data.map(item => [
        item.eventType,
        item.terminalCode,
        item.terminalName,
        item.referenceNo,
        String(item.amount ?? ''),
        item.status,
        item.eventDate || '',
        item.remarks || ''
      ]),
      type
    );
  }
}
