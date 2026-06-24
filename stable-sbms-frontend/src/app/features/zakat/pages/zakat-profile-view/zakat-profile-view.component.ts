import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { CharityFundResponse, CharityPayoutResponse, ZakatProfileResponse, formatEnumLabel } from '../../models/zakat.model';
import { ZakatService } from '../../services/zakat.service';

@Component({
  selector: 'app-zakat-profile-view',
  templateUrl: './zakat-profile-view.component.html',
  styleUrls: ['./zakat-profile-view.component.scss']
})
export class ZakatProfileViewComponent implements OnInit {

  id = 0;
  loading = false;
  item: ZakatProfileResponse | null = null;
  fundMovements: CharityFundResponse[] = [];
  payouts: CharityPayoutResponse[] = [];
  customer: CustomerResponse | null = null;
  customerImageUrl = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private zakatService: ZakatService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.loading = true;
    forkJoin({
      profile: this.zakatService.getProfileById(this.id),
      fund: this.zakatService.getCharityFund(),
      payouts: this.zakatService.getPayouts()
    }).subscribe({
      next: ({ profile, fund, payouts }) => {
        this.item = profile;
        this.fundMovements = fund.slice(0, 10);
        this.payouts = payouts.slice(0, 10);
        this.loadCustomerProfile(profile.customerId);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load zakat profile view.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/zakat/profiles']);
  }

  openCalc(): void {
    this.router.navigate(['/zakat/calc-run'], { queryParams: { profileId: this.id } });
  }

  openFund(): void {
    this.router.navigate(['/zakat/charity-fund']);
  }

  openPayouts(): void {
    this.router.navigate(['/zakat/payouts']);
  }

  previewSheet(): void {
    if (!this.item) return;
    window.open(this.zakatService.getProfileSheetPreviewUrl(this.item.id), '_blank');
  }

  downloadSheet(): void {
    if (!this.item) return;
    const link = document.createElement('a');
    link.href = this.zakatService.getProfileSheetDownloadUrl(this.item.id);
    link.target = '_blank';
    link.rel = 'noopener';
    link.click();
  }

  printSheet(): void {
    this.previewSheet();
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  previewProof(): void {
    if (!this.item?.proofDocumentName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.item.proofDocumentName), '_blank');
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
