import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FINANCING_TYPE_OPTIONS, FinancingProductRequest } from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-financing-product-edit',
  templateUrl: './financing-product-edit.component.html',
  styleUrls: ['./financing-product-edit.component.scss']
})
export class FinancingProductEditComponent implements OnInit {

  id = 0;
  loading = false;
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
        this.form = {
          productCode: data.productCode,
          productName: data.productName,
          financingType: data.financingType,
          minimumAmount: data.minimumAmount,
          maximumAmount: data.maximumAmount,
          tenureMonths: data.tenureMonths,
          profitRule: data.profitRule
        };
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load financing product.', 'error');
      }
    });
  }

  save(): void {
    this.saving = true;
    this.financingService.updateProduct(this.id, this.form).subscribe({
      next: data => {
        this.saving = false;
        Swal.fire('Success', 'Financing product updated successfully.', 'success');
        this.router.navigate(['/financing/products', data.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to update financing product.', 'error');
      }
    });
  }
}
