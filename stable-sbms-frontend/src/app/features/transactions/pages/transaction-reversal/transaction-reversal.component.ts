import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { TransactionResponse } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-transaction-reversal',
  templateUrl: './transaction-reversal.component.html',
  styleUrls: ['./transaction-reversal.component.scss']
})
export class TransactionReversalComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;
  item: TransactionResponse | null = null;
  reason = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private transactionApi: TransactionService,
    private accessControl: AccessControlService
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
    this.transactionApi.getTransactionById(id).subscribe({
      next: item => {
        this.item = item;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load transaction reversal page.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.canReverse) return;
    if (!this.id) return;
    if (!this.reason.trim()) {
      Swal.fire('Warning', 'Reversal reason is required.', 'warning');
      return;
    }
    this.saving = true;
    this.transactionApi.reverseTransaction(this.id, { reason: this.reason.trim() }).subscribe({
      next: response => {
        this.saving = false;
        Swal.fire('Success', 'Transaction reversed successfully.', 'success');
        this.router.navigate(['/transactions', response.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to reverse transaction.', 'error');
      }
    });
  }

  get canReverse(): boolean {
    return this.accessControl.hasPermission('TRANSACTION_REVERSE');
  }

  getActionTitle(label: string): string {
    return this.canReverse ? label : `${label} (No permission)`;
  }
}
