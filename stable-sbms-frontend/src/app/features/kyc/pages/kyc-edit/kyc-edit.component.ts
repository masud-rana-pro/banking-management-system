import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { CustomerDropdownResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import {
  KYC_REVIEW_STATUS_OPTIONS,
  RECORD_STATUS_OPTIONS,
  RISK_LEVEL_OPTIONS,
  KycProfileRequest
} from '../../models/kyc.model';
import { KycService } from '../../services/kyc.service';

@Component({
  selector: 'app-kyc-edit',
  templateUrl: './kyc-edit.component.html',
  styleUrls: ['./kyc-edit.component.scss']
})
export class KycEditComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;
  dropdownLoading = false;
  keyword = '';
  dropdownItems: CustomerDropdownResponse[] = [];

  riskLevelOptions = RISK_LEVEL_OPTIONS;
  reviewStatusOptions = KYC_REVIEW_STATUS_OPTIONS;
  recordStatusOptions = RECORD_STATUS_OPTIONS;

  form: KycProfileRequest = this.getInitialForm();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private kycApi: KycService,
    private customerApi: CustomerService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const customerIdParam = this.route.snapshot.queryParamMap.get('customerId');
    this.id = idParam ? Number(idParam) : null;

    this.loadDropdown();

    if (this.id) {
      this.loadProfile(this.id);
      return;
    }

    if (customerIdParam) {
      const customerId = Number(customerIdParam);
      this.form.customerId = customerId;
      this.kycApi.getProfileByCustomerId(customerId).subscribe({
        next: profile => this.router.navigate(['/kyc', profile.id]),
        error: () => {}
      });
    }
  }

  get selectedCustomerLabel(): string {
    const item = this.dropdownItems.find(entry => entry.id === this.form.customerId);
    return item ? item.displayName : 'No customer selected';
  }

  loadDropdown(keyword = ''): void {
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

  onDropdownSearch(): void {
    this.loadDropdown(this.keyword.trim());
  }

  cancel(): void {
    this.router.navigate(['/kyc/list']);
  }

  save(): void {
    if (!this.validateForm()) return;

    this.saving = true;
    const request = this.buildRequest();
    const save$ = this.id
      ? this.kycApi.updateProfile(this.id, request)
      : this.kycApi.createProfile(request);

    save$.subscribe({
      next: response => {
        this.saving = false;
        Swal.fire('Success', this.id ? 'KYC profile updated successfully.' : 'KYC profile created successfully.', 'success')
          .then(() => this.router.navigate(['/kyc', response.id]));
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save KYC profile.', 'error');
      }
    });
  }

  private loadProfile(id: number): void {
    this.loading = true;

    this.kycApi.getProfileById(id).subscribe({
      next: data => {
        this.form = {
          customerId: data.customerId,
          riskLevel: data.riskLevel || '',
          sourceOfFundsNote: data.sourceOfFundsNote || '',
          pepFlag: !!data.pepFlag,
          sanctionFlag: !!data.sanctionFlag,
          amlFlag: !!data.amlFlag,
          reviewStatus: data.reviewStatus || 'DRAFT',
          remarks: data.remarks || '',
          status: data.status || 'ACTIVE'
        };
        this.loadDropdown();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load KYC profile.', 'error');
      }
    });
  }

  private validateForm(): boolean {
    if (!this.form.customerId) {
      Swal.fire('Validation', 'Customer is required.', 'warning');
      return false;
    }

    if (!this.form.riskLevel) {
      Swal.fire('Validation', 'Risk level is required.', 'warning');
      return false;
    }

    return true;
  }

  private buildRequest(): KycProfileRequest {
    return {
      customerId: this.form.customerId,
      riskLevel: this.form.riskLevel,
      sourceOfFundsNote: this.form.sourceOfFundsNote.trim(),
      pepFlag: !!this.form.pepFlag,
      sanctionFlag: !!this.form.sanctionFlag,
      amlFlag: !!this.form.amlFlag,
      reviewStatus: this.form.reviewStatus,
      remarks: this.form.remarks.trim(),
      status: this.form.status
    };
  }

  private getInitialForm(): KycProfileRequest {
    return {
      customerId: null,
      riskLevel: '',
      sourceOfFundsNote: '',
      pepFlag: false,
      sanctionFlag: false,
      amlFlag: false,
      reviewStatus: 'DRAFT',
      remarks: '',
      status: 'ACTIVE'
    };
  }
}
