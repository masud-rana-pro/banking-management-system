import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { NotificationRoutingModule } from './notification-routing.module';

import { NotificationDashboardComponent } from './pages/notification-dashboard/notification-dashboard.component';
import { TemplateListComponent } from './pages/template-list/template-list.component';
import { TemplateCreateComponent } from './pages/template-create/template-create.component';
import { TemplateViewComponent } from './pages/template-view/template-view.component';
import { EventRuleListComponent } from './pages/event-rule-list/event-rule-list.component';
import { EventRuleCreateComponent } from './pages/event-rule-create/event-rule-create.component';
import { DeliveryLogListComponent } from './pages/delivery-log-list/delivery-log-list.component';
import { DeliveryLogViewComponent } from './pages/delivery-log-view/delivery-log-view.component';
import { RetryQueueComponent } from './pages/retry-queue/retry-queue.component';

@NgModule({
  declarations: [
    NotificationDashboardComponent,
    TemplateListComponent,
    TemplateCreateComponent,
    TemplateViewComponent,
    EventRuleListComponent,
    EventRuleCreateComponent,
    DeliveryLogListComponent,
    DeliveryLogViewComponent,
    RetryQueueComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    NotificationRoutingModule
  ]
})
export class NotificationModule {}
