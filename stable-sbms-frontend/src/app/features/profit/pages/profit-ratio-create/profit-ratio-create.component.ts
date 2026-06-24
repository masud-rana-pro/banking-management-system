import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccountTypeResponse } from '../../../accounts/models/account.model';
import { AccountService } from '../../../accounts/services/account.service';
import { ProfitRatioRequest } from '../../models/profit.model';
import { ProfitService } from '../../services/profit.service';

@Component({
  selector: 'app-profit-ratio-create',
  templateUrl: './profit-ratio-create.component.html',
  styleUrls: ['./profit-ratio-create.component.scss']
})
export class ProfitRatioCreateComponent implements OnInit {

  loading = false;
  saving = false;
  id: number | null = null;
  accountTypes: AccountTypeResponse[] = [];

  form: ProfitRatioRequest = {
    ratioCode: '',
    accountTypeId: null,
    effectiveFrom: '',
    effectiveTo: '',
    ratioPercent: null
  };

  constructor(
    private profitApi: ProfitService,
    private accountApi: AccountService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  get isEdit(): boolean {
    return !!this.id;
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    const queryTypeId = this.route.snapshot.queryParamMap.get('accountTypeId');
    if (queryTypeId) {
      this.form.accountTypeId = Number(queryTypeId);
    }
    this.form.effectiveFrom = new Date().toISOString().slice(0, 10);

    this.loading = true;
    forkJoin({
      accountTypes: this.accountApi.getAccountTypes(),
      ratio: this.id ? this.profitApi.getRatioById(this.id) : of(null)
    }).subscribe({
      next: ({ accountTypes, ratio }) => {
        this.accountTypes = (accountTypes || []).filter(item => item.profitApplicable);
        if (ratio) {
          this.form = {
            ratioCode: ratio.ratioCode || '',
            accountTypeId: ratio.accountTypeId,
            effectiveFrom: ratio.effectiveFrom || '',
            effectiveTo: ratio.effectiveTo || '',
            ratioPercent: ratio.ratioPercent
          };
        }
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load profit ratio form.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.form.accountTypeId || !this.form.effectiveFrom || !this.form.ratioPercent) {
      Swal.fire('Warning', 'Account type, effective from and ratio percent are required.', 'warning');
      return;
    }
    this.saving = true;
    const request: ProfitRatioRequest = {
      ...this.form,
      effectiveTo: this.form.effectiveTo || ''
    };

    const action$ = this.isEdit && this.id
      ? this.profitApi.updateRatio(this.id, request)
      : this.profitApi.createRatio(request);

    action$.subscribe({
      next: response => {
        this.saving = false;
        Swal.fire('Success', this.isEdit ? 'Profit ratio updated successfully.' : 'Profit ratio created successfully.', 'success');
        this.router.navigate(['/profit/ratios', response.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save profit ratio.', 'error');
      }
    });
  }
}
