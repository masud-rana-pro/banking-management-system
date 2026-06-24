import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { TransactionsRoutingModule } from './transactions-routing.module';

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

@NgModule({
  declarations: [
    TransactionDashboardComponent,
    CashDepositComponent,
    CashWithdrawComponent,
    FundTransferComponent,
    ChequeClearingComponent,
    StandingInstructionListComponent,
    StandingInstructionCreateComponent,
    TransactionListComponent,
    TransactionViewComponent,
    TransactionReversalComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    TransactionsRoutingModule
  ]
})
export class TransactionsModule {}
