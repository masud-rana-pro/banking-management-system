import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { KycDecisionHistoryResponse, KycProfileResponse, formatEnumLabel } from '../../models/kyc.model';
import { KycService } from '../../services/kyc.service';

@Component({
  selector: 'app-kyc-history',
  templateUrl: './kyc-history.component.html',
  styleUrls: ['./kyc-history.component.scss']
})
export class KycHistoryComponent implements OnInit {

  id: number | null = null;
  loading = false;
  profile: KycProfileResponse | null = null;
  history: KycDecisionHistoryResponse[] = [];
  customerImageUrl = '';
  customer: CustomerResponse | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private kycApi: KycService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;

    if (this.id) {
      this.loadData(this.id);
    }
  }

  back(): void {
    if (!this.id) return;
    this.router.navigate(['/kyc', this.id]);
  }

  openView(): void {
    if (!this.id) return;
    this.router.navigate(['/kyc', this.id]);
  }

  openReview(): void {
    if (!this.id) return;
    this.router.navigate(['/kyc', this.id, 'review']);
  }

  openUpload(): void {
    if (!this.id) return;
    this.router.navigate(['/kyc', this.id, 'documents']);
  }

  openCustomer(): void {
    if (!this.profile) return;
    this.router.navigate(['/customers', this.profile.customerId]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  private loadData(id: number): void {
    this.loading = true;

    forkJoin({
      profile: this.kycApi.getProfileById(id),
      history: this.kycApi.getDecisionHistory(id)
    }).subscribe({
      next: ({ profile, history }) => {
        this.profile = profile;
        this.history = history || [];
        this.loadCustomerImage(profile.customerId);
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load KYC history.', 'error');
      }
    });
  }

  private loadCustomerImage(customerId?: number | null): void {
    if (!customerId) {
      this.customerImageUrl = '';
      this.customer = null;
      this.loading = false;
      return;
    }

    this.customerService.getById(customerId).pipe(
      catchError(() => of(null as CustomerResponse | null))
    ).subscribe({
      next: customer => {
        this.customer = customer;
        this.customerImageUrl = customer?.profileImageName
          ? this.fileUploadService.resolveImageUrl(customer.profileImageName)
          : '';
        this.loading = false;
      },
      error: () => {
        this.customer = null;
        this.customerImageUrl = '';
        this.loading = false;
      }
    });
  }
}
