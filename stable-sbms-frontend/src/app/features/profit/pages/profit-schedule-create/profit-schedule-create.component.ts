import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccountResponse } from '../../../accounts/models/account.model';
import { AccountService } from '../../../accounts/services/account.service';
import { PROFIT_FREQUENCY_OPTIONS, ProfitScheduleRequest } from '../../models/profit.model';
import { ProfitService } from '../../services/profit.service';

@Component({
  selector: 'app-profit-schedule-create',
  templateUrl: './profit-schedule-create.component.html',
  styleUrls: ['./profit-schedule-create.component.scss']
})
export class ProfitScheduleCreateComponent implements OnInit {

  loading = false;
  saving = false;
  accounts: AccountResponse[] = [];
  accountTypeId: number | null = null;

  frequencies = PROFIT_FREQUENCY_OPTIONS;

  form: ProfitScheduleRequest = {
    accountId: null,
    profitFrequency: 'MONTHLY',
    nextPostingDate: new Date().toISOString().slice(0, 10)
  };

  constructor(
    private accountApi: AccountService,
    private profitApi: ProfitService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const queryAccountId = this.route.snapshot.queryParamMap.get('accountId');
    const queryAccountTypeId = this.route.snapshot.queryParamMap.get('accountTypeId');
    if (queryAccountId) {
      this.form.accountId = Number(queryAccountId);
    }
    if (queryAccountTypeId) {
      this.accountTypeId = Number(queryAccountTypeId);
    }
    this.loadAccounts();
  }

  get filteredAccounts(): AccountResponse[] {
    return this.accounts.filter(item => {
      const matchesType = !this.accountTypeId || item.accountTypeId === this.accountTypeId;
      return matchesType && item.accountStatus === 'ACTIVE';
    });
  }

  loadAccounts(): void {
    this.loading = true;
    this.accountApi.getAccounts().subscribe({
      next: accounts => {
        this.accounts = accounts || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load account options.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.form.accountId || !this.form.profitFrequency || !this.form.nextPostingDate) {
      Swal.fire('Warning', 'Account, frequency and next posting date are required.', 'warning');
      return;
    }
    this.saving = true;
    this.profitApi.createSchedule(this.form).subscribe({
      next: response => {
        this.saving = false;
        Swal.fire('Success', 'Profit schedule created successfully.', 'success');
        this.router.navigate(['/profit/schedules', response.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to create profit schedule.', 'error');
      }
    });
  }

  getAccountLabel(item: AccountResponse): string {
    return `${item.accountNumber} - ${item.customerName} - ${item.accountTypeCode}`;
  }
}
