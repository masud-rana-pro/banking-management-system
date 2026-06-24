import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccountResponse } from '../../../accounts/models/account.model';
import { AccountService } from '../../../accounts/services/account.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { FundTransferRequest, TRANSFER_MODE_OPTIONS } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-fund-transfer',
  templateUrl: './fund-transfer.component.html',
  styleUrls: ['./fund-transfer.component.scss']
})
export class FundTransferComponent implements OnInit {

  loading = false;
  saving = false;
  accounts: AccountResponse[] = [];
  branches: BranchResponse[] = [];
  transferModes = TRANSFER_MODE_OPTIONS;

  form: FundTransferRequest = {
    branchId: null,
    fromAccountId: null,
    toAccountId: null,
    transferMode: 'INTERNAL',
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

        const fromAccountId = Number(this.route.snapshot.queryParamMap.get('fromAccountId') || this.route.snapshot.queryParamMap.get('accountId') || 0);
        const toAccountId = Number(this.route.snapshot.queryParamMap.get('toAccountId') || 0);
        if (fromAccountId) {
          const matched = this.accounts.find(item => item.id === fromAccountId);
          if (matched) {
            this.form.fromAccountId = matched.id;
            this.form.branchId = matched.branchId || null;
          }
        }
        if (toAccountId) {
          this.form.toAccountId = toAccountId;
        }
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load fund transfer form.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.form.fromAccountId || !this.form.toAccountId || !this.form.branchId || !this.form.amount) {
      Swal.fire('Warning', 'Branch, both accounts and amount are required.', 'warning');
      return;
    }
    this.saving = true;
    this.transactionApi.transfer(this.form).subscribe({
      next: response => {
        this.saving = false;
        Swal.fire('Success', 'Fund transfer posted successfully.', 'success');
        this.router.navigate(['/transactions', response.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to post transfer.', 'error');
      }
    });
  }

  getAccountLabel(item: AccountResponse): string {
    return `${item.accountNumber} - ${item.customerName}`;
  }
}
