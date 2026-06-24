import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import {
  CustomerAddressResponse,
  CustomerDropdownResponse,
  CustomerIdentityResponse,
  CustomerResponse,
  formatEnumLabel
} from '../../models/customer.model';
import { CustomerService } from '../../services/customer.service';
import { KycService } from '../../../kyc/services/kyc.service';

@Component({
  selector: 'app-customer-status-action',
  templateUrl: './customer-status-action.component.html',
  styleUrls: ['./customer-status-action.component.scss']
})
export class CustomerStatusActionComponent implements OnInit {

  customerId: number | null = null;
  customer: CustomerResponse | null = null;
  addresses: CustomerAddressResponse[] = [];
  identities: CustomerIdentityResponse[] = [];
  dropdownItems: CustomerDropdownResponse[] = [];
  loading = false;
  dropdownLoading = false;
  keyword = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerApi: CustomerService,
    private kycApi: KycService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const routeId = this.route.snapshot.paramMap.get('id');
    const queryId = this.route.snapshot.queryParamMap.get('customerId');
    this.customerId = routeId ? Number(routeId) : queryId ? Number(queryId) : null;
    this.loadDropdown();

    if (this.customerId) {
      this.loadCustomer(this.customerId);
    }
  }

  onDropdownSearch(): void {
    this.loadDropdown(this.keyword);
  }

  onCustomerSelect(id: number | null): void {
    this.customerId = id ? Number(id) : null;
    if (this.customerId) {
      this.router.navigate(['/customers', this.customerId, 'status']);
    }
  }

  openCustomer(): void {
    if (!this.customerId) return;
    this.router.navigate(['/customers', this.customerId]);
  }

  manageAddress(): void {
    if (!this.customerId) return;
    this.router.navigate(['/customers', this.customerId, 'addresses']);
  }

  manageIdentity(): void {
    if (!this.customerId) return;
    this.router.navigate(['/customers', this.customerId, 'identities']);
  }

  openKyc(): void {
    if (!this.customerId) return;
    this.kycApi.getProfileByCustomerId(this.customerId).subscribe({
      next: profile => this.router.navigate(['/kyc', profile.id]),
      error: () => this.router.navigate(['/kyc/new'], { queryParams: { customerId: this.customerId } })
    });
  }

  openAccountRequest(): void {
    if (!this.customerId) return;
    this.router.navigate(['/accounts/opening-requests/new'], { queryParams: { customerId: this.customerId } });
  }

  activate(): void {
    if (!this.customerId || !this.customer) return;
    this.customerApi.activate(this.customerId).subscribe({
      next: () => {
        Swal.fire('Activated', 'Customer activated successfully.', 'success');
        this.loadCustomer(this.customerId!);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Activation failed.', 'error')
    });
  }

  block(): void {
    if (!this.customerId || !this.customer) return;
    this.customerApi.block(this.customerId).subscribe({
      next: () => {
        Swal.fire('Blocked', 'Customer blocked successfully.', 'success');
        this.loadCustomer(this.customerId!);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Block failed.', 'error')
    });
  }

  archive(): void {
    if (!this.customerId || !this.customer) return;
    this.customerApi.archive(this.customerId).subscribe({
      next: () => {
        Swal.fire('Archived', 'Customer archived successfully.', 'success');
        this.loadCustomer(this.customerId!);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Archive failed.', 'error')
    });
  }

  restore(): void {
    if (!this.customerId || !this.customer) return;
    this.customerApi.restore(this.customerId).subscribe({
      next: () => {
        Swal.fire('Restored', 'Customer restored successfully.', 'success');
        this.loadCustomer(this.customerId!);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Restore failed.', 'error')
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }

  private loadDropdown(keyword = ''): void {
    this.dropdownLoading = true;
    this.customerApi.dropdown(keyword).subscribe({
      next: data => {
        this.dropdownItems = data || [];
        this.dropdownLoading = false;
      },
      error: err => {
        console.error(err);
        this.dropdownLoading = false;
      }
    });
  }

  private loadCustomer(id: number): void {
    this.loading = true;

    forkJoin({
      customer: this.customerApi.getById(id),
      addresses: this.customerApi.getAddressesByCustomer(id).pipe(catchError(() => of([]))),
      identities: this.customerApi.getIdentitiesByCustomer(id).pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ customer, addresses, identities }) => {
        this.customer = customer;
        this.addresses = addresses;
        this.identities = identities;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load customer status action page.', 'error');
      }
    });
  }
}
