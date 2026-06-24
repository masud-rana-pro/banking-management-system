import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CharityBeneficiaryRequest, CharityBeneficiaryResponse } from '../../models/zakat.model';
import { ZakatService } from '../../services/zakat.service';

@Component({
  selector: 'app-beneficiary-create',
  templateUrl: './beneficiary-create.component.html',
  styleUrls: ['./beneficiary-create.component.scss']
})
export class BeneficiaryCreateComponent implements OnInit {

  id: number | null = null;
  loading = false;
  form: CharityBeneficiaryRequest = {
    beneficiaryCode: '',
    beneficiaryName: '',
    mobile: '',
    address: '',
    proofDocumentName: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private zakatService: ZakatService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.id = this.toNumber(this.route.snapshot.paramMap.get('id'));
    if (this.id) {
      this.load(this.id);
    }
  }

  load(id: number): void {
    this.loading = true;
    this.zakatService.getBeneficiaries().subscribe({
      next: data => {
        const found = data.find(item => item.id === id);
        if (!found) {
          this.loading = false;
          Swal.fire('Error', 'Beneficiary not found.', 'error');
          return;
        }
        this.patch(found);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load beneficiary.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.form.beneficiaryName.trim()) {
      Swal.fire('Missing data', 'Beneficiary name is required.', 'warning');
      return;
    }

    const request$ = this.id
      ? this.zakatService.updateBeneficiary(this.id, this.form)
      : this.zakatService.createBeneficiary(this.form);

    request$.subscribe({
      next: () => {
        Swal.fire('Success', `Beneficiary ${this.id ? 'updated' : 'created'} successfully.`, 'success');
        this.router.navigate(['/zakat/beneficiaries']);
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to save beneficiary.', 'error');
      }
    });
  }

  back(): void {
    this.router.navigate(['/zakat/beneficiaries']);
  }

  onProofSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.fileUploadService.uploadDocument(file).subscribe({
      next: result => {
        this.form.proofDocumentName = result.fileName;
        Swal.fire('Uploaded', 'Beneficiary proof document uploaded successfully.', 'success');
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to upload beneficiary proof document.', 'error');
      }
    });
  }

  previewProof(): void {
    if (!this.form.proofDocumentName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.form.proofDocumentName), '_blank');
  }

  private patch(item: CharityBeneficiaryResponse): void {
    this.form = {
      beneficiaryCode: item.beneficiaryCode,
      beneficiaryName: item.beneficiaryName,
      mobile: item.mobile || '',
      address: item.address || '',
      proofDocumentName: item.proofDocumentName || ''
    };
  }

  private toNumber(value: string | null): number | null {
    if (!value) return null;
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
  }
}
