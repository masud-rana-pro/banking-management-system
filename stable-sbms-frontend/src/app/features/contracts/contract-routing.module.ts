import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { ContractDashboardComponent } from './pages/contract-dashboard/contract-dashboard.component';
import { ContractTemplateListComponent } from './pages/contract-template-list/contract-template-list.component';
import { ContractTemplateCreateComponent } from './pages/contract-template-create/contract-template-create.component';
import { ContractTemplateEditComponent } from './pages/contract-template-edit/contract-template-edit.component';
import { ContractTemplateViewComponent } from './pages/contract-template-view/contract-template-view.component';
import { ContractListComponent } from './pages/contract-list/contract-list.component';
import { ContractGenerateComponent } from './pages/contract-generate/contract-generate.component';
import { ContractViewComponent } from './pages/contract-view/contract-view.component';
import { ContractSignComponent } from './pages/contract-sign/contract-sign.component';
import { ContractVersionHistoryComponent } from './pages/contract-version-history/contract-version-history.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: ContractDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CONTRACTS_ACCESS' } },
  { path: 'templates', component: ContractTemplateListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CONTRACTS_ACCESS' } },
  { path: 'templates/new', component: ContractTemplateCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CONTRACT_TEMPLATE_CREATE' } },
  { path: 'templates/:id/edit', component: ContractTemplateEditComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CONTRACT_TEMPLATE_EDIT' } },
  { path: 'templates/:id', component: ContractTemplateViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CONTRACTS_ACCESS' } },
  { path: 'list', component: ContractListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CONTRACTS_ACCESS' } },
  { path: 'generate', component: ContractGenerateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CONTRACT_GENERATE' } },
  { path: ':id/sign', component: ContractSignComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['CONTRACT_CUSTOMER_SIGN', 'CONTRACT_SHARIAH_SIGN'] } },
  { path: ':id/versions', component: ContractVersionHistoryComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CONTRACTS_ACCESS' } },
  { path: ':id', component: ContractViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CONTRACTS_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ContractRoutingModule {}
