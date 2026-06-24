import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'src/app/shared/shared.module';

import { GeneralDashboardRoutingModule } from './general-dashboard-routing.module';
import { GeneralDashboardComponent } from './pages/general-dashboard/general-dashboard.component';

@NgModule({
  declarations: [GeneralDashboardComponent],
  imports: [
    CommonModule,
    RouterModule,
    SharedModule,
    GeneralDashboardRoutingModule
  ]
})
export class GeneralDashboardModule {}
