import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { AccountService } from 'src/app/features/accounts/services/account.service';
import { AccountResponse } from 'src/app/features/accounts/models/account.model';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { FinancingApplicationResponse, FinancingDisbursementRequest, formatEnumLabel } from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-financing-disbursement',
  templateUrl: './financing-disbursement.component.html',
  styleUrls: ['./financing-disbursement.component.scss']
})
export class FinancingDisbursementComponent implements OnInit {

  id = 0;
  loading = false;
  saving = false;
  item: FinancingApplicationResponse | null = null;
  accounts: AccountResponse[] = [];
  customer: CustomerResponse | null = null;
  customerImageUrl = '';
  form: FinancingDisbursementRequest = {
    disbursementDate: new Date().toISOString().slice(0, 10),
    disbursedAmount: null,
    creditedAccountId: null,
    disbursedBy: 'SYSTEM_DISBURSER',
    remarks: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private financingService: FinancingService,
    private accountService: AccountService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.form.disbursedBy = this.accessControl.session?.username || 'SYSTEM_DISBURSER';
    this.load();
  }

  load(): void {
    this.loading = true;
    this.financingService.getApplicationById(this.id).subscribe({
      next: data => {
        this.item = data;
        this.loadCustomerProfile(data.customerId);
        this.form.disbursedAmount = data.requestedAmount;
        this.form.remarks = data.remarks || '';
        this.accountService.getAccounts().subscribe(accounts => {
          this.accounts = accounts.filter(account => account.customerId === data.customerId);
        });
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load financing disbursement page.', 'error');
      }
    });
  }

  save(): void {
    if (!this.canDisburse) {
      Swal.fire('Not ready', 'Only approved/sanctioned applications can be disbursed.', 'info');
      return;
    }
    if (!this.form.creditedAccountId) {
      Swal.fire('Required', 'Credited account is required for disbursement.', 'warning');
      return;
    }
    if (!this.form.disbursedAmount || this.form.disbursedAmount <= 0) {
      Swal.fire('Required', 'Disbursed amount must be greater than zero.', 'warning');
      return;
    }
    this.saving = true;
    this.financingService.disburseApplication(this.id, this.form).subscribe({
      next: data => {
        this.saving = false;
        Swal.fire('Success', 'Financing disbursement completed successfully.', 'success');
        this.router.navigate(['/financing/applications', data.id, 'schedule']);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to disburse financing application.', 'error');
      }
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  previewSupportingDocument(): void {
    if (!this.item?.supportingDocumentName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.item.supportingDocumentName), '_blank');
  }

  get canDisburse(): boolean {
    return this.accessControl.hasPermission('FINANCING_DISBURSE') && this.item?.applicationStatus === 'APPROVED';
  }

  getActionTitle(label: string): string {
    return this.canDisburse ? label : `${label} (No permission)`;
  }

  private loadCustomerProfile(customerId?: number | null): void {
    if (!customerId) {
      this.customer = null;
      this.customerImageUrl = '';
      return;
    }

    this.customerService.getById(customerId).subscribe({
      next: customer => {
        this.customer = customer;
        this.customerImageUrl = customer.profileImageName
          ? this.fileUploadService.resolveImageUrl(customer.profileImageName)
          : '';
      },
      error: () => {
        this.customer = null;
        this.customerImageUrl = '';
      }
    });
  }
}
