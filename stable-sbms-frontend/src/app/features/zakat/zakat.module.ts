import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { ZakatRoutingModule } from './zakat-routing.module';

import { ZakatDashboardComponent } from './pages/zakat-dashboard/zakat-dashboard.component';
import { ZakatProfileListComponent } from './pages/zakat-profile-list/zakat-profile-list.component';
import { ZakatProfileViewComponent } from './pages/zakat-profile-view/zakat-profile-view.component';
import { ZakatCalcRunComponent } from './pages/zakat-calc-run/zakat-calc-run.component';
import { CharityFundViewComponent } from './pages/charity-fund-view/charity-fund-view.component';
import { BeneficiaryListComponent } from './pages/beneficiary-list/beneficiary-list.component';
import { BeneficiaryCreateComponent } from './pages/beneficiary-create/beneficiary-create.component';
import { PayoutListComponent } from './pages/payout-list/payout-list.component';
import { PayoutCreateComponent } from './pages/payout-create/payout-create.component';

@NgModule({
  declarations: [
    ZakatDashboardComponent,
    ZakatProfileListComponent,
    ZakatProfileViewComponent,
    ZakatCalcRunComponent,
    CharityFundViewComponent,
    BeneficiaryListComponent,
    BeneficiaryCreateComponent,
    PayoutListComponent,
    PayoutCreateComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ZakatRoutingModule
  ]
})
export class ZakatModule {}
