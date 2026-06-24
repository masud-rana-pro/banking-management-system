import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from '../../customer/models/customer.model';
import { CustomerService } from '../../customer/services/customer.service';
import { CustomerStatementRequestResponse, formatEnumLabel } from '../models/statement.model';
import { StatementService } from '../services/statement.service';

@Component({
  selector: 'app-customer-statement-view',
  templateUrl: './customer-statement-view.component.html',
  styleUrls: ['./customer-statement-view.component.scss']
})
export class CustomerStatementViewComponent implements OnInit {

  id: number | null = null;
  item: CustomerStatementRequestResponse | null = null;
  loading = false;
  customerProfileImageName = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private statementApi: StatementService,
    private customerApi: CustomerService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) this.load(this.id);
  }

  load(id: number): void {
    this.loading = true;
    this.statementApi.getCustomerStatementById(id).subscribe({
      next: item => {
        this.item = item;
        this.customerApi.getById(item.customerId).pipe(catchError(() => of(null as CustomerResponse | null))).subscribe({
          next: (customer: CustomerResponse | null) => {
            this.customerProfileImageName = customer?.profileImageName || '';
            this.loading = false;
          },
          error: () => {
            this.customerProfileImageName = '';
            this.loading = false;
          }
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load customer statement view.', 'error');
      }
    });
  }

  download(): void {
    if (!this.id || !this.item) return;
    this.statementApi.downloadCustomerStatement(this.id).subscribe({
      next: response => {
        const blob = response.body;
        if (!blob) return;
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = this.resolveResponseFileName(response, this.item?.generatedFile?.originalFileName || `${this.item?.requestNo}.pdf`);
        link.click();
        URL.revokeObjectURL(url);
        if (this.id) this.load(this.id);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to download customer statement.', 'error')
    });
  }

  preview(): void {
    if (!this.id) return;
    this.statementApi.previewCustomerStatement(this.id).subscribe({
      next: response => {
        const blob = response.body;
        if (!blob) return;
        const url = URL.createObjectURL(blob);
        window.open(url, '_blank');
        setTimeout(() => URL.revokeObjectURL(url), 60000);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to preview customer statement.', 'error')
    });
  }

  print(): void {
    if (!this.id) {
      window.print();
      return;
    }
    this.statementApi.previewCustomerStatement(this.id).subscribe({
      next: response => {
        const blob = response.body;
        if (!blob) return;
        const url = URL.createObjectURL(blob);
        const printWindow = window.open(url, '_blank');
        if (printWindow) {
          setTimeout(() => {
            printWindow.focus();
            printWindow.print();
          }, 700);
        }
        setTimeout(() => URL.revokeObjectURL(url), 60000);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to open printable customer statement.', 'error')
    });
  }

  openCustomer(): void {
    if (!this.item) return;
    this.router.navigate(['/customers', this.item.customerId]);
  }

  openAccount(): void {
    if (!this.item) return;
    this.router.navigate(['/accounts', this.item.accountId]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getCustomerImageUrl(): string {
    return this.fileUploadService.resolveImageUrl(this.customerProfileImageName);
  }

  private resolveResponseFileName(response: any, fallback: string): string {
    const disposition = response?.headers?.get?.('content-disposition') || '';
    const match = /filename="?([^";]+)"?/i.exec(disposition);
    return match?.[1] || fallback;
  }
}
