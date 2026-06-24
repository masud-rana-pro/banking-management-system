import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { LookupDashboardComponent } from './pages/lookup-dashboard/lookup-dashboard.component';
import { LookupTypeListComponent } from './pages/lookup-type-list/lookup-type-list.component';
import { LookupTypeFormComponent } from './pages/lookup-type-form/lookup-type-form.component';
import { LookupTypeViewComponent } from './pages/lookup-type-view/lookup-type-view.component';
import { LookupValueListComponent } from './pages/lookup-value-list/lookup-value-list.component';
import { LookupValueFormComponent } from './pages/lookup-value-form/lookup-value-form.component';
import { LookupValueViewComponent } from './pages/lookup-value-view/lookup-value-view.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: LookupDashboardComponent },
  { path: 'types', component: LookupTypeListComponent },
  { path: 'types/new', component: LookupTypeFormComponent, canActivate: [PermissionGuard], data: { permissionCode: 'LOOKUP_TYPE_CREATE' } },
  { path: 'types/:id', component: LookupTypeViewComponent },
  { path: 'types/:id/edit', component: LookupTypeFormComponent, canActivate: [PermissionGuard], data: { permissionCode: 'LOOKUP_TYPE_EDIT' } },
  { path: 'values', component: LookupValueListComponent },
  { path: 'values/new', component: LookupValueFormComponent, canActivate: [PermissionGuard], data: { permissionCode: 'LOOKUP_VALUE_CREATE' } },
  { path: 'values/:id', component: LookupValueViewComponent },
  { path: 'values/:id/edit', component: LookupValueFormComponent, canActivate: [PermissionGuard], data: { permissionCode: 'LOOKUP_VALUE_EDIT' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LookupRoutingModule {}
