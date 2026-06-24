import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { ContractResponse, formatEnumLabel } from '../../models/contract.model';
import { ContractService } from '../../services/contract.service';

@Component({
  selector: 'app-contract-view',
  templateUrl: './contract-view.component.html',
  styleUrls: ['./contract-view.component.scss']
})
export class ContractViewComponent implements OnInit {

  id = 0;
  loading = false;
  item: ContractResponse | null = null;
  customer: CustomerResponse | null = null;
  customerImageUrl = '';

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
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load contract view.', 'error');
      }
    });
  }

  previewPrintCopy(): void {
    if (!this.item) return;
    window.open(this.contractService.getPrintCopyPreviewUrl(this.item.id), '_blank');
  }

  downloadPrintCopy(): void {
    if (!this.item) return;
    const link = document.createElement('a');
    link.href = this.contractService.getPrintCopyDownloadUrl(this.item.id);
    link.target = '_blank';
    link.rel = 'noopener';
    link.click();
  }

  printPrintCopy(): void {
    this.previewPrintCopy();
  }

  openSign(): void {
    this.router.navigate(['/contracts', this.id, 'sign']);
  }

  openVersions(): void {
    this.router.navigate(['/contracts', this.id, 'versions']);
  }

  regenerate(): void {
    if (!this.item) return;
    this.router.navigate(['/contracts/generate'], {
      queryParams: {
        templateId: this.item.templateId,
        customerId: this.item.customerId,
        referenceModule: this.item.referenceModule,
        referenceId: this.item.referenceId
      }
    });
  }

  openShariahCases(): void {
    this.router.navigate(['/shariah/cases'], {
      queryParams: {
        referenceModule: 'CONTRACT'
      }
    });
  }

  submitShariahCase(): void {
    if (!this.item) return;
    this.router.navigate(['/shariah/cases'], {
      queryParams: {
        referenceModule: 'CONTRACT',
        referenceId: this.item.id,
        create: 1
      }
    });
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

  canAny(permissionCodes: string[]): boolean {
    return this.accessControl.hasAnyPermission(permissionCodes);
  }
}
