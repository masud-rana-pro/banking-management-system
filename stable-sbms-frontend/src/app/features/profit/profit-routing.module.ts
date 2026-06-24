import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { ProfitDashboardComponent } from './pages/profit-dashboard/profit-dashboard.component';
import { ProfitRatioListComponent } from './pages/profit-ratio-list/profit-ratio-list.component';
import { ProfitRatioCreateComponent } from './pages/profit-ratio-create/profit-ratio-create.component';
import { ProfitRatioViewComponent } from './pages/profit-ratio-view/profit-ratio-view.component';
import { ProfitScheduleListComponent } from './pages/profit-schedule-list/profit-schedule-list.component';
import { ProfitScheduleCreateComponent } from './pages/profit-schedule-create/profit-schedule-create.component';
import { ProfitScheduleViewComponent } from './pages/profit-schedule-view/profit-schedule-view.component';
import { ProfitPostingListComponent } from './pages/profit-posting-list/profit-posting-list.component';
import { ProfitPostingRunComponent } from './pages/profit-posting-run/profit-posting-run.component';
import { ProfitPostingViewComponent } from './pages/profit-posting-view/profit-posting-view.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: ProfitDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'PROFIT_MANAGEMENT_ACCESS' } },
  { path: 'ratios', component: ProfitRatioListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'PROFIT_MANAGEMENT_ACCESS' } },
  { path: 'ratios/new', component: ProfitRatioCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'PROFIT_RATIO_CREATE' } },
  { path: 'ratios/:id/edit', component: ProfitRatioCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'PROFIT_RATIO_EDIT' } },
  { path: 'ratios/:id', component: ProfitRatioViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'PROFIT_MANAGEMENT_ACCESS' } },
  { path: 'schedules', component: ProfitScheduleListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'PROFIT_MANAGEMENT_ACCESS' } },
  { path: 'schedules/new', component: ProfitScheduleCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'PROFIT_SCHEDULE_CREATE' } },
  { path: 'schedules/:id', component: ProfitScheduleViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'PROFIT_MANAGEMENT_ACCESS' } },
  { path: 'postings', component: ProfitPostingListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'PROFIT_MANAGEMENT_ACCESS' } },
  { path: 'postings/run', component: ProfitPostingRunComponent, canActivate: [PermissionGuard], data: { permissionCode: 'PROFIT_POSTING_RUN' } },
  { path: 'postings/:id', component: ProfitPostingViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'PROFIT_MANAGEMENT_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProfitRoutingModule {}
