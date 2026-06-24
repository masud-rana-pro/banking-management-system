import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AtmTerminalService } from '../../services/atm-terminal.service';
import { TerminalRequest } from '../../models/terminal.model';
import { BranchResponse } from 'src/app/features/branch/models/branch.model';
import { BranchApiService } from 'src/app/features/branch/services/branch-api.service';

@Component({
  selector: 'app-terminal-edit',
  templateUrl: './terminal-edit.component.html',
  styleUrls: ['./terminal-edit.component.scss']
})
export class TerminalEditComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;
  submitted = false;
  branches: BranchResponse[] = [];

  form: TerminalRequest = {
    terminalCode: '',
    terminalName: '',
    terminalType: '',
    branchId: null,
    locationNote: '',
    ipAddress: '',
    serialNo: '',
    vendorName: '',
    installDate: '',
    status: 'ACTIVE'
  };

  terminalTypeOptions = [
    { value: '', label: 'Select Terminal Type' },
    { value: 'ATM', label: 'ATM' },
    { value: 'CDM', label: 'CDM' },
    { value: 'ATM_CDM', label: 'ATM + CDM' }
  ];

  statusOptions = [
    { value: 'ACTIVE', label: 'ACTIVE' },
    { value: 'INACTIVE', label: 'INACTIVE' },
    { value: 'MAINTENANCE', label: 'MAINTENANCE' },
    { value: 'OUT_OF_SERVICE', label: 'OUT OF SERVICE' },
    { value: 'ARCHIVED', label: 'ARCHIVED' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private atmApi: AtmTerminalService,
    private branchApi: BranchApiService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    this.loadBranches();

    if (this.id) {
      this.loadTerminal(this.id);
    }
  }

  loadBranches(): void {
    this.branchApi.getAll('', 'ACTIVE').subscribe({
      next: data => this.branches = data || [],
      error: err => {
        console.error(err);
        this.branches = [];
      }
    });
  }

  loadTerminal(id: number): void {
    this.loading = true;

    this.atmApi.getById(id).subscribe({
      next: data => {
        this.form = {
          terminalCode: data.terminalCode || '',
          terminalName: data.terminalName || '',
          terminalType: data.terminalType || '',
          branchId: data.branchId || null,
          locationNote: data.locationNote || '',
          ipAddress: data.ipAddress || '',
          serialNo: data.serialNo || '',
          vendorName: data.vendorName || '',
          installDate: data.installDate || '',
          status: data.status || 'ACTIVE'
        };

        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;

        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to load terminal information.'
        });
      }
    });
  }

  save(): void {
    this.submitted = true;

    if (!this.validate()) return;

    this.saving = true;

    const request: TerminalRequest = {
      ...this.form,
      terminalCode: this.form.terminalCode.trim(),
      terminalName: this.form.terminalName.trim(),
      terminalType: this.form.terminalType,
      branchId: this.toNumberOrNull(this.form.branchId),
      locationNote: this.form.locationNote?.trim(),
      ipAddress: this.form.ipAddress?.trim(),
      serialNo: this.form.serialNo?.trim(),
      vendorName: this.form.vendorName?.trim(),
      installDate: this.form.installDate,
      status: this.form.status || 'ACTIVE'
    };

    const call = this.id
      ? this.atmApi.update(this.id, request)
      : this.atmApi.create(request);

    call.subscribe({
      next: () => {
        this.saving = false;

        Swal.fire({
          icon: 'success',
          title: this.id ? 'Updated' : 'Created',
          text: this.id
            ? 'ATM/CDM terminal updated successfully.'
            : 'ATM/CDM terminal created successfully.',
          timer: 1400,
          showConfirmButton: false
        });

        this.router.navigate(['/atm/terminals']);
      },
      error: err => {
        console.error(err);
        this.saving = false;

        Swal.fire({
          icon: 'error',
          title: 'Save failed',
          text: err?.error?.message || 'Please check terminal information and try again.'
        });
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/atm/terminals']);
  }

  get selectedBranchLabel(): string {
    const branch = this.branches.find(item => Number(item.id) === Number(this.form.branchId));
    return branch ? `${branch.branchCode} - ${branch.branchName}` : 'No branch selected';
  }

  private validate(): boolean {
    if (!this.form.terminalCode?.trim()) {
      Swal.fire('Validation', 'Terminal code is required.', 'warning');
      return false;
    }

    if (!this.form.terminalName?.trim()) {
      Swal.fire('Validation', 'Terminal name is required.', 'warning');
      return false;
    }

    if (!this.form.terminalType) {
      Swal.fire('Validation', 'Terminal type is required.', 'warning');
      return false;
    }

    if (!this.form.branchId || Number(this.form.branchId) <= 0) {
      Swal.fire('Validation', 'Branch ID is required.', 'warning');
      return false;
    }

    if (!this.form.installDate) {
      Swal.fire('Validation', 'Install date is required.', 'warning');
      return false;
    }

    return true;
  }

  private toNumberOrNull(value: any): number | null {
    if (value === null || value === undefined || value === '') return null;
    return Number(value);
  }
}
