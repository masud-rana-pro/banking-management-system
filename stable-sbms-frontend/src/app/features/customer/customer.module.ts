import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { CustomerRoutingModule } from './customer-routing.module';

import { CustomerDashboardComponent } from './pages/customer-dashboard/customer-dashboard.component';
import { CustomerListComponent } from './pages/customer-list/customer-list.component';
import { CustomerEditComponent } from './pages/customer-edit/customer-edit.component';
import { CustomerViewComponent } from './pages/customer-view/customer-view.component';
import { CustomerAddressManageComponent } from './pages/customer-address-manage/customer-address-manage.component';
import { CustomerIdentityManageComponent } from './pages/customer-identity-manage/customer-identity-manage.component';
import { CustomerSearchComponent } from './pages/customer-search/customer-search.component';
import { CustomerStatusActionComponent } from './pages/customer-status-action/customer-status-action.component';

@NgModule({
  declarations: [
    CustomerDashboardComponent,
    CustomerListComponent,
    CustomerEditComponent,
    CustomerViewComponent,
    CustomerAddressManageComponent,
    CustomerIdentityManageComponent,
    CustomerSearchComponent,
    CustomerStatusActionComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    CustomerRoutingModule
  ]
})
export class CustomerModule {}
