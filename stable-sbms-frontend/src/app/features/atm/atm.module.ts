import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AtmRoutingModule } from './atm-routing.module';
import { SharedModule } from 'src/app/shared/shared.module';

import { TerminalListComponent } from './pages/terminal-list/terminal-list.component';
import { TerminalEditComponent } from './pages/terminal-edit/terminal-edit.component';
import { TerminalViewComponent } from './pages/terminal-view/terminal-view.component';
import { CashBinListComponent } from './pages/cash-bin-list/cash-bin-list.component';
import { CashBinEditComponent } from './pages/cash-bin-edit/cash-bin-edit.component';
import { CashBinViewComponent } from './pages/cash-bin-view/cash-bin-view.component';
import { TerminalDashboardComponent } from './pages/terminal-dashboard/terminal-dashboard.component';
import { ReplenishmentListComponent } from './pages/replenishment-list/replenishment-list.component';
import { ReplenishmentEditComponent } from './pages/replenishment-edit/replenishment-edit.component';
import { ReplenishmentViewComponent } from './pages/replenishment-view/replenishment-view.component';
import { ReconciliationListComponent } from './pages/reconciliation-list/reconciliation-list.component';
import { ReconciliationEditComponent } from './pages/reconciliation-edit/reconciliation-edit.component';
import { ReconciliationViewComponent } from './pages/reconciliation-view/reconciliation-view.component';
import { DeviceJournalListComponent } from './pages/device-journal-list/device-journal-list.component';

@NgModule({
  declarations: [
    TerminalDashboardComponent,
    TerminalListComponent,
    TerminalEditComponent,
    TerminalViewComponent,
    CashBinListComponent,
    CashBinEditComponent,
    CashBinViewComponent,
    ReplenishmentListComponent,
    ReplenishmentEditComponent,
    ReplenishmentViewComponent,
    ReconciliationListComponent,
    ReconciliationEditComponent,
    ReconciliationViewComponent,
    DeviceJournalListComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    AtmRoutingModule
  ]
})
export class AtmModule {}
