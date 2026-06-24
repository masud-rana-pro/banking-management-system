import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccountResponse } from '../../../accounts/models/account.model';
import { AccountService } from '../../../accounts/services/account.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { ChequeClearingRequest } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-cheque-clearing',
  templateUrl: './cheque-clearing.component.html',
  styleUrls: ['./cheque-clearing.component.scss']
})
export class ChequeClearingComponent implements OnInit {

  loading = false;
  saving = false;
  accounts: AccountResponse[] = [];
  branches: BranchResponse[] = [];

  form: ChequeClearingRequest = {
    branchId: null,
    creditAccountId: null,
    chequeNo: '',
    draweeBank: '',
    amount: null,
    remarks: '',
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
            this.form.creditAccountId = matched.id;
            this.form.branchId = matched.branchId || null;
          }
        }
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load cheque clearing form.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.form.creditAccountId || !this.form.branchId || !this.form.amount || !this.form.chequeNo.trim() || !this.form.draweeBank.trim()) {
      Swal.fire('Warning', 'Branch, account, cheque number, bank and amount are required.', 'warning');
      return;
    }
    this.saving = true;
    this.transactionApi.chequeClearing(this.form).subscribe({
      next: response => {
        this.saving = false;
        Swal.fire('Success', 'Cheque clearing posted successfully.', 'success');
        this.router.navigate(['/transactions', response.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to post cheque clearing.', 'error');
      }
    });
  }

  getAccountLabel(item: AccountResponse): string {
    return `${item.accountNumber} - ${item.customerName}`;
  }
}
