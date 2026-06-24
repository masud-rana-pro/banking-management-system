import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccountResponse } from '../../../accounts/models/account.model';
import { AccountService } from '../../../accounts/services/account.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { RECORD_STATUS_OPTIONS, STANDING_FREQUENCY_OPTIONS, StandingInstructionRequest, TRANSFER_MODE_OPTIONS } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-standing-instruction-create',
  templateUrl: './standing-instruction-create.component.html',
  styleUrls: ['./standing-instruction-create.component.scss']
})
export class StandingInstructionCreateComponent implements OnInit {

  loading = false;
  saving = false;
  accounts: AccountResponse[] = [];
  branches: BranchResponse[] = [];
  transferModes = TRANSFER_MODE_OPTIONS;
  frequencies = STANDING_FREQUENCY_OPTIONS;
  recordStatuses = RECORD_STATUS_OPTIONS;

  form: StandingInstructionRequest = {
    fromAccountId: null,
    toAccountId: null,
    branchId: null,
    amount: null,
    transferMode: 'INTERNAL',
    scheduleDate: new Date().toISOString().slice(0, 10),
    frequency: 'MONTHLY',
    remarks: '',
    status: 'ACTIVE'
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
        if (fromAccountId) {
          const matched = this.accounts.find(item => item.id === fromAccountId);
          if (matched) {
            this.form.fromAccountId = matched.id;
            this.form.branchId = matched.branchId || null;
          }
        }
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load standing instruction form.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.form.fromAccountId || !this.form.toAccountId || !this.form.branchId || !this.form.amount) {
      Swal.fire('Warning', 'Branch, both accounts and amount are required.', 'warning');
      return;
    }
    this.saving = true;
    this.transactionApi.createStandingInstruction(this.form).subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Success', 'Standing instruction created successfully.', 'success');
        this.router.navigate(['/transactions/standing-instructions']);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to create standing instruction.', 'error');
      }
    });
  }

  getAccountLabel(item: AccountResponse): string {
    return `${item.accountNumber} - ${item.customerName}`;
  }
}
