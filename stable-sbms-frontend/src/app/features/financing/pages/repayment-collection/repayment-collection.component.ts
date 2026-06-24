import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import {
  FinancingApplicationResponse,
  FinancingRepaymentCollectionRequest,
  FinancingRepaymentCollectionResponse,
  FinancingScheduleResponse,
  formatEnumLabel
} from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

@Component({
  selector: 'app-repayment-collection',
  templateUrl: './repayment-collection.component.html',
  styleUrls: ['./repayment-collection.component.scss']
})
export class RepaymentCollectionComponent implements OnInit {

  id = 0;
  loading = false;
  saving = false;
  item: FinancingApplicationResponse | null = null;
  result: FinancingRepaymentCollectionResponse | null = null;
  schedules: FinancingScheduleResponse[] = [];
  customer: CustomerResponse | null = null;
  customerImageUrl = '';
  form: FinancingRepaymentCollectionRequest = {
    paymentAmount: null,
    paymentDate: new Date().toISOString().slice(0, 10),
    remarks: '',
    collectedBy: 'SYSTEM_COLLECTION'
  };

  constructor(
    private route: ActivatedRoute,
    private financingService: FinancingService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.form.collectedBy = this.accessControl.session?.username || 'SYSTEM_COLLECTION';
    this.load();
  }

  load(): void {
    this.loading = true;
    this.financingService.getApplicationById(this.id).subscribe({
      next: data => {
        this.item = data;
        this.loadCustomerProfile(data.customerId);
        this.schedules = data.schedules || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load repayment collection page.', 'error');
      }
    });
  }

  collect(): void {
    this.saving = true;
    this.financingService.collectPayment(this.id, this.form).subscribe({
      next: data => {
        this.saving = false;
        this.result = data;
        Swal.fire('Success', 'Repayment collected successfully.', 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to collect repayment.', 'error');
      }
    });
  }

  print(): void {
    window.print();
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
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
