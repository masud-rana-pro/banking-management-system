import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccountResponse } from '../../../accounts/models/account.model';
import { AccountService } from '../../../accounts/services/account.service';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { CARD_STATUS_OPTIONS, CARD_TYPE_OPTIONS, CardRequest, formatEnumLabel } from '../../models/card.model';
import { CardService } from '../../services/card.service';

@Component({
  selector: 'app-card-create',
  templateUrl: './card-create.component.html',
  styleUrls: ['./card-create.component.scss']
})
export class CardCreateComponent implements OnInit {

  id: number | null = null;
  editMode = false;
  loading = false;
  saving = false;
  customers: CustomerResponse[] = [];
  accounts: AccountResponse[] = [];

  readonly cardTypes = CARD_TYPE_OPTIONS;
  readonly cardStatuses = CARD_STATUS_OPTIONS;

  model: CardRequest = {
    customerId: null,
    accountId: null,
    cardType: 'DEBIT_CARD',
    maskedCardNo: '',
    issueDate: new Date().toISOString().slice(0, 10),
    expiryDate: '',
    cardStatus: 'PENDING_ACTIVATION',
    blockReason: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private cardApi: CardService,
    private customerApi: CustomerService,
    private accountApi: AccountService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    this.editMode = !!this.id;

    const today = new Date();
    const after5Years = new Date(today.getFullYear() + 5, today.getMonth(), today.getDate());
    this.model.expiryDate = after5Years.toISOString().slice(0, 10);

    this.loadData();
  }

  get filteredAccounts(): AccountResponse[] {
    return !this.model.customerId
      ? this.accounts
      : this.accounts.filter(item => item.customerId === this.model.customerId);
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      customers: this.customerApi.getAll().pipe(catchError(() => of([]))),
      accounts: this.accountApi.getAccounts().pipe(catchError(() => of([]))),
      card: this.id ? this.cardApi.getById(this.id).pipe(catchError(() => of(null))) : of(null)
    }).subscribe({
      next: ({ customers, accounts, card }) => {
        this.customers = (customers || []).filter(item => item.customerStatus === 'ACTIVE' && item.status !== 'ARCHIVED');
        this.accounts = (accounts || []).filter(item => item.accountStatus === 'ACTIVE' && item.status !== 'ARCHIVED');

        const queryCustomerId = Number(this.route.snapshot.queryParamMap.get('customerId') || 0);
        const queryAccountId = Number(this.route.snapshot.queryParamMap.get('accountId') || 0);

        if (queryCustomerId > 0) {
          this.model.customerId = queryCustomerId;
        }
        if (queryAccountId > 0) {
          this.model.accountId = queryAccountId;
        }

        if (card) {
          this.model = {
            customerId: card.customerId,
            accountId: card.accountId,
            cardType: card.cardType,
            maskedCardNo: card.maskedCardNo,
            issueDate: card.issueDate,
            expiryDate: card.expiryDate,
            cardStatus: card.cardStatus,
            blockReason: card.blockReason || ''
          };
        }

        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load card form.', 'error');
      }
    });
  }

  onCustomerChange(): void {
    if (this.model.accountId && !this.filteredAccounts.find(item => item.id === this.model.accountId)) {
      this.model.accountId = null;
    }
  }

  save(): void {
    if (!this.model.customerId || !this.model.accountId || !this.model.cardType || !this.model.issueDate || !this.model.expiryDate) {
      Swal.fire('Validation', 'Customer, account, card type, issue date and expiry date are required.', 'warning');
      return;
    }
    if (this.model.cardStatus === 'BLOCKED' && !this.model.blockReason.trim()) {
      Swal.fire('Validation', 'Block reason is required for blocked card status.', 'warning');
      return;
    }

    this.saving = true;
    const action$ = this.editMode && this.id
      ? this.cardApi.update(this.id, this.model)
      : this.cardApi.create(this.model);

    action$.subscribe({
      next: card => {
        this.saving = false;
        Swal.fire('Success', this.editMode ? 'Card updated successfully.' : 'Card issued successfully.', 'success');
        this.router.navigate(['/cards', card.id]);
      },
      error: err => {
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save card.', 'error');
      }
    });
  }

  cancel(): void {
    if (this.id) {
      this.router.navigate(['/cards', this.id]);
      return;
    }
    this.router.navigate(['/cards/list']);
  }

  openCustomer(): void {
    if (!this.model.customerId) return;
    this.router.navigate(['/customers', this.model.customerId]);
  }

  openAccount(): void {
    if (!this.model.accountId) return;
    this.router.navigate(['/accounts', this.model.accountId]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
