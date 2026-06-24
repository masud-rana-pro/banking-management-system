import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { BranchApiService } from '../services/branch-api.service';
import { VaultBalanceApiService } from '../services/vault-balance-api.service';

import { BranchResponse } from '../models/branch.model';
import { VaultBalanceResponse } from '../models/vault-balance.model';

@Component({
  selector: 'app-vault-view',
  templateUrl: './vault-view.component.html',
  styleUrls: ['./vault-view.component.scss']
})
export class VaultViewComponent implements OnInit {

  id!: number;
  vault: VaultBalanceResponse | null = null;
  branch: BranchResponse | null = null;

  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private branchApi: BranchApiService,
    private vaultApi: VaultBalanceApiService
  ) { }

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadVault();
  }

  loadVault(): void {
    this.loading = true;

    this.vaultApi.getById(this.id).subscribe({
      next: data => {
        this.vault = data;
        this.loadBranch(data.branchId);
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load vault detail.', 'error');
      }
    });
  }

  loadBranch(branchId: number): void {
    this.branchApi.getById(branchId).subscribe({
      next: data => {
        this.branch = data;
        this.loading = false;
      },
      error: () => {
        this.branch = null;
        this.loading = false;
      }
    });
  }

  closeVault(): void {
    if (!this.vault || this.vault.isClosed) return;

    Swal.fire({
      title: 'Close Vault',
      html: `
        <input id="cashIn" type="number" min="0" class="swal2-input" placeholder="Total Cash In">
        <input id="cashOut" type="number" min="0" class="swal2-input" placeholder="Total Cash Out">
        <textarea id="remarks" class="swal2-textarea" placeholder="Closing remarks"></textarea>
      `,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Close Vault',
      cancelButtonText: 'Cancel',
      preConfirm: () => {
        const cashIn = Number((document.getElementById('cashIn') as HTMLInputElement).value || 0);
        const cashOut = Number((document.getElementById('cashOut') as HTMLInputElement).value || 0);
        const remarks = (document.getElementById('remarks') as HTMLTextAreaElement).value || '';

        if (cashIn < 0 || cashOut < 0) {
          Swal.showValidationMessage('Cash in/out cannot be negative.');
          return;
        }

        const closingBalance = Number(this.vault?.openingBalance || 0) + cashIn - cashOut;

        if (closingBalance < 0) {
          Swal.showValidationMessage('Closing balance cannot be negative.');
          return;
        }

        return { totalCashIn: cashIn, totalCashOut: cashOut, remarks };
      }
    }).then(result => {
      if (!result.isConfirmed || !result.value || !this.vault) return;

      this.vaultApi.close(this.vault.id, result.value).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Vault Closed',
            text: 'Vault closed and ledger updated successfully.',
            timer: 1400,
            showConfirmButton: false
          });

          this.loadVault();
        },
        error: err => {
          Swal.fire({
            icon: 'error',
            title: 'Close failed',
            text: err?.error?.message || 'Please check vault close rules and try again.'
          });
        }
      });
    });
  }

  back(): void {
    this.router.navigate(['/branches/vault']);
  }

  previewReport(): void {
    if (!this.vault) return;
    window.open(this.vaultApi.getReportPreviewUrl(this.vault.id), '_blank');
  }

  downloadReport(): void {
    if (!this.vault) return;
    const link = document.createElement('a');
    link.href = this.vaultApi.getReportDownloadUrl(this.vault.id);
    link.target = '_blank';
    link.rel = 'noopener';
    link.click();
  }

  printReport(): void {
    this.previewReport();
  }

  goLedger(): void {
    this.router.navigate(['/branches/cash-ledger']);
  }

  formatAmount(value: number): string {
    return Number(value || 0).toLocaleString('en-BD', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }
}
