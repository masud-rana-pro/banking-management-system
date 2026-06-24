import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { TerminalListComponent } from './pages/terminal-list/terminal-list.component';
import { TerminalEditComponent } from './pages/terminal-edit/terminal-edit.component';
import { TerminalViewComponent } from './pages/terminal-view/terminal-view.component';
import { CashBinEditComponent } from './pages/cash-bin-edit/cash-bin-edit.component';
import { CashBinViewComponent } from './pages/cash-bin-view/cash-bin-view.component';
import { CashBinListComponent } from './pages/cash-bin-list/cash-bin-list.component';
import { TerminalDashboardComponent } from './pages/terminal-dashboard/terminal-dashboard.component';
import { ReplenishmentListComponent } from './pages/replenishment-list/replenishment-list.component';
import { ReplenishmentEditComponent } from './pages/replenishment-edit/replenishment-edit.component';
import { ReplenishmentViewComponent } from './pages/replenishment-view/replenishment-view.component';
import { ReconciliationListComponent } from './pages/reconciliation-list/reconciliation-list.component';
import { ReconciliationEditComponent } from './pages/reconciliation-edit/reconciliation-edit.component';
import { ReconciliationViewComponent } from './pages/reconciliation-view/reconciliation-view.component';
import { DeviceJournalListComponent } from './pages/device-journal-list/device-journal-list.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    component: TerminalDashboardComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CDM_ACCESS' }
  },
  {
    path: 'terminals',
    component: TerminalListComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CDM_ACCESS' }
  },
  {
    path: 'terminals/new',
    component: TerminalEditComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_TERMINAL_CREATE' }
  },
  {
    path: 'terminals/:id/edit',
    component: TerminalEditComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_TERMINAL_EDIT' }
  },
  {
    path: 'terminals/:id',
    component: TerminalViewComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CDM_ACCESS' }
  },


  {
    path: 'cash-bins',
    component: CashBinListComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CDM_ACCESS' }
  },
  {
    path: 'cash-bin/new',
    component: CashBinEditComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CASH_BIN_CREATE' }
  },
  {
    path: 'cash-bin/:id',
    component: CashBinViewComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CDM_ACCESS' }
  },
  {
    path: 'cash-bin/:id/edit',
    component: CashBinEditComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CASH_BIN_EDIT' }
  },
  {
    path: 'replenishments',
    component: ReplenishmentListComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CDM_ACCESS' }
  },
  {
    path: 'replenishment/new',
    component: ReplenishmentEditComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_REPLENISHMENT_CREATE' }
  },
  {
    path: 'replenishment/:id',
    component: ReplenishmentViewComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CDM_ACCESS' }
  },
  {
    path: 'reconciliations',
    component: ReconciliationListComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CDM_ACCESS' }
  },
  {
    path: 'reconciliation/new',
    component: ReconciliationEditComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_RECONCILIATION_CREATE' }
  },
  {
    path: 'reconciliation/:id',
    component: ReconciliationViewComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CDM_ACCESS' }
  },
  {
    path: 'device-journal',
    component: DeviceJournalListComponent,
    canActivate: [PermissionGuard],
    data: { permissionCode: 'ATM_CDM_ACCESS' }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AtmRoutingModule { }
