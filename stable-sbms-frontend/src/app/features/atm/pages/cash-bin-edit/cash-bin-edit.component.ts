import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AtmTerminalService } from '../../services/atm-terminal.service';
import {
  CashBinRequest,
  CashBinStatus,
  TerminalDropdownResponse
} from '../../models/terminal.model';

@Component({
  selector: 'app-cash-bin-edit',
  templateUrl: './cash-bin-edit.component.html',
  styleUrls: ['./cash-bin-edit.component.scss']
})
export class CashBinEditComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;
  submitted = false;

  terminals: TerminalDropdownResponse[] = [];

  form: CashBinRequest = {
    terminalId: null,
    binNo: '',
    denomination: null,
    maxCapacity: null,
    currentCount: 0,
    status: 'ACTIVE'
  };

  statusOptions: { value: CashBinStatus; label: string }[] = [
    { value: 'ACTIVE', label: 'ACTIVE' },
    { value: 'LOW_CASH', label: 'LOW CASH' },
    { value: 'FULL', label: 'FULL' },
    { value: 'INACTIVE', label: 'INACTIVE' },
    { value: 'ARCHIVED', label: 'ARCHIVED' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private atmApi: AtmTerminalService
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const terminalIdParam = this.route.snapshot.queryParamMap.get('terminalId');

    this.id = idParam ? Number(idParam) : null;

    if (terminalIdParam && !this.id) {
      this.form.terminalId = Number(terminalIdParam);
    }

    this.loadTerminals();

    if (this.id) {
      this.loadCashBin(this.id);
    }
  }

  loadTerminals(): void {
    this.atmApi.dropdown().subscribe({
      next: data => this.terminals = data || [],
      error: err => {
        console.error(err);
        Swal.fire('Error', 'Failed to load terminal dropdown.', 'error');
      }
    });
  }

  loadCashBin(id: number): void {
    this.loading = true;

    this.atmApi.getCashBinById(id).subscribe({
      next: data => {
        this.form = {
          terminalId: data.terminalId,
          binNo: data.binNo || '',
          denomination: data.denomination,
          maxCapacity: data.maxCapacity,
          currentCount: data.currentCount,
          status: data.status || 'ACTIVE'
        };
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load cash bin information.', 'error');
      }
    });
  }

  save(): void {
    this.submitted = true;
    if (!this.validate()) return;

    this.saving = true;

    const request: CashBinRequest = {
      terminalId: this.toNumberOrNull(this.form.terminalId),
      binNo: this.form.binNo.trim(),
      denomination: this.toNumberOrNull(this.form.denomination),
      maxCapacity: this.toNumberOrNull(this.form.maxCapacity),
      currentCount: this.toNumberOrNull(this.form.currentCount) || 0,
      status: this.form.status || 'ACTIVE'
    };

    const call = this.id
      ? this.atmApi.updateCashBin(this.id, request)
      : this.atmApi.createCashBin(request);

    call.subscribe({
      next: () => {
        this.saving = false;

        Swal.fire({
          icon: 'success',
          title: this.id ? 'Updated' : 'Created',
          text: this.id ? 'Cash bin updated successfully.' : 'Cash bin created successfully.',
          timer: 1400,
          showConfirmButton: false
        });

        this.router.navigate(['/atm/cash-bins']);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire({
          icon: 'error',
          title: 'Save failed',
          text: err?.error?.message || 'Please check cash bin information and try again.'
        });
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/atm/cash-bins']);
  }

  get selectedTerminalLabel(): string {
    const terminal = this.terminals.find(t => Number(t.id) === Number(this.form.terminalId));
    return terminal ? `${terminal.terminalCode} - ${terminal.terminalName}` : 'No terminal selected';
  }

  get currentAmount(): number {
    const denomination = Number(this.form.denomination || 0);
    const currentCount = Number(this.form.currentCount || 0);
    return denomination * currentCount;
  }

  get capacityPercent(): number {
    const max = Number(this.form.maxCapacity || 0);
    const current = Number(this.form.currentCount || 0);

    if (max <= 0) return 0;

    const percent = Math.round((current / max) * 100);
    return Math.max(0, Math.min(percent, 100));
  }

  private validate(): boolean {
    if (!this.form.terminalId || Number(this.form.terminalId) <= 0) {
      Swal.fire('Validation', 'Terminal is required.', 'warning');
      return false;
    }

    if (!this.form.binNo?.trim()) {
      Swal.fire('Validation', 'Bin number is required.', 'warning');
      return false;
    }

    if (!this.form.denomination || Number(this.form.denomination) <= 0) {
      Swal.fire('Validation', 'Valid denomination is required.', 'warning');
      return false;
    }

    if (!this.form.maxCapacity || Number(this.form.maxCapacity) <= 0) {
      Swal.fire('Validation', 'Max capacity must be greater than zero.', 'warning');
      return false;
    }

    const currentCount = Number(this.form.currentCount || 0);

    if (currentCount < 0) {
      Swal.fire('Validation', 'Current count cannot be negative.', 'warning');
      return false;
    }

    if (currentCount > Number(this.form.maxCapacity)) {
      Swal.fire('Validation', 'Current count cannot exceed max capacity.', 'warning');
      return false;
    }

    return true;
  }

  private toNumberOrNull(value: any): number | null {
    if (value === null || value === undefined || value === '') return null;
    return Number(value);
  }
}