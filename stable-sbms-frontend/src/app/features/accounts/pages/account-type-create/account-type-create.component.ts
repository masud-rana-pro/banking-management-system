import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import {
  AccountTypeRequest,
  CURRENCY_OPTIONS,
  RECORD_STATUS_OPTIONS,
  SHARIAH_CONTRACT_OPTIONS
} from '../../models/account.model';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-account-type-create',
  templateUrl: './account-type-create.component.html',
  styleUrls: ['./account-type-create.component.scss']
})
export class AccountTypeCreateComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;

  currencyOptions = CURRENCY_OPTIONS;
  shariahOptions = SHARIAH_CONTRACT_OPTIONS;
  recordStatusOptions = RECORD_STATUS_OPTIONS;

  form: AccountTypeRequest = {
    typeCode: '',
    typeName: '',
    shariahContractType: '',
    currencyCode: 'BDT',
    minimumOpeningBalance: 0,
    profitApplicable: true,
    withdrawalAllowed: true,
    status: 'ACTIVE'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountApi: AccountService
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
    this.accountApi.getAccountTypeById(id).subscribe({
      next: item => {
        this.form = {
          typeCode: item.typeCode || '',
          typeName: item.typeName || '',
          shariahContractType: item.shariahContractType || '',
          currencyCode: item.currencyCode || 'BDT',
          minimumOpeningBalance: item.minimumOpeningBalance ?? 0,
          profitApplicable: !!item.profitApplicable,
          withdrawalAllowed: !!item.withdrawalAllowed,
          status: item.status || 'ACTIVE'
        };
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load account type.', 'error');
      }
    });
  }

  save(): void {
    if (!this.form.typeName.trim() || !this.form.shariahContractType || !this.form.currencyCode.trim()) {
      Swal.fire('Validation', 'Type name, contract and currency are required.', 'warning');
      return;
    }

    this.saving = true;
    const request: AccountTypeRequest = {
      ...this.form,
      typeCode: this.form.typeCode.trim(),
      typeName: this.form.typeName.trim(),
      currencyCode: this.form.currencyCode.trim().toUpperCase(),
      minimumOpeningBalance: Number(this.form.minimumOpeningBalance || 0)
    };

    const action$ = this.id
      ? this.accountApi.updateAccountType(this.id, request)
      : this.accountApi.createAccountType(request);

    action$.subscribe({
      next: res => {
        this.saving = false;
        Swal.fire('Success', `Account type ${this.id ? 'updated' : 'created'} successfully.`, 'success');
        this.router.navigate(['/accounts/account-types', res.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save account type.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate([this.id ? `/accounts/account-types/${this.id}` : '/accounts/account-types']);
  }
}
