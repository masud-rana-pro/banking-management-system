import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccountResponse } from '../../../accounts/models/account.model';
import { AccountService } from '../../../accounts/services/account.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { WithdrawalRequest } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-cash-withdraw',
  templateUrl: './cash-withdraw.component.html',
  styleUrls: ['./cash-withdraw.component.scss']
})
export class CashWithdrawComponent implements OnInit {

  loading = false;
  saving = false;
  accounts: AccountResponse[] = [];
  branches: BranchResponse[] = [];

  form: WithdrawalRequest = {
    branchId: null,
    terminalId: null,
    debitAccountId: null,
    tellerUserId: 101,
    amount: null,
    narration: '',
    transactionDate: new Date().toISOString().slice(0, 16)
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private transactionApi: TransactionService,
    private accountApi: AccountService,
    private branchApi: BranchApiService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      accounts: this.accountApi.getAccounts(),
      branches: this.branchApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ accounts, branches }) => {
        this.accounts = (accounts || []).filter(item => item.accountStatus === 'ACTIVE');
        this.branches = branches || [];
        const accountId = Number(this.route.snapshot.queryParamMap.get('accountId') || 0);
        if (accountId) {
          const matched = this.accounts.find(item => item.id === accountId);
          if (matched) {
            this.form.debitAccountId = matched.id;
            this.form.branchId = matched.branchId || null;
          }
        }
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load withdrawal form.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.form.debitAccountId || !this.form.branchId || !this.form.amount) {
      Swal.fire('Warning', 'Branch, account and amount are required.', 'warning');
      return;
    }
    this.saving = true;
    this.transactionApi.withdraw(this.form).subscribe({
      next: response => {
        this.saving = false;
        Swal.fire('Success', 'Cash withdrawal posted successfully.', 'success');
        this.router.navigate(['/transactions', response.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to post withdrawal.', 'error');
      }
    });
  }

  getAccountLabel(item: AccountResponse): string {
    return `${item.accountNumber} - ${item.customerName}`;
  }
}
