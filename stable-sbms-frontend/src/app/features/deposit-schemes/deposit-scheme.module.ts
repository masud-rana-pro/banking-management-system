import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { DepositSchemeRoutingModule } from './deposit-scheme-routing.module';

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

@NgModule({
  declarations: [
    SchemeDashboardComponent,
    SchemeListComponent,
    SchemeCreateComponent,
    SchemeEditComponent,
    SchemeViewComponent,
    EnrollmentListComponent,
    EnrollmentCreateComponent,
    EnrollmentViewComponent,
    DepositScheduleViewComponent,
    ProfitDistributionViewComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    DepositSchemeRoutingModule
  ]
})
export class DepositSchemeModule {}
