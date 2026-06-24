import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { BranchResponse } from 'src/app/features/branch/models/branch.model';
import { BranchApiService } from 'src/app/features/branch/services/branch-api.service';
import {
  MonthlyClosingDashboardSummaryResponse,
  MonthlyClosingRunRequest,
  MonthlyClosingRunResponse
} from '../../models/monthly-closing.model';
import { MonthlyClosingService } from '../../services/monthly-closing.service';

@Component({
  selector: 'app-monthly-closing-ops',
  templateUrl: './monthly-closing-ops.component.html',
  styleUrls: ['./monthly-closing-ops.component.scss']
})
export class MonthlyClosingOpsComponent implements OnInit {

  loading = false;
  saving = false;
  processing = false;
  branches: BranchResponse[] = [];
  runs: MonthlyClosingRunResponse[] = [];
  selectedRun: MonthlyClosingRunResponse | null = null;
  summary: MonthlyClosingDashboardSummaryResponse | null = null;

  filters = {
    branchId: null as number | null,
    status: '',
    closingMonth: this.currentMonth()
  };

  form: MonthlyClosingRunRequest = {
    branchId: null,
    closingMonth: this.currentMonth(),
    vaultClosedConfirmed: false,
    profitPostedConfirmed: false,
    reversalsReviewed: false,
    statementsGenerated: false,
    remarks: ''
  };

  decisionRemarks = '';

  readonly statusOptions = ['', 'DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'REOPENED'];

  constructor(
    private monthlyClosingService: MonthlyClosingService,
    private branchApiService: BranchApiService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    const branchId = this.accessControl.session?.branchId || null;
    if (branchId) {
      this.filters.branchId = branchId;
      this.form.branchId = branchId;
    }
    this.loadLookups();
    this.loadSummary();
    this.loadRuns();
  }

  loadLookups(): void {
    this.branchApiService.dropdown().subscribe({
      next: branches => {
        this.branches = branches || [];
      },
      error: err => {
        console.error(err);
      }
    });
  }

  loadSummary(): void {
    this.monthlyClosingService.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
      },
      error: err => {
        console.error(err);
      }
    });
  }

  loadRuns(): void {
    this.loading = true;
    this.monthlyClosingService.list(this.filters).subscribe({
      next: data => {
        this.runs = data || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load monthly closing runs.', 'error');
      }
    });
  }

  selectRun(item: MonthlyClosingRunResponse): void {
    this.selectedRun = item;
    this.form = {
      branchId: item.branchId,
      closingMonth: item.closingMonth,
      vaultClosedConfirmed: !!item.vaultClosedConfirmed,
      profitPostedConfirmed: !!item.profitPostedConfirmed,
      reversalsReviewed: !!item.reversalsReviewed,
      statementsGenerated: !!item.statementsGenerated,
      remarks: item.remarks || ''
    };
    this.decisionRemarks = item.remarks || '';
  }

  saveRun(): void {
    if (!this.canCreate) return;
    if (!this.form.branchId || !this.form.closingMonth) {
      Swal.fire('Missing data', 'Branch and closing month are required.', 'warning');
      return;
    }
    this.saving = true;
    this.monthlyClosingService.createOrRefresh(this.form).subscribe({
      next: data => {
        this.saving = false;
        this.selectedRun = data;
        this.selectRun(data);
        this.loadRuns();
        this.loadSummary();
        Swal.fire('Success', 'Monthly closing snapshot saved successfully.', 'success');
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to save monthly closing run.', 'error');
      }
    });
  }

  submitRun(): void {
    if (!this.selectedRun || !this.canSubmit) return;
    this.processing = true;
    this.monthlyClosingService.submit(this.selectedRun.id).subscribe({
      next: data => this.finishAction(data, 'Monthly closing run submitted successfully.'),
      error: err => this.failAction(err, 'Failed to submit monthly closing run.')
    });
  }

  approveRun(): void {
    if (!this.selectedRun || !this.canApprove) return;
    this.processing = true;
    this.monthlyClosingService.approve(this.selectedRun.id, { remarks: this.decisionRemarks }).subscribe({
      next: data => this.finishAction(data, 'Monthly closing run approved successfully.'),
      error: err => this.failAction(err, 'Failed to approve monthly closing run.')
    });
  }

  rejectRun(): void {
    if (!this.selectedRun || !this.canReject) return;
    if (!this.decisionRemarks.trim()) {
      Swal.fire('Remarks required', 'Rejection remarks are required.', 'warning');
      return;
    }
    this.processing = true;
    this.monthlyClosingService.reject(this.selectedRun.id, { remarks: this.decisionRemarks.trim() }).subscribe({
      next: data => this.finishAction(data, 'Monthly closing run rejected successfully.'),
      error: err => this.failAction(err, 'Failed to reject monthly closing run.')
    });
  }

  reopenRun(): void {
    if (!this.selectedRun || !this.canReopen) return;
    if (!this.decisionRemarks.trim()) {
      Swal.fire('Remarks required', 'Reopen remarks are required.', 'warning');
      return;
    }
    this.processing = true;
    this.monthlyClosingService.reopen(this.selectedRun.id, { remarks: this.decisionRemarks.trim() }).subscribe({
      next: data => this.finishAction(data, 'Monthly closing run reopened successfully.'),
      error: err => this.failAction(err, 'Failed to reopen monthly closing run.')
    });
  }

  resetForm(): void {
    const branchId = this.accessControl.session?.branchId || null;
    this.selectedRun = null;
    this.form = {
      branchId,
      closingMonth: this.currentMonth(),
      vaultClosedConfirmed: false,
      profitPostedConfirmed: false,
      reversalsReviewed: false,
      statementsGenerated: false,
      remarks: ''
    };
    this.decisionRemarks = '';
  }

  resetFilters(): void {
    this.filters.status = '';
    this.filters.closingMonth = this.currentMonth();
    this.filters.branchId = this.accessControl.session?.branchId || null;
    this.loadRuns();
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  get canCreate(): boolean {
    return this.can('MONTHLY_CLOSING_CREATE');
  }

  get canSubmit(): boolean {
    return this.can('MONTHLY_CLOSING_SUBMIT');
  }

  get canApprove(): boolean {
    return this.can('MONTHLY_CLOSING_APPROVE');
  }

  get canReject(): boolean {
    return this.can('MONTHLY_CLOSING_REJECT');
  }

  get canReopen(): boolean {
    return this.can('MONTHLY_CLOSING_REOPEN');
  }

  get canChooseBranch(): boolean {
    return !this.accessControl.session?.branchId || this.canApprove || this.canReject || this.canReopen;
  }

  get selectedPendingItems(): string[] {
    return this.selectedRun?.outstandingChecklistItems || [];
  }

  getActionTitle(label: string, permissionCode: string): string {
    return this.can(permissionCode) ? label : `${label} (No permission)`;
  }

  private finishAction(data: MonthlyClosingRunResponse, message: string): void {
    this.processing = false;
    this.selectedRun = data;
    this.selectRun(data);
    this.loadRuns();
    this.loadSummary();
    Swal.fire('Success', message, 'success');
  }

  private failAction(err: any, fallback: string): void {
    console.error(err);
    this.processing = false;
    Swal.fire('Error', err?.error?.message || fallback, 'error');
  }

  currentMonth(): string {
    return new Date().toISOString().slice(0, 10).slice(0, 7) + '-01';
  }
}
