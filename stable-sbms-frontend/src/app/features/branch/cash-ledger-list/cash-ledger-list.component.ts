import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';

import { BranchApiService } from '../services/branch-api.service';
import { BranchCashLedgerApiService } from '../services/branch-cash-ledger-api.service';

import { BranchResponse } from '../models/branch.model';
import { BranchCashLedgerResponse } from '../models/branch-cash-ledger.model';

@Component({
  selector: 'app-cash-ledger-list',
  templateUrl: './cash-ledger-list.component.html',
  styleUrls: ['./cash-ledger-list.component.scss']
})
export class CashLedgerListComponent implements OnInit {

  branches: BranchResponse[] = [];
  allLedgers: BranchCashLedgerResponse[] = [];
  ledgers: BranchCashLedgerResponse[] = [];
  filteredLedgers: BranchCashLedgerResponse[] = [];

  loading = false;

  page = 1;
  pageSize = 10;
  total = 0;

  filters = {
    search: '',
    branchId: '',
    entryType: '',
    sourceType: ''
  };

  entryTypeOptions = [
    { value: '', label: 'All Entry Types' },
    { value: 'DEBIT', label: 'DEBIT' },
    { value: 'CREDIT', label: 'CREDIT' }
  ];

  sourceTypeOptions = [
    { value: '', label: 'All Sources' },
    { value: 'VAULT_OPENING', label: 'VAULT_OPENING' },
    { value: 'VAULT_CLOSING', label: 'VAULT_CLOSING' }
  ];

  constructor(
    private branchApi: BranchApiService,
    private ledgerApi: BranchCashLedgerApiService
  ) { }

  ngOnInit(): void {
    this.loadBranches();
    this.loadLedgers();
  }

  loadBranches(): void {
    this.branchApi.getAll('', 'ACTIVE').subscribe({
      next: data => this.branches = data || [],
      error: () => this.branches = []
    });
  }

  loadLedgers(): void {
    this.loading = true;

    const branchId = this.filters.branchId ? Number(this.filters.branchId) : null;

    this.ledgerApi.getAll(branchId, this.filters.entryType, this.filters.sourceType).subscribe({
      next: data => {
        this.allLedgers = data || [];
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.allLedgers = [];
        this.applyFiltersAndPaging(true);
        this.loading = false;
        Swal.fire('Error', 'Failed to load branch cash ledger.', 'error');
      }
    });
  }

  onSearch(): void {
    this.loadLedgers();
  }

  onReset(): void {
    this.filters = {
      search: '',
      branchId: '',
      entryType: '',
      sourceType: ''
    };
    this.loadLedgers();
  }
  private safe(value: any): string {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFiltersAndPaging(false);
  }

  previewReport(): void {
    window.open(this.ledgerApi.getReportPreviewUrl(
      this.filters.branchId ? Number(this.filters.branchId) : null,
      this.filters.entryType,
      this.filters.sourceType
    ), '_blank');
  }

  downloadReport(): void {
    const link = document.createElement('a');
    link.href = this.ledgerApi.getReportDownloadUrl(
      this.filters.branchId ? Number(this.filters.branchId) : null,
      this.filters.entryType,
      this.filters.sourceType
    );
    link.target = '_blank';
    link.rel = 'noopener';
    link.click();
  }

  printReport(): void {
    this.previewReport();
  }

  getBranchName(branchId: number): string {
    return this.branches.find(b => b.id === branchId)?.branchName || `Branch #${branchId}`;
  }

  getBranchCode(branchId: number): string {
    return this.branches.find(b => b.id === branchId)?.branchCode || String(branchId);
  }

  formatAmount(value: number): string {
    return Number(value || 0).toLocaleString('en-BD', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  private applyFiltersAndPaging(resetPage: boolean): void {
    if (resetPage) this.page = 1;

    const keyword = (this.filters.search || '').trim().toLowerCase();

    const filtered = this.allLedgers.filter(item => {
      const branchName = this.getBranchName(item.branchId).toLowerCase();
      const branchCode = this.getBranchCode(item.branchId).toLowerCase();

      return !keyword ||
        branchName.includes(keyword) ||
        branchCode.includes(keyword) ||
        String(item.branchId).includes(keyword) ||
        (item.referenceNo || '').toLowerCase().includes(keyword) ||
        (item.ledgerDate || '').includes(keyword) ||
        (item.entryType || '').toLowerCase().includes(keyword) ||
        (item.sourceType || '').toLowerCase().includes(keyword);
    });

    this.filteredLedgers = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.ledgers = filtered.slice(start, start + this.pageSize);
  }

  onExport(type: 'csv' | 'excel' | 'pdf'): void {
    const data = this.getExportableLedgers();

    if (!data.length) {
      Swal.fire('No data', 'No cash ledger data to export.', 'warning');
      return;
    }

    if (type === 'csv') this.exportCSV(data);
    if (type === 'excel') this.exportExcel(data);
    if (type === 'pdf') this.exportPDF(data);
  }

  private getExportableLedgers(): BranchCashLedgerResponse[] {
    const hasFilter =
      !!this.filters.search?.trim() ||
      !!this.filters.branchId?.trim() ||
      !!this.filters.entryType?.trim() ||
      !!this.filters.sourceType?.trim();

    return hasFilter ? this.filteredLedgers : this.allLedgers;
  }

  private exportCSV(data: BranchCashLedgerResponse[]): void {
    const headers = [
      'Branch Code',
      'Branch Name',
      'Ledger Date',
      'Entry Type',
      'Source Type',
      'Reference No',
      'Debit Amount',
      'Credit Amount',
      'Balance After',
      'Remarks',
      'Created By'
    ];

    const rows = data.map(item => [
      this.getBranchCode(item.branchId),
      this.getBranchName(item.branchId),
      item.ledgerDate,
      item.entryType,
      item.sourceType,
      item.referenceNo || '',
      this.formatAmount(item.debitAmount),
      this.formatAmount(item.creditAmount),
      this.formatAmount(item.balanceAfter),
      item.remarks || '',
      item.createdBy || ''
    ]);

    const csvContent = [headers, ...rows]
      .map(row => row.map(value => `"${String(value ?? '').replace(/"/g, '""')}"`).join(','))
      .join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'branch-cash-ledger.csv';
    link.click();

    Swal.fire('Exported', 'CSV file downloaded.', 'success');
  }

  private exportExcel(data: BranchCashLedgerResponse[]): void {
    import('xlsx').then(xlsx => {
      const rows = data.map(item => ({
        'Branch Code': this.getBranchCode(item.branchId),
        'Branch Name': this.getBranchName(item.branchId),
        'Ledger Date': item.ledgerDate,
        'Entry Type': item.entryType,
        'Source Type': item.sourceType,
        'Reference No': item.referenceNo || '',
        'Debit Amount': this.formatAmount(item.debitAmount),
        'Credit Amount': this.formatAmount(item.creditAmount),
        'Balance After': this.formatAmount(item.balanceAfter),
        'Remarks': item.remarks || '',
        'Created By': item.createdBy || ''
      }));

      const worksheet = xlsx.utils.json_to_sheet(rows);
      const workbook = { Sheets: { 'Cash Ledger': worksheet }, SheetNames: ['Cash Ledger'] };

      const excelBuffer = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });
      const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });

      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = 'branch-cash-ledger.xlsx';
      link.click();

      Swal.fire('Exported', 'Excel file downloaded.', 'success');
    });
  }

  private exportPDF(data: BranchCashLedgerResponse[]): void {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(() => {
        const doc = new jsPDF.default('l');

        const rows = data.map(item => [
          this.getBranchCode(item.branchId),
          this.getBranchName(item.branchId),
          item.ledgerDate,
          item.entryType,
          item.sourceType,
          item.referenceNo || '',
          this.formatAmount(item.debitAmount),
          this.formatAmount(item.creditAmount),
          this.formatAmount(item.balanceAfter)
        ]);

        (doc as any).autoTable({
          head: [[
            'Branch Code',
            'Branch Name',
            'Date',
            'Entry',
            'Source',
            'Reference',
            'Debit',
            'Credit',
            'Balance'
          ]],
          body: rows,
          styles: { fontSize: 7 }
        });

        doc.save('branch-cash-ledger.pdf');
        Swal.fire('Exported', 'PDF downloaded.', 'success');
      });
    });
  }

}
