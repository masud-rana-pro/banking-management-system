import { Component } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { DEPOSIT_SCHEME_TYPE_OPTIONS, DepositSchemeRequest, PROFIT_FREQUENCY_OPTIONS } from '../models/deposit-scheme.model';
import { DepositSchemeService } from '../services/deposit-scheme.service';

@Component({
  selector: 'app-scheme-create',
  templateUrl: './scheme-create.component.html',
  styleUrls: ['./scheme-create.component.scss']
})
export class SchemeCreateComponent {

  saving = false;
  schemeTypeOptions = DEPOSIT_SCHEME_TYPE_OPTIONS;
  profitFrequencyOptions = PROFIT_FREQUENCY_OPTIONS;

  form: DepositSchemeRequest = {
    schemeCode: '',
    schemeName: '',
    schemeType: '',
    tenureMonths: null,
    minimumInstallment: null,
    profitRatio: null,
    profitFrequency: ''
  };

  constructor(
    private depositSchemeApi: DepositSchemeService,
    private router: Router
  ) {}

  back(): void {
    this.router.navigate(['/deposit-schemes/list']);
  }

  submit(): void {
    this.saving = true;
    this.depositSchemeApi.createScheme(this.form).subscribe({
      next: res => {
        this.saving = false;
        Swal.fire('Created', 'Deposit scheme created successfully.', 'success');
        this.router.navigate(['/deposit-schemes', res.id]);
      },
      error: err => {
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to create scheme.', 'error');
      }
    });
  }
}
