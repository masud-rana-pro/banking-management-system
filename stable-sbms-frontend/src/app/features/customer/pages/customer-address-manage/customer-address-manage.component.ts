import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import {
  ADDRESS_TYPE_OPTIONS,
  COUNTRY_OPTIONS,
  CustomerAddressRequest,
  CustomerAddressResponse,
  CustomerResponse,
  DISTRICT_OPTIONS,
  DIVISION_OPTIONS,
  RECORD_STATUS_OPTIONS,
  UPAZILA_OPTIONS,
  formatEnumLabel
} from '../../models/customer.model';
import { CustomerService } from '../../services/customer.service';

@Component({
  selector: 'app-customer-address-manage',
  templateUrl: './customer-address-manage.component.html',
  styleUrls: ['./customer-address-manage.component.scss']
})
export class CustomerAddressManageComponent implements OnInit {

  customerId: number | null = null;
  customer: CustomerResponse | null = null;
  addresses: CustomerAddressResponse[] = [];
  editingId: number | null = null;
  loading = false;
  saving = false;

  addressTypeOptions = ADDRESS_TYPE_OPTIONS;
  recordStatusOptions = RECORD_STATUS_OPTIONS;
  countries = COUNTRY_OPTIONS;

  form: CustomerAddressRequest = this.getInitialForm();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerApi: CustomerService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.customerId = idParam ? Number(idParam) : null;

    if (this.customerId) {
      this.loadData(this.customerId);
      this.form.customerId = this.customerId;
    }
  }

  get divisionOptions() {
    return DIVISION_OPTIONS.filter(item => !this.form.countryId || item.parentId === this.form.countryId);
  }

  get districtOptions() {
    return DISTRICT_OPTIONS.filter(item => !this.form.divisionId || item.parentId === this.form.divisionId);
  }

  get upazilaOptions() {
    return UPAZILA_OPTIONS.filter(item => !this.form.districtId || item.parentId === this.form.districtId);
  }

  get primaryCount(): number {
    return this.addresses.filter(item => item.primaryAddress).length;
  }

  get presentCount(): number {
    return this.addresses.filter(item => item.addressType === 'PRESENT').length;
  }

  get officeCount(): number {
    return this.addresses.filter(item => item.addressType === 'OFFICE').length;
  }

  back(): void {
    if (!this.customerId) return;
    this.router.navigate(['/customers', this.customerId]);
  }

  resetForm(): void {
    this.editingId = null;
    this.form = this.getInitialForm();
    this.form.customerId = this.customerId;
  }

  editAddress(item: CustomerAddressResponse): void {
    this.editingId = item.id;
    this.form = {
      customerId: item.customerId,
      addressType: item.addressType,
      addressLine1: item.addressLine1 || '',
      addressLine2: item.addressLine2 || '',
      countryId: item.countryId || null,
      divisionId: item.divisionId || null,
      districtId: item.districtId || null,
      upazilaId: item.upazilaId || null,
      postalCode: item.postalCode || '',
      primaryAddress: !!item.primaryAddress,
      status: item.status || 'ACTIVE'
    };
  }

  save(): void {
    if (!this.validateForm()) return;

    this.saving = true;
    const save$ = this.editingId
      ? this.customerApi.updateAddress(this.editingId, this.form)
      : this.customerApi.createAddress(this.form);

    save$.subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Saved', 'Customer address saved successfully.', 'success');
        this.resetForm();
        if (this.customerId) this.loadData(this.customerId);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save address.', 'error');
      }
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getCountryName(countryId?: number | null): string {
    return this.countries.find(item => item.id === countryId)?.name || '-';
  }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }

  private loadData(customerId: number): void {
    this.loading = true;

    forkJoin({
      customer: this.customerApi.getById(customerId),
      addresses: this.customerApi.getAddressesByCustomer(customerId)
    }).subscribe({
      next: ({ customer, addresses }) => {
        this.customer = customer;
        this.addresses = addresses || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load customer address information.', 'error');
      }
    });
  }

  private validateForm(): boolean {
    if (!this.form.addressType) {
      Swal.fire('Validation', 'Address type is required.', 'warning');
      return false;
    }

    if (!this.form.addressLine1.trim()) {
      Swal.fire('Validation', 'Address line 1 is required.', 'warning');
      return false;
    }

    return true;
  }

  private getInitialForm(): CustomerAddressRequest {
    return {
      customerId: null,
      addressType: '',
      addressLine1: '',
      addressLine2: '',
      countryId: 1,
      divisionId: 101,
      districtId: 1001,
      upazilaId: 10001,
      postalCode: '',
      primaryAddress: false,
      status: 'ACTIVE'
    };
  }
}
