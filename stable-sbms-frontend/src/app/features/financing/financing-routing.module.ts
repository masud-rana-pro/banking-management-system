import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { FinancingDashboardComponent } from './pages/financing-dashboard/financing-dashboard.component';
import { FinancingProductListComponent } from './pages/financing-product-list/financing-product-list.component';
import { FinancingProductCreateComponent } from './pages/financing-product-create/financing-product-create.component';
import { FinancingProductEditComponent } from './pages/financing-product-edit/financing-product-edit.component';
import { FinancingProductViewComponent } from './pages/financing-product-view/financing-product-view.component';
import { FinancingApplicationListComponent } from './pages/financing-application-list/financing-application-list.component';
import { FinancingApplicationCreateComponent } from './pages/financing-application-create/financing-application-create.component';
import { FinancingApplicationEditComponent } from './pages/financing-application-edit/financing-application-edit.component';
import { FinancingApplicationViewComponent } from './pages/financing-application-view/financing-application-view.component';
import { FinancingReviewComponent } from './pages/financing-review/financing-review.component';
import { FinancingDisbursementComponent } from './pages/financing-disbursement/financing-disbursement.component';
import { InstallmentScheduleViewComponent } from './pages/installment-schedule-view/installment-schedule-view.component';
import { RepaymentCollectionComponent } from './pages/repayment-collection/repayment-collection.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: FinancingDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_ACCESS' } },
  { path: 'products', component: FinancingProductListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_ACCESS' } },
  { path: 'products/new', component: FinancingProductCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_PRODUCT_CREATE' } },
  { path: 'products/:id/edit', component: FinancingProductEditComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_PRODUCT_EDIT' } },
  { path: 'products/:id', component: FinancingProductViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_ACCESS' } },
  { path: 'applications', component: FinancingApplicationListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_ACCESS' } },
  { path: 'applications/new', component: FinancingApplicationCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_APPLICATION_CREATE' } },
  { path: 'applications/:id/edit', component: FinancingApplicationEditComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_APPLICATION_EDIT' } },
  { path: 'applications/:id/review', component: FinancingReviewComponent, canActivate: [PermissionGuard], data: { permissionCodes: ['FINANCING_REVIEW', 'FINANCING_APPROVE', 'FINANCING_REJECT', 'FINANCING_RETURN'] } },
  { path: 'applications/:id/disburse', component: FinancingDisbursementComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_DISBURSE' } },
  { path: 'applications/:id/schedule', component: InstallmentScheduleViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_ACCESS' } },
  { path: 'applications/:id/repayment', component: RepaymentCollectionComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_COLLECT_PAYMENT' } },
  { path: 'applications/:id', component: FinancingApplicationViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'FINANCING_ACCESS' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FinancingRoutingModule {}
