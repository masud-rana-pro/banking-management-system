import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FinancingProductResponse, formatEnumLabel } from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-financing-product-view',
  templateUrl: './financing-product-view.component.html',
  styleUrls: ['./financing-product-view.component.scss']
})
export class FinancingProductViewComponent implements OnInit {

  id = 0;
  loading = false;
  item: FinancingProductResponse | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private financingService: FinancingService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.loading = true;
    this.financingService.getProductById(this.id).subscribe({
      next: data => {
        this.item = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load financing product view.', 'error');
      }
    });
  }

  toggleArchive(): void {
    if (!this.item) return;
    const request$ = this.item.status === 'ARCHIVED'
      ? this.financingService.restoreProduct(this.item.id)
      : this.financingService.archiveProduct(this.item.id);

    request$.subscribe({
      next: data => {
        this.item = data;
        Swal.fire('Success', `Financing product ${data.status === 'ARCHIVED' ? 'archived' : 'restored'} successfully.`, 'success');
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', 'Failed to change financing product status.', 'error');
      }
    });
  }

  print(): void {
    window.print();
  }

  openEdit(): void {
    this.router.navigate(['/financing/products', this.id, 'edit']);
  }

  openApplications(): void {
    this.router.navigate(['/financing/applications'], { queryParams: { productId: this.id } });
  }

  openCalculator(): void {
    if (!this.item) return;
    this.router.navigate(['/calculations/simulator'], {
      queryParams: {
        sourceModule: 'FINANCING',
        productType: this.item.financingType,
        principalAmount: this.item.minimumAmount,
        ratePercent: this.extractRate(this.item.profitRule) ?? 0,
        tenureMonths: this.item.tenureMonths,
        frequency: 'MONTHLY',
        productId: this.item.id,
        sourceName: `${this.item.productCode} - ${this.item.productName}`,
        returnRoute: `/financing/products/${this.item.id}`
      }
    });
  }

  openNewApplication(): void {
    this.router.navigate(['/financing/applications/new'], { queryParams: { productId: this.id } });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  private extractRate(value?: string | null): number | null {
    const match = String(value || '').match(/(\d+(?:\.\d+)?)/);
    return match ? Number(match[1]) : null;
  }
}
