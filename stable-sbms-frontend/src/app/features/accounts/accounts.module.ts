import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { AccountsRoutingModule } from './accounts-routing.module';

import { AccountDashboardComponent } from './pages/account-dashboard/account-dashboard.component';
import { AccountTypeListComponent } from './pages/account-type-list/account-type-list.component';
import { AccountTypeCreateComponent } from './pages/account-type-create/account-type-create.component';
import { AccountTypeViewComponent } from './pages/account-type-view/account-type-view.component';
import { AccountOpeningRequestListComponent } from './pages/account-opening-request-list/account-opening-request-list.component';
import { AccountOpeningRequestCreateComponent } from './pages/account-opening-request-create/account-opening-request-create.component';
import { AccountOpeningRequestViewComponent } from './pages/account-opening-request-view/account-opening-request-view.component';
import { AccountOpeningReviewComponent } from './pages/account-opening-review/account-opening-review.component';
import { AccountListComponent } from './pages/account-list/account-list.component';
import { AccountViewComponent } from './pages/account-view/account-view.component';
import { AccountStatusActionComponent } from './pages/account-status-action/account-status-action.component';

@NgModule({
  declarations: [
    AccountDashboardComponent,
    AccountTypeListComponent,
    AccountTypeCreateComponent,
    AccountTypeViewComponent,
    AccountOpeningRequestListComponent,
    AccountOpeningRequestCreateComponent,
    AccountOpeningRequestViewComponent,
    AccountOpeningReviewComponent,
    AccountListComponent,
    AccountViewComponent,
    AccountStatusActionComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    AccountsRoutingModule
  ]
})
export class AccountsModule {}
