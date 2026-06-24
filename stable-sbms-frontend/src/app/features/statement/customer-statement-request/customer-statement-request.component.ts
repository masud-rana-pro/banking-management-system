import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { AccountResponse } from '../../accounts/models/account.model';
import { AccountService } from '../../accounts/services/account.service';
import { CustomerResponse } from '../../customer/models/customer.model';
import { CustomerService } from '../../customer/services/customer.service';
import { CustomerStatementRequestRequest } from '../models/statement.model';
import { StatementService } from '../services/statement.service';

@Component({
  selector: 'app-customer-statement-request',
  templateUrl: './customer-statement-request.component.html',
  styleUrls: ['./customer-statement-request.component.scss']
})
export class CustomerStatementRequestComponent implements OnInit {

  loading = false;
  saving = false;
  customers: CustomerResponse[] = [];
  accounts: AccountResponse[] = [];
  selectedCustomer: CustomerResponse | null = null;
  customerImageUrl = '';

  model: CustomerStatementRequestRequest = {
    customerId: null,
    accountId: null,
    dateFrom: '',
    dateTo: '',
    requestedBy: 'SYSTEM'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerApi: CustomerService,
    private accountApi: AccountService,
    private fileUploadService: FileUploadService,
    private statementApi: StatementService,
    private accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    const today = new Date().toISOString().slice(0, 10);
    const monthStart = `${today.slice(0, 8)}01`;
    this.model.dateFrom = monthStart;
    this.model.dateTo = today;
    this.model.requestedBy = this.accessControl.session?.username || 'SYSTEM';
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
      accounts: this.accountApi.getAccounts().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ customers, accounts }) => {
        this.customers = (customers || []).filter(item => item.status !== 'ARCHIVED');
        this.accounts = (accounts || []).filter(item => item.status !== 'ARCHIVED');
        const customerId = Number(this.route.snapshot.queryParamMap.get('customerId') || 0);
        const accountId = Number(this.route.snapshot.queryParamMap.get('accountId') || 0);
        if (customerId > 0) this.model.customerId = customerId;
        if (accountId > 0) this.model.accountId = accountId;
        this.syncSelectedCustomer();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load customer statement request form.', 'error');
      }
    });
  }

  onCustomerChange(): void {
    if (this.model.accountId && !this.filteredAccounts.find(item => item.id === this.model.accountId)) {
      this.model.accountId = null;
    }
    this.syncSelectedCustomer();
  }

  submit(): void {
    if (!this.model.customerId || !this.model.accountId || !this.model.dateFrom || !this.model.dateTo) {
      Swal.fire('Validation', 'Customer, account and date range are required.', 'warning');
      return;
    }
    this.saving = true;
    this.statementApi.requestCustomerStatement(this.model).subscribe({
      next: response => {
        this.saving = false;
        Swal.fire('Success', 'Customer statement request accepted. The PDF will be ready shortly.', 'success');
        this.router.navigate(['/statement/customer', response.id]);
      },
      error: err => {
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to generate customer statement.', 'error');
      }
    });
  }

  private syncSelectedCustomer(): void {
    this.selectedCustomer = this.customers.find(item => item.id === this.model.customerId) || null;
    this.customerImageUrl = this.selectedCustomer?.profileImageName
      ? this.fileUploadService.resolveImageUrl(this.selectedCustomer.profileImageName)
      : '';
  }
}
