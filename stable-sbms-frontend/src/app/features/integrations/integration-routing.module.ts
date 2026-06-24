import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { IntegrationDashboardComponent } from './pages/integration-dashboard/integration-dashboard.component';
import { ProviderListComponent } from './pages/provider-list/provider-list.component';
import { ProviderCreateComponent } from './pages/provider-create/provider-create.component';
import { ProviderEditComponent } from './pages/provider-edit/provider-edit.component';
import { ProviderViewComponent } from './pages/provider-view/provider-view.component';
import { IntegrationLogListComponent } from './pages/integration-log-list/integration-log-list.component';
import { IntegrationLogViewComponent } from './pages/integration-log-view/integration-log-view.component';
import { ProviderTestComponent } from './pages/provider-test/provider-test.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: IntegrationDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'INTEGRATION_MANAGEMENT_ACCESS' } },
  { path: 'providers', component: ProviderListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'INTEGRATION_MANAGEMENT_ACCESS' } },
  { path: 'providers/new', component: ProviderCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'INTEGRATION_PROVIDER_CREATE' } },
  { path: 'providers/:id/edit', component: ProviderEditComponent, canActivate: [PermissionGuard], data: { permissionCode: 'INTEGRATION_PROVIDER_EDIT' } },
  { path: 'providers/:id', component: ProviderViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'INTEGRATION_MANAGEMENT_ACCESS' } },
  { path: 'logs', component: IntegrationLogListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'INTEGRATION_MANAGEMENT_ACCESS' } },
  { path: 'logs/:id', component: IntegrationLogViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'INTEGRATION_MANAGEMENT_ACCESS' } },
  { path: 'provider-test', component: ProviderTestComponent, canActivate: [PermissionGuard], data: { permissionCode: 'INTEGRATION_PROVIDER_TEST' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class IntegrationRoutingModule {}
