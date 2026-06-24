import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { StatementDashboardComponent } from './statement-dashboard/statement-dashboard.component';
import { CustomerStatementRequestComponent } from './customer-statement-request/customer-statement-request.component';
import { CustomerStatementListComponent } from './customer-statement-list/customer-statement-list.component';
import { CustomerStatementViewComponent } from './customer-statement-view/customer-statement-view.component';
import { BranchStatementRequestComponent } from './branch-statement-request/branch-statement-request.component';
import { BranchStatementListComponent } from './branch-statement-list/branch-statement-list.component';
import { BranchStatementViewComponent } from './branch-statement-view/branch-statement-view.component';
import { ExportCenterComponent } from './export-center/export-center.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: StatementDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'STATEMENTS_ACCESS' } },
  { path: 'customer/request', component: CustomerStatementRequestComponent, canActivate: [PermissionGuard], data: { permissionCode: 'STATEMENT_CUSTOMER_REQUEST' } },
  { path: 'customer/list', component: CustomerStatementListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'STATEMENTS_ACCESS' } },
  { path: 'customer/:id', component: CustomerStatementViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'STATEMENTS_ACCESS' } },
  { path: 'branch/request', component: BranchStatementRequestComponent, canActivate: [PermissionGuard], data: { permissionCode: 'STATEMENT_BRANCH_REQUEST' } },
  { path: 'branch/list', component: BranchStatementListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'STATEMENTS_ACCESS' } },
  { path: 'branch/:id', component: BranchStatementViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'STATEMENTS_ACCESS' } },
  { path: 'export-center', component: ExportCenterComponent, canActivate: [PermissionGuard], data: { permissionCode: 'STATEMENTS_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StatementRoutingModule {}
