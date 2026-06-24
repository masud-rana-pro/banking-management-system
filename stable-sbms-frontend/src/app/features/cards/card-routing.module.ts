import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { CardDashboardComponent } from './pages/card-dashboard/card-dashboard.component';
import { CardListComponent } from './pages/card-list/card-list.component';
import { CardCreateComponent } from './pages/card-create/card-create.component';
import { CardViewComponent } from './pages/card-view/card-view.component';
import { CardActivationComponent } from './pages/card-activation/card-activation.component';
import { CardBlockUnblockComponent } from './pages/card-block-unblock/card-block-unblock.component';
import { PinEventListComponent } from './pages/pin-event-list/pin-event-list.component';
import { AtmCdmTransactionListComponent } from './pages/atm-cdm-transaction-list/atm-cdm-transaction-list.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: CardDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CARD_MANAGEMENT_ACCESS' } },
  { path: 'list', component: CardListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CARD_MANAGEMENT_ACCESS' } },
  { path: 'new', component: CardCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CARD_CREATE' } },
  { path: ':id/edit', component: CardCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CARD_EDIT' } },
  { path: ':id', component: CardViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CARD_MANAGEMENT_ACCESS' } },
  { path: ':id/activate', component: CardActivationComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CARD_ACTIVATE' } },
  { path: ':id/block-unblock', component: CardBlockUnblockComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['CARD_BLOCK', 'CARD_UNBLOCK'] } },
  { path: ':id/pin-events', component: PinEventListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CARD_PIN_EVENT' } },
  { path: 'atm-cdm-transactions/list', component: AtmCdmTransactionListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CARD_MANAGEMENT_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CardRoutingModule {}
