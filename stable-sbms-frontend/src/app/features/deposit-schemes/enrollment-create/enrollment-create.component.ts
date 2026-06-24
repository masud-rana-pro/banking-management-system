import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { AccountResponse } from '../../accounts/models/account.model';
import { AccountService } from '../../accounts/services/account.service';
import { CustomerDropdownResponse } from '../../customer/models/customer.model';
import { CustomerService } from '../../customer/services/customer.service';
import { DepositSchemeEnrollmentRequest, DepositSchemeResponse, formatEnumLabel } from '../models/deposit-scheme.model';
import { DepositSchemeService } from '../services/deposit-scheme.service';

@Component({
  selector: 'app-enrollment-create',
  templateUrl: './enrollment-create.component.html',
  styleUrls: ['./enrollment-create.component.scss']
})
export class EnrollmentCreateComponent implements OnInit {

  loading = false;
  saving = false;
  schemes: DepositSchemeResponse[] = [];
  customers: CustomerDropdownResponse[] = [];
  accounts: AccountResponse[] = [];
  filteredAccounts: AccountResponse[] = [];

  form: DepositSchemeEnrollmentRequest = {
    schemeId: null,
    customerId: null,
    linkedAccountId: null,
    startDate: '',
    installmentAmount: null,
    remarks: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private depositSchemeApi: DepositSchemeService,
    private customerApi: CustomerService,
    private accountApi: AccountService
  ) {}

  ngOnInit(): void {
    this.form.startDate = new Date().toISOString().slice(0, 10);
    this.loading = true;
    forkJoin({
      schemes: this.depositSchemeApi.getSchemes(),
      customers: this.customerApi.dropdown(),
      accounts: this.accountApi.getAccounts()
    }).subscribe({
      next: ({ schemes, customers, accounts }) => {
        this.schemes = (schemes || []).filter(item => item.status !== 'ARCHIVED');
        this.customers = customers || [];
        this.accounts = accounts || [];
        this.route.queryParamMap.subscribe(params => {
          this.form.schemeId = params.get('schemeId') ? Number(params.get('schemeId')) : null;
          this.form.customerId = params.get('customerId') ? Number(params.get('customerId')) : null;
          this.form.linkedAccountId = params.get('accountId') ? Number(params.get('accountId')) : null;
          this.form.installmentAmount = params.get('installmentAmount') ? Number(params.get('installmentAmount')) : this.form.installmentAmount;
          this.form.remarks = params.get('remarks') || this.form.remarks;
          this.syncAccounts();
        });
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load enrollment setup data.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/deposit-schemes/enrollments/list']);
  }

  onCustomerChange(): void {
    this.form.linkedAccountId = null;
    this.syncAccounts();
  }

  syncAccounts(): void {
    this.filteredAccounts = this.accounts.filter(item => !this.form.customerId || item.customerId === this.form.customerId);
  }

  submit(): void {
    this.saving = true;
    this.depositSchemeApi.createEnrollment(this.form).subscribe({
      next: res => {
        this.saving = false;
        Swal.fire('Created', 'Scheme enrollment created successfully.', 'success');
        this.router.navigate(['/deposit-schemes/enrollments', res.id, 'schedule']);
      },
      error: err => {
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to create enrollment.', 'error');
      }
    });
  }

  getCustomerDisplay(item: CustomerDropdownResponse): string {
    return item.displayName || `${item.customerCode} - ${item.fullName}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
