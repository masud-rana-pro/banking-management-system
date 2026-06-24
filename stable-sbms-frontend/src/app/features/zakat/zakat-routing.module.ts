import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { ZakatDashboardComponent } from './pages/zakat-dashboard/zakat-dashboard.component';
import { ZakatProfileListComponent } from './pages/zakat-profile-list/zakat-profile-list.component';
import { ZakatProfileViewComponent } from './pages/zakat-profile-view/zakat-profile-view.component';
import { ZakatCalcRunComponent } from './pages/zakat-calc-run/zakat-calc-run.component';
import { CharityFundViewComponent } from './pages/charity-fund-view/charity-fund-view.component';
import { BeneficiaryListComponent } from './pages/beneficiary-list/beneficiary-list.component';
import { BeneficiaryCreateComponent } from './pages/beneficiary-create/beneficiary-create.component';
import { PayoutListComponent } from './pages/payout-list/payout-list.component';
import { PayoutCreateComponent } from './pages/payout-create/payout-create.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: ZakatDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ZAKAT_CHARITY_ACCESS' } },
  { path: 'profiles', component: ZakatProfileListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ZAKAT_CHARITY_ACCESS' } },
  { path: 'profiles/:id', component: ZakatProfileViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ZAKAT_CHARITY_ACCESS' } },
  { path: 'calc-run', component: ZakatCalcRunComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ZAKAT_CALCULATE' } },
  { path: 'charity-fund', component: CharityFundViewComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ZAKAT_CHARITY_ACCESS' } },
  { path: 'beneficiaries', component: BeneficiaryListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ZAKAT_CHARITY_ACCESS' } },
  { path: 'beneficiaries/new', component: BeneficiaryCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CHARITY_BENEFICIARY_CREATE' } },
  { path: 'beneficiaries/:id/edit', component: BeneficiaryCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CHARITY_BENEFICIARY_EDIT' } },
  { path: 'payouts', component: PayoutListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'ZAKAT_CHARITY_ACCESS' } },
  { path: 'payouts/new', component: PayoutCreateComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CHARITY_PAYOUT_CREATE' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ZakatRoutingModule {}
