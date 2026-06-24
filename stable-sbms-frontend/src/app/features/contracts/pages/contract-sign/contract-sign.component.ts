import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { ContractResponse, ContractSignRequest, formatEnumLabel } from '../../models/contract.model';
import { ContractService } from '../../services/contract.service';

@Component({
  selector: 'app-contract-sign',
  templateUrl: './contract-sign.component.html',
  styleUrls: ['./contract-sign.component.scss']
})
export class ContractSignComponent implements OnInit {

  id = 0;
  loading = false;
  processing = false;
  item: ContractResponse | null = null;
  customer: CustomerResponse | null = null;
  customerImageUrl = '';
  customerForm: ContractSignRequest = { signedBy: 'CUSTOMER_SIGNER', remarks: '' };
  shariahForm: ContractSignRequest = { signedBy: 'SHARIAH_SIGNER', remarks: '' };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private contractService: ContractService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.loading = true;
    this.contractService.getById(this.id).subscribe({
      next: data => {
        this.item = data;
        this.loadCustomerProfile(data.customerId);
        this.customerForm.remarks = data.remarks || '';
        this.shariahForm.remarks = data.remarks || '';
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load contract sign page.', 'error');
      }
    });
  }

  customerSign(): void {
    this.processing = true;
    this.contractService.customerSign(this.id, this.customerForm).subscribe({
      next: data => {
        this.processing = false;
        this.item = data;
        Swal.fire('Success', 'Customer signature captured successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.processing = false;
        Swal.fire('Error', err?.error?.message || 'Failed to capture customer signature.', 'error');
      }
    });
  }

  shariahSign(): void {
    this.processing = true;
    this.contractService.shariahSign(this.id, this.shariahForm).subscribe({
      next: data => {
        this.processing = false;
        this.item = data;
        Swal.fire('Success', 'Shariah signature captured successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.processing = false;
        Swal.fire('Error', err?.error?.message || 'Failed to capture shariah signature.', 'error');
      }
    });
  }

  openVersions(): void {
    this.router.navigate(['/contracts', this.id, 'versions']);
  }

  previewSupportingDocument(): void {
    if (!this.item?.supportingDocumentName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.item.supportingDocumentName), '_blank');
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

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
