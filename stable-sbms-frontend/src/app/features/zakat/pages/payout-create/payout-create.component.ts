import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CharityBeneficiaryResponse, CharityFundResponse, CharityPayoutRequest } from '../../models/zakat.model';
import { ZakatService } from '../../services/zakat.service';

@Component({
  selector: 'app-payout-create',
  templateUrl: './payout-create.component.html',
  styleUrls: ['./payout-create.component.scss']
})
export class PayoutCreateComponent implements OnInit {

  loading = false;
  beneficiaries: CharityBeneficiaryResponse[] = [];
  fund: CharityFundResponse[] = [];
  form: CharityPayoutRequest = {
    beneficiaryId: null,
    payoutDate: new Date().toISOString().slice(0, 10),
    amount: null,
    approvedBy: '',
    remarks: ''
  };
  selectedBeneficiary: CharityBeneficiaryResponse | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private zakatService: ZakatService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.form.beneficiaryId = this.toNumber(this.route.snapshot.queryParamMap.get('beneficiaryId'));
    this.load();
  }

  load(): void {
    this.loading = true;
    forkJoin({
      beneficiaries: this.zakatService.getBeneficiaries(),
      fund: this.zakatService.getCharityFund()
    }).subscribe({
      next: ({ beneficiaries, fund }) => {
        this.beneficiaries = beneficiaries.filter(item => item.status === 'ACTIVE');
        this.fund = fund;
        this.syncSelectedBeneficiary();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load payout prerequisites.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.form.beneficiaryId || !this.form.amount || !this.form.approvedBy.trim()) {
      Swal.fire('Missing data', 'Beneficiary, amount and approved by are required.', 'warning');
      return;
    }
    this.zakatService.createPayout(this.form).subscribe({
      next: () => {
        Swal.fire('Success', 'Charity payout created successfully.', 'success');
        this.router.navigate(['/zakat/payouts']);
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to create payout.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/zakat/payouts']);
  }

  get currentBalance(): number {
    return this.fund.length ? this.fund[0].balanceAfter : 0;
  }

  syncSelectedBeneficiary(): void {
    this.selectedBeneficiary = this.beneficiaries.find(item => item.id === this.form.beneficiaryId) || null;
  }

  previewBeneficiaryProof(): void {
    if (!this.selectedBeneficiary?.proofDocumentName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.selectedBeneficiary.proofDocumentName), '_blank');
  }

  private toNumber(value: string | null): number | null {
    if (!value) return null;
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
  }
}
