import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AtmTerminalService } from '../../services/atm-terminal.service';
import {
  CashBinResponse,
  ReplenishmentRequest,
  TerminalDropdownResponse,
  UserSummaryResponse
} from '../../models/terminal.model';

@Component({
  selector: 'app-replenishment-edit',
  templateUrl: './replenishment-edit.component.html',
  styleUrls: ['./replenishment-edit.component.scss']
})
export class ReplenishmentEditComponent implements OnInit {

  terminals: TerminalDropdownResponse[] = [];
  cashBins: CashBinResponse[] = [];
  users: UserSummaryResponse[] = [];

  loading = false;
  saving = false;

  form: ReplenishmentRequest = {
    terminalId: null,
    replenishmentDate: new Date().toISOString().slice(0, 10),
    binNo: '',
    quantityAdded: null,
    performedBy: null,
    remarks: '',
    status: 'COMPLETED'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private atmApi: AtmTerminalService
  ) {}

  ngOnInit(): void {
    const terminalIdParam = this.route.snapshot.queryParamMap.get('terminalId');
    if (terminalIdParam) {
      this.form.terminalId = Number(terminalIdParam);
    }

    this.loadDependencies();
  }

  loadDependencies(): void {
    this.loading = true;

    this.atmApi.dropdown().subscribe({
      next: data => {
        this.terminals = data || [];
        if (this.form.terminalId) {
          this.loadCashBins(this.form.terminalId);
        }
      }
    });

    this.atmApi.getUsers().subscribe({
      next: data => this.users = data || [],
      error: err => console.error(err)
    });

    this.loading = false;
  }

  onTerminalChange(): void {
    this.form.binNo = '';
    if (this.form.terminalId) {
      this.loadCashBins(this.form.terminalId);
    } else {
      this.cashBins = [];
    }
  }

  loadCashBins(terminalId: number): void {
    this.atmApi.getCashBinsByTerminal(terminalId).subscribe({
      next: data => this.cashBins = data || [],
      error: err => {
        console.error(err);
        this.cashBins = [];
      }
    });
  }

  save(): void {
    if (!this.validate()) return;

    this.saving = true;

    this.atmApi.createReplenishment({
      ...this.form,
      terminalId: Number(this.form.terminalId),
      quantityAdded: Number(this.form.quantityAdded),
      performedBy: Number(this.form.performedBy),
      binNo: this.form.binNo.trim(),
      remarks: this.form.remarks.trim()
    }).subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Saved', 'Replenishment saved successfully.', 'success');
        this.router.navigate(['/atm/replenishments']);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save replenishment.', 'error');
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/atm/replenishments']);
  }

  get selectedTerminalLabel(): string {
    const terminal = this.terminals.find(item => Number(item.id) === Number(this.form.terminalId));
    return terminal ? `${terminal.terminalCode} - ${terminal.terminalName}` : 'No terminal selected';
  }

  get selectedBin(): CashBinResponse | null {
    return this.cashBins.find(item => item.binNo === this.form.binNo) || null;
  }

  get calculatedAmount(): number {
    return Number(this.selectedBin?.denomination || 0) * Number(this.form.quantityAdded || 0);
  }

  private validate(): boolean {
    if (!this.form.terminalId) {
      Swal.fire('Validation', 'Terminal is required.', 'warning');
      return false;
    }

    if (!this.form.binNo.trim()) {
      Swal.fire('Validation', 'Bin is required.', 'warning');
      return false;
    }

    if (!this.form.quantityAdded || Number(this.form.quantityAdded) <= 0) {
      Swal.fire('Validation', 'Quantity must be greater than zero.', 'warning');
      return false;
    }

    if (!this.form.performedBy || Number(this.form.performedBy) <= 0) {
      Swal.fire('Validation', 'Performed by user is required.', 'warning');
      return false;
    }

    return true;
  }
}
