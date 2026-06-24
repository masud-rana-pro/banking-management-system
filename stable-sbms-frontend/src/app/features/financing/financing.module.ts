import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { FinancingRoutingModule } from './financing-routing.module';

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

@NgModule({
  declarations: [
    FinancingDashboardComponent,
    FinancingProductListComponent,
    FinancingProductCreateComponent,
    FinancingProductEditComponent,
    FinancingProductViewComponent,
    FinancingApplicationListComponent,
    FinancingApplicationCreateComponent,
    FinancingApplicationEditComponent,
    FinancingApplicationViewComponent,
    FinancingReviewComponent,
    FinancingDisbursementComponent,
    InstallmentScheduleViewComponent,
    RepaymentCollectionComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    FinancingRoutingModule
  ]
})
export class FinancingModule {}
