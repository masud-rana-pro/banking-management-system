import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { ZakatProfileRequest, ZakatProfileResponse, formatEnumLabel } from '../../models/zakat.model';
import { ZakatService } from '../../services/zakat.service';

@Component({
  selector: 'app-zakat-calc-run',
  templateUrl: './zakat-calc-run.component.html',
  styleUrls: ['./zakat-calc-run.component.scss']
})
export class ZakatCalcRunComponent implements OnInit {

  loading = false;
  profileId: number | null = null;
  profile: ZakatProfileResponse | null = null;
  customers: CustomerResponse[] = [];
  selectedCustomer: CustomerResponse | null = null;
  customerImageUrl = '';
  form: ZakatProfileRequest = {
    customerId: null,
    zakatYear: new Date().getFullYear(),
    nisabAmount: null,
    eligibleAssetAmount: null,
    remarks: '',
    proofDocumentName: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private zakatService: ZakatService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.profileId = this.toNumber(this.route.snapshot.queryParamMap.get('profileId'));
    this.form.customerId = this.toNumber(this.route.snapshot.queryParamMap.get('customerId'));
    this.customerService.getAll().subscribe(data => {
      this.customers = data || [];
      this.syncSelectedCustomer();
    });
    if (this.profileId) {
      this.loadProfile(this.profileId);
    }
  }

  loadProfile(id: number): void {
    this.loading = true;
    this.zakatService.getProfileById(id).subscribe({
      next: data => {
        this.profile = data;
        this.form = {
          customerId: data.customerId,
          zakatYear: data.zakatYear,
          nisabAmount: data.nisabAmount,
          eligibleAssetAmount: data.eligibleAssetAmount,
          remarks: data.remarks || '',
          proofDocumentName: data.proofDocumentName || ''
        };
        this.syncSelectedCustomer();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load zakat profile.', 'error');
      }
    });
  }

  saveProfile(): void {
    if (!this.form.customerId || !this.form.zakatYear || this.form.nisabAmount === null || this.form.eligibleAssetAmount === null) {
      Swal.fire('Missing data', 'Customer, year, nisab and eligible asset are required.', 'warning');
      return;
    }

    const request$ = this.profileId
      ? this.zakatService.updateProfile(this.profileId, this.form)
      : this.zakatService.createProfile(this.form);

    request$.subscribe({
      next: data => {
        this.profile = data;
        this.profileId = data.id;
        Swal.fire('Success', `Zakat profile ${this.profileId ? 'saved' : 'created'} successfully.`, 'success');
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to save zakat profile.', 'error');
      }
    });
  }

  async runCalculation(): Promise<void> {
    if (!this.profileId) {
      Swal.fire('Save first', 'Save the zakat profile before running calculation.', 'warning');
      return;
    }
    this.zakatService.calculate({
      profileId: this.profileId,
      nisabAmount: this.form.nisabAmount,
      eligibleAssetAmount: this.form.eligibleAssetAmount,
      remarks: this.form.remarks
    }).subscribe({
      next: data => {
        this.profile = data;
        this.form.nisabAmount = data.nisabAmount;
        this.form.eligibleAssetAmount = data.eligibleAssetAmount;
        this.form.remarks = data.remarks || '';
        this.form.proofDocumentName = data.proofDocumentName || '';
        Swal.fire('Success', 'Zakat calculation completed successfully.', 'success');
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to calculate zakat.', 'error');
      }
    });
  }

  openView(): void {
    if (!this.profileId) return;
    this.router.navigate(['/zakat/profiles', this.profileId]);
  }

  back(): void {
    this.router.navigate(['/zakat/profiles']);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  syncSelectedCustomer(): void {
    this.selectedCustomer = this.customers.find(customer => customer.id === this.form.customerId) || null;
    this.customerImageUrl = this.selectedCustomer?.profileImageName
      ? this.fileUploadService.resolveImageUrl(this.selectedCustomer.profileImageName)
      : '';
  }

  onProofSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.fileUploadService.uploadDocument(file).subscribe({
      next: result => {
        this.form.proofDocumentName = result.fileName;
        Swal.fire('Uploaded', 'Zakat proof document uploaded successfully.', 'success');
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to upload proof document.', 'error');
      }
    });
  }

  previewProof(): void {
    if (!this.form.proofDocumentName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.form.proofDocumentName), '_blank');
  }

  private toNumber(value: string | null): number | null {
    if (!value) return null;
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
  }
}
