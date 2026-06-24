import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { AccountDashboardComponent } from './pages/account-dashboard/account-dashboard.component';
import { AccountTypeListComponent } from './pages/account-type-list/account-type-list.component';
import { AccountTypeCreateComponent } from './pages/account-type-create/account-type-create.component';
import { AccountTypeViewComponent } from './pages/account-type-view/account-type-view.component';
import { AccountOpeningRequestListComponent } from './pages/account-opening-request-list/account-opening-request-list.component';
import { AccountOpeningRequestCreateComponent } from './pages/account-opening-request-create/account-opening-request-create.component';
import { AccountOpeningRequestViewComponent } from './pages/account-opening-request-view/account-opening-request-view.component';
import { AccountOpeningReviewComponent } from './pages/account-opening-review/account-opening-review.component';
import { AccountListComponent } from './pages/account-list/account-list.component';
import { AccountViewComponent } from './pages/account-view/account-view.component';
import { AccountStatusActionComponent } from './pages/account-status-action/account-status-action.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: AccountDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS' } },
  { path: 'account-types', component: AccountTypeListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS' } },
  { path: 'account-types/new', component: AccountTypeCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ACCOUNT_TYPE_CREATE' } },
  { path: 'account-types/:id/edit', component: AccountTypeCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ACCOUNT_TYPE_EDIT' } },
  { path: 'account-types/:id', component: AccountTypeViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS' } },
  { path: 'opening-requests', component: AccountOpeningRequestListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS' } },
  { path: 'opening-requests/new', component: AccountOpeningRequestCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ACCOUNT_REQUEST_CREATE' } },
  { path: 'opening-requests/:id/edit', component: AccountOpeningRequestCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ACCOUNT_REQUEST_EDIT' } },
  { path: 'opening-requests/:id/review', component: AccountOpeningReviewComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['ACCOUNT_REQUEST_VERIFY', 'ACCOUNT_REQUEST_APPROVE', 'ACCOUNT_REQUEST_REJECT', 'ACCOUNT_REQUEST_RETURN'] } },
  { path: 'opening-requests/:id', component: AccountOpeningRequestViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS' } },
  { path: 'list', component: AccountListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS' } },
  { path: ':id/status', component: AccountStatusActionComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['ACCOUNT_ACTIVATE', 'ACCOUNT_BLOCK', 'ACCOUNT_FREEZE', 'ACCOUNT_CLOSE'] } },
  { path: ':id', component: AccountViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AccountsRoutingModule {}
