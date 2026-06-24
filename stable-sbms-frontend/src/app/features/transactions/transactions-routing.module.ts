import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { TransactionDashboardComponent } from './pages/transaction-dashboard/transaction-dashboard.component';
import { CashDepositComponent } from './pages/cash-deposit/cash-deposit.component';
import { CashWithdrawComponent } from './pages/cash-withdraw/cash-withdraw.component';
import { FundTransferComponent } from './pages/fund-transfer/fund-transfer.component';
import { ChequeClearingComponent } from './pages/cheque-clearing/cheque-clearing.component';
import { StandingInstructionListComponent } from './pages/standing-instruction-list/standing-instruction-list.component';
import { StandingInstructionCreateComponent } from './pages/standing-instruction-create/standing-instruction-create.component';
import { TransactionListComponent } from './pages/transaction-list/transaction-list.component';
import { TransactionViewComponent } from './pages/transaction-view/transaction-view.component';
import { TransactionReversalComponent } from './pages/transaction-reversal/transaction-reversal.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: TransactionDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'TRANSACTIONS_ACCESS' } },
  { path: 'deposit', component: CashDepositComponent, canActivate: [PermissionGuard], data: { permissionCode: 'TRANSACTION_DEPOSIT' } },
  { path: 'withdraw', component: CashWithdrawComponent, canActivate: [PermissionGuard], data: { permissionCode: 'TRANSACTION_WITHDRAW' } },
  { path: 'transfer', component: FundTransferComponent, canActivate: [PermissionGuard], data: { permissionCode: 'TRANSACTION_TRANSFER' } },
  { path: 'cheque-clearing', component: ChequeClearingComponent, canActivate: [PermissionGuard], data: { permissionCode: 'TRANSACTION_CHEQUE_CLEARING' } },
  { path: 'standing-instructions', component: StandingInstructionListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'TRANSACTIONS_ACCESS' } },
  { path: 'standing-instructions/new', component: StandingInstructionCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'TRANSACTION_STANDING_INSTRUCTION_CREATE' } },
  { path: 'list', component: TransactionListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'TRANSACTIONS_ACCESS' } },
  { path: ':id/reverse', component: TransactionReversalComponent, canActivate: [PermissionGuard], data: { permissionCode: 'TRANSACTION_REVERSE' } },
  { path: ':id', component: TransactionViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'TRANSACTIONS_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TransactionsRoutingModule {}
