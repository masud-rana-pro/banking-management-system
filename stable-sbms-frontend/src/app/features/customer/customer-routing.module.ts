import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { CustomerDashboardComponent } from './pages/customer-dashboard/customer-dashboard.component';
import { CustomerListComponent } from './pages/customer-list/customer-list.component';
import { CustomerEditComponent } from './pages/customer-edit/customer-edit.component';
import { CustomerViewComponent } from './pages/customer-view/customer-view.component';
import { CustomerAddressManageComponent } from './pages/customer-address-manage/customer-address-manage.component';
import { CustomerIdentityManageComponent } from './pages/customer-identity-manage/customer-identity-manage.component';
import { CustomerSearchComponent } from './pages/customer-search/customer-search.component';
import { CustomerStatusActionComponent } from './pages/customer-status-action/customer-status-action.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: CustomerDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CUSTOMER_MANAGEMENT_ACCESS' } },
  { path: 'list', component: CustomerListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CUSTOMER_MANAGEMENT_ACCESS' } },
  { path: 'new', component: CustomerEditComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CUSTOMER_CREATE' } },
  { path: 'search', component: CustomerSearchComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CUSTOMER_MANAGEMENT_ACCESS' } },
  { path: 'status-action', component: CustomerStatusActionComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['CUSTOMER_ACTIVATE', 'CUSTOMER_BLOCK'] } },
  { path: ':id/edit', component: CustomerEditComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CUSTOMER_EDIT' } },
  { path: ':id/addresses', component: CustomerAddressManageComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CUSTOMER_ADDRESS_MANAGE' } },
  { path: ':id/identities', component: CustomerIdentityManageComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CUSTOMER_IDENTITY_MANAGE' } },
  { path: ':id/status', component: CustomerStatusActionComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['CUSTOMER_ACTIVATE', 'CUSTOMER_BLOCK'] } },
  { path: ':id', component: CustomerViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CUSTOMER_MANAGEMENT_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CustomerRoutingModule {}
