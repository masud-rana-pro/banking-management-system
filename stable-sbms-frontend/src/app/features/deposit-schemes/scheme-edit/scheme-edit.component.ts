import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { DEPOSIT_SCHEME_TYPE_OPTIONS, DepositSchemeRequest, PROFIT_FREQUENCY_OPTIONS } from '../models/deposit-scheme.model';
import { DepositSchemeService } from '../services/deposit-scheme.service';

@Component({
  selector: 'app-scheme-edit',
  templateUrl: './scheme-edit.component.html',
  styleUrls: ['./scheme-edit.component.scss']
})
export class SchemeEditComponent implements OnInit {

  id: number | null = null;
  saving = false;
  loading = false;
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
    private route: ActivatedRoute,
    private router: Router,
    private depositSchemeApi: DepositSchemeService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) {
      this.load(this.id);
    }
  }

  load(id: number): void {
    this.loading = true;
    this.depositSchemeApi.getSchemeById(id).subscribe({
      next: item => {
        this.form = {
          schemeCode: item.schemeCode,
          schemeName: item.schemeName,
          schemeType: item.schemeType,
          tenureMonths: item.tenureMonths,
          minimumInstallment: item.minimumInstallment,
          profitRatio: item.profitRatio,
          profitFrequency: item.profitFrequency
        };
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load scheme.', 'error');
      }
    });
  }

  back(): void {
    if (this.id) {
      this.router.navigate(['/deposit-schemes', this.id]);
      return;
    }
    this.router.navigate(['/deposit-schemes/list']);
  }

  submit(): void {
    if (!this.id) return;
    this.saving = true;
    this.depositSchemeApi.updateScheme(this.id, this.form).subscribe({
      next: res => {
        this.saving = false;
        Swal.fire('Updated', 'Deposit scheme updated successfully.', 'success');
        this.router.navigate(['/deposit-schemes', res.id]);
      },
      error: err => {
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to update scheme.', 'error');
      }
    });
  }
}
