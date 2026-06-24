import { Component } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FINANCING_TYPE_OPTIONS, FinancingProductRequest } from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-financing-product-create',
  templateUrl: './financing-product-create.component.html',
  styleUrls: ['./financing-product-create.component.scss']
})
export class FinancingProductCreateComponent {

  saving = false;
  financingTypes = FINANCING_TYPE_OPTIONS;
  form: FinancingProductRequest = {
    productCode: '',
    productName: '',
    financingType: '',
    minimumAmount: null,
    maximumAmount: null,
    tenureMonths: null,
    profitRule: ''
  };

  constructor(
    private financingService: FinancingService,
    private router: Router
  ) {}

  save(submitAfterCreate = false): void {
    this.saving = true;
    this.financingService.createProduct(this.form).subscribe({
      next: data => {
        this.saving = false;
        Swal.fire('Success', 'Financing product created successfully.', 'success');
        if (submitAfterCreate) {
          this.router.navigate(['/financing/applications/new'], { queryParams: { productId: data.id } });
          return;
        }
        this.router.navigate(['/financing/products', data.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to create financing product.', 'error');
      }
    });
  }
}
