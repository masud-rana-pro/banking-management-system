import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { TableExportService } from 'src/app/core/services/table-export.service';
import { CharityPayoutResponse } from '../../models/zakat.model';
import { ZakatService } from '../../services/zakat.service';

@Component({
  selector: 'app-payout-list',
  templateUrl: './payout-list.component.html',
  styleUrls: ['./payout-list.component.scss']
})
export class PayoutListComponent implements OnInit {

  loading = false;
  items: CharityPayoutResponse[] = [];

  constructor(
    private zakatService: ZakatService,
    private router: Router,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.zakatService.getPayouts().subscribe({
      next: data => {
        this.items = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load charity payouts.', 'error');
      }
    });
  }

  openCreate(): void {
    this.router.navigate(['/zakat/payouts/new']);
  }

  previewReceipt(item: CharityPayoutResponse): void {
    window.open(this.zakatService.getPayoutReceiptPreviewUrl(item.id), '_blank');
  }

  downloadReceipt(item: CharityPayoutResponse): void {
    const link = document.createElement('a');
    link.href = this.zakatService.getPayoutReceiptDownloadUrl(item.id);
    link.target = '_blank';
    link.rel = 'noopener';
    link.click();
  }

  printReceipt(item: CharityPayoutResponse): void {
    this.previewReceipt(item);
  }

  print(): void {
    if (!this.items.length) {
      Swal.fire('No data', 'No payout data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Charity Payout List',
      'Al-Barakah Shariah Banking Management System',
      ['Payout Code', 'Beneficiary', 'Amount', 'Status', 'Paid Date'],
      this.items.map(item => [
        item.beneficiaryCode,
        item.beneficiaryName,
        String(item.amount ?? ''),
        item.status,
        item.payoutDate || ''
      ])
    );
  }

  onExport(type: string): void {
    if (!this.items.length) {
      Swal.fire('No data', 'No payout data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Charity Payouts',
      'charity-payout-list',
      ['Payout Code', 'Beneficiary', 'Amount', 'Status', 'Paid Date'],
      this.items.map(item => [
        item.beneficiaryCode,
        item.beneficiaryName,
        String(item.amount ?? ''),
        item.status,
        item.payoutDate || ''
      ]),
      type as 'csv' | 'excel' | 'pdf'
    );
  }
}
