import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { ProfitRoutingModule } from './profit-routing.module';

import { ProfitDashboardComponent } from './pages/profit-dashboard/profit-dashboard.component';
import { ProfitRatioListComponent } from './pages/profit-ratio-list/profit-ratio-list.component';
import { ProfitRatioCreateComponent } from './pages/profit-ratio-create/profit-ratio-create.component';
import { ProfitRatioViewComponent } from './pages/profit-ratio-view/profit-ratio-view.component';
import { ProfitScheduleListComponent } from './pages/profit-schedule-list/profit-schedule-list.component';
import { ProfitScheduleCreateComponent } from './pages/profit-schedule-create/profit-schedule-create.component';
import { ProfitScheduleViewComponent } from './pages/profit-schedule-view/profit-schedule-view.component';
import { ProfitPostingListComponent } from './pages/profit-posting-list/profit-posting-list.component';
import { ProfitPostingRunComponent } from './pages/profit-posting-run/profit-posting-run.component';
import { ProfitPostingViewComponent } from './pages/profit-posting-view/profit-posting-view.component';

@NgModule({
  declarations: [
    ProfitDashboardComponent,
    ProfitRatioListComponent,
    ProfitRatioCreateComponent,
    ProfitRatioViewComponent,
    ProfitScheduleListComponent,
    ProfitScheduleCreateComponent,
    ProfitScheduleViewComponent,
    ProfitPostingListComponent,
    ProfitPostingRunComponent,
    ProfitPostingViewComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ProfitRoutingModule
  ]
})
export class ProfitModule {}
