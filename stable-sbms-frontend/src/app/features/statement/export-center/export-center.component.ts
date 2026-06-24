import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileReferenceResponse } from '../models/statement.model';
import { StatementService } from '../services/statement.service';

@Component({
  selector: 'app-export-center',
  templateUrl: './export-center.component.html',
  styleUrls: ['./export-center.component.scss']
})
export class ExportCenterComponent implements OnInit {

  loading = false;
  allItems: FileReferenceResponse[] = [];
  items: FileReferenceResponse[] = [];
  filters = { search: '', moduleName: '' };
  page = 1;
  pageSize = 10;
  total = 0;

  constructor(
    private router: Router,
    private statementApi: StatementService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.statementApi.getFiles().subscribe({
      next: files => {
        this.allItems = files || [];
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load export center.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.originalFileName.toLowerCase().includes(keyword)
        || item.fileName.toLowerCase().includes(keyword)
        || item.referenceTable.toLowerCase().includes(keyword)
        || String(item.referenceId) === keyword;
      const matchesModule = !this.filters.moduleName || item.moduleName === this.filters.moduleName;
      return matchesKeyword && matchesModule;
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
    this.filters = { search: '', moduleName: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openRelated(item: FileReferenceResponse): void {
    if (item.referenceTable === 'customer_statement_request') {
      this.router.navigate(['/statement/customer', item.referenceId]);
      return;
    }
    if (item.referenceTable === 'branch_statement_request') {
      this.router.navigate(['/statement/branch', item.referenceId]);
    }
  }

  previewFile(item: FileReferenceResponse): void {
    if (item.referenceTable === 'customer_statement_request') {
      this.statementApi.previewCustomerStatement(item.referenceId).subscribe({
        next: response => this.openBlob(response.body),
        error: err => Swal.fire('Error', err?.error?.message || 'Failed to preview statement file.', 'error')
      });
      return;
    }
    if (item.referenceTable === 'branch_statement_request') {
      this.statementApi.previewBranchStatement(item.referenceId).subscribe({
        next: response => this.openBlob(response.body),
        error: err => Swal.fire('Error', err?.error?.message || 'Failed to preview statement file.', 'error')
      });
    }
  }

  downloadFile(item: FileReferenceResponse): void {
    if (item.referenceTable === 'customer_statement_request') {
      this.statementApi.downloadCustomerStatement(item.referenceId).subscribe({
        next: response => this.saveBlob(response.body, item.originalFileName || item.fileName),
        error: err => Swal.fire('Error', err?.error?.message || 'Failed to download statement file.', 'error')
      });
      return;
    }
    if (item.referenceTable === 'branch_statement_request') {
      this.statementApi.downloadBranchStatement(item.referenceId).subscribe({
        next: response => this.saveBlob(response.body, item.originalFileName || item.fileName),
        error: err => Swal.fire('Error', err?.error?.message || 'Failed to download statement file.', 'error')
      });
    }
  }

  printFile(item: FileReferenceResponse): void {
    if (item.referenceTable === 'customer_statement_request') {
      this.statementApi.previewCustomerStatement(item.referenceId).subscribe({
        next: response => this.openBlob(response.body, true),
        error: err => Swal.fire('Error', err?.error?.message || 'Failed to open printable statement file.', 'error')
      });
      return;
    }
    if (item.referenceTable === 'branch_statement_request') {
      this.statementApi.previewBranchStatement(item.referenceId).subscribe({
        next: response => this.openBlob(response.body, true),
        error: err => Swal.fire('Error', err?.error?.message || 'Failed to open printable statement file.', 'error')
      });
    }
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
}
