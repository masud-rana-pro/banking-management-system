import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AtmTerminalService } from '../../services/atm-terminal.service';
import {
  ReconciliationRequest,
  TerminalDropdownResponse,
  UserSummaryResponse
} from '../../models/terminal.model';

@Component({
  selector: 'app-reconciliation-edit',
  templateUrl: './reconciliation-edit.component.html',
  styleUrls: ['./reconciliation-edit.component.scss']
})
export class ReconciliationEditComponent implements OnInit {

  terminals: TerminalDropdownResponse[] = [];
  users: UserSummaryResponse[] = [];
  saving = false;

  form: ReconciliationRequest = {
    terminalId: null,
    reconDate: new Date().toISOString().slice(0, 10),
    systemAmount: null,
    physicalAmount: null,
    approvedBy: null,
    remarks: ''
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

    this.atmApi.dropdown().subscribe({
      next: data => this.terminals = data || [],
      error: err => console.error(err)
    });

    this.atmApi.getUsers().subscribe({
      next: data => this.users = data || [],
      error: err => console.error(err)
    });
  }

  save(): void {
    if (!this.validate()) return;

    this.saving = true;

    this.atmApi.createReconciliation({
      ...this.form,
      terminalId: Number(this.form.terminalId),
      systemAmount: Number(this.form.systemAmount),
      physicalAmount: Number(this.form.physicalAmount),
      approvedBy: this.form.approvedBy ? Number(this.form.approvedBy) : null,
      remarks: this.form.remarks.trim()
    }).subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Saved', 'Reconciliation saved successfully.', 'success');
        this.router.navigate(['/atm/reconciliations']);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save reconciliation.', 'error');
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/atm/reconciliations']);
  }

  get selectedTerminalLabel(): string {
    const terminal = this.terminals.find(item => Number(item.id) === Number(this.form.terminalId));
    return terminal ? `${terminal.terminalCode} - ${terminal.terminalName}` : 'No terminal selected';
  }

  get varianceAmount(): number {
    return Number(this.form.physicalAmount || 0) - Number(this.form.systemAmount || 0);
  }

  get previewStatus(): string {
    if (this.form.approvedBy) return 'APPROVED';
    return this.varianceAmount === 0 ? 'MATCHED' : 'VARIANCE_FOUND';
  }

  private validate(): boolean {
    if (!this.form.terminalId) {
      Swal.fire('Validation', 'Terminal is required.', 'warning');
      return false;
    }

    if (this.form.systemAmount === null || Number(this.form.systemAmount) < 0) {
      Swal.fire('Validation', 'Valid system amount is required.', 'warning');
      return false;
    }

    if (this.form.physicalAmount === null || Number(this.form.physicalAmount) < 0) {
      Swal.fire('Validation', 'Valid physical amount is required.', 'warning');
      return false;
    }

    return true;
  }
}
