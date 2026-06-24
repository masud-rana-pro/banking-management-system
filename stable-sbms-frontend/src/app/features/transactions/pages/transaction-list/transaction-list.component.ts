import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { TransactionResponse, formatEnumLabel } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-transaction-list',
  templateUrl: './transaction-list.component.html',
  styleUrls: ['./transaction-list.component.scss']
})
export class TransactionListComponent implements OnInit {

  loading = false;
  allItems: TransactionResponse[] = [];
  items: TransactionResponse[] = [];
  branches: BranchResponse[] = [];

  filters = {
    search: '',
    transactionType: '',
    transactionStatus: '',
    branchId: ''
  };

  page = 1;
  pageSize = 10;
  total = 0;

  get depositCount(): number {
    return this.allItems.filter(item => item.transactionType === 'DEPOSIT').length;
  }

  get withdrawalCount(): number {
    return this.allItems.filter(item => item.transactionType === 'WITHDRAWAL').length;
  }

  get transferCount(): number {
    return this.allItems.filter(item => item.transactionType === 'TRANSFER').length;
  }

  constructor(
    private transactionApi: TransactionService,
    private branchApi: BranchApiService,
    private accessControl: AccessControlService,
    private router: Router,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      transactions: this.transactionApi.getTransactions(),
      branches: this.branchApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ transactions, branches }) => {
        this.allItems = transactions || [];
        this.branches = branches || [];
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load transaction list.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.transactionRef.toLowerCase().includes(keyword)
        || String(item.debitAccountNumber || '').toLowerCase().includes(keyword)
        || String(item.creditAccountNumber || '').toLowerCase().includes(keyword)
        || String(item.debitCustomerName || '').toLowerCase().includes(keyword)
        || String(item.creditCustomerName || '').toLowerCase().includes(keyword)
        || String(item.narration || '').toLowerCase().includes(keyword);
      const matchesType = !this.filters.transactionType || item.transactionType === this.filters.transactionType;
      const matchesStatus = !this.filters.transactionStatus || (item.transactionStatus || '') === this.filters.transactionStatus;
      const matchesBranch = !this.filters.branchId || String(item.branchId || '') === this.filters.branchId;
      return matchesKeyword && matchesType && matchesStatus && matchesBranch;
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
    this.filters = { search: '', transactionType: '', transactionStatus: '', branchId: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  onPrint(): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No transaction data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Transaction List',
      'Al-Barakah Shariah Banking Management System',
      ['Reference', 'Type', 'Branch', 'Customer', 'Counterparty', 'Amount', 'Status'],
      data.map(item => [
        item.transactionRef,
        item.transactionType,
        this.getBranchName(item.branchId),
        this.getCustomer(item),
        this.getCounterparty(item),
        String(item.amount ?? ''),
        item.transactionStatus || ''
      ])
    );
  }

  onExport(format: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No transaction data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Transactions',
      'transaction-list',
      ['Reference', 'Type', 'Branch', 'Customer', 'Counterparty', 'Amount', 'Status'],
      data.map(item => [
        item.transactionRef,
        item.transactionType,
        this.getBranchName(item.branchId),
        this.getCustomer(item),
        this.getCounterparty(item),
        String(item.amount ?? ''),
        item.transactionStatus || ''
      ]),
      format as 'csv' | 'excel' | 'pdf'
    );
  }

  onView(item: TransactionResponse): void {
    this.router.navigate(['/transactions', item.id]);
  }

  previewVoucher(item: TransactionResponse): void {
    if (!item?.id) return;
    this.openFallbackVoucherPreview(item);
  }

  downloadVoucher(item: TransactionResponse): void {
    if (!item?.id) return;
    this.transactionApi.downloadVoucherBlob(item.id).subscribe({
      next: blob => {
        const url = URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));
        const link = document.createElement('a');
        link.href = url;
        link.download = `transaction-voucher-${item.transactionRef || item.id}.pdf`;
        document.body.appendChild(link);
        link.click();
        link.remove();
        setTimeout(() => URL.revokeObjectURL(url), 60000);
      },
      error: err => {
        console.error(err);
        this.downloadFallbackVoucher(item);
      }
    });
  }

  onReverse(item: TransactionResponse): void {
    if (!this.can('TRANSACTION_REVERSE')) return;
    this.router.navigate(['/transactions', item.id, 'reverse']);
  }

  openAccount(accountId?: number | null): void {
    if (!accountId) return;
    this.router.navigate(['/accounts', accountId]);
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) return 'Unassigned Branch';
    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getCounterparty(item: TransactionResponse): string {
    if (item.transactionType === 'DEPOSIT' || item.transactionType === 'CHEQUE_CLEARING') {
      return item.creditAccountNumber || '-';
    }
    if (item.transactionType === 'WITHDRAWAL') {
      return item.debitAccountNumber || '-';
    }
    return `${item.debitAccountNumber || '-'} -> ${item.creditAccountNumber || '-'}`;
  }

  getCustomer(item: TransactionResponse): string {
    return item.creditCustomerName || item.debitCustomerName || '-';
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  getActionTitle(label: string, permissionCode?: string): string {
    if (!permissionCode || this.can(permissionCode)) {
      return label;
    }
    return `${label} (No permission)`;
  }

  private getExportableData(): TransactionResponse[] {
    const keyword = this.filters.search.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.transactionRef.toLowerCase().includes(keyword)
        || String(item.debitAccountNumber || '').toLowerCase().includes(keyword)
        || String(item.creditAccountNumber || '').toLowerCase().includes(keyword)
        || String(item.debitCustomerName || '').toLowerCase().includes(keyword)
        || String(item.creditCustomerName || '').toLowerCase().includes(keyword)
        || String(item.narration || '').toLowerCase().includes(keyword);
      const matchesType = !this.filters.transactionType || item.transactionType === this.filters.transactionType;
      const matchesStatus = !this.filters.transactionStatus || (item.transactionStatus || '') === this.filters.transactionStatus;
      const matchesBranch = !this.filters.branchId || String(item.branchId || '') === this.filters.branchId;
      return matchesKeyword && matchesType && matchesStatus && matchesBranch;
    });
  }

  private openFallbackVoucherPreview(item: TransactionResponse): void {
    const blob = new Blob([this.buildFallbackVoucherHtml(item)], { type: 'text/html;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    window.open(url, '_blank');
    setTimeout(() => URL.revokeObjectURL(url), 60000);
  }

  private downloadFallbackVoucher(item: TransactionResponse): void {
    const blob = new Blob([this.buildFallbackVoucherHtml(item)], { type: 'text/html;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `transaction-voucher-${item.transactionRef || item.id}.html`;
    document.body.appendChild(link);
    link.click();
    link.remove();
    setTimeout(() => URL.revokeObjectURL(url), 60000);
  }

  private buildFallbackVoucherHtml(item: TransactionResponse): string {
    const debitParty = this.escapeHtml(item.debitCustomerName || item.debitCustomerCode || '-');
    const creditParty = this.escapeHtml(item.creditCustomerName || item.creditCustomerCode || '-');
    const amount = Number(item.amount || 0).toLocaleString('en-BD', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    return `<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Transaction Voucher ${this.escapeHtml(item.transactionRef || '')}</title>
  <style>
    body{margin:0;background:#eef3f1;font-family:Arial,sans-serif;color:#0f172a;padding:28px;}
    .voucher{max-width:900px;margin:0 auto;background:#fff;border:1px solid #d7e3de;border-radius:18px;overflow:hidden;box-shadow:0 18px 38px rgba(15,23,42,.12);}
    .top{background:#063f30;color:#fff;padding:22px 28px;display:flex;justify-content:space-between;gap:18px;align-items:flex-start;}
    .brand{font-size:22px;font-weight:800}.sub{margin-top:5px;font-size:12px;color:#d7efe7}.ref{padding:9px 14px;border:1px solid rgba(255,255,255,.3);border-radius:999px;font-weight:800;}
    .accent{height:6px;background:linear-gradient(90deg,#0f766e,#d97706,#2563eb);}
    .content{padding:26px 28px;display:grid;gap:18px}.title h1{margin:0;font-size:28px}.title p{margin:7px 0 0;color:#64748b;}
    .grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:12px}.card{border:1px solid #dbe7e2;border-radius:12px;padding:13px;background:#fbfdfc;}
    .card span{display:block;font-size:10px;font-weight:800;letter-spacing:.1em;text-transform:uppercase;color:#64748b;margin-bottom:7px}.card strong{font-size:15px;color:#0f172a;word-break:break-word;}
    .wide{grid-column:span 2}.section-title{font-size:15px;font-weight:800;color:#064e3b;margin:6px 0 0;}
    .footer{background:#063f30;color:#dff7ec;padding:16px 28px;font-size:12px;display:flex;justify-content:space-between;gap:12px;}
    .actions{position:fixed;right:20px;bottom:20px;display:flex;gap:10px}.actions button{border:0;border-radius:9px;padding:10px 14px;background:#0f766e;color:#fff;font-weight:800;cursor:pointer;}
    @media print{body{background:#fff;padding:0}.voucher{box-shadow:none;border-radius:0}.actions{display:none}}
  </style>
</head>
<body>
  <div class="voucher">
    <div class="top">
      <div><div class="brand">Al-Barakah Shariah Bank PLC</div><div class="sub">Islamic Banking Document & Report Center</div></div>
      <div class="ref">${this.escapeHtml(item.transactionRef || `TXN-${item.id}`)}</div>
    </div>
    <div class="accent"></div>
    <div class="content">
      <div class="title"><h1>${this.escapeHtml(this.getLabel(item.transactionType))} Voucher</h1><p>System generated transaction evidence for branch operation, audit and customer support.</p></div>
      <div class="grid">
        <div class="card"><span>Amount</span><strong>BDT ${amount}</strong></div>
        <div class="card"><span>Status</span><strong>${this.escapeHtml(this.getLabel(item.transactionStatus))}</strong></div>
        <div class="card"><span>Date</span><strong>${this.escapeHtml(this.formatVoucherDate(item.transactionDate))}</strong></div>
        <div class="card"><span>Branch</span><strong>${this.escapeHtml(this.getBranchName(item.branchId))}</strong></div>
      </div>
      <div class="section-title">Account Movement</div>
      <div class="grid">
        <div class="card wide"><span>Debit Account</span><strong>${this.escapeHtml(item.debitAccountNumber || '-')}</strong></div>
        <div class="card wide"><span>Debit Customer</span><strong>${debitParty}</strong></div>
        <div class="card wide"><span>Credit Account</span><strong>${this.escapeHtml(item.creditAccountNumber || '-')}</strong></div>
        <div class="card wide"><span>Credit Customer</span><strong>${creditParty}</strong></div>
      </div>
      <div class="section-title">Audit Detail</div>
      <div class="grid">
        <div class="card wide"><span>Posted By</span><strong>${this.escapeHtml(item.postedBy || '-')}</strong></div>
        <div class="card wide"><span>Approved By</span><strong>${this.escapeHtml(item.approvedBy || '-')}</strong></div>
        <div class="card wide"><span>Narration</span><strong>${this.escapeHtml(item.narration || '-')}</strong></div>
        <div class="card wide"><span>Reversal</span><strong>${item.reversalFlag ? 'YES' : 'NO'}</strong></div>
      </div>
    </div>
    <div class="footer"><span>Generated from SBMS Transaction Journal</span><span>Official Preview / Printable Copy</span></div>
  </div>
  <div class="actions"><button onclick="window.print()">Print / Save PDF</button></div>
</body>
</html>`;
  }

  private formatVoucherDate(value?: string | null): string {
    if (!value) return '-';
    const parsed = new Date(value);
    return Number.isNaN(parsed.getTime()) ? value : parsed.toLocaleString();
  }

  private escapeHtml(value: string): string {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }
}
