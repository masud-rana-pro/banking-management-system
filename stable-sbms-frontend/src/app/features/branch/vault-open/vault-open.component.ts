import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { BranchApiService } from '../services/branch-api.service';
import { VaultBalanceApiService } from '../services/vault-balance-api.service';

import { BranchResponse } from '../models/branch.model';
import { VaultBalanceRequest } from '../models/vault-balance.model';

@Component({
  selector: 'app-vault-open',
  templateUrl: './vault-open.component.html',
  styleUrls: ['./vault-open.component.scss']
})
export class VaultOpenComponent implements OnInit {

  branches: BranchResponse[] = [];
  branchSearch = '';

  saving = false;

  form: VaultBalanceRequest = {
    branchId: 0,
    balanceDate: '',
    openingBalance: 0,
    remarks: ''
  };

  constructor(
    private router: Router,
    private branchApi: BranchApiService,
    private vaultApi: VaultBalanceApiService
  ) {}

  ngOnInit(): void {
    this.loadBranches();
  }

  loadBranches(): void {
    this.branchApi.getAll('', 'ACTIVE').subscribe({
      next: data => this.branches = data || [],
      error: () => this.branches = []
    });
  }

  onBranchPicked(): void {
    const value = (this.branchSearch || '').trim().toLowerCase();

    const selected = this.branches.find(b =>
      `${b.branchCode} - ${b.branchName}`.toLowerCase() === value ||
      (b.branchCode || '').toLowerCase() === value ||
      (b.branchName || '').toLowerCase() === value
    );

    this.form.branchId = selected ? selected.id : 0;
  }

  save(): void {
    this.onBranchPicked();

    if (!this.validateForm()) return;

    this.saving = true;

    const payload: VaultBalanceRequest = {
      branchId: Number(this.form.branchId),
      balanceDate: this.form.balanceDate,
      openingBalance: Number(this.form.openingBalance),
      remarks: this.form.remarks || ''
    };

    this.vaultApi.open(payload).subscribe({
      next: () => {
        this.saving = false;

        Swal.fire({
          icon: 'success',
          title: 'Vault Opened',
          text: 'Vault opening balance saved successfully.',
          timer: 1400,
          showConfirmButton: false
        });

        this.router.navigate(['/branches/vault']);
      },
      error: err => {
        this.saving = false;

        Swal.fire({
          icon: 'error',
          title: 'Open failed',
          text: err?.error?.message || 'Please check vault opening rules and try again.'
        });
      }
    });
  }

  resetForm(): void {
    this.branchSearch = '';

    this.form = {
      branchId: 0,
      balanceDate: '',
      openingBalance: 0,
      remarks: ''
    };
  }

  cancel(): void {
    this.router.navigate(['/branches/vault']);
  }

  private validateForm(): boolean {
    if (!this.form.branchId || Number(this.form.branchId) <= 0) {
      Swal.fire('Validation', 'Please select a valid branch from suggestion list.', 'warning');
      return false;
    }

    if (!this.form.balanceDate) {
      Swal.fire('Validation', 'Balance date is required.', 'warning');
      return false;
    }

    if (this.form.openingBalance === null || this.form.openingBalance === undefined || Number(this.form.openingBalance) < 0) {
      Swal.fire('Validation', 'Opening balance cannot be negative.', 'warning');
      return false;
    }

    return true;
  }
}