import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';

import { BranchApiService } from '../services/branch-api.service';
import { BranchAssignmentApiService } from '../services/branch-assignment-api.service';
import { TellerLimitApiService } from '../services/teller-limit-api.service';

import { BranchResponse } from '../models/branch.model';
import { BranchAssignmentResponse } from '../models/branch-assignment.model';
import { TellerLimitResponse } from '../models/teller-limit.model';

import { VaultBalanceApiService } from '../services/vault-balance-api.service';
import { BranchCashLedgerApiService } from '../services/branch-cash-ledger-api.service';

import { VaultBalanceResponse } from '../models/vault-balance.model';
import { BranchCashLedgerResponse } from '../models/branch-cash-ledger.model';

@Component({
  selector: 'app-branch-view',
  templateUrl: './branch-view.component.html',
  styleUrls: ['./branch-view.component.scss']
})
export class BranchViewComponent implements OnInit {

  id!: number;
  branch: BranchResponse | null = null;

  assignments: BranchAssignmentResponse[] = [];
  tellerLimits: TellerLimitResponse[] = [];

  vaults: VaultBalanceResponse[] = [];
  latestVault: VaultBalanceResponse | null = null;

  ledgerEntries: BranchCashLedgerResponse[] = [];
  loading = false;
  detailLoading = false;
  userImageMap: Record<string, string> = {};
  userDisplayMap: Record<number, string> = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService,
    private branchApi: BranchApiService,
    private assignmentApi: BranchAssignmentApiService,
    private tellerLimitApi: TellerLimitApiService,
    private vaultApi: VaultBalanceApiService,
    private ledgerApi: BranchCashLedgerApiService
  ) { }

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadUsers();
    this.loadBranch();
    this.loadRelatedData();
  }

  loadBranch(): void {
    this.loading = true;

    this.branchApi.getById(this.id).subscribe({
      next: data => {
        this.branch = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load branch details.', 'error');
      }
    });
  }

  loadRelatedData(): void {
    this.detailLoading = true;

    this.assignmentApi.getAll(this.id, '').subscribe({
      next: assignments => {
        this.assignments = assignments || [];

        this.tellerLimitApi.getAll('', this.id, null).subscribe({
          next: limits => {
            this.tellerLimits = limits || [];

            this.vaultApi.getAll(this.id).subscribe({
              next: vaults => {
                this.vaults = vaults || [];
                this.latestVault = this.vaults[0] || null;

                this.ledgerApi.getAll(this.id).subscribe({
                  next: ledger => {
                    this.ledgerEntries = ledger || [];
                    this.detailLoading = false;
                  },
                  error: () => this.detailLoading = false
                });
              },
              error: () => this.detailLoading = false
            });

          },
          error: () => this.detailLoading = false
        });
      },
      error: () => {
        this.assignments = [];
        this.tellerLimits = [];
        this.detailLoading = false;
      }
    });

  }

  cancel(): void {
    this.router.navigate(['/branches']);
  }

  edit(): void {
    this.router.navigate(['/branches', this.id, 'edit']);
  }

  goAssignments(): void {
    this.router.navigate(['/branches/assignments']);
  }

  goTellerLimits(): void {
    this.router.navigate(['/branches/teller-limits']);
  }

  goVaults(): void {
    this.router.navigate(['/branches/vault']);
  }

  goLedger(): void {
    this.router.navigate(['/branches/cash-ledger']);
  }

  goEodSummary(): void {
    this.router.navigate(['/branches/eod-summary'], { queryParams: { branchId: this.id } });
  }

  goInterBranchTransfer(): void {
    this.router.navigate(['/branches/inter-branch-transfer']);
  }

  goBranchStatements(): void {
    this.router.navigate(['/statement/branch/list']);
  }

  newBranchStatement(): void {
    this.router.navigate(['/statement/branch/request'], { queryParams: { branchId: this.id } });
  }

  activeAssignmentsCount(): number {
    return this.assignments.filter(a => a.status === 'ACTIVE').length;
  }

  activeTellerCount(): number {
    return this.assignments.filter(a => a.status === 'ACTIVE' && a.assignmentRole === 'TELLER').length;
  }

  activeLimitCount(): number {
    return this.tellerLimits.filter(l => l.status === 'ACTIVE').length;
  }

  getUserImageUrl(userId?: number | null): string {
    if (!userId) return '';
    return this.userImageMap[String(userId)] || '';
  }

  getUserDisplay(userId?: number | null): string {
    if (!userId) return '-';
    return this.userDisplayMap[userId] || `USER-${userId}`;
  }

  formatAmount(value: number): string {
    return Number(value || 0).toLocaleString('en-BD', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  private loadUsers(): void {
    this.userApi.getAll().subscribe({
      next: users => {
        this.userImageMap = this.buildUserImageMap(users || []);
      },
      error: () => {
        this.userImageMap = {};
      }
    });
  }

  private buildUserImageMap(users: UserResponse[]): Record<string, string> {
    return users.reduce<Record<string, string>>((acc, user) => {
      if (user.id && user.profileImageName) {
        acc[String(user.id)] = this.fileUploadService.resolveImageUrl(user.profileImageName);
      }
      if (user.id) {
        this.userDisplayMap[user.id] = user.fullName || user.username;
      }
      return acc;
    }, {});
  }

}
