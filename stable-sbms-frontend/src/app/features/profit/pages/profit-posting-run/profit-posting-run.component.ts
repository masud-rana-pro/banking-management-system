import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';

import { AccountResponse } from '../../../accounts/models/account.model';
import { AccountService } from '../../../accounts/services/account.service';
import { ProfitPostingRunRequest, ProfitPostingRunResponse, ProfitScheduleResponse } from '../../models/profit.model';
import { ProfitService } from '../../services/profit.service';

@Component({
  selector: 'app-profit-posting-run',
  templateUrl: './profit-posting-run.component.html',
  styleUrls: ['./profit-posting-run.component.scss']
})
export class ProfitPostingRunComponent implements OnInit {

  loading = false;
  running = false;
  accounts: AccountResponse[] = [];
  schedules: ProfitScheduleResponse[] = [];
  accountTypeId: number | null = null;
  runResult: ProfitPostingRunResponse | null = null;

  form: ProfitPostingRunRequest = {
    scheduleId: null,
    accountId: null,
    postingDate: new Date().toISOString().slice(0, 10),
    postedBy: ''
  };

  constructor(
    private accountApi: AccountService,
    private profitApi: ProfitService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const queryAccountId = this.route.snapshot.queryParamMap.get('accountId');
    const queryScheduleId = this.route.snapshot.queryParamMap.get('scheduleId');
    const queryAccountTypeId = this.route.snapshot.queryParamMap.get('accountTypeId');
    if (queryAccountId) {
      this.form.accountId = Number(queryAccountId);
    }
    if (queryScheduleId) {
      this.form.scheduleId = Number(queryScheduleId);
    }
    if (queryAccountTypeId) {
      this.accountTypeId = Number(queryAccountTypeId);
    }
    this.loadData();
  }

  get filteredAccounts(): AccountResponse[] {
    return this.accounts.filter(item => (!this.accountTypeId || item.accountTypeId === this.accountTypeId) && item.accountStatus === 'ACTIVE');
  }

  get filteredSchedules(): ProfitScheduleResponse[] {
    return this.schedules.filter(item => {
      const matchesType = !this.accountTypeId || item.accountTypeId === this.accountTypeId;
      const matchesAccount = !this.form.accountId || item.accountId === this.form.accountId;
      return matchesType && matchesAccount && item.status === 'ACTIVE';
    });
  }

  loadData(): void {
    this.loading = true;
    Promise.all([
      this.accountApi.getAccounts().toPromise(),
      this.profitApi.getSchedules().toPromise()
    ]).then(([accounts, schedules]) => {
      this.accounts = accounts || [];
      this.schedules = schedules || [];
      if (this.form.scheduleId) {
        const match = this.schedules.find(item => item.id === this.form.scheduleId);
        if (match) {
          this.form.accountId = match.accountId;
        }
      }
      this.loading = false;
    }).catch(err => {
      console.error(err);
      this.loading = false;
      Swal.fire('Error', 'Failed to load posting run setup.', 'error');
    });
  }

  onScheduleChange(): void {
    const schedule = this.schedules.find(item => item.id === this.form.scheduleId);
    if (schedule) {
      this.form.accountId = schedule.accountId;
    }
  }

  submit(): void {
    this.running = true;
    this.runResult = null;
    this.profitApi.runPosting(this.form).subscribe({
      next: response => {
        this.running = false;
        this.runResult = response;
        Swal.fire('Success', 'Profit posting run completed successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.running = false;
        Swal.fire('Error', err?.error?.message || 'Failed to run profit posting.', 'error');
      }
    });
  }

  getAccountLabel(item: AccountResponse): string {
    return `${item.accountNumber} - ${item.customerName} - ${item.accountTypeCode}`;
  }
}
