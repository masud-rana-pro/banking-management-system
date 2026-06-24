import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { SchemeDashboardComponent } from './scheme-dashboard/scheme-dashboard.component';
import { SchemeListComponent } from './scheme-list/scheme-list.component';
import { SchemeCreateComponent } from './scheme-create/scheme-create.component';
import { SchemeEditComponent } from './scheme-edit/scheme-edit.component';
import { SchemeViewComponent } from './scheme-view/scheme-view.component';
import { EnrollmentListComponent } from './enrollment-list/enrollment-list.component';
import { EnrollmentCreateComponent } from './enrollment-create/enrollment-create.component';
import { EnrollmentViewComponent } from './enrollment-view/enrollment-view.component';
import { DepositScheduleViewComponent } from './deposit-schedule-view/deposit-schedule-view.component';
import { ProfitDistributionViewComponent } from './profit-distribution-view/profit-distribution-view.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: SchemeDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'DEPOSIT_SCHEMES_ACCESS' } },
  { path: 'list', component: SchemeListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'DEPOSIT_SCHEMES_ACCESS' } },
  { path: 'new', component: SchemeCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'DEPOSIT_SCHEME_CREATE' } },
  { path: 'enrollments/list', component: EnrollmentListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'DEPOSIT_SCHEMES_ACCESS' } },
  { path: 'enrollments/new', component: EnrollmentCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'DEPOSIT_SCHEME_ENROLLMENT_CREATE' } },
  { path: 'enrollments/:id', component: EnrollmentViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'DEPOSIT_SCHEMES_ACCESS' } },
  { path: 'enrollments/:id/schedule', component: DepositScheduleViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'DEPOSIT_SCHEMES_ACCESS' } },
  { path: 'enrollments/:id/profit', component: ProfitDistributionViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'DEPOSIT_SCHEMES_ACCESS' } },
  { path: ':id/edit', component: SchemeEditComponent, canActivate: [PermissionGuard], data: { permissionCode: 'DEPOSIT_SCHEME_EDIT' } },
  { path: ':id', component: SchemeViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'DEPOSIT_SCHEMES_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DepositSchemeRoutingModule {}
