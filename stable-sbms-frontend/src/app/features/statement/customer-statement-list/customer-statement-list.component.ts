import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from '../../customer/models/customer.model';
import { CustomerService } from '../../customer/services/customer.service';
import { CustomerStatementRequestResponse, formatEnumLabel } from '../models/statement.model';
import { StatementService } from '../services/statement.service';

@Component({
  selector: 'app-customer-statement-list',
  templateUrl: './customer-statement-list.component.html',
  styleUrls: ['./customer-statement-list.component.scss']
})
export class CustomerStatementListComponent implements OnInit {

  loading = false;
  allItems: CustomerStatementRequestResponse[] = [];
  items: CustomerStatementRequestResponse[] = [];
  filters = {
    search: '',
    status: ''
  };
  page = 1;
  pageSize = 10;
  total = 0;
  customerImageMap: Record<number, string> = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private statementApi: StatementService,
    private customerApi: CustomerService,
    public accessControl: AccessControlService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.filters.search = this.route.snapshot.queryParamMap.get('customerId') || '';
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      statements: this.statementApi.getCustomerStatements(),
      customers: this.customerApi.getAll()
    }).subscribe({
      next: ({ statements, customers }: { statements: CustomerStatementRequestResponse[]; customers: CustomerResponse[] }) => {
        this.allItems = statements || [];
        this.customerImageMap = this.buildCustomerImageMap(customers || []);
        this.applyFilters();
        this.loading = false;
      },
      error: (err: any) => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load customer statement requests.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.requestNo.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.accountNumber.toLowerCase().includes(keyword)
        || String(item.customerId) === keyword;
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

  onView(item: CustomerStatementRequestResponse): void {
    this.router.navigate(['/statement/customer', item.id]);
  }

  onPrint(): void {
    window.print();
  }

  onExport(format: string): void {
    const exportType = format.toUpperCase() === 'PDF' ? 'PDF' : (format.toUpperCase() === 'EXCEL' ? 'EXCEL' : 'CSV');
    this.statementApi.exportCustomerStatements(exportType, this.filters.search, this.filters.status).subscribe({
      next: response => this.saveBlob(response.body, this.resolveResponseFileName(response, `customer-statements.${exportType === 'PDF' ? 'pdf' : (exportType === 'EXCEL' ? 'xlsx' : 'csv')}`)),
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to export customer statement register.', 'error')
    });
  }

  onDownload(item: CustomerStatementRequestResponse): void {
    this.statementApi.downloadCustomerStatement(item.id).subscribe({
      next: response => this.saveBlob(response.body, this.resolveResponseFileName(response, item.generatedFile?.originalFileName || `${item.requestNo}.pdf`)),
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to download statement.', 'error')
    });
  }

  onPrintFile(item: CustomerStatementRequestResponse): void {
    this.statementApi.previewCustomerStatement(item.id).subscribe({
      next: response => this.openBlob(response.body, true),
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to open printable statement.', 'error')
    });
  }

  onPreview(item: CustomerStatementRequestResponse): void {
    this.statementApi.previewCustomerStatement(item.id).subscribe({
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

  hasCustomerImage(customerId?: number | null): boolean {
    return !!(customerId && this.customerImageMap[customerId]);
  }

  getImageUrl(customerId?: number | null): string {
    return this.fileUploadService.resolveImageUrl(customerId ? this.customerImageMap[customerId] : '');
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, item) => {
      acc[item.id] = item.profileImageName || '';
      return acc;
    }, {} as Record<number, string>);
  }
}
