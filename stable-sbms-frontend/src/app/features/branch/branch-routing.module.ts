import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { BranchListComponent } from './branch-list/branch-list.component';
import { BranchEditComponent } from './branch-edit/branch-edit.component';
import { BranchViewComponent } from './branch-view/branch-view.component';
import { BranchAssignmentComponent } from './branch-assignment/branch-assignment.component';
import { TellerLimitComponent } from './teller-limit/teller-limit.component';
import { CashLedgerListComponent } from './cash-ledger-list/cash-ledger-list.component';
import { VaultViewComponent } from './vault-view/vault-view.component';
import { VaultOpenComponent } from './vault-open/vault-open.component';
import { VaultListComponent } from './vault-list/vault-list.component';
import { BranchDashboardComponent } from './branch-dashboard/branch-dashboard.component';
import { EodSummaryComponent } from './eod-summary/eod-summary.component';
import { InterBranchTransferComponent } from './inter-branch-transfer/inter-branch-transfer.component';


const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: BranchDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_MANAGEMENT_ACCESS' } },
  { path: 'list', component: BranchListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_MANAGEMENT_ACCESS' } },
  { path: 'new', component: BranchEditComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_CREATE' } },
  { path: 'assignments', component: BranchAssignmentComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_ASSIGN_USER' } },
  { path: 'teller-limits', component: TellerLimitComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_TELLER_LIMIT_MANAGE' } },
  { path: 'teller-limits/new', component: TellerLimitComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_TELLER_LIMIT_MANAGE' } },
  { path: 'teller-limits/:id', component: TellerLimitComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_TELLER_LIMIT_MANAGE' } },
  { path: 'vault', component: VaultListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_VAULT_MANAGE' } },
  { path: 'vault/open', component: VaultOpenComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_VAULT_MANAGE' } },
  { path: 'vault/:id', component: VaultViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_VAULT_MANAGE' } },
  { path: 'cash-ledger', component: CashLedgerListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_MANAGEMENT_ACCESS' } },
  { path: 'inter-branch-transfer', component: InterBranchTransferComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_MANAGEMENT_ACCESS' } },
  { path: 'eod-summary', component: EodSummaryComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_MANAGEMENT_ACCESS' } },
  { path: ':id/edit', component: BranchEditComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_EDIT' } },
  { path: ':id', component: BranchViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'BRANCH_MANAGEMENT_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BranchRoutingModule { }
