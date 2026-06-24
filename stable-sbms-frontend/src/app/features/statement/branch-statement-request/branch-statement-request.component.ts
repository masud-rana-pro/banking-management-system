import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { BranchResponse } from '../../branch/models/branch.model';
import { BranchApiService } from '../../branch/services/branch-api.service';
import { BranchStatementRequestRequest } from '../models/statement.model';
import { StatementService } from '../services/statement.service';

@Component({
  selector: 'app-branch-statement-request',
  templateUrl: './branch-statement-request.component.html',
  styleUrls: ['./branch-statement-request.component.scss']
})
export class BranchStatementRequestComponent implements OnInit {

  loading = false;
  saving = false;
  branches: BranchResponse[] = [];
  model: BranchStatementRequestRequest = {
    branchId: null,
    dateFrom: '',
    dateTo: '',
    requestedBy: 'SYSTEM'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private branchApi: BranchApiService,
    private statementApi: StatementService,
    private accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    const today = new Date().toISOString().slice(0, 10);
    const monthStart = `${today.slice(0, 8)}01`;
    this.model.dateFrom = monthStart;
    this.model.dateTo = today;
    this.model.requestedBy = this.accessControl.session?.username || 'SYSTEM';
    if (this.accessControl.session?.branchId) {
      this.model.branchId = this.accessControl.session.branchId;
    }
    this.loadBranches();
  }

  loadBranches(): void {
    this.loading = true;
    this.branchApi.getAll().subscribe({
      next: branches => {
        this.branches = branches || [];
        const branchId = Number(this.route.snapshot.queryParamMap.get('branchId') || 0);
        if (branchId > 0 && !this.accessControl.session?.branchId) {
          this.model.branchId = branchId;
        }
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load branch statement request form.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.model.branchId || !this.model.dateFrom || !this.model.dateTo) {
      Swal.fire('Validation', 'Branch and date range are required.', 'warning');
      return;
    }
    this.saving = true;
    this.statementApi.requestBranchStatement(this.model).subscribe({
      next: response => {
        this.saving = false;
        Swal.fire('Success', 'Branch statement request accepted. The PDF will be ready shortly.', 'success');
        this.router.navigate(['/statement/branch', response.id]);
      },
      error: err => {
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to generate branch statement.', 'error');
      }
    });
  }

  get isBranchSelectionLocked(): boolean {
    return !!this.accessControl.session?.branchId;
  }
}
