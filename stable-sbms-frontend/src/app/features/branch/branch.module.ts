import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { BranchRoutingModule } from './branch-routing.module';
import { SharedModule } from '../../shared/shared.module';

import { BranchListComponent } from './branch-list/branch-list.component';
import { BranchAssignmentComponent } from './branch-assignment/branch-assignment.component';
import { TellerLimitComponent } from './teller-limit/teller-limit.component';
import { BranchViewComponent } from './branch-view/branch-view.component';
import { BranchEditComponent } from './branch-edit/branch-edit.component';

import { VaultListComponent } from './vault-list/vault-list.component';
import { VaultOpenComponent } from './vault-open/vault-open.component';
import { VaultViewComponent } from './vault-view/vault-view.component';
import { CashLedgerListComponent } from './cash-ledger-list/cash-ledger-list.component';
import { BranchDashboardComponent } from './branch-dashboard/branch-dashboard.component';
import { EodSummaryComponent } from './eod-summary/eod-summary.component';
import { InterBranchTransferComponent } from './inter-branch-transfer/inter-branch-transfer.component';

@NgModule({
  declarations: [
    BranchDashboardComponent,
    BranchListComponent,
    BranchAssignmentComponent,
    TellerLimitComponent,
    BranchViewComponent,
    BranchEditComponent,
    VaultListComponent,
    VaultOpenComponent,
    VaultViewComponent,
    CashLedgerListComponent,
    EodSummaryComponent,
    InterBranchTransferComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    BranchRoutingModule
  ]
})
export class BranchModule { }
