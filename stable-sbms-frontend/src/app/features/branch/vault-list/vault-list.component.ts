import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { BranchApiService } from '../services/branch-api.service';
import { VaultBalanceApiService } from '../services/vault-balance-api.service';

import { BranchResponse } from '../models/branch.model';
import { VaultBalanceResponse } from '../models/vault-balance.model';

@Component({
  selector: 'app-vault-list',
  templateUrl: './vault-list.component.html',
  styleUrls: ['./vault-list.component.scss']
})
export class VaultListComponent implements OnInit {

  branches: BranchResponse[] = [];
  allVaults: VaultBalanceResponse[] = [];
  vaults: VaultBalanceResponse[] = [];
  filteredVaults: VaultBalanceResponse[] = [];

  loading = false;

  page = 1;
  pageSize = 10;
  total = 0;

  filters = {
    search: '',
    status: '',
    closed: ''
  };

  statusOptions = [
    { value: '', label: 'All Status' },
    { value: 'ACTIVE', label: 'ACTIVE' },
    { value: 'CLOSED', label: 'CLOSED' }
  ];

  closedOptions = [
    { value: '', label: 'All Vaults' },
    { value: 'false', label: 'Open Vaults' },
    { value: 'true', label: 'Closed Vaults' }
  ];

  constructor(
    private router: Router,
    private branchApi: BranchApiService,
    private vaultApi: VaultBalanceApiService
  ) { }

  ngOnInit(): void {
    this.loadBranches();
    this.loadVaults();
  }

  private safe(value: any): string {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  loadBranches(): void {
    this.branchApi.getAll('', 'ACTIVE').subscribe({
      next: data => this.branches = data || [],
      error: () => this.branches = []
    });
  }

  loadVaults(): void {
    this.loading = true;

    const isClosed =
      this.filters.closed === ''
        ? null
        : this.filters.closed === 'true';

    this.vaultApi.getAll(null, this.filters.status, isClosed).subscribe({
      next: data => {
        this.allVaults = data || [];
        this.applyFiltersAndPaging(true);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.allVaults = [];
        this.applyFiltersAndPaging(true);
        this.loading = false;
        Swal.fire('Error', 'Failed to load vault balances.', 'error');
      }
    });
  }

  onSearch(): void {
    this.loadVaults();
  }

  onReset(): void {
    this.filters = { search: '', status: '', closed: '' };
    this.loadVaults();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFiltersAndPaging(false);
  }

  openVault(): void {
    this.router.navigate(['/branches/vault/open']);
  }

  viewVault(item: VaultBalanceResponse): void {
    this.router.navigate(['/branches/vault', item.id]);
  }

  closeVault(item: VaultBalanceResponse): void {
    Swal.fire({
      title: 'Close Vault',
      html: `
        <input id="cashIn" type="number" min="0" class="swal2-input" placeholder="Total Cash In">
        <input id="cashOut" type="number" min="0" class="swal2-input" placeholder="Total Cash Out">
        <textarea id="remarks" class="swal2-textarea" placeholder="Closing remarks"></textarea>
      `,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Close Vault',
      cancelButtonText: 'Cancel',
      preConfirm: () => {
        const cashIn = Number((document.getElementById('cashIn') as HTMLInputElement).value || 0);
        const cashOut = Number((document.getElementById('cashOut') as HTMLInputElement).value || 0);
        const remarks = (document.getElementById('remarks') as HTMLTextAreaElement).value || '';

        if (cashIn < 0 || cashOut < 0) {
          Swal.showValidationMessage('Cash in/out cannot be negative.');
          return;
        }

        const closingBalance = Number(item.openingBalance || 0) + cashIn - cashOut;

        if (closingBalance < 0) {
          Swal.showValidationMessage('Closing balance cannot be negative.');
          return;
        }

        return { totalCashIn: cashIn, totalCashOut: cashOut, remarks };
      }
    }).then(result => {
      if (!result.isConfirmed || !result.value) return;

      this.vaultApi.close(item.id, result.value).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Vault Closed',
            text: 'Vault closed and ledger updated successfully.',
            timer: 1400,
            showConfirmButton: false
          });
          this.loadVaults();
        },
        error: err => {
          Swal.fire({
            icon: 'error',
            title: 'Close failed',
            text: err?.error?.message || 'Please check vault close rules and try again.'
          });
        }
      });
    });
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

    const filtered = this.allVaults.filter(item => {
      const branchName = this.getBranchName(item.branchId).toLowerCase();
      const branchCode = this.getBranchCode(item.branchId).toLowerCase();

      return !keyword ||
        branchName.includes(keyword) ||
        branchCode.includes(keyword) ||
        String(item.branchId).includes(keyword) ||
        String(item.balanceDate || '').includes(keyword);
    });

    this.filteredVaults = filtered;
    this.total = filtered.length;

    const start = (this.page - 1) * this.pageSize;
    this.vaults = filtered.slice(start, start + this.pageSize);
  }

  onExport(type: 'csv' | 'excel' | 'pdf'): void {
    const data = this.getExportableVaults();

    if (!data.length) {
      Swal.fire('No data', 'No vault data to export.', 'warning');
      return;
    }

    if (type === 'csv') this.exportCSV(data);
    if (type === 'excel') this.exportExcel(data);
    if (type === 'pdf') this.exportPDF(data);
  }

  private getExportableVaults(): VaultBalanceResponse[] {
    const hasFilter =
      !!this.filters.search?.trim() ||
      !!this.filters.status?.trim() ||
      !!this.filters.closed?.trim();

    return hasFilter ? this.filteredVaults : this.allVaults;
  }

  private exportCSV(data: VaultBalanceResponse[]): void {
    const headers = [
      'Branch Code',
      'Branch Name',
      'Balance Date',
      'Opening Balance',
      'Total Cash In',
      'Total Cash Out',
      'Closing Balance',
      'Is Closed',
      'Closed By',
      'Closed At',
      'Status'
    ];

    const rows = data.map(item => [
      this.getBranchCode(item.branchId),
      this.getBranchName(item.branchId),
      item.balanceDate,
      this.formatAmount(item.openingBalance),
      this.formatAmount(item.totalCashIn),
      this.formatAmount(item.totalCashOut),
      this.formatAmount(item.closingBalance),
      item.isClosed ? 'YES' : 'NO',
      item.closedBy || '',
      item.closedAt || '',
      item.status
    ]);

    const csvContent = [headers, ...rows]
      .map(row => row.map(value => `"${String(value ?? '').replace(/"/g, '""')}"`).join(','))
      .join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'vault-balance-list.csv';
    link.click();

    Swal.fire('Exported', 'CSV file downloaded.', 'success');
  }

  private exportExcel(data: VaultBalanceResponse[]): void {
    import('xlsx').then(xlsx => {
      const rows = data.map(item => ({
        'Branch Code': this.getBranchCode(item.branchId),
        'Branch Name': this.getBranchName(item.branchId),
        'Balance Date': item.balanceDate,
        'Opening Balance': this.formatAmount(item.openingBalance),
        'Total Cash In': this.formatAmount(item.totalCashIn),
        'Total Cash Out': this.formatAmount(item.totalCashOut),
        'Closing Balance': this.formatAmount(item.closingBalance),
        'Is Closed': item.isClosed ? 'YES' : 'NO',
        'Closed By': item.closedBy || '',
        'Closed At': item.closedAt || '',
        'Status': item.status
      }));

      const worksheet = xlsx.utils.json_to_sheet(rows);
      const workbook = { Sheets: { 'Vault Balances': worksheet }, SheetNames: ['Vault Balances'] };

      const excelBuffer = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });
      const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });

      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = 'vault-balance-list.xlsx';
      link.click();

      Swal.fire('Exported', 'Excel file downloaded.', 'success');
    });
  }

  private exportPDF(data: VaultBalanceResponse[]): void {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(() => {
        const doc = new jsPDF.default('l');

        const rows = data.map(item => [
          this.getBranchCode(item.branchId),
          this.getBranchName(item.branchId),
          item.balanceDate,
          this.formatAmount(item.openingBalance),
          this.formatAmount(item.totalCashIn),
          this.formatAmount(item.totalCashOut),
          this.formatAmount(item.closingBalance),
          item.isClosed ? 'YES' : 'NO',
          item.status
        ]);

        (doc as any).autoTable({
          head: [[
            'Branch Code',
            'Branch Name',
            'Date',
            'Opening',
            'Cash In',
            'Cash Out',
            'Closing',
            'Closed',
            'Status'
          ]],
          body: rows,
          styles: { fontSize: 8 }
        });

        doc.save('vault-balance-list.pdf');
        Swal.fire('Exported', 'PDF downloaded.', 'success');
      });
    });
  }
}
