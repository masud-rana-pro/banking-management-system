import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { CharityFundResponse, formatEnumLabel } from '../../models/zakat.model';
import { ZakatService } from '../../services/zakat.service';

@Component({
  selector: 'app-charity-fund-view',
  templateUrl: './charity-fund-view.component.html',
  styleUrls: ['./charity-fund-view.component.scss']
})
export class CharityFundViewComponent implements OnInit {

  loading = false;
  items: CharityFundResponse[] = [];

  constructor(
    private zakatService: ZakatService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.zakatService.getCharityFund().subscribe({
      next: data => {
        this.items = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load charity fund ledger.', 'error');
      }
    });
  }

  openPayout(): void {
    this.router.navigate(['/zakat/payouts/new']);
  }

  print(): void {
    window.print();
  }

  onExport(type: string): void {
    Swal.fire('Export', `Charity fund ${type.toUpperCase()} export is available from print-friendly browser output.`, 'info');
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  get currentBalance(): number {
    return this.items.length ? this.items[0].balanceAfter : 0;
  }
}
