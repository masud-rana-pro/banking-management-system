import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { BranchStatementRequestResponse, formatEnumLabel } from '../models/statement.model';
import { StatementService } from '../services/statement.service';

@Component({
  selector: 'app-branch-statement-list',
  templateUrl: './branch-statement-list.component.html',
  styleUrls: ['./branch-statement-list.component.scss']
})
export class BranchStatementListComponent implements OnInit {

  loading = false;
  allItems: BranchStatementRequestResponse[] = [];
  items: BranchStatementRequestResponse[] = [];
  filters = { search: '', status: '' };
  page = 1;
  pageSize = 10;
  total = 0;

  constructor(
    private router: Router,
    private statementApi: StatementService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.statementApi.getBranchStatements().subscribe({
      next: items => {
        this.allItems = items || [];
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load branch statement requests.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.requestNo.toLowerCase().includes(keyword)
        || item.branchCode.toLowerCase().includes(keyword)
        || item.branchName.toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.status || item.requestStatus === this.filters.status;
      return matchesKeyword && matchesStatus;
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
    this.filters = { search: '', status: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  onView(item: BranchStatementRequestResponse): void {
    this.router.navigate(['/statement/branch', item.id]);
  }

  onPrint(): void {
    window.print();
  }

  onExport(format: string): void {
    const exportType = format.toUpperCase() === 'PDF' ? 'PDF' : (format.toUpperCase() === 'EXCEL' ? 'EXCEL' : 'CSV');
    this.statementApi.exportBranchStatements(exportType, this.filters.search, this.filters.status).subscribe({
      next: response => this.saveBlob(response.body, this.resolveResponseFileName(response, `branch-statements.${exportType === 'PDF' ? 'pdf' : (exportType === 'EXCEL' ? 'xlsx' : 'csv')}`)),
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to export branch statement register.', 'error')
    });
  }

  onDownload(item: BranchStatementRequestResponse): void {
    this.statementApi.downloadBranchStatement(item.id).subscribe({
      next: response => this.saveBlob(response.body, this.resolveResponseFileName(response, item.generatedFile?.originalFileName || `${item.requestNo}.pdf`)),
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to download statement.', 'error')
    });
  }

  onPrintFile(item: BranchStatementRequestResponse): void {
    this.statementApi.previewBranchStatement(item.id).subscribe({
      next: response => this.openBlob(response.body, true),
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to open printable statement.', 'error')
    });
  }

  onPreview(item: BranchStatementRequestResponse): void {
    this.statementApi.previewBranchStatement(item.id).subscribe({
      next: response => this.openBlob(response.body),
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to preview statement.', 'error')
    });
  }

  private saveBlob(blob: Blob | null, fileName: string): void {
    if (!blob) return;
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    link.click();
    URL.revokeObjectURL(url);
  }

  private resolveResponseFileName(response: any, fallback: string): string {
    const disposition = response?.headers?.get?.('content-disposition') || '';
    const match = /filename="?([^";]+)"?/i.exec(disposition);
    return match?.[1] || fallback;
  }

  private openBlob(blob: Blob | null, printOnLoad = false): void {
    if (!blob) return;
    const url = URL.createObjectURL(blob);
    const previewWindow = window.open(url, '_blank');
    if (previewWindow && printOnLoad) {
      setTimeout(() => {
        previewWindow.focus();
        previewWindow.print();
      }, 700);
    }
    setTimeout(() => URL.revokeObjectURL(url), 60000);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
