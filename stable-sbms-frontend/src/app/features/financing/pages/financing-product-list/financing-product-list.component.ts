import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FinancingProductResponse, formatEnumLabel } from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-financing-product-list',
  templateUrl: './financing-product-list.component.html',
  styleUrls: ['./financing-product-list.component.scss']
})
export class FinancingProductListComponent implements OnInit {

  loading = false;
  allItems: FinancingProductResponse[] = [];
  items: FinancingProductResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  filters = {
    search: '',
    status: '',
    financingType: ''
  };

  constructor(
    private financingService: FinancingService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.financingService.getProducts().subscribe({
      next: data => {
        this.allItems = data;
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load financing products.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.productCode.toLowerCase().includes(keyword)
        || item.productName.toLowerCase().includes(keyword)
        || item.profitRule.toLowerCase().includes(keyword);
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      const matchesType = !this.filters.financingType || item.financingType === this.filters.financingType;
      return matchesKeyword && matchesStatus && matchesType;
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
    this.filters = { search: '', status: '', financingType: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  onView(item: FinancingProductResponse): void {
    this.router.navigate(['/financing/products', item.id]);
  }

  onEdit(item: FinancingProductResponse): void {
    this.router.navigate(['/financing/products', item.id, 'edit']);
  }

  onApplications(item: FinancingProductResponse): void {
    this.router.navigate(['/financing/applications'], { queryParams: { productId: item.id } });
  }

  openCalculator(item: FinancingProductResponse): void {
    this.router.navigate(['/calculations/simulator'], {
      queryParams: {
        sourceModule: 'FINANCING',
        productType: item.financingType,
        principalAmount: item.minimumAmount,
        ratePercent: this.extractRate(item.profitRule) ?? 0,
        tenureMonths: item.tenureMonths,
        frequency: 'MONTHLY',
        productId: item.id,
        sourceName: `${item.productCode} - ${item.productName}`,
        returnRoute: '/financing/products'
      }
    });
  }

  toggleArchive(item: FinancingProductResponse): void {
    const request$ = item.status === 'ARCHIVED'
      ? this.financingService.restoreProduct(item.id)
      : this.financingService.archiveProduct(item.id);

    request$.subscribe({
      next: () => {
        Swal.fire('Success', `Financing product ${item.status === 'ARCHIVED' ? 'restored' : 'archived'} successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', 'Failed to change financing product status.', 'error');
      }
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  private extractRate(value?: string | null): number | null {
    const match = String(value || '').match(/(\d+(?:\.\d+)?)/);
    return match ? Number(match[1]) : null;
  }

  get activeCount(): number {
    return this.allItems.filter(item => item.status === 'ACTIVE').length;
  }

  get archivedCount(): number {
    return this.allItems.filter(item => item.status === 'ARCHIVED').length;
  }

  get totalApplications(): number {
    return this.allItems.reduce((sum, item) => sum + (item.applicationCount || 0), 0);
  }
}
